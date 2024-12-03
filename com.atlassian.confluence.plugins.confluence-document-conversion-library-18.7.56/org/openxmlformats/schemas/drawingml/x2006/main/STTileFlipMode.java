/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STTileFlipMode
extends XmlToken {
    public static final SimpleTypeFactory<STTileFlipMode> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "sttileflipmode2429type");
    public static final SchemaType type = Factory.getType();
    public static final Enum NONE = Enum.forString("none");
    public static final Enum X = Enum.forString("x");
    public static final Enum Y = Enum.forString("y");
    public static final Enum XY = Enum.forString("xy");
    public static final int INT_NONE = 1;
    public static final int INT_X = 2;
    public static final int INT_Y = 3;
    public static final int INT_XY = 4;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_NONE = 1;
        static final int INT_X = 2;
        static final int INT_Y = 3;
        static final int INT_XY = 4;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("none", 1), new Enum("x", 2), new Enum("y", 3), new Enum("xy", 4)});
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

