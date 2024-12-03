/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlNMTOKEN;
import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface AllNNI
extends XmlAnySimpleType {
    public static final SimpleTypeFactory<AllNNI> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "allnni78cbtype");
    public static final SchemaType type = Factory.getType();

    public Object getObjectValue();

    public void setObjectValue(Object var1);

    public SchemaType instanceType();

    public static interface Member
    extends XmlNMTOKEN {
        public static final ElementFactory<Member> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "anon0330type");
        public static final SchemaType type = Factory.getType();
        public static final Enum UNBOUNDED = Enum.forString("unbounded");
        public static final int INT_UNBOUNDED = 1;

        public StringEnumAbstractBase getEnumValue();

        public void setEnumValue(StringEnumAbstractBase var1);

        public static final class Enum
        extends StringEnumAbstractBase {
            static final int INT_UNBOUNDED = 1;
            public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("unbounded", 1)});
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

