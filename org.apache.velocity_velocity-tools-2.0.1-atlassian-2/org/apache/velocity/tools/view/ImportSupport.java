/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.RequestDispatcher
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 *  org.apache.velocity.runtime.log.Log
 */
package org.apache.velocity.tools.view;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.apache.velocity.runtime.log.Log;

public abstract class ImportSupport {
    protected static final String VALID_SCHEME_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789+.-";
    protected static final String DEFAULT_ENCODING = "ISO-8859-1";
    protected Log LOG;
    protected ServletContext application;
    protected HttpServletRequest request;
    protected HttpServletResponse response;

    public void setLog(Log log) {
        if (log == null) {
            throw new NullPointerException("log should not be set to null");
        }
        this.LOG = log;
    }

    public void setRequest(HttpServletRequest request) {
        if (request == null) {
            throw new NullPointerException("request should not be null");
        }
        this.request = request;
    }

    public void setResponse(HttpServletResponse response) {
        if (response == null) {
            throw new NullPointerException("response should not be null");
        }
        this.response = response;
    }

    public void setServletContext(ServletContext application) {
        if (application == null) {
            throw new NullPointerException("servlet context should not be null");
        }
        this.application = application;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected String acquireString(String url) throws IOException, Exception {
        RequestDispatcher rd;
        if (ImportSupport.isAbsoluteUrl(url)) {
            BufferedReader r = null;
            try {
                int i;
                r = new BufferedReader(this.acquireReader(url));
                StringBuilder sb = new StringBuilder();
                while ((i = r.read()) != -1) {
                    sb.append((char)i);
                }
                String string = sb.toString();
                return string;
            }
            finally {
                if (r != null) {
                    try {
                        r.close();
                    }
                    catch (IOException ioe) {
                        this.LOG.error((Object)"ImportSupport : Could not close reader.", (Throwable)ioe);
                    }
                }
            }
        }
        if (!(this.request instanceof HttpServletRequest) || !(this.response instanceof HttpServletResponse)) {
            throw new Exception("Relative import from non-HTTP request not allowed");
        }
        if (!url.startsWith("/")) {
            String sp = this.request.getServletPath();
            url = sp.substring(0, sp.lastIndexOf(47)) + '/' + url;
        }
        if ((rd = this.application.getRequestDispatcher(url = ImportSupport.stripSession(url))) == null) {
            throw new Exception("Couldn't get a RequestDispatcher for \"" + url + "\"");
        }
        ImportResponseWrapper irw = new ImportResponseWrapper(this.response);
        try {
            rd.include((ServletRequest)this.request, (ServletResponse)irw);
        }
        catch (IOException ex) {
            throw new Exception("Problem importing the relative URL \"" + url + "\". " + ex);
        }
        catch (RuntimeException ex) {
            throw new Exception("Problem importing the relative URL \"" + url + "\". " + ex);
        }
        if (irw.getStatus() < 200 || irw.getStatus() > 299) {
            throw new Exception("Invalid response code '" + irw.getStatus() + "' for \"" + url + "\"");
        }
        return irw.getString();
    }

    protected Reader acquireReader(String url) throws IOException, Exception {
        if (!ImportSupport.isAbsoluteUrl(url)) {
            return new StringReader(this.acquireString(url));
        }
        URLConnection uc = null;
        HttpURLConnection huc = null;
        InputStream i = null;
        try {
            String charSet;
            int status;
            URL u = new URL(url);
            uc = u.openConnection();
            i = uc.getInputStream();
            if (uc instanceof HttpURLConnection && ((status = (huc = (HttpURLConnection)uc).getResponseCode()) < 200 || status > 299)) {
                throw new Exception(status + " " + url);
            }
            InputStreamReader r = null;
            String contentType = uc.getContentType();
            if (contentType != null) {
                charSet = ImportSupport.getContentTypeAttribute(contentType, "charset");
                if (charSet == null) {
                    charSet = DEFAULT_ENCODING;
                }
            } else {
                charSet = DEFAULT_ENCODING;
            }
            try {
                r = new InputStreamReader(i, charSet);
            }
            catch (UnsupportedEncodingException ueex) {
                r = new InputStreamReader(i, DEFAULT_ENCODING);
            }
            if (huc == null) {
                return r;
            }
            return new SafeClosingHttpURLConnectionReader(r, huc);
        }
        catch (IOException ex) {
            if (i != null) {
                try {
                    i.close();
                }
                catch (IOException ioe) {
                    this.LOG.error((Object)"ImportSupport : Could not close InputStream", (Throwable)ioe);
                }
            }
            if (huc != null) {
                huc.disconnect();
            }
            throw new Exception("Problem accessing the absolute URL \"" + url + "\". " + ex);
        }
        catch (RuntimeException ex) {
            if (i != null) {
                try {
                    i.close();
                }
                catch (IOException ioe) {
                    this.LOG.error((Object)"ImportSupport : Could not close InputStream", (Throwable)ioe);
                }
            }
            if (huc != null) {
                huc.disconnect();
            }
            throw new Exception("Problem accessing the absolute URL \"" + url + "\". " + ex);
        }
    }

    public static boolean isAbsoluteUrl(String url) {
        if (url == null) {
            return false;
        }
        int colonPos = url.indexOf(58);
        if (colonPos == -1) {
            return false;
        }
        for (int i = 0; i < colonPos; ++i) {
            if (VALID_SCHEME_CHARS.indexOf(url.charAt(i)) != -1) continue;
            return false;
        }
        return true;
    }

    public static String stripSession(String url) {
        int sessionStart;
        StringBuilder u = new StringBuilder(url);
        while ((sessionStart = u.toString().indexOf(";jsessionid=")) != -1) {
            int sessionEnd = u.toString().indexOf(";", sessionStart + 1);
            if (sessionEnd == -1) {
                sessionEnd = u.toString().indexOf("?", sessionStart + 1);
            }
            if (sessionEnd == -1) {
                sessionEnd = u.length();
            }
            u.delete(sessionStart, sessionEnd);
        }
        return u.toString();
    }

    public static String getContentTypeAttribute(String input, String name) {
        int end;
        int begin;
        int index = input.toUpperCase().indexOf(name.toUpperCase());
        if (index == -1) {
            return null;
        }
        index += name.length();
        if ((index = input.indexOf(61, index)) == -1) {
            return null;
        }
        if ((input = input.substring(++index).trim()).charAt(0) == '\"') {
            begin = 1;
            end = input.indexOf(34, begin);
            if (end == -1) {
                return null;
            }
        } else {
            begin = 0;
            end = input.indexOf(59);
            if (end == -1) {
                end = input.indexOf(32);
            }
            if (end == -1) {
                end = input.length();
            }
        }
        return input.substring(begin, end).trim();
    }

    protected static class ImportResponseWrapper
    extends HttpServletResponseWrapper {
        private StringWriter sw;
        private ByteArrayOutputStream bos;
        private boolean isWriterUsed;
        private boolean isStreamUsed;
        private int status = 200;

        public ImportResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        public PrintWriter getWriter() {
            if (this.isStreamUsed) {
                throw new IllegalStateException("Unexpected internal error during import: Target servlet called getWriter(), then getOutputStream()");
            }
            this.isWriterUsed = true;
            if (this.sw == null) {
                this.sw = new StringWriter();
            }
            return new PrintWriter(this.sw);
        }

        public ServletOutputStream getOutputStream() {
            if (this.isWriterUsed) {
                throw new IllegalStateException("Unexpected internal error during import: Target servlet called getOutputStream(), then getWriter()");
            }
            this.isStreamUsed = true;
            if (this.bos == null) {
                this.bos = new ByteArrayOutputStream();
            }
            ServletOutputStream sos = new ServletOutputStream(){

                public void write(int b) throws IOException {
                    bos.write(b);
                }
            };
            return sos;
        }

        public void setContentType(String x) {
        }

        public void setLocale(Locale x) {
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getStatus() {
            return this.status;
        }

        public String getString() throws UnsupportedEncodingException {
            if (this.isWriterUsed) {
                return this.sw.toString();
            }
            if (this.isStreamUsed) {
                return this.bos.toString(this.getCharacterEncoding());
            }
            return "";
        }
    }

    protected static class SafeClosingHttpURLConnectionReader
    extends Reader {
        private final HttpURLConnection huc;
        private final Reader wrappedReader;

        SafeClosingHttpURLConnectionReader(Reader r, HttpURLConnection huc) {
            this.wrappedReader = r;
            this.huc = huc;
        }

        @Override
        public void close() throws IOException {
            if (null != this.huc) {
                this.huc.disconnect();
            }
            this.wrappedReader.close();
        }

        @Override
        public void mark(int readAheadLimit) throws IOException {
            this.wrappedReader.mark(readAheadLimit);
        }

        @Override
        public boolean markSupported() {
            return this.wrappedReader.markSupported();
        }

        @Override
        public int read() throws IOException {
            return this.wrappedReader.read();
        }

        @Override
        public int read(char[] buf) throws IOException {
            return this.wrappedReader.read(buf);
        }

        @Override
        public int read(char[] buf, int off, int len) throws IOException {
            return this.wrappedReader.read(buf, off, len);
        }

        @Override
        public boolean ready() throws IOException {
            return this.wrappedReader.ready();
        }

        @Override
        public void reset() throws IOException {
            this.wrappedReader.reset();
        }

        @Override
        public long skip(long n) throws IOException {
            return this.wrappedReader.skip(n);
        }
    }
}

