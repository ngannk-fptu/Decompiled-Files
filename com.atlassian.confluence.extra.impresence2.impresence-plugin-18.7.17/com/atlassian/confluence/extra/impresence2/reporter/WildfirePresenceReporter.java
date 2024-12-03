/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.v2.RenderUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.extra.impresence2.reporter;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.extra.impresence2.reporter.PresenceException;
import com.atlassian.confluence.extra.impresence2.reporter.ServerPresenceReporter;
import com.atlassian.confluence.extra.impresence2.util.LocaleSupport;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.v2.RenderUtils;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;

public class WildfirePresenceReporter
extends ServerPresenceReporter {
    public static final String KEY = "wildfire";

    public WildfirePresenceReporter(LocaleSupport localeSupport, @ComponentImport BandanaManager bandanaManager) {
        super(localeSupport, bandanaManager);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getName() {
        return this.getText("presencereporter.wildfire.name");
    }

    @Override
    public String getServiceHomepage() {
        return this.getText("presencereporter.wildfire.servicehomepage");
    }

    @Override
    public String getPresenceXHTML(String id, boolean outputId) throws IOException, PresenceException {
        if (!StringUtils.isNotBlank((CharSequence)id)) {
            return RenderUtils.error((String)this.getText("presencereporter.wildfire.error.noscreename"));
        }
        StringBuffer out = new StringBuffer();
        out.append("<a href='jabber:").append(id).append("'").append(" title='Online status for ").append(id).append("'").append(" style='white-space:nowrap;'>");
        out.append("<img src='http://").append(GeneralUtil.htmlEncode((String)this.getServer())).append("/plugins/presence/status?jid=").append(id).append("'").append(" align='absmiddle' border='0' title='Status Indicator' alt='Status Indicator'/>");
        out.append("</a>");
        if (outputId) {
            out.append("&nbsp;<a href='jabber:").append(id).append("'").append(" title='Online status for ").append(id).append("'").append(" style='white-space:nowrap;'>").append(id).append("</a>");
        }
        return out.toString();
    }
}

