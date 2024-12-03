/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.buf.ByteChunk
 *  org.apache.tomcat.util.buf.MessageBytes
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.coyote;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import org.apache.coyote.AbstractProcessorLight;
import org.apache.coyote.ActionCode;
import org.apache.coyote.ActionHook;
import org.apache.coyote.Adapter;
import org.apache.coyote.AsyncContextCallback;
import org.apache.coyote.AsyncStateMachine;
import org.apache.coyote.CloseNowException;
import org.apache.coyote.ContinueResponseTiming;
import org.apache.coyote.ErrorState;
import org.apache.coyote.Request;
import org.apache.coyote.RequestInfo;
import org.apache.coyote.Response;
import org.apache.coyote.UpgradeToken;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.parser.Host;
import org.apache.tomcat.util.log.UserDataHelper;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.DispatchType;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;

public abstract class AbstractProcessor
extends AbstractProcessorLight
implements ActionHook {
    private static final StringManager sm = StringManager.getManager(AbstractProcessor.class);
    private char[] hostNameC = new char[0];
    protected final Adapter adapter;
    protected final AsyncStateMachine asyncStateMachine;
    private volatile long asyncTimeout = -1L;
    private volatile long asyncTimeoutGeneration = 0L;
    protected final Request request;
    protected final Response response;
    protected volatile SocketWrapperBase<?> socketWrapper = null;
    protected volatile SSLSupport sslSupport;
    private ErrorState errorState = ErrorState.NONE;
    protected final UserDataHelper userDataHelper;

    public AbstractProcessor(Adapter adapter) {
        this(adapter, new Request(), new Response());
    }

    protected AbstractProcessor(Adapter adapter, Request coyoteRequest, Response coyoteResponse) {
        this.adapter = adapter;
        this.asyncStateMachine = new AsyncStateMachine(this);
        this.request = coyoteRequest;
        this.response = coyoteResponse;
        this.response.setHook(this);
        this.request.setResponse(this.response);
        this.request.setHook(this);
        this.userDataHelper = new UserDataHelper(this.getLog());
    }

    protected void setErrorState(ErrorState errorState, Throwable t) {
        if (this.getLog().isDebugEnabled()) {
            this.getLog().debug((Object)sm.getString("abstractProcessor.setErrorState", new Object[]{errorState}), t);
        }
        this.response.setError();
        boolean blockIo = this.errorState.isIoAllowed() && !errorState.isIoAllowed();
        this.errorState = this.errorState.getMostSevere(errorState);
        if (this.response.getStatus() < 400 && !(t instanceof IOException)) {
            this.response.setStatus(500);
        }
        if (t != null) {
            this.request.setAttribute("javax.servlet.error.exception", t);
        }
        if (blockIo && this.isAsync() && this.asyncStateMachine.asyncError()) {
            this.processSocketEvent(SocketEvent.ERROR, true);
        }
    }

    protected ErrorState getErrorState() {
        return this.errorState;
    }

    @Override
    public Request getRequest() {
        return this.request;
    }

    public Adapter getAdapter() {
        return this.adapter;
    }

    protected void setSocketWrapper(SocketWrapperBase<?> socketWrapper) {
        this.socketWrapper = socketWrapper;
    }

    protected final SocketWrapperBase<?> getSocketWrapper() {
        return this.socketWrapper;
    }

    @Override
    public final void setSslSupport(SSLSupport sslSupport) {
        this.sslSupport = sslSupport;
    }

    protected void execute(Runnable runnable) {
        SocketWrapperBase<?> socketWrapper = this.socketWrapper;
        if (socketWrapper == null) {
            throw new RejectedExecutionException(sm.getString("abstractProcessor.noExecute"));
        }
        socketWrapper.execute(runnable);
    }

    @Override
    public boolean isAsync() {
        return this.asyncStateMachine.isAsync();
    }

    @Override
    public AbstractEndpoint.Handler.SocketState asyncPostProcess() {
        return this.asyncStateMachine.asyncPostProcess();
    }

    @Override
    public final AbstractEndpoint.Handler.SocketState dispatch(SocketEvent status) throws IOException {
        AbstractEndpoint.Handler.SocketState state;
        if (status == SocketEvent.OPEN_WRITE && this.response.getWriteListener() != null) {
            this.asyncStateMachine.asyncOperation();
            try {
                if (this.flushBufferedWrite()) {
                    return AbstractEndpoint.Handler.SocketState.LONG;
                }
            }
            catch (IOException ioe) {
                if (this.getLog().isDebugEnabled()) {
                    this.getLog().debug((Object)"Unable to write async data.", (Throwable)ioe);
                }
                status = SocketEvent.ERROR;
                this.request.setAttribute("javax.servlet.error.exception", ioe);
            }
        } else if (status == SocketEvent.OPEN_READ && this.request.getReadListener() != null) {
            this.dispatchNonBlockingRead();
        } else if (status == SocketEvent.ERROR) {
            if (this.request.getAttribute("javax.servlet.error.exception") == null) {
                this.request.setAttribute("javax.servlet.error.exception", this.socketWrapper.getError());
            }
            if (this.request.getReadListener() != null || this.response.getWriteListener() != null) {
                this.asyncStateMachine.asyncOperation();
            }
        }
        RequestInfo rp = this.request.getRequestProcessor();
        try {
            rp.setStage(3);
            if (!this.getAdapter().asyncDispatch(this.request, this.response, status)) {
                this.setErrorState(ErrorState.CLOSE_NOW, null);
            }
        }
        catch (InterruptedIOException e) {
            this.setErrorState(ErrorState.CLOSE_CONNECTION_NOW, e);
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            this.setErrorState(ErrorState.CLOSE_NOW, t);
            this.getLog().error((Object)sm.getString("http11processor.request.process"), t);
        }
        rp.setStage(7);
        if (this.getErrorState().isError()) {
            this.request.updateCounters();
            state = AbstractEndpoint.Handler.SocketState.CLOSED;
        } else if (this.isAsync()) {
            state = AbstractEndpoint.Handler.SocketState.LONG;
        } else {
            this.request.updateCounters();
            state = this.dispatchEndRequest();
        }
        if (this.getLog().isDebugEnabled()) {
            this.getLog().debug((Object)("Socket: [" + this.socketWrapper + "], Status in: [" + (Object)((Object)status) + "], State out: [" + (Object)((Object)state) + "]"));
        }
        return state;
    }

    protected void parseHost(MessageBytes valueMB) {
        if (valueMB == null || valueMB.isNull()) {
            this.populateHost();
            this.populatePort();
            return;
        }
        if (valueMB.getLength() == 0) {
            this.request.serverName().setString("");
            this.populatePort();
            return;
        }
        ByteChunk valueBC = valueMB.getByteChunk();
        byte[] valueB = valueBC.getBytes();
        int valueL = valueBC.getLength();
        int valueS = valueBC.getStart();
        if (this.hostNameC.length < valueL) {
            this.hostNameC = new char[valueL];
        }
        try {
            int colonPos = Host.parse(valueMB);
            if (colonPos != -1) {
                int port = 0;
                for (int i = colonPos + 1; i < valueL; ++i) {
                    char c = (char)valueB[i + valueS];
                    if (c < '0' || c > '9') {
                        this.response.setStatus(400);
                        this.setErrorState(ErrorState.CLOSE_CLEAN, null);
                        return;
                    }
                    port = port * 10 + c - 48;
                }
                this.request.setServerPort(port);
                valueL = colonPos;
            }
            for (int i = 0; i < valueL; ++i) {
                this.hostNameC[i] = (char)valueB[i + valueS];
            }
            this.request.serverName().setChars(this.hostNameC, 0, valueL);
        }
        catch (IllegalArgumentException e) {
            UserDataHelper.Mode logMode = this.userDataHelper.getNextMode();
            if (logMode != null) {
                String message = sm.getString("abstractProcessor.hostInvalid", new Object[]{valueMB.toString()});
                switch (logMode) {
                    case INFO_THEN_DEBUG: {
                        message = message + sm.getString("abstractProcessor.fallToDebug");
                    }
                    case INFO: {
                        this.getLog().info((Object)message, (Throwable)e);
                        break;
                    }
                    case DEBUG: {
                        this.getLog().debug((Object)message, (Throwable)e);
                    }
                }
            }
            this.response.setStatus(400);
            this.setErrorState(ErrorState.CLOSE_CLEAN, e);
        }
    }

    protected void populateHost() {
    }

    protected void populatePort() {
    }

    @Override
    public final void action(ActionCode actionCode, Object param) {
        switch (actionCode) {
            case COMMIT: {
                if (this.response.isCommitted()) break;
                try {
                    this.prepareResponse();
                }
                catch (IOException e) {
                    this.handleIOException(e);
                }
                break;
            }
            case CLOSE: {
                this.action(ActionCode.COMMIT, null);
                try {
                    this.finishResponse();
                }
                catch (IOException e) {
                    this.handleIOException(e);
                }
                break;
            }
            case ACK: {
                this.ack((ContinueResponseTiming)((Object)param));
                break;
            }
            case CLIENT_FLUSH: {
                this.action(ActionCode.COMMIT, null);
                try {
                    this.flush();
                }
                catch (IOException e) {
                    this.handleIOException(e);
                    this.response.setErrorException(e);
                }
                break;
            }
            case AVAILABLE: {
                this.request.setAvailable(this.available(Boolean.TRUE.equals(param)));
                break;
            }
            case REQ_SET_BODY_REPLAY: {
                ByteChunk body = (ByteChunk)param;
                this.setRequestBody(body);
                break;
            }
            case IS_ERROR: {
                ((AtomicBoolean)param).set(this.getErrorState().isError());
                break;
            }
            case IS_IO_ALLOWED: {
                ((AtomicBoolean)param).set(this.getErrorState().isIoAllowed());
                break;
            }
            case CLOSE_NOW: {
                this.setSwallowResponse();
                if (param instanceof Throwable) {
                    this.setErrorState(ErrorState.CLOSE_NOW, (Throwable)param);
                    break;
                }
                this.setErrorState(ErrorState.CLOSE_NOW, null);
                break;
            }
            case DISABLE_SWALLOW_INPUT: {
                this.disableSwallowRequest();
                this.setErrorState(ErrorState.CLOSE_CLEAN, null);
                break;
            }
            case REQ_HOST_ADDR_ATTRIBUTE: {
                if (!this.getPopulateRequestAttributesFromSocket() || this.socketWrapper == null) break;
                this.request.remoteAddr().setString(this.socketWrapper.getRemoteAddr());
                break;
            }
            case REQ_PEER_ADDR_ATTRIBUTE: {
                if (!this.getPopulateRequestAttributesFromSocket() || this.socketWrapper == null) break;
                this.request.peerAddr().setString(this.socketWrapper.getRemoteAddr());
                break;
            }
            case REQ_HOST_ATTRIBUTE: {
                this.populateRequestAttributeRemoteHost();
                break;
            }
            case REQ_LOCALPORT_ATTRIBUTE: {
                if (!this.getPopulateRequestAttributesFromSocket() || this.socketWrapper == null) break;
                this.request.setLocalPort(this.socketWrapper.getLocalPort());
                break;
            }
            case REQ_LOCAL_ADDR_ATTRIBUTE: {
                if (!this.getPopulateRequestAttributesFromSocket() || this.socketWrapper == null) break;
                this.request.localAddr().setString(this.socketWrapper.getLocalAddr());
                break;
            }
            case REQ_LOCAL_NAME_ATTRIBUTE: {
                if (!this.getPopulateRequestAttributesFromSocket() || this.socketWrapper == null) break;
                this.request.localName().setString(this.socketWrapper.getLocalName());
                break;
            }
            case REQ_REMOTEPORT_ATTRIBUTE: {
                if (!this.getPopulateRequestAttributesFromSocket() || this.socketWrapper == null) break;
                this.request.setRemotePort(this.socketWrapper.getRemotePort());
                break;
            }
            case REQ_SSL_ATTRIBUTE: {
                this.populateSslRequestAttributes();
                break;
            }
            case REQ_SSL_CERTIFICATE: {
                try {
                    this.sslReHandShake();
                }
                catch (IOException ioe) {
                    this.setErrorState(ErrorState.CLOSE_CONNECTION_NOW, ioe);
                }
                break;
            }
            case ASYNC_START: {
                this.asyncStateMachine.asyncStart((AsyncContextCallback)param);
                break;
            }
            case ASYNC_COMPLETE: {
                this.clearDispatches();
                if (!this.asyncStateMachine.asyncComplete()) break;
                this.processSocketEvent(SocketEvent.OPEN_READ, true);
                break;
            }
            case ASYNC_DISPATCH: {
                if (!this.asyncStateMachine.asyncDispatch()) break;
                this.processSocketEvent(SocketEvent.OPEN_READ, true);
                break;
            }
            case ASYNC_DISPATCHED: {
                this.asyncStateMachine.asyncDispatched();
                break;
            }
            case ASYNC_ERROR: {
                this.asyncStateMachine.asyncError();
                break;
            }
            case ASYNC_IS_ASYNC: {
                ((AtomicBoolean)param).set(this.asyncStateMachine.isAsync());
                break;
            }
            case ASYNC_IS_COMPLETING: {
                ((AtomicBoolean)param).set(this.asyncStateMachine.isCompleting());
                break;
            }
            case ASYNC_IS_DISPATCHING: {
                ((AtomicBoolean)param).set(this.asyncStateMachine.isAsyncDispatching());
                break;
            }
            case ASYNC_IS_ERROR: {
                ((AtomicBoolean)param).set(this.asyncStateMachine.isAsyncError());
                break;
            }
            case ASYNC_IS_STARTED: {
                ((AtomicBoolean)param).set(this.asyncStateMachine.isAsyncStarted());
                break;
            }
            case ASYNC_IS_TIMINGOUT: {
                ((AtomicBoolean)param).set(this.asyncStateMachine.isAsyncTimingOut());
                break;
            }
            case ASYNC_RUN: {
                this.asyncStateMachine.asyncRun((Runnable)param);
                break;
            }
            case ASYNC_SETTIMEOUT: {
                if (param == null) {
                    return;
                }
                long timeout = (Long)param;
                this.setAsyncTimeout(timeout);
                break;
            }
            case ASYNC_TIMEOUT: {
                AtomicBoolean result = (AtomicBoolean)param;
                result.set(this.asyncStateMachine.asyncTimeout());
                break;
            }
            case ASYNC_POST_PROCESS: {
                this.asyncStateMachine.asyncPostProcess();
                break;
            }
            case REQUEST_BODY_FULLY_READ: {
                AtomicBoolean result = (AtomicBoolean)param;
                result.set(this.isRequestBodyFullyRead());
                break;
            }
            case NB_READ_INTEREST: {
                AtomicBoolean isReady = (AtomicBoolean)param;
                isReady.set(this.isReadyForRead());
                break;
            }
            case NB_WRITE_INTEREST: {
                AtomicBoolean isReady = (AtomicBoolean)param;
                isReady.set(this.isReadyForWrite());
                break;
            }
            case DISPATCH_READ: {
                this.addDispatch(DispatchType.NON_BLOCKING_READ);
                break;
            }
            case DISPATCH_WRITE: {
                this.addDispatch(DispatchType.NON_BLOCKING_WRITE);
                break;
            }
            case DISPATCH_EXECUTE: {
                this.executeDispatches();
                break;
            }
            case UPGRADE: {
                this.doHttpUpgrade((UpgradeToken)param);
                break;
            }
            case IS_PUSH_SUPPORTED: {
                AtomicBoolean result = (AtomicBoolean)param;
                result.set(this.isPushSupported());
                break;
            }
            case PUSH_REQUEST: {
                this.doPush((Request)param);
                break;
            }
            case IS_TRAILER_FIELDS_READY: {
                AtomicBoolean result = (AtomicBoolean)param;
                result.set(this.isTrailerFieldsReady());
                break;
            }
            case IS_TRAILER_FIELDS_SUPPORTED: {
                AtomicBoolean result = (AtomicBoolean)param;
                result.set(this.isTrailerFieldsSupported());
                break;
            }
            case CONNECTION_ID: {
                AtomicReference result = (AtomicReference)param;
                result.set(this.getConnectionID());
                break;
            }
            case STREAM_ID: {
                AtomicReference result = (AtomicReference)param;
                result.set(this.getStreamID());
                break;
            }
        }
    }

    private void handleIOException(IOException ioe) {
        if (ioe instanceof CloseNowException) {
            this.setErrorState(ErrorState.CLOSE_NOW, ioe);
        } else {
            this.setErrorState(ErrorState.CLOSE_CONNECTION_NOW, ioe);
        }
    }

    protected void dispatchNonBlockingRead() {
        this.asyncStateMachine.asyncOperation();
    }

    @Override
    public void timeoutAsync(long now) {
        if (now < 0L) {
            this.doTimeoutAsync();
        } else {
            long asyncTimeout = this.getAsyncTimeout();
            if (asyncTimeout > 0L) {
                long asyncStart = this.asyncStateMachine.getLastAsyncStart();
                if (now - asyncStart > asyncTimeout) {
                    this.doTimeoutAsync();
                }
            } else if (!this.asyncStateMachine.isAvailable()) {
                this.doTimeoutAsync();
            }
        }
    }

    private void doTimeoutAsync() {
        this.setAsyncTimeout(-1L);
        this.asyncTimeoutGeneration = this.asyncStateMachine.getCurrentGeneration();
        this.processSocketEvent(SocketEvent.TIMEOUT, true);
    }

    @Override
    public boolean checkAsyncTimeoutGeneration() {
        return this.asyncTimeoutGeneration == this.asyncStateMachine.getCurrentGeneration();
    }

    public void setAsyncTimeout(long timeout) {
        this.asyncTimeout = timeout;
    }

    public long getAsyncTimeout() {
        return this.asyncTimeout;
    }

    @Override
    public void recycle() {
        this.errorState = ErrorState.NONE;
        this.asyncStateMachine.recycle();
    }

    protected abstract void prepareResponse() throws IOException;

    protected abstract void finishResponse() throws IOException;

    @Deprecated
    protected void ack() {
        this.ack(ContinueResponseTiming.ALWAYS);
    }

    protected abstract void ack(ContinueResponseTiming var1);

    protected abstract void flush() throws IOException;

    protected abstract int available(boolean var1);

    protected abstract void setRequestBody(ByteChunk var1);

    protected abstract void setSwallowResponse();

    protected abstract void disableSwallowRequest();

    protected boolean getPopulateRequestAttributesFromSocket() {
        return true;
    }

    protected void populateRequestAttributeRemoteHost() {
        if (this.getPopulateRequestAttributesFromSocket() && this.socketWrapper != null) {
            this.request.remoteHost().setString(this.socketWrapper.getRemoteHost());
        }
    }

    protected void populateSslRequestAttributes() {
        try {
            if (this.sslSupport != null) {
                Object sslO = this.sslSupport.getCipherSuite();
                if (sslO != null) {
                    this.request.setAttribute("javax.servlet.request.cipher_suite", sslO);
                }
                if ((sslO = this.sslSupport.getPeerCertificateChain()) != null) {
                    this.request.setAttribute("javax.servlet.request.X509Certificate", sslO);
                }
                if ((sslO = this.sslSupport.getKeySize()) != null) {
                    this.request.setAttribute("javax.servlet.request.key_size", sslO);
                }
                if ((sslO = this.sslSupport.getSessionId()) != null) {
                    this.request.setAttribute("javax.servlet.request.ssl_session_id", sslO);
                }
                if ((sslO = this.sslSupport.getProtocol()) != null) {
                    this.request.setAttribute("org.apache.tomcat.util.net.secure_protocol_version", sslO);
                }
                if ((sslO = this.sslSupport.getRequestedProtocols()) != null) {
                    this.request.setAttribute("org.apache.tomcat.util.net.secure_requested_protocol_versions", sslO);
                }
                if ((sslO = this.sslSupport.getRequestedCiphers()) != null) {
                    this.request.setAttribute("org.apache.tomcat.util.net.secure_requested_ciphers", sslO);
                }
                this.request.setAttribute("javax.servlet.request.ssl_session_mgr", this.sslSupport);
            }
        }
        catch (Exception e) {
            this.getLog().warn((Object)sm.getString("abstractProcessor.socket.ssl"), (Throwable)e);
        }
    }

    protected void sslReHandShake() throws IOException {
    }

    protected void processSocketEvent(SocketEvent event, boolean dispatch) {
        SocketWrapperBase<?> socketWrapper = this.getSocketWrapper();
        if (socketWrapper != null) {
            socketWrapper.processSocket(event, dispatch);
        }
    }

    protected boolean isReadyForRead() {
        if (this.available(true) > 0) {
            return true;
        }
        if (!this.isRequestBodyFullyRead()) {
            this.registerReadInterest();
        }
        return false;
    }

    protected abstract boolean isRequestBodyFullyRead();

    protected abstract void registerReadInterest();

    protected abstract boolean isReadyForWrite();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void executeDispatches() {
        SocketWrapperBase<?> socketWrapper = this.getSocketWrapper();
        Iterator<DispatchType> dispatches = this.getIteratorAndClearDispatches();
        if (socketWrapper != null) {
            Lock lock = socketWrapper.getLock();
            lock.lock();
            try {
                while (dispatches != null && dispatches.hasNext()) {
                    DispatchType dispatchType = dispatches.next();
                    socketWrapper.processSocket(dispatchType.getSocketStatus(), false);
                }
            }
            finally {
                lock.unlock();
            }
        }
    }

    @Override
    public UpgradeToken getUpgradeToken() {
        throw new IllegalStateException(sm.getString("abstractProcessor.httpupgrade.notsupported"));
    }

    protected void doHttpUpgrade(UpgradeToken upgradeToken) {
        throw new UnsupportedOperationException(sm.getString("abstractProcessor.httpupgrade.notsupported"));
    }

    @Override
    public ByteBuffer getLeftoverInput() {
        throw new IllegalStateException(sm.getString("abstractProcessor.httpupgrade.notsupported"));
    }

    @Override
    public boolean isUpgrade() {
        return false;
    }

    protected boolean isPushSupported() {
        return false;
    }

    protected void doPush(Request pushTarget) {
        throw new UnsupportedOperationException(sm.getString("abstractProcessor.pushrequest.notsupported"));
    }

    protected abstract boolean isTrailerFieldsReady();

    protected boolean isTrailerFieldsSupported() {
        return false;
    }

    protected Object getConnectionID() {
        return null;
    }

    protected Object getStreamID() {
        return null;
    }

    protected abstract boolean flushBufferedWrite() throws IOException;

    protected abstract AbstractEndpoint.Handler.SocketState dispatchEndRequest() throws IOException;

    @Override
    protected final void logAccess(SocketWrapperBase<?> socketWrapper) throws IOException {
        this.setSocketWrapper(socketWrapper);
        this.request.setStartTime(System.currentTimeMillis());
        this.response.setStatus(400);
        this.response.setError();
        this.getAdapter().log(this.request, this.response, 0L);
    }
}

