/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STEditAs
extends XmlToken {
    public static final SimpleTypeFactory<STEditAs> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "steditasad40type");
    public static final SchemaType type = Factory.getType();
    public static final Enum TWO_CELL = Enum.forString("twoCell");
    public static final Enum ONE_CELL = Enum.forString("oneCell");
    public static final Enum ABSOLUTE = Enum.forString("absolute");
    public static final int INT_TWO_CELL = 1;
    public static final int INT_ONE_CELL = 2;
    public static final int INT_ABSOLUTE = 3;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_TWO_CELL = 1;
        static final int INT_ONE_CELL = 2;
        static final int INT_ABSOLUTE = 3;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("twoCell", 1), new Enum("oneCell", 2), new Enum("absolute", 3)});
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

