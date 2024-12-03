/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.vml;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STShadowType
extends XmlString {
    public static final SimpleTypeFactory<STShadowType> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stshadowtype8b48type");
    public static final SchemaType type = Factory.getType();
    public static final Enum SINGLE = Enum.forString("single");
    public static final Enum DOUBLE = Enum.forString("double");
    public static final Enum EMBOSS = Enum.forString("emboss");
    public static final Enum PERSPECTIVE = Enum.forString("perspective");
    public static final int INT_SINGLE = 1;
    public static final int INT_DOUBLE = 2;
    public static final int INT_EMBOSS = 3;
    public static final int INT_PERSPECTIVE = 4;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_SINGLE = 1;
        static final int INT_DOUBLE = 2;
        static final int INT_EMBOSS = 3;
        static final int INT_PERSPECTIVE = 4;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("single", 1), new Enum("double", 2), new Enum("emboss", 3), new Enum("perspective", 4)});
        private static final long serialVersionUID = 1L;

        public static Enum forString(String s) {
            return (Enum)table.forString(s);
        }

        public static Enum forInt(int i) {
            return (Enum)table.forInt(i);
        }

        private Enum(String s, int i) {
            super(s, i);
        }

        private Object readResolve() {
            return Enum.forInt(this.intValue());
        }
    }
}

