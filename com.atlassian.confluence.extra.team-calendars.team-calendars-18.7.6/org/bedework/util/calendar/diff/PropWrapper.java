/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 */
package org.bedework.util.calendar.diff;

import ietf.params.xml.ns.icalendar_2.BaseParameterType;
import ietf.params.xml.ns.icalendar_2.BasePropertyType;
import ietf.params.xml.ns.icalendar_2.RecurrenceIdPropType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.bedework.util.calendar.XcalUtil;
import org.bedework.util.calendar.diff.BaseEntityWrapper;
import org.bedework.util.calendar.diff.ParamsWrapper;
import org.bedework.util.calendar.diff.PropsWrapper;
import org.bedework.util.calendar.diff.ValueComparator;
import org.oasis_open.docs.ws_calendar.ns.soap.ParametersSelectionType;
import org.oasis_open.docs.ws_calendar.ns.soap.PropertyReferenceType;
import org.oasis_open.docs.ws_calendar.ns.soap.PropertySelectionType;

class PropWrapper
extends BaseEntityWrapper<PropWrapper, PropsWrapper, BasePropertyType>
implements Comparable<PropWrapper> {
    ParamsWrapper params;
    private ValueComparator comparator;
    private static Map<QName, QName> mappedNames = new HashMap<QName, QName>();

    PropWrapper(PropsWrapper parent, QName name, BasePropertyType p) {
        super(parent, name, p);
        List<JAXBElement<? extends BaseParameterType>> plist = null;
        if (p.getParameters() != null) {
            plist = p.getParameters().getBaseParameter();
        }
        this.params = new ParamsWrapper(this, plist);
    }

    @Override
    QName getMappedName(QName name) {
        return mappedNames.get(name);
    }

    @Override
    boolean sameEntity(BaseEntityWrapper val) {
        int res = super.compareNameClass(val);
        return res == 0;
    }

    PropertyReferenceType makeRef() {
        PropertyReferenceType r = new PropertyReferenceType();
        r.setBaseProperty(this.getJaxbElement());
        return r;
    }

    PropertySelectionType getSelect(PropertySelectionType val) {
        if (val != null) {
            return val;
        }
        PropertySelectionType sel = new PropertySelectionType();
        sel.setBaseProperty(this.getJaxbElement());
        return sel;
    }

    public PropertySelectionType diff(PropWrapper that) {
        ParametersSelectionType psel;
        PropertySelectionType sel = null;
        if (this.params != null && (psel = this.params.diff(that.params)) != null) {
            sel = that.getSelect(sel);
            sel.setParameters(psel);
        }
        if (!this.equalValue(that)) {
            sel = that.getSelect(sel);
            PropertyReferenceType ct = new PropertyReferenceType();
            JAXBElement jel = this.getJaxbElement();
            jel.setValue(this.globals.matcher.getElementAndValue(this.getEntity()));
            ct.setBaseProperty(jel);
            sel.setChange(ct);
        }
        return sel;
    }

    public boolean equalValue(PropWrapper that) {
        return this.getComparator().equals(that.getComparator());
    }

    public int compareValue(PropWrapper that) {
        return this.getComparator().compareTo(that.getComparator());
    }

    ValueComparator getComparator() {
        if (this.comparator == null) {
            this.comparator = this.globals.matcher.getComparator(this.getEntity());
        }
        return this.comparator;
    }

    @Override
    public int compareTo(PropWrapper o) {
        if (this.getEntity() instanceof RecurrenceIdPropType) {
            if (!(o.getEntity() instanceof RecurrenceIdPropType)) {
                return this.getName().getLocalPart().compareTo(o.getName().getLocalPart());
            }
            RecurrenceIdPropType thatRid = (RecurrenceIdPropType)o.getEntity();
            RecurrenceIdPropType thisRid = (RecurrenceIdPropType)this.getEntity();
            try {
                String thatUTC = XcalUtil.getUTC(thatRid, this.globals.tzs);
                String thisUTC = XcalUtil.getUTC(thisRid, this.globals.tzs);
                return thatUTC.compareTo(thisUTC);
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
        int res = super.compareNameClass(o);
        if (res != 0) {
            return res;
        }
        res = this.compareValue(o);
        if (res != 0) {
            return res;
        }
        return this.params.compareTo(o.params);
    }

    public int hashCode() {
        return this.getName().hashCode() * this.getComparator().hashCode();
    }

    public boolean equals(Object o) {
        return this.compareTo((PropWrapper)o) == 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("PropWrapper{");
        super.toStringSegment(sb);
        sb.append("}");
        return sb.toString();
    }

    static {
        String ns = "urn:ietf:params:xml:ns:icalendar-2.0";
        mappedNames.put(new QName("urn:ietf:params:xml:ns:icalendar-2.0", "x-bedework-exsynch-organizer"), new QName("urn:ietf:params:xml:ns:icalendar-2.0", "organizer"));
    }
}

