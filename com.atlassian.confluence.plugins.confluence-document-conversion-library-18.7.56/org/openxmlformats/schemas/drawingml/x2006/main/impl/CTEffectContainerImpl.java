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
package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
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
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectContainer;
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

public class CTEffectContainerImpl
extends XmlComplexContentImpl
implements CTEffectContainer {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "cont"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "effect"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "alphaBiLevel"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "alphaCeiling"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "alphaFloor"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "alphaInv"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "alphaMod"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "alphaModFix"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "alphaOutset"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "alphaRepl"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "biLevel"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "blend"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "blur"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "clrChange"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "clrRepl"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "duotone"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "fill"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "fillOverlay"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "glow"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "grayscl"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "hsl"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "innerShdw"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lum"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "outerShdw"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "prstShdw"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "reflection"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "relOff"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "softEdge"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "tint"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "xfrm"), new QName("", "type"), new QName("", "name")};

    public CTEffectContainerImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTEffectContainer> getContList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTEffectContainer>(this::getContArray, this::setContArray, this::insertNewCont, this::removeCont, this::sizeOfContArray);
        }
    }

    @Override
    public CTEffectContainer[] getContArray() {
        return (CTEffectContainer[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTEffectContainer[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEffectContainer getContArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEffectContainer target = null;
            target = (CTEffectContainer)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfContArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setContArray(CTEffectContainer[] contArray) {
        this.check_orphaned();
        this.arraySetterHelper(contArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setContArray(int i, CTEffectContainer cont) {
        this.generatedSetterHelperImpl(cont, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEffectContainer insertNewCont(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEffectContainer target = null;
            target = (CTEffectContainer)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEffectContainer addNewCont() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEffectContainer target = null;
            target = (CTEffectContainer)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCont(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTEffectReference> getEffectList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTEffectReference>(this::getEffectArray, this::setEffectArray, this::insertNewEffect, this::removeEffect, this::sizeOfEffectArray);
        }
    }

    @Override
    public CTEffectReference[] getEffectArray() {
        return (CTEffectReference[])this.getXmlObjectArray(PROPERTY_QNAME[1], (XmlObject[])new CTEffectReference[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEffectReference getEffectArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEffectReference target = null;
            target = (CTEffectReference)this.get_store().find_element_user(PROPERTY_QNAME[1], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfEffectArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]);
        }
    }

    @Override
    public void setEffectArray(CTEffectReference[] effectArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])effectArray, PROPERTY_QNAME[1]);
    }

    @Override
    public void setEffectArray(int i, CTEffectReference effect) {
        this.generatedSetterHelperImpl((XmlObject)effect, PROPERTY_QNAME[1], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEffectReference insertNewEffect(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEffectReference target = null;
            target = (CTEffectReference)this.get_store().insert_element_user(PROPERTY_QNAME[1], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEffectReference addNewEffect() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEffectReference target = null;
            target = (CTEffectReference)this.get_store().add_element_user(PROPERTY_QNAME[1]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeEffect(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[1], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTAlphaBiLevelEffect> getAlphaBiLevelList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTAlphaBiLevelEffect>(this::getAlphaBiLevelArray, this::setAlphaBiLevelArray, this::insertNewAlphaBiLevel, this::removeAlphaBiLevel, this::sizeOfAlphaBiLevelArray);
        }
    }

    @Override
    public CTAlphaBiLevelEffect[] getAlphaBiLevelArray() {
        return (CTAlphaBiLevelEffect[])this.getXmlObjectArray(PROPERTY_QNAME[2], (XmlObject[])new CTAlphaBiLevelEffect[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAlphaBiLevelEffect getAlphaBiLevelArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAlphaBiLevelEffect target = null;
            target = (CTAlphaBiLevelEffect)this.get_store().find_element_user(PROPERTY_QNAME[2], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfAlphaBiLevelArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]);
        }
    }

    @Override
    public void setAlphaBiLevelArray(CTAlphaBiLevelEffect[] alphaBiLevelArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])alphaBiLevelArray, PROPERTY_QNAME[2]);
    }

    @Override
    public void setAlphaBiLevelArray(int i, CTAlphaBiLevelEffect alphaBiLevel) {
        this.generatedSetterHelperImpl((XmlObject)alphaBiLevel, PROPERTY_QNAME[2], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAlphaBiLevelEffect insertNewAlphaBiLevel(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAlphaBiLevelEffect target = null;
            target = (CTAlphaBiLevelEffect)this.get_store().insert_element_user(PROPERTY_QNAME[2], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAlphaBiLevelEffect addNewAlphaBiLevel() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAlphaBiLevelEffect target = null;
            target = (CTAlphaBiLevelEffect)this.get_store().add_element_user(PROPERTY_QNAME[2]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAlphaBiLevel(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[2], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTAlphaCeilingEffect> getAlphaCeilingList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTAlphaCeilingEffect>(this::getAlphaCeilingArray, this::setAlphaCeilingArray, this::insertNewAlphaCeiling, this::removeAlphaCeiling, this::sizeOfAlphaCeilingArray);
        }
    }

    @Override
    public CTAlphaCeilingEffect[] getAlphaCeilingArray() {
        return (CTAlphaCeilingEffect[])this.getXmlObjectArray(PROPERTY_QNAME[3], (XmlObject[])new CTAlphaCeilingEffect[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAlphaCeilingEffect getAlphaCeilingArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAlphaCeilingEffect target = null;
            target = (CTAlphaCeilingEffect)this.get_store().find_element_user(PROPERTY_QNAME[3], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfAlphaCeilingArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]);
        }
    }

    @Override
    public void setAlphaCeilingArray(CTAlphaCeilingEffect[] alphaCeilingArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])alphaCeilingArray, PROPERTY_QNAME[3]);
    }

    @Override
    public void setAlphaCeilingArray(int i, CTAlphaCeilingEffect alphaCeiling) {
        this.generatedSetterHelperImpl((XmlObject)alphaCeiling, PROPERTY_QNAME[3], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAlphaCeilingEffect insertNewAlphaCeiling(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAlphaCeilingEffect target = null;
            target = (CTAlphaCeilingEffect)this.get_store().insert_element_user(PROPERTY_QNAME[3], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAlphaCeilingEffect addNewAlphaCeiling() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAlphaCeilingEffect target = null;
            target = (CTAlphaCeilingEffect)this.get_store().add_element_user(PROPERTY_QNAME[3]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAlphaCeiling(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[3], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTAlphaFloorEffect> getAlphaFloorList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTAlphaFloorEffect>(this::getAlphaFloorArray, this::setAlphaFloorArray, this::insertNewAlphaFloor, this::removeAlphaFloor, this::sizeOfAlphaFloorArray);
        }
    }

    @Override
    public CTAlphaFloorEffect[] getAlphaFloorArray() {
        return (CTAlphaFloorEffect[])this.getXmlObjectArray(PROPERTY_QNAME[4], (XmlObject[])new CTAlphaFloorEffect[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAlphaFloorEffect getAlphaFloorArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAlphaFloorEffect target = null;
            target = (CTAlphaFloorEffect)this.get_store().find_element_user(PROPERTY_QNAME[4], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfAlphaFloorArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[4]);
        }
    }

    @Override
    public void setAlphaFloorArray(CTAlphaFloorEffect[] alphaFloorArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])alphaFloorArray, PROPERTY_QNAME[4]);
    }

    @Override
    public void setAlphaFloorArray(int i, CTAlphaFloorEffect alphaFloor) {
        this.generatedSetterHelperImpl((XmlObject)alphaFloor, PROPERTY_QNAME[4], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAlphaFloorEffect insertNewAlphaFloor(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAlphaFloorEffect target = null;
            target = (CTAlphaFloorEffect)this.get_store().insert_element_user(PROPERTY_QNAME[4], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAlphaFloorEffect addNewAlphaFloor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAlphaFloorEffect target = null;
            target = (CTAlphaFloorEffect)this.get_store().add_element_user(PROPERTY_QNAME[4]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAlphaFloor(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[4], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTAlphaInverseEffect> getAlphaInvList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTAlphaInverseEffect>(this::getAlphaInvArray, this::setAlphaInvArray, this::insertNewAlphaInv, this::removeAlphaInv, this::sizeOfAlphaInvArray);
        }
    }

    @Override
    public CTAlphaInverseEffect[] getAlphaInvArray() {
        return (CTAlphaInverseEffect[])this.getXmlObjectArray(PROPERTY_QNAME[5], (XmlObject[])new CTAlphaInverseEffect[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAlphaInverseEffect getAlphaInvArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAlphaInverseEffect target = null;
            target = (CTAlphaInverseEffect)this.get_store().find_element_user(PROPERTY_QNAME[5], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfAlphaInvArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[5]);
        }
    }

    @Override
    public void setAlphaInvArray(CTAlphaInverseEffect[] alphaInvArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])alphaInvArray, PROPERTY_QNAME[5]);
    }

    @Override
    public void setAlphaInvArray(int i, CTAlphaInverseEffect alphaInv) {
        this.generatedSetterHelperImpl((XmlObject)alphaInv, PROPERTY_QNAME[5], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAlphaInverseEffect insertNewAlphaInv(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAlphaInverseEffect target = null;
            target = (CTAlphaInverseEffect)this.get_store().insert_element_user(PROPERTY_QNAME[5], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAlphaInverseEffect addNewAlphaInv() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAlphaInverseEffect target = null;
            target = (CTAlphaInverseEffect)this.get_store().add_element_user(PROPERTY_QNAME[5]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAlphaInv(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[5], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTAlphaModulateEffect> getAlphaModList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTAlphaModulateEffect>(this::getAlphaModArray, this::setAlphaModArray, this::insertNewAlphaMod, this::removeAlphaMod, this::sizeOfAlphaModArray);
        }
    }

    @Override
    public CTAlphaModulateEffect[] getAlphaModArray() {
        return (CTAlphaModulateEffect[])this.getXmlObjectArray(PROPERTY_QNAME[6], (XmlObject[])new CTAlphaModulateEffect[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAlphaModulateEffect getAlphaModArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAlphaModulateEffect target = null;
            target = (CTAlphaModulateEffect)this.get_store().find_element_user(PROPERTY_QNAME[6], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfAlphaModArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[6]);
        }
    }

    @Override
    public void setAlphaModArray(CTAlphaModulateEffect[] alphaModArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])alphaModArray, PROPERTY_QNAME[6]);
    }

    @Override
    public void setAlphaModArray(int i, CTAlphaModulateEffect alphaMod) {
        this.generatedSetterHelperImpl((XmlObject)alphaMod, PROPERTY_QNAME[6], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAlphaModulateEffect insertNewAlphaMod(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAlphaModulateEffect target = null;
            target = (CTAlphaModulateEffect)this.get_store().insert_element_user(PROPERTY_QNAME[6], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAlphaModulateEffect addNewAlphaMod() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAlphaModulateEffect target = null;
            target = (CTAlphaModulateEffect)this.get_store().add_element_user(PROPERTY_QNAME[6]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAlphaMod(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[6], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTAlphaModulateFixedEffect> getAlphaModFixList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTAlphaModulateFixedEffect>(this::getAlphaModFixArray, this::setAlphaModFixArray, this::insertNewAlphaModFix, this::removeAlphaModFix, this::sizeOfAlphaModFixArray);
        }
    }

    @Override
    public CTAlphaModulateFixedEffect[] getAlphaModFixArray() {
        return (CTAlphaModulateFixedEffect[])this.getXmlObjectArray(PROPERTY_QNAME[7], new CTAlphaModulateFixedEffect[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAlphaModulateFixedEffect getAlphaModFixArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAlphaModulateFixedEffect target = null;
            target = (CTAlphaModulateFixedEffect)((Object)this.get_store().find_element_user(PROPERTY_QNAME[7], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfAlphaModFixArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[7]);
        }
    }

    @Override
    public void setAlphaModFixArray(CTAlphaModulateFixedEffect[] alphaModFixArray) {
        this.check_orphaned();
        this.arraySetterHelper(alphaModFixArray, PROPERTY_QNAME[7]);
    }

    @Override
    public void setAlphaModFixArray(int i, CTAlphaModulateFixedEffect alphaModFix) {
        this.generatedSetterHelperImpl(alphaModFix, PROPERTY_QNAME[7], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAlphaModulateFixedEffect insertNewAlphaModFix(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAlphaModulateFixedEffect target = null;
            target = (CTAlphaModulateFixedEffect)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[7], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAlphaModulateFixedEffect addNewAlphaModFix() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAlphaModulateFixedEffect target = null;
            target = (CTAlphaModulateFixedEffect)((Object)this.get_store().add_element_user(PROPERTY_QNAME[7]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAlphaModFix(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[7], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTAlphaOutsetEffect> getAlphaOutsetList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTAlphaOutsetEffect>(this::getAlphaOutsetArray, this::setAlphaOutsetArray, this::insertNewAlphaOutset, this::removeAlphaOutset, this::sizeOfAlphaOutsetArray);
        }
    }

    @Override
    public CTAlphaOutsetEffect[] getAlphaOutsetArray() {
        return (CTAlphaOutsetEffect[])this.getXmlObjectArray(PROPERTY_QNAME[8], (XmlObject[])new CTAlphaOutsetEffect[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAlphaOutsetEffect getAlphaOutsetArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAlphaOutsetEffect target = null;
            target = (CTAlphaOutsetEffect)this.get_store().find_element_user(PROPERTY_QNAME[8], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfAlphaOutsetArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[8]);
        }
    }

    @Override
    public void setAlphaOutsetArray(CTAlphaOutsetEffect[] alphaOutsetArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])alphaOutsetArray, PROPERTY_QNAME[8]);
    }

    @Override
    public void setAlphaOutsetArray(int i, CTAlphaOutsetEffect alphaOutset) {
        this.generatedSetterHelperImpl((XmlObject)alphaOutset, PROPERTY_QNAME[8], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAlphaOutsetEffect insertNewAlphaOutset(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAlphaOutsetEffect target = null;
            target = (CTAlphaOutsetEffect)this.get_store().insert_element_user(PROPERTY_QNAME[8], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAlphaOutsetEffect addNewAlphaOutset() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAlphaOutsetEffect target = null;
            target = (CTAlphaOutsetEffect)this.get_store().add_element_user(PROPERTY_QNAME[8]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAlphaOutset(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[8], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTAlphaReplaceEffect> getAlphaReplList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTAlphaReplaceEffect>(this::getAlphaReplArray, this::setAlphaReplArray, this::insertNewAlphaRepl, this::removeAlphaRepl, this::sizeOfAlphaReplArray);
        }
    }

    @Override
    public CTAlphaReplaceEffect[] getAlphaReplArray() {
        return (CTAlphaReplaceEffect[])this.getXmlObjectArray(PROPERTY_QNAME[9], (XmlObject[])new CTAlphaReplaceEffect[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAlphaReplaceEffect getAlphaReplArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAlphaReplaceEffect target = null;
            target = (CTAlphaReplaceEffect)this.get_store().find_element_user(PROPERTY_QNAME[9], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfAlphaReplArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[9]);
        }
    }

    @Override
    public void setAlphaReplArray(CTAlphaReplaceEffect[] alphaReplArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])alphaReplArray, PROPERTY_QNAME[9]);
    }

    @Override
    public void setAlphaReplArray(int i, CTAlphaReplaceEffect alphaRepl) {
        this.generatedSetterHelperImpl((XmlObject)alphaRepl, PROPERTY_QNAME[9], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAlphaReplaceEffect insertNewAlphaRepl(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAlphaReplaceEffect target = null;
            target = (CTAlphaReplaceEffect)this.get_store().insert_element_user(PROPERTY_QNAME[9], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAlphaReplaceEffect addNewAlphaRepl() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAlphaReplaceEffect target = null;
            target = (CTAlphaReplaceEffect)this.get_store().add_element_user(PROPERTY_QNAME[9]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAlphaRepl(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[9], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTBiLevelEffect> getBiLevelList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTBiLevelEffect>(this::getBiLevelArray, this::setBiLevelArray, this::insertNewBiLevel, this::removeBiLevel, this::sizeOfBiLevelArray);
        }
    }

    @Override
    public CTBiLevelEffect[] getBiLevelArray() {
        return (CTBiLevelEffect[])this.getXmlObjectArray(PROPERTY_QNAME[10], (XmlObject[])new CTBiLevelEffect[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBiLevelEffect getBiLevelArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBiLevelEffect target = null;
            target = (CTBiLevelEffect)this.get_store().find_element_user(PROPERTY_QNAME[10], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfBiLevelArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[10]);
        }
    }

    @Override
    public void setBiLevelArray(CTBiLevelEffect[] biLevelArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])biLevelArray, PROPERTY_QNAME[10]);
    }

    @Override
    public void setBiLevelArray(int i, CTBiLevelEffect biLevel) {
        this.generatedSetterHelperImpl((XmlObject)biLevel, PROPERTY_QNAME[10], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBiLevelEffect insertNewBiLevel(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBiLevelEffect target = null;
            target = (CTBiLevelEffect)this.get_store().insert_element_user(PROPERTY_QNAME[10], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBiLevelEffect addNewBiLevel() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBiLevelEffect target = null;
            target = (CTBiLevelEffect)this.get_store().add_element_user(PROPERTY_QNAME[10]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeBiLevel(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[10], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTBlendEffect> getBlendList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTBlendEffect>(this::getBlendArray, this::setBlendArray, this::insertNewBlend, this::removeBlend, this::sizeOfBlendArray);
        }
    }

    @Override
    public CTBlendEffect[] getBlendArray() {
        return (CTBlendEffect[])this.getXmlObjectArray(PROPERTY_QNAME[11], (XmlObject[])new CTBlendEffect[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBlendEffect getBlendArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBlendEffect target = null;
            target = (CTBlendEffect)this.get_store().find_element_user(PROPERTY_QNAME[11], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfBlendArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[11]);
        }
    }

    @Override
    public void setBlendArray(CTBlendEffect[] blendArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])blendArray, PROPERTY_QNAME[11]);
    }

    @Override
    public void setBlendArray(int i, CTBlendEffect blend) {
        this.generatedSetterHelperImpl((XmlObject)blend, PROPERTY_QNAME[11], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBlendEffect insertNewBlend(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBlendEffect target = null;
            target = (CTBlendEffect)this.get_store().insert_element_user(PROPERTY_QNAME[11], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBlendEffect addNewBlend() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBlendEffect target = null;
            target = (CTBlendEffect)this.get_store().add_element_user(PROPERTY_QNAME[11]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeBlend(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[11], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTBlurEffect> getBlurList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTBlurEffect>(this::getBlurArray, this::setBlurArray, this::insertNewBlur, this::removeBlur, this::sizeOfBlurArray);
        }
    }

    @Override
    public CTBlurEffect[] getBlurArray() {
        return (CTBlurEffect[])this.getXmlObjectArray(PROPERTY_QNAME[12], (XmlObject[])new CTBlurEffect[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBlurEffect getBlurArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBlurEffect target = null;
            target = (CTBlurEffect)this.get_store().find_element_user(PROPERTY_QNAME[12], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfBlurArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[12]);
        }
    }

    @Override
    public void setBlurArray(CTBlurEffect[] blurArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])blurArray, PROPERTY_QNAME[12]);
    }

    @Override
    public void setBlurArray(int i, CTBlurEffect blur) {
        this.generatedSetterHelperImpl((XmlObject)blur, PROPERTY_QNAME[12], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBlurEffect insertNewBlur(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBlurEffect target = null;
            target = (CTBlurEffect)this.get_store().insert_element_user(PROPERTY_QNAME[12], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBlurEffect addNewBlur() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBlurEffect target = null;
            target = (CTBlurEffect)this.get_store().add_element_user(PROPERTY_QNAME[12]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeBlur(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[12], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTColorChangeEffect> getClrChangeList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTColorChangeEffect>(this::getClrChangeArray, this::setClrChangeArray, this::insertNewClrChange, this::removeClrChange, this::sizeOfClrChangeArray);
        }
    }

    @Override
    public CTColorChangeEffect[] getClrChangeArray() {
        return (CTColorChangeEffect[])this.getXmlObjectArray(PROPERTY_QNAME[13], (XmlObject[])new CTColorChangeEffect[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColorChangeEffect getClrChangeArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColorChangeEffect target = null;
            target = (CTColorChangeEffect)this.get_store().find_element_user(PROPERTY_QNAME[13], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfClrChangeArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[13]);
        }
    }

    @Override
    public void setClrChangeArray(CTColorChangeEffect[] clrChangeArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])clrChangeArray, PROPERTY_QNAME[13]);
    }

    @Override
    public void setClrChangeArray(int i, CTColorChangeEffect clrChange) {
        this.generatedSetterHelperImpl((XmlObject)clrChange, PROPERTY_QNAME[13], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColorChangeEffect insertNewClrChange(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColorChangeEffect target = null;
            target = (CTColorChangeEffect)this.get_store().insert_element_user(PROPERTY_QNAME[13], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColorChangeEffect addNewClrChange() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColorChangeEffect target = null;
            target = (CTColorChangeEffect)this.get_store().add_element_user(PROPERTY_QNAME[13]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeClrChange(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[13], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTColorReplaceEffect> getClrReplList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTColorReplaceEffect>(this::getClrReplArray, this::setClrReplArray, this::insertNewClrRepl, this::removeClrRepl, this::sizeOfClrReplArray);
        }
    }

    @Override
    public CTColorReplaceEffect[] getClrReplArray() {
        return (CTColorReplaceEffect[])this.getXmlObjectArray(PROPERTY_QNAME[14], (XmlObject[])new CTColorReplaceEffect[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColorReplaceEffect getClrReplArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColorReplaceEffect target = null;
            target = (CTColorReplaceEffect)this.get_store().find_element_user(PROPERTY_QNAME[14], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfClrReplArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[14]);
        }
    }

    @Override
    public void setClrReplArray(CTColorReplaceEffect[] clrReplArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])clrReplArray, PROPERTY_QNAME[14]);
    }

    @Override
    public void setClrReplArray(int i, CTColorReplaceEffect clrRepl) {
        this.generatedSetterHelperImpl((XmlObject)clrRepl, PROPERTY_QNAME[14], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColorReplaceEffect insertNewClrRepl(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColorReplaceEffect target = null;
            target = (CTColorReplaceEffect)this.get_store().insert_element_user(PROPERTY_QNAME[14], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColorReplaceEffect addNewClrRepl() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColorReplaceEffect target = null;
            target = (CTColorReplaceEffect)this.get_store().add_element_user(PROPERTY_QNAME[14]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeClrRepl(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[14], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTDuotoneEffect> getDuotoneList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTDuotoneEffect>(this::getDuotoneArray, this::setDuotoneArray, this::insertNewDuotone, this::removeDuotone, this::sizeOfDuotoneArray);
        }
    }

    @Override
    public CTDuotoneEffect[] getDuotoneArray() {
        return (CTDuotoneEffect[])this.getXmlObjectArray(PROPERTY_QNAME[15], new CTDuotoneEffect[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDuotoneEffect getDuotoneArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDuotoneEffect target = null;
            target = (CTDuotoneEffect)((Object)this.get_store().find_element_user(PROPERTY_QNAME[15], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfDuotoneArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[15]);
        }
    }

    @Override
    public void setDuotoneArray(CTDuotoneEffect[] duotoneArray) {
        this.check_orphaned();
        this.arraySetterHelper(duotoneArray, PROPERTY_QNAME[15]);
    }

    @Override
    public void setDuotoneArray(int i, CTDuotoneEffect duotone) {
        this.generatedSetterHelperImpl(duotone, PROPERTY_QNAME[15], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDuotoneEffect insertNewDuotone(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDuotoneEffect target = null;
            target = (CTDuotoneEffect)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[15], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDuotoneEffect addNewDuotone() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDuotoneEffect target = null;
            target = (CTDuotoneEffect)((Object)this.get_store().add_element_user(PROPERTY_QNAME[15]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeDuotone(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[15], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTFillEffect> getFillList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTFillEffect>(this::getFillArray, this::setFillArray, this::insertNewFill, this::removeFill, this::sizeOfFillArray);
        }
    }

    @Override
    public CTFillEffect[] getFillArray() {
        return (CTFillEffect[])this.getXmlObjectArray(PROPERTY_QNAME[16], (XmlObject[])new CTFillEffect[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFillEffect getFillArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFillEffect target = null;
            target = (CTFillEffect)this.get_store().find_element_user(PROPERTY_QNAME[16], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfFillArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[16]);
        }
    }

    @Override
    public void setFillArray(CTFillEffect[] fillArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])fillArray, PROPERTY_QNAME[16]);
    }

    @Override
    public void setFillArray(int i, CTFillEffect fill) {
        this.generatedSetterHelperImpl((XmlObject)fill, PROPERTY_QNAME[16], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFillEffect insertNewFill(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFillEffect target = null;
            target = (CTFillEffect)this.get_store().insert_element_user(PROPERTY_QNAME[16], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFillEffect addNewFill() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFillEffect target = null;
            target = (CTFillEffect)this.get_store().add_element_user(PROPERTY_QNAME[16]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeFill(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[16], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTFillOverlayEffect> getFillOverlayList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTFillOverlayEffect>(this::getFillOverlayArray, this::setFillOverlayArray, this::insertNewFillOverlay, this::removeFillOverlay, this::sizeOfFillOverlayArray);
        }
    }

    @Override
    public CTFillOverlayEffect[] getFillOverlayArray() {
        return (CTFillOverlayEffect[])this.getXmlObjectArray(PROPERTY_QNAME[17], (XmlObject[])new CTFillOverlayEffect[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFillOverlayEffect getFillOverlayArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFillOverlayEffect target = null;
            target = (CTFillOverlayEffect)this.get_store().find_element_user(PROPERTY_QNAME[17], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfFillOverlayArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[17]);
        }
    }

    @Override
    public void setFillOverlayArray(CTFillOverlayEffect[] fillOverlayArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])fillOverlayArray, PROPERTY_QNAME[17]);
    }

    @Override
    public void setFillOverlayArray(int i, CTFillOverlayEffect fillOverlay) {
        this.generatedSetterHelperImpl((XmlObject)fillOverlay, PROPERTY_QNAME[17], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFillOverlayEffect insertNewFillOverlay(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFillOverlayEffect target = null;
            target = (CTFillOverlayEffect)this.get_store().insert_element_user(PROPERTY_QNAME[17], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFillOverlayEffect addNewFillOverlay() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFillOverlayEffect target = null;
            target = (CTFillOverlayEffect)this.get_store().add_element_user(PROPERTY_QNAME[17]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeFillOverlay(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[17], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTGlowEffect> getGlowList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTGlowEffect>(this::getGlowArray, this::setGlowArray, this::insertNewGlow, this::removeGlow, this::sizeOfGlowArray);
        }
    }

    @Override
    public CTGlowEffect[] getGlowArray() {
        return (CTGlowEffect[])this.getXmlObjectArray(PROPERTY_QNAME[18], (XmlObject[])new CTGlowEffect[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGlowEffect getGlowArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGlowEffect target = null;
            target = (CTGlowEffect)this.get_store().find_element_user(PROPERTY_QNAME[18], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfGlowArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[18]);
        }
    }

    @Override
    public void setGlowArray(CTGlowEffect[] glowArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])glowArray, PROPERTY_QNAME[18]);
    }

    @Override
    public void setGlowArray(int i, CTGlowEffect glow) {
        this.generatedSetterHelperImpl((XmlObject)glow, PROPERTY_QNAME[18], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGlowEffect insertNewGlow(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGlowEffect target = null;
            target = (CTGlowEffect)this.get_store().insert_element_user(PROPERTY_QNAME[18], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGlowEffect addNewGlow() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGlowEffect target = null;
            target = (CTGlowEffect)this.get_store().add_element_user(PROPERTY_QNAME[18]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeGlow(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[18], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTGrayscaleEffect> getGraysclList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTGrayscaleEffect>(this::getGraysclArray, this::setGraysclArray, this::insertNewGrayscl, this::removeGrayscl, this::sizeOfGraysclArray);
        }
    }

    @Override
    public CTGrayscaleEffect[] getGraysclArray() {
        return (CTGrayscaleEffect[])this.getXmlObjectArray(PROPERTY_QNAME[19], (XmlObject[])new CTGrayscaleEffect[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGrayscaleEffect getGraysclArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGrayscaleEffect target = null;
            target = (CTGrayscaleEffect)this.get_store().find_element_user(PROPERTY_QNAME[19], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfGraysclArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[19]);
        }
    }

    @Override
    public void setGraysclArray(CTGrayscaleEffect[] graysclArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])graysclArray, PROPERTY_QNAME[19]);
    }

    @Override
    public void setGraysclArray(int i, CTGrayscaleEffect grayscl) {
        this.generatedSetterHelperImpl((XmlObject)grayscl, PROPERTY_QNAME[19], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGrayscaleEffect insertNewGrayscl(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGrayscaleEffect target = null;
            target = (CTGrayscaleEffect)this.get_store().insert_element_user(PROPERTY_QNAME[19], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGrayscaleEffect addNewGrayscl() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGrayscaleEffect target = null;
            target = (CTGrayscaleEffect)this.get_store().add_element_user(PROPERTY_QNAME[19]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeGrayscl(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[19], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTHSLEffect> getHslList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTHSLEffect>(this::getHslArray, this::setHslArray, this::insertNewHsl, this::removeHsl, this::sizeOfHslArray);
        }
    }

    @Override
    public CTHSLEffect[] getHslArray() {
        return (CTHSLEffect[])this.getXmlObjectArray(PROPERTY_QNAME[20], (XmlObject[])new CTHSLEffect[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHSLEffect getHslArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHSLEffect target = null;
            target = (CTHSLEffect)this.get_store().find_element_user(PROPERTY_QNAME[20], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfHslArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[20]);
        }
    }

    @Override
    public void setHslArray(CTHSLEffect[] hslArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])hslArray, PROPERTY_QNAME[20]);
    }

    @Override
    public void setHslArray(int i, CTHSLEffect hsl) {
        this.generatedSetterHelperImpl((XmlObject)hsl, PROPERTY_QNAME[20], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHSLEffect insertNewHsl(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHSLEffect target = null;
            target = (CTHSLEffect)this.get_store().insert_element_user(PROPERTY_QNAME[20], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHSLEffect addNewHsl() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHSLEffect target = null;
            target = (CTHSLEffect)this.get_store().add_element_user(PROPERTY_QNAME[20]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeHsl(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[20], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTInnerShadowEffect> getInnerShdwList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTInnerShadowEffect>(this::getInnerShdwArray, this::setInnerShdwArray, this::insertNewInnerShdw, this::removeInnerShdw, this::sizeOfInnerShdwArray);
        }
    }

    @Override
    public CTInnerShadowEffect[] getInnerShdwArray() {
        return (CTInnerShadowEffect[])this.getXmlObjectArray(PROPERTY_QNAME[21], (XmlObject[])new CTInnerShadowEffect[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTInnerShadowEffect getInnerShdwArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTInnerShadowEffect target = null;
            target = (CTInnerShadowEffect)this.get_store().find_element_user(PROPERTY_QNAME[21], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfInnerShdwArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[21]);
        }
    }

    @Override
    public void setInnerShdwArray(CTInnerShadowEffect[] innerShdwArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])innerShdwArray, PROPERTY_QNAME[21]);
    }

    @Override
    public void setInnerShdwArray(int i, CTInnerShadowEffect innerShdw) {
        this.generatedSetterHelperImpl((XmlObject)innerShdw, PROPERTY_QNAME[21], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTInnerShadowEffect insertNewInnerShdw(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTInnerShadowEffect target = null;
            target = (CTInnerShadowEffect)this.get_store().insert_element_user(PROPERTY_QNAME[21], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTInnerShadowEffect addNewInnerShdw() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTInnerShadowEffect target = null;
            target = (CTInnerShadowEffect)this.get_store().add_element_user(PROPERTY_QNAME[21]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeInnerShdw(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[21], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTLuminanceEffect> getLumList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTLuminanceEffect>(this::getLumArray, this::setLumArray, this::insertNewLum, this::removeLum, this::sizeOfLumArray);
        }
    }

    @Override
    public CTLuminanceEffect[] getLumArray() {
        return (CTLuminanceEffect[])this.getXmlObjectArray(PROPERTY_QNAME[22], (XmlObject[])new CTLuminanceEffect[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLuminanceEffect getLumArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLuminanceEffect target = null;
            target = (CTLuminanceEffect)this.get_store().find_element_user(PROPERTY_QNAME[22], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfLumArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[22]);
        }
    }

    @Override
    public void setLumArray(CTLuminanceEffect[] lumArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])lumArray, PROPERTY_QNAME[22]);
    }

    @Override
    public void setLumArray(int i, CTLuminanceEffect lum) {
        this.generatedSetterHelperImpl((XmlObject)lum, PROPERTY_QNAME[22], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLuminanceEffect insertNewLum(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLuminanceEffect target = null;
            target = (CTLuminanceEffect)this.get_store().insert_element_user(PROPERTY_QNAME[22], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLuminanceEffect addNewLum() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLuminanceEffect target = null;
            target = (CTLuminanceEffect)this.get_store().add_element_user(PROPERTY_QNAME[22]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeLum(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[22], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTOuterShadowEffect> getOuterShdwList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTOuterShadowEffect>(this::getOuterShdwArray, this::setOuterShdwArray, this::insertNewOuterShdw, this::removeOuterShdw, this::sizeOfOuterShdwArray);
        }
    }

    @Override
    public CTOuterShadowEffect[] getOuterShdwArray() {
        return (CTOuterShadowEffect[])this.getXmlObjectArray(PROPERTY_QNAME[23], new CTOuterShadowEffect[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOuterShadowEffect getOuterShdwArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOuterShadowEffect target = null;
            target = (CTOuterShadowEffect)((Object)this.get_store().find_element_user(PROPERTY_QNAME[23], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfOuterShdwArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[23]);
        }
    }

    @Override
    public void setOuterShdwArray(CTOuterShadowEffect[] outerShdwArray) {
        this.check_orphaned();
        this.arraySetterHelper(outerShdwArray, PROPERTY_QNAME[23]);
    }

    @Override
    public void setOuterShdwArray(int i, CTOuterShadowEffect outerShdw) {
        this.generatedSetterHelperImpl(outerShdw, PROPERTY_QNAME[23], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOuterShadowEffect insertNewOuterShdw(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOuterShadowEffect target = null;
            target = (CTOuterShadowEffect)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[23], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOuterShadowEffect addNewOuterShdw() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOuterShadowEffect target = null;
            target = (CTOuterShadowEffect)((Object)this.get_store().add_element_user(PROPERTY_QNAME[23]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeOuterShdw(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[23], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTPresetShadowEffect> getPrstShdwList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTPresetShadowEffect>(this::getPrstShdwArray, this::setPrstShdwArray, this::insertNewPrstShdw, this::removePrstShdw, this::sizeOfPrstShdwArray);
        }
    }

    @Override
    public CTPresetShadowEffect[] getPrstShdwArray() {
        return (CTPresetShadowEffect[])this.getXmlObjectArray(PROPERTY_QNAME[24], (XmlObject[])new CTPresetShadowEffect[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPresetShadowEffect getPrstShdwArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPresetShadowEffect target = null;
            target = (CTPresetShadowEffect)this.get_store().find_element_user(PROPERTY_QNAME[24], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfPrstShdwArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[24]);
        }
    }

    @Override
    public void setPrstShdwArray(CTPresetShadowEffect[] prstShdwArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])prstShdwArray, PROPERTY_QNAME[24]);
    }

    @Override
    public void setPrstShdwArray(int i, CTPresetShadowEffect prstShdw) {
        this.generatedSetterHelperImpl((XmlObject)prstShdw, PROPERTY_QNAME[24], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPresetShadowEffect insertNewPrstShdw(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPresetShadowEffect target = null;
            target = (CTPresetShadowEffect)this.get_store().insert_element_user(PROPERTY_QNAME[24], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPresetShadowEffect addNewPrstShdw() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPresetShadowEffect target = null;
            target = (CTPresetShadowEffect)this.get_store().add_element_user(PROPERTY_QNAME[24]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removePrstShdw(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[24], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTReflectionEffect> getReflectionList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTReflectionEffect>(this::getReflectionArray, this::setReflectionArray, this::insertNewReflection, this::removeReflection, this::sizeOfReflectionArray);
        }
    }

    @Override
    public CTReflectionEffect[] getReflectionArray() {
        return (CTReflectionEffect[])this.getXmlObjectArray(PROPERTY_QNAME[25], (XmlObject[])new CTReflectionEffect[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTReflectionEffect getReflectionArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTReflectionEffect target = null;
            target = (CTReflectionEffect)this.get_store().find_element_user(PROPERTY_QNAME[25], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfReflectionArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[25]);
        }
    }

    @Override
    public void setReflectionArray(CTReflectionEffect[] reflectionArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])reflectionArray, PROPERTY_QNAME[25]);
    }

    @Override
    public void setReflectionArray(int i, CTReflectionEffect reflection) {
        this.generatedSetterHelperImpl((XmlObject)reflection, PROPERTY_QNAME[25], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTReflectionEffect insertNewReflection(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTReflectionEffect target = null;
            target = (CTReflectionEffect)this.get_store().insert_element_user(PROPERTY_QNAME[25], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTReflectionEffect addNewReflection() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTReflectionEffect target = null;
            target = (CTReflectionEffect)this.get_store().add_element_user(PROPERTY_QNAME[25]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeReflection(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[25], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTRelativeOffsetEffect> getRelOffList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTRelativeOffsetEffect>(this::getRelOffArray, this::setRelOffArray, this::insertNewRelOff, this::removeRelOff, this::sizeOfRelOffArray);
        }
    }

    @Override
    public CTRelativeOffsetEffect[] getRelOffArray() {
        return (CTRelativeOffsetEffect[])this.getXmlObjectArray(PROPERTY_QNAME[26], (XmlObject[])new CTRelativeOffsetEffect[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRelativeOffsetEffect getRelOffArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRelativeOffsetEffect target = null;
            target = (CTRelativeOffsetEffect)this.get_store().find_element_user(PROPERTY_QNAME[26], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfRelOffArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[26]);
        }
    }

    @Override
    public void setRelOffArray(CTRelativeOffsetEffect[] relOffArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])relOffArray, PROPERTY_QNAME[26]);
    }

    @Override
    public void setRelOffArray(int i, CTRelativeOffsetEffect relOff) {
        this.generatedSetterHelperImpl((XmlObject)relOff, PROPERTY_QNAME[26], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRelativeOffsetEffect insertNewRelOff(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRelativeOffsetEffect target = null;
            target = (CTRelativeOffsetEffect)this.get_store().insert_element_user(PROPERTY_QNAME[26], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRelativeOffsetEffect addNewRelOff() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRelativeOffsetEffect target = null;
            target = (CTRelativeOffsetEffect)this.get_store().add_element_user(PROPERTY_QNAME[26]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeRelOff(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[26], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTSoftEdgesEffect> getSoftEdgeList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTSoftEdgesEffect>(this::getSoftEdgeArray, this::setSoftEdgeArray, this::insertNewSoftEdge, this::removeSoftEdge, this::sizeOfSoftEdgeArray);
        }
    }

    @Override
    public CTSoftEdgesEffect[] getSoftEdgeArray() {
        return (CTSoftEdgesEffect[])this.getXmlObjectArray(PROPERTY_QNAME[27], (XmlObject[])new CTSoftEdgesEffect[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSoftEdgesEffect getSoftEdgeArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSoftEdgesEffect target = null;
            target = (CTSoftEdgesEffect)this.get_store().find_element_user(PROPERTY_QNAME[27], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfSoftEdgeArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[27]);
        }
    }

    @Override
    public void setSoftEdgeArray(CTSoftEdgesEffect[] softEdgeArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])softEdgeArray, PROPERTY_QNAME[27]);
    }

    @Override
    public void setSoftEdgeArray(int i, CTSoftEdgesEffect softEdge) {
        this.generatedSetterHelperImpl((XmlObject)softEdge, PROPERTY_QNAME[27], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSoftEdgesEffect insertNewSoftEdge(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSoftEdgesEffect target = null;
            target = (CTSoftEdgesEffect)this.get_store().insert_element_user(PROPERTY_QNAME[27], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSoftEdgesEffect addNewSoftEdge() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSoftEdgesEffect target = null;
            target = (CTSoftEdgesEffect)this.get_store().add_element_user(PROPERTY_QNAME[27]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSoftEdge(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[27], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTTintEffect> getTintList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTintEffect>(this::getTintArray, this::setTintArray, this::insertNewTint, this::removeTint, this::sizeOfTintArray);
        }
    }

    @Override
    public CTTintEffect[] getTintArray() {
        return (CTTintEffect[])this.getXmlObjectArray(PROPERTY_QNAME[28], (XmlObject[])new CTTintEffect[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTintEffect getTintArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTintEffect target = null;
            target = (CTTintEffect)this.get_store().find_element_user(PROPERTY_QNAME[28], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfTintArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[28]);
        }
    }

    @Override
    public void setTintArray(CTTintEffect[] tintArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])tintArray, PROPERTY_QNAME[28]);
    }

    @Override
    public void setTintArray(int i, CTTintEffect tint) {
        this.generatedSetterHelperImpl((XmlObject)tint, PROPERTY_QNAME[28], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTintEffect insertNewTint(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTintEffect target = null;
            target = (CTTintEffect)this.get_store().insert_element_user(PROPERTY_QNAME[28], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTintEffect addNewTint() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTintEffect target = null;
            target = (CTTintEffect)this.get_store().add_element_user(PROPERTY_QNAME[28]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeTint(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[28], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTTransformEffect> getXfrmList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTransformEffect>(this::getXfrmArray, this::setXfrmArray, this::insertNewXfrm, this::removeXfrm, this::sizeOfXfrmArray);
        }
    }

    @Override
    public CTTransformEffect[] getXfrmArray() {
        return (CTTransformEffect[])this.getXmlObjectArray(PROPERTY_QNAME[29], (XmlObject[])new CTTransformEffect[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTransformEffect getXfrmArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTransformEffect target = null;
            target = (CTTransformEffect)this.get_store().find_element_user(PROPERTY_QNAME[29], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfXfrmArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[29]);
        }
    }

    @Override
    public void setXfrmArray(CTTransformEffect[] xfrmArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])xfrmArray, PROPERTY_QNAME[29]);
    }

    @Override
    public void setXfrmArray(int i, CTTransformEffect xfrm) {
        this.generatedSetterHelperImpl((XmlObject)xfrm, PROPERTY_QNAME[29], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTransformEffect insertNewXfrm(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTransformEffect target = null;
            target = (CTTransformEffect)this.get_store().insert_element_user(PROPERTY_QNAME[29], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTransformEffect addNewXfrm() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTransformEffect target = null;
            target = (CTTransformEffect)this.get_store().add_element_user(PROPERTY_QNAME[29]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeXfrm(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[29], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STEffectContainerType.Enum getType() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[30]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_default_attribute_value(PROPERTY_QNAME[30]));
            }
            return target == null ? null : (STEffectContainerType.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STEffectContainerType xgetType() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STEffectContainerType target = null;
            target = (STEffectContainerType)this.get_store().find_attribute_user(PROPERTY_QNAME[30]);
            if (target == null) {
                target = (STEffectContainerType)this.get_default_attribute_value(PROPERTY_QNAME[30]);
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetType() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[30]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setType(STEffectContainerType.Enum type) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[30]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[30]));
            }
            target.setEnumValue(type);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetType(STEffectContainerType type) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STEffectContainerType target = null;
            target = (STEffectContainerType)this.get_store().find_attribute_user(PROPERTY_QNAME[30]);
            if (target == null) {
                target = (STEffectContainerType)this.get_store().add_attribute_user(PROPERTY_QNAME[30]);
            }
            target.set((XmlObject)type);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetType() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[30]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getName() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[31]));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlToken xgetName() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlToken target = null;
            target = (XmlToken)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[31]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetName() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[31]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setName(String name) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[31]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[31]));
            }
            target.setStringValue(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetName(XmlToken name) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlToken target = null;
            target = (XmlToken)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[31]));
            if (target == null) {
                target = (XmlToken)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[31]));
            }
            target.set(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetName() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[31]);
        }
    }
}

