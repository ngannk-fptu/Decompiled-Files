/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STPresetLineDashVal
extends XmlToken {
    public static final SimpleTypeFactory<STPresetLineDashVal> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stpresetlinedashval159dtype");
    public static final SchemaType type = Factory.getType();
    public static final Enum SOLID = Enum.forString("solid");
    public static final Enum DOT = Enum.forString("dot");
    public static final Enum DASH = Enum.forString("dash");
    public static final Enum LG_DASH = Enum.forString("lgDash");
    public static final Enum DASH_DOT = Enum.forString("dashDot");
    public static final Enum LG_DASH_DOT = Enum.forString("lgDashDot");
    public static final Enum LG_DASH_DOT_DOT = Enum.forString("lgDashDotDot");
    public static final Enum SYS_DASH = Enum.forString("sysDash");
    public static final Enum SYS_DOT = Enum.forString("sysDot");
    public static final Enum SYS_DASH_DOT = Enum.forString("sysDashDot");
    public static final Enum SYS_DASH_DOT_DOT = Enum.forString("sysDashDotDot");
    public static final int INT_SOLID = 1;
    public static final int INT_DOT = 2;
    public static final int INT_DASH = 3;
    public static final int INT_LG_DASH = 4;
    public static final int INT_DASH_DOT = 5;
    public static final int INT_LG_DASH_DOT = 6;
    public static final int INT_LG_DASH_DOT_DOT = 7;
    public static final int INT_SYS_DASH = 8;
    public static final int INT_SYS_DOT = 9;
    public static final int INT_SYS_DASH_DOT = 10;
    public static final int INT_SYS_DASH_DOT_DOT = 11;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_SOLID = 1;
        static final int INT_DOT = 2;
        static final int INT_DASH = 3;
        static final int INT_LG_DASH = 4;
        static final int INT_DASH_DOT = 5;
        static final int INT_LG_DASH_DOT = 6;
        static final int INT_LG_DASH_DOT_DOT = 7;
        static final int INT_SYS_DASH = 8;
        static final int INT_SYS_DOT = 9;
        static final int INT_SYS_DASH_DOT = 10;
        static final int INT_SYS_DASH_DOT_DOT = 11;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("solid", 1), new Enum("dot", 2), new Enum("dash", 3), new Enum("lgDash", 4), new Enum("dashDot", 5), new Enum("lgDashDot", 6), new Enum("lgDashDotDot", 7), new Enum("sysDash", 8), new Enum("sysDot", 9), new Enum("sysDashDot", 10), new Enum("sysDashDotDot", 11)});
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

