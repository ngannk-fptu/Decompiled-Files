/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import java.util.Calendar;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDateTime;

public interface CTMoveBookmark
extends CTBookmark {
    public static final DocumentFactory<CTMoveBookmark> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctmovebookmarkf7a1type");
    public static final SchemaType type = Factory.getType();

    public String getAuthor();

    public STString xgetAuthor();

    public void setAuthor(String var1);

    public void xsetAuthor(STString var1);

    public Calendar getDate();

    public STDateTime xgetDate();

    public void setDate(Calendar var1);

    public void xsetDate(STDateTime var1);
}

