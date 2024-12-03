/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTAngle
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTComplementTransform
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTGammaTransform
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTGrayscaleTransform
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTInverseGammaTransform
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTInverseTransform
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveFixedAngle
 */
package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAngle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTComplementTransform;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFixedPercentage;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGammaTransform;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGrayscaleTransform;
import org.openxmlformats.schemas.drawingml.x2006.main.CTInverseGammaTransform;
import org.openxmlformats.schemas.drawingml.x2006.main.CTInverseTransform;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPercentage;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveFixedAngle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveFixedPercentage;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositivePercentage;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSystemColor;
import org.openxmlformats.schemas.drawingml.x2006.main.STSystemColorVal;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STHexColorRGB;

public class CTSystemColorImpl
extends XmlComplexContentImpl
implements CTSystemColor {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "tint"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "shade"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "comp"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "inv"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "gray"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "alpha"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "alphaOff"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "alphaMod"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "hue"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "hueOff"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "hueMod"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "sat"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "satOff"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "satMod"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lum"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lumOff"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lumMod"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "red"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "redOff"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "redMod"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "green"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "greenOff"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "greenMod"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "blue"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "blueOff"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "blueMod"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "gamma"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "invGamma"), new QName("", "val"), new QName("", "lastClr")};

    public CTSystemColorImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTPositiveFixedPercentage> getTintList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTPositiveFixedPercentage>(this::getTintArray, this::setTintArray, this::insertNewTint, this::removeTint, this::sizeOfTintArray);
        }
    }

    @Override
    public CTPositiveFixedPercentage[] getTintArray() {
        return (CTPositiveFixedPercentage[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTPositiveFixedPercentage[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPositiveFixedPercentage getTintArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPositiveFixedPercentage target = null;
            target = (CTPositiveFixedPercentage)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setTintArray(CTPositiveFixedPercentage[] tintArray) {
        this.check_orphaned();
        this.arraySetterHelper(tintArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setTintArray(int i, CTPositiveFixedPercentage tint) {
        this.generatedSetterHelperImpl(tint, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPositiveFixedPercentage insertNewTint(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPositiveFixedPercentage target = null;
            target = (CTPositiveFixedPercentage)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPositiveFixedPercentage addNewTint() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPositiveFixedPercentage target = null;
            target = (CTPositiveFixedPercentage)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
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
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTPositiveFixedPercentage> getShadeList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTPositiveFixedPercentage>(this::getShadeArray, this::setShadeArray, this::insertNewShade, this::removeShade, this::sizeOfShadeArray);
        }
    }

    @Override
    public CTPositiveFixedPercentage[] getShadeArray() {
        return (CTPositiveFixedPercentage[])this.getXmlObjectArray(PROPERTY_QNAME[1], new CTPositiveFixedPercentage[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPositiveFixedPercentage getShadeArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPositiveFixedPercentage target = null;
            target = (CTPositiveFixedPercentage)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], i));
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
    public int sizeOfShadeArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]);
        }
    }

    @Override
    public void setShadeArray(CTPositiveFixedPercentage[] shadeArray) {
        this.check_orphaned();
        this.arraySetterHelper(shadeArray, PROPERTY_QNAME[1]);
    }

    @Override
    public void setShadeArray(int i, CTPositiveFixedPercentage shade) {
        this.generatedSetterHelperImpl(shade, PROPERTY_QNAME[1], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPositiveFixedPercentage insertNewShade(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPositiveFixedPercentage target = null;
            target = (CTPositiveFixedPercentage)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[1], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPositiveFixedPercentage addNewShade() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPositiveFixedPercentage target = null;
            target = (CTPositiveFixedPercentage)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeShade(int i) {
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
    public List<CTComplementTransform> getCompList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTComplementTransform>(this::getCompArray, this::setCompArray, this::insertNewComp, this::removeComp, this::sizeOfCompArray);
        }
    }

    @Override
    public CTComplementTransform[] getCompArray() {
        return (CTComplementTransform[])this.getXmlObjectArray(PROPERTY_QNAME[2], (XmlObject[])new CTComplementTransform[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTComplementTransform getCompArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTComplementTransform target = null;
            target = (CTComplementTransform)this.get_store().find_element_user(PROPERTY_QNAME[2], i);
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
    public int sizeOfCompArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]);
        }
    }

    @Override
    public void setCompArray(CTComplementTransform[] compArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])compArray, PROPERTY_QNAME[2]);
    }

    @Override
    public void setCompArray(int i, CTComplementTransform comp) {
        this.generatedSetterHelperImpl((XmlObject)comp, PROPERTY_QNAME[2], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTComplementTransform insertNewComp(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTComplementTransform target = null;
            target = (CTComplementTransform)this.get_store().insert_element_user(PROPERTY_QNAME[2], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTComplementTransform addNewComp() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTComplementTransform target = null;
            target = (CTComplementTransform)this.get_store().add_element_user(PROPERTY_QNAME[2]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeComp(int i) {
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
    public List<CTInverseTransform> getInvList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTInverseTransform>(this::getInvArray, this::setInvArray, this::insertNewInv, this::removeInv, this::sizeOfInvArray);
        }
    }

    @Override
    public CTInverseTransform[] getInvArray() {
        return (CTInverseTransform[])this.getXmlObjectArray(PROPERTY_QNAME[3], (XmlObject[])new CTInverseTransform[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTInverseTransform getInvArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTInverseTransform target = null;
            target = (CTInverseTransform)this.get_store().find_element_user(PROPERTY_QNAME[3], i);
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
    public int sizeOfInvArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]);
        }
    }

    @Override
    public void setInvArray(CTInverseTransform[] invArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])invArray, PROPERTY_QNAME[3]);
    }

    @Override
    public void setInvArray(int i, CTInverseTransform inv) {
        this.generatedSetterHelperImpl((XmlObject)inv, PROPERTY_QNAME[3], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTInverseTransform insertNewInv(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTInverseTransform target = null;
            target = (CTInverseTransform)this.get_store().insert_element_user(PROPERTY_QNAME[3], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTInverseTransform addNewInv() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTInverseTransform target = null;
            target = (CTInverseTransform)this.get_store().add_element_user(PROPERTY_QNAME[3]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeInv(int i) {
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
    public List<CTGrayscaleTransform> getGrayList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTGrayscaleTransform>(this::getGrayArray, this::setGrayArray, this::insertNewGray, this::removeGray, this::sizeOfGrayArray);
        }
    }

    @Override
    public CTGrayscaleTransform[] getGrayArray() {
        return (CTGrayscaleTransform[])this.getXmlObjectArray(PROPERTY_QNAME[4], (XmlObject[])new CTGrayscaleTransform[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGrayscaleTransform getGrayArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGrayscaleTransform target = null;
            target = (CTGrayscaleTransform)this.get_store().find_element_user(PROPERTY_QNAME[4], i);
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
    public int sizeOfGrayArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[4]);
        }
    }

    @Override
    public void setGrayArray(CTGrayscaleTransform[] grayArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])grayArray, PROPERTY_QNAME[4]);
    }

    @Override
    public void setGrayArray(int i, CTGrayscaleTransform gray) {
        this.generatedSetterHelperImpl((XmlObject)gray, PROPERTY_QNAME[4], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGrayscaleTransform insertNewGray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGrayscaleTransform target = null;
            target = (CTGrayscaleTransform)this.get_store().insert_element_user(PROPERTY_QNAME[4], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGrayscaleTransform addNewGray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGrayscaleTransform target = null;
            target = (CTGrayscaleTransform)this.get_store().add_element_user(PROPERTY_QNAME[4]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeGray(int i) {
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
    public List<CTPositiveFixedPercentage> getAlphaList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTPositiveFixedPercentage>(this::getAlphaArray, this::setAlphaArray, this::insertNewAlpha, this::removeAlpha, this::sizeOfAlphaArray);
        }
    }

    @Override
    public CTPositiveFixedPercentage[] getAlphaArray() {
        return (CTPositiveFixedPercentage[])this.getXmlObjectArray(PROPERTY_QNAME[5], new CTPositiveFixedPercentage[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPositiveFixedPercentage getAlphaArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPositiveFixedPercentage target = null;
            target = (CTPositiveFixedPercentage)((Object)this.get_store().find_element_user(PROPERTY_QNAME[5], i));
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
    public int sizeOfAlphaArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[5]);
        }
    }

    @Override
    public void setAlphaArray(CTPositiveFixedPercentage[] alphaArray) {
        this.check_orphaned();
        this.arraySetterHelper(alphaArray, PROPERTY_QNAME[5]);
    }

    @Override
    public void setAlphaArray(int i, CTPositiveFixedPercentage alpha) {
        this.generatedSetterHelperImpl(alpha, PROPERTY_QNAME[5], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPositiveFixedPercentage insertNewAlpha(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPositiveFixedPercentage target = null;
            target = (CTPositiveFixedPercentage)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[5], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPositiveFixedPercentage addNewAlpha() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPositiveFixedPercentage target = null;
            target = (CTPositiveFixedPercentage)((Object)this.get_store().add_element_user(PROPERTY_QNAME[5]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAlpha(int i) {
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
    public List<CTFixedPercentage> getAlphaOffList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTFixedPercentage>(this::getAlphaOffArray, this::setAlphaOffArray, this::insertNewAlphaOff, this::removeAlphaOff, this::sizeOfAlphaOffArray);
        }
    }

    @Override
    public CTFixedPercentage[] getAlphaOffArray() {
        return (CTFixedPercentage[])this.getXmlObjectArray(PROPERTY_QNAME[6], new CTFixedPercentage[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFixedPercentage getAlphaOffArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFixedPercentage target = null;
            target = (CTFixedPercentage)((Object)this.get_store().find_element_user(PROPERTY_QNAME[6], i));
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
    public int sizeOfAlphaOffArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[6]);
        }
    }

    @Override
    public void setAlphaOffArray(CTFixedPercentage[] alphaOffArray) {
        this.check_orphaned();
        this.arraySetterHelper(alphaOffArray, PROPERTY_QNAME[6]);
    }

    @Override
    public void setAlphaOffArray(int i, CTFixedPercentage alphaOff) {
        this.generatedSetterHelperImpl(alphaOff, PROPERTY_QNAME[6], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFixedPercentage insertNewAlphaOff(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFixedPercentage target = null;
            target = (CTFixedPercentage)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[6], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFixedPercentage addNewAlphaOff() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFixedPercentage target = null;
            target = (CTFixedPercentage)((Object)this.get_store().add_element_user(PROPERTY_QNAME[6]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAlphaOff(int i) {
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
    public List<CTPositivePercentage> getAlphaModList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTPositivePercentage>(this::getAlphaModArray, this::setAlphaModArray, this::insertNewAlphaMod, this::removeAlphaMod, this::sizeOfAlphaModArray);
        }
    }

    @Override
    public CTPositivePercentage[] getAlphaModArray() {
        return (CTPositivePercentage[])this.getXmlObjectArray(PROPERTY_QNAME[7], new CTPositivePercentage[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPositivePercentage getAlphaModArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPositivePercentage target = null;
            target = (CTPositivePercentage)((Object)this.get_store().find_element_user(PROPERTY_QNAME[7], i));
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
            return this.get_store().count_elements(PROPERTY_QNAME[7]);
        }
    }

    @Override
    public void setAlphaModArray(CTPositivePercentage[] alphaModArray) {
        this.check_orphaned();
        this.arraySetterHelper(alphaModArray, PROPERTY_QNAME[7]);
    }

    @Override
    public void setAlphaModArray(int i, CTPositivePercentage alphaMod) {
        this.generatedSetterHelperImpl(alphaMod, PROPERTY_QNAME[7], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPositivePercentage insertNewAlphaMod(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPositivePercentage target = null;
            target = (CTPositivePercentage)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[7], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPositivePercentage addNewAlphaMod() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPositivePercentage target = null;
            target = (CTPositivePercentage)((Object)this.get_store().add_element_user(PROPERTY_QNAME[7]));
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
            this.get_store().remove_element(PROPERTY_QNAME[7], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTPositiveFixedAngle> getHueList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTPositiveFixedAngle>(this::getHueArray, this::setHueArray, this::insertNewHue, this::removeHue, this::sizeOfHueArray);
        }
    }

    @Override
    public CTPositiveFixedAngle[] getHueArray() {
        return (CTPositiveFixedAngle[])this.getXmlObjectArray(PROPERTY_QNAME[8], (XmlObject[])new CTPositiveFixedAngle[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPositiveFixedAngle getHueArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPositiveFixedAngle target = null;
            target = (CTPositiveFixedAngle)this.get_store().find_element_user(PROPERTY_QNAME[8], i);
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
    public int sizeOfHueArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[8]);
        }
    }

    @Override
    public void setHueArray(CTPositiveFixedAngle[] hueArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])hueArray, PROPERTY_QNAME[8]);
    }

    @Override
    public void setHueArray(int i, CTPositiveFixedAngle hue) {
        this.generatedSetterHelperImpl((XmlObject)hue, PROPERTY_QNAME[8], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPositiveFixedAngle insertNewHue(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPositiveFixedAngle target = null;
            target = (CTPositiveFixedAngle)this.get_store().insert_element_user(PROPERTY_QNAME[8], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPositiveFixedAngle addNewHue() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPositiveFixedAngle target = null;
            target = (CTPositiveFixedAngle)this.get_store().add_element_user(PROPERTY_QNAME[8]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeHue(int i) {
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
    public List<CTAngle> getHueOffList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTAngle>(this::getHueOffArray, this::setHueOffArray, this::insertNewHueOff, this::removeHueOff, this::sizeOfHueOffArray);
        }
    }

    @Override
    public CTAngle[] getHueOffArray() {
        return (CTAngle[])this.getXmlObjectArray(PROPERTY_QNAME[9], (XmlObject[])new CTAngle[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAngle getHueOffArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAngle target = null;
            target = (CTAngle)this.get_store().find_element_user(PROPERTY_QNAME[9], i);
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
    public int sizeOfHueOffArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[9]);
        }
    }

    @Override
    public void setHueOffArray(CTAngle[] hueOffArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])hueOffArray, PROPERTY_QNAME[9]);
    }

    @Override
    public void setHueOffArray(int i, CTAngle hueOff) {
        this.generatedSetterHelperImpl((XmlObject)hueOff, PROPERTY_QNAME[9], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAngle insertNewHueOff(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAngle target = null;
            target = (CTAngle)this.get_store().insert_element_user(PROPERTY_QNAME[9], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAngle addNewHueOff() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAngle target = null;
            target = (CTAngle)this.get_store().add_element_user(PROPERTY_QNAME[9]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeHueOff(int i) {
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
    public List<CTPositivePercentage> getHueModList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTPositivePercentage>(this::getHueModArray, this::setHueModArray, this::insertNewHueMod, this::removeHueMod, this::sizeOfHueModArray);
        }
    }

    @Override
    public CTPositivePercentage[] getHueModArray() {
        return (CTPositivePercentage[])this.getXmlObjectArray(PROPERTY_QNAME[10], new CTPositivePercentage[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPositivePercentage getHueModArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPositivePercentage target = null;
            target = (CTPositivePercentage)((Object)this.get_store().find_element_user(PROPERTY_QNAME[10], i));
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
    public int sizeOfHueModArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[10]);
        }
    }

    @Override
    public void setHueModArray(CTPositivePercentage[] hueModArray) {
        this.check_orphaned();
        this.arraySetterHelper(hueModArray, PROPERTY_QNAME[10]);
    }

    @Override
    public void setHueModArray(int i, CTPositivePercentage hueMod) {
        this.generatedSetterHelperImpl(hueMod, PROPERTY_QNAME[10], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPositivePercentage insertNewHueMod(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPositivePercentage target = null;
            target = (CTPositivePercentage)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[10], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPositivePercentage addNewHueMod() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPositivePercentage target = null;
            target = (CTPositivePercentage)((Object)this.get_store().add_element_user(PROPERTY_QNAME[10]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeHueMod(int i) {
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
    public List<CTPercentage> getSatList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTPercentage>(this::getSatArray, this::setSatArray, this::insertNewSat, this::removeSat, this::sizeOfSatArray);
        }
    }

    @Override
    public CTPercentage[] getSatArray() {
        return (CTPercentage[])this.getXmlObjectArray(PROPERTY_QNAME[11], new CTPercentage[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage getSatArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().find_element_user(PROPERTY_QNAME[11], i));
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
    public int sizeOfSatArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[11]);
        }
    }

    @Override
    public void setSatArray(CTPercentage[] satArray) {
        this.check_orphaned();
        this.arraySetterHelper(satArray, PROPERTY_QNAME[11]);
    }

    @Override
    public void setSatArray(int i, CTPercentage sat) {
        this.generatedSetterHelperImpl(sat, PROPERTY_QNAME[11], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage insertNewSat(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[11], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage addNewSat() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().add_element_user(PROPERTY_QNAME[11]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSat(int i) {
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
    public List<CTPercentage> getSatOffList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTPercentage>(this::getSatOffArray, this::setSatOffArray, this::insertNewSatOff, this::removeSatOff, this::sizeOfSatOffArray);
        }
    }

    @Override
    public CTPercentage[] getSatOffArray() {
        return (CTPercentage[])this.getXmlObjectArray(PROPERTY_QNAME[12], new CTPercentage[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage getSatOffArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().find_element_user(PROPERTY_QNAME[12], i));
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
    public int sizeOfSatOffArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[12]);
        }
    }

    @Override
    public void setSatOffArray(CTPercentage[] satOffArray) {
        this.check_orphaned();
        this.arraySetterHelper(satOffArray, PROPERTY_QNAME[12]);
    }

    @Override
    public void setSatOffArray(int i, CTPercentage satOff) {
        this.generatedSetterHelperImpl(satOff, PROPERTY_QNAME[12], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage insertNewSatOff(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[12], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage addNewSatOff() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().add_element_user(PROPERTY_QNAME[12]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSatOff(int i) {
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
    public List<CTPercentage> getSatModList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTPercentage>(this::getSatModArray, this::setSatModArray, this::insertNewSatMod, this::removeSatMod, this::sizeOfSatModArray);
        }
    }

    @Override
    public CTPercentage[] getSatModArray() {
        return (CTPercentage[])this.getXmlObjectArray(PROPERTY_QNAME[13], new CTPercentage[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage getSatModArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().find_element_user(PROPERTY_QNAME[13], i));
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
    public int sizeOfSatModArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[13]);
        }
    }

    @Override
    public void setSatModArray(CTPercentage[] satModArray) {
        this.check_orphaned();
        this.arraySetterHelper(satModArray, PROPERTY_QNAME[13]);
    }

    @Override
    public void setSatModArray(int i, CTPercentage satMod) {
        this.generatedSetterHelperImpl(satMod, PROPERTY_QNAME[13], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage insertNewSatMod(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[13], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage addNewSatMod() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().add_element_user(PROPERTY_QNAME[13]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSatMod(int i) {
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
    public List<CTPercentage> getLumList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTPercentage>(this::getLumArray, this::setLumArray, this::insertNewLum, this::removeLum, this::sizeOfLumArray);
        }
    }

    @Override
    public CTPercentage[] getLumArray() {
        return (CTPercentage[])this.getXmlObjectArray(PROPERTY_QNAME[14], new CTPercentage[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage getLumArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().find_element_user(PROPERTY_QNAME[14], i));
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
            return this.get_store().count_elements(PROPERTY_QNAME[14]);
        }
    }

    @Override
    public void setLumArray(CTPercentage[] lumArray) {
        this.check_orphaned();
        this.arraySetterHelper(lumArray, PROPERTY_QNAME[14]);
    }

    @Override
    public void setLumArray(int i, CTPercentage lum) {
        this.generatedSetterHelperImpl(lum, PROPERTY_QNAME[14], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage insertNewLum(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[14], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage addNewLum() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().add_element_user(PROPERTY_QNAME[14]));
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
            this.get_store().remove_element(PROPERTY_QNAME[14], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTPercentage> getLumOffList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTPercentage>(this::getLumOffArray, this::setLumOffArray, this::insertNewLumOff, this::removeLumOff, this::sizeOfLumOffArray);
        }
    }

    @Override
    public CTPercentage[] getLumOffArray() {
        return (CTPercentage[])this.getXmlObjectArray(PROPERTY_QNAME[15], new CTPercentage[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage getLumOffArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().find_element_user(PROPERTY_QNAME[15], i));
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
    public int sizeOfLumOffArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[15]);
        }
    }

    @Override
    public void setLumOffArray(CTPercentage[] lumOffArray) {
        this.check_orphaned();
        this.arraySetterHelper(lumOffArray, PROPERTY_QNAME[15]);
    }

    @Override
    public void setLumOffArray(int i, CTPercentage lumOff) {
        this.generatedSetterHelperImpl(lumOff, PROPERTY_QNAME[15], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage insertNewLumOff(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[15], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage addNewLumOff() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().add_element_user(PROPERTY_QNAME[15]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeLumOff(int i) {
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
    public List<CTPercentage> getLumModList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTPercentage>(this::getLumModArray, this::setLumModArray, this::insertNewLumMod, this::removeLumMod, this::sizeOfLumModArray);
        }
    }

    @Override
    public CTPercentage[] getLumModArray() {
        return (CTPercentage[])this.getXmlObjectArray(PROPERTY_QNAME[16], new CTPercentage[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage getLumModArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().find_element_user(PROPERTY_QNAME[16], i));
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
    public int sizeOfLumModArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[16]);
        }
    }

    @Override
    public void setLumModArray(CTPercentage[] lumModArray) {
        this.check_orphaned();
        this.arraySetterHelper(lumModArray, PROPERTY_QNAME[16]);
    }

    @Override
    public void setLumModArray(int i, CTPercentage lumMod) {
        this.generatedSetterHelperImpl(lumMod, PROPERTY_QNAME[16], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage insertNewLumMod(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[16], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage addNewLumMod() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().add_element_user(PROPERTY_QNAME[16]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeLumMod(int i) {
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
    public List<CTPercentage> getRedList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTPercentage>(this::getRedArray, this::setRedArray, this::insertNewRed, this::removeRed, this::sizeOfRedArray);
        }
    }

    @Override
    public CTPercentage[] getRedArray() {
        return (CTPercentage[])this.getXmlObjectArray(PROPERTY_QNAME[17], new CTPercentage[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage getRedArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().find_element_user(PROPERTY_QNAME[17], i));
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
    public int sizeOfRedArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[17]);
        }
    }

    @Override
    public void setRedArray(CTPercentage[] redArray) {
        this.check_orphaned();
        this.arraySetterHelper(redArray, PROPERTY_QNAME[17]);
    }

    @Override
    public void setRedArray(int i, CTPercentage red) {
        this.generatedSetterHelperImpl(red, PROPERTY_QNAME[17], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage insertNewRed(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[17], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage addNewRed() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().add_element_user(PROPERTY_QNAME[17]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeRed(int i) {
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
    public List<CTPercentage> getRedOffList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTPercentage>(this::getRedOffArray, this::setRedOffArray, this::insertNewRedOff, this::removeRedOff, this::sizeOfRedOffArray);
        }
    }

    @Override
    public CTPercentage[] getRedOffArray() {
        return (CTPercentage[])this.getXmlObjectArray(PROPERTY_QNAME[18], new CTPercentage[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage getRedOffArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().find_element_user(PROPERTY_QNAME[18], i));
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
    public int sizeOfRedOffArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[18]);
        }
    }

    @Override
    public void setRedOffArray(CTPercentage[] redOffArray) {
        this.check_orphaned();
        this.arraySetterHelper(redOffArray, PROPERTY_QNAME[18]);
    }

    @Override
    public void setRedOffArray(int i, CTPercentage redOff) {
        this.generatedSetterHelperImpl(redOff, PROPERTY_QNAME[18], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage insertNewRedOff(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[18], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage addNewRedOff() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().add_element_user(PROPERTY_QNAME[18]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeRedOff(int i) {
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
    public List<CTPercentage> getRedModList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTPercentage>(this::getRedModArray, this::setRedModArray, this::insertNewRedMod, this::removeRedMod, this::sizeOfRedModArray);
        }
    }

    @Override
    public CTPercentage[] getRedModArray() {
        return (CTPercentage[])this.getXmlObjectArray(PROPERTY_QNAME[19], new CTPercentage[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage getRedModArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().find_element_user(PROPERTY_QNAME[19], i));
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
    public int sizeOfRedModArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[19]);
        }
    }

    @Override
    public void setRedModArray(CTPercentage[] redModArray) {
        this.check_orphaned();
        this.arraySetterHelper(redModArray, PROPERTY_QNAME[19]);
    }

    @Override
    public void setRedModArray(int i, CTPercentage redMod) {
        this.generatedSetterHelperImpl(redMod, PROPERTY_QNAME[19], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage insertNewRedMod(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[19], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage addNewRedMod() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().add_element_user(PROPERTY_QNAME[19]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeRedMod(int i) {
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
    public List<CTPercentage> getGreenList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTPercentage>(this::getGreenArray, this::setGreenArray, this::insertNewGreen, this::removeGreen, this::sizeOfGreenArray);
        }
    }

    @Override
    public CTPercentage[] getGreenArray() {
        return (CTPercentage[])this.getXmlObjectArray(PROPERTY_QNAME[20], new CTPercentage[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage getGreenArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().find_element_user(PROPERTY_QNAME[20], i));
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
    public int sizeOfGreenArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[20]);
        }
    }

    @Override
    public void setGreenArray(CTPercentage[] greenArray) {
        this.check_orphaned();
        this.arraySetterHelper(greenArray, PROPERTY_QNAME[20]);
    }

    @Override
    public void setGreenArray(int i, CTPercentage green) {
        this.generatedSetterHelperImpl(green, PROPERTY_QNAME[20], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage insertNewGreen(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[20], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage addNewGreen() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().add_element_user(PROPERTY_QNAME[20]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeGreen(int i) {
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
    public List<CTPercentage> getGreenOffList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTPercentage>(this::getGreenOffArray, this::setGreenOffArray, this::insertNewGreenOff, this::removeGreenOff, this::sizeOfGreenOffArray);
        }
    }

    @Override
    public CTPercentage[] getGreenOffArray() {
        return (CTPercentage[])this.getXmlObjectArray(PROPERTY_QNAME[21], new CTPercentage[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage getGreenOffArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().find_element_user(PROPERTY_QNAME[21], i));
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
    public int sizeOfGreenOffArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[21]);
        }
    }

    @Override
    public void setGreenOffArray(CTPercentage[] greenOffArray) {
        this.check_orphaned();
        this.arraySetterHelper(greenOffArray, PROPERTY_QNAME[21]);
    }

    @Override
    public void setGreenOffArray(int i, CTPercentage greenOff) {
        this.generatedSetterHelperImpl(greenOff, PROPERTY_QNAME[21], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage insertNewGreenOff(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[21], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage addNewGreenOff() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().add_element_user(PROPERTY_QNAME[21]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeGreenOff(int i) {
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
    public List<CTPercentage> getGreenModList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTPercentage>(this::getGreenModArray, this::setGreenModArray, this::insertNewGreenMod, this::removeGreenMod, this::sizeOfGreenModArray);
        }
    }

    @Override
    public CTPercentage[] getGreenModArray() {
        return (CTPercentage[])this.getXmlObjectArray(PROPERTY_QNAME[22], new CTPercentage[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage getGreenModArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().find_element_user(PROPERTY_QNAME[22], i));
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
    public int sizeOfGreenModArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[22]);
        }
    }

    @Override
    public void setGreenModArray(CTPercentage[] greenModArray) {
        this.check_orphaned();
        this.arraySetterHelper(greenModArray, PROPERTY_QNAME[22]);
    }

    @Override
    public void setGreenModArray(int i, CTPercentage greenMod) {
        this.generatedSetterHelperImpl(greenMod, PROPERTY_QNAME[22], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage insertNewGreenMod(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[22], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage addNewGreenMod() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().add_element_user(PROPERTY_QNAME[22]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeGreenMod(int i) {
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
    public List<CTPercentage> getBlueList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTPercentage>(this::getBlueArray, this::setBlueArray, this::insertNewBlue, this::removeBlue, this::sizeOfBlueArray);
        }
    }

    @Override
    public CTPercentage[] getBlueArray() {
        return (CTPercentage[])this.getXmlObjectArray(PROPERTY_QNAME[23], new CTPercentage[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage getBlueArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().find_element_user(PROPERTY_QNAME[23], i));
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
    public int sizeOfBlueArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[23]);
        }
    }

    @Override
    public void setBlueArray(CTPercentage[] blueArray) {
        this.check_orphaned();
        this.arraySetterHelper(blueArray, PROPERTY_QNAME[23]);
    }

    @Override
    public void setBlueArray(int i, CTPercentage blue) {
        this.generatedSetterHelperImpl(blue, PROPERTY_QNAME[23], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage insertNewBlue(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[23], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage addNewBlue() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().add_element_user(PROPERTY_QNAME[23]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeBlue(int i) {
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
    public List<CTPercentage> getBlueOffList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTPercentage>(this::getBlueOffArray, this::setBlueOffArray, this::insertNewBlueOff, this::removeBlueOff, this::sizeOfBlueOffArray);
        }
    }

    @Override
    public CTPercentage[] getBlueOffArray() {
        return (CTPercentage[])this.getXmlObjectArray(PROPERTY_QNAME[24], new CTPercentage[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage getBlueOffArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().find_element_user(PROPERTY_QNAME[24], i));
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
    public int sizeOfBlueOffArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[24]);
        }
    }

    @Override
    public void setBlueOffArray(CTPercentage[] blueOffArray) {
        this.check_orphaned();
        this.arraySetterHelper(blueOffArray, PROPERTY_QNAME[24]);
    }

    @Override
    public void setBlueOffArray(int i, CTPercentage blueOff) {
        this.generatedSetterHelperImpl(blueOff, PROPERTY_QNAME[24], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage insertNewBlueOff(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[24], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage addNewBlueOff() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().add_element_user(PROPERTY_QNAME[24]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeBlueOff(int i) {
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
    public List<CTPercentage> getBlueModList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTPercentage>(this::getBlueModArray, this::setBlueModArray, this::insertNewBlueMod, this::removeBlueMod, this::sizeOfBlueModArray);
        }
    }

    @Override
    public CTPercentage[] getBlueModArray() {
        return (CTPercentage[])this.getXmlObjectArray(PROPERTY_QNAME[25], new CTPercentage[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage getBlueModArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().find_element_user(PROPERTY_QNAME[25], i));
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
    public int sizeOfBlueModArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[25]);
        }
    }

    @Override
    public void setBlueModArray(CTPercentage[] blueModArray) {
        this.check_orphaned();
        this.arraySetterHelper(blueModArray, PROPERTY_QNAME[25]);
    }

    @Override
    public void setBlueModArray(int i, CTPercentage blueMod) {
        this.generatedSetterHelperImpl(blueMod, PROPERTY_QNAME[25], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage insertNewBlueMod(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[25], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPercentage addNewBlueMod() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPercentage target = null;
            target = (CTPercentage)((Object)this.get_store().add_element_user(PROPERTY_QNAME[25]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeBlueMod(int i) {
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
    public List<CTGammaTransform> getGammaList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTGammaTransform>(this::getGammaArray, this::setGammaArray, this::insertNewGamma, this::removeGamma, this::sizeOfGammaArray);
        }
    }

    @Override
    public CTGammaTransform[] getGammaArray() {
        return (CTGammaTransform[])this.getXmlObjectArray(PROPERTY_QNAME[26], (XmlObject[])new CTGammaTransform[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGammaTransform getGammaArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGammaTransform target = null;
            target = (CTGammaTransform)this.get_store().find_element_user(PROPERTY_QNAME[26], i);
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
    public int sizeOfGammaArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[26]);
        }
    }

    @Override
    public void setGammaArray(CTGammaTransform[] gammaArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])gammaArray, PROPERTY_QNAME[26]);
    }

    @Override
    public void setGammaArray(int i, CTGammaTransform gamma) {
        this.generatedSetterHelperImpl((XmlObject)gamma, PROPERTY_QNAME[26], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGammaTransform insertNewGamma(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGammaTransform target = null;
            target = (CTGammaTransform)this.get_store().insert_element_user(PROPERTY_QNAME[26], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGammaTransform addNewGamma() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGammaTransform target = null;
            target = (CTGammaTransform)this.get_store().add_element_user(PROPERTY_QNAME[26]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeGamma(int i) {
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
    public List<CTInverseGammaTransform> getInvGammaList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTInverseGammaTransform>(this::getInvGammaArray, this::setInvGammaArray, this::insertNewInvGamma, this::removeInvGamma, this::sizeOfInvGammaArray);
        }
    }

    @Override
    public CTInverseGammaTransform[] getInvGammaArray() {
        return (CTInverseGammaTransform[])this.getXmlObjectArray(PROPERTY_QNAME[27], (XmlObject[])new CTInverseGammaTransform[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTInverseGammaTransform getInvGammaArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTInverseGammaTransform target = null;
            target = (CTInverseGammaTransform)this.get_store().find_element_user(PROPERTY_QNAME[27], i);
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
    public int sizeOfInvGammaArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[27]);
        }
    }

    @Override
    public void setInvGammaArray(CTInverseGammaTransform[] invGammaArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])invGammaArray, PROPERTY_QNAME[27]);
    }

    @Override
    public void setInvGammaArray(int i, CTInverseGammaTransform invGamma) {
        this.generatedSetterHelperImpl((XmlObject)invGamma, PROPERTY_QNAME[27], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTInverseGammaTransform insertNewInvGamma(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTInverseGammaTransform target = null;
            target = (CTInverseGammaTransform)this.get_store().insert_element_user(PROPERTY_QNAME[27], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTInverseGammaTransform addNewInvGamma() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTInverseGammaTransform target = null;
            target = (CTInverseGammaTransform)this.get_store().add_element_user(PROPERTY_QNAME[27]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeInvGamma(int i) {
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
    public STSystemColorVal.Enum getVal() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[28]));
            return target == null ? null : (STSystemColorVal.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STSystemColorVal xgetVal() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STSystemColorVal target = null;
            target = (STSystemColorVal)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[28]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setVal(STSystemColorVal.Enum val) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[28]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[28]));
            }
            target.setEnumValue(val);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetVal(STSystemColorVal val) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STSystemColorVal target = null;
            target = (STSystemColorVal)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[28]));
            if (target == null) {
                target = (STSystemColorVal)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[28]));
            }
            target.set(val);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public byte[] getLastClr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[29]));
            return target == null ? null : target.getByteArrayValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STHexColorRGB xgetLastClr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STHexColorRGB target = null;
            target = (STHexColorRGB)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[29]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetLastClr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[29]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setLastClr(byte[] lastClr) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[29]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[29]));
            }
            target.setByteArrayValue(lastClr);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetLastClr(STHexColorRGB lastClr) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STHexColorRGB target = null;
            target = (STHexColorRGB)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[29]));
            if (target == null) {
                target = (STHexColorRGB)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[29]));
            }
            target.set(lastClr);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetLastClr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[29]);
        }
    }
}

