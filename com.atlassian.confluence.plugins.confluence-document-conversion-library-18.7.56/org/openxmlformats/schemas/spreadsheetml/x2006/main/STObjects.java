/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STObjects
extends XmlString {
    public static final SimpleTypeFactory<STObjects> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stobjects5c63type");
    public static final SchemaType type = Factory.getType();
    public static final Enum ALL = Enum.forString("all");
    public static final Enum PLACEHOLDERS = Enum.forString("placeholders");
    public static final Enum NONE = Enum.forString("none");
    public static final int INT_ALL = 1;
    public static final int INT_PLACEHOLDERS = 2;
    public static final int INT_NONE = 3;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_ALL = 1;
        static final int INT_PLACEHOLDERS = 2;
        static final int INT_NONE = 3;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("all", 1), new Enum("placeholders", 2), new Enum("none", 3)});
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

