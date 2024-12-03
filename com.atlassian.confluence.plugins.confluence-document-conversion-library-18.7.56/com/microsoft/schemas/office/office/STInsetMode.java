/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.office;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STInsetMode
extends XmlString {
    public static final SimpleTypeFactory<STInsetMode> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stinsetmode3b89type");
    public static final SchemaType type = Factory.getType();
    public static final Enum AUTO = Enum.forString("auto");
    public static final Enum CUSTOM = Enum.forString("custom");
    public static final int INT_AUTO = 1;
    public static final int INT_CUSTOM = 2;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_AUTO = 1;
        static final int INT_CUSTOM = 2;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("auto", 1), new Enum("custom", 2)});
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

