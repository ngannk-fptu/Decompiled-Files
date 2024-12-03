/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.schema;

import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.XMPSchema;
import org.apache.xmpbox.schema.XmpSchemaException;
import org.apache.xmpbox.type.PropertiesDescription;
import org.apache.xmpbox.type.PropertyType;

public class XMPSchemaFactory {
    private final String namespace;
    private final Class<? extends XMPSchema> schemaClass;
    private final PropertiesDescription propDef;

    public XMPSchemaFactory(String namespace, Class<? extends XMPSchema> schemaClass, PropertiesDescription propDef) {
        this.namespace = namespace;
        this.schemaClass = schemaClass;
        this.propDef = propDef;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public PropertyType getPropertyType(String name) {
        return this.propDef.getPropertyType(name);
    }

    public XMPSchema createXMPSchema(XMPMetadata metadata, String prefix) throws XmpSchemaException {
        Object[] schemaArgs;
        Class[] argsClass;
        if (this.schemaClass == XMPSchema.class) {
            argsClass = new Class[]{XMPMetadata.class, String.class, String.class};
            schemaArgs = new Object[]{metadata, this.namespace, prefix};
        } else if (prefix != null && !"".equals(prefix)) {
            argsClass = new Class[]{XMPMetadata.class, String.class};
            schemaArgs = new Object[]{metadata, prefix};
        } else {
            argsClass = new Class[]{XMPMetadata.class};
            schemaArgs = new Object[]{metadata};
        }
        try {
            XMPSchema schema = this.schemaClass.getDeclaredConstructor(argsClass).newInstance(schemaArgs);
            metadata.addSchema(schema);
            return schema;
        }
        catch (Exception e) {
            throw new XmpSchemaException("Cannot instantiate specified object schema", e);
        }
    }

    public PropertiesDescription getPropertyDefinition() {
        return this.propDef;
    }
}

