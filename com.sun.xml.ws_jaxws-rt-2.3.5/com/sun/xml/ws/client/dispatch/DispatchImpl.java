/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.activation.DataHandler
 *  javax.xml.bind.JAXBException
 *  javax.xml.ws.AsyncHandler
 *  javax.xml.ws.Dispatch
 *  javax.xml.ws.Response
 *  javax.xml.ws.Service$Mode
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.soap.SOAPFaultException
 */
package com.sun.xml.ws.client.dispatch;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.api.client.WSPortInfo;
import com.sun.xml.ws.api.message.AddressingUtils;
import com.sun.xml.ws.api.message.Attachment;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Fiber;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.api.server.ContainerResolver;
import com.sun.xml.ws.binding.BindingImpl;
import com.sun.xml.ws.client.AsyncInvoker;
import com.sun.xml.ws.client.AsyncResponseImpl;
import com.sun.xml.ws.client.RequestContext;
import com.sun.xml.ws.client.ResponseContext;
import com.sun.xml.ws.client.ResponseContextReceiver;
import com.sun.xml.ws.client.Stub;
import com.sun.xml.ws.client.WSServiceDelegate;
import com.sun.xml.ws.client.dispatch.RESTSourceDispatch;
import com.sun.xml.ws.client.dispatch.SOAPSourceDispatch;
import com.sun.xml.ws.encoding.soap.DeserializationException;
import com.sun.xml.ws.fault.SOAPFaultBuilder;
import com.sun.xml.ws.message.AttachmentSetImpl;
import com.sun.xml.ws.message.DataHandlerAttachment;
import com.sun.xml.ws.resources.DispatchMessages;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Response;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

