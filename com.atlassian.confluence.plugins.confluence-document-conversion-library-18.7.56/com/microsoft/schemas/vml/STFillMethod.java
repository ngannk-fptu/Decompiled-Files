/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.vml;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STFillMethod
extends XmlString {
    public static final SimpleTypeFactory<STFillMethod> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stfillmethoda592type");
    public static final SchemaType type = Factory.getType();
    public static final Enum NONE = Enum.forString("none");
    public static final Enum LINEAR = Enum.forString("linear");
    public static final Enum SIGMA = Enum.forString("sigma");
    public static final Enum ANY = Enum.forString("any");
    public static final Enum LINEAR_SIGMA = Enum.forString("linear sigma");
    public static final int INT_NONE = 1;
    public static final int INT_LINEAR = 2;
    public static final int INT_SIGMA = 3;
    public static final int INT_ANY = 4;
    public static final int INT_LINEAR_SIGMA = 5;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_NONE = 1;
        static final int INT_LINEAR = 2;
        static final int INT_SIGMA = 3;
        static final int INT_ANY = 4;
        static final int INT_LINEAR_SIGMA = 5;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("none", 1), new Enum("linear", 2), new Enum("sigma", 3), new Enum("any", 4), new Enum("linear sigma", 5)});
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

