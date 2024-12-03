/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STItemType
extends XmlString {
    public static final SimpleTypeFactory<STItemType> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stitemtype6186type");
    public static final SchemaType type = Factory.getType();
    public static final Enum DATA = Enum.forString("data");
    public static final Enum DEFAULT = Enum.forString("default");
    public static final Enum SUM = Enum.forString("sum");
    public static final Enum COUNT_A = Enum.forString("countA");
    public static final Enum AVG = Enum.forString("avg");
    public static final Enum MAX = Enum.forString("max");
    public static final Enum MIN = Enum.forString("min");
    public static final Enum PRODUCT = Enum.forString("product");
    public static final Enum COUNT = Enum.forString("count");
    public static final Enum STD_DEV = Enum.forString("stdDev");
    public static final Enum STD_DEV_P = Enum.forString("stdDevP");
    public static final Enum VAR = Enum.forString("var");
    public static final Enum VAR_P = Enum.forString("varP");
    public static final Enum GRAND = Enum.forString("grand");
    public static final Enum BLANK = Enum.forString("blank");
    public static final int INT_DATA = 1;
    public static final int INT_DEFAULT = 2;
    public static final int INT_SUM = 3;
    public static final int INT_COUNT_A = 4;
    public static final int INT_AVG = 5;
    public static final int INT_MAX = 6;
    public static final int INT_MIN = 7;
    public static final int INT_PRODUCT = 8;
    public static final int INT_COUNT = 9;
    public static final int INT_STD_DEV = 10;
    public static final int INT_STD_DEV_P = 11;
    public static final int INT_VAR = 12;
    public static final int INT_VAR_P = 13;
    public static final int INT_GRAND = 14;
    public static final int INT_BLANK = 15;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_DATA = 1;
        static final int INT_DEFAULT = 2;
        static final int INT_SUM = 3;
        static final int INT_COUNT_A = 4;
        static final int INT_AVG = 5;
        static final int INT_MAX = 6;
        static final int INT_MIN = 7;
        static final int INT_PRODUCT = 8;
        static final int INT_COUNT = 9;
        static final int INT_STD_DEV = 10;
        static final int INT_STD_DEV_P = 11;
        static final int INT_VAR = 12;
        static final int INT_VAR_P = 13;
        static final int INT_GRAND = 14;
        static final int INT_BLANK = 15;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("data", 1), new Enum("default", 2), new Enum("sum", 3), new Enum("countA", 4), new Enum("avg", 5), new Enum("max", 6), new Enum("min", 7), new Enum("product", 8), new Enum("count", 9), new Enum("stdDev", 10), new Enum("stdDevP", 11), new Enum("var", 12), new Enum("varP", 13), new Enum("grand", 14), new Enum("blank", 15)});
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

