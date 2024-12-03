/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCalcChain;

public interface CalcChainDocument
extends XmlObject {
    public static final DocumentFactory<CalcChainDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "calcchainfc37doctype");
    public static final SchemaType type = Factory.getType();

    public CTCalcChain getCalcChain();

    public void setCalcChain(CTCalcChain var1);

    public CTCalcChain addNewCalcChain();
}

