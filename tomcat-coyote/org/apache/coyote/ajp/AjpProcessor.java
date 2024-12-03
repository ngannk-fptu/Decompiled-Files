/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.buf.ByteChunk
 *  org.apache.tomcat.util.buf.MessageBytes
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.coyote.ajp;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.coyote.AbstractProcessor;
import org.apache.coyote.ActionCode;
import org.apache.coyote.Adapter;
import org.apache.coyote.ContinueResponseTiming;
import org.apache.coyote.ErrorState;
import org.apache.coyote.InputBuffer;
import org.apache.coyote.OutputBuffer;
import org.apache.coyote.RequestInfo;
import org.apache.coyote.ajp.AbstractAjpProtocol;
import org.apache.coyote.ajp.AjpMessage;
import org.apache.coyote.ajp.Constants;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.ApplicationBufferHandler;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;

public class AjpProcessor
extends AbstractProcessor {
    private static final Log log = LogFactory.getLog(AjpProcessor.class);
    private static final StringManager sm = StringManager.getManager(AjpProcessor.class);
    private static final byte[] endMessageArray;
    private static final byte[] endAndCloseMessageArray;
    private static final byte[] flushMessageArray;
    private static final byte[] pongMessageArray;
    private static final Set<String> javaxAttributes;
    private static final Set<String> iisTlsAttributes;
    private final AbstractAjpProtocol<?> protocol;
    private final byte[] getBodyMessageArray;
    private final int outputMaxChunkSize;
    private final AjpMessage requestHeaderMessage;
    private final AjpMessage responseMessage;
    private int responseMsgPos = -1;
    private final AjpMessage bodyMessage;
    private final MessageBytes bodyBytes = MessageBytes.newInstance();
    private final MessageBytes tmpMB = MessageBytes.newInstance();
    private final MessageBytes certificates = MessageBytes.newInstance();
    private boolean endOfStream = false;
    private boolean empty = true;
    private boolean first = true;
    private boolean waitingForBodyMessage = false;
    private boolean replay = false;
    private boolean swallowResponse = false;
    private boolean responseFinished = false;
    private long bytesWritten = 0L;

    public AjpProcessor(AbstractAjpProtocol<?> protocol, Adapter adapter) {
        super(adapter);
        this.protocol = protocol;
        int packetSize = protocol.getPacketSize();
        this.outputMaxChunkSize = packetSize - 8;
        this.request.setInputBuffer(new SocketInputBuffer());
        this.requestHeaderMessage = new AjpMessage(packetSize);
        this.responseMessage = new AjpMessage(packetSize);
        this.bodyMessage = new AjpMessage(packetSize);
        AjpMessage getBodyMessage = new AjpMessage(16);
        getBodyMessage.reset();
        getBodyMessage.appendByte(6);
        getBodyMessage.appendInt(8186 + packetSize - 8192);
        getBodyMessage.end();
        this.getBodyMessageArray = new byte[getBodyMessage.getLen()];
        System.arraycopy(getBodyMessage.getBuffer(), 0, this.getBodyMessageArray, 0, getBodyMessage.getLen());
        this.response.setOutputBuffer(new SocketOutputBuffer());
    }

    @Override
    protected boolean flushBufferedWrite() throws IOException {
        if (this.hasDataToWrite()) {
            this.socketWrapper.flush(false);
            if (this.hasDataToWrite()) {
                this.response.checkRegisterForWrite();
                return true;
            }
        }
        return false;
    }

    @Override
    protected void dispatchNonBlockingRead() {
        if (this.available(true) > 0) {
            super.dispatchNonBlockingRead();
        }
    }

    @Override
    protected AbstractEndpoint.Handler.SocketState dispatchEndRequest() {
        this.socketWrapper.setReadTimeout(this.protocol.getKeepAliveTimeout());
        this.recycle();
        if (this.protocol.isPaused()) {
            return AbstractEndpoint.Handler.SocketState.CLOSED;
        }
        return AbstractEndpoint.Handler.SocketState.OPEN;
    }

    @Override
    public AbstractEndpoint.Handler.SocketState service(SocketWrapperBase<?> socket) throws IOException {
        RequestInfo rp = this.request.getRequestProcessor();
        rp.setStage(1);
        this.socketWrapper = socket;
        boolean cping = false;
        boolean firstRead = true;
        while (!this.getErrorState().isError() && !this.protocol.isPaused()) {
            try {
                if (!this.readMessage(this.requestHeaderMessage, firstRead)) break;
                firstRead = false;
                this.socketWrapper.setReadTimeout(this.protocol.getConnectionTimeout());
                byte type = this.requestHeaderMessage.getByte();
                if (type == 10) {
                    if (this.protocol.isPaused()) {
                        this.recycle();
                        break;
                    }
                    cping = true;
                    try {
                        this.socketWrapper.write(true, pongMessageArray, 0, pongMessageArray.length);
                        this.socketWrapper.flush(true);
                    }
                    catch (IOException e) {
                        if (this.getLog().isDebugEnabled()) {
                            this.getLog().debug((Object)"Pong message failed", (Throwable)e);
                        }
                        this.setErrorState(ErrorState.CLOSE_CONNECTION_NOW, e);
                    }
                    this.recycle();
                    continue;
                }
                if (type != 2) {
                    if (this.getLog().isDebugEnabled()) {
                        this.getLog().debug((Object)("Unexpected message: " + type));
                    }
                    this.setErrorState(ErrorState.CLOSE_CONNECTION_NOW, null);
                    break;
                }
                this.request.setStartTime(System.currentTimeMillis());
            }
            catch (IOException e) {
                this.setErrorState(ErrorState.CLOSE_CONNECTION_NOW, e);
                break;
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                this.getLog().debug((Object)sm.getString("ajpprocessor.header.error"), t);
                this.response.setStatus(400);
                this.setErrorState(ErrorState.CLOSE_CLEAN, t);
            }
            if (this.getErrorState().isIoAllowed()) {
                rp.setStage(2);
                try {
                    this.prepareRequest();
                }
                catch (Throwable t) {
                    ExceptionUtils.handleThrowable((Throwable)t);
                    this.getLog().debug((Object)sm.getString("ajpprocessor.request.prepare"), t);
                    this.response.setStatus(500);
                    this.setErrorState(ErrorState.CLOSE_CLEAN, t);
                }
            }
            if (this.getErrorState().isIoAllowed() && !cping && this.protocol.isPaused()) {
                this.response.setStatus(503);
                this.setErrorState(ErrorState.CLOSE_CLEAN, null);
            }
            cping = false;
            if (this.getErrorState().isIoAllowed()) {
                try {
                    rp.setStage(3);
                    this.getAdapter().service(this.request, this.response);
                }
                catch (InterruptedIOException e) {
                    this.setErrorState(ErrorState.CLOSE_CONNECTION_NOW, e);
                }
                catch (Throwable t) {
                    ExceptionUtils.handleThrowable((Throwable)t);
                    this.getLog().error((Object)sm.getString("ajpprocessor.request.process"), t);
                    this.response.setStatus(500);
                    this.setErrorState(ErrorState.CLOSE_CLEAN, t);
                    this.getAdapter().log(this.request, this.response, 0L);
                }
            }
            if (this.isAsync() && !this.getErrorState().isError()) break;
            if (!this.responseFinished && this.getErrorState().isIoAllowed()) {
                try {
                    this.action(ActionCode.COMMIT, null);
                    this.finishResponse();
                }
                catch (IOException ioe) {
                    this.setErrorState(ErrorState.CLOSE_CONNECTION_NOW, ioe);
                }
                catch (Throwable t) {
                    ExceptionUtils.handleThrowable((Throwable)t);
                    this.setErrorState(ErrorState.CLOSE_NOW, t);
                }
            }
            if (this.getErrorState().isError()) {
                this.response.setStatus(500);
            }
            this.request.updateCounters();
            rp.setStage(6);
            this.socketWrapper.setReadTimeout(this.protocol.getKeepAliveTimeout());
            this.recycle();
        }
        rp.setStage(7);
        if (this.getErrorState().isError() || this.protocol.isPaused()) {
            return AbstractEndpoint.Handler.SocketState.CLOSED;
        }
        if (this.isAsync()) {
            return AbstractEndpoint.Handler.SocketState.LONG;
        }
        return AbstractEndpoint.Handler.SocketState.OPEN;
    }

    @Override
    public void recycle() {
        this.getAdapter().checkRecycled(this.request, this.response);
        super.recycle();
        this.request.recycle();
        this.response.recycle();
        this.first = true;
        this.endOfStream = false;
        this.waitingForBodyMessage = false;
        this.empty = true;
        this.replay = false;
        this.responseFinished = false;
        this.certificates.recycle();
        this.swallowResponse = false;
        this.bytesWritten = 0L;
    }

    @Override
    public void pause() {
    }

    private boolean receive(boolean block) throws IOException {
        this.bodyMessage.reset();
        if (!this.readMessage(this.bodyMessage, block)) {
            return false;
        }
        this.waitingForBodyMessage = false;
        if (this.bodyMessage.getLen() == 0) {
            return false;
        }
        int blen = this.bodyMessage.peekInt();
        if (blen == 0) {
            return false;
        }
        this.bodyMessage.getBodyBytes(this.bodyBytes);
        this.empty = false;
        return true;
    }

    private boolean readMessage(AjpMessage message, boolean block) throws IOException {
        byte[] buf = message.getBuffer();
        if (!this.read(buf, 0, 4, block)) {
            return false;
        }
        int messageLength = message.processHeader(true);
        if (messageLength < 0) {
            throw new IOException(sm.getString("ajpmessage.invalidLength", new Object[]{messageLength}));
        }
        if (messageLength == 0) {
            return true;
        }
        if (messageLength > message.getBuffer().length) {
            String msg = sm.getString("ajpprocessor.header.tooLong", new Object[]{messageLength, buf.length});
            log.error((Object)msg);
            throw new IllegalArgumentException(msg);
        }
        this.read(buf, 4, messageLength, true);
        return true;
    }

    protected boolean refillReadBuffer(boolean block) throws IOException {
        boolean moreData;
        if (this.replay) {
            this.endOfStream = true;
        }
        if (this.endOfStream) {
            return false;
        }
        if (this.first) {
            this.first = false;
            long contentLength = this.request.getContentLengthLong();
            if (contentLength > 0L) {
                this.waitingForBodyMessage = true;
            } else if (contentLength == 0L) {
                this.endOfStream = true;
                return false;
            }
        }
        if (!this.waitingForBodyMessage) {
            this.socketWrapper.write(true, this.getBodyMessageArray, 0, this.getBodyMessageArray.length);
            this.socketWrapper.flush(true);
            this.waitingForBodyMessage = true;
        }
        if (!(moreData = this.receive(block)) && !this.waitingForBodyMessage) {
            this.endOfStream = true;
        }
        return moreData;
    }

    private void prepareRequest() {
        ByteChunk uriBC;
        byte attributeCode;
        boolean isSSL;
        byte methodCode = this.requestHeaderMessage.getByte();
        if (methodCode != -1) {
            String methodName = Constants.getMethodForCode(methodCode - 1);
            this.request.method().setString(methodName);
        }
        this.requestHeaderMessage.getBytes(this.request.protocol());
        this.requestHeaderMessage.getBytes(this.request.requestURI());
        this.requestHeaderMessage.getBytes(this.request.remoteAddr());
        this.requestHeaderMessage.getBytes(this.request.remoteHost());
        this.requestHeaderMessage.getBytes(this.request.localName());
        this.request.setLocalPort(this.requestHeaderMessage.getInt());
        if (this.socketWrapper != null) {
            this.request.peerAddr().setString(this.socketWrapper.getRemoteAddr());
        }
        boolean bl = isSSL = this.requestHeaderMessage.getByte() != 0;
        if (isSSL) {
            this.request.scheme().setString("https");
        }
        MimeHeaders headers = this.request.getMimeHeaders();
        headers.setLimit(this.protocol.getMaxHeaderCount());
        boolean contentLengthSet = false;
        int hCount = this.requestHeaderMessage.getInt();
        for (int i = 0; i < hCount; ++i) {
            String hName = null;
            int isc = this.requestHeaderMessage.peekInt();
            int hId = isc & 0xFF;
            MessageBytes vMB = null;
            if (40960 == (isc &= 0xFF00)) {
                this.requestHeaderMessage.getInt();
                hName = Constants.getHeaderForCode(hId - 1);
                vMB = headers.addValue(hName);
            } else {
                hId = -1;
                this.requestHeaderMessage.getBytes(this.tmpMB);
                ByteChunk bc = this.tmpMB.getByteChunk();
                vMB = headers.addValue(bc.getBuffer(), bc.getStart(), bc.getLength());
            }
            this.requestHeaderMessage.getBytes(vMB);
            if (hId == 8 || hId == -1 && this.tmpMB.equalsIgnoreCase("Content-Length")) {
                long cl = vMB.getLong();
                if (contentLengthSet) {
                    this.response.setStatus(400);
                    this.setErrorState(ErrorState.CLOSE_CLEAN, null);
                    continue;
                }
                contentLengthSet = true;
                this.request.setContentLength(cl);
                continue;
            }
            if (hId != 7 && (hId != -1 || !this.tmpMB.equalsIgnoreCase("Content-Type"))) continue;
            ByteChunk bchunk = vMB.getByteChunk();
            this.request.contentType().setBytes(bchunk.getBytes(), bchunk.getOffset(), bchunk.getLength());
        }
        String secret = this.protocol.getSecret();
        boolean secretPresentInRequest = false;
        block18: while ((attributeCode = this.requestHeaderMessage.getByte()) != -1) {
            switch (attributeCode) {
                case 10: {
                    this.requestHeaderMessage.getBytes(this.tmpMB);
                    String n = this.tmpMB.toString();
                    this.requestHeaderMessage.getBytes(this.tmpMB);
                    String v = this.tmpMB.toString();
                    if (n.equals("AJP_LOCAL_ADDR")) {
                        this.request.localAddr().setString(v);
                        continue block18;
                    }
                    if (n.equals("AJP_REMOTE_PORT")) {
                        try {
                            this.request.setRemotePort(Integer.parseInt(v));
                        }
                        catch (NumberFormatException bchunk) {}
                        continue block18;
                    }
                    if (n.equals("AJP_SSL_PROTOCOL")) {
                        this.request.setAttribute("org.apache.tomcat.util.net.secure_protocol_version", v);
                        continue block18;
                    }
                    if (n.equals("JK_LB_ACTIVATION")) {
                        this.request.setAttribute(n, v);
                        continue block18;
                    }
                    if (javaxAttributes.contains(n)) {
                        this.request.setAttribute(n, v);
                        continue block18;
                    }
                    if (iisTlsAttributes.contains(n)) {
                        this.request.setAttribute(n, v);
                        continue block18;
                    }
                    Pattern pattern = this.protocol.getAllowedRequestAttributesPatternInternal();
                    if (pattern != null && pattern.matcher(n).matches()) {
                        this.request.setAttribute(n, v);
                        continue block18;
                    }
                    log.warn((Object)sm.getString("ajpprocessor.unknownAttribute", new Object[]{n}));
                    this.response.setStatus(403);
                    this.setErrorState(ErrorState.CLOSE_CLEAN, null);
                    continue block18;
                }
                case 1: {
                    this.requestHeaderMessage.getBytes(this.tmpMB);
                    continue block18;
                }
                case 2: {
                    this.requestHeaderMessage.getBytes(this.tmpMB);
                    continue block18;
                }
                case 3: {
                    boolean tomcatAuthorization = this.protocol.getTomcatAuthorization();
                    if (tomcatAuthorization || !this.protocol.getTomcatAuthentication()) {
                        this.requestHeaderMessage.getBytes(this.request.getRemoteUser());
                        this.request.setRemoteUserNeedsAuthorization(tomcatAuthorization);
                        continue block18;
                    }
                    this.requestHeaderMessage.getBytes(this.tmpMB);
                    continue block18;
                }
                case 4: {
                    if (this.protocol.getTomcatAuthorization() || !this.protocol.getTomcatAuthentication()) {
                        this.requestHeaderMessage.getBytes(this.request.getAuthType());
                        continue block18;
                    }
                    this.requestHeaderMessage.getBytes(this.tmpMB);
                    continue block18;
                }
                case 5: {
                    this.requestHeaderMessage.getBytes(this.request.queryString());
                    continue block18;
                }
                case 6: {
                    this.requestHeaderMessage.getBytes(this.tmpMB);
                    continue block18;
                }
                case 7: {
                    this.requestHeaderMessage.getBytes(this.certificates);
                    continue block18;
                }
                case 8: {
                    this.requestHeaderMessage.getBytes(this.tmpMB);
                    this.request.setAttribute("javax.servlet.request.cipher_suite", this.tmpMB.toString());
                    continue block18;
                }
                case 9: {
                    this.requestHeaderMessage.getBytes(this.tmpMB);
                    this.request.setAttribute("javax.servlet.request.ssl_session_id", this.tmpMB.toString());
                    continue block18;
                }
                case 11: {
                    this.request.setAttribute("javax.servlet.request.key_size", this.requestHeaderMessage.getInt());
                    continue block18;
                }
                case 13: {
                    this.requestHeaderMessage.getBytes(this.request.method());
                    continue block18;
                }
                case 12: {
                    this.requestHeaderMessage.getBytes(this.tmpMB);
                    if (secret == null || secret.length() <= 0) continue block18;
                    secretPresentInRequest = true;
                    if (this.tmpMB.equals(secret)) continue block18;
                    this.response.setStatus(403);
                    this.setErrorState(ErrorState.CLOSE_CLEAN, null);
                    continue block18;
                }
            }
        }
        if (secret != null && secret.length() > 0 && !secretPresentInRequest) {
            this.response.setStatus(403);
            this.setErrorState(ErrorState.CLOSE_CLEAN, null);
        }
        if ((uriBC = this.request.requestURI().getByteChunk()).startsWithIgnoreCase("http", 0)) {
            int pos = uriBC.indexOf("://", 0, 3, 4);
            int uriBCStart = uriBC.getStart();
            int slashPos = -1;
            if (pos != -1) {
                byte[] uriB = uriBC.getBytes();
                slashPos = uriBC.indexOf('/', pos + 3);
                if (slashPos == -1) {
                    slashPos = uriBC.getLength();
                    this.request.requestURI().setBytes(uriB, uriBCStart + pos + 1, 1);
                } else {
                    this.request.requestURI().setBytes(uriB, uriBCStart + slashPos, uriBC.getLength() - slashPos);
                }
                MessageBytes hostMB = headers.setValue("host");
                hostMB.setBytes(uriB, uriBCStart + pos + 3, slashPos - pos - 3);
            }
        }
        MessageBytes valueMB = this.request.getMimeHeaders().getValue("host");
        this.parseHost(valueMB);
        if (!this.getErrorState().isIoAllowed()) {
            this.getAdapter().log(this.request, this.response, 0L);
        }
    }

    @Override
    protected void populateHost() {
        try {
            this.request.serverName().duplicate(this.request.localName());
        }
        catch (IOException e) {
            this.response.setStatus(400);
            this.setErrorState(ErrorState.CLOSE_CLEAN, e);
        }
    }

    @Override
    protected void populatePort() {
        this.request.setServerPort(this.request.getLocalPort());
    }

    @Override
    protected final void prepareResponse() throws IOException {
        long contentLength;
        String contentLanguage;
        this.response.setCommitted(true);
        int statusCode = this.response.getStatus();
        if (statusCode < 200 || statusCode == 204 || statusCode == 205 || statusCode == 304 || this.request.method().equals("HEAD")) {
            this.swallowResponse = true;
        }
        MimeHeaders headers = this.response.getMimeHeaders();
        String contentType = this.response.getContentType();
        if (contentType != null) {
            headers.setValue("Content-Type").setString(contentType);
        }
        if ((contentLanguage = this.response.getContentLanguage()) != null) {
            headers.setValue("Content-Language").setString(contentLanguage);
        }
        if ((contentLength = this.response.getContentLengthLong()) >= 0L) {
            headers.setValue("Content-Length").setLong(contentLength);
        }
        this.tmpMB.recycle();
        this.responseMsgPos = -1;
        int numHeaders = headers.size();
        boolean needAjpMessageHeader = true;
        block2: while (needAjpMessageHeader) {
            this.responseMessage.reset();
            this.responseMessage.appendByte(4);
            this.responseMessage.appendInt(statusCode);
            this.tmpMB.setString(Integer.toString(this.response.getStatus()));
            this.responseMessage.appendBytes(this.tmpMB);
            this.responseMessage.appendInt(numHeaders);
            needAjpMessageHeader = false;
            for (int i = 0; i < numHeaders; ++i) {
                try {
                    MessageBytes hN = headers.getName(i);
                    int hC = Constants.getResponseAjpIndex(hN.toString());
                    if (hC > 0) {
                        this.responseMessage.appendInt(hC);
                    } else {
                        this.responseMessage.appendBytes(hN);
                    }
                    MessageBytes hV = headers.getValue(i);
                    this.responseMessage.appendBytes(hV);
                    continue;
                }
                catch (IllegalArgumentException iae) {
                    log.warn((Object)sm.getString("ajpprocessor.response.invalidHeader", new Object[]{headers.getName(i), headers.getValue(i)}), (Throwable)iae);
                    headers.removeHeader(i);
                    --numHeaders;
                    needAjpMessageHeader = true;
                    continue block2;
                }
            }
        }
        this.responseMessage.end();
        this.socketWrapper.write(true, this.responseMessage.getBuffer(), 0, this.responseMessage.getLen());
        this.socketWrapper.flush(true);
    }

    @Override
    protected final void flush() throws IOException {
        if (!this.responseFinished) {
            if (this.protocol.getAjpFlush()) {
                this.socketWrapper.write(true, flushMessageArray, 0, flushMessageArray.length);
            }
            this.socketWrapper.flush(true);
        }
    }

    @Override
    protected final void finishResponse() throws IOException {
        if (this.responseFinished) {
            return;
        }
        this.responseFinished = true;
        if (this.waitingForBodyMessage || this.first && this.request.getContentLengthLong() > 0L) {
            this.refillReadBuffer(true);
        }
        if (this.getErrorState().isError()) {
            this.socketWrapper.write(true, endAndCloseMessageArray, 0, endAndCloseMessageArray.length);
        } else {
            this.socketWrapper.write(true, endMessageArray, 0, endMessageArray.length);
        }
        this.socketWrapper.flush(true);
    }

    @Override
    protected final void ack(ContinueResponseTiming continueResponseTiming) {
    }

    @Override
    protected final int available(boolean doRead) {
        if (this.endOfStream) {
            return 0;
        }
        if (this.empty && doRead) {
            try {
                this.refillReadBuffer(false);
            }
            catch (IOException timeout) {
                return 1;
            }
        }
        if (this.empty) {
            return 0;
        }
        return this.request.getInputBuffer().available();
    }

    @Override
    protected final void setRequestBody(ByteChunk body) {
        int length = body.getLength();
        this.bodyBytes.setBytes(body.getBytes(), body.getStart(), length);
        this.request.setContentLength(length);
        this.first = false;
        this.empty = false;
        this.replay = true;
        this.endOfStream = false;
    }

    @Override
    protected final void setSwallowResponse() {
        this.swallowResponse = true;
    }

    @Override
    protected final void disableSwallowRequest() {
    }

    @Override
    protected final boolean getPopulateRequestAttributesFromSocket() {
        return false;
    }

    @Override
    protected final void populateRequestAttributeRemoteHost() {
        if (this.request.remoteHost().isNull()) {
            try {
                this.request.remoteHost().setString(InetAddress.getByName(this.request.remoteAddr().toString()).getHostName());
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    @Override
    protected final void populateSslRequestAttributes() {
        if (!this.certificates.isNull()) {
            ArrayList<X509Certificate> jsseCerts = new ArrayList<X509Certificate>();
            ByteChunk certData = this.certificates.getByteChunk();
            ByteArrayInputStream bais = new ByteArrayInputStream(certData.getBytes(), certData.getStart(), certData.getLength());
            try {
                String clientCertProvider = this.protocol.getClientCertProvider();
                CertificateFactory cf = clientCertProvider == null ? CertificateFactory.getInstance("X.509") : CertificateFactory.getInstance("X.509", clientCertProvider);
                while (bais.available() > 0) {
                    X509Certificate cert = (X509Certificate)cf.generateCertificate(bais);
                    jsseCerts.add(cert);
                }
            }
            catch (NoSuchProviderException | CertificateException e) {
                this.getLog().error((Object)sm.getString("ajpprocessor.certs.fail"), (Throwable)e);
                return;
            }
            this.request.setAttribute("javax.servlet.request.X509Certificate", jsseCerts.toArray(new X509Certificate[0]));
        }
    }

    @Override
    protected final boolean isRequestBodyFullyRead() {
        return this.endOfStream;
    }

    @Override
    protected final void registerReadInterest() {
        this.socketWrapper.registerReadInterest();
    }

    @Override
    protected final boolean isReadyForWrite() {
        return this.responseMsgPos == -1 && this.socketWrapper.isReadyForWrite();
    }

    @Override
    protected boolean isTrailerFieldsReady() {
        return true;
    }

    private boolean read(byte[] buf, int pos, int n, boolean block) throws IOException {
        int read = this.socketWrapper.read(block, buf, pos, n);
        if (read > 0 && read < n) {
            int left = n - read;
            int start = pos + read;
            while (left > 0) {
                read = this.socketWrapper.read(true, buf, start, left);
                if (read == -1) {
                    throw new EOFException();
                }
                left -= read;
                start += read;
            }
        } else if (read == -1) {
            throw new EOFException();
        }
        return read > 0;
    }

    private void writeData(ByteBuffer chunk) throws IOException {
        boolean blocking = this.response.getWriteListener() == null;
        int len = chunk.remaining();
        int off = 0;
        while (len > 0) {
            int thisTime = Math.min(len, this.outputMaxChunkSize);
            this.responseMessage.reset();
            this.responseMessage.appendByte(3);
            chunk.limit(chunk.position() + thisTime);
            this.responseMessage.appendBytes(chunk);
            this.responseMessage.end();
            this.socketWrapper.write(blocking, this.responseMessage.getBuffer(), 0, this.responseMessage.getLen());
            this.socketWrapper.flush(blocking);
            len -= thisTime;
            off += thisTime;
        }
        this.bytesWritten += (long)off;
    }

    private boolean hasDataToWrite() {
        return this.responseMsgPos != -1 || this.socketWrapper.hasDataToWrite();
    }

    @Override
    protected Log getLog() {
        return log;
    }

    static {
        AjpMessage endMessage = new AjpMessage(16);
        endMessage.reset();
        endMessage.appendByte(5);
        endMessage.appendByte(1);
        endMessage.end();
        endMessageArray = new byte[endMessage.getLen()];
        System.arraycopy(endMessage.getBuffer(), 0, endMessageArray, 0, endMessage.getLen());
        AjpMessage endAndCloseMessage = new AjpMessage(16);
        endAndCloseMessage.reset();
        endAndCloseMessage.appendByte(5);
        endAndCloseMessage.appendByte(0);
        endAndCloseMessage.end();
        endAndCloseMessageArray = new byte[endAndCloseMessage.getLen()];
        System.arraycopy(endAndCloseMessage.getBuffer(), 0, endAndCloseMessageArray, 0, endAndCloseMessage.getLen());
        AjpMessage flushMessage = new AjpMessage(16);
        flushMessage.reset();
        flushMessage.appendByte(3);
        flushMessage.appendInt(0);
        flushMessage.appendByte(0);
        flushMessage.end();
        flushMessageArray = new byte[flushMessage.getLen()];
        System.arraycopy(flushMessage.getBuffer(), 0, flushMessageArray, 0, flushMessage.getLen());
        AjpMessage pongMessage = new AjpMessage(16);
        pongMessage.reset();
        pongMessage.appendByte(9);
        pongMessage.end();
        pongMessageArray = new byte[pongMessage.getLen()];
        System.arraycopy(pongMessage.getBuffer(), 0, pongMessageArray, 0, pongMessage.getLen());
        HashSet<String> s2 = new HashSet<String>();
        s2.add("javax.servlet.request.cipher_suite");
        s2.add("javax.servlet.request.key_size");
        s2.add("javax.servlet.request.ssl_session");
        s2.add("javax.servlet.request.X509Certificate");
        javaxAttributes = Collections.unmodifiableSet(s2);
        HashSet<String> s = new HashSet<String>();
        s.add("CERT_ISSUER");
        s.add("CERT_SUBJECT");
        s.add("CERT_COOKIE");
        s.add("HTTPS_SERVER_SUBJECT");
        s.add("CERT_FLAGS");
        s.add("HTTPS_SECRETKEYSIZE");
        s.add("CERT_SERIALNUMBER");
        s.add("HTTPS_SERVER_ISSUER");
        s.add("HTTPS_KEYSIZE");
        iisTlsAttributes = Collections.unmodifiableSet(s);
    }

    protected class SocketInputBuffer
    implements InputBuffer {
        protected SocketInputBuffer() {
        }

        @Override
        public int doRead(ApplicationBufferHandler handler) throws IOException {
            if (AjpProcessor.this.endOfStream) {
                return -1;
            }
            if (AjpProcessor.this.empty && !AjpProcessor.this.refillReadBuffer(true)) {
                return -1;
            }
            ByteChunk bc = AjpProcessor.this.bodyBytes.getByteChunk();
            handler.setByteBuffer(ByteBuffer.wrap(bc.getBuffer(), bc.getStart(), bc.getLength()));
            AjpProcessor.this.empty = true;
            return handler.getByteBuffer().remaining();
        }

        @Override
        public int available() {
            if (AjpProcessor.this.empty) {
                return 0;
            }
            return AjpProcessor.this.bodyBytes.getByteChunk().getLength();
        }
    }

    protected class SocketOutputBuffer
    implements OutputBuffer {
        protected SocketOutputBuffer() {
        }

        @Override
        public int doWrite(ByteBuffer chunk) throws IOException {
            if (!AjpProcessor.this.response.isCommitted()) {
                try {
                    AjpProcessor.this.prepareResponse();
                }
                catch (IOException e) {
                    AjpProcessor.this.setErrorState(ErrorState.CLOSE_CONNECTION_NOW, e);
                }
            }
            int len = 0;
            if (!AjpProcessor.this.swallowResponse) {
                try {
                    len = chunk.remaining();
                    AjpProcessor.this.writeData(chunk);
                    len -= chunk.remaining();
                }
                catch (IOException ioe) {
                    AjpProcessor.this.setErrorState(ErrorState.CLOSE_CONNECTION_NOW, ioe);
                    throw ioe;
                }
            }
            return len;
        }

        @Override
        public long getBytesWritten() {
            return AjpProcessor.this.bytesWritten;
        }
    }
}

