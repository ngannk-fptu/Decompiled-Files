/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.picture;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.picture.CTPicture;

public interface PicDocument
extends XmlObject {
    public static final DocumentFactory<PicDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "pic8010doctype");
    public static final SchemaType type = Factory.getType();

    public CTPicture getPic();

    public void setPic(CTPicture var1);

    public CTPicture addNewPic();
}

