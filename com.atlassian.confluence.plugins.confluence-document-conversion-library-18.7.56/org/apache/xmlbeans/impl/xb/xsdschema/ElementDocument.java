/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelElement;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface ElementDocument
extends XmlObject {
    public static final DocumentFactory<ElementDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "element7f99doctype");
    public static final SchemaType type = Factory.getType();

    public TopLevelElement getElement();

    public void setElement(TopLevelElement var1);

    public TopLevelElement addNewElement();
}

