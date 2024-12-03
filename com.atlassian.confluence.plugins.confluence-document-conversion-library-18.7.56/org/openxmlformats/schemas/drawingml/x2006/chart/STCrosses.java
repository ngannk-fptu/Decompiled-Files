/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STCrosses
extends XmlString {
    public static final SimpleTypeFactory<STCrosses> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stcrosses3cc8type");
    public static final SchemaType type = Factory.getType();
    public static final Enum AUTO_ZERO = Enum.forString("autoZero");
    public static final Enum MAX = Enum.forString("max");
    public static final Enum MIN = Enum.forString("min");
    public static final int INT_AUTO_ZERO = 1;
    public static final int INT_MAX = 2;
    public static final int INT_MIN = 3;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_AUTO_ZERO = 1;
        static final int INT_MAX = 2;
        static final int INT_MIN = 3;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("autoZero", 1), new Enum("max", 2), new Enum("min", 3)});
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

