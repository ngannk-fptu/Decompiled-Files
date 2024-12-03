/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginArtifact
 *  com.atlassian.plugin.ReferenceMode
 */
package com.atlassian.plugin;

import com.atlassian.plugin.JarPluginArtifact;
import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.PluginArtifactFactory;
import com.atlassian.plugin.ReferenceMode;
import com.atlassian.plugin.XmlPluginArtifact;
import java.io.File;
import java.net.URI;

public class DefaultPluginArtifactFactory
implements PluginArtifactFactory {
    final ReferenceMode referenceMode;

    public DefaultPluginArtifactFactory() {
        this(ReferenceMode.FORBID_REFERENCE);
    }

    public DefaultPluginArtifactFactory(ReferenceMode referenceMode) {
        this.referenceMode = referenceMode;
    }

    @Override
    public PluginArtifact create(URI artifactUri) {
        Object artifact = null;
        String protocol = artifactUri.getScheme();
        if ("file".equalsIgnoreCase(protocol)) {
            File artifactFile = new File(artifactUri);
            String file = artifactFile.getName();
            if (file.endsWith(".jar")) {
                artifact = new JarPluginArtifact(artifactFile, this.referenceMode);
            } else if (file.endsWith(".xml")) {
                artifact = new XmlPluginArtifact(artifactFile);
            }
        }
        if (artifact == null) {
            throw new IllegalArgumentException("The artifact URI " + artifactUri + " is not a valid plugin artifact");
        }
        return artifact;
    }
}

