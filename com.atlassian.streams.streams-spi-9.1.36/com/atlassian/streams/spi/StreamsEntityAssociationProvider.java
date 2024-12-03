/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.common.Option
 */
package com.atlassian.streams.spi;

import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.spi.EntityIdentifier;
import java.net.URI;
import java.util.Optional;

public interface StreamsEntityAssociationProvider {
    public Iterable<EntityIdentifier> getEntityIdentifiers(URI var1);

    public Option<URI> getEntityURI(EntityIdentifier var1);

    public Option<String> getFilterKey(EntityIdentifier var1);

    public Option<Boolean> getCurrentUserViewPermission(EntityIdentifier var1);

    public Option<Boolean> getCurrentUserEditPermission(EntityIdentifier var1);

    default public Optional<Boolean> getCurrentUserViewPermissionForTargetlessEntity() {
        return Optional.empty();
    }
}

