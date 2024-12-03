/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.http.parser;

import java.nio.charset.StandardCharsets;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.http.ServerCookie;
import org.apache.tomcat.util.http.ServerCookies;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.apache.tomcat.util.http.parser.SkipResult;
import org.apache.tomcat.util.log.UserDataHelper;
import org.apache.tomcat.util.res.StringManager;

public class Cookie {
    private static final Log log;
    private static final UserDataHelper invalidCookieVersionLog;
    private static final UserDataHelper invalidCookieLog;
    private static final StringManager sm;
    private static final boolean[] isCookieOctet;
    private static final boolean[] isText;
    private static final byte[] VERSION_BYTES;
    private static final byte[] PATH_BYTES;
    private static final byte[] DOMAIN_BYTES;
    private static final byte[] EMPTY_BYTES;
    private static final byte TAB_BYTE = 9;
    private static final byte SPACE_BYTE = 32;
    private static final byte QUOTE_BYTE = 34;
    private static final byte COMMA_BYTE = 44;
    private static final byte FORWARDSLASH_BYTE = 47;
    private static final byte SEMICOLON_BYTE = 59;
    private static final byte EQUALS_BYTE = 61;
    private static final byte SLASH_BYTE = 92;
    private static final byte DEL_BYTE = 127;

    private Cookie() {
    }

    public static void parseCookie(byte[] bytes, int offset, int len, ServerCookies serverCookies) {
        ByteBuffer bb = new ByteBuffer(bytes, offset, len);
        Cookie.skipLWS(bb);
        int mark = bb.position();
        SkipResult skipResult = Cookie.skipBytes(bb, VERSION_BYTES);
        if (skipResult != SkipResult.FOUND) {
            Cookie.parseCookieRfc6265(bb, serverCookies);
            return;
        }
        Cookie.skipLWS(bb);
        skipResult = Cookie.skipByte(bb, (byte)61);
        if (skipResult != SkipResult.FOUND) {
            bb.position(mark);
            Cookie.parseCookieRfc6265(bb, serverCookies);
            return;
        }
        Cookie.skipLWS(bb);
        ByteBuffer value = Cookie.readCookieValue(bb);
        if (value != null && value.remaining() == 1) {
            int version = value.get() - 48;
            if (version == 1 || version == 0) {
                Cookie.skipLWS(bb);
                byte b = bb.get();
                if (b == 59 || b == 44) {
                    Cookie.parseCookieRfc2109(bb, serverCookies, version);
                }
            } else {
                value.rewind();
                Cookie.logInvalidVersion(value);
            }
        } else {
            Cookie.logInvalidVersion(value);
        }
    }

    public static String unescapeCookieValueRfc2109(String input) {
        if (input == null || input.length() < 2) {
            return input;
        }
        if (input.charAt(0) != '\"' && input.charAt(input.length() - 1) != '\"') {
            return input;
        }
        StringBuilder sb = new StringBuilder(input.length());
        char[] chars = input.toCharArray();
        boolean escaped = false;
        for (int i = 1; i < input.length() - 1; ++i) {
            if (chars[i] == '\\') {
                escaped = true;
                continue;
            }
            if (escaped) {
                escaped = false;
                if (chars[i] < '\u0080') {
                    sb.append(chars[i]);
                    continue;
                }
                sb.append('\\');
                sb.append(chars[i]);
                continue;
            }
            sb.append(chars[i]);
        }
        return sb.toString();
    }

    private static void parseCookieRfc6265(ByteBuffer bb, ServerCookies serverCookies) {
        boolean moreToProcess = true;
        while (moreToProcess) {
            Cookie.skipLWS(bb);
            int start = bb.position();
            ByteBuffer name = Cookie.readToken(bb);
            ByteBuffer value = null;
            Cookie.skipLWS(bb);
            SkipResult skipResult = Cookie.skipByte(bb, (byte)61);
            if (skipResult == SkipResult.FOUND) {
                Cookie.skipLWS(bb);
                value = Cookie.readCookieValueRfc6265(bb);
                if (value == null) {
                    Cookie.skipUntilSemiColon(bb);
                    Cookie.logInvalidHeader(start, bb);
                    continue;
                }
                Cookie.skipLWS(bb);
            }
            if ((skipResult = Cookie.skipByte(bb, (byte)59)) != SkipResult.FOUND) {
                if (skipResult == SkipResult.NOT_FOUND) {
                    Cookie.skipUntilSemiColon(bb);
                    Cookie.logInvalidHeader(start, bb);
                    continue;
                }
                moreToProcess = false;
            }
            if (!name.hasRemaining()) continue;
            ServerCookie sc = serverCookies.addCookie();
            sc.getName().setBytes(name.array(), name.position(), name.remaining());
            if (value == null) {
                sc.getValue().setBytes(EMPTY_BYTES, 0, EMPTY_BYTES.length);
                continue;
            }
            sc.getValue().setBytes(value.array(), value.position(), value.remaining());
        }
    }

