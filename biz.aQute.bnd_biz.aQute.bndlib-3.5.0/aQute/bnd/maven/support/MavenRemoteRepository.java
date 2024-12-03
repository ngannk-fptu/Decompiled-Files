/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.maven.support;

import aQute.bnd.maven.support.CachedPom;
import aQute.bnd.maven.support.Maven;
import aQute.bnd.maven.support.Pom;
import aQute.bnd.service.Plugin;
import aQute.bnd.service.Registry;
import aQute.bnd.service.RegistryPlugin;
import aQute.bnd.service.RepositoryPlugin;
import aQute.bnd.service.Strategy;
import aQute.bnd.version.Version;
import aQute.lib.io.IO;
import aQute.service.reporter.Reporter;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

public class MavenRemoteRepository
implements RepositoryPlugin,
RegistryPlugin,
Plugin {
    Reporter reporter;
    URI[] repositories;
    Registry registry;
    Maven maven;

    public File get(String bsn, String version, Strategy strategy, Map<String, String> properties) throws Exception {
        String value;
        String groupId = null;
        if (properties != null) {
            groupId = properties.get("groupId");
        }
        if (groupId == null) {
            int n = bsn.indexOf(43);
            if (n < 0) {
                return null;
            }
            groupId = bsn.substring(0, n);
            bsn = bsn.substring(n + 1);
        }
        String artifactId = bsn;
        if (version == null) {
            if (this.reporter != null) {
                this.reporter.error("Maven dependency version not set for %s - %s", groupId, artifactId);
            }
            return null;
        }
        CachedPom pom = this.getMaven().getPom(groupId, artifactId, version, this.repositories);
        String string = value = properties == null ? null : properties.get("scope");
        if (value == null) {
            return pom.getArtifact();
        }
        Pom.Scope action = null;
        try {
            action = Pom.Scope.valueOf(value);
            return pom.getLibrary(action, this.repositories);
        }
        catch (Exception e) {
            return pom.getArtifact();
        }
    }

    public Maven getMaven() {
        if (this.maven != null) {
            return this.maven;
        }
        this.maven = this.registry.getPlugin(Maven.class);
        return this.maven;
    }

    @Override
    public boolean canWrite() {
        return false;
    }

    @Override
    public RepositoryPlugin.PutResult put(InputStream stream, RepositoryPlugin.PutOptions options) throws Exception {
        throw new UnsupportedOperationException("cannot do put");
    }

    @Override
    public List<String> list(String regex) throws Exception {
        throw new UnsupportedOperationException("cannot do list");
    }

    @Override
    public SortedSet<Version> versions(String bsn) throws Exception {
        throw new UnsupportedOperationException("cannot do versions");
    }

    @Override
    public String getName() {
        return "maven";
    }

    public void setRepositories(URI ... urls) {
        this.repositories = urls;
    }

    @Override
    public void setProperties(Map<String, String> map) {
        String repoString = map.get("repositories");
        if (repoString != null) {
            String[] repos = repoString.split("\\s*,\\s*");
            this.repositories = new URI[repos.length];
            int n = 0;
            for (String repo : repos) {
                try {
                    URI uri = new URI(repo);
                    if (!uri.isAbsolute()) {
                        uri = IO.getFile(new File(""), repo).toURI();
                    }
                    this.repositories[n++] = uri;
                }
                catch (Exception e) {
                    if (this.reporter == null) continue;
                    this.reporter.error("Invalid repository %s for maven plugin, %s", repo, e);
                }
            }
        }
    }

    @Override
    public void setReporter(Reporter reporter) {
        this.reporter = reporter;
    }

    @Override
    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public void setMaven(Maven maven) {
        this.maven = maven;
    }

    @Override
    public String getLocation() {
        if (this.repositories == null || this.repositories.length == 0) {
            return "maven central";
        }
        return Arrays.toString(this.repositories);
    }

    @Override
    public File get(String bsn, Version version, Map<String, String> properties, RepositoryPlugin.DownloadListener ... listeners) throws Exception {
        File f = this.get(bsn, version.toString(), Strategy.EXACT, properties);
        if (f == null) {
            return null;
        }
        for (RepositoryPlugin.DownloadListener l : listeners) {
            try {
                l.success(f);
            }
            catch (Exception e) {
                this.reporter.exception(e, "Download listener for %s", f);
            }
        }
        return f;
    }
}

