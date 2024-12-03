/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STLineCap
extends XmlToken {
    public static final SimpleTypeFactory<STLineCap> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stlinecapcddftype");
    public static final SchemaType type = Factory.getType();
    public static final Enum RND = Enum.forString("rnd");
    public static final Enum SQ = Enum.forString("sq");
    public static final Enum FLAT = Enum.forString("flat");
    public static final int INT_RND = 1;
    public static final int INT_SQ = 2;
    public static final int INT_FLAT = 3;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_RND = 1;
        static final int INT_SQ = 2;
        static final int INT_FLAT = 3;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("rnd", 1), new Enum("sq", 2), new Enum("flat", 3)});
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

