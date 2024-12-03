/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.calendar.diff;

import ietf.params.xml.ns.icalendar_2.BaseComponentType;
import ietf.params.xml.ns.icalendar_2.CreatedPropType;
import ietf.params.xml.ns.icalendar_2.DtstampPropType;
import ietf.params.xml.ns.icalendar_2.IcalendarType;
import ietf.params.xml.ns.icalendar_2.LastModifiedPropType;
import ietf.params.xml.ns.icalendar_2.ProdidPropType;
import ietf.params.xml.ns.icalendar_2.VcalendarType;
import ietf.params.xml.ns.icalendar_2.VersionPropType;
import ietf.params.xml.ns.icalendar_2.XBedeworkUidParamType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bedework.util.calendar.XcalUtil;
import org.bedework.util.calendar.diff.CompWrapper;
import org.bedework.util.calendar.diff.ValueMatcher;
import org.bedework.util.misc.Logged;
import org.bedework.util.xml.tagdefs.XcalTags;
import org.oasis_open.docs.ws_calendar.ns.soap.ComponentSelectionType;
import org.oasis_open.docs.ws_calendar.ns.soap.ObjectFactory;

public class XmlIcalCompare
extends Logged {
    public static final List<Object> defaultSkipList = new ArrayList<Object>();
    private Globals globals;

    public XmlIcalCompare(List<?> skippedEntities, XcalUtil.TzGetter tzs) {
        this.globals = new Globals(new HashMap<String, Object>(), new ObjectFactory(), new ValueMatcher(), tzs);
        for (Object o : skippedEntities) {
            this.globals.skipMap.put(o.getClass().getCanonicalName(), o);
        }
    }

    public ComponentSelectionType diff(IcalendarType newval, IcalendarType oldval) {
        VcalendarType nv = newval.getVcalendar().get(0);
        VcalendarType ov = oldval.getVcalendar().get(0);
        CompWrapper ncw = new CompWrapper(this.globals, XcalTags.vcalendar, (BaseComponentType)nv);
        CompWrapper ocw = new CompWrapper(this.globals, XcalTags.vcalendar, (BaseComponentType)ov);
        return ncw.diff(ocw);
    }

    static {
        defaultSkipList.add(new ProdidPropType());
        defaultSkipList.add(new VersionPropType());
        defaultSkipList.add(new CreatedPropType());
        defaultSkipList.add(new DtstampPropType());
        defaultSkipList.add(new LastModifiedPropType());
        defaultSkipList.add(new XBedeworkUidParamType());
    }

    static class Globals {
        Map<String, Object> skipMap;
        ObjectFactory of;
        ValueMatcher matcher;
        XcalUtil.TzGetter tzs;

        Globals(Map<String, Object> skipMap, ObjectFactory of, ValueMatcher matcher, XcalUtil.TzGetter tzs) {
            this.skipMap = skipMap;
            this.of = of;
            this.matcher = matcher;
            this.tzs = tzs;
        }
    }
}

