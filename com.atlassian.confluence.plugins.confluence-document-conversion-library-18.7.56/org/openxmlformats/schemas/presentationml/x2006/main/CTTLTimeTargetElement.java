/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTEmbeddedWAVAudioFile
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTEmpty
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTTLSubShapeId
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEmbeddedWAVAudioFile;
import org.openxmlformats.schemas.presentationml.x2006.main.CTEmpty;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLShapeTargetElement;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLSubShapeId;

public interface CTTLTimeTargetElement
extends XmlObject {
    public static final DocumentFactory<CTTLTimeTargetElement> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttltimetargetelementdca9type");
    public static final SchemaType type = Factory.getType();

    public CTEmpty getSldTgt();

    public boolean isSetSldTgt();

    public void setSldTgt(CTEmpty var1);

    public CTEmpty addNewSldTgt();

    public void unsetSldTgt();

    public CTEmbeddedWAVAudioFile getSndTgt();

    public boolean isSetSndTgt();

    public void setSndTgt(CTEmbeddedWAVAudioFile var1);

    public CTEmbeddedWAVAudioFile addNewSndTgt();

    public void unsetSndTgt();

    public CTTLShapeTargetElement getSpTgt();

    public boolean isSetSpTgt();

    public void setSpTgt(CTTLShapeTargetElement var1);

    public CTTLShapeTargetElement addNewSpTgt();

    public void unsetSpTgt();

    public CTTLSubShapeId getInkTgt();

    public boolean isSetInkTgt();

    public void setInkTgt(CTTLSubShapeId var1);

    public CTTLSubShapeId addNewInkTgt();

    public void unsetInkTgt();
}

