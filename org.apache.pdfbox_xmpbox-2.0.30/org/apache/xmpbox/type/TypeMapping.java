/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.type;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.AdobePDFSchema;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.schema.ExifSchema;
import org.apache.xmpbox.schema.PDFAExtensionSchema;
import org.apache.xmpbox.schema.PDFAIdentificationSchema;
import org.apache.xmpbox.schema.PhotoshopSchema;
import org.apache.xmpbox.schema.TiffSchema;
import org.apache.xmpbox.schema.XMPBasicJobTicketSchema;
import org.apache.xmpbox.schema.XMPBasicSchema;
import org.apache.xmpbox.schema.XMPMediaManagementSchema;
import org.apache.xmpbox.schema.XMPRightsManagementSchema;
import org.apache.xmpbox.schema.XMPSchema;
import org.apache.xmpbox.schema.XMPSchemaFactory;
import org.apache.xmpbox.schema.XMPageTextSchema;
import org.apache.xmpbox.schema.XmpSchemaException;
import org.apache.xmpbox.type.AbstractSimpleProperty;
import org.apache.xmpbox.type.AbstractStructuredType;
import org.apache.xmpbox.type.AgentNameType;
import org.apache.xmpbox.type.ArrayProperty;
import org.apache.xmpbox.type.BadFieldValueException;
import org.apache.xmpbox.type.BooleanType;
import org.apache.xmpbox.type.Cardinality;
import org.apache.xmpbox.type.ChoiceType;
import org.apache.xmpbox.type.DateType;
import org.apache.xmpbox.type.DefinedStructuredType;
import org.apache.xmpbox.type.GUIDType;
import org.apache.xmpbox.type.IntegerType;
import org.apache.xmpbox.type.LocaleType;
import org.apache.xmpbox.type.MIMEType;
import org.apache.xmpbox.type.PartType;
import org.apache.xmpbox.type.ProperNameType;
import org.apache.xmpbox.type.PropertiesDescription;
import org.apache.xmpbox.type.PropertyType;
import org.apache.xmpbox.type.RealType;
import org.apache.xmpbox.type.RenditionClassType;
import org.apache.xmpbox.type.StructuredType;
import org.apache.xmpbox.type.TextType;
import org.apache.xmpbox.type.Types;
import org.apache.xmpbox.type.URIType;
import org.apache.xmpbox.type.URLType;
import org.apache.xmpbox.type.XPathType;

public final class TypeMapping {
    private Map<Types, PropertiesDescription> structuredMappings;
    private Map<String, Types> structuredNamespaces;
    private Map<String, String> definedStructuredNamespaces;
    private Map<String, PropertiesDescription> definedStructuredMappings;
    private final XMPMetadata metadata;
    private Map<String, XMPSchemaFactory> schemaMap;
    private static final Class<?>[] SIMPLEPROPERTYCONSTPARAMS = new Class[]{XMPMetadata.class, String.class, String.class, String.class, Object.class};

    public TypeMapping(XMPMetadata metadata) {
        this.metadata = metadata;
        this.initialize();
    }

    private void initialize() {
        this.structuredMappings = new EnumMap<Types, PropertiesDescription>(Types.class);
        this.structuredNamespaces = new HashMap<String, Types>();
        for (Types type : Types.values()) {
            if (!type.isStructured()) continue;
            Class<AbstractStructuredType> clz = type.getImplementingClass().asSubclass(AbstractStructuredType.class);
            StructuredType st = clz.getAnnotation(StructuredType.class);
            String ns = st.namespace();
            PropertiesDescription pm = this.initializePropMapping(clz);
            this.structuredNamespaces.put(ns, type);
            this.structuredMappings.put(type, pm);
        }
        this.definedStructuredNamespaces = new HashMap<String, String>();
        this.definedStructuredMappings = new HashMap<String, PropertiesDescription>();
        this.schemaMap = new HashMap<String, XMPSchemaFactory>();
        this.addNameSpace(XMPBasicSchema.class);
        this.addNameSpace(DublinCoreSchema.class);
        this.addNameSpace(PDFAExtensionSchema.class);
        this.addNameSpace(XMPMediaManagementSchema.class);
        this.addNameSpace(AdobePDFSchema.class);
        this.addNameSpace(PDFAIdentificationSchema.class);
        this.addNameSpace(XMPRightsManagementSchema.class);
        this.addNameSpace(PhotoshopSchema.class);
        this.addNameSpace(XMPBasicJobTicketSchema.class);
        this.addNameSpace(ExifSchema.class);
        this.addNameSpace(TiffSchema.class);
        this.addNameSpace(XMPageTextSchema.class);
    }

    public void addToDefinedStructuredTypes(String typeName, String ns, PropertiesDescription pm) {
        this.definedStructuredNamespaces.put(ns, typeName);
        this.definedStructuredMappings.put(typeName, pm);
    }

    public PropertiesDescription getDefinedDescriptionByNamespace(String namespace) {
        String dt = this.definedStructuredNamespaces.get(namespace);
        return this.definedStructuredMappings.get(dt);
    }

