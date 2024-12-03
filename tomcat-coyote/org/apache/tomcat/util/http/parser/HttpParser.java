/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.http.parser;

import java.io.IOException;
import java.io.Reader;
import org.apache.tomcat.util.http.parser.SkipResult;
import org.apache.tomcat.util.res.StringManager;

public class HttpParser {
    private static final StringManager sm = StringManager.getManager(HttpParser.class);
    private static final int ARRAY_SIZE = 128;
    private static final boolean[] IS_CONTROL = new boolean[128];
    private static final boolean[] IS_SEPARATOR = new boolean[128];
    private static final boolean[] IS_TOKEN = new boolean[128];
    private static final boolean[] IS_HEX = new boolean[128];
    private static final boolean[] IS_HTTP_PROTOCOL = new boolean[128];
    private static final boolean[] IS_ALPHA = new boolean[128];
    private static final boolean[] IS_NUMERIC = new boolean[128];
    private static final boolean[] IS_SCHEME = new boolean[128];
    private static final boolean[] IS_UNRESERVED = new boolean[128];
    private static final boolean[] IS_SUBDELIM = new boolean[128];
    private static final boolean[] IS_USERINFO = new boolean[128];
    private static final boolean[] IS_RELAXABLE = new boolean[128];
    private static final HttpParser DEFAULT;
    private final boolean[] IS_NOT_REQUEST_TARGET = new boolean[128];
    private final boolean[] IS_ABSOLUTEPATH_RELAXED = new boolean[128];
    private final boolean[] IS_QUERY_RELAXED = new boolean[128];

    public HttpParser(String relaxedPathChars, String relaxedQueryChars) {
        for (int i = 0; i < 128; ++i) {
            if (IS_CONTROL[i] || i == 32 || i == 34 || i == 35 || i == 60 || i == 62 || i == 92 || i == 94 || i == 96 || i == 123 || i == 124 || i == 125) {
                this.IS_NOT_REQUEST_TARGET[i] = true;
            }
            if (IS_USERINFO[i] || i == 64 || i == 47) {
                this.IS_ABSOLUTEPATH_RELAXED[i] = true;
            }
            if (!this.IS_ABSOLUTEPATH_RELAXED[i] && i != 63) continue;
            this.IS_QUERY_RELAXED[i] = true;
        }
        this.relax(this.IS_ABSOLUTEPATH_RELAXED, relaxedPathChars);
        this.relax(this.IS_QUERY_RELAXED, relaxedQueryChars);
    }

    public boolean isNotRequestTargetRelaxed(int c) {
        try {
            return this.IS_NOT_REQUEST_TARGET[c];
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            return true;
        }
    }

