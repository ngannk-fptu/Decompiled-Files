/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STBorderStyle
extends XmlString {
    public static final SimpleTypeFactory<STBorderStyle> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stborderstylec774type");
    public static final SchemaType type = Factory.getType();
    public static final Enum NONE = Enum.forString("none");
    public static final Enum THIN = Enum.forString("thin");
    public static final Enum MEDIUM = Enum.forString("medium");
    public static final Enum DASHED = Enum.forString("dashed");
    public static final Enum DOTTED = Enum.forString("dotted");
    public static final Enum THICK = Enum.forString("thick");
    public static final Enum DOUBLE = Enum.forString("double");
    public static final Enum HAIR = Enum.forString("hair");
    public static final Enum MEDIUM_DASHED = Enum.forString("mediumDashed");
    public static final Enum DASH_DOT = Enum.forString("dashDot");
    public static final Enum MEDIUM_DASH_DOT = Enum.forString("mediumDashDot");
    public static final Enum DASH_DOT_DOT = Enum.forString("dashDotDot");
    public static final Enum MEDIUM_DASH_DOT_DOT = Enum.forString("mediumDashDotDot");
    public static final Enum SLANT_DASH_DOT = Enum.forString("slantDashDot");
    public static final int INT_NONE = 1;
    public static final int INT_THIN = 2;
    public static final int INT_MEDIUM = 3;
    public static final int INT_DASHED = 4;
    public static final int INT_DOTTED = 5;
    public static final int INT_THICK = 6;
    public static final int INT_DOUBLE = 7;
    public static final int INT_HAIR = 8;
    public static final int INT_MEDIUM_DASHED = 9;
    public static final int INT_DASH_DOT = 10;
    public static final int INT_MEDIUM_DASH_DOT = 11;
    public static final int INT_DASH_DOT_DOT = 12;
    public static final int INT_MEDIUM_DASH_DOT_DOT = 13;
    public static final int INT_SLANT_DASH_DOT = 14;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_NONE = 1;
        static final int INT_THIN = 2;
        static final int INT_MEDIUM = 3;
        static final int INT_DASHED = 4;
        static final int INT_DOTTED = 5;
        static final int INT_THICK = 6;
        static final int INT_DOUBLE = 7;
        static final int INT_HAIR = 8;
        static final int INT_MEDIUM_DASHED = 9;
        static final int INT_DASH_DOT = 10;
        static final int INT_MEDIUM_DASH_DOT = 11;
        static final int INT_DASH_DOT_DOT = 12;
        static final int INT_MEDIUM_DASH_DOT_DOT = 13;
        static final int INT_SLANT_DASH_DOT = 14;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("none", 1), new Enum("thin", 2), new Enum("medium", 3), new Enum("dashed", 4), new Enum("dotted", 5), new Enum("thick", 6), new Enum("double", 7), new Enum("hair", 8), new Enum("mediumDashed", 9), new Enum("dashDot", 10), new Enum("mediumDashDot", 11), new Enum("dashDotDot", 12), new Enum("mediumDashDotDot", 13), new Enum("slantDashDot", 14)});
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

