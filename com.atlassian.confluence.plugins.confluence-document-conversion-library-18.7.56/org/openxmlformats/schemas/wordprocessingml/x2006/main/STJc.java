/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STJc
extends XmlString {
    public static final SimpleTypeFactory<STJc> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stjc977ftype");
    public static final SchemaType type = Factory.getType();
    public static final Enum START = Enum.forString("start");
    public static final Enum CENTER = Enum.forString("center");
    public static final Enum END = Enum.forString("end");
    public static final Enum BOTH = Enum.forString("both");
    public static final Enum MEDIUM_KASHIDA = Enum.forString("mediumKashida");
    public static final Enum DISTRIBUTE = Enum.forString("distribute");
    public static final Enum NUM_TAB = Enum.forString("numTab");
    public static final Enum HIGH_KASHIDA = Enum.forString("highKashida");
    public static final Enum LOW_KASHIDA = Enum.forString("lowKashida");
    public static final Enum THAI_DISTRIBUTE = Enum.forString("thaiDistribute");
    public static final Enum LEFT = Enum.forString("left");
    public static final Enum RIGHT = Enum.forString("right");
    public static final int INT_START = 1;
    public static final int INT_CENTER = 2;
    public static final int INT_END = 3;
    public static final int INT_BOTH = 4;
    public static final int INT_MEDIUM_KASHIDA = 5;
    public static final int INT_DISTRIBUTE = 6;
    public static final int INT_NUM_TAB = 7;
    public static final int INT_HIGH_KASHIDA = 8;
    public static final int INT_LOW_KASHIDA = 9;
    public static final int INT_THAI_DISTRIBUTE = 10;
    public static final int INT_LEFT = 11;
    public static final int INT_RIGHT = 12;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_START = 1;
        static final int INT_CENTER = 2;
        static final int INT_END = 3;
        static final int INT_BOTH = 4;
        static final int INT_MEDIUM_KASHIDA = 5;
        static final int INT_DISTRIBUTE = 6;
        static final int INT_NUM_TAB = 7;
        static final int INT_HIGH_KASHIDA = 8;
        static final int INT_LOW_KASHIDA = 9;
        static final int INT_THAI_DISTRIBUTE = 10;
        static final int INT_LEFT = 11;
        static final int INT_RIGHT = 12;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("start", 1), new Enum("center", 2), new Enum("end", 3), new Enum("both", 4), new Enum("mediumKashida", 5), new Enum("distribute", 6), new Enum("numTab", 7), new Enum("highKashida", 8), new Enum("lowKashida", 9), new Enum("thaiDistribute", 10), new Enum("left", 11), new Enum("right", 12)});
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

