/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STLblAlgn
extends XmlString {
    public static final SimpleTypeFactory<STLblAlgn> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stlblalgn934etype");
    public static final SchemaType type = Factory.getType();
    public static final Enum CTR = Enum.forString("ctr");
    public static final Enum L = Enum.forString("l");
    public static final Enum R = Enum.forString("r");
    public static final int INT_CTR = 1;
    public static final int INT_L = 2;
    public static final int INT_R = 3;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_CTR = 1;
        static final int INT_L = 2;
        static final int INT_R = 3;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("ctr", 1), new Enum("l", 2), new Enum("r", 3)});
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

