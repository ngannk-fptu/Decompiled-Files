/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.connector;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.Globals;
import org.apache.catalina.connector.Response;
import org.apache.catalina.security.SecurityUtil;
import org.apache.tomcat.util.res.StringManager;

public class ResponseFacade
implements HttpServletResponse {
    protected static final StringManager sm = StringManager.getManager(ResponseFacade.class);
    protected Response response = null;

    public ResponseFacade(Response response) {
        this.response = response;
    }

    public void clear() {
        this.response = null;
    }

    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public void finish() {
        this.checkFacade();
        this.response.setSuspended(true);
    }

    public boolean isFinished() {
        this.checkFacade();
        return this.response.isSuspended();
    }

    public long getContentWritten() {
        this.checkFacade();
        return this.response.getContentWritten();
    }

    public String getCharacterEncoding() {
        this.checkFacade();
        return this.response.getCharacterEncoding();
    }

    public ServletOutputStream getOutputStream() throws IOException {
        if (this.isFinished()) {
            this.response.setSuspended(true);
        }
        return this.response.getOutputStream();
    }

    public PrintWriter getWriter() throws IOException {
        if (this.isFinished()) {
            this.response.setSuspended(true);
        }
        return this.response.getWriter();
    }

    public void setContentLength(int len) {
        if (this.isCommitted()) {
            return;
        }
        this.response.setContentLength(len);
    }

    public void setContentLengthLong(long length) {
        if (this.isCommitted()) {
            return;
        }
        this.response.setContentLengthLong(length);
    }

    public void setContentType(String type) {
        if (this.isCommitted()) {
            return;
        }
        if (SecurityUtil.isPackageProtectionEnabled()) {
            AccessController.doPrivileged(new SetContentTypePrivilegedAction(type));
        } else {
            this.response.setContentType(type);
        }
    }

    public void setBufferSize(int size) {
        this.checkCommitted("coyoteResponse.setBufferSize.ise");
        this.response.setBufferSize(size);
    }

    public int getBufferSize() {
        this.checkFacade();
        return this.response.getBufferSize();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void flushBuffer() throws IOException {
        if (this.isFinished()) {
            return;
        }
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                AccessController.doPrivileged(new FlushBufferPrivilegedAction(this.response));
                return;
            }
            catch (PrivilegedActionException e) {
                Exception ex = e.getException();
                if (!(ex instanceof IOException)) return;
                throw (IOException)ex;
            }
        } else {
            this.response.setAppCommitted(true);
            this.response.flushBuffer();
        }
    }

    public void resetBuffer() {
        this.checkCommitted("coyoteResponse.resetBuffer.ise");
        this.response.resetBuffer();
    }

    public boolean isCommitted() {
        this.checkFacade();
        return this.response.isAppCommitted();
    }

    public void reset() {
        this.checkCommitted("coyoteResponse.reset.ise");
        this.response.reset();
    }

    public void setLocale(Locale loc) {
        if (this.isCommitted()) {
            return;
        }
        this.response.setLocale(loc);
    }

    public Locale getLocale() {
        this.checkFacade();
        return this.response.getLocale();
    }

    public void addCookie(Cookie cookie) {
        if (this.isCommitted()) {
            return;
        }
        this.response.addCookie(cookie);
    }

    public boolean containsHeader(String name) {
        this.checkFacade();
        return this.response.containsHeader(name);
    }

    public String encodeURL(String url) {
        this.checkFacade();
        return this.response.encodeURL(url);
    }

    public String encodeRedirectURL(String url) {
        this.checkFacade();
        return this.response.encodeRedirectURL(url);
    }

    public String encodeUrl(String url) {
        this.checkFacade();
        return this.response.encodeURL(url);
    }

    public String encodeRedirectUrl(String url) {
        this.checkFacade();
        return this.response.encodeRedirectURL(url);
    }

    public void sendError(int sc, String msg) throws IOException {
        this.checkCommitted("coyoteResponse.sendError.ise");
        this.response.setAppCommitted(true);
        this.response.sendError(sc, msg);
    }

    public void sendError(int sc) throws IOException {
        this.checkCommitted("coyoteResponse.sendError.ise");
        this.response.setAppCommitted(true);
        this.response.sendError(sc);
    }

    public void sendRedirect(String location) throws IOException {
        this.checkCommitted("coyoteResponse.sendRedirect.ise");
        this.response.setAppCommitted(true);
        this.response.sendRedirect(location);
    }

    public void setDateHeader(String name, long date) {
        if (this.isCommitted()) {
            return;
        }
        if (Globals.IS_SECURITY_ENABLED) {
            AccessController.doPrivileged(new DateHeaderPrivilegedAction(name, date, false));
        } else {
            this.response.setDateHeader(name, date);
        }
    }

    public void addDateHeader(String name, long date) {
        if (this.isCommitted()) {
            return;
        }
        if (Globals.IS_SECURITY_ENABLED) {
            AccessController.doPrivileged(new DateHeaderPrivilegedAction(name, date, true));
        } else {
            this.response.addDateHeader(name, date);
        }
    }

    public void setHeader(String name, String value) {
        if (this.isCommitted()) {
            return;
        }
        this.response.setHeader(name, value);
    }

    public void addHeader(String name, String value) {
        if (this.isCommitted()) {
            return;
        }
        this.response.addHeader(name, value);
    }

    public void setIntHeader(String name, int value) {
        if (this.isCommitted()) {
            return;
        }
        this.response.setIntHeader(name, value);
    }

    public void addIntHeader(String name, int value) {
        if (this.isCommitted()) {
            return;
        }
        this.response.addIntHeader(name, value);
    }

    public void setStatus(int sc) {
        if (this.isCommitted()) {
            return;
        }
        this.response.setStatus(sc);
    }

    public void setStatus(int sc, String sm) {
        if (this.isCommitted()) {
            return;
        }
        this.response.setStatus(sc, sm);
    }

    public String getContentType() {
        this.checkFacade();
        return this.response.getContentType();
    }

    public void setCharacterEncoding(String encoding) {
        this.checkFacade();
        this.response.setCharacterEncoding(encoding);
    }

    public int getStatus() {
        this.checkFacade();
        return this.response.getStatus();
    }

    public String getHeader(String name) {
        this.checkFacade();
        return this.response.getHeader(name);
    }

    public Collection<String> getHeaderNames() {
        this.checkFacade();
        return this.response.getHeaderNames();
    }

    public Collection<String> getHeaders(String name) {
        this.checkFacade();
        return this.response.getHeaders(name);
    }

    public void setTrailerFields(Supplier<Map<String, String>> supplier) {
        this.checkFacade();
        this.response.setTrailerFields(supplier);
    }

    public Supplier<Map<String, String>> getTrailerFields() {
        this.checkFacade();
        return this.response.getTrailerFields();
    }

    private void checkFacade() {
        if (this.response == null) {
            throw new IllegalStateException(sm.getString("responseFacade.nullResponse"));
        }
    }

    private void checkCommitted(String messageKey) {
        if (this.isCommitted()) {
            throw new IllegalStateException(sm.getString(messageKey));
        }
    }

    private final class SetContentTypePrivilegedAction
    implements PrivilegedAction<Void> {
        private final String contentType;

        SetContentTypePrivilegedAction(String contentType) {
            this.contentType = contentType;
        }

        @Override
        public Void run() {
            ResponseFacade.this.response.setContentType(this.contentType);
            return null;
        }
    }

    private static class FlushBufferPrivilegedAction
    implements PrivilegedExceptionAction<Void> {
        private final Response response;

        FlushBufferPrivilegedAction(Response response) {
            this.response = response;
        }

        @Override
        public Void run() throws IOException {
            this.response.setAppCommitted(true);
            this.response.flushBuffer();
            return null;
        }
    }

    private final class DateHeaderPrivilegedAction
    implements PrivilegedAction<Void> {
        private final String name;
        private final long value;
        private final boolean add;

        DateHeaderPrivilegedAction(String name, long value, boolean add) {
            this.name = name;
            this.value = value;
            this.add = add;
        }

        @Override
        public Void run() {
            if (this.add) {
                ResponseFacade.this.response.addDateHeader(this.name, this.value);
            } else {
                ResponseFacade.this.response.setDateHeader(this.name, this.value);
            }
            return null;
        }
    }
}

