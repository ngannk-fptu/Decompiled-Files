/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.maven.support;

import aQute.bnd.maven.support.MavenEntry;
import aQute.bnd.maven.support.Pom;
import java.io.File;
import java.net.URI;

public class CachedPom
extends Pom {
    final MavenEntry maven;

    CachedPom(MavenEntry mavenEntry, URI repo) throws Exception {
        super(mavenEntry.maven, mavenEntry.getPomFile(), repo);
        this.maven = mavenEntry;
    }

    @Override
    public File getArtifact() throws Exception {
        return this.maven.getArtifact();
    }

    public MavenEntry getMavenEntry() {
        return this.maven;
    }
}

