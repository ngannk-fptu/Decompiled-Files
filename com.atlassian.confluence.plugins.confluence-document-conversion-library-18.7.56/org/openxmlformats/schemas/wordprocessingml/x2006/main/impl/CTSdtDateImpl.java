/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import java.util.Calendar;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTCalendarType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLang;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtDate;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtDateMappingType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDateTime;

public class CTSdtDateImpl
extends XmlComplexContentImpl
implements CTSdtDate {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "dateFormat"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "lid"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "storeMappedDataAs"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "calendar"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "fullDate")};

    public CTSdtDateImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTString getDateFormat() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTString target = null;
            target = (CTString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDateFormat() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]) != 0;
        }
    }

    @Override
    public void setDateFormat(CTString dateFormat) {
        this.generatedSetterHelperImpl(dateFormat, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTString addNewDateFormat() {
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
    public void unsetDateFormat() {
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
    public CTLang getLid() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLang target = null;
            target = (CTLang)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetLid() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]) != 0;
        }
    }

    @Override
    public void setLid(CTLang lid) {
        this.generatedSetterHelperImpl(lid, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLang addNewLid() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLang target = null;
            target = (CTLang)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetLid() {
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
    public CTSdtDateMappingType getStoreMappedDataAs() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSdtDateMappingType target = null;
            target = (CTSdtDateMappingType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetStoreMappedDataAs() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]) != 0;
        }
    }

    @Override
    public void setStoreMappedDataAs(CTSdtDateMappingType storeMappedDataAs) {
        this.generatedSetterHelperImpl(storeMappedDataAs, PROPERTY_QNAME[2], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSdtDateMappingType addNewStoreMappedDataAs() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSdtDateMappingType target = null;
            target = (CTSdtDateMappingType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetStoreMappedDataAs() {
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
    public CTCalendarType getCalendar() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCalendarType target = null;
            target = (CTCalendarType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetCalendar() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]) != 0;
        }
    }

    @Override
    public void setCalendar(CTCalendarType calendar) {
        this.generatedSetterHelperImpl(calendar, PROPERTY_QNAME[3], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCalendarType addNewCalendar() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCalendarType target = null;
            target = (CTCalendarType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetCalendar() {
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
    public Calendar getFullDate() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[4]));
            return target == null ? null : target.getCalendarValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STDateTime xgetFullDate() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STDateTime target = null;
            target = (STDateTime)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[4]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetFullDate() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[4]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setFullDate(Calendar fullDate) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[4]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[4]));
            }
            target.setCalendarValue(fullDate);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetFullDate(STDateTime fullDate) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STDateTime target = null;
            target = (STDateTime)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[4]));
            if (target == null) {
                target = (STDateTime)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[4]));
            }
            target.set(fullDate);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetFullDate() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[4]);
        }
    }
}

