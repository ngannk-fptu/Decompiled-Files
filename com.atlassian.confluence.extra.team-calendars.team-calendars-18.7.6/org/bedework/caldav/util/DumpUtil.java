/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util;

import ietf.params.xml.ns.caldav.CalendarDataType;
import ietf.params.xml.ns.caldav.CompFilterType;
import ietf.params.xml.ns.caldav.CompType;
import ietf.params.xml.ns.caldav.FilterType;
import ietf.params.xml.ns.caldav.ParamFilterType;
import ietf.params.xml.ns.caldav.PropFilterType;
import ietf.params.xml.ns.caldav.PropType;
import ietf.params.xml.ns.caldav.TextMatchType;
import ietf.params.xml.ns.caldav.UTCTimeRangeType;
import org.apache.log4j.Logger;

public class DumpUtil {
    public static void dumpCalendarData(CalendarDataType cd, Logger log) {
        StringBuffer sb = new StringBuffer("  <calendar-data");
        if (cd.getContentType() != null) {
            sb.append("  content-type=\"");
            sb.append(cd.getContentType());
            sb.append("\"");
        }
        sb.append(">");
        log.debug(sb.toString());
        if (cd.getComp() != null) {
            DumpUtil.dumpComp(cd.getComp(), log, "    ");
        }
        if (cd.getExpand() != null) {
            DumpUtil.dumpUTCTimeRange(cd.getExpand(), "expand", log, "    ");
        }
        if (cd.getLimitRecurrenceSet() != null) {
            DumpUtil.dumpUTCTimeRange(cd.getLimitRecurrenceSet(), "limit-recurrence-set", log, "    ");
        }
        if (cd.getLimitFreebusySet() != null) {
            DumpUtil.dumpUTCTimeRange(cd.getLimitFreebusySet(), "limit-freebusy-set", log, "    ");
        }
        log.debug("  </calendar-data>");
    }

    public static void dumpComp(CompType comp, Logger log, String indent) {
        StringBuffer sb = new StringBuffer(indent);
        sb.append("<comp name=");
        sb.append(comp.getName());
        sb.append(">");
        log.debug(sb.toString());
        if (comp.getAllcomp() != null) {
            log.debug(indent + "  <allcomp/>");
        } else {
            for (CompType c : comp.getComp()) {
                DumpUtil.dumpComp(c, log, indent + "  ");
            }
        }
        if (comp.getAllprop() != null) {
            log.debug(indent + "  <allprop/>");
        } else {
            for (PropType prop : comp.getProp()) {
                DumpUtil.dumpProp(prop, log, indent + "  ");
            }
        }
        log.debug(indent + "</comp>");
    }

    public static void dumpProp(PropType prop, Logger log, String indent) {
        StringBuffer sb = new StringBuffer(indent);
        sb.append("<calddav:prop name=");
        sb.append(prop.getName());
        sb.append(" novalue=");
        sb.append(prop.getNovalue());
        sb.append("/>");
        log.debug(sb.toString());
    }

    public static void dumpUTCTimeRange(UTCTimeRangeType tr, String name, Logger log, String indent) {
        StringBuilder sb = new StringBuilder(indent);
        sb.append("<");
        sb.append(name);
        sb.append(" ");
        if (tr.getStart() != null) {
            sb.append("start=");
            sb.append(tr.getStart());
        }
        if (tr.getEnd() != null) {
            if (tr.getStart() != null) {
                sb.append(" ");
            }
            sb.append("end=");
            sb.append(tr.getEnd());
        }
        sb.append("/>");
        log.debug(sb.toString());
    }

    public static void dumpFilter(FilterType f, Logger log) {
        log.debug("<filter>");
        DumpUtil.dumpCompFilter(f.getCompFilter(), log, "  ");
        log.debug("</filter>");
    }

    public static void dumpCompFilter(CompFilterType cf, Logger log, String indent) {
        StringBuilder sb = new StringBuilder(indent);
        sb.append("<comp-filter name=\"");
        sb.append(cf.getName());
        sb.append("\">");
        log.debug(sb.toString());
        if (cf.getIsNotDefined() != null) {
            log.debug(indent + "  <is-not-defined/>");
        } else if (cf.getTimeRange() != null) {
            DumpUtil.dumpUTCTimeRange(cf.getTimeRange(), "time-range", log, indent + "  ");
        }
        if (cf.getCompFilter() != null) {
            for (CompFilterType subcf : cf.getCompFilter()) {
                DumpUtil.dumpCompFilter(subcf, log, indent + "  ");
            }
        }
        if (cf.getPropFilter() != null) {
            for (PropFilterType pf : cf.getPropFilter()) {
                DumpUtil.dumpPropFilter(pf, log, indent + "  ");
            }
        }
        log.debug(indent + "</comp-filter>");
    }

    public static void dumpPropFilter(PropFilterType pf, Logger log, String indent) {
        StringBuilder sb = new StringBuilder(indent);
        sb.append("<prop-filter name=\"");
        sb.append(pf.getName());
        sb.append("\">\n");
        log.debug(sb.toString());
        if (pf.getIsNotDefined() != null) {
            log.debug(indent + "  <is-not-defined/>\n");
        } else if (pf.getTimeRange() != null) {
            DumpUtil.dumpUTCTimeRange(pf.getTimeRange(), "time-range", log, indent + "  ");
        } else if (pf.getTextMatch() != null) {
            DumpUtil.dumpTextMatch(pf.getTextMatch(), log, indent + "  ");
        }
        if (pf.getParamFilter() != null) {
            for (ParamFilterType parf : pf.getParamFilter()) {
                DumpUtil.dumpParamFilter(parf, log, indent + "  ");
            }
        }
        log.debug(indent + "</prop-filter>");
    }

    public static void dumpParamFilter(ParamFilterType pf, Logger log, String indent) {
        StringBuilder sb = new StringBuilder(indent);
        sb.append("<param-filter name=\"");
        sb.append(pf.getName());
        sb.append(">\n");
        log.debug(sb.toString());
        if (pf.getIsNotDefined() != null) {
            log.debug(indent + "  <is-not-defined/>\n");
        } else {
            DumpUtil.dumpTextMatch(pf.getTextMatch(), log, indent + "  ");
        }
        log.debug(indent + "</param-filter>");
    }

    public static void dumpTextMatch(TextMatchType tm, Logger log, String indent) {
        StringBuilder sb = new StringBuilder(indent);
        sb.append("<text-match");
        sb.append(" collation=");
        sb.append(tm.getCollation());
        sb.append(" negate-condition=");
        sb.append(tm.getNegateCondition());
        sb.append(">");
        log.debug(sb.toString());
        log.debug(tm.getValue());
        log.debug(indent + "</text-match>\n");
    }
}

