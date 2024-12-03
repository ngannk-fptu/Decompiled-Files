/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.server.soap;

import ietf.params.xml.ns.caldav.CompFilterType;
import ietf.params.xml.ns.caldav.FilterType;
import ietf.params.xml.ns.caldav.IsNotDefinedType;
import ietf.params.xml.ns.caldav.ParamFilterType;
import ietf.params.xml.ns.caldav.PropFilterType;
import ietf.params.xml.ns.caldav.UTCTimeRangeType;
import ietf.params.xml.ns.icalendar_2.IcalendarType;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import org.bedework.caldav.server.CaldavReportMethod;
import org.bedework.util.calendar.XcalUtil;
import org.bedework.webdav.servlet.common.PropFindMethod;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavNsIntf;
import org.bedework.webdav.servlet.shared.WebdavNsNode;
import org.oasis_open.docs.ws_calendar.ns.soap.CalendarQueryType;
import org.oasis_open.docs.ws_calendar.ns.soap.ExpandType;
import org.oasis_open.docs.ws_calendar.ns.soap.LimitRecurrenceSetType;
import org.oasis_open.docs.ws_calendar.ns.soap.TextMatchType;
import org.w3c.dom.Document;

public class ReportBase
extends CaldavReportMethod {
    public ReportBase(WebdavNsIntf nsIntf) {
        this.nsIntf = nsIntf;
        this.xml = nsIntf.getXmlEmit();
    }

    public Document query(String qstring, String resourceUri) throws WebdavException {
        this.pm = new PropFindMethod();
        this.pm.init(this.getNsIntf(), true);
        Document doc = this.parseContent(qstring.length(), new StringReader(qstring));
        this.processDoc(doc);
        try {
            StringWriter sw = new StringWriter();
            this.xml.startEmit(sw);
            this.cqpars.depth = 1;
            this.process(this.cqpars, resourceUri);
            String s = sw.toString();
            return this.parseContent(s.length(), new StringReader(s));
        }
        catch (WebdavException we) {
            throw we;
        }
        catch (Throwable t) {
            throw new WebdavException(t);
        }
    }

    public Collection<WebdavNsNode> query(String resourceUri, CalendarQueryType cq) throws WebdavException {
        WebdavNsNode node = this.getNsIntf().getNode(resourceUri, 1, 3, false);
        CaldavReportMethod.CalendarQueryPars cqp = new CaldavReportMethod.CalendarQueryPars();
        cqp.filter = this.convertFilter(cq.getFilter());
        cqp.depth = 1;
        return this.doNodeAndChildren(cqp, node, this.convertExpand(cq.getExpand()), this.convertLimitRecurrenceSet(cq.getLimitRecurrenceSet()), null);
    }

    IcalendarType fetch(String resourceUri, String uid) throws WebdavException {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version='1.0' encoding='utf-8' ?>");
        sb.append("<C:calendar-query xmlns:C='urn:ietf:params:xml:ns:caldav'>");
        sb.append("  <D:prop xmlns:D='DAV:'>");
        sb.append("    <C:calendar-data content-type='application/calendar+xml'>");
        sb.append("      <C:comp name='VCALENDAR'>");
        sb.append("        <C:comp name='VEVENT'>");
        sb.append("        </C:comp>");
        sb.append("        <C:comp name='VTODO'>");
        sb.append("        </C:comp>");
        sb.append("      </C:comp>");
        sb.append("    </C:calendar-data>");
        sb.append("  </D:prop>");
        sb.append("  <C:filter>");
        sb.append("    <C:comp-filter name='VCALENDAR'>");
        sb.append("    </C:comp-filter>");
        sb.append("  </C:filter>");
        sb.append("</C:calendar-query>");
        this.pm = new PropFindMethod();
        this.pm.init(this.getNsIntf(), true);
        Document doc = this.parseContent(sb.length(), new StringReader(sb.toString()));
        this.processDoc(doc);
        this.cqpars.depth = 1;
        this.process(this.cqpars, resourceUri);
        return null;
    }

    private FilterType convertFilter(org.oasis_open.docs.ws_calendar.ns.soap.FilterType val) {
        if (val == null) {
            return null;
        }
        FilterType filter = new FilterType();
        filter.setCompFilter(this.convertCompFilter(val.getCompFilter()));
        return filter;
    }

    private CompFilterType convertCompFilter(org.oasis_open.docs.ws_calendar.ns.soap.CompFilterType val) {
        if (val == null) {
            return null;
        }
        CompFilterType compFilter = new CompFilterType();
        if (val.getIsNotDefined() != null) {
            compFilter.setIsNotDefined(new IsNotDefinedType());
        }
        if (val.getTest() != null) {
            compFilter.setTest(val.getTest());
        }
        compFilter.setTimeRange(this.convertTimeRange(val.getTimeRange()));
        for (org.oasis_open.docs.ws_calendar.ns.soap.PropFilterType pf : val.getPropFilter()) {
            compFilter.getPropFilter().add(this.convertPropFilter(pf));
        }
        for (org.oasis_open.docs.ws_calendar.ns.soap.CompFilterType cf : val.getCompFilter()) {
            compFilter.getCompFilter().add(this.convertCompFilter(cf));
        }
        if (val.getAnyComp() != null) {
            compFilter.setName("*");
        } else if (val.getVcalendar() != null) {
            compFilter.setName("vcalendar");
        } else {
            compFilter.setName(val.getBaseComponent().getName().getLocalPart());
        }
        return compFilter;
    }

    private UTCTimeRangeType convertTimeRange(org.oasis_open.docs.ws_calendar.ns.soap.UTCTimeRangeType val) {
        if (val == null) {
            return null;
        }
        UTCTimeRangeType res = new UTCTimeRangeType();
        res.setStart(XcalUtil.getIcalFormatDateTime(val.getStart()));
        res.setEnd(XcalUtil.getIcalFormatDateTime(val.getEnd()));
        return res;
    }

    private ietf.params.xml.ns.caldav.ExpandType convertExpand(ExpandType val) {
        if (val == null) {
            return null;
        }
        ietf.params.xml.ns.caldav.ExpandType res = new ietf.params.xml.ns.caldav.ExpandType();
        res.setStart(XcalUtil.getIcalFormatDateTime(val.getStart()));
        res.setEnd(XcalUtil.getIcalFormatDateTime(val.getEnd()));
        return res;
    }

    private ietf.params.xml.ns.caldav.LimitRecurrenceSetType convertLimitRecurrenceSet(LimitRecurrenceSetType val) {
        if (val == null) {
            return null;
        }
        ietf.params.xml.ns.caldav.LimitRecurrenceSetType res = new ietf.params.xml.ns.caldav.LimitRecurrenceSetType();
        res.setStart(XcalUtil.getIcalFormatDateTime(val.getStart()));
        res.setEnd(XcalUtil.getIcalFormatDateTime(val.getEnd()));
        return res;
    }

    private PropFilterType convertPropFilter(org.oasis_open.docs.ws_calendar.ns.soap.PropFilterType val) {
        PropFilterType pf = new PropFilterType();
        if (val.getIsNotDefined() != null) {
            pf.setIsNotDefined(new IsNotDefinedType());
        }
        if (val.getTest() != null) {
            pf.setTest(val.getTest());
        }
        pf.setTimeRange(this.convertTimeRange(val.getTimeRange()));
        pf.setTextMatch(this.convertTextMatch(val.getTextMatch()));
        for (org.oasis_open.docs.ws_calendar.ns.soap.ParamFilterType parf : val.getParamFilter()) {
            pf.getParamFilter().add(this.convertParamFilter(parf));
        }
        pf.setName(val.getBaseProperty().getName().getLocalPart());
        return pf;
    }

    private ietf.params.xml.ns.caldav.TextMatchType convertTextMatch(TextMatchType val) {
        ietf.params.xml.ns.caldav.TextMatchType tm = new ietf.params.xml.ns.caldav.TextMatchType();
        tm.setValue(val.getValue());
        tm.setCollation(val.getCollation());
        if (val.isNegateCondition()) {
            tm.setNegateCondition("yes");
        } else {
            tm.setNegateCondition("no");
        }
        return tm;
    }

    private ParamFilterType convertParamFilter(org.oasis_open.docs.ws_calendar.ns.soap.ParamFilterType val) {
        ParamFilterType pf = new ParamFilterType();
        if (val.getIsNotDefined() != null) {
            pf.setIsNotDefined(new IsNotDefinedType());
        }
        pf.setTextMatch(this.convertTextMatch(val.getTextMatch()));
        pf.setName(val.getBaseParameter().getName().getLocalPart());
        return pf;
    }
}

