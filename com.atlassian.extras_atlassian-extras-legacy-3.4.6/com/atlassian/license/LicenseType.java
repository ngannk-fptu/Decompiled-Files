/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.LicenseEdition
 */
package com.atlassian.license;

import com.atlassian.extras.api.LicenseEdition;

@Deprecated
public interface LicenseType {
    public boolean equals(Object var1);

    public int hashCode();

    public int getType();

    public String toString();

    public String getDescription();

    public String getNiceName();

    public boolean isEvaluationLicenseType();

    public boolean requiresUserLimit();

    public boolean expires();

    public String getNewLicenseTypeName();

    public LicenseEdition getEdition();
}

