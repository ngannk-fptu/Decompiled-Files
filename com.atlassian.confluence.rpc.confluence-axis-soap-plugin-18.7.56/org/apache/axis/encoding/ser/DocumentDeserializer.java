/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.encoding.ser;

import java.util.List;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.DeserializerImpl;
import org.apache.axis.message.MessageElement;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.xml.sax.SAXException;

public class DocumentDeserializer
extends DeserializerImpl {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$encoding$ser$DocumentDeserializer == null ? (class$org$apache$axis$encoding$ser$DocumentDeserializer = DocumentDeserializer.class$("org.apache.axis.encoding.ser.DocumentDeserializer")) : class$org$apache$axis$encoding$ser$DocumentDeserializer).getName());
    public static final String DESERIALIZE_CURRENT_ELEMENT = "DeserializeCurrentElement";
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$DocumentDeserializer;

    public final void onEndElement(String namespace, String localName, DeserializationContext context) throws SAXException {
        try {
            MessageElement msgElem = context.getCurElement();
            if (msgElem != null) {
                MessageContext messageContext = context.getMessageContext();
                Boolean currentElement = (Boolean)messageContext.getProperty(DESERIALIZE_CURRENT_ELEMENT);
                if (currentElement != null && currentElement.booleanValue()) {
                    this.value = msgElem.getAsDocument();
                    messageContext.setProperty(DESERIALIZE_CURRENT_ELEMENT, Boolean.FALSE);
                    return;
                }
                List children = msgElem.getChildren();
                if (children != null && (msgElem = (MessageElement)children.get(0)) != null) {
                    this.value = msgElem.getAsDocument();
                }
            }
        }
        catch (Exception exp) {
            log.error((Object)Messages.getMessage("exception00"), (Throwable)exp);
            throw new SAXException(exp);
        }
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

