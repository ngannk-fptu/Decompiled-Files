/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl.number.range;

public class PrefixInfixSuffixLengthHelper {
    public int lengthPrefix = 0;
    public int length1 = 0;
    public int lengthInfix = 0;
    public int length2 = 0;
    public int lengthSuffix = 0;

    public int index0() {
        return this.lengthPrefix;
    }

    public int index1() {
        return this.lengthPrefix + this.length1;
    }

    public int index2() {
        return this.lengthPrefix + this.length1 + this.lengthInfix;
    }

    public int index3() {
        return this.lengthPrefix + this.length1 + this.lengthInfix + this.length2;
    }

    public int index4() {
        return this.lengthPrefix + this.length1 + this.lengthInfix + this.length2 + this.lengthSuffix;
    }
}

