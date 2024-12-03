/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.map.ObjectMapper
 */
package com.atlassian.analytics.event.logging;

import com.atlassian.analytics.event.AnalyticsEvent;
import com.atlassian.analytics.event.logging.LogEventFormatter;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

public class MerlinLogEventFormatter
implements LogEventFormatter {
    public static final String LOG_FORMAT_VERSION = "1.8";
    private static final Set<String> PROPERTY_BLACKLIST = ImmutableSet.of((Object)"timestamp", (Object)"sessionId");
    private static final Predicate<String> IS_NOT_BLACKLISTED = Predicates.not((Predicate)Predicates.in(PROPERTY_BLACKLIST));
    private static final TimeZone LOCAL_TIME_ZONE = TimeZone.getTimeZone("Australia/Sydney");
    public static final String DEFAULT_USER_VALUE = "-";
    public static final String DEFAULT_SEN_VALUE = "";
    public static final String DEFAULT_SOURCE_IP_VALUE = "";
    public static final String DEFAULT_SUB_PRODUCT_VALUE = "";
    public static final String DEFAULT_ALT_PATH_VALUE = "";
    public static final String DEFAULT_APP_ACCESS_VALUE = "";
    public static final String DELIMITER = "|";
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss,SSS";
    private final DateFormat localFormat = MerlinLogEventFormatter.isoDateFormat(LOCAL_TIME_ZONE);
    public static final String UTC_TIME_ZONE_CODE = "UTC";
    private static final TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");
    private final DateFormat utcFormat = MerlinLogEventFormatter.isoDateFormat(UTC_TIME_ZONE);
    private final ObjectMapper mapper = new ObjectMapper();

    private static DateFormat isoDateFormat(TimeZone timeZone) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);
        dateFormat.setTimeZone(timeZone);
        return dateFormat;
    }

    @Override
    public String formatEvent(AnalyticsEvent event) throws IOException {
        return this.formatEvent(event, this.utcFormat);
    }

    @Override
    public String formatEventLocal(AnalyticsEvent event) throws IOException {
        return this.formatEvent(event, this.localFormat);
    }

    private String formatEvent(AnalyticsEvent event, DateFormat format) throws IOException {
        String user = event.getUser() != null ? event.getUser() : DEFAULT_USER_VALUE;
        long clientTime = event.getClientTime();
        Date receivedTime = new Date(event.getReceivedTime());
        String sen = event.getSen() != null ? event.getSen() : "";
        String sourceIP = event.getSourceIP() != null ? event.getSourceIP() : "";
        String subProduct = event.getSubProduct() != null ? event.getSubProduct() : "";
        String atlPath = event.getAtlPath() != null ? event.getAtlPath() : "";
        String appAccess = event.getAppAccess() != null ? event.getAppAccess() : "";
        ArrayList<String> values = new ArrayList<String>();
        values.add(LOG_FORMAT_VERSION);
        values.add(format.format(receivedTime));
        values.add(event.getServer());
        values.add(event.getProduct());
        values.add(event.getVersion());
        values.add(user);
        values.add(event.getSession());
        values.add(event.getName());
        values.add(this.mapper.writeValueAsString(this.getProperties(event)));
        values.add(Long.toString(clientTime));
        values.add(sen);
        values.add(sourceIP);
        values.add(subProduct);
        values.add(atlPath);
        values.add(appAccess);
        return values.stream().map(StringUtils::trimToEmpty).collect(Collectors.joining(DELIMITER));
    }

    private Map<String, Object> getProperties(AnalyticsEvent event) {
        return Maps.filterKeys(event.getProperties(), IS_NOT_BLACKLISTED);
    }
}

