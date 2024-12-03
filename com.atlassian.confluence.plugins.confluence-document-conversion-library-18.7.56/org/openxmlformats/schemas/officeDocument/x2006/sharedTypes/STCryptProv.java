/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.officeDocument.x2006.sharedTypes;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STCryptProv
extends XmlString {
    public static final SimpleTypeFactory<STCryptProv> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stcryptprov36a7type");
    public static final SchemaType type = Factory.getType();
    public static final Enum RSA_AES = Enum.forString("rsaAES");
    public static final Enum RSA_FULL = Enum.forString("rsaFull");
    public static final Enum CUSTOM = Enum.forString("custom");
    public static final int INT_RSA_AES = 1;
    public static final int INT_RSA_FULL = 2;
    public static final int INT_CUSTOM = 3;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_RSA_AES = 1;
        static final int INT_RSA_FULL = 2;
        static final int INT_CUSTOM = 3;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("rsaAES", 1), new Enum("rsaFull", 2), new Enum("custom", 3)});
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

