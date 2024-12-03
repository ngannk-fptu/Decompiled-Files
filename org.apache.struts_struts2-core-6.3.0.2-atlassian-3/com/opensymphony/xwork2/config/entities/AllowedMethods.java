/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.config.entities;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AllowedMethods {
    private static final Logger LOG = LogManager.getLogger(AllowedMethods.class);
    private Set<AllowedMethod> allowedMethods;
    private final boolean strictMethodInvocation;
    private String defaultRegex;

    public static AllowedMethods build(boolean strictMethodInvocation, Set<String> methods, String defaultRegex) {
        HashSet<AllowedMethod> allowedMethods = new HashSet<AllowedMethod>();
        for (String method : methods) {
            boolean isPattern = false;
            StringBuilder methodPattern = new StringBuilder();
            int len = method.length();
            for (int x = 0; x < len; ++x) {
                char c = method.charAt(x);
                if (x < len - 2 && c == '{' && '}' == method.charAt(x + 2)) {
                    methodPattern.append(defaultRegex);
                    isPattern = true;
                    x += 2;
                    continue;
                }
                methodPattern.append(c);
            }
            if (isPattern && !method.startsWith("regex:") && !strictMethodInvocation) {
                allowedMethods.add(new PatternAllowedMethod(methodPattern.toString(), method));
                continue;
            }
            if (method.startsWith("regex:")) {
                String pattern = method.substring(method.indexOf(58) + 1);
                allowedMethods.add(new PatternAllowedMethod(pattern, method));
                continue;
            }
            if (method.contains("*") && !method.startsWith("regex:") && !strictMethodInvocation) {
                String pattern = method.replace("*", defaultRegex);
                allowedMethods.add(new PatternAllowedMethod(pattern, method));
                continue;
            }
            if (!isPattern) {
                allowedMethods.add(new LiteralAllowedMethod(method));
                continue;
            }
            LOG.trace("Ignoring method name: [{}] when SMI is set to [{}]", (Object)method, (Object)strictMethodInvocation);
        }
        LOG.debug("Defined allowed methods: {}", allowedMethods);
        return new AllowedMethods(strictMethodInvocation, allowedMethods, defaultRegex);
    }

    private AllowedMethods(boolean strictMethodInvocation, Set<AllowedMethod> methods, String defaultRegex) {
        this.strictMethodInvocation = strictMethodInvocation;
        this.defaultRegex = defaultRegex;
        this.allowedMethods = Collections.unmodifiableSet(methods);
    }

    public boolean isAllowed(String method) {
        for (AllowedMethod allowedMethod : this.allowedMethods) {
            if (!allowedMethod.isAllowed(method)) continue;
            return true;
        }
        return false;
    }

    public Set<String> list() {
        HashSet<String> result = new HashSet<String>();
        for (AllowedMethod allowedMethod : this.allowedMethods) {
            result.add(allowedMethod.original());
        }
        return result;
    }

    public String getDefaultRegex() {
        return this.defaultRegex;
    }

    public boolean isStrictMethodInvocation() {
        return this.strictMethodInvocation;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AllowedMethods that = (AllowedMethods)o;
        return this.allowedMethods.equals(that.allowedMethods);
    }

    public int hashCode() {
        return this.allowedMethods.hashCode();
    }

    public String toString() {
        return "allowedMethods=" + this.allowedMethods;
    }

    private static class LiteralAllowedMethod
    implements AllowedMethod {
        private String allowedMethod;

        public LiteralAllowedMethod(String allowedMethod) {
            this.allowedMethod = allowedMethod;
        }

        @Override
        public boolean isAllowed(String methodName) {
            return methodName.equals(this.allowedMethod);
        }

        @Override
        public String original() {
            return this.allowedMethod;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            LiteralAllowedMethod that = (LiteralAllowedMethod)o;
            return this.allowedMethod.equals(that.allowedMethod);
        }

        public int hashCode() {
            return this.allowedMethod.hashCode();
        }

        public String toString() {
            return "LiteralAllowedMethod{allowedMethod='" + this.allowedMethod + '\'' + '}';
        }
    }

    private static class PatternAllowedMethod
    implements AllowedMethod {
        private final Pattern allowedMethodPattern;
        private String original;

        public PatternAllowedMethod(String pattern, String original) {
            this.original = original;
            this.allowedMethodPattern = Pattern.compile(pattern);
        }

        @Override
        public boolean isAllowed(String methodName) {
            return this.allowedMethodPattern.matcher(methodName).matches();
        }

        @Override
        public String original() {
            return this.original;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            PatternAllowedMethod that = (PatternAllowedMethod)o;
            return this.allowedMethodPattern.pattern().equals(that.allowedMethodPattern.pattern());
        }

        public int hashCode() {
            return this.allowedMethodPattern.pattern().hashCode();
        }

        public String toString() {
            return "PatternAllowedMethod{allowedMethodPattern=" + this.allowedMethodPattern + ", original='" + this.original + '\'' + '}';
        }
    }

    private static interface AllowedMethod {
        public boolean isAllowed(String var1);

        public String original();
    }
}

