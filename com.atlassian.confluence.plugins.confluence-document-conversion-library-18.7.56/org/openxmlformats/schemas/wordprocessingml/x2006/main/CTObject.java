/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTControl
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTObjectEmbed
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTObjectLink
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STTwipsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTControl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDrawing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTObjectEmbed;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTObjectLink;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRel;

public interface CTObject
extends XmlObject {
    public static final DocumentFactory<CTObject> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctobject47c9type");
    public static final SchemaType type = Factory.getType();

    public CTDrawing getDrawing();

    public boolean isSetDrawing();

    public void setDrawing(CTDrawing var1);

    public CTDrawing addNewDrawing();

    public void unsetDrawing();

    public CTControl getControl();

    public boolean isSetControl();

    public void setControl(CTControl var1);

    public CTControl addNewControl();

    public void unsetControl();

    public CTObjectLink getObjectLink();

    public boolean isSetObjectLink();

    public void setObjectLink(CTObjectLink var1);

    public CTObjectLink addNewObjectLink();

    public void unsetObjectLink();

    public CTObjectEmbed getObjectEmbed();

    public boolean isSetObjectEmbed();

    public void setObjectEmbed(CTObjectEmbed var1);

    public CTObjectEmbed addNewObjectEmbed();

    public void unsetObjectEmbed();

    public CTRel getMovie();

    public boolean isSetMovie();

    public void setMovie(CTRel var1);

    public CTRel addNewMovie();

    public void unsetMovie();

    public Object getDxaOrig();

    public STTwipsMeasure xgetDxaOrig();

    public boolean isSetDxaOrig();

    public void setDxaOrig(Object var1);

    public void xsetDxaOrig(STTwipsMeasure var1);

    public void unsetDxaOrig();

    public Object getDyaOrig();

    public STTwipsMeasure xgetDyaOrig();

    public boolean isSetDyaOrig();

    public void setDyaOrig(Object var1);

    public void xsetDyaOrig(STTwipsMeasure var1);

    public void unsetDyaOrig();
}

