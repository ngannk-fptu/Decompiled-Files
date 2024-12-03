/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  javax.annotation.Nonnull
 *  org.dom4j.Element
 */
package com.atlassian.crowd.embedded.admin.plugin;

import com.atlassian.crowd.embedded.admin.list.NewDirectoryType;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.dom4j.Element;

public class SupportedNewDirectoryTypesModuleDescriptor
extends AbstractModuleDescriptor<List<NewDirectoryType>> {
    private static final String ACTIVE_DIRECTORY = "active-directory";
    private static final String LDAP = "ldap";
    private static final String DELEGATING_LDAP = "delegating-ldap";
    private static final String CROWD = "crowd";
    private static final String JIRA = "jira";
    private static final String JIRAJDBC = "jira-jdbc";
    private List<NewDirectoryType> supportedDirectoryTypes;

    public SupportedNewDirectoryTypesModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element element) throws PluginParseException {
        super.init(plugin, element);
        this.supportedDirectoryTypes = new ArrayList<NewDirectoryType>();
        if (this.hasChild(element, ACTIVE_DIRECTORY)) {
            this.supportedDirectoryTypes.add(NewDirectoryType.ACTIVE_DIRECTORY);
        }
        if (this.hasChild(element, LDAP)) {
            this.supportedDirectoryTypes.add(NewDirectoryType.LDAP);
        }
        if (this.hasChild(element, DELEGATING_LDAP)) {
            this.supportedDirectoryTypes.add(NewDirectoryType.DELEGATING_LDAP);
        }
        if (this.hasChild(element, CROWD)) {
            this.supportedDirectoryTypes.add(NewDirectoryType.CROWD);
        }
        if (this.hasChild(element, JIRA)) {
            this.supportedDirectoryTypes.add(NewDirectoryType.JIRA);
        }
        if (this.hasChild(element, JIRAJDBC)) {
            this.supportedDirectoryTypes.add(NewDirectoryType.JIRAJDBC);
        }
    }

    public List<NewDirectoryType> getModule() {
        return this.supportedDirectoryTypes;
    }

    private boolean hasChild(Element element, String childName) {
        return element.element(childName) != null;
    }
}

