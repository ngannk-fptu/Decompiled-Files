/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  org.apache.commons.lang.StringEscapeUtils
 */
package com.atlassian.confluence.extra.webdav.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.extra.webdav.WebdavSettings;
import com.atlassian.confluence.extra.webdav.WebdavSettingsManager;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.commons.lang.StringEscapeUtils;

public class WebdavConfigAction
extends ConfluenceActionSupport {
    private String[] denyRegexes;
    private boolean contentUrlResourceEnabled;
    private boolean contentExportsResourceEnabled;
    private boolean contentVersionsResourceEnabled;
    private boolean disableStrictPathCheck;
    private boolean hiddenOptionsEnabled;
    private WebdavSettingsManager webdavSettingsManager;

    public void setWebdavSettingsManager(WebdavSettingsManager webdavSettingsManager) {
        this.webdavSettingsManager = webdavSettingsManager;
    }

    public String[] getDenyRegexes() {
        return null == this.denyRegexes ? new String[]{} : this.denyRegexes;
    }

    public void setDenyRegexes(String[] denyRegexes) {
        this.denyRegexes = denyRegexes;
    }

    public boolean isContentUrlResourceEnabled() {
        return this.contentUrlResourceEnabled;
    }

    public void setContentUrlResourceEnabled(boolean contentUrlResourceEnabled) {
        this.contentUrlResourceEnabled = contentUrlResourceEnabled;
    }

    public boolean isContentExportsResourceEnabled() {
        return this.contentExportsResourceEnabled;
    }

    public void setContentExportsResourceEnabled(boolean contentExportsResourceEnabled) {
        this.contentExportsResourceEnabled = contentExportsResourceEnabled;
    }

    public boolean isContentVersionsResourceEnabled() {
        return this.contentVersionsResourceEnabled;
    }

    public void setContentVersionsResourceEnabled(boolean contentVersionsResourceEnabled) {
        this.contentVersionsResourceEnabled = contentVersionsResourceEnabled;
    }

    public boolean isDisableStrictPathCheck() {
        return this.disableStrictPathCheck;
    }

    public void setDisableStrictPathCheck(boolean disableStrictPathCheck) {
        this.disableStrictPathCheck = disableStrictPathCheck;
    }

    public boolean isHiddenOptionsEnabled() {
        return this.hiddenOptionsEnabled;
    }

    public void setHiddenOptionsEnabled(boolean hiddenOptionsEnabled) {
        this.hiddenOptionsEnabled = hiddenOptionsEnabled;
    }

    public String execute() {
        WebdavSettings settings = this.getWebdavSettings();
        settings.setExcludedClientUserAgentRegexes(Arrays.asList(this.getDenyRegexes()));
        settings.setContentUrlResourceEnabled(this.isContentUrlResourceEnabled());
        settings.setContentExportsResourceEnabled(this.isContentExportsResourceEnabled());
        settings.setContentVersionsResourceEnabled(this.isContentVersionsResourceEnabled());
        settings.setStrictPageResourcePathCheckingDisabled(this.isDisableStrictPathCheck());
        this.webdavSettingsManager.save(settings);
        this.addActionMessage(this.getText("webdav.config.saved"));
        return "success";
    }

    public String doDefault() {
        WebdavSettings settings = this.getWebdavSettings();
        Set<String> excludedClientUserAgentRegexes = settings.getExcludedClientUserAgentRegexes();
        this.setDenyRegexes(excludedClientUserAgentRegexes.toArray(new String[excludedClientUserAgentRegexes.size()]));
        this.setContentUrlResourceEnabled(settings.isContentUrlResourceEnabled());
        this.setContentExportsResourceEnabled(settings.isContentExportsResourceEnabled());
        this.setContentVersionsResourceEnabled(settings.isContentVersionsResourceEnabled());
        this.setDisableStrictPathCheck(settings.isStrictPageResourcePathCheckingDisabled());
        return "success";
    }

    public WebdavSettings getWebdavSettings() {
        return this.webdavSettingsManager.getWebdavSettings();
    }

    public void validate() {
        super.validate();
        String[] deniedRegexes = this.getDenyRegexes();
        if (null != deniedRegexes && deniedRegexes.length > 0) {
            LinkedHashSet<String> invalidRegexes = new LinkedHashSet<String>();
            for (String deniedRegex : deniedRegexes) {
                try {
                    Pattern.compile(deniedRegex);
                }
                catch (PatternSyntaxException e) {
                    invalidRegexes.add(deniedRegex);
                }
            }
            if (!invalidRegexes.isEmpty()) {
                StringBuffer stringBuffer = new StringBuffer("<ul>");
                for (String invalidRegex : invalidRegexes) {
                    stringBuffer.append("<li>").append(StringEscapeUtils.escapeHtml((String)invalidRegex)).append("</li>");
                }
                stringBuffer.append("</ul>");
                this.addFieldError("denyRegexes", this.getText("webdav.config.denywrite.error.invalidregex", new String[]{stringBuffer.toString()}));
            }
        }
    }
}

