/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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
package com.atlassian.crowd.embedded.admin.crowd;

import com.atlassian.crowd.embedded.admin.ConfigurationController;
import com.atlassian.crowd.embedded.admin.crowd.CrowdDirectoryConfiguration;
import com.atlassian.crowd.embedded.admin.crowd.CrowdDirectoryConfigurationValidator;
import com.atlassian.crowd.embedded.admin.crowd.CrowdPermissionOption;
import com.atlassian.crowd.embedded.admin.plugin.PermissionOptionResolver;
import com.atlassian.crowd.embedded.api.Directory;
import java.util.ArrayList;
import java.util.List;
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
@RequestMapping(value={"/configure/crowd/**", "/configure/jira/**"})
public final class CrowdConfigurationController
extends ConfigurationController<CrowdDirectoryConfiguration> {
    private static final String FORM_VIEW = "configure-crowd-form";
    private static final String SUCCESS_VIEW = "redirect:/plugins/servlet/embedded-crowd/directories/list";
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

    @ModelAttribute(value="serverType")
    public String getServerType(HttpServletRequest request) throws Exception {
        return CrowdConfigurationController.isCrowdRequest(request) ? "crowd" : "jira";
    }

    @Override
    protected Directory createDirectory(CrowdDirectoryConfiguration configuration) {
        return this.directoryMapper.buildCrowdDirectory(configuration);
    }

    @Override
    protected CrowdDirectoryConfiguration createConfigurationFromRequest(HttpServletRequest request) throws Exception {
        if (this.directoryContextHelper.hasDirectoryId(request)) {
            Directory directory = this.directoryContextHelper.getDirectory(request);
            return this.directoryMapper.toCrowdConfiguration(directory);
        }
        CrowdDirectoryConfiguration configuration = new CrowdDirectoryConfiguration();
        configuration.setName(CrowdConfigurationController.isCrowdRequest(request) ? "Crowd Server" : "Jira Server");
        return configuration;
    }

    private static boolean isCrowdRequest(HttpServletRequest request) {
        return request.getPathInfo().endsWith("/crowd/");
    }

    @RequestMapping(method={RequestMethod.POST})
    public final ModelAndView onSubmit(HttpServletRequest request, @Valid @ModelAttribute(value="configuration") CrowdDirectoryConfiguration configuration, BindingResult errors) throws Exception {
        return this.handleSubmit(request, configuration, errors);
    }

    @InitBinder
    protected void initRequiredFields(WebDataBinder binder) {
        binder.setRequiredFields(new String[]{"name", "crowdServerUrl", "applicationName", "crowdPermissionOption", "crowdServerSynchroniseIntervalInMin"});
    }

    @InitBinder(value={"configuration"})
    protected void initConfigurationValidator(WebDataBinder binder) {
        binder.setValidator((Validator)new CrowdDirectoryConfigurationValidator());
    }

    @ModelAttribute(value="crowdPermissionOptions")
    public List<String> getCrowdPermissionOptions() {
        ArrayList<String> options = new ArrayList<String>();
        for (CrowdPermissionOption option : this.permissionOptionResolver.getEnabledCrowdPermissionOptions()) {
            options.add(option.name());
        }
        return options;
    }
}

