/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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
public class Afinn extends Lexicon{
    
    @Override
    public void populate(String path){
        try {
            FileInputStream fstream = new FileInputStream(path);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            int i = 0;
            while ((strLine = br.readLine()) != null) {
                    String[] temp = strLine.split("\t");
                    Double polarity=Double.parseDouble(temp[1])/5;
                    Polarity curr=null;
                    if(polarity>0){curr=new Polarity(temp[0],polarity,0);}
                    else if(polarity<0){curr=new Polarity(temp[0],0,-1*polarity);}
                    else{curr=new Polarity(temp[0],0,0);}
                    if(!this.pol.contains(curr)){this.pol.add(curr);}
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
