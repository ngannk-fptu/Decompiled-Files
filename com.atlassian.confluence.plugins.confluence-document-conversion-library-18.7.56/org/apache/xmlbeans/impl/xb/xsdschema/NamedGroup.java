/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlNCName;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.RealGroup;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface NamedGroup
extends RealGroup {
    public static final DocumentFactory<NamedGroup> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "namedgroup878dtype");
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

    public static interface All
    extends org.apache.xmlbeans.impl.xb.xsdschema.All {
        public static final ElementFactory<All> Factory = new ElementFactory(TypeSystemHolder.typeSystem, "all82daelemtype");
        public static final SchemaType type = Factory.getType();
    }
}

