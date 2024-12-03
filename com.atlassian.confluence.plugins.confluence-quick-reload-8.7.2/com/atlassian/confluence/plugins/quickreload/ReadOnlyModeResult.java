/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.quickreload;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ReadOnlyModeResult {
    @XmlElement
    private boolean isEnabled;
    @XmlElement
    private boolean isBannerMessageOn;
    @XmlElement
    private String bannerMessage;

    private ReadOnlyModeResult() {
    }

    public ReadOnlyModeResult(boolean isEnabled, boolean isBannerMessageOn, String bannerMessage) {
        this.isEnabled = isEnabled;
        this.isBannerMessageOn = isBannerMessageOn;
        this.bannerMessage = bannerMessage;
    }
}

