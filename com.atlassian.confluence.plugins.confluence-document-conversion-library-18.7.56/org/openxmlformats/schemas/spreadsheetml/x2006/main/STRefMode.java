/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STRefMode
extends XmlString {
    public static final SimpleTypeFactory<STRefMode> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "strefmodee5a5type");
    public static final SchemaType type = Factory.getType();
    public static final Enum A_1 = Enum.forString("A1");
    public static final Enum R_1_C_1 = Enum.forString("R1C1");
    public static final int INT_A_1 = 1;
    public static final int INT_R_1_C_1 = 2;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_A_1 = 1;
        static final int INT_R_1_C_1 = 2;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("A1", 1), new Enum("R1C1", 2)});
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

