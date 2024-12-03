/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommentAuthor;

public interface CTCommentAuthorList
extends XmlObject {
    public static final DocumentFactory<CTCommentAuthorList> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcommentauthorlisteb07type");
    public static final SchemaType type = Factory.getType();

    public List<CTCommentAuthor> getCmAuthorList();

    public CTCommentAuthor[] getCmAuthorArray();

    public CTCommentAuthor getCmAuthorArray(int var1);

    public int sizeOfCmAuthorArray();

    public void setCmAuthorArray(CTCommentAuthor[] var1);

    public void setCmAuthorArray(int var1, CTCommentAuthor var2);

    public CTCommentAuthor insertNewCmAuthor(int var1);

    public CTCommentAuthor addNewCmAuthor();

    public void removeCmAuthor(int var1);
}

