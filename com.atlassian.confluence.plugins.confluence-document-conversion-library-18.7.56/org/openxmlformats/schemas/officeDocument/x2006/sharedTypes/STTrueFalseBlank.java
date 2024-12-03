/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.officeDocument.x2006.sharedTypes;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STTrueFalseBlank
extends XmlString {
    public static final SimpleTypeFactory<STTrueFalseBlank> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "sttruefalseblank5459type");
    public static final SchemaType type = Factory.getType();
    public static final Enum T = Enum.forString("t");
    public static final Enum F = Enum.forString("f");
    public static final Enum TRUE = Enum.forString("true");
    public static final Enum FALSE = Enum.forString("false");
    public static final Enum X = Enum.forString("");
    public static final Enum TRUE_2 = Enum.forString("True");
    public static final Enum FALSE_2 = Enum.forString("False");
    public static final int INT_T = 1;
    public static final int INT_F = 2;
    public static final int INT_TRUE = 3;
    public static final int INT_FALSE = 4;
    public static final int INT_X = 5;
    public static final int INT_TRUE_2 = 6;
    public static final int INT_FALSE_2 = 7;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_T = 1;
        static final int INT_F = 2;
        static final int INT_TRUE = 3;
        static final int INT_FALSE = 4;
        static final int INT_X = 5;
        static final int INT_TRUE_2 = 6;
        static final int INT_FALSE_2 = 7;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("t", 1), new Enum("f", 2), new Enum("true", 3), new Enum("false", 4), new Enum("", 5), new Enum("True", 6), new Enum("False", 7)});
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

