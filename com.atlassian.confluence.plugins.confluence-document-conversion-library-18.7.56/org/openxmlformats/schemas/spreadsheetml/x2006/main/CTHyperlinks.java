/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTHyperlink;

public interface CTHyperlinks
extends XmlObject {
    public static final DocumentFactory<CTHyperlinks> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cthyperlinks6416type");
    public static final SchemaType type = Factory.getType();

    public List<CTHyperlink> getHyperlinkList();

    public CTHyperlink[] getHyperlinkArray();

    public CTHyperlink getHyperlinkArray(int var1);

    public int sizeOfHyperlinkArray();

    public void setHyperlinkArray(CTHyperlink[] var1);

    public void setHyperlinkArray(int var1, CTHyperlink var2);

    public CTHyperlink insertNewHyperlink(int var1);

    public CTHyperlink addNewHyperlink();

    public void removeHyperlink(int var1);
}

