/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STSdtDateMappingType
extends XmlString {
    public static final SimpleTypeFactory<STSdtDateMappingType> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stsdtdatemappingtype01a1type");
    public static final SchemaType type = Factory.getType();
    public static final Enum TEXT = Enum.forString("text");
    public static final Enum DATE = Enum.forString("date");
    public static final Enum DATE_TIME = Enum.forString("dateTime");
    public static final int INT_TEXT = 1;
    public static final int INT_DATE = 2;
    public static final int INT_DATE_TIME = 3;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_TEXT = 1;
        static final int INT_DATE = 2;
        static final int INT_DATE_TIME = 3;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("text", 1), new Enum("date", 2), new Enum("dateTime", 3)});
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

