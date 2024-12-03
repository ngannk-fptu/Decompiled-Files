/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.identicator.test;

import com.mchange.v1.identicator.IdHashSet;
import com.mchange.v1.identicator.Identicator;

public class TestIdHashSet {
    public static void main(String[] stringArray) {
        Identicator identicator = new Identicator(){

            @Override
            public boolean identical(Object object, Object object2) {
                return ((String)object).charAt(0) == ((String)object2).charAt(0);
            }

            @Override
            public int hash(Object object) {
                return ((String)object).charAt(0);
            }
        };
        IdHashSet idHashSet = new IdHashSet(identicator);
        System.out.println(idHashSet.add("hello"));
        System.out.println(idHashSet.add("world"));
        System.out.println(idHashSet.add("hi"));
        System.out.println(idHashSet.size());
        Object[] objectArray = idHashSet.toArray();
        for (int i = 0; i < objectArray.length; ++i) {
            System.out.println(objectArray[i]);
        }
    }
}

