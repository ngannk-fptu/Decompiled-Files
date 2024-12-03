/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Option
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.model.SupportOrganization;
import io.atlassian.fugue.Option;

public final class SupportDetails {
    Option<SupportOrganization> supportOrg;
    Option<String> emergencyContact;

    public Option<SupportOrganization> getSupportOrg() {
        return this.supportOrg;
    }

    public Option<String> getEmergencyContact() {
        return this.emergencyContact;
    }
}

