/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STBrType
extends XmlString {
    public static final SimpleTypeFactory<STBrType> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stbrtypeb52etype");
    public static final SchemaType type = Factory.getType();
    public static final Enum PAGE = Enum.forString("page");
    public static final Enum COLUMN = Enum.forString("column");
    public static final Enum TEXT_WRAPPING = Enum.forString("textWrapping");
    public static final int INT_PAGE = 1;
    public static final int INT_COLUMN = 2;
    public static final int INT_TEXT_WRAPPING = 3;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_PAGE = 1;
        static final int INT_COLUMN = 2;
        static final int INT_TEXT_WRAPPING = 3;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("page", 1), new Enum("column", 2), new Enum("textWrapping", 3)});
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

