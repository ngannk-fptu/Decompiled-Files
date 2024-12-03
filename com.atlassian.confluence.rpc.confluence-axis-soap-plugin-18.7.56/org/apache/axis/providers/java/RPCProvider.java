/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.OperationType
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.providers.java;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import javax.wsdl.OperationType;
import javax.xml.namespace.QName;
import javax.xml.rpc.holders.Holder;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.constants.Style;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCHeaderParam;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.providers.java.JavaProvider;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.xml.sax.SAXException;

public class RPCProvider
extends JavaProvider {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$providers$java$RPCProvider == null ? (class$org$apache$axis$providers$java$RPCProvider = RPCProvider.class$("org.apache.axis.providers.java.RPCProvider")) : class$org$apache$axis$providers$java$RPCProvider).getName());
    static /* synthetic */ Class class$org$apache$axis$providers$java$RPCProvider;
    static /* synthetic */ Class class$javax$xml$rpc$holders$Holder;

    public void processMessage(MessageContext msgContext, SOAPEnvelope reqEnv, SOAPEnvelope resEnv, Object obj) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: RPCProvider.processMessage()");
        }
        SOAPService service = msgContext.getService();
        ServiceDesc serviceDesc = service.getServiceDescription();
        OperationDesc operation = msgContext.getOperation();
        Vector bodies = reqEnv.getBodyElements();
        if (log.isDebugEnabled()) {
            log.debug((Object)Messages.getMessage("bodyElems00", "" + bodies.size()));
            if (bodies.size() > 0) {
                log.debug((Object)Messages.getMessage("bodyIs00", "" + bodies.get(0)));
            }
        }
        RPCElement body = null;
        for (int bNum = 0; body == null && bNum < bodies.size(); ++bNum) {
            if (!(bodies.get(bNum) instanceof RPCElement)) {
                ParameterDesc param;
                SOAPBodyElement bodyEl = (SOAPBodyElement)bodies.get(bNum);
                if (!bodyEl.isRoot() || operation == null || bodyEl.getID() != null || (param = operation.getParameter(bNum)) == null) continue;
                Object val = bodyEl.getValueAsType(param.getTypeQName());
                body = new RPCElement("", operation.getName(), new Object[]{val});
                continue;
            }
            body = (RPCElement)bodies.get(bNum);
        }
        if (body == null) {
            if (!serviceDesc.getStyle().equals(Style.DOCUMENT)) {
                throw new Exception(Messages.getMessage("noBody00"));
            }
            ArrayList ops = serviceDesc.getOperations();
            Iterator iterator = ops.iterator();
            while (iterator.hasNext()) {
                OperationDesc desc = (OperationDesc)iterator.next();
                if (desc.getNumInParams() != 0) continue;
                msgContext.setOperation(desc);
                body = new RPCElement(desc.getName());
                break;
            }
            if (body == null) {
                throw new Exception(Messages.getMessage("noBody00"));
            }
        }
        String methodName = body.getMethodName();
        Vector args = null;
        try {
            args = body.getParams();
        }
        catch (SAXException e) {
            if (e.getException() != null) {
                throw e.getException();
            }
            throw e;
        }
        int numArgs = args.size();
        operation = msgContext.getOperation();
        if (operation == null) {
            QName qname = new QName(body.getNamespaceURI(), body.getName());
            operation = serviceDesc.getOperationByElementQName(qname);
            if (operation == null) {
                SOAPConstants soapConstants;
                SOAPConstants sOAPConstants = soapConstants = msgContext == null ? SOAPConstants.SOAP11_CONSTANTS : msgContext.getSOAPConstants();
                if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
                    AxisFault fault = new AxisFault(Constants.FAULT_SOAP12_SENDER, Messages.getMessage("noSuchOperation", methodName), null, null);
                    fault.addFaultSubCode(Constants.FAULT_SUBCODE_PROC_NOT_PRESENT);
                    throw new SAXException(fault);
                }
                throw new AxisFault("Client", Messages.getMessage("noSuchOperation", methodName), null, null);
            }
            msgContext.setOperation(operation);
        }
        Object[] argValues = new Object[operation.getNumParams()];
        ArrayList<RPCParam> outs = new ArrayList<RPCParam>();
        for (int i = 0; i < numArgs; ++i) {
            RPCParam rpcParam = (RPCParam)args.get(i);
            Object value = rpcParam.getObjectValue();
            ParameterDesc paramDesc = rpcParam.getParamDesc();
            if (paramDesc != null && paramDesc.getJavaType() != null) {
                Class sigType = paramDesc.getJavaType();
                value = JavaUtils.convert(value, sigType);
                rpcParam.setObjectValue(value);
                if (paramDesc.getMode() == 3) {
                    outs.add(rpcParam);
                }
            }
            if (paramDesc == null || paramDesc.getOrder() == -1) {
                argValues[i] = value;
            } else {
                argValues[paramDesc.getOrder()] = value;
            }
            if (!log.isDebugEnabled()) continue;
            log.debug((Object)("  " + Messages.getMessage("value00", "" + argValues[i])));
        }
        String allowedMethods = (String)service.getOption("allowedMethods");
        this.checkMethodName(msgContext, allowedMethods, operation.getName());
        int count = numArgs;
        for (int i = 0; i < argValues.length; ++i) {
            ParameterDesc param = operation.getParameter(i);
            if (param.getMode() == 1) continue;
            Class holderClass = param.getJavaType();
            if (holderClass != null && (class$javax$xml$rpc$holders$Holder == null ? RPCProvider.class$("javax.xml.rpc.holders.Holder") : class$javax$xml$rpc$holders$Holder).isAssignableFrom(holderClass)) {
                int index = count++;
                if (param.getOrder() != -1) {
                    index = param.getOrder();
                }
                if (argValues[index] != null) continue;
                argValues[index] = holderClass.newInstance();
                RPCParam p = new RPCParam(param.getQName(), argValues[index]);
                p.setParamDesc(param);
                outs.add(p);
                continue;
            }
            throw new AxisFault(Messages.getMessage("badOutParameter00", "" + param.getQName(), operation.getName()));
        }
        Object objRes = null;
        try {
            objRes = this.invokeMethod(msgContext, operation.getMethod(), obj, argValues);
        }
        catch (IllegalArgumentException e) {
            String methodSig = operation.getMethod().toString();
            String argClasses = "";
            for (int i = 0; i < argValues.length; ++i) {
                argClasses = argValues[i] == null ? argClasses + "null" : argClasses + argValues[i].getClass().getName();
                if (i + 1 >= argValues.length) continue;
                argClasses = argClasses + ",";
            }
            log.info((Object)Messages.getMessage("dispatchIAE00", new String[]{methodSig, argClasses}), (Throwable)e);
            throw new AxisFault(Messages.getMessage("dispatchIAE00", new String[]{methodSig, argClasses}), e);
        }
        if (OperationType.ONE_WAY.equals(operation.getMep())) {
            return;
        }
        RPCElement resBody = new RPCElement(methodName + "Response");
        resBody.setPrefix(body.getPrefix());
        resBody.setNamespaceURI(body.getNamespaceURI());
        resBody.setEncodingStyle(msgContext.getEncodingStyle());
        if (operation.getMethod().getReturnType() != Void.TYPE) {
            QName returnQName = operation.getReturnQName();
            if (returnQName == null) {
                String nsp = body.getNamespaceURI();
                if (nsp == null || nsp.length() == 0) {
                    nsp = serviceDesc.getDefaultNamespace();
                }
                returnQName = new QName(msgContext.isEncoded() ? "" : nsp, methodName + "Return");
            }
            RPCParam param = new RPCParam(returnQName, objRes);
            param.setParamDesc(operation.getReturnParamDesc());
            if (!operation.isReturnHeader()) {
                if (msgContext.getSOAPConstants() == SOAPConstants.SOAP12_CONSTANTS && serviceDesc.getStyle().equals(Style.RPC)) {
                    RPCParam resultParam = new RPCParam(Constants.QNAME_RPC_RESULT, (Object)returnQName);
                    resultParam.setXSITypeGeneration(Boolean.FALSE);
                    resBody.addParam(resultParam);
                }
                resBody.addParam(param);
            } else {
                resEnv.addHeader(new RPCHeaderParam(param));
            }
        }
        if (!outs.isEmpty()) {
            Iterator i = outs.iterator();
            while (i.hasNext()) {
                RPCParam param = (RPCParam)i.next();
                Holder holder = (Holder)param.getObjectValue();
                Object value = JavaUtils.getHolderValue(holder);
                ParameterDesc paramDesc = param.getParamDesc();
                param.setObjectValue(value);
                if (paramDesc != null && paramDesc.isOutHeader()) {
                    resEnv.addHeader(new RPCHeaderParam(param));
                    continue;
                }
                resBody.addParam(param);
            }
        }
        resEnv.addBodyElement(resBody);
    }

    protected Object invokeMethod(MessageContext msgContext, Method method, Object obj, Object[] argValues) throws Exception {
        return method.invoke(obj, argValues);
    }

    protected void checkMethodName(MessageContext msgContext, String allowedMethods, String methodName) throws Exception {
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

