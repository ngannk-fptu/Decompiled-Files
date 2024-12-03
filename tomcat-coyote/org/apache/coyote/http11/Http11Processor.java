/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.buf.ByteChunk
 *  org.apache.tomcat.util.buf.ByteChunk$BufferOverflowException
 *  org.apache.tomcat.util.buf.MessageBytes
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Pattern;
import org.apache.coyote.AbstractProcessor;
import org.apache.coyote.ActionCode;
import org.apache.coyote.Adapter;
import org.apache.coyote.ContinueResponseTiming;
import org.apache.coyote.ErrorState;
import org.apache.coyote.Request;
import org.apache.coyote.RequestInfo;
import org.apache.coyote.UpgradeProtocol;
import org.apache.coyote.UpgradeToken;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.coyote.http11.HeadersTooLargeException;
import org.apache.coyote.http11.Http11InputBuffer;
import org.apache.coyote.http11.Http11OutputBuffer;
import org.apache.coyote.http11.InputFilter;
import org.apache.coyote.http11.OutputFilter;
import org.apache.coyote.http11.filters.BufferedInputFilter;
import org.apache.coyote.http11.filters.ChunkedInputFilter;
import org.apache.coyote.http11.filters.ChunkedOutputFilter;
import org.apache.coyote.http11.filters.GzipOutputFilter;
import org.apache.coyote.http11.filters.IdentityInputFilter;
import org.apache.coyote.http11.filters.IdentityOutputFilter;
import org.apache.coyote.http11.filters.SavedRequestInputFilter;
import org.apache.coyote.http11.filters.VoidInputFilter;
import org.apache.coyote.http11.filters.VoidOutputFilter;
import org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler;
import org.apache.coyote.http11.upgrade.UpgradeApplicationBufferHandler;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.FastHttpDateFormat;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.apache.tomcat.util.http.parser.TokenList;
import org.apache.tomcat.util.log.UserDataHelper;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SendfileDataBase;
import org.apache.tomcat.util.net.SendfileKeepAliveState;
import org.apache.tomcat.util.net.SendfileState;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;

