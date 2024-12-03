/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.officeDocument.x2006.math.CTRPR
 */
package org.openxmlformats.schemas.officeDocument.x2006.math.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTR;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTRPR;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDrawing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTEmpty;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFldChar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFtnEdnRef;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMarkup;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPTab;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPicture;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRel;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRuby;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSym;

public class CTRImpl
extends XmlComplexContentImpl
implements CTR {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "rPr"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rPr"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "br"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "t"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "contentPart"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "delText"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "instrText"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "delInstrText"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "noBreakHyphen"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "softHyphen"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "dayShort"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "monthShort"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "yearShort"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "dayLong"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "monthLong"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "yearLong"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "annotationRef"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "footnoteRef"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "endnoteRef"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "separator"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "continuationSeparator"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "sym"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "pgNum"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "cr"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tab"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "object"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "pict"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "fldChar"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "ruby"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "footnoteReference"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "endnoteReference"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "commentReference"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "drawing"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "ptab"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "lastRenderedPageBreak"), new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "t")};

    public CTRImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRPR getRPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRPR target = null;
            target = (CTRPR)this.get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetRPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]) != 0;
        }
    }

    @Override
    public void setRPr(CTRPR rPr) {
        this.generatedSetterHelperImpl((XmlObject)rPr, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRPR addNewRPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRPR target = null;
            target = (CTRPR)this.get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetRPr() {
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
    public CTRPr getRPr2() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRPr target = null;
            target = (CTRPr)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetRPr2() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]) != 0;
        }
    }

    @Override
    public void setRPr2(CTRPr rPr2) {
        this.generatedSetterHelperImpl(rPr2, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRPr addNewRPr2() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRPr target = null;
            target = (CTRPr)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetRPr2() {
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
    public List<CTBr> getBrList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTBr>(this::getBrArray, this::setBrArray, this::insertNewBr, this::removeBr, this::sizeOfBrArray);
        }
    }

    @Override
    public CTBr[] getBrArray() {
        return (CTBr[])this.getXmlObjectArray(PROPERTY_QNAME[2], new CTBr[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBr getBrArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBr target = null;
            target = (CTBr)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], i));
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
    public void setBrArray(CTBr[] brArray) {
        this.check_orphaned();
        this.arraySetterHelper(brArray, PROPERTY_QNAME[2]);
    }

    @Override
    public void setBrArray(int i, CTBr br) {
        this.generatedSetterHelperImpl(br, PROPERTY_QNAME[2], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBr insertNewBr(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBr target = null;
            target = (CTBr)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[2], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBr addNewBr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBr target = null;
            target = (CTBr)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
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
    public List<org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText> getTList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText>(this::getTArray, this::setTArray, this::insertNewT, this::removeT, this::sizeOfTArray);
        }
    }

    @Override
    public org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText[] getTArray() {
        return (org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText[])this.getXmlObjectArray(PROPERTY_QNAME[3], new org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText getTArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText target = null;
            target = (org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], i));
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
    public int sizeOfTArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]);
        }
    }

    @Override
    public void setTArray(org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText[] tArray) {
        this.check_orphaned();
        this.arraySetterHelper(tArray, PROPERTY_QNAME[3]);
    }

    @Override
    public void setTArray(int i, org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText t) {
        this.generatedSetterHelperImpl(t, PROPERTY_QNAME[3], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText insertNewT(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText target = null;
            target = (org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[3], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText addNewT() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText target = null;
            target = (org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeT(int i) {
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
    public List<CTRel> getContentPartList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTRel>(this::getContentPartArray, this::setContentPartArray, this::insertNewContentPart, this::removeContentPart, this::sizeOfContentPartArray);
        }
    }

    @Override
    public CTRel[] getContentPartArray() {
        return (CTRel[])this.getXmlObjectArray(PROPERTY_QNAME[4], new CTRel[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRel getContentPartArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRel target = null;
            target = (CTRel)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], i));
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
    public int sizeOfContentPartArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[4]);
        }
    }

    @Override
    public void setContentPartArray(CTRel[] contentPartArray) {
        this.check_orphaned();
        this.arraySetterHelper(contentPartArray, PROPERTY_QNAME[4]);
    }

    @Override
    public void setContentPartArray(int i, CTRel contentPart) {
        this.generatedSetterHelperImpl(contentPart, PROPERTY_QNAME[4], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRel insertNewContentPart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRel target = null;
            target = (CTRel)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[4], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRel addNewContentPart() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRel target = null;
            target = (CTRel)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeContentPart(int i) {
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
    public List<org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText> getDelTextList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText>(this::getDelTextArray, this::setDelTextArray, this::insertNewDelText, this::removeDelText, this::sizeOfDelTextArray);
        }
    }

    @Override
    public org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText[] getDelTextArray() {
        return (org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText[])this.getXmlObjectArray(PROPERTY_QNAME[5], new org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText getDelTextArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText target = null;
            target = (org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText)((Object)this.get_store().find_element_user(PROPERTY_QNAME[5], i));
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
    public int sizeOfDelTextArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[5]);
        }
    }

    @Override
    public void setDelTextArray(org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText[] delTextArray) {
        this.check_orphaned();
        this.arraySetterHelper(delTextArray, PROPERTY_QNAME[5]);
    }

    @Override
    public void setDelTextArray(int i, org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText delText) {
        this.generatedSetterHelperImpl(delText, PROPERTY_QNAME[5], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText insertNewDelText(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText target = null;
            target = (org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[5], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText addNewDelText() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText target = null;
            target = (org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText)((Object)this.get_store().add_element_user(PROPERTY_QNAME[5]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeDelText(int i) {
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
    public List<org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText> getInstrTextList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText>(this::getInstrTextArray, this::setInstrTextArray, this::insertNewInstrText, this::removeInstrText, this::sizeOfInstrTextArray);
        }
    }

    @Override
    public org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText[] getInstrTextArray() {
        return (org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText[])this.getXmlObjectArray(PROPERTY_QNAME[6], new org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText getInstrTextArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText target = null;
            target = (org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText)((Object)this.get_store().find_element_user(PROPERTY_QNAME[6], i));
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
    public int sizeOfInstrTextArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[6]);
        }
    }

    @Override
    public void setInstrTextArray(org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText[] instrTextArray) {
        this.check_orphaned();
        this.arraySetterHelper(instrTextArray, PROPERTY_QNAME[6]);
    }

    @Override
    public void setInstrTextArray(int i, org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText instrText) {
        this.generatedSetterHelperImpl(instrText, PROPERTY_QNAME[6], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText insertNewInstrText(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText target = null;
            target = (org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[6], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText addNewInstrText() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText target = null;
            target = (org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText)((Object)this.get_store().add_element_user(PROPERTY_QNAME[6]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeInstrText(int i) {
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
    public List<org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText> getDelInstrTextList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText>(this::getDelInstrTextArray, this::setDelInstrTextArray, this::insertNewDelInstrText, this::removeDelInstrText, this::sizeOfDelInstrTextArray);
        }
    }

    @Override
    public org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText[] getDelInstrTextArray() {
        return (org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText[])this.getXmlObjectArray(PROPERTY_QNAME[7], new org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText getDelInstrTextArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText target = null;
            target = (org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText)((Object)this.get_store().find_element_user(PROPERTY_QNAME[7], i));
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
    public int sizeOfDelInstrTextArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[7]);
        }
    }

    @Override
    public void setDelInstrTextArray(org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText[] delInstrTextArray) {
        this.check_orphaned();
        this.arraySetterHelper(delInstrTextArray, PROPERTY_QNAME[7]);
    }

    @Override
    public void setDelInstrTextArray(int i, org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText delInstrText) {
        this.generatedSetterHelperImpl(delInstrText, PROPERTY_QNAME[7], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText insertNewDelInstrText(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText target = null;
            target = (org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[7], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText addNewDelInstrText() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText target = null;
            target = (org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText)((Object)this.get_store().add_element_user(PROPERTY_QNAME[7]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeDelInstrText(int i) {
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
    public List<CTEmpty> getNoBreakHyphenList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTEmpty>(this::getNoBreakHyphenArray, this::setNoBreakHyphenArray, this::insertNewNoBreakHyphen, this::removeNoBreakHyphen, this::sizeOfNoBreakHyphenArray);
        }
    }

    @Override
    public CTEmpty[] getNoBreakHyphenArray() {
        return (CTEmpty[])this.getXmlObjectArray(PROPERTY_QNAME[8], new CTEmpty[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty getNoBreakHyphenArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().find_element_user(PROPERTY_QNAME[8], i));
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
    public int sizeOfNoBreakHyphenArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[8]);
        }
    }

    @Override
    public void setNoBreakHyphenArray(CTEmpty[] noBreakHyphenArray) {
        this.check_orphaned();
        this.arraySetterHelper(noBreakHyphenArray, PROPERTY_QNAME[8]);
    }

    @Override
    public void setNoBreakHyphenArray(int i, CTEmpty noBreakHyphen) {
        this.generatedSetterHelperImpl(noBreakHyphen, PROPERTY_QNAME[8], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty insertNewNoBreakHyphen(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[8], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty addNewNoBreakHyphen() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().add_element_user(PROPERTY_QNAME[8]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeNoBreakHyphen(int i) {
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
    public List<CTEmpty> getSoftHyphenList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTEmpty>(this::getSoftHyphenArray, this::setSoftHyphenArray, this::insertNewSoftHyphen, this::removeSoftHyphen, this::sizeOfSoftHyphenArray);
        }
    }

    @Override
    public CTEmpty[] getSoftHyphenArray() {
        return (CTEmpty[])this.getXmlObjectArray(PROPERTY_QNAME[9], new CTEmpty[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty getSoftHyphenArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().find_element_user(PROPERTY_QNAME[9], i));
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
    public int sizeOfSoftHyphenArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[9]);
        }
    }

    @Override
    public void setSoftHyphenArray(CTEmpty[] softHyphenArray) {
        this.check_orphaned();
        this.arraySetterHelper(softHyphenArray, PROPERTY_QNAME[9]);
    }

    @Override
    public void setSoftHyphenArray(int i, CTEmpty softHyphen) {
        this.generatedSetterHelperImpl(softHyphen, PROPERTY_QNAME[9], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty insertNewSoftHyphen(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[9], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty addNewSoftHyphen() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().add_element_user(PROPERTY_QNAME[9]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSoftHyphen(int i) {
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
    public List<CTEmpty> getDayShortList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTEmpty>(this::getDayShortArray, this::setDayShortArray, this::insertNewDayShort, this::removeDayShort, this::sizeOfDayShortArray);
        }
    }

    @Override
    public CTEmpty[] getDayShortArray() {
        return (CTEmpty[])this.getXmlObjectArray(PROPERTY_QNAME[10], new CTEmpty[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty getDayShortArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().find_element_user(PROPERTY_QNAME[10], i));
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
    public int sizeOfDayShortArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[10]);
        }
    }

    @Override
    public void setDayShortArray(CTEmpty[] dayShortArray) {
        this.check_orphaned();
        this.arraySetterHelper(dayShortArray, PROPERTY_QNAME[10]);
    }

    @Override
    public void setDayShortArray(int i, CTEmpty dayShort) {
        this.generatedSetterHelperImpl(dayShort, PROPERTY_QNAME[10], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty insertNewDayShort(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[10], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty addNewDayShort() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().add_element_user(PROPERTY_QNAME[10]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeDayShort(int i) {
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
    public List<CTEmpty> getMonthShortList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTEmpty>(this::getMonthShortArray, this::setMonthShortArray, this::insertNewMonthShort, this::removeMonthShort, this::sizeOfMonthShortArray);
        }
    }

    @Override
    public CTEmpty[] getMonthShortArray() {
        return (CTEmpty[])this.getXmlObjectArray(PROPERTY_QNAME[11], new CTEmpty[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty getMonthShortArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().find_element_user(PROPERTY_QNAME[11], i));
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
    public int sizeOfMonthShortArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[11]);
        }
    }

    @Override
    public void setMonthShortArray(CTEmpty[] monthShortArray) {
        this.check_orphaned();
        this.arraySetterHelper(monthShortArray, PROPERTY_QNAME[11]);
    }

    @Override
    public void setMonthShortArray(int i, CTEmpty monthShort) {
        this.generatedSetterHelperImpl(monthShort, PROPERTY_QNAME[11], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty insertNewMonthShort(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[11], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty addNewMonthShort() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().add_element_user(PROPERTY_QNAME[11]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeMonthShort(int i) {
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
    public List<CTEmpty> getYearShortList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTEmpty>(this::getYearShortArray, this::setYearShortArray, this::insertNewYearShort, this::removeYearShort, this::sizeOfYearShortArray);
        }
    }

    @Override
    public CTEmpty[] getYearShortArray() {
        return (CTEmpty[])this.getXmlObjectArray(PROPERTY_QNAME[12], new CTEmpty[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty getYearShortArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().find_element_user(PROPERTY_QNAME[12], i));
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
    public int sizeOfYearShortArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[12]);
        }
    }

    @Override
    public void setYearShortArray(CTEmpty[] yearShortArray) {
        this.check_orphaned();
        this.arraySetterHelper(yearShortArray, PROPERTY_QNAME[12]);
    }

    @Override
    public void setYearShortArray(int i, CTEmpty yearShort) {
        this.generatedSetterHelperImpl(yearShort, PROPERTY_QNAME[12], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty insertNewYearShort(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[12], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty addNewYearShort() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().add_element_user(PROPERTY_QNAME[12]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeYearShort(int i) {
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
    public List<CTEmpty> getDayLongList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTEmpty>(this::getDayLongArray, this::setDayLongArray, this::insertNewDayLong, this::removeDayLong, this::sizeOfDayLongArray);
        }
    }

    @Override
    public CTEmpty[] getDayLongArray() {
        return (CTEmpty[])this.getXmlObjectArray(PROPERTY_QNAME[13], new CTEmpty[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty getDayLongArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().find_element_user(PROPERTY_QNAME[13], i));
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
    public int sizeOfDayLongArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[13]);
        }
    }

    @Override
    public void setDayLongArray(CTEmpty[] dayLongArray) {
        this.check_orphaned();
        this.arraySetterHelper(dayLongArray, PROPERTY_QNAME[13]);
    }

    @Override
    public void setDayLongArray(int i, CTEmpty dayLong) {
        this.generatedSetterHelperImpl(dayLong, PROPERTY_QNAME[13], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty insertNewDayLong(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[13], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty addNewDayLong() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().add_element_user(PROPERTY_QNAME[13]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeDayLong(int i) {
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
    public List<CTEmpty> getMonthLongList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTEmpty>(this::getMonthLongArray, this::setMonthLongArray, this::insertNewMonthLong, this::removeMonthLong, this::sizeOfMonthLongArray);
        }
    }

    @Override
    public CTEmpty[] getMonthLongArray() {
        return (CTEmpty[])this.getXmlObjectArray(PROPERTY_QNAME[14], new CTEmpty[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty getMonthLongArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().find_element_user(PROPERTY_QNAME[14], i));
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
    public int sizeOfMonthLongArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[14]);
        }
    }

    @Override
    public void setMonthLongArray(CTEmpty[] monthLongArray) {
        this.check_orphaned();
        this.arraySetterHelper(monthLongArray, PROPERTY_QNAME[14]);
    }

    @Override
    public void setMonthLongArray(int i, CTEmpty monthLong) {
        this.generatedSetterHelperImpl(monthLong, PROPERTY_QNAME[14], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty insertNewMonthLong(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[14], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty addNewMonthLong() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().add_element_user(PROPERTY_QNAME[14]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeMonthLong(int i) {
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
    public List<CTEmpty> getYearLongList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTEmpty>(this::getYearLongArray, this::setYearLongArray, this::insertNewYearLong, this::removeYearLong, this::sizeOfYearLongArray);
        }
    }

    @Override
    public CTEmpty[] getYearLongArray() {
        return (CTEmpty[])this.getXmlObjectArray(PROPERTY_QNAME[15], new CTEmpty[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty getYearLongArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().find_element_user(PROPERTY_QNAME[15], i));
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
    public int sizeOfYearLongArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[15]);
        }
    }

    @Override
    public void setYearLongArray(CTEmpty[] yearLongArray) {
        this.check_orphaned();
        this.arraySetterHelper(yearLongArray, PROPERTY_QNAME[15]);
    }

    @Override
    public void setYearLongArray(int i, CTEmpty yearLong) {
        this.generatedSetterHelperImpl(yearLong, PROPERTY_QNAME[15], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty insertNewYearLong(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[15], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty addNewYearLong() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().add_element_user(PROPERTY_QNAME[15]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeYearLong(int i) {
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
    public List<CTEmpty> getAnnotationRefList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTEmpty>(this::getAnnotationRefArray, this::setAnnotationRefArray, this::insertNewAnnotationRef, this::removeAnnotationRef, this::sizeOfAnnotationRefArray);
        }
    }

    @Override
    public CTEmpty[] getAnnotationRefArray() {
        return (CTEmpty[])this.getXmlObjectArray(PROPERTY_QNAME[16], new CTEmpty[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty getAnnotationRefArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().find_element_user(PROPERTY_QNAME[16], i));
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
    public int sizeOfAnnotationRefArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[16]);
        }
    }

    @Override
    public void setAnnotationRefArray(CTEmpty[] annotationRefArray) {
        this.check_orphaned();
        this.arraySetterHelper(annotationRefArray, PROPERTY_QNAME[16]);
    }

    @Override
    public void setAnnotationRefArray(int i, CTEmpty annotationRef) {
        this.generatedSetterHelperImpl(annotationRef, PROPERTY_QNAME[16], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty insertNewAnnotationRef(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[16], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty addNewAnnotationRef() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().add_element_user(PROPERTY_QNAME[16]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAnnotationRef(int i) {
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
    public List<CTEmpty> getFootnoteRefList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTEmpty>(this::getFootnoteRefArray, this::setFootnoteRefArray, this::insertNewFootnoteRef, this::removeFootnoteRef, this::sizeOfFootnoteRefArray);
        }
    }

    @Override
    public CTEmpty[] getFootnoteRefArray() {
        return (CTEmpty[])this.getXmlObjectArray(PROPERTY_QNAME[17], new CTEmpty[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty getFootnoteRefArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().find_element_user(PROPERTY_QNAME[17], i));
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
    public int sizeOfFootnoteRefArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[17]);
        }
    }

    @Override
    public void setFootnoteRefArray(CTEmpty[] footnoteRefArray) {
        this.check_orphaned();
        this.arraySetterHelper(footnoteRefArray, PROPERTY_QNAME[17]);
    }

    @Override
    public void setFootnoteRefArray(int i, CTEmpty footnoteRef) {
        this.generatedSetterHelperImpl(footnoteRef, PROPERTY_QNAME[17], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty insertNewFootnoteRef(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[17], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty addNewFootnoteRef() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().add_element_user(PROPERTY_QNAME[17]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeFootnoteRef(int i) {
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
    public List<CTEmpty> getEndnoteRefList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTEmpty>(this::getEndnoteRefArray, this::setEndnoteRefArray, this::insertNewEndnoteRef, this::removeEndnoteRef, this::sizeOfEndnoteRefArray);
        }
    }

    @Override
    public CTEmpty[] getEndnoteRefArray() {
        return (CTEmpty[])this.getXmlObjectArray(PROPERTY_QNAME[18], new CTEmpty[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty getEndnoteRefArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().find_element_user(PROPERTY_QNAME[18], i));
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
    public int sizeOfEndnoteRefArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[18]);
        }
    }

    @Override
    public void setEndnoteRefArray(CTEmpty[] endnoteRefArray) {
        this.check_orphaned();
        this.arraySetterHelper(endnoteRefArray, PROPERTY_QNAME[18]);
    }

    @Override
    public void setEndnoteRefArray(int i, CTEmpty endnoteRef) {
        this.generatedSetterHelperImpl(endnoteRef, PROPERTY_QNAME[18], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty insertNewEndnoteRef(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[18], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty addNewEndnoteRef() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().add_element_user(PROPERTY_QNAME[18]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeEndnoteRef(int i) {
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
    public List<CTEmpty> getSeparatorList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTEmpty>(this::getSeparatorArray, this::setSeparatorArray, this::insertNewSeparator, this::removeSeparator, this::sizeOfSeparatorArray);
        }
    }

    @Override
    public CTEmpty[] getSeparatorArray() {
        return (CTEmpty[])this.getXmlObjectArray(PROPERTY_QNAME[19], new CTEmpty[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty getSeparatorArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().find_element_user(PROPERTY_QNAME[19], i));
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
    public int sizeOfSeparatorArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[19]);
        }
    }

    @Override
    public void setSeparatorArray(CTEmpty[] separatorArray) {
        this.check_orphaned();
        this.arraySetterHelper(separatorArray, PROPERTY_QNAME[19]);
    }

    @Override
    public void setSeparatorArray(int i, CTEmpty separator) {
        this.generatedSetterHelperImpl(separator, PROPERTY_QNAME[19], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty insertNewSeparator(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[19], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty addNewSeparator() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().add_element_user(PROPERTY_QNAME[19]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSeparator(int i) {
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
    public List<CTEmpty> getContinuationSeparatorList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTEmpty>(this::getContinuationSeparatorArray, this::setContinuationSeparatorArray, this::insertNewContinuationSeparator, this::removeContinuationSeparator, this::sizeOfContinuationSeparatorArray);
        }
    }

    @Override
    public CTEmpty[] getContinuationSeparatorArray() {
        return (CTEmpty[])this.getXmlObjectArray(PROPERTY_QNAME[20], new CTEmpty[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty getContinuationSeparatorArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().find_element_user(PROPERTY_QNAME[20], i));
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
    public int sizeOfContinuationSeparatorArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[20]);
        }
    }

    @Override
    public void setContinuationSeparatorArray(CTEmpty[] continuationSeparatorArray) {
        this.check_orphaned();
        this.arraySetterHelper(continuationSeparatorArray, PROPERTY_QNAME[20]);
    }

    @Override
    public void setContinuationSeparatorArray(int i, CTEmpty continuationSeparator) {
        this.generatedSetterHelperImpl(continuationSeparator, PROPERTY_QNAME[20], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty insertNewContinuationSeparator(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[20], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty addNewContinuationSeparator() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().add_element_user(PROPERTY_QNAME[20]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeContinuationSeparator(int i) {
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
    public List<CTSym> getSymList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTSym>(this::getSymArray, this::setSymArray, this::insertNewSym, this::removeSym, this::sizeOfSymArray);
        }
    }

    @Override
    public CTSym[] getSymArray() {
        return (CTSym[])this.getXmlObjectArray(PROPERTY_QNAME[21], new CTSym[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSym getSymArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSym target = null;
            target = (CTSym)((Object)this.get_store().find_element_user(PROPERTY_QNAME[21], i));
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
    public int sizeOfSymArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[21]);
        }
    }

    @Override
    public void setSymArray(CTSym[] symArray) {
        this.check_orphaned();
        this.arraySetterHelper(symArray, PROPERTY_QNAME[21]);
    }

    @Override
    public void setSymArray(int i, CTSym sym) {
        this.generatedSetterHelperImpl(sym, PROPERTY_QNAME[21], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSym insertNewSym(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSym target = null;
            target = (CTSym)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[21], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSym addNewSym() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSym target = null;
            target = (CTSym)((Object)this.get_store().add_element_user(PROPERTY_QNAME[21]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSym(int i) {
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
    public List<CTEmpty> getPgNumList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTEmpty>(this::getPgNumArray, this::setPgNumArray, this::insertNewPgNum, this::removePgNum, this::sizeOfPgNumArray);
        }
    }

    @Override
    public CTEmpty[] getPgNumArray() {
        return (CTEmpty[])this.getXmlObjectArray(PROPERTY_QNAME[22], new CTEmpty[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty getPgNumArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().find_element_user(PROPERTY_QNAME[22], i));
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
    public int sizeOfPgNumArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[22]);
        }
    }

    @Override
    public void setPgNumArray(CTEmpty[] pgNumArray) {
        this.check_orphaned();
        this.arraySetterHelper(pgNumArray, PROPERTY_QNAME[22]);
    }

    @Override
    public void setPgNumArray(int i, CTEmpty pgNum) {
        this.generatedSetterHelperImpl(pgNum, PROPERTY_QNAME[22], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty insertNewPgNum(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[22], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty addNewPgNum() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().add_element_user(PROPERTY_QNAME[22]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removePgNum(int i) {
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
    public List<CTEmpty> getCrList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTEmpty>(this::getCrArray, this::setCrArray, this::insertNewCr, this::removeCr, this::sizeOfCrArray);
        }
    }

    @Override
    public CTEmpty[] getCrArray() {
        return (CTEmpty[])this.getXmlObjectArray(PROPERTY_QNAME[23], new CTEmpty[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty getCrArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().find_element_user(PROPERTY_QNAME[23], i));
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
    public int sizeOfCrArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[23]);
        }
    }

    @Override
    public void setCrArray(CTEmpty[] crArray) {
        this.check_orphaned();
        this.arraySetterHelper(crArray, PROPERTY_QNAME[23]);
    }

    @Override
    public void setCrArray(int i, CTEmpty cr) {
        this.generatedSetterHelperImpl(cr, PROPERTY_QNAME[23], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty insertNewCr(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[23], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty addNewCr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().add_element_user(PROPERTY_QNAME[23]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCr(int i) {
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
    public List<CTEmpty> getTabList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTEmpty>(this::getTabArray, this::setTabArray, this::insertNewTab, this::removeTab, this::sizeOfTabArray);
        }
    }

    @Override
    public CTEmpty[] getTabArray() {
        return (CTEmpty[])this.getXmlObjectArray(PROPERTY_QNAME[24], new CTEmpty[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty getTabArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().find_element_user(PROPERTY_QNAME[24], i));
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
    public int sizeOfTabArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[24]);
        }
    }

    @Override
    public void setTabArray(CTEmpty[] tabArray) {
        this.check_orphaned();
        this.arraySetterHelper(tabArray, PROPERTY_QNAME[24]);
    }

    @Override
    public void setTabArray(int i, CTEmpty tab) {
        this.generatedSetterHelperImpl(tab, PROPERTY_QNAME[24], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty insertNewTab(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[24], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty addNewTab() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().add_element_user(PROPERTY_QNAME[24]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeTab(int i) {
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
    public List<CTObject> getObjectList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTObject>(this::getObjectArray, this::setObjectArray, this::insertNewObject, this::removeObject, this::sizeOfObjectArray);
        }
    }

    @Override
    public CTObject[] getObjectArray() {
        return (CTObject[])this.getXmlObjectArray(PROPERTY_QNAME[25], new CTObject[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTObject getObjectArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTObject target = null;
            target = (CTObject)((Object)this.get_store().find_element_user(PROPERTY_QNAME[25], i));
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
    public int sizeOfObjectArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[25]);
        }
    }

    @Override
    public void setObjectArray(CTObject[] objectArray) {
        this.check_orphaned();
        this.arraySetterHelper(objectArray, PROPERTY_QNAME[25]);
    }

    @Override
    public void setObjectArray(int i, CTObject object) {
        this.generatedSetterHelperImpl(object, PROPERTY_QNAME[25], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTObject insertNewObject(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTObject target = null;
            target = (CTObject)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[25], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTObject addNewObject() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTObject target = null;
            target = (CTObject)((Object)this.get_store().add_element_user(PROPERTY_QNAME[25]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeObject(int i) {
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
    public List<CTPicture> getPictList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTPicture>(this::getPictArray, this::setPictArray, this::insertNewPict, this::removePict, this::sizeOfPictArray);
        }
    }

    @Override
    public CTPicture[] getPictArray() {
        return (CTPicture[])this.getXmlObjectArray(PROPERTY_QNAME[26], new CTPicture[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPicture getPictArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPicture target = null;
            target = (CTPicture)((Object)this.get_store().find_element_user(PROPERTY_QNAME[26], i));
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
    public int sizeOfPictArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[26]);
        }
    }

    @Override
    public void setPictArray(CTPicture[] pictArray) {
        this.check_orphaned();
        this.arraySetterHelper(pictArray, PROPERTY_QNAME[26]);
    }

    @Override
    public void setPictArray(int i, CTPicture pict) {
        this.generatedSetterHelperImpl(pict, PROPERTY_QNAME[26], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPicture insertNewPict(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPicture target = null;
            target = (CTPicture)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[26], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPicture addNewPict() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPicture target = null;
            target = (CTPicture)((Object)this.get_store().add_element_user(PROPERTY_QNAME[26]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removePict(int i) {
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
    public List<CTFldChar> getFldCharList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTFldChar>(this::getFldCharArray, this::setFldCharArray, this::insertNewFldChar, this::removeFldChar, this::sizeOfFldCharArray);
        }
    }

    @Override
    public CTFldChar[] getFldCharArray() {
        return (CTFldChar[])this.getXmlObjectArray(PROPERTY_QNAME[27], new CTFldChar[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFldChar getFldCharArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFldChar target = null;
            target = (CTFldChar)((Object)this.get_store().find_element_user(PROPERTY_QNAME[27], i));
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
    public int sizeOfFldCharArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[27]);
        }
    }

    @Override
    public void setFldCharArray(CTFldChar[] fldCharArray) {
        this.check_orphaned();
        this.arraySetterHelper(fldCharArray, PROPERTY_QNAME[27]);
    }

    @Override
    public void setFldCharArray(int i, CTFldChar fldChar) {
        this.generatedSetterHelperImpl(fldChar, PROPERTY_QNAME[27], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFldChar insertNewFldChar(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFldChar target = null;
            target = (CTFldChar)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[27], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFldChar addNewFldChar() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFldChar target = null;
            target = (CTFldChar)((Object)this.get_store().add_element_user(PROPERTY_QNAME[27]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeFldChar(int i) {
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
    public List<CTRuby> getRubyList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTRuby>(this::getRubyArray, this::setRubyArray, this::insertNewRuby, this::removeRuby, this::sizeOfRubyArray);
        }
    }

    @Override
    public CTRuby[] getRubyArray() {
        return (CTRuby[])this.getXmlObjectArray(PROPERTY_QNAME[28], new CTRuby[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRuby getRubyArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRuby target = null;
            target = (CTRuby)((Object)this.get_store().find_element_user(PROPERTY_QNAME[28], i));
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
    public int sizeOfRubyArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[28]);
        }
    }

    @Override
    public void setRubyArray(CTRuby[] rubyArray) {
        this.check_orphaned();
        this.arraySetterHelper(rubyArray, PROPERTY_QNAME[28]);
    }

    @Override
    public void setRubyArray(int i, CTRuby ruby) {
        this.generatedSetterHelperImpl(ruby, PROPERTY_QNAME[28], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRuby insertNewRuby(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRuby target = null;
            target = (CTRuby)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[28], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRuby addNewRuby() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRuby target = null;
            target = (CTRuby)((Object)this.get_store().add_element_user(PROPERTY_QNAME[28]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeRuby(int i) {
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
    public List<CTFtnEdnRef> getFootnoteReferenceList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTFtnEdnRef>(this::getFootnoteReferenceArray, this::setFootnoteReferenceArray, this::insertNewFootnoteReference, this::removeFootnoteReference, this::sizeOfFootnoteReferenceArray);
        }
    }

    @Override
    public CTFtnEdnRef[] getFootnoteReferenceArray() {
        return (CTFtnEdnRef[])this.getXmlObjectArray(PROPERTY_QNAME[29], new CTFtnEdnRef[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFtnEdnRef getFootnoteReferenceArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFtnEdnRef target = null;
            target = (CTFtnEdnRef)((Object)this.get_store().find_element_user(PROPERTY_QNAME[29], i));
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
    public int sizeOfFootnoteReferenceArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[29]);
        }
    }

    @Override
    public void setFootnoteReferenceArray(CTFtnEdnRef[] footnoteReferenceArray) {
        this.check_orphaned();
        this.arraySetterHelper(footnoteReferenceArray, PROPERTY_QNAME[29]);
    }

    @Override
    public void setFootnoteReferenceArray(int i, CTFtnEdnRef footnoteReference) {
        this.generatedSetterHelperImpl(footnoteReference, PROPERTY_QNAME[29], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFtnEdnRef insertNewFootnoteReference(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFtnEdnRef target = null;
            target = (CTFtnEdnRef)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[29], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFtnEdnRef addNewFootnoteReference() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFtnEdnRef target = null;
            target = (CTFtnEdnRef)((Object)this.get_store().add_element_user(PROPERTY_QNAME[29]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeFootnoteReference(int i) {
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
    public List<CTFtnEdnRef> getEndnoteReferenceList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTFtnEdnRef>(this::getEndnoteReferenceArray, this::setEndnoteReferenceArray, this::insertNewEndnoteReference, this::removeEndnoteReference, this::sizeOfEndnoteReferenceArray);
        }
    }

    @Override
    public CTFtnEdnRef[] getEndnoteReferenceArray() {
        return (CTFtnEdnRef[])this.getXmlObjectArray(PROPERTY_QNAME[30], new CTFtnEdnRef[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFtnEdnRef getEndnoteReferenceArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFtnEdnRef target = null;
            target = (CTFtnEdnRef)((Object)this.get_store().find_element_user(PROPERTY_QNAME[30], i));
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
    public int sizeOfEndnoteReferenceArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[30]);
        }
    }

    @Override
    public void setEndnoteReferenceArray(CTFtnEdnRef[] endnoteReferenceArray) {
        this.check_orphaned();
        this.arraySetterHelper(endnoteReferenceArray, PROPERTY_QNAME[30]);
    }

    @Override
    public void setEndnoteReferenceArray(int i, CTFtnEdnRef endnoteReference) {
        this.generatedSetterHelperImpl(endnoteReference, PROPERTY_QNAME[30], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFtnEdnRef insertNewEndnoteReference(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFtnEdnRef target = null;
            target = (CTFtnEdnRef)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[30], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFtnEdnRef addNewEndnoteReference() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFtnEdnRef target = null;
            target = (CTFtnEdnRef)((Object)this.get_store().add_element_user(PROPERTY_QNAME[30]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeEndnoteReference(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[30], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTMarkup> getCommentReferenceList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTMarkup>(this::getCommentReferenceArray, this::setCommentReferenceArray, this::insertNewCommentReference, this::removeCommentReference, this::sizeOfCommentReferenceArray);
        }
    }

    @Override
    public CTMarkup[] getCommentReferenceArray() {
        return (CTMarkup[])this.getXmlObjectArray(PROPERTY_QNAME[31], new CTMarkup[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMarkup getCommentReferenceArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMarkup target = null;
            target = (CTMarkup)((Object)this.get_store().find_element_user(PROPERTY_QNAME[31], i));
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
    public int sizeOfCommentReferenceArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[31]);
        }
    }

    @Override
    public void setCommentReferenceArray(CTMarkup[] commentReferenceArray) {
        this.check_orphaned();
        this.arraySetterHelper(commentReferenceArray, PROPERTY_QNAME[31]);
    }

    @Override
    public void setCommentReferenceArray(int i, CTMarkup commentReference) {
        this.generatedSetterHelperImpl(commentReference, PROPERTY_QNAME[31], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMarkup insertNewCommentReference(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMarkup target = null;
            target = (CTMarkup)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[31], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMarkup addNewCommentReference() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMarkup target = null;
            target = (CTMarkup)((Object)this.get_store().add_element_user(PROPERTY_QNAME[31]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCommentReference(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[31], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTDrawing> getDrawingList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTDrawing>(this::getDrawingArray, this::setDrawingArray, this::insertNewDrawing, this::removeDrawing, this::sizeOfDrawingArray);
        }
    }

    @Override
    public CTDrawing[] getDrawingArray() {
        return (CTDrawing[])this.getXmlObjectArray(PROPERTY_QNAME[32], new CTDrawing[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDrawing getDrawingArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDrawing target = null;
            target = (CTDrawing)((Object)this.get_store().find_element_user(PROPERTY_QNAME[32], i));
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
    public int sizeOfDrawingArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[32]);
        }
    }

    @Override
    public void setDrawingArray(CTDrawing[] drawingArray) {
        this.check_orphaned();
        this.arraySetterHelper(drawingArray, PROPERTY_QNAME[32]);
    }

    @Override
    public void setDrawingArray(int i, CTDrawing drawing) {
        this.generatedSetterHelperImpl(drawing, PROPERTY_QNAME[32], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDrawing insertNewDrawing(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDrawing target = null;
            target = (CTDrawing)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[32], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDrawing addNewDrawing() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDrawing target = null;
            target = (CTDrawing)((Object)this.get_store().add_element_user(PROPERTY_QNAME[32]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeDrawing(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[32], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTPTab> getPtabList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTPTab>(this::getPtabArray, this::setPtabArray, this::insertNewPtab, this::removePtab, this::sizeOfPtabArray);
        }
    }

    @Override
    public CTPTab[] getPtabArray() {
        return (CTPTab[])this.getXmlObjectArray(PROPERTY_QNAME[33], new CTPTab[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPTab getPtabArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPTab target = null;
            target = (CTPTab)((Object)this.get_store().find_element_user(PROPERTY_QNAME[33], i));
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
    public int sizeOfPtabArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[33]);
        }
    }

    @Override
    public void setPtabArray(CTPTab[] ptabArray) {
        this.check_orphaned();
        this.arraySetterHelper(ptabArray, PROPERTY_QNAME[33]);
    }

    @Override
    public void setPtabArray(int i, CTPTab ptab) {
        this.generatedSetterHelperImpl(ptab, PROPERTY_QNAME[33], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPTab insertNewPtab(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPTab target = null;
            target = (CTPTab)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[33], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPTab addNewPtab() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPTab target = null;
            target = (CTPTab)((Object)this.get_store().add_element_user(PROPERTY_QNAME[33]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removePtab(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[33], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTEmpty> getLastRenderedPageBreakList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTEmpty>(this::getLastRenderedPageBreakArray, this::setLastRenderedPageBreakArray, this::insertNewLastRenderedPageBreak, this::removeLastRenderedPageBreak, this::sizeOfLastRenderedPageBreakArray);
        }
    }

    @Override
    public CTEmpty[] getLastRenderedPageBreakArray() {
        return (CTEmpty[])this.getXmlObjectArray(PROPERTY_QNAME[34], new CTEmpty[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty getLastRenderedPageBreakArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().find_element_user(PROPERTY_QNAME[34], i));
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
    public int sizeOfLastRenderedPageBreakArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[34]);
        }
    }

    @Override
    public void setLastRenderedPageBreakArray(CTEmpty[] lastRenderedPageBreakArray) {
        this.check_orphaned();
        this.arraySetterHelper(lastRenderedPageBreakArray, PROPERTY_QNAME[34]);
    }

    @Override
    public void setLastRenderedPageBreakArray(int i, CTEmpty lastRenderedPageBreak) {
        this.generatedSetterHelperImpl(lastRenderedPageBreak, PROPERTY_QNAME[34], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty insertNewLastRenderedPageBreak(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[34], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty addNewLastRenderedPageBreak() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().add_element_user(PROPERTY_QNAME[34]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeLastRenderedPageBreak(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[34], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTText> getT2List() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTText>(this::getT2Array, this::setT2Array, this::insertNewT2, this::removeT2, this::sizeOfT2Array);
        }
    }

    @Override
    public CTText[] getT2Array() {
        return (CTText[])this.getXmlObjectArray(PROPERTY_QNAME[35], new CTText[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTText getT2Array(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTText target = null;
            target = (CTText)((Object)this.get_store().find_element_user(PROPERTY_QNAME[35], i));
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
    public int sizeOfT2Array() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[35]);
        }
    }

    @Override
    public void setT2Array(CTText[] t2Array) {
        this.check_orphaned();
        this.arraySetterHelper(t2Array, PROPERTY_QNAME[35]);
    }

    @Override
    public void setT2Array(int i, CTText t2) {
        this.generatedSetterHelperImpl(t2, PROPERTY_QNAME[35], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTText insertNewT2(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTText target = null;
            target = (CTText)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[35], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTText addNewT2() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTText target = null;
            target = (CTText)((Object)this.get_store().add_element_user(PROPERTY_QNAME[35]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeT2(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[35], i);
        }
    }
}

