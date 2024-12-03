/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommentList;

public interface CmLstDocument
extends XmlObject {
    public static final DocumentFactory<CmLstDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cmlst3880doctype");
    public static final SchemaType type = Factory.getType();

    public CTCommentList getCmLst();

    public void setCmLst(CTCommentList var1);

    public CTCommentList addNewCmLst();
}

