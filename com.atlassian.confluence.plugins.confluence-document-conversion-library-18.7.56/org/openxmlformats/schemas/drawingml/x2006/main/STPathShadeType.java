/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STPathShadeType
extends XmlToken {
    public static final SimpleTypeFactory<STPathShadeType> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stpathshadetype93c3type");
    public static final SchemaType type = Factory.getType();
    public static final Enum SHAPE = Enum.forString("shape");
    public static final Enum CIRCLE = Enum.forString("circle");
    public static final Enum RECT = Enum.forString("rect");
    public static final int INT_SHAPE = 1;
    public static final int INT_CIRCLE = 2;
    public static final int INT_RECT = 3;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_SHAPE = 1;
        static final int INT_CIRCLE = 2;
        static final int INT_RECT = 3;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("shape", 1), new Enum("circle", 2), new Enum("rect", 3)});
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

