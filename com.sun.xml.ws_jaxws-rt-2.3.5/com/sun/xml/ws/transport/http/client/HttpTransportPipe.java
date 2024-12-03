/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.xml.bind.DatatypeConverter
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.WebServiceFeature
 *  javax.xml.ws.soap.SOAPBinding
 */
package com.sun.xml.ws.transport.http.client;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.ha.StickyFeature;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.pipe.ContentType;
import com.sun.xml.ws.api.pipe.NextAction;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.ws.client.ClientTransportException;
import com.sun.xml.ws.developer.HttpConfigFeature;
import com.sun.xml.ws.resources.ClientMessages;
import com.sun.xml.ws.resources.WsservletMessages;
import com.sun.xml.ws.transport.Headers;
import com.sun.xml.ws.transport.http.HttpAdapter;
import com.sun.xml.ws.transport.http.client.HttpClientTransport;
import com.sun.xml.ws.transport.http.client.HttpResponseProperties;
import com.sun.xml.ws.util.ByteArrayBuffer;
import com.sun.xml.ws.util.RuntimeVersion;
import com.sun.xml.ws.util.StreamUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.CookieHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.SOAPBinding;

public class HttpTransportPipe
extends AbstractTubeImpl {
    private static final List<String> USER_AGENT;
    private static final Logger LOGGER;
    public static boolean dump;
    private final Codec codec;
    private final WSBinding binding;
    private final CookieHandler cookieJar;
    private final boolean sticky;

    public HttpTransportPipe(Codec codec, WSBinding binding) {
        this.codec = codec;
        this.binding = binding;
        this.sticky = HttpTransportPipe.isSticky(binding);
        HttpConfigFeature configFeature = binding.getFeature(HttpConfigFeature.class);
        if (configFeature == null) {
            configFeature = new HttpConfigFeature();
        }
        this.cookieJar = configFeature.getCookieHandler();
    }

    private static boolean isSticky(WSBinding binding) {
        WebServiceFeature[] features;
        boolean tSticky = false;
        for (WebServiceFeature f : features = binding.getFeatures().toArray()) {
            if (!(f instanceof StickyFeature)) continue;
            tSticky = true;
            break;
        }
        return tSticky;
    }

    private HttpTransportPipe(HttpTransportPipe that, TubeCloner cloner) {
        this(that.codec.copy(), that.binding);
        cloner.add(that, this);
    }

    @Override
    public NextAction processException(@NotNull Throwable t) {
        return this.doThrow(t);
    }

    @Override
    public NextAction processRequest(@NotNull Packet request) {
        return this.doReturnWith(this.process(request));
    }

    @Override
    public NextAction processResponse(@NotNull Packet response) {
        return this.doReturnWith(response);
    }

    protected HttpClientTransport getTransport(Packet request, Map<String, List<String>> reqHeaders) {
        return new HttpClientTransport(request, reqHeaders);
    }

    @Override
    public Packet process(Packet request) {
        try {
            Headers reqHeaders = new Headers();
            Map userHeaders = (Map)request.invocationProperties.get("javax.xml.ws.http.request.headers");
            boolean addUserAgent = true;
            if (userHeaders != null) {
                reqHeaders.putAll(userHeaders);
                if (userHeaders.get("User-Agent") != null) {
                    addUserAgent = false;
                }
            }
            if (addUserAgent) {
                reqHeaders.put("User-Agent", USER_AGENT);
            }
            this.addBasicAuth(request, reqHeaders);
            this.addCookies(request, reqHeaders);
            HttpClientTransport con = this.getTransport(request, reqHeaders);
            request.addSatellite(new HttpResponseProperties(con));
            ContentType ct = this.codec.getStaticContentType(request);
            if (ct == null) {
                ByteArrayBuffer buf = new ByteArrayBuffer();
                ct = this.codec.encode(request, buf);
                reqHeaders.put("Content-Length", Collections.singletonList(Integer.toString(buf.size())));
                reqHeaders.put("Content-Type", Collections.singletonList(ct.getContentType()));
                if (ct.getAcceptHeader() != null) {
                    reqHeaders.put("Accept", Collections.singletonList(ct.getAcceptHeader()));
                }
                if (this.binding instanceof SOAPBinding) {
                    this.writeSOAPAction(reqHeaders, ct.getSOAPActionHeader());
                }
                if (dump || LOGGER.isLoggable(Level.FINER)) {
                    this.dump(buf, "HTTP request", reqHeaders);
                }
                buf.writeTo(con.getOutput());
            } else {
                reqHeaders.put("Content-Type", Collections.singletonList(ct.getContentType()));
                if (ct.getAcceptHeader() != null) {
                    reqHeaders.put("Accept", Collections.singletonList(ct.getAcceptHeader()));
                }
                if (this.binding instanceof SOAPBinding) {
                    this.writeSOAPAction(reqHeaders, ct.getSOAPActionHeader());
                }
                if (dump || LOGGER.isLoggable(Level.FINER)) {
                    ByteArrayBuffer buf = new ByteArrayBuffer();
                    this.codec.encode(request, buf);
                    this.dump(buf, "HTTP request - " + request.endpointAddress, reqHeaders);
                    OutputStream out = con.getOutput();
                    if (out != null) {
                        buf.writeTo(out);
                    }
                } else {
                    OutputStream os = con.getOutput();
                    if (os != null) {
                        this.codec.encode(request, os);
                    }
                }
            }
            con.closeOutput();
            return this.createResponsePacket(request, con);
        }
        catch (WebServiceException wex) {
            throw wex;
        }
        catch (Exception ex) {
            throw new WebServiceException((Throwable)ex);
        }
    }

    private Packet createResponsePacket(Packet request, HttpClientTransport con) throws IOException {
        con.readResponseCodeAndMessage();
        this.recordCookies(request, con);
        InputStream responseStream = con.getInput();
        if (dump || LOGGER.isLoggable(Level.FINER)) {
            ByteArrayBuffer buf = new ByteArrayBuffer();
            if (responseStream != null) {
                buf.write(responseStream);
                responseStream.close();
            }
            this.dump(buf, "HTTP response - " + request.endpointAddress + " - " + con.statusCode, con.getHeaders());
            responseStream = buf.newInputStream();
        }
        int cl = con.contentLength;
        InputStream tempIn = null;
        if (cl == -1 && (tempIn = StreamUtils.hasSomeData(responseStream)) != null) {
            responseStream = tempIn;
        }
        if ((cl == 0 || cl == -1 && tempIn == null) && responseStream != null) {
            responseStream.close();
            responseStream = null;
        }
        this.checkStatusCode(responseStream, con);
        if (cl == -1 && con.statusCode == 202 && "Accepted".equals(con.statusMessage) && responseStream != null) {
            ByteArrayBuffer buf = new ByteArrayBuffer();
            buf.write(responseStream);
            responseStream.close();
            responseStream = buf.size() == 0 ? null : buf.newInputStream();
            buf.close();
        }
        Packet reply = request.createClientResponse(null);
        reply.wasTransportSecure = con.isSecure();
        if (responseStream != null) {
            String contentType = con.getContentType();
            if (contentType != null && contentType.contains("text/html") && this.binding instanceof SOAPBinding) {
                throw new ClientTransportException(ClientMessages.localizableHTTP_STATUS_CODE(con.statusCode, con.statusMessage));
            }
            this.codec.decode(responseStream, contentType, reply);
        }
        return reply;
    }

    private void checkStatusCode(InputStream in, HttpClientTransport con) throws IOException {
        int statusCode = con.statusCode;
        String statusMessage = con.statusMessage;
        if (this.binding instanceof SOAPBinding) {
            if (this.binding.getSOAPVersion() == SOAPVersion.SOAP_12) {
                if (statusCode == 200 || statusCode == 202 || this.isErrorCode(statusCode)) {
                    if (this.isErrorCode(statusCode) && in == null) {
                        throw new ClientTransportException(ClientMessages.localizableHTTP_STATUS_CODE(statusCode, statusMessage));
                    }
                    return;
                }
            } else if (statusCode == 200 || statusCode == 202 || statusCode == 500) {
                if (statusCode == 500 && in == null) {
                    throw new ClientTransportException(ClientMessages.localizableHTTP_STATUS_CODE(statusCode, statusMessage));
                }
                return;
            }
            if (in != null) {
                in.close();
            }
            throw new ClientTransportException(ClientMessages.localizableHTTP_STATUS_CODE(statusCode, statusMessage));
        }
    }

    private boolean isErrorCode(int code) {
        return code == 500 || code == 400;
    }

    private void addCookies(Packet context, Map<String, List<String>> reqHeaders) throws IOException {
        Boolean shouldMaintainSessionProperty = (Boolean)context.invocationProperties.get("javax.xml.ws.session.maintain");
        if (shouldMaintainSessionProperty != null && !shouldMaintainSessionProperty.booleanValue()) {
            return;
        }
        if (this.sticky || shouldMaintainSessionProperty != null && shouldMaintainSessionProperty.booleanValue()) {
            Map<String, List<String>> rememberedCookies = this.cookieJar.get(context.endpointAddress.getURI(), reqHeaders);
            this.processCookieHeaders(reqHeaders, rememberedCookies, "Cookie");
            this.processCookieHeaders(reqHeaders, rememberedCookies, "Cookie2");
        }
    }

    private void processCookieHeaders(Map<String, List<String>> requestHeaders, Map<String, List<String>> rememberedCookies, String cookieHeader) {
        List<String> jarCookies = rememberedCookies.get(cookieHeader);
        if (jarCookies != null && !jarCookies.isEmpty()) {
            List<String> resultCookies = this.mergeUserCookies(jarCookies, requestHeaders.get(cookieHeader));
            requestHeaders.put(cookieHeader, resultCookies);
        }
    }

    private List<String> mergeUserCookies(List<String> rememberedCookies, List<String> userCookies) {
        if (userCookies == null || userCookies.isEmpty()) {
            return rememberedCookies;
        }
        HashMap<String, String> map = new HashMap<String, String>();
        this.cookieListToMap(rememberedCookies, map);
        this.cookieListToMap(userCookies, map);
        return new ArrayList<String>(map.values());
    }

    private void cookieListToMap(List<String> cookieList, Map<String, String> targetMap) {
        for (String cookie : cookieList) {
            int index = cookie.indexOf("=");
            String cookieName = cookie.substring(0, index);
            targetMap.put(cookieName, cookie);
        }
    }

    private void recordCookies(Packet context, HttpClientTransport con) throws IOException {
        Boolean shouldMaintainSessionProperty = (Boolean)context.invocationProperties.get("javax.xml.ws.session.maintain");
        if (shouldMaintainSessionProperty != null && !shouldMaintainSessionProperty.booleanValue()) {
            return;
        }
        if (this.sticky || shouldMaintainSessionProperty != null && shouldMaintainSessionProperty.booleanValue()) {
            this.cookieJar.put(context.endpointAddress.getURI(), con.getHeaders());
        }
    }

    private void addBasicAuth(Packet context, Map<String, List<String>> reqHeaders) {
        String pw;
        String user = (String)context.invocationProperties.get("javax.xml.ws.security.auth.username");
        if (user != null && (pw = (String)context.invocationProperties.get("javax.xml.ws.security.auth.password")) != null) {
            StringBuilder buf = new StringBuilder(user);
            buf.append(":");
            buf.append(pw);
            String creds = DatatypeConverter.printBase64Binary((byte[])buf.toString().getBytes());
            reqHeaders.put("Authorization", Collections.singletonList("Basic " + creds));
        }
    }

    private void writeSOAPAction(Map<String, List<String>> reqHeaders, String soapAction) {
        if (SOAPVersion.SOAP_12.equals((Object)this.binding.getSOAPVersion())) {
            return;
        }
        if (soapAction != null) {
            reqHeaders.put("SOAPAction", Collections.singletonList(soapAction));
        } else {
            reqHeaders.put("SOAPAction", Collections.singletonList("\"\""));
        }
    }

    @Override
    public void preDestroy() {
    }

    @Override
    public HttpTransportPipe copy(TubeCloner cloner) {
        return new HttpTransportPipe(this, cloner);
    }

    private void dump(ByteArrayBuffer buf, String caption, Map<String, List<String>> headers) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos, true);
        pw.println("---[" + caption + "]---");
        for (Map.Entry<String, List<String>> header : headers.entrySet()) {
            if (header.getValue().isEmpty()) {
                pw.println(header.getValue());
                continue;
            }
            for (String value : header.getValue()) {
                pw.println(header.getKey() + ": " + value);
            }
        }
        if (buf.size() > HttpAdapter.dump_threshold) {
            byte[] b = buf.getRawData();
            baos.write(b, 0, HttpAdapter.dump_threshold);
            pw.println();
            pw.println(WsservletMessages.MESSAGE_TOO_LONG(HttpAdapter.class.getName() + ".dumpTreshold"));
        } else {
            buf.writeTo(baos);
            pw.println();
        }
        pw.println("--------------------");
        String msg = baos.toString();
        if (dump) {
            System.out.println(msg);
        }
        if (LOGGER.isLoggable(Level.FINER)) {
            LOGGER.log(Level.FINER, msg);
        }
    }

    public static void setDump(boolean dumpMessages) {
        dump = dumpMessages;
    }

    static {
        block2: {
            USER_AGENT = Collections.singletonList(RuntimeVersion.VERSION.toString());
            LOGGER = Logger.getLogger(HttpTransportPipe.class.getName());
            try {
                dump = Boolean.getBoolean(HttpTransportPipe.class.getName() + ".dump");
            }
            catch (SecurityException se) {
                if (!LOGGER.isLoggable(Level.CONFIG)) break block2;
                LOGGER.log(Level.CONFIG, "Cannot read ''{0}'' property, using defaults.", new Object[]{HttpTransportPipe.class.getName() + ".dump"});
            }
        }
    }
}

