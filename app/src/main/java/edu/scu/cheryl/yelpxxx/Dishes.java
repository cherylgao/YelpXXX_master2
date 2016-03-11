package edu.scu.cheryl.yelpxxx;

/**
 * Created by HTF on 2016/3/10.
 */
public class Dishes {

    private String name;
    private int vote;
    public Dishes(String name, int vote){
        this.name = name;
        this.vote = vote;
    }

    public int getVote(){
        return vote;
    }

    public String getName(){
        return name;
    }
}
