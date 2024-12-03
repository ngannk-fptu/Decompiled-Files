/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STTotalsRowFunction
extends XmlString {
    public static final SimpleTypeFactory<STTotalsRowFunction> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "sttotalsrowfunctioncb72type");
    public static final SchemaType type = Factory.getType();
    public static final Enum NONE = Enum.forString("none");
    public static final Enum SUM = Enum.forString("sum");
    public static final Enum MIN = Enum.forString("min");
    public static final Enum MAX = Enum.forString("max");
    public static final Enum AVERAGE = Enum.forString("average");
    public static final Enum COUNT = Enum.forString("count");
    public static final Enum COUNT_NUMS = Enum.forString("countNums");
    public static final Enum STD_DEV = Enum.forString("stdDev");
    public static final Enum VAR = Enum.forString("var");
    public static final Enum CUSTOM = Enum.forString("custom");
    public static final int INT_NONE = 1;
    public static final int INT_SUM = 2;
    public static final int INT_MIN = 3;
    public static final int INT_MAX = 4;
    public static final int INT_AVERAGE = 5;
    public static final int INT_COUNT = 6;
    public static final int INT_COUNT_NUMS = 7;
    public static final int INT_STD_DEV = 8;
    public static final int INT_VAR = 9;
    public static final int INT_CUSTOM = 10;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_NONE = 1;
        static final int INT_SUM = 2;
        static final int INT_MIN = 3;
        static final int INT_MAX = 4;
        static final int INT_AVERAGE = 5;
        static final int INT_COUNT = 6;
        static final int INT_COUNT_NUMS = 7;
        static final int INT_STD_DEV = 8;
        static final int INT_VAR = 9;
        static final int INT_CUSTOM = 10;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("none", 1), new Enum("sum", 2), new Enum("min", 3), new Enum("max", 4), new Enum("average", 5), new Enum("count", 6), new Enum("countNums", 7), new Enum("stdDev", 8), new Enum("var", 9), new Enum("custom", 10)});
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

