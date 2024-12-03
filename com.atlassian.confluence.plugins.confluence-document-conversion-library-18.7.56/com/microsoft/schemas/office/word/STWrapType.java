/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.word;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STWrapType
extends XmlString {
    public static final SimpleTypeFactory<STWrapType> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stwraptype9ca5type");
    public static final SchemaType type = Factory.getType();
    public static final Enum TOP_AND_BOTTOM = Enum.forString("topAndBottom");
    public static final Enum SQUARE = Enum.forString("square");
    public static final Enum NONE = Enum.forString("none");
    public static final Enum TIGHT = Enum.forString("tight");
    public static final Enum THROUGH = Enum.forString("through");
    public static final int INT_TOP_AND_BOTTOM = 1;
    public static final int INT_SQUARE = 2;
    public static final int INT_NONE = 3;
    public static final int INT_TIGHT = 4;
    public static final int INT_THROUGH = 5;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_TOP_AND_BOTTOM = 1;
        static final int INT_SQUARE = 2;
        static final int INT_NONE = 3;
        static final int INT_TIGHT = 4;
        static final int INT_THROUGH = 5;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("topAndBottom", 1), new Enum("square", 2), new Enum("none", 3), new Enum("tight", 4), new Enum("through", 5)});
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

