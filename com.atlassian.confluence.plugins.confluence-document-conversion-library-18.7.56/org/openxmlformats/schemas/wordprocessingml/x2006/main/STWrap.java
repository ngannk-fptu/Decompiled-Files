/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STWrap
extends XmlString {
    public static final SimpleTypeFactory<STWrap> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stwrap3f4etype");
    public static final SchemaType type = Factory.getType();
    public static final Enum AUTO = Enum.forString("auto");
    public static final Enum NOT_BESIDE = Enum.forString("notBeside");
    public static final Enum AROUND = Enum.forString("around");
    public static final Enum TIGHT = Enum.forString("tight");
    public static final Enum THROUGH = Enum.forString("through");
    public static final Enum NONE = Enum.forString("none");
    public static final int INT_AUTO = 1;
    public static final int INT_NOT_BESIDE = 2;
    public static final int INT_AROUND = 3;
    public static final int INT_TIGHT = 4;
    public static final int INT_THROUGH = 5;
    public static final int INT_NONE = 6;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_AUTO = 1;
        static final int INT_NOT_BESIDE = 2;
        static final int INT_AROUND = 3;
        static final int INT_TIGHT = 4;
        static final int INT_THROUGH = 5;
        static final int INT_NONE = 6;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("auto", 1), new Enum("notBeside", 2), new Enum("around", 3), new Enum("tight", 4), new Enum("through", 5), new Enum("none", 6)});
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

