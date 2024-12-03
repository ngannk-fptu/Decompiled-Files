/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STSlideSizeType
extends XmlToken {
    public static final SimpleTypeFactory<STSlideSizeType> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stslidesizetypeb7f3type");
    public static final SchemaType type = Factory.getType();
    public static final Enum SCREEN_4_X_3 = Enum.forString("screen4x3");
    public static final Enum LETTER = Enum.forString("letter");
    public static final Enum A_4 = Enum.forString("A4");
    public static final Enum X_35_MM = Enum.forString("35mm");
    public static final Enum OVERHEAD = Enum.forString("overhead");
    public static final Enum BANNER = Enum.forString("banner");
    public static final Enum CUSTOM = Enum.forString("custom");
    public static final Enum LEDGER = Enum.forString("ledger");
    public static final Enum A_3 = Enum.forString("A3");
    public static final Enum B_4_ISO = Enum.forString("B4ISO");
    public static final Enum B_5_ISO = Enum.forString("B5ISO");
    public static final Enum B_4_JIS = Enum.forString("B4JIS");
    public static final Enum B_5_JIS = Enum.forString("B5JIS");
    public static final Enum HAGAKI_CARD = Enum.forString("hagakiCard");
    public static final Enum SCREEN_16_X_9 = Enum.forString("screen16x9");
    public static final Enum SCREEN_16_X_10 = Enum.forString("screen16x10");
    public static final int INT_SCREEN_4_X_3 = 1;
    public static final int INT_LETTER = 2;
    public static final int INT_A_4 = 3;
    public static final int INT_X_35_MM = 4;
    public static final int INT_OVERHEAD = 5;
    public static final int INT_BANNER = 6;
    public static final int INT_CUSTOM = 7;
    public static final int INT_LEDGER = 8;
    public static final int INT_A_3 = 9;
    public static final int INT_B_4_ISO = 10;
    public static final int INT_B_5_ISO = 11;
    public static final int INT_B_4_JIS = 12;
    public static final int INT_B_5_JIS = 13;
    public static final int INT_HAGAKI_CARD = 14;
    public static final int INT_SCREEN_16_X_9 = 15;
    public static final int INT_SCREEN_16_X_10 = 16;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_SCREEN_4_X_3 = 1;
        static final int INT_LETTER = 2;
        static final int INT_A_4 = 3;
        static final int INT_X_35_MM = 4;
        static final int INT_OVERHEAD = 5;
        static final int INT_BANNER = 6;
        static final int INT_CUSTOM = 7;
        static final int INT_LEDGER = 8;
        static final int INT_A_3 = 9;
        static final int INT_B_4_ISO = 10;
        static final int INT_B_5_ISO = 11;
        static final int INT_B_4_JIS = 12;
        static final int INT_B_5_JIS = 13;
        static final int INT_HAGAKI_CARD = 14;
        static final int INT_SCREEN_16_X_9 = 15;
        static final int INT_SCREEN_16_X_10 = 16;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("screen4x3", 1), new Enum("letter", 2), new Enum("A4", 3), new Enum("35mm", 4), new Enum("overhead", 5), new Enum("banner", 6), new Enum("custom", 7), new Enum("ledger", 8), new Enum("A3", 9), new Enum("B4ISO", 10), new Enum("B5ISO", 11), new Enum("B4JIS", 12), new Enum("B5JIS", 13), new Enum("hagakiCard", 14), new Enum("screen16x9", 15), new Enum("screen16x10", 16)});
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

