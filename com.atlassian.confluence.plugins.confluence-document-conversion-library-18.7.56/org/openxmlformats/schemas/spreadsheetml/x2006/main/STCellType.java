/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STCellType
extends XmlString {
    public static final SimpleTypeFactory<STCellType> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stcelltypebf95type");
    public static final SchemaType type = Factory.getType();
    public static final Enum B = Enum.forString("b");
    public static final Enum N = Enum.forString("n");
    public static final Enum E = Enum.forString("e");
    public static final Enum S = Enum.forString("s");
    public static final Enum STR = Enum.forString("str");
    public static final Enum INLINE_STR = Enum.forString("inlineStr");
    public static final int INT_B = 1;
    public static final int INT_N = 2;
    public static final int INT_E = 3;
    public static final int INT_S = 4;
    public static final int INT_STR = 5;
    public static final int INT_INLINE_STR = 6;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_B = 1;
        static final int INT_N = 2;
        static final int INT_E = 3;
        static final int INT_S = 4;
        static final int INT_STR = 5;
        static final int INT_INLINE_STR = 6;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("b", 1), new Enum("n", 2), new Enum("e", 3), new Enum("s", 4), new Enum("str", 5), new Enum("inlineStr", 6)});
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

