/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.random;

import com.opensymphony.util.GUID;

public class Test {
    public static void main(String[] args) {
        for (int i = 1; i <= 100; ++i) {
            System.out.println("guid = " + GUID.generateFormattedGUID());
        }
    }
}

