/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 */
package org.bedework.util.calendar.diff;

import ietf.params.xml.ns.icalendar_2.BaseComponentType;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.bind.JAXBElement;
import org.bedework.util.calendar.diff.BaseSetWrapper;
import org.bedework.util.calendar.diff.CompWrapper;
import org.bedework.util.xml.tagdefs.XcalTags;
import org.oasis_open.docs.ws_calendar.ns.soap.ComponentReferenceType;
import org.oasis_open.docs.ws_calendar.ns.soap.ComponentSelectionType;
import org.oasis_open.docs.ws_calendar.ns.soap.ComponentsSelectionType;

class CompsWrapper
extends BaseSetWrapper<CompWrapper, CompWrapper, JAXBElement<? extends BaseComponentType>>
implements Comparable<CompsWrapper> {
    CompsWrapper(CompWrapper parent, List<JAXBElement<? extends BaseComponentType>> clist) {
        super(parent, XcalTags.components, clist);
    }

    CompWrapper[] getTarray(int len) {
        return new CompWrapper[len];
    }

    @Override
    Set<CompWrapper> getWrapped(JAXBElement<? extends BaseComponentType> el) {
        TreeSet<CompWrapper> res = new TreeSet<CompWrapper>();
        res.add(new CompWrapper(this, el.getName(), (BaseComponentType)el.getValue()));
        return res;
    }

    public ComponentsSelectionType diff(CompsWrapper that) {
        CompWrapper thisOne;
        ComponentsSelectionType sel = null;
        int thatI = 0;
        int thisI = 0;
        while (thisI < this.size() && thatI < that.size()) {
            CompWrapper thatOne;
            thisOne = ((CompWrapper[])this.getTarray())[thisI];
            if (thisOne.sameEntity(thatOne = ((CompWrapper[])that.getTarray())[thatI])) {
                ComponentSelectionType csel = thisOne.diff(thatOne);
                if (csel != null) {
                    sel = this.select(sel, csel);
                }
                ++thisI;
                ++thatI;
                continue;
            }
            int cmp = thisOne.compareTo(thatOne);
            if (cmp < 0) {
                sel = this.add(sel, thisOne.makeRef(false));
                ++thisI;
                continue;
            }
            if (cmp == 0) {
                throw new RuntimeException("Comparison == 0: that's not right");
            }
            sel = this.remove(sel, thatOne.makeRef(true));
            ++thatI;
        }
        while (thisI < this.size()) {
            thisOne = ((CompWrapper[])this.getTarray())[thisI];
            sel = this.add(sel, thisOne.makeRef(false));
            ++thisI;
        }
        while (thatI < that.size()) {
            CompWrapper thatOne = ((CompWrapper[])that.getTarray())[thatI];
            sel = this.remove(sel, thatOne.makeRef(true));
            ++thatI;
        }
        return sel;
    }

    ComponentsSelectionType getSelect(ComponentsSelectionType val) {
        if (val != null) {
            return val;
        }
        ComponentsSelectionType sel = new ComponentsSelectionType();
        return sel;
    }

    ComponentsSelectionType add(ComponentsSelectionType sel, ComponentReferenceType val) {
        ComponentsSelectionType csel = this.getSelect(sel);
        csel.getAdd().add(val);
        return csel;
    }

    ComponentsSelectionType remove(ComponentsSelectionType sel, ComponentReferenceType val) {
        ComponentsSelectionType csel = this.getSelect(sel);
        csel.getRemove().add(val);
        return csel;
    }

    ComponentsSelectionType select(ComponentsSelectionType sel, ComponentSelectionType val) {
        ComponentsSelectionType csel = this.getSelect(sel);
        csel.getComponent().add(val);
        return csel;
    }

    @Override
    public int compareTo(CompsWrapper that) {
        if (this.size() < that.size()) {
            return -1;
        }
        if (this.size() > that.size()) {
            return 1;
        }
        Iterator it = that.getEls().iterator();
        for (CompWrapper c : this.getEls()) {
            CompWrapper thatC;
            int res = c.compareTo(thatC = (CompWrapper)it.next());
            if (res == 0) continue;
            return res;
        }
        return 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("CompsWrapper{");
        super.toStringSegment(sb);
        sb.append("}");
        return sb.toString();
    }
}