    public boolean isAbsolutePathRelaxed(int c) {
        try {
            return this.IS_ABSOLUTEPATH_RELAXED[c];
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }

    public boolean isQueryRelaxed(int c) {
        try {
            return this.IS_QUERY_RELAXED[c];
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }

    public static String unquote(String input) {
        int end;
        int start;
        if (input == null || input.length() < 2) {
            return input;
        }
        if (input.charAt(0) == '\"') {
            start = 1;
            end = input.length() - 1;
        } else {
            start = 0;
            end = input.length();
        }
        StringBuilder result = new StringBuilder();
        for (int i = start; i < end; ++i) {
            char c = input.charAt(i);
            if (input.charAt(i) == '\\') {
                if (++i == end) {
                    return null;
                }
                result.append(input.charAt(i));
                continue;
            }
            result.append(c);
        }
        return result.toString();
    }

    public static boolean isToken(int c) {
        try {
            return IS_TOKEN[c];
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }

    public static boolean isToken(String s) {
        if (s == null) {
            return false;
        }
        if (s.isEmpty()) {
            return false;
        }
        for (char c : s.toCharArray()) {
            if (HttpParser.isToken(c)) continue;
            return false;
        }
        return true;
    }

    public static boolean isHex(int c) {
        try {
            return IS_HEX[c];
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }

    public static boolean isNotRequestTarget(int c) {
        return DEFAULT.isNotRequestTargetRelaxed(c);
    }

    public static boolean isHttpProtocol(int c) {
        try {
            return IS_HTTP_PROTOCOL[c];
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }

    public static boolean isAlpha(int c) {
        try {
            return IS_ALPHA[c];
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }

    public static boolean isNumeric(int c) {
        try {
            return IS_NUMERIC[c];
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }

    public static boolean isScheme(int c) {
        try {
            return IS_SCHEME[c];
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }

    public static boolean isScheme(String s) {
        if (s == null) {
            return false;
        }
        if (s.isEmpty()) {
            return false;
        }
        char[] chars = s.toCharArray();
        if (!HttpParser.isAlpha(chars[0])) {
            return false;
        }
        if (chars.length > 1) {
            for (int i = 1; i < chars.length; ++i) {
                if (HttpParser.isScheme(chars[i])) continue;
                return false;
            }
        }
        return true;
    }

    public static boolean isUserInfo(int c) {
        try {
            return IS_USERINFO[c];
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }

    private static boolean isRelaxable(int c) {
        try {
            return IS_RELAXABLE[c];
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }

    public static boolean isAbsolutePath(int c) {
        return DEFAULT.isAbsolutePathRelaxed(c);
    }

    public static boolean isQuery(int c) {
        return DEFAULT.isQueryRelaxed(c);
    }

    public static boolean isControl(int c) {
        try {
            return IS_CONTROL[c];
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }

    static int skipLws(Reader input) throws IOException {
        input.mark(1);
        int c = input.read();
        while (c == 32 || c == 9 || c == 10 || c == 13) {
            input.mark(1);
            c = input.read();
        }
        input.reset();
        return c;
    }

    static SkipResult skipConstant(Reader input, String constant) throws IOException {
        int len = constant.length();
        HttpParser.skipLws(input);
        input.mark(len);
        int c = input.read();
        for (int i = 0; i < len; ++i) {
            if (i == 0 && c == -1) {
                return SkipResult.EOF;
            }
            if (c != constant.charAt(i)) {
                input.reset();
                return SkipResult.NOT_FOUND;
            }
            if (i == len - 1) continue;
            c = input.read();
        }
        return SkipResult.FOUND;
    }

    static String readToken(Reader input) throws IOException {
        StringBuilder result = new StringBuilder();
        HttpParser.skipLws(input);
        input.mark(1);
        int c = input.read();
        while (c != -1 && HttpParser.isToken(c)) {
            result.append((char)c);
            input.mark(1);
            c = input.read();
        }
        input.reset();
        if (c != -1 && result.length() == 0) {
            return null;
        }
        return result.toString();
    }

    static String readDigits(Reader input) throws IOException {
        StringBuilder result = new StringBuilder();
        HttpParser.skipLws(input);
        input.mark(1);
        int c = input.read();
        while (c != -1 && HttpParser.isNumeric(c)) {
            result.append((char)c);
            input.mark(1);
            c = input.read();
        }
        input.reset();
        return result.toString();
    }

    static long readLong(Reader input) throws IOException {
        String digits = HttpParser.readDigits(input);
        if (digits.length() == 0) {
            return -1L;
        }
        return Long.parseLong(digits);
    }

    static String readQuotedString(Reader input, boolean returnQuoted) throws IOException {
        HttpParser.skipLws(input);
        int c = input.read();
        if (c != 34) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        if (returnQuoted) {
            result.append('\"');
        }
        c = input.read();
        while (c != 34) {
            if (c == -1) {
                return null;
            }
            if (c == 92) {
                c = input.read();
                if (returnQuoted) {
                    result.append('\\');
                }
                result.append((char)c);
            } else {
                result.append((char)c);
            }
            c = input.read();
        }
        if (returnQuoted) {
            result.append('\"');
        }
        return result.toString();
    }

    static String readTokenOrQuotedString(Reader input, boolean returnQuoted) throws IOException {
        int c = HttpParser.skipLws(input);
        if (c == 34) {
            return HttpParser.readQuotedString(input, returnQuoted);
        }
        return HttpParser.readToken(input);
    }

    static String readQuotedToken(Reader input) throws IOException {
        StringBuilder result = new StringBuilder();
        boolean quoted = false;
        HttpParser.skipLws(input);
        input.mark(1);
        int c = input.read();
        if (c == 34) {
            quoted = true;
        } else {
            if (c == -1 || !HttpParser.isToken(c)) {
                return null;
            }
            result.append((char)c);
        }
        input.mark(1);
        c = input.read();
        while (c != -1 && HttpParser.isToken(c)) {
            result.append((char)c);
            input.mark(1);
            c = input.read();
        }
        if (quoted) {
            if (c != 34) {
                return null;
            }
        } else {
            input.reset();
        }
        if (c != -1 && result.length() == 0) {
            return null;
        }
        return result.toString();
    }

    static String readLhex(Reader input) throws IOException {
        StringBuilder result = new StringBuilder();
        boolean quoted = false;
        HttpParser.skipLws(input);
        input.mark(1);
        int c = input.read();
        if (c == 34) {
            quoted = true;
        } else {
            if (c == -1 || !HttpParser.isHex(c)) {
                return null;
            }
            if (65 <= c && c <= 70) {
                c += 32;
            }
            result.append((char)c);
        }
        input.mark(1);
        c = input.read();
        while (c != -1 && HttpParser.isHex(c)) {
            if (65 <= c && c <= 70) {
                c += 32;
            }
            result.append((char)c);
            input.mark(1);
            c = input.read();
        }
        if (quoted) {
            if (c != 34) {
                return null;
            }
        } else {
            input.reset();
        }
        if (c != -1 && result.length() == 0) {
            return null;
        }
        return result.toString();
    }

    static double readWeight(Reader input, char delimiter) throws IOException {
        StringBuilder value;
        int c;
        block12: {
            HttpParser.skipLws(input);
            c = input.read();
            if (c == -1 || c == delimiter) {
                return 1.0;
            }
            if (c != 113) {
                HttpParser.skipUntil(input, c, delimiter);
                return 0.0;
            }
            HttpParser.skipLws(input);
            c = input.read();
            if (c != 61) {
                HttpParser.skipUntil(input, c, delimiter);
                return 0.0;
            }
            HttpParser.skipLws(input);
            c = input.read();
            value = new StringBuilder(5);
            int decimalPlacesRead = -1;
            if (c == 48 || c == 49) {
                value.append((char)c);
                c = input.read();
                while (true) {
                    if (decimalPlacesRead == -1 && c == 46) {
                        value.append('.');
                        decimalPlacesRead = 0;
                    } else {
                        if (decimalPlacesRead <= -1 || c < 48 || c > 57) break block12;
                        if (decimalPlacesRead < 3) {
                            value.append((char)c);
                            ++decimalPlacesRead;
                        }
                    }
                    c = input.read();
                }
            }
            HttpParser.skipUntil(input, c, delimiter);
            return 0.0;
        }
        if (c == 9 || c == 32) {
            HttpParser.skipLws(input);
            c = input.read();
        }
        if (c != delimiter && c != -1) {
            HttpParser.skipUntil(input, c, delimiter);
            return 0.0;
        }
        double result = Double.parseDouble(value.toString());
        if (result > 1.0) {
            return 0.0;
        }
        return result;
    }

    /*
     * Enabled aggressive block sorting
     */
    static int readHostIPv4(Reader reader, boolean inIPv6) throws IOException {
        int octet = -1;
        int octetCount = 1;
        int pos = 0;
        reader.mark(1);
        while (true) {
            block19: {
                int c;
                if ((c = reader.read()) == 46) {
                    if (octet > -1 && octet < 256) {
                        ++octetCount;
                        octet = -1;
                        break block19;
                    } else {
                        if (!inIPv6 && octet != -1) {
                            reader.reset();
                            return HttpParser.readHostDomainName(reader);
                        }
                        throw new IllegalArgumentException(sm.getString("http.invalidOctet", new Object[]{Integer.toString(octet)}));
                    }
                }
                if (HttpParser.isNumeric(c)) {
                    if (octet == -1) {
                        octet = c - 48;
                    } else {
                        if (octet == 0) {
                            if (inIPv6) {
                                throw new IllegalArgumentException(sm.getString("http.invalidLeadingZero"));
                            }
                            reader.reset();
                            return HttpParser.readHostDomainName(reader);
                        }
                        if ((octet = octet * 10 + c - 48) > 255) {
                            break;
                        }
                    }
                } else {
                    if (c == 58) break;
                    if (c == -1) {
                        if (inIPv6) {
                            throw new IllegalArgumentException(sm.getString("http.noClosingBracket"));
                        }
                        pos = -1;
                        break;
                    }
                    if (c == 93) {
                        if (!inIPv6) {
                            throw new IllegalArgumentException(sm.getString("http.closingBracket"));
                        }
                        ++pos;
                        break;
                    }
                    if (!inIPv6 && (HttpParser.isAlpha(c) || c == 45)) {
                        reader.reset();
                        return HttpParser.readHostDomainName(reader);
                    }
                    throw new IllegalArgumentException(sm.getString("http.illegalCharacterIpv4", new Object[]{Character.toString((char)c)}));
                }
            }
            ++pos;
        }
        if (octetCount != 4 || octet < 0 || octet > 255) {
            reader.reset();
            return HttpParser.readHostDomainName(reader);
        }
        if (inIPv6) {
            return pos;
        }
        return HttpParser.validatePort(reader, pos);
    }

    static int readHostIPv6(Reader reader) throws IOException {
        int c = reader.read();
        if (c != 91) {
            throw new IllegalArgumentException(sm.getString("http.noOpeningBracket"));
        }
        int h16Count = 0;
        int h16Size = 0;
        int pos = 1;
        boolean parsedDoubleColon = false;
        int precedingColonsCount = 0;
        while (true) {
            c = reader.read();
            if (h16Count == 0 && precedingColonsCount == 1 && c != 58) {
                throw new IllegalArgumentException(sm.getString("http.singleColonStart"));
            }
            if (HttpParser.isHex(c)) {
                if (h16Size == 0) {
                    precedingColonsCount = 0;
                    ++h16Count;
                }
                if (++h16Size > 4) {
                    throw new IllegalArgumentException(sm.getString("http.invalidHextet"));
                }
            } else if (c == 58) {
                if (precedingColonsCount >= 2) {
                    throw new IllegalArgumentException(sm.getString("http.tooManyColons"));
                }
                if (precedingColonsCount == 1) {
                    if (parsedDoubleColon) {
                        throw new IllegalArgumentException(sm.getString("http.tooManyDoubleColons"));
                    }
                    parsedDoubleColon = true;
                    ++h16Count;
                }
                ++precedingColonsCount;
                reader.mark(4);
                h16Size = 0;
            } else {
                if (c == 93) {
                    if (precedingColonsCount == 1) {
                        throw new IllegalArgumentException(sm.getString("http.singleColonEnd"));
                    }
                    ++pos;
                    break;
                }
                if (c == 46) {
                    if (h16Count == 7 || h16Count < 7 && parsedDoubleColon) {
                        reader.reset();
                        pos -= h16Size;
                        pos += HttpParser.readHostIPv4(reader, true);
                        ++h16Count;
                        break;
                    }
                    throw new IllegalArgumentException(sm.getString("http.invalidIpv4Location"));
                }
                throw new IllegalArgumentException(sm.getString("http.illegalCharacterIpv6", new Object[]{Character.toString((char)c)}));
            }
            ++pos;
        }
        if (h16Count > 8) {
            throw new IllegalArgumentException(sm.getString("http.tooManyHextets", new Object[]{Integer.toString(h16Count)}));
        }
        if (h16Count != 8 && !parsedDoubleColon) {
            throw new IllegalArgumentException(sm.getString("http.tooFewHextets", new Object[]{Integer.toString(h16Count)}));
        }
        c = reader.read();
        if (c == 58) {
            return HttpParser.validatePort(reader, pos);
        }
        if (c == -1) {
            return -1;
        }
        throw new IllegalArgumentException(sm.getString("http.illegalAfterIpv6", new Object[]{Character.toString((char)c)}));
    }

    static int readHostDomainName(Reader reader) throws IOException {
        DomainParseState state = DomainParseState.NEW;
        int pos = 0;
        while (state.mayContinue()) {
            state = state.next(reader.read());
            ++pos;
        }
        if (DomainParseState.COLON == state) {
            return HttpParser.validatePort(reader, pos - 1);
        }
        return -1;
    }

    static int validatePort(Reader reader, int colonPosition) throws IOException {
        HttpParser.readLong(reader);
        if (reader.read() == -1) {
            return colonPosition;
        }
        throw new IllegalArgumentException();
    }

    static SkipResult skipUntil(Reader input, int c, char target) throws IOException {
        while (c != -1 && c != target) {
            c = input.read();
        }
        if (c == -1) {
            return SkipResult.EOF;
        }
        return SkipResult.FOUND;
    }

    private void relax(boolean[] flags, String relaxedChars) {
        if (relaxedChars != null && relaxedChars.length() > 0) {
            char[] chars;
            for (char c : chars = relaxedChars.toCharArray()) {
                if (!HttpParser.isRelaxable(c)) continue;
                flags[c] = true;
                this.IS_NOT_REQUEST_TARGET[c] = false;
            }
        }
    }

    static {
        for (int i = 0; i < 128; ++i) {
            if (i < 32 || i == 127) {
                HttpParser.IS_CONTROL[i] = true;
            }
            if (i == 40 || i == 41 || i == 60 || i == 62 || i == 64 || i == 44 || i == 59 || i == 58 || i == 92 || i == 34 || i == 47 || i == 91 || i == 93 || i == 63 || i == 61 || i == 123 || i == 125 || i == 32 || i == 9) {
                HttpParser.IS_SEPARATOR[i] = true;
            }
            if (!IS_CONTROL[i] && !IS_SEPARATOR[i] && i < 128) {
                HttpParser.IS_TOKEN[i] = true;
            }
            if (i >= 48 && i <= 57 || i >= 97 && i <= 102 || i >= 65 && i <= 70) {
                HttpParser.IS_HEX[i] = true;
            }
            if (i == 72 || i == 84 || i == 80 || i == 47 || i == 46 || i >= 48 && i <= 57) {
                HttpParser.IS_HTTP_PROTOCOL[i] = true;
            }
            if (i >= 48 && i <= 57) {
                HttpParser.IS_NUMERIC[i] = true;
            }
            if (i >= 97 && i <= 122 || i >= 65 && i <= 90) {
                HttpParser.IS_ALPHA[i] = true;
            }
            if (IS_ALPHA[i] || IS_NUMERIC[i] || i == 43 || i == 45 || i == 46) {
                HttpParser.IS_SCHEME[i] = true;
            }
            if (IS_ALPHA[i] || IS_NUMERIC[i] || i == 45 || i == 46 || i == 95 || i == 126) {
                HttpParser.IS_UNRESERVED[i] = true;
            }
            if (i == 33 || i == 36 || i == 38 || i == 39 || i == 40 || i == 41 || i == 42 || i == 43 || i == 44 || i == 59 || i == 61) {
                HttpParser.IS_SUBDELIM[i] = true;
            }
            if (IS_UNRESERVED[i] || i == 37 || IS_SUBDELIM[i] || i == 58) {
                HttpParser.IS_USERINFO[i] = true;
            }
            if (i != 34 && i != 60 && i != 62 && i != 91 && i != 92 && i != 93 && i != 94 && i != 96 && i != 123 && i != 124 && i != 125) continue;
            HttpParser.IS_RELAXABLE[i] = true;
        }
        DEFAULT = new HttpParser(null, null);
    }

    private static enum DomainParseState {
        NEW(true, false, false, false, "http.invalidCharacterDomain.atStart"),
        ALPHA(true, true, true, true, "http.invalidCharacterDomain.afterLetter"),
        NUMERIC(true, true, true, true, "http.invalidCharacterDomain.afterNumber"),
        PERIOD(true, false, false, true, "http.invalidCharacterDomain.afterPeriod"),
        HYPHEN(true, true, false, false, "http.invalidCharacterDomain.afterHyphen"),
        COLON(false, false, false, false, "http.invalidCharacterDomain.afterColon"),
        END(false, false, false, false, "http.invalidCharacterDomain.atEnd");

        private final boolean mayContinue;
        private final boolean allowsHyphen;
        private final boolean allowsPeriod;
        private final boolean allowsEnd;
        private final String errorMsg;

        private DomainParseState(boolean mayContinue, boolean allowsHyphen, boolean allowsPeriod, boolean allowsEnd, String errorMsg) {
            this.mayContinue = mayContinue;
            this.allowsHyphen = allowsHyphen;
            this.allowsPeriod = allowsPeriod;
            this.allowsEnd = allowsEnd;
            this.errorMsg = errorMsg;
        }

        public boolean mayContinue() {
            return this.mayContinue;
        }

        public DomainParseState next(int c) {
            if (c == -1) {
                if (this.allowsEnd) {
                    return END;
                }
                throw new IllegalArgumentException(sm.getString("http.invalidSegmentEndState", new Object[]{this.name()}));
            }
            if (HttpParser.isAlpha(c)) {
                return ALPHA;
            }
            if (HttpParser.isNumeric(c)) {
                return NUMERIC;
            }
            if (c == 46) {
                if (this.allowsPeriod) {
                    return PERIOD;
                }
                throw new IllegalArgumentException(sm.getString(this.errorMsg, new Object[]{Character.toString((char)c)}));
            }
            if (c == 58) {
                if (this.allowsEnd) {
                    return COLON;
                }
                throw new IllegalArgumentException(sm.getString(this.errorMsg, new Object[]{Character.toString((char)c)}));
            }
            if (c == 45) {
                if (this.allowsHyphen) {
                    return HYPHEN;
                }
                throw new IllegalArgumentException(sm.getString(this.errorMsg, new Object[]{Character.toString((char)c)}));
            }
            throw new IllegalArgumentException(sm.getString("http.illegalCharacterDomain", new Object[]{Character.toString((char)c)}));
        }
    }
}

