package main.utils;

import lombok.Data;

@Data
public class Rank  implements Comparable<Rank>{

    long numPage = -1;
    int absRange = 0;
    double relRange = 0;

    public Rank(long numPage, int absRange, double relRange) {
        this.numPage = numPage;
        this.absRange = absRange;
        this.relRange = relRange;
    }

    @Override
    public int compareTo(Rank o) {

        int rez = this.absRange - o.absRange;
        if (rez == 0 ) {rez = -1;};
        return rez;
    }
}
