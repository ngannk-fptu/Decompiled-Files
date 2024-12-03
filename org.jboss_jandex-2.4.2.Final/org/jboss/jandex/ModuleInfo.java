/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;

public final class ModuleInfo {
    private static final int OPEN = 32;
    private final ClassInfo moduleInfoClass;
    private final DotName name;
    private final short flags;
    private final String version;
    private DotName mainClass;
    private List<RequiredModuleInfo> requires;
    private List<ExportedPackageInfo> exports;
    private List<OpenedPackageInfo> opens;
    private List<DotName> uses;
    private List<ProvidedServiceInfo> provides;
    private List<DotName> packages;

    ModuleInfo(ClassInfo moduleInfoClass, DotName name, short flags, String version) {
        this.moduleInfoClass = moduleInfoClass;
        this.name = name;
        this.flags = flags;
        this.version = version;
        this.packages = Collections.emptyList();
        moduleInfoClass.setModule(this);
    }

    public String toString() {
        return this.name.toString();
    }

    public ClassInfo moduleInfoClass() {
        return this.moduleInfoClass;
    }

    public DotName name() {
        return this.name;
    }

    public short flags() {
        return this.flags;
    }

    public boolean isOpen() {
        return (this.flags & 0x20) != 0;
    }

    public String version() {
        return this.version;
    }

    public DotName mainClass() {
        return this.mainClass;
    }

    List<RequiredModuleInfo> requiresList() {
        return this.requires;
    }

    public List<RequiredModuleInfo> requires() {
        return Collections.unmodifiableList(this.requires);
    }

    List<ExportedPackageInfo> exportsList() {
        return this.exports;
    }

    public List<ExportedPackageInfo> exports() {
        return Collections.unmodifiableList(this.exports);
    }

    List<OpenedPackageInfo> opensList() {
        return this.opens;
    }

    public List<OpenedPackageInfo> opens() {
        return Collections.unmodifiableList(this.opens);
    }

    List<DotName> usesList() {
        return this.uses;
    }

    public List<DotName> uses() {
        return Collections.unmodifiableList(this.uses);
    }

    List<ProvidedServiceInfo> providesList() {
        return this.provides;
    }

    public List<ProvidedServiceInfo> provides() {
        return Collections.unmodifiableList(this.provides);
    }

    List<DotName> packagesList() {
        return this.packages;
    }

    public List<DotName> packages() {
        return Collections.unmodifiableList(this.packages);
    }

    public final AnnotationInstance annotation(DotName name) {
        return this.moduleInfoClass.classAnnotation(name);
    }

    public final Collection<AnnotationInstance> annotations() {
        return this.moduleInfoClass.classAnnotations();
    }

    public final List<AnnotationInstance> annotationsWithRepeatable(DotName name, IndexView index) {
        return this.moduleInfoClass.classAnnotationsWithRepeatable(name, index);
    }

    void setMainClass(DotName mainClass) {
        this.mainClass = mainClass;
    }

    void setRequires(List<RequiredModuleInfo> requires) {
        this.requires = requires;
    }

    void setExports(List<ExportedPackageInfo> exports) {
        this.exports = exports;
    }

    void setOpens(List<OpenedPackageInfo> opens) {
        this.opens = opens;
    }

    void setUses(List<DotName> uses) {
        this.uses = uses;
    }

    void setProvides(List<ProvidedServiceInfo> provides) {
        this.provides = provides;
    }

    void setPackages(List<DotName> packages) {
        this.packages = packages;
    }

    public static final class ProvidedServiceInfo {
        private final DotName service;
        private final List<DotName> providers;

        ProvidedServiceInfo(DotName name, List<DotName> providers) {
            this.service = name;
            this.providers = providers;
        }

        public String toString() {
            StringBuilder result = new StringBuilder("provides ");
            result.append(this.service.toString());
            if (!this.providers.isEmpty()) {
                result.append(" with ");
                int m = this.providers.size();
                for (int i = 0; i < m; ++i) {
                    if (i > 0) {
                        result.append(", ");
                    }
                    result.append(this.providers.get(i));
                }
            }
            return result.toString();
        }

        public DotName service() {
            return this.service;
        }

        List<DotName> providersList() {
            return this.providers;
        }

        public List<DotName> providers() {
            return Collections.unmodifiableList(this.providers);
        }
    }

    public static final class OpenedPackageInfo {
        private final DotName source;
        private final int flags;
        private final List<DotName> targets;

        OpenedPackageInfo(DotName source, int flags, List<DotName> targets) {
            this.source = source;
            this.flags = flags;
            this.targets = targets;
        }

        public String toString() {
            StringBuilder result = new StringBuilder("opens ");
            result.append(this.source.toString());
            if (!this.targets.isEmpty()) {
                result.append(" to ");
                int m = this.targets.size();
                for (int i = 0; i < m; ++i) {
                    if (i > 0) {
                        result.append(", ");
                    }
                    result.append(this.targets.get(i));
                }
            }
            return result.toString();
        }

        public DotName source() {
            return this.source;
        }

        public int flags() {
            return this.flags;
        }

        public boolean isQualified() {
            return !this.targets.isEmpty();
        }

        List<DotName> targetsList() {
            return this.targets;
        }

        public List<DotName> targets() {
            return Collections.unmodifiableList(this.targets);
        }
    }

    public static final class ExportedPackageInfo {
        private final DotName source;
        private final int flags;
        private final List<DotName> targets;

        ExportedPackageInfo(DotName source, int flags, List<DotName> targets) {
            this.source = source;
            this.flags = flags;
            this.targets = targets;
        }

        public String toString() {
            StringBuilder result = new StringBuilder("exports ");
            result.append(this.source.toString());
            if (!this.targets.isEmpty()) {
                result.append(" to ");
                int m = this.targets.size();
                for (int i = 0; i < m; ++i) {
                    if (i > 0) {
                        result.append(", ");
                    }
                    result.append(this.targets.get(i));
                }
            }
            return result.toString();
        }

        public DotName source() {
            return this.source;
        }

        public int flags() {
            return this.flags;
        }

        public boolean isQualified() {
            return !this.targets.isEmpty();
        }

        List<DotName> targetsList() {
            return this.targets;
        }

        public List<DotName> targets() {
            return Collections.unmodifiableList(this.targets);
        }
    }

    public static final class RequiredModuleInfo {
        private static final int TRANSITIVE = 32;
        private static final int STATIC = 64;
        private final DotName name;
        private final int flags;
        private final String version;

        RequiredModuleInfo(DotName name, int flags, String version) {
            this.name = name;
            this.flags = flags;
            this.version = version;
        }

        public String toString() {
            StringBuilder result = new StringBuilder("requires ");
            if (this.isStatic()) {
                result.append("static ");
            }
            if (this.isTransitive()) {
                result.append("transitive ");
            }
            result.append(this.name.toString());
            if (this.version != null) {
                result.append('@');
                result.append(this.version);
            }
            return result.toString();
        }

        public DotName name() {
            return this.name;
        }

        public int flags() {
            return this.flags;
        }

        public String version() {
            return this.version;
        }

        public boolean isStatic() {
            return (this.flags & 0x40) != 0;
        }

        public boolean isTransitive() {
            return (this.flags & 0x20) != 0;
        }
    }
}

