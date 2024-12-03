/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STDLblPos
extends XmlString {
    public static final SimpleTypeFactory<STDLblPos> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stdlblpos1cf4type");
    public static final SchemaType type = Factory.getType();
    public static final Enum BEST_FIT = Enum.forString("bestFit");
    public static final Enum B = Enum.forString("b");
    public static final Enum CTR = Enum.forString("ctr");
    public static final Enum IN_BASE = Enum.forString("inBase");
    public static final Enum IN_END = Enum.forString("inEnd");
    public static final Enum L = Enum.forString("l");
    public static final Enum OUT_END = Enum.forString("outEnd");
    public static final Enum R = Enum.forString("r");
    public static final Enum T = Enum.forString("t");
    public static final int INT_BEST_FIT = 1;
    public static final int INT_B = 2;
    public static final int INT_CTR = 3;
    public static final int INT_IN_BASE = 4;
    public static final int INT_IN_END = 5;
    public static final int INT_L = 6;
    public static final int INT_OUT_END = 7;
    public static final int INT_R = 8;
    public static final int INT_T = 9;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_BEST_FIT = 1;
        static final int INT_B = 2;
        static final int INT_CTR = 3;
        static final int INT_IN_BASE = 4;
        static final int INT_IN_END = 5;
        static final int INT_L = 6;
        static final int INT_OUT_END = 7;
        static final int INT_R = 8;
        static final int INT_T = 9;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("bestFit", 1), new Enum("b", 2), new Enum("ctr", 3), new Enum("inBase", 4), new Enum("inEnd", 5), new Enum("l", 6), new Enum("outEnd", 7), new Enum("r", 8), new Enum("t", 9)});
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

