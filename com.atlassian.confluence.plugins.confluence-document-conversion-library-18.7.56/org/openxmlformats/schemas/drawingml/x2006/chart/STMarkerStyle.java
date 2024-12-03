/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STMarkerStyle
extends XmlString {
    public static final SimpleTypeFactory<STMarkerStyle> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stmarkerstyle177ftype");
    public static final SchemaType type = Factory.getType();
    public static final Enum CIRCLE = Enum.forString("circle");
    public static final Enum DASH = Enum.forString("dash");
    public static final Enum DIAMOND = Enum.forString("diamond");
    public static final Enum DOT = Enum.forString("dot");
    public static final Enum NONE = Enum.forString("none");
    public static final Enum PICTURE = Enum.forString("picture");
    public static final Enum PLUS = Enum.forString("plus");
    public static final Enum SQUARE = Enum.forString("square");
    public static final Enum STAR = Enum.forString("star");
    public static final Enum TRIANGLE = Enum.forString("triangle");
    public static final Enum X = Enum.forString("x");
    public static final Enum AUTO = Enum.forString("auto");
    public static final int INT_CIRCLE = 1;
    public static final int INT_DASH = 2;
    public static final int INT_DIAMOND = 3;
    public static final int INT_DOT = 4;
    public static final int INT_NONE = 5;
    public static final int INT_PICTURE = 6;
    public static final int INT_PLUS = 7;
    public static final int INT_SQUARE = 8;
    public static final int INT_STAR = 9;
    public static final int INT_TRIANGLE = 10;
    public static final int INT_X = 11;
    public static final int INT_AUTO = 12;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_CIRCLE = 1;
        static final int INT_DASH = 2;
        static final int INT_DIAMOND = 3;
        static final int INT_DOT = 4;
        static final int INT_NONE = 5;
        static final int INT_PICTURE = 6;
        static final int INT_PLUS = 7;
        static final int INT_SQUARE = 8;
        static final int INT_STAR = 9;
        static final int INT_TRIANGLE = 10;
        static final int INT_X = 11;
        static final int INT_AUTO = 12;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("circle", 1), new Enum("dash", 2), new Enum("diamond", 3), new Enum("dot", 4), new Enum("none", 5), new Enum("picture", 6), new Enum("plus", 7), new Enum("square", 8), new Enum("star", 9), new Enum("triangle", 10), new Enum("x", 11), new Enum("auto", 12)});
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

