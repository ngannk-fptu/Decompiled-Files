/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.osgi.rest.representations;

import com.atlassian.upm.osgi.Bundle;
import com.atlassian.upm.osgi.Service;
import com.atlassian.upm.osgi.impl.Wrapper;
import com.atlassian.upm.osgi.rest.representations.BundleSummaryRepresentation;
import com.atlassian.upm.osgi.rest.representations.ServiceSummaryRepresentation;
import com.atlassian.upm.rest.UpmUriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public final class ServiceRepresentation
extends ServiceSummaryRepresentation {
    @JsonProperty
    private final BundleSummaryRepresentation bundle;
    @JsonProperty
    private final Collection<BundleSummaryRepresentation> usingBundles;
    @JsonProperty
    private final Collection<String> objectClasses;
    @JsonProperty
    @Nullable
    private final String description;
    @JsonProperty
    private final Collection<String> pid;
    @JsonProperty
    private final int ranking;
    @JsonProperty
    @Nullable
    private final String vendor;

    @JsonCreator
    ServiceRepresentation(@JsonProperty(value="bundle") BundleSummaryRepresentation bundle, @JsonProperty(value="usingBundles") Collection<BundleSummaryRepresentation> usingBundles, @JsonProperty(value="objectClasses") Collection<String> objectClasses, @JsonProperty(value="description") @Nullable String description, @JsonProperty(value="id") long id, @JsonProperty(value="pid") Collection<String> pid, @JsonProperty(value="ranking") int ranking, @JsonProperty(value="vendor") @Nullable String vendor, @JsonProperty(value="links") Map<String, URI> links) {
        super(id, links);
        this.bundle = Objects.requireNonNull(bundle, "bundle");
        this.usingBundles = Collections.unmodifiableList(new ArrayList<BundleSummaryRepresentation>(usingBundles));
        this.objectClasses = Collections.unmodifiableList(new ArrayList<String>(objectClasses));
        this.description = description;
        this.pid = Collections.unmodifiableList(new ArrayList<String>(pid));
        this.ranking = ranking;
        this.vendor = vendor;
    }

    public ServiceRepresentation(Service service, UpmUriBuilder uriBuilder) {
        super(service, uriBuilder);
        Wrapper<Bundle, BundleSummaryRepresentation> wrap = BundleSummaryRepresentation.wrapSummary(uriBuilder);
        this.bundle = Objects.requireNonNull(wrap.fromSingleton(service.getBundle()), "bundle");
        this.usingBundles = Objects.requireNonNull(wrap.fromIterable(service.getUsingBundles()), "usingBundles");
        this.objectClasses = Collections.unmodifiableList(StreamSupport.stream(service.getObjectClasses().spliterator(), false).collect(Collectors.toList()));
        this.description = service.getDescription();
        this.pid = Collections.unmodifiableList(StreamSupport.stream(service.getPid().spliterator(), false).collect(Collectors.toList()));
        this.ranking = service.getRanking();
        this.vendor = service.getVendor();
    }

    public BundleSummaryRepresentation getBundle() {
        return this.bundle;
    }

    public Collection<BundleSummaryRepresentation> getUsingBundles() {
        return this.usingBundles;
    }

    public Collection<String> getObjectClasses() {
        return this.objectClasses;
    }

    @Nullable
    public String getDescription() {
        return this.description;
    }

    public Collection<String> getPid() {
        return this.pid;
    }

    public int getRanking() {
        return this.ranking;
    }

    @Nullable
    public String getVendor() {
        return this.vendor;
    }

    public static Wrapper<Service, ServiceRepresentation> wrap(final UpmUriBuilder uriBuilder) {
        return new Wrapper<Service, ServiceRepresentation>("serviceRepresentation"){

            @Override
            public ServiceRepresentation wrap(Service service) {
                return new ServiceRepresentation(service, uriBuilder);
            }
        };
    }
}

