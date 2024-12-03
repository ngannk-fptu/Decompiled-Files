/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.troubleshooting.stp.rest;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LicenseStatus {
    @XmlElement
    private String sen;
    @XmlElement
    private String productName;
    @XmlElement
    private int daysToExpiry;

    private LicenseStatus() {
    }

    public LicenseStatus(String productName, String sen, int daysToExpiry) {
        this.sen = sen;
        this.productName = productName;
        this.daysToExpiry = daysToExpiry;
    }
}

