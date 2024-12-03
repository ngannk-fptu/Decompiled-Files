/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpSession
 *  org.apache.coyote.ActionCode
 *  org.apache.coyote.RequestInfo
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.buf.HexUtils
 *  org.apache.tomcat.util.collections.SynchronizedStack
 *  org.apache.tomcat.util.net.IPv6Utils
 */
package org.apache.catalina.valves;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import org.apache.catalina.AccessLog;
import org.apache.catalina.Session;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.util.TLSUtil;
import org.apache.catalina.valves.ValveBase;
import org.apache.coyote.ActionCode;
import org.apache.coyote.RequestInfo;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.HexUtils;
import org.apache.tomcat.util.collections.SynchronizedStack;
import org.apache.tomcat.util.net.IPv6Utils;

public abstract class AbstractAccessLogValve
extends ValveBase
implements AccessLog {
    private static final Log log = LogFactory.getLog(AbstractAccessLogValve.class);
    protected boolean enabled = true;
    private boolean ipv6Canonical = false;
    protected String pattern = null;
    private static final int globalCacheSize = 300;
    private static final int localCacheSize = 60;
    private static final DateFormatCache globalDateCache = new DateFormatCache(300, Locale.getDefault(), null);
    private static final ThreadLocal<DateFormatCache> localDateCache = ThreadLocal.withInitial(() -> new DateFormatCache(60, Locale.getDefault(), globalDateCache));
    private static final ThreadLocal<Date> localDate = ThreadLocal.withInitial(Date::new);
    protected String condition = null;
    protected String conditionIf = null;
    protected String localeName = Locale.getDefault().toString();
    protected Locale locale = Locale.getDefault();
    protected AccessLogElement[] logElements = null;
    protected CachedElement[] cachedElements = null;
    protected boolean requestAttributesEnabled = false;
    private SynchronizedStack<CharArrayWriter> charArrayWriters = new SynchronizedStack();
    private int maxLogMessageBufferSize = 256;
    private boolean tlsAttributeRequired = false;

    public AbstractAccessLogValve() {
        super(true);
    }

    public int getMaxLogMessageBufferSize() {
        return this.maxLogMessageBufferSize;
    }

    public void setMaxLogMessageBufferSize(int maxLogMessageBufferSize) {
        this.maxLogMessageBufferSize = maxLogMessageBufferSize;
    }

    public boolean getIpv6Canonical() {
        return this.ipv6Canonical;
    }

    public void setIpv6Canonical(boolean ipv6Canonical) {
        this.ipv6Canonical = ipv6Canonical;
    }

    @Override
    public void setRequestAttributesEnabled(boolean requestAttributesEnabled) {
        this.requestAttributesEnabled = requestAttributesEnabled;
    }

    @Override
    public boolean getRequestAttributesEnabled() {
        return this.requestAttributesEnabled;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPattern() {
        return this.pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern == null ? "" : (pattern.equals("common") ? "%h %l %u %t \"%r\" %s %b" : (pattern.equals("combined") ? "%h %l %u %t \"%r\" %s %b \"%{Referer}i\" \"%{User-Agent}i\"" : pattern));
        this.logElements = this.createLogElements();
        if (this.logElements != null) {
            this.cachedElements = this.createCachedElements(this.logElements);
        }
    }

    public String getCondition() {
        return this.condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getConditionUnless() {
        return this.getCondition();
    }

    public void setConditionUnless(String condition) {
        this.setCondition(condition);
    }

    public String getConditionIf() {
        return this.conditionIf;
    }

    public void setConditionIf(String condition) {
        this.conditionIf = condition;
    }

    public String getLocale() {
        return this.localeName;
    }

    public void setLocale(String localeName) {
        this.localeName = localeName;
        this.locale = AbstractAccessLogValve.findLocale(localeName, this.locale);
    }

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        if (this.tlsAttributeRequired) {
            request.getAttribute("javax.servlet.request.X509Certificate");
        }
        if (this.cachedElements != null) {
            for (CachedElement element : this.cachedElements) {
                element.cache(request);
            }
        }
        this.getNext().invoke(request, response);
    }

    @Override
    public void log(Request request, Response response, long time) {
        if (!this.getState().isAvailable() || !this.getEnabled() || this.logElements == null || this.condition != null && null != request.getRequest().getAttribute(this.condition) || this.conditionIf != null && null == request.getRequest().getAttribute(this.conditionIf)) {
            return;
        }
        long start = request.getCoyoteRequest().getStartTime();
        Date date = AbstractAccessLogValve.getDate(start + time);
        CharArrayWriter result = (CharArrayWriter)this.charArrayWriters.pop();
        if (result == null) {
            result = new CharArrayWriter(128);
        }
        for (AccessLogElement logElement : this.logElements) {
            logElement.addElement(result, date, request, response, time);
        }
        this.log(result);
        if (result.size() <= this.maxLogMessageBufferSize) {
            result.reset();
            this.charArrayWriters.push((Object)result);
        }
    }

    protected abstract void log(CharArrayWriter var1);

    private static Date getDate(long systime) {
        Date date = localDate.get();
        date.setTime(systime);
        return date;
    }

    protected static Locale findLocale(String name, Locale fallback) {
        if (name == null || name.isEmpty()) {
            return Locale.getDefault();
        }
        for (Locale l : Locale.getAvailableLocales()) {
            if (!name.equals(l.toString())) continue;
            return l;
        }
        log.error((Object)sm.getString("accessLogValve.invalidLocale", new Object[]{name}));
        return fallback;
    }

    protected AccessLogElement[] createLogElements() {
        ArrayList<AccessLogElement> list = new ArrayList<AccessLogElement>();
        boolean replace = false;
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < this.pattern.length(); ++i) {
            char ch = this.pattern.charAt(i);
            if (replace) {
                if ('{' == ch) {
                    int j;
                    StringBuilder name = new StringBuilder();
                    for (j = i + 1; j < this.pattern.length() && '}' != this.pattern.charAt(j); ++j) {
                        name.append(this.pattern.charAt(j));
                    }
                    if (j + 1 < this.pattern.length()) {
                        list.add(this.createAccessLogElement(name.toString(), this.pattern.charAt(++j)));
                        i = j;
                    } else {
                        list.add(this.createAccessLogElement(ch));
                    }
                } else {
                    list.add(this.createAccessLogElement(ch));
                }
                replace = false;
                continue;
            }
            if (ch == '%') {
                replace = true;
                list.add(new StringElement(buf.toString()));
                buf = new StringBuilder();
                continue;
            }
            buf.append(ch);
        }
        if (buf.length() > 0) {
            list.add(new StringElement(buf.toString()));
        }
        return list.toArray(new AccessLogElement[0]);
    }

    private CachedElement[] createCachedElements(AccessLogElement[] elements) {
        ArrayList<CachedElement> list = new ArrayList<CachedElement>();
        for (AccessLogElement element : elements) {
            if (!(element instanceof CachedElement)) continue;
            list.add((CachedElement)((Object)element));
        }
        return list.toArray(new CachedElement[0]);
    }

    protected AccessLogElement createAccessLogElement(String name, char pattern) {
        switch (pattern) {
            case 'i': {
                return new HeaderElement(name);
            }
            case 'c': {
                return new CookieElement(name);
            }
            case 'o': {
                return new ResponseHeaderElement(name);
            }
            case 'a': {
                return new RemoteAddrElement(name);
            }
            case 'p': {
                return new PortElement(name);
            }
            case 'r': {
                if (TLSUtil.isTLSRequestAttribute(name)) {
                    this.tlsAttributeRequired = true;
                }
                return new RequestAttributeElement(name);
            }
            case 's': {
                return new SessionAttributeElement(name);
            }
            case 't': {
                return new DateAndTimeElement(name);
            }
        }
        return new StringElement("???");
    }

    protected AccessLogElement createAccessLogElement(char pattern) {
        switch (pattern) {
            case 'a': {
                return new RemoteAddrElement();
            }
            case 'A': {
                return new LocalAddrElement(this.ipv6Canonical);
            }
            case 'b': {
                return new ByteSentElement(true);
            }
            case 'B': {
                return new ByteSentElement(false);
            }
            case 'D': {
                return new ElapsedTimeElement(true);
            }
            case 'F': {
                return new FirstByteTimeElement();
            }
            case 'h': {
                return new HostElement();
            }
            case 'H': {
                return new ProtocolElement();
            }
            case 'l': {
                return new LogicalUserNameElement();
            }
            case 'm': {
                return new MethodElement();
            }
            case 'p': {
                return new PortElement();
            }
            case 'q': {
                return new QueryElement();
            }
            case 'r': {
                return new RequestElement();
            }
            case 's': {
                return new HttpStatusCodeElement();
            }
            case 'S': {
                return new SessionIdElement();
            }
            case 't': {
                return new DateAndTimeElement();
            }
            case 'T': {
                return new ElapsedTimeElement(false);
            }
            case 'u': {
                return new UserElement();
            }
            case 'U': {
                return new RequestURIElement();
            }
            case 'v': {
                return new LocalServerNameElement();
            }
            case 'I': {
                return new ThreadNameElement();
            }
            case 'X': {
                return new ConnectionStatusElement();
            }
        }
        return new StringElement("???" + pattern + "???");
    }

    protected static void escapeAndAppend(String input, CharArrayWriter dest) {
        if (input == null || input.isEmpty()) {
            dest.append('-');
            return;
        }
        int len = input.length();
        int next = 0;
        block10: for (int current = 0; current < len; ++current) {
            char c = input.charAt(current);
            if (c >= ' ' && c < '\u007f') {
                switch (c) {
                    case '\\': {
                        if (current > next) {
                            dest.write(input, next, current - next);
                        }
                        next = current + 1;
                        dest.append("\\\\");
                        break;
                    }
                    case '\"': {
                        if (current > next) {
                            dest.write(input, next, current - next);
                        }
                        next = current + 1;
                        dest.append("\\\"");
                        break;
                    }
                }
                continue;
            }
            if (current > next) {
                dest.write(input, next, current - next);
            }
            next = current + 1;
            switch (c) {
                case '\f': {
                    dest.append("\\f");
                    continue block10;
                }
                case '\n': {
                    dest.append("\\n");
                    continue block10;
                }
                case '\r': {
                    dest.append("\\r");
                    continue block10;
                }
                case '\t': {
                    dest.append("\\t");
                    continue block10;
                }
                default: {
                    dest.append("\\u");
                    dest.append(HexUtils.toHexString((char)c));
                }
            }
        }
        if (len > next) {
            dest.write(input, next, len - next);
        }
    }

    protected static interface AccessLogElement {
        public void addElement(CharArrayWriter var1, Date var2, Request var3, Response var4, long var5);
    }

    protected static interface CachedElement {
        public void cache(Request var1);
    }

    protected static class StringElement
    implements AccessLogElement {
        private final String str;

        public StringElement(String str) {
            this.str = str;
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            buf.append(this.str);
        }
    }

    protected static class HeaderElement
    implements AccessLogElement {
        private final String header;

        public HeaderElement(String header) {
            this.header = header;
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            Enumeration<String> iter = request.getHeaders(this.header);
            if (iter.hasMoreElements()) {
                AbstractAccessLogValve.escapeAndAppend(iter.nextElement(), buf);
                while (iter.hasMoreElements()) {
                    buf.append(',');
                    AbstractAccessLogValve.escapeAndAppend(iter.nextElement(), buf);
                }
                return;
            }
            buf.append('-');
        }
    }

    protected static class CookieElement
    implements AccessLogElement {
        private final String cookieNameToLog;

        public CookieElement(String cookieNameToLog) {
            this.cookieNameToLog = cookieNameToLog;
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            StringBuilder value = new StringBuilder();
            boolean first = true;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (!this.cookieNameToLog.equals(cookie.getName())) continue;
                    if (first) {
                        first = false;
                    } else {
                        value.append(',');
                    }
                    value.append(cookie.getValue());
                }
            }
            if (value.length() == 0) {
                buf.append('-');
            } else {
                AbstractAccessLogValve.escapeAndAppend(value.toString(), buf);
            }
        }
    }

    protected static class ResponseHeaderElement
    implements AccessLogElement {
        private final String header;

        public ResponseHeaderElement(String header) {
            this.header = header;
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            Iterator<String> iter;
            if (null != response && (iter = response.getHeaders(this.header).iterator()).hasNext()) {
                AbstractAccessLogValve.escapeAndAppend(iter.next(), buf);
                while (iter.hasNext()) {
                    buf.append(',');
                    AbstractAccessLogValve.escapeAndAppend(iter.next(), buf);
                }
                return;
            }
            buf.append('-');
        }
    }

    protected class RemoteAddrElement
    implements AccessLogElement,
    CachedElement {
        private static final String remoteAddress = "remote";
        private static final String peerAddress = "peer";
        private final RemoteAddressType remoteAddressType;

        public RemoteAddrElement() {
            this.remoteAddressType = RemoteAddressType.REMOTE;
        }

        public RemoteAddrElement(String type) {
            switch (type) {
                case "remote": {
                    this.remoteAddressType = RemoteAddressType.REMOTE;
                    break;
                }
                case "peer": {
                    this.remoteAddressType = RemoteAddressType.PEER;
                    break;
                }
                default: {
                    log.error((Object)ValveBase.sm.getString("accessLogValve.invalidRemoteAddressType", new Object[]{type}));
                    this.remoteAddressType = RemoteAddressType.REMOTE;
                }
            }
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            Object addr;
            String value = null;
            value = this.remoteAddressType == RemoteAddressType.PEER ? request.getPeerAddr() : (AbstractAccessLogValve.this.requestAttributesEnabled ? ((addr = request.getAttribute("org.apache.catalina.AccessLog.RemoteAddr")) == null ? request.getRemoteAddr() : addr.toString()) : request.getRemoteAddr());
            if (AbstractAccessLogValve.this.ipv6Canonical) {
                value = IPv6Utils.canonize((String)value);
            }
            buf.append(value);
        }

        @Override
        public void cache(Request request) {
            if (!AbstractAccessLogValve.this.requestAttributesEnabled) {
                if (this.remoteAddressType == RemoteAddressType.PEER) {
                    request.getPeerAddr();
                } else {
                    request.getRemoteAddr();
                }
            }
        }
    }

    protected class PortElement
    implements AccessLogElement,
    CachedElement {
        private static final String localPort = "local";
        private static final String remotePort = "remote";
        private final PortType portType;

        public PortElement() {
            this.portType = PortType.LOCAL;
        }

        public PortElement(String type) {
            switch (type) {
                case "remote": {
                    this.portType = PortType.REMOTE;
                    break;
                }
                case "local": {
                    this.portType = PortType.LOCAL;
                    break;
                }
                default: {
                    log.error((Object)ValveBase.sm.getString("accessLogValve.invalidPortType", new Object[]{type}));
                    this.portType = PortType.LOCAL;
                }
            }
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            if (AbstractAccessLogValve.this.requestAttributesEnabled && this.portType == PortType.LOCAL) {
                Object port = request.getAttribute("org.apache.catalina.AccessLog.ServerPort");
                if (port == null) {
                    buf.append(Integer.toString(request.getServerPort()));
                } else {
                    buf.append(port.toString());
                }
            } else if (this.portType == PortType.LOCAL) {
                buf.append(Integer.toString(request.getServerPort()));
            } else {
                buf.append(Integer.toString(request.getRemotePort()));
            }
        }

        @Override
        public void cache(Request request) {
            if (this.portType == PortType.REMOTE) {
                request.getRemotePort();
            }
        }
    }

    protected static class RequestAttributeElement
    implements AccessLogElement {
        private final String attribute;

        public RequestAttributeElement(String attribute) {
            this.attribute = attribute;
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            Object value = null;
            value = request != null ? request.getAttribute(this.attribute) : "??";
            if (value != null) {
                if (value instanceof String) {
                    AbstractAccessLogValve.escapeAndAppend((String)value, buf);
                } else {
                    AbstractAccessLogValve.escapeAndAppend(value.toString(), buf);
                }
            } else {
                buf.append('-');
            }
        }
    }

    protected static class SessionAttributeElement
    implements AccessLogElement {
        private final String attribute;

        public SessionAttributeElement(String attribute) {
            this.attribute = attribute;
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            Object value = null;
            if (null != request) {
                HttpSession sess = request.getSession(false);
                if (null != sess) {
                    value = sess.getAttribute(this.attribute);
                }
            } else {
                value = "??";
            }
            if (value != null) {
                if (value instanceof String) {
                    AbstractAccessLogValve.escapeAndAppend((String)value, buf);
                } else {
                    AbstractAccessLogValve.escapeAndAppend(value.toString(), buf);
                }
            } else {
                buf.append('-');
            }
        }
    }

    protected class DateAndTimeElement
    implements AccessLogElement {
        private static final String requestStartPrefix = "begin";
        private static final String responseEndPrefix = "end";
        private static final String prefixSeparator = ":";
        private static final String secFormat = "sec";
        private static final String msecFormat = "msec";
        private static final String msecFractionFormat = "msec_frac";
        private static final String msecPattern = "{#}";
        private static final String tripleMsecPattern = "{#}{#}{#}";
        private final String format;
        private final boolean needsEscaping;
        private final boolean usesBegin;
        private final FormatType type;
        private boolean usesMsecs = false;

        protected DateAndTimeElement() {
            this(null);
        }

        private String tidyFormat(String format) {
            boolean escape = false;
            StringBuilder result = new StringBuilder();
            int len = format.length();
            for (int i = 0; i < len; ++i) {
                char x = format.charAt(i);
                if (escape || x != 'S') {
                    result.append(x);
                } else {
                    result.append(msecPattern);
                    this.usesMsecs = true;
                }
                if (x != '\'') continue;
                escape = !escape;
            }
            return result.toString();
        }

        protected DateAndTimeElement(String sdf) {
            String format = sdf;
            boolean needsEscaping = false;
            if (sdf != null) {
                CharArrayWriter writer = new CharArrayWriter();
                AbstractAccessLogValve.escapeAndAppend(sdf, writer);
                String escaped = writer.toString();
                if (!escaped.equals(sdf)) {
                    needsEscaping = true;
                }
            }
            this.needsEscaping = needsEscaping;
            boolean usesBegin = false;
            FormatType type = FormatType.CLF;
            if (format != null) {
                if (format.equals(requestStartPrefix)) {
                    usesBegin = true;
                    format = "";
                } else if (format.startsWith("begin:")) {
                    usesBegin = true;
                    format = format.substring(6);
                } else if (format.equals(responseEndPrefix)) {
                    usesBegin = false;
                    format = "";
                } else if (format.startsWith("end:")) {
                    usesBegin = false;
                    format = format.substring(4);
                }
                if (format.length() == 0) {
                    type = FormatType.CLF;
                } else if (format.equals(secFormat)) {
                    type = FormatType.SEC;
                } else if (format.equals(msecFormat)) {
                    type = FormatType.MSEC;
                } else if (format.equals(msecFractionFormat)) {
                    type = FormatType.MSEC_FRAC;
                } else {
                    type = FormatType.SDF;
                    format = this.tidyFormat(format);
                }
            }
            this.format = format;
            this.usesBegin = usesBegin;
            this.type = type;
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            long timestamp = date.getTime();
            if (this.usesBegin) {
                timestamp -= time;
            }
            if (this.type == FormatType.CLF) {
                buf.append(((DateFormatCache)localDateCache.get()).getFormat(timestamp));
            } else if (this.type == FormatType.SEC) {
                buf.append(Long.toString(timestamp / 1000L));
            } else if (this.type == FormatType.MSEC) {
                buf.append(Long.toString(timestamp));
            } else if (this.type == FormatType.MSEC_FRAC) {
                long frac = timestamp % 1000L;
                if (frac < 100L) {
                    if (frac < 10L) {
                        buf.append('0');
                        buf.append('0');
                    } else {
                        buf.append('0');
                    }
                }
                buf.append(Long.toString(frac));
            } else {
                String temp = ((DateFormatCache)localDateCache.get()).getFormat(this.format, AbstractAccessLogValve.this.locale, timestamp);
                if (this.usesMsecs) {
                    long frac = timestamp % 1000L;
                    StringBuilder tripleMsec = new StringBuilder(4);
                    if (frac < 100L) {
                        if (frac < 10L) {
                            tripleMsec.append('0');
                            tripleMsec.append('0');
                        } else {
                            tripleMsec.append('0');
                        }
                    }
                    tripleMsec.append(frac);
                    temp = temp.replace(tripleMsecPattern, tripleMsec);
                    temp = temp.replace(msecPattern, Long.toString(frac));
                }
                if (this.needsEscaping) {
                    AbstractAccessLogValve.escapeAndAppend(temp, buf);
                } else {
                    buf.append(temp);
                }
            }
        }
    }

    protected static class LocalAddrElement
    implements AccessLogElement {
        private final String localAddrValue;

        public LocalAddrElement(boolean ipv6Canonical) {
            String init;
            try {
                init = InetAddress.getLocalHost().getHostAddress();
            }
            catch (Throwable e) {
                ExceptionUtils.handleThrowable((Throwable)e);
                init = "127.0.0.1";
            }
            this.localAddrValue = ipv6Canonical ? IPv6Utils.canonize((String)init) : init;
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            buf.append(this.localAddrValue);
        }
    }

    protected static class ByteSentElement
    implements AccessLogElement {
        private final boolean conversion;

        public ByteSentElement(boolean conversion) {
            this.conversion = conversion;
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            Object end;
            Object start;
            long length = response.getBytesWritten(false);
            if (length <= 0L && (start = request.getAttribute("org.apache.tomcat.sendfile.start")) instanceof Long && (end = request.getAttribute("org.apache.tomcat.sendfile.end")) instanceof Long) {
                length = (Long)end - (Long)start;
            }
            if (length <= 0L && this.conversion) {
                buf.append('-');
            } else {
                buf.append(Long.toString(length));
            }
        }
    }

    protected static class ElapsedTimeElement
    implements AccessLogElement {
        private final boolean millis;

        public ElapsedTimeElement(boolean millis) {
            this.millis = millis;
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            if (this.millis) {
                buf.append(Long.toString(time));
            } else {
                buf.append(Long.toString(time / 1000L));
                buf.append('.');
                int remains = (int)(time % 1000L);
                buf.append(Long.toString(remains / 100));
                buf.append(Long.toString((remains %= 100) / 10));
                buf.append(Long.toString(remains % 10));
            }
        }
    }

    protected static class FirstByteTimeElement
    implements AccessLogElement {
        protected FirstByteTimeElement() {
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            long commitTime = response.getCoyoteResponse().getCommitTime();
            if (commitTime == -1L) {
                buf.append('-');
            } else {
                long delta = commitTime - request.getCoyoteRequest().getStartTime();
                buf.append(Long.toString(delta));
            }
        }
    }

    protected class HostElement
    implements AccessLogElement,
    CachedElement {
        protected HostElement() {
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            Object host;
            String value = null;
            if (AbstractAccessLogValve.this.requestAttributesEnabled && (host = request.getAttribute("org.apache.catalina.AccessLog.RemoteHost")) != null) {
                value = host.toString();
            }
            if (value == null || value.length() == 0) {
                value = request.getRemoteHost();
            }
            if (value == null || value.length() == 0) {
                value = "-";
            }
            if (AbstractAccessLogValve.this.ipv6Canonical) {
                value = IPv6Utils.canonize((String)value);
            }
            buf.append(value);
        }

        @Override
        public void cache(Request request) {
            if (!AbstractAccessLogValve.this.requestAttributesEnabled) {
                request.getRemoteHost();
            }
        }
    }

    protected class ProtocolElement
    implements AccessLogElement {
        protected ProtocolElement() {
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            if (AbstractAccessLogValve.this.requestAttributesEnabled) {
                Object proto = request.getAttribute("org.apache.catalina.AccessLog.Protocol");
                if (proto == null) {
                    buf.append(request.getProtocol());
                } else {
                    buf.append(proto.toString());
                }
            } else {
                buf.append(request.getProtocol());
            }
        }
    }

    protected static class LogicalUserNameElement
    implements AccessLogElement {
        protected LogicalUserNameElement() {
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            buf.append('-');
        }
    }

    protected static class MethodElement
    implements AccessLogElement {
        protected MethodElement() {
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            if (request != null) {
                buf.append(request.getMethod());
            }
        }
    }

    protected static class QueryElement
    implements AccessLogElement {
        protected QueryElement() {
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            String query = null;
            if (request != null) {
                query = request.getQueryString();
            }
            if (query != null) {
                buf.append('?');
                buf.append(query);
            }
        }
    }

    protected static class RequestElement
    implements AccessLogElement {
        protected RequestElement() {
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            if (request != null) {
                String method = request.getMethod();
                if (method == null) {
                    buf.append('-');
                } else {
                    buf.append(request.getMethod());
                    buf.append(' ');
                    buf.append(request.getRequestURI());
                    if (request.getQueryString() != null) {
                        buf.append('?');
                        buf.append(request.getQueryString());
                    }
                    buf.append(' ');
                    buf.append(request.getProtocol());
                }
            } else {
                buf.append('-');
            }
        }
    }

    protected static class HttpStatusCodeElement
    implements AccessLogElement {
        protected HttpStatusCodeElement() {
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            if (response != null) {
                int status = response.getStatus();
                if (100 <= status && status < 1000) {
                    buf.append((char)(48 + status / 100)).append((char)(48 + status / 10 % 10)).append((char)(48 + status % 10));
                } else {
                    buf.append(Integer.toString(status));
                }
            } else {
                buf.append('-');
            }
        }
    }

    protected static class SessionIdElement
    implements AccessLogElement {
        protected SessionIdElement() {
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            if (request == null) {
                buf.append('-');
            } else {
                Session session = request.getSessionInternal(false);
                if (session == null) {
                    buf.append('-');
                } else {
                    buf.append(session.getIdInternal());
                }
            }
        }
    }

    protected static class UserElement
    implements AccessLogElement {
        protected UserElement() {
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            if (request != null) {
                String value = request.getRemoteUser();
                if (value != null) {
                    AbstractAccessLogValve.escapeAndAppend(value, buf);
                } else {
                    buf.append('-');
                }
            } else {
                buf.append('-');
            }
        }
    }

    protected static class RequestURIElement
    implements AccessLogElement {
        protected RequestURIElement() {
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            if (request != null) {
                buf.append(request.getRequestURI());
            } else {
                buf.append('-');
            }
        }
    }

    protected class LocalServerNameElement
    implements AccessLogElement {
        protected LocalServerNameElement() {
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            Object serverName;
            String value = null;
            if (AbstractAccessLogValve.this.requestAttributesEnabled && (serverName = request.getAttribute("org.apache.catalina.AccessLog.ServerName")) != null) {
                value = serverName.toString();
            }
            if (value == null || value.length() == 0) {
                value = request.getServerName();
            }
            if (value == null || value.length() == 0) {
                value = "-";
            }
            if (AbstractAccessLogValve.this.ipv6Canonical) {
                value = IPv6Utils.canonize((String)value);
            }
            buf.append(value);
        }
    }

    protected static class ThreadNameElement
    implements AccessLogElement {
        protected ThreadNameElement() {
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            RequestInfo info = request.getCoyoteRequest().getRequestProcessor();
            if (info != null) {
                buf.append(info.getWorkerThreadName());
            } else {
                buf.append('-');
            }
        }
    }

    protected static class ConnectionStatusElement
    implements AccessLogElement {
        protected ConnectionStatusElement() {
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            if (response != null && request != null) {
                Throwable ex;
                boolean statusFound = false;
                AtomicBoolean isIoAllowed = new AtomicBoolean(false);
                request.getCoyoteRequest().action(ActionCode.IS_IO_ALLOWED, (Object)isIoAllowed);
                if (!isIoAllowed.get()) {
                    buf.append('X');
                    statusFound = true;
                } else if (response.isError() && (ex = (Throwable)request.getAttribute("javax.servlet.error.exception")) instanceof ClientAbortException) {
                    buf.append('X');
                    statusFound = true;
                }
                if (!statusFound) {
                    String connStatus = response.getHeader("Connection");
                    if ("close".equalsIgnoreCase(connStatus)) {
                        buf.append('-');
                    } else {
                        buf.append('+');
                    }
                }
            } else {
                buf.append('?');
            }
        }
    }

    protected static class DateFormatCache {
        private int cacheSize = 0;
        private final Locale cacheDefaultLocale;
        private final DateFormatCache parent;
        protected final Cache cLFCache;
        private final Map<String, Cache> formatCache = new HashMap<String, Cache>();

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected DateFormatCache(int size, Locale loc, DateFormatCache parent) {
            this.cacheSize = size;
            this.cacheDefaultLocale = loc;
            this.parent = parent;
            Cache parentCache = null;
            if (parent != null) {
                DateFormatCache dateFormatCache = parent;
                synchronized (dateFormatCache) {
                    parentCache = parent.getCache(null, null);
                }
            }
            this.cLFCache = new Cache(parentCache);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private Cache getCache(String format, Locale loc) {
            Cache cache;
            if (format == null) {
                cache = this.cLFCache;
            } else {
                cache = this.formatCache.get(format);
                if (cache == null) {
                    Cache parentCache = null;
                    if (this.parent != null) {
                        DateFormatCache dateFormatCache = this.parent;
                        synchronized (dateFormatCache) {
                            parentCache = this.parent.getCache(format, loc);
                        }
                    }
                    cache = new Cache(format, loc, parentCache);
                    this.formatCache.put(format, cache);
                }
            }
            return cache;
        }

        public String getFormat(long time) {
            return this.cLFCache.getFormatInternal(time);
        }

        public String getFormat(String format, Locale loc, long time) {
            return this.getCache(format, loc).getFormatInternal(time);
        }

        protected class Cache {
            private static final String cLFFormat = "dd/MMM/yyyy:HH:mm:ss Z";
            private long previousSeconds = Long.MIN_VALUE;
            private String previousFormat = "";
            private long first = Long.MIN_VALUE;
            private long last = Long.MIN_VALUE;
            private int offset = 0;
            private final Date currentDate = new Date();
            protected final String[] cache;
            private SimpleDateFormat formatter;
            private boolean isCLF = false;
            private Cache parent = null;

            private Cache(Cache parent) {
                this(null, parent);
            }

            private Cache(String format, Cache parent) {
                this(format, null, parent);
            }

            private Cache(String format, Locale loc, Cache parent) {
                this.cache = new String[DateFormatCache.this.cacheSize];
                for (int i = 0; i < DateFormatCache.this.cacheSize; ++i) {
                    this.cache[i] = null;
                }
                if (loc == null) {
                    loc = DateFormatCache.this.cacheDefaultLocale;
                }
                if (format == null) {
                    this.isCLF = true;
                    format = cLFFormat;
                    this.formatter = new SimpleDateFormat(format, Locale.US);
                } else {
                    this.formatter = new SimpleDateFormat(format, loc);
                }
                this.formatter.setTimeZone(TimeZone.getDefault());
                this.parent = parent;
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            private String getFormatInternal(long time) {
                int i;
                long seconds = time / 1000L;
                if (seconds == this.previousSeconds) {
                    return this.previousFormat;
                }
                this.previousSeconds = seconds;
                int index = (this.offset + (int)(seconds - this.first)) % DateFormatCache.this.cacheSize;
                if (index < 0) {
                    index += DateFormatCache.this.cacheSize;
                }
                if (seconds >= this.first && seconds <= this.last) {
                    if (this.cache[index] != null) {
                        this.previousFormat = this.cache[index];
                        return this.previousFormat;
                    }
                } else if (seconds >= this.last + (long)DateFormatCache.this.cacheSize || seconds <= this.first - (long)DateFormatCache.this.cacheSize) {
                    this.first = seconds;
                    this.last = this.first + (long)DateFormatCache.this.cacheSize - 1L;
                    index = 0;
                    this.offset = 0;
                    for (i = 1; i < DateFormatCache.this.cacheSize; ++i) {
                        this.cache[i] = null;
                    }
                } else if (seconds > this.last) {
                    i = 1;
                    while ((long)i < seconds - this.last) {
                        this.cache[(index + ((DateFormatCache)DateFormatCache.this).cacheSize - i) % ((DateFormatCache)DateFormatCache.this).cacheSize] = null;
                        ++i;
                    }
                    this.first = seconds - (long)(DateFormatCache.this.cacheSize - 1);
                    this.last = seconds;
                    this.offset = (index + 1) % DateFormatCache.this.cacheSize;
                } else if (seconds < this.first) {
                    i = 1;
                    while ((long)i < this.first - seconds) {
                        this.cache[(index + i) % ((DateFormatCache)DateFormatCache.this).cacheSize] = null;
                        ++i;
                    }
                    this.first = seconds;
                    this.last = seconds + (long)(DateFormatCache.this.cacheSize - 1);
                    this.offset = index;
                }
                if (this.parent != null) {
                    Cache i2 = this.parent;
                    synchronized (i2) {
                        this.previousFormat = this.parent.getFormatInternal(time);
                    }
                } else {
                    this.currentDate.setTime(time);
                    this.previousFormat = this.formatter.format(this.currentDate);
                    if (this.isCLF) {
                        StringBuilder current = new StringBuilder(32);
                        current.append('[');
                        current.append(this.previousFormat);
                        current.append(']');
                        this.previousFormat = current.toString();
                    }
                }
                this.cache[index] = this.previousFormat;
                return this.previousFormat;
            }
        }
    }

    private static enum RemoteAddressType {
        REMOTE,
        PEER;

    }

    private static enum PortType {
        LOCAL,
        REMOTE;

    }

    private static enum FormatType {
        CLF,
        SEC,
        MSEC,
        MSEC_FRAC,
        SDF;

    }
}

