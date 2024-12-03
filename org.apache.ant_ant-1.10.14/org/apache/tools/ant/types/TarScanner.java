/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import java.io.IOException;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.ArchiveScanner;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.TarResource;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;

public class TarScanner
extends ArchiveScanner {
    @Override
    protected void fillMapsFromArchive(Resource src, String encoding, Map<String, Resource> fileEntries, Map<String, Resource> matchFileEntries, Map<String, Resource> dirEntries, Map<String, Resource> matchDirEntries) {
        try (TarInputStream ti = new TarInputStream(src.getInputStream(), encoding);){
            try {
                TarEntry entry = null;
                while ((entry = ti.getNextEntry()) != null) {
                    TarResource r = new TarResource(src, entry);
                    String name = entry.getName();
                    if (entry.isDirectory()) {
                        name = TarScanner.trimSeparator(name);
                        dirEntries.put(name, r);
                        if (!this.match(name)) continue;
                        matchDirEntries.put(name, r);
                        continue;
                    }
                    fileEntries.put(name, r);
                    if (!this.match(name)) continue;
                    matchFileEntries.put(name, r);
                }
            }
            catch (IOException ex) {
                throw new BuildException("problem reading " + this.srcFile, ex);
            }
        }
        catch (IOException ex) {
            throw new BuildException("problem opening " + this.srcFile, ex);
        }
    }
}

