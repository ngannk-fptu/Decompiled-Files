/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STPane
extends XmlString {
    public static final SimpleTypeFactory<STPane> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stpane2ac1type");
    public static final SchemaType type = Factory.getType();
    public static final Enum BOTTOM_RIGHT = Enum.forString("bottomRight");
    public static final Enum TOP_RIGHT = Enum.forString("topRight");
    public static final Enum BOTTOM_LEFT = Enum.forString("bottomLeft");
    public static final Enum TOP_LEFT = Enum.forString("topLeft");
    public static final int INT_BOTTOM_RIGHT = 1;
    public static final int INT_TOP_RIGHT = 2;
    public static final int INT_BOTTOM_LEFT = 3;
    public static final int INT_TOP_LEFT = 4;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_BOTTOM_RIGHT = 1;
        static final int INT_TOP_RIGHT = 2;
        static final int INT_BOTTOM_LEFT = 3;
        static final int INT_TOP_LEFT = 4;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("bottomRight", 1), new Enum("topRight", 2), new Enum("bottomLeft", 3), new Enum("topLeft", 4)});
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

