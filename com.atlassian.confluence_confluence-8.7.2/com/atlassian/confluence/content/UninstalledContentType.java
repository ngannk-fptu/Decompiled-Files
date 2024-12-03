/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 */
package com.atlassian.confluence.content;

import com.atlassian.confluence.content.ContentEntityAdapter;
import com.atlassian.confluence.content.ContentType;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.content.ui.ContentUiSupport;
import com.atlassian.confluence.content.ui.SimpleUiSupport;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.VersionChildOwnerPolicy;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.security.NoPermissionDelegate;
import com.atlassian.confluence.security.PermissionDelegate;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;

public class UninstalledContentType
implements ContentType {
    private final WebResourceUrlProvider webResourceUrlProvider;

    public static ContentType getInstance(WebResourceUrlProvider webResourceUrlProvider) {
        return new UninstalledContentType(webResourceUrlProvider);
    }

    private UninstalledContentType(WebResourceUrlProvider webResourceUrlProvider) {
        this.webResourceUrlProvider = webResourceUrlProvider;
    }

    @Override
    public ContentEntityAdapter getContentAdapter() {
        return new UninstalledPluginContentAdapter();
    }

    @Override
    public PermissionDelegate getPermissionDelegate() {
        return new NoPermissionDelegate();
    }

    @Override
    public ContentUiSupport getContentUiSupport() {
        return SimpleUiSupport.getUnknown(this.webResourceUrlProvider);
    }

    private static class UninstalledPluginContentAdapter
    implements ContentEntityAdapter {
        private UninstalledPluginContentAdapter() {
        }

        @Override
        @Deprecated
        public Option<String> getUrlPath(CustomContentEntityObject pluginContentEntityObject) {
            return Option.none();
        }

        @Override
        @Deprecated
        public Option<String> getDisplayTitle(CustomContentEntityObject pluginContentEntityObject) {
            return Option.none();
        }

        @Override
        @Deprecated
        public Option<String> getNameForComparison(CustomContentEntityObject pluginContentEntityObject) {
            return Option.none();
        }

        @Override
        @Deprecated
        public Option<String> getAttachmentsUrlPath(CustomContentEntityObject pluginContentEntityObject) {
            return Option.none();
        }

        @Override
        @Deprecated
        public Option<String> getAttachmentUrlPath(CustomContentEntityObject pluginContentEntityObject, Attachment attachment) {
            return Option.none();
        }

        @Override
        public BodyType getDefaultBodyType(CustomContentEntityObject pluginContentEntityObject) {
            return BodyType.UNKNOWN;
        }

        @Override
        @Deprecated
        public Option<String> getExcerpt(CustomContentEntityObject pluginContentEntityObject) {
            return Option.none();
        }

        @Override
        public boolean isAllowedParent(CustomContentEntityObject child, CustomContentEntityObject parent) {
            return false;
        }

        @Override
        public boolean isAllowedContainer(ContentEntityObject child, ContentEntityObject owner) {
            return false;
        }

        @Override
        public boolean isIndexable(CustomContentEntityObject pluginContentEntityObject, boolean isDefaultIndexable) {
            return false;
        }

        @Override
        public boolean shouldConvertToContent(CustomContentEntityObject pluginContentEntityObject) {
            return false;
        }

        @Override
        public VersionChildOwnerPolicy getVersionChildPolicy(com.atlassian.confluence.api.model.content.ContentType contentType) {
            return VersionChildOwnerPolicy.currentVersion;
        }
    }
}

