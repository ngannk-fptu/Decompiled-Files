/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCommentPr
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STGuid;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCommentPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRst;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STRef;

public interface CTComment
extends XmlObject {
    public static final DocumentFactory<CTComment> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcomment7bfetype");
    public static final SchemaType type = Factory.getType();

    public CTRst getText();

    public void setText(CTRst var1);

    public CTRst addNewText();

    public CTCommentPr getCommentPr();

    public boolean isSetCommentPr();

    public void setCommentPr(CTCommentPr var1);

    public CTCommentPr addNewCommentPr();

    public void unsetCommentPr();

    public String getRef();

    public STRef xgetRef();

    public void setRef(String var1);

    public void xsetRef(STRef var1);

    public long getAuthorId();

    public XmlUnsignedInt xgetAuthorId();

    public void setAuthorId(long var1);

    public void xsetAuthorId(XmlUnsignedInt var1);

    public String getGuid();

    public STGuid xgetGuid();

    public boolean isSetGuid();

    public void setGuid(String var1);

    public void xsetGuid(STGuid var1);

    public void unsetGuid();

    public long getShapeId();

    public XmlUnsignedInt xgetShapeId();

    public boolean isSetShapeId();

    public void setShapeId(long var1);

    public void xsetShapeId(XmlUnsignedInt var1);

    public void unsetShapeId();
}

