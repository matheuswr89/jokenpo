package com.matheus.jokenpo.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Comparator;

@Entity
public class Placar implements Comparable<Placar> {

    @PrimaryKey
    public Integer uid;
    public String winner;
    public Integer hum;
    public Integer pc;
    public Double duration;

    public Placar(String winner, Integer hum, Integer pc, Double duration) {
        this.winner = winner;
        this.hum = hum;
        this.pc = pc;
        this.duration = duration;
    }

    public Integer getHum() {
        return hum;
    }

    public void setHum(Integer hum) {
        this.hum = hum;
    }

    public Integer getPc() {
        return pc;
    }

    public void setPc(Integer pc) {
        this.pc = pc;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return String.format("%s - %d - %d - %.2f s", winner, hum, pc, duration);
    }

    @Override
    public int compareTo(Placar placar) {
        return Comparator.comparing(Placar::getHum)
                .thenComparing(Placar::getPc)
                .thenComparing(Placar::getDuration)
                .compare(this, placar);
    }
}
