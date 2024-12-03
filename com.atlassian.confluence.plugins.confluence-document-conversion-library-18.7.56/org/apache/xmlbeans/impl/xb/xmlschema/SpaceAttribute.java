/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xmlschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlNCName;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.metadata.system.sXMLLANG.TypeSystemHolder;

public interface SpaceAttribute
extends XmlObject {
    public static final DocumentFactory<SpaceAttribute> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "space9344attrtypetype");
    public static final SchemaType type = Factory.getType();

    public Space.Enum getSpace();

    public Space xgetSpace();

    public boolean isSetSpace();

    public void setSpace(Space.Enum var1);

    public void xsetSpace(Space var1);

    public void unsetSpace();

    public static interface Space
    extends XmlNCName {
        public static final ElementFactory<Space> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "spaceb986attrtype");
        public static final SchemaType type = Factory.getType();
        public static final Enum DEFAULT = Enum.forString("default");
        public static final Enum PRESERVE = Enum.forString("preserve");
        public static final int INT_DEFAULT = 1;
        public static final int INT_PRESERVE = 2;

        public StringEnumAbstractBase getEnumValue();

        public void setEnumValue(StringEnumAbstractBase var1);

        public static final class Enum
        extends StringEnumAbstractBase {
            static final int INT_DEFAULT = 1;
            static final int INT_PRESERVE = 2;
            public static final StringEnumAbstractBase.Table table = new StringEnumAbstractBase.Table(new Enum[]{new Enum("default", 1), new Enum("preserve", 2)});
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

