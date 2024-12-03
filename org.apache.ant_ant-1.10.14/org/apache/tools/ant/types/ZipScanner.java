/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.ArchiveScanner;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.ZipResource;
import org.apache.tools.ant.util.StreamUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

public class ZipScanner
extends ArchiveScanner {
    @Override
    protected void fillMapsFromArchive(Resource src, String encoding, Map<String, Resource> fileEntries, Map<String, Resource> matchFileEntries, Map<String, Resource> dirEntries, Map<String, Resource> matchDirEntries) {
        File srcFile = src.asOptional(FileProvider.class).map(FileProvider::getFile).orElseThrow(() -> new BuildException("Only file provider resources are supported"));
        try (ZipFile zf = new ZipFile(srcFile, encoding);){
            StreamUtils.enumerationAsStream(zf.getEntries()).forEach(entry -> {
                ZipResource r = new ZipResource(srcFile, encoding, (ZipEntry)entry);
                String name = entry.getName();
                if (entry.isDirectory()) {
                    name = ZipScanner.trimSeparator(name);
                    dirEntries.put(name, r);
                    if (this.match(name)) {
                        matchDirEntries.put(name, r);
                    }
                } else {
                    fileEntries.put(name, r);
                    if (this.match(name)) {
                        matchFileEntries.put(name, r);
                    }
                }
            });
        }
        catch (ZipException ex) {
            throw new BuildException("Problem reading " + srcFile, ex);
        }
        catch (IOException ex) {
            throw new BuildException("Problem opening " + srcFile, ex);
        }
    }
}

