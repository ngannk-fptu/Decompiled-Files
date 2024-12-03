/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.WildcardHelper;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MethodFilterInterceptorUtil {
    public static boolean applyMethod(Set<String> excludeMethods, Set<String> includeMethods, String method) {
        int[] compiledPattern;
        HashMap<String, String> matchedPatterns;
        boolean matches;
        boolean needsPatternMatch = false;
        for (String includeMethod : includeMethods) {
            if ("*".equals(includeMethod) || !includeMethod.contains("*")) continue;
            needsPatternMatch = true;
            break;
        }
        for (String excludeMethod : excludeMethods) {
            if ("*".equals(excludeMethod) || !excludeMethod.contains("*")) continue;
            needsPatternMatch = true;
            break;
        }
        if (!needsPatternMatch && (includeMethods.contains("*") || includeMethods.size() == 0) && excludeMethods != null && excludeMethods.contains(method) && !includeMethods.contains(method)) {
            return false;
        }
        WildcardHelper wildcard = new WildcardHelper();
        String methodCopy = method == null ? "" : new String(method);
        for (String pattern : includeMethods) {
            if (!(pattern.contains("*") ? (matches = wildcard.match((Map<String, String>)(matchedPatterns = new HashMap<String, String>()), methodCopy, compiledPattern = wildcard.compilePattern(pattern))) : pattern.equals(methodCopy))) continue;
            return true;
        }
        if (excludeMethods.contains("*")) {
            return false;
        }
        for (String pattern : excludeMethods) {
            if (!(pattern.contains("*") ? (matches = wildcard.match((Map<String, String>)(matchedPatterns = new HashMap()), methodCopy, compiledPattern = wildcard.compilePattern(pattern))) : pattern.equals(methodCopy))) continue;
            return false;
        }
        return includeMethods.size() == 0 || includeMethods.contains(method) || includeMethods.contains("*");
    }

    public static boolean applyMethod(String excludeMethods, String includeMethods, String method) {
        Set<String> includeMethodsSet = TextParseUtil.commaDelimitedStringToSet(includeMethods == null ? "" : includeMethods);
        Set<String> excludeMethodsSet = TextParseUtil.commaDelimitedStringToSet(excludeMethods == null ? "" : excludeMethods);
        return MethodFilterInterceptorUtil.applyMethod(excludeMethodsSet, includeMethodsSet, method);
    }
}

