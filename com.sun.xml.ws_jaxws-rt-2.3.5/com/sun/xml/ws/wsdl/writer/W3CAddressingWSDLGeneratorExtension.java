/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.txw2.TypedXmlWriter
 *  javax.xml.ws.Action
 *  javax.xml.ws.FaultAction
 *  javax.xml.ws.soap.AddressingFeature
 */
package com.sun.xml.ws.wsdl.writer;

import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.model.CheckedException;
import com.sun.xml.ws.api.model.JavaMethod;
import com.sun.xml.ws.api.wsdl.writer.WSDLGenExtnContext;
import com.sun.xml.ws.api.wsdl.writer.WSDLGeneratorExtension;
import com.sun.xml.ws.wsdl.writer.UsingAddressing;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;
import javax.xml.ws.Action;
import javax.xml.ws.FaultAction;
import javax.xml.ws.soap.AddressingFeature;

public class W3CAddressingWSDLGeneratorExtension
extends WSDLGeneratorExtension {
    private boolean enabled;
    private boolean required = false;
    private static final Logger LOGGER = Logger.getLogger(W3CAddressingWSDLGeneratorExtension.class.getName());

    @Override
    public void start(WSDLGenExtnContext ctxt) {
        WSBinding binding = ctxt.getBinding();
        TypedXmlWriter root = ctxt.getRoot();
        this.enabled = binding.isFeatureEnabled(AddressingFeature.class);
        if (!this.enabled) {
            return;
        }
        AddressingFeature ftr = binding.getFeature(AddressingFeature.class);
        this.required = ftr.isRequired();
        root._namespace(AddressingVersion.W3C.wsdlNsUri, AddressingVersion.W3C.getWsdlPrefix());
    }

    @Override
    public void addOperationInputExtension(TypedXmlWriter input, JavaMethod method) {
        if (!this.enabled) {
            return;
        }
        Action a = method.getSEIMethod().getAnnotation(Action.class);
        if (a != null && !a.input().equals("")) {
            this.addAttribute(input, a.input());
        } else {
            String soapAction = method.getBinding().getSOAPAction();
            if (soapAction == null || soapAction.equals("")) {
                String defaultAction = W3CAddressingWSDLGeneratorExtension.getDefaultAction(method);
                this.addAttribute(input, defaultAction);
            }
        }
    }

    protected static final String getDefaultAction(JavaMethod method) {
        String tns = method.getOwner().getTargetNamespace();
        String delim = "/";
        try {
            URI uri = new URI(tns);
            if (uri.getScheme().equalsIgnoreCase("urn")) {
                delim = ":";
            }
        }
        catch (URISyntaxException e) {
            LOGGER.warning("TargetNamespace of WebService is not a valid URI");
        }
        if (tns.endsWith(delim)) {
            tns = tns.substring(0, tns.length() - 1);
        }
        String name = method.getMEP().isOneWay() ? method.getOperationName() : method.getOperationName() + "Request";
        return tns + delim + method.getOwner().getPortTypeName().getLocalPart() + delim + name;
    }

    @Override
    public void addOperationOutputExtension(TypedXmlWriter output, JavaMethod method) {
        if (!this.enabled) {
            return;
        }
        Action a = method.getSEIMethod().getAnnotation(Action.class);
        if (a != null && !a.output().equals("")) {
            this.addAttribute(output, a.output());
        }
    }

    @Override
    public void addOperationFaultExtension(TypedXmlWriter fault, JavaMethod method, CheckedException ce) {
        if (!this.enabled) {
            return;
        }
        Action a = method.getSEIMethod().getAnnotation(Action.class);
        Class<?>[] exs = method.getSEIMethod().getExceptionTypes();
        if (exs == null) {
            return;
        }
        if (a != null && a.fault() != null) {
            for (FaultAction fa : a.fault()) {
                if (!fa.className().getName().equals(ce.getExceptionClass().getName())) continue;
                if (fa.value().equals("")) {
                    return;
                }
                this.addAttribute(fault, fa.value());
                return;
            }
        }
    }

    private void addAttribute(TypedXmlWriter writer, String attrValue) {
        writer._attribute(AddressingVersion.W3C.wsdlActionTag, (Object)attrValue);
    }

    @Override
    public void addBindingExtension(TypedXmlWriter binding) {
        if (!this.enabled) {
            return;
        }
        binding._element(AddressingVersion.W3C.wsdlExtensionTag, UsingAddressing.class);
    }
}

