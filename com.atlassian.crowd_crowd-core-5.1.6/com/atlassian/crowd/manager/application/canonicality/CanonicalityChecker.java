/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.SetMultimap
 */
package com.atlassian.crowd.manager.application.canonicality;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.search.EntityDescriptor;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import java.util.List;
import java.util.Set;

public interface CanonicalityChecker {
    public void removeNonCanonicalEntities(Multimap<Long, String> var1, EntityDescriptor var2);

    public SetMultimap<Long, String> groupByCanonicalId(Set<String> var1, EntityDescriptor var2);

    public List<Directory> getDirectories();
}

