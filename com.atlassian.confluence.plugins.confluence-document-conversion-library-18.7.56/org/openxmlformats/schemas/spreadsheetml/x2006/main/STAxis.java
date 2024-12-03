/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STAxis
extends XmlString {
    public static final SimpleTypeFactory<STAxis> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "staxis45batype");
    public static final SchemaType type = Factory.getType();
    public static final Enum AXIS_ROW = Enum.forString("axisRow");
    public static final Enum AXIS_COL = Enum.forString("axisCol");
    public static final Enum AXIS_PAGE = Enum.forString("axisPage");
    public static final Enum AXIS_VALUES = Enum.forString("axisValues");
    public static final int INT_AXIS_ROW = 1;
    public static final int INT_AXIS_COL = 2;
    public static final int INT_AXIS_PAGE = 3;
    public static final int INT_AXIS_VALUES = 4;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_AXIS_ROW = 1;
        static final int INT_AXIS_COL = 2;
        static final int INT_AXIS_PAGE = 3;
        static final int INT_AXIS_VALUES = 4;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("axisRow", 1), new Enum("axisCol", 2), new Enum("axisPage", 3), new Enum("axisValues", 4)});
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

