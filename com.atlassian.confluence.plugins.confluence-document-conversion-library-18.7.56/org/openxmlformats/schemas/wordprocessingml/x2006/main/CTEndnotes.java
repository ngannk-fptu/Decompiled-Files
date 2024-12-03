/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFtnEdn;

public interface CTEndnotes
extends XmlObject {
    public static final DocumentFactory<CTEndnotes> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctendnotescee2type");
    public static final SchemaType type = Factory.getType();

    public List<CTFtnEdn> getEndnoteList();

    public CTFtnEdn[] getEndnoteArray();

    public CTFtnEdn getEndnoteArray(int var1);

    public int sizeOfEndnoteArray();

    public void setEndnoteArray(CTFtnEdn[] var1);

    public void setEndnoteArray(int var1, CTFtnEdn var2);

    public CTFtnEdn insertNewEndnote(int var1);

    public CTFtnEdn addNewEndnote();

    public void removeEndnote(int var1);
}

