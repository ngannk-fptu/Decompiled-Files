/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import java.util.Calendar;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTCalendarType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLang;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtDateMappingType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDateTime;

public interface CTSdtDate
extends XmlObject {
    public static final DocumentFactory<CTSdtDate> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsdtdatedfa1type");
    public static final SchemaType type = Factory.getType();

    public CTString getDateFormat();

    public boolean isSetDateFormat();

    public void setDateFormat(CTString var1);

    public CTString addNewDateFormat();

    public void unsetDateFormat();

    public CTLang getLid();

    public boolean isSetLid();

    public void setLid(CTLang var1);

    public CTLang addNewLid();

    public void unsetLid();

    public CTSdtDateMappingType getStoreMappedDataAs();

    public boolean isSetStoreMappedDataAs();

    public void setStoreMappedDataAs(CTSdtDateMappingType var1);

    public CTSdtDateMappingType addNewStoreMappedDataAs();

    public void unsetStoreMappedDataAs();

    public CTCalendarType getCalendar();

    public boolean isSetCalendar();

    public void setCalendar(CTCalendarType var1);

    public CTCalendarType addNewCalendar();

    public void unsetCalendar();

    public Calendar getFullDate();

    public STDateTime xgetFullDate();

    public boolean isSetFullDate();

    public void setFullDate(Calendar var1);

    public void xsetFullDate(STDateTime var1);

    public void unsetFullDate();
}

