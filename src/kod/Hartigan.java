package kod;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import no.uib.cipr.matrix.sparse.SparseVector;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author marek
 */


public class Hartigan {

    public String fileIn;
    public String fileOut;
    public String fileLog;
    public int clNum;
    public int dim;
    public int dataNum;
    public String initMembership;
    public double epsNum;
    public int dataOnBitsSum;
    public String representation;
    public double omega;
    public int initTimes;

    public List<Cluster> clusters;
    public List<Membership> memberships;
    public FlexCompRowMatrix data;
    
    public double minEnergy;

    public Hartigan(String propertiesFile) throws FileNotFoundException, IOException {
        Properties properties = new Properties();
        InputStream input = new FileInputStream(propertiesFile);
        properties.load(input);

        //zaczytanie danych
        fileIn = properties.getProperty("fileIn");
        fileOut = properties.getProperty("fileOut");
        clNum = Integer.parseInt(properties.getProperty("clNum"));
        epsNum = Double.parseDouble(properties.getProperty("epsNum"));
        omega = Double.parseDouble(properties.getProperty("omega"));
        if (properties.getProperty("membership") != null && !properties.getProperty("membership").isEmpty()) {
            initMembership = properties.getProperty("membership");
        }

        representation = properties.getProperty("representation");
        fileLog = properties.getProperty("fileLog");
        initTimes = Integer.parseInt(properties.getProperty("initTimes"));

        minEnergy = Double.MAX_VALUE;
    }

    public void hart() throws IOException {
        double energy = 0;
        System.out.println("START");
        init();
        System.out.println("INIT");
        boolean switched = true;
        int iterations = 0;
        print();
        while (switched) {
            System.out.println("ITERATION " + iterations);
            switched = false;
            for (int i = 0; i < dataNum; ++i) {

                Membership membership = memberships.get(i);
                double cost;
                if (!clusters.get(membership.cluster).validCluster) {
                    membership.cluster = 0;
                }
                if (membership.cluster == 0) {
                    cost = Double.MAX_VALUE;
                } else {
                    clusters.get(membership.cluster).getEnergyOutOfCluster(membership);
                    cost = 0;
                }
                int newCl = membership.cluster;
                
                for (int j = 1; j <= clNum; ++j) {
                    if (clusters.get(j).validCluster && j != membership.cluster) {

                        clusters.get(j).getEnergyInCluster(membership);
                        double newCost = energyAfterSwitched(membership.cluster, j);

                        if (newCost < cost) {
                            newCl = j;
                            cost = newCost;
                            switched = true;
                        }

                    }
                }

                if (newCl != membership.cluster) {
                    int oldCl = membership.cluster;
                    switchIt(membership, newCl);
                    if (oldCl != 0 && clusters.get(oldCl).size < dataNum*epsNum) {
                        clusters.get(oldCl).validCluster = false;
                        clusters.get(oldCl).energy = 0;
                        clusters.get(oldCl).size = 0;

                    }
                }

            }
            ++iterations;
            energy = print();
        }
        if(energy < minEnergy){
            minEnergy = energy;
            System.out.println("Update membership with energy = " + energy);
            InOut.writeMembership(memberships, fileOut + ".out");
        } else{
            System.out.println("No update, energy = " + energy);
        }
        
    }

    public static void main(String[] args) throws IOException {
        Hartigan hartigan;
        if (args.length == 0) {
            hartigan = new Hartigan("toy.properties");
        } else {
            hartigan = new Hartigan(args[0]);
        }
        for(int i = 0; i < hartigan.initTimes; ++i)
            hartigan.hart();
        System.out.println("Minimal energy = " + hartigan.minEnergy);
    }


