/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.host.spi.HostApplication
 *  com.atlassian.applinks.spi.link.MutatingApplicationLinkService
 *  com.atlassian.applinks.spi.manifest.ManifestRetriever
 *  com.atlassian.applinks.spi.util.TypeAccessor
 *  com.atlassian.config.setup.SetupPersister
 *  com.atlassian.core.exception.InfrastructureException
 *  com.atlassian.crowd.directory.InternalDirectory
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.InvalidGroupException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.manager.directory.DirectoryPermissionException
 *  com.atlassian.crowd.model.group.GroupTemplate
 *  com.atlassian.crowd.model.user.UserTemplate
 *  com.atlassian.dragonfly.api.CrowdApplicationEntity
 *  com.atlassian.dragonfly.api.JiraGroupHelper
 *  com.atlassian.dragonfly.api.JiraIntegrationConfigurationException
 *  com.atlassian.dragonfly.core.ApplicationLinkConfiguratorImpl
 *  com.atlassian.dragonfly.core.CrowdIntegrationConfiguratorImpl
 *  com.atlassian.dragonfly.core.JiraAccessUtilImpl
 *  com.atlassian.dragonfly.core.JiraGroupHelperImpl
 *  com.atlassian.dragonfly.spi.JiraIntegrationSetupHelper
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.seraph.auth.AuthenticatorException
 *  com.atlassian.seraph.config.SecurityConfigFactory
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.atlassian.xwork.RequireSecurityToken
 *  com.google.common.base.Joiner
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup.actions;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.host.spi.HostApplication;
import com.atlassian.applinks.spi.link.MutatingApplicationLinkService;
import com.atlassian.applinks.spi.manifest.ManifestRetriever;
import com.atlassian.applinks.spi.util.TypeAccessor;
import com.atlassian.config.setup.SetupPersister;
import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.setup.actions.AbstractSetupAction;
import com.atlassian.confluence.setup.actions.ConfluenceJiraIntegrationSetupHelper;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.user.crowd.EmbeddedCrowdBootstrap;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.core.exception.InfrastructureException;
import com.atlassian.crowd.directory.InternalDirectory;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.InvalidGroupException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.manager.directory.DirectoryPermissionException;
import com.atlassian.crowd.model.group.GroupTemplate;
import com.atlassian.crowd.model.user.UserTemplate;
import com.atlassian.dragonfly.api.CrowdApplicationEntity;
import com.atlassian.dragonfly.api.JiraGroupHelper;
import com.atlassian.dragonfly.api.JiraIntegrationConfigurationException;
import com.atlassian.dragonfly.core.ApplicationLinkConfiguratorImpl;
import com.atlassian.dragonfly.core.CrowdIntegrationConfiguratorImpl;
import com.atlassian.dragonfly.core.JiraAccessUtilImpl;
import com.atlassian.dragonfly.core.JiraGroupHelperImpl;
import com.atlassian.dragonfly.spi.JiraIntegrationSetupHelper;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.seraph.auth.AuthenticatorException;
import com.atlassian.seraph.config.SecurityConfigFactory;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.atlassian.xwork.RequireSecurityToken;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSudoRequired
@SystemAdminOnly
public class ConnectToJiraAction
extends AbstractSetupAction {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectToJiraAction.class);
    private static final Set<String> SUPPORTED_APPLINKS_SCHEMES = ImmutableSet.of((Object)"http", (Object)"https");
    private ManifestRetriever applinkManifestRetriever;
    private MutatingApplicationLinkService applinkApplicationLink;
    private TypeAccessor applinkTypeAccessor;
    private CrowdDirectoryService crowdDirectoryService;
    private DirectoryManager directoryManager;
    private HostApplication applinkHostApplication;
    private EmbeddedCrowdBootstrap embeddedCrowdBootstrap;
    private SetupPersister setupPersister;
    private SpacePermissionManager spacePermissionManager;
    private String jiraBaseUrl;
    private String confluenceBaseUrl;
    private String username;
    private String password;
    private List<String> jiraAdminGroups = Arrays.asList("jira-administrators");
    private List<String> jiraUserGroups = Arrays.asList("jira-software-users");
    private URI jiraUri;
    private URI confluenceUri;
    private boolean applinkAndCrowdReady;
    private String crowdApplicationName;
    private String crowdApplicationPassword;

    @Override
    public void validate() {
        if (StringUtils.isEmpty((CharSequence)this.username)) {
            this.addFieldError("username", this.getText("error.username.not.valid"));
        }
        if (!this.applinkAndCrowdReady && StringUtils.isEmpty((CharSequence)this.password)) {
            this.addFieldError("password", this.getText("error.password.not.valid"));
        }
        if (StringUtils.isEmpty((CharSequence)this.jiraBaseUrl)) {
            this.addFieldError("jiraBaseUrl", this.getText("error.url.empty"));
        } else {
            this.jiraUri = this.toURI(this.jiraBaseUrl);
            if (this.jiraUri == null) {
                this.addFieldError("jiraBaseUrl", this.getText("error.url.malformed"));
            }
        }
        if (StringUtils.isEmpty((CharSequence)this.confluenceBaseUrl)) {
            this.addFieldError("confluenceBaseUrl", this.getText("error.url.empty"));
        } else {
            this.confluenceUri = this.toURI(this.confluenceBaseUrl);
            if (this.confluenceUri == null) {
                this.addFieldError("confluenceBaseUrl", this.getText("error.url.malformed"));
            }
        }
        if (this.jiraAdminGroups.isEmpty()) {
            this.addFieldError("jiraAdminGroups", this.getText("error.jiraadmingroups.not.valid"));
        }
        if (this.jiraUserGroups.isEmpty()) {
            this.addFieldError("jiraUserGroups", this.getText("error.jirausergroups.not.valid"));
        }
    }

    @PermittedMethods(value={HttpMethod.POST})
    @RequireSecurityToken(value=true)
    public String execute() throws Exception {
        this.validate();
        if (this.hasFieldErrors()) {
            return "error";
        }
        JiraAccessUtilImpl jiraAccessUtil = new JiraAccessUtilImpl(this.applinkManifestRetriever, this.applinkTypeAccessor, this.applinkApplicationLink);
        try {
            LOG.info("Testing for Jira connectivity at {}", (Object)this.jiraUri);
            if (!jiraAccessUtil.checkTargetIsSupportedJira(this.jiraUri)) {
                LOG.warn("Jira connectivity failed at {}", (Object)this.jiraUri);
                this.addFieldError("jiraBaseUrl", this.getText("error.jirabaseurl.connection.refused"));
                return "error";
            }
            LOG.info("Testing for Jira admin credentials at {}", (Object)this.jiraUri);
            if (!jiraAccessUtil.checkAdminCredential(this.jiraUri, this.username, this.password)) {
                LOG.warn("Jira admin credentials failed at {}", (Object)this.jiraUri);
                this.addFieldError("username", this.getText("error.credential.not.admin"));
                return "error";
            }
        }
        catch (Exception exception) {
            LOG.warn("Jira validation failed", (Throwable)exception);
            this.addFieldError("jiraBaseUrl", this.getText("error.jirabaseurl.not.valid"));
            return "error";
        }
        this.embeddedCrowdBootstrap.ensureApplicationExists();
        CrowdApplicationEntity crowdApplicationEntity = this.configureJira();
        if (crowdApplicationEntity != null) {
            this.crowdApplicationName = crowdApplicationEntity.getName();
            this.crowdApplicationPassword = crowdApplicationEntity.getPassword();
            this.embeddedCrowdBootstrap.ensureInternalDirectoryExists();
            Directory internalDirectory = this.getInternalDirectory();
            if (internalDirectory == null) {
                throw new IllegalStateException("No internal directory found.");
            }
            Settings settings = this.settingsManager.getGlobalSettings();
            settings.setBaseUrl(this.confluenceBaseUrl);
            this.settingsManager.updateGlobalSettings(settings);
            this.createDefaultGroups(internalDirectory);
            this.setDefaultPermissions();
            this.createAdminInInternalDirectory(internalDirectory);
            this.loginAdmin();
        }
        if (this.hasFieldErrors()) {
            return "error";
        }
        JiraGroupHelperImpl jiraGroupHelper = new JiraGroupHelperImpl(this.jiraUri.toString(), this.crowdApplicationName, this.crowdApplicationPassword);
        this.verifyJiraGroups((JiraGroupHelper)jiraGroupHelper);
        if (this.hasFieldErrors()) {
            return "error";
        }
        this.setupGlobalPermissions();
        this.getSetupPersister().progessSetupStep();
        return super.execute();
    }

    private CrowdApplicationEntity configureJira() {
        if (!this.applinkAndCrowdReady) {
            CrowdApplicationEntity crowdApplicationEntity;
            ApplicationLink applicationLink;
            ConfluenceJiraIntegrationSetupHelper confluenceJiraIntegrationSetupHelper = new ConfluenceJiraIntegrationSetupHelper(this.crowdDirectoryService);
            ApplicationLinkConfiguratorImpl applicationLinkConfigurator = new ApplicationLinkConfiguratorImpl(this.applinkApplicationLink, this.applinkTypeAccessor, (JiraIntegrationSetupHelper)confluenceJiraIntegrationSetupHelper);
            try {
                LOG.info("Establishing app link to Jira at {} with username [{}] and Confluence at {}", new Object[]{this.jiraUri, this.username, this.confluenceUri});
                applicationLink = applicationLinkConfigurator.configureApplicationLinks(this.jiraUri, this.confluenceUri, this.username, this.password);
            }
            catch (JiraIntegrationConfigurationException exception) {
                this.addJiraConnectionFailedError(exception);
                return null;
            }
            CrowdIntegrationConfiguratorImpl crowdIntegrationConfigurator = new CrowdIntegrationConfiguratorImpl((JiraIntegrationSetupHelper)confluenceJiraIntegrationSetupHelper, this.applinkHostApplication.getBaseUrl().getHost(), this.applinkHostApplication.getId().get());
            try {
                crowdApplicationEntity = crowdIntegrationConfigurator.configureCrowdAuthentication(this.jiraUri, this.username, this.password);
            }
            catch (JiraIntegrationConfigurationException e) {
                applicationLinkConfigurator.rollbackApplicationLinkConfiguration(applicationLink);
                throw new RuntimeException("Error while setting up connection to Jira", e);
            }
            this.applinkAndCrowdReady = true;
            return crowdApplicationEntity;
        }
        return null;
    }

    private void addJiraConnectionFailedError(JiraIntegrationConfigurationException exception) {
        LOG.warn("Failure due to exception: " + exception.getMessage());
        ArrayList<String> arguments = new ArrayList<String>();
        arguments.add(this.getDocLink("help.embedded.crowd.directory.configure.crowd"));
        this.addFieldError("jiraBaseUrl", this.getText("error.applicationlink.connection.refused", arguments));
    }

    private void verifyJiraGroups(JiraGroupHelper jiraGroupHelper) {
        if (this.applinkAndCrowdReady) {
            HashSet<String> nonExistingUserGroups = new HashSet<String>();
            for (String group : this.jiraUserGroups) {
                if (jiraGroupHelper.doesGroupExist(group)) continue;
                nonExistingUserGroups.add(group);
            }
            if (nonExistingUserGroups.size() > 0) {
                this.addFieldError("jiraUserGroups", this.getText("error.group.not.exist", this.generateGroupCsvParam(nonExistingUserGroups)));
            }
            HashSet<String> nonExistingAdminGroups = new HashSet<String>();
            for (String group : this.jiraAdminGroups) {
                if (jiraGroupHelper.doesGroupExist(group)) continue;
                nonExistingAdminGroups.add(group);
            }
            if (nonExistingAdminGroups.size() > 0) {
                this.addFieldError("jiraAdminGroups", this.getText("error.group.not.exist", this.generateGroupCsvParam(nonExistingAdminGroups)));
            }
        }
    }

    private List generateGroupCsvParam(Set<String> groups) {
        return Arrays.asList(StringUtils.join((Iterable)Collections2.transform(groups, HtmlUtil::htmlEncode), (char)','));
    }

    private void createDefaultGroups(Directory internalDirectory) {
        long directoryId = internalDirectory.getId();
        try {
            this.createGroupIfNeccessary(directoryId, "confluence-administrators");
        }
        catch (Exception e) {
            String msg = "Failed to create default group: confluence-administrators";
            throw new InfrastructureException(msg, (Throwable)e);
        }
        try {
            this.createGroupIfNeccessary(directoryId, this.userAccessor.getNewUserDefaultGroupName());
        }
        catch (Exception e) {
            String msg = "Failed to create default group: " + this.userAccessor.getNewUserDefaultGroupName();
            throw new InfrastructureException(msg, (Throwable)e);
        }
    }

    private boolean createGroupIfNeccessary(long directoryId, String groupName) throws DirectoryPermissionException, DirectoryNotFoundException, OperationFailedException {
        try {
            this.directoryManager.addGroup(directoryId, new GroupTemplate(groupName, directoryId));
            return true;
        }
        catch (InvalidGroupException e) {
            return false;
        }
    }

    private void setDefaultPermissions() {
        Set<SpacePermission> defaultPerms = this.spacePermissionManager.getDefaultGlobalPermissions();
        for (SpacePermission spacePermission : defaultPerms) {
            this.spacePermissionManager.savePermission(spacePermission);
        }
    }

    private void createAdminInInternalDirectory(Directory internalDirectory) {
        long directoryId = internalDirectory.getId();
        try {
            UserTemplate template = new UserTemplate(this.username, directoryId);
            template.setActive(true);
            this.directoryManager.addUser(internalDirectory.getId().longValue(), template, PasswordCredential.unencrypted((String)this.password));
            for (String groupName : this.userAccessor.getAllDefaultGroupNames()) {
                this.directoryManager.addUserToGroup(directoryId, this.username, groupName);
            }
        }
        catch (Exception e) {
            throw new InfrastructureException("Failed to create admin user", (Throwable)e);
        }
    }

    private void loginAdmin() throws AuthenticatorException {
        boolean isLoginSuccess = SecurityConfigFactory.getInstance().getAuthenticator().login(ServletActionContext.getRequest(), ServletActionContext.getResponse(), this.username, this.password, true);
        if (!isLoginSuccess) {
            LOG.warn("Could not get credential for Rest call due login failed");
            return;
        }
    }

    private Directory getInternalDirectory() {
        for (Directory directory : this.crowdDirectoryService.findAllDirectories()) {
            if (!directory.getImplementationClass().equals(InternalDirectory.class.getName())) continue;
            return directory;
        }
        return null;
    }

    private void setupGlobalPermissions() {
        for (String adminGroup : this.jiraAdminGroups) {
            this.spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission("USECONFLUENCE", null, adminGroup));
            this.spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission("PERSONALSPACE", null, adminGroup));
            this.spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission("ADMINISTRATECONFLUENCE", null, adminGroup));
            this.spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission("SYSTEMADMINISTRATOR", null, adminGroup));
            this.spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission("CREATESPACE", null, adminGroup));
        }
        for (String userGroup : this.jiraUserGroups) {
            this.spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission("USECONFLUENCE", null, userGroup));
            this.spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission("PERSONALSPACE", null, userGroup));
            this.spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission("CREATESPACE", null, userGroup));
        }
    }

    private URI toURI(String url) {
        url = StringUtils.stripEnd((String)StringUtils.trim((String)url), (String)"/");
        try {
            URI uri = new URI(url);
            if (uri.getHost() != null && SUPPORTED_APPLINKS_SCHEMES.contains(uri.getScheme())) {
                return uri;
            }
        }
        catch (URISyntaxException uRISyntaxException) {
            // empty catch block
        }
        if (!url.startsWith("http")) {
            return this.toURI("http://" + url);
        }
        return null;
    }

    private List<String> getGroupNamesFromCsv(String groupNamesCsv) {
        ArrayList<String> groups = new ArrayList<String>();
        if (StringUtils.isBlank((CharSequence)groupNamesCsv)) {
            return groups;
        }
        for (String groupName : StringUtils.split((String)groupNamesCsv, (String)",")) {
            if (!StringUtils.isNotBlank((CharSequence)groupName)) continue;
            groups.add(groupName.trim());
        }
        return groups;
    }

    public void setConfluenceBaseUrl(String confluenceBaseUrl) {
        this.confluenceBaseUrl = confluenceBaseUrl;
    }

    public String getConfluenceBaseUrl() {
        if (this.confluenceBaseUrl == null || this.confluenceBaseUrl.trim().isEmpty()) {
            this.confluenceBaseUrl = this.applinkHostApplication.getBaseUrl().toString();
        }
        return this.confluenceBaseUrl;
    }

    public ManifestRetriever getApplinkManifestRetriever() {
        return this.applinkManifestRetriever;
    }

    public void setApplinkManifestRetriever(ManifestRetriever applinkManifestRetriever) {
        this.applinkManifestRetriever = applinkManifestRetriever;
    }

    public MutatingApplicationLinkService getApplinkApplicationLink() {
        return this.applinkApplicationLink;
    }

    public void setApplinkApplicationLink(MutatingApplicationLinkService applinkApplicationLink) {
        this.applinkApplicationLink = applinkApplicationLink;
    }

    public TypeAccessor getApplinkTypeAccessor() {
        return this.applinkTypeAccessor;
    }

    public void setApplinkTypeAccessor(TypeAccessor applinkTypeAccessor) {
        this.applinkTypeAccessor = applinkTypeAccessor;
    }

    public String getJiraBaseUrl() {
        return this.jiraBaseUrl;
    }

    public void setJiraBaseUrl(String jiraBaseUrl) {
        this.jiraBaseUrl = jiraBaseUrl;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public HostApplication getApplinkHostApplication() {
        return this.applinkHostApplication;
    }

    public void setApplinkHostApplication(HostApplication applinkHostApplication) {
        this.applinkHostApplication = applinkHostApplication;
    }

    public CrowdDirectoryService getCrowdDirectoryService() {
        return this.crowdDirectoryService;
    }

    public void setCrowdDirectoryService(CrowdDirectoryService crowdDirectoryService) {
        this.crowdDirectoryService = crowdDirectoryService;
    }

    public void setCrowdDirectoryManager(DirectoryManager directoryManager) {
        this.directoryManager = directoryManager;
    }

    @Override
    public void setSpacePermissionManager(SpacePermissionManager spacePermissionManager) {
        this.spacePermissionManager = spacePermissionManager;
    }

    public void setEmbeddedCrowdBootstrap(EmbeddedCrowdBootstrap embeddedCrowdBootstrap) {
        this.embeddedCrowdBootstrap = embeddedCrowdBootstrap;
    }

    public String getJiraUserGroups() {
        return Joiner.on((String)",").join(this.jiraUserGroups);
    }

    public String getJiraAdminGroups() {
        return Joiner.on((String)",").join(this.jiraAdminGroups);
    }

    public void setJiraUserGroups(String userGroups) {
        this.jiraUserGroups = this.getGroupNamesFromCsv(userGroups);
    }

    public void setJiraAdminGroups(String adminGroups) {
        this.jiraAdminGroups = this.getGroupNamesFromCsv(adminGroups);
    }

    public boolean isApplinkAndCrowdReady() {
        return this.applinkAndCrowdReady;
    }

    public void setApplinkAndCrowdReady(boolean applinkAndCrowdReady) {
        this.applinkAndCrowdReady = applinkAndCrowdReady;
    }

    public String getCrowdApplicationName() {
        return this.crowdApplicationName;
    }

    public void setCrowdApplicationName(String crowdApplicationName) {
        this.crowdApplicationName = crowdApplicationName;
    }

    public String getCrowdApplicationPassword() {
        return this.crowdApplicationPassword;
    }

    public void setCrowdApplicationPassword(String crowdApplicationPassword) {
        this.crowdApplicationPassword = crowdApplicationPassword;
    }
}

