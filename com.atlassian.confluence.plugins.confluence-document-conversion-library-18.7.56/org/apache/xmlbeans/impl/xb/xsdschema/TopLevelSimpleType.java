/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlNCName;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.SimpleType;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface TopLevelSimpleType
extends SimpleType {
    public static final DocumentFactory<TopLevelSimpleType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "toplevelsimpletypec958type");
    public static final SchemaType type = Factory.getType();

    @Override
    public String getName();

    @Override
    public XmlNCName xgetName();

    @Override
    public boolean isSetName();

    @Override
    public void setName(String var1);

    @Override
    public void xsetName(XmlNCName var1);

    @Override
    public void unsetName();
}

