/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STDataValidationOperator
extends XmlString {
    public static final SimpleTypeFactory<STDataValidationOperator> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stdatavalidationoperatore0e0type");
    public static final SchemaType type = Factory.getType();
    public static final Enum BETWEEN = Enum.forString("between");
    public static final Enum NOT_BETWEEN = Enum.forString("notBetween");
    public static final Enum EQUAL = Enum.forString("equal");
    public static final Enum NOT_EQUAL = Enum.forString("notEqual");
    public static final Enum LESS_THAN = Enum.forString("lessThan");
    public static final Enum LESS_THAN_OR_EQUAL = Enum.forString("lessThanOrEqual");
    public static final Enum GREATER_THAN = Enum.forString("greaterThan");
    public static final Enum GREATER_THAN_OR_EQUAL = Enum.forString("greaterThanOrEqual");
    public static final int INT_BETWEEN = 1;
    public static final int INT_NOT_BETWEEN = 2;
    public static final int INT_EQUAL = 3;
    public static final int INT_NOT_EQUAL = 4;
    public static final int INT_LESS_THAN = 5;
    public static final int INT_LESS_THAN_OR_EQUAL = 6;
    public static final int INT_GREATER_THAN = 7;
    public static final int INT_GREATER_THAN_OR_EQUAL = 8;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_BETWEEN = 1;
        static final int INT_NOT_BETWEEN = 2;
        static final int INT_EQUAL = 3;
        static final int INT_NOT_EQUAL = 4;
        static final int INT_LESS_THAN = 5;
        static final int INT_LESS_THAN_OR_EQUAL = 6;
        static final int INT_GREATER_THAN = 7;
        static final int INT_GREATER_THAN_OR_EQUAL = 8;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("between", 1), new Enum("notBetween", 2), new Enum("equal", 3), new Enum("notEqual", 4), new Enum("lessThan", 5), new Enum("lessThanOrEqual", 6), new Enum("greaterThan", 7), new Enum("greaterThanOrEqual", 8)});
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

