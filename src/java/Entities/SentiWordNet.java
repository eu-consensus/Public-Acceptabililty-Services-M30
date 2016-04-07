/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 *
 * @author ViP
 */
public class SentiWordNet extends Lexicon{
   
    @Override
    public void populate(String path){
        try {
            FileInputStream fstream = new FileInputStream(path);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            int i = 0;
            while ((strLine = br.readLine()) != null) {
                if((!strLine.startsWith("#"))&&(!strLine.startsWith("\t"))){
                    String[] temp = strLine.split("\t");
                    String[] tempSplitFirst = temp[4].split(" ");
                    for(int j=0;j<tempSplitFirst.length;j++){
                        String[] tempSplitSecond=tempSplitFirst[j].split("#");
                        Polarity curr=new Polarity(tempSplitSecond[0],Double.parseDouble(temp[2]),Double.parseDouble(temp[3]));
                    if(!this.pol.contains(curr)){this.pol.add(curr);}}
                }
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
