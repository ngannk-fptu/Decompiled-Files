/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.healthcheck.checks.eol;

import com.atlassian.troubleshooting.healthcheck.checks.eol.Release;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.Nonnull;

class Product {
    private final String name;
    private final Set<Release> releases;

    public Product(@Nonnull String name) {
        this.name = name;
        this.releases = new TreeSet(Comparator.reverseOrder());
    }

    @Nonnull
    public String getName() {
        return this.name;
    }

    @Nonnull
    public Set<Release> getReleases() {
        return Collections.unmodifiableSet(this.releases);
    }

    public void addRelease(Release release) {
        this.releases.add(release);
    }
}

