package main.utils;

import lombok.Data;

@Data
public class Rank {

    long numPage = -1;
    double absRange = 0;
    double relRange = 0;

    public Rank(long numPage, double absRange, double relRange) {
        this.numPage = numPage;
        this.absRange = absRange;
        this.relRange = relRange;
    }
}
