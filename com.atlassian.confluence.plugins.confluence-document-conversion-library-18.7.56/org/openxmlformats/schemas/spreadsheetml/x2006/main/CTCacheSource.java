/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTConsolidation
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTConsolidation;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheetSource;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSourceType;

public interface CTCacheSource
extends XmlObject {
    public static final DocumentFactory<CTCacheSource> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcachesource00dctype");
    public static final SchemaType type = Factory.getType();

    public CTWorksheetSource getWorksheetSource();

    public boolean isSetWorksheetSource();

    public void setWorksheetSource(CTWorksheetSource var1);

    public CTWorksheetSource addNewWorksheetSource();

    public void unsetWorksheetSource();

    public CTConsolidation getConsolidation();

    public boolean isSetConsolidation();

    public void setConsolidation(CTConsolidation var1);

    public CTConsolidation addNewConsolidation();

    public void unsetConsolidation();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();

    public STSourceType.Enum getType();

    public STSourceType xgetType();

    public void setType(STSourceType.Enum var1);

    public void xsetType(STSourceType var1);

    public long getConnectionId();

    public XmlUnsignedInt xgetConnectionId();

    public boolean isSetConnectionId();

    public void setConnectionId(long var1);

    public void xsetConnectionId(XmlUnsignedInt var1);

    public void unsetConnectionId();
}

