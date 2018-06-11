package kod;


import java.util.Iterator;
import no.uib.cipr.matrix.VectorEntry;
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
public class Cluster {
    
    Hartigan hartigan;
    
    public boolean validCluster;
    
    public int size;
    public double energy;
    public double onBitsEntropy = 0;
    public double countsEntropy = 0;
    public long onBitsSum = 0;
    public int[] onBits;
    public int[] counts;
    
    public int tmpSize;
    public double tmpEnergy;
    public double tmpOnBitsEntropy = 0;
    public double tmpCountsEntropy = 0;
    public long tmpOnBitsSum = 0;
    public int[] tmpOnBits = null;
    public int[] tmpCounts = null;
    
    public int[] tmpOnBitsIndices = null;
    public int tmpElementCount = 0;
    
    public double enId = 0;
    public double enLen = 0;
    public double enText = 0;
    public double tmpEnId = 0;
    public double tmpEnLen = 0;
    public double tmpEnText = 0;
    
    Cluster(Hartigan hartigan, boolean valid) {
        this.hartigan = hartigan;
        validCluster = valid;
        
        tmpSize = 0;
        tmpOnBitsEntropy = 0;
        tmpCountsEntropy = 0;
        tmpOnBitsSum = 0;
        tmpEnergy = 0;
        tmpOnBitsIndices = new int[0];
        tmpOnBits = new int[0];
        tmpCounts = new int[hartigan.data.numColumns()+1];
        tmpElementCount = 0;

        size = 0;
        onBits = new int[hartigan.data.numColumns()];
        counts = new int[hartigan.data.numColumns()+1];
        onBitsEntropy = 0;
        countsEntropy = 0;
        onBitsSum = 0;
        energy = 0;
        
        
        
    }

    public void getEnergyOutOfCluster(Membership membership) {
        tmpSize = size - 1;
        Pair<int[], int[]> updatedBits = substractSparse(hartigan.data.getRow(membership.index));
        tmpOnBitsIndices = updatedBits.fst;
        tmpOnBits = updatedBits.snd;

        tmpElementCount = hartigan.data.getRow(membership.index).getUsed();
        tmpCounts[tmpElementCount] = counts[tmpElementCount] - 1;
        tmpCountsEntropy = countsEntropy - Maths.sh(counts[tmpElementCount]) + Maths.sh(tmpCounts[tmpElementCount]);
        
        if (tmpSize != 0) {
            tmpEnergy = hartigan.getClusterMem(tmpSize, tmpOnBitsEntropy, tmpOnBitsSum, tmpCountsEntropy);
            tmpEnId = hartigan.getIdMem(tmpSize, tmpOnBitsEntropy, tmpOnBitsSum, tmpCountsEntropy);
            tmpEnLen = hartigan.getLenMem(tmpSize, tmpOnBitsEntropy, tmpOnBitsSum, tmpCountsEntropy);
            tmpEnText = hartigan.getTextMem(tmpOnBitsEntropy, tmpOnBitsSum, tmpCountsEntropy);
        } else {
            tmpEnergy = 0;
        }
    }

    public void getEnergyInCluster(Membership membership) {
        tmpSize = size + 1;

        Pair<int[], int[]> updatedBits = addSparse(hartigan.data.getRow(membership.index));
        tmpOnBitsIndices = updatedBits.fst;
        tmpOnBits = updatedBits.snd;

        tmpElementCount = hartigan.data.getRow(membership.index).getUsed();
        tmpCounts[tmpElementCount] = counts[tmpElementCount] + 1;
        tmpCountsEntropy = countsEntropy - Maths.sh(counts[tmpElementCount]) + Maths.sh(tmpCounts[tmpElementCount]);

        if (tmpSize != 0) {
            tmpEnergy = hartigan.getClusterMem(tmpSize, tmpOnBitsEntropy, tmpOnBitsSum, tmpCountsEntropy);
            tmpEnId = hartigan.getIdMem(tmpSize, tmpOnBitsEntropy, tmpOnBitsSum, tmpCountsEntropy);
            tmpEnLen = hartigan.getLenMem(tmpSize, tmpOnBitsEntropy, tmpOnBitsSum, tmpCountsEntropy);
            tmpEnText = hartigan.getTextMem(tmpOnBitsEntropy, tmpOnBitsSum, tmpCountsEntropy);
        } else {
            tmpEnergy = 0;
        }
    }

    public void updateClusterParameters() {
        size = tmpSize;
        for (int i = 0; i < tmpOnBitsIndices.length; ++i) {
            onBits[tmpOnBitsIndices[i]] = tmpOnBits[i];
        }
        counts[tmpElementCount] = tmpCounts[tmpElementCount];
        onBitsEntropy = tmpOnBitsEntropy;
        onBitsSum = tmpOnBitsSum;
        countsEntropy = tmpCountsEntropy;

        energy = tmpEnergy;
        enId = tmpEnId;
        enLen = tmpEnLen;
        enText = tmpEnText;
    }

    public Pair<int[], int[]> substractSparse(SparseVector vector) {
        int[] indices = new int[vector.getUsed()];
        int[] bits = new int[indices.length];
        tmpOnBitsSum = onBitsSum;
        tmpOnBitsEntropy = onBitsEntropy;
        Iterator<VectorEntry> it = vector.iterator();
        int i = 0;
        while (it.hasNext()) {
            VectorEntry e = it.next();
            int newValue = (int) (onBits[e.index()] - e.get());
            indices[i] = e.index();
            bits[i] = newValue;
            ++i;
            tmpOnBitsSum = tmpOnBitsSum - onBits[e.index()] + newValue;
            tmpOnBitsEntropy = tmpOnBitsEntropy - Maths.sh(onBits[e.index()]) + Maths.sh(newValue);
        }
        return new Pair<>(indices, bits);
    }

    public Pair<int[], int[]> addSparse(SparseVector vector) {
        int[] indices = new int[vector.getUsed()];
        int[] bits = new int[indices.length];
        tmpOnBitsSum = onBitsSum;
        tmpOnBitsEntropy = onBitsEntropy;
        Iterator<VectorEntry> it = vector.iterator();
        int i = 0;
        while (it.hasNext()) {
            VectorEntry e = it.next();
            int newValue = (int) (onBits[e.index()] + e.get());
            indices[i] = e.index();
            bits[i] = newValue;
            ++i;
            tmpOnBitsSum = tmpOnBitsSum - onBits[e.index()] + newValue;
            tmpOnBitsEntropy = tmpOnBitsEntropy - Maths.sh(onBits[e.index()]) + Maths.sh(newValue);
        }
        return new Pair<>(indices, bits);
    }
}
