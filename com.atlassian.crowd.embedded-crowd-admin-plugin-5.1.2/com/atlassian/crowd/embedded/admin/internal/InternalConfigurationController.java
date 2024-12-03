/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.stereotype.Controller
 *  org.springframework.validation.BindingResult
 *  org.springframework.web.bind.annotation.ModelAttribute
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RequestMethod
 *  org.springframework.web.servlet.ModelAndView
 */
package com.atlassian.crowd.embedded.admin.internal;

import com.atlassian.crowd.embedded.admin.ConfigurationController;
import com.atlassian.crowd.embedded.admin.internal.InternalDirectoryConfiguration;
import com.atlassian.crowd.embedded.api.Directory;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value={"/configure/internal/**"})
public final class InternalConfigurationController
extends ConfigurationController<InternalDirectoryConfiguration> {
    private static final String FORM_VIEW = "configure-internal-form";
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
    protected Directory createDirectory(InternalDirectoryConfiguration configuration) {
        return this.directoryMapper.buildInternalDirectory(configuration);
    }

    @Override
    protected InternalDirectoryConfiguration createConfigurationFromRequest(HttpServletRequest request) throws Exception {
        if (this.directoryContextHelper.hasDirectoryId(request)) {
            Directory directory = this.directoryContextHelper.getDirectory(request);
            return this.directoryMapper.toInternalConfiguration(directory);
        }
        return new InternalDirectoryConfiguration();
    }

    @RequestMapping(method={RequestMethod.POST})
    public final ModelAndView onSubmit(HttpServletRequest request, @ModelAttribute(value="configuration") InternalDirectoryConfiguration configuration, BindingResult errors) throws Exception {
        return this.handleSubmit(request, configuration, errors);
    }
}

