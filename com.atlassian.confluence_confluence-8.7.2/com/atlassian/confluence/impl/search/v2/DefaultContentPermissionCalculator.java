/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  javax.annotation.Nullable
 *  javax.xml.bind.DatatypeConverter
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.lucene.util.IndexableBinaryStringTools
 */
package com.atlassian.confluence.impl.search.v2;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.InheritedContentPermissionManager;
import com.atlassian.confluence.impl.search.v2.lucene.ContentPermissionSearchUtils;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Contained;
import com.atlassian.confluence.search.v2.ContentPermissionCalculator;
import com.atlassian.confluence.security.ContentPermission;
import com.atlassian.confluence.security.ContentPermissionSet;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.util.IndexableBinaryStringTools;

public class DefaultContentPermissionCalculator
implements ContentPermissionCalculator {
    private static final String ESCAPE_CHAR = "#";
    private static final String USER_PREFIX = "u";
    private static final Pattern HASH_PATTERN = Pattern.compile("\\#");
    private static final Pattern PIPE_PATTERN = Pattern.compile("\\|");
    private static final Pattern AMPERSAND_PATTERN = Pattern.compile("\\&");
    private static final String ESCAPED_HASH = "#h";
    private static final String ESCAPED_PIPE = "#p";
    private static final String ESCAPED_AMPERSAND = "#a";
    private static final String GROUP_PREFIX = "g";
    private final InheritedContentPermissionManager inheritedContentPermissionManager;

    public DefaultContentPermissionCalculator(InheritedContentPermissionManager inheritedContentPermissionManager) {
        this.inheritedContentPermissionManager = Objects.requireNonNull(inheritedContentPermissionManager);
    }

    public List<ContentPermissionSet> calculate(@Nullable ContentEntityObject contentEntityObject) {
        AbstractPage page = this.getTopLevelContainer(contentEntityObject);
        if (page == null) {
            return Collections.emptyList();
        }
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.addAll(this.getViewPermissions(page));
        builder.addAll(this.getInheritedPermissions(page));
        builder.addAll(this.getSharedPermissions(page));
        return builder.build();
    }

    @Override
    public String getEncodedContentPermissionSets(Collection<ContentPermissionSet> contentPermissionSets) {
        return contentPermissionSets.stream().filter(Objects::nonNull).map(ContentPermissionSearchUtils::getEncodedPermissionsCollection).map(c -> String.join((CharSequence)"|", c)).collect(Collectors.joining("&"));
    }

    @Override
    public Collection<String> getEncodedPermissionsCollection(ContentPermissionSet contentPermissionSet) {
        return ContentPermissionSearchUtils.getEncodedPermissionsCollection(contentPermissionSet);
    }

    @Override
    public String getEncodedGroupName(String groupname) {
        if (StringUtils.isEmpty((CharSequence)groupname)) {
            return groupname;
        }
        return GROUP_PREFIX + this.escapeEntityName(groupname);
    }

    @Override
    public String getEncodedUserKey(ConfluenceUser user) {
        if (user == null) {
            return "";
        }
        return this.getEncodedUserKey(user.getKey());
    }

    @Override
    public String getEncodedUserKey(UserKey userKey) {
        if (userKey == null) {
            return "";
        }
        String key = this.compressKey(userKey.getStringValue());
        return USER_PREFIX + this.escapeEntityName(key);
    }

    private String compressKey(String key) {
        byte[] bytes = DatatypeConverter.parseHexBinary((String)key);
        int encodedLength = IndexableBinaryStringTools.getEncodedLength((byte[])bytes, (int)0, (int)bytes.length);
        char[] outputArray = new char[encodedLength];
        IndexableBinaryStringTools.encode((byte[])bytes, (int)0, (int)bytes.length, (char[])outputArray, (int)0, (int)encodedLength);
        return new String(outputArray);
    }

    private String escapeEntityName(String entityName) {
        entityName = HASH_PATTERN.matcher(entityName).replaceAll(ESCAPED_HASH);
        entityName = PIPE_PATTERN.matcher(entityName).replaceAll(ESCAPED_PIPE);
        entityName = AMPERSAND_PATTERN.matcher(entityName).replaceAll(ESCAPED_AMPERSAND);
        return entityName;
    }

    private AbstractPage getTopLevelContainer(ContentEntityObject contentEntity) {
        if (contentEntity instanceof AbstractPage) {
            return (AbstractPage)contentEntity;
        }
        if (!(contentEntity instanceof Contained)) {
            return null;
        }
        Contained container = (Contained)((Object)contentEntity);
        return this.getTopLevelContainer((ContentEntityObject)container.getContainer());
    }

    private Collection<ContentPermissionSet> getViewPermissions(ContentEntityObject contentEntity) {
        ContentPermissionSet contentPermissionSet = contentEntity.getContentPermissionSet("View");
        return contentPermissionSet != null ? Collections.singletonList(contentPermissionSet) : Collections.emptyList();
    }

    private Collection<ContentPermissionSet> getInheritedPermissions(ContentEntityObject contentEntity) {
        ContentEntityObject latestVersion = (ContentEntityObject)contentEntity.getLatestVersion();
        return this.inheritedContentPermissionManager.getInheritedContentPermissionSets(latestVersion);
    }

    private Collection<ContentPermissionSet> getSharedPermissions(ContentEntityObject contentEntity) {
        if (!contentEntity.isDraft() || contentEntity.getCreator() == null) {
            return Collections.emptyList();
        }
        ContentPermissionSet permissionSet = contentEntity.getContentPermissionSet("Share");
        if (permissionSet == null) {
            permissionSet = new ContentPermissionSet("Share", contentEntity);
        }
        permissionSet.addContentPermission(ContentPermission.createUserPermission("Share", contentEntity.getCreator()));
        return Collections.singletonList(permissionSet);
    }
}

