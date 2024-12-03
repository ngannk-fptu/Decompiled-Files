/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.http.HTTPBinding
 */
package com.sun.xml.ws.transport.http;

import com.oracle.webservices.api.message.PropertySet;
import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.Component;
import com.sun.xml.ws.api.EndpointAddress;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.addressing.NonAnonymousResponseProcessor;
import com.sun.xml.ws.api.ha.HaInfo;
import com.sun.xml.ws.api.message.ExceptionHasMessage;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.pipe.ContentType;
import com.sun.xml.ws.api.server.AbstractServerAsyncTransport;
import com.sun.xml.ws.api.server.Adapter;
import com.sun.xml.ws.api.server.BoundEndpoint;
import com.sun.xml.ws.api.server.DocumentAddressResolver;
import com.sun.xml.ws.api.server.Module;
import com.sun.xml.ws.api.server.PortAddressResolver;
import com.sun.xml.ws.api.server.SDDocument;
import com.sun.xml.ws.api.server.ServiceDefinition;
import com.sun.xml.ws.api.server.TransportBackChannel;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.api.server.WebServiceContextDelegate;
import com.sun.xml.ws.fault.SOAPFaultBuilder;
import com.sun.xml.ws.resources.WsservletMessages;
import com.sun.xml.ws.server.UnsupportedMediaException;
import com.sun.xml.ws.transport.http.HttpAdapterList;
import com.sun.xml.ws.transport.http.HttpMetadataPublisher;
import com.sun.xml.ws.transport.http.WSHTTPConnection;
import com.sun.xml.ws.util.ByteArrayBuffer;
import com.sun.xml.ws.util.Pool;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.http.HTTPBinding;

