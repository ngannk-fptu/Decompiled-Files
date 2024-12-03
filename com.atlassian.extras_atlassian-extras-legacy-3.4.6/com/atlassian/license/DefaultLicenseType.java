/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.LicenseEdition
 */
package com.atlassian.license;

import com.atlassian.extras.api.LicenseEdition;
import com.atlassian.license.LicenseType;

@Deprecated
public class DefaultLicenseType
implements LicenseType {
    protected int type;
    protected String description;
    protected boolean isEvaluation;
    protected boolean requiresUserLimit;
    protected boolean expires;
    final String newLicenseTypeName;
    final LicenseEdition edition;

    public DefaultLicenseType(int type, String description, boolean isEvaluation, boolean requiresUserLimit, String newLicenseTypeName) {
        this(type, description, isEvaluation, requiresUserLimit, false, newLicenseTypeName, null);
    }

    public DefaultLicenseType(int type, String description, boolean isEvaluation, boolean requiresUserLimit, String newLicenseTypeName, LicenseEdition edition) {
        this(type, description, isEvaluation, requiresUserLimit, false, newLicenseTypeName, edition);
    }

    public DefaultLicenseType(int type, String description, boolean isEvaluation, boolean requiresUserLimit, boolean expires, String newLicenseTypeName) {
        this(type, description, isEvaluation, requiresUserLimit, expires, newLicenseTypeName, null);
    }

    public DefaultLicenseType(int type, String description, boolean isEvaluation, boolean requiresUserLimit, boolean expires, String newLicenseTypeName, LicenseEdition edition) {
        this.type = type;
        this.description = description;
        this.isEvaluation = isEvaluation;
        this.requiresUserLimit = requiresUserLimit;
        this.expires = expires;
        this.newLicenseTypeName = newLicenseTypeName;
        this.edition = edition;
    }

    @Override
    public LicenseEdition getEdition() {
        return this.edition;
    }

    @Override
    public String getNewLicenseTypeName() {
        return this.newLicenseTypeName;
    }

    @Override
    public int hashCode() {
        return this.type + this.description.hashCode();
    }

    @Override
    public int getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return this.getDescription();
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public boolean isEvaluationLicenseType() {
        return this.isEvaluation;
    }

    @Override
    public boolean expires() {
        return this.expires;
    }

    @Override
    public boolean requiresUserLimit() {
        return this.requiresUserLimit;
    }

    @Override
    public String getNiceName() {
        return this.description;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof LicenseType) {
            LicenseType license = (LicenseType)o;
            if (this.getType() == license.getType()) {
                return true;
            }
        }
        return false;
    }
}

