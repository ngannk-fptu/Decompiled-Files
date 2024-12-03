/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.encoding.ser;

import java.util.Vector;
import javax.xml.namespace.QName;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerImpl;
import org.apache.axis.encoding.DeserializerTarget;
import org.apache.axis.message.SOAPHandler;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class VectorDeserializer
extends DeserializerImpl {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$encoding$ser$VectorDeserializer == null ? (class$org$apache$axis$encoding$ser$VectorDeserializer = VectorDeserializer.class$("org.apache.axis.encoding.ser.VectorDeserializer")) : class$org$apache$axis$encoding$ser$VectorDeserializer).getName());
    public int curIndex = 0;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$VectorDeserializer;

    public void onStartElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: VectorDeserializer::startElement()");
        }
        if (context.isNil(attributes)) {
            return;
        }
        this.setValue(new Vector());
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: VectorDeserializer::startElement()");
        }
    }

    public SOAPHandler onStartChild(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: VectorDeserializer::onStartChild()");
        }
        if (attributes == null) {
            throw new SAXException(Messages.getMessage("noType01"));
        }
        if (context.isNil(attributes)) {
            this.setChildValue(null, new Integer(this.curIndex++));
            return null;
        }
        QName itemType = context.getTypeFromAttributes(namespace, localName, attributes);
        Deserializer dSer = null;
        if (itemType != null) {
            dSer = context.getDeserializerForType(itemType);
        }
        if (dSer == null) {
            dSer = new DeserializerImpl();
        }
        dSer.registerValueTarget(new DeserializerTarget(this, new Integer(this.curIndex)));
        ++this.curIndex;
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: VectorDeserializer::onStartChild()");
        }
        this.addChildDeserializer(dSer);
        return (SOAPHandler)((Object)dSer);
    }

    public void setChildValue(Object value, Object hint) throws SAXException {
        Vector v;
        int offset;
        if (log.isDebugEnabled()) {
            log.debug((Object)Messages.getMessage("gotValue00", "VectorDeserializer", "" + value));
        }
        if ((offset = ((Integer)hint).intValue()) >= (v = (Vector)this.value).size()) {
            v.setSize(offset + 1);
        }
        v.setElementAt(value, offset);
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

