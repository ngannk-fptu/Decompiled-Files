/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlNonNegativeInteger;
import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STTextRotation
extends XmlAnySimpleType {
    public static final SimpleTypeFactory<STTextRotation> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "sttextrotationec64type");
    public static final SchemaType type = Factory.getType();

    public Object getObjectValue();

    public void setObjectValue(Object var1);

    public SchemaType instanceType();

    public static interface Member2
    extends XmlNonNegativeInteger {
        public static final ElementFactory<Member2> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "anon1c0atype");
        public static final SchemaType type = Factory.getType();
    }

    public static interface Member
    extends XmlNonNegativeInteger {
        public static final ElementFactory<Member> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "anon9d89type");
        public static final SchemaType type = Factory.getType();

        public int getIntValue();

        public void setIntValue(int var1);
    }
}

