/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.Annotated;
import org.apache.xmlbeans.impl.xb.xsdschema.LocalSimpleType;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface UnionDocument
extends XmlObject {
    public static final DocumentFactory<UnionDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "union5866doctype");
    public static final SchemaType type = Factory.getType();

    public Union getUnion();

    public void setUnion(Union var1);

    public Union addNewUnion();

    public static interface Union
    extends Annotated {
        public static final ElementFactory<Union> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "union498belemtype");
        public static final SchemaType type = Factory.getType();

        public List<LocalSimpleType> getSimpleTypeList();

        public LocalSimpleType[] getSimpleTypeArray();

        public LocalSimpleType getSimpleTypeArray(int var1);

        public int sizeOfSimpleTypeArray();

        public void setSimpleTypeArray(LocalSimpleType[] var1);

        public void setSimpleTypeArray(int var1, LocalSimpleType var2);

        public LocalSimpleType insertNewSimpleType(int var1);

        public LocalSimpleType addNewSimpleType();

        public void removeSimpleType(int var1);

        public List getMemberTypes();

        public MemberTypes xgetMemberTypes();

        public boolean isSetMemberTypes();

        public void setMemberTypes(List var1);

        public void xsetMemberTypes(MemberTypes var1);

        public void unsetMemberTypes();

        public static interface MemberTypes
        extends XmlAnySimpleType {
            public static final ElementFactory<MemberTypes> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "membertypes2404attrtype");
            public static final SchemaType type = Factory.getType();

            public List getListValue();

            public List xgetListValue();

            public void setListValue(List<?> var1);
        }
    }
}

