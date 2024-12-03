/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STTLTimeNodeRestartType
extends XmlToken {
    public static final SimpleTypeFactory<STTLTimeNodeRestartType> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "sttltimenoderestarttype4e5dtype");
    public static final SchemaType type = Factory.getType();
    public static final Enum ALWAYS = Enum.forString("always");
    public static final Enum WHEN_NOT_ACTIVE = Enum.forString("whenNotActive");
    public static final Enum NEVER = Enum.forString("never");
    public static final int INT_ALWAYS = 1;
    public static final int INT_WHEN_NOT_ACTIVE = 2;
    public static final int INT_NEVER = 3;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_ALWAYS = 1;
        static final int INT_WHEN_NOT_ACTIVE = 2;
        static final int INT_NEVER = 3;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("always", 1), new Enum("whenNotActive", 2), new Enum("never", 3)});
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

