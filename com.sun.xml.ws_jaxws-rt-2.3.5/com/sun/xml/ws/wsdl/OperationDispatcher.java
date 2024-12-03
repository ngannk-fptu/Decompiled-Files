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
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.WSDLOperationMapping;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.fault.SOAPFaultBuilder;
import com.sun.xml.ws.resources.ServerMessages;
import com.sun.xml.ws.wsdl.ActionBasedOperationFinder;
import com.sun.xml.ws.wsdl.DispatchException;
import com.sun.xml.ws.wsdl.PayloadQNameBasedOperationFinder;
import com.sun.xml.ws.wsdl.SOAPActionBasedOperationFinder;
import com.sun.xml.ws.wsdl.WSDLOperationFinder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;

public class OperationDispatcher {
    private List<WSDLOperationFinder> opFinders;
    private WSBinding binding;

    public OperationDispatcher(@NotNull WSDLPort wsdlModel, @NotNull WSBinding binding, @Nullable SEIModel seiModel) {
        this.binding = binding;
        this.opFinders = new ArrayList<WSDLOperationFinder>();
        if (binding.getAddressingVersion() != null) {
            this.opFinders.add(new ActionBasedOperationFinder(wsdlModel, binding, seiModel));
        }
        this.opFinders.add(new PayloadQNameBasedOperationFinder(wsdlModel, binding, seiModel));
        this.opFinders.add(new SOAPActionBasedOperationFinder(wsdlModel, binding, seiModel));
    }

    @NotNull
    public QName getWSDLOperationQName(Packet request) throws DispatchException {
        WSDLOperationMapping m = this.getWSDLOperationMapping(request);
        return m != null ? m.getOperationName() : null;
    }

    @NotNull
    public WSDLOperationMapping getWSDLOperationMapping(Packet request) throws DispatchException {
        for (WSDLOperationFinder finder : this.opFinders) {
            WSDLOperationMapping opName = finder.getWSDLOperationMapping(request);
            if (opName == null) continue;
            return opName;
        }
        String err = MessageFormat.format("Request=[SOAPAction={0},Payload='{'{1}'}'{2}]", request.soapAction, request.getMessage().getPayloadNamespaceURI(), request.getMessage().getPayloadLocalPart());
        String faultString = ServerMessages.DISPATCH_CANNOT_FIND_METHOD(err);
        Message faultMsg = SOAPFaultBuilder.createSOAPFaultMessage(this.binding.getSOAPVersion(), faultString, this.binding.getSOAPVersion().faultCodeClient);
        throw new DispatchException(faultMsg);
    }
}

