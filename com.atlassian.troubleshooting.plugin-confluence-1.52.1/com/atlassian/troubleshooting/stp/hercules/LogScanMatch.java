/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.troubleshooting.stp.hercules;

import com.atlassian.sisyphus.LogLine;
import com.atlassian.troubleshooting.stp.hercules.HerculesDateTimeUtils;
import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;

public class LogScanMatch
implements Comparable<LogScanMatch>,
Serializable {
    private static final long serialVersionUID = 1L;
    private static final String LOG_SCAN = "logScan";
    private static final String SCHEDULED_LOG_SCAN = "scheduledLogScan";
    private final String pageName;
    private final String patternId;
    private final String url;
    private final String sourceId;
    private String priority = "";
    private String status = "";
    private String resolution = "";
    private String fixVersion = "";
    private int count;
    private int latestLineNumber;
    private Date lastOccurence;
    private String logLevel;

    LogScanMatch(String sourceId, String patternId, String pageName, String url) {
        this.sourceId = sourceId;
        this.pageName = pageName;
        this.patternId = patternId;
        this.url = url;
    }

    LogScanMatch(String sourceId, String patternId, String pageName, String url, String priority, String status, String resolution, String fixVersion) {
        this.sourceId = sourceId;
        this.pageName = pageName;
        this.patternId = patternId;
        this.url = url;
        this.priority = priority;
        this.status = status;
        this.resolution = resolution;
        this.fixVersion = fixVersion;
    }

    public void add(LogLine line) {
        ++this.count;
        this.latestLineNumber = Math.max(line.getLineNo(), this.latestLineNumber);
        this.lastOccurence = line.getDate();
        this.logLevel = line.getLogLevel();
    }

    @Override
    public int compareTo(LogScanMatch o) {
        return o.latestLineNumber - this.latestLineNumber;
    }

    public int getCount() {
        return this.count;
    }

    public int getLatestLineNumber() {
        return this.latestLineNumber;
    }

    public String getLastOccurence() {
        if (this.lastOccurence != null) {
            return HerculesDateTimeUtils.getTimeInRelativeFormat(this.lastOccurence.getTime());
        }
        return "";
    }

    public String getLastTime() {
        if (this.lastOccurence != null) {
            return this.lastOccurence.toString();
        }
        return "";
    }

    public String getPatternId() {
        return this.patternId;
    }

    public String getPageName() {
        return this.pageName;
    }

    public String getLogScanUrl() {
        return this.getInstrumentedURL(LOG_SCAN);
    }

    public String getScheduledLogScanUrl() {
        return this.getInstrumentedURL(SCHEDULED_LOG_SCAN);
    }

    public String getUrl() {
        return this.url;
    }

    public String getSourceId() {
        return this.sourceId;
    }

    public String getLogLevel() {
        return this.logLevel;
    }

    public String getPriority() {
        return this.priority;
    }

    public String getStatus() {
        return this.status;
    }

    public String getResolution() {
        return this.resolution;
    }

    public String getFixVersion() {
        return this.fixVersion;
    }

    private String getInstrumentedURL(String medium) {
        if (StringUtils.isBlank((CharSequence)this.url) || StringUtils.isBlank((CharSequence)medium)) {
            return this.url;
        }
        String newUrl = this.url.trim();
        newUrl = newUrl + (newUrl.indexOf(63) == -1 ? (char)'?' : '&');
        newUrl = newUrl + "utm_source=STP";
        newUrl = newUrl + "&utm_medium=" + medium;
        return newUrl;
    }
}

