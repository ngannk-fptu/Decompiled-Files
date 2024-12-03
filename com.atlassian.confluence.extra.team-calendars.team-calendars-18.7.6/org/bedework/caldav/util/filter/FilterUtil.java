/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.filter;

import ietf.params.xml.ns.caldav.PropFilterType;
import ietf.params.xml.ns.caldav.TextMatchType;
import ietf.params.xml.ns.caldav.UTCTimeRangeType;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.property.DateProperty;

public class FilterUtil {
    public static boolean filter(PropFilterType pf, Component c) {
        PropertyList<Property> pl = c.getProperties();
        if (pl == null) {
            return false;
        }
        Object prop = pl.getProperty(pf.getName());
        if (prop == null) {
            return pf.getIsNotDefined() != null;
        }
        TextMatchType match = pf.getTextMatch();
        if (match != null) {
            return FilterUtil.matches(match, ((Content)prop).getValue());
        }
        UTCTimeRangeType tr = pf.getTimeRange();
        if (tr == null) {
            return true;
        }
        return FilterUtil.matches(tr, prop);
    }

    public static boolean matches(TextMatchType tm, String candidate) {
        if (candidate == null) {
            return false;
        }
        boolean upperMatch = tm.getCollation().equals("i;ascii-casemap");
        boolean isThere = !upperMatch ? candidate.contains(tm.getValue()) : candidate.toUpperCase().contains(tm.getValue());
        if (tm.getNegateCondition().equals("yes")) {
            return !isThere;
        }
        return isThere;
    }

    public static boolean matches(UTCTimeRangeType tr, Property candidate) {
        return candidate instanceof DateProperty;
    }
}

