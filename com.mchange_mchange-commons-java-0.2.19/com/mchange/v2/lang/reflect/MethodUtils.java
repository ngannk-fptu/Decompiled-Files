/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.lang.reflect;

import java.lang.reflect.Method;
import java.util.Comparator;

public final class MethodUtils {
    public static final Comparator METHOD_COMPARATOR = new Comparator(){

        public int compare(Object object, Object object2) {
            String string;
            Method method = (Method)object;
            Method method2 = (Method)object2;
            String string2 = method.getName();
            int n = String.CASE_INSENSITIVE_ORDER.compare(string2, string = method2.getName());
            if (n == 0) {
                if (string2.equals(string)) {
                    Class<?>[] classArray;
                    Class<?>[] classArray2 = method.getParameterTypes();
                    if (classArray2.length < (classArray = method2.getParameterTypes()).length) {
                        n = -1;
                    } else if (classArray2.length > classArray.length) {
                        n = 1;
                    } else {
                        String string3;
                        String string4;
                        int n2 = classArray2.length;
                        for (int i = 0; i < n2 && (n = (string4 = classArray2[i].getName()).compareTo(string3 = classArray[i].getName())) == 0; ++i) {
                        }
                    }
                } else {
                    n = string2.compareTo(string);
                }
            }
            return n;
        }
    };
}

