/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.core.io;

import java.math.BigDecimal;
import java.util.Arrays;

public final class BigDecimalParser {
    private static final int MAX_CHARS_TO_REPORT = 1000;

    private BigDecimalParser() {
    }

    public static BigDecimal parse(String valueStr) {
        return BigDecimalParser.parse(valueStr.toCharArray());
    }

    public static BigDecimal parse(char[] chars, int off, int len) {
        try {
            if (len < 500) {
                return new BigDecimal(chars, off, len);
            }
            return BigDecimalParser.parseBigDecimal(chars, off, len, len / 10);
        }
        catch (ArithmeticException | NumberFormatException e) {
            String desc = e.getMessage();
            if (desc == null) {
                desc = "Not a valid number representation";
            }
            String stringToReport = len <= 1000 ? new String(chars, off, len) : new String(Arrays.copyOfRange(chars, off, 1000)) + "(truncated, full length is " + chars.length + " chars)";
            throw new NumberFormatException("Value \"" + stringToReport + "\" can not be represented as `java.math.BigDecimal`, reason: " + desc);
        }
    }

    public static BigDecimal parse(char[] chars) {
        return BigDecimalParser.parse(chars, 0, chars.length);
    }

    private static BigDecimal parseBigDecimal(char[] chars, int off, int len, int splitLen) {
        BigDecimal res;
        int numEndIdx;
        boolean numHasSign = false;
        boolean expHasSign = false;
        boolean neg = false;
        int numIdx = off;
        int expIdx = -1;
        int dotIdx = -1;
        int scale = 0;
        int endIdx = off + len;
        block6: for (int i = off; i < endIdx; ++i) {
            char c = chars[i];
            switch (c) {
                case '+': {
                    if (expIdx >= 0) {
                        if (expHasSign) {
                            throw new NumberFormatException("Multiple signs in exponent");
                        }
                        expHasSign = true;
                        continue block6;
                    }
                    if (numHasSign) {
                        throw new NumberFormatException("Multiple signs in number");
                    }
                    numHasSign = true;
                    numIdx = i + 1;
                    continue block6;
                }
                case '-': {
                    if (expIdx >= 0) {
                        if (expHasSign) {
                            throw new NumberFormatException("Multiple signs in exponent");
                        }
                        expHasSign = true;
                        continue block6;
                    }
                    if (numHasSign) {
                        throw new NumberFormatException("Multiple signs in number");
                    }
                    numHasSign = true;
                    neg = true;
                    numIdx = i + 1;
                    continue block6;
                }
                case 'E': 
                case 'e': {
                    if (expIdx >= 0) {
                        throw new NumberFormatException("Multiple exponent markers");
                    }
                    expIdx = i;
                    continue block6;
                }
                case '.': {
                    if (dotIdx >= 0) {
                        throw new NumberFormatException("Multiple decimal points");
                    }
                    dotIdx = i;
                    continue block6;
                }
                default: {
                    if (dotIdx < 0 || expIdx != -1) continue block6;
                    ++scale;
                }
            }
        }
        int exp = 0;
        if (expIdx >= 0) {
            numEndIdx = expIdx;
            String expStr = new String(chars, expIdx + 1, endIdx - expIdx - 1);
            exp = Integer.parseInt(expStr);
            scale = BigDecimalParser.adjustScale(scale, exp);
        } else {
            numEndIdx = endIdx;
        }
        if (dotIdx >= 0) {
            int leftLen = dotIdx - numIdx;
            BigDecimal left = BigDecimalParser.toBigDecimalRec(chars, numIdx, leftLen, exp, splitLen);
            int rightLen = numEndIdx - dotIdx - 1;
            BigDecimal right = BigDecimalParser.toBigDecimalRec(chars, dotIdx + 1, rightLen, exp - rightLen, splitLen);
            res = left.add(right);
        } else {
            res = BigDecimalParser.toBigDecimalRec(chars, numIdx, numEndIdx - numIdx, exp, splitLen);
        }
        if (scale != 0) {
            res = res.setScale(scale);
        }
        if (neg) {
            res = res.negate();
        }
        return res;
    }

    private static int adjustScale(int scale, long exp) {
        long adjScale = (long)scale - exp;
        if (adjScale > Integer.MAX_VALUE || adjScale < Integer.MIN_VALUE) {
            throw new NumberFormatException("Scale out of range: " + adjScale + " while adjusting scale " + scale + " to exponent " + exp);
        }
        return (int)adjScale;
    }

    private static BigDecimal toBigDecimalRec(char[] chars, int off, int len, int scale, int splitLen) {
        if (len > splitLen) {
            int mid = len / 2;
            BigDecimal left = BigDecimalParser.toBigDecimalRec(chars, off, mid, scale + len - mid, splitLen);
            BigDecimal right = BigDecimalParser.toBigDecimalRec(chars, off + mid, len - mid, scale, splitLen);
            return left.add(right);
        }
        return len == 0 ? BigDecimal.ZERO : new BigDecimal(chars, off, len).movePointRight(scale);
    }
}

