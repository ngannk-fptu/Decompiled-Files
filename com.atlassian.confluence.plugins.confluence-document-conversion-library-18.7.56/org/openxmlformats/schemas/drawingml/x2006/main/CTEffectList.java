/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTBlurEffect
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTFillOverlayEffect
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTGlowEffect
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTInnerShadowEffect
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTPresetShadowEffect
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTReflectionEffect
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTSoftEdgesEffect
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlurEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFillOverlayEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGlowEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTInnerShadowEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOuterShadowEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetShadowEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTReflectionEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSoftEdgesEffect;

public interface CTEffectList
extends XmlObject {
    public static final DocumentFactory<CTEffectList> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cteffectlist6featype");
    public static final SchemaType type = Factory.getType();

    public CTBlurEffect getBlur();

    public boolean isSetBlur();

    public void setBlur(CTBlurEffect var1);

    public CTBlurEffect addNewBlur();

    public void unsetBlur();

    public CTFillOverlayEffect getFillOverlay();

    public boolean isSetFillOverlay();

    public void setFillOverlay(CTFillOverlayEffect var1);

    public CTFillOverlayEffect addNewFillOverlay();

    public void unsetFillOverlay();

    public CTGlowEffect getGlow();

    public boolean isSetGlow();

    public void setGlow(CTGlowEffect var1);

    public CTGlowEffect addNewGlow();

    public void unsetGlow();

    public CTInnerShadowEffect getInnerShdw();

    public boolean isSetInnerShdw();

    public void setInnerShdw(CTInnerShadowEffect var1);

    public CTInnerShadowEffect addNewInnerShdw();

    public void unsetInnerShdw();

    public CTOuterShadowEffect getOuterShdw();

    public boolean isSetOuterShdw();

    public void setOuterShdw(CTOuterShadowEffect var1);

    public CTOuterShadowEffect addNewOuterShdw();

    public void unsetOuterShdw();

    public CTPresetShadowEffect getPrstShdw();

    public boolean isSetPrstShdw();

    public void setPrstShdw(CTPresetShadowEffect var1);

    public CTPresetShadowEffect addNewPrstShdw();

    public void unsetPrstShdw();

    public CTReflectionEffect getReflection();

    public boolean isSetReflection();

    public void setReflection(CTReflectionEffect var1);

    public CTReflectionEffect addNewReflection();

    public void unsetReflection();

    public CTSoftEdgesEffect getSoftEdge();

    public boolean isSetSoftEdge();

    public void setSoftEdge(CTSoftEdgesEffect var1);

    public CTSoftEdgesEffect addNewSoftEdge();

    public void unsetSoftEdge();
}

