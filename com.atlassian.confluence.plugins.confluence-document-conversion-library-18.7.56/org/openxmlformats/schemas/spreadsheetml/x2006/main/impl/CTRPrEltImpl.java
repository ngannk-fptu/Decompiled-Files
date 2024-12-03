/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBooleanProperty;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFontName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFontScheme;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFontSize;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTIntProperty;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRPrElt;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTUnderlineProperty;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTVerticalAlignFontProperty;

public class CTRPrEltImpl
extends XmlComplexContentImpl
implements CTRPrElt {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "rFont"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "charset"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "family"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "b"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "i"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "strike"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "outline"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "shadow"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "condense"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extend"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "color"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sz"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "u"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "vertAlign"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "scheme")};

    public CTRPrEltImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTFontName> getRFontList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTFontName>(this::getRFontArray, this::setRFontArray, this::insertNewRFont, this::removeRFont, this::sizeOfRFontArray);
        }
    }

    @Override
    public CTFontName[] getRFontArray() {
        return (CTFontName[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTFontName[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFontName getRFontArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFontName target = null;
            target = (CTFontName)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfRFontArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setRFontArray(CTFontName[] rFontArray) {
        this.check_orphaned();
        this.arraySetterHelper(rFontArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setRFontArray(int i, CTFontName rFont) {
        this.generatedSetterHelperImpl(rFont, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFontName insertNewRFont(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFontName target = null;
            target = (CTFontName)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFontName addNewRFont() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFontName target = null;
            target = (CTFontName)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeRFont(int i) {
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
    public List<CTIntProperty> getCharsetList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTIntProperty>(this::getCharsetArray, this::setCharsetArray, this::insertNewCharset, this::removeCharset, this::sizeOfCharsetArray);
        }
    }

    @Override
    public CTIntProperty[] getCharsetArray() {
        return (CTIntProperty[])this.getXmlObjectArray(PROPERTY_QNAME[1], new CTIntProperty[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTIntProperty getCharsetArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTIntProperty target = null;
            target = (CTIntProperty)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], i));
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
    public int sizeOfCharsetArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]);
        }
    }

    @Override
    public void setCharsetArray(CTIntProperty[] charsetArray) {
        this.check_orphaned();
        this.arraySetterHelper(charsetArray, PROPERTY_QNAME[1]);
    }

    @Override
    public void setCharsetArray(int i, CTIntProperty charset) {
        this.generatedSetterHelperImpl(charset, PROPERTY_QNAME[1], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTIntProperty insertNewCharset(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTIntProperty target = null;
            target = (CTIntProperty)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[1], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTIntProperty addNewCharset() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTIntProperty target = null;
            target = (CTIntProperty)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCharset(int i) {
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
    public List<CTIntProperty> getFamilyList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTIntProperty>(this::getFamilyArray, this::setFamilyArray, this::insertNewFamily, this::removeFamily, this::sizeOfFamilyArray);
        }
    }

    @Override
    public CTIntProperty[] getFamilyArray() {
        return (CTIntProperty[])this.getXmlObjectArray(PROPERTY_QNAME[2], new CTIntProperty[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTIntProperty getFamilyArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTIntProperty target = null;
            target = (CTIntProperty)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], i));
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
    public int sizeOfFamilyArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]);
        }
    }

    @Override
    public void setFamilyArray(CTIntProperty[] familyArray) {
        this.check_orphaned();
        this.arraySetterHelper(familyArray, PROPERTY_QNAME[2]);
    }

    @Override
    public void setFamilyArray(int i, CTIntProperty family) {
        this.generatedSetterHelperImpl(family, PROPERTY_QNAME[2], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTIntProperty insertNewFamily(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTIntProperty target = null;
            target = (CTIntProperty)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[2], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTIntProperty addNewFamily() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTIntProperty target = null;
            target = (CTIntProperty)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeFamily(int i) {
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
    public List<CTBooleanProperty> getBList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTBooleanProperty>(this::getBArray, this::setBArray, this::insertNewB, this::removeB, this::sizeOfBArray);
        }
    }

    @Override
    public CTBooleanProperty[] getBArray() {
        return (CTBooleanProperty[])this.getXmlObjectArray(PROPERTY_QNAME[3], new CTBooleanProperty[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBooleanProperty getBArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBooleanProperty target = null;
            target = (CTBooleanProperty)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], i));
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
            return this.get_store().count_elements(PROPERTY_QNAME[3]);
        }
    }

    @Override
    public void setBArray(CTBooleanProperty[] bArray) {
        this.check_orphaned();
        this.arraySetterHelper(bArray, PROPERTY_QNAME[3]);
    }

    @Override
    public void setBArray(int i, CTBooleanProperty b) {
        this.generatedSetterHelperImpl(b, PROPERTY_QNAME[3], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBooleanProperty insertNewB(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBooleanProperty target = null;
            target = (CTBooleanProperty)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[3], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBooleanProperty addNewB() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBooleanProperty target = null;
            target = (CTBooleanProperty)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
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
            this.get_store().remove_element(PROPERTY_QNAME[3], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTBooleanProperty> getIList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTBooleanProperty>(this::getIArray, this::setIArray, this::insertNewI, this::removeI, this::sizeOfIArray);
        }
    }

    @Override
    public CTBooleanProperty[] getIArray() {
        return (CTBooleanProperty[])this.getXmlObjectArray(PROPERTY_QNAME[4], new CTBooleanProperty[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBooleanProperty getIArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBooleanProperty target = null;
            target = (CTBooleanProperty)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], i));
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
    public void setIArray(CTBooleanProperty[] iValueArray) {
        this.check_orphaned();
        this.arraySetterHelper(iValueArray, PROPERTY_QNAME[4]);
    }

    @Override
    public void setIArray(int i, CTBooleanProperty iValue) {
        this.generatedSetterHelperImpl(iValue, PROPERTY_QNAME[4], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBooleanProperty insertNewI(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBooleanProperty target = null;
            target = (CTBooleanProperty)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[4], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBooleanProperty addNewI() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBooleanProperty target = null;
            target = (CTBooleanProperty)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
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
    public List<CTBooleanProperty> getStrikeList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTBooleanProperty>(this::getStrikeArray, this::setStrikeArray, this::insertNewStrike, this::removeStrike, this::sizeOfStrikeArray);
        }
    }

    @Override
    public CTBooleanProperty[] getStrikeArray() {
        return (CTBooleanProperty[])this.getXmlObjectArray(PROPERTY_QNAME[5], new CTBooleanProperty[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBooleanProperty getStrikeArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBooleanProperty target = null;
            target = (CTBooleanProperty)((Object)this.get_store().find_element_user(PROPERTY_QNAME[5], i));
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
            return this.get_store().count_elements(PROPERTY_QNAME[5]);
        }
    }

    @Override
    public void setStrikeArray(CTBooleanProperty[] strikeArray) {
        this.check_orphaned();
        this.arraySetterHelper(strikeArray, PROPERTY_QNAME[5]);
    }

    @Override
    public void setStrikeArray(int i, CTBooleanProperty strike) {
        this.generatedSetterHelperImpl(strike, PROPERTY_QNAME[5], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBooleanProperty insertNewStrike(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBooleanProperty target = null;
            target = (CTBooleanProperty)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[5], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBooleanProperty addNewStrike() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBooleanProperty target = null;
            target = (CTBooleanProperty)((Object)this.get_store().add_element_user(PROPERTY_QNAME[5]));
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
            this.get_store().remove_element(PROPERTY_QNAME[5], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTBooleanProperty> getOutlineList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTBooleanProperty>(this::getOutlineArray, this::setOutlineArray, this::insertNewOutline, this::removeOutline, this::sizeOfOutlineArray);
        }
    }

    @Override
    public CTBooleanProperty[] getOutlineArray() {
        return (CTBooleanProperty[])this.getXmlObjectArray(PROPERTY_QNAME[6], new CTBooleanProperty[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBooleanProperty getOutlineArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBooleanProperty target = null;
            target = (CTBooleanProperty)((Object)this.get_store().find_element_user(PROPERTY_QNAME[6], i));
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
            return this.get_store().count_elements(PROPERTY_QNAME[6]);
        }
    }

    @Override
    public void setOutlineArray(CTBooleanProperty[] outlineArray) {
        this.check_orphaned();
        this.arraySetterHelper(outlineArray, PROPERTY_QNAME[6]);
    }

    @Override
    public void setOutlineArray(int i, CTBooleanProperty outline) {
        this.generatedSetterHelperImpl(outline, PROPERTY_QNAME[6], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBooleanProperty insertNewOutline(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBooleanProperty target = null;
            target = (CTBooleanProperty)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[6], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBooleanProperty addNewOutline() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBooleanProperty target = null;
            target = (CTBooleanProperty)((Object)this.get_store().add_element_user(PROPERTY_QNAME[6]));
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
            this.get_store().remove_element(PROPERTY_QNAME[6], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTBooleanProperty> getShadowList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTBooleanProperty>(this::getShadowArray, this::setShadowArray, this::insertNewShadow, this::removeShadow, this::sizeOfShadowArray);
        }
    }

    @Override
    public CTBooleanProperty[] getShadowArray() {
        return (CTBooleanProperty[])this.getXmlObjectArray(PROPERTY_QNAME[7], new CTBooleanProperty[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBooleanProperty getShadowArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBooleanProperty target = null;
            target = (CTBooleanProperty)((Object)this.get_store().find_element_user(PROPERTY_QNAME[7], i));
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
            return this.get_store().count_elements(PROPERTY_QNAME[7]);
        }
    }

    @Override
    public void setShadowArray(CTBooleanProperty[] shadowArray) {
        this.check_orphaned();
        this.arraySetterHelper(shadowArray, PROPERTY_QNAME[7]);
    }

    @Override
    public void setShadowArray(int i, CTBooleanProperty shadow) {
        this.generatedSetterHelperImpl(shadow, PROPERTY_QNAME[7], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBooleanProperty insertNewShadow(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBooleanProperty target = null;
            target = (CTBooleanProperty)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[7], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBooleanProperty addNewShadow() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBooleanProperty target = null;
            target = (CTBooleanProperty)((Object)this.get_store().add_element_user(PROPERTY_QNAME[7]));
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
            this.get_store().remove_element(PROPERTY_QNAME[7], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTBooleanProperty> getCondenseList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTBooleanProperty>(this::getCondenseArray, this::setCondenseArray, this::insertNewCondense, this::removeCondense, this::sizeOfCondenseArray);
        }
    }

    @Override
    public CTBooleanProperty[] getCondenseArray() {
        return (CTBooleanProperty[])this.getXmlObjectArray(PROPERTY_QNAME[8], new CTBooleanProperty[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBooleanProperty getCondenseArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBooleanProperty target = null;
            target = (CTBooleanProperty)((Object)this.get_store().find_element_user(PROPERTY_QNAME[8], i));
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
    public int sizeOfCondenseArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[8]);
        }
    }

    @Override
    public void setCondenseArray(CTBooleanProperty[] condenseArray) {
        this.check_orphaned();
        this.arraySetterHelper(condenseArray, PROPERTY_QNAME[8]);
    }

    @Override
    public void setCondenseArray(int i, CTBooleanProperty condense) {
        this.generatedSetterHelperImpl(condense, PROPERTY_QNAME[8], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBooleanProperty insertNewCondense(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBooleanProperty target = null;
            target = (CTBooleanProperty)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[8], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBooleanProperty addNewCondense() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBooleanProperty target = null;
            target = (CTBooleanProperty)((Object)this.get_store().add_element_user(PROPERTY_QNAME[8]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCondense(int i) {
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
    public List<CTBooleanProperty> getExtendList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTBooleanProperty>(this::getExtendArray, this::setExtendArray, this::insertNewExtend, this::removeExtend, this::sizeOfExtendArray);
        }
    }

    @Override
    public CTBooleanProperty[] getExtendArray() {
        return (CTBooleanProperty[])this.getXmlObjectArray(PROPERTY_QNAME[9], new CTBooleanProperty[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBooleanProperty getExtendArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBooleanProperty target = null;
            target = (CTBooleanProperty)((Object)this.get_store().find_element_user(PROPERTY_QNAME[9], i));
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
    public int sizeOfExtendArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[9]);
        }
    }

    @Override
    public void setExtendArray(CTBooleanProperty[] extendArray) {
        this.check_orphaned();
        this.arraySetterHelper(extendArray, PROPERTY_QNAME[9]);
    }

    @Override
    public void setExtendArray(int i, CTBooleanProperty extend) {
        this.generatedSetterHelperImpl(extend, PROPERTY_QNAME[9], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBooleanProperty insertNewExtend(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBooleanProperty target = null;
            target = (CTBooleanProperty)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[9], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBooleanProperty addNewExtend() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBooleanProperty target = null;
            target = (CTBooleanProperty)((Object)this.get_store().add_element_user(PROPERTY_QNAME[9]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeExtend(int i) {
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
    public List<CTColor> getColorList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTColor>(this::getColorArray, this::setColorArray, this::insertNewColor, this::removeColor, this::sizeOfColorArray);
        }
    }

    @Override
    public CTColor[] getColorArray() {
        return (CTColor[])this.getXmlObjectArray(PROPERTY_QNAME[10], new CTColor[0]);
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
            target = (CTColor)((Object)this.get_store().find_element_user(PROPERTY_QNAME[10], i));
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
            return this.get_store().count_elements(PROPERTY_QNAME[10]);
        }
    }

    @Override
    public void setColorArray(CTColor[] colorArray) {
        this.check_orphaned();
        this.arraySetterHelper(colorArray, PROPERTY_QNAME[10]);
    }

    @Override
    public void setColorArray(int i, CTColor color) {
        this.generatedSetterHelperImpl(color, PROPERTY_QNAME[10], i, (short)2);
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
            target = (CTColor)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[10], i));
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
            target = (CTColor)((Object)this.get_store().add_element_user(PROPERTY_QNAME[10]));
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
            this.get_store().remove_element(PROPERTY_QNAME[10], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTFontSize> getSzList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTFontSize>(this::getSzArray, this::setSzArray, this::insertNewSz, this::removeSz, this::sizeOfSzArray);
        }
    }

    @Override
    public CTFontSize[] getSzArray() {
        return (CTFontSize[])this.getXmlObjectArray(PROPERTY_QNAME[11], new CTFontSize[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFontSize getSzArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFontSize target = null;
            target = (CTFontSize)((Object)this.get_store().find_element_user(PROPERTY_QNAME[11], i));
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
            return this.get_store().count_elements(PROPERTY_QNAME[11]);
        }
    }

    @Override
    public void setSzArray(CTFontSize[] szArray) {
        this.check_orphaned();
        this.arraySetterHelper(szArray, PROPERTY_QNAME[11]);
    }

    @Override
    public void setSzArray(int i, CTFontSize sz) {
        this.generatedSetterHelperImpl(sz, PROPERTY_QNAME[11], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFontSize insertNewSz(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFontSize target = null;
            target = (CTFontSize)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[11], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFontSize addNewSz() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFontSize target = null;
            target = (CTFontSize)((Object)this.get_store().add_element_user(PROPERTY_QNAME[11]));
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
            this.get_store().remove_element(PROPERTY_QNAME[11], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTUnderlineProperty> getUList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTUnderlineProperty>(this::getUArray, this::setUArray, this::insertNewU, this::removeU, this::sizeOfUArray);
        }
    }

    @Override
    public CTUnderlineProperty[] getUArray() {
        return (CTUnderlineProperty[])this.getXmlObjectArray(PROPERTY_QNAME[12], new CTUnderlineProperty[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTUnderlineProperty getUArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTUnderlineProperty target = null;
            target = (CTUnderlineProperty)((Object)this.get_store().find_element_user(PROPERTY_QNAME[12], i));
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
            return this.get_store().count_elements(PROPERTY_QNAME[12]);
        }
    }

    @Override
    public void setUArray(CTUnderlineProperty[] uArray) {
        this.check_orphaned();
        this.arraySetterHelper(uArray, PROPERTY_QNAME[12]);
    }

    @Override
    public void setUArray(int i, CTUnderlineProperty u) {
        this.generatedSetterHelperImpl(u, PROPERTY_QNAME[12], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTUnderlineProperty insertNewU(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTUnderlineProperty target = null;
            target = (CTUnderlineProperty)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[12], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTUnderlineProperty addNewU() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTUnderlineProperty target = null;
            target = (CTUnderlineProperty)((Object)this.get_store().add_element_user(PROPERTY_QNAME[12]));
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
            this.get_store().remove_element(PROPERTY_QNAME[12], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTVerticalAlignFontProperty> getVertAlignList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTVerticalAlignFontProperty>(this::getVertAlignArray, this::setVertAlignArray, this::insertNewVertAlign, this::removeVertAlign, this::sizeOfVertAlignArray);
        }
    }

    @Override
    public CTVerticalAlignFontProperty[] getVertAlignArray() {
        return (CTVerticalAlignFontProperty[])this.getXmlObjectArray(PROPERTY_QNAME[13], new CTVerticalAlignFontProperty[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTVerticalAlignFontProperty getVertAlignArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTVerticalAlignFontProperty target = null;
            target = (CTVerticalAlignFontProperty)((Object)this.get_store().find_element_user(PROPERTY_QNAME[13], i));
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
            return this.get_store().count_elements(PROPERTY_QNAME[13]);
        }
    }

    @Override
    public void setVertAlignArray(CTVerticalAlignFontProperty[] vertAlignArray) {
        this.check_orphaned();
        this.arraySetterHelper(vertAlignArray, PROPERTY_QNAME[13]);
    }

    @Override
    public void setVertAlignArray(int i, CTVerticalAlignFontProperty vertAlign) {
        this.generatedSetterHelperImpl(vertAlign, PROPERTY_QNAME[13], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTVerticalAlignFontProperty insertNewVertAlign(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTVerticalAlignFontProperty target = null;
            target = (CTVerticalAlignFontProperty)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[13], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTVerticalAlignFontProperty addNewVertAlign() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTVerticalAlignFontProperty target = null;
            target = (CTVerticalAlignFontProperty)((Object)this.get_store().add_element_user(PROPERTY_QNAME[13]));
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
            this.get_store().remove_element(PROPERTY_QNAME[13], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTFontScheme> getSchemeList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTFontScheme>(this::getSchemeArray, this::setSchemeArray, this::insertNewScheme, this::removeScheme, this::sizeOfSchemeArray);
        }
    }

    @Override
    public CTFontScheme[] getSchemeArray() {
        return (CTFontScheme[])this.getXmlObjectArray(PROPERTY_QNAME[14], new CTFontScheme[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFontScheme getSchemeArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFontScheme target = null;
            target = (CTFontScheme)((Object)this.get_store().find_element_user(PROPERTY_QNAME[14], i));
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
    public int sizeOfSchemeArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[14]);
        }
    }

    @Override
    public void setSchemeArray(CTFontScheme[] schemeArray) {
        this.check_orphaned();
        this.arraySetterHelper(schemeArray, PROPERTY_QNAME[14]);
    }

    @Override
    public void setSchemeArray(int i, CTFontScheme scheme) {
        this.generatedSetterHelperImpl(scheme, PROPERTY_QNAME[14], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFontScheme insertNewScheme(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFontScheme target = null;
            target = (CTFontScheme)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[14], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFontScheme addNewScheme() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFontScheme target = null;
            target = (CTFontScheme)((Object)this.get_store().add_element_user(PROPERTY_QNAME[14]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeScheme(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[14], i);
        }
    }
}

