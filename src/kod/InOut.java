package kod;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilterReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author marek
 */
public class InOut {

    static FlexCompRowMatrix readData(String fileIn, String representation) throws FileNotFoundException, IOException {
        if (representation.equals("sparse")) {
            return readDataSparse(fileIn);
        } else {
            return readDataDense(fileIn);
        }
    }

    static FlexCompRowMatrix readDataDense(String fileIn) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileIn));
        String line = br.readLine();
        String[] st = line.split(" ");
        int dataNum = Integer.parseInt(st[0]);
        int dim = Integer.parseInt(st[1]);
        FlexCompRowMatrix matrix = new FlexCompRowMatrix(dataNum, dim);
        for (int i = 0; i < dataNum; ++i) {
            line = br.readLine();
            st = line.split(" ");
            for (int j = 0; j < dim; ++j) {
                if (!st[j].equals("0")) {
                    matrix.set(i, j, Integer.parseInt(st[j]));
                }
            }
        }
        br.close();
        return matrix;
    }

    static FlexCompRowMatrix readDataSparse(String fileIn) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileIn));
        String line = br.readLine();
        String[] st = line.split(" ");
        int dataNum = Integer.parseInt(st[0]);
        int dim = Integer.parseInt(st[1]);
        FlexCompRowMatrix matrix = new FlexCompRowMatrix(dataNum, dim);
        for (int i = 0; i < dataNum; ++i) {
            line = br.readLine();
            if (line.length() > 0) {
                st = line.split(" ");
                for (String j : st) {
                    matrix.set(i, Integer.parseInt(j), 1);
                }
            }
        }
        br.close();
        return matrix;
    }

    static List<Membership> readMembership(String initMembership) throws IOException {
        List<Membership> memberships = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(initMembership));
        String line = br.readLine();
        int dataNum = Integer.parseInt(line);
        for (int i = 0; i < dataNum; ++i) {
            line = br.readLine();
            memberships.add(new Membership(i, Integer.parseInt(line)));
        }
        br.close();

        return memberships;
    }

    static void dense2Sparse(String fileIn, String fileOut) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileIn));
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileOut));
        String line = br.readLine();
        String[] st = line.split(" ");
        int dataNum = Integer.parseInt(st[0]);
        int dim = Integer.parseInt(st[1]);
        bw.write(dataNum + " " + dim + "\n");
        for (int i = 0; i < dataNum; ++i) {
            line = br.readLine();
            st = line.split(" ");
            for (int j = 0; j < dim; ++j) {
                if (!st[j].equals("0")) {
                    bw.write(j + " ");
                }
            }
            bw.write("\n");
        }
        br.close();
        bw.close();
    }

    public static void writeMembership(List<Membership> membership, String file) throws IOException {

        TreeMap<Integer, Integer> kolejnosc = new TreeMap<Integer, Integer>();
        int kol = 1;
        for (Membership m : membership) {

            if (!kolejnosc.containsKey(m.cluster)) {
                kolejnosc.put(m.cluster, kol);
                ++kol;
            }
        }

        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (Membership m : membership) {
            indices.add(0);

        }

        int kk = 0;
        for (Membership m : membership) {
            indices.set(kk, kolejnosc.get(m.cluster));
            ++kk;
        }

        FileWriter fw = null;
        fw = new FileWriter(file);
        BufferedWriter out = new BufferedWriter(fw);
        for (Integer i : indices) {
            out.write(i.toString() + "\n");
            out.flush();

        }

        fw.close();

    }

    public static void log(String file, List<Cluster> clusters, int clNum, double omega) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        double e = 0;
        double enId = 0;
        double enLen = 0;
        double enText = 0;
        double sizes = 0;
        bw.write("id, size, id, len, text, cost\n");
        for (int i = 0; i <= clNum; ++i) {
            if (clusters.get(i).validCluster) {
                e += clusters.get(i).energy;
                enId += clusters.get(i).enId;
                enLen += clusters.get(i).enLen;
                enText += clusters.get(i).enText;
                sizes += clusters.get(i).size;
                bw.write(i + " " + clusters.get(i).size + " " + clusters.get(i).enId + " " + clusters.get(i).enLen + " " + clusters.get(i).enText + " " + clusters.get(i).energy + "\n");

            }
        }
        bw.write("total " + sizes + " " + enId + " " + enLen + " " + enText + " " + e + "\n");
        bw.close();
    }

}
