/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.message.EnvelopeHandler;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.NodeImpl;
import org.apache.axis.message.RPCHandler;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.toJava.Utils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class RPCElement
extends SOAPBodyElement {
    protected boolean needDeser = false;
    OperationDesc[] operations = null;

    public RPCElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context, OperationDesc[] operations) throws AxisFault {
        super(namespace, localName, prefix, attributes, context);
        this.needDeser = true;
        if (operations == null) {
            this.updateOperationsByName();
        } else {
            this.operations = operations;
        }
    }

    public RPCElement(String namespace, String methodName, Object[] args) {
        this.setNamespaceURI(namespace);
        this.name = methodName;
        for (int i = 0; args != null && i < args.length; ++i) {
            if (args[i] instanceof RPCParam) {
                this.addParam((RPCParam)args[i]);
                continue;
            }
            String name = null;
            if (name == null) {
                name = "arg" + i;
            }
            this.addParam(new RPCParam(namespace, name, args[i]));
        }
    }

    public RPCElement(String methodName) {
        this.name = methodName;
    }

    public void updateOperationsByName() throws AxisFault {
        if (this.context == null) {
            return;
        }
        MessageContext msgContext = this.context.getMessageContext();
        if (msgContext == null) {
            return;
        }
        SOAPService service = msgContext.getService();
        if (service == null) {
            return;
        }
        ServiceDesc serviceDesc = service.getInitializedServiceDesc(msgContext);
        String lc = Utils.xmlNameToJava(this.name);
        if (serviceDesc == null) {
            throw AxisFault.makeFault(new ClassNotFoundException(Messages.getMessage("noClassForService00", lc)));
        }
        this.operations = serviceDesc.getOperationsByName(lc);
    }

    public void updateOperationsByQName() throws AxisFault {
        if (this.context == null) {
            return;
        }
        MessageContext msgContext = this.context.getMessageContext();
        if (msgContext == null) {
            return;
        }
        this.operations = msgContext.getPossibleOperationsByQName(this.getQName());
    }

    public OperationDesc[] getOperations() {
        return this.operations;
    }

    public String getMethodName() {
        return this.name;
    }

    public void setNeedDeser(boolean needDeser) {
        this.needDeser = needDeser;
    }

    public void deserialize() throws SAXException {
        this.needDeser = false;
        MessageContext msgContext = this.context.getMessageContext();
        Message msg = msgContext.getCurrentMessage();
        SOAPConstants soapConstants = msgContext.getSOAPConstants();
        boolean isResponse = msg != null && "response".equals(msg.getMessageType());
        RPCHandler rpcHandler = new RPCHandler(this, isResponse);
        if (this.operations != null) {
            int numParams = this.getChildren() == null ? 0 : this.getChildren().size();
            SAXException savedException = null;
            boolean acceptMissingParams = msgContext.isPropertyTrue("acceptMissingParams", true);
            for (int i = 0; i < this.operations.length; ++i) {
                OperationDesc operation = this.operations[i];
                boolean needHeaderProcessing = this.needHeaderProcessing(operation, isResponse);
                if (operation.getStyle() != Style.DOCUMENT && operation.getStyle() != Style.WRAPPED && operation.getUse() != Use.LITERAL && !(acceptMissingParams ? operation.getNumInParams() >= numParams : operation.getNumInParams() == numParams)) continue;
                boolean isEncoded = operation.getUse() == Use.ENCODED;
                rpcHandler.setOperation(operation);
                try {
                    if (msgContext.isClient() && operation.getStyle() == Style.DOCUMENT || !msgContext.isClient() && operation.getStyle() == Style.DOCUMENT && operation.getNumInParams() > 0) {
                        this.context.pushElementHandler(rpcHandler);
                        this.context.setCurElement(null);
                    } else {
                        this.context.pushElementHandler(new EnvelopeHandler(rpcHandler));
                        this.context.setCurElement(this);
                    }
                    this.publishToHandler(this.context);
                    if (needHeaderProcessing) {
                        this.processHeaders(operation, isResponse, this.context, rpcHandler);
                    }
                    boolean match = true;
                    List params = this.getParams2();
                    for (int j = 0; j < params.size() && match; ++j) {
                        RPCParam rpcParam = (RPCParam)params.get(j);
                        ArrayList value = rpcParam.getObjectValue();
                        ParameterDesc paramDesc = rpcParam.getParamDesc();
                        if (paramDesc == null || paramDesc.getJavaType() == null) continue;
                        Class sigType = paramDesc.getJavaType();
                        if (sigType.isArray() && value != null && JavaUtils.isConvertable(value, sigType.getComponentType()) && !value.getClass().isArray() && !(value instanceof Collection)) {
                            ArrayList list = new ArrayList();
                            list.add(value);
                            value = list;
                            rpcParam.setObjectValue(value);
                        }
                        if (JavaUtils.isConvertable(value, sigType, isEncoded)) continue;
                        match = false;
                    }
                    if (match) {
                        msgContext.setOperation(operation);
                        return;
                    }
                    this.children = new ArrayList();
                    continue;
                }
                catch (SAXException e) {
                    savedException = e;
                    this.children = new ArrayList();
                    continue;
                }
                catch (AxisFault e) {
                    savedException = new SAXException(e);
                    this.children = new ArrayList();
                }
            }
            if (!msgContext.isClient() && soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
                AxisFault fault = new AxisFault(Constants.FAULT_SOAP12_SENDER, "string", null, null);
                fault.addFaultSubCode(Constants.FAULT_SUBCODE_BADARGS);
                throw new SAXException(fault);
            }
            if (savedException != null) {
                throw savedException;
            }
            if (!msgContext.isClient()) {
                QName faultCode = new QName("Server.userException");
                if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
                    faultCode = Constants.FAULT_SOAP12_SENDER;
                }
                AxisFault fault = new AxisFault(faultCode, null, Messages.getMessage("noSuchOperation", this.name), null, null, null);
                throw new SAXException(fault);
            }
        }
        if (this.operations != null) {
            rpcHandler.setOperation(this.operations[0]);
        }
        if (this.operations != null && this.operations.length > 0 && this.operations[0].getStyle() == Style.DOCUMENT) {
            this.context.pushElementHandler(rpcHandler);
            this.context.setCurElement(null);
        } else {
            this.context.pushElementHandler(new EnvelopeHandler(rpcHandler));
            this.context.setCurElement(this);
        }
        this.publishToHandler(this.context);
    }

    private List getParams2() {
        return this.getParams(new ArrayList());
    }

    private List getParams(List list) {
        for (int i = 0; this.children != null && i < this.children.size(); ++i) {
            Object child = this.children.get(i);
            if (!(child instanceof RPCParam)) continue;
            list.add(child);
        }
        return list;
    }

    public RPCParam getParam(String name) throws SAXException {
        if (this.needDeser) {
            this.deserialize();
        }
        List params = this.getParams2();
        for (int i = 0; i < params.size(); ++i) {
            RPCParam param = (RPCParam)params.get(i);
            if (!param.getName().equals(name)) continue;
            return param;
        }
        return null;
    }

    public Vector getParams() throws SAXException {
        if (this.needDeser) {
            this.deserialize();
        }
        return (Vector)this.getParams(new Vector());
    }

    public void addParam(RPCParam param) {
        param.setRPCCall(this);
        this.initializeChildren();
        this.children.add(param);
    }

    protected void outputImpl(SerializationContext context) throws Exception {
        boolean noParams;
        MessageContext msgContext = context.getMessageContext();
        boolean hasOperationElement = msgContext == null || msgContext.getOperationStyle() == Style.RPC || msgContext.getOperationStyle() == Style.WRAPPED;
        boolean bl = noParams = this.getParams2().size() == 0;
        if (hasOperationElement || noParams) {
            if (this.encodingStyle != null && this.encodingStyle.equals("")) {
                context.registerPrefixForURI("", this.getNamespaceURI());
            }
            context.startElement(new QName(this.getNamespaceURI(), this.name), this.attributes);
        }
        if (noParams) {
            if (this.children != null) {
                Iterator it = this.children.iterator();
                while (it.hasNext()) {
                    ((NodeImpl)it.next()).output(context);
                }
            }
        } else {
            List params = this.getParams2();
            for (int i = 0; i < params.size(); ++i) {
                RPCParam param = (RPCParam)params.get(i);
                if (!hasOperationElement && this.encodingStyle != null && this.encodingStyle.equals("")) {
                    context.registerPrefixForURI("", param.getQName().getNamespaceURI());
                }
                param.serialize(context);
            }
        }
        if (hasOperationElement || noParams) {
            context.endElement();
        }
    }

    private boolean needHeaderProcessing(OperationDesc operation, boolean isResponse) {
        ArrayList paramDescs = operation.getParameters();
        if (paramDescs != null) {
            for (int j = 0; j < paramDescs.size(); ++j) {
                ParameterDesc paramDesc = (ParameterDesc)paramDescs.get(j);
                if ((isResponse || !paramDesc.isInHeader()) && (!isResponse || !paramDesc.isOutHeader())) continue;
                return true;
            }
        }
        return isResponse && operation.getReturnParamDesc() != null && operation.getReturnParamDesc().isOutHeader();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void processHeaders(OperationDesc operation, boolean isResponse, DeserializationContext context, RPCHandler handler) throws AxisFault, SAXException {
        try {
            SOAPElement envelope;
            handler.setHeaderElement(true);
            for (envelope = this.getParentElement(); envelope != null && !(envelope instanceof SOAPEnvelope); envelope = envelope.getParentElement()) {
            }
            if (envelope == null) {
                return;
            }
            ArrayList paramDescs = operation.getParameters();
            if (paramDescs != null) {
                for (int j = 0; j < paramDescs.size(); ++j) {
                    ParameterDesc paramDesc = (ParameterDesc)paramDescs.get(j);
                    if ((isResponse || !paramDesc.isInHeader()) && (!isResponse || !paramDesc.isOutHeader())) continue;
                    Enumeration headers = ((SOAPEnvelope)envelope).getHeadersByName(paramDesc.getQName().getNamespaceURI(), paramDesc.getQName().getLocalPart(), true);
                    while (headers != null && headers.hasMoreElements()) {
                        context.pushElementHandler(handler);
                        context.setCurElement(null);
                        ((MessageElement)headers.nextElement()).publishToHandler(context);
                    }
                }
            }
            if (isResponse && operation.getReturnParamDesc() != null && operation.getReturnParamDesc().isOutHeader()) {
                ParameterDesc paramDesc = operation.getReturnParamDesc();
                Enumeration headers = ((SOAPEnvelope)envelope).getHeadersByName(paramDesc.getQName().getNamespaceURI(), paramDesc.getQName().getLocalPart(), true);
                while (headers != null && headers.hasMoreElements()) {
                    context.pushElementHandler(handler);
                    context.setCurElement(null);
                    ((MessageElement)headers.nextElement()).publishToHandler(context);
                }
            }
        }
        finally {
            handler.setHeaderElement(false);
        }
    }
}

