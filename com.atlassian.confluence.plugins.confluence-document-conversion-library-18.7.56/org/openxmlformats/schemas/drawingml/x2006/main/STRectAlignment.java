/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STRectAlignment
extends XmlToken {
    public static final SimpleTypeFactory<STRectAlignment> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "strectalignmentd400type");
    public static final SchemaType type = Factory.getType();
    public static final Enum TL = Enum.forString("tl");
    public static final Enum T = Enum.forString("t");
    public static final Enum TR = Enum.forString("tr");
    public static final Enum L = Enum.forString("l");
    public static final Enum CTR = Enum.forString("ctr");
    public static final Enum R = Enum.forString("r");
    public static final Enum BL = Enum.forString("bl");
    public static final Enum B = Enum.forString("b");
    public static final Enum BR = Enum.forString("br");
    public static final int INT_TL = 1;
    public static final int INT_T = 2;
    public static final int INT_TR = 3;
    public static final int INT_L = 4;
    public static final int INT_CTR = 5;
    public static final int INT_R = 6;
    public static final int INT_BL = 7;
    public static final int INT_B = 8;
    public static final int INT_BR = 9;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_TL = 1;
        static final int INT_T = 2;
        static final int INT_TR = 3;
        static final int INT_L = 4;
        static final int INT_CTR = 5;
        static final int INT_R = 6;
        static final int INT_BL = 7;
        static final int INT_B = 8;
        static final int INT_BR = 9;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("tl", 1), new Enum("t", 2), new Enum("tr", 3), new Enum("l", 4), new Enum("ctr", 5), new Enum("r", 6), new Enum("bl", 7), new Enum("b", 8), new Enum("br", 9)});
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