    public AbstractStructuredType instanciateStructuredType(Types type, String propertyName) throws BadFieldValueException {
        try {
            Class<AbstractStructuredType> propertyTypeClass = type.getImplementingClass().asSubclass(AbstractStructuredType.class);
            Constructor<AbstractStructuredType> construct = propertyTypeClass.getDeclaredConstructor(XMPMetadata.class);
            AbstractStructuredType tmp = construct.newInstance(this.metadata);
            tmp.setPropertyName(propertyName);
            return tmp;
        }
        catch (InvocationTargetException e) {
            throw new BadFieldValueException("Failed to instantiate structured type : " + (Object)((Object)type), e);
        }
        catch (IllegalArgumentException e) {
            throw new BadFieldValueException("Failed to instantiate structured type : " + (Object)((Object)type), e);
        }
        catch (InstantiationException e) {
            throw new BadFieldValueException("Failed to instantiate structured type : " + (Object)((Object)type), e);
        }
        catch (IllegalAccessException e) {
            throw new BadFieldValueException("Failed to instantiate structured type : " + (Object)((Object)type), e);
        }
        catch (SecurityException e) {
            throw new BadFieldValueException("Failed to instantiate structured type : " + (Object)((Object)type), e);
        }
        catch (NoSuchMethodException e) {
            throw new BadFieldValueException("Failed to instantiate structured type : " + (Object)((Object)type), e);
        }
    }

    public AbstractStructuredType instanciateDefinedType(String propertyName, String namespace) {
        return new DefinedStructuredType(this.metadata, namespace, null, propertyName);
    }

