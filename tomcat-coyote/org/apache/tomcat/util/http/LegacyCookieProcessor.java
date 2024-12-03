/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.buf.ByteChunk
 *  org.apache.tomcat.util.buf.MessageBytes
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.http;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.util.BitSet;
import java.util.Date;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.CookieProcessorBase;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.apache.tomcat.util.http.ServerCookies;
import org.apache.tomcat.util.log.UserDataHelper;
import org.apache.tomcat.util.res.StringManager;

public final class LegacyCookieProcessor
extends CookieProcessorBase {
    private static final Log log = LogFactory.getLog(LegacyCookieProcessor.class);
    private static final UserDataHelper userDataLog = new UserDataHelper(log);
    private static final StringManager sm = StringManager.getManager((String)"org.apache.tomcat.util.http");
    private static final char[] V0_SEPARATORS = new char[]{',', ';', ' ', '\t'};
    private static final BitSet V0_SEPARATOR_FLAGS = new BitSet(128);
    private static final char[] HTTP_SEPARATORS = new char[]{'\t', ' ', '\"', '(', ')', ',', ':', ';', '<', '=', '>', '?', '@', '[', '\\', ']', '{', '}'};
    private final boolean STRICT_SERVLET_COMPLIANCE = Boolean.getBoolean("org.apache.catalina.STRICT_SERVLET_COMPLIANCE");
    private boolean allowEqualsInValue = false;
    private boolean allowNameOnly = false;
    private boolean allowHttpSepsInV0 = false;
    private boolean alwaysAddExpires = !this.STRICT_SERVLET_COMPLIANCE;
    private final BitSet httpSeparatorFlags = new BitSet(128);
    private final BitSet allowedWithoutQuotes = new BitSet(128);

    public LegacyCookieProcessor() {
        for (char c : HTTP_SEPARATORS) {
            this.httpSeparatorFlags.set(c);
        }
        boolean b = this.STRICT_SERVLET_COMPLIANCE;
        if (b) {
            this.httpSeparatorFlags.set(47);
        }
        String separators = this.getAllowHttpSepsInV0() ? ",; " : "()<>@,;:\\\"/[]?={} \t";
        this.allowedWithoutQuotes.set(32, 127);
        for (char ch : separators.toCharArray()) {
            this.allowedWithoutQuotes.clear(ch);
        }
        if (!this.getAllowHttpSepsInV0() && !this.getForwardSlashIsSeparator()) {
            this.allowedWithoutQuotes.set(47);
        }
    }

    public boolean getAllowEqualsInValue() {
        return this.allowEqualsInValue;
    }

    public void setAllowEqualsInValue(boolean allowEqualsInValue) {
        this.allowEqualsInValue = allowEqualsInValue;
    }

    public boolean getAllowNameOnly() {
        return this.allowNameOnly;
    }

    public void setAllowNameOnly(boolean allowNameOnly) {
        this.allowNameOnly = allowNameOnly;
    }

    public boolean getAllowHttpSepsInV0() {
        return this.allowHttpSepsInV0;
    }

    public void setAllowHttpSepsInV0(boolean allowHttpSepsInV0) {
        char[] seps;
        this.allowHttpSepsInV0 = allowHttpSepsInV0;
        for (char sep : seps = "()<>@:\\\"[]?={}\t".toCharArray()) {
            if (allowHttpSepsInV0) {
                this.allowedWithoutQuotes.set(sep);
                continue;
            }
            this.allowedWithoutQuotes.clear(sep);
        }
        if (this.getForwardSlashIsSeparator() && !allowHttpSepsInV0) {
            this.allowedWithoutQuotes.clear(47);
        } else {
            this.allowedWithoutQuotes.set(47);
        }
    }

    public boolean getForwardSlashIsSeparator() {
        return this.httpSeparatorFlags.get(47);
    }

    public void setForwardSlashIsSeparator(boolean forwardSlashIsSeparator) {
        if (forwardSlashIsSeparator) {
            this.httpSeparatorFlags.set(47);
        } else {
            this.httpSeparatorFlags.clear(47);
        }
        if (forwardSlashIsSeparator && !this.getAllowHttpSepsInV0()) {
            this.allowedWithoutQuotes.clear(47);
        } else {
            this.allowedWithoutQuotes.set(47);
        }
    }

    public boolean getAlwaysAddExpires() {
        return this.alwaysAddExpires;
    }

    public void setAlwaysAddExpires(boolean alwaysAddExpires) {
        this.alwaysAddExpires = alwaysAddExpires;
    }

    @Override
    public Charset getCharset() {
        return StandardCharsets.ISO_8859_1;
    }

    @Override
    public void parseCookieHeader(MimeHeaders headers, ServerCookies serverCookies) {
        if (headers == null) {
            return;
        }
        int pos = headers.findHeader("Cookie", 0);
        while (pos >= 0) {
            MessageBytes cookieValue = headers.getValue(pos);
            if (cookieValue != null && !cookieValue.isNull()) {
                if (cookieValue.getType() != 2) {
                    Exception e = new Exception();
                    log.debug((Object)"Cookies: Parsing cookie as String. Expected bytes.", (Throwable)e);
                    cookieValue.toBytes();
                }
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Cookies: Parsing b[]: " + cookieValue.toString()));
                }
                ByteChunk bc = cookieValue.getByteChunk();
                this.processCookieHeader(bc.getBytes(), bc.getOffset(), bc.getLength(), serverCookies);
            }
            ++pos;
            pos = headers.findHeader("Cookie", pos);
        }
    }

    @Override
    public String generateHeader(Cookie cookie) {
        return this.generateHeader(cookie, null);
    }

    @Override
    public String generateHeader(Cookie cookie, HttpServletRequest request) {
        SameSiteCookies sameSiteCookiesValue;
        int maxAge;
        int version = cookie.getVersion();
        String value = cookie.getValue();
        String path = cookie.getPath();
        String domain = cookie.getDomain();
        String comment = cookie.getComment();
        if (version == 0 && (this.needsQuotes(value, 0) || comment != null || this.needsQuotes(path, 0) || this.needsQuotes(domain, 0))) {
            version = 1;
        }
        StringBuffer buf = new StringBuffer();
        buf.append(cookie.getName());
        buf.append('=');
        this.maybeQuote(buf, value, version);
        if (version == 1) {
            buf.append("; Version=1");
            if (comment != null) {
                buf.append("; Comment=");
                this.maybeQuote(buf, comment, version);
            }
        }
        if (domain != null) {
            buf.append("; Domain=");
            this.maybeQuote(buf, domain, version);
        }
        if ((maxAge = cookie.getMaxAge()) >= 0) {
            if (version > 0) {
                buf.append("; Max-Age=");
                buf.append(maxAge);
            }
            if (version == 0 || this.getAlwaysAddExpires()) {
                buf.append("; Expires=");
                if (maxAge == 0) {
                    buf.append(ANCIENT_DATE);
                } else {
                    ((DateFormat)COOKIE_DATE_FORMAT.get()).format(new Date(System.currentTimeMillis() + (long)maxAge * 1000L), buf, new FieldPosition(0));
                }
            }
        }
        if (path != null) {
            buf.append("; Path=");
            this.maybeQuote(buf, path, version);
        }
        if (cookie.getSecure()) {
            buf.append("; Secure");
        }
        if (cookie.isHttpOnly()) {
            buf.append("; HttpOnly");
        }
        if (!(sameSiteCookiesValue = this.getSameSiteCookies()).equals((Object)SameSiteCookies.UNSET)) {
            buf.append("; SameSite=");
            buf.append(sameSiteCookiesValue.getValue());
        }
        return buf.toString();
    }

    private void maybeQuote(StringBuffer buf, String value, int version) {
        if (value == null || value.length() == 0) {
            buf.append("\"\"");
        } else if (LegacyCookieProcessor.alreadyQuoted(value)) {
            buf.append('\"');
            LegacyCookieProcessor.escapeDoubleQuotes(buf, value, 1, value.length() - 1);
            buf.append('\"');
        } else if (this.needsQuotes(value, version)) {
            buf.append('\"');
            LegacyCookieProcessor.escapeDoubleQuotes(buf, value, 0, value.length());
            buf.append('\"');
        } else {
            buf.append(value);
        }
    }

    private static void escapeDoubleQuotes(StringBuffer b, String s, int beginIndex, int endIndex) {
        if (s.indexOf(34) == -1 && s.indexOf(92) == -1) {
            b.append(s);
            return;
        }
        for (int i = beginIndex; i < endIndex; ++i) {
            char c = s.charAt(i);
            if (c == '\\') {
                b.append('\\').append('\\');
                continue;
            }
            if (c == '\"') {
                b.append('\\').append('\"');
                continue;
            }
            b.append(c);
        }
    }

    private boolean needsQuotes(String value, int version) {
        if (value == null) {
            return false;
        }
        int i = 0;
        int len = value.length();
        if (LegacyCookieProcessor.alreadyQuoted(value)) {
            ++i;
            --len;
        }
        while (i < len) {
            char c = value.charAt(i);
            if (c < ' ' && c != '\t' || c >= '\u007f') {
                throw new IllegalArgumentException("Control character in cookie value or attribute.");
            }
            if (version == 0 && !this.allowedWithoutQuotes.get(c) || version == 1 && this.isHttpSeparator(c)) {
                return true;
            }
            ++i;
        }
        return false;
    }

    private static boolean alreadyQuoted(String value) {
        return value.length() >= 2 && value.charAt(0) == '\"' && value.charAt(value.length() - 1) == '\"';
    }

    /*
     * Unable to fully structure code
     */
    private void processCookieHeader(byte[] bytes, int off, int len, ServerCookies serverCookies) {
        if (len <= 0 || bytes == null) {
            return;
        }
        end = off + len;
        pos = off;
        nameStart = 0;
        nameEnd = 0;
        valueStart = 0;
        valueEnd = 0;
        version = 0;
        sc = null;
        while (pos < end) {
            block34: {
                block33: {
                    isSpecial = false;
                    isQuoted = false;
                    while (pos < end && (this.isHttpSeparator((char)bytes[pos]) && !this.getAllowHttpSepsInV0() || LegacyCookieProcessor.isV0Separator((char)bytes[pos]) || LegacyCookieProcessor.isWhiteSpace(bytes[pos]))) {
                        ++pos;
                    }
                    if (pos >= end) {
                        return;
                    }
                    if (bytes[pos] == 36) {
                        isSpecial = true;
                    }
                    valueStart = nameStart = ++pos;
                    valueEnd = nameStart;
                    for (pos = nameEnd = this.getTokenEndPosition(bytes, pos, end, version, true); pos < end && LegacyCookieProcessor.isWhiteSpace(bytes[pos]); ++pos) {
                    }
                    if (pos >= end - 1 || bytes[pos] != 61) break block33;
                    while (++pos < end && LegacyCookieProcessor.isWhiteSpace(bytes[pos])) {
                    }
                    if (pos >= end) {
                        return;
                    }
                    switch (bytes[pos]) {
                        case 34: {
                            isQuoted = true;
                            valueStart = pos + 1;
                            pos = valueEnd = LegacyCookieProcessor.getQuotedValueEndPosition(bytes, valueStart, end);
                            if (pos >= end) {
                                return;
                            }
                            break block34;
                        }
                        case 44: 
                        case 59: {
                            valueEnd = -1;
                            valueStart = -1;
                            break block34;
                        }
                        default: {
                            if ((version != 0 || LegacyCookieProcessor.isV0Separator((char)bytes[pos]) || !this.getAllowHttpSepsInV0()) && this.isHttpSeparator((char)bytes[pos]) && bytes[pos] != 61) ** GOTO lbl50
                            valueStart = pos;
                            pos = valueEnd = this.getTokenEndPosition(bytes, valueStart, end, version, false);
                            if (valueStart == valueEnd) {
                                valueStart = -1;
                                valueEnd = -1;
                            }
                            break block34;
lbl50:
                            // 1 sources

                            logMode = LegacyCookieProcessor.userDataLog.getNextMode();
                            if (logMode != null) {
                                message = LegacyCookieProcessor.sm.getString("cookies.invalidCookieToken");
                                switch (1.$SwitchMap$org$apache$tomcat$util$log$UserDataHelper$Mode[logMode.ordinal()]) {
                                    case 1: {
                                        message = message + LegacyCookieProcessor.sm.getString("cookies.fallToDebug");
                                    }
                                    case 2: {
                                        LegacyCookieProcessor.log.info((Object)message);
                                        break;
                                    }
                                    case 3: {
                                        LegacyCookieProcessor.log.debug((Object)message);
                                    }
                                }
                            }
                            while (pos < end && bytes[pos] != 59 && bytes[pos] != 44) {
                                ++pos;
                            }
                            ++pos;
                            sc = null;
                            break;
                        }
                    }
                    continue;
                }
                valueEnd = -1;
                valueStart = -1;
                pos = nameEnd;
            }
            while (pos < end && LegacyCookieProcessor.isWhiteSpace(bytes[pos])) {
                ++pos;
            }
            while (pos < end && bytes[pos] != 59 && bytes[pos] != 44) {
                ++pos;
            }
            ++pos;
            if (isSpecial) {
                isSpecial = false;
                if (LegacyCookieProcessor.equals("Version", bytes, nameStart, nameEnd) && sc == null) {
                    if (bytes[valueStart] != 49 || valueEnd != valueStart + 1) continue;
                    version = 1;
                    continue;
                }
                if (sc == null) continue;
                if (LegacyCookieProcessor.equals("Domain", bytes, nameStart, nameEnd)) {
                    sc.getDomain().setBytes(bytes, valueStart, valueEnd - valueStart);
                    continue;
                }
                if (LegacyCookieProcessor.equals("Path", bytes, nameStart, nameEnd)) {
                    sc.getPath().setBytes(bytes, valueStart, valueEnd - valueStart);
                    continue;
                }
                if (LegacyCookieProcessor.equals("Port", bytes, nameStart, nameEnd) || LegacyCookieProcessor.equals("CommentURL", bytes, nameStart, nameEnd) || (logMode = LegacyCookieProcessor.userDataLog.getNextMode()) == null) continue;
                message = LegacyCookieProcessor.sm.getString("cookies.invalidSpecial");
                switch (1.$SwitchMap$org$apache$tomcat$util$log$UserDataHelper$Mode[logMode.ordinal()]) {
                    case 1: {
                        message = message + LegacyCookieProcessor.sm.getString("cookies.fallToDebug");
                    }
                    case 2: {
                        LegacyCookieProcessor.log.info((Object)message);
                        break;
                    }
                    case 3: {
                        LegacyCookieProcessor.log.debug((Object)message);
                    }
                }
                continue;
            }
            if (valueStart == -1 && !this.getAllowNameOnly()) continue;
            sc = serverCookies.addCookie();
            sc.setVersion(version);
            sc.getName().setBytes(bytes, nameStart, nameEnd - nameStart);
            if (valueStart != -1) {
                sc.getValue().setBytes(bytes, valueStart, valueEnd - valueStart);
                if (!isQuoted) continue;
                LegacyCookieProcessor.unescapeDoubleQuotes(sc.getValue().getByteChunk());
                continue;
            }
            sc.getValue().setString("");
        }
    }

    private int getTokenEndPosition(byte[] bytes, int off, int end, int version, boolean isName) {
        int pos;
        for (pos = off; pos < end && (!this.isHttpSeparator((char)bytes[pos]) || version == 0 && this.getAllowHttpSepsInV0() && bytes[pos] != 61 && !LegacyCookieProcessor.isV0Separator((char)bytes[pos]) || !isName && bytes[pos] == 61 && this.getAllowEqualsInValue()); ++pos) {
        }
        if (pos > end) {
            return end;
        }
        return pos;
    }

    private boolean isHttpSeparator(char c) {
        if ((c < ' ' || c >= '\u007f') && c != '\t') {
            throw new IllegalArgumentException("Control character in cookie value or attribute.");
        }
        return this.httpSeparatorFlags.get(c);
    }

    private static boolean isV0Separator(char c) {
        if ((c < ' ' || c >= '\u007f') && c != '\t') {
            throw new IllegalArgumentException("Control character in cookie value or attribute.");
        }
        return V0_SEPARATOR_FLAGS.get(c);
    }

    private static int getQuotedValueEndPosition(byte[] bytes, int off, int end) {
        int pos = off;
        while (pos < end) {
            if (bytes[pos] == 34) {
                return pos;
            }
            if (bytes[pos] == 92 && pos < end - 1) {
                pos += 2;
                continue;
            }
            ++pos;
        }
        return end;
    }

    private static boolean equals(String s, byte[] b, int start, int end) {
        int blen = end - start;
        if (b == null || blen != s.length()) {
            return false;
        }
        int boff = start;
        for (int i = 0; i < blen; ++i) {
            if (b[boff++] == s.charAt(i)) continue;
            return false;
        }
        return true;
    }

    private static boolean isWhiteSpace(byte c) {
        return c == 32 || c == 9 || c == 10 || c == 13 || c == 12;
    }

    private static void unescapeDoubleQuotes(ByteChunk bc) {
        if (bc == null || bc.getLength() == 0 || bc.indexOf('\"', 0) == -1) {
            return;
        }
        byte[] original = bc.getBuffer();
        int len = bc.getLength();
        byte[] copy = new byte[len];
        System.arraycopy(original, bc.getStart(), copy, 0, len);
        int dest = 0;
        for (int src = 0; src < len; ++src) {
            if (copy[src] == 92 && src < len && copy[src + 1] == 34) {
                ++src;
            }
            copy[dest] = copy[src];
            ++dest;
        }
        bc.setBytes(copy, 0, dest);
    }

    static {
        for (char c : V0_SEPARATORS) {
            V0_SEPARATOR_FLAGS.set(c);
        }
    }
}

