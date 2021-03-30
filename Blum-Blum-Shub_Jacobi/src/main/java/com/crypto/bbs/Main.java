package com.crypto.bbs;

import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.lang.Math;

public class Main {

    public static void main(String[] args){
        BlumBlumShub bbs = new BlumBlumShub(1024);
        BigInteger seed = bbs.getFirstSeed();
        bbs.setSb(new StringBuilder());
        bbs.calculateSeeds(seed);

        bbs.showResult();
        bbs.doCompression();

    }
}
