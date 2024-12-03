/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.security.ContentPermissionSet;
import java.util.List;

public interface InheritedContentPermissionManager {
    public List<ContentPermissionSet> getInheritedContentPermissionSets(ContentEntityObject var1);

    public List<ContentPermissionSet> getInheritedContentPermissionSetsIncludeEdit(ContentEntityObject var1);
}

