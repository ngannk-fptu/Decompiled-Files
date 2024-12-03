/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmarkRange;

public interface CTBookmark
extends CTBookmarkRange {
    public static final DocumentFactory<CTBookmark> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctbookmarkd672type");
    public static final SchemaType type = Factory.getType();

    public String getName();

    public STString xgetName();

    public void setName(String var1);

    public void xsetName(STString var1);
}