    //different initialization schemes
    private void init() throws IOException {
        data = InOut.readData(fileIn, representation);
        dataNum = data.numRows();
        dim = data.numColumns();
        dataOnBitsSum = onBits(data);

        this.clusters = new ArrayList<>();
        clusters.add(new Cluster(this, false));
        for (int i = 1; i <= clNum; ++i) {
            clusters.add(new Cluster(this, true));
        }

        if (initMembership == null) {
            Random r = new Random();
            this.memberships = new ArrayList<>();
            for (int i = 0; i < dataNum; ++i) {
                int v = r.nextInt(clNum);
                Membership m = new Membership(i, v + 1);
                memberships.add(m);
                clusters.get(m.cluster).getEnergyInCluster(m);
                clusters.get(m.cluster).updateClusterParameters();

            }
        } else if (initMembership.equals("seed")){
            Random r = new Random();
            List<Integer> seeds = new ArrayList<Integer>();
            for(int i = 0 ; i < clNum; ++i){
                int x = r.nextInt(dataNum);
                seeds.add(x);
            }
            this.memberships = new ArrayList<>();
            for (int i = 0; i < dataNum; ++i) {
                int cl = 0;
                if(seeds.contains(i)){
                    cl = seeds.indexOf(i) + 1;
                    System.out.println(i + " "+ cl);
                } else{
                    cl = 0;
                }
                Membership m = new Membership(i, cl);
                memberships.add(m);
                clusters.get(m.cluster).getEnergyInCluster(m);
                clusters.get(m.cluster).updateClusterParameters();

            }
        }else {
            //from file
            initMembership();
        }
    }

    private void initMembership() throws IOException {
        if (initMembership != null) {
            memberships = InOut.readMembership(initMembership);
            for (Membership m : memberships) {
                clusters.get(m.cluster).getEnergyInCluster(m);
                clusters.get(m.cluster).updateClusterParameters();
            }
        }
    }


    private double energyAfterSwitched(int out, int in) {
        return clusters.get(out).tmpEnergy + clusters.get(in).tmpEnergy
                - (clusters.get(out).energy + clusters.get(in).energy);

    }

    public void switchIt(Membership membership, int newCl) {
        clusters.get(membership.cluster).updateClusterParameters();
        clusters.get(newCl).updateClusterParameters();
        membership.cluster = newCl;
    }

    public double getClusterMem(int size, double onBitsEntropy, long onBitsSum, double countsEntropy) {
        return getIdMem(size, onBitsEntropy, onBitsSum, countsEntropy)
                //+ getLenMem(size, onBitsEntropy, onBitsSum, countsEntropy)
                + getTextMem(onBitsEntropy, onBitsSum, countsEntropy);

    }

    public double getIdMem(int size, double onBitsEntropy, long onBitsSum, double countsEntropy) {
        return omega / dataNum * (size * Maths.log(2, dataNum) - size * Maths.log(2, size));
    }

    public double getLenMem(int size, double onBitsEntropy, long onBitsSum, double countsEntropy) {
//        System.out.println(size + " " +  (size * Maths.log(2, size)) + " " + countsEntropy);
        return (1 - omega) / dataNum * (size * Maths.log(2, size) + countsEntropy);
    }

    public double getTextMem(double onBitsEntropy, long onBitsSum, double countsEntropy) {
        return (1 - omega) / dataNum * (onBitsSum * Maths.log(2, onBitsSum) + onBitsEntropy);
    }

    private double print() {
        double e = 0;
        double enId = 0;
        double enLen = 0;
        double enText = 0;
        double sizes = 0;
        int valids = 0;
        String formatStr1 = "%-5s %-7s %-7s %-10s %-10s %-10s %-10s%n";
        String formatStr2 = "%-5s %-7s %-7s %-10.5f %-10.5f %-10.5f %-10.5f%n";
        System.out.print(String.format(formatStr1, "cl", "size", "valid", "idCost", "lenCost", "textCost", "cost"));
        for (int i = 0; i <= clNum; ++i) {
            if (clusters.get(i).validCluster) {
                valids++;
                e += clusters.get(i).energy;
                enId += clusters.get(i).enId;
                enLen += clusters.get(i).enLen;
                enText += clusters.get(i).enText;
                sizes += clusters.get(i).size;
                System.out.print(String.format(formatStr2, i, clusters.get(i).size, clusters.get(i).validCluster, clusters.get(i).enId, clusters.get(i).enLen, clusters.get(i).enText, clusters.get(i).energy));

            }
        }
        System.out.print(String.format(formatStr1, valids, sizes, "true", enId, enLen, enText, e));
        return e;
    }

    private int onBits(FlexCompRowMatrix data) {
        int sum = 0;
        for (int i = 0; i < data.numRows(); ++i) {
            sum += data.getRow(i).getUsed();
        }
        return sum;
    }
}
