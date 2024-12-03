/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.wsdl;

import com.sun.istack.Nullable;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.WSDLOperationMapping;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.fault.SOAPFaultBuilder;
import com.sun.xml.ws.model.AbstractSEIModelImpl;
import com.sun.xml.ws.model.JavaMethodImpl;
import com.sun.xml.ws.resources.ServerMessages;
import com.sun.xml.ws.util.QNameMap;
import com.sun.xml.ws.wsdl.DispatchException;
import com.sun.xml.ws.wsdl.WSDLOperationFinder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.namespace.QName;

final class PayloadQNameBasedOperationFinder
extends WSDLOperationFinder {
    private static final Logger LOGGER = Logger.getLogger(PayloadQNameBasedOperationFinder.class.getName());
    public static final String EMPTY_PAYLOAD_LOCAL = "";
    public static final String EMPTY_PAYLOAD_NSURI = "";
    public static final QName EMPTY_PAYLOAD = new QName("", "");
    private final QNameMap<WSDLOperationMapping> methodHandlers = new QNameMap();
    private final QNameMap<List<String>> unique = new QNameMap();

    public PayloadQNameBasedOperationFinder(WSDLPort wsdlModel, WSBinding binding, @Nullable SEIModel seiModel) {
        super(wsdlModel, binding, seiModel);
        if (seiModel != null) {
            QName name;
            for (JavaMethodImpl javaMethodImpl : ((AbstractSEIModelImpl)seiModel).getJavaMethods()) {
                List<String> methods;
                if (javaMethodImpl.getMEP().isAsync) continue;
                name = javaMethodImpl.getRequestPayloadName();
                if (name == null) {
                    name = EMPTY_PAYLOAD;
                }
                if ((methods = this.unique.get(name)) == null) {
                    methods = new ArrayList<String>();
                    this.unique.put(name, methods);
                }
                methods.add(javaMethodImpl.getMethod().getName());
            }
            for (QNameMap.Entry entry : this.unique.entrySet()) {
                if (((List)entry.getValue()).size() <= 1) continue;
                LOGGER.warning(ServerMessages.NON_UNIQUE_DISPATCH_QNAME(entry.getValue(), entry.createQName()));
            }
            for (JavaMethodImpl javaMethodImpl : ((AbstractSEIModelImpl)seiModel).getJavaMethods()) {
                name = javaMethodImpl.getRequestPayloadName();
                if (name == null) {
                    name = EMPTY_PAYLOAD;
                }
                if (this.unique.get(name).size() != 1) continue;
                this.methodHandlers.put(name, this.wsdlOperationMapping(javaMethodImpl));
            }
        } else {
            for (WSDLBoundOperation wSDLBoundOperation : wsdlModel.getBinding().getBindingOperations()) {
                QName name = wSDLBoundOperation.getRequestPayloadName();
                if (name == null) {
                    name = EMPTY_PAYLOAD;
                }
                this.methodHandlers.put(name, this.wsdlOperationMapping(wSDLBoundOperation));
            }
        }
    }

    @Override
    public WSDLOperationMapping getWSDLOperationMapping(Packet request) throws DispatchException {
        String nsUri;
        Message message = request.getMessage();
        String localPart = message.getPayloadLocalPart();
        if (localPart == null) {
            localPart = "";
            nsUri = "";
        } else {
            nsUri = message.getPayloadNamespaceURI();
            if (nsUri == null) {
                nsUri = "";
            }
        }
        WSDLOperationMapping op = this.methodHandlers.get(nsUri, localPart);
        if (op == null && !this.unique.containsKey(nsUri, localPart)) {
            String dispatchKey = "{" + nsUri + "}" + localPart;
            String faultString = ServerMessages.DISPATCH_CANNOT_FIND_METHOD(dispatchKey);
            throw new DispatchException(SOAPFaultBuilder.createSOAPFaultMessage(this.binding.getSOAPVersion(), faultString, this.binding.getSOAPVersion().faultCodeClient));
        }
        return op;
    }
}

