/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTAuthors;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCommentList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;

public interface CTComments
extends XmlObject {
    public static final DocumentFactory<CTComments> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcommentse3bdtype");
    public static final SchemaType type = Factory.getType();

    public CTAuthors getAuthors();

    public void setAuthors(CTAuthors var1);

    public CTAuthors addNewAuthors();

    public CTCommentList getCommentList();

    public void setCommentList(CTCommentList var1);

    public CTCommentList addNewCommentList();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();
}

