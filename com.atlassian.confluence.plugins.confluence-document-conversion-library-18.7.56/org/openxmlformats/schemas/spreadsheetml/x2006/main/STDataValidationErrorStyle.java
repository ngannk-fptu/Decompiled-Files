/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STDataValidationErrorStyle
extends XmlString {
    public static final SimpleTypeFactory<STDataValidationErrorStyle> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stdatavalidationerrorstyleca85type");
    public static final SchemaType type = Factory.getType();
    public static final Enum STOP = Enum.forString("stop");
    public static final Enum WARNING = Enum.forString("warning");
    public static final Enum INFORMATION = Enum.forString("information");
    public static final int INT_STOP = 1;
    public static final int INT_WARNING = 2;
    public static final int INT_INFORMATION = 3;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_STOP = 1;
        static final int INT_WARNING = 2;
        static final int INT_INFORMATION = 3;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("stop", 1), new Enum("warning", 2), new Enum("information", 3)});
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

