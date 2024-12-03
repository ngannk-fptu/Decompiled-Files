/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.v2.RenderUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.extra.impresence2.reporter;

import com.atlassian.confluence.extra.impresence2.reporter.LocaleAwarePresenceReporter;
import com.atlassian.confluence.extra.impresence2.reporter.PresenceException;
import com.atlassian.confluence.extra.impresence2.util.LocaleSupport;
import com.atlassian.renderer.v2.RenderUtils;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;

public class YahooPresenceReporter
extends LocaleAwarePresenceReporter {
    public static final String KEY = "yahoo";

    public YahooPresenceReporter(LocaleSupport localeSupport) {
        super(localeSupport);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getName() {
        return this.getText("presencereporter.yahoo.name");
    }

    @Override
    public String getServiceHomepage() {
        return this.getText("presencereporter.yahoo.servicehomepage");
    }

    @Override
    public boolean hasConfig() {
        return false;
    }

    @Override
    public boolean requiresConfig() {
        return false;
    }

    @Override
    public String getPresenceXHTML(String id, boolean outputId) throws IOException, PresenceException {
        if (!StringUtils.isNotBlank((CharSequence)id)) {
            return RenderUtils.error((String)this.getText("presencereporter.yahoo.error.noyahooid"));
        }
        return "<a title=\"Yahoo! " + id + "\" href=\"ymsgr:sendIM?" + id + "\">\n<img border=0  src=\"http://opi.yahoo.com/online?u=" + id + "&m=g&t=0\" style='margin:0px 3px; vertical-align:bottom;' height='12' width='12' ></a>" + (String)(outputId ? "&nbsp;<a href=\"ymsgr:sendIM?" + id + "\">" + id + "</a>" : "");
    }
}

