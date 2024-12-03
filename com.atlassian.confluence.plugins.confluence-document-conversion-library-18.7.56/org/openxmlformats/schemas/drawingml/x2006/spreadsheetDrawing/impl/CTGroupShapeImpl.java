/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTConnector;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTGraphicalObjectFrame;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTGroupShape;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTGroupShapeNonVisual;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTPicture;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTShape;

public class CTGroupShapeImpl
extends XmlComplexContentImpl
implements CTGroupShape {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "nvGrpSpPr"), new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "grpSpPr"), new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "sp"), new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "grpSp"), new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "graphicFrame"), new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "cxnSp"), new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "pic")};

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
    public List<CTGraphicalObjectFrame> getGraphicFrameList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTGraphicalObjectFrame>(this::getGraphicFrameArray, this::setGraphicFrameArray, this::insertNewGraphicFrame, this::removeGraphicFrame, this::sizeOfGraphicFrameArray);
        }
    }

    @Override
    public CTGraphicalObjectFrame[] getGraphicFrameArray() {
        return (CTGraphicalObjectFrame[])this.getXmlObjectArray(PROPERTY_QNAME[4], new CTGraphicalObjectFrame[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGraphicalObjectFrame getGraphicFrameArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGraphicalObjectFrame target = null;
            target = (CTGraphicalObjectFrame)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], i));
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
    public int sizeOfGraphicFrameArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[4]);
        }
    }

    @Override
    public void setGraphicFrameArray(CTGraphicalObjectFrame[] graphicFrameArray) {
        this.check_orphaned();
        this.arraySetterHelper(graphicFrameArray, PROPERTY_QNAME[4]);
    }

    @Override
    public void setGraphicFrameArray(int i, CTGraphicalObjectFrame graphicFrame) {
        this.generatedSetterHelperImpl(graphicFrame, PROPERTY_QNAME[4], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGraphicalObjectFrame insertNewGraphicFrame(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGraphicalObjectFrame target = null;
            target = (CTGraphicalObjectFrame)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[4], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGraphicalObjectFrame addNewGraphicFrame() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGraphicalObjectFrame target = null;
            target = (CTGraphicalObjectFrame)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeGraphicFrame(int i) {
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
    public List<CTConnector> getCxnSpList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTConnector>(this::getCxnSpArray, this::setCxnSpArray, this::insertNewCxnSp, this::removeCxnSp, this::sizeOfCxnSpArray);
        }
    }

    @Override
    public CTConnector[] getCxnSpArray() {
        return (CTConnector[])this.getXmlObjectArray(PROPERTY_QNAME[5], new CTConnector[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTConnector getCxnSpArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTConnector target = null;
            target = (CTConnector)((Object)this.get_store().find_element_user(PROPERTY_QNAME[5], i));
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
    public int sizeOfCxnSpArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[5]);
        }
    }

    @Override
    public void setCxnSpArray(CTConnector[] cxnSpArray) {
        this.check_orphaned();
        this.arraySetterHelper(cxnSpArray, PROPERTY_QNAME[5]);
    }

    @Override
    public void setCxnSpArray(int i, CTConnector cxnSp) {
        this.generatedSetterHelperImpl(cxnSp, PROPERTY_QNAME[5], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTConnector insertNewCxnSp(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTConnector target = null;
            target = (CTConnector)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[5], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTConnector addNewCxnSp() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTConnector target = null;
            target = (CTConnector)((Object)this.get_store().add_element_user(PROPERTY_QNAME[5]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCxnSp(int i) {
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
    public List<CTPicture> getPicList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTPicture>(this::getPicArray, this::setPicArray, this::insertNewPic, this::removePic, this::sizeOfPicArray);
        }
    }

    @Override
    public CTPicture[] getPicArray() {
        return (CTPicture[])this.getXmlObjectArray(PROPERTY_QNAME[6], new CTPicture[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPicture getPicArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPicture target = null;
            target = (CTPicture)((Object)this.get_store().find_element_user(PROPERTY_QNAME[6], i));
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
    public int sizeOfPicArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[6]);
        }
    }

    @Override
    public void setPicArray(CTPicture[] picArray) {
        this.check_orphaned();
        this.arraySetterHelper(picArray, PROPERTY_QNAME[6]);
    }

    @Override
    public void setPicArray(int i, CTPicture pic) {
        this.generatedSetterHelperImpl(pic, PROPERTY_QNAME[6], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPicture insertNewPic(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPicture target = null;
            target = (CTPicture)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[6], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPicture addNewPic() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPicture target = null;
            target = (CTPicture)((Object)this.get_store().add_element_user(PROPERTY_QNAME[6]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removePic(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[6], i);
        }
    }
}

