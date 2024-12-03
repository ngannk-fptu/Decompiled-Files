/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.ApplicationType
 *  com.atlassian.dragonfly.api.ApplicationNameGenerator
 *  com.atlassian.dragonfly.api.CrowdApplicationEntity
 *  com.atlassian.dragonfly.api.CrowdIntegrationConfigurator
 *  com.atlassian.dragonfly.api.JiraIntegrationConfigurationException
 *  com.atlassian.dragonfly.spi.JiraIntegrationSetupHelper
 *  com.atlassian.security.random.DefaultSecureTokenGenerator
 *  javax.xml.bind.JAXB
 *  org.apache.commons.httpclient.Credentials
 *  org.apache.commons.httpclient.HttpClient
 *  org.apache.commons.httpclient.HttpMethod
 *  org.apache.commons.httpclient.UsernamePasswordCredentials
 *  org.apache.commons.httpclient.auth.AuthScope
 *  org.apache.commons.httpclient.methods.ByteArrayRequestEntity
 *  org.apache.commons.httpclient.methods.PostMethod
 *  org.apache.commons.httpclient.methods.RequestEntity
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.dragonfly.core;

import com.atlassian.crowd.model.application.ApplicationType;
import com.atlassian.dragonfly.api.ApplicationNameGenerator;
import com.atlassian.dragonfly.api.CrowdApplicationEntity;
import com.atlassian.dragonfly.api.CrowdIntegrationConfigurator;
import com.atlassian.dragonfly.api.JiraIntegrationConfigurationException;
import com.atlassian.dragonfly.core.DefaultApplicationNameGenerator;
import com.atlassian.dragonfly.spi.JiraIntegrationSetupHelper;
import com.atlassian.security.random.DefaultSecureTokenGenerator;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.bind.JAXB;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.lang.StringUtils;

public class CrowdIntegrationConfiguratorImpl
implements CrowdIntegrationConfigurator {
    private final JiraIntegrationSetupHelper jiraIntegrationSetupHelper;
    private final ApplicationNameGenerator applicationNameGenerator;

    public CrowdIntegrationConfiguratorImpl(JiraIntegrationSetupHelper jiraIntegrationSetupHelper, String hostName, String id) {
        this(jiraIntegrationSetupHelper, new DefaultApplicationNameGenerator(StringUtils.defaultString((String)hostName), id, jiraIntegrationSetupHelper.getApplicationType().getDisplayName()));
    }

    public CrowdIntegrationConfiguratorImpl(JiraIntegrationSetupHelper jiraIntegrationSetupHelper, ApplicationNameGenerator applicationNameGenerator) {
        this.jiraIntegrationSetupHelper = jiraIntegrationSetupHelper;
        this.applicationNameGenerator = applicationNameGenerator;
    }

    public CrowdApplicationEntity configureCrowdAuthentication(URI jiraUrl, String username, String password) throws JiraIntegrationConfigurationException {
        try {
            CrowdApplicationEntity applicationEntity = this.createApplicationInCrowd(jiraUrl, username, password);
            this.jiraIntegrationSetupHelper.switchToCrowdAuthentication(jiraUrl, applicationEntity.getName(), applicationEntity.getPassword());
            return applicationEntity;
        }
        catch (JiraIntegrationConfigurationException jice) {
            this.rollbackCrowdAuthenticationConfiguration();
            throw jice;
        }
    }

    public void rollbackCrowdAuthenticationConfiguration() {
        this.jiraIntegrationSetupHelper.switchToDefaultAuthentication();
    }

    private CrowdApplicationEntity createApplicationInCrowd(URI jiraUrl, String username, String password) throws JiraIntegrationConfigurationException {
        CrowdApplicationEntity applicationEntity = this.createApplicationEntity();
        HttpClient client = new HttpClient();
        client.getParams().setAuthenticationPreemptive(true);
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        client.getState().setCredentials(new AuthScope(jiraUrl.getHost(), -1), (Credentials)credentials);
        PostMethod post = new PostMethod(jiraUrl.toString() + "/rest/appmanagement/1/application?include-request-address=true");
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        JAXB.marshal((Object)applicationEntity, (OutputStream)bs);
        post.setRequestEntity((RequestEntity)new ByteArrayRequestEntity(bs.toByteArray(), "application/xml"));
        post.setRequestHeader("Accept", "application/xml");
        try {
            int statusCode = client.executeMethod((HttpMethod)post);
            if (!this.isSuccess(statusCode)) {
                throw new JiraIntegrationConfigurationException("cannot create application in Jira. Status Code =" + statusCode);
            }
        }
        catch (IOException e) {
            throw new JiraIntegrationConfigurationException("cannot create application in Jira", (Throwable)e);
        }
        finally {
            post.releaseConnection();
        }
        return applicationEntity;
    }

    private boolean isSuccess(int statusCode) {
        return statusCode >= 200 && statusCode < 300;
    }

    private CrowdApplicationEntity createApplicationEntity() {
        ApplicationType applicationType = this.jiraIntegrationSetupHelper.getApplicationType();
        String appname = this.applicationNameGenerator.generateApplicationName();
        String password = DefaultSecureTokenGenerator.getInstance().generateToken();
        String description = "Automatically created by the setup of " + applicationType.getDisplayName() + " on " + SimpleDateFormat.getDateInstance().format(new Date());
        return new CrowdApplicationEntity(applicationType, appname, password, description, true);
    }
}

