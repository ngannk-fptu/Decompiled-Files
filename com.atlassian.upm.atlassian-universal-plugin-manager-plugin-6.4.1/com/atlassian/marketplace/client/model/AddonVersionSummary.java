/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  io.atlassian.fugue.Option
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.model.AddonCategorySummary;
import com.atlassian.marketplace.client.model.AddonVersionBase;
import com.atlassian.marketplace.client.model.ArtifactInfo;
import com.google.common.collect.ImmutableList;
import io.atlassian.fugue.Option;
import java.net.URI;
import java.util.Iterator;

public final class AddonVersionSummary
extends AddonVersionBase {
    Embedded _embedded;

    @Override
    public Option<ArtifactInfo> getArtifactInfo() {
        return this._embedded.artifact;
    }

    @Override
    public Option<URI> getArtifactUri() {
        Iterator iterator = this._embedded.artifact.iterator();
        if (iterator.hasNext()) {
            ArtifactInfo a = (ArtifactInfo)iterator.next();
            return Option.some((Object)a.getBinaryUri());
        }
        return Option.none();
    }

    @Override
    public Iterable<AddonCategorySummary> getFunctionalCategories() {
        return this._embedded.functionalCategories;
    }

    @Override
    public Option<URI> getRemoteDescriptorUri() {
        Iterator iterator = this._embedded.artifact.iterator();
        if (iterator.hasNext()) {
            ArtifactInfo a = (ArtifactInfo)iterator.next();
            return a.getRemoteDescriptorUri();
        }
        return Option.none();
    }

    static final class Embedded {
        Option<ArtifactInfo> artifact;
        ImmutableList<AddonCategorySummary> functionalCategories;

        Embedded() {
        }
    }
}

