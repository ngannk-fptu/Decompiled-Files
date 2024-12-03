/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaBiLevelEffect
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaCeilingEffect
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaFloorEffect
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaInverseEffect
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaModulateEffect
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaOutsetEffect
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaReplaceEffect
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTBiLevelEffect
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTBlendEffect
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTBlurEffect
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTColorChangeEffect
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTColorReplaceEffect
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTEffectReference
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTFillEffect
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTFillOverlayEffect
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTGlowEffect
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTGrayscaleEffect
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTHSLEffect
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTInnerShadowEffect
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTLuminanceEffect
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTPresetShadowEffect
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTReflectionEffect
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTRelativeOffsetEffect
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTSoftEdgesEffect
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTTintEffect
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTTransformEffect
 *  org.openxmlformats.schemas.drawingml.x2006.main.STEffectContainerType
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaBiLevelEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaCeilingEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaFloorEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaInverseEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaModulateEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaModulateFixedEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaOutsetEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaReplaceEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBiLevelEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlendEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlurEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorChangeEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorReplaceEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTDuotoneEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectReference;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFillEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFillOverlayEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGlowEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGrayscaleEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTHSLEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTInnerShadowEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLuminanceEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOuterShadowEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetShadowEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTReflectionEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRelativeOffsetEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSoftEdgesEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTintEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransformEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.STEffectContainerType;

