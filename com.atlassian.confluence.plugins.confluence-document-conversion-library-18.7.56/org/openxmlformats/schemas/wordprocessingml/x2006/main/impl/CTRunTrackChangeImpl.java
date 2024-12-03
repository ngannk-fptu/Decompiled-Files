/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.officeDocument.x2006.math.CTAcc
 *  org.openxmlformats.schemas.officeDocument.x2006.math.CTBar
 *  org.openxmlformats.schemas.officeDocument.x2006.math.CTBorderBox
 *  org.openxmlformats.schemas.officeDocument.x2006.math.CTBox
 *  org.openxmlformats.schemas.officeDocument.x2006.math.CTEqArr
 *  org.openxmlformats.schemas.officeDocument.x2006.math.CTF
 *  org.openxmlformats.schemas.officeDocument.x2006.math.CTFunc
 *  org.openxmlformats.schemas.officeDocument.x2006.math.CTGroupChr
 *  org.openxmlformats.schemas.officeDocument.x2006.math.CTLimLow
 *  org.openxmlformats.schemas.officeDocument.x2006.math.CTLimUpp
 *  org.openxmlformats.schemas.officeDocument.x2006.math.CTNary
 *  org.openxmlformats.schemas.officeDocument.x2006.math.CTPhant
 *  org.openxmlformats.schemas.officeDocument.x2006.math.CTRad
 *  org.openxmlformats.schemas.officeDocument.x2006.math.CTSPre
 *  org.openxmlformats.schemas.officeDocument.x2006.math.CTSSubSup
 *  org.openxmlformats.schemas.officeDocument.x2006.math.CTSSup
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTAcc;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTBar;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTBorderBox;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTBox;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTD;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTEqArr;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTF;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTFunc;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTGroupChr;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTLimLow;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTLimUpp;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTM;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTNary;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMath;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMathPara;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTPhant;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTRad;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTSPre;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTSSub;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTSSubSup;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTSSup;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBdoContentRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTCustomXmlRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDirContentRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMarkup;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMarkupRange;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMoveBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPerm;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPermStart;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTProofErr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRunTrackChange;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSmartTagRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrackChange;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.impl.CTTrackChangeImpl;

