/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.maven;

import aQute.bnd.maven.BsnToMavenPath;
import aQute.bnd.service.Plugin;
import aQute.service.reporter.Reporter;
import java.util.Map;

public class MavenGroup
implements BsnToMavenPath,
Plugin {
    String groupId = "";

    @Override
    public String[] getGroupAndArtifact(String bsn) {
        String[] result = new String[]{this.groupId, bsn};
        return result;
    }

    @Override
    public void setProperties(Map<String, String> map) {
        if (map.containsKey("groupId")) {
            this.groupId = map.get("groupId");
        }
    }

    @Override
    public void setReporter(Reporter processor) {
    }
}

