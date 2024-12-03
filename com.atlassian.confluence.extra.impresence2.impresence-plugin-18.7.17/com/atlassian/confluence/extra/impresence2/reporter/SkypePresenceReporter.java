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
import com.atlassian.confluence.extra.impresence2.reporter.PresenceReporter;
import com.atlassian.confluence.extra.impresence2.util.LocaleSupport;
import com.atlassian.renderer.v2.RenderUtils;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;

public class SkypePresenceReporter
extends LocaleAwarePresenceReporter
implements PresenceReporter {
    public static final String KEY = "skype";

    public SkypePresenceReporter(LocaleSupport localeSupport) {
        super(localeSupport);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getName() {
        return this.getText("presencereporter.skype.name");
    }

    @Override
    public String getServiceHomepage() {
        return this.getText("presencereporter.skype.servicehomepage");
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
            return RenderUtils.error((String)this.getText("presencereporter.skype.error.noscreenname"));
        }
        StringBuffer out = new StringBuffer();
        out.append("<script type='text/javascript' src='http://download.skype.com/share/skypebuttons/js/skypeCheck.js'></script>").append("<a href='skype:").append(id).append("?call'>").append("<img src='http://mystatus.skype.com/smallclassic/").append(id).append("' style='border: none;' align='absmiddle' alt='My status' />").append("</a>");
        if (outputId) {
            out.append("&nbsp;<a href='skype:").append(id).append("?call'>").append(id).append("</a>");
        }
        return out.toString();
    }
}

