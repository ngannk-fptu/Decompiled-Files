/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.thirdparty.jackson.core.io;

import java.math.BigInteger;
import software.amazon.awssdk.thirdparty.jackson.core.io.doubleparser.JavaBigIntegerParser;

public final class BigIntegerParser {
    private BigIntegerParser() {
    }

    public static BigInteger parseWithFastParser(String valueStr) {
        try {
            return JavaBigIntegerParser.parseBigInteger(valueStr);
        }
        catch (NumberFormatException nfe) {
            String reportNum = valueStr.length() <= 1000 ? valueStr : valueStr.substring(0, 1000) + " [truncated]";
            throw new NumberFormatException("Value \"" + reportNum + "\" can not be represented as `java.math.BigInteger`, reason: " + nfe.getMessage());
        }
    }

    public static BigInteger parseWithFastParser(String valueStr, int radix) {
        try {
            return JavaBigIntegerParser.parseBigInteger(valueStr, radix);
        }
        catch (NumberFormatException nfe) {
            String reportNum = valueStr.length() <= 1000 ? valueStr : valueStr.substring(0, 1000) + " [truncated]";
            throw new NumberFormatException("Value \"" + reportNum + "\" can not be represented as `java.math.BigInteger` with radix " + radix + ", reason: " + nfe.getMessage());
        }
    }
}

