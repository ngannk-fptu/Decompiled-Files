/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.presentationml.x2006.main.CTComment;

public interface CTCommentList
extends XmlObject {
    public static final DocumentFactory<CTCommentList> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcommentlistf692type");
    public static final SchemaType type = Factory.getType();

    public List<CTComment> getCmList();

    public CTComment[] getCmArray();

    public CTComment getCmArray(int var1);

    public int sizeOfCmArray();

    public void setCmArray(CTComment[] var1);

    public void setCmArray(int var1, CTComment var2);

    public CTComment insertNewCm(int var1);

    public CTComment addNewCm();

    public void removeCm(int var1);
}

