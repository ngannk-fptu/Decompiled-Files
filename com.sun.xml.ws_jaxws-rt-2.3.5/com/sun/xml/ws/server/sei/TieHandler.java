/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.jws.WebParam$Mode
 *  javax.xml.bind.JAXBException
 *  javax.xml.ws.ProtocolException
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.server.sei;

import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.databinding.EndpointCallBridge;
import com.sun.xml.ws.api.databinding.JavaCallInfo;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.MessageContextFactory;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.JavaMethod;
import com.sun.xml.ws.fault.SOAPFaultBuilder;
import com.sun.xml.ws.model.JavaMethodImpl;
import com.sun.xml.ws.model.ParameterImpl;
import com.sun.xml.ws.model.WrapperParameter;
import com.sun.xml.ws.server.sei.EndpointArgumentsBuilder;
import com.sun.xml.ws.server.sei.EndpointResponseMessageBuilder;
import com.sun.xml.ws.server.sei.EndpointValueSetter;
import com.sun.xml.ws.server.sei.MessageFiller;
import com.sun.xml.ws.server.sei.ValueGetter;
import com.sun.xml.ws.wsdl.DispatchException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebParam;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.WebServiceException;

public final class TieHandler
implements EndpointCallBridge {
    private final SOAPVersion soapVersion;
    private final Method method;
    private final int noOfArgs;
    private final JavaMethodImpl javaMethodModel;
    private final Boolean isOneWay;
    private final EndpointArgumentsBuilder argumentsBuilder;
    private final EndpointResponseMessageBuilder bodyBuilder;
    private final MessageFiller[] outFillers;
    protected MessageContextFactory packetFactory;
    private static final Logger LOGGER = Logger.getLogger(TieHandler.class.getName());

    public TieHandler(JavaMethodImpl method, WSBinding binding, MessageContextFactory mcf) {
        this.soapVersion = binding.getSOAPVersion();
        this.method = method.getMethod();
        this.javaMethodModel = method;
        this.argumentsBuilder = this.createArgumentsBuilder();
        ArrayList<MessageFiller> fillers = new ArrayList<MessageFiller>();
        this.bodyBuilder = this.createResponseMessageBuilder(fillers);
        this.outFillers = fillers.toArray(new MessageFiller[fillers.size()]);
        this.isOneWay = method.getMEP().isOneWay();
        this.noOfArgs = this.method.getParameterTypes().length;
        this.packetFactory = mcf;
    }

    private EndpointArgumentsBuilder createArgumentsBuilder() {
        EndpointArgumentsBuilder argsBuilder;
        List<ParameterImpl> rp = this.javaMethodModel.getRequestParameters();
        ArrayList<EndpointArgumentsBuilder> builders = new ArrayList<EndpointArgumentsBuilder>();
        block10: for (ParameterImpl param : rp) {
            EndpointValueSetter setter = EndpointValueSetter.get(param);
            switch (param.getInBinding().kind) {
                case BODY: {
                    if (param.isWrapperStyle()) {
                        if (param.getParent().getBinding().isRpcLit()) {
                            builders.add(new EndpointArgumentsBuilder.RpcLit((WrapperParameter)param));
                            continue block10;
                        }
                        builders.add(new EndpointArgumentsBuilder.DocLit((WrapperParameter)param, WebParam.Mode.OUT));
                        continue block10;
                    }
                    builders.add(new EndpointArgumentsBuilder.Body(param.getXMLBridge(), setter));
                    continue block10;
                }
                case HEADER: {
                    builders.add(new EndpointArgumentsBuilder.Header(this.soapVersion, param, setter));
                    continue block10;
                }
                case ATTACHMENT: {
                    builders.add(EndpointArgumentsBuilder.AttachmentBuilder.createAttachmentBuilder(param, setter));
                    continue block10;
                }
                case UNBOUND: {
                    builders.add(new EndpointArgumentsBuilder.NullSetter(setter, EndpointArgumentsBuilder.getVMUninitializedValue(param.getTypeInfo().type)));
                    continue block10;
                }
            }
            throw new AssertionError();
        }
        List<ParameterImpl> resp = this.javaMethodModel.getResponseParameters();
        for (ParameterImpl param : resp) {
            if (param.isWrapperStyle()) {
                WrapperParameter wp = (WrapperParameter)param;
                List<ParameterImpl> children = wp.getWrapperChildren();
                for (ParameterImpl p : children) {
                    if (!p.isOUT() || p.getIndex() == -1) continue;
                    EndpointValueSetter setter = EndpointValueSetter.get(p);
                    builders.add(new EndpointArgumentsBuilder.NullSetter(setter, null));
                }
                continue;
            }
            if (!param.isOUT() || param.getIndex() == -1) continue;
            EndpointValueSetter setter = EndpointValueSetter.get(param);
            builders.add(new EndpointArgumentsBuilder.NullSetter(setter, null));
        }
        switch (builders.size()) {
            case 0: {
                argsBuilder = EndpointArgumentsBuilder.NONE;
                break;
            }
            case 1: {
                argsBuilder = (EndpointArgumentsBuilder)builders.get(0);
                break;
            }
            default: {
                argsBuilder = new EndpointArgumentsBuilder.Composite(builders);
            }
        }
        return argsBuilder;
    }

    private EndpointResponseMessageBuilder createResponseMessageBuilder(List<MessageFiller> fillers) {
        EndpointResponseMessageBuilder tmpBodyBuilder = null;
        List<ParameterImpl> rp = this.javaMethodModel.getResponseParameters();
        block10: for (ParameterImpl param : rp) {
            ValueGetter getter = ValueGetter.get(param);
            switch (param.getOutBinding().kind) {
                case BODY: {
                    if (param.isWrapperStyle()) {
                        if (param.getParent().getBinding().isRpcLit()) {
                            tmpBodyBuilder = new EndpointResponseMessageBuilder.RpcLit((WrapperParameter)param, this.soapVersion);
                            continue block10;
                        }
                        tmpBodyBuilder = new EndpointResponseMessageBuilder.DocLit((WrapperParameter)param, this.soapVersion);
                        continue block10;
                    }
                    tmpBodyBuilder = new EndpointResponseMessageBuilder.Bare(param, this.soapVersion);
                    continue block10;
                }
                case HEADER: {
                    fillers.add(new MessageFiller.Header(param.getIndex(), param.getXMLBridge(), getter));
                    continue block10;
                }
                case ATTACHMENT: {
                    fillers.add(MessageFiller.AttachmentFiller.createAttachmentFiller(param, getter));
                    continue block10;
                }
                case UNBOUND: {
                    continue block10;
                }
            }
            throw new AssertionError();
        }
        if (tmpBodyBuilder == null) {
            switch (this.soapVersion) {
                case SOAP_11: {
                    tmpBodyBuilder = EndpointResponseMessageBuilder.EMPTY_SOAP11;
                    break;
                }
                case SOAP_12: {
                    tmpBodyBuilder = EndpointResponseMessageBuilder.EMPTY_SOAP12;
                    break;
                }
                default: {
                    throw new AssertionError();
                }
            }
        }
        return tmpBodyBuilder;
    }

    public Object[] readRequest(Message reqMsg) {
        Object[] args = new Object[this.noOfArgs];
        try {
            this.argumentsBuilder.readRequest(reqMsg, args);
        }
        catch (JAXBException e) {
            throw new WebServiceException((Throwable)e);
        }
        catch (XMLStreamException e) {
            throw new WebServiceException((Throwable)e);
        }
        return args;
    }

    public Message createResponse(com.oracle.webservices.api.databinding.JavaCallInfo call) {
        Message responseMessage;
        if (call.getException() == null) {
            responseMessage = this.isOneWay != false ? null : this.createResponseMessage(call.getParameters(), call.getReturnValue());
        } else {
            Throwable e = call.getException();
            Throwable serviceException = this.getServiceException(e);
            if (e instanceof InvocationTargetException || serviceException != null) {
                if (serviceException != null) {
                    LOGGER.log(Level.FINE, serviceException.getMessage(), serviceException);
                    responseMessage = SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, this.javaMethodModel.getCheckedException(serviceException.getClass()), serviceException);
                } else {
                    Throwable cause = e.getCause();
                    if (cause instanceof ProtocolException) {
                        LOGGER.log(Level.FINE, cause.getMessage(), cause);
                    } else {
                        LOGGER.log(Level.SEVERE, cause.getMessage(), cause);
                    }
                    responseMessage = SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, null, cause);
                }
            } else if (e instanceof DispatchException) {
                responseMessage = ((DispatchException)e).fault;
            } else {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                responseMessage = SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, null, e);
            }
        }
        return responseMessage;
    }

    Throwable getServiceException(Throwable throwable) {
        Throwable cause;
        if (this.javaMethodModel.getCheckedException(throwable.getClass()) != null) {
            return throwable;
        }
        if (throwable.getCause() != null && this.javaMethodModel.getCheckedException((cause = throwable.getCause()).getClass()) != null) {
            return cause;
        }
        return null;
    }

    private Message createResponseMessage(Object[] args, Object returnValue) {
        Message msg = this.bodyBuilder.createMessage(args, returnValue);
        for (MessageFiller filler : this.outFillers) {
            filler.fillIn(args, returnValue, msg);
        }
        return msg;
    }

    public Method getMethod() {
        return this.method;
    }

    @Override
    public com.oracle.webservices.api.databinding.JavaCallInfo deserializeRequest(Packet req) {
        JavaCallInfo call = new JavaCallInfo();
        call.setMethod(this.getMethod());
        Object[] args = this.readRequest(req.getMessage());
        call.setParameters(args);
        return call;
    }

    @Override
    public Packet serializeResponse(com.oracle.webservices.api.databinding.JavaCallInfo call) {
        Message msg = this.createResponse(call);
        Packet p = msg == null ? (Packet)this.packetFactory.createContext() : (Packet)this.packetFactory.createContext(msg);
        p.setState(Packet.State.ServerResponse);
        return p;
    }

    @Override
    public JavaMethod getOperationModel() {
        return this.javaMethodModel;
    }
}

