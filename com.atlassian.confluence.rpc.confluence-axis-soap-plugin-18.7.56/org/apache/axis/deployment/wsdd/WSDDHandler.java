/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.deployment.wsdd;

import java.io.IOException;
import javax.xml.namespace.QName;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.deployment.wsdd.WSDDDeployableItem;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDException;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.utils.Messages;
import org.w3c.dom.Element;
import org.xml.sax.helpers.AttributesImpl;

public class WSDDHandler
extends WSDDDeployableItem {
    static /* synthetic */ Class class$org$apache$axis$deployment$wsdd$WSDDHandler;

    public WSDDHandler() {
    }

    public WSDDHandler(Element e) throws WSDDException {
        super(e);
        if (this.type == null && this.getClass() == (class$org$apache$axis$deployment$wsdd$WSDDHandler == null ? (class$org$apache$axis$deployment$wsdd$WSDDHandler = WSDDHandler.class$("org.apache.axis.deployment.wsdd.WSDDHandler")) : class$org$apache$axis$deployment$wsdd$WSDDHandler)) {
            throw new WSDDException(Messages.getMessage("noTypeAttr00"));
        }
    }

    protected QName getElementName() {
        return QNAME_HANDLER;
    }

    public void writeToContext(SerializationContext context) throws IOException {
        AttributesImpl attrs = new AttributesImpl();
        QName name = this.getQName();
        if (name != null) {
            attrs.addAttribute("", "name", "name", "CDATA", context.qName2String(name));
        }
        attrs.addAttribute("", "type", "type", "CDATA", context.qName2String(this.getType()));
        context.startElement(WSDDConstants.QNAME_HANDLER, attrs);
        this.writeParamsToContext(context);
        context.endElement();
    }

    public void deployToRegistry(WSDDDeployment deployment) {
        deployment.addHandler(this);
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

