/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.admin.actions;

import com.atlassian.confluence.admin.actions.LookAndFeel;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.impl.security.AdminOnly;
import com.atlassian.confluence.security.CaptchaManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.util.HTMLPairType;
import com.atlassian.confluence.util.LabelUtil;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

@WebSudoRequired
@AdminOnly
public class ConfigureCaptchaAction
extends ConfluenceActionSupport
implements LookAndFeel {
    protected CaptchaManager captchaManager;
    protected Boolean captchaEnabled;
    protected String exclude;
    protected Collection captchaGroups;
    protected Collection invalidGroups = new ArrayList();
    protected String selectedGroups;
    protected String group;

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    @Override
    public void validate() {
        super.validate();
        if (this.selectedGroups != null) {
            for (String checkGroup : this.getSelectedGroupsList()) {
                if (this.userAccessor.getGroup(checkGroup) != null) continue;
                this.invalidGroups.add(checkGroup);
            }
            if (this.invalidGroups.size() > 0) {
                this.addFieldError("captchaGroups", this.getText("captcha.group.not.valid"));
            }
        }
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String execute() throws Exception {
        if (this.captchaEnabled != null) {
            this.captchaManager.setCaptchaEnabled(this.captchaEnabled);
        }
        if (this.exclude != null) {
            this.captchaManager.setExclude(this.exclude.trim());
        }
        if (this.selectedGroups != null) {
            this.captchaManager.setCaptchaGroups(this.getSelectedGroupsList());
        }
        return "success";
    }

    @Override
    public String doDefault() throws Exception {
        this.setExclude(this.settingsManager.getGlobalSettings().getCaptchaSettings().getExclude());
        Collection<String> captchaGroups = this.settingsManager.getGlobalSettings().getCaptchaSettings().getCaptchaGroups();
        this.setSelectedGroups(StringUtils.join(captchaGroups.iterator(), (String)" "));
        this.setSelectedGroups(StringUtils.join(captchaGroups.iterator(), (String)", "));
        return super.doDefault();
    }

    public List getExcludeOptions() {
        ArrayList<HTMLPairType> result = new ArrayList<HTMLPairType>();
        result.add(new HTMLPairType("none", this.getText("captcha.exclude.noone")));
        result.add(new HTMLPairType("registered", this.getText("captcha.registered.users")));
        result.add(new HTMLPairType("groups", this.getText("captcha.group.members")));
        return result;
    }

    public void setCaptchaEnabled(Boolean captchaEnabled) {
        this.captchaEnabled = captchaEnabled;
    }

    public Boolean getCaptchaEnabled() {
        return this.captchaEnabled;
    }

    public String getSelectedGroups() {
        return this.selectedGroups;
    }

    public void setSelectedGroups(String selectedGroups) {
        this.selectedGroups = selectedGroups;
    }

    public Collection getCaptchaGroups() {
        Settings settings = this.settingsManager.getGlobalSettings();
        return settings.getCaptchaSettings().getCaptchaGroups();
    }

    public Collection getSelectedGroupsList() {
        return LabelUtil.split(this.selectedGroups);
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getGroup() {
        return this.group;
    }

    public CaptchaManager getCaptchaManager() {
        return this.captchaManager;
    }

    public void setCaptchaManager(CaptchaManager captchaManager) {
        this.captchaManager = captchaManager;
    }

    public Collection getInvalidGroups() {
        return this.invalidGroups;
    }

    public void setInvalidGroups(Collection invalidGroups) {
        this.invalidGroups = invalidGroups;
    }

    public String getExclude() {
        return this.exclude;
    }

    public void setExclude(String exclude) {
        this.exclude = exclude;
    }
}

