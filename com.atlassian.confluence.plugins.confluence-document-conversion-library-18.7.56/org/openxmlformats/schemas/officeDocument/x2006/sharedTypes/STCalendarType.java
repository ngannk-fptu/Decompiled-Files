/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.officeDocument.x2006.sharedTypes;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STCalendarType
extends XmlString {
    public static final SimpleTypeFactory<STCalendarType> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stcalendartype8cd2type");
    public static final SchemaType type = Factory.getType();
    public static final Enum GREGORIAN = Enum.forString("gregorian");
    public static final Enum GREGORIAN_US = Enum.forString("gregorianUs");
    public static final Enum GREGORIAN_ME_FRENCH = Enum.forString("gregorianMeFrench");
    public static final Enum GREGORIAN_ARABIC = Enum.forString("gregorianArabic");
    public static final Enum HIJRI = Enum.forString("hijri");
    public static final Enum HEBREW = Enum.forString("hebrew");
    public static final Enum TAIWAN = Enum.forString("taiwan");
    public static final Enum JAPAN = Enum.forString("japan");
    public static final Enum THAI = Enum.forString("thai");
    public static final Enum KOREA = Enum.forString("korea");
    public static final Enum SAKA = Enum.forString("saka");
    public static final Enum GREGORIAN_XLIT_ENGLISH = Enum.forString("gregorianXlitEnglish");
    public static final Enum GREGORIAN_XLIT_FRENCH = Enum.forString("gregorianXlitFrench");
    public static final Enum NONE = Enum.forString("none");
    public static final int INT_GREGORIAN = 1;
    public static final int INT_GREGORIAN_US = 2;
    public static final int INT_GREGORIAN_ME_FRENCH = 3;
    public static final int INT_GREGORIAN_ARABIC = 4;
    public static final int INT_HIJRI = 5;
    public static final int INT_HEBREW = 6;
    public static final int INT_TAIWAN = 7;
    public static final int INT_JAPAN = 8;
    public static final int INT_THAI = 9;
    public static final int INT_KOREA = 10;
    public static final int INT_SAKA = 11;
    public static final int INT_GREGORIAN_XLIT_ENGLISH = 12;
    public static final int INT_GREGORIAN_XLIT_FRENCH = 13;
    public static final int INT_NONE = 14;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_GREGORIAN = 1;
        static final int INT_GREGORIAN_US = 2;
        static final int INT_GREGORIAN_ME_FRENCH = 3;
        static final int INT_GREGORIAN_ARABIC = 4;
        static final int INT_HIJRI = 5;
        static final int INT_HEBREW = 6;
        static final int INT_TAIWAN = 7;
        static final int INT_JAPAN = 8;
        static final int INT_THAI = 9;
        static final int INT_KOREA = 10;
        static final int INT_SAKA = 11;
        static final int INT_GREGORIAN_XLIT_ENGLISH = 12;
        static final int INT_GREGORIAN_XLIT_FRENCH = 13;
        static final int INT_NONE = 14;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("gregorian", 1), new Enum("gregorianUs", 2), new Enum("gregorianMeFrench", 3), new Enum("gregorianArabic", 4), new Enum("hijri", 5), new Enum("hebrew", 6), new Enum("taiwan", 7), new Enum("japan", 8), new Enum("thai", 9), new Enum("korea", 10), new Enum("saka", 11), new Enum("gregorianXlitEnglish", 12), new Enum("gregorianXlitFrench", 13), new Enum("none", 14)});
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

