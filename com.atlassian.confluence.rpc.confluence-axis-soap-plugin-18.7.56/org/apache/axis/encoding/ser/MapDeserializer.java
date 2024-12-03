/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.encoding.ser;

import java.util.HashMap;
import java.util.Map;
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

public class MapDeserializer
extends DeserializerImpl {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$encoding$ser$MapDeserializer == null ? (class$org$apache$axis$encoding$ser$MapDeserializer = MapDeserializer.class$("org.apache.axis.encoding.ser.MapDeserializer")) : class$org$apache$axis$encoding$ser$MapDeserializer).getName());
    public static final Object KEYHINT = new Object();
    public static final Object VALHINT = new Object();
    public static final Object NILHINT = new Object();
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$MapDeserializer;

    public void onStartElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter MapDeserializer::startElement()");
        }
        if (context.isNil(attributes)) {
            return;
        }
        this.setValue(new HashMap());
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: MapDeserializer::startElement()");
        }
    }

    public SOAPHandler onStartChild(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: MapDeserializer::onStartChild()");
        }
        if (localName.equals("item")) {
            ItemHandler handler = new ItemHandler(this);
            this.addChildDeserializer(handler);
            if (log.isDebugEnabled()) {
                log.debug((Object)"Exit: MapDeserializer::onStartChild()");
            }
            return handler;
        }
        return this;
    }

    public void setChildValue(Object value, Object hint) throws SAXException {
        if (log.isDebugEnabled()) {
            log.debug((Object)Messages.getMessage("gotValue00", "MapDeserializer", "" + value));
        }
        ((Map)this.value).put(hint, value);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    class ItemHandler
    extends DeserializerImpl {
        Object key;
        Object myValue;
        int numSet = 0;
        MapDeserializer md = null;

        ItemHandler(MapDeserializer md) {
            this.md = md;
        }

        public void setChildValue(Object val, Object hint) throws SAXException {
            if (hint == KEYHINT) {
                this.key = val;
            } else if (hint == VALHINT) {
                this.myValue = val;
            } else if (hint != NILHINT) {
                return;
            }
            ++this.numSet;
            if (this.numSet == 2) {
                this.md.setChildValue(this.myValue, this.key);
            }
        }

        public SOAPHandler onStartChild(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
            QName typeQName = context.getTypeFromAttributes(namespace, localName, attributes);
            Deserializer dser = context.getDeserializerForType(typeQName);
            if (dser == null) {
                dser = new DeserializerImpl();
            }
            DeserializerTarget dt = null;
            if (context.isNil(attributes)) {
                dt = new DeserializerTarget(this, NILHINT);
            } else if (localName.equals("key")) {
                dt = new DeserializerTarget(this, KEYHINT);
            } else if (localName.equals("value")) {
                dt = new DeserializerTarget(this, VALHINT);
            }
            if (dt != null) {
                dser.registerValueTarget(dt);
            }
            this.addChildDeserializer(dser);
            return (SOAPHandler)((Object)dser);
        }
    }
}

