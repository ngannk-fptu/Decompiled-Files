/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.encoding;

import java.io.StringWriter;
import java.util.HashSet;
import java.util.Vector;
import javax.xml.namespace.QName;
import org.apache.axis.Constants;
import org.apache.axis.Part;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.Callback;
import org.apache.axis.encoding.CallbackTarget;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Target;
import org.apache.axis.message.EnvelopeHandler;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.SAX2EventRecorder;
import org.apache.axis.message.SAXOutputter;
import org.apache.axis.message.SOAPHandler;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class DeserializerImpl
extends SOAPHandler
implements javax.xml.rpc.encoding.Deserializer,
Deserializer,
Callback {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$encoding$DeserializerImpl == null ? (class$org$apache$axis$encoding$DeserializerImpl = DeserializerImpl.class$("org.apache.axis.encoding.DeserializerImpl")) : class$org$apache$axis$encoding$DeserializerImpl).getName());
    protected Object value = null;
    private final boolean debugEnabled = log.isDebugEnabled();
    protected boolean isEnded = false;
    protected Vector targets = null;
    protected QName defaultType = null;
    protected boolean componentsReadyFlag = false;
    private HashSet activeDeserializers = new HashSet();
    protected boolean isHref = false;
    protected boolean isNil = false;
    protected String id = null;
    static /* synthetic */ Class class$org$apache$axis$encoding$DeserializerImpl;

    public String getMechanismType() {
        return "Axis SAX Mechanism";
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue(Object hint) {
        return null;
    }

    public void setChildValue(Object value, Object hint) throws SAXException {
    }

    public void setValue(Object value, Object hint) throws SAXException {
        if (hint instanceof Deserializer) {
            this.activeDeserializers.remove(hint);
            if (this.componentsReady()) {
                this.valueComplete();
            }
        }
    }

    public void setDefaultType(QName qName) {
        this.defaultType = qName;
    }

    public QName getDefaultType() {
        return this.defaultType;
    }

    public void registerValueTarget(Target target) {
        if (this.targets == null) {
            this.targets = new Vector();
        }
        this.targets.addElement(target);
    }

    public Vector getValueTargets() {
        return this.targets;
    }

    public void removeValueTargets() {
        if (this.targets != null) {
            this.targets = null;
        }
    }

    public void moveValueTargets(Deserializer other) {
        if (other == null || other.getValueTargets() == null) {
            return;
        }
        if (this.targets == null) {
            this.targets = new Vector();
        }
        this.targets.addAll(other.getValueTargets());
        other.removeValueTargets();
    }

    public boolean componentsReady() {
        return this.componentsReadyFlag || !this.isHref && this.isEnded && this.activeDeserializers.isEmpty();
    }

    public void valueComplete() throws SAXException {
        if (this.componentsReady() && this.targets != null) {
            for (int i = 0; i < this.targets.size(); ++i) {
                Target target = (Target)this.targets.get(i);
                target.set(this.value);
                if (!this.debugEnabled) continue;
                log.debug((Object)Messages.getMessage("setValueInTarget00", "" + this.value, "" + target));
            }
            this.removeValueTargets();
        }
    }

    public void addChildDeserializer(Deserializer dSer) {
        if (this.activeDeserializers != null) {
            this.activeDeserializers.add(dSer);
        }
        dSer.registerValueTarget(new CallbackTarget(this, dSer));
    }

    public void startElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        String href;
        super.startElement(namespace, localName, prefix, attributes, context);
        if (context.isNil(attributes)) {
            this.value = null;
            this.isNil = true;
            return;
        }
        SOAPConstants soapConstants = context.getSOAPConstants();
        this.id = attributes.getValue("id");
        if (this.id != null) {
            context.addObjectById(this.id, this.value);
            if (this.debugEnabled) {
                log.debug((Object)Messages.getMessage("deserInitPutValueDebug00", "" + this.value, this.id));
            }
            context.registerFixup("#" + this.id, this);
        }
        if ((href = attributes.getValue(soapConstants.getAttrHref())) != null) {
            this.isHref = true;
            Object ref = context.getObjectByRef(href);
            if (this.debugEnabled) {
                log.debug((Object)Messages.getMessage("gotForID00", new String[]{"" + ref, href, ref == null ? "*null*" : ref.getClass().toString()}));
            }
            if (ref == null) {
                context.registerFixup(href, this);
                return;
            }
            if (ref instanceof MessageElement) {
                context.replaceElementHandler(new EnvelopeHandler(this));
                SAX2EventRecorder r = context.getRecorder();
                context.setRecorder(null);
                ((MessageElement)ref).publishToHandler(context);
                context.setRecorder(r);
            } else {
                Deserializer dser;
                if (!href.startsWith("#") && this.defaultType != null && ref instanceof Part && null != (dser = context.getDeserializerForType(this.defaultType))) {
                    dser.startElement(namespace, localName, prefix, attributes, context);
                    ref = dser.getValue();
                }
                this.value = ref;
                this.componentsReadyFlag = true;
                this.valueComplete();
            }
        } else {
            this.isHref = false;
            this.onStartElement(namespace, localName, prefix, attributes, context);
        }
    }

    public void onStartElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        if (this.getClass().equals(class$org$apache$axis$encoding$DeserializerImpl == null ? (class$org$apache$axis$encoding$DeserializerImpl = DeserializerImpl.class$("org.apache.axis.encoding.DeserializerImpl")) : class$org$apache$axis$encoding$DeserializerImpl)) {
            QName type = context.getTypeFromAttributes(namespace, localName, attributes);
            if (type == null && (type = this.defaultType) == null) {
                type = Constants.XSD_STRING;
            }
            if (this.debugEnabled) {
                log.debug((Object)Messages.getMessage("gotType00", "Deser", "" + type));
            }
            if (type != null) {
                Deserializer dser = context.getDeserializerForType(type);
                if (dser == null) {
                    dser = context.getDeserializerForClass(null);
                }
                if (dser != null) {
                    dser.moveValueTargets(this);
                    context.replaceElementHandler((SOAPHandler)((Object)dser));
                    boolean isRef = context.isProcessingRef();
                    context.setProcessingRef(true);
                    dser.startElement(namespace, localName, prefix, attributes, context);
                    context.setProcessingRef(isRef);
                } else {
                    throw new SAXException(Messages.getMessage("noDeser00", "" + type));
                }
            }
        }
    }

    public SOAPHandler onStartChild(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        return null;
    }

    public final void endElement(String namespace, String localName, DeserializationContext context) throws SAXException {
        super.endElement(namespace, localName, context);
        this.isEnded = true;
        if (!this.isHref) {
            this.onEndElement(namespace, localName, context);
        }
        if (this.componentsReady()) {
            this.valueComplete();
        }
        if (this.id != null) {
            context.addObjectById(this.id, this.value);
            if (this.debugEnabled) {
                log.debug((Object)Messages.getMessage("deserPutValueDebug00", "" + this.value, this.id));
            }
        }
    }

    public void onEndElement(String namespace, String localName, DeserializationContext context) throws SAXException {
        if (this.getClass().equals(class$org$apache$axis$encoding$DeserializerImpl == null ? (class$org$apache$axis$encoding$DeserializerImpl = DeserializerImpl.class$("org.apache.axis.encoding.DeserializerImpl")) : class$org$apache$axis$encoding$DeserializerImpl) && this.targets != null && !this.targets.isEmpty()) {
            StringWriter writer = new StringWriter();
            SerializationContext serContext = new SerializationContext(writer, context.getMessageContext());
            serContext.setSendDecl(false);
            SAXOutputter so = null;
            so = new SAXOutputter(serContext);
            context.getCurElement().publishContents(so);
            if (!this.isNil) {
                this.value = writer.getBuffer().toString();
            }
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

