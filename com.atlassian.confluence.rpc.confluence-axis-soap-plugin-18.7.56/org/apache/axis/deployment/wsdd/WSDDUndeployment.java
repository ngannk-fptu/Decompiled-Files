/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.deployment.wsdd;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import javax.xml.namespace.QName;
import org.apache.axis.ConfigurationException;
import org.apache.axis.MessageContext;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDElement;
import org.apache.axis.deployment.wsdd.WSDDException;
import org.apache.axis.deployment.wsdd.WSDDTypeMapping;
import org.apache.axis.deployment.wsdd.WSDDTypeMappingContainer;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.utils.Messages;
import org.w3c.dom.Element;
import org.xml.sax.helpers.AttributesImpl;

public class WSDDUndeployment
extends WSDDElement
implements WSDDTypeMappingContainer {
    private Vector handlers = new Vector();
    private Vector chains = new Vector();
    private Vector services = new Vector();
    private Vector transports = new Vector();
    private Vector typeMappings = new Vector();

    public void addHandler(QName handler) {
        this.handlers.add(handler);
    }

    public void addChain(QName chain) {
        this.chains.add(chain);
    }

    public void addTransport(QName transport) {
        this.transports.add(transport);
    }

    public void addService(QName service) {
        this.services.add(service);
    }

    public void deployTypeMapping(WSDDTypeMapping typeMapping) throws WSDDException {
        this.typeMappings.add(typeMapping);
    }

    public WSDDUndeployment() {
    }

    private QName getQName(Element el) throws WSDDException {
        String attr = el.getAttribute("name");
        if (attr == null || "".equals(attr)) {
            throw new WSDDException(Messages.getMessage("badNameAttr00"));
        }
        return new QName("", attr);
    }

    public WSDDUndeployment(Element e) throws WSDDException {
        super(e);
        int i;
        Element[] elements = this.getChildElements(e, "handler");
        for (i = 0; i < elements.length; ++i) {
            this.addHandler(this.getQName(elements[i]));
        }
        elements = this.getChildElements(e, "chain");
        for (i = 0; i < elements.length; ++i) {
            this.addChain(this.getQName(elements[i]));
        }
        elements = this.getChildElements(e, "transport");
        for (i = 0; i < elements.length; ++i) {
            this.addTransport(this.getQName(elements[i]));
        }
        elements = this.getChildElements(e, "service");
        for (i = 0; i < elements.length; ++i) {
            this.addService(this.getQName(elements[i]));
        }
    }

    protected QName getElementName() {
        return QNAME_UNDEPLOY;
    }

    public void undeployFromRegistry(WSDDDeployment registry) throws ConfigurationException {
        QName qname;
        int n;
        for (n = 0; n < this.handlers.size(); ++n) {
            qname = (QName)this.handlers.get(n);
            registry.undeployHandler(qname);
        }
        for (n = 0; n < this.chains.size(); ++n) {
            qname = (QName)this.chains.get(n);
            registry.undeployHandler(qname);
        }
        for (n = 0; n < this.transports.size(); ++n) {
            qname = (QName)this.transports.get(n);
            registry.undeployTransport(qname);
        }
        for (n = 0; n < this.services.size(); ++n) {
            qname = (QName)this.services.get(n);
            try {
                SOAPService service;
                String sname = qname.getLocalPart();
                MessageContext messageContext = MessageContext.getCurrentContext();
                if (messageContext != null && (service = messageContext.getAxisEngine().getService(sname)) != null) {
                    service.clearSessions();
                }
            }
            catch (Exception exp) {
                throw new ConfigurationException(exp);
            }
            registry.undeployService(qname);
        }
    }

    private void writeElement(SerializationContext context, QName elementQName, QName qname) throws IOException {
        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("", "name", "name", "CDATA", context.qName2String(qname));
        context.startElement(elementQName, attrs);
        context.endElement();
    }

    public void writeToContext(SerializationContext context) throws IOException {
        QName qname;
        context.registerPrefixForURI("", "http://xml.apache.org/axis/wsdd/");
        context.startElement(WSDDConstants.QNAME_UNDEPLOY, null);
        Iterator i = this.handlers.iterator();
        while (i.hasNext()) {
            qname = (QName)i.next();
            this.writeElement(context, QNAME_HANDLER, qname);
        }
        i = this.chains.iterator();
        while (i.hasNext()) {
            qname = (QName)i.next();
            this.writeElement(context, QNAME_CHAIN, qname);
        }
        i = this.services.iterator();
        while (i.hasNext()) {
            qname = (QName)i.next();
            this.writeElement(context, QNAME_SERVICE, qname);
        }
        i = this.transports.iterator();
        while (i.hasNext()) {
            qname = (QName)i.next();
            this.writeElement(context, QNAME_TRANSPORT, qname);
        }
        i = this.typeMappings.iterator();
        while (i.hasNext()) {
            WSDDTypeMapping mapping = (WSDDTypeMapping)i.next();
            mapping.writeToContext(context);
        }
        context.endElement();
    }

    public WSDDTypeMapping[] getTypeMappings() {
        WSDDTypeMapping[] t = new WSDDTypeMapping[this.typeMappings.size()];
        this.typeMappings.toArray(t);
        return t;
    }
}

