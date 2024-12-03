/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.type;

import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.type.AbstractStructuredType;
import org.apache.xmpbox.type.Cardinality;
import org.apache.xmpbox.type.PropertyType;
import org.apache.xmpbox.type.StructuredType;
import org.apache.xmpbox.type.Types;

@StructuredType(preferedPrefix="stJob", namespace="http://ns.adobe.com/xap/1.0/sType/Job#")
public class JobType
extends AbstractStructuredType {
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String ID = "id";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String NAME = "name";
    @PropertyType(type=Types.URL, card=Cardinality.Simple)
    public static final String URL = "url";

    public JobType(XMPMetadata metadata) {
        this(metadata, null);
    }

    public JobType(XMPMetadata metadata, String fieldPrefix) {
        super(metadata, fieldPrefix);
        this.addNamespace(this.getNamespace(), this.getPrefix());
    }

    public void setId(String id) {
        this.addSimpleProperty(ID, id);
    }

    public void setName(String name) {
        this.addSimpleProperty(NAME, name);
    }

    public void setUrl(String name) {
        this.addSimpleProperty(URL, name);
    }

    public String getId() {
        return this.getPropertyValueAsString(ID);
    }

    public String getName() {
        return this.getPropertyValueAsString(NAME);
    }

    public String getUrl() {
        return this.getPropertyValueAsString(URL);
    }
}

