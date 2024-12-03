/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.tools;

import org.codehaus.groovy.ast.Parameter;

public class ParameterUtils {
    public static boolean parametersEqual(Parameter[] a, Parameter[] b) {
        if (a.length == b.length) {
            boolean answer = true;
            for (int i = 0; i < a.length; ++i) {
                if (a[i].getType().equals(b[i].getType())) continue;
                answer = false;
                break;
            }
            return answer;
        }
        return false;
    }
}

