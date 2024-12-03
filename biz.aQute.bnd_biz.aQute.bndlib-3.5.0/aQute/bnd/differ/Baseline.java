/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package aQute.bnd.differ;

import aQute.bnd.header.OSGiHeader;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.osgi.Instructions;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Processor;
import aQute.bnd.service.diff.Delta;
import aQute.bnd.service.diff.Diff;
import aQute.bnd.service.diff.Differ;
import aQute.bnd.service.diff.Tree;
import aQute.bnd.service.diff.Type;
import aQute.bnd.version.Version;
import aQute.libg.generics.Create;
import aQute.service.reporter.Reporter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Formatter;
import java.util.Map;
import java.util.Set;
import java.util.jar.Manifest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Baseline {
    private static final Logger logger = LoggerFactory.getLogger(Baseline.class);
    final Differ differ;
    final Reporter bnd;
    final BundleInfo binfo = new BundleInfo();
    Diff diff;
    Set<Info> infos;
    String bsn;
    Version newerVersion;
    Version olderVersion;
    Version suggestedVersion;
    String releaseRepository;

    public Baseline(Reporter bnd, Differ differ) throws IOException {
        this.differ = differ;
        this.bnd = bnd;
    }

    public Set<Info> baseline(Jar newer, Jar older, Instructions packageFilters) throws Exception {
        Tree n = this.differ.tree(newer);
        Parameters nExports = this.getExports(newer);
        Tree o = this.differ.tree(older);
        Parameters oExports = this.getExports(older);
        if (packageFilters == null) {
            packageFilters = new Instructions();
        }
        return this.baseline(n, nExports, o, oExports, packageFilters);
    }

    public Set<Info> baseline(Tree n, Parameters nExports, Tree o, Parameters oExports, Instructions packageFilters) throws Exception {
        this.diff = n.diff(o);
        Diff apiDiff = this.diff.get("<api>");
        this.infos = Create.set();
        this.bsn = this.getBsn(n);
        this.newerVersion = this.getVersion(n);
        this.olderVersion = this.getVersion(o);
        boolean firstRelease = false;
        if (o.get("<manifest>") == null) {
            firstRelease = true;
            if (this.newerVersion.equals(Version.emptyVersion)) {
                this.newerVersion = Version.ONE;
            }
        }
        Delta highestDelta = Delta.UNCHANGED;
        for (Diff diff : apiDiff.getChildren()) {
            Delta content;
            if (diff.getType() != Type.PACKAGE || diff.getName().startsWith("java.") || !packageFilters.matches(diff.getName())) continue;
            final Info info = new Info();
            this.infos.add(info);
            info.reason = this.getRootCauses(diff);
            info.packageDiff = diff;
            info.packageName = diff.getName();
            info.attributes = nExports.get(info.packageName);
            logger.debug("attrs for {} {}", (Object)info.packageName, info.attributes);
            info.newerVersion = this.getVersion(info.attributes);
            info.olderVersion = this.getVersion(oExports.get(info.packageName));
            if (diff.getDelta() == Delta.UNCHANGED) {
                info.suggestedVersion = info.olderVersion;
                if (info.olderVersion.getQualifier() != null) {
                    info.suggestedVersion = this.bump(Delta.MICRO, info.olderVersion, 1, 0);
                    info.warning = info.warning + "Found package version with qualifier. Bumping micro version";
                } else if (!info.newerVersion.equals(info.olderVersion)) {
                    info.warning = info.warning + "No difference but versions are not equal";
                }
            } else if (diff.getDelta() == Delta.REMOVED) {
                info.suggestedVersion = null;
            } else if (diff.getDelta() == Delta.ADDED) {
                info.suggestedVersion = info.newerVersion;
            } else {
                info.suggestedVersion = this.bump(diff.getDelta(), info.olderVersion, 1, 0);
                if (info.newerVersion.compareTo(info.suggestedVersion) < 0) {
                    info.mismatch = true;
                    if (diff.getDelta() == Delta.MAJOR) {
                        Delta tryDelta;
                        info.providers = Create.set();
                        if (info.attributes != null) {
                            info.providers.addAll(Processor.split(info.attributes.get("x-provider-type:")));
                        }
                        if ((tryDelta = diff.getDelta(new Diff.Ignore(){

                            @Override
                            public boolean contains(Diff diff) {
                                if (diff.getType() == Type.INTERFACE && diff.getDelta() == Delta.MAJOR) {
                                    info.providers.add(Descriptors.getShortName(diff.getName()));
                                    return true;
                                }
                                return false;
                            }
                        })) != Delta.MAJOR) {
                            info.suggestedIfProviders = this.bump(tryDelta, info.olderVersion, 1, 0);
                        }
                    }
                }
            }
            switch (diff.getDelta()) {
                case IGNORED: 
                case UNCHANGED: {
                    content = Delta.UNCHANGED;
                    break;
                }
                case ADDED: {
                    content = Delta.MINOR;
                    break;
                }
                case CHANGED: {
                    content = Delta.MICRO;
                    break;
                }
                case MICRO: {
                    content = diff.getDelta();
                    break;
                }
                case MINOR: {
                    content = diff.getDelta();
                    break;
                }
                case MAJOR: {
                    content = diff.getDelta();
                    break;
                }
                default: {
                    content = Delta.MAJOR;
                }
            }
            if (content.compareTo(highestDelta) <= 0) continue;
            highestDelta = content;
        }
        if (firstRelease || !this.bsn.equals(this.getBsn(o))) {
            this.suggestedVersion = this.newerVersion;
        } else {
            this.suggestedVersion = this.bumpBundle(highestDelta, this.olderVersion, 1, 0);
            if (this.suggestedVersion.compareTo(this.newerVersion) < 0) {
                this.suggestedVersion = this.newerVersion;
            }
        }
        this.binfo.bsn = this.bsn;
        this.binfo.suggestedVersion = this.suggestedVersion;
        this.binfo.version = this.binfo.olderVersion = this.olderVersion;
        this.binfo.newerVersion = this.newerVersion;
        if (this.newerVersion.getWithoutQualifier().equals(this.olderVersion.getWithoutQualifier()) && this.getDiff().getDelta() == Delta.UNCHANGED) {
            return this.infos;
        }
        if (this.newerVersion.getWithoutQualifier().compareTo(this.getSuggestedVersion()) < 0) {
            this.binfo.mismatch = true;
            this.binfo.reason = this.getRootCauses(apiDiff);
        }
        return this.infos;
    }

    private String getRootCauses(Diff apiDiff) {
        try (Formatter f = new Formatter();){
            this.getRootCauses(f, apiDiff, "");
            String string = f.toString();
            return string;
        }
    }

    private void getRootCauses(Formatter f, Diff diff, String path) {
        for (Diff diff2 : diff.getChildren()) {
            String cpath = path + "/" + diff2.getName();
            if (diff2.getDelta() == diff.getDelta()) {
                this.getRootCauses(f, diff2, cpath);
                continue;
            }
            if (diff2.getDelta() == Delta.ADDED) {
                f.format("+ %s\n", cpath);
                continue;
            }
            if (diff2.getDelta() != Delta.REMOVED) continue;
            f.format("- %s\n", cpath);
        }
    }

    public Diff getDiff() {
        return this.diff;
    }

    public Set<Info> getPackageInfos() {
        if (this.infos == null) {
            return Collections.emptySet();
        }
        return this.infos;
    }

    public String getBsn() {
        return this.bsn;
    }

    public Version getSuggestedVersion() {
        return this.suggestedVersion;
    }

    public void setSuggestedVersion(Version suggestedVersion) {
        this.suggestedVersion = suggestedVersion;
    }

    public Version getNewerVersion() {
        return this.newerVersion;
    }

    public Version getOlderVersion() {
        return this.olderVersion;
    }

    public String getReleaseRepository() {
        return this.releaseRepository;
    }

    public void setReleaseRepository(String releaseRepository) {
        this.releaseRepository = releaseRepository;
    }

    private Version bump(Delta delta, Version last, int offset, int base) {
        switch (delta) {
            case UNCHANGED: {
                return last;
            }
            case MINOR: {
                return new Version(last.getMajor(), last.getMinor() + offset, base);
            }
            case MAJOR: {
                return new Version(last.getMajor() + 1, base, base);
            }
            case ADDED: {
                return last;
            }
        }
        return new Version(last.getMajor(), last.getMinor(), last.getMicro() + offset);
    }

    private Version getVersion(Map<String, String> map) {
        if (map == null) {
            return Version.LOWEST;
        }
        return Version.parseVersion(map.get("version"));
    }

    private Parameters getExports(Jar jar) throws Exception {
        Manifest m = jar.getManifest();
        if (m == null) {
            return new Parameters();
        }
        return OSGiHeader.parseHeader(m.getMainAttributes().getValue("Export-Package"));
    }

    private Version getVersion(Tree top) {
        Tree manifest = top.get("<manifest>");
        if (manifest == null) {
            return Version.emptyVersion;
        }
        for (Tree tree : manifest.getChildren()) {
            if (!tree.getName().startsWith("Bundle-Version")) continue;
            return Version.parseVersion(tree.getName().substring(15));
        }
        return Version.emptyVersion;
    }

    private String getBsn(Tree top) {
        Tree manifest = top.get("<manifest>");
        if (manifest == null) {
            return "";
        }
        for (Tree tree : manifest.getChildren()) {
            if (!tree.getName().startsWith("Bundle-SymbolicName") || tree.getChildren().length <= 0) continue;
            return tree.getChildren()[0].getName();
        }
        return "";
    }

    private Version bumpBundle(Delta delta, Version last, int offset, int base) {
        switch (delta) {
            case MINOR: {
                return new Version(last.getMajor(), last.getMinor() + offset, base);
            }
            case MAJOR: {
                return new Version(last.getMajor() + offset, base, base);
            }
            case ADDED: {
                return new Version(last.getMajor(), last.getMinor() + offset, base);
            }
        }
        return new Version(last.getMajor(), last.getMinor(), last.getMicro() + offset);
    }

    public BundleInfo getBundleInfo() {
        return this.binfo;
    }

    public static class BundleInfo {
        public String bsn;
        public Version olderVersion;
        public Version newerVersion;
        public Version suggestedVersion;
        public boolean mismatch;
        public String reason;
        @Deprecated
        public Version version;
    }

    public static class Info {
        public String packageName;
        public Diff packageDiff;
        public Collection<String> providers;
        public Map<String, String> attributes;
        public Version newerVersion;
        public Version olderVersion;
        public Version suggestedVersion;
        public Version suggestedIfProviders;
        public boolean mismatch;
        public String warning = "";
        public String reason;
    }
}

