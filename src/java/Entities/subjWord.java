/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

/**
 *
 * @author ViP
 */
public class subjWord {
    String word;
    String pos;
    boolean strong;

    public subjWord(String word, String pos, boolean strong) {
        this.word = word.toLowerCase().trim();
        this.pos = pos.toLowerCase().trim();
        this.strong = strong;
    }

    public String getWord() {
        return word;
    }

    public String getPos() {
        return pos;
    }

    public boolean isStrong() {
        return strong;
    }
    
    public boolean issame(String word, String pos){
        if(!pos.equalsIgnoreCase("anypos"))
            return this.word.equalsIgnoreCase(word.trim()) && this.pos.equalsIgnoreCase(pos.trim());
        else
            return this.word.equalsIgnoreCase(word.trim());
    }
}
