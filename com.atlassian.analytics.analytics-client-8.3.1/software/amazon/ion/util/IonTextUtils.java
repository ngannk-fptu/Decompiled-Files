/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.util;

import java.io.IOException;
import java.math.BigDecimal;
import software.amazon.ion.EmptySymbolException;
import software.amazon.ion.impl.PrivateIonConstants;
import software.amazon.ion.impl.PrivateIonTextAppender;
import software.amazon.ion.impl.PrivateIonTextWriterBuilder;

public class IonTextUtils {
    public static boolean isWhitespace(int codePoint) {
        switch (codePoint) {
            case 9: 
            case 10: 
            case 13: 
            case 32: {
                return true;
            }
        }
        return false;
    }

    public static boolean isNumericStop(int codePoint) {
        switch (codePoint) {
            case -1: 
            case 9: 
            case 10: 
            case 13: 
            case 32: 
            case 34: 
            case 39: 
            case 40: 
            case 41: 
            case 44: 
            case 91: 
            case 93: 
            case 123: 
            case 125: {
                return true;
            }
        }
        return false;
    }

    public static boolean isDigit(int codePoint, int radix) {
        switch (codePoint) {
            case 48: 
            case 49: 
            case 50: 
            case 51: 
            case 52: 
            case 53: 
            case 54: 
            case 55: {
                return radix == 8 || radix == 10 || radix == 16;
            }
            case 56: 
            case 57: {
                return radix == 10 || radix == 16;
            }
            case 65: 
            case 66: 
            case 67: 
            case 68: 
            case 69: 
            case 70: 
            case 97: 
            case 98: 
            case 99: 
            case 100: 
            case 101: 
            case 102: {
                return radix == 16;
            }
        }
        return false;
    }

    public static boolean isIdentifierStart(int codePoint) {
        return PrivateIonTextAppender.isIdentifierStart(codePoint);
    }

    public static boolean isIdentifierPart(int codePoint) {
        return PrivateIonTextAppender.isIdentifierPart(codePoint);
    }

    public static boolean isOperatorPart(int codePoint) {
        return PrivateIonTextAppender.isOperatorPart(codePoint);
    }

    public static SymbolVariant symbolVariant(CharSequence symbol) {
        int length = symbol.length();
        if (length == 0) {
            throw new EmptySymbolException();
        }
        if (PrivateIonTextAppender.isIdentifierKeyword(symbol)) {
            return SymbolVariant.QUOTED;
        }
        char c = symbol.charAt(0);
        if (IonTextUtils.isIdentifierStart(c)) {
            for (int ii = 0; ii < length; ++ii) {
                c = symbol.charAt(ii);
                if (c != '\'' && c >= ' ' && c <= '~' && IonTextUtils.isIdentifierPart(c)) continue;
                return SymbolVariant.QUOTED;
            }
            return SymbolVariant.IDENTIFIER;
        }
        if (IonTextUtils.isOperatorPart(c)) {
            for (int ii = 0; ii < length; ++ii) {
                c = symbol.charAt(ii);
                if (IonTextUtils.isOperatorPart(c)) continue;
                return SymbolVariant.QUOTED;
            }
            return SymbolVariant.OPERATOR;
        }
        return SymbolVariant.QUOTED;
    }

    public static void printStringCodePoint(Appendable out, int codePoint) throws IOException {
        IonTextUtils.printCodePoint(out, codePoint, EscapeMode.ION_STRING);
    }

    public static void printSymbolCodePoint(Appendable out, int codePoint) throws IOException {
        IonTextUtils.printCodePoint(out, codePoint, EscapeMode.ION_SYMBOL);
    }

    public static void printJsonCodePoint(Appendable out, int codePoint) throws IOException {
        IonTextUtils.printCodePoint(out, codePoint, EscapeMode.JSON);
    }

