/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtension;

public interface CTOfficeArtExtensionList
extends XmlObject {
    public static final DocumentFactory<CTOfficeArtExtensionList> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctofficeartextensionlista211type");
    public static final SchemaType type = Factory.getType();

    public List<CTOfficeArtExtension> getExtList();

    public CTOfficeArtExtension[] getExtArray();

    public CTOfficeArtExtension getExtArray(int var1);

    public int sizeOfExtArray();

    public void setExtArray(CTOfficeArtExtension[] var1);

    public void setExtArray(int var1, CTOfficeArtExtension var2);

    public CTOfficeArtExtension insertNewExt(int var1);

    public CTOfficeArtExtension addNewExt();

    public void removeExt(int var1);
}

