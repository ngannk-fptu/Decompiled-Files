/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class CharConstants {
    private static final String ALPHA_S = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-_";
    private static final String LINEBR_S = "\n\r";
    private static final String NULL_OR_LINEBR_S = "\u0000\n\r";
    private static final String NULL_BL_LINEBR_S = " \u0000\n\r";
    private static final String NULL_BL_T_LINEBR_S = "\t \u0000\n\r";
    private static final String NULL_BL_T_S = "\u0000 \t";
    private static final String URI_CHARS_S = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-_-;/?:@&=+$,_.!~*'()[]%";
    public static final CharConstants LINEBR = new CharConstants("\n\r");
    public static final CharConstants NULL_OR_LINEBR = new CharConstants("\u0000\n\r");
    public static final CharConstants NULL_BL_LINEBR = new CharConstants(" \u0000\n\r");
    public static final CharConstants NULL_BL_T_LINEBR = new CharConstants("\t \u0000\n\r");
    public static final CharConstants NULL_BL_T = new CharConstants("\u0000 \t");
    public static final CharConstants URI_CHARS = new CharConstants("abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-_-;/?:@&=+$,_.!~*'()[]%");
    public static final CharConstants ALPHA = new CharConstants("abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-_");
    private static final int ASCII_SIZE = 128;
    boolean[] contains = new boolean[128];
    public static final Map<Integer, Character> ESCAPE_REPLACEMENTS;
    public static final Map<Character, Integer> ESCAPES;
    public static final Map<Character, Integer> ESCAPE_CODES;

    private CharConstants(String content) {
        Arrays.fill(this.contains, false);
        for (int i = 0; i < content.length(); ++i) {
            int c = content.codePointAt(i);
            this.contains[c] = true;
        }
    }

    public boolean has(int c) {
        return c < 128 && this.contains[c];
    }

    public boolean hasNo(int c) {
        return !this.has(c);
    }

    public boolean has(int c, String additional) {
        return this.has(c) || additional.indexOf(c) != -1;
    }

    public boolean hasNo(int c, String additional) {
        return !this.has(c, additional);
    }

    static {
        HashMap<Integer, Character> escapeReplacements = new HashMap<Integer, Character>();
        HashMap escapes = new HashMap();
        escapeReplacements.put(48, Character.valueOf('\u0000'));
        escapeReplacements.put(97, Character.valueOf('\u0007'));
        escapeReplacements.put(98, Character.valueOf('\b'));
        escapeReplacements.put(116, Character.valueOf('\t'));
        escapeReplacements.put(110, Character.valueOf('\n'));
        escapeReplacements.put(118, Character.valueOf('\u000b'));
        escapeReplacements.put(102, Character.valueOf('\f'));
        escapeReplacements.put(114, Character.valueOf('\r'));
        escapeReplacements.put(101, Character.valueOf('\u001b'));
        escapeReplacements.put(32, Character.valueOf(' '));
        escapeReplacements.put(34, Character.valueOf('\"'));
        escapeReplacements.put(47, Character.valueOf('/'));
        escapeReplacements.put(92, Character.valueOf('\\'));
        escapeReplacements.put(78, Character.valueOf('\u0085'));
        escapeReplacements.put(95, Character.valueOf('\u00a0'));
        escapeReplacements.put(76, Character.valueOf('\u2028'));
        escapeReplacements.put(80, Character.valueOf('\u2029'));
        escapeReplacements.entrySet().stream().filter(entry -> (Integer)entry.getKey() != 32 && (Integer)entry.getKey() != 47).forEach(entry -> {
            Integer cfr_ignored_0 = (Integer)escapes.put(entry.getValue(), entry.getKey());
        });
        ESCAPE_REPLACEMENTS = Collections.unmodifiableMap(escapeReplacements);
        ESCAPES = Collections.unmodifiableMap(escapes);
        HashMap<Character, Integer> escapeCodes = new HashMap<Character, Integer>();
        escapeCodes.put(Character.valueOf('x'), 2);
        escapeCodes.put(Character.valueOf('u'), 4);
        escapeCodes.put(Character.valueOf('U'), 8);
        ESCAPE_CODES = Collections.unmodifiableMap(escapeCodes);
    }
}

