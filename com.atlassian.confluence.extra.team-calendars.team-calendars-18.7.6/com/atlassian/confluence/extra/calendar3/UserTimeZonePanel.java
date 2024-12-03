/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.web.model.WebPanel
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.confluence.extra.calendar3.JodaIcal4jTimeZoneMapper;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.web.model.WebPanel;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class UserTimeZonePanel
implements WebPanel {
    private final JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper;

    public UserTimeZonePanel(JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper) {
        this.jodaIcal4jTimeZoneMapper = jodaIcal4jTimeZoneMapper;
    }

    public void writeHtml(Writer writer, Map<String, Object> contextMap) throws IOException {
        writer.write(this.getHtml(contextMap));
    }

    public String getHtml(Map<String, Object> context) {
        String userTimeZone = this.jodaIcal4jTimeZoneMapper.getUserTimeZoneIdJoda(AuthenticatedUserThreadLocal.get());
        return "<meta id=\"team-calendars-user-timezone\" content=\"" + userTimeZone + "\">";
    }
}

