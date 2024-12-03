/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.type;

import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.type.AbstractField;
import org.apache.xmpbox.type.AbstractStructuredType;
import org.apache.xmpbox.type.Attribute;
import org.apache.xmpbox.type.Cardinality;
import org.apache.xmpbox.type.PropertyType;
import org.apache.xmpbox.type.StructuredType;
import org.apache.xmpbox.type.TextType;
import org.apache.xmpbox.type.Types;

@StructuredType(preferedPrefix="photoshop", namespace="http://ns.adobe.com/photoshop/1.0/")
public class LayerType
extends AbstractStructuredType {
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String LAYER_NAME = "LayerName";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String LAYER_TEXT = "LayerText";

    public LayerType(XMPMetadata metadata) {
        super(metadata);
        this.setAttribute(new Attribute("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "parseType", "Resource"));
    }

    public String getLayerName() {
        AbstractField absProp = this.getFirstEquivalentProperty(LAYER_NAME, TextType.class);
        if (absProp != null) {
            return ((TextType)absProp).getStringValue();
        }
        return null;
    }

    public void setLayerName(String image) {
        this.addProperty(this.createTextType(LAYER_NAME, image));
    }

    public String getLayerText() {
        AbstractField absProp = this.getFirstEquivalentProperty(LAYER_TEXT, TextType.class);
        if (absProp != null) {
            return ((TextType)absProp).getStringValue();
        }
        return null;
    }

    public void setLayerText(String image) {
        this.addProperty(this.createTextType(LAYER_TEXT, image));
    }
}

