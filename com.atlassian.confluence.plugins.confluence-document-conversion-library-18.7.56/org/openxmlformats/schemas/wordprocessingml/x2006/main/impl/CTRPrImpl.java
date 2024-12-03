/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTEastAsianLayout
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTextEffect
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTColor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTEastAsianLayout;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTEm;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFitText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHighlight;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHpsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLanguage;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPrChange;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSignedHpsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSignedTwipsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTextEffect;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTextScale;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTUnderline;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVerticalAlignRun;

public class CTRPrImpl
extends XmlComplexContentImpl
implements CTRPr {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rStyle"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rFonts"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "b"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bCs"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "i"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "iCs"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "caps"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "smallCaps"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "strike"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "dstrike"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "outline"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "shadow"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "emboss"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "imprint"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "noProof"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "snapToGrid"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "vanish"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "webHidden"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "color"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "spacing"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "w"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "kern"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "position"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "sz"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "szCs"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "highlight"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "u"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "effect"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bdr"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "shd"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "fitText"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "vertAlign"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rtl"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "cs"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "em"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "lang"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "eastAsianLayout"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "specVanish"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "oMath"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rPrChange")};

    public CTRPrImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTString> getRStyleList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTString>(this::getRStyleArray, this::setRStyleArray, this::insertNewRStyle, this::removeRStyle, this::sizeOfRStyleArray);
        }
    }

    @Override
    public CTString[] getRStyleArray() {
        return (CTString[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTString[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTString getRStyleArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTString target = null;
            target = (CTString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfRStyleArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setRStyleArray(CTString[] rStyleArray) {
        this.check_orphaned();
        this.arraySetterHelper(rStyleArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setRStyleArray(int i, CTString rStyle) {
        this.generatedSetterHelperImpl(rStyle, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTString insertNewRStyle(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTString target = null;
            target = (CTString)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTString addNewRStyle() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTString target = null;
            target = (CTString)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeRStyle(int i) {
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
    public List<CTFonts> getRFontsList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTFonts>(this::getRFontsArray, this::setRFontsArray, this::insertNewRFonts, this::removeRFonts, this::sizeOfRFontsArray);
        }
    }

    @Override
    public CTFonts[] getRFontsArray() {
        return (CTFonts[])this.getXmlObjectArray(PROPERTY_QNAME[1], new CTFonts[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFonts getRFontsArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFonts target = null;
            target = (CTFonts)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], i));
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
    public int sizeOfRFontsArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]);
        }
    }

    @Override
    public void setRFontsArray(CTFonts[] rFontsArray) {
        this.check_orphaned();
        this.arraySetterHelper(rFontsArray, PROPERTY_QNAME[1]);
    }

    @Override
    public void setRFontsArray(int i, CTFonts rFonts) {
        this.generatedSetterHelperImpl(rFonts, PROPERTY_QNAME[1], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFonts insertNewRFonts(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFonts target = null;
            target = (CTFonts)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[1], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFonts addNewRFonts() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFonts target = null;
            target = (CTFonts)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeRFonts(int i) {
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
    public List<CTOnOff> getBList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTOnOff>(this::getBArray, this::setBArray, this::insertNewB, this::removeB, this::sizeOfBArray);
        }
    }

    @Override
    public CTOnOff[] getBArray() {
        return (CTOnOff[])this.getXmlObjectArray(PROPERTY_QNAME[2], new CTOnOff[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getBArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], i));
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
    public int sizeOfBArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]);
        }
    }

    @Override
    public void setBArray(CTOnOff[] bArray) {
        this.check_orphaned();
        this.arraySetterHelper(bArray, PROPERTY_QNAME[2]);
    }

    @Override
    public void setBArray(int i, CTOnOff b) {
        this.generatedSetterHelperImpl(b, PROPERTY_QNAME[2], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff insertNewB(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[2], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewB() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeB(int i) {
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
    public List<CTOnOff> getBCsList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTOnOff>(this::getBCsArray, this::setBCsArray, this::insertNewBCs, this::removeBCs, this::sizeOfBCsArray);
        }
    }

    @Override
    public CTOnOff[] getBCsArray() {
        return (CTOnOff[])this.getXmlObjectArray(PROPERTY_QNAME[3], new CTOnOff[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getBCsArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], i));
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
    public int sizeOfBCsArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]);
        }
    }

    @Override
    public void setBCsArray(CTOnOff[] bCsArray) {
        this.check_orphaned();
        this.arraySetterHelper(bCsArray, PROPERTY_QNAME[3]);
    }

    @Override
    public void setBCsArray(int i, CTOnOff bCs) {
        this.generatedSetterHelperImpl(bCs, PROPERTY_QNAME[3], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff insertNewBCs(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[3], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewBCs() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeBCs(int i) {
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
    public List<CTOnOff> getIList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTOnOff>(this::getIArray, this::setIArray, this::insertNewI, this::removeI, this::sizeOfIArray);
        }
    }

    @Override
    public CTOnOff[] getIArray() {
        return (CTOnOff[])this.getXmlObjectArray(PROPERTY_QNAME[4], new CTOnOff[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getIArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], i));
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
    public int sizeOfIArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[4]);
        }
    }

    @Override
    public void setIArray(CTOnOff[] iValueArray) {
        this.check_orphaned();
        this.arraySetterHelper(iValueArray, PROPERTY_QNAME[4]);
    }

    @Override
    public void setIArray(int i, CTOnOff iValue) {
        this.generatedSetterHelperImpl(iValue, PROPERTY_QNAME[4], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff insertNewI(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[4], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewI() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeI(int i) {
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
    public List<CTOnOff> getICsList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTOnOff>(this::getICsArray, this::setICsArray, this::insertNewICs, this::removeICs, this::sizeOfICsArray);
        }
    }

    @Override
    public CTOnOff[] getICsArray() {
        return (CTOnOff[])this.getXmlObjectArray(PROPERTY_QNAME[5], new CTOnOff[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getICsArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[5], i));
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
    public int sizeOfICsArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[5]);
        }
    }

    @Override
    public void setICsArray(CTOnOff[] iCsArray) {
        this.check_orphaned();
        this.arraySetterHelper(iCsArray, PROPERTY_QNAME[5]);
    }

    @Override
    public void setICsArray(int i, CTOnOff iCs) {
        this.generatedSetterHelperImpl(iCs, PROPERTY_QNAME[5], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff insertNewICs(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[5], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewICs() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[5]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeICs(int i) {
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
    public List<CTOnOff> getCapsList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTOnOff>(this::getCapsArray, this::setCapsArray, this::insertNewCaps, this::removeCaps, this::sizeOfCapsArray);
        }
    }

    @Override
    public CTOnOff[] getCapsArray() {
        return (CTOnOff[])this.getXmlObjectArray(PROPERTY_QNAME[6], new CTOnOff[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getCapsArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[6], i));
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
    public int sizeOfCapsArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[6]);
        }
    }

    @Override
    public void setCapsArray(CTOnOff[] capsArray) {
        this.check_orphaned();
        this.arraySetterHelper(capsArray, PROPERTY_QNAME[6]);
    }

    @Override
    public void setCapsArray(int i, CTOnOff caps) {
        this.generatedSetterHelperImpl(caps, PROPERTY_QNAME[6], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff insertNewCaps(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[6], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewCaps() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[6]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCaps(int i) {
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
    public List<CTOnOff> getSmallCapsList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTOnOff>(this::getSmallCapsArray, this::setSmallCapsArray, this::insertNewSmallCaps, this::removeSmallCaps, this::sizeOfSmallCapsArray);
        }
    }

    @Override
    public CTOnOff[] getSmallCapsArray() {
        return (CTOnOff[])this.getXmlObjectArray(PROPERTY_QNAME[7], new CTOnOff[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getSmallCapsArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[7], i));
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
    public int sizeOfSmallCapsArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[7]);
        }
    }

    @Override
    public void setSmallCapsArray(CTOnOff[] smallCapsArray) {
        this.check_orphaned();
        this.arraySetterHelper(smallCapsArray, PROPERTY_QNAME[7]);
    }

    @Override
    public void setSmallCapsArray(int i, CTOnOff smallCaps) {
        this.generatedSetterHelperImpl(smallCaps, PROPERTY_QNAME[7], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff insertNewSmallCaps(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[7], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewSmallCaps() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[7]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSmallCaps(int i) {
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
    public List<CTOnOff> getStrikeList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTOnOff>(this::getStrikeArray, this::setStrikeArray, this::insertNewStrike, this::removeStrike, this::sizeOfStrikeArray);
        }
    }

    @Override
    public CTOnOff[] getStrikeArray() {
        return (CTOnOff[])this.getXmlObjectArray(PROPERTY_QNAME[8], new CTOnOff[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getStrikeArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[8], i));
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
    public int sizeOfStrikeArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[8]);
        }
    }

    @Override
    public void setStrikeArray(CTOnOff[] strikeArray) {
        this.check_orphaned();
        this.arraySetterHelper(strikeArray, PROPERTY_QNAME[8]);
    }

    @Override
    public void setStrikeArray(int i, CTOnOff strike) {
        this.generatedSetterHelperImpl(strike, PROPERTY_QNAME[8], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff insertNewStrike(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[8], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewStrike() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[8]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeStrike(int i) {
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
    public List<CTOnOff> getDstrikeList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTOnOff>(this::getDstrikeArray, this::setDstrikeArray, this::insertNewDstrike, this::removeDstrike, this::sizeOfDstrikeArray);
        }
    }

    @Override
    public CTOnOff[] getDstrikeArray() {
        return (CTOnOff[])this.getXmlObjectArray(PROPERTY_QNAME[9], new CTOnOff[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getDstrikeArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[9], i));
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
    public int sizeOfDstrikeArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[9]);
        }
    }

    @Override
    public void setDstrikeArray(CTOnOff[] dstrikeArray) {
        this.check_orphaned();
        this.arraySetterHelper(dstrikeArray, PROPERTY_QNAME[9]);
    }

    @Override
    public void setDstrikeArray(int i, CTOnOff dstrike) {
        this.generatedSetterHelperImpl(dstrike, PROPERTY_QNAME[9], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff insertNewDstrike(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[9], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewDstrike() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[9]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeDstrike(int i) {
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
    public List<CTOnOff> getOutlineList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTOnOff>(this::getOutlineArray, this::setOutlineArray, this::insertNewOutline, this::removeOutline, this::sizeOfOutlineArray);
        }
    }

    @Override
    public CTOnOff[] getOutlineArray() {
        return (CTOnOff[])this.getXmlObjectArray(PROPERTY_QNAME[10], new CTOnOff[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getOutlineArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[10], i));
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
    public int sizeOfOutlineArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[10]);
        }
    }

    @Override
    public void setOutlineArray(CTOnOff[] outlineArray) {
        this.check_orphaned();
        this.arraySetterHelper(outlineArray, PROPERTY_QNAME[10]);
    }

    @Override
    public void setOutlineArray(int i, CTOnOff outline) {
        this.generatedSetterHelperImpl(outline, PROPERTY_QNAME[10], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff insertNewOutline(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[10], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewOutline() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[10]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeOutline(int i) {
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
    public List<CTOnOff> getShadowList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTOnOff>(this::getShadowArray, this::setShadowArray, this::insertNewShadow, this::removeShadow, this::sizeOfShadowArray);
        }
    }

    @Override
    public CTOnOff[] getShadowArray() {
        return (CTOnOff[])this.getXmlObjectArray(PROPERTY_QNAME[11], new CTOnOff[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getShadowArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[11], i));
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
    public int sizeOfShadowArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[11]);
        }
    }

    @Override
    public void setShadowArray(CTOnOff[] shadowArray) {
        this.check_orphaned();
        this.arraySetterHelper(shadowArray, PROPERTY_QNAME[11]);
    }

    @Override
    public void setShadowArray(int i, CTOnOff shadow) {
        this.generatedSetterHelperImpl(shadow, PROPERTY_QNAME[11], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff insertNewShadow(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[11], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewShadow() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[11]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeShadow(int i) {
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
    public List<CTOnOff> getEmbossList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTOnOff>(this::getEmbossArray, this::setEmbossArray, this::insertNewEmboss, this::removeEmboss, this::sizeOfEmbossArray);
        }
    }

    @Override
    public CTOnOff[] getEmbossArray() {
        return (CTOnOff[])this.getXmlObjectArray(PROPERTY_QNAME[12], new CTOnOff[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getEmbossArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[12], i));
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
    public int sizeOfEmbossArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[12]);
        }
    }

    @Override
    public void setEmbossArray(CTOnOff[] embossArray) {
        this.check_orphaned();
        this.arraySetterHelper(embossArray, PROPERTY_QNAME[12]);
    }

    @Override
    public void setEmbossArray(int i, CTOnOff emboss) {
        this.generatedSetterHelperImpl(emboss, PROPERTY_QNAME[12], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff insertNewEmboss(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[12], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewEmboss() {
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
    public void removeEmboss(int i) {
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
    public List<CTOnOff> getImprintList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTOnOff>(this::getImprintArray, this::setImprintArray, this::insertNewImprint, this::removeImprint, this::sizeOfImprintArray);
        }
    }

    @Override
    public CTOnOff[] getImprintArray() {
        return (CTOnOff[])this.getXmlObjectArray(PROPERTY_QNAME[13], new CTOnOff[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getImprintArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[13], i));
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
    public int sizeOfImprintArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[13]);
        }
    }

    @Override
    public void setImprintArray(CTOnOff[] imprintArray) {
        this.check_orphaned();
        this.arraySetterHelper(imprintArray, PROPERTY_QNAME[13]);
    }

    @Override
    public void setImprintArray(int i, CTOnOff imprint) {
        this.generatedSetterHelperImpl(imprint, PROPERTY_QNAME[13], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff insertNewImprint(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[13], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewImprint() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[13]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeImprint(int i) {
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
    public List<CTOnOff> getNoProofList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTOnOff>(this::getNoProofArray, this::setNoProofArray, this::insertNewNoProof, this::removeNoProof, this::sizeOfNoProofArray);
        }
    }

    @Override
    public CTOnOff[] getNoProofArray() {
        return (CTOnOff[])this.getXmlObjectArray(PROPERTY_QNAME[14], new CTOnOff[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getNoProofArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[14], i));
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
    public int sizeOfNoProofArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[14]);
        }
    }

    @Override
    public void setNoProofArray(CTOnOff[] noProofArray) {
        this.check_orphaned();
        this.arraySetterHelper(noProofArray, PROPERTY_QNAME[14]);
    }

    @Override
    public void setNoProofArray(int i, CTOnOff noProof) {
        this.generatedSetterHelperImpl(noProof, PROPERTY_QNAME[14], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff insertNewNoProof(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[14], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewNoProof() {
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
    public void removeNoProof(int i) {
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
    public List<CTOnOff> getSnapToGridList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTOnOff>(this::getSnapToGridArray, this::setSnapToGridArray, this::insertNewSnapToGrid, this::removeSnapToGrid, this::sizeOfSnapToGridArray);
        }
    }

    @Override
    public CTOnOff[] getSnapToGridArray() {
        return (CTOnOff[])this.getXmlObjectArray(PROPERTY_QNAME[15], new CTOnOff[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getSnapToGridArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[15], i));
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
    public int sizeOfSnapToGridArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[15]);
        }
    }

    @Override
    public void setSnapToGridArray(CTOnOff[] snapToGridArray) {
        this.check_orphaned();
        this.arraySetterHelper(snapToGridArray, PROPERTY_QNAME[15]);
    }

    @Override
    public void setSnapToGridArray(int i, CTOnOff snapToGrid) {
        this.generatedSetterHelperImpl(snapToGrid, PROPERTY_QNAME[15], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff insertNewSnapToGrid(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[15], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewSnapToGrid() {
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
    public void removeSnapToGrid(int i) {
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
    public List<CTOnOff> getVanishList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTOnOff>(this::getVanishArray, this::setVanishArray, this::insertNewVanish, this::removeVanish, this::sizeOfVanishArray);
        }
    }

    @Override
    public CTOnOff[] getVanishArray() {
        return (CTOnOff[])this.getXmlObjectArray(PROPERTY_QNAME[16], new CTOnOff[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getVanishArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[16], i));
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
    public int sizeOfVanishArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[16]);
        }
    }

    @Override
    public void setVanishArray(CTOnOff[] vanishArray) {
        this.check_orphaned();
        this.arraySetterHelper(vanishArray, PROPERTY_QNAME[16]);
    }

    @Override
    public void setVanishArray(int i, CTOnOff vanish) {
        this.generatedSetterHelperImpl(vanish, PROPERTY_QNAME[16], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff insertNewVanish(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[16], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewVanish() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[16]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeVanish(int i) {
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
    public List<CTOnOff> getWebHiddenList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTOnOff>(this::getWebHiddenArray, this::setWebHiddenArray, this::insertNewWebHidden, this::removeWebHidden, this::sizeOfWebHiddenArray);
        }
    }

    @Override
    public CTOnOff[] getWebHiddenArray() {
        return (CTOnOff[])this.getXmlObjectArray(PROPERTY_QNAME[17], new CTOnOff[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getWebHiddenArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[17], i));
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
    public int sizeOfWebHiddenArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[17]);
        }
    }

    @Override
    public void setWebHiddenArray(CTOnOff[] webHiddenArray) {
        this.check_orphaned();
        this.arraySetterHelper(webHiddenArray, PROPERTY_QNAME[17]);
    }

    @Override
    public void setWebHiddenArray(int i, CTOnOff webHidden) {
        this.generatedSetterHelperImpl(webHidden, PROPERTY_QNAME[17], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff insertNewWebHidden(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[17], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewWebHidden() {
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
    public void removeWebHidden(int i) {
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
    public List<CTColor> getColorList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTColor>(this::getColorArray, this::setColorArray, this::insertNewColor, this::removeColor, this::sizeOfColorArray);
        }
    }

    @Override
    public CTColor[] getColorArray() {
        return (CTColor[])this.getXmlObjectArray(PROPERTY_QNAME[18], new CTColor[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColor getColorArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColor target = null;
            target = (CTColor)((Object)this.get_store().find_element_user(PROPERTY_QNAME[18], i));
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
    public int sizeOfColorArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[18]);
        }
    }

    @Override
    public void setColorArray(CTColor[] colorArray) {
        this.check_orphaned();
        this.arraySetterHelper(colorArray, PROPERTY_QNAME[18]);
    }

    @Override
    public void setColorArray(int i, CTColor color) {
        this.generatedSetterHelperImpl(color, PROPERTY_QNAME[18], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColor insertNewColor(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColor target = null;
            target = (CTColor)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[18], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColor addNewColor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColor target = null;
            target = (CTColor)((Object)this.get_store().add_element_user(PROPERTY_QNAME[18]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeColor(int i) {
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
    public List<CTSignedTwipsMeasure> getSpacingList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTSignedTwipsMeasure>(this::getSpacingArray, this::setSpacingArray, this::insertNewSpacing, this::removeSpacing, this::sizeOfSpacingArray);
        }
    }

    @Override
    public CTSignedTwipsMeasure[] getSpacingArray() {
        return (CTSignedTwipsMeasure[])this.getXmlObjectArray(PROPERTY_QNAME[19], new CTSignedTwipsMeasure[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSignedTwipsMeasure getSpacingArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSignedTwipsMeasure target = null;
            target = (CTSignedTwipsMeasure)((Object)this.get_store().find_element_user(PROPERTY_QNAME[19], i));
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
    public int sizeOfSpacingArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[19]);
        }
    }

    @Override
    public void setSpacingArray(CTSignedTwipsMeasure[] spacingArray) {
        this.check_orphaned();
        this.arraySetterHelper(spacingArray, PROPERTY_QNAME[19]);
    }

    @Override
    public void setSpacingArray(int i, CTSignedTwipsMeasure spacing) {
        this.generatedSetterHelperImpl(spacing, PROPERTY_QNAME[19], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSignedTwipsMeasure insertNewSpacing(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSignedTwipsMeasure target = null;
            target = (CTSignedTwipsMeasure)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[19], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSignedTwipsMeasure addNewSpacing() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSignedTwipsMeasure target = null;
            target = (CTSignedTwipsMeasure)((Object)this.get_store().add_element_user(PROPERTY_QNAME[19]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSpacing(int i) {
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
    public List<CTTextScale> getWList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTextScale>(this::getWArray, this::setWArray, this::insertNewW, this::removeW, this::sizeOfWArray);
        }
    }

    @Override
    public CTTextScale[] getWArray() {
        return (CTTextScale[])this.getXmlObjectArray(PROPERTY_QNAME[20], new CTTextScale[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextScale getWArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextScale target = null;
            target = (CTTextScale)((Object)this.get_store().find_element_user(PROPERTY_QNAME[20], i));
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
    public int sizeOfWArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[20]);
        }
    }

    @Override
    public void setWArray(CTTextScale[] wArray) {
        this.check_orphaned();
        this.arraySetterHelper(wArray, PROPERTY_QNAME[20]);
    }

    @Override
    public void setWArray(int i, CTTextScale w) {
        this.generatedSetterHelperImpl(w, PROPERTY_QNAME[20], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextScale insertNewW(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextScale target = null;
            target = (CTTextScale)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[20], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextScale addNewW() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextScale target = null;
            target = (CTTextScale)((Object)this.get_store().add_element_user(PROPERTY_QNAME[20]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeW(int i) {
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
    public List<CTHpsMeasure> getKernList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTHpsMeasure>(this::getKernArray, this::setKernArray, this::insertNewKern, this::removeKern, this::sizeOfKernArray);
        }
    }

    @Override
    public CTHpsMeasure[] getKernArray() {
        return (CTHpsMeasure[])this.getXmlObjectArray(PROPERTY_QNAME[21], new CTHpsMeasure[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHpsMeasure getKernArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHpsMeasure target = null;
            target = (CTHpsMeasure)((Object)this.get_store().find_element_user(PROPERTY_QNAME[21], i));
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
    public int sizeOfKernArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[21]);
        }
    }

    @Override
    public void setKernArray(CTHpsMeasure[] kernArray) {
        this.check_orphaned();
        this.arraySetterHelper(kernArray, PROPERTY_QNAME[21]);
    }

    @Override
    public void setKernArray(int i, CTHpsMeasure kern) {
        this.generatedSetterHelperImpl(kern, PROPERTY_QNAME[21], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHpsMeasure insertNewKern(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHpsMeasure target = null;
            target = (CTHpsMeasure)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[21], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHpsMeasure addNewKern() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHpsMeasure target = null;
            target = (CTHpsMeasure)((Object)this.get_store().add_element_user(PROPERTY_QNAME[21]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeKern(int i) {
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
    public List<CTSignedHpsMeasure> getPositionList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTSignedHpsMeasure>(this::getPositionArray, this::setPositionArray, this::insertNewPosition, this::removePosition, this::sizeOfPositionArray);
        }
    }

    @Override
    public CTSignedHpsMeasure[] getPositionArray() {
        return (CTSignedHpsMeasure[])this.getXmlObjectArray(PROPERTY_QNAME[22], new CTSignedHpsMeasure[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSignedHpsMeasure getPositionArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSignedHpsMeasure target = null;
            target = (CTSignedHpsMeasure)((Object)this.get_store().find_element_user(PROPERTY_QNAME[22], i));
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
    public int sizeOfPositionArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[22]);
        }
    }

    @Override
    public void setPositionArray(CTSignedHpsMeasure[] positionArray) {
        this.check_orphaned();
        this.arraySetterHelper(positionArray, PROPERTY_QNAME[22]);
    }

    @Override
    public void setPositionArray(int i, CTSignedHpsMeasure position) {
        this.generatedSetterHelperImpl(position, PROPERTY_QNAME[22], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSignedHpsMeasure insertNewPosition(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSignedHpsMeasure target = null;
            target = (CTSignedHpsMeasure)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[22], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSignedHpsMeasure addNewPosition() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSignedHpsMeasure target = null;
            target = (CTSignedHpsMeasure)((Object)this.get_store().add_element_user(PROPERTY_QNAME[22]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removePosition(int i) {
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
    public List<CTHpsMeasure> getSzList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTHpsMeasure>(this::getSzArray, this::setSzArray, this::insertNewSz, this::removeSz, this::sizeOfSzArray);
        }
    }

    @Override
    public CTHpsMeasure[] getSzArray() {
        return (CTHpsMeasure[])this.getXmlObjectArray(PROPERTY_QNAME[23], new CTHpsMeasure[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHpsMeasure getSzArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHpsMeasure target = null;
            target = (CTHpsMeasure)((Object)this.get_store().find_element_user(PROPERTY_QNAME[23], i));
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
    public int sizeOfSzArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[23]);
        }
    }

    @Override
    public void setSzArray(CTHpsMeasure[] szArray) {
        this.check_orphaned();
        this.arraySetterHelper(szArray, PROPERTY_QNAME[23]);
    }

    @Override
    public void setSzArray(int i, CTHpsMeasure sz) {
        this.generatedSetterHelperImpl(sz, PROPERTY_QNAME[23], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHpsMeasure insertNewSz(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHpsMeasure target = null;
            target = (CTHpsMeasure)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[23], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHpsMeasure addNewSz() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHpsMeasure target = null;
            target = (CTHpsMeasure)((Object)this.get_store().add_element_user(PROPERTY_QNAME[23]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSz(int i) {
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
    public List<CTHpsMeasure> getSzCsList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTHpsMeasure>(this::getSzCsArray, this::setSzCsArray, this::insertNewSzCs, this::removeSzCs, this::sizeOfSzCsArray);
        }
    }

    @Override
    public CTHpsMeasure[] getSzCsArray() {
        return (CTHpsMeasure[])this.getXmlObjectArray(PROPERTY_QNAME[24], new CTHpsMeasure[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHpsMeasure getSzCsArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHpsMeasure target = null;
            target = (CTHpsMeasure)((Object)this.get_store().find_element_user(PROPERTY_QNAME[24], i));
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
    public int sizeOfSzCsArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[24]);
        }
    }

    @Override
    public void setSzCsArray(CTHpsMeasure[] szCsArray) {
        this.check_orphaned();
        this.arraySetterHelper(szCsArray, PROPERTY_QNAME[24]);
    }

    @Override
    public void setSzCsArray(int i, CTHpsMeasure szCs) {
        this.generatedSetterHelperImpl(szCs, PROPERTY_QNAME[24], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHpsMeasure insertNewSzCs(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHpsMeasure target = null;
            target = (CTHpsMeasure)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[24], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHpsMeasure addNewSzCs() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHpsMeasure target = null;
            target = (CTHpsMeasure)((Object)this.get_store().add_element_user(PROPERTY_QNAME[24]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSzCs(int i) {
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
    public List<CTHighlight> getHighlightList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTHighlight>(this::getHighlightArray, this::setHighlightArray, this::insertNewHighlight, this::removeHighlight, this::sizeOfHighlightArray);
        }
    }

    @Override
    public CTHighlight[] getHighlightArray() {
        return (CTHighlight[])this.getXmlObjectArray(PROPERTY_QNAME[25], new CTHighlight[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHighlight getHighlightArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHighlight target = null;
            target = (CTHighlight)((Object)this.get_store().find_element_user(PROPERTY_QNAME[25], i));
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
    public int sizeOfHighlightArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[25]);
        }
    }

    @Override
    public void setHighlightArray(CTHighlight[] highlightArray) {
        this.check_orphaned();
        this.arraySetterHelper(highlightArray, PROPERTY_QNAME[25]);
    }

    @Override
    public void setHighlightArray(int i, CTHighlight highlight) {
        this.generatedSetterHelperImpl(highlight, PROPERTY_QNAME[25], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHighlight insertNewHighlight(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHighlight target = null;
            target = (CTHighlight)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[25], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHighlight addNewHighlight() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHighlight target = null;
            target = (CTHighlight)((Object)this.get_store().add_element_user(PROPERTY_QNAME[25]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeHighlight(int i) {
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
    public List<CTUnderline> getUList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTUnderline>(this::getUArray, this::setUArray, this::insertNewU, this::removeU, this::sizeOfUArray);
        }
    }

    @Override
    public CTUnderline[] getUArray() {
        return (CTUnderline[])this.getXmlObjectArray(PROPERTY_QNAME[26], new CTUnderline[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTUnderline getUArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTUnderline target = null;
            target = (CTUnderline)((Object)this.get_store().find_element_user(PROPERTY_QNAME[26], i));
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
    public int sizeOfUArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[26]);
        }
    }

    @Override
    public void setUArray(CTUnderline[] uArray) {
        this.check_orphaned();
        this.arraySetterHelper(uArray, PROPERTY_QNAME[26]);
    }

    @Override
    public void setUArray(int i, CTUnderline u) {
        this.generatedSetterHelperImpl(u, PROPERTY_QNAME[26], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTUnderline insertNewU(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTUnderline target = null;
            target = (CTUnderline)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[26], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTUnderline addNewU() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTUnderline target = null;
            target = (CTUnderline)((Object)this.get_store().add_element_user(PROPERTY_QNAME[26]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeU(int i) {
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
    public List<CTTextEffect> getEffectList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTextEffect>(this::getEffectArray, this::setEffectArray, this::insertNewEffect, this::removeEffect, this::sizeOfEffectArray);
        }
    }

    @Override
    public CTTextEffect[] getEffectArray() {
        return (CTTextEffect[])this.getXmlObjectArray(PROPERTY_QNAME[27], (XmlObject[])new CTTextEffect[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextEffect getEffectArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextEffect target = null;
            target = (CTTextEffect)this.get_store().find_element_user(PROPERTY_QNAME[27], i);
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
    public int sizeOfEffectArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[27]);
        }
    }

    @Override
    public void setEffectArray(CTTextEffect[] effectArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])effectArray, PROPERTY_QNAME[27]);
    }

    @Override
    public void setEffectArray(int i, CTTextEffect effect) {
        this.generatedSetterHelperImpl((XmlObject)effect, PROPERTY_QNAME[27], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextEffect insertNewEffect(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextEffect target = null;
            target = (CTTextEffect)this.get_store().insert_element_user(PROPERTY_QNAME[27], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextEffect addNewEffect() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextEffect target = null;
            target = (CTTextEffect)this.get_store().add_element_user(PROPERTY_QNAME[27]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeEffect(int i) {
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
    public List<CTBorder> getBdrList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTBorder>(this::getBdrArray, this::setBdrArray, this::insertNewBdr, this::removeBdr, this::sizeOfBdrArray);
        }
    }

    @Override
    public CTBorder[] getBdrArray() {
        return (CTBorder[])this.getXmlObjectArray(PROPERTY_QNAME[28], new CTBorder[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBorder getBdrArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBorder target = null;
            target = (CTBorder)((Object)this.get_store().find_element_user(PROPERTY_QNAME[28], i));
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
    public int sizeOfBdrArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[28]);
        }
    }

    @Override
    public void setBdrArray(CTBorder[] bdrArray) {
        this.check_orphaned();
        this.arraySetterHelper(bdrArray, PROPERTY_QNAME[28]);
    }

    @Override
    public void setBdrArray(int i, CTBorder bdr) {
        this.generatedSetterHelperImpl(bdr, PROPERTY_QNAME[28], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBorder insertNewBdr(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBorder target = null;
            target = (CTBorder)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[28], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBorder addNewBdr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBorder target = null;
            target = (CTBorder)((Object)this.get_store().add_element_user(PROPERTY_QNAME[28]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeBdr(int i) {
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
    public List<CTShd> getShdList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTShd>(this::getShdArray, this::setShdArray, this::insertNewShd, this::removeShd, this::sizeOfShdArray);
        }
    }

    @Override
    public CTShd[] getShdArray() {
        return (CTShd[])this.getXmlObjectArray(PROPERTY_QNAME[29], new CTShd[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTShd getShdArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTShd target = null;
            target = (CTShd)((Object)this.get_store().find_element_user(PROPERTY_QNAME[29], i));
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
    public int sizeOfShdArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[29]);
        }
    }

    @Override
    public void setShdArray(CTShd[] shdArray) {
        this.check_orphaned();
        this.arraySetterHelper(shdArray, PROPERTY_QNAME[29]);
    }

    @Override
    public void setShdArray(int i, CTShd shd) {
        this.generatedSetterHelperImpl(shd, PROPERTY_QNAME[29], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTShd insertNewShd(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTShd target = null;
            target = (CTShd)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[29], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTShd addNewShd() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTShd target = null;
            target = (CTShd)((Object)this.get_store().add_element_user(PROPERTY_QNAME[29]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeShd(int i) {
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
    public List<CTFitText> getFitTextList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTFitText>(this::getFitTextArray, this::setFitTextArray, this::insertNewFitText, this::removeFitText, this::sizeOfFitTextArray);
        }
    }

    @Override
    public CTFitText[] getFitTextArray() {
        return (CTFitText[])this.getXmlObjectArray(PROPERTY_QNAME[30], new CTFitText[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFitText getFitTextArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFitText target = null;
            target = (CTFitText)((Object)this.get_store().find_element_user(PROPERTY_QNAME[30], i));
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
    public int sizeOfFitTextArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[30]);
        }
    }

    @Override
    public void setFitTextArray(CTFitText[] fitTextArray) {
        this.check_orphaned();
        this.arraySetterHelper(fitTextArray, PROPERTY_QNAME[30]);
    }

    @Override
    public void setFitTextArray(int i, CTFitText fitText) {
        this.generatedSetterHelperImpl(fitText, PROPERTY_QNAME[30], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFitText insertNewFitText(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFitText target = null;
            target = (CTFitText)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[30], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFitText addNewFitText() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFitText target = null;
            target = (CTFitText)((Object)this.get_store().add_element_user(PROPERTY_QNAME[30]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeFitText(int i) {
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
    public List<CTVerticalAlignRun> getVertAlignList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTVerticalAlignRun>(this::getVertAlignArray, this::setVertAlignArray, this::insertNewVertAlign, this::removeVertAlign, this::sizeOfVertAlignArray);
        }
    }

    @Override
    public CTVerticalAlignRun[] getVertAlignArray() {
        return (CTVerticalAlignRun[])this.getXmlObjectArray(PROPERTY_QNAME[31], new CTVerticalAlignRun[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTVerticalAlignRun getVertAlignArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTVerticalAlignRun target = null;
            target = (CTVerticalAlignRun)((Object)this.get_store().find_element_user(PROPERTY_QNAME[31], i));
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
    public int sizeOfVertAlignArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[31]);
        }
    }

    @Override
    public void setVertAlignArray(CTVerticalAlignRun[] vertAlignArray) {
        this.check_orphaned();
        this.arraySetterHelper(vertAlignArray, PROPERTY_QNAME[31]);
    }

    @Override
    public void setVertAlignArray(int i, CTVerticalAlignRun vertAlign) {
        this.generatedSetterHelperImpl(vertAlign, PROPERTY_QNAME[31], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTVerticalAlignRun insertNewVertAlign(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTVerticalAlignRun target = null;
            target = (CTVerticalAlignRun)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[31], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTVerticalAlignRun addNewVertAlign() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTVerticalAlignRun target = null;
            target = (CTVerticalAlignRun)((Object)this.get_store().add_element_user(PROPERTY_QNAME[31]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeVertAlign(int i) {
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
    public List<CTOnOff> getRtlList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTOnOff>(this::getRtlArray, this::setRtlArray, this::insertNewRtl, this::removeRtl, this::sizeOfRtlArray);
        }
    }

    @Override
    public CTOnOff[] getRtlArray() {
        return (CTOnOff[])this.getXmlObjectArray(PROPERTY_QNAME[32], new CTOnOff[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getRtlArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[32], i));
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
    public int sizeOfRtlArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[32]);
        }
    }

    @Override
    public void setRtlArray(CTOnOff[] rtlArray) {
        this.check_orphaned();
        this.arraySetterHelper(rtlArray, PROPERTY_QNAME[32]);
    }

    @Override
    public void setRtlArray(int i, CTOnOff rtl) {
        this.generatedSetterHelperImpl(rtl, PROPERTY_QNAME[32], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff insertNewRtl(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[32], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewRtl() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[32]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeRtl(int i) {
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
    public List<CTOnOff> getCsList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTOnOff>(this::getCsArray, this::setCsArray, this::insertNewCs, this::removeCs, this::sizeOfCsArray);
        }
    }

    @Override
    public CTOnOff[] getCsArray() {
        return (CTOnOff[])this.getXmlObjectArray(PROPERTY_QNAME[33], new CTOnOff[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getCsArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[33], i));
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
    public int sizeOfCsArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[33]);
        }
    }

    @Override
    public void setCsArray(CTOnOff[] csArray) {
        this.check_orphaned();
        this.arraySetterHelper(csArray, PROPERTY_QNAME[33]);
    }

    @Override
    public void setCsArray(int i, CTOnOff cs) {
        this.generatedSetterHelperImpl(cs, PROPERTY_QNAME[33], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff insertNewCs(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[33], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewCs() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[33]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCs(int i) {
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
    public List<CTEm> getEmList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTEm>(this::getEmArray, this::setEmArray, this::insertNewEm, this::removeEm, this::sizeOfEmArray);
        }
    }

    @Override
    public CTEm[] getEmArray() {
        return (CTEm[])this.getXmlObjectArray(PROPERTY_QNAME[34], new CTEm[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEm getEmArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEm target = null;
            target = (CTEm)((Object)this.get_store().find_element_user(PROPERTY_QNAME[34], i));
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
    public int sizeOfEmArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[34]);
        }
    }

    @Override
    public void setEmArray(CTEm[] emArray) {
        this.check_orphaned();
        this.arraySetterHelper(emArray, PROPERTY_QNAME[34]);
    }

    @Override
    public void setEmArray(int i, CTEm em) {
        this.generatedSetterHelperImpl(em, PROPERTY_QNAME[34], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEm insertNewEm(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEm target = null;
            target = (CTEm)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[34], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEm addNewEm() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEm target = null;
            target = (CTEm)((Object)this.get_store().add_element_user(PROPERTY_QNAME[34]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeEm(int i) {
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
    public List<CTLanguage> getLangList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTLanguage>(this::getLangArray, this::setLangArray, this::insertNewLang, this::removeLang, this::sizeOfLangArray);
        }
    }

    @Override
    public CTLanguage[] getLangArray() {
        return (CTLanguage[])this.getXmlObjectArray(PROPERTY_QNAME[35], new CTLanguage[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLanguage getLangArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLanguage target = null;
            target = (CTLanguage)((Object)this.get_store().find_element_user(PROPERTY_QNAME[35], i));
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
    public int sizeOfLangArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[35]);
        }
    }

    @Override
    public void setLangArray(CTLanguage[] langArray) {
        this.check_orphaned();
        this.arraySetterHelper(langArray, PROPERTY_QNAME[35]);
    }

    @Override
    public void setLangArray(int i, CTLanguage lang) {
        this.generatedSetterHelperImpl(lang, PROPERTY_QNAME[35], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLanguage insertNewLang(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLanguage target = null;
            target = (CTLanguage)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[35], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLanguage addNewLang() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLanguage target = null;
            target = (CTLanguage)((Object)this.get_store().add_element_user(PROPERTY_QNAME[35]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeLang(int i) {
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
    public List<CTEastAsianLayout> getEastAsianLayoutList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTEastAsianLayout>(this::getEastAsianLayoutArray, this::setEastAsianLayoutArray, this::insertNewEastAsianLayout, this::removeEastAsianLayout, this::sizeOfEastAsianLayoutArray);
        }
    }

    @Override
    public CTEastAsianLayout[] getEastAsianLayoutArray() {
        return (CTEastAsianLayout[])this.getXmlObjectArray(PROPERTY_QNAME[36], (XmlObject[])new CTEastAsianLayout[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEastAsianLayout getEastAsianLayoutArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEastAsianLayout target = null;
            target = (CTEastAsianLayout)this.get_store().find_element_user(PROPERTY_QNAME[36], i);
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
    public int sizeOfEastAsianLayoutArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[36]);
        }
    }

    @Override
    public void setEastAsianLayoutArray(CTEastAsianLayout[] eastAsianLayoutArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])eastAsianLayoutArray, PROPERTY_QNAME[36]);
    }

    @Override
    public void setEastAsianLayoutArray(int i, CTEastAsianLayout eastAsianLayout) {
        this.generatedSetterHelperImpl((XmlObject)eastAsianLayout, PROPERTY_QNAME[36], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEastAsianLayout insertNewEastAsianLayout(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEastAsianLayout target = null;
            target = (CTEastAsianLayout)this.get_store().insert_element_user(PROPERTY_QNAME[36], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEastAsianLayout addNewEastAsianLayout() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEastAsianLayout target = null;
            target = (CTEastAsianLayout)this.get_store().add_element_user(PROPERTY_QNAME[36]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeEastAsianLayout(int i) {
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
    public List<CTOnOff> getSpecVanishList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTOnOff>(this::getSpecVanishArray, this::setSpecVanishArray, this::insertNewSpecVanish, this::removeSpecVanish, this::sizeOfSpecVanishArray);
        }
    }

    @Override
    public CTOnOff[] getSpecVanishArray() {
        return (CTOnOff[])this.getXmlObjectArray(PROPERTY_QNAME[37], new CTOnOff[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getSpecVanishArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[37], i));
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
    public int sizeOfSpecVanishArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[37]);
        }
    }

    @Override
    public void setSpecVanishArray(CTOnOff[] specVanishArray) {
        this.check_orphaned();
        this.arraySetterHelper(specVanishArray, PROPERTY_QNAME[37]);
    }

    @Override
    public void setSpecVanishArray(int i, CTOnOff specVanish) {
        this.generatedSetterHelperImpl(specVanish, PROPERTY_QNAME[37], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff insertNewSpecVanish(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[37], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewSpecVanish() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[37]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSpecVanish(int i) {
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
    public List<CTOnOff> getOMathList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTOnOff>(this::getOMathArray, this::setOMathArray, this::insertNewOMath, this::removeOMath, this::sizeOfOMathArray);
        }
    }

    @Override
    public CTOnOff[] getOMathArray() {
        return (CTOnOff[])this.getXmlObjectArray(PROPERTY_QNAME[38], new CTOnOff[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getOMathArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[38], i));
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
            return this.get_store().count_elements(PROPERTY_QNAME[38]);
        }
    }

    @Override
    public void setOMathArray(CTOnOff[] oMathArray) {
        this.check_orphaned();
        this.arraySetterHelper(oMathArray, PROPERTY_QNAME[38]);
    }

    @Override
    public void setOMathArray(int i, CTOnOff oMath) {
        this.generatedSetterHelperImpl(oMath, PROPERTY_QNAME[38], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff insertNewOMath(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[38], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewOMath() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[38]));
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
            this.get_store().remove_element(PROPERTY_QNAME[38], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRPrChange getRPrChange() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRPrChange target = null;
            target = (CTRPrChange)((Object)this.get_store().find_element_user(PROPERTY_QNAME[39], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetRPrChange() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[39]) != 0;
        }
    }

    @Override
    public void setRPrChange(CTRPrChange rPrChange) {
        this.generatedSetterHelperImpl(rPrChange, PROPERTY_QNAME[39], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRPrChange addNewRPrChange() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRPrChange target = null;
            target = (CTRPrChange)((Object)this.get_store().add_element_user(PROPERTY_QNAME[39]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetRPrChange() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[39], 0);
        }
    }
}

