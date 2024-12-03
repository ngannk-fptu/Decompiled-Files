/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalReference;

public interface CTExternalReferences
extends XmlObject {
    public static final DocumentFactory<CTExternalReferences> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctexternalreferencesd77ctype");
    public static final SchemaType type = Factory.getType();

    public List<CTExternalReference> getExternalReferenceList();

    public CTExternalReference[] getExternalReferenceArray();

    public CTExternalReference getExternalReferenceArray(int var1);

    public int sizeOfExternalReferenceArray();

    public void setExternalReferenceArray(CTExternalReference[] var1);

    public void setExternalReferenceArray(int var1, CTExternalReference var2);

    public CTExternalReference insertNewExternalReference(int var1);

    public CTExternalReference addNewExternalReference();

    public void removeExternalReference(int var1);
}