    public AbstractSimpleProperty instanciateSimpleProperty(String nsuri, String prefix, String name, Object value, Types type) {
        Object[] params = new Object[]{this.metadata, nsuri, prefix, name, value};
        Class<AbstractSimpleProperty> clz = type.getImplementingClass().asSubclass(AbstractSimpleProperty.class);
        try {
            Constructor<AbstractSimpleProperty> cons = clz.getDeclaredConstructor(SIMPLEPROPERTYCONSTPARAMS);
            return cons.newInstance(params);
        }
        catch (NoSuchMethodError e) {
            throw new IllegalArgumentException("Failed to instantiate " + clz.getSimpleName() + " property with value " + value, e);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to instantiate " + clz.getSimpleName() + " property with value " + value, e);
        }
        catch (InstantiationException e) {
            throw new IllegalArgumentException("Failed to instantiate " + clz.getSimpleName() + " property with value " + value, e);
        }
        catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Failed to instantiate " + clz.getSimpleName() + " property with value " + value, e);
        }
        catch (InvocationTargetException e) {
            throw new IllegalArgumentException("Failed to instantiate " + clz.getSimpleName() + " property with value " + value, e);
        }
        catch (SecurityException e) {
            throw new IllegalArgumentException("Failed to instantiate " + clz.getSimpleName() + " property with value " + value, e);
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Failed to instantiate " + clz.getSimpleName() + " property with value " + value, e);
        }
    }

    public AbstractSimpleProperty instanciateSimpleField(Class<?> clz, String nsuri, String prefix, String propertyName, Object value) {
        PropertiesDescription pm = this.initializePropMapping(clz);
        PropertyType simpleType = pm.getPropertyType(propertyName);
        Types type = simpleType.type();
        return this.instanciateSimpleProperty(nsuri, prefix, propertyName, value, type);
    }

    public boolean isStructuredTypeNamespace(String namespace) {
        return this.structuredNamespaces.containsKey(namespace);
    }

    public boolean isDefinedTypeNamespace(String namespace) {
        return this.definedStructuredNamespaces.containsKey(namespace);
    }

    public boolean isDefinedType(String name) {
        return this.definedStructuredMappings.containsKey(name);
    }

    private void addNameSpace(Class<? extends XMPSchema> classSchem) {
        StructuredType st = classSchem.getAnnotation(StructuredType.class);
        String ns = st.namespace();
        this.schemaMap.put(ns, new XMPSchemaFactory(ns, classSchem, this.initializePropMapping(classSchem)));
    }

    public void addNewNameSpace(String ns, String preferred) {
        PropertiesDescription mapping = new PropertiesDescription();
        this.schemaMap.put(ns, new XMPSchemaFactory(ns, XMPSchema.class, mapping));
    }

    public PropertiesDescription getStructuredPropMapping(Types type) {
        return this.structuredMappings.get((Object)type);
    }

    public XMPSchema getAssociatedSchemaObject(XMPMetadata metadata, String namespace, String prefix) throws XmpSchemaException {
        if (this.schemaMap.containsKey(namespace)) {
            XMPSchemaFactory factory = this.schemaMap.get(namespace);
            return factory.createXMPSchema(metadata, prefix);
        }
        XMPSchemaFactory factory = this.getSchemaFactory(namespace);
        return factory != null ? factory.createXMPSchema(metadata, prefix) : null;
    }

    public XMPSchemaFactory getSchemaFactory(String namespace) {
        return this.schemaMap.get(namespace);
    }

    public boolean isDefinedSchema(String namespace) {
        return this.schemaMap.containsKey(namespace);
    }

    public boolean isDefinedNamespace(String namespace) {
        return this.isDefinedSchema(namespace) || this.isStructuredTypeNamespace(namespace) || this.isDefinedTypeNamespace(namespace);
    }

    public PropertyType getSpecifiedPropertyType(QName name) throws BadFieldValueException {
        XMPSchemaFactory factory = this.getSchemaFactory(name.getNamespaceURI());
        if (factory != null) {
            return factory.getPropertyType(name.getLocalPart());
        }
        Types st = this.structuredNamespaces.get(name.getNamespaceURI());
        if (st != null) {
            return TypeMapping.createPropertyType(st, Cardinality.Simple);
        }
        String dt = this.definedStructuredNamespaces.get(name.getNamespaceURI());
        if (dt == null) {
            throw new BadFieldValueException("No descriptor found for " + name);
        }
        return TypeMapping.createPropertyType(Types.DefinedType, Cardinality.Simple);
    }

    public PropertiesDescription initializePropMapping(Class<?> classSchem) {
        PropertiesDescription propMap = new PropertiesDescription();
        Field[] fields = classSchem.getFields();
        String propName = null;
        for (Field field : fields) {
            if (!field.isAnnotationPresent(PropertyType.class)) continue;
            try {
                propName = (String)field.get(propName);
            }
            catch (Exception e) {
                throw new IllegalArgumentException("couldn't read one type declaration, please check accessibility and declaration of fields annotated in " + classSchem.getName(), e);
            }
            PropertyType propType = field.getAnnotation(PropertyType.class);
            propMap.addNewProperty(propName, propType);
        }
        return propMap;
    }

    public BooleanType createBoolean(String namespaceURI, String prefix, String propertyName, boolean value) {
        return new BooleanType(this.metadata, namespaceURI, prefix, propertyName, value);
    }

    public DateType createDate(String namespaceURI, String prefix, String propertyName, Calendar value) {
        return new DateType(this.metadata, namespaceURI, prefix, propertyName, value);
    }

    public IntegerType createInteger(String namespaceURI, String prefix, String propertyName, int value) {
        return new IntegerType(this.metadata, namespaceURI, prefix, propertyName, value);
    }

    public RealType createReal(String namespaceURI, String prefix, String propertyName, float value) {
        return new RealType(this.metadata, namespaceURI, prefix, propertyName, Float.valueOf(value));
    }

    public TextType createText(String namespaceURI, String prefix, String propertyName, String value) {
        return new TextType(this.metadata, namespaceURI, prefix, propertyName, value);
    }

    public ProperNameType createProperName(String namespaceURI, String prefix, String propertyName, String value) {
        return new ProperNameType(this.metadata, namespaceURI, prefix, propertyName, value);
    }

    public URIType createURI(String namespaceURI, String prefix, String propertyName, String value) {
        return new URIType(this.metadata, namespaceURI, prefix, propertyName, value);
    }

    public URLType createURL(String namespaceURI, String prefix, String propertyName, String value) {
        return new URLType(this.metadata, namespaceURI, prefix, propertyName, value);
    }

    public RenditionClassType createRenditionClass(String namespaceURI, String prefix, String propertyName, String value) {
        return new RenditionClassType(this.metadata, namespaceURI, prefix, propertyName, value);
    }

    public PartType createPart(String namespaceURI, String prefix, String propertyName, String value) {
        return new PartType(this.metadata, namespaceURI, prefix, propertyName, value);
    }

    public MIMEType createMIMEType(String namespaceURI, String prefix, String propertyName, String value) {
        return new MIMEType(this.metadata, namespaceURI, prefix, propertyName, value);
    }

    public LocaleType createLocale(String namespaceURI, String prefix, String propertyName, String value) {
        return new LocaleType(this.metadata, namespaceURI, prefix, propertyName, value);
    }

    public GUIDType createGUID(String namespaceURI, String prefix, String propertyName, String value) {
        return new GUIDType(this.metadata, namespaceURI, prefix, propertyName, value);
    }

    public ChoiceType createChoice(String namespaceURI, String prefix, String propertyName, String value) {
        return new ChoiceType(this.metadata, namespaceURI, prefix, propertyName, value);
    }

    public AgentNameType createAgentName(String namespaceURI, String prefix, String propertyName, String value) {
        return new AgentNameType(this.metadata, namespaceURI, prefix, propertyName, value);
    }

    public XPathType createXPath(String namespaceURI, String prefix, String propertyName, String value) {
        return new XPathType(this.metadata, namespaceURI, prefix, propertyName, value);
    }

    public ArrayProperty createArrayProperty(String namespace, String prefix, String propertyName, Cardinality type) {
        return new ArrayProperty(this.metadata, namespace, prefix, propertyName, type);
    }

    public static PropertyType createPropertyType(final Types type, final Cardinality card) {
        return new PropertyType(){

            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public Types type() {
                return type;
            }

            @Override
            public Cardinality card() {
                return card;
            }
        };
    }
}

