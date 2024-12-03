/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.wsdl;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.message.AddressingUtils;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.api.message.Messages;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.WSDLOperationMapping;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.model.AbstractSEIModelImpl;
import com.sun.xml.ws.model.JavaMethodImpl;
import com.sun.xml.ws.resources.AddressingMessages;
import com.sun.xml.ws.wsdl.ActionBasedOperationSignature;
import com.sun.xml.ws.wsdl.DispatchException;
import com.sun.xml.ws.wsdl.PayloadQNameBasedOperationFinder;
import com.sun.xml.ws.wsdl.WSDLOperationFinder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.xml.namespace.QName;

final class ActionBasedOperationFinder
extends WSDLOperationFinder {
    private static final Logger LOGGER = Logger.getLogger(ActionBasedOperationFinder.class.getName());
    private final Map<ActionBasedOperationSignature, WSDLOperationMapping> uniqueOpSignatureMap;
    private final Map<String, WSDLOperationMapping> actionMap;
    @NotNull
    private final AddressingVersion av;

    public ActionBasedOperationFinder(WSDLPort wsdlModel, WSBinding binding, @Nullable SEIModel seiModel) {
        super(wsdlModel, binding, seiModel);
        assert (binding.getAddressingVersion() != null);
        this.av = binding.getAddressingVersion();
        this.uniqueOpSignatureMap = new HashMap<ActionBasedOperationSignature, WSDLOperationMapping>();
        this.actionMap = new HashMap<String, WSDLOperationMapping>();
        if (seiModel != null) {
            for (JavaMethodImpl javaMethodImpl : ((AbstractSEIModelImpl)seiModel).getJavaMethods()) {
                if (javaMethodImpl.getMEP().isAsync) continue;
                String action = javaMethodImpl.getInputAction();
                QName payloadName = javaMethodImpl.getRequestPayloadName();
                if (payloadName == null) {
                    payloadName = PayloadQNameBasedOperationFinder.EMPTY_PAYLOAD;
                }
                if ((action == null || action.equals("")) && javaMethodImpl.getOperation() != null) {
                    action = javaMethodImpl.getOperation().getOperation().getInput().getAction();
                }
                if (action == null) continue;
                ActionBasedOperationSignature opSignature = new ActionBasedOperationSignature(action, payloadName);
                if (this.uniqueOpSignatureMap.get(opSignature) != null) {
                    LOGGER.warning(AddressingMessages.NON_UNIQUE_OPERATION_SIGNATURE(this.uniqueOpSignatureMap.get(opSignature), javaMethodImpl.getOperationQName(), action, payloadName));
                }
                this.uniqueOpSignatureMap.put(opSignature, this.wsdlOperationMapping(javaMethodImpl));
                this.actionMap.put(action, this.wsdlOperationMapping(javaMethodImpl));
            }
        } else {
            for (WSDLBoundOperation wSDLBoundOperation : wsdlModel.getBinding().getBindingOperations()) {
                String action;
                ActionBasedOperationSignature opSignature;
                QName payloadName = wSDLBoundOperation.getRequestPayloadName();
                if (payloadName == null) {
                    payloadName = PayloadQNameBasedOperationFinder.EMPTY_PAYLOAD;
                }
                if (this.uniqueOpSignatureMap.get(opSignature = new ActionBasedOperationSignature(action = wSDLBoundOperation.getOperation().getInput().getAction(), payloadName)) != null) {
                    LOGGER.warning(AddressingMessages.NON_UNIQUE_OPERATION_SIGNATURE(this.uniqueOpSignatureMap.get(opSignature), wSDLBoundOperation.getName(), action, payloadName));
                }
                this.uniqueOpSignatureMap.put(opSignature, this.wsdlOperationMapping(wSDLBoundOperation));
                this.actionMap.put(action, this.wsdlOperationMapping(wSDLBoundOperation));
            }
        }
    }

    @Override
    public WSDLOperationMapping getWSDLOperationMapping(Packet request) throws DispatchException {
        QName payloadName;
        MessageHeaders hl = request.getMessage().getHeaders();
        String action = AddressingUtils.getAction(hl, this.av, this.binding.getSOAPVersion());
        if (action == null) {
            return null;
        }
        Message message = request.getMessage();
        String localPart = message.getPayloadLocalPart();
        if (localPart == null) {
            payloadName = PayloadQNameBasedOperationFinder.EMPTY_PAYLOAD;
        } else {
            String nsUri = message.getPayloadNamespaceURI();
            if (nsUri == null) {
                nsUri = "";
            }
            payloadName = new QName(nsUri, localPart);
        }
        WSDLOperationMapping opMapping = this.uniqueOpSignatureMap.get(new ActionBasedOperationSignature(action, payloadName));
        if (opMapping != null) {
            return opMapping;
        }
        opMapping = this.actionMap.get(action);
        if (opMapping != null) {
            return opMapping;
        }
        Message result = Messages.create(action, this.av, this.binding.getSOAPVersion());
        throw new DispatchException(result);
    }
}

