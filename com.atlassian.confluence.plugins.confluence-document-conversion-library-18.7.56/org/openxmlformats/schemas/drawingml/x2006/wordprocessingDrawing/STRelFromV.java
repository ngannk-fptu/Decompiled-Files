/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STRelFromV
extends XmlToken {
    public static final SimpleTypeFactory<STRelFromV> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "strelfromv56dctype");
    public static final SchemaType type = Factory.getType();
    public static final Enum MARGIN = Enum.forString("margin");
    public static final Enum PAGE = Enum.forString("page");
    public static final Enum PARAGRAPH = Enum.forString("paragraph");
    public static final Enum LINE = Enum.forString("line");
    public static final Enum TOP_MARGIN = Enum.forString("topMargin");
    public static final Enum BOTTOM_MARGIN = Enum.forString("bottomMargin");
    public static final Enum INSIDE_MARGIN = Enum.forString("insideMargin");
    public static final Enum OUTSIDE_MARGIN = Enum.forString("outsideMargin");
    public static final int INT_MARGIN = 1;
    public static final int INT_PAGE = 2;
    public static final int INT_PARAGRAPH = 3;
    public static final int INT_LINE = 4;
    public static final int INT_TOP_MARGIN = 5;
    public static final int INT_BOTTOM_MARGIN = 6;
    public static final int INT_INSIDE_MARGIN = 7;
    public static final int INT_OUTSIDE_MARGIN = 8;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_MARGIN = 1;
        static final int INT_PAGE = 2;
        static final int INT_PARAGRAPH = 3;
        static final int INT_LINE = 4;
        static final int INT_TOP_MARGIN = 5;
        static final int INT_BOTTOM_MARGIN = 6;
        static final int INT_INSIDE_MARGIN = 7;
        static final int INT_OUTSIDE_MARGIN = 8;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("margin", 1), new Enum("page", 2), new Enum("paragraph", 3), new Enum("line", 4), new Enum("topMargin", 5), new Enum("bottomMargin", 6), new Enum("insideMargin", 7), new Enum("outsideMargin", 8)});
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

