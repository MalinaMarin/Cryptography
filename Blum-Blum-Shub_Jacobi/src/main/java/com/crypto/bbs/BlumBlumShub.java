package com.crypto.bbs;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class BlumBlumShub {


    private static final BigInteger one = BigInteger.valueOf(1L);
    private static final BigInteger two = BigInteger.valueOf(2L);
    private static final BigInteger three = BigInteger.valueOf(3L);
    private static final BigInteger four = BigInteger.valueOf(4L);

    private BigInteger n;
    private final int bits;
    private final int nrSeeds = 1024*1024;
    private List<Integer> result = new ArrayList<>();
    Random rand = new Random();
    private StringBuilder sb;

    //constructor , param biti, calc automat si N
    public BlumBlumShub(int bits) {
        this.bits = bits;
        this.n = calculateN(bits, rand);
    }

    public BigInteger getN() {
        return n;
    }

    public int getBits() {
        return bits;
    }


    public void setN(BigInteger n) {
        this.n = n;
    }

    public List<Integer> getResult() {
        return result;
    }

    public void setResult(List<Integer> result) {
        this.result = result;
    }

    public StringBuilder getSb() {
        return sb;
    }

    public void setSb(StringBuilder sb) {
        this.sb = sb;
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

    public void calculateSeeds(BigInteger seed){

        BigInteger max = BigInteger.valueOf(1048576);
        //System.out.println("Max ul : "+ max);
        BigInteger auxSeed = seed;

        for(BigInteger i = BigInteger.ZERO; i.compareTo(max) < 0; i = i.add(BigInteger.ONE)){
            BigInteger x = auxSeed.modPow(two, getN());
            Integer res = x.mod(two).intValue();
            String resString = res.toString();
            getResult().add(res);
            sb.append(resString);
            auxSeed = x;
        }
    }

    public void showResult(){
        //setResult(result);
        int count0 = 0, count1 = 0;
        for(int i=0; i < getSb().length(); i++){
            if(getSb().charAt(i) == '0'){
                count0++;
            }
        }
        count1 = nrSeeds - count0;
        double nr0 = (double)(count0*100)/nrSeeds;
        double nr1 = (double)(count1*100)/nrSeeds;
        System.out.printf("procent 0: %f, procent 1: %f\n", nr0, nr1);
        System.out.println("nr de 0:  " + count0 + ", nr de 1: " + count1);
        //System.out.println("Procent 0:" + (float)((count0*100)/getBits()) + "Procent 1: " + (float)((count1*100)/getBits()));
    }

    public void doCompression(){

       // Path file = Paths.get("the-file-name");
        //Files.write(file, data);

        //File f = new File("C:\\compresie\\filebbs.txt");
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter("filebbs.txt", false);
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
            fileWriter2 = new FileWriter("file2.txt", false);
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
            in = new FileInputStream("filebbs.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        byte[] bytes = new byte[1024];
        int count = 0;

        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream("test.zip", false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ZipOutputStream zout = new ZipOutputStream(fout);

        ZipEntry ze = new ZipEntry("filebbs.txt");
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
