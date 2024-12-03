/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.GadgetRequestContext
 *  com.atlassian.gadgets.GadgetRequestContext$User
 *  com.atlassian.gadgets.dashboard.DashboardItemModules
 *  com.atlassian.gadgets.directory.Category
 *  com.atlassian.gadgets.directory.Directory$DashboardDirectoryEntry
 *  com.atlassian.gadgets.directory.Directory$Entry
 *  com.atlassian.gadgets.directory.Directory$EntryScope
 *  com.atlassian.gadgets.directory.DirectoryEntryVisitor
 *  com.atlassian.gadgets.plugins.DashboardItemModule
 *  com.atlassian.gadgets.plugins.DashboardItemModule$DirectoryDefinition
 *  com.atlassian.gadgets.plugins.DashboardItemModuleDescriptor
 *  com.atlassian.gadgets.util.DashboardItemConditionContext
 *  com.atlassian.gadgets.util.I18nFunction
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.web.Condition
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.collect.Iterables
 *  io.atlassian.fugue.Iterables
 *  io.atlassian.fugue.Option
 *  io.atlassian.fugue.Options
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.gadgets.directory.internal.impl;

import com.atlassian.gadgets.GadgetRequestContext;
import com.atlassian.gadgets.dashboard.DashboardItemModules;
import com.atlassian.gadgets.directory.Category;
import com.atlassian.gadgets.directory.Directory;
import com.atlassian.gadgets.directory.DirectoryEntryVisitor;
import com.atlassian.gadgets.directory.internal.DirectoryEntryProvider;
import com.atlassian.gadgets.plugins.DashboardItemModule;
import com.atlassian.gadgets.plugins.DashboardItemModuleDescriptor;
import com.atlassian.gadgets.util.DashboardItemConditionContext;
import com.atlassian.gadgets.util.I18nFunction;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.web.Condition;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.collect.Iterables;
import io.atlassian.fugue.Option;
import io.atlassian.fugue.Options;
import java.net.URI;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class DashboardItemDirectoryEntryProvider
implements DirectoryEntryProvider {
    private final Collection<DashboardItemModules> dashboardItemModules;
    private final ApplicationProperties applicationProperties;
    private final I18nResolver i18n;

    public DashboardItemDirectoryEntryProvider(ApplicationProperties applicationProperties, I18nResolver i18n, Collection<DashboardItemModules> dashboardItemModules) {
        this.dashboardItemModules = dashboardItemModules;
        this.applicationProperties = applicationProperties;
        this.i18n = i18n;
    }

    @Override
    public Iterable<Directory.Entry<?>> entries(final GadgetRequestContext gadgetRequestContext, Directory.EntryScope entryScope) {
        Iterable descriptors = Iterables.concat((Iterable)io.atlassian.fugue.Iterables.transform(this.dashboardItemModules, dim -> dim.getDashboardItemsWithDirectoryDefinition()));
        return Options.flatten((Iterable)Options.filterNone((Iterable)io.atlassian.fugue.Iterables.transform((Iterable)descriptors, (Function)new Function<DashboardItemModuleDescriptor, Option<Directory.Entry<?>>>(){

            @Override
            public Option<Directory.Entry<?>> apply(DashboardItemModuleDescriptor moduleDescriptor) {
                ModuleCompleteKey moduleCompleteKey;
                DashboardItemModule module = (DashboardItemModule)moduleDescriptor.getModule();
                Condition condition = module.getCondition();
                if (condition.shouldDisplay(DashboardItemDirectoryEntryProvider.this.getConditionContext(moduleCompleteKey = new ModuleCompleteKey(moduleDescriptor.getCompleteKey()), gadgetRequestContext).getSerializedContext())) {
                    return Option.some((Object)new DashboardDirectoryEntryImpl(moduleDescriptor, (DashboardItemModule.DirectoryDefinition)moduleDescriptor.getDirectoryDefinition().get(), DashboardItemDirectoryEntryProvider.this.applicationProperties, DashboardItemDirectoryEntryProvider.this.i18n));
                }
                return Option.none();
            }
        })));
    }

    private DashboardItemConditionContext getConditionContext(ModuleCompleteKey moduleCompleteKey, GadgetRequestContext gadgetRequestContext) {
        return DashboardItemConditionContext.forDirectory((ModuleCompleteKey)moduleCompleteKey, (GadgetRequestContext.User)((GadgetRequestContext.User)gadgetRequestContext.getUser().getOrNull()));
    }

    @Override
    public boolean contains(URI gadgetSpecUri) {
        return false;
    }

    private static class DashboardDirectoryEntryImpl
    implements Directory.DashboardDirectoryEntry {
        private final DashboardItemModuleDescriptor moduleDescriptor;
        private final DashboardItemModule.DirectoryDefinition directoryDefinition;
        private final ApplicationProperties applicationProperties;
        private final I18nResolver i18n;

        public DashboardDirectoryEntryImpl(DashboardItemModuleDescriptor moduleDescriptor, DashboardItemModule.DirectoryDefinition directoryDefinition, ApplicationProperties applicationProperties, I18nResolver i18n) {
            this.moduleDescriptor = moduleDescriptor;
            this.directoryDefinition = directoryDefinition;
            this.applicationProperties = applicationProperties;
            this.i18n = i18n;
        }

        @Nullable
        public URI getSelf() {
            return null;
        }

        public boolean isDeletable() {
            return false;
        }

        public ModuleCompleteKey getId() {
            return new ModuleCompleteKey(this.moduleDescriptor.getPluginKey(), this.moduleDescriptor.getKey());
        }

        public String getTitle() {
            return (String)this.directoryDefinition.getTitleI18nKey().map((Function)new I18nFunction(this.i18n)).getOrElse((Object)this.directoryDefinition.getTitle());
        }

        @Nullable
        public URI getTitleUri() {
            return null;
        }

        @Nullable
        public URI getThumbnailUri() {
            return (URI)this.directoryDefinition.getThumbnail().map((Function)new Function<URI, URI>(){

                @Override
                public URI apply(URI uri) {
                    return this.isAbsolute(uri) ? uri : URI.create(applicationProperties.getBaseUrl(UrlMode.AUTO) + uri.toString());
                }

                private boolean isAbsolute(URI uri) {
                    return uri.isAbsolute() || uri.toString().startsWith("//");
                }
            }).getOrNull();
        }

        public String getAuthorName() {
            return this.directoryDefinition.getAuthor().getFullname();
        }

        public String getAuthorEmail() {
            return (String)this.directoryDefinition.getAuthor().getEmail().getOrNull();
        }

        public String getDescription() {
            return (String)Option.option((Object)this.moduleDescriptor.getDescriptionKey()).map((Function)new Function<String, String>(){

                @Override
                public String apply(@Nullable String i18nDescriptionKey) {
                    return i18n.getText(i18nDescriptionKey);
                }
            }).getOrElse((Object)StringUtils.defaultIfEmpty((CharSequence)this.moduleDescriptor.getDescription(), (CharSequence)""));
        }

        @Nonnull
        public Set<Category> getCategories() {
            return this.directoryDefinition.getCategories();
        }

        public <V> V accept(DirectoryEntryVisitor<V> visitor) {
            return (V)visitor.visit((Directory.DashboardDirectoryEntry)this);
        }
    }
}

