/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.analytics.client.eventfilter;

import com.atlassian.analytics.client.eventfilter.parser.SimpleListParser;
import com.atlassian.analytics.client.eventfilter.reader.FilterListReader;
import com.atlassian.analytics.client.eventfilter.reader.LocalListReader;
import com.atlassian.analytics.client.eventfilter.reader.RemoteListReader;
import com.atlassian.analytics.client.properties.AnalyticsPropertyService;
import com.atlassian.analytics.client.s3.AnalyticsS3Client;
import com.atlassian.analytics.event.RawEvent;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlacklistFilter {
    private static final Logger LOG = LoggerFactory.getLogger(BlacklistFilter.class);
    private final AnalyticsPropertyService applicationProperties;
    private Set<String> blacklistEventNames;
    private Set<Pattern> blacklistEventNamePatterns;
    private final AnalyticsS3Client analyticsS3Client;

    public BlacklistFilter(AnalyticsPropertyService applicationProperties, AnalyticsS3Client analyticsS3Client) {
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties can't be null");
        this.analyticsS3Client = Objects.requireNonNull(analyticsS3Client, "analyticsS3Client can't be null");
        this.readBlacklist(new LocalListReader());
    }

    private String getProductName() {
        return this.applicationProperties.getDisplayName().toLowerCase();
    }

    public static String getListName(String appName) {
        return "blacklist_" + appName + ".txt";
    }

    public void readRemoteList() {
        try {
            this.readBlacklist(new RemoteListReader(this.analyticsS3Client));
        }
        catch (Exception e) {
            LOG.debug("Couldn't read the remote blacklist, keeping the local blacklist for now - exception: ", (Throwable)e);
        }
    }

    public void readBlacklist(FilterListReader filterListReader) {
        SimpleListParser simpleListParser = new SimpleListParser(filterListReader);
        Set<String> eventNames = simpleListParser.readSimpleFilterList(BlacklistFilter.getListName(this.getProductName()));
        this.blacklistEventNames = new HashSet<String>();
        this.blacklistEventNamePatterns = new HashSet<Pattern>();
        if (eventNames != null) {
            for (String eventName : eventNames) {
                if (eventName.contains("*")) {
                    Pattern pattern = Pattern.compile("^" + eventName.replaceAll("\\.", "\\\\.").replaceAll("\\*", "\\.\\*") + "$");
                    this.blacklistEventNamePatterns.add(pattern);
                    continue;
                }
                this.blacklistEventNames.add(eventName);
            }
        }
    }

    public boolean isEventBlacklisted(RawEvent event) {
        String eventName = event.getName();
        if (this.blacklistEventNames.contains(eventName)) {
            return true;
        }
        for (Pattern blacklistNamePattern : this.blacklistEventNamePatterns) {
            if (!blacklistNamePattern.matcher(eventName).matches()) continue;
            return true;
        }
        return false;
    }
}

