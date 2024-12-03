/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STHeightRule
extends XmlString {
    public static final SimpleTypeFactory<STHeightRule> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stheightrulea535type");
    public static final SchemaType type = Factory.getType();
    public static final Enum AUTO = Enum.forString("auto");
    public static final Enum EXACT = Enum.forString("exact");
    public static final Enum AT_LEAST = Enum.forString("atLeast");
    public static final int INT_AUTO = 1;
    public static final int INT_EXACT = 2;
    public static final int INT_AT_LEAST = 3;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_AUTO = 1;
        static final int INT_EXACT = 2;
        static final int INT_AT_LEAST = 3;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("auto", 1), new Enum("exact", 2), new Enum("atLeast", 3)});
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

