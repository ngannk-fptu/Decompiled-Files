/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.algorithm;

import com.sun.xml.fastinfoset.algorithm.BASE64EncodingAlgorithm;
import com.sun.xml.fastinfoset.algorithm.BooleanEncodingAlgorithm;
import com.sun.xml.fastinfoset.algorithm.BuiltInEncodingAlgorithm;
import com.sun.xml.fastinfoset.algorithm.DoubleEncodingAlgorithm;
import com.sun.xml.fastinfoset.algorithm.FloatEncodingAlgorithm;
import com.sun.xml.fastinfoset.algorithm.HexadecimalEncodingAlgorithm;
import com.sun.xml.fastinfoset.algorithm.IntEncodingAlgorithm;
import com.sun.xml.fastinfoset.algorithm.LongEncodingAlgorithm;
import com.sun.xml.fastinfoset.algorithm.ShortEncodingAlgorithm;
import com.sun.xml.fastinfoset.algorithm.UUIDEncodingAlgorithm;

public final class BuiltInEncodingAlgorithmFactory {
    private static final BuiltInEncodingAlgorithm[] table = new BuiltInEncodingAlgorithm[10];
    public static final HexadecimalEncodingAlgorithm hexadecimalEncodingAlgorithm = new HexadecimalEncodingAlgorithm();
    public static final BASE64EncodingAlgorithm base64EncodingAlgorithm = new BASE64EncodingAlgorithm();
    public static final BooleanEncodingAlgorithm booleanEncodingAlgorithm = new BooleanEncodingAlgorithm();
    public static final ShortEncodingAlgorithm shortEncodingAlgorithm = new ShortEncodingAlgorithm();
    public static final IntEncodingAlgorithm intEncodingAlgorithm = new IntEncodingAlgorithm();
    public static final LongEncodingAlgorithm longEncodingAlgorithm = new LongEncodingAlgorithm();
    public static final FloatEncodingAlgorithm floatEncodingAlgorithm = new FloatEncodingAlgorithm();
    public static final DoubleEncodingAlgorithm doubleEncodingAlgorithm = new DoubleEncodingAlgorithm();
    public static final UUIDEncodingAlgorithm uuidEncodingAlgorithm = new UUIDEncodingAlgorithm();

    public static BuiltInEncodingAlgorithm getAlgorithm(int index) {
        return table[index];
    }

    static {
        BuiltInEncodingAlgorithmFactory.table[0] = hexadecimalEncodingAlgorithm;
        BuiltInEncodingAlgorithmFactory.table[1] = base64EncodingAlgorithm;
        BuiltInEncodingAlgorithmFactory.table[2] = shortEncodingAlgorithm;
        BuiltInEncodingAlgorithmFactory.table[3] = intEncodingAlgorithm;
        BuiltInEncodingAlgorithmFactory.table[4] = longEncodingAlgorithm;
        BuiltInEncodingAlgorithmFactory.table[5] = booleanEncodingAlgorithm;
        BuiltInEncodingAlgorithmFactory.table[6] = floatEncodingAlgorithm;
        BuiltInEncodingAlgorithmFactory.table[7] = doubleEncodingAlgorithm;
        BuiltInEncodingAlgorithmFactory.table[8] = uuidEncodingAlgorithm;
    }
}

