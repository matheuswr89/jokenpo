package com.matheus.jokenpo.model;

import com.google.firebase.Timestamp;

import java.util.Map;

public class Placar {

    public Integer uid;
    public String winner;
    public Integer hum;
    public Integer pc;
    public Double duration;
    public Timestamp timestamp;

    public Placar(String winner, Integer hum, Integer pc, Double duration, Timestamp timestamp) {
        this.winner = winner;
        this.hum = hum;
        this.pc = pc;
        this.duration = duration;
        this.timestamp = timestamp;
    }

    public Placar(Map<String, Object> objectMap) {
        this.duration = Double.parseDouble(objectMap.get("duration").toString());
        this.hum = Integer.parseInt(objectMap.get("hum").toString());
        this.pc = Integer.parseInt(objectMap.get("pc").toString());
        this.winner = objectMap.get("winner").toString();
        this.timestamp = ((Timestamp) objectMap.get("timestamp"));
    }

    public String getWinner() {
        return winner;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public Integer getHum() {
        return hum;
    }
    public Integer getPc() {
        return pc;
    }


    @Override
    public String toString() {
        return String.format("%s - %d - %d - %.2f s", winner, hum, pc, duration);
    }
}
