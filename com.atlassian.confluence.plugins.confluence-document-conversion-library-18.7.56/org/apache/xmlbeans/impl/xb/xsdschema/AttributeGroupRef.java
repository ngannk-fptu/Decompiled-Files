/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlQName;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.AttributeGroup;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface AttributeGroupRef
extends AttributeGroup {
    public static final DocumentFactory<AttributeGroupRef> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "attributegroupref8375type");
    public static final SchemaType type = Factory.getType();

    @Override
    public QName getRef();

    @Override
    public XmlQName xgetRef();

    @Override
    public boolean isSetRef();

    @Override
    public void setRef(QName var1);

    @Override
    public void xsetRef(XmlQName var1);

    @Override
    public void unsetRef();
}

