/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.Unmarshaller
 */
package org.bedework.caldav.util.filter.parse;

import ietf.params.xml.ns.caldav.CompFilterType;
import ietf.params.xml.ns.caldav.FilterType;
import ietf.params.xml.ns.caldav.ParamFilterType;
import ietf.params.xml.ns.caldav.PropFilterType;
import ietf.params.xml.ns.caldav.TextMatchType;
import ietf.params.xml.ns.caldav.UTCTimeRangeType;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import net.fortuna.ical4j.model.DateTime;
import org.bedework.caldav.util.TimeRange;
import org.bedework.caldav.util.filter.EntityTimeRangeFilter;
import org.bedework.caldav.util.filter.EntityTypeFilter;
import org.bedework.caldav.util.filter.FilterBase;
import org.bedework.caldav.util.filter.ObjectFilter;
import org.bedework.caldav.util.filter.PresenceFilter;
import org.bedework.caldav.util.filter.PropertyFilter;
import org.bedework.caldav.util.filter.parse.EventQuery;
import org.bedework.util.calendar.PropertyIndex;
import org.bedework.util.calendar.XcalUtil;
import org.bedework.util.misc.Util;
import org.bedework.util.xml.tagdefs.CaldavTags;
import org.bedework.webdav.servlet.shared.WebdavBadRequest;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavForbidden;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class Filters {
    public static boolean matchAll(CompFilterType cf) {
        return cf.getTimeRange() == null && Util.isEmpty(cf.getCompFilter()) && Util.isEmpty(cf.getPropFilter());
    }

    public static boolean caseless(TextMatchType tm) {
        return tm.getCollation().equals("i;ascii-casemap");
    }

    public static FilterType parse(String xmlStr) throws WebdavException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlStr)));
            return Filters.parse(doc.getDocumentElement());
        }
        catch (WebdavException cfe) {
            throw cfe;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public static FilterType parse(Node nd) throws WebdavException {
        try {
            JAXBContext jc = JAXBContext.newInstance((String)"ietf.params.xml.ns.caldav");
            Unmarshaller u = jc.createUnmarshaller();
            JAXBElement jel = (JAXBElement)u.unmarshal(nd);
            if (jel == null) {
                return null;
            }
            return (FilterType)jel.getValue();
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public static EventQuery getQuery(FilterType f) throws WebdavException {
        EventQuery eventq = new EventQuery();
        eventq.filter = Filters.getQueryFilter(f.getCompFilter(), eventq, 0);
        return eventq;
    }

    public static FilterBase getQueryFilter(CompFilterType cf, EventQuery eq, int exprDepth) throws WebdavException {
        FilterBase filter = null;
        int entityType = 0;
        boolean isNotDefined = cf.getIsNotDefined() != null;
        boolean andThem = "allof".equals(cf.getTest());
        String name = cf.getName().toUpperCase();
        if (exprDepth == 0) {
            if (!"VCALENDAR".equals(name)) {
                throw new WebdavBadRequest();
            }
        } else if (exprDepth == 1) {
            filter = EntityTypeFilter.makeIcalEntityTypeFilter(null, name, false);
            entityType = (Integer)filter.getEntity();
        } else if (exprDepth == 2) {
            entityType = 1;
            filter = Filters.makeFilter(name, entityType, isNotDefined, Filters.matchAll(cf), Filters.makeTimeRange(cf.getTimeRange()), null, false, null);
            if (filter == null) {
                throw new WebdavBadRequest();
            }
        } else {
            throw new WebdavBadRequest("expr too deep");
        }
        if (filter != null && isNotDefined) {
            filter.setNot(true);
        }
        if (Filters.matchAll(cf)) {
            return filter;
        }
        if (exprDepth < 2 && cf.getTimeRange() != null) {
            EntityTimeRangeFilter etrf = new EntityTimeRangeFilter(null, entityType, Filters.makeTimeRange(cf.getTimeRange()));
            filter = FilterBase.addAndChild(filter, etrf);
        }
        if (exprDepth > 0) {
            filter = FilterBase.addAndChild(filter, Filters.processPropFilters(cf, eq, entityType));
        }
        if (!Util.isEmpty(cf.getCompFilter())) {
            FilterBase cfilters = null;
            for (CompFilterType subcf : cf.getCompFilter()) {
                FilterBase subqf = Filters.getQueryFilter(subcf, eq, exprDepth + 1);
                if (andThem) {
                    cfilters = FilterBase.addAndChild(cfilters, subqf);
                    continue;
                }
                cfilters = FilterBase.addOrChild(cfilters, subqf);
            }
            filter = FilterBase.addAndChild(filter, cfilters);
        }
        return filter;
    }

    private static TimeRange makeTimeRange(UTCTimeRangeType utr) throws WebdavException {
        if (utr == null) {
            return null;
        }
        try {
            DateTime st = null;
            DateTime et = null;
            if (utr.getStart() != null) {
                st = new DateTime(XcalUtil.getIcalFormatDateTime(utr.getStart()));
            }
            if (utr.getEnd() != null) {
                et = new DateTime(XcalUtil.getIcalFormatDateTime(utr.getEnd()));
            }
            if (st == null && et == null) {
                throw new WebdavForbidden(CaldavTags.validFilter, "Invalid time-range - no start and no end");
            }
            if (st != null && !st.isUtc()) {
                throw new WebdavForbidden(CaldavTags.validFilter, "Invalid time-range - start not UTC");
            }
            if (et != null && !et.isUtc()) {
                throw new WebdavForbidden(CaldavTags.validFilter, "Invalid time-range - end not UTC");
            }
            return new TimeRange(st, et);
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavForbidden(CaldavTags.validFilter, "Invalid time-range");
        }
    }

    private static FilterBase processPropFilters(CompFilterType cf, EventQuery eq, int entityType) throws WebdavException {
        if (Util.isEmpty(cf.getPropFilter())) {
            return null;
        }
        FilterBase pfilters = null;
        boolean andThem = "allof".equals(cf.getTest());
        for (PropFilterType pf : cf.getPropFilter()) {
            boolean andParams;
            FilterBase filter;
            String pname = pf.getName();
            UTCTimeRangeType utr = pf.getTimeRange();
            TextMatchType tm = pf.getTextMatch();
            boolean isNotDefined = pf.getIsNotDefined() != null;
            boolean testPresent = !isNotDefined && utr == null && tm == null && Util.isEmpty(pf.getParamFilter());
            TimeRange tr = null;
            if (utr != null) {
                tr = Filters.makeTimeRange(utr);
            }
            if ((filter = Filters.makeFilter(pname, -1, isNotDefined, testPresent, tr, tm, andParams = "allof".equals(pf.getTest()), pf.getParamFilter())) != null) {
                if (andThem) {
                    pfilters = FilterBase.addAndChild(pfilters, filter);
                    continue;
                }
                pfilters = FilterBase.addOrChild(pfilters, filter);
                continue;
            }
            eq.postFilter = true;
            if (entityType == 0) {
                eq.eventFilters = Filters.addPropFilter(eq.eventFilters, pf);
                continue;
            }
            if (entityType == 2) {
                eq.todoFilters = Filters.addPropFilter(eq.todoFilters, pf);
                continue;
            }
            if (entityType == 3) {
                eq.journalFilters = Filters.addPropFilter(eq.journalFilters, pf);
                continue;
            }
            if (entityType != 1) continue;
            eq.alarmFilters = Filters.addPropFilter(eq.alarmFilters, pf);
        }
        return pfilters;
    }

    private static FilterBase makeFilter(String pname, int entityType, boolean testNotDefined, boolean testPresent, TimeRange timeRange, TextMatchType match, boolean andParamFilters, Collection<ParamFilterType> paramFilters) throws WebdavException {
        PropertyFilter filter = null;
        PropertyIndex.PropertyInfoIndex pi = PropertyIndex.PropertyInfoIndex.fromName(pname);
        if (pi == null) {
            throw new WebdavForbidden(CaldavTags.supportedFilter, "Unknown property " + pname);
        }
        if (testNotDefined) {
            filter = new PresenceFilter(null, pi, false);
        } else if (testPresent) {
            filter = new PresenceFilter(null, pi, true);
        } else if (timeRange != null) {
            filter = entityType < 0 ? ObjectFilter.makeFilter(null, pi, timeRange, null, null) : new EntityTimeRangeFilter(null, entityType, timeRange);
        } else if (match != null) {
            ObjectFilter<String> f = new ObjectFilter<String>(null, pi);
            f.setEntity(match.getValue());
            f.setExact(false);
            boolean caseless = match.getCollation().equals("i;ascii-casemap") && pi != PropertyIndex.PropertyInfoIndex.UID;
            f.setCaseless(caseless);
            f.setNot(match.getNegateCondition().equals("yes"));
            filter = f;
        } else if (Util.isEmpty(paramFilters)) {
            throw new WebdavBadRequest();
        }
        if (Util.isEmpty(paramFilters)) {
            return filter;
        }
        return FilterBase.addAndChild(filter, Filters.processParamFilters(pi, andParamFilters, paramFilters));
    }

    private static FilterBase processParamFilters(PropertyIndex.PropertyInfoIndex parentIndex, boolean andThem, Collection<ParamFilterType> paramFilters) throws WebdavException {
        FilterBase parfilters = null;
        for (ParamFilterType pf : paramFilters) {
            boolean testPresent;
            TextMatchType tm = pf.getTextMatch();
            boolean isNotDefined = pf.getIsNotDefined() != null;
            boolean bl = testPresent = isNotDefined && tm == null;
            PropertyFilter filter = (PropertyFilter)Filters.makeFilter(pf.getName(), -1, isNotDefined, testPresent, null, tm, false, null);
            if (filter == null) continue;
            filter.setParentPropertyIndex(parentIndex);
            if (andThem) {
                parfilters = FilterBase.addAndChild(parfilters, filter);
                continue;
            }
            parfilters = FilterBase.addOrChild(parfilters, filter);
        }
        return parfilters;
    }

    private static List<PropFilterType> addPropFilter(List<PropFilterType> pfs, PropFilterType val) {
        if (pfs == null) {
            pfs = new ArrayList<PropFilterType>();
        }
        pfs.add(val);
        return pfs;
    }
}

