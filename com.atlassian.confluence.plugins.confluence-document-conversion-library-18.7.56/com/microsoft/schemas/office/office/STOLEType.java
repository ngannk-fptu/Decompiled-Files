/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.office;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STOLEType
extends XmlString {
    public static final SimpleTypeFactory<STOLEType> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stoletype716btype");
    public static final SchemaType type = Factory.getType();
    public static final Enum EMBED = Enum.forString("Embed");
    public static final Enum LINK = Enum.forString("Link");
    public static final int INT_EMBED = 1;
    public static final int INT_LINK = 2;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_EMBED = 1;
        static final int INT_LINK = 2;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("Embed", 1), new Enum("Link", 2)});
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

