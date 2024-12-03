/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.stereotype.Controller
 *  org.springframework.validation.BindingResult
 *  org.springframework.web.bind.WebDataBinder
 *  org.springframework.web.bind.annotation.InitBinder
 *  org.springframework.web.bind.annotation.ModelAttribute
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RequestMethod
 *  org.springframework.web.servlet.ModelAndView
 */
package com.atlassian.crowd.embedded.admin.jirajdbc;

import com.atlassian.crowd.embedded.admin.ConfigurationController;
import com.atlassian.crowd.embedded.admin.jirajdbc.JiraJdbcDirectoryConfiguration;
import com.atlassian.crowd.embedded.api.Directory;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value={"/configure/jirajdbc/**"})
public class JiraJdbcConfigurationController
extends ConfigurationController<JiraJdbcDirectoryConfiguration> {
    private static final String FORM_VIEW = "configure-jirajdbc-form";
    private static final String SUCCESS_VIEW = "redirect:/plugins/servlet/embedded-crowd/directories/list";

    @Override
    protected String getFormView() {
        return FORM_VIEW;
    }

    @Override
    protected String getSuccessView() {
        return SUCCESS_VIEW;
    }

    @Override
    protected Directory createDirectory(JiraJdbcDirectoryConfiguration configuration) {
        return this.directoryMapper.buildJiraJdbcDirectory(configuration);
    }

    @Override
    protected JiraJdbcDirectoryConfiguration createConfigurationFromRequest(HttpServletRequest request) throws Exception {
        if (this.directoryContextHelper.hasDirectoryId(request)) {
            Directory directory = this.directoryContextHelper.getDirectory(request);
            return this.directoryMapper.toJiraJdbcConfiguration(directory);
        }
        return new JiraJdbcDirectoryConfiguration();
    }

    @RequestMapping(method={RequestMethod.POST})
    public final ModelAndView onSubmit(HttpServletRequest request, @ModelAttribute(value="configuration") JiraJdbcDirectoryConfiguration configuration, BindingResult errors) throws Exception {
        return this.handleSubmit(request, configuration, errors);
    }

    @InitBinder
    protected void initRequiredFields(WebDataBinder binder) {
        binder.setRequiredFields(new String[]{"name", "datasourceJndiName"});
    }
}

