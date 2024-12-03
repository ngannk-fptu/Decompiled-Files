/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.MicrosoftActiveDirectory
 *  com.atlassian.crowd.directory.ldap.LDAPPropertiesMapper
 *  com.atlassian.crowd.directory.ldap.LdapTypeConfig
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.password.factory.PasswordEncoderFactory
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Controller
 *  org.springframework.validation.BindingResult
 *  org.springframework.validation.Validator
 *  org.springframework.web.bind.WebDataBinder
 *  org.springframework.web.bind.annotation.InitBinder
 *  org.springframework.web.bind.annotation.ModelAttribute
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RequestMethod
 *  org.springframework.web.servlet.ModelAndView
 */
package com.atlassian.crowd.embedded.admin.ldap;

import com.atlassian.crowd.directory.MicrosoftActiveDirectory;
import com.atlassian.crowd.directory.ldap.LDAPPropertiesMapper;
import com.atlassian.crowd.directory.ldap.LdapTypeConfig;
import com.atlassian.crowd.embedded.admin.ConfigurationController;
import com.atlassian.crowd.embedded.admin.ldap.LdapDirectoryConfiguration;
import com.atlassian.crowd.embedded.admin.ldap.LdapDirectoryConfigurationValidator;
import com.atlassian.crowd.embedded.admin.plugin.PermissionOptionResolver;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.password.factory.PasswordEncoderFactory;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value={"/configure/activedirectory/**", "/configure/ldap/**"})
public final class LdapConfigurationController
extends ConfigurationController<LdapDirectoryConfiguration> {
    private static final String FORM_VIEW = "configure-ldap-form";
    private static final String SUCCESS_VIEW = "redirect:/plugins/servlet/embedded-crowd/directories/troubleshoot?directoryId={directoryId}&forceTest=true";
    @Autowired
    private PasswordEncoderFactory passwordEncoderFactory;
    @Autowired
    private LDAPPropertiesMapper ldapPropertiesMapper;
    @Autowired
    private PermissionOptionResolver permissionOptionResolver;

    @Override
    protected String getFormView() {
        return FORM_VIEW;
    }

    @Override
    protected String getSuccessView() {
        return SUCCESS_VIEW;
    }

    @Override
    protected Directory createDirectory(LdapDirectoryConfiguration command) {
        return this.directoryMapper.buildLdapDirectory(command);
    }

    @Override
    protected LdapDirectoryConfiguration createConfigurationFromRequest(HttpServletRequest request) throws Exception {
        if (this.directoryContextHelper.hasDirectoryId(request)) {
            Directory directory = this.directoryContextHelper.getDirectory(request);
            return this.directoryMapper.toLdapConfiguration(directory);
        }
        LdapDirectoryConfiguration configuration = new LdapDirectoryConfiguration();
        if (request.getPathInfo().endsWith("/activedirectory/")) {
            configuration.setType(MicrosoftActiveDirectory.class.getName());
            configuration.setName(this.getI18nResolver().getText("embedded.crowd.directory.edit.ldap.field.default.ad"));
        } else {
            configuration.setName(this.getI18nResolver().getText("embedded.crowd.directory.edit.ldap.field.default.ldap"));
        }
        configuration.setLdapAutoAddGroups(this.getDefaultLdapAutoAddGroups());
        this.directoryMapper.setDefaultSpringLdapProperties(configuration);
        return configuration;
    }

    @RequestMapping(method={RequestMethod.POST})
    public final ModelAndView onSubmit(HttpServletRequest request, @Valid @ModelAttribute(value="configuration") LdapDirectoryConfiguration configuration, BindingResult errors) throws Exception {
        return this.handleSubmit(request, configuration, errors);
    }

    @InitBinder
    protected void initRequiredFields(WebDataBinder binder) {
        binder.setRequiredFields(new String[]{"name", "type", "hostname", "port", "ldapPermissionOption", "ldapUserObjectclass", "ldapUserFilter", "ldapUserUsername", "ldapUserFirstname", "ldapUserLastname", "ldapUserDisplayname", "ldapUserEmail", "ldapUserGroup", "ldapUserPassword", "ldapGroupObjectclass", "ldapGroupFilter", "ldapGroupName", "ldapGroupDescription", "ldapGroupUsernames", "ldapCacheSynchroniseIntervalInMin"});
    }

    @InitBinder(value={"configuration"})
    protected void initConfigurationValidator(WebDataBinder binder) {
        binder.setValidator((Validator)new LdapDirectoryConfigurationValidator());
    }

    @ModelAttribute(value="ldapDirectoryTypes")
    public Map<String, String> getLdapDirectoryTypes() {
        return this.ldapPropertiesMapper.getImplementations().entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }

    @ModelAttribute(value="ldapPermissionOptions")
    public List<String> getLdapPermissionOptions() {
        return this.permissionOptionResolver.getEnabledPermissionOptions().stream().map(Enum::name).collect(Collectors.toList());
    }

    @ModelAttribute(value="ldapTypeConfigurations")
    public List<LdapTypeConfig> getLdapTypeConfigurations() {
        return this.ldapPropertiesMapper.getLdapTypeConfigurations();
    }

    @ModelAttribute(value="ldapPasswordEncryptionTypes")
    public Map<String, String> getPasswordEncryptionTypes() {
        return this.passwordEncoderFactory.getSupportedLdapEncoders().stream().collect(Collectors.toMap(Function.identity(), String::toUpperCase));
    }
}

