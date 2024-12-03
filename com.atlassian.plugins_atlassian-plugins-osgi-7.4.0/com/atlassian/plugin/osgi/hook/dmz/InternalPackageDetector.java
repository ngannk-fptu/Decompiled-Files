/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.wiring.BundleCapability
 */
package com.atlassian.plugin.osgi.hook.dmz;

import com.atlassian.plugin.osgi.hook.dmz.PackageMatcher;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import org.osgi.framework.wiring.BundleCapability;

public class InternalPackageDetector {
    static final String ATTR_WIRING_PACKAGE = "osgi.wiring.package";
    private final Set<String> osgiPublicPackages;
    private final Set<String> osgiPublicPackagesExcludes;
    private final Set<String> osgiDeprecatedPackages;

    InternalPackageDetector(Set<String> publicPackages, Set<String> publicPackagesExcludes, Set<String> deprecatedPackages) {
        this.osgiPublicPackages = publicPackages;
        this.osgiPublicPackagesExcludes = publicPackagesExcludes;
        this.osgiDeprecatedPackages = deprecatedPackages;
    }

    public InternalPackageDetector(Set<String> publicPackages, Set<String> publicPackagesExcludes) {
        this(publicPackages, publicPackagesExcludes, Collections.emptySet());
    }

    private Optional<String> getWiringPackage(BundleCapability capability) {
        return Optional.ofNullable(capability.getAttributes().get(ATTR_WIRING_PACKAGE)).filter(String.class::isInstance).map(String.class::cast);
    }

    public boolean isInternalPackage(BundleCapability capability) {
        return this.getWiringPackage(capability).map(wiringPackage -> {
            boolean isPublic = this.osgiPublicPackages.stream().anyMatch(pattern -> new PackageMatcher((String)pattern, (String)wiringPackage).match());
            boolean isNotExcluded = this.osgiPublicPackagesExcludes.stream().noneMatch(pattern -> new PackageMatcher((String)pattern, (String)wiringPackage).match());
            boolean publicPackage = isPublic && isNotExcluded;
            return !publicPackage;
        }).orElse(false);
    }

    public boolean isDeprecatedPackage(BundleCapability capability) {
        return this.getWiringPackage(capability).map(wiringPackage -> this.osgiDeprecatedPackages.stream().anyMatch(pattern -> new PackageMatcher((String)pattern, (String)wiringPackage).match())).orElse(false);
    }
}

