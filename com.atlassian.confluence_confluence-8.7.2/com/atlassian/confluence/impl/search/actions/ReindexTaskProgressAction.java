/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.impl.search.actions;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.search.IndexManager;
import com.atlassian.confluence.search.ReIndexTask;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.util.Progress;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.LinkedHashMap;

public class ReindexTaskProgressAction
extends ConfluenceActionSupport
implements Beanable {
    private IndexManager indexManager;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return "success";
    }

    @Override
    public Object getBean() {
        LinkedHashMap<String, String> result = null;
        ReIndexTask lastReindexingTask = this.indexManager.getLastReindexingTask();
        if (lastReindexingTask != null) {
            result = new LinkedHashMap<String, String>();
            Progress progress = lastReindexingTask.getProgress();
            result.put("percentageComplete", String.valueOf(progress.getPercentComplete()));
            result.put("compactElapsedTime", lastReindexingTask.getCompactElapsedTime());
            result.put("count", String.valueOf(progress.getCount()));
            result.put("total", String.valueOf(progress.getTotal()));
        }
        return result;
    }

    public void setIndexManager(IndexManager indexManager) {
        this.indexManager = indexManager;
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }
}

