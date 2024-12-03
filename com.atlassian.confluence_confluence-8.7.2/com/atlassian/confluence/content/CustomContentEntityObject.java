/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  net.jcip.annotations.NotThreadSafe
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.content;

import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.content.ContentEntityAdapter;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.core.VersionChildOwnerPolicy;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.Contained;
import com.atlassian.confluence.pages.ContentConvertible;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.Spaced;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.jcip.annotations.NotThreadSafe;
import org.checkerframework.checker.nullness.qual.Nullable;

@NotThreadSafe
public class CustomContentEntityObject
extends SpaceContentEntityObject
implements Contained<ContentEntityObject>,
ContentConvertible {
    public static final String CONTENT_TYPE = "custom";
    private CustomContentEntityObject parent;
    private ContentEntityAdapter adapter;
    private String pluginModuleKey;
    private String pluginVersion;
    private boolean gettingUrlGuard = false;

    @Override
    public String getType() {
        return CONTENT_TYPE;
    }

    @Override
    public String getUrlPath() {
        try {
            if (!this.gettingUrlGuard) {
                this.gettingUrlGuard = true;
                Optional<String> path = this.adapter.urlPath(this);
                String string = path.orElse("");
                return string;
            }
            String string = "";
            return string;
        }
        finally {
            this.gettingUrlGuard = false;
        }
    }

    @Override
    public String getDisplayTitle() {
        return this.adapter.displayTitle(this).orElseGet(() -> super.getDisplayTitle());
    }

    @Override
    public String getNameForComparison() {
        return this.adapter.nameForComparison(this).orElseGet(this::getDisplayTitle);
    }

    @Override
    public String getAttachmentUrlPath(Attachment attachment) {
        return this.adapter.attachmentUrlPath(this, attachment).orElseGet(() -> super.getAttachmentUrlPath(attachment));
    }

    @Override
    public String getAttachmentsUrlPath() {
        return this.adapter.attachmentsUrlPath(this).orElseGet(() -> super.getAttachmentsUrlPath());
    }

    @Override
    public String getExcerpt() {
        return this.adapter.excerpt(this).orElseGet(() -> super.getExcerpt());
    }

    @Override
    public BodyType getDefaultBodyType() {
        return this.adapter.getDefaultBodyType(this);
    }

    public String getPluginModuleKey() {
        return this.pluginModuleKey;
    }

    public void setPluginModuleKey(String pluginModuleKey) {
        this.pluginModuleKey = pluginModuleKey;
    }

    public String getPluginVersion() {
        return this.pluginVersion;
    }

    public void setPluginVersion(String pluginVersion) {
        this.pluginVersion = pluginVersion;
    }

    public CustomContentEntityObject getParent() {
        return this.parent;
    }

    public void setParent(CustomContentEntityObject parent) {
        if (!this.adapter.isAllowedParent(this, parent)) {
            throw new IllegalArgumentException(parent + " is not a permitted parent of " + this);
        }
        this.checkAncestorValid(parent);
        this.parent = parent;
    }

    private CustomContentEntityObject getParentContent() {
        return this.parent;
    }

    private void setParentContent(CustomContentEntityObject parent) {
        this.parent = parent;
    }

    @Override
    public @Nullable ContentEntityObject getContainer() {
        return this.getContainerContent();
    }

    public void setContainer(@Nullable ContentEntityObject container) {
        if (!this.adapter.isAllowedContainer(this, container)) {
            throw new IllegalArgumentException(container + " is not a permitted container of " + this + " (denied by child adapter)");
        }
        if (container instanceof CustomContentEntityObject && !((CustomContentEntityObject)container).adapter.isAllowedContainer(this, container)) {
            throw new IllegalArgumentException(container + " is not a permitted container of " + this + " (denied by container adapter)");
        }
        this.checkContainerHierarchyValid(container);
        this.setContainerContent(container);
    }

    public void setAdapter(ContentEntityAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void convertToHistoricalVersion() {
        super.convertToHistoricalVersion();
        this.parent = null;
    }

    @Override
    public boolean isIndexable() {
        return this.adapter.isIndexable(this, super.isIndexable());
    }

    @Override
    public String toString() {
        String toString = "[" + this.getPluginModuleKey() + "] ";
        if (this.adapter == null) {
            return toString + this.getType() + ": (" + this.getId() + ")";
        }
        return toString + super.toString();
    }

    public List<CustomContentEntityObject> getAncestors() {
        if (this.getParent() == null) {
            return Collections.emptyList();
        }
        ArrayList<CustomContentEntityObject> ancestors = new ArrayList<CustomContentEntityObject>();
        for (CustomContentEntityObject parent = this.getParent(); parent != null; parent = parent.getParent()) {
            ancestors.add(parent);
        }
        Collections.reverse(ancestors);
        return ancestors;
    }

    private void checkAncestorValid(CustomContentEntityObject parent) {
        boolean parentInSameSpace;
        if (parent == null) {
            return;
        }
        if (parent == this || this.getId() != 0L && parent.getId() == this.getId()) {
            throw new IllegalArgumentException("Can not set content as its own ancestor.");
        }
        Space parentSpace = parent.getSpace();
        Space childSpace = this.getSpace();
        boolean bl = parentInSameSpace = parentSpace == null || childSpace == null || parentSpace.getKey() == null || childSpace.getKey() == null || parentSpace.getKey().equals(childSpace.getKey());
        if (!parentInSameSpace) {
            throw new IllegalArgumentException("Can't add an ancestor from another space.");
        }
        if (parent.getParent() != null) {
            this.checkAncestorValid(parent.getParent());
        }
    }

    private void checkContainerHierarchyValid(ContentEntityObject owner) {
        boolean ownerInSameSpace;
        if (owner == null) {
            return;
        }
        if (owner == this || this.getId() != 0L && owner.getId() == this.getId()) {
            throw new IllegalArgumentException("Can not set content as its own owner.");
        }
        Space ownerSpace = CustomContentEntityObject.getOwnerSpace(owner);
        Space childSpace = this.getSpace();
        boolean bl = ownerInSameSpace = ownerSpace == null || childSpace == null || ownerSpace.getKey() == null || childSpace.getKey() == null || ownerSpace.getKey().equals(childSpace.getKey());
        if (!ownerInSameSpace) {
            throw new IllegalArgumentException("Can't add an owner from another space.");
        }
        ContentEntityObject nextContainer = CustomContentEntityObject.getNextContainer(owner);
        if (nextContainer != null) {
            this.checkContainerHierarchyValid(nextContainer);
        }
    }

    private static Space getOwnerSpace(ContentEntityObject owner) {
        if (owner instanceof Spaced) {
            return ((Spaced)((Object)owner)).getSpace();
        }
        return null;
    }

    private static ContentEntityObject getNextContainer(ContentEntityObject container) {
        if (container instanceof Contained) {
            return ((Contained)((Object)container)).getContainer();
        }
        return null;
    }

    @Override
    public ContentType getContentTypeObject() {
        return ContentType.valueOf((String)this.pluginModuleKey);
    }

    @Override
    public ContentId getContentId() {
        return ContentId.of((ContentType)this.getContentTypeObject(), (long)this.getId());
    }

    @Override
    public boolean shouldConvertToContent() {
        return this.adapter.shouldConvertToContent(this);
    }

    @Override
    public VersionChildOwnerPolicy getVersionChildPolicy(ContentType contentType) {
        return this.adapter.getVersionChildPolicy(contentType);
    }

    @Override
    public String getBodyAsString() {
        return this.getDefaultBodyType().equals(BodyType.UNKNOWN) ? this.getBodyContent(this.getBodyContent().getBodyType()).getBody() : this.getBodyContent(this.getDefaultBodyType()).getBody();
    }
}

