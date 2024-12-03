/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.vml;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STEditAs
extends XmlString {
    public static final SimpleTypeFactory<STEditAs> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "steditas85aatype");
    public static final SchemaType type = Factory.getType();
    public static final Enum CANVAS = Enum.forString("canvas");
    public static final Enum ORGCHART = Enum.forString("orgchart");
    public static final Enum RADIAL = Enum.forString("radial");
    public static final Enum CYCLE = Enum.forString("cycle");
    public static final Enum STACKED = Enum.forString("stacked");
    public static final Enum VENN = Enum.forString("venn");
    public static final Enum BULLSEYE = Enum.forString("bullseye");
    public static final int INT_CANVAS = 1;
    public static final int INT_ORGCHART = 2;
    public static final int INT_RADIAL = 3;
    public static final int INT_CYCLE = 4;
    public static final int INT_STACKED = 5;
    public static final int INT_VENN = 6;
    public static final int INT_BULLSEYE = 7;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_CANVAS = 1;
        static final int INT_ORGCHART = 2;
        static final int INT_RADIAL = 3;
        static final int INT_CYCLE = 4;
        static final int INT_STACKED = 5;
        static final int INT_VENN = 6;
        static final int INT_BULLSEYE = 7;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("canvas", 1), new Enum("orgchart", 2), new Enum("radial", 3), new Enum("cycle", 4), new Enum("stacked", 5), new Enum("venn", 6), new Enum("bullseye", 7)});
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

