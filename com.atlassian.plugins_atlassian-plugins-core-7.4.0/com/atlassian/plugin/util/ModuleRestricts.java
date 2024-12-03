/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Application
 *  com.atlassian.plugin.InstallationMode
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.StringUtils
 *  org.dom4j.Element
 */
package com.atlassian.plugin.util;

import com.atlassian.plugin.Application;
import com.atlassian.plugin.InstallationMode;
import com.atlassian.plugin.util.VersionRange;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

final class ModuleRestricts {
    final Iterable<ModuleRestrict> restricts;

    private ModuleRestricts() {
        this((Iterable<ModuleRestrict>)ImmutableList.of());
    }

    private ModuleRestricts(Iterable<ModuleRestrict> restricts) {
        this.restricts = ImmutableList.copyOf(restricts);
    }

    static ModuleRestricts parse(Element moduleElement) {
        String applicationKeys = moduleElement.attributeValue("application");
        if (applicationKeys != null) {
            return ModuleRestricts.parseApplicationsFromAttribute(applicationKeys);
        }
        if (!moduleElement.elements("restrict").isEmpty()) {
            List restrict = moduleElement.elements("restrict");
            return ModuleRestricts.parseApplicationsFromRestrictElements(restrict);
        }
        return new ModuleRestricts();
    }

    private static ModuleRestricts parseApplicationsFromRestrictElements(List<Element> restrictElements) {
        return new ModuleRestricts(Iterables.transform(restrictElements, restrictElement -> {
            String application = restrictElement.attributeValue("application");
            Preconditions.checkState((application != null ? 1 : 0) != 0, (Object)"No application defined for 'restrict' element.");
            return new ModuleRestrict(application, ModuleRestricts.parseInstallationMode(restrictElement).orElse(null), ModuleRestricts.parseVersionRange(restrictElement));
        }));
    }

    private static Optional<InstallationMode> parseInstallationMode(Element restrictElement) {
        return InstallationMode.of((String)restrictElement.attributeValue("mode"));
    }

    private static VersionRange parseVersionRange(Element restrictElement) {
        String version = restrictElement.attributeValue("version");
        if (version != null) {
            return VersionRange.parse(version);
        }
        List versionElements = restrictElement.elements("version");
        if (!versionElements.isEmpty()) {
            VersionRange range = VersionRange.empty();
            for (Element versionElement : versionElements) {
                range = range.or(VersionRange.parse(versionElement.getText()));
            }
            return range;
        }
        return VersionRange.all();
    }

    private static ModuleRestricts parseApplicationsFromAttribute(String applicationKeys) {
        Object[] keys = applicationKeys.split("\\s*,[,\\s]*");
        Iterable restricts = Iterables.transform((Iterable)Iterables.filter((Iterable)Lists.newArrayList((Object[])keys), (Predicate)new IsNotBlankPredicate()), ModuleRestrict::new);
        return new ModuleRestricts(restricts);
    }

    public boolean isValidFor(Set<Application> applications, InstallationMode mode) {
        if (Iterables.isEmpty(this.restricts)) {
            return true;
        }
        for (Application application : applications) {
            if (!Iterables.any(this.restricts, (Predicate)new RestrictMatchesApplication(application, mode))) continue;
            return true;
        }
        return false;
    }

    public String toString() {
        return this.restricts.toString();
    }

    private static final class RestrictMatchesApplication
    implements Predicate<ModuleRestrict> {
        private final Application app;
        private final InstallationMode installationMode;

        public RestrictMatchesApplication(Application app, InstallationMode installationMode) {
            this.app = (Application)Preconditions.checkNotNull((Object)app);
            this.installationMode = (InstallationMode)Preconditions.checkNotNull((Object)installationMode);
        }

        public boolean apply(ModuleRestrict restrict) {
            return restrict.application.equals(this.app.getKey()) && this.isInstallModeValid(restrict.mode) && restrict.version.isInRange(this.app.getVersion());
        }

        private boolean isInstallModeValid(InstallationMode mode) {
            return mode == null || mode.equals((Object)this.installationMode);
        }
    }

    private static final class IsNotBlankPredicate
    implements Predicate<String> {
        private IsNotBlankPredicate() {
        }

        public boolean apply(String input) {
            return StringUtils.isNotBlank((CharSequence)input);
        }
    }

    static final class ModuleRestrict {
        final String application;
        final InstallationMode mode;
        final VersionRange version;

        ModuleRestrict(String application) {
            this(application, null);
        }

        ModuleRestrict(String application, InstallationMode mode) {
            this(application, mode, VersionRange.all());
        }

        ModuleRestrict(String application, InstallationMode mode, VersionRange version) {
            this.application = Objects.requireNonNull(application, "application");
            this.mode = mode;
            this.version = Objects.requireNonNull(version);
        }

        public String toString() {
            return MoreObjects.toStringHelper((String)"restrict").add("application", (Object)this.application).add("mode", (Object)this.mode).add("range", (Object)this.version).toString();
        }
    }
}

