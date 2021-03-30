package com.crypto.jacobi;

import java.math.BigInteger;


public class Main {

    public static void main(String[] args) {

        Jacobi jacobi = new Jacobi(1024); // am calculat si N-ul automat aici
        StringBuilder sb = new StringBuilder();
        BigInteger a = jacobi.getFirstSeed();
        //System.out.println("first seed is " + a);
        BigInteger n = jacobi.getN();

        for(BigInteger i = BigInteger.ZERO; i.compareTo(BigInteger.valueOf(1024*1024)) < 0; i = i.add(BigInteger.ONE))  {
            if((jacobi.jacobiSymbol(a.add(i), n)).compareTo(BigInteger.ONE) == 0){
                sb.append(1);
            }
            else
                sb.append(0);
        }

        jacobi.doCompression(sb);
        jacobi.showResult(sb);

    }
}
