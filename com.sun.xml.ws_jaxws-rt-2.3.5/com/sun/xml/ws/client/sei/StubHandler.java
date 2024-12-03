/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.client.sei;

import com.oracle.webservices.api.databinding.JavaCallInfo;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.databinding.ClientCallBridge;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.MessageContextFactory;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.JavaMethod;
import com.sun.xml.ws.client.sei.BodyBuilder;
import com.sun.xml.ws.client.sei.MessageFiller;
import com.sun.xml.ws.client.sei.ResponseBuilder;
import com.sun.xml.ws.client.sei.ValueGetter;
import com.sun.xml.ws.client.sei.ValueGetterFactory;
import com.sun.xml.ws.client.sei.ValueSetter;
import com.sun.xml.ws.client.sei.ValueSetterFactory;
import com.sun.xml.ws.fault.SOAPFaultBuilder;
import com.sun.xml.ws.model.CheckedExceptionImpl;
import com.sun.xml.ws.model.JavaMethodImpl;
import com.sun.xml.ws.model.ParameterImpl;
import com.sun.xml.ws.model.WrapperParameter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

public class StubHandler
implements ClientCallBridge {
    private final BodyBuilder bodyBuilder;
    private final MessageFiller[] inFillers;
    protected final String soapAction;
    protected final boolean isOneWay;
    protected final JavaMethodImpl javaMethod;
    protected final Map<QName, CheckedExceptionImpl> checkedExceptions;
    protected SOAPVersion soapVersion = SOAPVersion.SOAP_11;
    protected ResponseBuilder responseBuilder;
    protected MessageContextFactory packetFactory;

    public StubHandler(JavaMethodImpl method, MessageContextFactory mcf) {
        this.checkedExceptions = new HashMap<QName, CheckedExceptionImpl>();
        for (CheckedExceptionImpl ce : method.getCheckedExceptions()) {
            this.checkedExceptions.put(ce.getBond().getTypeInfo().tagName, ce);
        }
        String soapActionFromBinding = method.getBinding().getSOAPAction();
        this.soapAction = method.getInputAction() != null && soapActionFromBinding != null && !soapActionFromBinding.equals("") ? method.getInputAction() : soapActionFromBinding;
        this.javaMethod = method;
        this.packetFactory = mcf;
        this.soapVersion = this.javaMethod.getBinding().getSOAPVersion();
        List<ParameterImpl> rp = method.getRequestParameters();
        BodyBuilder bodyBuilder = null;
        ArrayList<MessageFiller> fillers = new ArrayList<MessageFiller>();
        block11: for (ParameterImpl param : rp) {
            ValueGetter getter = this.getValueGetterFactory().get(param);
            switch (param.getInBinding().kind) {
                case BODY: {
                    if (param.isWrapperStyle()) {
                        if (param.getParent().getBinding().isRpcLit()) {
                            bodyBuilder = new BodyBuilder.RpcLit((WrapperParameter)param, this.soapVersion, this.getValueGetterFactory());
                            continue block11;
                        }
                        bodyBuilder = new BodyBuilder.DocLit((WrapperParameter)param, this.soapVersion, this.getValueGetterFactory());
                        continue block11;
                    }
                    bodyBuilder = new BodyBuilder.Bare(param, this.soapVersion, getter);
                    continue block11;
                }
                case HEADER: {
                    fillers.add(new MessageFiller.Header(param.getIndex(), param.getXMLBridge(), getter));
                    continue block11;
                }
                case ATTACHMENT: {
                    fillers.add(MessageFiller.AttachmentFiller.createAttachmentFiller(param, getter));
                    continue block11;
                }
                case UNBOUND: {
                    continue block11;
                }
            }
            throw new AssertionError();
        }
        if (bodyBuilder == null) {
            switch (this.soapVersion) {
                case SOAP_11: {
                    bodyBuilder = BodyBuilder.EMPTY_SOAP11;
                    break;
                }
                case SOAP_12: {
                    bodyBuilder = BodyBuilder.EMPTY_SOAP12;
                    break;
                }
                default: {
                    throw new AssertionError();
                }
            }
        }
        this.bodyBuilder = bodyBuilder;
        this.inFillers = fillers.toArray(new MessageFiller[fillers.size()]);
        this.isOneWay = method.getMEP().isOneWay();
        this.responseBuilder = this.buildResponseBuilder(method, ValueSetterFactory.SYNC);
    }

    ResponseBuilder buildResponseBuilder(JavaMethodImpl method, ValueSetterFactory setterFactory) {
        ResponseBuilder rb;
        List<ParameterImpl> rp = method.getResponseParameters();
        ArrayList<ResponseBuilder> builders = new ArrayList<ResponseBuilder>();
        block10: for (ParameterImpl param : rp) {
            switch (param.getOutBinding().kind) {
                case BODY: {
                    if (param.isWrapperStyle()) {
                        if (param.getParent().getBinding().isRpcLit()) {
                            builders.add(new ResponseBuilder.RpcLit((WrapperParameter)param, setterFactory));
                            continue block10;
                        }
                        builders.add(new ResponseBuilder.DocLit((WrapperParameter)param, setterFactory));
                        continue block10;
                    }
                    ValueSetter setter = setterFactory.get(param);
                    builders.add(new ResponseBuilder.Body(param.getXMLBridge(), setter));
                    continue block10;
                }
                case HEADER: {
                    ValueSetter setter = setterFactory.get(param);
                    builders.add(new ResponseBuilder.Header(this.soapVersion, param, setter));
                    continue block10;
                }
                case ATTACHMENT: {
                    ValueSetter setter = setterFactory.get(param);
                    builders.add(ResponseBuilder.AttachmentBuilder.createAttachmentBuilder(param, setter));
                    continue block10;
                }
                case UNBOUND: {
                    ValueSetter setter = setterFactory.get(param);
                    builders.add(new ResponseBuilder.NullSetter(setter, ResponseBuilder.getVMUninitializedValue(param.getTypeInfo().type)));
                    continue block10;
                }
            }
            throw new AssertionError();
        }
        switch (builders.size()) {
            case 0: {
                rb = ResponseBuilder.NONE;
                break;
            }
            case 1: {
                rb = (ResponseBuilder)builders.get(0);
                break;
            }
            default: {
                rb = new ResponseBuilder.Composite(builders);
            }
        }
        return rb;
    }

    @Override
    public Packet createRequestPacket(JavaCallInfo args) {
        Message msg = this.bodyBuilder.createMessage(args.getParameters());
        for (MessageFiller filler : this.inFillers) {
            filler.fillIn(args.getParameters(), msg);
        }
        Packet req = (Packet)this.packetFactory.createContext(msg);
        req.setState(Packet.State.ClientRequest);
        req.soapAction = this.soapAction;
        req.expectReply = !this.isOneWay;
        req.getMessage().assertOneWay(this.isOneWay);
        req.setWSDLOperation(this.getOperationName());
        return req;
    }

    ValueGetterFactory getValueGetterFactory() {
        return ValueGetterFactory.SYNC;
    }

    @Override
    public JavaCallInfo readResponse(Packet p, JavaCallInfo call) throws Throwable {
        Message msg = p.getMessage();
        if (msg.isFault()) {
            SOAPFaultBuilder faultBuilder = SOAPFaultBuilder.create(msg);
            Throwable t = faultBuilder.createException(this.checkedExceptions);
            call.setException(t);
            throw t;
        }
        this.initArgs(call.getParameters());
        Object ret = this.responseBuilder.readResponse(msg, call.getParameters());
        call.setReturnValue(ret);
        return call;
    }

    public QName getOperationName() {
        return this.javaMethod.getOperationQName();
    }

    public String getSoapAction() {
        return this.soapAction;
    }

    public boolean isOneWay() {
        return this.isOneWay;
    }

    protected void initArgs(Object[] args) throws Exception {
    }

    @Override
    public Method getMethod() {
        return this.javaMethod.getMethod();
    }

    @Override
    public JavaMethod getOperationModel() {
        return this.javaMethod;
    }
}

