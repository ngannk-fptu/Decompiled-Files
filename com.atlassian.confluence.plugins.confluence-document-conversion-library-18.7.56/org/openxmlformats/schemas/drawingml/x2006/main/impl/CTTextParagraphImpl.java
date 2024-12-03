/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRegularTextRun;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextField;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextLineBreak;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraphProperties;

public class CTTextParagraphImpl
extends XmlComplexContentImpl
implements CTTextParagraph {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "pPr"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "r"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "br"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "fld"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "endParaRPr")};

    public CTTextParagraphImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextParagraphProperties getPPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextParagraphProperties target = null;
            target = (CTTextParagraphProperties)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetPPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]) != 0;
        }
    }

    @Override
    public void setPPr(CTTextParagraphProperties pPr) {
        this.generatedSetterHelperImpl(pPr, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextParagraphProperties addNewPPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextParagraphProperties target = null;
            target = (CTTextParagraphProperties)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetPPr() {
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
    public List<CTRegularTextRun> getRList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTRegularTextRun>(this::getRArray, this::setRArray, this::insertNewR, this::removeR, this::sizeOfRArray);
        }
    }

    @Override
    public CTRegularTextRun[] getRArray() {
        return (CTRegularTextRun[])this.getXmlObjectArray(PROPERTY_QNAME[1], new CTRegularTextRun[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRegularTextRun getRArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRegularTextRun target = null;
            target = (CTRegularTextRun)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], i));
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
    public int sizeOfRArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]);
        }
    }

    @Override
    public void setRArray(CTRegularTextRun[] rArray) {
        this.check_orphaned();
        this.arraySetterHelper(rArray, PROPERTY_QNAME[1]);
    }

    @Override
    public void setRArray(int i, CTRegularTextRun r) {
        this.generatedSetterHelperImpl(r, PROPERTY_QNAME[1], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRegularTextRun insertNewR(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRegularTextRun target = null;
            target = (CTRegularTextRun)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[1], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRegularTextRun addNewR() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRegularTextRun target = null;
            target = (CTRegularTextRun)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeR(int i) {
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
    public List<CTTextLineBreak> getBrList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTextLineBreak>(this::getBrArray, this::setBrArray, this::insertNewBr, this::removeBr, this::sizeOfBrArray);
        }
    }

    @Override
    public CTTextLineBreak[] getBrArray() {
        return (CTTextLineBreak[])this.getXmlObjectArray(PROPERTY_QNAME[2], new CTTextLineBreak[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextLineBreak getBrArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextLineBreak target = null;
            target = (CTTextLineBreak)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], i));
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
    public int sizeOfBrArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]);
        }
    }

    @Override
    public void setBrArray(CTTextLineBreak[] brArray) {
        this.check_orphaned();
        this.arraySetterHelper(brArray, PROPERTY_QNAME[2]);
    }

    @Override
    public void setBrArray(int i, CTTextLineBreak br) {
        this.generatedSetterHelperImpl(br, PROPERTY_QNAME[2], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextLineBreak insertNewBr(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextLineBreak target = null;
            target = (CTTextLineBreak)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[2], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextLineBreak addNewBr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextLineBreak target = null;
            target = (CTTextLineBreak)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeBr(int i) {
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
    public List<CTTextField> getFldList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTextField>(this::getFldArray, this::setFldArray, this::insertNewFld, this::removeFld, this::sizeOfFldArray);
        }
    }

    @Override
    public CTTextField[] getFldArray() {
        return (CTTextField[])this.getXmlObjectArray(PROPERTY_QNAME[3], new CTTextField[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextField getFldArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextField target = null;
            target = (CTTextField)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], i));
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
    public int sizeOfFldArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]);
        }
    }

    @Override
    public void setFldArray(CTTextField[] fldArray) {
        this.check_orphaned();
        this.arraySetterHelper(fldArray, PROPERTY_QNAME[3]);
    }

    @Override
    public void setFldArray(int i, CTTextField fld) {
        this.generatedSetterHelperImpl(fld, PROPERTY_QNAME[3], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextField insertNewFld(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextField target = null;
            target = (CTTextField)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[3], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextField addNewFld() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextField target = null;
            target = (CTTextField)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeFld(int i) {
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
    public CTTextCharacterProperties getEndParaRPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextCharacterProperties target = null;
            target = (CTTextCharacterProperties)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetEndParaRPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[4]) != 0;
        }
    }

    @Override
    public void setEndParaRPr(CTTextCharacterProperties endParaRPr) {
        this.generatedSetterHelperImpl(endParaRPr, PROPERTY_QNAME[4], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextCharacterProperties addNewEndParaRPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextCharacterProperties target = null;
            target = (CTTextCharacterProperties)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetEndParaRPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[4], 0);
        }
    }
}