public abstract class DispatchImpl<T>
extends Stub
implements Dispatch<T> {
    private static final Logger LOGGER = Logger.getLogger(DispatchImpl.class.getName());
    final Service.Mode mode;
    final SOAPVersion soapVersion;
    final boolean allowFaultResponseMsg;
    static final long AWAIT_TERMINATION_TIME = 800L;
    static final String HTTP_REQUEST_METHOD_GET = "GET";
    static final String HTTP_REQUEST_METHOD_POST = "POST";
    static final String HTTP_REQUEST_METHOD_PUT = "PUT";

    @Deprecated
    protected DispatchImpl(QName port, Service.Mode mode, WSServiceDelegate owner, Tube pipe, BindingImpl binding, @Nullable WSEndpointReference epr) {
        super(port, owner, pipe, binding, owner.getWsdlService() != null ? owner.getWsdlService().get(port) : null, owner.getEndpointAddress(port), epr);
        this.mode = mode;
        this.soapVersion = binding.getSOAPVersion();
        this.allowFaultResponseMsg = false;
    }

    protected DispatchImpl(WSPortInfo portInfo, Service.Mode mode, BindingImpl binding, @Nullable WSEndpointReference epr) {
        this(portInfo, mode, binding, epr, false);
    }

    protected DispatchImpl(WSPortInfo portInfo, Service.Mode mode, BindingImpl binding, @Nullable WSEndpointReference epr, boolean allowFaultResponseMsg) {
        this(portInfo, mode, binding, null, epr, allowFaultResponseMsg);
    }

    protected DispatchImpl(WSPortInfo portInfo, Service.Mode mode, BindingImpl binding, Tube pipe, @Nullable WSEndpointReference epr, boolean allowFaultResponseMsg) {
        super(portInfo, binding, pipe, portInfo.getEndpointAddress(), epr);
        this.mode = mode;
        this.soapVersion = binding.getSOAPVersion();
        this.allowFaultResponseMsg = allowFaultResponseMsg;
    }

    protected DispatchImpl(WSPortInfo portInfo, Service.Mode mode, Tube pipe, BindingImpl binding, @Nullable WSEndpointReference epr, boolean allowFaultResponseMsg) {
        super(portInfo, binding, pipe, portInfo.getEndpointAddress(), epr);
        this.mode = mode;
        this.soapVersion = binding.getSOAPVersion();
        this.allowFaultResponseMsg = allowFaultResponseMsg;
    }

    abstract Packet createPacket(T var1);

    abstract T toReturnValue(Packet var1);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final Response<T> invokeAsync(T param) {
        Container old = ContainerResolver.getDefault().enterContainer(this.owner.getContainer());
        try {
            if (LOGGER.isLoggable(Level.FINE)) {
                this.dumpParam(param, "invokeAsync(T)");
            }
            DispatchAsyncInvoker invoker = new DispatchAsyncInvoker(param);
            AsyncResponseImpl ft = new AsyncResponseImpl((Runnable)invoker, null);
            invoker.setReceiver(ft);
            ft.run();
            AsyncResponseImpl asyncResponseImpl = ft;
            return asyncResponseImpl;
        }
        finally {
            ContainerResolver.getDefault().exitContainer(old);
        }
    }

    private void dumpParam(T param, String method) {
        if (param instanceof Packet) {
            Packet message = (Packet)param;
            if (LOGGER.isLoggable(Level.FINE)) {
                AddressingVersion av = this.getBinding().getAddressingVersion();
                SOAPVersion sv = this.getBinding().getSOAPVersion();
                String action = av != null && message.getMessage() != null ? AddressingUtils.getAction(message.getMessage().getHeaders(), av, sv) : null;
                String msgId = av != null && message.getMessage() != null ? AddressingUtils.getMessageID(message.getMessage().getHeaders(), av, sv) : null;
                LOGGER.fine("In DispatchImpl." + method + " for message with action: " + action + " and msg ID: " + msgId + " msg: " + message.getMessage());
                if (message.getMessage() == null) {
                    LOGGER.fine("Dispatching null message for action: " + action + " and msg ID: " + msgId);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final Future<?> invokeAsync(T param, AsyncHandler<T> asyncHandler) {
        Container old = ContainerResolver.getDefault().enterContainer(this.owner.getContainer());
        try {
            if (LOGGER.isLoggable(Level.FINE)) {
                this.dumpParam(param, "invokeAsync(T, AsyncHandler<T>)");
            }
            DispatchAsyncInvoker invoker = new DispatchAsyncInvoker(param);
            AsyncResponseImpl<T> ft = new AsyncResponseImpl<T>((Runnable)invoker, asyncHandler);
            invoker.setReceiver(ft);
            invoker.setNonNullAsyncHandlerGiven(asyncHandler != null);
            ft.run();
            AsyncResponseImpl<T> asyncResponseImpl = ft;
            return asyncResponseImpl;
        }
        finally {
            ContainerResolver.getDefault().exitContainer(old);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final T doInvoke(T in, RequestContext rc, ResponseContextReceiver receiver) {
        Packet response = null;
        try {
            try {
                DispatchImpl.checkNullAllowed(in, rc, this.binding, this.mode);
                Packet message = this.createPacket(in);
                message.setState(Packet.State.ClientRequest);
                this.resolveEndpointAddress(message, rc);
                this.setProperties(message, true);
                response = this.process(message, rc, receiver);
                Message msg = response.getMessage();
                if (msg != null && msg.isFault() && !this.allowFaultResponseMsg) {
                    SOAPFaultBuilder faultBuilder = SOAPFaultBuilder.create(msg);
                    throw (SOAPFaultException)faultBuilder.createException(null);
                }
            }
            catch (JAXBException e) {
                throw new DeserializationException(DispatchMessages.INVALID_RESPONSE_DESERIALIZATION(), new Object[]{e});
            }
            catch (WebServiceException e) {
                throw e;
            }
            catch (Throwable e) {
                throw new WebServiceException(e);
            }
            T t = this.toReturnValue(response);
            return t;
        }
        finally {
            if (response != null && response.transportBackChannel != null) {
                response.transportBackChannel.close();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final T invoke(T in) {
        Container old = ContainerResolver.getDefault().enterContainer(this.owner.getContainer());
        try {
            if (LOGGER.isLoggable(Level.FINE)) {
                this.dumpParam(in, "invoke(T)");
            }
            T t = this.doInvoke(in, this.requestContext, this);
            return t;
        }
        finally {
            ContainerResolver.getDefault().exitContainer(old);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void invokeOneWay(T in) {
        Container old = ContainerResolver.getDefault().enterContainer(this.owner.getContainer());
        try {
            if (LOGGER.isLoggable(Level.FINE)) {
                this.dumpParam(in, "invokeOneWay(T)");
            }
            try {
                DispatchImpl.checkNullAllowed(in, this.requestContext, this.binding, this.mode);
                Packet request = this.createPacket(in);
                request.setState(Packet.State.ClientRequest);
                this.setProperties(request, false);
                this.process(request, this.requestContext, this);
            }
            catch (WebServiceException e) {
                throw e;
            }
            catch (Throwable e) {
                throw new WebServiceException(e);
            }
        }
        finally {
            ContainerResolver.getDefault().exitContainer(old);
        }
    }

    void setProperties(Packet packet, boolean expectReply) {
        packet.expectReply = expectReply;
    }

    static boolean isXMLHttp(@NotNull WSBinding binding) {
        return binding.getBindingId().equals(BindingID.XML_HTTP);
    }

    static boolean isPAYLOADMode(@NotNull Service.Mode mode) {
        return mode == Service.Mode.PAYLOAD;
    }

    static void checkNullAllowed(@Nullable Object in, RequestContext rc, WSBinding binding, Service.Mode mode) {
        if (in != null) {
            return;
        }
        if (DispatchImpl.isXMLHttp(binding)) {
            if (DispatchImpl.methodNotOk(rc)) {
                throw new WebServiceException(DispatchMessages.INVALID_NULLARG_XMLHTTP_REQUEST_METHOD(HTTP_REQUEST_METHOD_POST, HTTP_REQUEST_METHOD_GET));
            }
        } else if (mode == Service.Mode.MESSAGE) {
            throw new WebServiceException(DispatchMessages.INVALID_NULLARG_SOAP_MSGMODE(mode.name(), Service.Mode.PAYLOAD.toString()));
        }
    }

    static boolean methodNotOk(@NotNull RequestContext rc) {
        String requestMethod = (String)rc.get("javax.xml.ws.http.request.method");
        String request = requestMethod == null ? HTTP_REQUEST_METHOD_POST : requestMethod;
        return HTTP_REQUEST_METHOD_POST.equalsIgnoreCase(request) || HTTP_REQUEST_METHOD_PUT.equalsIgnoreCase(request);
    }

    public static void checkValidSOAPMessageDispatch(WSBinding binding, Service.Mode mode) {
        if (DispatchImpl.isXMLHttp(binding)) {
            throw new WebServiceException(DispatchMessages.INVALID_SOAPMESSAGE_DISPATCH_BINDING("http://www.w3.org/2004/08/wsdl/http", "http://schemas.xmlsoap.org/wsdl/soap/http or http://www.w3.org/2003/05/soap/bindings/HTTP/"));
        }
        if (DispatchImpl.isPAYLOADMode(mode)) {
            throw new WebServiceException(DispatchMessages.INVALID_SOAPMESSAGE_DISPATCH_MSGMODE(mode.name(), Service.Mode.MESSAGE.toString()));
        }
    }

    public static void checkValidDataSourceDispatch(WSBinding binding, Service.Mode mode) {
        if (!DispatchImpl.isXMLHttp(binding)) {
            throw new WebServiceException(DispatchMessages.INVALID_DATASOURCE_DISPATCH_BINDING("SOAP/HTTP", "http://www.w3.org/2004/08/wsdl/http"));
        }
        if (DispatchImpl.isPAYLOADMode(mode)) {
            throw new WebServiceException(DispatchMessages.INVALID_DATASOURCE_DISPATCH_MSGMODE(mode.name(), Service.Mode.MESSAGE.toString()));
        }
    }

    @Override
    @NotNull
    public final QName getPortName() {
        return this.portname;
    }

    void resolveEndpointAddress(@NotNull Packet message, @NotNull RequestContext requestContext) {
        boolean p = message.packetTakesPriorityOverRequestContext;
        String endpoint = p && message.endpointAddress != null ? message.endpointAddress.toString() : (String)requestContext.get("javax.xml.ws.service.endpoint.address");
        if (endpoint == null) {
            if (message.endpointAddress == null) {
                throw new WebServiceException(DispatchMessages.INVALID_NULLARG_URI());
            }
            endpoint = message.endpointAddress.toString();
        }
        String pathInfo = null;
        String queryString = null;
        if (p && message.invocationProperties.get("javax.xml.ws.http.request.pathinfo") != null) {
            pathInfo = (String)message.invocationProperties.get("javax.xml.ws.http.request.pathinfo");
        } else if (requestContext.get("javax.xml.ws.http.request.pathinfo") != null) {
            pathInfo = (String)requestContext.get("javax.xml.ws.http.request.pathinfo");
        }
        if (p && message.invocationProperties.get("javax.xml.ws.http.request.querystring") != null) {
            queryString = (String)message.invocationProperties.get("javax.xml.ws.http.request.querystring");
        } else if (requestContext.get("javax.xml.ws.http.request.querystring") != null) {
            queryString = (String)requestContext.get("javax.xml.ws.http.request.querystring");
        }
        if (pathInfo != null || queryString != null) {
            pathInfo = DispatchImpl.checkPath(pathInfo);
            queryString = DispatchImpl.checkQuery(queryString);
            if (endpoint != null) {
                try {
                    URI endpointURI = new URI(endpoint);
                    endpoint = this.resolveURI(endpointURI, pathInfo, queryString);
                }
                catch (URISyntaxException e) {
                    throw new WebServiceException(DispatchMessages.INVALID_URI(endpoint));
                }
            }
        }
        requestContext.put("javax.xml.ws.service.endpoint.address", endpoint);
    }

    @NotNull
    protected String resolveURI(@NotNull URI endpointURI, @Nullable String pathInfo, @Nullable String queryString) {
        String query = null;
        String fragment = null;
        if (queryString != null) {
            URI result;
            try {
                URI tp = new URI(null, null, endpointURI.getPath(), queryString, null);
                result = endpointURI.resolve(tp);
            }
            catch (URISyntaxException e) {
                throw new WebServiceException(DispatchMessages.INVALID_QUERY_STRING(queryString));
            }
            query = result.getQuery();
            fragment = result.getFragment();
        }
        String path = pathInfo != null ? pathInfo : endpointURI.getPath();
        try {
            StringBuilder spec = new StringBuilder();
            if (path != null) {
                spec.append(path);
            }
            if (query != null) {
                spec.append("?");
                spec.append(query);
            }
            if (fragment != null) {
                spec.append("#");
                spec.append(fragment);
            }
            return new URL(endpointURI.toURL(), spec.toString()).toExternalForm();
        }
        catch (MalformedURLException e) {
            throw new WebServiceException(DispatchMessages.INVALID_URI_RESOLUTION(path));
        }
    }

    private static String checkPath(@Nullable String path) {
        return path == null || path.startsWith("/") ? path : "/" + path;
    }

    private static String checkQuery(@Nullable String query) {
        if (query == null) {
            return null;
        }
        if (query.indexOf(63) == 0) {
            throw new WebServiceException(DispatchMessages.INVALID_QUERY_LEADING_CHAR(query));
        }
        return query;
    }

    protected AttachmentSet setOutboundAttachments() {
        HashMap attachments = (HashMap)this.getRequestContext().get("javax.xml.ws.binding.attachments.outbound");
        if (attachments != null) {
            ArrayList<Attachment> alist = new ArrayList<Attachment>();
            for (Map.Entry att : attachments.entrySet()) {
                DataHandlerAttachment dha = new DataHandlerAttachment((String)att.getKey(), (DataHandler)att.getValue());
                alist.add(dha);
            }
            return new AttachmentSetImpl(alist);
        }
        return new AttachmentSetImpl();
    }

    @Override
    public void setOutboundHeaders(Object ... headers) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public static Dispatch<Source> createSourceDispatch(QName port, Service.Mode mode, WSServiceDelegate owner, Tube pipe, BindingImpl binding, WSEndpointReference epr) {
        if (DispatchImpl.isXMLHttp(binding)) {
            return new RESTSourceDispatch(port, mode, owner, pipe, binding, epr);
        }
        return new SOAPSourceDispatch(port, mode, owner, pipe, binding, epr);
    }

    public static Dispatch<Source> createSourceDispatch(WSPortInfo portInfo, Service.Mode mode, BindingImpl binding, WSEndpointReference epr) {
        if (DispatchImpl.isXMLHttp(binding)) {
            return new RESTSourceDispatch(portInfo, mode, binding, epr);
        }
        return new SOAPSourceDispatch(portInfo, mode, binding, epr);
    }

    private class DispatchAsyncInvoker
    extends AsyncInvoker {
        private final T param;
        private final RequestContext rc;

        DispatchAsyncInvoker(T param) {
            this.rc = DispatchImpl.this.requestContext.copy();
            this.param = param;
        }

        @Override
        public void do_run() {
            DispatchImpl.checkNullAllowed(this.param, this.rc, DispatchImpl.this.binding, DispatchImpl.this.mode);
            Packet message = DispatchImpl.this.createPacket(this.param);
            message.setState(Packet.State.ClientRequest);
            message.nonNullAsyncHandlerGiven = this.nonNullAsyncHandlerGiven;
            DispatchImpl.this.resolveEndpointAddress(message, this.rc);
            DispatchImpl.this.setProperties(message, true);
            String action = null;
            String msgId = null;
            if (LOGGER.isLoggable(Level.FINE)) {
                AddressingVersion av = DispatchImpl.this.getBinding().getAddressingVersion();
                SOAPVersion sv = DispatchImpl.this.getBinding().getSOAPVersion();
                action = av != null && message.getMessage() != null ? AddressingUtils.getAction(message.getMessage().getHeaders(), av, sv) : null;
                msgId = av != null && message.getMessage() != null ? AddressingUtils.getMessageID(message.getMessage().getHeaders(), av, sv) : null;
                LOGGER.fine("In DispatchAsyncInvoker.do_run for async message with action: " + action + " and msg ID: " + msgId);
            }
            final String actionUse = action;
            final String msgIdUse = msgId;
            Fiber.CompletionCallback callback = new Fiber.CompletionCallback(){

                @Override
                public void onCompletion(@NotNull Packet response) {
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.fine("Done with processAsync in DispatchAsyncInvoker.do_run, and setting response for async message with action: " + actionUse + " and msg ID: " + msgIdUse);
                    }
                    Message msg = response.getMessage();
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.fine("Done with processAsync in DispatchAsyncInvoker.do_run, and setting response for async message with action: " + actionUse + " and msg ID: " + msgIdUse + " msg: " + msg);
                    }
                    try {
                        if (msg != null && msg.isFault() && !DispatchImpl.this.allowFaultResponseMsg) {
                            SOAPFaultBuilder faultBuilder = SOAPFaultBuilder.create(msg);
                            throw (SOAPFaultException)faultBuilder.createException(null);
                        }
                        DispatchAsyncInvoker.this.responseImpl.setResponseContext(new ResponseContext(response));
                        DispatchAsyncInvoker.this.responseImpl.set(DispatchImpl.this.toReturnValue(response), null);
                    }
                    catch (JAXBException e) {
                        DispatchAsyncInvoker.this.responseImpl.set(null, (Throwable)((Object)new DeserializationException(DispatchMessages.INVALID_RESPONSE_DESERIALIZATION(), new Object[]{e})));
                    }
                    catch (WebServiceException e) {
                        DispatchAsyncInvoker.this.responseImpl.set(null, e);
                    }
                    catch (Throwable e) {
                        DispatchAsyncInvoker.this.responseImpl.set(null, new WebServiceException(e));
                    }
                }

                @Override
                public void onCompletion(@NotNull Throwable error) {
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.fine("Done with processAsync in DispatchAsyncInvoker.do_run, and setting response for async message with action: " + actionUse + " and msg ID: " + msgIdUse + " Throwable: " + error.toString());
                    }
                    if (error instanceof WebServiceException) {
                        DispatchAsyncInvoker.this.responseImpl.set(null, error);
                    } else {
                        DispatchAsyncInvoker.this.responseImpl.set(null, new WebServiceException(error));
                    }
                }
            };
            DispatchImpl.this.processAsync(this.responseImpl, message, this.rc, callback);
        }
    }

    private class Invoker
    implements Callable {
        private final T param;
        private final RequestContext rc;
        private ResponseContextReceiver receiver;

        Invoker(T param) {
            this.rc = DispatchImpl.this.requestContext.copy();
            this.param = param;
        }

        public T call() throws Exception {
            if (LOGGER.isLoggable(Level.FINE)) {
                DispatchImpl.this.dumpParam(this.param, "call()");
            }
            return DispatchImpl.this.doInvoke(this.param, this.rc, this.receiver);
        }

        void setReceiver(ResponseContextReceiver receiver) {
            this.receiver = receiver;
        }
    }
}

