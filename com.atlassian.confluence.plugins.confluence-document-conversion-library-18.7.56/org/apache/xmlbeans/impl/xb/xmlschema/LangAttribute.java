/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xmlschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.metadata.system.sXMLLANG.TypeSystemHolder;

public interface LangAttribute
extends XmlObject {
    public static final DocumentFactory<LangAttribute> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "lange126attrtypetype");
    public static final SchemaType type = Factory.getType();

    public String getLang();

    public Lang xgetLang();

    public boolean isSetLang();

    public void setLang(String var1);

    public void xsetLang(Lang var1);

    public void unsetLang();

    public static interface Lang
    extends XmlAnySimpleType {
        public static final ElementFactory<Lang> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "lang1224attrtype");
        public static final SchemaType type = Factory.getType();

        public Object getObjectValue();

        public void setObjectValue(Object var1);

        public SchemaType instanceType();

        public static interface Member
        extends XmlString {
            public static final ElementFactory<Member> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "anon695ftype");
            public static final SchemaType type = Factory.getType();
            public static final Enum X = Enum.forString("");
            public static final int INT_X = 1;

            public StringEnumAbstractBase getEnumValue();

            public void setEnumValue(StringEnumAbstractBase var1);

            public static final class Enum
            extends StringEnumAbstractBase {
                static final int INT_X = 1;
                public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("", 1)});
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
    }
}

