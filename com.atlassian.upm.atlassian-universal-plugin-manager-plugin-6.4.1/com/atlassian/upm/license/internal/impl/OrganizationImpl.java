/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.Organisation
 */
package com.atlassian.upm.license.internal.impl;

import com.atlassian.extras.api.Organisation;
import com.atlassian.upm.api.license.entity.Organization;
import java.util.Objects;

public class OrganizationImpl
implements Organization {
    private final String name;

    public OrganizationImpl(Organisation organization) {
        this(Objects.requireNonNull(organization, "organization").getName());
    }

    public OrganizationImpl(String name) {
        this.name = Objects.requireNonNull(name, "name");
    }

    @Override
    public String getName() {
        return this.name;
    }

    public String toString() {
        return "OrganizationImpl[" + this.getName() + "]";
    }
}

