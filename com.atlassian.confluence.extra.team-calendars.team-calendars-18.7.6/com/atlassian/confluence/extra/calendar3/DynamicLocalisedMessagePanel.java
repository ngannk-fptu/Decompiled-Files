/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.web.model.WebPanel
 *  com.atlassian.user.User
 *  com.google.common.collect.Maps
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.joda.time.ReadableInstant
 *  org.joda.time.format.DateTimeFormat
 *  org.joda.time.format.DateTimeFormatter
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.web.model.WebPanel;
import com.atlassian.user.User;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DynamicLocalisedMessagePanel
implements WebPanel {
    private static final Collection<String> MONTHS_NAMES = Arrays.asList("january", "february", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december");
    private static final Collection<String> DAY_NAMES = Arrays.asList("sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday");
    private final LocaleManager localeManager;
    private final TemplateRenderer templateRenderer;

    public DynamicLocalisedMessagePanel(TemplateRenderer templateRenderer, LocaleManager localeManager) {
        this.templateRenderer = templateRenderer;
        this.localeManager = localeManager;
    }

    public String getHtml(Map<String, Object> contextMap) {
        HashMap templateContext = Maps.newHashMap();
        templateContext.putAll(contextMap);
        templateContext.put("i18nMessages", new HashMap<String, String>(){
            {
                this.putAll(DynamicLocalisedMessagePanel.this.getMonthNamesI18n(MONTH_FORMATS.MMMM));
                this.putAll(DynamicLocalisedMessagePanel.this.getMonthNamesI18n(MONTH_FORMATS.MMM));
                this.putAll(DynamicLocalisedMessagePanel.this.getDayNamesI18n(DAY_FORMATS.EEE));
                this.putAll(DynamicLocalisedMessagePanel.this.getDayNamesI18n(DAY_FORMATS.EEEE));
            }
        }.entrySet());
        StringBuilder output = new StringBuilder();
        this.templateRenderer.renderTo((Appendable)output, "com.atlassian.confluence.extra.team-calendars:server-soy-templates", "Confluence.TeamCalendars.Server.Templates.messages.soy", (Map)templateContext);
        return output.toString();
    }

    public void writeHtml(Writer writer, Map<String, Object> contextMap) throws IOException {
        writer.write(this.getHtml(contextMap));
    }

    private Map<String, String> getMonthNamesI18n(MONTH_FORMATS month_formats) {
        StringBuilder i18nMonthNameBuilder = new StringBuilder();
        HashMap<String, String> i18nMonthNames = new HashMap<String, String>();
        DateTime dateTime = new DateTime(DateTimeZone.UTC).withMonthOfYear(1);
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern((String)month_formats.name()).withLocale(this.getUserLocale());
        for (String monthName : MONTHS_NAMES) {
            i18nMonthNameBuilder.setLength(0);
            i18nMonthNames.put(i18nMonthNameBuilder.append("calendar3.month.").append(month_formats.toString()).append('.').append(monthName).toString(), dateTimeFormatter.print((ReadableInstant)dateTime));
            dateTime = dateTime.plusMonths(1);
        }
        return i18nMonthNames;
    }

    private Locale getUserLocale() {
        return this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get());
    }

    private Map<String, String> getDayNamesI18n(DAY_FORMATS day_formats) {
        StringBuilder i18nDayNameBuilder = new StringBuilder();
        HashMap<String, String> i18nDayNames = new HashMap<String, String>();
        DateTime dateTime = new DateTime(DateTimeZone.UTC).withDayOfWeek(7);
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern((String)day_formats.name()).withLocale(this.getUserLocale());
        for (String dayName : DAY_NAMES) {
            i18nDayNameBuilder.setLength(0);
            i18nDayNames.put(i18nDayNameBuilder.append("calendar3.day.").append(day_formats.toString()).append('.').append(dayName).toString(), dateTimeFormatter.print((ReadableInstant)dateTime));
            dateTime = dateTime.plusDays(1);
        }
        return i18nDayNames;
    }

    private static enum DAY_FORMATS {
        EEE("short"),
        EEEE("long");

        private final String name;

        private DAY_FORMATS(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }

    private static enum MONTH_FORMATS {
        MMM("short"),
        MMMM("long");

        private final String name;

        private MONTH_FORMATS(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }
}

