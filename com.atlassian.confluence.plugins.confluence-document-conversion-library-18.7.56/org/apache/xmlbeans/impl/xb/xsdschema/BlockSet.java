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
import org.apache.xmlbeans.impl.xb.xsdschema.DerivationControl;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface BlockSet
extends XmlAnySimpleType {
    public static final SimpleTypeFactory<BlockSet> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "blockset815etype");
    public static final SchemaType type = Factory.getType();

    public Object getObjectValue();

    public void setObjectValue(Object var1);

    public SchemaType instanceType();

    public static interface Member2
    extends XmlAnySimpleType {
        public static final ElementFactory<Member2> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "anonc904type");
        public static final SchemaType type = Factory.getType();

        public List getListValue();

        public List xgetListValue();

        public void setListValue(List<?> var1);

        public static interface Item
        extends DerivationControl {
            public static final ElementFactory<Item> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "anon421ctype");
            public static final SchemaType type = Factory.getType();
            public static final DerivationControl.Enum EXTENSION = DerivationControl.EXTENSION;
            public static final DerivationControl.Enum RESTRICTION = DerivationControl.RESTRICTION;
            public static final DerivationControl.Enum SUBSTITUTION = DerivationControl.SUBSTITUTION;
            public static final int INT_EXTENSION = 2;
            public static final int INT_RESTRICTION = 3;
            public static final int INT_SUBSTITUTION = 1;
        }
    }

    public static interface Member
    extends XmlToken {
        public static final ElementFactory<Member> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "anon0683type");
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