public class Http11Processor
extends AbstractProcessor {
    private static final Log log = LogFactory.getLog(Http11Processor.class);
    private static final StringManager sm = StringManager.getManager(Http11Processor.class);
    private final AbstractHttp11Protocol<?> protocol;
    private final Http11InputBuffer inputBuffer;
    private final Http11OutputBuffer outputBuffer;
    private final HttpParser httpParser;
    private int pluggableFilterIndex = Integer.MAX_VALUE;
    private volatile boolean keepAlive = true;
    private volatile boolean openSocket = false;
    private volatile boolean readComplete = true;
    private boolean http11 = true;
    private boolean http09 = false;
    private boolean contentDelimitation = true;
    private UpgradeToken upgradeToken = null;
    private SendfileDataBase sendfileData = null;

    public Http11Processor(AbstractHttp11Protocol<?> protocol, Adapter adapter) {
        super(adapter);
        this.protocol = protocol;
        this.httpParser = new HttpParser(protocol.getRelaxedPathChars(), protocol.getRelaxedQueryChars());
        this.inputBuffer = new Http11InputBuffer(this.request, protocol.getMaxHttpRequestHeaderSize(), protocol.getRejectIllegalHeader(), this.httpParser);
        this.request.setInputBuffer(this.inputBuffer);
        this.outputBuffer = new Http11OutputBuffer(this.response, protocol.getMaxHttpResponseHeaderSize());
        this.response.setOutputBuffer(this.outputBuffer);
        this.inputBuffer.addFilter(new IdentityInputFilter(protocol.getMaxSwallowSize()));
        this.outputBuffer.addFilter(new IdentityOutputFilter());
        this.inputBuffer.addFilter(new ChunkedInputFilter(protocol.getMaxTrailerSize(), protocol.getAllowedTrailerHeadersInternal(), protocol.getMaxExtensionSize(), protocol.getMaxSwallowSize()));
        this.outputBuffer.addFilter(new ChunkedOutputFilter());
        this.inputBuffer.addFilter(new VoidInputFilter());
        this.outputBuffer.addFilter(new VoidOutputFilter());
        this.inputBuffer.addFilter(new BufferedInputFilter(protocol.getMaxSwallowSize()));
        this.outputBuffer.addFilter(new GzipOutputFilter());
        this.pluggableFilterIndex = this.inputBuffer.getFilters().length;
    }

    private static boolean statusDropsConnection(int status) {
        return status == 400 || status == 408 || status == 411 || status == 413 || status == 414 || status == 500 || status == 503 || status == 501;
    }

    private void addInputFilter(InputFilter[] inputFilters, String encodingName) {
        if (this.contentDelimitation) {
            this.response.setStatus(400);
            this.setErrorState(ErrorState.CLOSE_CLEAN, null);
            if (log.isDebugEnabled()) {
                log.debug((Object)(sm.getString("http11processor.request.prepare") + " Transfer encoding lists chunked before [" + encodingName + "]"));
            }
            return;
        }
        if (encodingName.equals("chunked")) {
            this.inputBuffer.addActiveFilter(inputFilters[1]);
            this.contentDelimitation = true;
        } else {
            for (int i = this.pluggableFilterIndex; i < inputFilters.length; ++i) {
                if (!inputFilters[i].getEncodingName().toString().equals(encodingName)) continue;
                this.inputBuffer.addActiveFilter(inputFilters[i]);
                return;
            }
            this.response.setStatus(501);
            this.setErrorState(ErrorState.CLOSE_CLEAN, null);
            if (log.isDebugEnabled()) {
                log.debug((Object)(sm.getString("http11processor.request.prepare") + " Unsupported transfer encoding [" + encodingName + "]"));
            }
        }
    }

    @Override
    public AbstractEndpoint.Handler.SocketState service(SocketWrapperBase<?> socketWrapper) throws IOException {
        RequestInfo rp = this.request.getRequestProcessor();
        rp.setStage(1);
        this.setSocketWrapper(socketWrapper);
        this.keepAlive = true;
        this.openSocket = false;
        this.readComplete = true;
        boolean keptAlive = false;
        SendfileState sendfileState = SendfileState.DONE;
        while (!this.getErrorState().isError() && this.keepAlive && !this.isAsync() && this.upgradeToken == null && sendfileState == SendfileState.DONE && !this.protocol.isPaused()) {
            int maxKeepAliveRequests;
            String requestedProtocol;
            UpgradeProtocol upgradeProtocol;
            try {
                if (!this.inputBuffer.parseRequestLine(keptAlive, this.protocol.getConnectionTimeout(), this.protocol.getKeepAliveTimeout())) {
                    if (this.inputBuffer.getParsingRequestLinePhase() == -1) {
                        return AbstractEndpoint.Handler.SocketState.UPGRADING;
                    }
                    if (this.handleIncompleteRequestLineRead()) break;
                }
                this.prepareRequestProtocol();
                if (this.protocol.isPaused()) {
                    this.response.setStatus(503);
                    this.setErrorState(ErrorState.CLOSE_CLEAN, null);
                } else {
                    keptAlive = true;
                    this.request.getMimeHeaders().setLimit(this.protocol.getMaxHeaderCount());
                    if (!this.http09 && !this.inputBuffer.parseHeaders()) {
                        this.openSocket = true;
                        this.readComplete = false;
                        break;
                    }
                    if (!this.protocol.getDisableUploadTimeout()) {
                        socketWrapper.setReadTimeout(this.protocol.getConnectionUploadTimeout());
                    }
                }
            }
            catch (IOException e) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("http11processor.header.parse"), (Throwable)e);
                }
                this.setErrorState(ErrorState.CLOSE_CONNECTION_NOW, e);
                break;
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                UserDataHelper.Mode logMode = this.userDataHelper.getNextMode();
                if (logMode != null) {
                    String message = sm.getString("http11processor.header.parse");
                    switch (logMode) {
                        case INFO_THEN_DEBUG: {
                            message = message + sm.getString("http11processor.fallToDebug");
                        }
                        case INFO: {
                            log.info((Object)message, t);
                            break;
                        }
                        case DEBUG: {
                            log.debug((Object)message, t);
                        }
                    }
                }
                this.response.setStatus(400);
                this.setErrorState(ErrorState.CLOSE_CLEAN, t);
            }
            if (Http11Processor.isConnectionToken(this.request.getMimeHeaders(), "upgrade") && (upgradeProtocol = this.protocol.getUpgradeProtocol(requestedProtocol = this.request.getHeader("Upgrade"))) != null && upgradeProtocol.accept(this.request)) {
                Request upgradeRequest = null;
                try {
                    upgradeRequest = this.cloneRequest(this.request);
                }
                catch (ByteChunk.BufferOverflowException ioe) {
                    this.response.setStatus(413);
                    this.setErrorState(ErrorState.CLOSE_CLEAN, null);
                }
                catch (IOException ioe) {
                    this.response.setStatus(500);
                    this.setErrorState(ErrorState.CLOSE_CLEAN, ioe);
                }
                if (upgradeRequest != null) {
                    this.response.setStatus(101);
                    this.response.setHeader("Connection", "Upgrade");
                    this.response.setHeader("Upgrade", requestedProtocol);
                    this.action(ActionCode.CLOSE, null);
                    this.getAdapter().log(this.request, this.response, 0L);
                    InternalHttpUpgradeHandler upgradeHandler = upgradeProtocol.getInternalUpgradeHandler(socketWrapper, this.getAdapter(), upgradeRequest);
                    UpgradeToken upgradeToken = new UpgradeToken(upgradeHandler, null, null, requestedProtocol);
                    this.action(ActionCode.UPGRADE, upgradeToken);
                    return AbstractEndpoint.Handler.SocketState.UPGRADING;
                }
            }
            if (this.getErrorState().isIoAllowed()) {
                rp.setStage(2);
                try {
                    this.prepareRequest();
                }
                catch (Throwable t) {
                    ExceptionUtils.handleThrowable((Throwable)t);
                    if (log.isDebugEnabled()) {
                        log.debug((Object)sm.getString("http11processor.request.prepare"), t);
                    }
                    this.response.setStatus(500);
                    this.setErrorState(ErrorState.CLOSE_CLEAN, t);
                }
            }
            if ((maxKeepAliveRequests = this.protocol.getMaxKeepAliveRequests()) == 1) {
                this.keepAlive = false;
            } else if (maxKeepAliveRequests > 0 && socketWrapper.decrementKeepAlive() <= 0) {
                this.keepAlive = false;
            }
            if (this.getErrorState().isIoAllowed()) {
                try {
                    rp.setStage(3);
                    this.getAdapter().service(this.request, this.response);
                    if (this.keepAlive && !this.getErrorState().isError() && !this.isAsync() && Http11Processor.statusDropsConnection(this.response.getStatus())) {
                        this.setErrorState(ErrorState.CLOSE_CLEAN, null);
                    }
                }
                catch (InterruptedIOException e) {
                    this.setErrorState(ErrorState.CLOSE_CONNECTION_NOW, e);
                }
                catch (HeadersTooLargeException e) {
                    log.error((Object)sm.getString("http11processor.request.process"), (Throwable)e);
                    if (this.response.isCommitted()) {
                        this.setErrorState(ErrorState.CLOSE_NOW, e);
                    } else {
                        this.response.reset();
                        this.response.setStatus(500);
                        this.setErrorState(ErrorState.CLOSE_CLEAN, e);
                        this.response.setHeader("Connection", "close");
                    }
                }
                catch (Throwable t) {
                    ExceptionUtils.handleThrowable((Throwable)t);
                    log.error((Object)sm.getString("http11processor.request.process"), t);
                    this.response.setStatus(500);
                    this.setErrorState(ErrorState.CLOSE_CLEAN, t);
                    this.getAdapter().log(this.request, this.response, 0L);
                }
            }
            rp.setStage(4);
            if (!this.isAsync()) {
                this.endRequest();
            }
            rp.setStage(5);
            if (this.getErrorState().isError()) {
                this.response.setStatus(500);
            }
            if (!this.isAsync() || this.getErrorState().isError()) {
                this.request.updateCounters();
                if (this.getErrorState().isIoAllowed()) {
                    this.inputBuffer.nextRequest();
                    this.outputBuffer.nextRequest();
                }
            }
            if (!this.protocol.getDisableUploadTimeout()) {
                int connectionTimeout = this.protocol.getConnectionTimeout();
                if (connectionTimeout > 0) {
                    socketWrapper.setReadTimeout(connectionTimeout);
                } else {
                    socketWrapper.setReadTimeout(0L);
                }
            }
            rp.setStage(6);
            sendfileState = this.processSendfile(socketWrapper);
        }
        rp.setStage(7);
        if (this.getErrorState().isError() || this.protocol.isPaused() && !this.isAsync()) {
            return AbstractEndpoint.Handler.SocketState.CLOSED;
        }
        if (this.isAsync()) {
            return AbstractEndpoint.Handler.SocketState.LONG;
        }
        if (this.isUpgrade()) {
            return AbstractEndpoint.Handler.SocketState.UPGRADING;
        }
        if (sendfileState == SendfileState.PENDING) {
            return AbstractEndpoint.Handler.SocketState.SENDFILE;
        }
        if (this.openSocket) {
            if (this.readComplete) {
                return AbstractEndpoint.Handler.SocketState.OPEN;
            }
            return AbstractEndpoint.Handler.SocketState.LONG;
        }
        return AbstractEndpoint.Handler.SocketState.CLOSED;
    }

    @Override
    protected final void setSocketWrapper(SocketWrapperBase<?> socketWrapper) {
        super.setSocketWrapper(socketWrapper);
        this.inputBuffer.init(socketWrapper);
        this.outputBuffer.init(socketWrapper);
    }

    private Request cloneRequest(Request source) throws IOException {
        Request dest = new Request();
        dest.decodedURI().duplicate(source.decodedURI());
        dest.method().duplicate(source.method());
        dest.getMimeHeaders().duplicate(source.getMimeHeaders());
        dest.requestURI().duplicate(source.requestURI());
        dest.queryString().duplicate(source.queryString());
        MimeHeaders headers = source.getMimeHeaders();
        this.prepareExpectation(headers);
        this.prepareInputFilters(headers);
        this.ack();
        ByteChunk body = new ByteChunk();
        int maxSavePostSize = this.protocol.getMaxSavePostSize();
        if (maxSavePostSize != 0) {
            body.setLimit(maxSavePostSize);
            UpgradeApplicationBufferHandler buffer = new UpgradeApplicationBufferHandler();
            while (source.getInputBuffer().doRead(buffer) >= 0) {
                body.append(buffer.getByteBuffer());
            }
        }
        SavedRequestInputFilter srif = new SavedRequestInputFilter(body);
        dest.setInputBuffer(srif);
        return dest;
    }

    private boolean handleIncompleteRequestLineRead() {
        this.openSocket = true;
        if (this.inputBuffer.getParsingRequestLinePhase() > 1) {
            if (this.protocol.isPaused()) {
                this.response.setStatus(503);
                this.setErrorState(ErrorState.CLOSE_CLEAN, null);
                return false;
            }
            this.readComplete = false;
        }
        return true;
    }

    private void checkExpectationAndResponseStatus() {
        if (this.request.hasExpectation() && !this.isRequestBodyFullyRead() && (this.response.getStatus() < 200 || this.response.getStatus() > 299)) {
            this.inputBuffer.setSwallowInput(false);
            this.keepAlive = false;
        }
    }

    private void checkMaxSwallowSize() {
        long contentLength = -1L;
        try {
            contentLength = this.request.getContentLengthLong();
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (contentLength > 0L && this.protocol.getMaxSwallowSize() > -1 && contentLength - this.request.getBytesRead() > (long)this.protocol.getMaxSwallowSize()) {
            this.keepAlive = false;
        }
    }

    private void prepareRequestProtocol() {
        MessageBytes protocolMB = this.request.protocol();
        if (protocolMB.equals("HTTP/1.1")) {
            this.http09 = false;
            this.http11 = true;
            protocolMB.setString("HTTP/1.1");
        } else if (protocolMB.equals("HTTP/1.0")) {
            this.http09 = false;
            this.http11 = false;
            this.keepAlive = false;
            protocolMB.setString("HTTP/1.0");
        } else if (protocolMB.equals("")) {
            this.http09 = true;
            this.http11 = false;
            this.keepAlive = false;
        } else {
            this.http09 = false;
            this.http11 = false;
            this.response.setStatus(505);
            this.setErrorState(ErrorState.CLOSE_CLEAN, null);
            if (log.isDebugEnabled()) {
                log.debug((Object)(sm.getString("http11processor.request.prepare") + " Unsupported HTTP version \"" + protocolMB + "\""));
            }
        }
    }

    private void prepareRequest() throws IOException {
        String userAgentValue;
        MessageBytes userAgentValueMB;
        Pattern restrictedUserAgents;
        MimeHeaders headers;
        MessageBytes connectionValueMB;
        if (this.protocol.isSSLEnabled()) {
            this.request.scheme().setString("https");
        }
        if ((connectionValueMB = (headers = this.request.getMimeHeaders()).getValue("Connection")) != null && !connectionValueMB.isNull()) {
            HashSet<String> tokens = new HashSet<String>();
            TokenList.parseTokenList(headers.values("Connection"), tokens);
            if (tokens.contains("close")) {
                this.keepAlive = false;
            } else if (tokens.contains("keep-alive")) {
                this.keepAlive = true;
            }
        }
        if (this.http11) {
            this.prepareExpectation(headers);
        }
        if ((restrictedUserAgents = this.protocol.getRestrictedUserAgentsPattern()) != null && (this.http11 || this.keepAlive) && (userAgentValueMB = headers.getValue("user-agent")) != null && !userAgentValueMB.isNull() && restrictedUserAgents.matcher(userAgentValue = userAgentValueMB.toString()).matches()) {
            this.http11 = false;
            this.keepAlive = false;
        }
        MessageBytes hostValueMB = null;
        try {
            hostValueMB = headers.getUniqueValue("host");
        }
        catch (IllegalArgumentException iae) {
            this.badRequest("http11processor.request.multipleHosts");
        }
        if (this.http11 && hostValueMB == null) {
            this.badRequest("http11processor.request.noHostHeader");
        }
        ByteChunk uriBC = this.request.requestURI().getByteChunk();
        byte[] uriB = uriBC.getBytes();
        if (uriBC.startsWithIgnoreCase("http", 0)) {
            int pos = 4;
            if (uriBC.startsWithIgnoreCase("s", pos)) {
                ++pos;
            }
            if (uriBC.startsWith("://", pos)) {
                int uriBCStart = uriBC.getStart();
                int slashPos = uriBC.indexOf('/', pos += 3);
                int atPos = uriBC.indexOf('@', pos);
                if (slashPos > -1 && atPos > slashPos) {
                    atPos = -1;
                }
                if (slashPos == -1) {
                    slashPos = uriBC.getLength();
                    this.request.requestURI().setBytes(uriB, uriBCStart + 6, 1);
                } else {
                    this.request.requestURI().setBytes(uriB, uriBCStart + slashPos, uriBC.getLength() - slashPos);
                }
                if (atPos != -1) {
                    while (pos < atPos) {
                        byte c = uriB[uriBCStart + pos];
                        if (!HttpParser.isUserInfo(c)) {
                            this.badRequest("http11processor.request.invalidUserInfo");
                            break;
                        }
                        ++pos;
                    }
                    pos = atPos + 1;
                }
                if (this.http11) {
                    if (hostValueMB != null && !hostValueMB.getByteChunk().equals(uriB, uriBCStart + pos, slashPos - pos)) {
                        if (this.protocol.getAllowHostHeaderMismatch()) {
                            hostValueMB = headers.setValue("host");
                            hostValueMB.setBytes(uriB, uriBCStart + pos, slashPos - pos);
                        } else {
                            this.badRequest("http11processor.request.inconsistentHosts");
                        }
                    }
                } else {
                    try {
                        hostValueMB = headers.setValue("host");
                        hostValueMB.setBytes(uriB, uriBCStart + pos, slashPos - pos);
                    }
                    catch (IllegalStateException illegalStateException) {}
                }
            } else {
                this.badRequest("http11processor.request.invalidScheme");
            }
        }
        for (int i = uriBC.getStart(); i < uriBC.getEnd(); ++i) {
            if (this.httpParser.isAbsolutePathRelaxed(uriB[i])) continue;
            this.badRequest("http11processor.request.invalidUri");
            break;
        }
        this.prepareInputFilters(headers);
        this.parseHost(hostValueMB);
        if (!this.getErrorState().isIoAllowed()) {
            this.getAdapter().log(this.request, this.response, 0L);
        }
    }

    private void prepareExpectation(MimeHeaders headers) {
        MessageBytes expectMB = headers.getValue("expect");
        if (expectMB != null && !expectMB.isNull()) {
            if (expectMB.toString().trim().equalsIgnoreCase("100-continue")) {
                this.request.setExpectation(true);
            } else {
                this.response.setStatus(417);
                this.setErrorState(ErrorState.CLOSE_CLEAN, null);
            }
        }
    }

    private void prepareInputFilters(MimeHeaders headers) throws IOException {
        MessageBytes transferEncodingValueMB;
        this.contentDelimitation = false;
        InputFilter[] inputFilters = this.inputBuffer.getFilters();
        if (!this.http09 && (transferEncodingValueMB = headers.getValue("transfer-encoding")) != null) {
            ArrayList<String> encodingNames = new ArrayList<String>();
            if (TokenList.parseTokenList(headers.values("transfer-encoding"), encodingNames)) {
                for (String encodingName : encodingNames) {
                    this.addInputFilter(inputFilters, encodingName);
                }
            } else {
                this.badRequest("http11processor.request.invalidTransferEncoding");
            }
        }
        long contentLength = -1L;
        try {
            contentLength = this.request.getContentLengthLong();
        }
        catch (NumberFormatException e) {
            this.badRequest("http11processor.request.nonNumericContentLength");
        }
        catch (IllegalArgumentException e) {
            this.badRequest("http11processor.request.multipleContentLength");
        }
        if (contentLength >= 0L) {
            if (this.contentDelimitation) {
                headers.removeHeader("content-length");
                this.request.setContentLength(-1L);
                this.keepAlive = false;
            } else {
                this.inputBuffer.addActiveFilter(inputFilters[0]);
                this.contentDelimitation = true;
            }
        }
        if (!this.contentDelimitation) {
            this.inputBuffer.addActiveFilter(inputFilters[2]);
            this.contentDelimitation = true;
        }
    }

    private void badRequest(String errorKey) {
        this.response.setStatus(400);
        this.setErrorState(ErrorState.CLOSE_CLEAN, null);
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString(errorKey));
        }
    }

    @Override
    protected final void prepareResponse() throws IOException {
        String server;
        boolean entityBody = true;
        this.contentDelimitation = false;
        OutputFilter[] outputFilters = this.outputBuffer.getFilters();
        if (this.http09) {
            this.outputBuffer.addActiveFilter(outputFilters[0]);
            this.outputBuffer.commit();
            return;
        }
        int statusCode = this.response.getStatus();
        if (statusCode < 200 || statusCode == 204 || statusCode == 205 || statusCode == 304) {
            this.outputBuffer.addActiveFilter(outputFilters[2]);
            entityBody = false;
            this.contentDelimitation = true;
            if (statusCode == 205) {
                this.response.setContentLength(0L);
            } else {
                this.response.setContentLength(-1L);
            }
        }
        if (this.request.method().equals("HEAD")) {
            this.outputBuffer.addActiveFilter(outputFilters[2]);
            this.contentDelimitation = true;
        }
        if (this.protocol.getUseSendfile()) {
            this.prepareSendfile(outputFilters);
        }
        boolean useCompression = false;
        if (entityBody && this.sendfileData == null) {
            useCompression = this.protocol.useCompression(this.request, this.response);
        }
        MimeHeaders headers = this.response.getMimeHeaders();
        if (entityBody || statusCode == 204) {
            String contentLanguage;
            String contentType = this.response.getContentType();
            if (contentType != null) {
                headers.setValue("Content-Type").setString(contentType);
            }
            if ((contentLanguage = this.response.getContentLanguage()) != null) {
                headers.setValue("Content-Language").setString(contentLanguage);
            }
        }
        long contentLength = this.response.getContentLengthLong();
        boolean connectionClosePresent = Http11Processor.isConnectionToken(headers, "close");
        if (this.http11 && this.response.getTrailerFields() != null) {
            this.outputBuffer.addActiveFilter(outputFilters[1]);
            this.contentDelimitation = true;
            headers.addValue("Transfer-Encoding").setString("chunked");
        } else if (contentLength != -1L) {
            headers.setValue("Content-Length").setLong(contentLength);
            this.outputBuffer.addActiveFilter(outputFilters[0]);
            this.contentDelimitation = true;
        } else if (this.http11 && entityBody && !connectionClosePresent) {
            this.outputBuffer.addActiveFilter(outputFilters[1]);
            this.contentDelimitation = true;
            headers.addValue("Transfer-Encoding").setString("chunked");
        } else {
            this.outputBuffer.addActiveFilter(outputFilters[0]);
        }
        if (useCompression) {
            this.outputBuffer.addActiveFilter(outputFilters[3]);
        }
        if (headers.getValue("Date") == null) {
            headers.addValue("Date").setString(FastHttpDateFormat.getCurrentDate());
        }
        if (entityBody && !this.contentDelimitation || connectionClosePresent) {
            this.keepAlive = false;
        }
        this.checkExpectationAndResponseStatus();
        this.checkMaxSwallowSize();
        if (this.keepAlive && Http11Processor.statusDropsConnection(statusCode)) {
            this.keepAlive = false;
        }
        if (!this.keepAlive) {
            if (!connectionClosePresent) {
                headers.addValue("Connection").setString("close");
            }
        } else if (!this.getErrorState().isError()) {
            int keepAliveTimeout;
            boolean connectionKeepAlivePresent;
            if (!this.http11) {
                headers.addValue("Connection").setString("keep-alive");
            }
            if (this.protocol.getUseKeepAliveResponseHeader() && (connectionKeepAlivePresent = Http11Processor.isConnectionToken(this.request.getMimeHeaders(), "keep-alive")) && (keepAliveTimeout = this.protocol.getKeepAliveTimeout()) > 0) {
                String value = "timeout=" + (long)keepAliveTimeout / 1000L;
                headers.setValue("Keep-Alive").setString(value);
                if (this.http11) {
                    MessageBytes connectionHeaderValue = headers.getValue("Connection");
                    if (connectionHeaderValue == null) {
                        headers.addValue("Connection").setString("keep-alive");
                    } else {
                        connectionHeaderValue.setString(connectionHeaderValue.getString() + ", " + "keep-alive");
                    }
                }
            }
        }
        if ((server = this.protocol.getServer()) == null) {
            if (this.protocol.getServerRemoveAppProvidedValues()) {
                headers.removeHeader("server");
            }
        } else {
            headers.setValue("Server").setString(server);
        }
        try {
            this.outputBuffer.sendStatus();
            int size = headers.size();
            for (int i = 0; i < size; ++i) {
                try {
                    this.outputBuffer.sendHeader(headers.getName(i), headers.getValue(i));
                    continue;
                }
                catch (IllegalArgumentException iae) {
                    log.warn((Object)sm.getString("http11processor.response.invalidHeader", new Object[]{headers.getName(i), headers.getValue(i)}), (Throwable)iae);
                    headers.removeHeader(i);
                    --size;
                    this.outputBuffer.resetHeaderBuffer();
                    i = 0;
                    this.outputBuffer.sendStatus();
                }
            }
            this.outputBuffer.endHeaders();
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            this.outputBuffer.resetHeaderBuffer();
            throw t;
        }
        this.outputBuffer.commit();
    }

    private static boolean isConnectionToken(MimeHeaders headers, String token) throws IOException {
        MessageBytes connection = headers.getValue("Connection");
        if (connection == null) {
            return false;
        }
        HashSet<String> tokens = new HashSet<String>();
        TokenList.parseTokenList(headers.values("Connection"), tokens);
        return tokens.contains(token);
    }

    private void prepareSendfile(OutputFilter[] outputFilters) {
        String fileName = (String)this.request.getAttribute("org.apache.tomcat.sendfile.filename");
        if (fileName == null) {
            this.sendfileData = null;
        } else {
            this.outputBuffer.addActiveFilter(outputFilters[2]);
            this.contentDelimitation = true;
            long pos = (Long)this.request.getAttribute("org.apache.tomcat.sendfile.start");
            long end = (Long)this.request.getAttribute("org.apache.tomcat.sendfile.end");
            this.sendfileData = this.socketWrapper.createSendfileData(fileName, pos, end - pos);
        }
    }

    @Override
    protected void populatePort() {
        this.request.action(ActionCode.REQ_LOCALPORT_ATTRIBUTE, this.request);
        this.request.setServerPort(this.request.getLocalPort());
    }

    @Override
    protected boolean flushBufferedWrite() throws IOException {
        if (this.outputBuffer.hasDataToWrite() && this.outputBuffer.flushBuffer(false)) {
            this.outputBuffer.registerWriteInterest();
            return true;
        }
        return false;
    }

    @Override
    protected AbstractEndpoint.Handler.SocketState dispatchEndRequest() {
        if (!this.keepAlive || this.protocol.isPaused()) {
            return AbstractEndpoint.Handler.SocketState.CLOSED;
        }
        this.endRequest();
        this.inputBuffer.nextRequest();
        this.outputBuffer.nextRequest();
        if (this.socketWrapper.isReadPending()) {
            return AbstractEndpoint.Handler.SocketState.LONG;
        }
        return AbstractEndpoint.Handler.SocketState.OPEN;
    }

    @Override
    protected Log getLog() {
        return log;
    }

    private void endRequest() {
        if (this.getErrorState().isError()) {
            this.inputBuffer.setSwallowInput(false);
        } else {
            this.checkExpectationAndResponseStatus();
        }
        if (this.getErrorState().isIoAllowed()) {
            try {
                this.inputBuffer.endRequest();
            }
            catch (IOException e) {
                this.setErrorState(ErrorState.CLOSE_CONNECTION_NOW, e);
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                this.response.setStatus(500);
                this.setErrorState(ErrorState.CLOSE_NOW, t);
                log.error((Object)sm.getString("http11processor.request.finish"), t);
            }
        }
        if (this.getErrorState().isIoAllowed()) {
            try {
                this.action(ActionCode.COMMIT, null);
                this.outputBuffer.end();
            }
            catch (IOException e) {
                this.setErrorState(ErrorState.CLOSE_CONNECTION_NOW, e);
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                this.setErrorState(ErrorState.CLOSE_NOW, t);
                log.error((Object)sm.getString("http11processor.response.finish"), t);
            }
        }
    }

    @Override
    protected final void finishResponse() throws IOException {
        this.outputBuffer.end();
    }

    @Override
    protected final void ack() {
        this.ack(ContinueResponseTiming.ALWAYS);
    }

    @Override
    protected final void ack(ContinueResponseTiming continueResponseTiming) {
        if ((continueResponseTiming == ContinueResponseTiming.ALWAYS || continueResponseTiming == this.protocol.getContinueResponseTimingInternal()) && !this.response.isCommitted() && this.request.hasExpectation()) {
            try {
                this.outputBuffer.sendAck();
            }
            catch (IOException e) {
                this.setErrorState(ErrorState.CLOSE_CONNECTION_NOW, e);
            }
        }
    }

    @Override
    protected final void flush() throws IOException {
        this.outputBuffer.flush();
    }

    @Override
    protected final int available(boolean doRead) {
        return this.inputBuffer.available(doRead);
    }

    @Override
    protected final void setRequestBody(ByteChunk body) {
        SavedRequestInputFilter savedBody = new SavedRequestInputFilter(body);
        Http11InputBuffer internalBuffer = (Http11InputBuffer)this.request.getInputBuffer();
        internalBuffer.addActiveFilter(savedBody);
    }

    @Override
    protected final void setSwallowResponse() {
        this.outputBuffer.responseFinished = true;
    }

    @Override
    protected final void disableSwallowRequest() {
        this.inputBuffer.setSwallowInput(false);
    }

    @Override
    protected final void sslReHandShake() throws IOException {
        if (this.sslSupport != null) {
            InputFilter[] inputFilters = this.inputBuffer.getFilters();
            ((BufferedInputFilter)inputFilters[3]).setLimit(this.protocol.getMaxSavePostSize());
            this.inputBuffer.addActiveFilter(inputFilters[3]);
            this.socketWrapper.doClientAuth(this.sslSupport);
            try {
                X509Certificate[] sslO = this.sslSupport.getPeerCertificateChain();
                if (sslO != null) {
                    this.request.setAttribute("javax.servlet.request.X509Certificate", sslO);
                }
            }
            catch (IOException ioe) {
                log.warn((Object)sm.getString("http11processor.socket.ssl"), (Throwable)ioe);
            }
        }
    }

    @Override
    protected final boolean isRequestBodyFullyRead() {
        return this.inputBuffer.isFinished();
    }

    @Override
    protected final void registerReadInterest() {
        this.socketWrapper.registerReadInterest();
    }

    @Override
    protected final boolean isReadyForWrite() {
        return this.outputBuffer.isReady();
    }

    @Override
    public UpgradeToken getUpgradeToken() {
        return this.upgradeToken;
    }

    @Override
    protected final void doHttpUpgrade(UpgradeToken upgradeToken) {
        this.upgradeToken = upgradeToken;
        this.outputBuffer.responseFinished = true;
    }

    @Override
    public ByteBuffer getLeftoverInput() {
        return this.inputBuffer.getLeftover();
    }

    @Override
    public boolean isUpgrade() {
        return this.upgradeToken != null;
    }

    @Override
    protected boolean isTrailerFieldsReady() {
        if (this.inputBuffer.isChunking()) {
            return this.inputBuffer.isFinished();
        }
        return true;
    }

    @Override
    protected boolean isTrailerFieldsSupported() {
        if (!this.http11) {
            return false;
        }
        if (!this.response.isCommitted()) {
            return true;
        }
        return this.outputBuffer.isChunking();
    }

    private SendfileState processSendfile(SocketWrapperBase<?> socketWrapper) {
        this.openSocket = this.keepAlive;
        SendfileState result = SendfileState.DONE;
        if (this.sendfileData != null && !this.getErrorState().isError()) {
            this.sendfileData.keepAliveState = this.keepAlive ? (this.available(false) == 0 ? SendfileKeepAliveState.OPEN : SendfileKeepAliveState.PIPELINED) : SendfileKeepAliveState.NONE;
            result = socketWrapper.processSendfile(this.sendfileData);
            switch (result) {
                case ERROR: {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)sm.getString("http11processor.sendfile.error"));
                    }
                    this.setErrorState(ErrorState.CLOSE_CONNECTION_NOW, null);
                }
            }
            this.sendfileData = null;
        }
        return result;
    }

    @Override
    public final void recycle() {
        this.getAdapter().checkRecycled(this.request, this.response);
        super.recycle();
        this.inputBuffer.recycle();
        this.outputBuffer.recycle();
        this.upgradeToken = null;
        this.socketWrapper = null;
        this.sendfileData = null;
        this.sslSupport = null;
    }

    @Override
    public void pause() {
    }
}

