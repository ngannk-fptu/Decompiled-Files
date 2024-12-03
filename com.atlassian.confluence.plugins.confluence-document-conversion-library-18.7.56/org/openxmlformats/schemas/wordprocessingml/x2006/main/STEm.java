/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STEm
extends XmlString {
    public static final SimpleTypeFactory<STEm> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stem5e70type");
    public static final SchemaType type = Factory.getType();
    public static final Enum NONE = Enum.forString("none");
    public static final Enum DOT = Enum.forString("dot");
    public static final Enum COMMA = Enum.forString("comma");
    public static final Enum CIRCLE = Enum.forString("circle");
    public static final Enum UNDER_DOT = Enum.forString("underDot");
    public static final int INT_NONE = 1;
    public static final int INT_DOT = 2;
    public static final int INT_COMMA = 3;
    public static final int INT_CIRCLE = 4;
    public static final int INT_UNDER_DOT = 5;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_NONE = 1;
        static final int INT_DOT = 2;
        static final int INT_COMMA = 3;
        static final int INT_CIRCLE = 4;
        static final int INT_UNDER_DOT = 5;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("none", 1), new Enum("dot", 2), new Enum("comma", 3), new Enum("circle", 4), new Enum("underDot", 5)});
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

