/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.coalesce;

import com.mchange.v2.coalesce.Coalescer;
import com.mchange.v2.coalesce.CoalescerFactory;

public class CoalesceTest {
    static final int NUM_ITERS = 10000;
    static final Coalescer c = CoalescerFactory.createCoalescer(null, true, true);

    public static void main(String[] stringArray) {
        CoalesceTest.doTest();
        System.gc();
        System.err.println("num coalesced after gc: " + c.countCoalesced());
    }

    private static void doTest() {
        String[] stringArray = new String[10000];
        for (int i = 0; i < 10000; ++i) {
            stringArray[i] = new String("Hello");
        }
        long l = System.currentTimeMillis();
        for (int i = 0; i < 10000; ++i) {
            String string = stringArray[i];
            Object object = c.coalesce(string);
        }
        long l2 = System.currentTimeMillis() - l;
        System.out.println("avg time: " + (float)l2 / 10000.0f + "ms (" + 10000 + " iterations)");
        System.err.println("num coalesced: " + c.countCoalesced());
    }
}

