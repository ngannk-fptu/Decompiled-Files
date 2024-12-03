/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlQName;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.Annotated;
import org.apache.xmlbeans.impl.xb.xsdschema.LocalSimpleType;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface ListDocument
extends XmlObject {
    public static final DocumentFactory<ListDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "listcde5doctype");
    public static final SchemaType type = Factory.getType();

    public List getList();

    public void setList(List var1);

    public List addNewList();

    public static interface List
    extends Annotated {
        public static final ElementFactory<List> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "list391felemtype");
        public static final SchemaType type = Factory.getType();

        public LocalSimpleType getSimpleType();

        public boolean isSetSimpleType();

        public void setSimpleType(LocalSimpleType var1);

        public LocalSimpleType addNewSimpleType();

        public void unsetSimpleType();

        public QName getItemType();

        public XmlQName xgetItemType();

        public boolean isSetItemType();

        public void setItemType(QName var1);

        public void xsetItemType(XmlQName var1);

        public void unsetItemType();
    }
}