    private static void parseCookieRfc2109(ByteBuffer bb, ServerCookies serverCookies, int version) {
        boolean moreToProcess = true;
        while (moreToProcess) {
            Cookie.skipLWS(bb);
            boolean parseAttributes = true;
            int start = bb.position();
            ByteBuffer name = Cookie.readToken(bb);
            ByteBuffer value = null;
            ByteBuffer path = null;
            ByteBuffer domain = null;
            Cookie.skipLWS(bb);
            SkipResult skipResult = Cookie.skipByte(bb, (byte)61);
            if (skipResult == SkipResult.FOUND) {
                Cookie.skipLWS(bb);
                value = Cookie.readCookieValueRfc2109(bb, false);
                if (value == null) {
                    Cookie.skipInvalidCookie(start, bb);
                    continue;
                }
                Cookie.skipLWS(bb);
            }
            if ((skipResult = Cookie.skipByte(bb, (byte)44)) == SkipResult.FOUND) {
                parseAttributes = false;
            } else {
                skipResult = Cookie.skipByte(bb, (byte)59);
            }
            if (skipResult == SkipResult.EOF) {
                parseAttributes = false;
                moreToProcess = false;
            } else if (skipResult == SkipResult.NOT_FOUND) {
                Cookie.skipInvalidCookie(start, bb);
                continue;
            }
            if (parseAttributes) {
                Cookie.skipLWS(bb);
                skipResult = Cookie.skipBytes(bb, PATH_BYTES);
                if (skipResult == SkipResult.FOUND) {
                    Cookie.skipLWS(bb);
                    skipResult = Cookie.skipByte(bb, (byte)61);
                    if (skipResult != SkipResult.FOUND) {
                        Cookie.skipInvalidCookie(start, bb);
                        continue;
                    }
                    Cookie.skipLWS(bb);
                    path = Cookie.readCookieValueRfc2109(bb, true);
                    if (path == null) {
                        Cookie.skipInvalidCookie(start, bb);
                        continue;
                    }
                    Cookie.skipLWS(bb);
                    skipResult = Cookie.skipByte(bb, (byte)44);
                    if (skipResult == SkipResult.FOUND) {
                        parseAttributes = false;
                    } else {
                        skipResult = Cookie.skipByte(bb, (byte)59);
                    }
                    if (skipResult == SkipResult.EOF) {
                        parseAttributes = false;
                        moreToProcess = false;
                    } else if (skipResult == SkipResult.NOT_FOUND) {
                        Cookie.skipInvalidCookie(start, bb);
                        continue;
                    }
                }
            }
            if (parseAttributes) {
                Cookie.skipLWS(bb);
                skipResult = Cookie.skipBytes(bb, DOMAIN_BYTES);
                if (skipResult == SkipResult.FOUND) {
                    Cookie.skipLWS(bb);
                    skipResult = Cookie.skipByte(bb, (byte)61);
                    if (skipResult != SkipResult.FOUND) {
                        Cookie.skipInvalidCookie(start, bb);
                        continue;
                    }
                    Cookie.skipLWS(bb);
                    domain = Cookie.readCookieValueRfc2109(bb, false);
                    if (domain == null) {
                        Cookie.skipInvalidCookie(start, bb);
                        continue;
                    }
                    Cookie.skipLWS(bb);
                    skipResult = Cookie.skipByte(bb, (byte)44);
                    if (skipResult == SkipResult.FOUND) {
                        parseAttributes = false;
                    } else {
                        skipResult = Cookie.skipByte(bb, (byte)59);
                    }
                    if (skipResult == SkipResult.EOF) {
                        parseAttributes = false;
                        moreToProcess = false;
                    } else if (skipResult == SkipResult.NOT_FOUND) {
                        Cookie.skipInvalidCookie(start, bb);
                        continue;
                    }
                }
            }
            if (!name.hasRemaining() || value == null || !value.hasRemaining()) continue;
            ServerCookie sc = serverCookies.addCookie();
            sc.setVersion(version);
            sc.getName().setBytes(name.array(), name.position(), name.remaining());
            sc.getValue().setBytes(value.array(), value.position(), value.remaining());
            if (domain != null) {
                sc.getDomain().setBytes(domain.array(), domain.position(), domain.remaining());
            }
            if (path == null) continue;
            sc.getPath().setBytes(path.array(), path.position(), path.remaining());
        }
    }

