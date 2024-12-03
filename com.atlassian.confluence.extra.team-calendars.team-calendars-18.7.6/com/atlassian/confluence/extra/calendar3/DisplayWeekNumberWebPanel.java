/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.model.WebPanel
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.confluence.extra.calendar3.CalendarSettingsManager;
import com.atlassian.plugin.web.model.WebPanel;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class DisplayWeekNumberWebPanel
implements WebPanel {
    private final CalendarSettingsManager calendarSettingsManager;

    public DisplayWeekNumberWebPanel(CalendarSettingsManager calendarSettingsManager) {
        this.calendarSettingsManager = calendarSettingsManager;
    }

    public String getHtml(Map<String, Object> context) {
        return "<meta id=\"team-calendars-display-week-number\" content=\"" + this.calendarSettingsManager.getDisplayWeekNumber() + "\">";
    }

    public void writeHtml(Writer writer, Map<String, Object> context) throws IOException {
        writer.write(this.getHtml(context));
    }
}

