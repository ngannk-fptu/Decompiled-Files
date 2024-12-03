/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.algorithm;

import com.sun.xml.fastinfoset.algorithm.BuiltInEncodingAlgorithm;

public abstract class IEEE754FloatingPointEncodingAlgorithm
extends BuiltInEncodingAlgorithm {
    public static final int FLOAT_SIZE = 4;
    public static final int DOUBLE_SIZE = 8;
    public static final int FLOAT_MAX_CHARACTER_SIZE = 14;
    public static final int DOUBLE_MAX_CHARACTER_SIZE = 24;
}

