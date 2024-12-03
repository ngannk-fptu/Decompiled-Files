/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STErrValType
extends XmlString {
    public static final SimpleTypeFactory<STErrValType> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "sterrvaltype96d6type");
    public static final SchemaType type = Factory.getType();
    public static final Enum CUST = Enum.forString("cust");
    public static final Enum FIXED_VAL = Enum.forString("fixedVal");
    public static final Enum PERCENTAGE = Enum.forString("percentage");
    public static final Enum STD_DEV = Enum.forString("stdDev");
    public static final Enum STD_ERR = Enum.forString("stdErr");
    public static final int INT_CUST = 1;
    public static final int INT_FIXED_VAL = 2;
    public static final int INT_PERCENTAGE = 3;
    public static final int INT_STD_DEV = 4;
    public static final int INT_STD_ERR = 5;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_CUST = 1;
        static final int INT_FIXED_VAL = 2;
        static final int INT_PERCENTAGE = 3;
        static final int INT_STD_DEV = 4;
        static final int INT_STD_ERR = 5;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("cust", 1), new Enum("fixedVal", 2), new Enum("percentage", 3), new Enum("stdDev", 4), new Enum("stdErr", 5)});
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

