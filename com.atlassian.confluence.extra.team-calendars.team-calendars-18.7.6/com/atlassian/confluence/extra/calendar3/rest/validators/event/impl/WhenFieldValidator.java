/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.ws.rs.WebApplicationException
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.joda.time.LocalDate
 *  org.joda.time.ReadableInstant
 *  org.joda.time.ReadablePartial
 *  org.joda.time.format.DateTimeFormat
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.rest.validators.event.impl;

import com.atlassian.confluence.extra.calendar3.CalendarSettingsManager;
import com.atlassian.confluence.extra.calendar3.rest.param.UpdateEventParam;
import com.atlassian.confluence.extra.calendar3.rest.validators.event.AbstractEventValidator;
import com.atlassian.confluence.extra.calendar3.util.TimeZoneUtil;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.ws.rs.WebApplicationException;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WhenFieldValidator
extends AbstractEventValidator {
    private static Logger LOG = LoggerFactory.getLogger(WhenFieldValidator.class);
    private final CalendarSettingsManager calendarSettingsManager;

    @Autowired
    public WhenFieldValidator(@ComponentImport LocaleManager localeManager, @ComponentImport I18NBeanFactory i18NBeanFactory, CalendarSettingsManager calendarSettingsManager) {
        super(localeManager, i18NBeanFactory);
        this.calendarSettingsManager = calendarSettingsManager;
    }

    @Override
    public boolean isValid(UpdateEventParam param, Map<String, List<String>> fieldErrors) throws WebApplicationException {
        DateTime end;
        DateTime start;
        String userTimeZoneId = param.getUserTimeZoneId();
        DateTimeZone userTimeZone = DateTimeZone.forID((String)userTimeZoneId);
        DateTime now = new DateTime(userTimeZone);
        boolean dragAndDropUpdate = param.isDragAndDropUpdate();
        boolean allDayEvent = param.isAllDayEvent();
        DateTime dateTime = dragAndDropUpdate ? DateTimeFormat.forPattern((String)"ddMMyyyyHHmm").withZone(userTimeZone).parseDateTime(param.getStartDateDnd()) : (start = TimeZoneUtil.tryParseDateTimeStringForEventEdit(this.localeManager, param.getStartDate(), allDayEvent ? null : param.getStartTime(), userTimeZone, this.calendarSettingsManager.isTimeFormat24Hour() ? "H:mm" : "h:mm a"));
        if (start == null) {
            this.addFieldError(fieldErrors, "when", this.getText(allDayEvent ? "calendar3.error.invaliddate" : "calendar3.error.invaliddatetime", Arrays.asList(TimeZoneUtil.getDateFormatter(this.localeManager, userTimeZone).print((ReadableInstant)now), TimeZoneUtil.getTimeFormatter(this.localeManager, userTimeZone, this.calendarSettingsManager.isTimeFormat24Hour() ? "H:mm" : "h:mm a").print((ReadableInstant)now))));
        }
        DateTime dateTime2 = dragAndDropUpdate ? DateTimeFormat.forPattern((String)"ddMMyyyyHHmm").withZone(userTimeZone).parseDateTime(param.getEndDateDnd()) : (end = TimeZoneUtil.tryParseDateTimeStringForEventEdit(this.localeManager, param.getEndDate(), allDayEvent ? null : param.getEndTime(), userTimeZone, this.calendarSettingsManager.isTimeFormat24Hour() ? "H:mm" : "h:mm a"));
        if (end == null && !param.isSingleJiraDate()) {
            this.addFieldError(fieldErrors, "when", this.getText(allDayEvent ? "calendar3.error.invaliddate" : "calendar3.error.invaliddatetime", Arrays.asList(TimeZoneUtil.getDateFormatter(this.localeManager, userTimeZone).print((ReadableInstant)now), TimeZoneUtil.getTimeFormatter(this.localeManager, userTimeZone, this.calendarSettingsManager.isTimeFormat24Hour() ? "H:mm" : "h:mm a").print((ReadableInstant)now))));
        }
        if (StringUtils.isNotEmpty(param.getUntil())) {
            LocalDate untilDate = TimeZoneUtil.tryParseBasicDateStringForEventEdit(param.getUntil());
            if (untilDate == null) {
                this.addFieldError(fieldErrors, "until", this.getText("calendar3.error.invaliddate", Arrays.asList(TimeZoneUtil.getDateFormatter(this.localeManager, userTimeZone).print((ReadableInstant)now), TimeZoneUtil.getTimeFormatter(this.localeManager, userTimeZone, this.calendarSettingsManager.isTimeFormat24Hour() ? "H:mm" : "h:mm a").print((ReadableInstant)now))));
            } else if (start != null && untilDate.isBefore((ReadablePartial)start.toLocalDate())) {
                this.addFieldError(fieldErrors, "until", this.getText("calendar3.error.repeatendsbeforestart", Arrays.asList(TimeZoneUtil.getDateFormatter(this.localeManager, userTimeZone).print((ReadableInstant)now), TimeZoneUtil.getTimeFormatter(this.localeManager, userTimeZone, this.calendarSettingsManager.isTimeFormat24Hour() ? "H:mm" : "h:mm a").print((ReadableInstant)now))));
            }
        }
        if (start == null || end == null || param.isSingleJiraDate()) {
            return true;
        }
        if (allDayEvent && start.isAfter((ReadableInstant)end)) {
            this.addFieldError(fieldErrors, "when", this.getText("calendar3.error.startafterend"));
        } else if (!allDayEvent && (start.isAfter((ReadableInstant)end) || start.isEqual((ReadableInstant)end))) {
            this.addFieldError(fieldErrors, "when", this.getText("calendar3.error.startafterorequalend"));
        }
        return true;
    }
}

