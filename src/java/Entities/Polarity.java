/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

/**
 *
 * @author ViP
 */
public class Polarity {
    String word;
    double posValue;
    double negValue;
    double obsValue;

    public Polarity(String word, double posValue, double negValue) {
        this.word = word;
        this.posValue = posValue;
        this.negValue = negValue;
        this.obsValue = 1-posValue-negValue;
    }
    
    

    @Override
    public String toString() {
        return this.word+" : "+this.posValue+" : "+this.obsValue+" : "+this.negValue;
    }

    public String getWord() {
        return word;
    }

    public double getPosValue() {
        return posValue;
    }

    public double getNegValue() {
        return negValue;
    }

    public double getObsValue() {
        return obsValue;
    }
    
    
}
