/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.maven.support;

import aQute.bnd.maven.support.CachedPom;
import aQute.bnd.maven.support.MavenEntry;
import aQute.bnd.maven.support.Pom;
import aQute.bnd.maven.support.ProjectPom;
import aQute.lib.io.IO;
import java.io.File;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class Maven {
    final Map<String, MavenEntry> entries = new ConcurrentHashMap<String, MavenEntry>();
    static final String[] ALGORITHMS = new String[]{"md5", "sha1"};
    boolean usecache = false;
    final Executor executor;
    static final String MAVEN_REPO_LOCAL = System.getProperty("maven.repo.local", "~/.m2/repository");
    File repository = IO.getFile(MAVEN_REPO_LOCAL);
    static Pattern MAVEN_RANGE = Pattern.compile("(\\[|\\()(.+)(,(.+))(\\]|\\))");

    public Maven(Executor executor) {
        this.executor = executor == null ? Executors.newCachedThreadPool() : executor;
    }

    public CachedPom getPom(String groupId, String artifactId, String version, URI ... extra) throws Exception {
        MavenEntry entry = this.getEntry(groupId, artifactId, version);
        return entry.getPom(extra);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MavenEntry getEntry(String groupId, String artifactId, String version) throws Exception {
        MavenEntry entry;
        String path = this.dirpath(groupId, artifactId, version);
        Map<String, MavenEntry> map = this.entries;
        synchronized (map) {
            entry = this.entries.get(path);
            if (entry != null) {
                return entry;
            }
            entry = new MavenEntry(this, path);
            this.entries.put(path, entry);
        }
        return entry;
    }

    private String dirpath(String groupId, String artifactId, String version) {
        return groupId.replace('.', '/') + '/' + artifactId + '/' + version + "/" + artifactId + "-" + version;
    }

    public void schedule(Runnable runnable) {
        if (this.executor == null) {
            runnable.run();
        } else {
            this.executor.execute(runnable);
        }
    }

    public ProjectPom createProjectModel(File file) throws Exception {
        ProjectPom pom = new ProjectPom(this, file);
        pom.parse();
        return pom;
    }

    public MavenEntry getEntry(Pom pom) throws Exception {
        return this.getEntry(pom.getGroupId(), pom.getArtifactId(), pom.getVersion());
    }

    public void setM2(File dir) {
        this.repository = new File(dir, "repository");
    }

    public String toString() {
        return "Maven [" + (this.repository != null ? "m2=" + this.repository : "") + "]";
    }
}

