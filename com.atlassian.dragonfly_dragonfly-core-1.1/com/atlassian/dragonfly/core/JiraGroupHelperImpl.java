/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.ApplicationPermissionException
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.InvalidAuthenticationException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.integration.rest.service.factory.RestCrowdClientFactory
 *  com.atlassian.crowd.service.client.CrowdClient
 *  com.atlassian.dragonfly.api.CrowdApplicationEntity
 *  com.atlassian.dragonfly.api.JiraGroupHelper
 */
package com.atlassian.dragonfly.core;

import com.atlassian.crowd.exception.ApplicationPermissionException;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.InvalidAuthenticationException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.integration.rest.service.factory.RestCrowdClientFactory;
import com.atlassian.crowd.service.client.CrowdClient;
import com.atlassian.dragonfly.api.CrowdApplicationEntity;
import com.atlassian.dragonfly.api.JiraGroupHelper;

public class JiraGroupHelperImpl
implements JiraGroupHelper {
    private RestCrowdClientFactory factory = new RestCrowdClientFactory();
    private CrowdClient client;

    public JiraGroupHelperImpl(String jiraUrl, CrowdApplicationEntity crowdApplicationEntity) {
        this.client = this.factory.newInstance(jiraUrl, crowdApplicationEntity.getName(), crowdApplicationEntity.getPassword());
    }

    public JiraGroupHelperImpl(String jiraUrl, String application, String password) {
        this.client = this.factory.newInstance(jiraUrl, application, password);
    }

    public boolean doesGroupExist(String group) {
        try {
            this.client.getGroup(group);
            return true;
        }
        catch (GroupNotFoundException e) {
            return false;
        }
        catch (OperationFailedException e) {
            throw new IllegalStateException("must be able to talk to Jira", e);
        }
        catch (InvalidAuthenticationException e) {
            throw new IllegalStateException("must be able to talk to Jira", e);
        }
        catch (ApplicationPermissionException e) {
            throw new IllegalStateException("must be able to talk to Jira", e);
        }
    }
}

