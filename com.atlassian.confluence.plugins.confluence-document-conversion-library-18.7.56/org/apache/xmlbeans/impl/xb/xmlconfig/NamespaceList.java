/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xmlconfig;

import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;
import org.apache.xmlbeans.metadata.system.sXMLCONFIG.TypeSystemHolder;

public interface NamespaceList
extends XmlAnySimpleType {
    public static final SimpleTypeFactory<NamespaceList> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "namespacelist20datype");
    public static final SchemaType type = Factory.getType();

    public Object getObjectValue();

    public void setObjectValue(Object var1);

    public SchemaType instanceType();

    public static interface Member2
    extends XmlAnySimpleType {
        public static final ElementFactory<Member2> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "anon5680type");
        public static final SchemaType type = Factory.getType();

        public List getListValue();

        public List xgetListValue();

        public void setListValue(List<?> var1);

        public static interface Item
        extends XmlAnySimpleType {
            public static final ElementFactory<Item> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "anon0798type");
            public static final SchemaType type = Factory.getType();

            public Object getObjectValue();

            public void setObjectValue(Object var1);

            public SchemaType instanceType();

            public static interface Member
            extends XmlToken {
                public static final ElementFactory<Member> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "anon1dd3type");
                public static final SchemaType type = Factory.getType();
                public static final Enum LOCAL = Enum.forString("##local");
                public static final int INT_LOCAL = 1;

                public StringEnumAbstractBase getEnumValue();

                public void setEnumValue(StringEnumAbstractBase var1);

                public static final class Enum
                extends StringEnumAbstractBase {
                    static final int INT_LOCAL = 1;
                    public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("##local", 1)});
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

    public static interface Member
    extends XmlToken {
        public static final ElementFactory<Member> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "anonc6fftype");
        public static final SchemaType type = Factory.getType();
        public static final Enum ANY = Enum.forString("##any");
        public static final int INT_ANY = 1;

        public StringEnumAbstractBase getEnumValue();

        public void setEnumValue(StringEnumAbstractBase var1);

        public static final class Enum
        extends StringEnumAbstractBase {
            static final int INT_ANY = 1;
            public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("##any", 1)});
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

