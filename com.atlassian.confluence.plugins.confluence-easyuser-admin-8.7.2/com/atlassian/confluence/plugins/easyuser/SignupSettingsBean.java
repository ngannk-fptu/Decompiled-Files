/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.easyuser;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SignupSettingsBean {
    @XmlElement
    boolean notify;
    @XmlElement
    String domains;
    @XmlElement
    boolean enabled;

    public boolean isNotifyAdmin() {
        return this.notify;
    }

    public String getDomains() {
        return this.domains;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setNotifyAdmin(boolean notify) {
        this.notify = notify;
    }

    public void setDomains(String domains) {
        this.domains = domains;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public SignupSettingsBean() {
    }

    public SignupSettingsBean(boolean enabled, String domains, boolean notifyAdmin) {
        this.notify = notifyAdmin;
        this.domains = domains;
        this.enabled = enabled;
    }
}

