package com.sharecontact.conactshare.DATABASE;

public class CONTACT {
    String name;
    String nummber;

    public CONTACT(String name, String nummber) {
        this.name = name;
        this.nummber = nummber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNummber() {
        return nummber;
    }

    public void setNummber(String nummber) {
        this.nummber = nummber;
    }

    @Override
    public String toString() {
        return "CONTACT{" +
                "name='" + name + '\'' +
                ", nummber='" + nummber + '\'' +
                '}';
    }
}
