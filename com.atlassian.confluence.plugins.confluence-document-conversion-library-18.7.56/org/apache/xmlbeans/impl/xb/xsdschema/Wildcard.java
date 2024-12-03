/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlNMTOKEN;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.Annotated;
import org.apache.xmlbeans.impl.xb.xsdschema.NamespaceList;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface Wildcard
extends Annotated {
    public static final DocumentFactory<Wildcard> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "wildcarde0b9type");
    public static final SchemaType type = Factory.getType();

    public Object getNamespace();

    public NamespaceList xgetNamespace();

    public boolean isSetNamespace();

    public void setNamespace(Object var1);

    public void xsetNamespace(NamespaceList var1);

    public void unsetNamespace();

    public ProcessContents.Enum getProcessContents();

    public ProcessContents xgetProcessContents();

    public boolean isSetProcessContents();

    public void setProcessContents(ProcessContents.Enum var1);

    public void xsetProcessContents(ProcessContents var1);

    public void unsetProcessContents();

    public static interface ProcessContents
    extends XmlNMTOKEN {
        public static final ElementFactory<ProcessContents> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "processcontents864aattrtype");
        public static final SchemaType type = Factory.getType();
        public static final Enum SKIP = Enum.forString("skip");
        public static final Enum LAX = Enum.forString("lax");
        public static final Enum STRICT = Enum.forString("strict");
        public static final int INT_SKIP = 1;
        public static final int INT_LAX = 2;
        public static final int INT_STRICT = 3;

        public StringEnumAbstractBase getEnumValue();

        public void setEnumValue(StringEnumAbstractBase var1);

        public static final class Enum
        extends StringEnumAbstractBase {
            static final int INT_SKIP = 1;
            static final int INT_LAX = 2;
            static final int INT_STRICT = 3;
            public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("skip", 1), new Enum("lax", 2), new Enum("strict", 3)});
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

