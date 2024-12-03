/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugin.notifications.api.template;

import com.atlassian.plugin.notifications.api.macros.MacroResolver;
import com.atlassian.plugin.notifications.api.medium.RecipientType;
import com.atlassian.plugin.notifications.api.template.TemplatePathResolver;
import com.atlassian.plugin.notifications.spi.TemplateParams;
import com.atlassian.plugin.notifications.spi.TemplateParamsBuilder;
import com.atlassian.sal.api.ApplicationProperties;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class TemplatePathResolverImpl
implements TemplatePathResolver {
    private final ApplicationProperties applicationProperties;
    private final MacroResolver macroResolver;

    public TemplatePathResolverImpl(ApplicationProperties applicationProperties, MacroResolver macroResolver) {
        this.applicationProperties = applicationProperties;
        this.macroResolver = macroResolver;
    }

    @Override
    public Iterable<TemplatePathResolver.TemplatePath> getCustomTemplatePaths(TemplateParams params) {
        ArrayList paths = Lists.newArrayList();
        String customTemplatePath = params.getCustomTemplatePath();
        if (StringUtils.isNotBlank((CharSequence)params.getCustomTemplatePath())) {
            while (StringUtils.isNotBlank((CharSequence)customTemplatePath) && customTemplatePath.indexOf(123) != -1) {
                this.getAllTemplatePaths(params, paths, customTemplatePath);
                customTemplatePath = new File(customTemplatePath).getParent();
            }
            if (StringUtils.isNotBlank((CharSequence)customTemplatePath)) {
                this.getAllTemplatePaths(params, paths, customTemplatePath);
            }
        }
        return paths;
    }

    private void getAllTemplatePaths(TemplateParams params, List<TemplatePathResolver.TemplatePath> paths, String customTemplatePath) {
        String resolvedCustomTemplatePath = this.macroResolver.resolveAll(customTemplatePath, params.getContext());
        File customTemplateDirectory = this.getCustomTemplateDirectory(resolvedCustomTemplatePath);
        if (customTemplateDirectory != null) {
            TemplateParams newParams = TemplateParamsBuilder.create(params).mediumKey(null).build();
            Iterables.addAll(paths, this.getTemplatePaths(customTemplateDirectory, newParams));
        }
    }

    @Override
    public Iterable<TemplatePathResolver.TemplatePath> getTemplatePaths(File baseDir, TemplateParams params) {
        File base;
        ArrayList ret = Lists.newArrayList();
        File file = base = params.getMediumKey() != null ? new File(baseDir, params.getMediumKey() + File.separator + params.getTemplateType()) : new File(baseDir, params.getTemplateType());
        if (StringUtils.isNotBlank((CharSequence)params.getEventTypeKey())) {
            if (params.getRecipientType().equals((Object)RecipientType.INDIVIDUAL)) {
                ret.add(new TemplatePathResolver.TemplatePath(base, params.getEventTypeKey() + "_individual.vm"));
            } else {
                ret.add(new TemplatePathResolver.TemplatePath(base, params.getEventTypeKey() + "_group.vm"));
            }
            ret.add(new TemplatePathResolver.TemplatePath(base, params.getEventTypeKey() + ".vm"));
        }
        if (params.getRecipientType().equals((Object)RecipientType.INDIVIDUAL)) {
            ret.add(new TemplatePathResolver.TemplatePath(base, "individual.vm"));
        } else {
            ret.add(new TemplatePathResolver.TemplatePath(base, "group.vm"));
        }
        ret.add(new TemplatePathResolver.TemplatePath(base, "generic.vm"));
        return ret;
    }

    private File getCustomTemplateDirectory(String customTemplatePath) {
        File homeNotificationsDir = this.getHomeNotificationsDirectory();
        if (homeNotificationsDir == null) {
            return null;
        }
        if (StringUtils.isBlank((CharSequence)customTemplatePath)) {
            return null;
        }
        File customTemplateDir = new File(homeNotificationsDir, customTemplatePath);
        if (this.isSecure(customTemplateDir) && customTemplateDir.exists() && customTemplateDir.canRead()) {
            return customTemplateDir;
        }
        return null;
    }

    private File getHomeNotificationsDirectory() {
        File homeDirectory = this.applicationProperties.getHomeDirectory();
        File dataDir = new File(homeDirectory, "data");
        dataDir.mkdirs();
        File templatesDir = new File(dataDir, "notification-templates");
        if (templatesDir.exists() && templatesDir.canRead()) {
            return templatesDir;
        }
        return null;
    }

    private boolean isSecure(File templateFile) {
        return TemplatePathResolverImpl.ensurePathInSecureDir(this.getHomeNotificationsDirectory().getAbsolutePath(), templateFile.getAbsolutePath());
    }

    private static boolean ensurePathInSecureDir(String secureDir, String untrustedPath) {
        try {
            String canonicalSecureDir = new File(secureDir).getCanonicalPath();
            String canonicalUntrustedPath = new File(untrustedPath).getCanonicalPath();
            return canonicalUntrustedPath.startsWith(canonicalSecureDir);
        }
        catch (IOException e) {
            return false;
        }
    }
}

