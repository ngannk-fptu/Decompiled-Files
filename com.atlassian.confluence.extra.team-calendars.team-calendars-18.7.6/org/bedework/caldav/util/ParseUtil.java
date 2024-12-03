/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util;

import ietf.params.xml.ns.caldav.UTCTimeRangeType;
import java.util.Calendar;
import net.fortuna.ical4j.model.DateTime;
import org.bedework.caldav.util.TimeRange;
import org.bedework.util.timezones.DateTimeUtil;
import org.bedework.util.xml.tagdefs.CaldavTags;
import org.bedework.webdav.servlet.shared.WebdavBadRequest;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ParseUtil {
    public static TimeRange parseTimeRange(Node nd, boolean required) throws WebdavException {
        DateTime start = null;
        DateTime end = null;
        NamedNodeMap nnm = nd.getAttributes();
        if (nnm == null) {
            throw new WebdavBadRequest(CaldavTags.validFilter, "Infinite time range");
        }
        int attrCt = nnm.getLength();
        if (attrCt == 0 || required && attrCt != 2) {
            throw new WebdavBadRequest(CaldavTags.validFilter, "Infinite time range");
        }
        try {
            String dt;
            Node nmAttr = nnm.getNamedItem("start");
            if (nmAttr != null) {
                --attrCt;
                dt = nmAttr.getNodeValue();
                if (!ParseUtil.checkUTC(dt)) {
                    throw new WebdavBadRequest(CaldavTags.validFilter, "Not UTC");
                }
                start = new DateTime(dt);
            } else if (required) {
                throw new WebdavBadRequest(CaldavTags.validFilter, "Missing start");
            }
            nmAttr = nnm.getNamedItem("end");
            if (nmAttr != null) {
                --attrCt;
                dt = nmAttr.getNodeValue();
                if (!ParseUtil.checkUTC(dt)) {
                    throw new WebdavBadRequest(CaldavTags.validFilter, "Not UTC");
                }
                end = new DateTime(dt);
            } else if (required) {
                throw new WebdavBadRequest(CaldavTags.validFilter, "Missing end");
            }
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavBadRequest(CaldavTags.validFilter, "Invalid time-range");
        }
        if (attrCt != 0) {
            throw new WebdavBadRequest(CaldavTags.validFilter);
        }
        return new TimeRange(start, end);
    }

    public static UTCTimeRangeType parseUTCTimeRange(UTCTimeRangeType val, Node nd, boolean required) throws WebdavException {
        String st = null;
        String et = null;
        NamedNodeMap nnm = nd.getAttributes();
        if (nnm == null) {
            throw new WebdavBadRequest(CaldavTags.validFilter, "Infinite time range");
        }
        int attrCt = nnm.getLength();
        if (attrCt == 0 || required && attrCt != 2) {
            throw new WebdavBadRequest(CaldavTags.validFilter, "Infinite time range");
        }
        try {
            Node nmAttr = nnm.getNamedItem("start");
            if (nmAttr != null) {
                --attrCt;
                st = nmAttr.getNodeValue();
                if (!ParseUtil.checkUTC(st)) {
                    throw new WebdavBadRequest(CaldavTags.validFilter, "Not UTC");
                }
            } else if (required) {
                throw new WebdavBadRequest(CaldavTags.validFilter, "Missing start");
            }
            if ((nmAttr = nnm.getNamedItem("end")) != null) {
                --attrCt;
                et = nmAttr.getNodeValue();
                if (!ParseUtil.checkUTC(et)) {
                    throw new WebdavBadRequest(CaldavTags.validFilter, "Not UTC");
                }
            } else if (required) {
                throw new WebdavBadRequest(CaldavTags.validFilter, "Missing end");
            }
            if (attrCt != 0) {
                throw new WebdavBadRequest(CaldavTags.validFilter);
            }
            if (val == null) {
                UTCTimeRangeType utr = new UTCTimeRangeType();
                utr.setStart(st);
                utr.setEnd(et);
                return utr;
            }
            if (st != null) {
                val.setStart(st);
            }
            if (et != null) {
                val.setEnd(et);
            }
            return val;
        }
        catch (WebdavException wde) {
            throw wde;
        }
        catch (Throwable t) {
            throw new WebdavBadRequest(CaldavTags.validFilter, "Invalid time-range");
        }
    }

    public static TimeRange getPeriod(String start, String end, int defaultField, int defaultVal, int maxField, int maxVal) throws WebdavException {
        Calendar startCal = Calendar.getInstance();
        startCal.set(11, 0);
        startCal.set(12, 0);
        startCal.set(13, 0);
        Calendar endCal = Calendar.getInstance();
        endCal.set(11, 0);
        endCal.set(12, 0);
        endCal.set(13, 0);
        try {
            if (start != null) {
                startCal.setTime(DateTimeUtil.fromDate(start));
            }
            if (end == null) {
                endCal.add(defaultField, defaultVal);
            } else {
                endCal.setTime(DateTimeUtil.fromDate(end));
            }
        }
        catch (DateTimeUtil.BadDateException bde) {
            throw new WebdavBadRequest();
        }
        if (maxVal > 0) {
            Calendar check = Calendar.getInstance();
            check.setTime(startCal.getTime());
            check.add(maxField, maxVal);
            if (check.before(endCal)) {
                return null;
            }
        }
        return new TimeRange(new DateTime(startCal.getTime()), new DateTime(endCal.getTime()));
    }

    private static boolean checkUTC(String val) {
        if (val.length() != 16) {
            return false;
        }
        byte[] b = val.getBytes();
        if (b[8] != 84) {
            return false;
        }
        return b[15] == 90;
    }
}