public interface CTEffectContainer
extends XmlObject {
    public static final DocumentFactory<CTEffectContainer> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cteffectcontainer2e21type");
    public static final SchemaType type = Factory.getType();

    public List<CTEffectContainer> getContList();

    public CTEffectContainer[] getContArray();

    public CTEffectContainer getContArray(int var1);

    public int sizeOfContArray();

    public void setContArray(CTEffectContainer[] var1);

    public void setContArray(int var1, CTEffectContainer var2);

    public CTEffectContainer insertNewCont(int var1);

    public CTEffectContainer addNewCont();

    public void removeCont(int var1);

    public List<CTEffectReference> getEffectList();

    public CTEffectReference[] getEffectArray();

    public CTEffectReference getEffectArray(int var1);

    public int sizeOfEffectArray();

    public void setEffectArray(CTEffectReference[] var1);

    public void setEffectArray(int var1, CTEffectReference var2);

    public CTEffectReference insertNewEffect(int var1);

    public CTEffectReference addNewEffect();

    public void removeEffect(int var1);

    public List<CTAlphaBiLevelEffect> getAlphaBiLevelList();

    public CTAlphaBiLevelEffect[] getAlphaBiLevelArray();

    public CTAlphaBiLevelEffect getAlphaBiLevelArray(int var1);

    public int sizeOfAlphaBiLevelArray();

    public void setAlphaBiLevelArray(CTAlphaBiLevelEffect[] var1);

    public void setAlphaBiLevelArray(int var1, CTAlphaBiLevelEffect var2);

    public CTAlphaBiLevelEffect insertNewAlphaBiLevel(int var1);

    public CTAlphaBiLevelEffect addNewAlphaBiLevel();

    public void removeAlphaBiLevel(int var1);

    public List<CTAlphaCeilingEffect> getAlphaCeilingList();

    public CTAlphaCeilingEffect[] getAlphaCeilingArray();

    public CTAlphaCeilingEffect getAlphaCeilingArray(int var1);

    public int sizeOfAlphaCeilingArray();

    public void setAlphaCeilingArray(CTAlphaCeilingEffect[] var1);

    public void setAlphaCeilingArray(int var1, CTAlphaCeilingEffect var2);

    public CTAlphaCeilingEffect insertNewAlphaCeiling(int var1);

    public CTAlphaCeilingEffect addNewAlphaCeiling();

    public void removeAlphaCeiling(int var1);

    public List<CTAlphaFloorEffect> getAlphaFloorList();

    public CTAlphaFloorEffect[] getAlphaFloorArray();

    public CTAlphaFloorEffect getAlphaFloorArray(int var1);

    public int sizeOfAlphaFloorArray();

    public void setAlphaFloorArray(CTAlphaFloorEffect[] var1);

    public void setAlphaFloorArray(int var1, CTAlphaFloorEffect var2);

    public CTAlphaFloorEffect insertNewAlphaFloor(int var1);

    public CTAlphaFloorEffect addNewAlphaFloor();

    public void removeAlphaFloor(int var1);

    public List<CTAlphaInverseEffect> getAlphaInvList();

    public CTAlphaInverseEffect[] getAlphaInvArray();

    public CTAlphaInverseEffect getAlphaInvArray(int var1);

    public int sizeOfAlphaInvArray();

    public void setAlphaInvArray(CTAlphaInverseEffect[] var1);

    public void setAlphaInvArray(int var1, CTAlphaInverseEffect var2);

    public CTAlphaInverseEffect insertNewAlphaInv(int var1);

    public CTAlphaInverseEffect addNewAlphaInv();

    public void removeAlphaInv(int var1);

    public List<CTAlphaModulateEffect> getAlphaModList();

    public CTAlphaModulateEffect[] getAlphaModArray();

    public CTAlphaModulateEffect getAlphaModArray(int var1);

    public int sizeOfAlphaModArray();

    public void setAlphaModArray(CTAlphaModulateEffect[] var1);

    public void setAlphaModArray(int var1, CTAlphaModulateEffect var2);

    public CTAlphaModulateEffect insertNewAlphaMod(int var1);

    public CTAlphaModulateEffect addNewAlphaMod();

    public void removeAlphaMod(int var1);

    public List<CTAlphaModulateFixedEffect> getAlphaModFixList();

    public CTAlphaModulateFixedEffect[] getAlphaModFixArray();

    public CTAlphaModulateFixedEffect getAlphaModFixArray(int var1);

    public int sizeOfAlphaModFixArray();

    public void setAlphaModFixArray(CTAlphaModulateFixedEffect[] var1);

    public void setAlphaModFixArray(int var1, CTAlphaModulateFixedEffect var2);

    public CTAlphaModulateFixedEffect insertNewAlphaModFix(int var1);

    public CTAlphaModulateFixedEffect addNewAlphaModFix();

    public void removeAlphaModFix(int var1);

    public List<CTAlphaOutsetEffect> getAlphaOutsetList();

    public CTAlphaOutsetEffect[] getAlphaOutsetArray();

    public CTAlphaOutsetEffect getAlphaOutsetArray(int var1);

    public int sizeOfAlphaOutsetArray();

    public void setAlphaOutsetArray(CTAlphaOutsetEffect[] var1);

    public void setAlphaOutsetArray(int var1, CTAlphaOutsetEffect var2);

    public CTAlphaOutsetEffect insertNewAlphaOutset(int var1);

    public CTAlphaOutsetEffect addNewAlphaOutset();

    public void removeAlphaOutset(int var1);

    public List<CTAlphaReplaceEffect> getAlphaReplList();

    public CTAlphaReplaceEffect[] getAlphaReplArray();

    public CTAlphaReplaceEffect getAlphaReplArray(int var1);

    public int sizeOfAlphaReplArray();

    public void setAlphaReplArray(CTAlphaReplaceEffect[] var1);

    public void setAlphaReplArray(int var1, CTAlphaReplaceEffect var2);

    public CTAlphaReplaceEffect insertNewAlphaRepl(int var1);

    public CTAlphaReplaceEffect addNewAlphaRepl();

    public void removeAlphaRepl(int var1);

    public List<CTBiLevelEffect> getBiLevelList();

    public CTBiLevelEffect[] getBiLevelArray();

    public CTBiLevelEffect getBiLevelArray(int var1);

    public int sizeOfBiLevelArray();

    public void setBiLevelArray(CTBiLevelEffect[] var1);

    public void setBiLevelArray(int var1, CTBiLevelEffect var2);

    public CTBiLevelEffect insertNewBiLevel(int var1);

    public CTBiLevelEffect addNewBiLevel();

    public void removeBiLevel(int var1);

    public List<CTBlendEffect> getBlendList();

    public CTBlendEffect[] getBlendArray();

    public CTBlendEffect getBlendArray(int var1);

    public int sizeOfBlendArray();

    public void setBlendArray(CTBlendEffect[] var1);

    public void setBlendArray(int var1, CTBlendEffect var2);

    public CTBlendEffect insertNewBlend(int var1);

    public CTBlendEffect addNewBlend();

    public void removeBlend(int var1);

    public List<CTBlurEffect> getBlurList();

    public CTBlurEffect[] getBlurArray();

    public CTBlurEffect getBlurArray(int var1);

    public int sizeOfBlurArray();

    public void setBlurArray(CTBlurEffect[] var1);

    public void setBlurArray(int var1, CTBlurEffect var2);

    public CTBlurEffect insertNewBlur(int var1);

    public CTBlurEffect addNewBlur();

    public void removeBlur(int var1);

    public List<CTColorChangeEffect> getClrChangeList();

    public CTColorChangeEffect[] getClrChangeArray();

    public CTColorChangeEffect getClrChangeArray(int var1);

    public int sizeOfClrChangeArray();

    public void setClrChangeArray(CTColorChangeEffect[] var1);

    public void setClrChangeArray(int var1, CTColorChangeEffect var2);

    public CTColorChangeEffect insertNewClrChange(int var1);

    public CTColorChangeEffect addNewClrChange();

    public void removeClrChange(int var1);

    public List<CTColorReplaceEffect> getClrReplList();

    public CTColorReplaceEffect[] getClrReplArray();

    public CTColorReplaceEffect getClrReplArray(int var1);

    public int sizeOfClrReplArray();

    public void setClrReplArray(CTColorReplaceEffect[] var1);

    public void setClrReplArray(int var1, CTColorReplaceEffect var2);

    public CTColorReplaceEffect insertNewClrRepl(int var1);

    public CTColorReplaceEffect addNewClrRepl();

    public void removeClrRepl(int var1);

    public List<CTDuotoneEffect> getDuotoneList();

    public CTDuotoneEffect[] getDuotoneArray();

    public CTDuotoneEffect getDuotoneArray(int var1);

    public int sizeOfDuotoneArray();

    public void setDuotoneArray(CTDuotoneEffect[] var1);

    public void setDuotoneArray(int var1, CTDuotoneEffect var2);

    public CTDuotoneEffect insertNewDuotone(int var1);

    public CTDuotoneEffect addNewDuotone();

    public void removeDuotone(int var1);

    public List<CTFillEffect> getFillList();

    public CTFillEffect[] getFillArray();

    public CTFillEffect getFillArray(int var1);

    public int sizeOfFillArray();

    public void setFillArray(CTFillEffect[] var1);

    public void setFillArray(int var1, CTFillEffect var2);

    public CTFillEffect insertNewFill(int var1);

    public CTFillEffect addNewFill();

    public void removeFill(int var1);

    public List<CTFillOverlayEffect> getFillOverlayList();

    public CTFillOverlayEffect[] getFillOverlayArray();

    public CTFillOverlayEffect getFillOverlayArray(int var1);

    public int sizeOfFillOverlayArray();

    public void setFillOverlayArray(CTFillOverlayEffect[] var1);

    public void setFillOverlayArray(int var1, CTFillOverlayEffect var2);

    public CTFillOverlayEffect insertNewFillOverlay(int var1);

    public CTFillOverlayEffect addNewFillOverlay();

    public void removeFillOverlay(int var1);

    public List<CTGlowEffect> getGlowList();

    public CTGlowEffect[] getGlowArray();

    public CTGlowEffect getGlowArray(int var1);

    public int sizeOfGlowArray();

    public void setGlowArray(CTGlowEffect[] var1);

    public void setGlowArray(int var1, CTGlowEffect var2);

    public CTGlowEffect insertNewGlow(int var1);

    public CTGlowEffect addNewGlow();

    public void removeGlow(int var1);

    public List<CTGrayscaleEffect> getGraysclList();

    public CTGrayscaleEffect[] getGraysclArray();

    public CTGrayscaleEffect getGraysclArray(int var1);

    public int sizeOfGraysclArray();

    public void setGraysclArray(CTGrayscaleEffect[] var1);

    public void setGraysclArray(int var1, CTGrayscaleEffect var2);

    public CTGrayscaleEffect insertNewGrayscl(int var1);

    public CTGrayscaleEffect addNewGrayscl();

    public void removeGrayscl(int var1);

    public List<CTHSLEffect> getHslList();

    public CTHSLEffect[] getHslArray();

    public CTHSLEffect getHslArray(int var1);

    public int sizeOfHslArray();

    public void setHslArray(CTHSLEffect[] var1);

    public void setHslArray(int var1, CTHSLEffect var2);

    public CTHSLEffect insertNewHsl(int var1);

    public CTHSLEffect addNewHsl();

    public void removeHsl(int var1);

    public List<CTInnerShadowEffect> getInnerShdwList();

    public CTInnerShadowEffect[] getInnerShdwArray();

    public CTInnerShadowEffect getInnerShdwArray(int var1);

    public int sizeOfInnerShdwArray();

    public void setInnerShdwArray(CTInnerShadowEffect[] var1);

    public void setInnerShdwArray(int var1, CTInnerShadowEffect var2);

    public CTInnerShadowEffect insertNewInnerShdw(int var1);

    public CTInnerShadowEffect addNewInnerShdw();

    public void removeInnerShdw(int var1);

    public List<CTLuminanceEffect> getLumList();

    public CTLuminanceEffect[] getLumArray();

    public CTLuminanceEffect getLumArray(int var1);

    public int sizeOfLumArray();

    public void setLumArray(CTLuminanceEffect[] var1);

    public void setLumArray(int var1, CTLuminanceEffect var2);

    public CTLuminanceEffect insertNewLum(int var1);

    public CTLuminanceEffect addNewLum();

    public void removeLum(int var1);

    public List<CTOuterShadowEffect> getOuterShdwList();

    public CTOuterShadowEffect[] getOuterShdwArray();

    public CTOuterShadowEffect getOuterShdwArray(int var1);

    public int sizeOfOuterShdwArray();

    public void setOuterShdwArray(CTOuterShadowEffect[] var1);

    public void setOuterShdwArray(int var1, CTOuterShadowEffect var2);

    public CTOuterShadowEffect insertNewOuterShdw(int var1);

    public CTOuterShadowEffect addNewOuterShdw();

    public void removeOuterShdw(int var1);

    public List<CTPresetShadowEffect> getPrstShdwList();

    public CTPresetShadowEffect[] getPrstShdwArray();

    public CTPresetShadowEffect getPrstShdwArray(int var1);

    public int sizeOfPrstShdwArray();

    public void setPrstShdwArray(CTPresetShadowEffect[] var1);

    public void setPrstShdwArray(int var1, CTPresetShadowEffect var2);

    public CTPresetShadowEffect insertNewPrstShdw(int var1);

    public CTPresetShadowEffect addNewPrstShdw();

    public void removePrstShdw(int var1);

    public List<CTReflectionEffect> getReflectionList();

    public CTReflectionEffect[] getReflectionArray();

    public CTReflectionEffect getReflectionArray(int var1);

    public int sizeOfReflectionArray();

    public void setReflectionArray(CTReflectionEffect[] var1);

    public void setReflectionArray(int var1, CTReflectionEffect var2);

    public CTReflectionEffect insertNewReflection(int var1);

    public CTReflectionEffect addNewReflection();

    public void removeReflection(int var1);

    public List<CTRelativeOffsetEffect> getRelOffList();

    public CTRelativeOffsetEffect[] getRelOffArray();

    public CTRelativeOffsetEffect getRelOffArray(int var1);

    public int sizeOfRelOffArray();

    public void setRelOffArray(CTRelativeOffsetEffect[] var1);

    public void setRelOffArray(int var1, CTRelativeOffsetEffect var2);

    public CTRelativeOffsetEffect insertNewRelOff(int var1);

    public CTRelativeOffsetEffect addNewRelOff();

    public void removeRelOff(int var1);

    public List<CTSoftEdgesEffect> getSoftEdgeList();

    public CTSoftEdgesEffect[] getSoftEdgeArray();

    public CTSoftEdgesEffect getSoftEdgeArray(int var1);

    public int sizeOfSoftEdgeArray();

    public void setSoftEdgeArray(CTSoftEdgesEffect[] var1);

    public void setSoftEdgeArray(int var1, CTSoftEdgesEffect var2);

    public CTSoftEdgesEffect insertNewSoftEdge(int var1);

    public CTSoftEdgesEffect addNewSoftEdge();

    public void removeSoftEdge(int var1);

    public List<CTTintEffect> getTintList();

    public CTTintEffect[] getTintArray();

    public CTTintEffect getTintArray(int var1);

    public int sizeOfTintArray();

    public void setTintArray(CTTintEffect[] var1);

    public void setTintArray(int var1, CTTintEffect var2);

    public CTTintEffect insertNewTint(int var1);

    public CTTintEffect addNewTint();

    public void removeTint(int var1);

    public List<CTTransformEffect> getXfrmList();

    public CTTransformEffect[] getXfrmArray();

    public CTTransformEffect getXfrmArray(int var1);

    public int sizeOfXfrmArray();

    public void setXfrmArray(CTTransformEffect[] var1);

    public void setXfrmArray(int var1, CTTransformEffect var2);

    public CTTransformEffect insertNewXfrm(int var1);

    public CTTransformEffect addNewXfrm();

    public void removeXfrm(int var1);

    public STEffectContainerType.Enum getType();

    public STEffectContainerType xgetType();

    public boolean isSetType();

    public void setType(STEffectContainerType.Enum var1);

    public void xsetType(STEffectContainerType var1);

    public void unsetType();

    public String getName();

    public XmlToken xgetName();

    public boolean isSetName();

    public void setName(String var1);

    public void xsetName(XmlToken var1);

    public void unsetName();
}