public class CTRunTrackChangeImpl
extends CTTrackChangeImpl
implements CTRunTrackChange {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXml"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "smartTag"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "sdt"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "dir"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bdo"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "r"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "proofErr"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "permStart"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "permEnd"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bookmarkStart"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bookmarkEnd"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveFromRangeStart"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveFromRangeEnd"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveToRangeStart"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveToRangeEnd"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "commentRangeStart"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "commentRangeEnd"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlInsRangeStart"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlInsRangeEnd"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlDelRangeStart"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlDelRangeEnd"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlMoveFromRangeStart"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlMoveFromRangeEnd"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlMoveToRangeStart"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlMoveToRangeEnd"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "ins"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "del"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveFrom"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveTo"), new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "oMathPara"), new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "oMath"), new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "acc"), new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "bar"), new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "box"), new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "borderBox"), new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "d"), new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "eqArr"), new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "f"), new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "func"), new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "groupChr"), new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "limLow"), new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "limUpp"), new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "m"), new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "nary"), new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "phant"), new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "rad"), new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "sPre"), new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "sSub"), new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "sSubSup"), new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "sSup"), new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "r")};

    public CTRunTrackChangeImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTCustomXmlRun> getCustomXmlList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTCustomXmlRun>(this::getCustomXmlArray, this::setCustomXmlArray, this::insertNewCustomXml, this::removeCustomXml, this::sizeOfCustomXmlArray);
        }
    }

    @Override
    public CTCustomXmlRun[] getCustomXmlArray() {
        return (CTCustomXmlRun[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTCustomXmlRun[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCustomXmlRun getCustomXmlArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCustomXmlRun target = null;
            target = (CTCustomXmlRun)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public void setCustomXmlArray(CTCustomXmlRun[] customXmlArray) {
        this.check_orphaned();
        this.arraySetterHelper(customXmlArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setCustomXmlArray(int i, CTCustomXmlRun customXml) {
        this.generatedSetterHelperImpl(customXml, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCustomXmlRun insertNewCustomXml(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCustomXmlRun target = null;
            target = (CTCustomXmlRun)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCustomXmlRun addNewCustomXml() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCustomXmlRun target = null;
            target = (CTCustomXmlRun)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
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
    public List<CTSmartTagRun> getSmartTagList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTSmartTagRun>(this::getSmartTagArray, this::setSmartTagArray, this::insertNewSmartTag, this::removeSmartTag, this::sizeOfSmartTagArray);
        }
    }

    @Override
    public CTSmartTagRun[] getSmartTagArray() {
        return (CTSmartTagRun[])this.getXmlObjectArray(PROPERTY_QNAME[1], new CTSmartTagRun[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSmartTagRun getSmartTagArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSmartTagRun target = null;
            target = (CTSmartTagRun)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], i));
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
    public int sizeOfSmartTagArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]);
        }
    }

    @Override
    public void setSmartTagArray(CTSmartTagRun[] smartTagArray) {
        this.check_orphaned();
        this.arraySetterHelper(smartTagArray, PROPERTY_QNAME[1]);
    }

    @Override
    public void setSmartTagArray(int i, CTSmartTagRun smartTag) {
        this.generatedSetterHelperImpl(smartTag, PROPERTY_QNAME[1], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSmartTagRun insertNewSmartTag(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSmartTagRun target = null;
            target = (CTSmartTagRun)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[1], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSmartTagRun addNewSmartTag() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSmartTagRun target = null;
            target = (CTSmartTagRun)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSmartTag(int i) {
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
    public List<CTSdtRun> getSdtList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTSdtRun>(this::getSdtArray, this::setSdtArray, this::insertNewSdt, this::removeSdt, this::sizeOfSdtArray);
        }
    }

    @Override
    public CTSdtRun[] getSdtArray() {
        return (CTSdtRun[])this.getXmlObjectArray(PROPERTY_QNAME[2], new CTSdtRun[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSdtRun getSdtArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSdtRun target = null;
            target = (CTSdtRun)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], i));
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
            return this.get_store().count_elements(PROPERTY_QNAME[2]);
        }
    }

    @Override
    public void setSdtArray(CTSdtRun[] sdtArray) {
        this.check_orphaned();
        this.arraySetterHelper(sdtArray, PROPERTY_QNAME[2]);
    }

    @Override
    public void setSdtArray(int i, CTSdtRun sdt) {
        this.generatedSetterHelperImpl(sdt, PROPERTY_QNAME[2], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSdtRun insertNewSdt(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSdtRun target = null;
            target = (CTSdtRun)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[2], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSdtRun addNewSdt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSdtRun target = null;
            target = (CTSdtRun)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
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
            this.get_store().remove_element(PROPERTY_QNAME[2], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTDirContentRun> getDirList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTDirContentRun>(this::getDirArray, this::setDirArray, this::insertNewDir, this::removeDir, this::sizeOfDirArray);
        }
    }

    @Override
    public CTDirContentRun[] getDirArray() {
        return (CTDirContentRun[])this.getXmlObjectArray(PROPERTY_QNAME[3], new CTDirContentRun[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDirContentRun getDirArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDirContentRun target = null;
            target = (CTDirContentRun)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], i));
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
    public int sizeOfDirArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]);
        }
    }

    @Override
    public void setDirArray(CTDirContentRun[] dirArray) {
        this.check_orphaned();
        this.arraySetterHelper(dirArray, PROPERTY_QNAME[3]);
    }

    @Override
    public void setDirArray(int i, CTDirContentRun dir) {
        this.generatedSetterHelperImpl(dir, PROPERTY_QNAME[3], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDirContentRun insertNewDir(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDirContentRun target = null;
            target = (CTDirContentRun)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[3], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDirContentRun addNewDir() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDirContentRun target = null;
            target = (CTDirContentRun)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeDir(int i) {
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
    public List<CTBdoContentRun> getBdoList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTBdoContentRun>(this::getBdoArray, this::setBdoArray, this::insertNewBdo, this::removeBdo, this::sizeOfBdoArray);
        }
    }

    @Override
    public CTBdoContentRun[] getBdoArray() {
        return (CTBdoContentRun[])this.getXmlObjectArray(PROPERTY_QNAME[4], new CTBdoContentRun[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBdoContentRun getBdoArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBdoContentRun target = null;
            target = (CTBdoContentRun)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], i));
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
    public int sizeOfBdoArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[4]);
        }
    }

    @Override
    public void setBdoArray(CTBdoContentRun[] bdoArray) {
        this.check_orphaned();
        this.arraySetterHelper(bdoArray, PROPERTY_QNAME[4]);
    }

    @Override
    public void setBdoArray(int i, CTBdoContentRun bdo) {
        this.generatedSetterHelperImpl(bdo, PROPERTY_QNAME[4], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBdoContentRun insertNewBdo(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBdoContentRun target = null;
            target = (CTBdoContentRun)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[4], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBdoContentRun addNewBdo() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBdoContentRun target = null;
            target = (CTBdoContentRun)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeBdo(int i) {
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
    public List<CTR> getRList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTR>(this::getRArray, this::setRArray, this::insertNewR, this::removeR, this::sizeOfRArray);
        }
    }

    @Override
    public CTR[] getRArray() {
        return (CTR[])this.getXmlObjectArray(PROPERTY_QNAME[5], new CTR[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTR getRArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTR target = null;
            target = (CTR)((Object)this.get_store().find_element_user(PROPERTY_QNAME[5], i));
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
            return this.get_store().count_elements(PROPERTY_QNAME[5]);
        }
    }

    @Override
    public void setRArray(CTR[] rArray) {
        this.check_orphaned();
        this.arraySetterHelper(rArray, PROPERTY_QNAME[5]);
    }

    @Override
    public void setRArray(int i, CTR r) {
        this.generatedSetterHelperImpl(r, PROPERTY_QNAME[5], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTR insertNewR(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTR target = null;
            target = (CTR)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[5], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTR addNewR() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTR target = null;
            target = (CTR)((Object)this.get_store().add_element_user(PROPERTY_QNAME[5]));
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
            this.get_store().remove_element(PROPERTY_QNAME[5], i);
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
        return (CTProofErr[])this.getXmlObjectArray(PROPERTY_QNAME[6], new CTProofErr[0]);
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
            target = (CTProofErr)((Object)this.get_store().find_element_user(PROPERTY_QNAME[6], i));
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
            return this.get_store().count_elements(PROPERTY_QNAME[6]);
        }
    }

    @Override
    public void setProofErrArray(CTProofErr[] proofErrArray) {
        this.check_orphaned();
        this.arraySetterHelper(proofErrArray, PROPERTY_QNAME[6]);
    }

    @Override
    public void setProofErrArray(int i, CTProofErr proofErr) {
        this.generatedSetterHelperImpl(proofErr, PROPERTY_QNAME[6], i, (short)2);
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
            target = (CTProofErr)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[6], i));
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
            target = (CTProofErr)((Object)this.get_store().add_element_user(PROPERTY_QNAME[6]));
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
            this.get_store().remove_element(PROPERTY_QNAME[6], i);
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
        return (CTPermStart[])this.getXmlObjectArray(PROPERTY_QNAME[7], new CTPermStart[0]);
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
            target = (CTPermStart)((Object)this.get_store().find_element_user(PROPERTY_QNAME[7], i));
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
            return this.get_store().count_elements(PROPERTY_QNAME[7]);
        }
    }

    @Override
    public void setPermStartArray(CTPermStart[] permStartArray) {
        this.check_orphaned();
        this.arraySetterHelper(permStartArray, PROPERTY_QNAME[7]);
    }

    @Override
    public void setPermStartArray(int i, CTPermStart permStart) {
        this.generatedSetterHelperImpl(permStart, PROPERTY_QNAME[7], i, (short)2);
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
            target = (CTPermStart)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[7], i));
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
            target = (CTPermStart)((Object)this.get_store().add_element_user(PROPERTY_QNAME[7]));
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
            this.get_store().remove_element(PROPERTY_QNAME[7], i);
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
        return (CTPerm[])this.getXmlObjectArray(PROPERTY_QNAME[8], new CTPerm[0]);
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
            target = (CTPerm)((Object)this.get_store().find_element_user(PROPERTY_QNAME[8], i));
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
            return this.get_store().count_elements(PROPERTY_QNAME[8]);
        }
    }

    @Override
    public void setPermEndArray(CTPerm[] permEndArray) {
        this.check_orphaned();
        this.arraySetterHelper(permEndArray, PROPERTY_QNAME[8]);
    }

    @Override
    public void setPermEndArray(int i, CTPerm permEnd) {
        this.generatedSetterHelperImpl(permEnd, PROPERTY_QNAME[8], i, (short)2);
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
            target = (CTPerm)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[8], i));
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
            target = (CTPerm)((Object)this.get_store().add_element_user(PROPERTY_QNAME[8]));
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
            this.get_store().remove_element(PROPERTY_QNAME[8], i);
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
        return (CTBookmark[])this.getXmlObjectArray(PROPERTY_QNAME[9], new CTBookmark[0]);
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
            target = (CTBookmark)((Object)this.get_store().find_element_user(PROPERTY_QNAME[9], i));
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
            return this.get_store().count_elements(PROPERTY_QNAME[9]);
        }
    }

    @Override
    public void setBookmarkStartArray(CTBookmark[] bookmarkStartArray) {
        this.check_orphaned();
        this.arraySetterHelper(bookmarkStartArray, PROPERTY_QNAME[9]);
    }

    @Override
    public void setBookmarkStartArray(int i, CTBookmark bookmarkStart) {
        this.generatedSetterHelperImpl(bookmarkStart, PROPERTY_QNAME[9], i, (short)2);
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
            target = (CTBookmark)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[9], i));
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
            target = (CTBookmark)((Object)this.get_store().add_element_user(PROPERTY_QNAME[9]));
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
            this.get_store().remove_element(PROPERTY_QNAME[9], i);
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
        return (CTMarkupRange[])this.getXmlObjectArray(PROPERTY_QNAME[10], new CTMarkupRange[0]);
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
    public int sizeOfBookmarkEndArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[10]);
        }
    }

    @Override
    public void setBookmarkEndArray(CTMarkupRange[] bookmarkEndArray) {
        this.check_orphaned();
        this.arraySetterHelper(bookmarkEndArray, PROPERTY_QNAME[10]);
    }

    @Override
    public void setBookmarkEndArray(int i, CTMarkupRange bookmarkEnd) {
        this.generatedSetterHelperImpl(bookmarkEnd, PROPERTY_QNAME[10], i, (short)2);
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
            target = (CTMarkupRange)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[10], i));
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
            target = (CTMarkupRange)((Object)this.get_store().add_element_user(PROPERTY_QNAME[10]));
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
            this.get_store().remove_element(PROPERTY_QNAME[10], i);
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
        return (CTMoveBookmark[])this.getXmlObjectArray(PROPERTY_QNAME[11], new CTMoveBookmark[0]);
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
    public int sizeOfMoveFromRangeStartArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[11]);
        }
    }

    @Override
    public void setMoveFromRangeStartArray(CTMoveBookmark[] moveFromRangeStartArray) {
        this.check_orphaned();
        this.arraySetterHelper(moveFromRangeStartArray, PROPERTY_QNAME[11]);
    }

    @Override
    public void setMoveFromRangeStartArray(int i, CTMoveBookmark moveFromRangeStart) {
        this.generatedSetterHelperImpl(moveFromRangeStart, PROPERTY_QNAME[11], i, (short)2);
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
            target = (CTMoveBookmark)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[11], i));
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
            target = (CTMoveBookmark)((Object)this.get_store().add_element_user(PROPERTY_QNAME[11]));
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
            this.get_store().remove_element(PROPERTY_QNAME[11], i);
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
        return (CTMarkupRange[])this.getXmlObjectArray(PROPERTY_QNAME[12], new CTMarkupRange[0]);
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
    public int sizeOfMoveFromRangeEndArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[12]);
        }
    }

    @Override
    public void setMoveFromRangeEndArray(CTMarkupRange[] moveFromRangeEndArray) {
        this.check_orphaned();
        this.arraySetterHelper(moveFromRangeEndArray, PROPERTY_QNAME[12]);
    }

    @Override
    public void setMoveFromRangeEndArray(int i, CTMarkupRange moveFromRangeEnd) {
        this.generatedSetterHelperImpl(moveFromRangeEnd, PROPERTY_QNAME[12], i, (short)2);
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
            target = (CTMarkupRange)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[12], i));
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
            target = (CTMarkupRange)((Object)this.get_store().add_element_user(PROPERTY_QNAME[12]));
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
            this.get_store().remove_element(PROPERTY_QNAME[12], i);
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
        return (CTMoveBookmark[])this.getXmlObjectArray(PROPERTY_QNAME[13], new CTMoveBookmark[0]);
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
            target = (CTMoveBookmark)((Object)this.get_store().find_element_user(PROPERTY_QNAME[13], i));
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
            return this.get_store().count_elements(PROPERTY_QNAME[13]);
        }
    }

    @Override
    public void setMoveToRangeStartArray(CTMoveBookmark[] moveToRangeStartArray) {
        this.check_orphaned();
        this.arraySetterHelper(moveToRangeStartArray, PROPERTY_QNAME[13]);
    }

    @Override
    public void setMoveToRangeStartArray(int i, CTMoveBookmark moveToRangeStart) {
        this.generatedSetterHelperImpl(moveToRangeStart, PROPERTY_QNAME[13], i, (short)2);
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
            target = (CTMoveBookmark)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[13], i));
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
            target = (CTMoveBookmark)((Object)this.get_store().add_element_user(PROPERTY_QNAME[13]));
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
            this.get_store().remove_element(PROPERTY_QNAME[13], i);
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
        return (CTMarkupRange[])this.getXmlObjectArray(PROPERTY_QNAME[14], new CTMarkupRange[0]);
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
    public int sizeOfMoveToRangeEndArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[14]);
        }
    }

    @Override
    public void setMoveToRangeEndArray(CTMarkupRange[] moveToRangeEndArray) {
        this.check_orphaned();
        this.arraySetterHelper(moveToRangeEndArray, PROPERTY_QNAME[14]);
    }

    @Override
    public void setMoveToRangeEndArray(int i, CTMarkupRange moveToRangeEnd) {
        this.generatedSetterHelperImpl(moveToRangeEnd, PROPERTY_QNAME[14], i, (short)2);
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
            target = (CTMarkupRange)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[14], i));
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
            target = (CTMarkupRange)((Object)this.get_store().add_element_user(PROPERTY_QNAME[14]));
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
            this.get_store().remove_element(PROPERTY_QNAME[14], i);
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
        return (CTMarkupRange[])this.getXmlObjectArray(PROPERTY_QNAME[15], new CTMarkupRange[0]);
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
            target = (CTMarkupRange)((Object)this.get_store().find_element_user(PROPERTY_QNAME[15], i));
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
            return this.get_store().count_elements(PROPERTY_QNAME[15]);
        }
    }

    @Override
    public void setCommentRangeStartArray(CTMarkupRange[] commentRangeStartArray) {
        this.check_orphaned();
        this.arraySetterHelper(commentRangeStartArray, PROPERTY_QNAME[15]);
    }

    @Override
    public void setCommentRangeStartArray(int i, CTMarkupRange commentRangeStart) {
        this.generatedSetterHelperImpl(commentRangeStart, PROPERTY_QNAME[15], i, (short)2);
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
            target = (CTMarkupRange)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[15], i));
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
            target = (CTMarkupRange)((Object)this.get_store().add_element_user(PROPERTY_QNAME[15]));
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
            this.get_store().remove_element(PROPERTY_QNAME[15], i);
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
        return (CTMarkupRange[])this.getXmlObjectArray(PROPERTY_QNAME[16], new CTMarkupRange[0]);
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
            target = (CTMarkupRange)((Object)this.get_store().find_element_user(PROPERTY_QNAME[16], i));
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
            return this.get_store().count_elements(PROPERTY_QNAME[16]);
        }
    }

    @Override
    public void setCommentRangeEndArray(CTMarkupRange[] commentRangeEndArray) {
        this.check_orphaned();
        this.arraySetterHelper(commentRangeEndArray, PROPERTY_QNAME[16]);
    }

    @Override
    public void setCommentRangeEndArray(int i, CTMarkupRange commentRangeEnd) {
        this.generatedSetterHelperImpl(commentRangeEnd, PROPERTY_QNAME[16], i, (short)2);
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
            target = (CTMarkupRange)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[16], i));
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
            target = (CTMarkupRange)((Object)this.get_store().add_element_user(PROPERTY_QNAME[16]));
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
            this.get_store().remove_element(PROPERTY_QNAME[16], i);
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
        return (CTTrackChange[])this.getXmlObjectArray(PROPERTY_QNAME[17], new CTTrackChange[0]);
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
    public int sizeOfCustomXmlInsRangeStartArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[17]);
        }
    }

    @Override
    public void setCustomXmlInsRangeStartArray(CTTrackChange[] customXmlInsRangeStartArray) {
        this.check_orphaned();
        this.arraySetterHelper(customXmlInsRangeStartArray, PROPERTY_QNAME[17]);
    }

    @Override
    public void setCustomXmlInsRangeStartArray(int i, CTTrackChange customXmlInsRangeStart) {
        this.generatedSetterHelperImpl(customXmlInsRangeStart, PROPERTY_QNAME[17], i, (short)2);
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
            target = (CTTrackChange)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[17], i));
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
            target = (CTTrackChange)((Object)this.get_store().add_element_user(PROPERTY_QNAME[17]));
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
            this.get_store().remove_element(PROPERTY_QNAME[17], i);
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
        return (CTMarkup[])this.getXmlObjectArray(PROPERTY_QNAME[18], new CTMarkup[0]);
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
    public int sizeOfCustomXmlInsRangeEndArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[18]);
        }
    }

    @Override
    public void setCustomXmlInsRangeEndArray(CTMarkup[] customXmlInsRangeEndArray) {
        this.check_orphaned();
        this.arraySetterHelper(customXmlInsRangeEndArray, PROPERTY_QNAME[18]);
    }

    @Override
    public void setCustomXmlInsRangeEndArray(int i, CTMarkup customXmlInsRangeEnd) {
        this.generatedSetterHelperImpl(customXmlInsRangeEnd, PROPERTY_QNAME[18], i, (short)2);
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
            target = (CTMarkup)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[18], i));
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
            target = (CTMarkup)((Object)this.get_store().add_element_user(PROPERTY_QNAME[18]));
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
            this.get_store().remove_element(PROPERTY_QNAME[18], i);
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
        return (CTTrackChange[])this.getXmlObjectArray(PROPERTY_QNAME[19], new CTTrackChange[0]);
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
    public int sizeOfCustomXmlDelRangeStartArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[19]);
        }
    }

    @Override
    public void setCustomXmlDelRangeStartArray(CTTrackChange[] customXmlDelRangeStartArray) {
        this.check_orphaned();
        this.arraySetterHelper(customXmlDelRangeStartArray, PROPERTY_QNAME[19]);
    }

    @Override
    public void setCustomXmlDelRangeStartArray(int i, CTTrackChange customXmlDelRangeStart) {
        this.generatedSetterHelperImpl(customXmlDelRangeStart, PROPERTY_QNAME[19], i, (short)2);
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
            target = (CTTrackChange)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[19], i));
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
            target = (CTTrackChange)((Object)this.get_store().add_element_user(PROPERTY_QNAME[19]));
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
            this.get_store().remove_element(PROPERTY_QNAME[19], i);
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
        return (CTMarkup[])this.getXmlObjectArray(PROPERTY_QNAME[20], new CTMarkup[0]);
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
    public int sizeOfCustomXmlDelRangeEndArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[20]);
        }
    }

    @Override
    public void setCustomXmlDelRangeEndArray(CTMarkup[] customXmlDelRangeEndArray) {
        this.check_orphaned();
        this.arraySetterHelper(customXmlDelRangeEndArray, PROPERTY_QNAME[20]);
    }

    @Override
    public void setCustomXmlDelRangeEndArray(int i, CTMarkup customXmlDelRangeEnd) {
        this.generatedSetterHelperImpl(customXmlDelRangeEnd, PROPERTY_QNAME[20], i, (short)2);
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
            target = (CTMarkup)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[20], i));
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
            target = (CTMarkup)((Object)this.get_store().add_element_user(PROPERTY_QNAME[20]));
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
            this.get_store().remove_element(PROPERTY_QNAME[20], i);
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
        return (CTTrackChange[])this.getXmlObjectArray(PROPERTY_QNAME[21], new CTTrackChange[0]);
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
    public int sizeOfCustomXmlMoveFromRangeStartArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[21]);
        }
    }

    @Override
    public void setCustomXmlMoveFromRangeStartArray(CTTrackChange[] customXmlMoveFromRangeStartArray) {
        this.check_orphaned();
        this.arraySetterHelper(customXmlMoveFromRangeStartArray, PROPERTY_QNAME[21]);
    }

    @Override
    public void setCustomXmlMoveFromRangeStartArray(int i, CTTrackChange customXmlMoveFromRangeStart) {
        this.generatedSetterHelperImpl(customXmlMoveFromRangeStart, PROPERTY_QNAME[21], i, (short)2);
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
            target = (CTTrackChange)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[21], i));
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
            target = (CTTrackChange)((Object)this.get_store().add_element_user(PROPERTY_QNAME[21]));
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
            this.get_store().remove_element(PROPERTY_QNAME[21], i);
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
        return (CTMarkup[])this.getXmlObjectArray(PROPERTY_QNAME[22], new CTMarkup[0]);
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
    public int sizeOfCustomXmlMoveFromRangeEndArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[22]);
        }
    }

    @Override
    public void setCustomXmlMoveFromRangeEndArray(CTMarkup[] customXmlMoveFromRangeEndArray) {
        this.check_orphaned();
        this.arraySetterHelper(customXmlMoveFromRangeEndArray, PROPERTY_QNAME[22]);
    }

    @Override
    public void setCustomXmlMoveFromRangeEndArray(int i, CTMarkup customXmlMoveFromRangeEnd) {
        this.generatedSetterHelperImpl(customXmlMoveFromRangeEnd, PROPERTY_QNAME[22], i, (short)2);
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
            target = (CTMarkup)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[22], i));
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
            target = (CTMarkup)((Object)this.get_store().add_element_user(PROPERTY_QNAME[22]));
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
            this.get_store().remove_element(PROPERTY_QNAME[22], i);
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
        return (CTTrackChange[])this.getXmlObjectArray(PROPERTY_QNAME[23], new CTTrackChange[0]);
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
            target = (CTTrackChange)((Object)this.get_store().find_element_user(PROPERTY_QNAME[23], i));
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
            return this.get_store().count_elements(PROPERTY_QNAME[23]);
        }
    }

    @Override
    public void setCustomXmlMoveToRangeStartArray(CTTrackChange[] customXmlMoveToRangeStartArray) {
        this.check_orphaned();
        this.arraySetterHelper(customXmlMoveToRangeStartArray, PROPERTY_QNAME[23]);
    }

    @Override
    public void setCustomXmlMoveToRangeStartArray(int i, CTTrackChange customXmlMoveToRangeStart) {
        this.generatedSetterHelperImpl(customXmlMoveToRangeStart, PROPERTY_QNAME[23], i, (short)2);
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
            target = (CTTrackChange)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[23], i));
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
            target = (CTTrackChange)((Object)this.get_store().add_element_user(PROPERTY_QNAME[23]));
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
            this.get_store().remove_element(PROPERTY_QNAME[23], i);
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
        return (CTMarkup[])this.getXmlObjectArray(PROPERTY_QNAME[24], new CTMarkup[0]);
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
            target = (CTMarkup)((Object)this.get_store().find_element_user(PROPERTY_QNAME[24], i));
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
            return this.get_store().count_elements(PROPERTY_QNAME[24]);
        }
    }

    @Override
    public void setCustomXmlMoveToRangeEndArray(CTMarkup[] customXmlMoveToRangeEndArray) {
        this.check_orphaned();
        this.arraySetterHelper(customXmlMoveToRangeEndArray, PROPERTY_QNAME[24]);
    }

    @Override
    public void setCustomXmlMoveToRangeEndArray(int i, CTMarkup customXmlMoveToRangeEnd) {
        this.generatedSetterHelperImpl(customXmlMoveToRangeEnd, PROPERTY_QNAME[24], i, (short)2);
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
            target = (CTMarkup)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[24], i));
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
            target = (CTMarkup)((Object)this.get_store().add_element_user(PROPERTY_QNAME[24]));
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
            this.get_store().remove_element(PROPERTY_QNAME[24], i);
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
        return (CTRunTrackChange[])this.getXmlObjectArray(PROPERTY_QNAME[25], new CTRunTrackChange[0]);
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
    public int sizeOfInsArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[25]);
        }
    }

    @Override
    public void setInsArray(CTRunTrackChange[] insArray) {
        this.check_orphaned();
        this.arraySetterHelper(insArray, PROPERTY_QNAME[25]);
    }

    @Override
    public void setInsArray(int i, CTRunTrackChange ins) {
        this.generatedSetterHelperImpl(ins, PROPERTY_QNAME[25], i, (short)2);
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
            target = (CTRunTrackChange)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[25], i));
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
            target = (CTRunTrackChange)((Object)this.get_store().add_element_user(PROPERTY_QNAME[25]));
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
            this.get_store().remove_element(PROPERTY_QNAME[25], i);
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
        return (CTRunTrackChange[])this.getXmlObjectArray(PROPERTY_QNAME[26], new CTRunTrackChange[0]);
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
    public int sizeOfDelArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[26]);
        }
    }

    @Override
    public void setDelArray(CTRunTrackChange[] delArray) {
        this.check_orphaned();
        this.arraySetterHelper(delArray, PROPERTY_QNAME[26]);
    }

    @Override
    public void setDelArray(int i, CTRunTrackChange del) {
        this.generatedSetterHelperImpl(del, PROPERTY_QNAME[26], i, (short)2);
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
            target = (CTRunTrackChange)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[26], i));
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
            target = (CTRunTrackChange)((Object)this.get_store().add_element_user(PROPERTY_QNAME[26]));
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
            this.get_store().remove_element(PROPERTY_QNAME[26], i);
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
        return (CTRunTrackChange[])this.getXmlObjectArray(PROPERTY_QNAME[27], new CTRunTrackChange[0]);
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
            target = (CTRunTrackChange)((Object)this.get_store().find_element_user(PROPERTY_QNAME[27], i));
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
            return this.get_store().count_elements(PROPERTY_QNAME[27]);
        }
    }

    @Override
    public void setMoveFromArray(CTRunTrackChange[] moveFromArray) {
        this.check_orphaned();
        this.arraySetterHelper(moveFromArray, PROPERTY_QNAME[27]);
    }

    @Override
    public void setMoveFromArray(int i, CTRunTrackChange moveFrom) {
        this.generatedSetterHelperImpl(moveFrom, PROPERTY_QNAME[27], i, (short)2);
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
            target = (CTRunTrackChange)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[27], i));
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
            target = (CTRunTrackChange)((Object)this.get_store().add_element_user(PROPERTY_QNAME[27]));
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
            this.get_store().remove_element(PROPERTY_QNAME[27], i);
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
        return (CTRunTrackChange[])this.getXmlObjectArray(PROPERTY_QNAME[28], new CTRunTrackChange[0]);
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
            target = (CTRunTrackChange)((Object)this.get_store().find_element_user(PROPERTY_QNAME[28], i));
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
            return this.get_store().count_elements(PROPERTY_QNAME[28]);
        }
    }

    @Override
    public void setMoveToArray(CTRunTrackChange[] moveToArray) {
        this.check_orphaned();
        this.arraySetterHelper(moveToArray, PROPERTY_QNAME[28]);
    }

    @Override
    public void setMoveToArray(int i, CTRunTrackChange moveTo) {
        this.generatedSetterHelperImpl(moveTo, PROPERTY_QNAME[28], i, (short)2);
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
            target = (CTRunTrackChange)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[28], i));
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
            target = (CTRunTrackChange)((Object)this.get_store().add_element_user(PROPERTY_QNAME[28]));
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
            this.get_store().remove_element(PROPERTY_QNAME[28], i);
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
        return (CTOMathPara[])this.getXmlObjectArray(PROPERTY_QNAME[29], new CTOMathPara[0]);
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
            target = (CTOMathPara)((Object)this.get_store().find_element_user(PROPERTY_QNAME[29], i));
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
            return this.get_store().count_elements(PROPERTY_QNAME[29]);
        }
    }

    @Override
    public void setOMathParaArray(CTOMathPara[] oMathParaArray) {
        this.check_orphaned();
        this.arraySetterHelper(oMathParaArray, PROPERTY_QNAME[29]);
    }

    @Override
    public void setOMathParaArray(int i, CTOMathPara oMathPara) {
        this.generatedSetterHelperImpl(oMathPara, PROPERTY_QNAME[29], i, (short)2);
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
            target = (CTOMathPara)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[29], i));
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
            target = (CTOMathPara)((Object)this.get_store().add_element_user(PROPERTY_QNAME[29]));
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
            this.get_store().remove_element(PROPERTY_QNAME[29], i);
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
        return (CTOMath[])this.getXmlObjectArray(PROPERTY_QNAME[30], new CTOMath[0]);
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
            target = (CTOMath)((Object)this.get_store().find_element_user(PROPERTY_QNAME[30], i));
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
            return this.get_store().count_elements(PROPERTY_QNAME[30]);
        }
    }

    @Override
    public void setOMathArray(CTOMath[] oMathArray) {
        this.check_orphaned();
        this.arraySetterHelper(oMathArray, PROPERTY_QNAME[30]);
    }

    @Override
    public void setOMathArray(int i, CTOMath oMath) {
        this.generatedSetterHelperImpl(oMath, PROPERTY_QNAME[30], i, (short)2);
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
            target = (CTOMath)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[30], i));
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
            target = (CTOMath)((Object)this.get_store().add_element_user(PROPERTY_QNAME[30]));
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
            this.get_store().remove_element(PROPERTY_QNAME[30], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTAcc> getAccList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTAcc>(this::getAccArray, this::setAccArray, this::insertNewAcc, this::removeAcc, this::sizeOfAccArray);
        }
    }

    @Override
    public CTAcc[] getAccArray() {
        return (CTAcc[])this.getXmlObjectArray(PROPERTY_QNAME[31], (XmlObject[])new CTAcc[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAcc getAccArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAcc target = null;
            target = (CTAcc)this.get_store().find_element_user(PROPERTY_QNAME[31], i);
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
    public int sizeOfAccArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[31]);
        }
    }

    @Override
    public void setAccArray(CTAcc[] accArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])accArray, PROPERTY_QNAME[31]);
    }

    @Override
    public void setAccArray(int i, CTAcc acc) {
        this.generatedSetterHelperImpl((XmlObject)acc, PROPERTY_QNAME[31], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAcc insertNewAcc(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAcc target = null;
            target = (CTAcc)this.get_store().insert_element_user(PROPERTY_QNAME[31], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAcc addNewAcc() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAcc target = null;
            target = (CTAcc)this.get_store().add_element_user(PROPERTY_QNAME[31]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAcc(int i) {
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
    public List<CTBar> getBarList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTBar>(this::getBarArray, this::setBarArray, this::insertNewBar, this::removeBar, this::sizeOfBarArray);
        }
    }

    @Override
    public CTBar[] getBarArray() {
        return (CTBar[])this.getXmlObjectArray(PROPERTY_QNAME[32], (XmlObject[])new CTBar[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBar getBarArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBar target = null;
            target = (CTBar)this.get_store().find_element_user(PROPERTY_QNAME[32], i);
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
    public int sizeOfBarArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[32]);
        }
    }

    @Override
    public void setBarArray(CTBar[] barArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])barArray, PROPERTY_QNAME[32]);
    }

    @Override
    public void setBarArray(int i, CTBar bar) {
        this.generatedSetterHelperImpl((XmlObject)bar, PROPERTY_QNAME[32], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBar insertNewBar(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBar target = null;
            target = (CTBar)this.get_store().insert_element_user(PROPERTY_QNAME[32], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBar addNewBar() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBar target = null;
            target = (CTBar)this.get_store().add_element_user(PROPERTY_QNAME[32]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeBar(int i) {
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
    public List<CTBox> getBoxList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTBox>(this::getBoxArray, this::setBoxArray, this::insertNewBox, this::removeBox, this::sizeOfBoxArray);
        }
    }

    @Override
    public CTBox[] getBoxArray() {
        return (CTBox[])this.getXmlObjectArray(PROPERTY_QNAME[33], (XmlObject[])new CTBox[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBox getBoxArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBox target = null;
            target = (CTBox)this.get_store().find_element_user(PROPERTY_QNAME[33], i);
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
    public int sizeOfBoxArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[33]);
        }
    }

    @Override
    public void setBoxArray(CTBox[] boxArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])boxArray, PROPERTY_QNAME[33]);
    }

    @Override
    public void setBoxArray(int i, CTBox box) {
        this.generatedSetterHelperImpl((XmlObject)box, PROPERTY_QNAME[33], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBox insertNewBox(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBox target = null;
            target = (CTBox)this.get_store().insert_element_user(PROPERTY_QNAME[33], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBox addNewBox() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBox target = null;
            target = (CTBox)this.get_store().add_element_user(PROPERTY_QNAME[33]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeBox(int i) {
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
    public List<CTBorderBox> getBorderBoxList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTBorderBox>(this::getBorderBoxArray, this::setBorderBoxArray, this::insertNewBorderBox, this::removeBorderBox, this::sizeOfBorderBoxArray);
        }
    }

    @Override
    public CTBorderBox[] getBorderBoxArray() {
        return (CTBorderBox[])this.getXmlObjectArray(PROPERTY_QNAME[34], (XmlObject[])new CTBorderBox[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBorderBox getBorderBoxArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBorderBox target = null;
            target = (CTBorderBox)this.get_store().find_element_user(PROPERTY_QNAME[34], i);
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
    public int sizeOfBorderBoxArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[34]);
        }
    }

    @Override
    public void setBorderBoxArray(CTBorderBox[] borderBoxArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])borderBoxArray, PROPERTY_QNAME[34]);
    }

    @Override
    public void setBorderBoxArray(int i, CTBorderBox borderBox) {
        this.generatedSetterHelperImpl((XmlObject)borderBox, PROPERTY_QNAME[34], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBorderBox insertNewBorderBox(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBorderBox target = null;
            target = (CTBorderBox)this.get_store().insert_element_user(PROPERTY_QNAME[34], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBorderBox addNewBorderBox() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBorderBox target = null;
            target = (CTBorderBox)this.get_store().add_element_user(PROPERTY_QNAME[34]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeBorderBox(int i) {
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
    public List<CTD> getDList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTD>(this::getDArray, this::setDArray, this::insertNewD, this::removeD, this::sizeOfDArray);
        }
    }

    @Override
    public CTD[] getDArray() {
        return (CTD[])this.getXmlObjectArray(PROPERTY_QNAME[35], new CTD[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTD getDArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTD target = null;
            target = (CTD)((Object)this.get_store().find_element_user(PROPERTY_QNAME[35], i));
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
    public int sizeOfDArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[35]);
        }
    }

    @Override
    public void setDArray(CTD[] dArray) {
        this.check_orphaned();
        this.arraySetterHelper(dArray, PROPERTY_QNAME[35]);
    }

    @Override
    public void setDArray(int i, CTD d) {
        this.generatedSetterHelperImpl(d, PROPERTY_QNAME[35], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTD insertNewD(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTD target = null;
            target = (CTD)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[35], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTD addNewD() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTD target = null;
            target = (CTD)((Object)this.get_store().add_element_user(PROPERTY_QNAME[35]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeD(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[35], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTEqArr> getEqArrList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTEqArr>(this::getEqArrArray, this::setEqArrArray, this::insertNewEqArr, this::removeEqArr, this::sizeOfEqArrArray);
        }
    }

    @Override
    public CTEqArr[] getEqArrArray() {
        return (CTEqArr[])this.getXmlObjectArray(PROPERTY_QNAME[36], (XmlObject[])new CTEqArr[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEqArr getEqArrArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEqArr target = null;
            target = (CTEqArr)this.get_store().find_element_user(PROPERTY_QNAME[36], i);
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
    public int sizeOfEqArrArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[36]);
        }
    }

    @Override
    public void setEqArrArray(CTEqArr[] eqArrArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])eqArrArray, PROPERTY_QNAME[36]);
    }

    @Override
    public void setEqArrArray(int i, CTEqArr eqArr) {
        this.generatedSetterHelperImpl((XmlObject)eqArr, PROPERTY_QNAME[36], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEqArr insertNewEqArr(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEqArr target = null;
            target = (CTEqArr)this.get_store().insert_element_user(PROPERTY_QNAME[36], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEqArr addNewEqArr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEqArr target = null;
            target = (CTEqArr)this.get_store().add_element_user(PROPERTY_QNAME[36]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeEqArr(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[36], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTF> getFList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTF>(this::getFArray, this::setFArray, this::insertNewF, this::removeF, this::sizeOfFArray);
        }
    }

    @Override
    public CTF[] getFArray() {
        return (CTF[])this.getXmlObjectArray(PROPERTY_QNAME[37], (XmlObject[])new CTF[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTF getFArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTF target = null;
            target = (CTF)this.get_store().find_element_user(PROPERTY_QNAME[37], i);
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
    public int sizeOfFArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[37]);
        }
    }

    @Override
    public void setFArray(CTF[] fArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])fArray, PROPERTY_QNAME[37]);
    }

    @Override
    public void setFArray(int i, CTF f) {
        this.generatedSetterHelperImpl((XmlObject)f, PROPERTY_QNAME[37], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTF insertNewF(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTF target = null;
            target = (CTF)this.get_store().insert_element_user(PROPERTY_QNAME[37], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTF addNewF() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTF target = null;
            target = (CTF)this.get_store().add_element_user(PROPERTY_QNAME[37]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeF(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[37], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTFunc> getFuncList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTFunc>(this::getFuncArray, this::setFuncArray, this::insertNewFunc, this::removeFunc, this::sizeOfFuncArray);
        }
    }

    @Override
    public CTFunc[] getFuncArray() {
        return (CTFunc[])this.getXmlObjectArray(PROPERTY_QNAME[38], (XmlObject[])new CTFunc[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFunc getFuncArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFunc target = null;
            target = (CTFunc)this.get_store().find_element_user(PROPERTY_QNAME[38], i);
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
    public int sizeOfFuncArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[38]);
        }
    }

    @Override
    public void setFuncArray(CTFunc[] funcArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])funcArray, PROPERTY_QNAME[38]);
    }

    @Override
    public void setFuncArray(int i, CTFunc func) {
        this.generatedSetterHelperImpl((XmlObject)func, PROPERTY_QNAME[38], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFunc insertNewFunc(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFunc target = null;
            target = (CTFunc)this.get_store().insert_element_user(PROPERTY_QNAME[38], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFunc addNewFunc() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFunc target = null;
            target = (CTFunc)this.get_store().add_element_user(PROPERTY_QNAME[38]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeFunc(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[38], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTGroupChr> getGroupChrList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTGroupChr>(this::getGroupChrArray, this::setGroupChrArray, this::insertNewGroupChr, this::removeGroupChr, this::sizeOfGroupChrArray);
        }
    }

    @Override
    public CTGroupChr[] getGroupChrArray() {
        return (CTGroupChr[])this.getXmlObjectArray(PROPERTY_QNAME[39], (XmlObject[])new CTGroupChr[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGroupChr getGroupChrArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGroupChr target = null;
            target = (CTGroupChr)this.get_store().find_element_user(PROPERTY_QNAME[39], i);
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
    public int sizeOfGroupChrArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[39]);
        }
    }

    @Override
    public void setGroupChrArray(CTGroupChr[] groupChrArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])groupChrArray, PROPERTY_QNAME[39]);
    }

    @Override
    public void setGroupChrArray(int i, CTGroupChr groupChr) {
        this.generatedSetterHelperImpl((XmlObject)groupChr, PROPERTY_QNAME[39], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGroupChr insertNewGroupChr(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGroupChr target = null;
            target = (CTGroupChr)this.get_store().insert_element_user(PROPERTY_QNAME[39], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGroupChr addNewGroupChr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGroupChr target = null;
            target = (CTGroupChr)this.get_store().add_element_user(PROPERTY_QNAME[39]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeGroupChr(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[39], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTLimLow> getLimLowList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTLimLow>(this::getLimLowArray, this::setLimLowArray, this::insertNewLimLow, this::removeLimLow, this::sizeOfLimLowArray);
        }
    }

    @Override
    public CTLimLow[] getLimLowArray() {
        return (CTLimLow[])this.getXmlObjectArray(PROPERTY_QNAME[40], (XmlObject[])new CTLimLow[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLimLow getLimLowArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLimLow target = null;
            target = (CTLimLow)this.get_store().find_element_user(PROPERTY_QNAME[40], i);
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
    public int sizeOfLimLowArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[40]);
        }
    }

    @Override
    public void setLimLowArray(CTLimLow[] limLowArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])limLowArray, PROPERTY_QNAME[40]);
    }

    @Override
    public void setLimLowArray(int i, CTLimLow limLow) {
        this.generatedSetterHelperImpl((XmlObject)limLow, PROPERTY_QNAME[40], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLimLow insertNewLimLow(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLimLow target = null;
            target = (CTLimLow)this.get_store().insert_element_user(PROPERTY_QNAME[40], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLimLow addNewLimLow() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLimLow target = null;
            target = (CTLimLow)this.get_store().add_element_user(PROPERTY_QNAME[40]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeLimLow(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[40], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTLimUpp> getLimUppList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTLimUpp>(this::getLimUppArray, this::setLimUppArray, this::insertNewLimUpp, this::removeLimUpp, this::sizeOfLimUppArray);
        }
    }

    @Override
    public CTLimUpp[] getLimUppArray() {
        return (CTLimUpp[])this.getXmlObjectArray(PROPERTY_QNAME[41], (XmlObject[])new CTLimUpp[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLimUpp getLimUppArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLimUpp target = null;
            target = (CTLimUpp)this.get_store().find_element_user(PROPERTY_QNAME[41], i);
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
    public int sizeOfLimUppArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[41]);
        }
    }

    @Override
    public void setLimUppArray(CTLimUpp[] limUppArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])limUppArray, PROPERTY_QNAME[41]);
    }

    @Override
    public void setLimUppArray(int i, CTLimUpp limUpp) {
        this.generatedSetterHelperImpl((XmlObject)limUpp, PROPERTY_QNAME[41], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLimUpp insertNewLimUpp(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLimUpp target = null;
            target = (CTLimUpp)this.get_store().insert_element_user(PROPERTY_QNAME[41], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLimUpp addNewLimUpp() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLimUpp target = null;
            target = (CTLimUpp)this.get_store().add_element_user(PROPERTY_QNAME[41]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeLimUpp(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[41], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTM> getMList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTM>(this::getMArray, this::setMArray, this::insertNewM, this::removeM, this::sizeOfMArray);
        }
    }

    @Override
    public CTM[] getMArray() {
        return (CTM[])this.getXmlObjectArray(PROPERTY_QNAME[42], new CTM[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTM getMArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTM target = null;
            target = (CTM)((Object)this.get_store().find_element_user(PROPERTY_QNAME[42], i));
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
    public int sizeOfMArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[42]);
        }
    }

    @Override
    public void setMArray(CTM[] mArray) {
        this.check_orphaned();
        this.arraySetterHelper(mArray, PROPERTY_QNAME[42]);
    }

    @Override
    public void setMArray(int i, CTM m) {
        this.generatedSetterHelperImpl(m, PROPERTY_QNAME[42], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTM insertNewM(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTM target = null;
            target = (CTM)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[42], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTM addNewM() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTM target = null;
            target = (CTM)((Object)this.get_store().add_element_user(PROPERTY_QNAME[42]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeM(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[42], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTNary> getNaryList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTNary>(this::getNaryArray, this::setNaryArray, this::insertNewNary, this::removeNary, this::sizeOfNaryArray);
        }
    }

    @Override
    public CTNary[] getNaryArray() {
        return (CTNary[])this.getXmlObjectArray(PROPERTY_QNAME[43], (XmlObject[])new CTNary[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTNary getNaryArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTNary target = null;
            target = (CTNary)this.get_store().find_element_user(PROPERTY_QNAME[43], i);
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
    public int sizeOfNaryArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[43]);
        }
    }

    @Override
    public void setNaryArray(CTNary[] naryArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])naryArray, PROPERTY_QNAME[43]);
    }

    @Override
    public void setNaryArray(int i, CTNary nary) {
        this.generatedSetterHelperImpl((XmlObject)nary, PROPERTY_QNAME[43], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTNary insertNewNary(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTNary target = null;
            target = (CTNary)this.get_store().insert_element_user(PROPERTY_QNAME[43], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTNary addNewNary() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTNary target = null;
            target = (CTNary)this.get_store().add_element_user(PROPERTY_QNAME[43]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeNary(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[43], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTPhant> getPhantList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTPhant>(this::getPhantArray, this::setPhantArray, this::insertNewPhant, this::removePhant, this::sizeOfPhantArray);
        }
    }

    @Override
    public CTPhant[] getPhantArray() {
        return (CTPhant[])this.getXmlObjectArray(PROPERTY_QNAME[44], (XmlObject[])new CTPhant[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPhant getPhantArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPhant target = null;
            target = (CTPhant)this.get_store().find_element_user(PROPERTY_QNAME[44], i);
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
    public int sizeOfPhantArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[44]);
        }
    }

    @Override
    public void setPhantArray(CTPhant[] phantArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])phantArray, PROPERTY_QNAME[44]);
    }

    @Override
    public void setPhantArray(int i, CTPhant phant) {
        this.generatedSetterHelperImpl((XmlObject)phant, PROPERTY_QNAME[44], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPhant insertNewPhant(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPhant target = null;
            target = (CTPhant)this.get_store().insert_element_user(PROPERTY_QNAME[44], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPhant addNewPhant() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPhant target = null;
            target = (CTPhant)this.get_store().add_element_user(PROPERTY_QNAME[44]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removePhant(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[44], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTRad> getRadList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTRad>(this::getRadArray, this::setRadArray, this::insertNewRad, this::removeRad, this::sizeOfRadArray);
        }
    }

    @Override
    public CTRad[] getRadArray() {
        return (CTRad[])this.getXmlObjectArray(PROPERTY_QNAME[45], (XmlObject[])new CTRad[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRad getRadArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRad target = null;
            target = (CTRad)this.get_store().find_element_user(PROPERTY_QNAME[45], i);
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
    public int sizeOfRadArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[45]);
        }
    }

    @Override
    public void setRadArray(CTRad[] radArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])radArray, PROPERTY_QNAME[45]);
    }

    @Override
    public void setRadArray(int i, CTRad rad) {
        this.generatedSetterHelperImpl((XmlObject)rad, PROPERTY_QNAME[45], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRad insertNewRad(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRad target = null;
            target = (CTRad)this.get_store().insert_element_user(PROPERTY_QNAME[45], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRad addNewRad() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRad target = null;
            target = (CTRad)this.get_store().add_element_user(PROPERTY_QNAME[45]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeRad(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[45], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTSPre> getSPreList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTSPre>(this::getSPreArray, this::setSPreArray, this::insertNewSPre, this::removeSPre, this::sizeOfSPreArray);
        }
    }

    @Override
    public CTSPre[] getSPreArray() {
        return (CTSPre[])this.getXmlObjectArray(PROPERTY_QNAME[46], (XmlObject[])new CTSPre[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSPre getSPreArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSPre target = null;
            target = (CTSPre)this.get_store().find_element_user(PROPERTY_QNAME[46], i);
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
    public int sizeOfSPreArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[46]);
        }
    }

    @Override
    public void setSPreArray(CTSPre[] sPreArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])sPreArray, PROPERTY_QNAME[46]);
    }

    @Override
    public void setSPreArray(int i, CTSPre sPre) {
        this.generatedSetterHelperImpl((XmlObject)sPre, PROPERTY_QNAME[46], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSPre insertNewSPre(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSPre target = null;
            target = (CTSPre)this.get_store().insert_element_user(PROPERTY_QNAME[46], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSPre addNewSPre() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSPre target = null;
            target = (CTSPre)this.get_store().add_element_user(PROPERTY_QNAME[46]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSPre(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[46], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTSSub> getSSubList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTSSub>(this::getSSubArray, this::setSSubArray, this::insertNewSSub, this::removeSSub, this::sizeOfSSubArray);
        }
    }

    @Override
    public CTSSub[] getSSubArray() {
        return (CTSSub[])this.getXmlObjectArray(PROPERTY_QNAME[47], new CTSSub[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSSub getSSubArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSSub target = null;
            target = (CTSSub)((Object)this.get_store().find_element_user(PROPERTY_QNAME[47], i));
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
    public int sizeOfSSubArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[47]);
        }
    }

    @Override
    public void setSSubArray(CTSSub[] sSubArray) {
        this.check_orphaned();
        this.arraySetterHelper(sSubArray, PROPERTY_QNAME[47]);
    }

    @Override
    public void setSSubArray(int i, CTSSub sSub) {
        this.generatedSetterHelperImpl(sSub, PROPERTY_QNAME[47], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSSub insertNewSSub(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSSub target = null;
            target = (CTSSub)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[47], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSSub addNewSSub() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSSub target = null;
            target = (CTSSub)((Object)this.get_store().add_element_user(PROPERTY_QNAME[47]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSSub(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[47], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTSSubSup> getSSubSupList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTSSubSup>(this::getSSubSupArray, this::setSSubSupArray, this::insertNewSSubSup, this::removeSSubSup, this::sizeOfSSubSupArray);
        }
    }

    @Override
    public CTSSubSup[] getSSubSupArray() {
        return (CTSSubSup[])this.getXmlObjectArray(PROPERTY_QNAME[48], (XmlObject[])new CTSSubSup[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSSubSup getSSubSupArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSSubSup target = null;
            target = (CTSSubSup)this.get_store().find_element_user(PROPERTY_QNAME[48], i);
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
    public int sizeOfSSubSupArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[48]);
        }
    }

    @Override
    public void setSSubSupArray(CTSSubSup[] sSubSupArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])sSubSupArray, PROPERTY_QNAME[48]);
    }

    @Override
    public void setSSubSupArray(int i, CTSSubSup sSubSup) {
        this.generatedSetterHelperImpl((XmlObject)sSubSup, PROPERTY_QNAME[48], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSSubSup insertNewSSubSup(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSSubSup target = null;
            target = (CTSSubSup)this.get_store().insert_element_user(PROPERTY_QNAME[48], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSSubSup addNewSSubSup() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSSubSup target = null;
            target = (CTSSubSup)this.get_store().add_element_user(PROPERTY_QNAME[48]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSSubSup(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[48], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTSSup> getSSupList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTSSup>(this::getSSupArray, this::setSSupArray, this::insertNewSSup, this::removeSSup, this::sizeOfSSupArray);
        }
    }

    @Override
    public CTSSup[] getSSupArray() {
        return (CTSSup[])this.getXmlObjectArray(PROPERTY_QNAME[49], (XmlObject[])new CTSSup[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSSup getSSupArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSSup target = null;
            target = (CTSSup)this.get_store().find_element_user(PROPERTY_QNAME[49], i);
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
    public int sizeOfSSupArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[49]);
        }
    }

    @Override
    public void setSSupArray(CTSSup[] sSupArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])sSupArray, PROPERTY_QNAME[49]);
    }

    @Override
    public void setSSupArray(int i, CTSSup sSup) {
        this.generatedSetterHelperImpl((XmlObject)sSup, PROPERTY_QNAME[49], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSSup insertNewSSup(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSSup target = null;
            target = (CTSSup)this.get_store().insert_element_user(PROPERTY_QNAME[49], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSSup addNewSSup() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSSup target = null;
            target = (CTSSup)this.get_store().add_element_user(PROPERTY_QNAME[49]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSSup(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[49], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<org.openxmlformats.schemas.officeDocument.x2006.math.CTR> getR2List() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<org.openxmlformats.schemas.officeDocument.x2006.math.CTR>(this::getR2Array, this::setR2Array, this::insertNewR2, this::removeR2, this::sizeOfR2Array);
        }
    }

    @Override
    public org.openxmlformats.schemas.officeDocument.x2006.math.CTR[] getR2Array() {
        return (org.openxmlformats.schemas.officeDocument.x2006.math.CTR[])this.getXmlObjectArray(PROPERTY_QNAME[50], new org.openxmlformats.schemas.officeDocument.x2006.math.CTR[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public org.openxmlformats.schemas.officeDocument.x2006.math.CTR getR2Array(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            org.openxmlformats.schemas.officeDocument.x2006.math.CTR target = null;
            target = (org.openxmlformats.schemas.officeDocument.x2006.math.CTR)((Object)this.get_store().find_element_user(PROPERTY_QNAME[50], i));
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
    public int sizeOfR2Array() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[50]);
        }
    }

    @Override
    public void setR2Array(org.openxmlformats.schemas.officeDocument.x2006.math.CTR[] r2Array) {
        this.check_orphaned();
        this.arraySetterHelper(r2Array, PROPERTY_QNAME[50]);
    }

    @Override
    public void setR2Array(int i, org.openxmlformats.schemas.officeDocument.x2006.math.CTR r2) {
        this.generatedSetterHelperImpl(r2, PROPERTY_QNAME[50], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public org.openxmlformats.schemas.officeDocument.x2006.math.CTR insertNewR2(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            org.openxmlformats.schemas.officeDocument.x2006.math.CTR target = null;
            target = (org.openxmlformats.schemas.officeDocument.x2006.math.CTR)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[50], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public org.openxmlformats.schemas.officeDocument.x2006.math.CTR addNewR2() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            org.openxmlformats.schemas.officeDocument.x2006.math.CTR target = null;
            target = (org.openxmlformats.schemas.officeDocument.x2006.math.CTR)((Object)this.get_store().add_element_user(PROPERTY_QNAME[50]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeR2(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[50], i);
        }
    }
}

