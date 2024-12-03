/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.client.sei;

import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.client.sei.BodyBuilder;
import com.sun.xml.ws.client.sei.MessageFiller;
import com.sun.xml.ws.client.sei.MethodHandler;
import com.sun.xml.ws.client.sei.ResponseBuilder;
import com.sun.xml.ws.client.sei.SEIStub;
import com.sun.xml.ws.client.sei.ValueGetter;
import com.sun.xml.ws.client.sei.ValueGetterFactory;
import com.sun.xml.ws.client.sei.ValueSetter;
import com.sun.xml.ws.client.sei.ValueSetterFactory;
import com.sun.xml.ws.model.CheckedExceptionImpl;
import com.sun.xml.ws.model.JavaMethodImpl;
import com.sun.xml.ws.model.ParameterImpl;
import com.sun.xml.ws.model.WrapperParameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

abstract class SEIMethodHandler
extends MethodHandler {
    private BodyBuilder bodyBuilder;
    private MessageFiller[] inFillers;
    protected String soapAction;
    protected boolean isOneWay;
    protected JavaMethodImpl javaMethod;
    protected Map<QName, CheckedExceptionImpl> checkedExceptions;

    SEIMethodHandler(SEIStub owner) {
        super(owner, null);
    }

    SEIMethodHandler(SEIStub owner, JavaMethodImpl method) {
        super(owner, null);
        this.checkedExceptions = new HashMap<QName, CheckedExceptionImpl>();
        for (CheckedExceptionImpl ce : method.getCheckedExceptions()) {
            this.checkedExceptions.put(ce.getBond().getTypeInfo().tagName, ce);
        }
        this.soapAction = method.getInputAction() != null && !method.getBinding().getSOAPAction().equals("") ? method.getInputAction() : method.getBinding().getSOAPAction();
        this.javaMethod = method;
        List<ParameterImpl> rp = method.getRequestParameters();
        BodyBuilder tmpBodyBuilder = null;
        ArrayList<MessageFiller> fillers = new ArrayList<MessageFiller>();
        block11: for (ParameterImpl param : rp) {
            ValueGetter getter = this.getValueGetterFactory().get(param);
            switch (param.getInBinding().kind) {
                case BODY: {
                    if (param.isWrapperStyle()) {
                        if (param.getParent().getBinding().isRpcLit()) {
                            tmpBodyBuilder = new BodyBuilder.RpcLit((WrapperParameter)param, owner.soapVersion, this.getValueGetterFactory());
                            continue block11;
                        }
                        tmpBodyBuilder = new BodyBuilder.DocLit((WrapperParameter)param, owner.soapVersion, this.getValueGetterFactory());
                        continue block11;
                    }
                    tmpBodyBuilder = new BodyBuilder.Bare(param, owner.soapVersion, getter);
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
        if (tmpBodyBuilder == null) {
            switch (owner.soapVersion) {
                case SOAP_11: {
                    tmpBodyBuilder = BodyBuilder.EMPTY_SOAP11;
                    break;
                }
                case SOAP_12: {
                    tmpBodyBuilder = BodyBuilder.EMPTY_SOAP12;
                    break;
                }
                default: {
                    throw new AssertionError();
                }
            }
        }
        this.bodyBuilder = tmpBodyBuilder;
        this.inFillers = fillers.toArray(new MessageFiller[fillers.size()]);
        this.isOneWay = method.getMEP().isOneWay();
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
                    builders.add(new ResponseBuilder.Header(this.owner.soapVersion, param, setter));
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

    Message createRequestMessage(Object[] args) {
        Message msg = this.bodyBuilder.createMessage(args);
        for (MessageFiller filler : this.inFillers) {
            filler.fillIn(args, msg);
        }
        return msg;
    }

    abstract ValueGetterFactory getValueGetterFactory();
}