    private static void skipInvalidCookie(int start, ByteBuffer bb) {
        Cookie.skipUntilSemiColonOrComma(bb);
        Cookie.logInvalidHeader(start, bb);
    }

    private static void skipLWS(ByteBuffer bb) {
        while (bb.hasRemaining()) {
            byte b = bb.get();
            if (b == 9 || b == 32) continue;
            bb.rewind();
            break;
        }
    }

    private static void skipUntilSemiColon(ByteBuffer bb) {
        while (bb.hasRemaining() && bb.get() != 59) {
        }
    }

    private static void skipUntilSemiColonOrComma(ByteBuffer bb) {
        byte b;
        while (bb.hasRemaining() && (b = bb.get()) != 59 && b != 44) {
        }
    }

    private static SkipResult skipByte(ByteBuffer bb, byte target) {
        if (!bb.hasRemaining()) {
            return SkipResult.EOF;
        }
        if (bb.get() == target) {
            return SkipResult.FOUND;
        }
        bb.rewind();
        return SkipResult.NOT_FOUND;
    }

    private static SkipResult skipBytes(ByteBuffer bb, byte[] target) {
        int mark = bb.position();
        for (byte b : target) {
            if (!bb.hasRemaining()) {
                bb.position(mark);
                return SkipResult.EOF;
            }
            if (bb.get() == b) continue;
            bb.position(mark);
            return SkipResult.NOT_FOUND;
        }
        return SkipResult.FOUND;
    }

    private static ByteBuffer readCookieValue(ByteBuffer bb) {
        boolean quoted = false;
        if (bb.hasRemaining()) {
            if (bb.get() == 34) {
                quoted = true;
            } else {
                bb.rewind();
            }
        }
        int start = bb.position();
        int end = bb.limit();
        while (bb.hasRemaining()) {
            byte b = bb.get();
            if (isCookieOctet[b & 0xFF]) continue;
            if (b == 59 || b == 44 || b == 32 || b == 9) {
                end = bb.position() - 1;
                bb.position(end);
                break;
            }
            if (quoted && b == 34) {
                end = bb.position() - 1;
                break;
            }
            return null;
        }
        return new ByteBuffer(bb.bytes, start, end - start);
    }

    private static ByteBuffer readCookieValueRfc6265(ByteBuffer bb) {
        boolean quoted = false;
        if (bb.hasRemaining()) {
            if (bb.get() == 34) {
                quoted = true;
            } else {
                bb.rewind();
            }
        }
        int start = bb.position();
        int end = bb.limit();
        while (bb.hasRemaining()) {
            byte b = bb.get();
            if (isCookieOctet[b & 0xFF]) continue;
            if (b == 59 || b == 32 || b == 9) {
                end = bb.position() - 1;
                bb.position(end);
                break;
            }
            if (quoted && b == 34) {
                end = bb.position() - 1;
                break;
            }
            return null;
        }
        return new ByteBuffer(bb.bytes, start, end - start);
    }

    private static ByteBuffer readCookieValueRfc2109(ByteBuffer bb, boolean allowForwardSlash) {
        if (!bb.hasRemaining()) {
            return null;
        }
        if (bb.peek() == 34) {
            return Cookie.readQuotedString(bb);
        }
        if (allowForwardSlash) {
            return Cookie.readTokenAllowForwardSlash(bb);
        }
        return Cookie.readToken(bb);
    }

    private static ByteBuffer readToken(ByteBuffer bb) {
        int start = bb.position();
        int end = bb.limit();
        while (bb.hasRemaining()) {
            if (HttpParser.isToken(bb.get())) continue;
            end = bb.position() - 1;
            bb.position(end);
            break;
        }
        return new ByteBuffer(bb.bytes, start, end - start);
    }

