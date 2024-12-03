/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.type;

import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.type.AbstractStructuredType;
import org.apache.xmpbox.type.Cardinality;
import org.apache.xmpbox.type.ChoiceType;
import org.apache.xmpbox.type.PropertyType;
import org.apache.xmpbox.type.StructuredType;
import org.apache.xmpbox.type.TextType;
import org.apache.xmpbox.type.Types;

@StructuredType(preferedPrefix="pdfaProperty", namespace="http://www.aiim.org/pdfa/ns/property#")
public class PDFAPropertyType
extends AbstractStructuredType {
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String NAME = "name";
    @PropertyType(type=Types.Choice, card=Cardinality.Simple)
    public static final String VALUETYPE = "valueType";
    @PropertyType(type=Types.Choice, card=Cardinality.Simple)
    public static final String CATEGORY = "category";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String DESCRIPTION = "description";

    public PDFAPropertyType(XMPMetadata metadata) {
        super(metadata);
    }

    public String getName() {
        TextType tt = (TextType)this.getProperty(NAME);
        return tt == null ? null : tt.getStringValue();
    }

    public String getValueType() {
        ChoiceType tt = (ChoiceType)this.getProperty(VALUETYPE);
        return tt == null ? null : tt.getStringValue();
    }

    public String getDescription() {
        TextType tt = (TextType)this.getProperty(DESCRIPTION);
        return tt == null ? null : tt.getStringValue();
    }

    public String getCategory() {
        ChoiceType tt = (ChoiceType)this.getProperty(CATEGORY);
        return tt == null ? null : tt.getStringValue();
    }
}

