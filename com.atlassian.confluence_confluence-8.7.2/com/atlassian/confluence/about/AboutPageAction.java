/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.about;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.plugin.descriptor.aboutpage.AboutPagePanelModuleDescriptor;
import com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess;
import com.atlassian.confluence.setup.BuildInformation;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.struts2.ServletActionContext;

@RequiresAnyConfluenceAccess
public class AboutPageAction
extends ConfluenceActionSupport {
    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return "success";
    }

    @Deprecated
    public String getCopyrightUntil() {
        return this.getI18n().getText("aboutpage.copyright.until");
    }

    public String getBuildYear() {
        return BuildInformation.INSTANCE.getBuildYear();
    }

    public String getVersionNumber() {
        return GeneralUtil.getVersionNumber();
    }

    public boolean getShowLicenses() throws MalformedURLException {
        return ServletActionContext.getServletContext().getResource("/about/lgpl-libs.vm") != null;
    }

    public List<String> getPluginPanels() {
        ArrayList<String> result = new ArrayList<String>();
        PluginAccessor accessor = (PluginAccessor)ContainerManager.getComponent((String)"pluginAccessor");
        List descriptors = accessor.getEnabledModuleDescriptorsByClass(AboutPagePanelModuleDescriptor.class);
        for (AboutPagePanelModuleDescriptor descriptor : descriptors) {
            result.add(descriptor.getPluginSectionHtml());
        }
        return result;
    }
}

