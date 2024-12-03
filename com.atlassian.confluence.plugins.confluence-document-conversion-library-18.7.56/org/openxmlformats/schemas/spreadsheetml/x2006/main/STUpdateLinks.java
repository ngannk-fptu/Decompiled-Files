/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STUpdateLinks
extends XmlString {
    public static final SimpleTypeFactory<STUpdateLinks> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stupdatelinksfb3ftype");
    public static final SchemaType type = Factory.getType();
    public static final Enum USER_SET = Enum.forString("userSet");
    public static final Enum NEVER = Enum.forString("never");
    public static final Enum ALWAYS = Enum.forString("always");
    public static final int INT_USER_SET = 1;
    public static final int INT_NEVER = 2;
    public static final int INT_ALWAYS = 3;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_USER_SET = 1;
        static final int INT_NEVER = 2;
        static final int INT_ALWAYS = 3;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("userSet", 1), new Enum("never", 2), new Enum("always", 3)});
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

