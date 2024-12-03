/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xmlconfig;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;
import org.apache.xmlbeans.metadata.system.sXMLCONFIG.TypeSystemHolder;

public interface Qnametargetenum
extends XmlToken {
    public static final SimpleTypeFactory<Qnametargetenum> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "qnametargetenum9f8ftype");
    public static final SchemaType type = Factory.getType();
    public static final Enum TYPE = Enum.forString("type");
    public static final Enum DOCUMENT_TYPE = Enum.forString("document-type");
    public static final Enum ACCESSOR_ELEMENT = Enum.forString("accessor-element");
    public static final Enum ACCESSOR_ATTRIBUTE = Enum.forString("accessor-attribute");
    public static final int INT_TYPE = 1;
    public static final int INT_DOCUMENT_TYPE = 2;
    public static final int INT_ACCESSOR_ELEMENT = 3;
    public static final int INT_ACCESSOR_ATTRIBUTE = 4;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_TYPE = 1;
        static final int INT_DOCUMENT_TYPE = 2;
        static final int INT_ACCESSOR_ELEMENT = 3;
        static final int INT_ACCESSOR_ATTRIBUTE = 4;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("type", 1), new Enum("document-type", 2), new Enum("accessor-element", 3), new Enum("accessor-attribute", 4)});
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

