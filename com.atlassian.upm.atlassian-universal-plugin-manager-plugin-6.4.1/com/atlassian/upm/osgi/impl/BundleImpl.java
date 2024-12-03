/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Suppliers
 *  javax.annotation.Nullable
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.ServiceReference
 */
package com.atlassian.upm.osgi.impl;

import com.atlassian.upm.Functions;
import com.atlassian.upm.osgi.Bundle;
import com.atlassian.upm.osgi.Package;
import com.atlassian.upm.osgi.PackageAccessor;
import com.atlassian.upm.osgi.Service;
import com.atlassian.upm.osgi.Version;
import com.atlassian.upm.osgi.VersionRange;
import com.atlassian.upm.osgi.impl.ServiceImpl;
import com.atlassian.upm.osgi.impl.Versions;
import com.atlassian.upm.osgi.impl.Wrapper;
import com.atlassian.upm.osgi.impl.Wrapper2;
import com.google.common.base.Suppliers;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.sling.commons.osgi.ManifestHeader;
import org.osgi.framework.ServiceReference;

public final class BundleImpl
implements Bundle {
    private final org.osgi.framework.Bundle bundle;
    private final PackageAccessor packageAccessor;
    private final Wrapper2<Function<Bundle.HeaderClause, Package>, ManifestHeader.Entry, Bundle.HeaderClause> wrapHeaderClause;
    private final Wrapper2<String, String, Iterable<Bundle.HeaderClause>> parseHeader;
    private static final Map<Integer, Bundle.State> states = new HashMap<Integer, Bundle.State>();
    private final Supplier<Map<String, String>> unparsedHeaders;
    private final Supplier<Map<String, Iterable<Bundle.HeaderClause>>> parsedHeaders;
    private static final Predicate<String> parseable;
    private static final Predicate<Map.Entry> parseableEntry;
    private final Functions.Function2<String, Bundle.HeaderClause, Package> getPackageFn;

    BundleImpl(org.osgi.framework.Bundle bundle, PackageAccessor packageAccessor) {
        this.bundle = bundle;
        this.packageAccessor = packageAccessor;
        this.wrapHeaderClause = new Wrapper2<Function<Bundle.HeaderClause, Package>, ManifestHeader.Entry, Bundle.HeaderClause>(String.format("bundle-%d.headerClause", this.getId())){

            @Override
            protected Bundle.HeaderClause wrap(Function<Bundle.HeaderClause, Package> getPackageFn, ManifestHeader.Entry headerEntry) {
                return new HeaderClauseImpl(getPackageFn, headerEntry);
            }
        };
        this.parseHeader = new Wrapper2<String, String, Iterable<Bundle.HeaderClause>>(String.format("bundle-%d.header", this.getId())){

            @Override
            protected Iterable<Bundle.HeaderClause> wrap(@Nullable String headerName, @Nullable String headerEntries) {
                return BundleImpl.this.wrapHeaderClause.fromArray(Functions.curry(BundleImpl.this.getPackageFn).apply(headerName), ManifestHeader.parse(headerEntries).getEntries());
            }
        };
        this.unparsedHeaders = Suppliers.memoize(() -> this.getHeaders().entrySet().stream().filter(parseableEntry.negate()).collect(Collectors.toMap(e -> (String)e.getKey(), e -> (String)e.getValue())));
        this.parsedHeaders = Suppliers.memoize(() -> this.parseHeader.fromSingletonValuedMap(this.getHeaders().entrySet().stream().filter(parseableEntry).collect(Collectors.toMap(e -> (String)e.getKey(), e -> (String)e.getValue()))));
        this.getPackageFn = (headerName, headerClause) -> {
            if ("Import-Package".equals(headerName) || "DynamicImport-Package".equals(headerName)) {
                String versionRange = headerClause.getParameters().get("version");
                return packageAccessor.getImportedPackage(this.getId(), headerClause.getPath(), VersionRange.fromString(versionRange == null ? "0" : versionRange));
            }
            if ("Export-Package".equals(headerName)) {
                String version = headerClause.getParameters().get("version");
                return packageAccessor.getExportedPackage(this.getId(), headerClause.getPath(), Versions.fromString(version == null ? "0" : version));
            }
            return null;
        };
    }

    @Override
    public Bundle.State getState() {
        return Objects.requireNonNull(states.get(this.bundle.getState()), "state");
    }

    @Override
    public Map<String, String> getUnparsedHeaders() {
        return this.unparsedHeaders.get();
    }

    @Override
    public Map<String, Iterable<Bundle.HeaderClause>> getParsedHeaders() {
        return this.parsedHeaders.get();
    }

    @Override
    public long getId() {
        return this.bundle.getBundleId();
    }

    @Override
    @Nullable
    public URI getLocation() {
        try {
            return new URI(this.bundle.getLocation());
        }
        catch (URISyntaxException e) {
            return null;
        }
    }

    @Override
    public Iterable<Service> getRegisteredServices() {
        return ServiceImpl.wrap(this.packageAccessor).fromArray((ServiceReference[])this.bundle.getRegisteredServices());
    }

    @Override
    public Iterable<Service> getServicesInUse() {
        return ServiceImpl.wrap(this.packageAccessor).fromArray((ServiceReference[])this.bundle.getServicesInUse());
    }

    @Override
    public String getSymbolicName() {
        return this.bundle.getSymbolicName();
    }

    @Override
    @Nullable
    public String getName() {
        return this.getUnparsedHeaders().get("Bundle-Name");
    }

    @Override
    public Version getVersion() {
        return Versions.wrap.fromSingleton(this.bundle.getVersion());
    }

    static Wrapper<org.osgi.framework.Bundle, Bundle> wrap(final PackageAccessor packageAccessor) {
        return new Wrapper<org.osgi.framework.Bundle, Bundle>("bundle"){

            @Override
            protected Bundle wrap(org.osgi.framework.Bundle bundle) {
                return new BundleImpl(bundle, packageAccessor);
            }
        };
    }

    private Map<String, String> getHeaders() {
        Dictionary headers = this.bundle.getHeaders();
        HashMap<String, String> builder = new HashMap<String, String>();
        Enumeration keys = headers.keys();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            String value = (String)headers.get(key);
            builder.put(key, value);
        }
        return Collections.unmodifiableMap(builder);
    }

    static {
        states.put(1, Bundle.State.UNINSTALLED);
        states.put(2, Bundle.State.INSTALLED);
        states.put(4, Bundle.State.RESOLVED);
        states.put(8, Bundle.State.STARTING);
        states.put(16, Bundle.State.STOPPING);
        states.put(32, Bundle.State.ACTIVE);
        parseable = p -> Arrays.asList("Bundle-ClassPath", "Bundle-NativeCode", "Bundle-RequiredExecutionEnvironment", "DynamicImport-Package", "Export-Package", "Fragment-Host", "Ignore-Package", "Import-Package", "Private-Package", "Require-Bundle").contains(p);
        parseableEntry = p -> Arrays.asList("Bundle-ClassPath", "Bundle-NativeCode", "Bundle-RequiredExecutionEnvironment", "DynamicImport-Package", "Export-Package", "Fragment-Host", "Ignore-Package", "Import-Package", "Private-Package", "Require-Bundle").contains(p.getKey());
    }

    public static class HeaderClauseImpl
    implements Bundle.HeaderClause {
        private final String path;
        private final Map<String, String> parameters;
        private final Function<Bundle.HeaderClause, Package> getPackageFn;

        HeaderClauseImpl(Function<Bundle.HeaderClause, Package> getPackageFn, ManifestHeader.Entry entry) {
            HashMap<String, String> propertiesBuilder = new HashMap<String, String>();
            for (ManifestHeader.NameValuePair attribute : entry.getAttributes()) {
                propertiesBuilder.put(attribute.getName(), attribute.getValue());
            }
            for (ManifestHeader.NameValuePair directive : entry.getDirectives()) {
                propertiesBuilder.put(directive.getName(), directive.getValue());
            }
            this.getPackageFn = getPackageFn;
            this.path = Objects.requireNonNull(entry.getValue());
            this.parameters = Collections.unmodifiableMap(propertiesBuilder);
        }

        @Override
        public String getPath() {
            return this.path;
        }

        @Override
        public Map<String, String> getParameters() {
            return this.parameters;
        }

        @Override
        public Package getReferencedPackage() {
            return this.getPackageFn.apply(this);
        }
    }
}

