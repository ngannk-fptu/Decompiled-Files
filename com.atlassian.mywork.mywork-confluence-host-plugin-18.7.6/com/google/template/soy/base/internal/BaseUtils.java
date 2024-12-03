/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Sets
 *  com.google.common.hash.Hashing
 */
package com.google.template.soy.base.internal;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.common.hash.Hashing;
import java.io.File;
import java.util.Set;
import java.util.regex.Pattern;

public class BaseUtils {
    private static final Set<String> KNOWN_EXISTING_DIRS = Sets.newHashSet();
    public static final String IDENT_RE = "[a-zA-Z_][a-zA-Z_0-9]*";
    private static final Pattern IDENT_PATTERN = Pattern.compile("[a-zA-Z_][a-zA-Z_0-9]*");
    private static final Pattern IDENT_WITH_LEADING_DOT_PATTERN = Pattern.compile("[.][a-zA-Z_][a-zA-Z_0-9]*");
    public static final String DOTTED_IDENT_RE = "[a-zA-Z_][a-zA-Z_0-9]*(?:[.][a-zA-Z_][a-zA-Z_0-9]*)*";
    private static final Pattern DOTTED_IDENT_PATTERN = Pattern.compile("[a-zA-Z_][a-zA-Z_0-9]*(?:[.][a-zA-Z_][a-zA-Z_0-9]*)*");
    private static final String DASHED_IDENT_RE = "[a-zA-Z_][a-zA-Z_0-9]*(?:[-][a-zA-Z_0-9]*)*";
    private static final Pattern DOTTED_OR_DASHED_IDENT_PATTERN = Pattern.compile(String.format("(?:%s)|(?:%s)", "[a-zA-Z_][a-zA-Z_0-9]*(?:[.][a-zA-Z_][a-zA-Z_0-9]*)*", "[a-zA-Z_][a-zA-Z_0-9]*(?:[-][a-zA-Z_0-9]*)*"));
    private static final Pattern LEADING_OR_TRAILING_UNDERSCORE_PATTERN = Pattern.compile("^_+|_+\\Z");
    private static final Pattern WORD_BOUNDARY_IN_IDENT_PATTERN = Pattern.compile("(?<= [a-zA-Z])(?= [A-Z][a-z])| (?<= [a-zA-Z])(?= [0-9])| (?<= [0-9])(?= [a-zA-Z])", 4);
    private static final Pattern CONSECUTIVE_UNDERSCORES_PATTERN = Pattern.compile("_ _ _*", 4);
    private static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private BaseUtils() {
    }

    public static void ensureDirsExistInPath(String path) {
        String dirPath;
        if (path == null || path.length() == 0) {
            throw new AssertionError((Object)"ensureDirsExistInPath called with null or empty path.");
        }
        String string = dirPath = path.charAt(path.length() - 1) == File.separatorChar ? path.substring(0, path.length() - 1) : new File(path).getParent();
        if (dirPath == null || KNOWN_EXISTING_DIRS.contains(dirPath)) {
            return;
        }
        new File(dirPath).mkdirs();
        KNOWN_EXISTING_DIRS.add(dirPath);
    }

    public static boolean isIdentifier(String s) {
        return IDENT_PATTERN.matcher(s).matches();
    }

    public static boolean isIdentifierWithLeadingDot(String s) {
        return IDENT_WITH_LEADING_DOT_PATTERN.matcher(s).matches();
    }

    public static boolean isDottedOrDashedIdent(String s) {
        return DOTTED_OR_DASHED_IDENT_PATTERN.matcher(s).matches();
    }

    public static boolean isDottedIdentifier(String s) {
        return DOTTED_IDENT_PATTERN.matcher(s).matches();
    }

    public static String extractPartAfterLastDot(String dottedIdent) {
        int lastDotIndex = dottedIdent.lastIndexOf(46);
        return lastDotIndex == -1 ? dottedIdent : dottedIdent.substring(lastDotIndex + 1);
    }

    public static String convertToUpperUnderscore(String ident) {
        ident = LEADING_OR_TRAILING_UNDERSCORE_PATTERN.matcher(ident).replaceAll("");
        ident = WORD_BOUNDARY_IN_IDENT_PATTERN.matcher(ident).replaceAll("_");
        ident = CONSECUTIVE_UNDERSCORES_PATTERN.matcher(ident).replaceAll("_");
        return ident.toUpperCase();
    }

    public static String escapeToSoyString(String value, boolean shouldEscapeToAscii) {
        int codePoint;
        int len = value.length();
        StringBuilder out = new StringBuilder(len * 9 / 8);
        out.append('\'');
        block10: for (int i = 0; i < len; i += Character.charCount(codePoint)) {
            codePoint = value.codePointAt(i);
            switch (codePoint) {
                case 10: {
                    out.append("\\n");
                    continue block10;
                }
                case 13: {
                    out.append("\\r");
                    continue block10;
                }
                case 9: {
                    out.append("\\t");
                    continue block10;
                }
                case 8: {
                    out.append("\\b");
                    continue block10;
                }
                case 12: {
                    out.append("\\f");
                    continue block10;
                }
                case 92: {
                    out.append("\\\\");
                    continue block10;
                }
                case 39: {
                    out.append("\\'");
                    continue block10;
                }
                case 34: {
                    out.append('\"');
                    continue block10;
                }
                default: {
                    if (shouldEscapeToAscii && (codePoint < 32 || codePoint >= 127)) {
                        BaseUtils.appendHexEscape(out, codePoint);
                        continue block10;
                    }
                    out.appendCodePoint(codePoint);
                }
            }
        }
        out.append('\'');
        return out.toString();
    }

    public static void appendHexEscape(StringBuilder out, int codePoint) {
        if (Character.isSupplementaryCodePoint(codePoint)) {
            char[] surrogates = Character.toChars(codePoint);
            BaseUtils.appendHexEscape(out, surrogates[0]);
            BaseUtils.appendHexEscape(out, surrogates[1]);
        } else {
            out.append("\\u").append(HEX_DIGITS[codePoint >>> 12 & 0xF]).append(HEX_DIGITS[codePoint >>> 8 & 0xF]).append(HEX_DIGITS[codePoint >>> 4 & 0xF]).append(HEX_DIGITS[codePoint & 0xF]);
        }
    }

    public static String computeSha1AsHexString(String strToHash) {
        return BaseUtils.computePartialSha1AsHexString(strToHash, 160);
    }

    public static String computePartialSha1AsHexString(String strToHash, int numBits) {
        Preconditions.checkArgument((numBits > 0 && numBits <= 160 && numBits % 8 == 0 ? 1 : 0) != 0);
        int numBytes = numBits / 8;
        return Hashing.sha1().hashString((CharSequence)strToHash, Charsets.UTF_8).toString().substring(0, numBytes * 2);
    }
}

