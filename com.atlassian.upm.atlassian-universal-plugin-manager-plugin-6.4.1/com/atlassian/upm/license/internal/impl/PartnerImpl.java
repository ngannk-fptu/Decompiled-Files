/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.Partner
 */
package com.atlassian.upm.license.internal.impl;

import com.atlassian.upm.api.license.entity.Partner;
import java.util.Objects;

public class PartnerImpl
implements Partner {
    private final String name;

    public PartnerImpl(com.atlassian.extras.api.Partner partner) {
        this(Objects.requireNonNull(partner, "partner").getName());
    }

    public PartnerImpl(String name) {
        this.name = Objects.requireNonNull(name, "name");
    }

    @Override
    public String getName() {
        return this.name;
    }

    public String toString() {
        return "PartnerImpl[" + this.getName() + "]";
    }
}

