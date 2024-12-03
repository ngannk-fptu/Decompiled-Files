/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.launch.Locator
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.launch.Locator;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;

public class ManifestClassPath
extends Task {
    private String name;
    private File dir;
    private int maxParentLevels = 2;
    private Path path;

    @Override
    public void execute() {
        if (this.name == null) {
            throw new BuildException("Missing 'property' attribute!");
        }
        if (this.dir == null) {
            throw new BuildException("Missing 'jarfile' attribute!");
        }
        if (this.getProject().getProperty(this.name) != null) {
            throw new BuildException("Property '%s' already set!", this.name);
        }
        if (this.path == null) {
            throw new BuildException("Missing nested <classpath>!");
        }
        StringBuilder tooLongSb = new StringBuilder();
        for (int i = 0; i < this.maxParentLevels + 1; ++i) {
            tooLongSb.append("../");
        }
        String tooLongPrefix = tooLongSb.toString();
        FileUtils fileUtils = FileUtils.getFileUtils();
        this.dir = fileUtils.normalize(this.dir.getAbsolutePath());
        StringBuilder buffer = new StringBuilder();
        for (String element : this.path.list()) {
            File pathEntry = new File(element);
            String fullPath = pathEntry.getAbsolutePath();
            pathEntry = fileUtils.normalize(fullPath);
            String relPath = null;
            String canonicalPath = null;
            try {
                relPath = this.dir.equals(pathEntry) ? "." : FileUtils.getRelativePath(this.dir, pathEntry);
                canonicalPath = pathEntry.getCanonicalPath();
                if (File.separatorChar != '/') {
                    canonicalPath = canonicalPath.replace(File.separatorChar, '/');
                }
            }
            catch (Exception e) {
                throw new BuildException("error trying to get the relative path from " + this.dir + " to " + fullPath, e);
            }
            if (relPath.equals(canonicalPath) || relPath.startsWith(tooLongPrefix)) {
                throw new BuildException("No suitable relative path from %s to %s", this.dir, fullPath);
            }
            if (pathEntry.isDirectory() && !relPath.endsWith("/")) {
                relPath = relPath + '/';
            }
            relPath = Locator.encodeURI((String)relPath);
            buffer.append(relPath);
            buffer.append(' ');
        }
        this.getProject().setNewProperty(this.name, buffer.toString().trim());
    }

    public void setProperty(String name) {
        this.name = name;
    }

    public void setJarFile(File jarfile) {
        File parent = jarfile.getParentFile();
        if (!parent.isDirectory()) {
            throw new BuildException("Jar's directory not found: %s", parent);
        }
        this.dir = parent;
    }

    public void setMaxParentLevels(int levels) {
        if (levels < 0) {
            throw new BuildException("maxParentLevels must not be a negative number");
        }
        this.maxParentLevels = levels;
    }

    public void addClassPath(Path path) {
        this.path = path;
    }
}

