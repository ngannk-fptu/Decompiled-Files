/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommentAuthorList;

public interface CmAuthorLstDocument
extends XmlObject {
    public static final DocumentFactory<CmAuthorLstDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cmauthorlst86abdoctype");
    public static final SchemaType type = Factory.getType();

    public CTCommentAuthorList getCmAuthorLst();

    public void setCmAuthorLst(CTCommentAuthorList var1);

    public CTCommentAuthorList addNewCmAuthorLst();
}

