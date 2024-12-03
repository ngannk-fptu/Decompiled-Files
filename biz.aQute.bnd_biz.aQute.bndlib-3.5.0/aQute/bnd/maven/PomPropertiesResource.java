/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.maven;

import aQute.bnd.maven.PomResource;
import aQute.bnd.osgi.WriteResource;
import aQute.lib.utf8properties.UTF8Properties;
import java.io.IOException;
import java.io.OutputStream;

public class PomPropertiesResource
extends WriteResource {
    private final UTF8Properties pomProperties = new UTF8Properties();
    private final String where;

    public PomPropertiesResource(PomResource pomResource) {
        this.pomProperties.setProperty("groupId", pomResource.getGroupId());
        this.pomProperties.setProperty("artifactId", pomResource.getArtifactId());
        this.pomProperties.setProperty("version", pomResource.getVersion());
        this.where = pomResource.getWhere().replaceFirst("(?<=^|/)pom\\.xml$", "pom\\.properties");
    }

    public String getWhere() {
        return this.where;
    }

    @Override
    public long lastModified() {
        return 0L;
    }

    @Override
    public void write(OutputStream out) throws IOException {
        this.pomProperties.store(out);
    }
}

