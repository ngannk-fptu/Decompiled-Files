/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.plugins.index.api.mapping.NestedStringFieldMapping
 *  com.atlassian.confluence.search.v2.AtlassianDocument
 *  com.atlassian.confluence.search.v2.ContentPermissionCalculator
 *  com.atlassian.confluence.search.v2.SearchFieldMappings
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.Spaced
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.sal.api.user.UserKey
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.edgeindex;

import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.edgeindex.EdgeDocumentFactory;
import com.atlassian.confluence.plugins.edgeindex.EdgeIndexFieldMappings;
import com.atlassian.confluence.plugins.index.api.mapping.NestedStringFieldMapping;
import com.atlassian.confluence.search.v2.AtlassianDocument;
import com.atlassian.confluence.search.v2.ContentPermissionCalculator;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.sal.api.user.UserKey;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class DefaultEdgeDocumentFactory
implements EdgeDocumentFactory {
    private final ContentPermissionCalculator contentPermissionCalculator;

    public DefaultEdgeDocumentFactory(ContentPermissionCalculator contentPermissionCalculator) {
        this.contentPermissionCalculator = contentPermissionCalculator;
    }

    @Override
    public AtlassianDocument buildDocument(String edgeId, UserKey userKey, ContentEntityObject target, Date date, String edgeTypeKey) {
        ConfluenceUser targetAuthor;
        AtlassianDocument document = new AtlassianDocument();
        document.addField(EdgeIndexFieldMappings.EDGE_USERKEY.createField(userKey == null ? "" : (String)StringUtils.defaultIfBlank((CharSequence)userKey.getStringValue(), (CharSequence)"")));
        document.addField(EdgeIndexFieldMappings.EDGE_TARGET_ID.createField(String.valueOf(this.getTargetId(target))));
        long dateInSeconds = date.getTime() / 1000L;
        document.addField(EdgeIndexFieldMappings.EDGE_DATE_FIELD.createField(dateInSeconds));
        document.addField(EdgeIndexFieldMappings.EDGE_TYPE.createField(edgeTypeKey));
        if (edgeId != null) {
            document.addField(EdgeIndexFieldMappings.EDGE_ID.createField(edgeId));
        }
        document.addField(EdgeIndexFieldMappings.EDGE_TARGET_AUTHOR.createField((targetAuthor = this.getTargetAuthor(target)) == null ? "" : targetAuthor.getKey().getStringValue()));
        document.addField(EdgeIndexFieldMappings.EDGE_TARGET_TYPE.createField(this.getTargetType(target)));
        String spaceKey = this.getSpaceKey(target);
        if (StringUtils.isNotBlank((CharSequence)spaceKey)) {
            document.addField(SearchFieldMappings.SPACE_KEY.createField(this.getSpaceKey(target)));
        } else {
            document.addField(SearchFieldMappings.IN_SPACE.createField(false));
        }
        Collection permissions = this.contentPermissionCalculator.calculate(target);
        if (!permissions.isEmpty()) {
            document.addFields((Collection)permissions.stream().map(arg_0 -> ((ContentPermissionCalculator)this.contentPermissionCalculator).getEncodedPermissionsCollection(arg_0)).map(arg_0 -> ((NestedStringFieldMapping)SearchFieldMappings.CONTENT_PERMISSION_SETS).createField(arg_0)).collect(Collectors.toList()));
        }
        return document;
    }

    private String getSpaceKey(Object target) {
        if (!(target instanceof Spaced)) {
            return null;
        }
        Space space = ((Spaced)target).getSpace();
        return space != null ? space.getKey() : null;
    }

    private long getTargetId(Object target) {
        if (target instanceof ContentEntityObject) {
            return ((ContentEntityObject)target).getId();
        }
        throw new UnsupportedOperationException("this target object is not supported: " + target);
    }

    private ConfluenceUser getTargetAuthor(Object target) {
        if (target instanceof ContentEntityObject) {
            return ((ContentEntityObject)target).getCreator();
        }
        return null;
    }

    private String getTargetType(Object target) {
        if (target instanceof CustomContentEntityObject) {
            return ((CustomContentEntityObject)target).getPluginModuleKey();
        }
        if (target instanceof ContentEntityObject) {
            return ((ContentEntityObject)target).getType();
        }
        throw new UnsupportedOperationException("this target object is not supported: " + target);
    }
}

