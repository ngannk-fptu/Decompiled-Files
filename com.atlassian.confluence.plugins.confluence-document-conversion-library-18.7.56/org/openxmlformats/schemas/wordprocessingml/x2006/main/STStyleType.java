/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STStyleType
extends XmlString {
    public static final SimpleTypeFactory<STStyleType> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "ststyletypec2b7type");
    public static final SchemaType type = Factory.getType();
    public static final Enum PARAGRAPH = Enum.forString("paragraph");
    public static final Enum CHARACTER = Enum.forString("character");
    public static final Enum TABLE = Enum.forString("table");
    public static final Enum NUMBERING = Enum.forString("numbering");
    public static final int INT_PARAGRAPH = 1;
    public static final int INT_CHARACTER = 2;
    public static final int INT_TABLE = 3;
    public static final int INT_NUMBERING = 4;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_PARAGRAPH = 1;
        static final int INT_CHARACTER = 2;
        static final int INT_TABLE = 3;
        static final int INT_NUMBERING = 4;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("paragraph", 1), new Enum("character", 2), new Enum("table", 3), new Enum("numbering", 4)});
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

