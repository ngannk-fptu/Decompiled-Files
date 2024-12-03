/*
 * Decompiled with CFR 0.152.
 */
package org.egothor.stemmer;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import org.egothor.stemmer.Diff;
import org.egothor.stemmer.Gener;
import org.egothor.stemmer.Lift;
import org.egothor.stemmer.MultiTrie2;
import org.egothor.stemmer.Optimizer;
import org.egothor.stemmer.Optimizer2;
import org.egothor.stemmer.Trie;

public class Compile {
    static boolean backward;
    static boolean multi;
    static Trie trie;

    private Compile() {
    }

    public static void main(String[] args) throws Exception {
        int i;
        if (args.length < 1) {
            return;
        }
        args[0].toUpperCase(Locale.ROOT);
        backward = args[0].charAt(0) == '-';
        int qq = backward ? 1 : 0;
        boolean storeorig = false;
        if (args[0].charAt(qq) == '0') {
            storeorig = true;
            ++qq;
        }
        boolean bl = multi = args[0].charAt(qq) == 'M';
        if (multi) {
            ++qq;
        }
        String charset = System.getProperty("egothor.stemmer.charset", "UTF-8");
        char[] optimizer = new char[args[0].length() - qq];
        for (i = 0; i < optimizer.length; ++i) {
            optimizer[i] = args[0].charAt(qq + i);
        }
        for (i = 1; i < args.length; ++i) {
            Diff diff = new Diff();
            boolean stems = false;
            int words = 0;
            Compile.allocTrie();
            System.out.println(args[i]);
            LineNumberReader in = new LineNumberReader(new BufferedReader(new InputStreamReader((InputStream)new FileInputStream(args[i]), charset)));
            String line = in.readLine();
            while (line != null) {
                try {
                    line = line.toLowerCase(Locale.ROOT);
                    StringTokenizer st = new StringTokenizer(line);
                    String stem = st.nextToken();
                    if (storeorig) {
                        trie.add(stem, "-a");
                        ++words;
                    }
                    while (st.hasMoreTokens()) {
                        String token = st.nextToken();
                        if (token.equals(stem)) continue;
                        trie.add(token, diff.exec(token, stem));
                        ++words;
                    }
                }
                catch (NoSuchElementException st) {
                    // empty catch block
                }
                line = in.readLine();
            }
            in.close();
            Optimizer o = new Optimizer();
            Optimizer2 o2 = new Optimizer2();
            Lift l = new Lift(true);
            Lift e = new Lift(false);
            Gener g = new Gener();
            block13: for (int j = 0; j < optimizer.length; ++j) {
                String prefix;
                switch (optimizer[j]) {
                    case 'G': {
                        trie = trie.reduce(g);
                        prefix = "G: ";
                        break;
                    }
                    case 'L': {
                        trie = trie.reduce(l);
                        prefix = "L: ";
                        break;
                    }
                    case 'E': {
                        trie = trie.reduce(e);
                        prefix = "E: ";
                        break;
                    }
                    case '2': {
                        trie = trie.reduce(o2);
                        prefix = "2: ";
                        break;
                    }
                    case '1': {
                        trie = trie.reduce(o);
                        prefix = "1: ";
                        break;
                    }
                    default: {
                        continue block13;
                    }
                }
                trie.printInfo(System.out, prefix + " ");
            }
            DataOutputStream os = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(args[i] + ".out")));
            os.writeUTF(args[0]);
            trie.store(os);
            os.close();
        }
    }

    static void allocTrie() {
        trie = multi ? new MultiTrie2(!backward) : new Trie(!backward);
    }
}

