/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.jrcs.diff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import org.apache.commons.jrcs.diff.DiffAlgorithm;
import org.apache.commons.jrcs.diff.DifferentiationFailedException;
import org.apache.commons.jrcs.diff.Revision;
import org.apache.commons.jrcs.diff.myers.MyersDiff;
import org.apache.commons.jrcs.util.ToString;

public class Diff
extends ToString {
    public static final String NL = System.getProperty("line.separator");
    public static final String RCS_EOL = "\n";
    protected final Object[] orig;
    protected DiffAlgorithm algorithm;

    public Diff(Object[] original) {
        this(original, null);
    }

    public Diff(Object[] original, DiffAlgorithm algorithm) {
        if (original == null) {
            throw new IllegalArgumentException();
        }
        this.orig = original;
        this.algorithm = algorithm != null ? algorithm : this.defaultAlgorithm();
    }

    protected DiffAlgorithm defaultAlgorithm() {
        return new MyersDiff();
    }

    public static Revision diff(Object[] orig, Object[] rev) throws DifferentiationFailedException {
        if (orig == null || rev == null) {
            throw new IllegalArgumentException();
        }
        return Diff.diff(orig, rev, null);
    }

    public static Revision diff(Object[] orig, Object[] rev, DiffAlgorithm algorithm) throws DifferentiationFailedException {
        if (orig == null || rev == null) {
            throw new IllegalArgumentException();
        }
        return new Diff(orig, algorithm).diff(rev);
    }

    public Revision diff(Object[] rev) throws DifferentiationFailedException {
        return this.algorithm.diff(this.orig, rev);
    }

    public static boolean compare(Object[] orig, Object[] rev) {
        if (orig.length != rev.length) {
            return false;
        }
        int i = 0;
        while (i < orig.length) {
            if (!orig[i].equals(rev[i])) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public static String arrayToString(Object[] o) {
        return ToString.arrayToString(o, NL);
    }

    public static Object[] editAll(Object[] text) {
        Object[] result = new String[text.length];
        int i = 0;
        while (i < text.length) {
            result[i] = text[i] + " <edited>";
            ++i;
        }
        return result;
    }

    public static Object[] randomEdit(Object[] text) {
        return Diff.randomEdit(text, text.length);
    }

    public static Object[] randomEdit(Object[] text, long seed) {
        ArrayList<Object> result = new ArrayList<Object>(Arrays.asList(text));
        Random r = new Random(seed);
        int nops = r.nextInt(10);
        int i = 0;
        while (i < nops) {
            boolean del = r.nextBoolean();
            int pos = r.nextInt(result.size() + 1);
            int len = Math.min(result.size() - pos, 1 + r.nextInt(4));
            if (del && result.size() > 0) {
                result.subList(pos, pos + len).clear();
            } else {
                int k = 0;
                while (k < len) {
                    result.add(pos, "[" + i + "] random edit[" + i + "][" + i + "]");
                    ++k;
                    ++pos;
                }
            }
            ++i;
        }
        return result.toArray();
    }

    public static Object[] shuffle(Object[] text) {
        return Diff.shuffle(text, text.length);
    }

    public static Object[] shuffle(Object[] text, long seed) {
        ArrayList<Object> result = new ArrayList<Object>(Arrays.asList(text));
        Collections.shuffle(result);
        return result.toArray();
    }

    public static Object[] randomSequence(int size) {
        return Diff.randomSequence(size, size);
    }

    public static Object[] randomSequence(int size, long seed) {
        Object[] result = new Integer[size];
        Random r = new Random(seed);
        int i = 0;
        while (i < result.length) {
            result[i] = new Integer(r.nextInt(size));
            ++i;
        }
        return result;
    }
}

