/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STPathFillMode
extends XmlToken {
    public static final SimpleTypeFactory<STPathFillMode> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stpathfillmode3cf6type");
    public static final SchemaType type = Factory.getType();
    public static final Enum NONE = Enum.forString("none");
    public static final Enum NORM = Enum.forString("norm");
    public static final Enum LIGHTEN = Enum.forString("lighten");
    public static final Enum LIGHTEN_LESS = Enum.forString("lightenLess");
    public static final Enum DARKEN = Enum.forString("darken");
    public static final Enum DARKEN_LESS = Enum.forString("darkenLess");
    public static final int INT_NONE = 1;
    public static final int INT_NORM = 2;
    public static final int INT_LIGHTEN = 3;
    public static final int INT_LIGHTEN_LESS = 4;
    public static final int INT_DARKEN = 5;
    public static final int INT_DARKEN_LESS = 6;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_NONE = 1;
        static final int INT_NORM = 2;
        static final int INT_LIGHTEN = 3;
        static final int INT_LIGHTEN_LESS = 4;
        static final int INT_DARKEN = 5;
        static final int INT_DARKEN_LESS = 6;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("none", 1), new Enum("norm", 2), new Enum("lighten", 3), new Enum("lightenLess", 4), new Enum("darken", 5), new Enum("darkenLess", 6)});
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

