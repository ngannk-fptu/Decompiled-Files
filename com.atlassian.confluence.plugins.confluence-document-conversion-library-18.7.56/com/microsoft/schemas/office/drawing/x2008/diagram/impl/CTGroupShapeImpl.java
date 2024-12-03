/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.drawing.x2008.diagram.impl;

import com.microsoft.schemas.office.drawing.x2008.diagram.CTGroupShape;
import com.microsoft.schemas.office.drawing.x2008.diagram.CTGroupShapeNonVisual;
import com.microsoft.schemas.office.drawing.x2008.diagram.CTShape;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;

public class CTGroupShapeImpl
extends XmlComplexContentImpl
implements CTGroupShape {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.microsoft.com/office/drawing/2008/diagram", "nvGrpSpPr"), new QName("http://schemas.microsoft.com/office/drawing/2008/diagram", "grpSpPr"), new QName("http://schemas.microsoft.com/office/drawing/2008/diagram", "sp"), new QName("http://schemas.microsoft.com/office/drawing/2008/diagram", "grpSp"), new QName("http://schemas.microsoft.com/office/drawing/2008/diagram", "extLst")};

    public CTGroupShapeImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGroupShapeNonVisual getNvGrpSpPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGroupShapeNonVisual target = null;
            target = (CTGroupShapeNonVisual)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setNvGrpSpPr(CTGroupShapeNonVisual nvGrpSpPr) {
        this.generatedSetterHelperImpl(nvGrpSpPr, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGroupShapeNonVisual addNewNvGrpSpPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGroupShapeNonVisual target = null;
            target = (CTGroupShapeNonVisual)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGroupShapeProperties getGrpSpPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGroupShapeProperties target = null;
            target = (CTGroupShapeProperties)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setGrpSpPr(CTGroupShapeProperties grpSpPr) {
        this.generatedSetterHelperImpl(grpSpPr, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGroupShapeProperties addNewGrpSpPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGroupShapeProperties target = null;
            target = (CTGroupShapeProperties)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTShape> getSpList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTShape>(this::getSpArray, this::setSpArray, this::insertNewSp, this::removeSp, this::sizeOfSpArray);
        }
    }

    @Override
    public CTShape[] getSpArray() {
        return (CTShape[])this.getXmlObjectArray(PROPERTY_QNAME[2], new CTShape[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTShape getSpArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTShape target = null;
            target = (CTShape)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], i));
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
    public int sizeOfSpArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]);
        }
    }

    @Override
    public void setSpArray(CTShape[] spArray) {
        this.check_orphaned();
        this.arraySetterHelper(spArray, PROPERTY_QNAME[2]);
    }

    @Override
    public void setSpArray(int i, CTShape sp) {
        this.generatedSetterHelperImpl(sp, PROPERTY_QNAME[2], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTShape insertNewSp(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTShape target = null;
            target = (CTShape)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[2], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTShape addNewSp() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTShape target = null;
            target = (CTShape)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSp(int i) {
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
    public List<CTGroupShape> getGrpSpList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTGroupShape>(this::getGrpSpArray, this::setGrpSpArray, this::insertNewGrpSp, this::removeGrpSp, this::sizeOfGrpSpArray);
        }
    }

    @Override
    public CTGroupShape[] getGrpSpArray() {
        return (CTGroupShape[])this.getXmlObjectArray(PROPERTY_QNAME[3], new CTGroupShape[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGroupShape getGrpSpArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGroupShape target = null;
            target = (CTGroupShape)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], i));
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
    public int sizeOfGrpSpArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]);
        }
    }

    @Override
    public void setGrpSpArray(CTGroupShape[] grpSpArray) {
        this.check_orphaned();
        this.arraySetterHelper(grpSpArray, PROPERTY_QNAME[3]);
    }

    @Override
    public void setGrpSpArray(int i, CTGroupShape grpSp) {
        this.generatedSetterHelperImpl(grpSp, PROPERTY_QNAME[3], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGroupShape insertNewGrpSp(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGroupShape target = null;
            target = (CTGroupShape)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[3], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGroupShape addNewGrpSp() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGroupShape target = null;
            target = (CTGroupShape)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeGrpSp(int i) {
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
    public CTOfficeArtExtensionList getExtLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOfficeArtExtensionList target = null;
            target = (CTOfficeArtExtensionList)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetExtLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[4]) != 0;
        }
    }

    @Override
    public void setExtLst(CTOfficeArtExtensionList extLst) {
        this.generatedSetterHelperImpl(extLst, PROPERTY_QNAME[4], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOfficeArtExtensionList addNewExtLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOfficeArtExtensionList target = null;
            target = (CTOfficeArtExtensionList)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetExtLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[4], 0);
        }
    }
}

