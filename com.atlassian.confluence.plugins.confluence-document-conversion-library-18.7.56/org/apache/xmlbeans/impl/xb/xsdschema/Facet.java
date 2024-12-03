/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.Annotated;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface Facet
extends Annotated {
    public static final DocumentFactory<Facet> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "facet446etype");
    public static final SchemaType type = Factory.getType();

    public XmlAnySimpleType getValue();

    public void setValue(XmlAnySimpleType var1);

    public XmlAnySimpleType addNewValue();

    public boolean getFixed();

    public XmlBoolean xgetFixed();

    public boolean isSetFixed();

    public void setFixed(boolean var1);

    public void xsetFixed(XmlBoolean var1);

    public void unsetFixed();
}

