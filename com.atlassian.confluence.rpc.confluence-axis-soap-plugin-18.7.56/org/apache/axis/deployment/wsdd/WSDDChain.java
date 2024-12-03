/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.deployment.wsdd;

import java.io.IOException;
import java.util.Vector;
import javax.xml.namespace.QName;
import org.apache.axis.ConfigurationException;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.SimpleChain;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDException;
import org.apache.axis.deployment.wsdd.WSDDHandler;
import org.apache.axis.encoding.SerializationContext;
import org.w3c.dom.Element;
import org.xml.sax.helpers.AttributesImpl;

public class WSDDChain
extends WSDDHandler {
    private Vector handlers = new Vector();

    public WSDDChain() {
    }

    public WSDDChain(Element e) throws WSDDException {
        super(e);
        int i;
        if (this.type != null) {
            return;
        }
        Element[] elements = this.getChildElements(e, "handler");
        if (elements.length != 0) {
            for (i = 0; i < elements.length; ++i) {
                WSDDHandler handler = new WSDDHandler(elements[i]);
                this.addHandler(handler);
            }
        }
        if ((elements = this.getChildElements(e, "chain")).length != 0) {
            for (i = 0; i < elements.length; ++i) {
                WSDDChain chain = new WSDDChain(elements[i]);
                this.addHandler(chain);
            }
        }
    }

    protected QName getElementName() {
        return WSDDConstants.QNAME_CHAIN;
    }

    public void addHandler(WSDDHandler handler) {
        this.handlers.add(handler);
    }

    public Vector getHandlers() {
        return this.handlers;
    }

    public void removeHandler(WSDDHandler victim) {
        this.handlers.remove(victim);
    }

    public Handler makeNewInstance(EngineConfiguration registry) throws ConfigurationException {
        SimpleChain c = new SimpleChain();
        for (int n = 0; n < this.handlers.size(); ++n) {
            WSDDHandler handler = (WSDDHandler)this.handlers.get(n);
            Handler h = handler.getInstance(registry);
            if (h == null) {
                throw new ConfigurationException("Can't find handler name:'" + handler.getQName() + "' type:'" + handler.getType() + "' in the registry");
            }
            c.addHandler(h);
        }
        return c;
    }

    public void writeToContext(SerializationContext context) throws IOException {
        AttributesImpl attrs = new AttributesImpl();
        QName name = this.getQName();
        if (name != null) {
            attrs.addAttribute("", "name", "name", "CDATA", context.qName2String(name));
        }
        if (this.getType() != null) {
            attrs.addAttribute("", "type", "type", "CDATA", context.qName2String(this.getType()));
        }
        context.startElement(this.getElementName(), attrs);
        for (int n = 0; n < this.handlers.size(); ++n) {
            WSDDHandler handler = (WSDDHandler)this.handlers.get(n);
            handler.writeToContext(context);
        }
        context.endElement();
    }

    public void deployToRegistry(WSDDDeployment registry) {
        if (this.getQName() != null) {
            registry.addHandler(this);
        }
        for (int n = 0; n < this.handlers.size(); ++n) {
            WSDDHandler handler = (WSDDHandler)this.handlers.get(n);
            if (handler.getQName() == null) continue;
            handler.deployToRegistry(registry);
        }
    }
}

