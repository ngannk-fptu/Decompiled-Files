/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.expression;

import org.apache.commons.beanutils.expression.Resolver;

public class DefaultResolver
implements Resolver {
    private static final char NESTED = '.';
    private static final char MAPPED_START = '(';
    private static final char MAPPED_END = ')';
    private static final char INDEXED_START = '[';
    private static final char INDEXED_END = ']';

    @Override
    public int getIndex(String expression) {
        if (expression == null || expression.length() == 0) {
            return -1;
        }
        for (int i = 0; i < expression.length(); ++i) {
            char c = expression.charAt(i);
            if (c == '.' || c == '(') {
                return -1;
            }
            if (c != '[') continue;
            int end = expression.indexOf(93, i);
            if (end < 0) {
                throw new IllegalArgumentException("Missing End Delimiter");
            }
            String value = expression.substring(i + 1, end);
            if (value.length() == 0) {
                throw new IllegalArgumentException("No Index Value");
            }
            int index = 0;
            try {
                index = Integer.parseInt(value, 10);
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Invalid index value '" + value + "'");
            }
            return index;
        }
        return -1;
    }

    @Override
    public String getKey(String expression) {
        if (expression == null || expression.length() == 0) {
            return null;
        }
        for (int i = 0; i < expression.length(); ++i) {
            char c = expression.charAt(i);
            if (c == '.' || c == '[') {
                return null;
            }
            if (c != '(') continue;
            int end = expression.indexOf(41, i);
            if (end < 0) {
                throw new IllegalArgumentException("Missing End Delimiter");
            }
            return expression.substring(i + 1, end);
        }
        return null;
    }

    @Override
    public String getProperty(String expression) {
        if (expression == null || expression.length() == 0) {
            return expression;
        }
        for (int i = 0; i < expression.length(); ++i) {
            char c = expression.charAt(i);
            if (c == '.') {
                return expression.substring(0, i);
            }
            if (c != '(' && c != '[') continue;
            return expression.substring(0, i);
        }
        return expression;
    }

    @Override
    public boolean hasNested(String expression) {
        if (expression == null || expression.length() == 0) {
            return false;
        }
        return this.remove(expression) != null;
    }

    @Override
    public boolean isIndexed(String expression) {
        if (expression == null || expression.length() == 0) {
            return false;
        }
        for (int i = 0; i < expression.length(); ++i) {
            char c = expression.charAt(i);
            if (c == '.' || c == '(') {
                return false;
            }
            if (c != '[') continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isMapped(String expression) {
        if (expression == null || expression.length() == 0) {
            return false;
        }
        for (int i = 0; i < expression.length(); ++i) {
            char c = expression.charAt(i);
            if (c == '.' || c == '[') {
                return false;
            }
            if (c != '(') continue;
            return true;
        }
        return false;
    }

    @Override
    public String next(String expression) {
        if (expression == null || expression.length() == 0) {
            return null;
        }
        boolean indexed = false;
        boolean mapped = false;
        for (int i = 0; i < expression.length(); ++i) {
            char c = expression.charAt(i);
            if (indexed) {
                if (c != ']') continue;
                return expression.substring(0, i + 1);
            }
            if (mapped) {
                if (c != ')') continue;
                return expression.substring(0, i + 1);
            }
            if (c == '.') {
                return expression.substring(0, i);
            }
            if (c == '(') {
                mapped = true;
                continue;
            }
            if (c != '[') continue;
            indexed = true;
        }
        return expression;
    }

    @Override
    public String remove(String expression) {
        if (expression == null || expression.length() == 0) {
            return null;
        }
        String property = this.next(expression);
        if (expression.length() == property.length()) {
            return null;
        }
        int start = property.length();
        if (expression.charAt(start) == '.') {
            ++start;
        }
        return expression.substring(start);
    }
}

