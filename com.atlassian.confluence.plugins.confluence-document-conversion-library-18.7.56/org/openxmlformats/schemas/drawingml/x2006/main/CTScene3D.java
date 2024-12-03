/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTBackdrop
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTCamera
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTLightRig
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBackdrop;
import org.openxmlformats.schemas.drawingml.x2006.main.CTCamera;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLightRig;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;

public interface CTScene3D
extends XmlObject {
    public static final DocumentFactory<CTScene3D> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctscene3d736etype");
    public static final SchemaType type = Factory.getType();

    public CTCamera getCamera();

    public void setCamera(CTCamera var1);

    public CTCamera addNewCamera();

    public CTLightRig getLightRig();

    public void setLightRig(CTLightRig var1);

    public CTLightRig addNewLightRig();

    public CTBackdrop getBackdrop();

    public boolean isSetBackdrop();

    public void setBackdrop(CTBackdrop var1);

    public CTBackdrop addNewBackdrop();

    public void unsetBackdrop();

    public CTOfficeArtExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTOfficeArtExtensionList var1);

    public CTOfficeArtExtensionList addNewExtLst();

    public void unsetExtLst();
}

