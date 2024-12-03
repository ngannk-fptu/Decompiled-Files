/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import java.util.Calendar;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMarkup;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDateTime;

public interface CTTrackChange
extends CTMarkup {
    public static final DocumentFactory<CTTrackChange> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttrackchangec317type");
    public static final SchemaType type = Factory.getType();

    public String getAuthor();

    public STString xgetAuthor();

    public void setAuthor(String var1);

    public void xsetAuthor(STString var1);

    public Calendar getDate();

    public STDateTime xgetDate();

    public boolean isSetDate();

    public void setDate(Calendar var1);

    public void xsetDate(STDateTime var1);

    public void unsetDate();
}

