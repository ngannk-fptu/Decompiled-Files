/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import java.util.zip.GZIPOutputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.WebdavResponse;
import org.apache.jackrabbit.webdav.header.CodedUrlHeader;
import org.apache.jackrabbit.webdav.lock.ActiveLock;
import org.apache.jackrabbit.webdav.lock.LockDiscovery;
import org.apache.jackrabbit.webdav.observation.EventDiscovery;
import org.apache.jackrabbit.webdav.observation.Subscription;
import org.apache.jackrabbit.webdav.observation.SubscriptionDiscovery;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class WebdavResponseImpl
implements WebdavResponse {
    private static Logger log = LoggerFactory.getLogger(WebdavResponseImpl.class);
    private HttpServletResponse httpResponse;

    public WebdavResponseImpl(HttpServletResponse httpResponse) {
        this(httpResponse, false);
    }

    public WebdavResponseImpl(HttpServletResponse httpResponse, boolean noCache) {
        this.httpResponse = httpResponse;
        if (noCache) {
            this.addHeader("Pragma", "No-cache");
            this.addHeader("Cache-Control", "no-cache");
        }
    }

    @Override
    public void sendError(DavException exception) throws IOException {
        if (!exception.hasErrorCondition()) {
            this.httpResponse.sendError(exception.getErrorCode(), exception.getStatusPhrase());
        } else {
            this.sendXmlResponse(exception, exception.getErrorCode());
        }
    }

    @Override
    public void sendMultiStatus(MultiStatus multistatus) throws IOException {
        this.sendXmlResponse(multistatus, 207);
    }

    @Override
    public void sendMultiStatus(MultiStatus multistatus, List<String> acceptableContentCodings) throws IOException {
        this.sendXmlResponse(multistatus, 207, acceptableContentCodings);
    }

    @Override
    public void sendRefreshLockResponse(ActiveLock[] locks) throws IOException {
        DavPropertySet propSet = new DavPropertySet();
        propSet.add(new LockDiscovery(locks));
        this.sendXmlResponse(propSet, 200);
    }

    @Override
    public void sendXmlResponse(XmlSerializable serializable, int status) throws IOException {
        this.sendXmlResponse(serializable, status, Collections.emptyList());
    }

    @Override
    public void sendXmlResponse(XmlSerializable serializable, int status, List<String> acceptableContentCodings) throws IOException {
        block18: {
            this.httpResponse.setStatus(status);
            if (serializable != null) {
                try {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    Document doc = DomUtil.createDocument();
                    doc.appendChild(serializable.toXml(doc));
                    DomUtil.transformDocument(doc, out);
                    out.close();
                    this.httpResponse.setContentType("text/xml; charset=UTF-8");
                    if (out.size() < 256 || !acceptableContentCodings.contains("gzip")) {
                        this.httpResponse.setContentLength(out.size());
                        out.writeTo((OutputStream)this.httpResponse.getOutputStream());
                        break block18;
                    }
                    this.httpResponse.setHeader("Content-Encoding", "gzip");
                    try (GZIPOutputStream os = new GZIPOutputStream((OutputStream)this.httpResponse.getOutputStream());){
                        out.writeTo(os);
                    }
                }
                catch (ParserConfigurationException e) {
                    log.error(e.getMessage());
                    throw new IOException(e.getMessage());
                }
                catch (TransformerException e) {
                    log.error(e.getMessage());
                    throw new IOException(e.getMessage());
                }
                catch (SAXException e) {
                    log.error(e.getMessage());
                    throw new IOException(e.getMessage());
                }
            }
        }
    }

    @Override
    public void sendSubscriptionResponse(Subscription subscription) throws IOException {
        String id = subscription.getSubscriptionId();
        if (id != null) {
            CodedUrlHeader h = new CodedUrlHeader("SubscriptionId", id);
            this.httpResponse.setHeader(h.getHeaderName(), h.getHeaderValue());
        }
        DavPropertySet propSet = new DavPropertySet();
        propSet.add(new SubscriptionDiscovery(subscription));
        this.sendXmlResponse(propSet, 200);
    }

    @Override
    public void sendPollResponse(EventDiscovery eventDiscovery) throws IOException {
        this.sendXmlResponse(eventDiscovery, 200);
    }

    public void addCookie(Cookie cookie) {
        this.httpResponse.addCookie(cookie);
    }

    public boolean containsHeader(String s) {
        return this.httpResponse.containsHeader(s);
    }

    public String encodeURL(String s) {
        return this.httpResponse.encodeRedirectURL(s);
    }

    public String encodeRedirectURL(String s) {
        return this.httpResponse.encodeRedirectURL(s);
    }

    public String encodeUrl(String s) {
        return this.httpResponse.encodeUrl(s);
    }

    public String encodeRedirectUrl(String s) {
        return this.httpResponse.encodeRedirectURL(s);
    }

    public void sendError(int i, String s) throws IOException {
        this.httpResponse.sendError(i, s);
    }

    public void sendError(int i) throws IOException {
        this.httpResponse.sendError(i);
    }

    public void sendRedirect(String s) throws IOException {
        this.httpResponse.sendRedirect(s);
    }

    public void setDateHeader(String s, long l) {
        this.httpResponse.setDateHeader(s, l);
    }

    public void addDateHeader(String s, long l) {
        this.httpResponse.addDateHeader(s, l);
    }

    public void setHeader(String s, String s1) {
        this.httpResponse.setHeader(s, s1);
    }

    public void addHeader(String s, String s1) {
        this.httpResponse.addHeader(s, s1);
    }

    public void setIntHeader(String s, int i) {
        this.httpResponse.setIntHeader(s, i);
    }

    public void addIntHeader(String s, int i) {
        this.httpResponse.addIntHeader(s, i);
    }

    public void setStatus(int i) {
        this.httpResponse.setStatus(i);
    }

    public void setStatus(int i, String s) {
        this.httpResponse.setStatus(i, s);
    }

    public String getCharacterEncoding() {
        return this.httpResponse.getCharacterEncoding();
    }

    public ServletOutputStream getOutputStream() throws IOException {
        return this.httpResponse.getOutputStream();
    }

    public PrintWriter getWriter() throws IOException {
        return this.httpResponse.getWriter();
    }

    public void setContentLength(int i) {
        this.httpResponse.setContentLength(i);
    }

    public void setContentType(String s) {
        this.httpResponse.setContentType(s);
    }

    public void setBufferSize(int i) {
        this.httpResponse.setBufferSize(i);
    }

    public int getBufferSize() {
        return this.httpResponse.getBufferSize();
    }

    public void flushBuffer() throws IOException {
        this.httpResponse.flushBuffer();
    }

    public void resetBuffer() {
        this.httpResponse.resetBuffer();
    }

    public boolean isCommitted() {
        return this.httpResponse.isCommitted();
    }

    public void reset() {
        this.httpResponse.reset();
    }

    public void setLocale(Locale locale) {
        this.httpResponse.setLocale(locale);
    }

    public Locale getLocale() {
        return this.httpResponse.getLocale();
    }

    public String getContentType() {
        return this.httpResponse.getContentType();
    }

    public void setCharacterEncoding(String charset) {
        this.httpResponse.setCharacterEncoding(charset);
    }

    public int getStatus() {
        return this.httpResponse.getStatus();
    }

    public String getHeader(String name) {
        return this.httpResponse.getHeader(name);
    }

    public Collection<String> getHeaders(String name) {
        return this.httpResponse.getHeaders(name);
    }

    public Collection<String> getHeaderNames() {
        return this.httpResponse.getHeaderNames();
    }

    public void setContentLengthLong(long len) {
        this.httpResponse.setContentLengthLong(len);
    }

    @Override
    public void setTrailerFields(Supplier<Map<String, String>> supplier) {
        try {
            Method stf = this.httpResponse.getClass().getDeclaredMethod("setTrailerFields", Supplier.class);
            stf.invoke((Object)this.httpResponse, supplier);
        }
        catch (IllegalAccessException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
            throw new UnsupportedOperationException("no servlet 4.0 support on: " + this.httpResponse.getClass(), ex);
        }
    }

    @Override
    public Supplier<Map<String, String>> getTrailerFields() {
        try {
            Method stf = this.httpResponse.getClass().getDeclaredMethod("getTrailerFields", new Class[0]);
            return (Supplier)stf.invoke((Object)this.httpResponse, new Object[0]);
        }
        catch (IllegalAccessException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
            throw new UnsupportedOperationException("no servlet 4.0 support on: " + this.httpResponse.getClass(), ex);
        }
    }
}

