/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STCfvoType
extends XmlString {
    public static final SimpleTypeFactory<STCfvoType> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stcfvotypeeb0ftype");
    public static final SchemaType type = Factory.getType();
    public static final Enum NUM = Enum.forString("num");
    public static final Enum PERCENT = Enum.forString("percent");
    public static final Enum MAX = Enum.forString("max");
    public static final Enum MIN = Enum.forString("min");
    public static final Enum FORMULA = Enum.forString("formula");
    public static final Enum PERCENTILE = Enum.forString("percentile");
    public static final int INT_NUM = 1;
    public static final int INT_PERCENT = 2;
    public static final int INT_MAX = 3;
    public static final int INT_MIN = 4;
    public static final int INT_FORMULA = 5;
    public static final int INT_PERCENTILE = 6;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_NUM = 1;
        static final int INT_PERCENT = 2;
        static final int INT_MAX = 3;
        static final int INT_MIN = 4;
        static final int INT_FORMULA = 5;
        static final int INT_PERCENTILE = 6;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("num", 1), new Enum("percent", 2), new Enum("max", 3), new Enum("min", 4), new Enum("formula", 5), new Enum("percentile", 6)});
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

