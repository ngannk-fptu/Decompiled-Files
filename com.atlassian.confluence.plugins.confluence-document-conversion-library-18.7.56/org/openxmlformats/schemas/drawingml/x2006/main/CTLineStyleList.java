/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineProperties;

public interface CTLineStyleList
extends XmlObject {
    public static final DocumentFactory<CTLineStyleList> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctlinestylelist510ctype");
    public static final SchemaType type = Factory.getType();

    public List<CTLineProperties> getLnList();

    public CTLineProperties[] getLnArray();

    public CTLineProperties getLnArray(int var1);

    public int sizeOfLnArray();

    public void setLnArray(CTLineProperties[] var1);

    public void setLnArray(int var1, CTLineProperties var2);

    public CTLineProperties insertNewLn(int var1);

    public CTLineProperties addNewLn();

    public void removeLn(int var1);
}