    private static void printCodePoint(Appendable out, int c, EscapeMode mode) throws IOException {
        switch (c) {
            case 0: {
                out.append(mode == EscapeMode.JSON ? "\\u0000" : "\\0");
                return;
            }
            case 9: {
                out.append("\\t");
                return;
            }
            case 10: {
                if (mode == EscapeMode.ION_LONG_STRING) {
                    out.append('\n');
                } else {
                    out.append("\\n");
                }
                return;
            }
            case 13: {
                out.append("\\r");
                return;
            }
            case 12: {
                out.append("\\f");
                return;
            }
            case 8: {
                out.append("\\b");
                return;
            }
            case 7: {
                out.append(mode == EscapeMode.JSON ? "\\u0007" : "\\a");
                return;
            }
            case 11: {
                out.append(mode == EscapeMode.JSON ? "\\u000b" : "\\v");
                return;
            }
            case 34: {
                if (mode != EscapeMode.JSON && mode != EscapeMode.ION_STRING) break;
                out.append("\\\"");
                return;
            }
            case 39: {
                if (mode != EscapeMode.ION_SYMBOL && mode != EscapeMode.ION_LONG_STRING) break;
                out.append("\\'");
                return;
            }
            case 92: {
                out.append("\\\\");
                return;
            }
        }
        if (c < 32) {
            if (mode == EscapeMode.JSON) {
                IonTextUtils.printCodePointAsFourHexDigits(out, c);
            } else {
                IonTextUtils.printCodePointAsTwoHexDigits(out, c);
            }
        } else if (c < 127) {
            out.append((char)c);
        } else if (c <= 255) {
            if (mode == EscapeMode.JSON) {
                IonTextUtils.printCodePointAsFourHexDigits(out, c);
            } else {
                IonTextUtils.printCodePointAsTwoHexDigits(out, c);
            }
        } else if (c <= 65535) {
            IonTextUtils.printCodePointAsFourHexDigits(out, c);
        } else if (mode == EscapeMode.JSON) {
            IonTextUtils.printCodePointAsSurrogatePairHexDigits(out, c);
        } else {
            IonTextUtils.printCodePointAsEightHexDigits(out, c);
        }
    }

    private static void printCodePointAsTwoHexDigits(Appendable out, int c) throws IOException {
        String s = Integer.toHexString(c);
        out.append("\\x");
        if (s.length() < 2) {
            out.append(PrivateIonTextAppender.ZERO_PADDING[2 - s.length()]);
        }
        out.append(s);
    }

    private static void printCodePointAsFourHexDigits(Appendable out, int c) throws IOException {
        String s = Integer.toHexString(c);
        out.append("\\u");
        out.append(PrivateIonTextAppender.ZERO_PADDING[4 - s.length()]);
        out.append(s);
    }

    private static void printCodePointAsEightHexDigits(Appendable out, int c) throws IOException {
        String s = Integer.toHexString(c);
        out.append("\\U");
        out.append(PrivateIonTextAppender.ZERO_PADDING[8 - s.length()]);
        out.append(s);
    }

    private static void printCodePointAsSurrogatePairHexDigits(Appendable out, int c) throws IOException {
        for (char unit : Character.toChars(c)) {
            IonTextUtils.printCodePointAsFourHexDigits(out, unit);
        }
    }

    public static void printString(Appendable out, CharSequence text) throws IOException {
        if (text == null) {
            out.append("null.string");
        } else {
            out.append('\"');
            IonTextUtils.printCodePoints(out, text, EscapeMode.ION_STRING);
            out.append('\"');
        }
    }

    public static void printJsonString(Appendable out, CharSequence text) throws IOException {
        if (text == null) {
            out.append("null");
        } else {
            out.append('\"');
            IonTextUtils.printCodePoints(out, text, EscapeMode.JSON);
            out.append('\"');
        }
    }

    public static String printString(CharSequence text) {
        if (text == null) {
            return "null.string";
        }
        if (text.length() == 0) {
            return "\"\"";
        }
        StringBuilder builder = new StringBuilder(text.length() + 2);
        try {
            IonTextUtils.printString(builder, text);
        }
        catch (IOException e) {
            throw new Error(e);
        }
        return builder.toString();
    }

    public static String printLongString(CharSequence text) {
        if (text == null) {
            return "null.string";
        }
        if (text.length() == 0) {
            return "''''''";
        }
        StringBuilder builder = new StringBuilder(text.length() + 6);
        try {
            IonTextUtils.printLongString(builder, text);
        }
        catch (IOException e) {
            throw new Error(e);
        }
        return builder.toString();
    }

    public static void printLongString(Appendable out, CharSequence text) throws IOException {
        if (text == null) {
            out.append("null.string");
        } else {
            out.append("'''");
            IonTextUtils.printCodePoints(out, text, EscapeMode.ION_LONG_STRING);
            out.append("'''");
        }
    }

    public static String printCodePointAsString(int codePoint) {
        StringBuilder builder = new StringBuilder(12);
        builder.append('\"');
        try {
            IonTextUtils.printStringCodePoint(builder, codePoint);
        }
        catch (IOException e) {
            throw new Error(e);
        }
        builder.append('\"');
        return builder.toString();
    }

    public static void printSymbol(Appendable out, CharSequence text) throws IOException {
        if (text == null) {
            out.append("null.symbol");
        } else if (PrivateIonTextAppender.symbolNeedsQuoting(text, true)) {
            IonTextUtils.printQuotedSymbol(out, text);
        } else {
            out.append(text);
        }
    }

    public static String printSymbol(CharSequence text) {
        if (text == null) {
            return "null.symbol";
        }
        StringBuilder builder = new StringBuilder(text.length() + 2);
        try {
            IonTextUtils.printSymbol(builder, text);
        }
        catch (IOException e) {
            throw new Error(e);
        }
        return builder.toString();
    }

