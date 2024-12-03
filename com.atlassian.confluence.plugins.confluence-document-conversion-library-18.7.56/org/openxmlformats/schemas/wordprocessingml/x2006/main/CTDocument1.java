/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STConformanceClass
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STConformanceClass;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocumentBase;

public interface CTDocument1
extends CTDocumentBase {
    public static final DocumentFactory<CTDocument1> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctdocument64adtype");
    public static final SchemaType type = Factory.getType();

    public CTBody getBody();

    public boolean isSetBody();

    public void setBody(CTBody var1);

    public CTBody addNewBody();

    public void unsetBody();

    public STConformanceClass.Enum getConformance();

    public STConformanceClass xgetConformance();

    public boolean isSetConformance();

    public void setConformance(STConformanceClass.Enum var1);

    public void xsetConformance(STConformanceClass var1);

    public void unsetConformance();
}

