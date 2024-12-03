/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.wsdl;

import com.sun.istack.Nullable;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.WSDLOperationMapping;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.model.AbstractSEIModelImpl;
import com.sun.xml.ws.model.JavaMethodImpl;
import com.sun.xml.ws.wsdl.DispatchException;
import com.sun.xml.ws.wsdl.WSDLOperationFinder;
import java.util.HashMap;
import java.util.Map;

final class SOAPActionBasedOperationFinder
extends WSDLOperationFinder {
    private final Map<String, WSDLOperationMapping> methodHandlers = new HashMap<String, WSDLOperationMapping>();

    public SOAPActionBasedOperationFinder(WSDLPort wsdlModel, WSBinding binding, @Nullable SEIModel seiModel) {
        super(wsdlModel, binding, seiModel);
        HashMap<String, Integer> unique = new HashMap<String, Integer>();
        if (seiModel != null) {
            String soapAction;
            for (JavaMethodImpl javaMethodImpl : ((AbstractSEIModelImpl)seiModel).getJavaMethods()) {
                soapAction = javaMethodImpl.getSOAPAction();
                Integer count = (Integer)unique.get(soapAction);
                if (count == null) {
                    unique.put(soapAction, 1);
                    continue;
                }
                count = count + 1;
                unique.put(soapAction, count);
            }
            for (JavaMethodImpl javaMethodImpl : ((AbstractSEIModelImpl)seiModel).getJavaMethods()) {
                soapAction = javaMethodImpl.getSOAPAction();
                if ((Integer)unique.get(soapAction) != 1) continue;
                this.methodHandlers.put('\"' + soapAction + '\"', this.wsdlOperationMapping(javaMethodImpl));
            }
        } else {
            for (WSDLBoundOperation wSDLBoundOperation : wsdlModel.getBinding().getBindingOperations()) {
                this.methodHandlers.put(wSDLBoundOperation.getSOAPAction(), this.wsdlOperationMapping(wSDLBoundOperation));
            }
        }
    }

    @Override
    public WSDLOperationMapping getWSDLOperationMapping(Packet request) throws DispatchException {
        return request.soapAction == null ? null : this.methodHandlers.get(request.soapAction);
    }
}

