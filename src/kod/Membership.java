package kod;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */




/**
 * Holds index of a vector and its cluster membership.
 *
 * @author m.smieja
 */
public class Membership {
    public int index;
    public int cluster;

    public Membership(int index, int cluster) {
        this.index = index;
        this.cluster = cluster;
    }

    @Override
    public String toString() {
        return "Membership{" + "index=" + index + ", cluster=" + cluster + '}';
    }
}
