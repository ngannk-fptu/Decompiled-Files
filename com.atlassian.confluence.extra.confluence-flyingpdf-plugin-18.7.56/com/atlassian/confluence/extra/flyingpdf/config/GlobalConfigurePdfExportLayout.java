/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.extra.flyingpdf.config;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.extra.flyingpdf.config.AbstractConfigurePdfExportAction;
import com.atlassian.confluence.extra.flyingpdf.config.OutboundConnectionValidator;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.stream.Stream;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

public class GlobalConfigurePdfExportLayout
extends AbstractConfigurePdfExportAction {
    private String header;
    private String footer;
    private String titlePage;
    private boolean hasSSRFError = false;
    protected OutboundConnectionValidator outboundConnectionValidator;

    public void setOutboundConnectionValidator(OutboundConnectionValidator outboundConnectionValidator) {
        this.outboundConnectionValidator = outboundConnectionValidator;
    }

    public void setTitlePage(String titlePage) {
        this.titlePage = titlePage;
    }

    public String getTitlePage() {
        return this.pdfSettings.getTitlePage(this.getBandanaContext());
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getHeader() {
        return this.pdfSettings.getHeader(this.getBandanaContext());
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public String getFooter() {
        return this.pdfSettings.getFooter(this.getBandanaContext());
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
        this.pdfSettings.setTitlePage(this.getBandanaContext(), this.titlePage);
        this.pdfSettings.setHeader(this.getBandanaContext(), this.header);
        this.pdfSettings.setFooter(this.getBandanaContext(), this.footer);
        return "success";
    }

    private boolean IsSSRFCheckPassed() {
        Stream.of(this.titlePage, this.header, this.footer).forEach(this::checkSSRFAgainstElementSrc);
        return !this.hasSSRFError;
    }

    private void checkSSRFAgainstElementSrc(String html) throws IllegalArgumentException, ServiceException {
        Stream.of("audio", "embed", "iframe", "img", "input", "script", "track", "video").forEach(tag -> {
            Elements elements = Jsoup.parseBodyFragment(html).getElementsByTag((String)tag);
            if (elements.isEmpty()) {
                return;
            }
            elements.stream().filter(element -> element.hasAttr("src")).forEach(element -> {
                OutboundConnectionValidator.ValidateResult result = this.outboundConnectionValidator.validate(element.attr("src").trim());
                if (!result.isValid()) {
                    this.hasSSRFError = true;
                    if (!this.getActionErrors().contains(result.getErrorMessage())) {
                        this.addActionError(result.getErrorMessage());
                    }
                }
            });
        });
    }
}

