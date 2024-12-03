/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTCustomXmlBlock
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMath;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMathPara;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAltChunk;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTComment;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTCustomXmlBlock;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMarkup;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMarkupRange;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMoveBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPerm;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPermStart;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTProofErr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRunTrackChange;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtBlock;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrackChange;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.impl.CTTrackChangeImpl;

public class CTCommentImpl
extends CTTrackChangeImpl
implements CTComment {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXml"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "sdt"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "p"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tbl"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "proofErr"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "permStart"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "permEnd"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bookmarkStart"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bookmarkEnd"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveFromRangeStart"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveFromRangeEnd"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveToRangeStart"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveToRangeEnd"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "commentRangeStart"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "commentRangeEnd"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlInsRangeStart"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlInsRangeEnd"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlDelRangeStart"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlDelRangeEnd"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlMoveFromRangeStart"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlMoveFromRangeEnd"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlMoveToRangeStart"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlMoveToRangeEnd"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "ins"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "del"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveFrom"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveTo"), new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "oMathPara"), new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "oMath"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "altChunk"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "initials")};

    public CTCommentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTCustomXmlBlock> getCustomXmlList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTCustomXmlBlock>(this::getCustomXmlArray, this::setCustomXmlArray, this::insertNewCustomXml, this::removeCustomXml, this::sizeOfCustomXmlArray);
        }
    }

    @Override
    public CTCustomXmlBlock[] getCustomXmlArray() {
        return (CTCustomXmlBlock[])this.getXmlObjectArray(PROPERTY_QNAME[0], (XmlObject[])new CTCustomXmlBlock[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCustomXmlBlock getCustomXmlArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCustomXmlBlock target = null;
            target = (CTCustomXmlBlock)this.get_store().find_element_user(PROPERTY_QNAME[0], i);
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
    public int sizeOfCustomXmlArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setCustomXmlArray(CTCustomXmlBlock[] customXmlArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])customXmlArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setCustomXmlArray(int i, CTCustomXmlBlock customXml) {
        this.generatedSetterHelperImpl((XmlObject)customXml, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCustomXmlBlock insertNewCustomXml(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCustomXmlBlock target = null;
            target = (CTCustomXmlBlock)this.get_store().insert_element_user(PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCustomXmlBlock addNewCustomXml() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCustomXmlBlock target = null;
            target = (CTCustomXmlBlock)this.get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCustomXml(int i) {
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
    public List<CTSdtBlock> getSdtList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTSdtBlock>(this::getSdtArray, this::setSdtArray, this::insertNewSdt, this::removeSdt, this::sizeOfSdtArray);
        }
    }

    @Override
    public CTSdtBlock[] getSdtArray() {
        return (CTSdtBlock[])this.getXmlObjectArray(PROPERTY_QNAME[1], new CTSdtBlock[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSdtBlock getSdtArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSdtBlock target = null;
            target = (CTSdtBlock)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], i));
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
    public int sizeOfSdtArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]);
        }
    }

    @Override
    public void setSdtArray(CTSdtBlock[] sdtArray) {
        this.check_orphaned();
        this.arraySetterHelper(sdtArray, PROPERTY_QNAME[1]);
    }

    @Override
    public void setSdtArray(int i, CTSdtBlock sdt) {
        this.generatedSetterHelperImpl(sdt, PROPERTY_QNAME[1], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSdtBlock insertNewSdt(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSdtBlock target = null;
            target = (CTSdtBlock)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[1], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSdtBlock addNewSdt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSdtBlock target = null;
            target = (CTSdtBlock)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSdt(int i) {
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
    public List<CTP> getPList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTP>(this::getPArray, this::setPArray, this::insertNewP, this::removeP, this::sizeOfPArray);
        }
    }

    @Override
    public CTP[] getPArray() {
        return (CTP[])this.getXmlObjectArray(PROPERTY_QNAME[2], new CTP[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTP getPArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTP target = null;
            target = (CTP)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], i));
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
    public int sizeOfPArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]);
        }
    }

    @Override
    public void setPArray(CTP[] pArray) {
        this.check_orphaned();
        this.arraySetterHelper(pArray, PROPERTY_QNAME[2]);
    }

    @Override
    public void setPArray(int i, CTP p) {
        this.generatedSetterHelperImpl(p, PROPERTY_QNAME[2], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTP insertNewP(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTP target = null;
            target = (CTP)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[2], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTP addNewP() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTP target = null;
            target = (CTP)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeP(int i) {
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
    public List<CTTbl> getTblList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTbl>(this::getTblArray, this::setTblArray, this::insertNewTbl, this::removeTbl, this::sizeOfTblArray);
        }
    }

    @Override
    public CTTbl[] getTblArray() {
        return (CTTbl[])this.getXmlObjectArray(PROPERTY_QNAME[3], new CTTbl[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTbl getTblArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTbl target = null;
            target = (CTTbl)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], i));
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
    public int sizeOfTblArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]);
        }
    }

    @Override
    public void setTblArray(CTTbl[] tblArray) {
        this.check_orphaned();
        this.arraySetterHelper(tblArray, PROPERTY_QNAME[3]);
    }

    @Override
    public void setTblArray(int i, CTTbl tbl) {
        this.generatedSetterHelperImpl(tbl, PROPERTY_QNAME[3], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTbl insertNewTbl(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTbl target = null;
            target = (CTTbl)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[3], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTbl addNewTbl() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTbl target = null;
            target = (CTTbl)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeTbl(int i) {
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
    public List<CTProofErr> getProofErrList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTProofErr>(this::getProofErrArray, this::setProofErrArray, this::insertNewProofErr, this::removeProofErr, this::sizeOfProofErrArray);
        }
    }

    @Override
    public CTProofErr[] getProofErrArray() {
        return (CTProofErr[])this.getXmlObjectArray(PROPERTY_QNAME[4], new CTProofErr[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTProofErr getProofErrArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTProofErr target = null;
            target = (CTProofErr)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], i));
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
    public int sizeOfProofErrArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[4]);
        }
    }

    @Override
    public void setProofErrArray(CTProofErr[] proofErrArray) {
        this.check_orphaned();
        this.arraySetterHelper(proofErrArray, PROPERTY_QNAME[4]);
    }

    @Override
    public void setProofErrArray(int i, CTProofErr proofErr) {
        this.generatedSetterHelperImpl(proofErr, PROPERTY_QNAME[4], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTProofErr insertNewProofErr(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTProofErr target = null;
            target = (CTProofErr)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[4], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTProofErr addNewProofErr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTProofErr target = null;
            target = (CTProofErr)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeProofErr(int i) {
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
    public List<CTPermStart> getPermStartList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTPermStart>(this::getPermStartArray, this::setPermStartArray, this::insertNewPermStart, this::removePermStart, this::sizeOfPermStartArray);
        }
    }

    @Override
    public CTPermStart[] getPermStartArray() {
        return (CTPermStart[])this.getXmlObjectArray(PROPERTY_QNAME[5], new CTPermStart[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPermStart getPermStartArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPermStart target = null;
            target = (CTPermStart)((Object)this.get_store().find_element_user(PROPERTY_QNAME[5], i));
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
    public int sizeOfPermStartArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[5]);
        }
    }

    @Override
    public void setPermStartArray(CTPermStart[] permStartArray) {
        this.check_orphaned();
        this.arraySetterHelper(permStartArray, PROPERTY_QNAME[5]);
    }

    @Override
    public void setPermStartArray(int i, CTPermStart permStart) {
        this.generatedSetterHelperImpl(permStart, PROPERTY_QNAME[5], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPermStart insertNewPermStart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPermStart target = null;
            target = (CTPermStart)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[5], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPermStart addNewPermStart() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPermStart target = null;
            target = (CTPermStart)((Object)this.get_store().add_element_user(PROPERTY_QNAME[5]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removePermStart(int i) {
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
    public List<CTPerm> getPermEndList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTPerm>(this::getPermEndArray, this::setPermEndArray, this::insertNewPermEnd, this::removePermEnd, this::sizeOfPermEndArray);
        }
    }

    @Override
    public CTPerm[] getPermEndArray() {
        return (CTPerm[])this.getXmlObjectArray(PROPERTY_QNAME[6], new CTPerm[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPerm getPermEndArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPerm target = null;
            target = (CTPerm)((Object)this.get_store().find_element_user(PROPERTY_QNAME[6], i));
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
    public int sizeOfPermEndArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[6]);
        }
    }

    @Override
    public void setPermEndArray(CTPerm[] permEndArray) {
        this.check_orphaned();
        this.arraySetterHelper(permEndArray, PROPERTY_QNAME[6]);
    }

    @Override
    public void setPermEndArray(int i, CTPerm permEnd) {
        this.generatedSetterHelperImpl(permEnd, PROPERTY_QNAME[6], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPerm insertNewPermEnd(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPerm target = null;
            target = (CTPerm)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[6], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPerm addNewPermEnd() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPerm target = null;
            target = (CTPerm)((Object)this.get_store().add_element_user(PROPERTY_QNAME[6]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removePermEnd(int i) {
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
    public List<CTBookmark> getBookmarkStartList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTBookmark>(this::getBookmarkStartArray, this::setBookmarkStartArray, this::insertNewBookmarkStart, this::removeBookmarkStart, this::sizeOfBookmarkStartArray);
        }
    }

    @Override
    public CTBookmark[] getBookmarkStartArray() {
        return (CTBookmark[])this.getXmlObjectArray(PROPERTY_QNAME[7], new CTBookmark[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBookmark getBookmarkStartArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBookmark target = null;
            target = (CTBookmark)((Object)this.get_store().find_element_user(PROPERTY_QNAME[7], i));
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
    public int sizeOfBookmarkStartArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[7]);
        }
    }

    @Override
    public void setBookmarkStartArray(CTBookmark[] bookmarkStartArray) {
        this.check_orphaned();
        this.arraySetterHelper(bookmarkStartArray, PROPERTY_QNAME[7]);
    }

    @Override
    public void setBookmarkStartArray(int i, CTBookmark bookmarkStart) {
        this.generatedSetterHelperImpl(bookmarkStart, PROPERTY_QNAME[7], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBookmark insertNewBookmarkStart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBookmark target = null;
            target = (CTBookmark)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[7], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBookmark addNewBookmarkStart() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBookmark target = null;
            target = (CTBookmark)((Object)this.get_store().add_element_user(PROPERTY_QNAME[7]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeBookmarkStart(int i) {
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
    public List<CTMarkupRange> getBookmarkEndList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTMarkupRange>(this::getBookmarkEndArray, this::setBookmarkEndArray, this::insertNewBookmarkEnd, this::removeBookmarkEnd, this::sizeOfBookmarkEndArray);
        }
    }

    @Override
    public CTMarkupRange[] getBookmarkEndArray() {
        return (CTMarkupRange[])this.getXmlObjectArray(PROPERTY_QNAME[8], new CTMarkupRange[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMarkupRange getBookmarkEndArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMarkupRange target = null;
            target = (CTMarkupRange)((Object)this.get_store().find_element_user(PROPERTY_QNAME[8], i));
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
    public int sizeOfBookmarkEndArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[8]);
        }
    }

    @Override
    public void setBookmarkEndArray(CTMarkupRange[] bookmarkEndArray) {
        this.check_orphaned();
        this.arraySetterHelper(bookmarkEndArray, PROPERTY_QNAME[8]);
    }

    @Override
    public void setBookmarkEndArray(int i, CTMarkupRange bookmarkEnd) {
        this.generatedSetterHelperImpl(bookmarkEnd, PROPERTY_QNAME[8], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMarkupRange insertNewBookmarkEnd(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMarkupRange target = null;
            target = (CTMarkupRange)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[8], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMarkupRange addNewBookmarkEnd() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMarkupRange target = null;
            target = (CTMarkupRange)((Object)this.get_store().add_element_user(PROPERTY_QNAME[8]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeBookmarkEnd(int i) {
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
    public List<CTMoveBookmark> getMoveFromRangeStartList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTMoveBookmark>(this::getMoveFromRangeStartArray, this::setMoveFromRangeStartArray, this::insertNewMoveFromRangeStart, this::removeMoveFromRangeStart, this::sizeOfMoveFromRangeStartArray);
        }
    }

    @Override
    public CTMoveBookmark[] getMoveFromRangeStartArray() {
        return (CTMoveBookmark[])this.getXmlObjectArray(PROPERTY_QNAME[9], new CTMoveBookmark[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMoveBookmark getMoveFromRangeStartArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMoveBookmark target = null;
            target = (CTMoveBookmark)((Object)this.get_store().find_element_user(PROPERTY_QNAME[9], i));
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
    public int sizeOfMoveFromRangeStartArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[9]);
        }
    }

    @Override
    public void setMoveFromRangeStartArray(CTMoveBookmark[] moveFromRangeStartArray) {
        this.check_orphaned();
        this.arraySetterHelper(moveFromRangeStartArray, PROPERTY_QNAME[9]);
    }

    @Override
    public void setMoveFromRangeStartArray(int i, CTMoveBookmark moveFromRangeStart) {
        this.generatedSetterHelperImpl(moveFromRangeStart, PROPERTY_QNAME[9], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMoveBookmark insertNewMoveFromRangeStart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMoveBookmark target = null;
            target = (CTMoveBookmark)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[9], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMoveBookmark addNewMoveFromRangeStart() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMoveBookmark target = null;
            target = (CTMoveBookmark)((Object)this.get_store().add_element_user(PROPERTY_QNAME[9]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeMoveFromRangeStart(int i) {
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
    public List<CTMarkupRange> getMoveFromRangeEndList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTMarkupRange>(this::getMoveFromRangeEndArray, this::setMoveFromRangeEndArray, this::insertNewMoveFromRangeEnd, this::removeMoveFromRangeEnd, this::sizeOfMoveFromRangeEndArray);
        }
    }

    @Override
    public CTMarkupRange[] getMoveFromRangeEndArray() {
        return (CTMarkupRange[])this.getXmlObjectArray(PROPERTY_QNAME[10], new CTMarkupRange[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMarkupRange getMoveFromRangeEndArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMarkupRange target = null;
            target = (CTMarkupRange)((Object)this.get_store().find_element_user(PROPERTY_QNAME[10], i));
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
    public int sizeOfMoveFromRangeEndArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[10]);
        }
    }

    @Override
    public void setMoveFromRangeEndArray(CTMarkupRange[] moveFromRangeEndArray) {
        this.check_orphaned();
        this.arraySetterHelper(moveFromRangeEndArray, PROPERTY_QNAME[10]);
    }

    @Override
    public void setMoveFromRangeEndArray(int i, CTMarkupRange moveFromRangeEnd) {
        this.generatedSetterHelperImpl(moveFromRangeEnd, PROPERTY_QNAME[10], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMarkupRange insertNewMoveFromRangeEnd(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMarkupRange target = null;
            target = (CTMarkupRange)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[10], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMarkupRange addNewMoveFromRangeEnd() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMarkupRange target = null;
            target = (CTMarkupRange)((Object)this.get_store().add_element_user(PROPERTY_QNAME[10]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeMoveFromRangeEnd(int i) {
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
    public List<CTMoveBookmark> getMoveToRangeStartList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTMoveBookmark>(this::getMoveToRangeStartArray, this::setMoveToRangeStartArray, this::insertNewMoveToRangeStart, this::removeMoveToRangeStart, this::sizeOfMoveToRangeStartArray);
        }
    }

    @Override
    public CTMoveBookmark[] getMoveToRangeStartArray() {
        return (CTMoveBookmark[])this.getXmlObjectArray(PROPERTY_QNAME[11], new CTMoveBookmark[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMoveBookmark getMoveToRangeStartArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMoveBookmark target = null;
            target = (CTMoveBookmark)((Object)this.get_store().find_element_user(PROPERTY_QNAME[11], i));
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
    public int sizeOfMoveToRangeStartArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[11]);
        }
    }

    @Override
    public void setMoveToRangeStartArray(CTMoveBookmark[] moveToRangeStartArray) {
        this.check_orphaned();
        this.arraySetterHelper(moveToRangeStartArray, PROPERTY_QNAME[11]);
    }

    @Override
    public void setMoveToRangeStartArray(int i, CTMoveBookmark moveToRangeStart) {
        this.generatedSetterHelperImpl(moveToRangeStart, PROPERTY_QNAME[11], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMoveBookmark insertNewMoveToRangeStart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMoveBookmark target = null;
            target = (CTMoveBookmark)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[11], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMoveBookmark addNewMoveToRangeStart() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMoveBookmark target = null;
            target = (CTMoveBookmark)((Object)this.get_store().add_element_user(PROPERTY_QNAME[11]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeMoveToRangeStart(int i) {
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
    public List<CTMarkupRange> getMoveToRangeEndList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTMarkupRange>(this::getMoveToRangeEndArray, this::setMoveToRangeEndArray, this::insertNewMoveToRangeEnd, this::removeMoveToRangeEnd, this::sizeOfMoveToRangeEndArray);
        }
    }

    @Override
    public CTMarkupRange[] getMoveToRangeEndArray() {
        return (CTMarkupRange[])this.getXmlObjectArray(PROPERTY_QNAME[12], new CTMarkupRange[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMarkupRange getMoveToRangeEndArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMarkupRange target = null;
            target = (CTMarkupRange)((Object)this.get_store().find_element_user(PROPERTY_QNAME[12], i));
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
    public int sizeOfMoveToRangeEndArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[12]);
        }
    }

    @Override
    public void setMoveToRangeEndArray(CTMarkupRange[] moveToRangeEndArray) {
        this.check_orphaned();
        this.arraySetterHelper(moveToRangeEndArray, PROPERTY_QNAME[12]);
    }

    @Override
    public void setMoveToRangeEndArray(int i, CTMarkupRange moveToRangeEnd) {
        this.generatedSetterHelperImpl(moveToRangeEnd, PROPERTY_QNAME[12], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMarkupRange insertNewMoveToRangeEnd(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMarkupRange target = null;
            target = (CTMarkupRange)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[12], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMarkupRange addNewMoveToRangeEnd() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMarkupRange target = null;
            target = (CTMarkupRange)((Object)this.get_store().add_element_user(PROPERTY_QNAME[12]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeMoveToRangeEnd(int i) {
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
    public List<CTMarkupRange> getCommentRangeStartList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTMarkupRange>(this::getCommentRangeStartArray, this::setCommentRangeStartArray, this::insertNewCommentRangeStart, this::removeCommentRangeStart, this::sizeOfCommentRangeStartArray);
        }
    }

    @Override
    public CTMarkupRange[] getCommentRangeStartArray() {
        return (CTMarkupRange[])this.getXmlObjectArray(PROPERTY_QNAME[13], new CTMarkupRange[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMarkupRange getCommentRangeStartArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMarkupRange target = null;
            target = (CTMarkupRange)((Object)this.get_store().find_element_user(PROPERTY_QNAME[13], i));
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
    public int sizeOfCommentRangeStartArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[13]);
        }
    }

    @Override
    public void setCommentRangeStartArray(CTMarkupRange[] commentRangeStartArray) {
        this.check_orphaned();
        this.arraySetterHelper(commentRangeStartArray, PROPERTY_QNAME[13]);
    }

    @Override
    public void setCommentRangeStartArray(int i, CTMarkupRange commentRangeStart) {
        this.generatedSetterHelperImpl(commentRangeStart, PROPERTY_QNAME[13], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMarkupRange insertNewCommentRangeStart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMarkupRange target = null;
            target = (CTMarkupRange)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[13], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMarkupRange addNewCommentRangeStart() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMarkupRange target = null;
            target = (CTMarkupRange)((Object)this.get_store().add_element_user(PROPERTY_QNAME[13]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCommentRangeStart(int i) {
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
    public List<CTMarkupRange> getCommentRangeEndList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTMarkupRange>(this::getCommentRangeEndArray, this::setCommentRangeEndArray, this::insertNewCommentRangeEnd, this::removeCommentRangeEnd, this::sizeOfCommentRangeEndArray);
        }
    }

    @Override
    public CTMarkupRange[] getCommentRangeEndArray() {
        return (CTMarkupRange[])this.getXmlObjectArray(PROPERTY_QNAME[14], new CTMarkupRange[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMarkupRange getCommentRangeEndArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMarkupRange target = null;
            target = (CTMarkupRange)((Object)this.get_store().find_element_user(PROPERTY_QNAME[14], i));
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
    public int sizeOfCommentRangeEndArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[14]);
        }
    }

    @Override
    public void setCommentRangeEndArray(CTMarkupRange[] commentRangeEndArray) {
        this.check_orphaned();
        this.arraySetterHelper(commentRangeEndArray, PROPERTY_QNAME[14]);
    }

    @Override
    public void setCommentRangeEndArray(int i, CTMarkupRange commentRangeEnd) {
        this.generatedSetterHelperImpl(commentRangeEnd, PROPERTY_QNAME[14], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMarkupRange insertNewCommentRangeEnd(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMarkupRange target = null;
            target = (CTMarkupRange)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[14], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMarkupRange addNewCommentRangeEnd() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMarkupRange target = null;
            target = (CTMarkupRange)((Object)this.get_store().add_element_user(PROPERTY_QNAME[14]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCommentRangeEnd(int i) {
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
    public List<CTTrackChange> getCustomXmlInsRangeStartList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTrackChange>(this::getCustomXmlInsRangeStartArray, this::setCustomXmlInsRangeStartArray, this::insertNewCustomXmlInsRangeStart, this::removeCustomXmlInsRangeStart, this::sizeOfCustomXmlInsRangeStartArray);
        }
    }

    @Override
    public CTTrackChange[] getCustomXmlInsRangeStartArray() {
        return (CTTrackChange[])this.getXmlObjectArray(PROPERTY_QNAME[15], new CTTrackChange[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTrackChange getCustomXmlInsRangeStartArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTrackChange target = null;
            target = (CTTrackChange)((Object)this.get_store().find_element_user(PROPERTY_QNAME[15], i));
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
    public int sizeOfCustomXmlInsRangeStartArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[15]);
        }
    }

    @Override
    public void setCustomXmlInsRangeStartArray(CTTrackChange[] customXmlInsRangeStartArray) {
        this.check_orphaned();
        this.arraySetterHelper(customXmlInsRangeStartArray, PROPERTY_QNAME[15]);
    }

    @Override
    public void setCustomXmlInsRangeStartArray(int i, CTTrackChange customXmlInsRangeStart) {
        this.generatedSetterHelperImpl(customXmlInsRangeStart, PROPERTY_QNAME[15], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTrackChange insertNewCustomXmlInsRangeStart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTrackChange target = null;
            target = (CTTrackChange)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[15], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTrackChange addNewCustomXmlInsRangeStart() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTrackChange target = null;
            target = (CTTrackChange)((Object)this.get_store().add_element_user(PROPERTY_QNAME[15]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCustomXmlInsRangeStart(int i) {
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
    public List<CTMarkup> getCustomXmlInsRangeEndList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTMarkup>(this::getCustomXmlInsRangeEndArray, this::setCustomXmlInsRangeEndArray, this::insertNewCustomXmlInsRangeEnd, this::removeCustomXmlInsRangeEnd, this::sizeOfCustomXmlInsRangeEndArray);
        }
    }

    @Override
    public CTMarkup[] getCustomXmlInsRangeEndArray() {
        return (CTMarkup[])this.getXmlObjectArray(PROPERTY_QNAME[16], new CTMarkup[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMarkup getCustomXmlInsRangeEndArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMarkup target = null;
            target = (CTMarkup)((Object)this.get_store().find_element_user(PROPERTY_QNAME[16], i));
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
    public int sizeOfCustomXmlInsRangeEndArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[16]);
        }
    }

    @Override
    public void setCustomXmlInsRangeEndArray(CTMarkup[] customXmlInsRangeEndArray) {
        this.check_orphaned();
        this.arraySetterHelper(customXmlInsRangeEndArray, PROPERTY_QNAME[16]);
    }

    @Override
    public void setCustomXmlInsRangeEndArray(int i, CTMarkup customXmlInsRangeEnd) {
        this.generatedSetterHelperImpl(customXmlInsRangeEnd, PROPERTY_QNAME[16], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMarkup insertNewCustomXmlInsRangeEnd(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMarkup target = null;
            target = (CTMarkup)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[16], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMarkup addNewCustomXmlInsRangeEnd() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMarkup target = null;
            target = (CTMarkup)((Object)this.get_store().add_element_user(PROPERTY_QNAME[16]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCustomXmlInsRangeEnd(int i) {
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
    public List<CTTrackChange> getCustomXmlDelRangeStartList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTrackChange>(this::getCustomXmlDelRangeStartArray, this::setCustomXmlDelRangeStartArray, this::insertNewCustomXmlDelRangeStart, this::removeCustomXmlDelRangeStart, this::sizeOfCustomXmlDelRangeStartArray);
        }
    }

    @Override
    public CTTrackChange[] getCustomXmlDelRangeStartArray() {
        return (CTTrackChange[])this.getXmlObjectArray(PROPERTY_QNAME[17], new CTTrackChange[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTrackChange getCustomXmlDelRangeStartArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTrackChange target = null;
            target = (CTTrackChange)((Object)this.get_store().find_element_user(PROPERTY_QNAME[17], i));
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
    public int sizeOfCustomXmlDelRangeStartArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[17]);
        }
    }

    @Override
    public void setCustomXmlDelRangeStartArray(CTTrackChange[] customXmlDelRangeStartArray) {
        this.check_orphaned();
        this.arraySetterHelper(customXmlDelRangeStartArray, PROPERTY_QNAME[17]);
    }

    @Override
    public void setCustomXmlDelRangeStartArray(int i, CTTrackChange customXmlDelRangeStart) {
        this.generatedSetterHelperImpl(customXmlDelRangeStart, PROPERTY_QNAME[17], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTrackChange insertNewCustomXmlDelRangeStart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTrackChange target = null;
            target = (CTTrackChange)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[17], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTrackChange addNewCustomXmlDelRangeStart() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTrackChange target = null;
            target = (CTTrackChange)((Object)this.get_store().add_element_user(PROPERTY_QNAME[17]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCustomXmlDelRangeStart(int i) {
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
    public List<CTMarkup> getCustomXmlDelRangeEndList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTMarkup>(this::getCustomXmlDelRangeEndArray, this::setCustomXmlDelRangeEndArray, this::insertNewCustomXmlDelRangeEnd, this::removeCustomXmlDelRangeEnd, this::sizeOfCustomXmlDelRangeEndArray);
        }
    }

    @Override
    public CTMarkup[] getCustomXmlDelRangeEndArray() {
        return (CTMarkup[])this.getXmlObjectArray(PROPERTY_QNAME[18], new CTMarkup[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMarkup getCustomXmlDelRangeEndArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMarkup target = null;
            target = (CTMarkup)((Object)this.get_store().find_element_user(PROPERTY_QNAME[18], i));
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
    public int sizeOfCustomXmlDelRangeEndArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[18]);
        }
    }

    @Override
    public void setCustomXmlDelRangeEndArray(CTMarkup[] customXmlDelRangeEndArray) {
        this.check_orphaned();
        this.arraySetterHelper(customXmlDelRangeEndArray, PROPERTY_QNAME[18]);
    }

    @Override
    public void setCustomXmlDelRangeEndArray(int i, CTMarkup customXmlDelRangeEnd) {
        this.generatedSetterHelperImpl(customXmlDelRangeEnd, PROPERTY_QNAME[18], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMarkup insertNewCustomXmlDelRangeEnd(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMarkup target = null;
            target = (CTMarkup)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[18], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMarkup addNewCustomXmlDelRangeEnd() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMarkup target = null;
            target = (CTMarkup)((Object)this.get_store().add_element_user(PROPERTY_QNAME[18]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCustomXmlDelRangeEnd(int i) {
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
    public List<CTTrackChange> getCustomXmlMoveFromRangeStartList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTrackChange>(this::getCustomXmlMoveFromRangeStartArray, this::setCustomXmlMoveFromRangeStartArray, this::insertNewCustomXmlMoveFromRangeStart, this::removeCustomXmlMoveFromRangeStart, this::sizeOfCustomXmlMoveFromRangeStartArray);
        }
    }

    @Override
    public CTTrackChange[] getCustomXmlMoveFromRangeStartArray() {
        return (CTTrackChange[])this.getXmlObjectArray(PROPERTY_QNAME[19], new CTTrackChange[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTrackChange getCustomXmlMoveFromRangeStartArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTrackChange target = null;
            target = (CTTrackChange)((Object)this.get_store().find_element_user(PROPERTY_QNAME[19], i));
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
    public int sizeOfCustomXmlMoveFromRangeStartArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[19]);
        }
    }

    @Override
    public void setCustomXmlMoveFromRangeStartArray(CTTrackChange[] customXmlMoveFromRangeStartArray) {
        this.check_orphaned();
        this.arraySetterHelper(customXmlMoveFromRangeStartArray, PROPERTY_QNAME[19]);
    }

    @Override
    public void setCustomXmlMoveFromRangeStartArray(int i, CTTrackChange customXmlMoveFromRangeStart) {
        this.generatedSetterHelperImpl(customXmlMoveFromRangeStart, PROPERTY_QNAME[19], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTrackChange insertNewCustomXmlMoveFromRangeStart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTrackChange target = null;
            target = (CTTrackChange)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[19], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTrackChange addNewCustomXmlMoveFromRangeStart() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTrackChange target = null;
            target = (CTTrackChange)((Object)this.get_store().add_element_user(PROPERTY_QNAME[19]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCustomXmlMoveFromRangeStart(int i) {
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
    public List<CTMarkup> getCustomXmlMoveFromRangeEndList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTMarkup>(this::getCustomXmlMoveFromRangeEndArray, this::setCustomXmlMoveFromRangeEndArray, this::insertNewCustomXmlMoveFromRangeEnd, this::removeCustomXmlMoveFromRangeEnd, this::sizeOfCustomXmlMoveFromRangeEndArray);
        }
    }

    @Override
    public CTMarkup[] getCustomXmlMoveFromRangeEndArray() {
        return (CTMarkup[])this.getXmlObjectArray(PROPERTY_QNAME[20], new CTMarkup[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMarkup getCustomXmlMoveFromRangeEndArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMarkup target = null;
            target = (CTMarkup)((Object)this.get_store().find_element_user(PROPERTY_QNAME[20], i));
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
    public int sizeOfCustomXmlMoveFromRangeEndArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[20]);
        }
    }

    @Override
    public void setCustomXmlMoveFromRangeEndArray(CTMarkup[] customXmlMoveFromRangeEndArray) {
        this.check_orphaned();
        this.arraySetterHelper(customXmlMoveFromRangeEndArray, PROPERTY_QNAME[20]);
    }

    @Override
    public void setCustomXmlMoveFromRangeEndArray(int i, CTMarkup customXmlMoveFromRangeEnd) {
        this.generatedSetterHelperImpl(customXmlMoveFromRangeEnd, PROPERTY_QNAME[20], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMarkup insertNewCustomXmlMoveFromRangeEnd(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMarkup target = null;
            target = (CTMarkup)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[20], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMarkup addNewCustomXmlMoveFromRangeEnd() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMarkup target = null;
            target = (CTMarkup)((Object)this.get_store().add_element_user(PROPERTY_QNAME[20]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCustomXmlMoveFromRangeEnd(int i) {
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
    public List<CTTrackChange> getCustomXmlMoveToRangeStartList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTrackChange>(this::getCustomXmlMoveToRangeStartArray, this::setCustomXmlMoveToRangeStartArray, this::insertNewCustomXmlMoveToRangeStart, this::removeCustomXmlMoveToRangeStart, this::sizeOfCustomXmlMoveToRangeStartArray);
        }
    }

    @Override
    public CTTrackChange[] getCustomXmlMoveToRangeStartArray() {
        return (CTTrackChange[])this.getXmlObjectArray(PROPERTY_QNAME[21], new CTTrackChange[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTrackChange getCustomXmlMoveToRangeStartArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTrackChange target = null;
            target = (CTTrackChange)((Object)this.get_store().find_element_user(PROPERTY_QNAME[21], i));
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
    public int sizeOfCustomXmlMoveToRangeStartArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[21]);
        }
    }

    @Override
    public void setCustomXmlMoveToRangeStartArray(CTTrackChange[] customXmlMoveToRangeStartArray) {
        this.check_orphaned();
        this.arraySetterHelper(customXmlMoveToRangeStartArray, PROPERTY_QNAME[21]);
    }

    @Override
    public void setCustomXmlMoveToRangeStartArray(int i, CTTrackChange customXmlMoveToRangeStart) {
        this.generatedSetterHelperImpl(customXmlMoveToRangeStart, PROPERTY_QNAME[21], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTrackChange insertNewCustomXmlMoveToRangeStart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTrackChange target = null;
            target = (CTTrackChange)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[21], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTrackChange addNewCustomXmlMoveToRangeStart() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTrackChange target = null;
            target = (CTTrackChange)((Object)this.get_store().add_element_user(PROPERTY_QNAME[21]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCustomXmlMoveToRangeStart(int i) {
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
    public List<CTMarkup> getCustomXmlMoveToRangeEndList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTMarkup>(this::getCustomXmlMoveToRangeEndArray, this::setCustomXmlMoveToRangeEndArray, this::insertNewCustomXmlMoveToRangeEnd, this::removeCustomXmlMoveToRangeEnd, this::sizeOfCustomXmlMoveToRangeEndArray);
        }
    }

    @Override
    public CTMarkup[] getCustomXmlMoveToRangeEndArray() {
        return (CTMarkup[])this.getXmlObjectArray(PROPERTY_QNAME[22], new CTMarkup[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMarkup getCustomXmlMoveToRangeEndArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMarkup target = null;
            target = (CTMarkup)((Object)this.get_store().find_element_user(PROPERTY_QNAME[22], i));
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
    public int sizeOfCustomXmlMoveToRangeEndArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[22]);
        }
    }

    @Override
    public void setCustomXmlMoveToRangeEndArray(CTMarkup[] customXmlMoveToRangeEndArray) {
        this.check_orphaned();
        this.arraySetterHelper(customXmlMoveToRangeEndArray, PROPERTY_QNAME[22]);
    }

    @Override
    public void setCustomXmlMoveToRangeEndArray(int i, CTMarkup customXmlMoveToRangeEnd) {
        this.generatedSetterHelperImpl(customXmlMoveToRangeEnd, PROPERTY_QNAME[22], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMarkup insertNewCustomXmlMoveToRangeEnd(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMarkup target = null;
            target = (CTMarkup)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[22], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMarkup addNewCustomXmlMoveToRangeEnd() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMarkup target = null;
            target = (CTMarkup)((Object)this.get_store().add_element_user(PROPERTY_QNAME[22]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCustomXmlMoveToRangeEnd(int i) {
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
    public List<CTRunTrackChange> getInsList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTRunTrackChange>(this::getInsArray, this::setInsArray, this::insertNewIns, this::removeIns, this::sizeOfInsArray);
        }
    }

    @Override
    public CTRunTrackChange[] getInsArray() {
        return (CTRunTrackChange[])this.getXmlObjectArray(PROPERTY_QNAME[23], new CTRunTrackChange[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRunTrackChange getInsArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRunTrackChange target = null;
            target = (CTRunTrackChange)((Object)this.get_store().find_element_user(PROPERTY_QNAME[23], i));
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
    public int sizeOfInsArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[23]);
        }
    }

    @Override
    public void setInsArray(CTRunTrackChange[] insArray) {
        this.check_orphaned();
        this.arraySetterHelper(insArray, PROPERTY_QNAME[23]);
    }

    @Override
    public void setInsArray(int i, CTRunTrackChange ins) {
        this.generatedSetterHelperImpl(ins, PROPERTY_QNAME[23], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRunTrackChange insertNewIns(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRunTrackChange target = null;
            target = (CTRunTrackChange)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[23], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRunTrackChange addNewIns() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRunTrackChange target = null;
            target = (CTRunTrackChange)((Object)this.get_store().add_element_user(PROPERTY_QNAME[23]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeIns(int i) {
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
    public List<CTRunTrackChange> getDelList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTRunTrackChange>(this::getDelArray, this::setDelArray, this::insertNewDel, this::removeDel, this::sizeOfDelArray);
        }
    }

    @Override
    public CTRunTrackChange[] getDelArray() {
        return (CTRunTrackChange[])this.getXmlObjectArray(PROPERTY_QNAME[24], new CTRunTrackChange[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRunTrackChange getDelArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRunTrackChange target = null;
            target = (CTRunTrackChange)((Object)this.get_store().find_element_user(PROPERTY_QNAME[24], i));
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
    public int sizeOfDelArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[24]);
        }
    }

    @Override
    public void setDelArray(CTRunTrackChange[] delArray) {
        this.check_orphaned();
        this.arraySetterHelper(delArray, PROPERTY_QNAME[24]);
    }

    @Override
    public void setDelArray(int i, CTRunTrackChange del) {
        this.generatedSetterHelperImpl(del, PROPERTY_QNAME[24], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRunTrackChange insertNewDel(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRunTrackChange target = null;
            target = (CTRunTrackChange)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[24], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRunTrackChange addNewDel() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRunTrackChange target = null;
            target = (CTRunTrackChange)((Object)this.get_store().add_element_user(PROPERTY_QNAME[24]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeDel(int i) {
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
    public List<CTRunTrackChange> getMoveFromList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTRunTrackChange>(this::getMoveFromArray, this::setMoveFromArray, this::insertNewMoveFrom, this::removeMoveFrom, this::sizeOfMoveFromArray);
        }
    }

    @Override
    public CTRunTrackChange[] getMoveFromArray() {
        return (CTRunTrackChange[])this.getXmlObjectArray(PROPERTY_QNAME[25], new CTRunTrackChange[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRunTrackChange getMoveFromArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRunTrackChange target = null;
            target = (CTRunTrackChange)((Object)this.get_store().find_element_user(PROPERTY_QNAME[25], i));
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
    public int sizeOfMoveFromArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[25]);
        }
    }

    @Override
    public void setMoveFromArray(CTRunTrackChange[] moveFromArray) {
        this.check_orphaned();
        this.arraySetterHelper(moveFromArray, PROPERTY_QNAME[25]);
    }

    @Override
    public void setMoveFromArray(int i, CTRunTrackChange moveFrom) {
        this.generatedSetterHelperImpl(moveFrom, PROPERTY_QNAME[25], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRunTrackChange insertNewMoveFrom(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRunTrackChange target = null;
            target = (CTRunTrackChange)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[25], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRunTrackChange addNewMoveFrom() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRunTrackChange target = null;
            target = (CTRunTrackChange)((Object)this.get_store().add_element_user(PROPERTY_QNAME[25]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeMoveFrom(int i) {
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
    public List<CTRunTrackChange> getMoveToList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTRunTrackChange>(this::getMoveToArray, this::setMoveToArray, this::insertNewMoveTo, this::removeMoveTo, this::sizeOfMoveToArray);
        }
    }

    @Override
    public CTRunTrackChange[] getMoveToArray() {
        return (CTRunTrackChange[])this.getXmlObjectArray(PROPERTY_QNAME[26], new CTRunTrackChange[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRunTrackChange getMoveToArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRunTrackChange target = null;
            target = (CTRunTrackChange)((Object)this.get_store().find_element_user(PROPERTY_QNAME[26], i));
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
    public int sizeOfMoveToArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[26]);
        }
    }

    @Override
    public void setMoveToArray(CTRunTrackChange[] moveToArray) {
        this.check_orphaned();
        this.arraySetterHelper(moveToArray, PROPERTY_QNAME[26]);
    }

    @Override
    public void setMoveToArray(int i, CTRunTrackChange moveTo) {
        this.generatedSetterHelperImpl(moveTo, PROPERTY_QNAME[26], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRunTrackChange insertNewMoveTo(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRunTrackChange target = null;
            target = (CTRunTrackChange)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[26], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRunTrackChange addNewMoveTo() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRunTrackChange target = null;
            target = (CTRunTrackChange)((Object)this.get_store().add_element_user(PROPERTY_QNAME[26]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeMoveTo(int i) {
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
    public List<CTOMathPara> getOMathParaList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTOMathPara>(this::getOMathParaArray, this::setOMathParaArray, this::insertNewOMathPara, this::removeOMathPara, this::sizeOfOMathParaArray);
        }
    }

    @Override
    public CTOMathPara[] getOMathParaArray() {
        return (CTOMathPara[])this.getXmlObjectArray(PROPERTY_QNAME[27], new CTOMathPara[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOMathPara getOMathParaArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOMathPara target = null;
            target = (CTOMathPara)((Object)this.get_store().find_element_user(PROPERTY_QNAME[27], i));
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
    public int sizeOfOMathParaArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[27]);
        }
    }

    @Override
    public void setOMathParaArray(CTOMathPara[] oMathParaArray) {
        this.check_orphaned();
        this.arraySetterHelper(oMathParaArray, PROPERTY_QNAME[27]);
    }

    @Override
    public void setOMathParaArray(int i, CTOMathPara oMathPara) {
        this.generatedSetterHelperImpl(oMathPara, PROPERTY_QNAME[27], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOMathPara insertNewOMathPara(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOMathPara target = null;
            target = (CTOMathPara)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[27], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOMathPara addNewOMathPara() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOMathPara target = null;
            target = (CTOMathPara)((Object)this.get_store().add_element_user(PROPERTY_QNAME[27]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeOMathPara(int i) {
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
    public List<CTOMath> getOMathList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTOMath>(this::getOMathArray, this::setOMathArray, this::insertNewOMath, this::removeOMath, this::sizeOfOMathArray);
        }
    }

    @Override
    public CTOMath[] getOMathArray() {
        return (CTOMath[])this.getXmlObjectArray(PROPERTY_QNAME[28], new CTOMath[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOMath getOMathArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOMath target = null;
            target = (CTOMath)((Object)this.get_store().find_element_user(PROPERTY_QNAME[28], i));
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
    public int sizeOfOMathArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[28]);
        }
    }

    @Override
    public void setOMathArray(CTOMath[] oMathArray) {
        this.check_orphaned();
        this.arraySetterHelper(oMathArray, PROPERTY_QNAME[28]);
    }

    @Override
    public void setOMathArray(int i, CTOMath oMath) {
        this.generatedSetterHelperImpl(oMath, PROPERTY_QNAME[28], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOMath insertNewOMath(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOMath target = null;
            target = (CTOMath)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[28], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOMath addNewOMath() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOMath target = null;
            target = (CTOMath)((Object)this.get_store().add_element_user(PROPERTY_QNAME[28]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeOMath(int i) {
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
    public List<CTAltChunk> getAltChunkList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTAltChunk>(this::getAltChunkArray, this::setAltChunkArray, this::insertNewAltChunk, this::removeAltChunk, this::sizeOfAltChunkArray);
        }
    }

    @Override
    public CTAltChunk[] getAltChunkArray() {
        return (CTAltChunk[])this.getXmlObjectArray(PROPERTY_QNAME[29], new CTAltChunk[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAltChunk getAltChunkArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAltChunk target = null;
            target = (CTAltChunk)((Object)this.get_store().find_element_user(PROPERTY_QNAME[29], i));
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
    public int sizeOfAltChunkArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[29]);
        }
    }

    @Override
    public void setAltChunkArray(CTAltChunk[] altChunkArray) {
        this.check_orphaned();
        this.arraySetterHelper(altChunkArray, PROPERTY_QNAME[29]);
    }

    @Override
    public void setAltChunkArray(int i, CTAltChunk altChunk) {
        this.generatedSetterHelperImpl(altChunk, PROPERTY_QNAME[29], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAltChunk insertNewAltChunk(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAltChunk target = null;
            target = (CTAltChunk)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[29], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAltChunk addNewAltChunk() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAltChunk target = null;
            target = (CTAltChunk)((Object)this.get_store().add_element_user(PROPERTY_QNAME[29]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAltChunk(int i) {
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
    public String getInitials() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[30]));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STString xgetInitials() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STString target = null;
            target = (STString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[30]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetInitials() {
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
    public void setInitials(String initials) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[30]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[30]));
            }
            target.setStringValue(initials);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetInitials(STString initials) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STString target = null;
            target = (STString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[30]));
            if (target == null) {
                target = (STString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[30]));
            }
            target.set(initials);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetInitials() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[30]);
        }
    }
}

