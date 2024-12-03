/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.ExplicitGroup;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface ChoiceDocument
extends XmlObject {
    public static final DocumentFactory<ChoiceDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "choicedf82doctype");
    public static final SchemaType type = Factory.getType();

    public ExplicitGroup getChoice();

    public void setChoice(ExplicitGroup var1);

    public ExplicitGroup addNewChoice();
}

