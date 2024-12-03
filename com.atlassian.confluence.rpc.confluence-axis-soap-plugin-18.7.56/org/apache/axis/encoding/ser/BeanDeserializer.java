/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.encoding.ser;

import java.io.CharArrayWriter;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.axis.Constants;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.description.ElementDesc;
import org.apache.axis.description.FieldDesc;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.ConstructorTarget;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerImpl;
import org.apache.axis.encoding.Target;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.ser.ArrayDeserializer;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanPropertyTarget;
import org.apache.axis.encoding.ser.SimpleDeserializer;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.SOAPHandler;
import org.apache.axis.message.Text;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.BeanPropertyDescriptor;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class BeanDeserializer
extends DeserializerImpl
implements Serializable {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$encoding$ser$BeanDeserializer == null ? (class$org$apache$axis$encoding$ser$BeanDeserializer = BeanDeserializer.class$("org.apache.axis.encoding.ser.BeanDeserializer")) : class$org$apache$axis$encoding$ser$BeanDeserializer).getName());
    private final CharArrayWriter val = new CharArrayWriter();
    QName xmlType;
    Class javaType;
    protected Map propertyMap = null;
    protected QName prevQName;
    protected Constructor constructorToUse = null;
    protected Target constructorTarget = null;
    protected TypeDesc typeDesc = null;
    protected int collectionIndex = -1;
    protected SimpleDeserializer cacheStringDSer = null;
    protected QName cacheXMLType = null;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$BeanDeserializer;
    static /* synthetic */ Class class$java$lang$String;

    public BeanDeserializer(Class javaType, QName xmlType) {
        this(javaType, xmlType, TypeDesc.getTypeDescForClass(javaType));
    }

    public BeanDeserializer(Class javaType, QName xmlType, TypeDesc typeDesc) {
        this(javaType, xmlType, typeDesc, BeanDeserializerFactory.getProperties(javaType, typeDesc));
    }

    public BeanDeserializer(Class javaType, QName xmlType, TypeDesc typeDesc, Map propertyMap) {
        this.xmlType = xmlType;
        this.javaType = javaType;
        this.typeDesc = typeDesc;
        this.propertyMap = propertyMap;
        try {
            this.value = javaType.newInstance();
        }
        catch (Exception e) {
            // empty catch block
        }
    }

    public void startElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        block4: {
            if (this.value == null) {
                try {
                    this.value = this.javaType.newInstance();
                }
                catch (Exception e) {
                    Constructor<?>[] constructors = this.javaType.getConstructors();
                    if (constructors.length > 0) {
                        this.constructorToUse = constructors[0];
                    }
                    if (this.constructorToUse != null) break block4;
                    throw new SAXException(Messages.getMessage("cantCreateBean00", this.javaType.getName(), e.toString()));
                }
            }
        }
        super.startElement(namespace, localName, prefix, attributes, context);
    }

    public SOAPHandler onStartChild(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        Deserializer dSer;
        this.handleMixedContent();
        BeanPropertyDescriptor propDesc = null;
        FieldDesc fieldDesc = null;
        SOAPConstants soapConstants = context.getSOAPConstants();
        String encodingStyle = context.getEncodingStyle();
        boolean isEncoded = Constants.isSOAP_ENC(encodingStyle);
        QName elemQName = new QName(namespace, localName);
        if (this.prevQName == null || !this.prevQName.equals(elemQName)) {
            this.collectionIndex = -1;
        }
        boolean isArray = false;
        QName itemQName = null;
        if (this.typeDesc != null) {
            String fieldName = this.typeDesc.getFieldNameForElement(elemQName, isEncoded);
            propDesc = (BeanPropertyDescriptor)this.propertyMap.get(fieldName);
            fieldDesc = this.typeDesc.getFieldByName(fieldName);
            if (fieldDesc != null) {
                ElementDesc element = (ElementDesc)fieldDesc;
                isArray = element.isMaxOccursUnbounded();
                itemQName = element.getItemQName();
            }
        }
        if (propDesc == null) {
            propDesc = (BeanPropertyDescriptor)this.propertyMap.get(localName);
        }
        if (propDesc == null || this.prevQName != null && this.prevQName.equals(elemQName) && !propDesc.isIndexed() && !isArray && this.getAnyPropertyDesc() != null) {
            this.prevQName = elemQName;
            propDesc = this.getAnyPropertyDesc();
            if (propDesc != null) {
                try {
                    MessageElement thisEl;
                    MessageElement[] curElements = (MessageElement[])propDesc.get(this.value);
                    int length = 0;
                    if (curElements != null) {
                        length = curElements.length;
                    }
                    MessageElement[] newElements = new MessageElement[length + 1];
                    if (curElements != null) {
                        System.arraycopy(curElements, 0, newElements, 0, length);
                    }
                    newElements[length] = thisEl = context.getCurElement();
                    propDesc.set(this.value, newElements);
                    if (!localName.equals(thisEl.getName())) {
                        return new SOAPHandler(newElements, length);
                    }
                    return new SOAPHandler();
                }
                catch (Exception e) {
                    throw new SAXException(e);
                }
            }
        }
        if (propDesc == null) {
            throw new SAXException(Messages.getMessage("badElem00", this.javaType.getName(), localName));
        }
        this.prevQName = elemQName;
        QName childXMLType = context.getTypeFromAttributes(namespace, localName, attributes);
        String href = attributes.getValue(soapConstants.getAttrHref());
        Class fieldType = propDesc.getType();
        if (childXMLType == null && fieldDesc != null && href == null) {
            childXMLType = fieldDesc.getXmlType();
            if (itemQName != null) {
                childXMLType = Constants.SOAP_ARRAY;
                fieldType = propDesc.getActualType();
            } else {
                childXMLType = fieldDesc.getXmlType();
            }
        }
        if ((dSer = this.getDeserializer(childXMLType, fieldType, href, context)) == null) {
            dSer = context.getDeserializerForClass(propDesc.getType());
        }
        if (context.isNil(attributes)) {
            if (!(propDesc == null || !propDesc.isIndexed() && !isArray || dSer != null && dSer instanceof ArrayDeserializer)) {
                ++this.collectionIndex;
                dSer.registerValueTarget(new BeanPropertyTarget(this.value, propDesc, this.collectionIndex));
                this.addChildDeserializer(dSer);
                return (SOAPHandler)((Object)dSer);
            }
            return null;
        }
        if (dSer == null) {
            throw new SAXException(Messages.getMessage("noDeser00", childXMLType.toString()));
        }
        if (this.constructorToUse != null) {
            if (this.constructorTarget == null) {
                this.constructorTarget = new ConstructorTarget(this.constructorToUse, this);
            }
            dSer.registerValueTarget(this.constructorTarget);
        } else if (propDesc.isWriteable()) {
            if ((itemQName != null || propDesc.isIndexed() || isArray) && !(dSer instanceof ArrayDeserializer)) {
                ++this.collectionIndex;
                dSer.registerValueTarget(new BeanPropertyTarget(this.value, propDesc, this.collectionIndex));
            } else {
                this.collectionIndex = -1;
                dSer.registerValueTarget(new BeanPropertyTarget(this.value, propDesc));
            }
        }
        this.addChildDeserializer(dSer);
        return (SOAPHandler)((Object)dSer);
    }

    public BeanPropertyDescriptor getAnyPropertyDesc() {
        if (this.typeDesc == null) {
            return null;
        }
        return this.typeDesc.getAnyDesc();
    }

    public void onStartElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        if (this.value == null && this.constructorToUse == null) {
            try {
                this.value = this.javaType.newInstance();
            }
            catch (Exception e) {
                throw new SAXException(Messages.getMessage("cantCreateBean00", this.javaType.getName(), e.toString()));
            }
        }
        if (this.typeDesc == null) {
            return;
        }
        for (int i = 0; i < attributes.getLength(); ++i) {
            QName attrQName = new QName(attributes.getURI(i), attributes.getLocalName(i));
            String fieldName = this.typeDesc.getFieldNameForAttribute(attrQName);
            if (fieldName == null) continue;
            FieldDesc fieldDesc = this.typeDesc.getFieldByName(fieldName);
            BeanPropertyDescriptor bpd = (BeanPropertyDescriptor)this.propertyMap.get(fieldName);
            if (bpd == null || this.constructorToUse == null && (!bpd.isWriteable() || bpd.isIndexed())) continue;
            Deserializer dSer = this.getDeserializer(fieldDesc.getXmlType(), bpd.getType(), null, context);
            if (dSer == null) {
                dSer = context.getDeserializerForClass(bpd.getType());
            }
            if (dSer == null) {
                throw new SAXException(Messages.getMessage("unregistered00", bpd.getType().toString()));
            }
            if (!(dSer instanceof SimpleDeserializer)) {
                throw new SAXException(Messages.getMessage("AttrNotSimpleType00", bpd.getName(), bpd.getType().toString()));
            }
            try {
                dSer.onStartElement(namespace, localName, prefix, attributes, context);
                Object val = ((SimpleDeserializer)dSer).makeValue(attributes.getValue(i));
                if (this.constructorToUse == null) {
                    bpd.set(this.value, val);
                    continue;
                }
                if (this.constructorTarget == null) {
                    this.constructorTarget = new ConstructorTarget(this.constructorToUse, this);
                }
                this.constructorTarget.set(val);
                continue;
            }
            catch (Exception e) {
                throw new SAXException(e);
            }
        }
    }

    protected Deserializer getDeserializer(QName xmlType, Class javaType, String href, DeserializationContext context) {
        if (javaType.isArray()) {
            context.setDestinationClass(javaType);
        }
        if (this.cacheStringDSer != null && (class$java$lang$String == null ? (class$java$lang$String = BeanDeserializer.class$("java.lang.String")) : class$java$lang$String).equals(javaType) && href == null && (this.cacheXMLType == null && xmlType == null || this.cacheXMLType != null && this.cacheXMLType.equals(xmlType))) {
            this.cacheStringDSer.reset();
            return this.cacheStringDSer;
        }
        Deserializer dSer = null;
        if (xmlType != null && href == null) {
            dSer = context.getDeserializerForType(xmlType);
        } else {
            TypeMapping tm = context.getTypeMapping();
            QName defaultXMLType = tm.getTypeQName(javaType);
            if (href == null) {
                dSer = context.getDeserializer(javaType, defaultXMLType);
            } else {
                dSer = new DeserializerImpl();
                context.setDestinationClass(javaType);
                dSer.setDefaultType(defaultXMLType);
            }
        }
        if (javaType.equals(class$java$lang$String == null ? (class$java$lang$String = BeanDeserializer.class$("java.lang.String")) : class$java$lang$String) && dSer instanceof SimpleDeserializer) {
            this.cacheStringDSer = (SimpleDeserializer)dSer;
            this.cacheXMLType = xmlType;
        }
        return dSer;
    }

    public void characters(char[] chars, int start, int end) throws SAXException {
        this.val.write(chars, start, end);
    }

    public void onEndElement(String namespace, String localName, DeserializationContext context) throws SAXException {
        this.handleMixedContent();
    }

    protected void handleMixedContent() throws SAXException {
        BeanPropertyDescriptor propDesc = this.getAnyPropertyDesc();
        if (propDesc == null || this.val.size() == 0) {
            return;
        }
        String textValue = this.val.toString().trim();
        this.val.reset();
        if (textValue.length() == 0) {
            return;
        }
        try {
            MessageElement thisEl;
            MessageElement[] curElements = (MessageElement[])propDesc.get(this.value);
            int length = 0;
            if (curElements != null) {
                length = curElements.length;
            }
            MessageElement[] newElements = new MessageElement[length + 1];
            if (curElements != null) {
                System.arraycopy(curElements, 0, newElements, 0, length);
            }
            newElements[length] = thisEl = new MessageElement(new Text(textValue));
            propDesc.set(this.value, newElements);
        }
        catch (Exception e) {
            throw new SAXException(e);
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

