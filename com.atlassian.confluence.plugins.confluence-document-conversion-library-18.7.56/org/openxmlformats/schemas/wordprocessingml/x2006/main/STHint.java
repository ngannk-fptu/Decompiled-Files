/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STHint
extends XmlString {
    public static final SimpleTypeFactory<STHint> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "sthint8a31type");
    public static final SchemaType type = Factory.getType();
    public static final Enum DEFAULT = Enum.forString("default");
    public static final Enum EAST_ASIA = Enum.forString("eastAsia");
    public static final int INT_DEFAULT = 1;
    public static final int INT_EAST_ASIA = 2;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_DEFAULT = 1;
        static final int INT_EAST_ASIA = 2;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("default", 1), new Enum("eastAsia", 2)});
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

