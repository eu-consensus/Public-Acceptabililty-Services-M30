/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.util.ArrayList;

/**
 *
 * @author ViP
 */
public class Lexicon {
    ArrayList<Polarity> pol;

    public Lexicon() {
        this.pol = new ArrayList<Polarity>();
    }
    
    public void populate(String path){
        throw new AbstractMethodError();
    }
    
    public void print(){
        for(int i=0;i<this.pol.size();i++){
            System.out.println(this.pol.get(i).toString());
        }
    }
    
    public Double testWord(String word){
            Polarity pola=getWordPolarity(word);
            if(pola!=null){
            return pola.getPosValue()-pola.getNegValue();
            }
            else{
                return null;
            }
    }
    
    public Double testWord(String word,ArrayList<Polarity> pol2){
            Polarity pola=getWordPolarity(word);
            Polarity pola2=null;
            for(int i=0;i<pol2.size();i++){
                if(pol2.get(i).getWord().toLowerCase().trim().equals(word.trim().toLowerCase())){
                    pola2=pol2.get(i);
                }
            }
            if(pola2!=null && pola!=null){
                return pola.getPosValue()+pola2.getPosValue()-pola.getNegValue()-pola2.getNegValue();
            }
            else if(pola!=null){
                return pola.getPosValue()-pola.getNegValue();
            }
            else if(pola2!=null){
                return pola2.getPosValue()-pola2.getNegValue();   
            }
            else{
                return null;
            }
    }
    
    public int getWordCount(){
        return pol.size();
    }
    
    public Polarity getWordPolarity(String word){
        for(int i=0;i<pol.size();i++){
            if(pol.get(i).getWord().toLowerCase().trim().equals(word.trim().toLowerCase())){
                return pol.get(i);
            }
        }
        return null;
    }
}
