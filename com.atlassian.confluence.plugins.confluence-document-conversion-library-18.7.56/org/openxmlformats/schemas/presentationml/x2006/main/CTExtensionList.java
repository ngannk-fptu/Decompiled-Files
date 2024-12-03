/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.presentationml.x2006.main.CTExtension;

public interface CTExtensionList
extends XmlObject {
    public static final DocumentFactory<CTExtensionList> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctextensionlist4772type");
    public static final SchemaType type = Factory.getType();

    public List<CTExtension> getExtList();

    public CTExtension[] getExtArray();

    public CTExtension getExtArray(int var1);

    public int sizeOfExtArray();

    public void setExtArray(CTExtension[] var1);

    public void setExtArray(int var1, CTExtension var2);

    public CTExtension insertNewExt(int var1);

    public CTExtension addNewExt();

    public void removeExt(int var1);
}

