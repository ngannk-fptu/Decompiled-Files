/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.vml;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STStrokeJoinStyle
extends XmlString {
    public static final SimpleTypeFactory<STStrokeJoinStyle> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "ststrokejoinstyle3c13type");
    public static final SchemaType type = Factory.getType();
    public static final Enum ROUND = Enum.forString("round");
    public static final Enum BEVEL = Enum.forString("bevel");
    public static final Enum MITER = Enum.forString("miter");
    public static final int INT_ROUND = 1;
    public static final int INT_BEVEL = 2;
    public static final int INT_MITER = 3;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_ROUND = 1;
        static final int INT_BEVEL = 2;
        static final int INT_MITER = 3;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("round", 1), new Enum("bevel", 2), new Enum("miter", 3)});
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

