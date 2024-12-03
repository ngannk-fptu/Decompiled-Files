/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.directory.ldap.LDAPPropertiesMapper
 *  com.atlassian.crowd.directory.loader.DirectoryInstanceLoader
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.crowd.embedded.api.PermissionOption
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.Message
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Controller
 *  org.springframework.ui.ModelMap
 *  org.springframework.web.bind.annotation.ModelAttribute
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RequestMethod
 *  org.springframework.web.servlet.ModelAndView
 */
package com.atlassian.crowd.embedded.admin.support;

import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.directory.ldap.LDAPPropertiesMapper;
import com.atlassian.crowd.directory.loader.DirectoryInstanceLoader;
import com.atlassian.crowd.embedded.admin.DirectoryContextHelper;
import com.atlassian.crowd.embedded.admin.support.DirectoryTroubleshooter;
import com.atlassian.crowd.embedded.admin.support.TroubleshootingCommand;
import com.atlassian.crowd.embedded.admin.util.HtmlEncoder;
import com.atlassian.crowd.embedded.admin.util.SimpleMessage;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.embedded.api.PermissionOption;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.Message;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value={"/directories/troubleshoot/**"})
public final class TroubleshootingController {
    private static final Logger log = LoggerFactory.getLogger(TroubleshootingController.class);
    private static final String TYPE_KEY_PREFIX = "embedded.crowd.directory.type.";
    private static final String FORM_VIEW = "troubleshooting-form";
    private static final String EDIT_VIEW = "redirect:/plugins/servlet/embedded-crowd/directories/edit?directoryId={directoryId}";
    private static final String CREDENTIAL = "credential";
    @Autowired
    private CrowdDirectoryService crowdDirectoryService;
    @Autowired
    private HtmlEncoder htmlEncoder;
    @Autowired
    private DirectoryContextHelper directoryContextHelper;
    @Autowired
    private I18nResolver i18nResolver;
    @Autowired
    private LDAPPropertiesMapper ldapPropertiesMapper;
    @Autowired
    private DirectoryInstanceLoader directoryInstanceLoader;
    @Autowired
    private DirectoryTroubleshooter directoryTroubleshooter;

    @ModelAttribute
    public void populateDirectoryAttributes(HttpServletRequest request, ModelMap model) {
        try {
            Directory directory = this.directoryContextHelper.getDirectory(request);
            model.addAttribute("directoryName", (Object)directory.getName());
            model.addAttribute("directoryType", (Object)this.getTypeName(directory));
            model.addAttribute("isUserFromDirectory", (Object)this.directoryContextHelper.isContextUserFromDirectory(directory, request));
        }
        catch (DirectoryNotFoundException e) {
            log.error("Directory not found: ", (Throwable)e);
            this.setError((Map<String, Object>)model, "embedded.crowd.directory.not.found");
        }
    }

    @RequestMapping(method={RequestMethod.GET})
    public String initializeForm(@ModelAttribute(value="credential") TroubleshootingCommand tCommand) {
        return FORM_VIEW;
    }

    @RequestMapping(method={RequestMethod.GET}, params={"forceTest=true"})
    public ModelAndView forceTest(@ModelAttribute(value="credential") TroubleshootingCommand tCommand) throws Exception {
        return this.onSubmit(tCommand);
    }

    @RequestMapping(method={RequestMethod.POST})
    public ModelAndView onSubmit(@ModelAttribute(value="credential") TroubleshootingCommand tCommand) throws Exception {
        if (tCommand.redirectToEdit()) {
            String editView = StringUtils.replace((String)EDIT_VIEW, (String)"{directoryId}", (String)String.valueOf(tCommand.getDirectoryId()));
            return new ModelAndView(editView);
        }
        HashMap<String, Object> model = new HashMap<String, Object>();
        model.put(CREDENTIAL, tCommand);
        Directory directory = this.crowdDirectoryService.findDirectoryById(tCommand.getDirectoryId());
        if (directory == null) {
            this.setError(model, "embedded.crowd.directory.not.found");
        } else {
            RemoteDirectory rawDirectory = this.directoryInstanceLoader.getRawDirectory(directory.getId(), directory.getImplementationClass(), directory.getAttributes());
            if (rawDirectory == null) {
                this.setError(model, "embedded.crowd.directory.not.found");
            } else {
                model.put("testResults", this.directoryTroubleshooter.troubleshootDirectory(rawDirectory, tCommand.getUsername(), tCommand.getPassword()));
            }
        }
        return new ModelAndView(FORM_VIEW, model);
    }

    @ModelAttribute(value="htmlEncoder")
    public HtmlEncoder getHtmlEncoder() {
        return this.htmlEncoder;
    }

    private void setError(Map<String, Object> model, String message) {
        model.put("errors", Collections.singleton(this.i18nResolver.getText(message)));
    }

    private Message getTypeName(Directory directory) {
        DirectoryType directoryType = directory.getType();
        switch (directoryType) {
            case CONNECTOR: {
                String implementationName = this.getNameForImplementation(directory.getImplementationClass());
                String name = implementationName == null ? directoryType.name() : implementationName;
                PermissionOption permissionOption = PermissionOption.fromAllowedOperations((Set)directory.getAllowedOperations());
                return SimpleMessage.instance(TYPE_KEY_PREFIX + directoryType.name() + "." + permissionOption.name(), new Serializable[]{name});
            }
            case CUSTOM: {
                return SimpleMessage.instance(TYPE_KEY_PREFIX + directoryType.name() + this.getClassNameOnly(directory.getImplementationClass()), new Serializable[0]);
            }
            case DELEGATING: {
                String implementationClass = directory.getValue("crowd.delegated.directory.type");
                return SimpleMessage.instance(TYPE_KEY_PREFIX + directoryType.name(), new Serializable[]{this.getNameForImplementation(implementationClass)});
            }
        }
        return SimpleMessage.instance(TYPE_KEY_PREFIX + directoryType.name(), new Serializable[0]);
    }

    private String getNameForImplementation(String implementationClass) {
        Map implementations = this.ldapPropertiesMapper.getImplementations();
        for (Map.Entry entry : implementations.entrySet()) {
            if (!((String)entry.getValue()).equals(implementationClass)) continue;
            return (String)entry.getKey();
        }
        return null;
    }

    private String getClassNameOnly(String implementationClass) {
        return implementationClass.substring(implementationClass.lastIndexOf("."));
    }
}

