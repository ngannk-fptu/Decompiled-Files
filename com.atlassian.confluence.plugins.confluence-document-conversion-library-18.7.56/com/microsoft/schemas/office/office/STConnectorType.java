/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.office;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STConnectorType
extends XmlString {
    public static final SimpleTypeFactory<STConnectorType> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stconnectortypecbd0type");
    public static final SchemaType type = Factory.getType();
    public static final Enum NONE = Enum.forString("none");
    public static final Enum STRAIGHT = Enum.forString("straight");
    public static final Enum ELBOW = Enum.forString("elbow");
    public static final Enum CURVED = Enum.forString("curved");
    public static final int INT_NONE = 1;
    public static final int INT_STRAIGHT = 2;
    public static final int INT_ELBOW = 3;
    public static final int INT_CURVED = 4;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_NONE = 1;
        static final int INT_STRAIGHT = 2;
        static final int INT_ELBOW = 3;
        static final int INT_CURVED = 4;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("none", 1), new Enum("straight", 2), new Enum("elbow", 3), new Enum("curved", 4)});
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

