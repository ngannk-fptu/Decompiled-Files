/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.type;

import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.type.AbstractField;
import org.apache.xmpbox.type.AbstractStructuredType;
import org.apache.xmpbox.type.Attribute;
import org.apache.xmpbox.type.Cardinality;
import org.apache.xmpbox.type.ChoiceType;
import org.apache.xmpbox.type.IntegerType;
import org.apache.xmpbox.type.PropertyType;
import org.apache.xmpbox.type.StructuredType;
import org.apache.xmpbox.type.TextType;
import org.apache.xmpbox.type.Types;

@StructuredType(preferedPrefix="xmpGImg", namespace="http://ns.adobe.com/xap/1.0/g/img/")
public class ThumbnailType
extends AbstractStructuredType {
    @PropertyType(type=Types.Choice, card=Cardinality.Simple)
    public static final String FORMAT = "format";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String HEIGHT = "height";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String WIDTH = "width";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String IMAGE = "image";

    public ThumbnailType(XMPMetadata metadata) {
        super(metadata);
        this.setAttribute(new Attribute("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "parseType", "Resource"));
    }

    public Integer getHeight() {
        AbstractField absProp = this.getFirstEquivalentProperty(HEIGHT, IntegerType.class);
        if (absProp != null) {
            return ((IntegerType)absProp).getValue();
        }
        return null;
    }

    public void setHeight(Integer height) {
        this.addSimpleProperty(HEIGHT, height);
    }

    public Integer getWidth() {
        AbstractField absProp = this.getFirstEquivalentProperty(WIDTH, IntegerType.class);
        if (absProp != null) {
            return ((IntegerType)absProp).getValue();
        }
        return null;
    }

    public void setWidth(Integer width) {
        this.addSimpleProperty(WIDTH, width);
    }

    public String getImage() {
        AbstractField absProp = this.getFirstEquivalentProperty(IMAGE, TextType.class);
        if (absProp != null) {
            return ((TextType)absProp).getStringValue();
        }
        return null;
    }

    public void setImage(String image) {
        this.addSimpleProperty(IMAGE, image);
    }

    public String getFormat() {
        AbstractField absProp = this.getFirstEquivalentProperty(FORMAT, ChoiceType.class);
        if (absProp != null) {
            return ((TextType)absProp).getStringValue();
        }
        return null;
    }

    public void setFormat(String format) {
        this.addSimpleProperty(FORMAT, format);
    }
}

