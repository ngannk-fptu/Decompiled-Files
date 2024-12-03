/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.exception.InfrastructureException
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.seraph.auth.Authenticator
 *  com.atlassian.seraph.auth.AuthenticatorException
 *  com.atlassian.seraph.config.SecurityConfigFactory
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.atlassian.xwork.RequireSecurityToken
 *  org.apache.struts2.ServletActionContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup.actions;

import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.setup.actions.AbstractSetupAction;
import com.atlassian.confluence.user.UserForm;
import com.atlassian.confluence.user.UserFormValidator;
import com.atlassian.confluence.user.crowd.EmbeddedCrowdBootstrap;
import com.atlassian.core.exception.InfrastructureException;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.seraph.auth.Authenticator;
import com.atlassian.seraph.auth.AuthenticatorException;
import com.atlassian.seraph.config.SecurityConfigFactory;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.atlassian.xwork.RequireSecurityToken;
import java.util.List;
import java.util.Set;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSudoRequired
@SystemAdminOnly
public class SetupAdministrator
extends AbstractSetupAction {
    private static final Logger LOGGER = LoggerFactory.getLogger(SetupAdministrator.class);
    private UserFormValidator validator;
    private String username = "admin";
    private String password;
    private String confirm;
    private String email;
    private String fullName;
    private boolean test;
    private EmbeddedCrowdBootstrap embeddedCrowdBootstrap;

    public void setTest(boolean test) {
        this.test = test;
    }

    @Override
    public void validate() {
        UserForm form = new UserForm(this.username, this.fullName, this.email, this.password, this.confirm);
        this.validator.validateNewUser(form, this.messageHolder);
    }

    @PermittedMethods(value={HttpMethod.POST})
    @RequireSecurityToken(value=true)
    public String execute() throws Exception {
        this.embeddedCrowdBootstrap.bootstrap();
        this.createDefaultGroups();
        this.setDefaultPermissions();
        this.createAdmin();
        this.loginAdmin();
        this.getSetupPersister().progessSetupStep();
        this.getSetupPersister().progessSetupStep();
        if ("install".equals(this.getSetupPersister().getSetupType())) {
            return "quick-setup";
        }
        if ("custom".equals(this.getSetupPersister().getSetupType())) {
            return "custom-setup";
        }
        return super.execute();
    }

    private void createDefaultGroups() {
        try {
            this.permissionManager.withExemption(() -> this.userAccessor.createGroup("confluence-administrators"));
            this.userAccessor.getGroup("confluence-administrators");
        }
        catch (Exception e) {
            String msg = "Failed to create default group: confluence-administrators";
            throw new InfrastructureException(msg, (Throwable)e);
        }
        String defaultUsersGroup = this.settingsManager.getGlobalSettings().getDefaultUsersGroup();
        try {
            this.permissionManager.withExemption(() -> this.userAccessor.createGroup(defaultUsersGroup));
            this.userAccessor.getGroup(defaultUsersGroup);
        }
        catch (Exception e) {
            String msg = "Failed to create default group: " + defaultUsersGroup;
            throw new InfrastructureException(msg, (Throwable)e);
        }
    }

    private void setDefaultPermissions() {
        Set<SpacePermission> defaultPerms = this.spacePermissionManager.getDefaultGlobalPermissions();
        for (SpacePermission spacePermission : defaultPerms) {
            this.spacePermissionManager.savePermission(spacePermission);
        }
    }

    private void createAdmin() {
        try {
            List<String> defaultGroupNames = this.userAccessor.getAllDefaultGroupNames();
            String[] groups = defaultGroupNames.toArray(new String[defaultGroupNames.size()]);
            this.permissionManager.withExemption(() -> this.userAccessor.addUser(this.username, this.password, this.email, this.fullName, groups));
        }
        catch (Exception e) {
            throw new InfrastructureException("Failed to create admin user", (Throwable)e);
        }
    }

    private void loginAdmin() throws AuthenticatorException {
        if (this.test) {
            return;
        }
        boolean isLoginSuccess = this.getAuthenticator().login(ServletActionContext.getRequest(), ServletActionContext.getResponse(), this.username, this.password, true);
        if (!isLoginSuccess) {
            LOGGER.warn("Could not get credential for Rest call due login failed");
            return;
        }
    }

    private Authenticator getAuthenticator() {
        return SecurityConfigFactory.getInstance().getAuthenticator();
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

    public String getConfirm() {
        return this.confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmbeddedCrowdBootstrap(EmbeddedCrowdBootstrap embeddedCrowdBootstrap) {
        this.embeddedCrowdBootstrap = embeddedCrowdBootstrap;
    }

    public void setUserFormValidator(UserFormValidator validator) {
        this.validator = validator;
    }
}

