/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.asm.internal;

import org.aspectj.asm.internal.CharOperation;

public class NameConvertor {
    private static final char BOOLEAN = 'Z';
    private static final char BYTE = 'B';
    private static final char CHAR = 'C';
    private static final char DOUBLE = 'D';
    private static final char FLOAT = 'F';
    private static final char INT = 'I';
    private static final char LONG = 'J';
    private static final char SHORT = 'S';
    private static final char ARRAY = '[';
    private static final char RESOLVED = 'L';
    private static final char UNRESOLVED = 'Q';
    public static final char PARAMETERIZED = 'P';
    private static final char[] BOOLEAN_NAME = new char[]{'b', 'o', 'o', 'l', 'e', 'a', 'n'};
    private static final char[] BYTE_NAME = new char[]{'b', 'y', 't', 'e'};
    private static final char[] CHAR_NAME = new char[]{'c', 'h', 'a', 'r'};
    private static final char[] DOUBLE_NAME = new char[]{'d', 'o', 'u', 'b', 'l', 'e'};
    private static final char[] FLOAT_NAME = new char[]{'f', 'l', 'o', 'a', 't'};
    private static final char[] INT_NAME = new char[]{'i', 'n', 't'};
    private static final char[] LONG_NAME = new char[]{'l', 'o', 'n', 'g'};
    private static final char[] SHORT_NAME = new char[]{'s', 'h', 'o', 'r', 't'};
    private static final char[] SQUARE_BRACKETS = new char[]{'[', ']'};
    private static final char[] GREATER_THAN = new char[]{'>'};
    private static final char[] LESS_THAN = new char[]{'<'};
    private static final char[] COMMA = new char[]{','};
    private static final char[] BACKSLASH_LESSTHAN = new char[]{'\\', '<'};
    private static final char[] SEMICOLON = new char[]{';'};

    public static char[] convertFromSignature(char[] c) {
        int lt = CharOperation.indexOf('<', c);
        int sc = CharOperation.indexOf(';', c);
        int gt = CharOperation.indexOf('>', c);
        int smallest = 0;
        if (lt == -1 && sc == -1 && gt == -1) {
            return NameConvertor.getFullyQualifiedTypeName(c);
        }
        smallest = !(lt == -1 || sc != -1 && lt > sc || gt != -1 && lt > gt) ? lt : (!(sc == -1 || lt != -1 && sc > lt || gt != -1 && sc > gt) ? sc : gt);
        char[] first = CharOperation.subarray(c, 0, smallest);
        char[] second = CharOperation.subarray(c, smallest + 1, c.length);
        if (smallest == 0 && first.length == 0 && c[0] == '>') {
            return GREATER_THAN;
        }
        if (first.length == 1 && second.length == 0) {
            return first;
        }
        if (second.length == 0 || second.length == 1 && second[0] == ';') {
            return NameConvertor.convertFromSignature(first);
        }
        if (smallest == lt) {
            char[] inclLT = CharOperation.concat(NameConvertor.convertFromSignature(first), LESS_THAN);
            return CharOperation.concat(inclLT, NameConvertor.convertFromSignature(second));
        }
        if (smallest == gt) {
            char[] inclLT = CharOperation.concat(NameConvertor.convertFromSignature(first), GREATER_THAN);
            return CharOperation.concat(inclLT, NameConvertor.convertFromSignature(second));
        }
        if (second.length != 2) {
            char[] inclComma = CharOperation.concat(NameConvertor.convertFromSignature(first), COMMA);
            return CharOperation.concat(inclComma, NameConvertor.convertFromSignature(second));
        }
        return CharOperation.concat(NameConvertor.convertFromSignature(first), NameConvertor.convertFromSignature(second));
    }

    private static char[] getFullyQualifiedTypeName(char[] c) {
        if (c.length == 0) {
            return c;
        }
        if (c[0] == 'Z') {
            return BOOLEAN_NAME;
        }
        if (c[0] == 'B') {
            return BYTE_NAME;
        }
        if (c[0] == 'C') {
            return CHAR_NAME;
        }
        if (c[0] == 'D') {
            return DOUBLE_NAME;
        }
        if (c[0] == 'F') {
            return FLOAT_NAME;
        }
        if (c[0] == 'I') {
            return INT_NAME;
        }
        if (c[0] == 'J') {
            return LONG_NAME;
        }
        if (c[0] == 'S') {
            return SHORT_NAME;
        }
        if (c[0] == '[') {
            return CharOperation.concat(NameConvertor.getFullyQualifiedTypeName(CharOperation.subarray(c, 1, c.length)), SQUARE_BRACKETS);
        }
        char[] type = CharOperation.subarray(c, 1, c.length);
        CharOperation.replace(type, '/', '.');
        return type;
    }

