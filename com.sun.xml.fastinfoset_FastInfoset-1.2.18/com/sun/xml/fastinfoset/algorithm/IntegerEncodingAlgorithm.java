/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.algorithm;

import com.sun.xml.fastinfoset.algorithm.BuiltInEncodingAlgorithm;

public abstract class IntegerEncodingAlgorithm
extends BuiltInEncodingAlgorithm {
    public static final int SHORT_SIZE = 2;
    public static final int INT_SIZE = 4;
    public static final int LONG_SIZE = 8;
    public static final int SHORT_MAX_CHARACTER_SIZE = 6;
    public static final int INT_MAX_CHARACTER_SIZE = 11;
    public static final int LONG_MAX_CHARACTER_SIZE = 20;
}

