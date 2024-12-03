/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STTabTlc
extends XmlString {
    public static final SimpleTypeFactory<STTabTlc> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "sttabtlc6f42type");
    public static final SchemaType type = Factory.getType();
    public static final Enum NONE = Enum.forString("none");
    public static final Enum DOT = Enum.forString("dot");
    public static final Enum HYPHEN = Enum.forString("hyphen");
    public static final Enum UNDERSCORE = Enum.forString("underscore");
    public static final Enum HEAVY = Enum.forString("heavy");
    public static final Enum MIDDLE_DOT = Enum.forString("middleDot");
    public static final int INT_NONE = 1;
    public static final int INT_DOT = 2;
    public static final int INT_HYPHEN = 3;
    public static final int INT_UNDERSCORE = 4;
    public static final int INT_HEAVY = 5;
    public static final int INT_MIDDLE_DOT = 6;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_NONE = 1;
        static final int INT_DOT = 2;
        static final int INT_HYPHEN = 3;
        static final int INT_UNDERSCORE = 4;
        static final int INT_HEAVY = 5;
        static final int INT_MIDDLE_DOT = 6;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("none", 1), new Enum("dot", 2), new Enum("hyphen", 3), new Enum("underscore", 4), new Enum("heavy", 5), new Enum("middleDot", 6)});
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

