/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.setup.BootstrapManager
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.confluence.extra.impresence2.reporter;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.extra.impresence2.reporter.LocaleAwarePresenceReporter;
import com.atlassian.confluence.extra.impresence2.reporter.PresenceReporter;
import com.atlassian.confluence.extra.impresence2.util.LocaleSupport;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.apache.commons.lang.StringUtils;

public abstract class LoginPresenceReporter
extends LocaleAwarePresenceReporter
implements PresenceReporter {
    private static final String ID_PREFIX = "extra.im.account.";
    private static final String PASSWORD_PREFIX = "extra.im.password.";
    protected final BandanaManager bandanaManager;
    protected final BootstrapManager bootstrapManager;

    public LoginPresenceReporter(LocaleSupport localeSupport, @ComponentImport BandanaManager bandanaManager, @ComponentImport BootstrapManager bootstrapManager) {
        super(localeSupport);
        this.bandanaManager = bandanaManager;
        this.bootstrapManager = bootstrapManager;
    }

    public String getId() {
        return (String)this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, ID_PREFIX + this.getKey());
    }

    public void setId(String id) {
        this.bandanaManager.setValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, ID_PREFIX + this.getKey(), (Object)StringUtils.defaultString((String)StringUtils.trim((String)id)));
    }

    public String getPassword() {
        return (String)this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, PASSWORD_PREFIX + this.getKey());
    }

    public void setPassword(String password) {
        this.bandanaManager.setValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, PASSWORD_PREFIX + this.getKey(), (Object)StringUtils.defaultString((String)StringUtils.trim((String)password)));
    }

    @Override
    public boolean hasConfig() {
        return true;
    }

    @Override
    public boolean requiresConfig() {
        return StringUtils.isBlank((String)this.getId()) || StringUtils.isBlank((String)this.getPassword());
    }

    protected String getPresenceLink(String id, String icon, String status, boolean outputId) {
        String url = this.getPresenceURL(id);
        StringBuffer out = new StringBuffer();
        if (url != null) {
            out.append("<A href='").append(url).append("'>");
        }
        out.append("<img src='").append(this.bootstrapManager.getWebAppContextPath()).append("/download/resources/confluence.extra.impresence2:im/images/").append(icon).append(".gif'").append(" style='vertical-align:bottom; margin:0px 1px;' border='0'").append("' title='").append(status).append("'").append("/>");
        if (url != null) {
            out.append("</a>");
        }
        if (outputId) {
            out.append("&nbsp;");
            if (url != null) {
                out.append("<A href='").append(url).append("' title='").append(status).append("'>");
            }
            out.append(id);
            if (url != null) {
                out.append("</a>");
            }
        }
        return out.toString();
    }

    protected abstract String getPresenceURL(String var1);
}

