package test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author marek
 */
public class Generator {

    static double p = 0.2;//prawd losowania jedynki w zadkiej reprezentacji

    //prawdopodobieństwo jedynki na współrzędnej w macierzy związków(wiersze = związki, kolumny = wymiary):
    //p*alphga   | p*(1-alpha)
    //p*(1-alpha) | p*alpha
    static void generate(double alpha, int ile, int dimPol, String file) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        Random r = new Random();
        bw.write((2*ile) + " " +(2*dimPol) + "\n");
        for (int j = 0; j < ile; ++j) {
            losuj(alpha, 0, dimPol, bw, r);
            losuj(1-alpha, dimPol, 2*dimPol, bw, r);
            bw.write("\n");
        }
        for (int j = ile; j < 2*ile; ++j) {
            losuj(1-alpha, 0, dimPol, bw, r);
            losuj(alpha, dimPol, 2*dimPol, bw, r);
            bw.write("\n");
        }
        bw.close();
    }
    
    
    static void losuj(double alpha, int dimPol1, int dimPol2, BufferedWriter bw, Random r) throws IOException{
        for (int i = dimPol1; i < dimPol2; ++i) {
                double los = r.nextDouble();
                if (los < p * alpha) {
                    bw.write(i + " ");
                }
            }
    }
    
    public static void main(String[] args) throws IOException {
        for(int i = 0; i <= 10; ++i){
            generate(0.1*i, 50, 10, "dane/eksp/alpha_" + i + ".txt");
        }
        
        
    }
}
