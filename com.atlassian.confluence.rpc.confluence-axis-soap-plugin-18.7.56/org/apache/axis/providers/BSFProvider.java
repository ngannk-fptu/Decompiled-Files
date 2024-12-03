/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.providers;

import java.util.Vector;
import javax.xml.namespace.QName;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.components.script.Script;
import org.apache.axis.components.script.ScriptFactory;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCHeaderParam;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.providers.BasicProvider;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class BSFProvider
extends BasicProvider {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$providers$BSFProvider == null ? (class$org$apache$axis$providers$BSFProvider = BSFProvider.class$("org.apache.axis.providers.BSFProvider")) : class$org$apache$axis$providers$BSFProvider).getName());
    public static final String OPTION_LANGUAGE = "language";
    public static final String OPTION_SRC = "src";
    public static final String OPTION_SCRIPT = "script";
    static /* synthetic */ Class class$org$apache$axis$providers$BSFProvider;

    public void invoke(MessageContext msgContext) throws AxisFault {
        try {
            SOAPEnvelope resEnv;
            SOAPService service = msgContext.getService();
            String language = (String)service.getOption(OPTION_LANGUAGE);
            String scriptStr = (String)service.getOption(OPTION_SRC);
            if (log.isDebugEnabled()) {
                log.debug((Object)"Enter: BSFProvider.processMessage()");
            }
            OperationDesc operation = msgContext.getOperation();
            Vector bodies = msgContext.getRequestMessage().getSOAPEnvelope().getBodyElements();
            if (log.isDebugEnabled()) {
                log.debug((Object)Messages.getMessage("bodyElems00", "" + bodies.size()));
                log.debug((Object)Messages.getMessage("bodyIs00", "" + bodies.get(0)));
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
            String methodName = body.getMethodName();
            Vector args = body.getParams();
            int numArgs = args.size();
            Object[] argValues = new Object[numArgs];
            for (int i = 0; i < numArgs; ++i) {
                RPCParam rpcParam = (RPCParam)args.get(i);
                Object value = rpcParam.getObjectValue();
                ParameterDesc paramDesc = rpcParam.getParamDesc();
                if (paramDesc != null && paramDesc.getJavaType() != null) {
                    Class sigType = paramDesc.getJavaType();
                    value = JavaUtils.convert(value, sigType);
                    rpcParam.setObjectValue(value);
                }
                argValues[i] = value;
            }
            Script script = ScriptFactory.getScript();
            Object result = script.run(language, service.getName(), scriptStr, methodName, argValues);
            RPCElement resBody = new RPCElement(methodName + "Response");
            resBody.setPrefix(body.getPrefix());
            resBody.setNamespaceURI(body.getNamespaceURI());
            resBody.setEncodingStyle(msgContext.getEncodingStyle());
            Message resMsg = msgContext.getResponseMessage();
            if (resMsg == null) {
                resEnv = new SOAPEnvelope(msgContext.getSOAPConstants());
                resMsg = new Message(resEnv);
                msgContext.setResponseMessage(resMsg);
            } else {
                resEnv = resMsg.getSOAPEnvelope();
            }
            QName returnQName = operation.getReturnQName();
            if (returnQName == null) {
                returnQName = new QName("", methodName + "Return");
            }
            if (msgContext.getSOAPConstants() == SOAPConstants.SOAP12_CONSTANTS) {
                returnQName = Constants.QNAME_RPC_RESULT;
            }
            RPCParam param = new RPCParam(returnQName, result);
            param.setParamDesc(operation.getReturnParamDesc());
            if (!operation.isReturnHeader()) {
                resBody.addParam(param);
            } else {
                resEnv.addHeader(new RPCHeaderParam(param));
            }
            resEnv.addBodyElement(resBody);
        }
        catch (Exception e) {
            entLog.debug((Object)Messages.getMessage("toAxisFault00"), (Throwable)e);
            throw AxisFault.makeFault(e);
        }
    }

    public void initServiceDesc(SOAPService service, MessageContext msgContext) throws AxisFault {
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

