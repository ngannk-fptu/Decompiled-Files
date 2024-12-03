/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.RequireSecurityToken
 *  org.apache.commons.lang.StringUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.extra.impresence2.config;

import com.atlassian.confluence.extra.impresence2.config.LoginPresenceConfigAction;
import com.atlassian.confluence.extra.impresence2.reporter.JabberPresenceReporter;
import com.atlassian.xwork.RequireSecurityToken;
import org.apache.commons.lang.StringUtils;

public class JabberPresenceConfigAction
extends LoginPresenceConfigAction {
    private String domain;
    private String port;

    @Override
    protected String getServiceKey() {
        return "jabber";
    }

    @Override
    protected String getServiceName() {
        return this.getText("jabber.config.service.name");
    }

    @Override
    public String doDefault() throws Exception {
        JabberPresenceReporter reporter = (JabberPresenceReporter)this.getReporter();
        if (null != reporter) {
            this.setDomain(reporter.getDomain());
            this.setPort(String.valueOf(reporter.getPort()));
        }
        return super.doDefault();
    }

    @Override
    @RequireSecurityToken(value=true)
    public String execute() throws Exception {
        JabberPresenceReporter reporter = (JabberPresenceReporter)this.getReporter();
        if (reporter == null) {
            this.addActionError(this.getText("error.general.nosuchreporter", new String[]{this.getServiceName()}));
            return "error";
        }
        reporter.setDomain(StringUtils.trim((String)this.getDomain()));
        try {
            reporter.setPort(Integer.parseInt(this.getPort()));
        }
        catch (NumberFormatException invalidPort) {
            reporter.setPort(null);
        }
        return super.execute();
    }

    public void validate() {
        super.validate();
        if (org.apache.commons.lang3.StringUtils.isNotBlank((CharSequence)this.port) && !org.apache.commons.lang3.StringUtils.isNumeric((CharSequence)this.port)) {
            this.addActionError(this.getText("impresence.jabber.error.invalidport", new String[]{this.port}));
        }
    }

    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPort() {
        return this.port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}

