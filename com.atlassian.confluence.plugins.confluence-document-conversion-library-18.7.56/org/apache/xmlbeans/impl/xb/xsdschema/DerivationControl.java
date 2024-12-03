/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlNMTOKEN;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface DerivationControl
extends XmlNMTOKEN {
    public static final SimpleTypeFactory<DerivationControl> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "derivationcontrola5dftype");
    public static final SchemaType type = Factory.getType();
    public static final Enum SUBSTITUTION = Enum.forString("substitution");
    public static final Enum EXTENSION = Enum.forString("extension");
    public static final Enum RESTRICTION = Enum.forString("restriction");
    public static final Enum LIST = Enum.forString("list");
    public static final Enum UNION = Enum.forString("union");
    public static final int INT_SUBSTITUTION = 1;
    public static final int INT_EXTENSION = 2;
    public static final int INT_RESTRICTION = 3;
    public static final int INT_LIST = 4;
    public static final int INT_UNION = 5;

    public StringEnumAbstractBase getEnumValue();

    public void setEnumValue(StringEnumAbstractBase var1);

    public static final class Enum
    extends StringEnumAbstractBase {
        static final int INT_SUBSTITUTION = 1;
        static final int INT_EXTENSION = 2;
        static final int INT_RESTRICTION = 3;
        static final int INT_LIST = 4;
        static final int INT_UNION = 5;
        public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("substitution", 1), new Enum("extension", 2), new Enum("restriction", 3), new Enum("list", 4), new Enum("union", 5)});
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

