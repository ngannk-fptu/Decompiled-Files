/*
 * Decompiled with CFR 0.152.
 */
package org.egothor.stemmer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import org.egothor.stemmer.Diff;

public class DiffIt {
    private DiffIt() {
    }

    static int get(int i, String s) {
        try {
            return Integer.parseInt(s.substring(i, i + 1));
        }
        catch (Throwable x) {
            return 1;
        }
    }

    public static void main(String[] args) throws Exception {
        int ins = DiffIt.get(0, args[0]);
        int del = DiffIt.get(1, args[0]);
        int rep = DiffIt.get(2, args[0]);
        int nop = DiffIt.get(3, args[0]);
        for (int i = 1; i < args.length; ++i) {
            Diff diff = new Diff(ins, del, rep, nop);
            String charset = System.getProperty("egothor.stemmer.charset", "UTF-8");
            LineNumberReader in = new LineNumberReader(new BufferedReader(new InputStreamReader((InputStream)new FileInputStream(args[i]), charset)));
            String line = in.readLine();
            while (line != null) {
                try {
                    line = line.toLowerCase(Locale.ROOT);
                    StringTokenizer st = new StringTokenizer(line);
                    String stem = st.nextToken();
                    System.out.println(stem + " -a");
                    while (st.hasMoreTokens()) {
                        String token = st.nextToken();
                        if (token.equals(stem)) continue;
                        System.out.println(stem + " " + diff.exec(token, stem));
                    }
                }
                catch (NoSuchElementException noSuchElementException) {
                    // empty catch block
                }
                line = in.readLine();
            }
        }
    }
}