public class HttpAdapter
extends Adapter<HttpToolkit> {
    private static final Logger LOGGER;
    protected Map<String, SDDocument> wsdls;
    private Map<SDDocument, String> revWsdls;
    private ServiceDefinition serviceDefinition = null;
    public final HttpAdapterList<? extends HttpAdapter> owner;
    public final String urlPattern;
    protected boolean stickyCookie;
    protected boolean disableJreplicaCookie = false;
    public static final CompletionCallback NO_OP_COMPLETION_CALLBACK;
    public static volatile boolean dump;
    public static volatile int dump_threshold;
    public static volatile boolean publishStatusPage;

    public static HttpAdapter createAlone(WSEndpoint endpoint) {
        return new DummyList().createAdapter("", "", endpoint);
    }

    protected HttpAdapter(WSEndpoint endpoint, HttpAdapterList<? extends HttpAdapter> owner) {
        this(endpoint, owner, null);
    }

    protected HttpAdapter(WSEndpoint endpoint, HttpAdapterList<? extends HttpAdapter> owner, String urlPattern) {
        super(endpoint);
        this.owner = owner;
        this.urlPattern = urlPattern;
        this.initWSDLMap(endpoint.getServiceDefinition());
    }

    public ServiceDefinition getServiceDefinition() {
        return this.serviceDefinition;
    }

    public final void initWSDLMap(final ServiceDefinition serviceDefinition) {
        this.serviceDefinition = serviceDefinition;
        if (serviceDefinition == null) {
            this.wsdls = Collections.emptyMap();
            this.revWsdls = Collections.emptyMap();
        } else {
            this.wsdls = new AbstractMap<String, SDDocument>(){
                private Map<String, SDDocument> delegate = null;

                private synchronized Map<String, SDDocument> delegate() {
                    if (this.delegate != null) {
                        return this.delegate;
                    }
                    this.delegate = new HashMap<String, SDDocument>();
                    TreeMap<String, SDDocument> systemIds = new TreeMap<String, SDDocument>();
                    for (SDDocument sdd : serviceDefinition) {
                        if (sdd == serviceDefinition.getPrimary()) {
                            this.delegate.put("wsdl", sdd);
                            this.delegate.put("WSDL", sdd);
                            continue;
                        }
                        systemIds.put(sdd.getURL().toString(), sdd);
                    }
                    int wsdlnum = 1;
                    int xsdnum = 1;
                    for (Map.Entry e : systemIds.entrySet()) {
                        SDDocument sdd = (SDDocument)e.getValue();
                        if (sdd.isWSDL()) {
                            this.delegate.put("wsdl=" + wsdlnum++, sdd);
                        }
                        if (!sdd.isSchema()) continue;
                        this.delegate.put("xsd=" + xsdnum++, sdd);
                    }
                    return this.delegate;
                }

                @Override
                public void clear() {
                    this.delegate().clear();
                }

                @Override
                public boolean containsKey(Object arg0) {
                    return this.delegate().containsKey(arg0);
                }

                @Override
                public boolean containsValue(Object arg0) {
                    return this.delegate.containsValue(arg0);
                }

                @Override
                public SDDocument get(Object arg0) {
                    return this.delegate().get(arg0);
                }

                @Override
                public boolean isEmpty() {
                    return this.delegate().isEmpty();
                }

                @Override
                public Set<String> keySet() {
                    return this.delegate().keySet();
                }

                @Override
                public SDDocument put(String arg0, SDDocument arg1) {
                    return this.delegate().put(arg0, arg1);
                }

                @Override
                public void putAll(Map<? extends String, ? extends SDDocument> arg0) {
                    this.delegate().putAll(arg0);
                }

                @Override
                public SDDocument remove(Object arg0) {
                    return this.delegate().remove(arg0);
                }

                @Override
                public int size() {
                    return this.delegate().size();
                }

                @Override
                public Collection<SDDocument> values() {
                    return this.delegate().values();
                }

                @Override
                public Set<Map.Entry<String, SDDocument>> entrySet() {
                    return this.delegate().entrySet();
                }
            };
            this.revWsdls = new AbstractMap<SDDocument, String>(){
                private Map<SDDocument, String> delegate = null;

                private synchronized Map<SDDocument, String> delegate() {
                    if (this.delegate != null) {
                        return this.delegate;
                    }
                    this.delegate = new HashMap<SDDocument, String>();
                    for (Map.Entry<String, SDDocument> e : HttpAdapter.this.wsdls.entrySet()) {
                        if (e.getKey().equals("WSDL")) continue;
                        this.delegate.put(e.getValue(), e.getKey());
                    }
                    return this.delegate;
                }

                @Override
                public void clear() {
                    this.delegate().clear();
                }

                @Override
                public boolean containsKey(Object key) {
                    return this.delegate().containsKey(key);
                }

                @Override
                public boolean containsValue(Object value) {
                    return this.delegate().containsValue(value);
                }

                @Override
                public Set<Map.Entry<SDDocument, String>> entrySet() {
                    return this.delegate().entrySet();
                }

                @Override
                public String get(Object key) {
                    return this.delegate().get(key);
                }

                @Override
                public boolean isEmpty() {
                    return super.isEmpty();
                }

                @Override
                public Set<SDDocument> keySet() {
                    return this.delegate().keySet();
                }

                @Override
                public String put(SDDocument key, String value) {
                    return this.delegate().put(key, value);
                }

                @Override
                public void putAll(Map<? extends SDDocument, ? extends String> m) {
                    this.delegate().putAll(m);
                }

                @Override
                public String remove(Object key) {
                    return this.delegate().remove(key);
                }

                @Override
                public int size() {
                    return this.delegate().size();
                }

                @Override
                public Collection<String> values() {
                    return this.delegate().values();
                }
            };
        }
    }

    public String getValidPath() {
        if (this.urlPattern.endsWith("/*")) {
            return this.urlPattern.substring(0, this.urlPattern.length() - 2);
        }
        return this.urlPattern;
    }

    @Override
    protected HttpToolkit createToolkit() {
        return new HttpToolkit();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void handle(@NotNull WSHTTPConnection connection) throws IOException {
        if (this.handleGet(connection)) {
            return;
        }
        Pool currentPool = this.getPool();
        HttpToolkit tk = (HttpToolkit)currentPool.take();
        try {
            tk.handle(connection);
        }
        finally {
            currentPool.recycle(tk);
        }
    }

    public boolean handleGet(@NotNull WSHTTPConnection connection) throws IOException {
        if (connection.getRequestMethod().equals("GET")) {
            for (Component c : this.endpoint.getComponents()) {
                HttpMetadataPublisher spi = c.getSPI(HttpMetadataPublisher.class);
                if (spi == null || !spi.handleMetadataRequest(this, connection)) continue;
                return true;
            }
            if (this.isMetadataQuery(connection.getQueryString())) {
                this.publishWSDL(connection);
                return true;
            }
            WSBinding binding = this.getEndpoint().getBinding();
            if (!(binding instanceof HTTPBinding)) {
                this.writeWebServicesHtmlPage(connection);
                return true;
            }
        } else if (connection.getRequestMethod().equals("HEAD")) {
            connection.getInput().close();
            WSBinding binding = this.getEndpoint().getBinding();
            if (this.isMetadataQuery(connection.getQueryString())) {
                SDDocument doc = this.wsdls.get(connection.getQueryString());
                connection.setStatus(doc != null ? 200 : 404);
                connection.getOutput().close();
                connection.close();
                return true;
            }
            if (!(binding instanceof HTTPBinding)) {
                connection.setStatus(404);
                connection.getOutput().close();
                connection.close();
                return true;
            }
        }
        return false;
    }

    private Packet decodePacket(@NotNull WSHTTPConnection con, @NotNull Codec codec) throws IOException {
        String ct = con.getRequestHeader("Content-Type");
        InputStream in = con.getInput();
        Packet packet = new Packet();
        packet.soapAction = HttpAdapter.fixQuotesAroundSoapAction(con.getRequestHeader("SOAPAction"));
        packet.wasTransportSecure = con.isSecure();
        packet.acceptableMimeTypes = con.getRequestHeader("Accept");
        packet.addSatellite(con);
        this.addSatellites(packet);
        packet.isAdapterDeliversNonAnonymousResponse = true;
        packet.component = this;
        packet.transportBackChannel = new Oneway(con);
        packet.webServiceContextDelegate = con.getWebServiceContextDelegate();
        packet.setState(Packet.State.ServerRequest);
        if (dump || LOGGER.isLoggable(Level.FINER)) {
            ByteArrayBuffer buf = new ByteArrayBuffer();
            buf.write(in);
            in.close();
            HttpAdapter.dump(buf, "HTTP request", con.getRequestHeaders());
            in = buf.newInputStream();
        }
        codec.decode(in, ct, packet);
        return packet;
    }

    protected void addSatellites(Packet packet) {
    }

    public static String fixQuotesAroundSoapAction(String soapAction) {
        if (!(soapAction == null || soapAction.startsWith("\"") && soapAction.endsWith("\""))) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Received WS-I BP non-conformant Unquoted SoapAction HTTP header: {0}", soapAction);
            }
            String fixedSoapAction = soapAction;
            if (!soapAction.startsWith("\"")) {
                fixedSoapAction = "\"" + fixedSoapAction;
            }
            if (!soapAction.endsWith("\"")) {
                fixedSoapAction = fixedSoapAction + "\"";
            }
            return fixedSoapAction;
        }
        return soapAction;
    }

    protected NonAnonymousResponseProcessor getNonAnonymousResponseProcessor() {
        return NonAnonymousResponseProcessor.getDefault();
    }

    protected void writeClientError(int connStatus, @NotNull OutputStream os, @NotNull Packet packet) throws IOException {
    }

    private boolean isClientErrorStatus(int connStatus) {
        return connStatus == 403;
    }

    private boolean isNonAnonymousUri(EndpointAddress addr) {
        return addr != null && !addr.toString().equals(AddressingVersion.W3C.anonymousUri) && !addr.toString().equals(AddressingVersion.MEMBER.anonymousUri);
    }

    private void encodePacket(@NotNull Packet packet, @NotNull WSHTTPConnection con, @NotNull Codec codec) throws IOException {
        ByteArrayBuffer buf;
        OutputStream os;
        if (this.isNonAnonymousUri(packet.endpointAddress) && packet.getMessage() != null) {
            try {
                packet = this.getNonAnonymousResponseProcessor().process(packet);
            }
            catch (RuntimeException re) {
                SOAPVersion soapVersion = packet.getBinding().getSOAPVersion();
                Message faultMsg = SOAPFaultBuilder.createSOAPFaultMessage(soapVersion, null, re);
                packet = packet.createServerResponse(faultMsg, packet.endpoint.getPort(), null, packet.endpoint.getBinding());
            }
        }
        if (con.isClosed()) {
            return;
        }
        Message responseMessage = packet.getMessage();
        this.addStickyCookie(con);
        this.addReplicaCookie(con, packet);
        if (responseMessage == null) {
            if (!con.isClosed()) {
                if (con.getStatus() == 0) {
                    con.setStatus(202);
                }
                OutputStream outputStream = os = con.getProtocol().contains("1.1") ? con.getOutput() : new Http10OutputStream(con);
                if (dump || LOGGER.isLoggable(Level.FINER)) {
                    buf = new ByteArrayBuffer();
                    codec.encode(packet, buf);
                    HttpAdapter.dump(buf, "HTTP response " + con.getStatus(), con.getResponseHeaders());
                    buf.writeTo(os);
                } else {
                    codec.encode(packet, os);
                }
                try {
                    os.close();
                }
                catch (IOException e) {
                    throw new WebServiceException((Throwable)e);
                }
            }
        } else {
            if (con.getStatus() == 0) {
                con.setStatus(responseMessage.isFault() ? 500 : 200);
            }
            if (this.isClientErrorStatus(con.getStatus())) {
                os = con.getOutput();
                if (dump || LOGGER.isLoggable(Level.FINER)) {
                    buf = new ByteArrayBuffer();
                    this.writeClientError(con.getStatus(), buf, packet);
                    HttpAdapter.dump(buf, "HTTP response " + con.getStatus(), con.getResponseHeaders());
                    buf.writeTo(os);
                } else {
                    this.writeClientError(con.getStatus(), os, packet);
                }
                os.close();
                return;
            }
            ContentType contentType = codec.getStaticContentType(packet);
            if (contentType != null) {
                OutputStream os2;
                con.setContentTypeResponseHeader(contentType.getContentType());
                OutputStream outputStream = os2 = con.getProtocol().contains("1.1") ? con.getOutput() : new Http10OutputStream(con);
                if (dump || LOGGER.isLoggable(Level.FINER)) {
                    ByteArrayBuffer buf2 = new ByteArrayBuffer();
                    codec.encode(packet, buf2);
                    HttpAdapter.dump(buf2, "HTTP response " + con.getStatus(), con.getResponseHeaders());
                    buf2.writeTo(os2);
                } else {
                    codec.encode(packet, os2);
                }
                os2.close();
            } else {
                buf = new ByteArrayBuffer();
                contentType = codec.encode(packet, buf);
                con.setContentTypeResponseHeader(contentType.getContentType());
                if (dump || LOGGER.isLoggable(Level.FINER)) {
                    HttpAdapter.dump(buf, "HTTP response " + con.getStatus(), con.getResponseHeaders());
                }
                OutputStream os3 = con.getOutput();
                buf.writeTo(os3);
                os3.close();
            }
        }
    }

    private void addStickyCookie(WSHTTPConnection con) {
        if (this.stickyCookie) {
            String proxyJroute = con.getRequestHeader("proxy-jroute");
            if (proxyJroute == null) {
                return;
            }
            String jrouteId = con.getCookie("JROUTE");
            if (jrouteId == null || !jrouteId.equals(proxyJroute)) {
                con.setCookie("JROUTE", proxyJroute);
            }
        }
    }

    private void addReplicaCookie(WSHTTPConnection con, Packet packet) {
        if (this.stickyCookie) {
            HaInfo haInfo = null;
            if (packet.supports("com.sun.xml.ws.api.message.packet.hainfo")) {
                haInfo = (HaInfo)packet.get("com.sun.xml.ws.api.message.packet.hainfo");
            }
            if (haInfo != null) {
                con.setCookie("METRO_KEY", haInfo.getKey());
                if (!this.disableJreplicaCookie) {
                    con.setCookie("JREPLICA", haInfo.getReplicaInstance());
                }
            }
        }
    }

    public void invokeAsync(WSHTTPConnection con) throws IOException {
        this.invokeAsync(con, NO_OP_COMPLETION_CALLBACK);
    }

    public void invokeAsync(final WSHTTPConnection con, final CompletionCallback callback) throws IOException {
        Packet request;
        if (this.handleGet(con)) {
            callback.onCompletion();
            return;
        }
        final Pool currentPool = this.getPool();
        final HttpToolkit tk = (HttpToolkit)currentPool.take();
        try {
            request = this.decodePacket(con, tk.codec);
        }
        catch (ExceptionHasMessage e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), (Throwable)((Object)e));
            Packet response = new Packet();
            response.setMessage(e.getFaultMessage());
            this.encodePacket(response, con, tk.codec);
            currentPool.recycle(tk);
            con.close();
            callback.onCompletion();
            return;
        }
        catch (UnsupportedMediaException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), (Throwable)((Object)e));
            Packet response = new Packet();
            con.setStatus(415);
            this.encodePacket(response, con, tk.codec);
            currentPool.recycle(tk);
            con.close();
            callback.onCompletion();
            return;
        }
        this.endpoint.process(request, new WSEndpoint.CompletionCallback(){

            @Override
            public void onCompletion(@NotNull Packet response) {
                try {
                    try {
                        HttpAdapter.this.encodePacket(response, con, tk.codec);
                    }
                    catch (IOException ioe) {
                        LOGGER.log(Level.SEVERE, ioe.getMessage(), ioe);
                    }
                    currentPool.recycle(tk);
                }
                finally {
                    con.close();
                    callback.onCompletion();
                }
            }
        }, null);
    }

    private boolean isMetadataQuery(String query) {
        return query != null && (query.equals("WSDL") || query.startsWith("wsdl") || query.startsWith("xsd="));
    }

    public void publishWSDL(@NotNull WSHTTPConnection con) throws IOException {
        con.getInput().close();
        SDDocument doc = this.wsdls.get(con.getQueryString());
        if (doc == null) {
            this.writeNotFoundErrorPage(con, "Invalid Request");
            return;
        }
        con.setStatus(200);
        con.setContentTypeResponseHeader("text/xml;charset=utf-8");
        OutputStream os = con.getProtocol().contains("1.1") ? con.getOutput() : new Http10OutputStream(con);
        PortAddressResolver portAddressResolver = this.getPortAddressResolver(con.getBaseAddress());
        DocumentAddressResolver resolver = this.getDocumentAddressResolver(portAddressResolver);
        doc.writeTo(portAddressResolver, resolver, os);
        os.close();
    }

    public PortAddressResolver getPortAddressResolver(String baseAddress) {
        return this.owner.createPortAddressResolver(baseAddress, this.endpoint.getImplementationClass());
    }

    public DocumentAddressResolver getDocumentAddressResolver(PortAddressResolver portAddressResolver) {
        final String address = portAddressResolver.getAddressFor(this.endpoint.getServiceName(), this.endpoint.getPortName().getLocalPart());
        assert (address != null);
        return new DocumentAddressResolver(){

            @Override
            public String getRelativeAddressFor(@NotNull SDDocument current, @NotNull SDDocument referenced) {
                assert (HttpAdapter.this.revWsdls.containsKey(referenced));
                return address + '?' + (String)HttpAdapter.this.revWsdls.get(referenced);
            }
        };
    }

    private void writeNotFoundErrorPage(WSHTTPConnection con, String message) throws IOException {
        con.setStatus(404);
        con.setContentTypeResponseHeader("text/html; charset=utf-8");
        PrintWriter out = new PrintWriter(new OutputStreamWriter(con.getOutput(), "UTF-8"));
        out.println("<html>");
        out.println("<head><title>");
        out.println(WsservletMessages.SERVLET_HTML_TITLE());
        out.println("</title></head>");
        out.println("<body>");
        out.println(WsservletMessages.SERVLET_HTML_NOT_FOUND(message));
        out.println("</body>");
        out.println("</html>");
        out.close();
    }

    private void writeInternalServerError(WSHTTPConnection con) throws IOException {
        con.setStatus(500);
        con.getOutput().close();
    }

    private static void dump(ByteArrayBuffer buf, String caption, Map<String, List<String>> headers) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos, true);
        pw.println("---[" + caption + "]---");
        if (headers != null) {
            for (Map.Entry<String, List<String>> header : headers.entrySet()) {
                if (header.getValue().isEmpty()) {
                    pw.println(header.getValue());
                    continue;
                }
                for (String value : header.getValue()) {
                    pw.println(header.getKey() + ": " + value);
                }
            }
        }
        if (buf.size() > dump_threshold) {
            byte[] b = buf.getRawData();
            baos.write(b, 0, dump_threshold);
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

    private void writeWebServicesHtmlPage(WSHTTPConnection con) throws IOException {
        if (!publishStatusPage) {
            return;
        }
        con.getInput().close();
        con.setStatus(200);
        con.setContentTypeResponseHeader("text/html; charset=utf-8");
        PrintWriter out = new PrintWriter(new OutputStreamWriter(con.getOutput(), "UTF-8"));
        out.println("<html>");
        out.println("<head><title>");
        out.println(WsservletMessages.SERVLET_HTML_TITLE());
        out.println("</title></head>");
        out.println("<body>");
        out.println(WsservletMessages.SERVLET_HTML_TITLE_2());
        Module module = this.getEndpoint().getContainer().getSPI(Module.class);
        List<Object> endpoints = Collections.emptyList();
        if (module != null) {
            endpoints = module.getBoundEndpoints();
        }
        if (endpoints.isEmpty()) {
            out.println(WsservletMessages.SERVLET_HTML_NO_INFO_AVAILABLE());
        } else {
            out.println("<table width='100%' border='1'>");
            out.println("<tr>");
            out.println("<td>");
            out.println(WsservletMessages.SERVLET_HTML_COLUMN_HEADER_PORT_NAME());
            out.println("</td>");
            out.println("<td>");
            out.println(WsservletMessages.SERVLET_HTML_COLUMN_HEADER_INFORMATION());
            out.println("</td>");
            out.println("</tr>");
            for (BoundEndpoint boundEndpoint : endpoints) {
                String endpointAddress = boundEndpoint.getAddress(con.getBaseAddress()).toString();
                out.println("<tr>");
                out.println("<td>");
                out.println(WsservletMessages.SERVLET_HTML_ENDPOINT_TABLE(boundEndpoint.getEndpoint().getServiceName(), boundEndpoint.getEndpoint().getPortName()));
                out.println("</td>");
                out.println("<td>");
                out.println(WsservletMessages.SERVLET_HTML_INFORMATION_TABLE(endpointAddress, boundEndpoint.getEndpoint().getImplementationClass().getName()));
                out.println("</td>");
                out.println("</tr>");
            }
            out.println("</table>");
        }
        out.println("</body>");
        out.println("</html>");
        out.close();
    }

    public static synchronized void setPublishStatus(boolean publish) {
        publishStatusPage = publish;
    }

    public static void setDump(boolean dumpMessages) {
        dump = dumpMessages;
    }

    public static void setDumpTreshold(int treshold) {
        if (treshold < 0) {
            throw new IllegalArgumentException("Treshold must be positive number");
        }
        dump_threshold = treshold;
    }

    static {
        block9: {
            block8: {
                block7: {
                    LOGGER = Logger.getLogger(HttpAdapter.class.getName());
                    NO_OP_COMPLETION_CALLBACK = new CompletionCallback(){

                        @Override
                        public void onCompletion() {
                        }
                    };
                    dump = false;
                    dump_threshold = 4096;
                    publishStatusPage = true;
                    try {
                        dump = Boolean.getBoolean(HttpAdapter.class.getName() + ".dump");
                    }
                    catch (SecurityException se) {
                        if (!LOGGER.isLoggable(Level.CONFIG)) break block7;
                        LOGGER.log(Level.CONFIG, "Cannot read ''{0}'' property, using defaults.", new Object[]{HttpAdapter.class.getName() + ".dump"});
                    }
                }
                try {
                    dump_threshold = Integer.getInteger(HttpAdapter.class.getName() + ".dumpTreshold", 4096);
                }
                catch (SecurityException se) {
                    if (!LOGGER.isLoggable(Level.CONFIG)) break block8;
                    LOGGER.log(Level.CONFIG, "Cannot read ''{0}'' property, using defaults.", new Object[]{HttpAdapter.class.getName() + ".dumpTreshold"});
                }
            }
            try {
                if (System.getProperty(HttpAdapter.class.getName() + ".publishStatusPage") != null) {
                    HttpAdapter.setPublishStatus(Boolean.getBoolean(HttpAdapter.class.getName() + ".publishStatusPage"));
                }
            }
            catch (SecurityException se) {
                if (!LOGGER.isLoggable(Level.CONFIG)) break block9;
                LOGGER.log(Level.CONFIG, "Cannot read ''{0}'' property, using defaults.", new Object[]{HttpAdapter.class.getName() + ".publishStatusPage"});
            }
        }
    }

    private static final class DummyList
    extends HttpAdapterList<HttpAdapter> {
        private DummyList() {
        }

        @Override
        protected HttpAdapter createHttpAdapter(String name, String urlPattern, WSEndpoint<?> endpoint) {
            return new HttpAdapter(endpoint, this, urlPattern);
        }
    }

    private static final class Http10OutputStream
    extends ByteArrayBuffer {
        private final WSHTTPConnection con;

        Http10OutputStream(WSHTTPConnection con) {
            this.con = con;
        }

        @Override
        public void close() throws IOException {
            super.close();
            this.con.setContentLengthResponseHeader(this.size());
            OutputStream os = this.con.getOutput();
            this.writeTo(os);
            os.close();
        }
    }

    final class HttpToolkit
    extends Adapter.Toolkit {
        HttpToolkit() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void handle(WSHTTPConnection con) throws IOException {
            try {
                Packet packet;
                boolean invoke = false;
                try {
                    packet = HttpAdapter.this.decodePacket(con, this.codec);
                    invoke = true;
                }
                catch (Exception e) {
                    packet = new Packet();
                    if (e instanceof ExceptionHasMessage) {
                        LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        packet.setMessage(((ExceptionHasMessage)((Object)e)).getFaultMessage());
                    }
                    if (e instanceof UnsupportedMediaException) {
                        LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        con.setStatus(415);
                    }
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    con.setStatus(500);
                }
                if (invoke) {
                    try {
                        packet = this.head.process(packet, con.getWebServiceContextDelegate(), packet.transportBackChannel);
                    }
                    catch (Throwable e) {
                        LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        if (!con.isClosed()) {
                            HttpAdapter.this.writeInternalServerError(con);
                        }
                        if (!con.isClosed()) {
                            if (LOGGER.isLoggable(Level.FINE)) {
                                LOGGER.log(Level.FINE, "Closing HTTP Connection with status: {0}", con.getStatus());
                            }
                            con.close();
                        }
                        return;
                    }
                }
                HttpAdapter.this.encodePacket(packet, con, this.codec);
            }
            finally {
                if (!con.isClosed()) {
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.log(Level.FINE, "Closing HTTP Connection with status: {0}", con.getStatus());
                    }
                    con.close();
                }
            }
        }
    }

    static final class Oneway
    implements TransportBackChannel {
        WSHTTPConnection con;
        boolean closed;

        Oneway(WSHTTPConnection con) {
            this.con = con;
        }

        @Override
        public void close() {
            if (!this.closed) {
                this.closed = true;
                if (this.con.getStatus() == 0) {
                    this.con.setStatus(202);
                }
                OutputStream output = null;
                try {
                    output = this.con.getOutput();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                if (dump || LOGGER.isLoggable(Level.FINER)) {
                    try {
                        ByteArrayBuffer buf = new ByteArrayBuffer();
                        HttpAdapter.dump(buf, "HTTP response " + this.con.getStatus(), this.con.getResponseHeaders());
                    }
                    catch (Exception e) {
                        throw new WebServiceException(e.toString(), (Throwable)e);
                    }
                }
                if (output != null) {
                    try {
                        output.close();
                    }
                    catch (IOException e) {
                        throw new WebServiceException((Throwable)e);
                    }
                }
                this.con.close();
            }
        }
    }

    final class AsyncTransport
    extends AbstractServerAsyncTransport<WSHTTPConnection> {
        public AsyncTransport() {
            super(HttpAdapter.this.endpoint);
        }

        public void handleAsync(WSHTTPConnection con) throws IOException {
            super.handle(con);
        }

        @Override
        protected void encodePacket(WSHTTPConnection con, @NotNull Packet packet, @NotNull Codec codec) throws IOException {
            HttpAdapter.this.encodePacket(packet, con, codec);
        }

        @Override
        @Nullable
        protected String getAcceptableMimeTypes(WSHTTPConnection con) {
            return null;
        }

        @Override
        @Nullable
        protected TransportBackChannel getTransportBackChannel(WSHTTPConnection con) {
            return new Oneway(con);
        }

        @Override
        @NotNull
        protected PropertySet getPropertySet(WSHTTPConnection con) {
            return con;
        }

        @Override
        @NotNull
        protected WebServiceContextDelegate getWebServiceContextDelegate(WSHTTPConnection con) {
            return con.getWebServiceContextDelegate();
        }
    }

    public static interface CompletionCallback {
        public void onCompletion();
    }
}

