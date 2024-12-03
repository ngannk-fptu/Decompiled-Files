/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package aQute.bnd.maven;

import aQute.bnd.maven.BsnToMavenPath;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Builder;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.Verifier;
import aQute.bnd.service.Plugin;
import aQute.bnd.service.RepositoryPlugin;
import aQute.bnd.service.Strategy;
import aQute.bnd.version.Version;
import aQute.bnd.version.VersionRange;
import aQute.lib.collections.SortedList;
import aQute.lib.io.IO;
import aQute.service.reporter.Reporter;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class MavenRepository
implements RepositoryPlugin,
Plugin,
BsnToMavenPath {
    private static final Logger logger = LoggerFactory.getLogger(MavenRepository.class);
    public static final String NAME = "name";
    static final String MAVEN_REPO_LOCAL = System.getProperty("maven.repo.local", "~/.m2/repository");
    File root;
    Reporter reporter;
    String name;

    public String toString() {
        return "maven:" + this.root;
    }

    @Override
    public boolean canWrite() {
        return false;
    }

    private File[] get(String bsn, String version) throws Exception {
        List<BsnToMavenPath> plugins;
        VersionRange range = new VersionRange("0");
        if (version != null) {
            range = new VersionRange(version);
        }
        if ((plugins = ((Processor)this.reporter).getPlugins(BsnToMavenPath.class)).isEmpty()) {
            plugins.add(this);
        }
        for (BsnToMavenPath cvr : plugins) {
            File[] files;
            String[] paths = cvr.getGroupAndArtifact(bsn);
            if (paths == null || (files = this.find(paths[0], paths[1], range)) == null) continue;
            return files;
        }
        logger.debug("Cannot find in maven: {}-{}", (Object)bsn, (Object)version);
        return null;
    }

    File[] find(String groupId, String artifactId, VersionRange range) {
        String path = groupId.replace(".", "/");
        File vsdir = Processor.getFile(this.root, path);
        if (!vsdir.isDirectory()) {
            return null;
        }
        vsdir = Processor.getFile(vsdir, artifactId);
        ArrayList<File> result = new ArrayList<File>();
        if (vsdir.isDirectory()) {
            String[] versions;
            for (String v : versions = vsdir.list()) {
                String vv = Analyzer.cleanupVersion(v);
                if (Verifier.isVersion(vv)) {
                    Version vvv = new Version(vv);
                    if (!range.includes(vvv)) continue;
                    File file = Processor.getFile(vsdir, v + "/" + artifactId + "-" + v + ".jar");
                    if (file.isFile()) {
                        result.add(file);
                        continue;
                    }
                    this.reporter.warning("Expected maven entry was not a valid file %s ", file);
                    continue;
                }
                this.reporter.warning("Expected a version directory in maven: dir=%s raw-version=%s cleaned-up-version=%s", vsdir, vv, v);
            }
        } else {
            return null;
        }
        return result.toArray(new File[0]);
    }

    @Override
    public List<String> list(String regex) {
        ArrayList<String> bsns = new ArrayList<String>();
        Pattern match = Pattern.compile(".*");
        if (regex != null) {
            match = Pattern.compile(regex);
        }
        this.find(bsns, match, this.root, "");
        return bsns;
    }

    void find(List<String> bsns, Pattern pattern, File base, String name) {
        if (base.isDirectory()) {
            String[] list = base.list();
            boolean found = false;
            for (String entry : list) {
                char c = entry.charAt(0);
                if (c >= '0' && c <= '9') {
                    if (!pattern.matcher(name).matches()) continue;
                    found = true;
                    continue;
                }
                String nextName = entry;
                if (name.length() != 0) {
                    nextName = name + "." + entry;
                }
                File next = Processor.getFile(base, entry);
                this.find(bsns, pattern, next, nextName);
            }
            if (found) {
                bsns.add(name);
            }
        }
    }

    @Override
    public RepositoryPlugin.PutResult put(InputStream stream, RepositoryPlugin.PutOptions options) throws Exception {
        throw new UnsupportedOperationException("Maven does not support the put command");
    }

    @Override
    public SortedSet<Version> versions(String bsn) throws Exception {
        File[] files = this.get(bsn, null);
        ArrayList<Version> versions = new ArrayList<Version>();
        for (File f : files) {
            String version = f.getParentFile().getName();
            version = Builder.cleanupVersion(version);
            Version v = new Version(version);
            versions.add(v);
        }
        if (versions.isEmpty()) {
            return SortedList.empty();
        }
        return new SortedList<Version>(versions);
    }

    @Override
    public void setProperties(Map<String, String> map) {
        String root = map.get("root");
        if (root == null) {
            this.root = IO.getFile(MAVEN_REPO_LOCAL);
        } else {
            File home = new File("");
            this.root = Processor.getFile(home, root).getAbsoluteFile();
        }
        if (!this.root.isDirectory()) {
            this.reporter.error("Maven repository did not get a proper URL to the repository %s", root);
        }
        this.name = map.get(NAME);
    }

    @Override
    public void setReporter(Reporter processor) {
        this.reporter = processor;
    }

    @Override
    public String[] getGroupAndArtifact(String bsn) {
        int n = bsn.indexOf(46);
        while (n > 0) {
            String artifactId = bsn.substring(n + 1);
            String groupId = bsn.substring(0, n);
            File gdir = new File(this.root, groupId.replace('.', File.separatorChar)).getAbsoluteFile();
            File adir = new File(gdir, artifactId).getAbsoluteFile();
            if (adir.isDirectory()) {
                return new String[]{groupId, artifactId};
            }
            n = bsn.indexOf(46, n + 1);
        }
        return null;
    }

    @Override
    public String getName() {
        if (this.name == null) {
            return this.toString();
        }
        return this.name;
    }

    public File get(String bsn, String range, Strategy strategy, Map<String, String> properties) throws Exception {
        File[] files = this.get(bsn, range);
        if (files.length >= 0) {
            switch (strategy) {
                case LOWEST: {
                    return files[0];
                }
                case HIGHEST: {
                    return files[files.length - 1];
                }
            }
        }
        return null;
    }

    public void setRoot(File f) {
        this.root = f;
    }

    @Override
    public String getLocation() {
        return this.root.toString();
    }

    @Override
    public File get(String bsn, Version version, Map<String, String> properties, RepositoryPlugin.DownloadListener ... listeners) throws Exception {
        File file = this.get(bsn, version.toString(), Strategy.EXACT, properties);
        if (file == null) {
            return null;
        }
        for (RepositoryPlugin.DownloadListener l : listeners) {
            try {
                l.success(file);
            }
            catch (Exception e) {
                this.reporter.exception(e, "Download listener for %s", file);
            }
        }
        return file;
    }
}

