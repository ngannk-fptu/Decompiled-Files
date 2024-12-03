/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrackChangeNumbering
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrackChange;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrackChangeNumbering;

public interface CTNumPr
extends XmlObject {
    public static final DocumentFactory<CTNumPr> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctnumpr16aatype");
    public static final SchemaType type = Factory.getType();

    public CTDecimalNumber getIlvl();

    public boolean isSetIlvl();

    public void setIlvl(CTDecimalNumber var1);

    public CTDecimalNumber addNewIlvl();

    public void unsetIlvl();

    public CTDecimalNumber getNumId();

    public boolean isSetNumId();

    public void setNumId(CTDecimalNumber var1);

    public CTDecimalNumber addNewNumId();

    public void unsetNumId();

    public CTTrackChangeNumbering getNumberingChange();

    public boolean isSetNumberingChange();

    public void setNumberingChange(CTTrackChangeNumbering var1);

    public CTTrackChangeNumbering addNewNumberingChange();

    public void unsetNumberingChange();

    public CTTrackChange getIns();

    public boolean isSetIns();

    public void setIns(CTTrackChange var1);

    public CTTrackChange addNewIns();

    public void unsetIns();
}

