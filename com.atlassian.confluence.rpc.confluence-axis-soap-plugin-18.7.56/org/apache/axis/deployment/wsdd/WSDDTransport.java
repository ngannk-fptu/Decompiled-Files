/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.deployment.wsdd;

import java.io.IOException;
import javax.xml.namespace.QName;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDException;
import org.apache.axis.deployment.wsdd.WSDDTargetedChain;
import org.apache.axis.encoding.SerializationContext;
import org.w3c.dom.Element;
import org.xml.sax.helpers.AttributesImpl;

public class WSDDTransport
extends WSDDTargetedChain {
    public WSDDTransport() {
    }

    public WSDDTransport(Element e) throws WSDDException {
        super(e);
    }

    protected QName getElementName() {
        return WSDDConstants.QNAME_TRANSPORT;
    }

    public void writeToContext(SerializationContext context) throws IOException {
        AttributesImpl attrs = new AttributesImpl();
        QName name = this.getQName();
        if (name != null) {
            attrs.addAttribute("", "name", "name", "CDATA", context.qName2String(name));
        }
        if ((name = this.getPivotQName()) != null) {
            attrs.addAttribute("", "pivot", "pivot", "CDATA", context.qName2String(name));
        }
        context.startElement(WSDDConstants.QNAME_TRANSPORT, attrs);
        this.writeFlowsToContext(context);
        this.writeParamsToContext(context);
        context.endElement();
    }

    public void deployToRegistry(WSDDDeployment registry) {
        registry.addTransport(this);
        super.deployToRegistry(registry);
    }
}

