/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STTextDirection
extends XmlString {
    public static final SimpleTypeFactory<STTextDirection> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "sttextdirectionf150type");
    public static final SchemaType type = Factory.getType();
    public static final Enum TB = Enum.forString("tb");
    public static final Enum RL = Enum.forString("rl");
    public static final Enum LR = Enum.forString("lr");
    public static final Enum TB_V = Enum.forString("tbV");
    public static final Enum RL_V = Enum.forString("rlV");
    public static final Enum LR_V = Enum.forString("lrV");
    public static final Enum BT_LR = Enum.forString("btLr");
    public static final Enum LR_TB = Enum.forString("lrTb");
    public static final Enum LR_TB_V = Enum.forString("lrTbV");
    public static final Enum TB_LR_V = Enum.forString("tbLrV");
    public static final Enum TB_RL = Enum.forString("tbRl");
    public static final Enum TB_RL_V = Enum.forString("tbRlV");
    public static final int INT_TB = 1;
    public static final int INT_RL = 2;
    public static final int INT_LR = 3;
    public static final int INT_TB_V = 4;
    public static final int INT_RL_V = 5;
    public static final int INT_LR_V = 6;
    public static final int INT_BT_LR = 7;
    public static final int INT_LR_TB = 8;
    public static final int INT_LR_TB_V = 9;
    public static final int INT_TB_LR_V = 10;
    public static final int INT_TB_RL = 11;
    public static final int INT_TB_RL_V = 12;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_TB = 1;
        static final int INT_RL = 2;
        static final int INT_LR = 3;
        static final int INT_TB_V = 4;
        static final int INT_RL_V = 5;
        static final int INT_LR_V = 6;
        static final int INT_BT_LR = 7;
        static final int INT_LR_TB = 8;
        static final int INT_LR_TB_V = 9;
        static final int INT_TB_LR_V = 10;
        static final int INT_TB_RL = 11;
        static final int INT_TB_RL_V = 12;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("tb", 1), new Enum("rl", 2), new Enum("lr", 3), new Enum("tbV", 4), new Enum("rlV", 5), new Enum("lrV", 6), new Enum("btLr", 7), new Enum("lrTb", 8), new Enum("lrTbV", 9), new Enum("tbLrV", 10), new Enum("tbRl", 11), new Enum("tbRlV", 12)});
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

