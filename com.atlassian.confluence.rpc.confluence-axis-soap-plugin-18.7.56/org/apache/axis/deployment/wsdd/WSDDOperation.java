/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.deployment.wsdd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.namespace.QName;
import org.apache.axis.deployment.wsdd.WSDDDocumentation;
import org.apache.axis.deployment.wsdd.WSDDElement;
import org.apache.axis.deployment.wsdd.WSDDException;
import org.apache.axis.deployment.wsdd.WSDDFault;
import org.apache.axis.deployment.wsdd.WSDDParameter;
import org.apache.axis.description.FaultDesc;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Element;
import org.xml.sax.helpers.AttributesImpl;

public class WSDDOperation
extends WSDDElement {
    OperationDesc desc = new OperationDesc();

    public WSDDOperation(OperationDesc desc) {
        this.desc = desc;
    }

    public WSDDOperation(Element e, ServiceDesc parent) throws WSDDException {
        super(e);
        String mepString;
        String soapAction;
        String retItemType;
        String retItemQName;
        String retHStr;
        String retTypeStr;
        String retQNameStr;
        this.desc.setParent(parent);
        this.desc.setName(e.getAttribute("name"));
        String qNameStr = e.getAttribute("qname");
        if (qNameStr != null && !qNameStr.equals("")) {
            this.desc.setElementQName(XMLUtils.getQNameFromString(qNameStr, e));
        }
        if ((retQNameStr = e.getAttribute("returnQName")) != null && !retQNameStr.equals("")) {
            this.desc.setReturnQName(XMLUtils.getQNameFromString(retQNameStr, e));
        }
        if ((retTypeStr = e.getAttribute("returnType")) != null && !retTypeStr.equals("")) {
            this.desc.setReturnType(XMLUtils.getQNameFromString(retTypeStr, e));
        }
        if ((retHStr = e.getAttribute("returnHeader")) != null) {
            this.desc.setReturnHeader(JavaUtils.isTrueExplicitly(retHStr));
        }
        if ((retItemQName = e.getAttribute("returnItemQName")) != null && !retItemQName.equals("")) {
            ParameterDesc param = this.desc.getReturnParamDesc();
            param.setItemQName(XMLUtils.getQNameFromString(retItemQName, e));
        }
        if ((retItemType = e.getAttribute("returnItemType")) != null && !retItemType.equals("")) {
            ParameterDesc param = this.desc.getReturnParamDesc();
            param.setItemType(XMLUtils.getQNameFromString(retItemType, e));
        }
        if ((soapAction = e.getAttribute("soapAction")) != null) {
            this.desc.setSoapAction(soapAction);
        }
        if ((mepString = e.getAttribute("mep")) != null) {
            this.desc.setMep(mepString);
        }
        Element[] parameters = this.getChildElements(e, "parameter");
        for (int i = 0; i < parameters.length; ++i) {
            Element paramEl = parameters[i];
            WSDDParameter parameter = new WSDDParameter(paramEl, this.desc);
            this.desc.addParameter(parameter.getParameter());
        }
        Element[] faultElems = this.getChildElements(e, "fault");
        for (int i = 0; i < faultElems.length; ++i) {
            Element faultElem = faultElems[i];
            WSDDFault fault = new WSDDFault(faultElem);
            this.desc.addFault(fault.getFaultDesc());
        }
        Element docElem = this.getChildElement(e, "documentation");
        if (docElem != null) {
            WSDDDocumentation documentation = new WSDDDocumentation(docElem);
            this.desc.setDocumentation(documentation.getValue());
        }
    }

    public void writeToContext(SerializationContext context) throws IOException {
        QName retItemQName;
        AttributesImpl attrs = new AttributesImpl();
        if (this.desc.getReturnQName() != null) {
            attrs.addAttribute("", "returnQName", "returnQName", "CDATA", context.qName2String(this.desc.getReturnQName()));
        }
        if (this.desc.getReturnType() != null) {
            attrs.addAttribute("", "returnType", "returnType", "CDATA", context.qName2String(this.desc.getReturnType()));
        }
        if (this.desc.isReturnHeader()) {
            attrs.addAttribute("", "returnHeader", "returnHeader", "CDATA", "true");
        }
        if (this.desc.getName() != null) {
            attrs.addAttribute("", "name", "name", "CDATA", this.desc.getName());
        }
        if (this.desc.getElementQName() != null) {
            attrs.addAttribute("", "qname", "qname", "CDATA", context.qName2String(this.desc.getElementQName()));
        }
        if ((retItemQName = this.desc.getReturnParamDesc().getItemQName()) != null) {
            attrs.addAttribute("", "returnItemQName", "returnItemQName", "CDATA", context.qName2String(retItemQName));
        }
        if (this.desc.getSoapAction() != null) {
            attrs.addAttribute("", "soapAction", "soapAction", "CDATA", this.desc.getSoapAction());
        }
        context.startElement(this.getElementName(), attrs);
        if (this.desc.getDocumentation() != null) {
            WSDDDocumentation documentation = new WSDDDocumentation(this.desc.getDocumentation());
            documentation.writeToContext(context);
        }
        ArrayList params = this.desc.getParameters();
        Iterator i = params.iterator();
        while (i.hasNext()) {
            ParameterDesc parameterDesc = (ParameterDesc)i.next();
            WSDDParameter p = new WSDDParameter(parameterDesc);
            p.writeToContext(context);
        }
        ArrayList faults = this.desc.getFaults();
        if (faults != null) {
            Iterator i2 = faults.iterator();
            while (i2.hasNext()) {
                FaultDesc faultDesc = (FaultDesc)i2.next();
                WSDDFault f = new WSDDFault(faultDesc);
                f.writeToContext(context);
            }
        }
        context.endElement();
    }

    protected QName getElementName() {
        return QNAME_OPERATION;
    }

    public OperationDesc getOperationDesc() {
        return this.desc;
    }
}

