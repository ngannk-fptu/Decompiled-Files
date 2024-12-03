/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.PlainTextToHtmlConverter
 *  com.atlassian.plugin.web.model.WebPanel
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.confluence.extra.calendar3.CalendarSettingsManager;
import com.atlassian.confluence.util.PlainTextToHtmlConverter;
import com.atlassian.plugin.web.model.WebPanel;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class DisplayTimeFormatWebPanel
implements WebPanel {
    private final CalendarSettingsManager calendarSettingsManager;

    public DisplayTimeFormatWebPanel(CalendarSettingsManager calendarSettingsManager) {
        this.calendarSettingsManager = calendarSettingsManager;
    }

    public String getHtml(Map<String, Object> context) {
        return "<meta name=\"ajs-team-calendars-display-time-format\" content=\"" + PlainTextToHtmlConverter.encodeHtmlEntities((String)this.calendarSettingsManager.getDisplayTimeFormat()) + "\">";
    }

    public void writeHtml(Writer writer, Map<String, Object> context) throws IOException {
        writer.write(this.getHtml(context));
    }
}

