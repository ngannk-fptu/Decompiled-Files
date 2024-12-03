/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.filter.parse;

import ietf.params.xml.ns.caldav.PropFilterType;
import java.io.Serializable;
import java.util.List;
import org.bedework.caldav.util.filter.FilterBase;

public class EventQuery
implements Serializable {
    public FilterBase filter;
    public boolean postFilter;
    public List<PropFilterType> eventFilters;
    public List<PropFilterType> todoFilters;
    public List<PropFilterType> journalFilters;
    public List<PropFilterType> alarmFilters;
}

