/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.extra.flyingpdf.config;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.confluence.extra.flyingpdf.config.AbstractConfigurePdfExportAction;
import com.atlassian.confluence.extra.flyingpdf.config.OutboundConnectionValidator;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GlobalConfigurePdfExportStyleSheet
extends AbstractConfigurePdfExportAction {
    private String style;
    private boolean hasSSRFError = false;
    protected OutboundConnectionValidator outboundConnectionValidator;

    public void setOutboundConnectionValidator(OutboundConnectionValidator outboundConnectionValidator) {
        this.outboundConnectionValidator = outboundConnectionValidator;
    }

    public String getStyle() {
        return this.pdfSettings.getStyle(this.getBandanaContext());
    }

    public void setStyle(String style) {
        this.style = style;
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    @XsrfProtectionExcluded
    public String execute() {
        return "success";
    }

    public String doEdit() {
        if (!this.IsSSRFCheckPassed()) {
            return "error";
        }
        this.pdfSettings.setStyle(this.getBandanaContext(), this.style);
        return "success";
    }

    private boolean IsSSRFCheckPassed() {
        this.getStyleUrls().stream().forEach(url -> {
            OutboundConnectionValidator.ValidateResult result = this.outboundConnectionValidator.validate((String)url);
            if (!result.isValid()) {
                this.hasSSRFError = true;
                if (!this.getActionErrors().contains(result.getErrorMessage())) {
                    this.addActionError(result.getErrorMessage());
                }
            }
        });
        return !this.hasSSRFError;
    }

    private List<String> getStyleUrls() {
        ArrayList<String> urls = new ArrayList<String>();
        Matcher matcher = Pattern.compile("url\\(([^)]*)\\)", 2).matcher(this.style);
        while (matcher.find()) {
            urls.add(matcher.group(1).replaceAll("'|\"", "").trim());
        }
        return urls;
    }
}