    public static void printQuotedSymbol(Appendable out, CharSequence text) throws IOException {
        if (text == null) {
            out.append("null.symbol");
        } else {
            if (text.length() == 0) {
                throw new EmptySymbolException();
            }
            out.append('\'');
            IonTextUtils.printCodePoints(out, text, EscapeMode.ION_SYMBOL);
            out.append('\'');
        }
    }

    public static String printQuotedSymbol(CharSequence text) {
        if (text == null) {
            return "null.symbol";
        }
        StringBuilder builder = new StringBuilder(text.length() + 2);
        try {
            IonTextUtils.printQuotedSymbol(builder, text);
        }
        catch (IOException e) {
            throw new Error(e);
        }
        return builder.toString();
    }

    private static void printCodePoints(Appendable out, CharSequence text, EscapeMode mode) throws IOException {
        int len = text.length();
        for (int i = 0; i < len; ++i) {
            int c = text.charAt(i);
            if (PrivateIonConstants.isHighSurrogate(c)) {
                char c2;
                if (++i >= len || !PrivateIonConstants.isLowSurrogate(c2 = text.charAt(i))) {
                    String message = "text is invalid UTF-16. It contains an unmatched high surrogate 0x" + Integer.toHexString(c) + " at index " + i;
                    throw new IllegalArgumentException(message);
                }
                c = PrivateIonConstants.makeUnicodeScalar(c, c2);
            } else if (PrivateIonConstants.isLowSurrogate(c)) {
                String message = "text is invalid UTF-16. It contains an unmatched low surrogate 0x" + Integer.toHexString(c) + " at index " + i;
                throw new IllegalArgumentException(message);
            }
            IonTextUtils.printCodePoint(out, c, mode);
        }
    }

    public static void printDecimal(Appendable out, BigDecimal decimal) throws IOException {
        PrivateIonTextAppender appender = PrivateIonTextAppender.forAppendable(out);
        appender.printDecimal(PrivateIonTextWriterBuilder.STANDARD, decimal);
    }

    public static String printDecimal(BigDecimal decimal) {
        if (decimal == null) {
            return "null.decimal";
        }
        StringBuilder builder = new StringBuilder(64);
        try {
            IonTextUtils.printDecimal(builder, decimal);
        }
        catch (IOException e) {
            throw new Error(e);
        }
        return builder.toString();
    }

    public static void printFloat(Appendable out, double value) throws IOException {
        PrivateIonTextAppender appender = PrivateIonTextAppender.forAppendable(out);
        appender.printFloat(value);
    }

    public static String printFloat(double value) {
        StringBuilder builder = new StringBuilder(64);
        try {
            IonTextUtils.printFloat((Appendable)builder, value);
        }
        catch (IOException e) {
            throw new Error(e);
        }
        return builder.toString();
    }

    public static void printFloat(Appendable out, Double value) throws IOException {
        PrivateIonTextAppender appender = PrivateIonTextAppender.forAppendable(out);
        appender.printFloat(value);
    }

    public static String printFloat(Double value) {
        if (value == null) {
            return "null.float";
        }
        return IonTextUtils.printFloat((double)value);
    }

    public static void printBlob(Appendable out, byte[] value) throws IOException {
        if (value == null) {
            out.append("null.blob");
        } else {
            PrivateIonTextAppender appender = PrivateIonTextAppender.forAppendable(out);
            appender.printBlob(PrivateIonTextWriterBuilder.STANDARD, value, 0, value.length);
        }
    }

    public static String printBlob(byte[] value) {
        if (value == null) {
            return "null.blob";
        }
        StringBuilder builder = new StringBuilder(64);
        try {
            IonTextUtils.printBlob(builder, value);
        }
        catch (IOException e) {
            throw new Error(e);
        }
        return builder.toString();
    }

    public static void printClob(Appendable out, byte[] value) throws IOException {
        if (value == null) {
            out.append("null.clob");
        } else {
            PrivateIonTextAppender appender = PrivateIonTextAppender.forAppendable(out);
            appender.printClob(PrivateIonTextWriterBuilder.STANDARD, value, 0, value.length);
        }
    }

    public static String printClob(byte[] value) {
        if (value == null) {
            return "null.clob";
        }
        StringBuilder builder = new StringBuilder(64);
        try {
            IonTextUtils.printClob(builder, value);
        }
        catch (IOException e) {
            throw new Error(e);
        }
        return builder.toString();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static enum EscapeMode {
        JSON,
        ION_SYMBOL,
        ION_STRING,
        ION_LONG_STRING;

    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum SymbolVariant {
        IDENTIFIER,
        OPERATOR,
        QUOTED;

    }
}