    private static ByteBuffer readTokenAllowForwardSlash(ByteBuffer bb) {
        int start = bb.position();
        int end = bb.limit();
        while (bb.hasRemaining()) {
            byte b = bb.get();
            if (b == 47 || HttpParser.isToken(b)) continue;
            end = bb.position() - 1;
            bb.position(end);
            break;
        }
        return new ByteBuffer(bb.bytes, start, end - start);
    }

    private static ByteBuffer readQuotedString(ByteBuffer bb) {
        int start = bb.position();
        bb.get();
        boolean escaped = false;
        while (bb.hasRemaining()) {
            byte b = bb.get();
            if (b == 92) {
                escaped = true;
                continue;
            }
            if (escaped && b > -1) {
                escaped = false;
                continue;
            }
            if (b == 34) {
                return new ByteBuffer(bb.bytes, start, bb.position() - start);
            }
            if (isText[b & 0xFF]) {
                escaped = false;
                continue;
            }
            return null;
        }
        return null;
    }

    private static void logInvalidHeader(int start, ByteBuffer bb) {
        UserDataHelper.Mode logMode = invalidCookieLog.getNextMode();
        if (logMode != null) {
            String headerValue = new String(bb.array(), start, bb.position() - start, StandardCharsets.UTF_8);
            String message = sm.getString("cookie.invalidCookieValue", new Object[]{headerValue});
            switch (logMode) {
                case INFO_THEN_DEBUG: {
                    message = message + sm.getString("cookie.fallToDebug");
                }
                case INFO: {
                    log.info((Object)message);
                    break;
                }
                case DEBUG: {
                    log.debug((Object)message);
                }
            }
        }
    }

    private static void logInvalidVersion(ByteBuffer value) {
        UserDataHelper.Mode logMode = invalidCookieVersionLog.getNextMode();
        if (logMode != null) {
            String version = value == null ? sm.getString("cookie.valueNotPresent") : new String(value.bytes, value.position(), value.limit() - value.position(), StandardCharsets.UTF_8);
            String message = sm.getString("cookie.invalidCookieVersion", new Object[]{version});
            switch (logMode) {
                case INFO_THEN_DEBUG: {
                    message = message + sm.getString("cookie.fallToDebug");
                }
                case INFO: {
                    log.info((Object)message);
                    break;
                }
                case DEBUG: {
                    log.debug((Object)message);
                }
            }
        }
    }

    static {
        int i;
        log = LogFactory.getLog(Cookie.class);
        invalidCookieVersionLog = new UserDataHelper(log);
        invalidCookieLog = new UserDataHelper(log);
        sm = StringManager.getManager((String)"org.apache.tomcat.util.http.parser");
        isCookieOctet = new boolean[256];
        isText = new boolean[256];
        VERSION_BYTES = "$Version".getBytes(StandardCharsets.ISO_8859_1);
        PATH_BYTES = "$Path".getBytes(StandardCharsets.ISO_8859_1);
        DOMAIN_BYTES = "$Domain".getBytes(StandardCharsets.ISO_8859_1);
        EMPTY_BYTES = new byte[0];
        for (i = 0; i < 256; ++i) {
            Cookie.isCookieOctet[i] = i >= 33 && i != 34 && i != 44 && i != 59 && i != 92 && i != 127;
        }
        for (i = 0; i < 256; ++i) {
            Cookie.isText[i] = i >= 9 && (i <= 9 || i >= 32) && i != 127;
        }
    }

    private static class ByteBuffer {
        private final byte[] bytes;
        private int limit;
        private int position = 0;

        ByteBuffer(byte[] bytes, int offset, int len) {
            this.bytes = bytes;
            this.position = offset;
            this.limit = offset + len;
        }

        public int position() {
            return this.position;
        }

        public void position(int position) {
            this.position = position;
        }

        public int limit() {
            return this.limit;
        }

        public int remaining() {
            return this.limit - this.position;
        }

        public boolean hasRemaining() {
            return this.position < this.limit;
        }

        public byte get() {
            return this.bytes[this.position++];
        }

        public byte peek() {
            return this.bytes[this.position];
        }

        public void rewind() {
            --this.position;
        }

        public byte[] array() {
            return this.bytes;
        }

        public String toString() {
            return "position [" + this.position + "], limit [" + this.limit + "]";
        }
    }
}

