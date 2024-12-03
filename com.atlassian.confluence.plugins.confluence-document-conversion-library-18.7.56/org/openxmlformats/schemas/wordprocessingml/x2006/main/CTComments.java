/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTComment;

public interface CTComments
extends XmlObject {
    public static final DocumentFactory<CTComments> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcomments7674type");
    public static final SchemaType type = Factory.getType();

    public List<CTComment> getCommentList();

    public CTComment[] getCommentArray();

    public CTComment getCommentArray(int var1);

    public int sizeOfCommentArray();

    public void setCommentArray(CTComment[] var1);

    public void setCommentArray(int var1, CTComment var2);

    public CTComment insertNewComment(int var1);

    public CTComment addNewComment();

    public void removeComment(int var1);
}

