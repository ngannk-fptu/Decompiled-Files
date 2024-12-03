/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.applinks.core.rest.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="configFormValues")
public class ConfigurationFormValuesEntity {
    private boolean trustEachOther;
    private boolean shareUserbase;

    public ConfigurationFormValuesEntity() {
    }

    public ConfigurationFormValuesEntity(boolean trustEachOther, boolean shareUserbase) {
        this.trustEachOther = trustEachOther;
        this.shareUserbase = shareUserbase;
    }

    public boolean shareUserbase() {
        return this.shareUserbase;
    }

    public boolean trustEachOther() {
        return this.trustEachOther;
    }
}

