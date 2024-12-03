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
import com.atlassian.upm.osgi.impl.Wrapper;
import com.atlassian.upm.osgi.impl.Wrapper2;
import com.atlassian.upm.osgi.rest.representations.BundleSummaryRepresentation;
import com.atlassian.upm.osgi.rest.representations.PackageRepresentation;
import com.atlassian.upm.osgi.rest.representations.ServiceRepresentation;
import com.atlassian.upm.rest.UpmUriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public final class BundleRepresentation
extends BundleSummaryRepresentation {
    @JsonProperty
    private final Map<String, String> unparsedHeaders;
    @JsonProperty
    private final Map<String, Collection<HeaderClauseRepresentation>> parsedHeaders;
    @JsonProperty
    private final Collection<ServiceRepresentation> registeredServices;
    @JsonProperty
    private final Collection<ServiceRepresentation> servicesInUse;

    @JsonCreator
    BundleRepresentation(@JsonProperty(value="state") Bundle.State state, @JsonProperty(value="unparsedHeaders") Map<String, String> unparsedHeaders, @JsonProperty(value="parsedHeaders") Map<String, Collection<HeaderClauseRepresentation>> parsedHeaders, @JsonProperty(value="id") long id, @JsonProperty(value="location") @Nullable URI location, @JsonProperty(value="registeredServices") Collection<ServiceRepresentation> registeredServices, @JsonProperty(value="servicesInUse") Collection<ServiceRepresentation> servicesInUse, @JsonProperty(value="symbolicName") String symbolicName, @JsonProperty(value="name") @Nullable String name, @JsonProperty(value="version") String version, @JsonProperty(value="links") Map<String, URI> links) {
        super(state, id, location, symbolicName, name, version, links);
        this.unparsedHeaders = Collections.unmodifiableMap(new HashMap<String, String>(unparsedHeaders));
        this.parsedHeaders = Collections.unmodifiableMap(new HashMap<String, Collection<HeaderClauseRepresentation>>(parsedHeaders));
        this.registeredServices = Collections.unmodifiableList(new ArrayList<ServiceRepresentation>(registeredServices));
        this.servicesInUse = Collections.unmodifiableList(new ArrayList<ServiceRepresentation>(servicesInUse));
    }

    public BundleRepresentation(Bundle bundle, UpmUriBuilder uriBuilder) {
        super(bundle, uriBuilder);
        this.unparsedHeaders = Collections.unmodifiableMap(new HashMap<String, String>(bundle.getUnparsedHeaders()));
        this.parsedHeaders = HeaderClauseRepresentation.wrap(uriBuilder).fromIterableValuedMap(bundle.getParsedHeaders());
        this.registeredServices = ServiceRepresentation.wrap(uriBuilder).fromIterable(bundle.getRegisteredServices());
        this.servicesInUse = ServiceRepresentation.wrap(uriBuilder).fromIterable(bundle.getServicesInUse());
    }

    public Map<String, String> getUnparsedHeaders() {
        return this.unparsedHeaders;
    }

    public Map<String, Collection<HeaderClauseRepresentation>> getParsedHeaders() {
        return this.parsedHeaders;
    }

    public Collection<ServiceRepresentation> getRegisteredServices() {
        return this.registeredServices;
    }

    public Collection<ServiceRepresentation> getServicesInUse() {
        return this.servicesInUse;
    }

    public static Wrapper<Bundle, BundleRepresentation> wrap(final UpmUriBuilder uriBuilder) {
        return new Wrapper<Bundle, BundleRepresentation>("bundleRepresentation"){

            @Override
            public BundleRepresentation wrap(Bundle bundle) {
                return new BundleRepresentation(bundle, uriBuilder);
            }
        };
    }

    public static final class HeaderClauseRepresentation {
        @JsonProperty
        private final String path;
        @JsonProperty
        private final Map<String, String> parameters;
        @JsonProperty
        @Nullable
        private final PackageRepresentation referencedPackage;

        @JsonCreator
        HeaderClauseRepresentation(@JsonProperty(value="path") String path, @JsonProperty(value="parameters") Map<String, String> parameters, @JsonProperty(value="packages") @Nullable PackageRepresentation referencedPackage) {
            this.path = Objects.requireNonNull(path, "path");
            this.parameters = Collections.unmodifiableMap(new HashMap<String, String>(parameters));
            this.referencedPackage = referencedPackage;
        }

        HeaderClauseRepresentation(Bundle.HeaderClause headerClause, UpmUriBuilder uriBuilder) {
            this.path = Objects.requireNonNull(headerClause.getPath());
            this.parameters = Collections.unmodifiableMap(new HashMap<String, String>(headerClause.getParameters()));
            this.referencedPackage = PackageRepresentation.wrap(uriBuilder).fromSingleton(headerClause.getReferencedPackage());
        }

        public String getPath() {
            return this.path;
        }

        public Map<String, String> getParameters() {
            return this.parameters;
        }

        @Nullable
        public PackageRepresentation getReferencedPackage() {
            return this.referencedPackage;
        }

        static Wrapper2<String, Bundle.HeaderClause, HeaderClauseRepresentation> wrap(final UpmUriBuilder uriBuilder) {
            return new Wrapper2<String, Bundle.HeaderClause, HeaderClauseRepresentation>("headerClauseRepresentation"){

                @Override
                protected HeaderClauseRepresentation wrap(String headerName, Bundle.HeaderClause headerClause) {
                    return new HeaderClauseRepresentation(headerClause, uriBuilder);
                }
            };
        }
    }
}

