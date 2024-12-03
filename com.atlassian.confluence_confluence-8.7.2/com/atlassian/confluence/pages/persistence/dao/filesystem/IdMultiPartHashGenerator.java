/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.persistence.dao.filesystem;

import java.util.ArrayList;
import java.util.List;

public class IdMultiPartHashGenerator {
    private static final int SPLIT_MAX_SIZE = 9;
    private int split;
    private int modulo;
    private int parts;

    public IdMultiPartHashGenerator(int split, int modulo, int parts) {
        if (split < 1 || split > 9) {
            throw new IllegalArgumentException("The split parameter must be in the range 1 - 9");
        }
        if (modulo < 1) {
            throw new IllegalArgumentException("The modulo parameter must be greater than 0");
        }
        if (parts < 1) {
            throw new IllegalArgumentException("The parts parameter must be greater than 0");
        }
        this.split = split;
        this.modulo = modulo;
        this.parts = parts;
    }

    public List<Integer> generate(long value) {
        if (value < 0L) {
            throw new IllegalArgumentException("The value to split must be larger than 0");
        }
        String strValue = String.valueOf(value);
        int strPos = strValue.length();
        ArrayList<Integer> multipartHash = new ArrayList<Integer>(this.parts);
        for (int i = 0; i < this.parts; ++i) {
            String subPart;
            if ((strPos -= this.split) <= 0) {
                subPart = strValue.substring(0, strPos + this.split);
                multipartHash.add(this.modulo(subPart));
                this.padToSize(multipartHash);
                break;
            }
            subPart = strValue.substring(strPos, strPos + this.split);
            multipartHash.add(this.modulo(subPart));
        }
        return multipartHash;
    }

    private int modulo(String value) {
        int intValue = Integer.parseInt(value);
        return intValue % this.modulo;
    }

    private List<Integer> padToSize(List<Integer> multiPartHash) {
        for (int i = multiPartHash.size(); i < this.parts; ++i) {
            multiPartHash.add(0);
        }
        return multiPartHash;
    }

    public int getSplit() {
        return this.split;
    }
}

