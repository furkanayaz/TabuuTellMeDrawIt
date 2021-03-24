package com.furkanayaz.anlatbakalimcizbakalim;

public class Teams {
    private String teamAname;
    private String teamBname;

    public Teams() {
    }

    public Teams(String teamAname, String teamBname) {
        this.teamAname = teamAname;
        this.teamBname = teamBname;
    }

    public String getTeamAname() {
        return teamAname;
    }

    public void setTeamAname(String teamAname) {
        this.teamAname = teamAname;
    }

    public String getTeamBname() {
        return teamBname;
    }

    public void setTeamBname(String teamBname) {
        this.teamBname = teamBname;
    }
}
