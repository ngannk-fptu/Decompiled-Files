/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STCalcMode
extends XmlString {
    public static final SimpleTypeFactory<STCalcMode> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stcalcmode5e71type");
    public static final SchemaType type = Factory.getType();
    public static final Enum MANUAL = Enum.forString("manual");
    public static final Enum AUTO = Enum.forString("auto");
    public static final Enum AUTO_NO_TABLE = Enum.forString("autoNoTable");
    public static final int INT_MANUAL = 1;
    public static final int INT_AUTO = 2;
    public static final int INT_AUTO_NO_TABLE = 3;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_MANUAL = 1;
        static final int INT_AUTO = 2;
        static final int INT_AUTO_NO_TABLE = 3;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("manual", 1), new Enum("auto", 2), new Enum("autoNoTable", 3)});
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

