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

public interface CTFootnotes
extends XmlObject {
    public static final DocumentFactory<CTFootnotes> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctfootnotes691ftype");
    public static final SchemaType type = Factory.getType();

    public List<CTFtnEdn> getFootnoteList();

    public CTFtnEdn[] getFootnoteArray();

    public CTFtnEdn getFootnoteArray(int var1);

    public int sizeOfFootnoteArray();

    public void setFootnoteArray(CTFtnEdn[] var1);

    public void setFootnoteArray(int var1, CTFtnEdn var2);

    public CTFtnEdn insertNewFootnote(int var1);

    public CTFtnEdn addNewFootnote();

    public void removeFootnote(int var1);
}

