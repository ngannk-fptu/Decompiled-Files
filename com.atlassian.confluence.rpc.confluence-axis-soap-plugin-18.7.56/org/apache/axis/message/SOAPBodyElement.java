/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.message;

import java.io.InputStream;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import org.apache.axis.AxisFault;
import org.apache.axis.InternalException;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.SOAPBody;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

public class SOAPBodyElement
extends MessageElement
implements javax.xml.soap.SOAPBodyElement {
    private static Log log = LogFactory.getLog((class$org$apache$axis$message$SOAPBodyElement == null ? (class$org$apache$axis$message$SOAPBodyElement = SOAPBodyElement.class$("org.apache.axis.message.SOAPBodyElement")) : class$org$apache$axis$message$SOAPBodyElement).getName());
    static /* synthetic */ Class class$org$apache$axis$message$SOAPBodyElement;

    public SOAPBodyElement(String namespace, String localPart, String prefix, Attributes attributes, DeserializationContext context) throws AxisFault {
        super(namespace, localPart, prefix, attributes, context);
    }

    public SOAPBodyElement(Name name) {
        super(name);
    }

    public SOAPBodyElement(QName qname) {
        super(qname);
    }

    public SOAPBodyElement(QName qname, Object value) {
        super(qname, value);
    }

    public SOAPBodyElement(Element elem) {
        super(elem);
    }

    public SOAPBodyElement() {
    }

    public SOAPBodyElement(InputStream input) {
        super(SOAPBodyElement.getDocumentElement(input));
    }

    public SOAPBodyElement(String namespace, String localPart) {
        super(namespace, localPart);
    }

    private static Element getDocumentElement(InputStream input) {
        try {
            return XMLUtils.newDocument(input).getDocumentElement();
        }
        catch (Exception e) {
            throw new InternalException(e);
        }
    }

    public void setParentElement(SOAPElement parent) throws SOAPException {
        if (parent == null) {
            throw new IllegalArgumentException(Messages.getMessage("nullParent00"));
        }
        if (parent instanceof SOAPEnvelope) {
            log.warn((Object)Messages.getMessage("bodyElementParent"));
            parent = ((SOAPEnvelope)parent).getBody();
        }
        if (!(parent instanceof SOAPBody) && !(parent instanceof RPCElement)) {
            throw new IllegalArgumentException(Messages.getMessage("illegalArgumentException00"));
        }
        super.setParentElement(parent);
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

