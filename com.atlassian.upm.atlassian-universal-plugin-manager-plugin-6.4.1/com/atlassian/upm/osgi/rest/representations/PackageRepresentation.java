/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.osgi.rest.representations;

import com.atlassian.upm.osgi.Package;
import com.atlassian.upm.osgi.impl.Wrapper;
import com.atlassian.upm.osgi.rest.representations.BundleSummaryRepresentation;
import com.atlassian.upm.osgi.rest.representations.PackageSummaryRepresentation;
import com.atlassian.upm.rest.UpmUriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public final class PackageRepresentation
extends PackageSummaryRepresentation {
    @JsonProperty
    private final BundleSummaryRepresentation exportingBundle;
    @JsonProperty
    private final Collection<BundleSummaryRepresentation> importingBundles;

    @JsonCreator
    PackageRepresentation(@JsonProperty(value="name") String name, @JsonProperty(value="exportingBundle") BundleSummaryRepresentation exportingBundle, @JsonProperty(value="importingBundles") Collection<BundleSummaryRepresentation> importingBundles, @JsonProperty(value="version") String version, @JsonProperty(value="links") Map<String, URI> links) {
        super(name, version, links);
        this.exportingBundle = Objects.requireNonNull(exportingBundle, "exportingBundle");
        this.importingBundles = Collections.unmodifiableList(new ArrayList<BundleSummaryRepresentation>(importingBundles));
    }

    public PackageRepresentation(Package pkg, UpmUriBuilder uriBuilder) {
        super(pkg, uriBuilder);
        this.exportingBundle = BundleSummaryRepresentation.wrapSummary(uriBuilder).fromSingleton(pkg.getExportingBundle());
        this.importingBundles = BundleSummaryRepresentation.wrapSummary(uriBuilder).fromIterable(pkg.getImportingBundles());
    }

    public BundleSummaryRepresentation getExportingBundle() {
        return this.exportingBundle;
    }

    public Collection<BundleSummaryRepresentation> getImportingBundles() {
        return this.importingBundles;
    }

    public static Wrapper<Package, PackageRepresentation> wrap(final UpmUriBuilder uriBuilder) {
        return new Wrapper<Package, PackageRepresentation>("packageRepresentation"){

            @Override
            public PackageRepresentation wrap(Package pkg) {
                return new PackageRepresentation(pkg, uriBuilder);
            }
        };
    }
}

