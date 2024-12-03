/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.utility;

import java.lang.reflect.Method;
import java.util.Comparator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum MethodComparator implements Comparator<Method>
{
    INSTANCE;


    @Override
    public int compare(Method left, Method right) {
        if (left == right) {
            return 0;
        }
        int comparison = left.getName().compareTo(right.getName());
        if (comparison == 0) {
            Class<?>[] rightParameterType;
            Class<?>[] leftParameterType = left.getParameterTypes();
            if (leftParameterType.length < (rightParameterType = right.getParameterTypes()).length) {
                return -1;
            }
            if (leftParameterType.length > rightParameterType.length) {
                return 1;
            }
            for (int index = 0; index < leftParameterType.length; ++index) {
                int comparisonParameterType = leftParameterType[index].getName().compareTo(rightParameterType[index].getName());
                if (comparisonParameterType == 0) continue;
                return comparisonParameterType;
            }
            return left.getReturnType().getName().compareTo(right.getReturnType().getName());
        }
        return comparison;
    }
}

