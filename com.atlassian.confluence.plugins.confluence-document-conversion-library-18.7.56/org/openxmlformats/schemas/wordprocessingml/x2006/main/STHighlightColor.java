/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STHighlightColor
extends XmlString {
    public static final SimpleTypeFactory<STHighlightColor> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "sthighlightcolora8e9type");
    public static final SchemaType type = Factory.getType();
    public static final Enum BLACK = Enum.forString("black");
    public static final Enum BLUE = Enum.forString("blue");
    public static final Enum CYAN = Enum.forString("cyan");
    public static final Enum GREEN = Enum.forString("green");
    public static final Enum MAGENTA = Enum.forString("magenta");
    public static final Enum RED = Enum.forString("red");
    public static final Enum YELLOW = Enum.forString("yellow");
    public static final Enum WHITE = Enum.forString("white");
    public static final Enum DARK_BLUE = Enum.forString("darkBlue");
    public static final Enum DARK_CYAN = Enum.forString("darkCyan");
    public static final Enum DARK_GREEN = Enum.forString("darkGreen");
    public static final Enum DARK_MAGENTA = Enum.forString("darkMagenta");
    public static final Enum DARK_RED = Enum.forString("darkRed");
    public static final Enum DARK_YELLOW = Enum.forString("darkYellow");
    public static final Enum DARK_GRAY = Enum.forString("darkGray");
    public static final Enum LIGHT_GRAY = Enum.forString("lightGray");
    public static final Enum NONE = Enum.forString("none");
    public static final int INT_BLACK = 1;
    public static final int INT_BLUE = 2;
    public static final int INT_CYAN = 3;
    public static final int INT_GREEN = 4;
    public static final int INT_MAGENTA = 5;
    public static final int INT_RED = 6;
    public static final int INT_YELLOW = 7;
    public static final int INT_WHITE = 8;
    public static final int INT_DARK_BLUE = 9;
    public static final int INT_DARK_CYAN = 10;
    public static final int INT_DARK_GREEN = 11;
    public static final int INT_DARK_MAGENTA = 12;
    public static final int INT_DARK_RED = 13;
    public static final int INT_DARK_YELLOW = 14;
    public static final int INT_DARK_GRAY = 15;
    public static final int INT_LIGHT_GRAY = 16;
    public static final int INT_NONE = 17;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_BLACK = 1;
        static final int INT_BLUE = 2;
        static final int INT_CYAN = 3;
        static final int INT_GREEN = 4;
        static final int INT_MAGENTA = 5;
        static final int INT_RED = 6;
        static final int INT_YELLOW = 7;
        static final int INT_WHITE = 8;
        static final int INT_DARK_BLUE = 9;
        static final int INT_DARK_CYAN = 10;
        static final int INT_DARK_GREEN = 11;
        static final int INT_DARK_MAGENTA = 12;
        static final int INT_DARK_RED = 13;
        static final int INT_DARK_YELLOW = 14;
        static final int INT_DARK_GRAY = 15;
        static final int INT_LIGHT_GRAY = 16;
        static final int INT_NONE = 17;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("black", 1), new Enum("blue", 2), new Enum("cyan", 3), new Enum("green", 4), new Enum("magenta", 5), new Enum("red", 6), new Enum("yellow", 7), new Enum("white", 8), new Enum("darkBlue", 9), new Enum("darkCyan", 10), new Enum("darkGreen", 11), new Enum("darkMagenta", 12), new Enum("darkRed", 13), new Enum("darkYellow", 14), new Enum("darkGray", 15), new Enum("lightGray", 16), new Enum("none", 17)});
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

