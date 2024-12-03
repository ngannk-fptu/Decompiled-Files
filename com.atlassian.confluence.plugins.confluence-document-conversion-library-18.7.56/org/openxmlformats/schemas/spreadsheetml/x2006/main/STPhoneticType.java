/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STPhoneticType
extends XmlString {
    public static final SimpleTypeFactory<STPhoneticType> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stphonetictype4f93type");
    public static final SchemaType type = Factory.getType();
    public static final Enum HALFWIDTH_KATAKANA = Enum.forString("halfwidthKatakana");
    public static final Enum FULLWIDTH_KATAKANA = Enum.forString("fullwidthKatakana");
    public static final Enum HIRAGANA = Enum.forString("Hiragana");
    public static final Enum NO_CONVERSION = Enum.forString("noConversion");
    public static final int INT_HALFWIDTH_KATAKANA = 1;
    public static final int INT_FULLWIDTH_KATAKANA = 2;
    public static final int INT_HIRAGANA = 3;
    public static final int INT_NO_CONVERSION = 4;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_HALFWIDTH_KATAKANA = 1;
        static final int INT_FULLWIDTH_KATAKANA = 2;
        static final int INT_HIRAGANA = 3;
        static final int INT_NO_CONVERSION = 4;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("halfwidthKatakana", 1), new Enum("fullwidthKatakana", 2), new Enum("Hiragana", 3), new Enum("noConversion", 4)});
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

