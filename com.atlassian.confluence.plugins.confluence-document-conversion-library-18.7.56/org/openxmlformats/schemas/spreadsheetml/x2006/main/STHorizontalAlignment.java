/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STHorizontalAlignment
extends XmlString {
    public static final SimpleTypeFactory<STHorizontalAlignment> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "sthorizontalalignmentf92etype");
    public static final SchemaType type = Factory.getType();
    public static final Enum GENERAL = Enum.forString("general");
    public static final Enum LEFT = Enum.forString("left");
    public static final Enum CENTER = Enum.forString("center");
    public static final Enum RIGHT = Enum.forString("right");
    public static final Enum FILL = Enum.forString("fill");
    public static final Enum JUSTIFY = Enum.forString("justify");
    public static final Enum CENTER_CONTINUOUS = Enum.forString("centerContinuous");
    public static final Enum DISTRIBUTED = Enum.forString("distributed");
    public static final int INT_GENERAL = 1;
    public static final int INT_LEFT = 2;
    public static final int INT_CENTER = 3;
    public static final int INT_RIGHT = 4;
    public static final int INT_FILL = 5;
    public static final int INT_JUSTIFY = 6;
    public static final int INT_CENTER_CONTINUOUS = 7;
    public static final int INT_DISTRIBUTED = 8;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_GENERAL = 1;
        static final int INT_LEFT = 2;
        static final int INT_CENTER = 3;
        static final int INT_RIGHT = 4;
        static final int INT_FILL = 5;
        static final int INT_JUSTIFY = 6;
        static final int INT_CENTER_CONTINUOUS = 7;
        static final int INT_DISTRIBUTED = 8;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("general", 1), new Enum("left", 2), new Enum("center", 3), new Enum("right", 4), new Enum("fill", 5), new Enum("justify", 6), new Enum("centerContinuous", 7), new Enum("distributed", 8)});
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

