/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.DispatcherType;
import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public abstract class HttpServlet
extends GenericServlet {
    private static final long serialVersionUID = 1L;
    private static final String METHOD_DELETE = "DELETE";
    private static final String METHOD_HEAD = "HEAD";
    private static final String METHOD_GET = "GET";
    private static final String METHOD_OPTIONS = "OPTIONS";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_TRACE = "TRACE";
    private static final String HEADER_IFMODSINCE = "If-Modified-Since";
    private static final String HEADER_LASTMOD = "Last-Modified";
    private static final String LSTRING_FILE = "javax.servlet.http.LocalStrings";
    private static final ResourceBundle lStrings = ResourceBundle.getBundle("javax.servlet.http.LocalStrings");
    private static final List<String> SENSITIVE_HTTP_HEADERS = Arrays.asList("authorization", "cookie", "x-forwarded", "forwarded", "proxy-authorization");

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String msg = lStrings.getString("http.method_get_not_supported");
        this.sendMethodNotAllowed(req, resp, msg);
    }

    protected long getLastModified(HttpServletRequest req) {
        return -1L;
    }

    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (DispatcherType.INCLUDE.equals((Object)req.getDispatcherType())) {
            this.doGet(req, resp);
        } else {
            NoBodyResponse response = new NoBodyResponse(resp);
            this.doGet(req, response);
            if (req.isAsyncStarted()) {
                req.getAsyncContext().addListener(new NoBodyAsyncContextListener(response));
            } else {
                response.setContentLength();
            }
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String msg = lStrings.getString("http.method_post_not_supported");
        this.sendMethodNotAllowed(req, resp, msg);
    }

    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String msg = lStrings.getString("http.method_put_not_supported");
        this.sendMethodNotAllowed(req, resp, msg);
    }

    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String msg = lStrings.getString("http.method_delete_not_supported");
        this.sendMethodNotAllowed(req, resp, msg);
    }

    private void sendMethodNotAllowed(HttpServletRequest req, HttpServletResponse resp, String msg) throws IOException {
        String protocol = req.getProtocol();
        if (protocol.length() == 0 || protocol.endsWith("0.9") || protocol.endsWith("1.0")) {
            resp.sendError(400, msg);
        } else {
            resp.sendError(405, msg);
        }
    }

    private static Method[] getAllDeclaredMethods(Class<?> c) {
        if (c.equals(HttpServlet.class)) {
            return null;
        }
        Method[] parentMethods = HttpServlet.getAllDeclaredMethods(c.getSuperclass());
        Method[] thisMethods = c.getDeclaredMethods();
        if (parentMethods != null && parentMethods.length > 0) {
            Method[] allMethods = new Method[parentMethods.length + thisMethods.length];
            System.arraycopy(parentMethods, 0, allMethods, 0, parentMethods.length);
            System.arraycopy(thisMethods, 0, allMethods, parentMethods.length, thisMethods.length);
            thisMethods = allMethods;
        }
        return thisMethods;
    }

    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Method[] methods = HttpServlet.getAllDeclaredMethods(this.getClass());
        boolean ALLOW_GET = false;
        boolean ALLOW_HEAD = false;
        boolean ALLOW_POST = false;
        boolean ALLOW_PUT = false;
        boolean ALLOW_DELETE = false;
        boolean ALLOW_TRACE = true;
        boolean ALLOW_OPTIONS = true;
        Class<?> clazz = null;
        try {
            clazz = Class.forName("org.apache.catalina.connector.RequestFacade");
            Method getAllowTrace = clazz.getMethod("getAllowTrace", null);
            ALLOW_TRACE = (Boolean)getAllowTrace.invoke((Object)req, (Object[])null);
        }
        catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException getAllowTrace) {
            // empty catch block
        }
        for (int i = 0; i < methods.length; ++i) {
            Method m = methods[i];
            if (m.getName().equals("doGet")) {
                ALLOW_GET = true;
                ALLOW_HEAD = true;
            }
            if (m.getName().equals("doPost")) {
                ALLOW_POST = true;
            }
            if (m.getName().equals("doPut")) {
                ALLOW_PUT = true;
            }
            if (!m.getName().equals("doDelete")) continue;
            ALLOW_DELETE = true;
        }
        String allow = null;
        if (ALLOW_GET) {
            allow = METHOD_GET;
        }
        if (ALLOW_HEAD) {
            allow = allow == null ? METHOD_HEAD : allow + ", HEAD";
        }
        if (ALLOW_POST) {
            allow = allow == null ? METHOD_POST : allow + ", POST";
        }
        if (ALLOW_PUT) {
            allow = allow == null ? METHOD_PUT : allow + ", PUT";
        }
        if (ALLOW_DELETE) {
            allow = allow == null ? METHOD_DELETE : allow + ", DELETE";
        }
        if (ALLOW_TRACE) {
            allow = allow == null ? METHOD_TRACE : allow + ", TRACE";
        }
        if (ALLOW_OPTIONS) {
            allow = allow == null ? METHOD_OPTIONS : allow + ", OPTIONS";
        }
        resp.setHeader("Allow", allow);
    }

    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String CRLF = "\r\n";
        StringBuilder buffer = new StringBuilder("TRACE ").append(req.getRequestURI()).append(' ').append(req.getProtocol());
        Enumeration<String> reqHeaderNames = req.getHeaderNames();
        while (reqHeaderNames.hasMoreElements()) {
            String headerName = reqHeaderNames.nextElement();
            if (this.isSensitiveHeader(headerName)) continue;
            Enumeration<String> headerValues = req.getHeaders(headerName);
            while (headerValues.hasMoreElements()) {
                String headerValue = headerValues.nextElement();
                buffer.append(CRLF).append(headerName).append(": ").append(headerValue);
            }
        }
        buffer.append(CRLF);
        int responseLength = buffer.length();
        resp.setContentType("message/http");
        resp.setContentLength(responseLength);
        ServletOutputStream out = resp.getOutputStream();
        out.print(buffer.toString());
        out.close();
    }

    private boolean isSensitiveHeader(String headerName) {
        String lcHeaderName = headerName.toLowerCase(Locale.ENGLISH);
        for (String sensitiveHeaderName : SENSITIVE_HTTP_HEADERS) {
            if (!lcHeaderName.startsWith(sensitiveHeaderName)) continue;
            return true;
        }
        return false;
    }

    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        if (method.equals(METHOD_GET)) {
            long lastModified = this.getLastModified(req);
            if (lastModified == -1L) {
                this.doGet(req, resp);
            } else {
                long ifModifiedSince;
                try {
                    ifModifiedSince = req.getDateHeader(HEADER_IFMODSINCE);
                }
                catch (IllegalArgumentException iae) {
                    ifModifiedSince = -1L;
                }
                if (ifModifiedSince < lastModified / 1000L * 1000L) {
                    this.maybeSetLastModified(resp, lastModified);
                    this.doGet(req, resp);
                } else {
                    resp.setStatus(304);
                }
            }
        } else if (method.equals(METHOD_HEAD)) {
            long lastModified = this.getLastModified(req);
            this.maybeSetLastModified(resp, lastModified);
            this.doHead(req, resp);
        } else if (method.equals(METHOD_POST)) {
            this.doPost(req, resp);
        } else if (method.equals(METHOD_PUT)) {
            this.doPut(req, resp);
        } else if (method.equals(METHOD_DELETE)) {
            this.doDelete(req, resp);
        } else if (method.equals(METHOD_OPTIONS)) {
            this.doOptions(req, resp);
        } else if (method.equals(METHOD_TRACE)) {
            this.doTrace(req, resp);
        } else {
            String errMsg = lStrings.getString("http.method_not_implemented");
            Object[] errArgs = new Object[]{method};
            errMsg = MessageFormat.format(errMsg, errArgs);
            resp.sendError(501, errMsg);
        }
    }

    private void maybeSetLastModified(HttpServletResponse resp, long lastModified) {
        if (resp.containsHeader(HEADER_LASTMOD)) {
            return;
        }
        if (lastModified >= 0L) {
            resp.setDateHeader(HEADER_LASTMOD, lastModified);
        }
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        HttpServletResponse response;
        HttpServletRequest request;
        try {
            request = (HttpServletRequest)req;
            response = (HttpServletResponse)res;
        }
        catch (ClassCastException e) {
            throw new ServletException(lStrings.getString("http.non_http"));
        }
        this.service(request, response);
    }

    private static class NoBodyResponse
    extends HttpServletResponseWrapper {
        private final NoBodyOutputStream noBodyOutputStream = new NoBodyOutputStream(this);
        private ServletOutputStream originalOutputStream;
        private NoBodyPrintWriter noBodyWriter;
        private boolean didSetContentLength;

        private NoBodyResponse(HttpServletResponse r) {
            super(r);
        }

        private void setContentLength() {
            if (!this.didSetContentLength) {
                if (this.noBodyWriter != null) {
                    this.noBodyWriter.flush();
                }
                super.setContentLengthLong(this.noBodyOutputStream.getWrittenByteCount());
            }
        }

        @Override
        public void setContentLength(int len) {
            super.setContentLength(len);
            this.didSetContentLength = true;
        }

        @Override
        public void setContentLengthLong(long len) {
            super.setContentLengthLong(len);
            this.didSetContentLength = true;
        }

        @Override
        public void setHeader(String name, String value) {
            super.setHeader(name, value);
            this.checkHeader(name);
        }

        @Override
        public void addHeader(String name, String value) {
            super.addHeader(name, value);
            this.checkHeader(name);
        }

        @Override
        public void setIntHeader(String name, int value) {
            super.setIntHeader(name, value);
            this.checkHeader(name);
        }

        @Override
        public void addIntHeader(String name, int value) {
            super.addIntHeader(name, value);
            this.checkHeader(name);
        }

        private void checkHeader(String name) {
            if ("content-length".equalsIgnoreCase(name)) {
                this.didSetContentLength = true;
            }
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            this.originalOutputStream = this.getResponse().getOutputStream();
            return this.noBodyOutputStream;
        }

        @Override
        public PrintWriter getWriter() throws UnsupportedEncodingException {
            if (this.noBodyWriter == null) {
                this.noBodyWriter = new NoBodyPrintWriter(this.noBodyOutputStream, this.getCharacterEncoding());
            }
            return this.noBodyWriter;
        }

        @Override
        public void reset() {
            super.reset();
            this.resetBuffer();
            this.originalOutputStream = null;
        }

        @Override
        public void resetBuffer() {
            this.noBodyOutputStream.resetBuffer();
            if (this.noBodyWriter != null) {
                this.noBodyWriter.resetBuffer();
            }
        }
    }

    private static class NoBodyAsyncContextListener
    implements AsyncListener {
        private final NoBodyResponse noBodyResponse;

        NoBodyAsyncContextListener(NoBodyResponse noBodyResponse) {
            this.noBodyResponse = noBodyResponse;
        }

        @Override
        public void onComplete(AsyncEvent event) throws IOException {
            this.noBodyResponse.setContentLength();
        }

        @Override
        public void onTimeout(AsyncEvent event) throws IOException {
        }

        @Override
        public void onError(AsyncEvent event) throws IOException {
        }

        @Override
        public void onStartAsync(AsyncEvent event) throws IOException {
        }
    }

    private static class NoBodyPrintWriter
    extends PrintWriter {
        private final NoBodyOutputStream out;
        private final String encoding;
        private PrintWriter pw;

        NoBodyPrintWriter(NoBodyOutputStream out, String encoding) throws UnsupportedEncodingException {
            super(out);
            this.out = out;
            this.encoding = encoding;
            OutputStreamWriter osw = new OutputStreamWriter((OutputStream)out, encoding);
            this.pw = new PrintWriter(osw);
        }

        private void resetBuffer() {
            this.out.resetBuffer();
            OutputStreamWriter osw = null;
            try {
                osw = new OutputStreamWriter((OutputStream)this.out, this.encoding);
            }
            catch (UnsupportedEncodingException unsupportedEncodingException) {
                // empty catch block
            }
            this.pw = new PrintWriter(osw);
        }

        @Override
        public void flush() {
            this.pw.flush();
        }

        @Override
        public void close() {
            this.pw.close();
        }

        @Override
        public boolean checkError() {
            return this.pw.checkError();
        }

        @Override
        public void write(int c) {
            this.pw.write(c);
        }

        @Override
        public void write(char[] buf, int off, int len) {
            this.pw.write(buf, off, len);
        }

        @Override
        public void write(char[] buf) {
            this.pw.write(buf);
        }

        @Override
        public void write(String s, int off, int len) {
            this.pw.write(s, off, len);
        }

        @Override
        public void write(String s) {
            this.pw.write(s);
        }

        @Override
        public void print(boolean b) {
            this.pw.print(b);
        }

        @Override
        public void print(char c) {
            this.pw.print(c);
        }

        @Override
        public void print(int i) {
            this.pw.print(i);
        }

        @Override
        public void print(long l) {
            this.pw.print(l);
        }

        @Override
        public void print(float f) {
            this.pw.print(f);
        }

        @Override
        public void print(double d) {
            this.pw.print(d);
        }

        @Override
        public void print(char[] s) {
            this.pw.print(s);
        }

        @Override
        public void print(String s) {
            this.pw.print(s);
        }

        @Override
        public void print(Object obj) {
            this.pw.print(obj);
        }

        @Override
        public void println() {
            this.pw.println();
        }

        @Override
        public void println(boolean x) {
            this.pw.println(x);
        }

        @Override
        public void println(char x) {
            this.pw.println(x);
        }

        @Override
        public void println(int x) {
            this.pw.println(x);
        }

        @Override
        public void println(long x) {
            this.pw.println(x);
        }

        @Override
        public void println(float x) {
            this.pw.println(x);
        }

        @Override
        public void println(double x) {
            this.pw.println(x);
        }

        @Override
        public void println(char[] x) {
            this.pw.println(x);
        }

        @Override
        public void println(String x) {
            this.pw.println(x);
        }

        @Override
        public void println(Object x) {
            this.pw.println(x);
        }
    }

    private static class NoBodyOutputStream
    extends ServletOutputStream {
        private static final String LSTRING_FILE = "javax.servlet.http.LocalStrings";
        private static final ResourceBundle lStrings = ResourceBundle.getBundle("javax.servlet.http.LocalStrings");
        private final NoBodyResponse response;
        private boolean flushed = false;
        private long writtenByteCount = 0L;

        private NoBodyOutputStream(NoBodyResponse response) {
            this.response = response;
        }

        private long getWrittenByteCount() {
            return this.writtenByteCount;
        }

        @Override
        public void write(int b) throws IOException {
            ++this.writtenByteCount;
            this.checkCommit();
        }

        @Override
        public void write(byte[] buf, int offset, int len) throws IOException {
            if (buf == null) {
                throw new NullPointerException(lStrings.getString("err.io.nullArray"));
            }
            if (offset < 0 || len < 0 || offset + len > buf.length) {
                String msg = lStrings.getString("err.io.indexOutOfBounds");
                Object[] msgArgs = new Object[]{offset, len, buf.length};
                msg = MessageFormat.format(msg, msgArgs);
                throw new IndexOutOfBoundsException(msg);
            }
            this.writtenByteCount += (long)len;
            this.checkCommit();
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener listener) {
            this.response.originalOutputStream.setWriteListener(listener);
        }

        private void checkCommit() throws IOException {
            if (!this.flushed && this.writtenByteCount > (long)this.response.getBufferSize()) {
                this.response.flushBuffer();
                this.flushed = true;
            }
        }

        private void resetBuffer() {
            if (this.flushed) {
                throw new IllegalStateException(lStrings.getString("err.state.commit"));
            }
            this.writtenByteCount = 0L;
        }
    }
}

