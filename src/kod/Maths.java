package kod;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * A class for mathematical calculations.
 * 
 * @author m.smieja
 */
public class Maths {

    /**
     * Logarithm with an arbitrary base.
     * 
     * @param a
     * @param x
     * @return 
     */
    public static double log(double a, double x) {
        return Math.log(x) / Math.log(a);
    }

    /**
     * Entropy of a sequence of integers.
     * 
     * @param probs
     * @return 
     */
    public static double entropy(int [] probs) {
        double sum = 0;
        for(int i :probs){
            sum += sh(i);
        }
        return sum;
    }

    /**
     * Entropy of (p,1-p)-distribution.
     * 
     * @param prob
     * @return 
     */
    public static double binaryEntropy(double prob) {
        if (prob == 0 || prob == 1) {
            return 0;
        }
        return -prob * Maths.log(2, prob) - (1 - prob) * Maths.log(2, 1 - prob);
    }

    /**
     * Shannon function of a number.
     * 
     * @param prob
     * @return 
     */
    public static double sh(double prob) {
        if (prob == 0 || prob == 1) {
            return 0;
        }
        return -prob * Maths.log(2, prob);
    }
    
    

}
