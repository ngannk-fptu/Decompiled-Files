/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  io.atlassian.fugue.Option
 *  org.joda.time.LocalDate
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.api.ApplicationKey;
import com.atlassian.marketplace.client.api.HostingType;
import com.atlassian.marketplace.client.model.ArtifactInfo;
import com.atlassian.marketplace.client.model.Links;
import com.atlassian.marketplace.client.model.PaymentModel;
import com.atlassian.marketplace.client.model.VersionCompatibility;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import io.atlassian.fugue.Option;
import java.net.URI;
import java.util.Iterator;
import org.joda.time.LocalDate;

public final class ProductVersion {
    Links _links;
    Embedded _embedded;
    String name;
    int buildNumber;
    PaymentModel paymentModel;
    LocalDate releaseDate;
    ImmutableList<VersionCompatibility> compatibilities;

    public Option<URI> getArtifactUri() {
        Iterator iterator = this._embedded.artifact.iterator();
        if (iterator.hasNext()) {
            ArtifactInfo a = (ArtifactInfo)iterator.next();
            return Option.some((Object)a.getBinaryUri());
        }
        return Option.none();
    }

    public Option<URI> getLearnMoreUri() {
        return this._links.getUri("view");
    }

    public Option<URI> getReleaseNotesUri() {
        return this._links.getUri("releaseNotes");
    }

    public String getName() {
        return this.name;
    }

    public int getBuildNumber() {
        return this.buildNumber;
    }

    public PaymentModel getPaymentModel() {
        return this.paymentModel;
    }

    public LocalDate getReleaseDate() {
        return this.releaseDate;
    }

    public Iterable<VersionCompatibility> getCompatibilities() {
        return this.compatibilities;
    }

    public boolean isCompatibleWith(final ApplicationKey application, final HostingType hosting, final int buildNumber) {
        return Iterables.any(this.compatibilities, (Predicate)new Predicate<VersionCompatibility>(){

            public boolean apply(VersionCompatibility vc) {
                return vc.isCompatibleWith((Predicate<ApplicationKey>)Predicates.equalTo((Object)application), hosting, buildNumber);
            }
        });
    }

    static final class Embedded {
        Option<ArtifactInfo> artifact;

        Embedded() {
        }
    }
}

