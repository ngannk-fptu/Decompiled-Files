/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.scheduler.config.IntervalScheduleInfo
 *  com.atlassian.scheduler.config.Schedule
 *  com.google.common.collect.ImmutableMap
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp.scheduler;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.scheduler.config.IntervalScheduleInfo;
import com.atlassian.scheduler.config.Schedule;
import com.atlassian.troubleshooting.stp.SimpleXsrfTokenGenerator;
import com.atlassian.troubleshooting.stp.ValidationLog;
import com.atlassian.troubleshooting.stp.events.StpScheduledLogScannerRanEvent;
import com.atlassian.troubleshooting.stp.hercules.LogScanReportSettings;
import com.atlassian.troubleshooting.stp.hercules.LogScanService;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.salext.mail.EmailValidator;
import com.atlassian.troubleshooting.stp.salext.mail.MailUtility;
import com.atlassian.troubleshooting.stp.scheduler.ScheduleFactory;
import com.atlassian.troubleshooting.stp.security.UserService;
import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class ScheduledHerculesHealthReportAction {
    protected static final String FREQUENCY_DAILY = "daily";
    protected static final String FREQUENCY_WEEKLY = "weekly";
    private static final Map<String, String> FREQUENCY_OPTIONS = ImmutableMap.of((Object)"daily", (Object)"stp.scheduler.frequency.daily", (Object)"weekly", (Object)"stp.scheduler.frequency.weekly");
    private static final long MINIMUM_WAIT_PERIOD_MS = TimeUnit.MINUTES.toMillis(2L);
    private static final Pattern COMMA_SPLIT = Pattern.compile("\\s*,\\s*");
    private final EventPublisher eventPublisher;
    private final LogScanService scanService;
    private final SupportApplicationInfo info;
    private final SimpleXsrfTokenGenerator tokenGenerator;
    private final ScheduleFactory scheduleFactory;
    private final UserService userService;
    private final MailUtility mailUtility;
    protected ValidationLog validationLog;
    private LogScanReportSettings settings;
    private boolean enabled;
    private int startHour = 0;
    private int startMinute = 0;
    private long intervalMillis = TimeUnit.DAYS.toMillis(1L);
    private Schedule schedule;
    private String frequency = "";
    private String recipients = "";

    @Autowired
    public ScheduledHerculesHealthReportAction(EventPublisher eventPublisher, LogScanService scanService, SupportApplicationInfo info, ScheduleFactory scheduleFactory, UserService userService, MailUtility mailUtility) {
        this.eventPublisher = eventPublisher;
        this.scanService = scanService;
        this.info = info;
        this.tokenGenerator = new SimpleXsrfTokenGenerator();
        this.scheduleFactory = scheduleFactory;
        this.userService = userService;
        this.mailUtility = mailUtility;
    }

    public Map<String, Object> getScannerSettings(HttpServletRequest req) {
        this.settings = this.scanService.getReportSettings();
        this.enabled = this.settings.isEnabled();
        this.recipients = this.settings.getRecipients();
        this.schedule = this.settings.getSchedule();
        if (this.recipients == null) {
            this.recipients = this.userService.getUserEmail().orElse("");
        }
        if (this.recipients == null) {
            this.recipients = "";
        }
        this.getSchedulerDetails(this.schedule);
        String frequency = this.getFrequencyName();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("isEnabled", this.enabled);
        map.put("recipients", this.recipients);
        map.put("startHour", this.startHour);
        map.put("startMinute", this.startMinute);
        map.put("frequencyOptions", FREQUENCY_OPTIONS);
        map.put("frequency", frequency);
        map.put("isMailServerConfigured", this.mailUtility.isMailServerConfigured());
        map.put("mailServerConfigurationUrl", this.info.getMailServerConfigurationURL(req));
        map.put("applicationName", this.info.getApplicationName());
        return map;
    }

    public Map<String, Object> storeScannerSettings(String isEnabled, int startHour, int startMinute, String frequency, String recipients, String token, HttpServletRequest req) {
        this.validationLog = new ValidationLog(this.info);
        HashMap<String, Object> map = new HashMap<String, Object>();
        if (!this.tokenGenerator.validateToken(req, token)) {
            this.validationLog.addError("stp.scheduler.xsrf.error", new Serializable[0]);
            map.put("tokenError", this.validationLog.getErrors().get(0).getBody());
            return map;
        }
        this.validateRequest(startHour, startMinute, this.parseFrequency(frequency));
        String tidiedRecipients = this.validateAndTidyRecipientsField(recipients);
        if (this.validationLog.hasErrors()) {
            map.put("errors", this.validationLog.getErrors().get(0).getBody());
            return map;
        }
        this.doAnalytics(isEnabled, frequency, startHour, startMinute);
        this.enabled = this.parseEnabled(isEnabled);
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.recipients = tidiedRecipients;
        this.frequency = frequency;
        this.intervalMillis = this.parseFrequency(frequency);
        this.schedule = this.scheduleFactory.createSchedule(this.intervalMillis, startHour, startMinute);
        boolean updated = this.storeSettings(this.schedule);
        if (updated || this.validationLog.hasFeedback()) {
            map.put("feedback", this.validationLog.getFeedback().get(0).getBody());
            return map;
        }
        return null;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public int getStartHour() {
        return this.startHour;
    }

    public int getStartMinute() {
        return this.startMinute;
    }

    public String getRecipients() {
        return this.recipients;
    }

    public ValidationLog getValidationLog() {
        return this.validationLog;
    }

    protected void validateRequest(int startHour, int startMinute, long intervalMillis) {
        if (startHour == -1 || startMinute == -1) {
            this.validationLog.addError("stp.scheduler.invalid.start.time", new Serializable[0]);
        }
        if (intervalMillis < MINIMUM_WAIT_PERIOD_MS) {
            this.validationLog.addError("stp.scheduler.invalid.frequency", new Serializable[0]);
        }
    }

    protected String validateAndTidyRecipientsField(String recipients) {
        CharSequence[] recipientsArr = recipients != null ? COMMA_SPLIT.split(recipients.trim()) : null;
        StringBuilder invalidRecipients = new StringBuilder();
        if (recipientsArr == null || recipientsArr.length == 0 || recipientsArr.length == 1 && recipientsArr[0].isEmpty()) {
            this.validationLog.addError("stp.scheduler.missing.recipients", new Serializable[0]);
            return recipients;
        }
        for (String string : recipientsArr) {
            if (EmailValidator.isValidEmailAddress(string)) continue;
            invalidRecipients.append(string).append(", ");
        }
        if (invalidRecipients.length() > 0) {
            invalidRecipients.deleteCharAt(invalidRecipients.length() - 2);
            this.validationLog.addError("stp.scheduler.invalid.recipient", new Serializable[]{invalidRecipients.toString()});
            return recipients;
        }
        return String.join((CharSequence)", ", recipientsArr);
    }

    protected String getFrequencyName() {
        if (this.intervalMillis >= 0L) {
            return this.frequencyToName(this.intervalMillis);
        }
        if (this.schedule == null || this.schedule.getIntervalScheduleInfo() == null) {
            return FREQUENCY_DAILY;
        }
        return this.frequencyToName(this.schedule.getIntervalScheduleInfo().getIntervalInMillis());
    }

    private void getSchedulerDetails(Schedule schedule) {
        IntervalScheduleInfo scheduleInfo;
        if (schedule != null && schedule.getIntervalScheduleInfo() != null && (scheduleInfo = schedule.getIntervalScheduleInfo()).getFirstRunTime() != null) {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(scheduleInfo.getFirstRunTime());
            this.startHour = cal.get(11);
            this.startMinute = cal.get(12);
            this.intervalMillis = scheduleInfo.getIntervalInMillis();
        }
    }

    private String frequencyToName(long intervalMillis) {
        if (intervalMillis == TimeUnit.DAYS.toMillis(1L)) {
            return FREQUENCY_DAILY;
        }
        if (intervalMillis == TimeUnit.DAYS.toMillis(7L)) {
            return FREQUENCY_WEEKLY;
        }
        return null;
    }

    private boolean storeSettings(Schedule newSchedule) {
        this.settings = this.scanService.getReportSettings();
        LogScanReportSettings newSettings = new LogScanReportSettings.Builder().enabled(this.enabled).recipients(this.recipients).schedule(newSchedule).build();
        if (newSettings.equals(this.settings)) {
            this.validationLog.addFeedback("stp.scheduler.task.unchanged", new Serializable[0]);
            return false;
        }
        this.scanService.setReportSettings(newSettings);
        this.settings = newSettings;
        if (this.enabled) {
            this.validationLog.addFeedback("stp.scheduler.task.enabled", new Serializable[]{this.frequency, newSchedule.getIntervalScheduleInfo().getFirstRunTime()});
        } else {
            this.validationLog.addFeedback("stp.scheduler.task.disabled", new Serializable[0]);
        }
        return true;
    }

    private boolean parseEnabled(String isEnabled) {
        return StringUtils.isNotEmpty((CharSequence)isEnabled) && "on".equalsIgnoreCase(isEnabled);
    }

    private long parseFrequency(String frequency) {
        if (FREQUENCY_WEEKLY.equals(frequency)) {
            return TimeUnit.DAYS.toMillis(7L);
        }
        if (FREQUENCY_DAILY.equals(frequency)) {
            return TimeUnit.DAYS.toMillis(1L);
        }
        if (StringUtils.isNotEmpty((CharSequence)frequency) && StringUtils.isNumeric((CharSequence)frequency)) {
            return Integer.parseInt(frequency);
        }
        return -1L;
    }

    private void doAnalytics(String isEnabled, String frequency, int startHour, int startMinute) {
        String time = startHour + ":" + startMinute;
        StpScheduledLogScannerRanEvent event = new StpScheduledLogScannerRanEvent(this.parseEnabled(isEnabled), time, frequency);
        this.eventPublisher.publish((Object)event);
    }
}

