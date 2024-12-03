/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STDataValidationType
extends XmlString {
    public static final SimpleTypeFactory<STDataValidationType> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stdatavalidationtypeabf6type");
    public static final SchemaType type = Factory.getType();
    public static final Enum NONE = Enum.forString("none");
    public static final Enum WHOLE = Enum.forString("whole");
    public static final Enum DECIMAL = Enum.forString("decimal");
    public static final Enum LIST = Enum.forString("list");
    public static final Enum DATE = Enum.forString("date");
    public static final Enum TIME = Enum.forString("time");
    public static final Enum TEXT_LENGTH = Enum.forString("textLength");
    public static final Enum CUSTOM = Enum.forString("custom");
    public static final int INT_NONE = 1;
    public static final int INT_WHOLE = 2;
    public static final int INT_DECIMAL = 3;
    public static final int INT_LIST = 4;
    public static final int INT_DATE = 5;
    public static final int INT_TIME = 6;
    public static final int INT_TEXT_LENGTH = 7;
    public static final int INT_CUSTOM = 8;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_NONE = 1;
        static final int INT_WHOLE = 2;
        static final int INT_DECIMAL = 3;
        static final int INT_LIST = 4;
        static final int INT_DATE = 5;
        static final int INT_TIME = 6;
        static final int INT_TEXT_LENGTH = 7;
        static final int INT_CUSTOM = 8;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("none", 1), new Enum("whole", 2), new Enum("decimal", 3), new Enum("list", 4), new Enum("date", 5), new Enum("time", 6), new Enum("textLength", 7), new Enum("custom", 8)});
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

