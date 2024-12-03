/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLineNumber
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPrChange
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTColumns;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocGrid;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTEdnProps;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFtnProps;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHdrFtrRef;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLineNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPaperSource;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRel;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPrChange;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTextDirection;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVerticalJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLongHexNumber;

public class CTSectPrImpl
extends XmlComplexContentImpl
implements CTSectPr {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "headerReference"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "footerReference"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "footnotePr"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "endnotePr"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "type"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "pgSz"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "pgMar"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "paperSrc"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "pgBorders"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "lnNumType"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "pgNumType"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "cols"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "formProt"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "vAlign"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "noEndnote"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "titlePg"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "textDirection"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bidi"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rtlGutter"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "docGrid"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "printerSettings"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "sectPrChange"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rsidRPr"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rsidDel"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rsidR"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rsidSect")};

    public CTSectPrImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTHdrFtrRef> getHeaderReferenceList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTHdrFtrRef>(this::getHeaderReferenceArray, this::setHeaderReferenceArray, this::insertNewHeaderReference, this::removeHeaderReference, this::sizeOfHeaderReferenceArray);
        }
    }

    @Override
    public CTHdrFtrRef[] getHeaderReferenceArray() {
        return (CTHdrFtrRef[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTHdrFtrRef[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHdrFtrRef getHeaderReferenceArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHdrFtrRef target = null;
            target = (CTHdrFtrRef)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfHeaderReferenceArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setHeaderReferenceArray(CTHdrFtrRef[] headerReferenceArray) {
        this.check_orphaned();
        this.arraySetterHelper(headerReferenceArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setHeaderReferenceArray(int i, CTHdrFtrRef headerReference) {
        this.generatedSetterHelperImpl(headerReference, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHdrFtrRef insertNewHeaderReference(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHdrFtrRef target = null;
            target = (CTHdrFtrRef)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHdrFtrRef addNewHeaderReference() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHdrFtrRef target = null;
            target = (CTHdrFtrRef)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeHeaderReference(int i) {
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
    public List<CTHdrFtrRef> getFooterReferenceList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTHdrFtrRef>(this::getFooterReferenceArray, this::setFooterReferenceArray, this::insertNewFooterReference, this::removeFooterReference, this::sizeOfFooterReferenceArray);
        }
    }

    @Override
    public CTHdrFtrRef[] getFooterReferenceArray() {
        return (CTHdrFtrRef[])this.getXmlObjectArray(PROPERTY_QNAME[1], new CTHdrFtrRef[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHdrFtrRef getFooterReferenceArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHdrFtrRef target = null;
            target = (CTHdrFtrRef)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], i));
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
    public int sizeOfFooterReferenceArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]);
        }
    }

    @Override
    public void setFooterReferenceArray(CTHdrFtrRef[] footerReferenceArray) {
        this.check_orphaned();
        this.arraySetterHelper(footerReferenceArray, PROPERTY_QNAME[1]);
    }

    @Override
    public void setFooterReferenceArray(int i, CTHdrFtrRef footerReference) {
        this.generatedSetterHelperImpl(footerReference, PROPERTY_QNAME[1], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHdrFtrRef insertNewFooterReference(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHdrFtrRef target = null;
            target = (CTHdrFtrRef)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[1], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHdrFtrRef addNewFooterReference() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHdrFtrRef target = null;
            target = (CTHdrFtrRef)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeFooterReference(int i) {
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
    public CTFtnProps getFootnotePr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFtnProps target = null;
            target = (CTFtnProps)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetFootnotePr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]) != 0;
        }
    }

    @Override
    public void setFootnotePr(CTFtnProps footnotePr) {
        this.generatedSetterHelperImpl(footnotePr, PROPERTY_QNAME[2], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFtnProps addNewFootnotePr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFtnProps target = null;
            target = (CTFtnProps)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetFootnotePr() {
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
    public CTEdnProps getEndnotePr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEdnProps target = null;
            target = (CTEdnProps)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetEndnotePr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]) != 0;
        }
    }

    @Override
    public void setEndnotePr(CTEdnProps endnotePr) {
        this.generatedSetterHelperImpl(endnotePr, PROPERTY_QNAME[3], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEdnProps addNewEndnotePr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEdnProps target = null;
            target = (CTEdnProps)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetEndnotePr() {
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
    public CTSectType getType() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSectType target = null;
            target = (CTSectType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], 0));
            return target == null ? null : target;
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
            return this.get_store().count_elements(PROPERTY_QNAME[4]) != 0;
        }
    }

    @Override
    public void setType(CTSectType type) {
        this.generatedSetterHelperImpl(type, PROPERTY_QNAME[4], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSectType addNewType() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSectType target = null;
            target = (CTSectType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
            return target;
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
            this.get_store().remove_element(PROPERTY_QNAME[4], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPageSz getPgSz() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPageSz target = null;
            target = (CTPageSz)((Object)this.get_store().find_element_user(PROPERTY_QNAME[5], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetPgSz() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[5]) != 0;
        }
    }

    @Override
    public void setPgSz(CTPageSz pgSz) {
        this.generatedSetterHelperImpl(pgSz, PROPERTY_QNAME[5], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPageSz addNewPgSz() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPageSz target = null;
            target = (CTPageSz)((Object)this.get_store().add_element_user(PROPERTY_QNAME[5]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetPgSz() {
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
    public CTPageMar getPgMar() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPageMar target = null;
            target = (CTPageMar)((Object)this.get_store().find_element_user(PROPERTY_QNAME[6], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetPgMar() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[6]) != 0;
        }
    }

    @Override
    public void setPgMar(CTPageMar pgMar) {
        this.generatedSetterHelperImpl(pgMar, PROPERTY_QNAME[6], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPageMar addNewPgMar() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPageMar target = null;
            target = (CTPageMar)((Object)this.get_store().add_element_user(PROPERTY_QNAME[6]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetPgMar() {
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
    public CTPaperSource getPaperSrc() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPaperSource target = null;
            target = (CTPaperSource)((Object)this.get_store().find_element_user(PROPERTY_QNAME[7], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetPaperSrc() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[7]) != 0;
        }
    }

    @Override
    public void setPaperSrc(CTPaperSource paperSrc) {
        this.generatedSetterHelperImpl(paperSrc, PROPERTY_QNAME[7], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPaperSource addNewPaperSrc() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPaperSource target = null;
            target = (CTPaperSource)((Object)this.get_store().add_element_user(PROPERTY_QNAME[7]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetPaperSrc() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[7], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPageBorders getPgBorders() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPageBorders target = null;
            target = (CTPageBorders)((Object)this.get_store().find_element_user(PROPERTY_QNAME[8], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetPgBorders() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[8]) != 0;
        }
    }

    @Override
    public void setPgBorders(CTPageBorders pgBorders) {
        this.generatedSetterHelperImpl(pgBorders, PROPERTY_QNAME[8], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPageBorders addNewPgBorders() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPageBorders target = null;
            target = (CTPageBorders)((Object)this.get_store().add_element_user(PROPERTY_QNAME[8]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetPgBorders() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[8], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLineNumber getLnNumType() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLineNumber target = null;
            target = (CTLineNumber)this.get_store().find_element_user(PROPERTY_QNAME[9], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetLnNumType() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[9]) != 0;
        }
    }

    @Override
    public void setLnNumType(CTLineNumber lnNumType) {
        this.generatedSetterHelperImpl((XmlObject)lnNumType, PROPERTY_QNAME[9], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLineNumber addNewLnNumType() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLineNumber target = null;
            target = (CTLineNumber)this.get_store().add_element_user(PROPERTY_QNAME[9]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetLnNumType() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[9], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPageNumber getPgNumType() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPageNumber target = null;
            target = (CTPageNumber)((Object)this.get_store().find_element_user(PROPERTY_QNAME[10], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetPgNumType() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[10]) != 0;
        }
    }

    @Override
    public void setPgNumType(CTPageNumber pgNumType) {
        this.generatedSetterHelperImpl(pgNumType, PROPERTY_QNAME[10], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPageNumber addNewPgNumType() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPageNumber target = null;
            target = (CTPageNumber)((Object)this.get_store().add_element_user(PROPERTY_QNAME[10]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetPgNumType() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[10], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColumns getCols() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColumns target = null;
            target = (CTColumns)((Object)this.get_store().find_element_user(PROPERTY_QNAME[11], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetCols() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[11]) != 0;
        }
    }

    @Override
    public void setCols(CTColumns cols) {
        this.generatedSetterHelperImpl(cols, PROPERTY_QNAME[11], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColumns addNewCols() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColumns target = null;
            target = (CTColumns)((Object)this.get_store().add_element_user(PROPERTY_QNAME[11]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetCols() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[11], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getFormProt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[12], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetFormProt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[12]) != 0;
        }
    }

    @Override
    public void setFormProt(CTOnOff formProt) {
        this.generatedSetterHelperImpl(formProt, PROPERTY_QNAME[12], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewFormProt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[12]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetFormProt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[12], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTVerticalJc getVAlign() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTVerticalJc target = null;
            target = (CTVerticalJc)((Object)this.get_store().find_element_user(PROPERTY_QNAME[13], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetVAlign() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[13]) != 0;
        }
    }

    @Override
    public void setVAlign(CTVerticalJc vAlign) {
        this.generatedSetterHelperImpl(vAlign, PROPERTY_QNAME[13], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTVerticalJc addNewVAlign() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTVerticalJc target = null;
            target = (CTVerticalJc)((Object)this.get_store().add_element_user(PROPERTY_QNAME[13]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetVAlign() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[13], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getNoEndnote() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[14], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetNoEndnote() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[14]) != 0;
        }
    }

    @Override
    public void setNoEndnote(CTOnOff noEndnote) {
        this.generatedSetterHelperImpl(noEndnote, PROPERTY_QNAME[14], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewNoEndnote() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[14]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetNoEndnote() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[14], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getTitlePg() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[15], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetTitlePg() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[15]) != 0;
        }
    }

    @Override
    public void setTitlePg(CTOnOff titlePg) {
        this.generatedSetterHelperImpl(titlePg, PROPERTY_QNAME[15], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewTitlePg() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[15]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetTitlePg() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[15], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextDirection getTextDirection() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextDirection target = null;
            target = (CTTextDirection)((Object)this.get_store().find_element_user(PROPERTY_QNAME[16], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetTextDirection() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[16]) != 0;
        }
    }

    @Override
    public void setTextDirection(CTTextDirection textDirection) {
        this.generatedSetterHelperImpl(textDirection, PROPERTY_QNAME[16], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextDirection addNewTextDirection() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextDirection target = null;
            target = (CTTextDirection)((Object)this.get_store().add_element_user(PROPERTY_QNAME[16]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetTextDirection() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[16], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getBidi() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[17], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetBidi() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[17]) != 0;
        }
    }

    @Override
    public void setBidi(CTOnOff bidi) {
        this.generatedSetterHelperImpl(bidi, PROPERTY_QNAME[17], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewBidi() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[17]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetBidi() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[17], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getRtlGutter() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[18], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetRtlGutter() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[18]) != 0;
        }
    }

    @Override
    public void setRtlGutter(CTOnOff rtlGutter) {
        this.generatedSetterHelperImpl(rtlGutter, PROPERTY_QNAME[18], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewRtlGutter() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[18]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetRtlGutter() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[18], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDocGrid getDocGrid() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDocGrid target = null;
            target = (CTDocGrid)((Object)this.get_store().find_element_user(PROPERTY_QNAME[19], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDocGrid() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[19]) != 0;
        }
    }

    @Override
    public void setDocGrid(CTDocGrid docGrid) {
        this.generatedSetterHelperImpl(docGrid, PROPERTY_QNAME[19], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDocGrid addNewDocGrid() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDocGrid target = null;
            target = (CTDocGrid)((Object)this.get_store().add_element_user(PROPERTY_QNAME[19]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDocGrid() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[19], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRel getPrinterSettings() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRel target = null;
            target = (CTRel)((Object)this.get_store().find_element_user(PROPERTY_QNAME[20], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetPrinterSettings() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[20]) != 0;
        }
    }

    @Override
    public void setPrinterSettings(CTRel printerSettings) {
        this.generatedSetterHelperImpl(printerSettings, PROPERTY_QNAME[20], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRel addNewPrinterSettings() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRel target = null;
            target = (CTRel)((Object)this.get_store().add_element_user(PROPERTY_QNAME[20]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetPrinterSettings() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[20], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSectPrChange getSectPrChange() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSectPrChange target = null;
            target = (CTSectPrChange)this.get_store().find_element_user(PROPERTY_QNAME[21], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSectPrChange() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[21]) != 0;
        }
    }

    @Override
    public void setSectPrChange(CTSectPrChange sectPrChange) {
        this.generatedSetterHelperImpl((XmlObject)sectPrChange, PROPERTY_QNAME[21], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSectPrChange addNewSectPrChange() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSectPrChange target = null;
            target = (CTSectPrChange)this.get_store().add_element_user(PROPERTY_QNAME[21]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSectPrChange() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[21], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public byte[] getRsidRPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[22]));
            return target == null ? null : target.getByteArrayValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STLongHexNumber xgetRsidRPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STLongHexNumber target = null;
            target = (STLongHexNumber)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[22]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetRsidRPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[22]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setRsidRPr(byte[] rsidRPr) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[22]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[22]));
            }
            target.setByteArrayValue(rsidRPr);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetRsidRPr(STLongHexNumber rsidRPr) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STLongHexNumber target = null;
            target = (STLongHexNumber)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[22]));
            if (target == null) {
                target = (STLongHexNumber)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[22]));
            }
            target.set(rsidRPr);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetRsidRPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[22]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public byte[] getRsidDel() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[23]));
            return target == null ? null : target.getByteArrayValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STLongHexNumber xgetRsidDel() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STLongHexNumber target = null;
            target = (STLongHexNumber)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[23]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetRsidDel() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[23]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setRsidDel(byte[] rsidDel) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[23]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[23]));
            }
            target.setByteArrayValue(rsidDel);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetRsidDel(STLongHexNumber rsidDel) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STLongHexNumber target = null;
            target = (STLongHexNumber)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[23]));
            if (target == null) {
                target = (STLongHexNumber)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[23]));
            }
            target.set(rsidDel);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetRsidDel() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[23]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public byte[] getRsidR() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[24]));
            return target == null ? null : target.getByteArrayValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STLongHexNumber xgetRsidR() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STLongHexNumber target = null;
            target = (STLongHexNumber)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[24]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetRsidR() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[24]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setRsidR(byte[] rsidR) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[24]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[24]));
            }
            target.setByteArrayValue(rsidR);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetRsidR(STLongHexNumber rsidR) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STLongHexNumber target = null;
            target = (STLongHexNumber)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[24]));
            if (target == null) {
                target = (STLongHexNumber)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[24]));
            }
            target.set(rsidR);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetRsidR() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[24]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public byte[] getRsidSect() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[25]));
            return target == null ? null : target.getByteArrayValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STLongHexNumber xgetRsidSect() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STLongHexNumber target = null;
            target = (STLongHexNumber)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[25]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetRsidSect() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[25]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setRsidSect(byte[] rsidSect) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[25]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[25]));
            }
            target.setByteArrayValue(rsidSect);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetRsidSect(STLongHexNumber rsidSect) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STLongHexNumber target = null;
            target = (STLongHexNumber)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[25]));
            if (target == null) {
                target = (STLongHexNumber)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[25]));
            }
            target.set(rsidSect);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetRsidSect() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[25]);
        }
    }
}

