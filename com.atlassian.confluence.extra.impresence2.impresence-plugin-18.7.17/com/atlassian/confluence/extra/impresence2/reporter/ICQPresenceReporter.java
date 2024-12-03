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

public class ICQPresenceReporter
extends LocaleAwarePresenceReporter {
    public static final String KEY = "icq";

    public ICQPresenceReporter(LocaleSupport localeSupport) {
        super(localeSupport);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getName() {
        return this.getText("presencereporter.icq.name");
    }

    @Override
    public String getServiceHomepage() {
        return this.getText("presencereporter.icq.servicehomepage");
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
            return RenderUtils.error((String)this.getText("presencereporter.icq.error.nouin"));
        }
        return "<a title=\"ICQ " + id + "\" href=\"http://wwp.icq.com/scripts/contact.dll?msgto=" + id + "\"><img border=\"0\" align=\"absmiddle\" src=\"http://status.icq.com/online.gif?icq=" + id + "&img=5\"></a>" + (String)(outputId ? "&nbsp<a title=\"ICQ " + id + "\" href=\"http://wwp.icq.com/scripts/contact.dll?msgto=" + id + "\">" + id + "</a>" : "");
    }
}

