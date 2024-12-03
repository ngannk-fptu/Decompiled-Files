/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.upgrade;

import com.atlassian.confluence.upgrade.BuildNumberUpgradeConstraint;
import com.atlassian.confluence.upgrade.UpgradeError;
import com.atlassian.confluence.upgrade.UpgradeTaskInfo;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface UpgradeTask
extends UpgradeTaskInfo {
    public static final Logger log = LoggerFactory.getLogger(UpgradeTask.class);

    public BuildNumberUpgradeConstraint getConstraint();

    public void validate() throws Exception;

    public void doUpgrade() throws Exception;

    public Collection<UpgradeError> getErrors();
}

