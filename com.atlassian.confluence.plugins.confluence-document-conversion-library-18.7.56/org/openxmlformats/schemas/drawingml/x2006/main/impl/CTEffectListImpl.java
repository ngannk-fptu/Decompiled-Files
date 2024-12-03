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
package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlurEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFillOverlayEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGlowEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTInnerShadowEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOuterShadowEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetShadowEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTReflectionEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSoftEdgesEffect;

public class CTEffectListImpl
extends XmlComplexContentImpl
implements CTEffectList {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "blur"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "fillOverlay"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "glow"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "innerShdw"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "outerShdw"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "prstShdw"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "reflection"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "softEdge")};

    public CTEffectListImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBlurEffect getBlur() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBlurEffect target = null;
            target = (CTBlurEffect)this.get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetBlur() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]) != 0;
        }
    }

    @Override
    public void setBlur(CTBlurEffect blur) {
        this.generatedSetterHelperImpl((XmlObject)blur, PROPERTY_QNAME[0], 0, (short)1);
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
            target = (CTBlurEffect)this.get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetBlur() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFillOverlayEffect getFillOverlay() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFillOverlayEffect target = null;
            target = (CTFillOverlayEffect)this.get_store().find_element_user(PROPERTY_QNAME[1], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetFillOverlay() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]) != 0;
        }
    }

    @Override
    public void setFillOverlay(CTFillOverlayEffect fillOverlay) {
        this.generatedSetterHelperImpl((XmlObject)fillOverlay, PROPERTY_QNAME[1], 0, (short)1);
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
            target = (CTFillOverlayEffect)this.get_store().add_element_user(PROPERTY_QNAME[1]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetFillOverlay() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[1], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGlowEffect getGlow() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGlowEffect target = null;
            target = (CTGlowEffect)this.get_store().find_element_user(PROPERTY_QNAME[2], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetGlow() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]) != 0;
        }
    }

    @Override
    public void setGlow(CTGlowEffect glow) {
        this.generatedSetterHelperImpl((XmlObject)glow, PROPERTY_QNAME[2], 0, (short)1);
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
            target = (CTGlowEffect)this.get_store().add_element_user(PROPERTY_QNAME[2]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetGlow() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[2], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTInnerShadowEffect getInnerShdw() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTInnerShadowEffect target = null;
            target = (CTInnerShadowEffect)this.get_store().find_element_user(PROPERTY_QNAME[3], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetInnerShdw() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]) != 0;
        }
    }

    @Override
    public void setInnerShdw(CTInnerShadowEffect innerShdw) {
        this.generatedSetterHelperImpl((XmlObject)innerShdw, PROPERTY_QNAME[3], 0, (short)1);
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
            target = (CTInnerShadowEffect)this.get_store().add_element_user(PROPERTY_QNAME[3]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetInnerShdw() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[3], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOuterShadowEffect getOuterShdw() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOuterShadowEffect target = null;
            target = (CTOuterShadowEffect)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetOuterShdw() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[4]) != 0;
        }
    }

    @Override
    public void setOuterShdw(CTOuterShadowEffect outerShdw) {
        this.generatedSetterHelperImpl(outerShdw, PROPERTY_QNAME[4], 0, (short)1);
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
            target = (CTOuterShadowEffect)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetOuterShdw() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[4], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPresetShadowEffect getPrstShdw() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPresetShadowEffect target = null;
            target = (CTPresetShadowEffect)this.get_store().find_element_user(PROPERTY_QNAME[5], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetPrstShdw() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[5]) != 0;
        }
    }

    @Override
    public void setPrstShdw(CTPresetShadowEffect prstShdw) {
        this.generatedSetterHelperImpl((XmlObject)prstShdw, PROPERTY_QNAME[5], 0, (short)1);
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
            target = (CTPresetShadowEffect)this.get_store().add_element_user(PROPERTY_QNAME[5]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetPrstShdw() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[5], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTReflectionEffect getReflection() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTReflectionEffect target = null;
            target = (CTReflectionEffect)this.get_store().find_element_user(PROPERTY_QNAME[6], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetReflection() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[6]) != 0;
        }
    }

    @Override
    public void setReflection(CTReflectionEffect reflection) {
        this.generatedSetterHelperImpl((XmlObject)reflection, PROPERTY_QNAME[6], 0, (short)1);
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
            target = (CTReflectionEffect)this.get_store().add_element_user(PROPERTY_QNAME[6]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetReflection() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[6], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSoftEdgesEffect getSoftEdge() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSoftEdgesEffect target = null;
            target = (CTSoftEdgesEffect)this.get_store().find_element_user(PROPERTY_QNAME[7], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSoftEdge() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[7]) != 0;
        }
    }

    @Override
    public void setSoftEdge(CTSoftEdgesEffect softEdge) {
        this.generatedSetterHelperImpl((XmlObject)softEdge, PROPERTY_QNAME[7], 0, (short)1);
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
            target = (CTSoftEdgesEffect)this.get_store().add_element_user(PROPERTY_QNAME[7]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSoftEdge() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[7], 0);
        }
    }
}

