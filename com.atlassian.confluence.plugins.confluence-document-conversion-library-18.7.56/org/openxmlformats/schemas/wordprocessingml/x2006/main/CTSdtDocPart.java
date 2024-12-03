/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;

public interface CTSdtDocPart
extends XmlObject {
    public static final DocumentFactory<CTSdtDocPart> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsdtdocpartcea0type");
    public static final SchemaType type = Factory.getType();

    public CTString getDocPartGallery();

    public boolean isSetDocPartGallery();

    public void setDocPartGallery(CTString var1);

    public CTString addNewDocPartGallery();

    public void unsetDocPartGallery();

    public CTString getDocPartCategory();

    public boolean isSetDocPartCategory();

    public void setDocPartCategory(CTString var1);

    public CTString addNewDocPartCategory();

    public void unsetDocPartCategory();

    public CTOnOff getDocPartUnique();

    public boolean isSetDocPartUnique();

    public void setDocPartUnique(CTOnOff var1);

    public CTOnOff addNewDocPartUnique();

    public void unsetDocPartUnique();
}

