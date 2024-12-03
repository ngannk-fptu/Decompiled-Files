/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 */
package org.bedework.util.calendar.diff;

import ietf.params.xml.ns.icalendar_2.BasePropertyType;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.bedework.util.calendar.diff.BaseSetWrapper;
import org.bedework.util.calendar.diff.CompWrapper;
import org.bedework.util.calendar.diff.PropWrapper;
import org.bedework.util.xml.tagdefs.XcalTags;
import org.oasis_open.docs.ws_calendar.ns.soap.PropertiesSelectionType;
import org.oasis_open.docs.ws_calendar.ns.soap.PropertyReferenceType;
import org.oasis_open.docs.ws_calendar.ns.soap.PropertySelectionType;

class PropsWrapper
extends BaseSetWrapper<PropWrapper, CompWrapper, JAXBElement<? extends BasePropertyType>>
implements Comparable<PropsWrapper> {
    PropsWrapper(CompWrapper parent, List<JAXBElement<? extends BasePropertyType>> propsList) {
        super(parent, XcalTags.properties, propsList);
    }

    PropWrapper[] getTarray(int len) {
        return new PropWrapper[len];
    }

    @Override
    Set<PropWrapper> getWrapped(JAXBElement<? extends BasePropertyType> el) {
        QName nm = el.getName();
        if (this.skipThis(el.getValue())) {
            return null;
        }
        TreeSet<PropWrapper> res = new TreeSet<PropWrapper>();
        List normed = this.globals.matcher.getNormalized(el.getValue());
        for (BasePropertyType bp : normed) {
            res.add(new PropWrapper(this, nm, bp));
        }
        return res;
    }

    public PropertiesSelectionType diff(PropsWrapper that) {
        PropWrapper thisOne;
        PropertiesSelectionType sel = null;
        int thatI = 0;
        int thisI = 0;
        while (that != null && thisI < this.size() && thatI < that.size()) {
            PropWrapper thatOne;
            thisOne = ((PropWrapper[])this.getTarray())[thisI];
            if (thisOne.equals(thatOne = ((PropWrapper[])that.getTarray())[thatI])) {
                ++thisI;
                ++thatI;
                continue;
            }
            int ncmp = thisOne.compareNames(thatOne);
            if (ncmp == 0) {
                PropWrapper nextThisOne;
                int nextThisI;
                PropWrapper nextThatOne;
                if (thisI + 1 == this.size() && thatI + 1 == that.size()) {
                    sel = this.select(sel, thisOne.diff(thatOne));
                    ++thisI;
                    ++thatI;
                    continue;
                }
                int nextThatI = thatI + 1;
                boolean matchFound = false;
                if (this.debug && thatOne.getMappedName().equals(PropWrapper.XBedeworkWrapperQNAME)) {
                    this.debug("At wrapped x-prop");
                }
                while (nextThatI < that.size() && thisOne.compareNames(nextThatOne = ((PropWrapper[])that.getTarray())[nextThatI]) == 0) {
                    if (thisOne.equals(nextThatOne)) {
                        matchFound = true;
                        break;
                    }
                    ++nextThatI;
                }
                if (matchFound) {
                    while (thatI < nextThatI) {
                        thatOne = ((PropWrapper[])that.getTarray())[thatI];
                        sel = this.remove(sel, thatOne.makeRef());
                        ++thatI;
                    }
                    continue;
                }
                for (nextThisI = thisI + 1; nextThisI < this.size() && (nextThisOne = ((PropWrapper[])this.getTarray())[nextThisI]).compareNames(thatOne) == 0; ++nextThisI) {
                    if (!nextThisOne.equals(thatOne)) continue;
                    matchFound = true;
                    break;
                }
                if (matchFound) {
                    while (thisI < nextThisI) {
                        thisOne = ((PropWrapper[])this.getTarray())[thisI];
                        sel = this.add(sel, thisOne.makeRef());
                        ++thisI;
                    }
                    continue;
                }
                sel = this.select(sel, thisOne.diff(thatOne));
                ++thisI;
                ++thatI;
                continue;
            }
            if (ncmp < 0) {
                sel = this.add(sel, thisOne.makeRef());
                ++thisI;
                continue;
            }
            sel = this.remove(sel, thatOne.makeRef());
            ++thatI;
        }
        while (thisI < this.size()) {
            thisOne = ((PropWrapper[])this.getTarray())[thisI];
            if (this.debug) {
                this.debug("Adding " + thisOne.getMappedName());
            }
            sel = this.add(sel, thisOne.makeRef());
            ++thisI;
        }
        while (that != null && thatI < that.size()) {
            PropWrapper thatOne = ((PropWrapper[])that.getTarray())[thatI];
            if (this.debug) {
                this.debug("Removing " + thatOne.getMappedName());
            }
            sel = this.remove(sel, thatOne.makeRef());
            ++thatI;
        }
        return sel;
    }

    PropertiesSelectionType getSelect(PropertiesSelectionType val) {
        if (val != null) {
            return val;
        }
        return new PropertiesSelectionType();
    }

    PropertiesSelectionType add(PropertiesSelectionType sel, PropertyReferenceType val) {
        PropertiesSelectionType csel = this.getSelect(sel);
        csel.getAdd().add(val);
        return csel;
    }

    PropertiesSelectionType remove(PropertiesSelectionType sel, PropertyReferenceType val) {
        PropertiesSelectionType csel = this.getSelect(sel);
        csel.getRemove().add(val);
        return csel;
    }

    PropertiesSelectionType select(PropertiesSelectionType sel, PropertySelectionType val) {
        PropertiesSelectionType csel = this.getSelect(sel);
        csel.getProperty().add(val);
        return csel;
    }

    @Override
    public int compareTo(PropsWrapper that) {
        if (this.size() < that.size()) {
            return -1;
        }
        if (this.size() > that.size()) {
            return 1;
        }
        Iterator it = that.getEls().iterator();
        for (PropWrapper p : this.getEls()) {
            PropWrapper thatP;
            int res = p.compareTo(thatP = (PropWrapper)it.next());
            if (res == 0) continue;
            return res;
        }
        return 0;
    }

    public int hashCode() {
        int hc = this.size() + 1;
        for (PropWrapper p : this.getEls()) {
            hc += p.hashCode();
        }
        return hc;
    }

    public boolean equals(Object o) {
        return this.compareTo((PropsWrapper)o) == 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("PropsWrapper{");
        super.toStringSegment(sb);
        sb.append("}");
        return sb.toString();
    }
}

