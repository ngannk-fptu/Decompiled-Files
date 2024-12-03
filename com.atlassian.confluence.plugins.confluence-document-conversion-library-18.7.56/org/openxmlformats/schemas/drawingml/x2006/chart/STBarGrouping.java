/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STBarGrouping
extends XmlString {
    public static final SimpleTypeFactory<STBarGrouping> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stbargrouping8400type");
    public static final SchemaType type = Factory.getType();
    public static final Enum PERCENT_STACKED = Enum.forString("percentStacked");
    public static final Enum CLUSTERED = Enum.forString("clustered");
    public static final Enum STANDARD = Enum.forString("standard");
    public static final Enum STACKED = Enum.forString("stacked");
    public static final int INT_PERCENT_STACKED = 1;
    public static final int INT_CLUSTERED = 2;
    public static final int INT_STANDARD = 3;
    public static final int INT_STACKED = 4;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_PERCENT_STACKED = 1;
        static final int INT_CLUSTERED = 2;
        static final int INT_STANDARD = 3;
        static final int INT_STACKED = 4;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("percentStacked", 1), new Enum("clustered", 2), new Enum("standard", 3), new Enum("stacked", 4)});
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

