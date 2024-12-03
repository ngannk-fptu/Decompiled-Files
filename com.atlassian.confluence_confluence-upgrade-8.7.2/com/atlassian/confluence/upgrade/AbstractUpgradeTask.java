/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanNameAware
 */
package com.atlassian.confluence.upgrade;

import com.atlassian.confluence.upgrade.BackupSupport;
import com.atlassian.confluence.upgrade.BuildNumberUpgradeConstraint;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;
import com.atlassian.confluence.upgrade.IsNewerThan;
import com.atlassian.confluence.upgrade.UpgradeError;
import com.atlassian.confluence.upgrade.UpgradeTask;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.BeanNameAware;

public abstract class AbstractUpgradeTask
implements UpgradeTask,
BackupSupport,
BeanNameAware {
    private List<UpgradeError> errors = new ArrayList<UpgradeError>();
    private String buildNumber;
    private String beanName;

    @Override
    public String getShortDescription() {
        return "Upgrade to build number: " + this.getBuildNumber();
    }

    protected void addError(UpgradeError error) {
        this.errors.add(error);
    }

    protected void addError(String errorMessage) {
        this.addError(new UpgradeError(errorMessage));
    }

    protected void addAllErrors(Collection<UpgradeError> errors) {
        this.errors.addAll(errors);
    }

    @Override
    public Collection<UpgradeError> getErrors() {
        return this.errors;
    }

    public void setBuildNumber(String buildNumber) {
        this.buildNumber = buildNumber;
    }

    @Override
    public String getBuildNumber() {
        if (this.buildNumber == null) {
            String className = this.getClass().getName().substring(this.getClass().getName().lastIndexOf(46) + 1);
            this.buildNumber = className.replaceFirst("Build", "").replaceFirst("UpgradeTask", "");
        }
        return this.buildNumber;
    }

    @Override
    public void validate() throws Exception {
    }

    @Override
    public BuildNumberUpgradeConstraint getConstraint() {
        return new IsNewerThan(this.getBuildNumber());
    }

    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override
    public String getName() {
        return this.beanName;
    }

    @Override
    public boolean isDatabaseUpgrade() {
        return this instanceof DatabaseUpgradeTask;
    }
}

