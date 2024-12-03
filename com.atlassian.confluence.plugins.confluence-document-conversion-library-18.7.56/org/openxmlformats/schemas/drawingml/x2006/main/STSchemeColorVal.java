/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STSchemeColorVal
extends XmlToken {
    public static final SimpleTypeFactory<STSchemeColorVal> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stschemecolorval722etype");
    public static final SchemaType type = Factory.getType();
    public static final Enum BG_1 = Enum.forString("bg1");
    public static final Enum TX_1 = Enum.forString("tx1");
    public static final Enum BG_2 = Enum.forString("bg2");
    public static final Enum TX_2 = Enum.forString("tx2");
    public static final Enum ACCENT_1 = Enum.forString("accent1");
    public static final Enum ACCENT_2 = Enum.forString("accent2");
    public static final Enum ACCENT_3 = Enum.forString("accent3");
    public static final Enum ACCENT_4 = Enum.forString("accent4");
    public static final Enum ACCENT_5 = Enum.forString("accent5");
    public static final Enum ACCENT_6 = Enum.forString("accent6");
    public static final Enum HLINK = Enum.forString("hlink");
    public static final Enum FOL_HLINK = Enum.forString("folHlink");
    public static final Enum PH_CLR = Enum.forString("phClr");
    public static final Enum DK_1 = Enum.forString("dk1");
    public static final Enum LT_1 = Enum.forString("lt1");
    public static final Enum DK_2 = Enum.forString("dk2");
    public static final Enum LT_2 = Enum.forString("lt2");
    public static final int INT_BG_1 = 1;
    public static final int INT_TX_1 = 2;
    public static final int INT_BG_2 = 3;
    public static final int INT_TX_2 = 4;
    public static final int INT_ACCENT_1 = 5;
    public static final int INT_ACCENT_2 = 6;
    public static final int INT_ACCENT_3 = 7;
    public static final int INT_ACCENT_4 = 8;
    public static final int INT_ACCENT_5 = 9;
    public static final int INT_ACCENT_6 = 10;
    public static final int INT_HLINK = 11;
    public static final int INT_FOL_HLINK = 12;
    public static final int INT_PH_CLR = 13;
    public static final int INT_DK_1 = 14;
    public static final int INT_LT_1 = 15;
    public static final int INT_DK_2 = 16;
    public static final int INT_LT_2 = 17;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_BG_1 = 1;
        static final int INT_TX_1 = 2;
        static final int INT_BG_2 = 3;
        static final int INT_TX_2 = 4;
        static final int INT_ACCENT_1 = 5;
        static final int INT_ACCENT_2 = 6;
        static final int INT_ACCENT_3 = 7;
        static final int INT_ACCENT_4 = 8;
        static final int INT_ACCENT_5 = 9;
        static final int INT_ACCENT_6 = 10;
        static final int INT_HLINK = 11;
        static final int INT_FOL_HLINK = 12;
        static final int INT_PH_CLR = 13;
        static final int INT_DK_1 = 14;
        static final int INT_LT_1 = 15;
        static final int INT_DK_2 = 16;
        static final int INT_LT_2 = 17;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("bg1", 1), new Enum("tx1", 2), new Enum("bg2", 3), new Enum("tx2", 4), new Enum("accent1", 5), new Enum("accent2", 6), new Enum("accent3", 7), new Enum("accent4", 8), new Enum("accent5", 9), new Enum("accent6", 10), new Enum("hlink", 11), new Enum("folHlink", 12), new Enum("phClr", 13), new Enum("dk1", 14), new Enum("lt1", 15), new Enum("dk2", 16), new Enum("lt2", 17)});
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

