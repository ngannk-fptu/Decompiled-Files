/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.Condition
 *  com.atlassian.plugin.web.conditions.AlwaysDisplayCondition
 *  io.atlassian.fugue.Option
 *  javax.annotation.Nonnull
 */
package com.atlassian.gadgets.plugins;

import com.atlassian.gadgets.directory.Category;
import com.atlassian.plugin.web.Condition;
import com.atlassian.plugin.web.conditions.AlwaysDisplayCondition;
import io.atlassian.fugue.Option;
import java.io.Writer;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

public interface DashboardItemModule {
    public static final Condition ALWAYS_TRUE_CONDITION = new AlwaysDisplayCondition();

    public Option<DirectoryDefinition> getDirectoryDefinition();

    public boolean isConfigurable();

    public Option<String> getAMDModule();

    public Option<String> getWebResourceKey();

    public void renderContent(Writer var1, Map<String, Object> var2);

    @Nonnull
    public Condition getCondition();

    public static interface Author {
        public String getFullname();

        public Option<String> getEmail();
    }

    public static interface DirectoryDefinition {
        public String getTitle();

        public Option<String> getTitleI18nKey();

        public Author getAuthor();

        public Set<Category> getCategories();

        public Option<URI> getThumbnail();
    }
}

