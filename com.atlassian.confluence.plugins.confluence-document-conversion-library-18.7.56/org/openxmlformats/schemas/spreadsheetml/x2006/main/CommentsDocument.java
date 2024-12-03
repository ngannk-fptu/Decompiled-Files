/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTComments;

public interface CommentsDocument
extends XmlObject {
    public static final DocumentFactory<CommentsDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "comments4c11doctype");
    public static final SchemaType type = Factory.getType();

    public CTComments getComments();

    public void setComments(CTComments var1);

    public CTComments addNewComments();
}

