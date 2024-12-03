/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.extension;

import java.io.File;
import java.util.List;
import java.util.Vector;
import java.util.stream.Stream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.optional.extension.Extension;
import org.apache.tools.ant.taskdefs.optional.extension.ExtensionAdapter;
import org.apache.tools.ant.taskdefs.optional.extension.ExtensionSet;
import org.apache.tools.ant.taskdefs.optional.extension.ExtensionUtil;

public class JarLibAvailableTask
extends Task {
    private File libraryFile;
    private final List<ExtensionSet> extensionFileSets = new Vector<ExtensionSet>();
    private String propertyName;
    private ExtensionAdapter requiredExtension;

    public void setProperty(String property) {
        this.propertyName = property;
    }

    public void setFile(File file) {
        this.libraryFile = file;
    }

    public void addConfiguredExtension(ExtensionAdapter extension) {
        if (null != this.requiredExtension) {
            throw new BuildException("Can not specify extension to search for multiple times.");
        }
        this.requiredExtension = extension;
    }

    public void addConfiguredExtensionSet(ExtensionSet extensionSet) {
        this.extensionFileSets.add(extensionSet);
    }

    @Override
    public void execute() throws BuildException {
        this.validate();
        Project prj = this.getProject();
        Stream<Extension> extensions = !this.extensionFileSets.isEmpty() ? this.extensionFileSets.stream().map(xset -> xset.toExtensions(prj)).flatMap(Stream::of) : Stream.of(Extension.getAvailable(ExtensionUtil.getManifest(this.libraryFile)));
        Extension test = this.requiredExtension.toExtension();
        if (extensions.anyMatch(x -> x.isCompatibleWith(test))) {
            prj.setNewProperty(this.propertyName, "true");
        }
    }

    private void validate() throws BuildException {
        if (null == this.requiredExtension) {
            throw new BuildException("Extension element must be specified.");
        }
        if (null == this.libraryFile) {
            if (this.extensionFileSets.isEmpty()) {
                throw new BuildException("File attribute not specified.");
            }
        } else {
            if (!this.libraryFile.exists()) {
                throw new BuildException("File '%s' does not exist.", this.libraryFile);
            }
            if (!this.libraryFile.isFile()) {
                throw new BuildException("'%s' is not a file.", this.libraryFile);
            }
        }
    }
}

