/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpSession
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.ExceptionUtils
 */
package org.apache.catalina.valves;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.util.ServerInfo;
import org.apache.catalina.util.URLEncoder;
import org.apache.catalina.valves.AbstractAccessLogValve;
import org.apache.catalina.valves.AccessLogValve;
import org.apache.catalina.valves.ValveBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;

public class ExtendedAccessLogValve
extends AccessLogValve {
    private static final Log log = LogFactory.getLog(ExtendedAccessLogValve.class);

    static String wrap(Object value) {
        String svalue;
        if (value == null || "-".equals(value)) {
            return "-";
        }
        try {
            svalue = value.toString();
        }
        catch (Throwable e) {
            ExceptionUtils.handleThrowable((Throwable)e);
            return "-";
        }
        StringBuilder buffer = new StringBuilder(svalue.length() + 2);
        buffer.append('\"');
        int i = 0;
        while (i < svalue.length()) {
            int j = svalue.indexOf(34, i);
            if (j == -1) {
                buffer.append(svalue.substring(i));
                i = svalue.length();
                continue;
            }
            buffer.append(svalue.substring(i, j + 1));
            buffer.append('\"');
            i = j + 1;
        }
        buffer.append('\"');
        return buffer.toString();
    }

    @Override
    protected synchronized void open() {
        super.open();
        if (this.currentLogFile.length() == 0L) {
            this.writer.println("#Fields: " + this.pattern);
            this.writer.println("#Version: 2.0");
            this.writer.println("#Software: " + ServerInfo.getServerInfo());
        }
    }

    @Override
    protected AbstractAccessLogValve.AccessLogElement[] createLogElements() {
        if (log.isDebugEnabled()) {
            log.debug((Object)("decodePattern, pattern =" + this.pattern));
        }
        ArrayList<AbstractAccessLogValve.AccessLogElement> list = new ArrayList<AbstractAccessLogValve.AccessLogElement>();
        PatternTokenizer tokenizer = new PatternTokenizer(this.pattern);
        try {
            tokenizer.getWhiteSpaces();
            if (tokenizer.isEnded()) {
                log.info((Object)sm.getString("extendedAccessLogValve.emptyPattern"));
                return null;
            }
            String token = tokenizer.getToken();
            while (token != null) {
                AbstractAccessLogValve.AccessLogElement element;
                if (log.isDebugEnabled()) {
                    log.debug((Object)("token = " + token));
                }
                if ((element = this.getLogElement(token, tokenizer)) == null) break;
                list.add(element);
                String whiteSpaces = tokenizer.getWhiteSpaces();
                if (whiteSpaces.length() > 0) {
                    list.add(new AbstractAccessLogValve.StringElement(whiteSpaces));
                }
                if (tokenizer.isEnded()) break;
                token = tokenizer.getToken();
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)("finished decoding with element size of: " + list.size()));
            }
            return list.toArray(new AbstractAccessLogValve.AccessLogElement[0]);
        }
        catch (IOException e) {
            log.error((Object)sm.getString("extendedAccessLogValve.patternParseError", new Object[]{this.pattern}), (Throwable)e);
            return null;
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected AbstractAccessLogValve.AccessLogElement getLogElement(String token, PatternTokenizer tokenizer) throws IOException {
        if ("date".equals(token)) {
            return new DateElement();
        }
        if ("time".equals(token)) {
            if (!tokenizer.hasSubToken()) return new TimeElement();
            String nextToken = tokenizer.getToken();
            if ("taken".equals(nextToken)) {
                return new AbstractAccessLogValve.ElapsedTimeElement(false);
            }
        } else {
            if ("bytes".equals(token)) {
                return new AbstractAccessLogValve.ByteSentElement(true);
            }
            if ("cached".equals(token)) {
                return new AbstractAccessLogValve.StringElement("-");
            }
            if ("c".equals(token)) {
                String nextToken = tokenizer.getToken();
                if ("ip".equals(nextToken)) {
                    return new AbstractAccessLogValve.RemoteAddrElement();
                }
                if ("dns".equals(nextToken)) {
                    return new AbstractAccessLogValve.HostElement();
                }
            } else if ("s".equals(token)) {
                String nextToken = tokenizer.getToken();
                if ("ip".equals(nextToken)) {
                    return new AbstractAccessLogValve.LocalAddrElement(this.getIpv6Canonical());
                }
                if ("dns".equals(nextToken)) {
                    return new AbstractAccessLogValve.AccessLogElement(){

                        @Override
                        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
                            String value;
                            try {
                                value = InetAddress.getLocalHost().getHostName();
                            }
                            catch (Throwable e) {
                                ExceptionUtils.handleThrowable((Throwable)e);
                                value = "localhost";
                            }
                            buf.append(value);
                        }
                    };
                }
            } else {
                if ("cs".equals(token)) {
                    return this.getClientToServerElement(tokenizer);
                }
                if ("sc".equals(token)) {
                    return this.getServerToClientElement(tokenizer);
                }
                if ("sr".equals(token) || "rs".equals(token)) {
                    return this.getProxyElement(tokenizer);
                }
                if ("x".equals(token)) {
                    return this.getXParameterElement(tokenizer);
                }
            }
        }
        log.error((Object)sm.getString("extendedAccessLogValve.decodeError", new Object[]{token}));
        return null;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected AbstractAccessLogValve.AccessLogElement getClientToServerElement(PatternTokenizer tokenizer) throws IOException {
        if (tokenizer.hasSubToken()) {
            String token = tokenizer.getToken();
            if ("method".equals(token)) {
                return new AbstractAccessLogValve.MethodElement();
            }
            if ("uri".equals(token)) {
                if (!tokenizer.hasSubToken()) return new AbstractAccessLogValve.AccessLogElement(){

                    @Override
                    public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
                        String query = request.getQueryString();
                        if (query == null) {
                            buf.append(request.getRequestURI());
                        } else {
                            buf.append(request.getRequestURI());
                            buf.append('?');
                            buf.append(request.getQueryString());
                        }
                    }
                };
                token = tokenizer.getToken();
                if ("stem".equals(token)) {
                    return new AbstractAccessLogValve.RequestURIElement();
                }
                if ("query".equals(token)) {
                    return new AbstractAccessLogValve.AccessLogElement(){

                        @Override
                        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
                            String query = request.getQueryString();
                            if (query != null) {
                                buf.append(query);
                            } else {
                                buf.append('-');
                            }
                        }
                    };
                }
            }
        } else if (tokenizer.hasParameter()) {
            String parameter = tokenizer.getParameter();
            if (parameter != null) return new RequestHeaderElement(parameter);
            log.error((Object)sm.getString("extendedAccessLogValve.noClosing"));
            return null;
        }
        log.error((Object)sm.getString("extendedAccessLogValve.decodeError", new Object[]{tokenizer.getRemains()}));
        return null;
    }

    protected AbstractAccessLogValve.AccessLogElement getServerToClientElement(PatternTokenizer tokenizer) throws IOException {
        if (tokenizer.hasSubToken()) {
            String token = tokenizer.getToken();
            if ("status".equals(token)) {
                return new AbstractAccessLogValve.HttpStatusCodeElement();
            }
            if ("comment".equals(token)) {
                return new AbstractAccessLogValve.StringElement("?");
            }
        } else if (tokenizer.hasParameter()) {
            String parameter = tokenizer.getParameter();
            if (parameter == null) {
                log.error((Object)sm.getString("extendedAccessLogValve.noClosing"));
                return null;
            }
            return new ResponseHeaderElement(parameter);
        }
        log.error((Object)sm.getString("extendedAccessLogValve.decodeError", new Object[]{tokenizer.getRemains()}));
        return null;
    }

    protected AbstractAccessLogValve.AccessLogElement getProxyElement(PatternTokenizer tokenizer) throws IOException {
        Object token = null;
        if (tokenizer.hasSubToken()) {
            tokenizer.getToken();
            return new AbstractAccessLogValve.StringElement("-");
        }
        if (tokenizer.hasParameter()) {
            tokenizer.getParameter();
            return new AbstractAccessLogValve.StringElement("-");
        }
        log.error((Object)sm.getString("extendedAccessLogValve.decodeError", new Object[]{token}));
        return null;
    }

    protected AbstractAccessLogValve.AccessLogElement getXParameterElement(PatternTokenizer tokenizer) throws IOException {
        if (!tokenizer.hasSubToken()) {
            log.error((Object)sm.getString("extendedAccessLogValve.badXParam"));
            return null;
        }
        String token = tokenizer.getToken();
        if ("threadname".equals(token)) {
            return new AbstractAccessLogValve.ThreadNameElement();
        }
        if (!tokenizer.hasParameter()) {
            log.error((Object)sm.getString("extendedAccessLogValve.badXParam"));
            return null;
        }
        String parameter = tokenizer.getParameter();
        if (parameter == null) {
            log.error((Object)sm.getString("extendedAccessLogValve.noClosing"));
            return null;
        }
        if ("A".equals(token)) {
            return new ServletContextElement(parameter);
        }
        if ("C".equals(token)) {
            return new CookieElement(parameter);
        }
        if ("R".equals(token)) {
            return new RequestAttributeElement(parameter);
        }
        if ("S".equals(token)) {
            return new SessionAttributeElement(parameter);
        }
        if ("H".equals(token)) {
            return this.getServletRequestElement(parameter);
        }
        if ("P".equals(token)) {
            return new RequestParameterElement(parameter);
        }
        if ("O".equals(token)) {
            return new ResponseAllHeaderElement(parameter);
        }
        log.error((Object)sm.getString("extendedAccessLogValve.badXParamValue", new Object[]{token}));
        return null;
    }

    protected AbstractAccessLogValve.AccessLogElement getServletRequestElement(String parameter) {
        if ("authType".equals(parameter)) {
            return new AbstractAccessLogValve.AccessLogElement(){

                @Override
                public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
                    buf.append(ExtendedAccessLogValve.wrap(request.getAuthType()));
                }
            };
        }
        if ("remoteUser".equals(parameter)) {
            return new AbstractAccessLogValve.AccessLogElement(){

                @Override
                public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
                    buf.append(ExtendedAccessLogValve.wrap(request.getRemoteUser()));
                }
            };
        }
        if ("requestedSessionId".equals(parameter)) {
            return new AbstractAccessLogValve.AccessLogElement(){

                @Override
                public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
                    buf.append(ExtendedAccessLogValve.wrap(request.getRequestedSessionId()));
                }
            };
        }
        if ("requestedSessionIdFromCookie".equals(parameter)) {
            return new AbstractAccessLogValve.AccessLogElement(){

                @Override
                public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
                    buf.append(ExtendedAccessLogValve.wrap("" + request.isRequestedSessionIdFromCookie()));
                }
            };
        }
        if ("requestedSessionIdValid".equals(parameter)) {
            return new AbstractAccessLogValve.AccessLogElement(){

                @Override
                public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
                    buf.append(ExtendedAccessLogValve.wrap("" + request.isRequestedSessionIdValid()));
                }
            };
        }
        if ("contentLength".equals(parameter)) {
            return new AbstractAccessLogValve.AccessLogElement(){

                @Override
                public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
                    buf.append(ExtendedAccessLogValve.wrap("" + request.getContentLengthLong()));
                }
            };
        }
        if ("characterEncoding".equals(parameter)) {
            return new AbstractAccessLogValve.AccessLogElement(){

                @Override
                public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
                    buf.append(ExtendedAccessLogValve.wrap(request.getCharacterEncoding()));
                }
            };
        }
        if ("locale".equals(parameter)) {
            return new AbstractAccessLogValve.AccessLogElement(){

                @Override
                public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
                    buf.append(ExtendedAccessLogValve.wrap(request.getLocale()));
                }
            };
        }
        if ("protocol".equals(parameter)) {
            return new AbstractAccessLogValve.AccessLogElement(){

                @Override
                public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
                    buf.append(ExtendedAccessLogValve.wrap(request.getProtocol()));
                }
            };
        }
        if ("scheme".equals(parameter)) {
            return new AbstractAccessLogValve.AccessLogElement(){

                @Override
                public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
                    buf.append(request.getScheme());
                }
            };
        }
        if ("secure".equals(parameter)) {
            return new AbstractAccessLogValve.AccessLogElement(){

                @Override
                public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
                    buf.append(ExtendedAccessLogValve.wrap("" + request.isSecure()));
                }
            };
        }
        log.error((Object)sm.getString("extendedAccessLogValve.badXParamValue", new Object[]{parameter}));
        return null;
    }

    protected static class PatternTokenizer {
        private final StringReader sr;
        private StringBuilder buf = new StringBuilder();
        private boolean ended = false;
        private boolean subToken;
        private boolean parameter;

        public PatternTokenizer(String str) {
            this.sr = new StringReader(str);
        }

        public boolean hasSubToken() {
            return this.subToken;
        }

        public boolean hasParameter() {
            return this.parameter;
        }

        public String getToken() throws IOException {
            if (this.ended) {
                return null;
            }
            String result = null;
            this.subToken = false;
            this.parameter = false;
            int c = this.sr.read();
            while (c != -1) {
                switch (c) {
                    case 32: {
                        result = this.buf.toString();
                        this.buf.setLength(0);
                        this.buf.append((char)c);
                        return result;
                    }
                    case 45: {
                        result = this.buf.toString();
                        this.buf.setLength(0);
                        this.subToken = true;
                        return result;
                    }
                    case 40: {
                        result = this.buf.toString();
                        this.buf.setLength(0);
                        this.parameter = true;
                        return result;
                    }
                    case 41: {
                        throw new IOException(ValveBase.sm.getString("patternTokenizer.unexpectedParenthesis"));
                    }
                }
                this.buf.append((char)c);
                c = this.sr.read();
            }
            this.ended = true;
            if (this.buf.length() != 0) {
                return this.buf.toString();
            }
            return null;
        }

        public String getParameter() throws IOException {
            if (!this.parameter) {
                return null;
            }
            this.parameter = false;
            int c = this.sr.read();
            while (c != -1) {
                if (c == 41) {
                    String result = this.buf.toString();
                    this.buf = new StringBuilder();
                    return result;
                }
                this.buf.append((char)c);
                c = this.sr.read();
            }
            return null;
        }

        public String getWhiteSpaces() throws IOException {
            if (this.isEnded()) {
                return "";
            }
            StringBuilder whiteSpaces = new StringBuilder();
            if (this.buf.length() > 0) {
                whiteSpaces.append((CharSequence)this.buf);
                this.buf = new StringBuilder();
            }
            int c = this.sr.read();
            while (Character.isWhitespace((char)c)) {
                whiteSpaces.append((char)c);
                c = this.sr.read();
            }
            if (c == -1) {
                this.ended = true;
            } else {
                this.buf.append((char)c);
            }
            return whiteSpaces.toString();
        }

        public boolean isEnded() {
            return this.ended;
        }

        public String getRemains() throws IOException {
            StringBuilder remains = new StringBuilder();
            int c = this.sr.read();
            while (c != -1) {
                remains.append((char)c);
                c = this.sr.read();
            }
            return remains.toString();
        }
    }

    protected static class DateElement
    implements AbstractAccessLogValve.AccessLogElement {
        private static final long INTERVAL = 86400000L;
        private static final ThreadLocal<ElementTimestampStruct> currentDate = ThreadLocal.withInitial(() -> new ElementTimestampStruct("yyyy-MM-dd"));

        protected DateElement() {
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            ElementTimestampStruct eds = currentDate.get();
            long millis = eds.currentTimestamp.getTime();
            if (date.getTime() > millis + 86400000L - 1L || date.getTime() < millis) {
                eds.currentTimestamp.setTime(date.getTime() - date.getTime() % 86400000L);
                eds.currentTimestampString = eds.currentTimestampFormat.format(eds.currentTimestamp);
            }
            buf.append(eds.currentTimestampString);
        }
    }

    protected static class TimeElement
    implements AbstractAccessLogValve.AccessLogElement {
        private static final long INTERVAL = 1000L;
        private static final ThreadLocal<ElementTimestampStruct> currentTime = ThreadLocal.withInitial(() -> new ElementTimestampStruct("HH:mm:ss"));

        protected TimeElement() {
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            ElementTimestampStruct eds = currentTime.get();
            long millis = eds.currentTimestamp.getTime();
            if (date.getTime() > millis + 1000L - 1L || date.getTime() < millis) {
                eds.currentTimestamp.setTime(date.getTime() - date.getTime() % 1000L);
                eds.currentTimestampString = eds.currentTimestampFormat.format(eds.currentTimestamp);
            }
            buf.append(eds.currentTimestampString);
        }
    }

    protected static class RequestHeaderElement
    implements AbstractAccessLogValve.AccessLogElement {
        private final String header;

        public RequestHeaderElement(String header) {
            this.header = header;
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            buf.append(ExtendedAccessLogValve.wrap(request.getHeader(this.header)));
        }
    }

    protected static class ResponseHeaderElement
    implements AbstractAccessLogValve.AccessLogElement {
        private final String header;

        public ResponseHeaderElement(String header) {
            this.header = header;
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            buf.append(ExtendedAccessLogValve.wrap(response.getHeader(this.header)));
        }
    }

    protected static class ServletContextElement
    implements AbstractAccessLogValve.AccessLogElement {
        private final String attribute;

        public ServletContextElement(String attribute) {
            this.attribute = attribute;
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            buf.append(ExtendedAccessLogValve.wrap(request.getContext().getServletContext().getAttribute(this.attribute)));
        }
    }

    protected static class CookieElement
    implements AbstractAccessLogValve.AccessLogElement {
        private final String name;

        public CookieElement(String name) {
            this.name = name;
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            StringBuilder value = new StringBuilder();
            boolean first = true;
            Cookie[] c = request.getCookies();
            for (int i = 0; c != null && i < c.length; ++i) {
                if (!this.name.equals(c[i].getName())) continue;
                if (first) {
                    first = false;
                } else {
                    value.append(',');
                }
                value.append(c[i].getValue());
            }
            if (value.length() == 0) {
                buf.append('-');
            } else {
                buf.append(ExtendedAccessLogValve.wrap(value.toString()));
            }
        }
    }

    protected static class RequestAttributeElement
    implements AbstractAccessLogValve.AccessLogElement {
        private final String attribute;

        public RequestAttributeElement(String attribute) {
            this.attribute = attribute;
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            buf.append(ExtendedAccessLogValve.wrap(request.getAttribute(this.attribute)));
        }
    }

    protected static class SessionAttributeElement
    implements AbstractAccessLogValve.AccessLogElement {
        private final String attribute;

        public SessionAttributeElement(String attribute) {
            this.attribute = attribute;
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            HttpSession session = null;
            if (request != null && (session = request.getSession(false)) != null) {
                buf.append(ExtendedAccessLogValve.wrap(session.getAttribute(this.attribute)));
            }
        }
    }

    protected static class RequestParameterElement
    implements AbstractAccessLogValve.AccessLogElement {
        private final String parameter;

        public RequestParameterElement(String parameter) {
            this.parameter = parameter;
        }

        private String urlEncode(String value) {
            if (null == value || value.length() == 0) {
                return null;
            }
            return URLEncoder.QUERY.encode(value, StandardCharsets.UTF_8);
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            buf.append(ExtendedAccessLogValve.wrap(this.urlEncode(request.getParameter(this.parameter))));
        }
    }

    protected static class ResponseAllHeaderElement
    implements AbstractAccessLogValve.AccessLogElement {
        private final String header;

        public ResponseAllHeaderElement(String header) {
            this.header = header;
        }

        @Override
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            if (null != response) {
                Iterator<String> iter = response.getHeaders(this.header).iterator();
                if (iter.hasNext()) {
                    StringBuilder buffer = new StringBuilder();
                    boolean first = true;
                    while (iter.hasNext()) {
                        if (first) {
                            first = false;
                        } else {
                            buffer.append(',');
                        }
                        buffer.append(iter.next());
                    }
                    buf.append(ExtendedAccessLogValve.wrap(buffer.toString()));
                }
                return;
            }
            buf.append('-');
        }
    }

    private static class ElementTimestampStruct {
        private final Date currentTimestamp = new Date(0L);
        private final SimpleDateFormat currentTimestampFormat;
        private String currentTimestampString;

        ElementTimestampStruct(String format) {
            this.currentTimestampFormat = new SimpleDateFormat(format, Locale.US);
            this.currentTimestampFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        }
    }
}

