/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.type;

import java.util.Calendar;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.type.AbstractComplexProperty;
import org.apache.xmpbox.type.AbstractSimpleProperty;
import org.apache.xmpbox.type.ArrayProperty;
import org.apache.xmpbox.type.Cardinality;
import org.apache.xmpbox.type.DateType;
import org.apache.xmpbox.type.StructuredType;
import org.apache.xmpbox.type.TextType;
import org.apache.xmpbox.type.TypeMapping;

public abstract class AbstractStructuredType
extends AbstractComplexProperty {
    protected static final String STRUCTURE_ARRAY_NAME = "li";
    private String namespace;
    private String preferedPrefix;
    private String prefix;

    public AbstractStructuredType(XMPMetadata metadata) {
        this(metadata, null, null, null);
    }

    public AbstractStructuredType(XMPMetadata metadata, String namespaceURI) {
        this(metadata, namespaceURI, null, null);
        StructuredType st = this.getClass().getAnnotation(StructuredType.class);
        if (st == null) {
            throw new IllegalArgumentException(" StructuredType annotation cannot be null");
        }
        this.namespace = st.namespace();
        this.preferedPrefix = st.preferedPrefix();
        this.prefix = this.preferedPrefix;
    }

    public AbstractStructuredType(XMPMetadata metadata, String namespaceURI, String fieldPrefix, String propertyName) {
        super(metadata, propertyName);
        StructuredType st = this.getClass().getAnnotation(StructuredType.class);
        if (st != null) {
            this.namespace = st.namespace();
            this.preferedPrefix = st.preferedPrefix();
        } else {
            if (namespaceURI == null) {
                throw new IllegalArgumentException("Both StructuredType annotation and namespace parameter cannot be null");
            }
            this.namespace = namespaceURI;
            this.preferedPrefix = fieldPrefix;
        }
        this.prefix = fieldPrefix == null ? this.preferedPrefix : fieldPrefix;
    }

    @Override
    public final String getNamespace() {
        return this.namespace;
    }

    public final void setNamespace(String ns) {
        this.namespace = ns;
    }

    @Override
    public final String getPrefix() {
        return this.prefix;
    }

    public final void setPrefix(String pf) {
        this.prefix = pf;
    }

    public final String getPreferedPrefix() {
        return this.preferedPrefix;
    }

    protected void addSimpleProperty(String propertyName, Object value) {
        TypeMapping tm = this.getMetadata().getTypeMapping();
        AbstractSimpleProperty asp = tm.instanciateSimpleField(this.getClass(), null, this.getPrefix(), propertyName, value);
        this.addProperty(asp);
    }

    protected String getPropertyValueAsString(String fieldName) {
        AbstractSimpleProperty absProp = (AbstractSimpleProperty)this.getProperty(fieldName);
        if (absProp == null) {
            return null;
        }
        return absProp.getStringValue();
    }

    protected Calendar getDatePropertyAsCalendar(String fieldName) {
        DateType absProp = (DateType)this.getFirstEquivalentProperty(fieldName, DateType.class);
        if (absProp != null) {
            return absProp.getValue();
        }
        return null;
    }

    public TextType createTextType(String propertyName, String value) {
        return this.getMetadata().getTypeMapping().createText(this.getNamespace(), this.getPrefix(), propertyName, value);
    }

    public ArrayProperty createArrayProperty(String propertyName, Cardinality type) {
        return this.getMetadata().getTypeMapping().createArrayProperty(this.getNamespace(), this.getPrefix(), propertyName, type);
    }
}

