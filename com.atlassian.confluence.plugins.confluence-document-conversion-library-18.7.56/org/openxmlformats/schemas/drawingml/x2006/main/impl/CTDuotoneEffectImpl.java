/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTDuotoneEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTHslColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSRgbColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTScRgbColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSchemeColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSystemColor;

public class CTDuotoneEffectImpl
extends XmlComplexContentImpl
implements CTDuotoneEffect {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "scrgbClr"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "srgbClr"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "hslClr"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "sysClr"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "schemeClr"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "prstClr")};

    public CTDuotoneEffectImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTScRgbColor> getScrgbClrList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTScRgbColor>(this::getScrgbClrArray, this::setScrgbClrArray, this::insertNewScrgbClr, this::removeScrgbClr, this::sizeOfScrgbClrArray);
        }
    }

    @Override
    public CTScRgbColor[] getScrgbClrArray() {
        return (CTScRgbColor[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTScRgbColor[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTScRgbColor getScrgbClrArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTScRgbColor target = null;
            target = (CTScRgbColor)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfScrgbClrArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setScrgbClrArray(CTScRgbColor[] scrgbClrArray) {
        this.check_orphaned();
        this.arraySetterHelper(scrgbClrArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setScrgbClrArray(int i, CTScRgbColor scrgbClr) {
        this.generatedSetterHelperImpl(scrgbClr, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTScRgbColor insertNewScrgbClr(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTScRgbColor target = null;
            target = (CTScRgbColor)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTScRgbColor addNewScrgbClr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTScRgbColor target = null;
            target = (CTScRgbColor)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeScrgbClr(int i) {
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
    public List<CTSRgbColor> getSrgbClrList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTSRgbColor>(this::getSrgbClrArray, this::setSrgbClrArray, this::insertNewSrgbClr, this::removeSrgbClr, this::sizeOfSrgbClrArray);
        }
    }

    @Override
    public CTSRgbColor[] getSrgbClrArray() {
        return (CTSRgbColor[])this.getXmlObjectArray(PROPERTY_QNAME[1], new CTSRgbColor[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSRgbColor getSrgbClrArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSRgbColor target = null;
            target = (CTSRgbColor)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], i));
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
    public int sizeOfSrgbClrArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]);
        }
    }

    @Override
    public void setSrgbClrArray(CTSRgbColor[] srgbClrArray) {
        this.check_orphaned();
        this.arraySetterHelper(srgbClrArray, PROPERTY_QNAME[1]);
    }

    @Override
    public void setSrgbClrArray(int i, CTSRgbColor srgbClr) {
        this.generatedSetterHelperImpl(srgbClr, PROPERTY_QNAME[1], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSRgbColor insertNewSrgbClr(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSRgbColor target = null;
            target = (CTSRgbColor)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[1], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSRgbColor addNewSrgbClr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSRgbColor target = null;
            target = (CTSRgbColor)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSrgbClr(int i) {
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
    public List<CTHslColor> getHslClrList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTHslColor>(this::getHslClrArray, this::setHslClrArray, this::insertNewHslClr, this::removeHslClr, this::sizeOfHslClrArray);
        }
    }

    @Override
    public CTHslColor[] getHslClrArray() {
        return (CTHslColor[])this.getXmlObjectArray(PROPERTY_QNAME[2], new CTHslColor[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHslColor getHslClrArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHslColor target = null;
            target = (CTHslColor)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], i));
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
    public int sizeOfHslClrArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]);
        }
    }

    @Override
    public void setHslClrArray(CTHslColor[] hslClrArray) {
        this.check_orphaned();
        this.arraySetterHelper(hslClrArray, PROPERTY_QNAME[2]);
    }

    @Override
    public void setHslClrArray(int i, CTHslColor hslClr) {
        this.generatedSetterHelperImpl(hslClr, PROPERTY_QNAME[2], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHslColor insertNewHslClr(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHslColor target = null;
            target = (CTHslColor)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[2], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHslColor addNewHslClr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHslColor target = null;
            target = (CTHslColor)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeHslClr(int i) {
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
    public List<CTSystemColor> getSysClrList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTSystemColor>(this::getSysClrArray, this::setSysClrArray, this::insertNewSysClr, this::removeSysClr, this::sizeOfSysClrArray);
        }
    }

    @Override
    public CTSystemColor[] getSysClrArray() {
        return (CTSystemColor[])this.getXmlObjectArray(PROPERTY_QNAME[3], new CTSystemColor[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSystemColor getSysClrArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSystemColor target = null;
            target = (CTSystemColor)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], i));
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
    public int sizeOfSysClrArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]);
        }
    }

    @Override
    public void setSysClrArray(CTSystemColor[] sysClrArray) {
        this.check_orphaned();
        this.arraySetterHelper(sysClrArray, PROPERTY_QNAME[3]);
    }

    @Override
    public void setSysClrArray(int i, CTSystemColor sysClr) {
        this.generatedSetterHelperImpl(sysClr, PROPERTY_QNAME[3], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSystemColor insertNewSysClr(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSystemColor target = null;
            target = (CTSystemColor)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[3], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSystemColor addNewSysClr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSystemColor target = null;
            target = (CTSystemColor)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSysClr(int i) {
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
    public List<CTSchemeColor> getSchemeClrList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTSchemeColor>(this::getSchemeClrArray, this::setSchemeClrArray, this::insertNewSchemeClr, this::removeSchemeClr, this::sizeOfSchemeClrArray);
        }
    }

    @Override
    public CTSchemeColor[] getSchemeClrArray() {
        return (CTSchemeColor[])this.getXmlObjectArray(PROPERTY_QNAME[4], new CTSchemeColor[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSchemeColor getSchemeClrArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSchemeColor target = null;
            target = (CTSchemeColor)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], i));
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
    public int sizeOfSchemeClrArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[4]);
        }
    }

    @Override
    public void setSchemeClrArray(CTSchemeColor[] schemeClrArray) {
        this.check_orphaned();
        this.arraySetterHelper(schemeClrArray, PROPERTY_QNAME[4]);
    }

    @Override
    public void setSchemeClrArray(int i, CTSchemeColor schemeClr) {
        this.generatedSetterHelperImpl(schemeClr, PROPERTY_QNAME[4], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSchemeColor insertNewSchemeClr(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSchemeColor target = null;
            target = (CTSchemeColor)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[4], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSchemeColor addNewSchemeClr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSchemeColor target = null;
            target = (CTSchemeColor)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSchemeClr(int i) {
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
    public List<CTPresetColor> getPrstClrList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTPresetColor>(this::getPrstClrArray, this::setPrstClrArray, this::insertNewPrstClr, this::removePrstClr, this::sizeOfPrstClrArray);
        }
    }

    @Override
    public CTPresetColor[] getPrstClrArray() {
        return (CTPresetColor[])this.getXmlObjectArray(PROPERTY_QNAME[5], new CTPresetColor[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPresetColor getPrstClrArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPresetColor target = null;
            target = (CTPresetColor)((Object)this.get_store().find_element_user(PROPERTY_QNAME[5], i));
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
    public int sizeOfPrstClrArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[5]);
        }
    }

    @Override
    public void setPrstClrArray(CTPresetColor[] prstClrArray) {
        this.check_orphaned();
        this.arraySetterHelper(prstClrArray, PROPERTY_QNAME[5]);
    }

    @Override
    public void setPrstClrArray(int i, CTPresetColor prstClr) {
        this.generatedSetterHelperImpl(prstClr, PROPERTY_QNAME[5], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPresetColor insertNewPrstClr(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPresetColor target = null;
            target = (CTPresetColor)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[5], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPresetColor addNewPrstClr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPresetColor target = null;
            target = (CTPresetColor)((Object)this.get_store().add_element_user(PROPERTY_QNAME[5]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removePrstClr(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[5], i);
        }
    }
}

