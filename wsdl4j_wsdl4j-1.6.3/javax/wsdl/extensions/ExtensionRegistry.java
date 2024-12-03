/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl.extensions;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.ExtensionDeserializer;
import javax.wsdl.extensions.ExtensionSerializer;
import javax.wsdl.extensions.UnknownExtensionDeserializer;
import javax.wsdl.extensions.UnknownExtensionSerializer;
import javax.xml.namespace.QName;

public class ExtensionRegistry
implements Serializable {
    public static final long serialVersionUID = 1L;
    protected Map serializerReg = new Hashtable();
    protected Map deserializerReg = new Hashtable();
    protected Map extensionTypeReg = new Hashtable();
    protected ExtensionSerializer defaultSer = null;
    protected ExtensionDeserializer defaultDeser = null;
    protected Map extensionAttributeTypeReg = new Hashtable();

    public ExtensionRegistry() {
        this.setDefaultSerializer(new UnknownExtensionSerializer());
        this.setDefaultDeserializer(new UnknownExtensionDeserializer());
    }

    public void setDefaultSerializer(ExtensionSerializer defaultSer) {
        this.defaultSer = defaultSer;
    }

    public ExtensionSerializer getDefaultSerializer() {
        return this.defaultSer;
    }

    public void setDefaultDeserializer(ExtensionDeserializer defaultDeser) {
        this.defaultDeser = defaultDeser;
    }

    public ExtensionDeserializer getDefaultDeserializer() {
        return this.defaultDeser;
    }

    public void registerSerializer(Class parentType, QName elementType, ExtensionSerializer es) {
        Hashtable<QName, ExtensionSerializer> innerSerializerReg = (Hashtable<QName, ExtensionSerializer>)this.serializerReg.get(parentType);
        if (innerSerializerReg == null) {
            innerSerializerReg = new Hashtable<QName, ExtensionSerializer>();
            this.serializerReg.put(parentType, innerSerializerReg);
        }
        innerSerializerReg.put(elementType, es);
    }

    public void registerDeserializer(Class parentType, QName elementType, ExtensionDeserializer ed) {
        Hashtable<QName, ExtensionDeserializer> innerDeserializerReg = (Hashtable<QName, ExtensionDeserializer>)this.deserializerReg.get(parentType);
        if (innerDeserializerReg == null) {
            innerDeserializerReg = new Hashtable<QName, ExtensionDeserializer>();
            this.deserializerReg.put(parentType, innerDeserializerReg);
        }
        innerDeserializerReg.put(elementType, ed);
    }

    public ExtensionSerializer querySerializer(Class parentType, QName elementType) throws WSDLException {
        Map innerSerializerReg = (Map)this.serializerReg.get(parentType);
        ExtensionSerializer es = null;
        if (innerSerializerReg != null) {
            es = (ExtensionSerializer)innerSerializerReg.get(elementType);
        }
        if (es == null) {
            es = this.defaultSer;
        }
        if (es == null) {
            throw new WSDLException("CONFIGURATION_ERROR", "No ExtensionSerializer found to serialize a '" + elementType + "' element in the context of a '" + parentType.getName() + "'.");
        }
        return es;
    }

    public ExtensionDeserializer queryDeserializer(Class parentType, QName elementType) throws WSDLException {
        Map innerDeserializerReg = (Map)this.deserializerReg.get(parentType);
        ExtensionDeserializer ed = null;
        if (innerDeserializerReg != null) {
            ed = (ExtensionDeserializer)innerDeserializerReg.get(elementType);
        }
        if (ed == null) {
            ed = this.defaultDeser;
        }
        if (ed == null) {
            throw new WSDLException("CONFIGURATION_ERROR", "No ExtensionDeserializer found to deserialize a '" + elementType + "' element in the context of a '" + parentType.getName() + "'.");
        }
        return ed;
    }

    public Set getAllowableExtensions(Class parentType) {
        Map innerDeserializerReg = (Map)this.deserializerReg.get(parentType);
        return innerDeserializerReg != null ? innerDeserializerReg.keySet() : null;
    }

    public void mapExtensionTypes(Class parentType, QName elementType, Class extensionType) {
        Hashtable<QName, Class> innerExtensionTypeReg = (Hashtable<QName, Class>)this.extensionTypeReg.get(parentType);
        if (innerExtensionTypeReg == null) {
            innerExtensionTypeReg = new Hashtable<QName, Class>();
            this.extensionTypeReg.put(parentType, innerExtensionTypeReg);
        }
        innerExtensionTypeReg.put(elementType, extensionType);
    }

    public ExtensibilityElement createExtension(Class parentType, QName elementType) throws WSDLException {
        Map innerExtensionTypeReg = (Map)this.extensionTypeReg.get(parentType);
        Class extensionType = null;
        if (innerExtensionTypeReg != null) {
            extensionType = (Class)innerExtensionTypeReg.get(elementType);
        }
        if (extensionType == null) {
            throw new WSDLException("CONFIGURATION_ERROR", "No Java extensionType found to represent a '" + elementType + "' element in the context of a '" + parentType.getName() + "'.");
        }
        if (!ExtensibilityElement.class.isAssignableFrom(extensionType)) {
            throw new WSDLException("CONFIGURATION_ERROR", "The Java extensionType '" + extensionType.getName() + "' does " + "not implement the ExtensibilityElement " + "interface.");
        }
        try {
            ExtensibilityElement ee = (ExtensibilityElement)extensionType.newInstance();
            if (ee.getElementType() == null) {
                ee.setElementType(elementType);
            }
            return ee;
        }
        catch (Exception e) {
            throw new WSDLException("CONFIGURATION_ERROR", "Problem instantiating Java extensionType '" + extensionType.getName() + "'.", e);
        }
    }

    public void registerExtensionAttributeType(Class parentType, QName attrName, int attrType) {
        Hashtable<QName, Integer> innerExtensionAttributeTypeReg = (Hashtable<QName, Integer>)this.extensionAttributeTypeReg.get(parentType);
        if (innerExtensionAttributeTypeReg == null) {
            innerExtensionAttributeTypeReg = new Hashtable<QName, Integer>();
            this.extensionAttributeTypeReg.put(parentType, innerExtensionAttributeTypeReg);
        }
        innerExtensionAttributeTypeReg.put(attrName, new Integer(attrType));
    }

    public int queryExtensionAttributeType(Class parentType, QName attrName) {
        Map innerExtensionAttributeTypeReg = (Map)this.extensionAttributeTypeReg.get(parentType);
        Integer attrType = null;
        if (innerExtensionAttributeTypeReg != null) {
            attrType = (Integer)innerExtensionAttributeTypeReg.get(attrName);
        }
        if (attrType != null) {
            return attrType;
        }
        return -1;
    }
}

