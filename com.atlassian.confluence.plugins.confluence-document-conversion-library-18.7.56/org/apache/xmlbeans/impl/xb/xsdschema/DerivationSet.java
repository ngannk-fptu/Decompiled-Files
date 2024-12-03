/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface DerivationSet
extends XmlAnySimpleType {
    public static final SimpleTypeFactory<DerivationSet> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "derivationset037atype");
    public static final SchemaType type = Factory.getType();

    public Object getObjectValue();

    public void setObjectValue(Object var1);

    public SchemaType instanceType();

    public static interface Member2
    extends XmlAnySimpleType {
        public static final ElementFactory<Member2> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "anon9394type");
        public static final SchemaType type = Factory.getType();

        public List getListValue();

        public List xgetListValue();

        public void setListValue(List<?> var1);
    }

    public static interface Member
    extends XmlToken {
        public static final ElementFactory<Member> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "anoned75type");
        public static final SchemaType type = Factory.getType();
        public static final Enum ALL = Enum.forString("#all");
        public static final int INT_ALL = 1;

        public StringEnumAbstractBase getEnumValue();

        public void setEnumValue(StringEnumAbstractBase var1);

        public static final class Enum
        extends StringEnumAbstractBase {
            static final int INT_ALL = 1;
            public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("#all", 1)});
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

