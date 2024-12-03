/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.exception.InfrastructureException
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.module.ContainerManagedPlugin
 *  com.atlassian.spring.container.ContainerManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin;

import com.atlassian.confluence.plugin.LegacySpringContainerAccessor;
import com.atlassian.core.exception.InfrastructureException;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.module.ContainerManagedPlugin;
import com.atlassian.spring.container.ContainerManager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluencePluginUtils {
    public static final String PLUGIN_DESCRIPTOR_FILENAME = "atlassian-plugin.xml";
    public static final String PARENT_DIRECTORY_PROPERTY_KEY = "confluence.plugin.parentdirectory";
    private static final Logger log = LoggerFactory.getLogger(ConfluencePluginUtils.class);

    public static <T> T instantiatePluginModule(Plugin plugin, Class<T> cls) {
        if (log.isDebugEnabled()) {
            log.debug("Instantiating module '" + cls.getSimpleName() + "' for plugin '" + plugin.getName() + "' Version: " + plugin.getPluginsVersion() + " Description: " + plugin.getPluginInformation().getDescription() + " Key: " + plugin.getKey());
        }
        if (plugin instanceof ContainerManagedPlugin) {
            return LegacySpringContainerAccessor.createBean(plugin, cls);
        }
        return (T)ContainerManager.getInstance().getContainerContext().createComponent(cls);
    }

    public static File getPluginsBaseDirectory(String homeLocation) {
        File homeDirectory = new File(homeLocation);
        String parentDirectory = System.getProperty(PARENT_DIRECTORY_PROPERTY_KEY);
        if (parentDirectory != null) {
            File baseDirectory = new File(homeDirectory, parentDirectory);
            ConfluencePluginUtils.createDirectoryIfDoesntExist(baseDirectory);
            return baseDirectory;
        }
        return homeDirectory;
    }

    public static File createDirectoryIfDoesntExist(File directory) {
        try {
            ConfluencePluginUtils.mkdirs(directory);
            return directory;
        }
        catch (IOException e) {
            throw new InfrastructureException(e.getMessage(), (Throwable)e);
        }
    }

    private static void mkdirs(File directory) throws IOException {
        if (directory != null && !directory.mkdirs() && !directory.isDirectory()) {
            if (directory.isFile()) {
                throw new IOException("Cannot create directory '" + directory + "' due to a file of the same name");
            }
            File child = directory;
            File parent = child.getParentFile();
            while (parent != null && !parent.isDirectory()) {
                child = parent;
                parent = child.getParentFile();
            }
            if (parent != null) {
                StringBuilder troubleShooting = new StringBuilder("Closest directory created: '" + parent + "'. ");
                while (parent != null) {
                    troubleShooting.append("ACL for '").append(parent).append("': ");
                    AclFileAttributeView aclFileAttributeView = Files.getFileAttributeView(parent.toPath(), AclFileAttributeView.class, new LinkOption[0]);
                    if (aclFileAttributeView != null) {
                        troubleShooting.append(aclFileAttributeView.getAcl());
                    } else {
                        PosixFileAttributeView posixFileAttributeView = Files.getFileAttributeView(parent.toPath(), PosixFileAttributeView.class, new LinkOption[0]);
                        PosixFileAttributes attributes = posixFileAttributeView.readAttributes();
                        troubleShooting.append("Owner: '").append(attributes.owner()).append("', Group: '").append(attributes.group()).append("', Permissions: ").append(attributes.permissions());
                    }
                    child = parent;
                    parent = child.getParentFile();
                }
                throw new IOException("Cannot create directory '" + directory + "'. as user: '" + System.getProperty("user.name") + "'. " + troubleShooting);
            }
            throw new IOException("Cannot create directory '" + directory + "' as user: '" + System.getProperty("user.name") + "'. for any segment of the path");
        }
    }
}

