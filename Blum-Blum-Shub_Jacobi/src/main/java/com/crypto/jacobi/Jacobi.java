package com.crypto.jacobi;

import java.io.*;
import java.math.BigInteger;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Jacobi {

    private static final BigInteger zero = BigInteger.valueOf(0L);
    private static final BigInteger one = BigInteger.valueOf(1L);
    private static final BigInteger two = BigInteger.valueOf(2L);
    private static final BigInteger three = BigInteger.valueOf(3L);
    private static final BigInteger four = BigInteger.valueOf(4L);
    private static final BigInteger five = BigInteger.valueOf(5L);
    private static final BigInteger eight = BigInteger.valueOf(8L);

    Random rand = new Random();
    private BigInteger n;
    private final int bits;
    private final int nrSeeds = 1024*1024;

    public Jacobi(int bits) {
        this.bits = bits;
        this.n = calculateN(bits, rand);
    }

    public BigInteger getN() {
        return n;
    }

    public void setN(BigInteger n) {
        this.n = n;
    }

    public int getBits() {
        return bits;
    }

    private static BigInteger getPrime(int bits, Random rand) {
        BigInteger p;
        do {
            p = BigInteger.probablePrime(bits, rand);
        } while (!p.mod(four).equals(three));
        return p;
    }


    public BigInteger calculateN(int bits, Random rand) {
        BigInteger p = getPrime(bits/2, rand);
        BigInteger q = getPrime(bits/2, rand);

        while (p.equals(q)) {
            q = getPrime(bits, rand);
        }
        return p.multiply(q);

    }

    public BigInteger getFirstSeed(){
        BigInteger seed = BigInteger.valueOf(System.currentTimeMillis());

        while(seed.compareTo(one) < 0 || seed.compareTo(getN()) > 0){
            seed = getPrime(getBits(), rand);
        }

        return seed.modPow(two, getN());
    }

    public static BigInteger jacobiSymbol(BigInteger a, BigInteger n) {
        if (a.compareTo(zero) < 0 || n.mod(two).equals(0)) {
            throw new IllegalArgumentException("Invalid value/s...a is " + a + " and n is " + n);
        }

        BigInteger b = a.mod(n);
        BigInteger c = n;
        BigInteger s = one;
        while (b.compareTo(two) >= 0) {
            while (b.mod(four).equals(zero)) {
                b = b.divide(four);
            }
            if (b.mod(two).equals(zero)) {
                if (c.mod(eight).equals(three) || c.mod(eight).equals(five)) {
                    s = s.negate();
                }
                b = b.divide(two);
            }
            if (b.equals(one)) {
                break;
            }
            if (b.mod(four).equals(three) && c.mod(four).equals(three)) {
                s = s.negate();
            }
            BigInteger aux = b;
            b = c.mod(b);
            c = aux;

        }
        return s.multiply(b);
    }


    public void showResult(StringBuilder sb){
        int count0 = 0, count1 = 0;
        for(int i=0; i < sb.length(); i++){
            if(sb.charAt(i) == '0'){
                count0++;
            }
        }
        count1 = nrSeeds - count0;
        double nr0 = (double)(count0*100)/nrSeeds;
        double nr1 = (double)(count1*100)/nrSeeds;
        System.out.printf("procent 0: %f, procent 1: %f\n", nr0, nr1);
        System.out.println("nr de 0:  " + count0 + ", nr de 1: " + count1);

    }


    public void doCompression(StringBuilder sb){

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter("filejacobi.txt", false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        try {
            bufferedWriter.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }


        //new sb
        StringBuilder sb2 = new StringBuilder();
        int size = sb.length();
        for(int i = 0; i < size; i++){
            sb2.append("1");
        }


        //File f2 = new File("C:\\compresie\\file2.txt");
        FileWriter fileWriter2 = null;
        try {
            fileWriter2 = new FileWriter("file2jacobi.txt", false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedWriter bufferedWriter2 = new BufferedWriter(fileWriter2);
        try {
            bufferedWriter2.write(sb2.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }


        FileInputStream in = null;
        try {
            in = new FileInputStream("filejacobi.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        byte[] bytes = new byte[1024];
        int count = 0;

        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream("testjacobi.zip", false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ZipOutputStream zout = new ZipOutputStream(fout);

        ZipEntry ze = new ZipEntry("filejacobi.txt");
        try {
            zout.putNextEntry(ze);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                if (!((count = in.read(bytes)) > 0)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                zout.write(bytes, 0, count);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            zout.closeEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            zout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

