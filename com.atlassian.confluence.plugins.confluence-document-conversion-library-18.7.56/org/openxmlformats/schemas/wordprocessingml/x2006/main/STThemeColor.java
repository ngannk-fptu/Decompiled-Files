/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STThemeColor
extends XmlString {
    public static final SimpleTypeFactory<STThemeColor> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stthemecolor063etype");
    public static final SchemaType type = Factory.getType();
    public static final Enum DARK_1 = Enum.forString("dark1");
    public static final Enum LIGHT_1 = Enum.forString("light1");
    public static final Enum DARK_2 = Enum.forString("dark2");
    public static final Enum LIGHT_2 = Enum.forString("light2");
    public static final Enum ACCENT_1 = Enum.forString("accent1");
    public static final Enum ACCENT_2 = Enum.forString("accent2");
    public static final Enum ACCENT_3 = Enum.forString("accent3");
    public static final Enum ACCENT_4 = Enum.forString("accent4");
    public static final Enum ACCENT_5 = Enum.forString("accent5");
    public static final Enum ACCENT_6 = Enum.forString("accent6");
    public static final Enum HYPERLINK = Enum.forString("hyperlink");
    public static final Enum FOLLOWED_HYPERLINK = Enum.forString("followedHyperlink");
    public static final Enum NONE = Enum.forString("none");
    public static final Enum BACKGROUND_1 = Enum.forString("background1");
    public static final Enum TEXT_1 = Enum.forString("text1");
    public static final Enum BACKGROUND_2 = Enum.forString("background2");
    public static final Enum TEXT_2 = Enum.forString("text2");
    public static final int INT_DARK_1 = 1;
    public static final int INT_LIGHT_1 = 2;
    public static final int INT_DARK_2 = 3;
    public static final int INT_LIGHT_2 = 4;
    public static final int INT_ACCENT_1 = 5;
    public static final int INT_ACCENT_2 = 6;
    public static final int INT_ACCENT_3 = 7;
    public static final int INT_ACCENT_4 = 8;
    public static final int INT_ACCENT_5 = 9;
    public static final int INT_ACCENT_6 = 10;
    public static final int INT_HYPERLINK = 11;
    public static final int INT_FOLLOWED_HYPERLINK = 12;
    public static final int INT_NONE = 13;
    public static final int INT_BACKGROUND_1 = 14;
    public static final int INT_TEXT_1 = 15;
    public static final int INT_BACKGROUND_2 = 16;
    public static final int INT_TEXT_2 = 17;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_DARK_1 = 1;
        static final int INT_LIGHT_1 = 2;
        static final int INT_DARK_2 = 3;
        static final int INT_LIGHT_2 = 4;
        static final int INT_ACCENT_1 = 5;
        static final int INT_ACCENT_2 = 6;
        static final int INT_ACCENT_3 = 7;
        static final int INT_ACCENT_4 = 8;
        static final int INT_ACCENT_5 = 9;
        static final int INT_ACCENT_6 = 10;
        static final int INT_HYPERLINK = 11;
        static final int INT_FOLLOWED_HYPERLINK = 12;
        static final int INT_NONE = 13;
        static final int INT_BACKGROUND_1 = 14;
        static final int INT_TEXT_1 = 15;
        static final int INT_BACKGROUND_2 = 16;
        static final int INT_TEXT_2 = 17;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("dark1", 1), new Enum("light1", 2), new Enum("dark2", 3), new Enum("light2", 4), new Enum("accent1", 5), new Enum("accent2", 6), new Enum("accent3", 7), new Enum("accent4", 8), new Enum("accent5", 9), new Enum("accent6", 10), new Enum("hyperlink", 11), new Enum("followedHyperlink", 12), new Enum("none", 13), new Enum("background1", 14), new Enum("text1", 15), new Enum("background2", 16), new Enum("text2", 17)});
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

