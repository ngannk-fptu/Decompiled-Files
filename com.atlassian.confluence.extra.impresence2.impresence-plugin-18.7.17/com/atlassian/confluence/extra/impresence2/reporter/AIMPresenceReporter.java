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

public class AIMPresenceReporter
extends LocaleAwarePresenceReporter {
    public static final String KEY = "aim";

    public AIMPresenceReporter(LocaleSupport localeSupport) {
        super(localeSupport);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getName() {
        return this.getText("presencereporter.aim.name");
    }

    @Override
    public String getServiceHomepage() {
        return this.getText("presencereporter.aim.servicehomepage");
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
            return RenderUtils.error((String)this.getText("presencereporter.aim.error.noscreenname"));
        }
        return "<a href='aim:GoIM?screenname=" + id + "'><img src='http://api.oscar.aol.com/SOA/key=jo1rkjdnQ-LEjx49/presence/" + id + "' height='16' width='16' style='vertical-align:bottom; margin:0px 1px;' border='0'/></a>" + (String)(outputId ? "&nbsp;<a title=\"AIM " + id + "\" href=\"aim:GoIM?screenname=" + id + "\">" + id + "</a>" : "");
    }
}