    public static char[] createShortName(char[] c, boolean haveFullyQualifiedAtLeastOneThing, boolean needsFullyQualifiedFirstEntry) {
        if (c[0] == '[') {
            char[] ret = CharOperation.concat(new char[]{'\\', '['}, NameConvertor.createShortName(CharOperation.subarray(c, 1, c.length), haveFullyQualifiedAtLeastOneThing, needsFullyQualifiedFirstEntry));
            return ret;
        }
        if (c[0] == '+') {
            char[] ret = CharOperation.concat(new char[]{'+'}, NameConvertor.createShortName(CharOperation.subarray(c, 1, c.length), haveFullyQualifiedAtLeastOneThing, needsFullyQualifiedFirstEntry));
            return ret;
        }
        if (c[0] == '*') {
            return c;
        }
        int lt = CharOperation.indexOf('<', c);
        int sc = CharOperation.indexOf(';', c);
        int gt = CharOperation.indexOf('>', c);
        int smallest = 0;
        if (lt == -1 && sc == -1 && gt == -1) {
            if (!needsFullyQualifiedFirstEntry) {
                return NameConvertor.getTypeName(c, true);
            }
            return NameConvertor.getTypeName(c, haveFullyQualifiedAtLeastOneThing);
        }
        smallest = !(lt == -1 || sc != -1 && lt > sc || gt != -1 && lt > gt) ? lt : (!(sc == -1 || lt != -1 && sc > lt || gt != -1 && sc > gt) ? sc : gt);
        char[] first = CharOperation.subarray(c, 0, smallest);
        char[] second = CharOperation.subarray(c, smallest + 1, c.length);
        if (smallest == 0 && first.length == 0 && c[0] == '>') {
            return c;
        }
        if (first.length == 1 && second.length == 0) {
            return first;
        }
        if (second.length == 0 || second.length == 1 && second[0] == ';') {
            return CharOperation.concat(NameConvertor.createShortName(first, haveFullyQualifiedAtLeastOneThing, needsFullyQualifiedFirstEntry), new char[]{';'});
        }
        if (smallest == lt) {
            char[] inclLT = CharOperation.concat(NameConvertor.createShortName(first, haveFullyQualifiedAtLeastOneThing, true), BACKSLASH_LESSTHAN);
            return CharOperation.concat(inclLT, NameConvertor.createShortName(second, true, false));
        }
        if (smallest == gt) {
            char[] inclLT = CharOperation.concat(NameConvertor.createShortName(first, haveFullyQualifiedAtLeastOneThing, needsFullyQualifiedFirstEntry), GREATER_THAN);
            return CharOperation.concat(inclLT, NameConvertor.createShortName(second, true, false));
        }
        char[] firstTypeParam = CharOperation.concat(NameConvertor.createShortName(first, haveFullyQualifiedAtLeastOneThing, false), SEMICOLON);
        return CharOperation.concat(firstTypeParam, NameConvertor.createShortName(second, true, false));
    }

    public static char[] getTypeName(char[] name, boolean haveFullyQualifiedAtLeastOneThing) {
        if (!haveFullyQualifiedAtLeastOneThing) {
            if (name[0] == 'L' || name[0] == 'P') {
                char[] sub = CharOperation.subarray(name, 1, name.length);
                CharOperation.replace(sub, '/', '.');
                return CharOperation.concat(new char[]{'Q'}, sub);
            }
            char[] sub = CharOperation.subarray(name, 1, name.length);
            CharOperation.replace(sub, '/', '.');
            return CharOperation.concat(new char[]{name[0]}, sub);
        }
        int i = CharOperation.lastIndexOf('/', name);
        if (i != -1) {
            if (name[0] == 'L' || name[0] == 'P') {
                return CharOperation.concat(new char[]{'Q'}, CharOperation.subarray(name, i + 1, name.length));
            }
            return CharOperation.concat(new char[]{name[0]}, CharOperation.subarray(name, i + 1, name.length));
        }
        return name;
    }
}

