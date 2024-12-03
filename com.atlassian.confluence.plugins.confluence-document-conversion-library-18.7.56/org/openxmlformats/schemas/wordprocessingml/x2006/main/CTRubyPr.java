/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHpsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLang;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRubyAlign;

public interface CTRubyPr
extends XmlObject {
    public static final DocumentFactory<CTRubyPr> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctrubyprb2actype");
    public static final SchemaType type = Factory.getType();

    public CTRubyAlign getRubyAlign();

    public void setRubyAlign(CTRubyAlign var1);

    public CTRubyAlign addNewRubyAlign();

    public CTHpsMeasure getHps();

    public void setHps(CTHpsMeasure var1);

    public CTHpsMeasure addNewHps();

    public CTHpsMeasure getHpsRaise();

    public void setHpsRaise(CTHpsMeasure var1);

    public CTHpsMeasure addNewHpsRaise();

    public CTHpsMeasure getHpsBaseText();

    public void setHpsBaseText(CTHpsMeasure var1);

    public CTHpsMeasure addNewHpsBaseText();

    public CTLang getLid();

    public void setLid(CTLang var1);

    public CTLang addNewLid();

    public CTOnOff getDirty();

    public boolean isSetDirty();

    public void setDirty(CTOnOff var1);

    public CTOnOff addNewDirty();

    public void unsetDirty();
}

