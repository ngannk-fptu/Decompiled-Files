/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.vml;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STStrokeArrowType
extends XmlString {
    public static final SimpleTypeFactory<STStrokeArrowType> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "ststrokearrowtype7b4ftype");
    public static final SchemaType type = Factory.getType();
    public static final Enum NONE = Enum.forString("none");
    public static final Enum BLOCK = Enum.forString("block");
    public static final Enum CLASSIC = Enum.forString("classic");
    public static final Enum OVAL = Enum.forString("oval");
    public static final Enum DIAMOND = Enum.forString("diamond");
    public static final Enum OPEN = Enum.forString("open");
    public static final int INT_NONE = 1;
    public static final int INT_BLOCK = 2;
    public static final int INT_CLASSIC = 3;
    public static final int INT_OVAL = 4;
    public static final int INT_DIAMOND = 5;
    public static final int INT_OPEN = 6;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_NONE = 1;
        static final int INT_BLOCK = 2;
        static final int INT_CLASSIC = 3;
        static final int INT_OVAL = 4;
        static final int INT_DIAMOND = 5;
        static final int INT_OPEN = 6;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("none", 1), new Enum("block", 2), new Enum("classic", 3), new Enum("oval", 4), new Enum("diamond", 5), new Enum("open", 6)});
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

