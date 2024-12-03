/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.ldap.LDAPPropertiesMapper
 *  com.atlassian.crowd.directory.ldap.LdapTypeConfig
 *  com.atlassian.crowd.embedded.api.Directory
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
package com.atlassian.crowd.embedded.admin.delegatingldap;

import com.atlassian.crowd.directory.ldap.LDAPPropertiesMapper;
import com.atlassian.crowd.directory.ldap.LdapTypeConfig;
import com.atlassian.crowd.embedded.admin.ConfigurationController;
import com.atlassian.crowd.embedded.admin.delegatingldap.DelegatingLdapDirectoryConfiguration;
import com.atlassian.crowd.embedded.admin.delegatingldap.DelegatingLdapDirectoryConfigurationValidator;
import com.atlassian.crowd.embedded.api.Directory;
import java.util.List;
import java.util.Map;
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
@RequestMapping(value={"/configure/delegatingldap/**"})
public final class DelegatingLdapConfigurationController
extends ConfigurationController<DelegatingLdapDirectoryConfiguration> {
    private static final String FORM_VIEW = "configure-delegatingldap-form";
    private static final String SUCCESS_VIEW = "redirect:/plugins/servlet/embedded-crowd/directories/list";
    @Autowired
    private LDAPPropertiesMapper ldapPropertiesMapper;

    @Override
    protected String getFormView() {
        return FORM_VIEW;
    }

    @Override
    protected String getSuccessView() {
        return SUCCESS_VIEW;
    }

    @Override
    protected Directory createDirectory(DelegatingLdapDirectoryConfiguration command) {
        return this.directoryMapper.buildDelegatingLdapDirectory(command);
    }

    @Override
    protected DelegatingLdapDirectoryConfiguration createConfigurationFromRequest(HttpServletRequest request) throws Exception {
        if (this.directoryContextHelper.hasDirectoryId(request)) {
            Directory directory = this.directoryContextHelper.getDirectory(request);
            return this.directoryMapper.toDelegatingLdapConfiguration(directory);
        }
        DelegatingLdapDirectoryConfiguration configuration = new DelegatingLdapDirectoryConfiguration();
        configuration.setLdapAutoAddGroups(this.getDefaultLdapAutoAddGroups());
        this.directoryMapper.setDefaultSpringLdapProperties(configuration);
        return configuration;
    }

    @RequestMapping(method={RequestMethod.POST})
    public final ModelAndView onSubmit(HttpServletRequest request, @Valid @ModelAttribute(value="configuration") DelegatingLdapDirectoryConfiguration configuration, BindingResult errors) throws Exception {
        return this.handleSubmit(request, configuration, errors);
    }

    @InitBinder
    protected void initRequiredFields(WebDataBinder binder) {
        binder.setRequiredFields(new String[]{"name", "type", "hostname", "port", "ldapUserUsername"});
    }

    @InitBinder(value={"configuration"})
    protected void initConfigurationValidator(WebDataBinder binder) {
        binder.setValidator((Validator)new DelegatingLdapDirectoryConfigurationValidator());
    }

    @ModelAttribute(value="ldapDirectoryTypes")
    public Map<String, String> getLdapDirectoryTypes() {
        return this.ldapPropertiesMapper.getImplementations().entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }

    @ModelAttribute(value="ldapTypeConfigurations")
    public List<LdapTypeConfig> getLdapTypeConfigurations() {
        return this.ldapPropertiesMapper.getLdapTypeConfigurations();
    }
}

