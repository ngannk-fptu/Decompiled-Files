/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STProofErr
extends XmlString {
    public static final SimpleTypeFactory<STProofErr> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stprooferr6bf7type");
    public static final SchemaType type = Factory.getType();
    public static final Enum SPELL_START = Enum.forString("spellStart");
    public static final Enum SPELL_END = Enum.forString("spellEnd");
    public static final Enum GRAM_START = Enum.forString("gramStart");
    public static final Enum GRAM_END = Enum.forString("gramEnd");
    public static final int INT_SPELL_START = 1;
    public static final int INT_SPELL_END = 2;
    public static final int INT_GRAM_START = 3;
    public static final int INT_GRAM_END = 4;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_SPELL_START = 1;
        static final int INT_SPELL_END = 2;
        static final int INT_GRAM_START = 3;
        static final int INT_GRAM_END = 4;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("spellStart", 1), new Enum("spellEnd", 2), new Enum("gramStart", 3), new Enum("gramEnd", 4)});
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

