/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.extra.calendar3.watchdog;

import com.atlassian.confluence.extra.calendar3.watchdog.WatchDogServiceState;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WatchDogServiceStatus {
    @XmlElement
    private WatchDogServiceState state;
    @XmlElement
    private String lastReportMessage;

    public WatchDogServiceStatus(WatchDogServiceState state, String lastReportMessage) {
        this.state = state;
        this.lastReportMessage = lastReportMessage;
    }

    public WatchDogServiceState getState() {
        return this.state;
    }

    public String getLastReportMessage() {
        return this.lastReportMessage;
    }
}

