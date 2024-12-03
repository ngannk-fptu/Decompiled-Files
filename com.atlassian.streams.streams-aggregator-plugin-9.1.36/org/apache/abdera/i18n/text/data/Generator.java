/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.text.data;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.util.regex.MatchResult;

public class Generator {
    public static void main(String ... args) {
        PrintWriter pw = new PrintWriter(System.out);
        BitSet exclusions = Generator.getExclusions(args[0]);
        Generator.writeDecomposition(pw, args[1], exclusions);
    }

    private static void writeDecomposition(PrintWriter pw, String file, BitSet excluded) {
        Scanner s = Generator.read(file);
        BitSet compat = new BitSet();
        ArrayList<Integer> cc_idx = new ArrayList<Integer>();
        ArrayList<Integer> cc_data = new ArrayList<Integer>();
        ArrayList<Integer> decomp_idx = new ArrayList<Integer>();
        ArrayList<Integer[]> decomp_data = new ArrayList<Integer[]>();
        ArrayList<Integer[]> comps = new ArrayList<Integer[]>();
        ArrayList<Integer[]> hanguls = new ArrayList<Integer[]>();
        while (s.hasNextLine() && s.hasNext()) {
            String dc;
            if (s.findInLine("([^;\\s]*);[^;]*;[^;]*;([^;]*);[^;]*;([^;]*);.*") == null) continue;
            MatchResult result = s.match();
            int codepoint = Integer.parseInt(result.group(1), 16);
            int cc = Integer.parseInt(result.group(2));
            if (cc != 0) {
                cc_idx.add(codepoint);
                cc_data.add(cc);
            }
            if ((dc = result.group(3).trim()).length() <= 0) continue;
            if (dc.charAt(0) == '<') {
                compat.set(codepoint);
            }
            dc = dc.substring(dc.indexOf(62) + 1).trim();
            String[] points = dc.split("\\s");
            ArrayList<Integer> list = new ArrayList<Integer>();
            for (int n = 0; n < points.length; ++n) {
                list.add(Integer.parseInt(points[n], 16));
            }
            decomp_idx.add(codepoint);
            decomp_data.add(list.toArray(new Integer[list.size()]));
            if (compat.get(codepoint) || excluded.get(codepoint)) continue;
            int f = list.size() > 1 ? (int)((Integer)list.get(0)) : 0;
            char l = list.size() > 1 ? (char)((Integer)list.get(1)).intValue() : (char)((Integer)list.get(0)).intValue();
            comps.add(new Integer[]{f << 16 | l, codepoint});
        }
        for (int z = 0; z < 11172; ++z) {
            int t = z % 28;
            char f = t != 0 ? (char)(44032 + z - t) : (char)(4352 + z / 588);
            char e = t != 0 ? (char)(4519 + t) : (char)(4449 + z % 588 / 28);
            int pair = f << 16 | e;
            int value = z + 44032;
            hanguls.add(new Integer[]{pair, value});
        }
        Comparator<Integer[]> comp = new Comparator<Integer[]>(){

            @Override
            public int compare(Integer[] o1, Integer[] o2) {
                int i2;
                int i1 = o1[0];
                return i1 < (i2 = o2[0].intValue()) ? -1 : (i1 > i2 ? 1 : 0);
            }
        };
        Collections.sort(comps, comp);
        Collections.sort(hanguls, comp);
        pw.print("  private static int[] getCompat() { return new int[] {");
        int i = compat.nextSetBit(0);
        int n = 0;
        pw.print(i);
        i = compat.nextSetBit(i);
        while (i >= 0) {
            pw.print(',');
            pw.print(i);
            if (n % 20 == 0) {
                pw.print("\n    ");
                n = 0;
            }
            i = compat.nextSetBit(i + 1);
            ++n;
        }
        pw.print("};}\n\n");
        pw.flush();
        pw.print("  private static int[] getCCIdx() { return new int[] {");
        i = 0;
        n = 0;
        while (i < cc_idx.size()) {
            pw.print(cc_idx.get(i));
            if (n % 20 == 0) {
                pw.print("\n    ");
                n = 0;
            }
            if (i < cc_idx.size() - 1) {
                pw.print(',');
            }
            ++i;
            ++n;
        }
        pw.print("};}\n\n");
        pw.flush();
        pw.print("  private static int[] getCCData() { return new int[] {");
        i = 0;
        n = 0;
        while (i < cc_data.size()) {
            pw.print(cc_data.get(i));
            if (n % 20 == 0) {
                pw.print("\n    ");
                n = 0;
            }
            if (i < cc_data.size() - 1) {
                pw.print(',');
            }
            ++i;
            ++n;
        }
        pw.print("};}\n\n");
        pw.flush();
        pw.print("  private static int[] getComposeIdx() { return new int[] {");
        i = 0;
        n = 0;
        while (i < comps.size()) {
            pw.print(((Integer[])comps.get(i))[0]);
            if (n % 20 == 0) {
                pw.print("\n    ");
                n = 0;
            }
            if (i < comps.size() - 1) {
                pw.print(',');
            }
            ++i;
            ++n;
        }
        pw.print("};}\n\n");
        pw.flush();
        pw.print("  private static int[] getComposeData() { return new int[] {");
        i = 0;
        n = 0;
        while (i < comps.size()) {
            pw.print(((Integer[])comps.get(i))[1]);
            if (n % 20 == 0) {
                pw.print("\n    ");
                n = 0;
            }
            if (i < comps.size() - 1) {
                pw.print(',');
            }
            ++i;
            ++n;
        }
        pw.print("};}\n\n");
        pw.flush();
        pw.print("  private static int[] getDecompIdx() { return new int[] {");
        i = 0;
        n = 0;
        while (i < decomp_idx.size()) {
            pw.print(decomp_idx.get(i));
            if (n % 20 == 0) {
                pw.print("\n    ");
                n = 0;
            }
            if (i < decomp_idx.size() - 1) {
                pw.print(',');
            }
            ++i;
            ++n;
        }
        pw.print("};}\n\n");
        int sets = 2;
        int size = decomp_idx.size() / sets;
        i = 0;
        for (int a = 0; a < sets; ++a) {
            pw.print("  private static int[][] getDecompData" + (a + 1) + "() { return new int[][] {");
            i = a * i;
            n = 0;
            while (i < size * (a + 1)) {
                Integer[] data = (Integer[])decomp_data.get(i);
                pw.print('{');
                for (int q = 0; q < data.length; ++q) {
                    pw.print(data[q]);
                    if (q >= data.length - 1) continue;
                    pw.print(',');
                }
                pw.print('}');
                if (n % 20 == 0) {
                    pw.print("\n    ");
                    n = 0;
                }
                if (i < decomp_idx.size() - 1) {
                    pw.print(',');
                }
                ++i;
                ++n;
            }
            pw.print("};}\n\n");
        }
        pw.println("  private static int[][] getDecompData() {");
        for (n = 0; n < sets; ++n) {
            pw.println("    int[][] d" + (n + 1) + " = getDecompData" + (n + 1) + "();");
        }
        pw.print("    int[][] d = new int[");
        for (n = 0; n < sets; ++n) {
            pw.print("d" + (n + 1) + ".length");
            if (n >= sets - 1) continue;
            pw.print('+');
        }
        pw.println("][];");
        String len = "0";
        for (n = 0; n < sets; ++n) {
            pw.println("    System.arraycopy(d" + (n + 1) + ",0,d," + len + ",d" + (n + 1) + ".length);");
            len = "d" + (n + 1) + ".length";
        }
        pw.println("    return d;}");
        pw.flush();
        sets = 2;
        i = 0;
        int e = 0;
        size = hanguls.size() / sets;
        for (int a = 0; a < sets; ++a) {
            pw.print("  private static int[] getHangulPairs" + (a + 1) + "() { return new int[] {");
            i = a * i;
            n = 0;
            while (i < size * (a + 1)) {
                pw.print(((Integer[])hanguls.get(i))[0]);
                if (n % 20 == 0) {
                    pw.print("\n    ");
                    n = 0;
                }
                if (i < hanguls.size() - 1) {
                    pw.print(',');
                }
                ++i;
                ++n;
            }
            pw.print("};}\n\n");
            pw.flush();
            pw.print("  private static int[] getHangulCodepoints" + (a + 1) + "() { return new int[] {");
            e = a * e;
            n = 0;
            while (e < size * (a + 1)) {
                pw.print(((Integer[])hanguls.get(e))[1]);
                if (n % 20 == 0) {
                    pw.print("\n    ");
                    n = 0;
                }
                if (e < hanguls.size() - 1) {
                    pw.print(',');
                }
                ++e;
                ++n;
            }
            pw.print("};}\n\n");
            pw.flush();
        }
        pw.println("  private static int[] getHangulPairs() {");
        for (n = 0; n < sets; ++n) {
            pw.println("    int[] d" + (n + 1) + " = getHangulPairs" + (n + 1) + "();");
        }
        pw.print("    int[] d = new int[");
        for (n = 0; n < sets; ++n) {
            pw.print("d" + (n + 1) + ".length");
            if (n >= sets - 1) continue;
            pw.print('+');
        }
        pw.println("];");
        len = "0";
        for (n = 0; n < sets; ++n) {
            pw.println("    System.arraycopy(d" + (n + 1) + ",0,d," + len + ",d" + (n + 1) + ".length);");
            len = "d" + (n + 1) + ".length";
        }
        pw.println("    return d;}");
        pw.flush();
        pw.println("  private static int[] getHangulCodepoints() {");
        for (n = 0; n < sets; ++n) {
            pw.println("    int[] d" + (n + 1) + " = getHangulCodepoints" + (n + 1) + "();");
        }
        pw.print("    int[] d = new int[");
        for (n = 0; n < sets; ++n) {
            pw.print("d" + (n + 1) + ".length");
            if (n >= sets - 1) continue;
            pw.print('+');
        }
        pw.println("];");
        len = "0";
        for (n = 0; n < sets; ++n) {
            pw.println("    System.arraycopy(d" + (n + 1) + ",0,d," + len + ",d" + (n + 1) + ".length);");
            len = "d" + (n + 1) + ".length";
        }
        pw.println("    return d;}\n\n");
        pw.flush();
    }

    private static BitSet getExclusions(String file) {
        Scanner s = Generator.read(file).useDelimiter("\\s*#.*");
        BitSet set = new BitSet();
        while (s.hasNext()) {
            String exc = s.next().trim();
            if (exc.length() <= 0) continue;
            int i = Integer.parseInt(exc, 16);
            set.set(i);
        }
        return set;
    }

    private static Scanner read(String f) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream in = cl.getResourceAsStream(f);
        if (in == null) {
            try {
                in = new FileInputStream(f);
            }
            catch (Exception e) {
                // empty catch block
            }
        }
        if (in == null) {
            try {
                URL url = new URL(f);
                in = url.openStream();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return in != null ? new Scanner(in) : null;
    }
}

