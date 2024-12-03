/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 */
package org.bedework.util.calendar.diff;

import ietf.params.xml.ns.icalendar_2.BaseParameterType;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.bind.JAXBElement;
import org.bedework.util.calendar.diff.BaseSetWrapper;
import org.bedework.util.calendar.diff.ParamWrapper;
import org.bedework.util.calendar.diff.PropWrapper;
import org.bedework.util.xml.tagdefs.XcalTags;
import org.oasis_open.docs.ws_calendar.ns.soap.ParameterReferenceType;
import org.oasis_open.docs.ws_calendar.ns.soap.ParameterSelectionType;
import org.oasis_open.docs.ws_calendar.ns.soap.ParametersSelectionType;

class ParamsWrapper
extends BaseSetWrapper<ParamWrapper, PropWrapper, JAXBElement<? extends BaseParameterType>>
implements Comparable<ParamsWrapper> {
    ParamsWrapper(PropWrapper parent, List<JAXBElement<? extends BaseParameterType>> plist) {
        super(parent, XcalTags.parameters, plist);
    }

    ParamWrapper[] getTarray(int len) {
        return new ParamWrapper[len];
    }

    @Override
    Set<ParamWrapper> getWrapped(JAXBElement<? extends BaseParameterType> el) {
        if (this.skipThis(el.getValue())) {
            return null;
        }
        TreeSet<ParamWrapper> res = new TreeSet<ParamWrapper>();
        res.add(new ParamWrapper(this, el.getName(), (BaseParameterType)el.getValue()));
        return res;
    }

    public ParametersSelectionType diff(ParamsWrapper that) {
        ParamWrapper thisOne;
        ParametersSelectionType sel = null;
        int thatI = 0;
        int thisI = 0;
        while (thisI < this.size() && thatI < that.size()) {
            ParamWrapper thatOne;
            thisOne = ((ParamWrapper[])this.getTarray())[thisI];
            if (thisOne.equals(thatOne = ((ParamWrapper[])that.getTarray())[thatI])) {
                ++thisI;
                ++thatI;
                continue;
            }
            int ncmp = thisOne.compareNames(thatOne);
            if (ncmp == 0) {
                ParamWrapper nextThisOne;
                int nextThisI;
                ParamWrapper nextThatOne;
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
                while (nextThatI < that.size() && thisOne.compareNames(nextThatOne = ((ParamWrapper[])that.getTarray())[nextThatI]) == 0) {
                    if (thisOne.equals(nextThatOne)) {
                        matchFound = true;
                        break;
                    }
                    ++nextThatI;
                }
                if (matchFound) {
                    while (thatI < nextThatI) {
                        thatOne = ((ParamWrapper[])that.getTarray())[thatI];
                        sel = this.remove(sel, thatOne.makeRef());
                        ++thatI;
                    }
                    continue;
                }
                for (nextThisI = thisI + 1; nextThisI < this.size() && (nextThisOne = ((ParamWrapper[])this.getTarray())[nextThisI]).compareNames(thatOne) == 0; ++nextThisI) {
                    if (!nextThisOne.equals(thatOne)) continue;
                    matchFound = true;
                    break;
                }
                if (matchFound) {
                    while (thisI < nextThisI) {
                        thisOne = ((ParamWrapper[])this.getTarray())[thisI];
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
            thisOne = ((ParamWrapper[])this.getTarray())[thisI];
            sel = this.add(sel, thisOne.makeRef());
            ++thisI;
        }
        while (thatI < that.size()) {
            ParamWrapper thatOne = ((ParamWrapper[])that.getTarray())[thatI];
            sel = this.remove(sel, thatOne.makeRef());
            ++thatI;
        }
        return sel;
    }

    ParametersSelectionType getSelect(ParametersSelectionType val) {
        if (val != null) {
            return val;
        }
        return new ParametersSelectionType();
    }

    ParametersSelectionType add(ParametersSelectionType sel, ParameterReferenceType val) {
        ParametersSelectionType csel = this.getSelect(sel);
        csel.getAdd().add(val);
        return csel;
    }

    ParametersSelectionType remove(ParametersSelectionType sel, ParameterReferenceType val) {
        ParametersSelectionType csel = this.getSelect(sel);
        csel.getRemove().add(val);
        return csel;
    }

    ParametersSelectionType select(ParametersSelectionType sel, ParameterSelectionType val) {
        ParametersSelectionType csel = this.getSelect(sel);
        csel.getParameter().add(val);
        return csel;
    }

    @Override
    public int compareTo(ParamsWrapper that) {
        if (this.size() < that.size()) {
            return -1;
        }
        if (this.size() > that.size()) {
            return 1;
        }
        Iterator it = that.getEls().iterator();
        for (ParamWrapper p : this.getEls()) {
            ParamWrapper thatP;
            int res = p.compareTo(thatP = (ParamWrapper)it.next());
            if (res == 0) continue;
            return res;
        }
        return 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ParamsWrapper{");
        super.toStringSegment(sb);
        sb.append("}");
        return sb.toString();
    }
}

