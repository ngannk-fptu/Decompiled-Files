/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview;

import java.io.IOException;
import java.io.RandomAccessFile;

public class HexDump {
    public static void printData(byte[] data) {
        char[] parts = new char[17];
        int partsloc = 0;
        for (int i = 0; i < data.length; ++i) {
            int d = data[i] & 0xFF;
            parts[partsloc++] = d == 0 ? 46 : (d < 32 || d >= 127 ? 63 : (char)d);
            if (i % 16 == 0) {
                int start = Integer.toHexString(data.length).length();
                int end = Integer.toHexString(i).length();
                for (int j = start; j > end; --j) {
                    System.out.print("0");
                }
                System.out.print(Integer.toHexString(i) + ": ");
            }
            if (d < 16) {
                System.out.print("0" + Integer.toHexString(d));
            } else {
                System.out.print(Integer.toHexString(d));
            }
            if ((i & 0xF) == 15 || i == data.length - 1) {
                System.out.println("      " + new String(parts));
                partsloc = 0;
                continue;
            }
            if ((i & 7) == 7) {
                System.out.print("  ");
                parts[partsloc++] = 32;
                continue;
            }
            if ((i & 1) != 1) continue;
            System.out.print(" ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: ");
            System.out.println("    HexDump <filename>");
            System.exit(-1);
        }
        try {
            RandomAccessFile raf = new RandomAccessFile(args[0], "r");
            int size = (int)raf.length();
            byte[] data = new byte[size];
            raf.readFully(data);
            HexDump.printData(data);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}

