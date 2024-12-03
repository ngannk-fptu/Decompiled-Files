/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STTextVerticalType
extends XmlToken {
    public static final SimpleTypeFactory<STTextVerticalType> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "sttextverticaltyped988type");
    public static final SchemaType type = Factory.getType();
    public static final Enum HORZ = Enum.forString("horz");
    public static final Enum VERT = Enum.forString("vert");
    public static final Enum VERT_270 = Enum.forString("vert270");
    public static final Enum WORD_ART_VERT = Enum.forString("wordArtVert");
    public static final Enum EA_VERT = Enum.forString("eaVert");
    public static final Enum MONGOLIAN_VERT = Enum.forString("mongolianVert");
    public static final Enum WORD_ART_VERT_RTL = Enum.forString("wordArtVertRtl");
    public static final int INT_HORZ = 1;
    public static final int INT_VERT = 2;
    public static final int INT_VERT_270 = 3;
    public static final int INT_WORD_ART_VERT = 4;
    public static final int INT_EA_VERT = 5;
    public static final int INT_MONGOLIAN_VERT = 6;
    public static final int INT_WORD_ART_VERT_RTL = 7;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_HORZ = 1;
        static final int INT_VERT = 2;
        static final int INT_VERT_270 = 3;
        static final int INT_WORD_ART_VERT = 4;
        static final int INT_EA_VERT = 5;
        static final int INT_MONGOLIAN_VERT = 6;
        static final int INT_WORD_ART_VERT_RTL = 7;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("horz", 1), new Enum("vert", 2), new Enum("vert270", 3), new Enum("wordArtVert", 4), new Enum("eaVert", 5), new Enum("mongolianVert", 6), new Enum("wordArtVertRtl", 7)});
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

