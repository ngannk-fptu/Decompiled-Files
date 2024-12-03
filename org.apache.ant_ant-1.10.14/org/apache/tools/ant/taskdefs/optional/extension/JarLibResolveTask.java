/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.extension;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.optional.extension.Extension;
import org.apache.tools.ant.taskdefs.optional.extension.ExtensionAdapter;
import org.apache.tools.ant.taskdefs.optional.extension.ExtensionResolver;
import org.apache.tools.ant.taskdefs.optional.extension.ExtensionUtil;
import org.apache.tools.ant.taskdefs.optional.extension.resolvers.AntResolver;
import org.apache.tools.ant.taskdefs.optional.extension.resolvers.LocationResolver;
import org.apache.tools.ant.taskdefs.optional.extension.resolvers.URLResolver;

public class JarLibResolveTask
extends Task {
    private String propertyName;
    private Extension requiredExtension;
    private final List<ExtensionResolver> resolvers = new ArrayList<ExtensionResolver>();
    private boolean checkExtension = true;
    private boolean failOnError = true;

    public void setProperty(String property) {
        this.propertyName = property;
    }

    public void setCheckExtension(boolean checkExtension) {
        this.checkExtension = checkExtension;
    }

    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    public void addConfiguredLocation(LocationResolver loc) {
        this.resolvers.add(loc);
    }

    public void addConfiguredUrl(URLResolver url) {
        this.resolvers.add(url);
    }

    public void addConfiguredAnt(AntResolver ant) {
        this.resolvers.add(ant);
    }

    public void addConfiguredExtension(ExtensionAdapter extension) {
        if (null != this.requiredExtension) {
            throw new BuildException("Can not specify extension to resolve multiple times.");
        }
        this.requiredExtension = extension.toExtension();
    }

    @Override
    public void execute() throws BuildException {
        this.validate();
        this.getProject().log("Resolving extension: " + this.requiredExtension, 3);
        String candidate = this.getProject().getProperty(this.propertyName);
        if (null != candidate) {
            String message = "Property Already set to: " + candidate;
            if (this.failOnError) {
                throw new BuildException(message);
            }
            this.getProject().log(message, 0);
            return;
        }
        for (ExtensionResolver resolver : this.resolvers) {
            this.getProject().log("Searching for extension using Resolver:" + resolver, 3);
            try {
                File file = resolver.resolve(this.requiredExtension, this.getProject());
                try {
                    this.checkExtension(file);
                    return;
                }
                catch (BuildException be) {
                    this.getProject().log("File " + file + " returned by resolver failed to satisfy extension due to: " + be.getMessage(), 1);
                }
            }
            catch (BuildException be) {
                this.getProject().log("Failed to resolve extension to file using resolver " + resolver + " due to: " + be, 1);
            }
        }
        this.missingExtension();
    }

    private void missingExtension() {
        String message = "Unable to resolve extension to a file";
        if (this.failOnError) {
            throw new BuildException("Unable to resolve extension to a file");
        }
        this.getProject().log("Unable to resolve extension to a file", 0);
    }

    private void checkExtension(File file) {
        if (!file.exists()) {
            throw new BuildException("File %s does not exist", file);
        }
        if (!file.isFile()) {
            throw new BuildException("File %s is not a file", file);
        }
        if (this.checkExtension) {
            this.getProject().log("Checking file " + file + " to see if it satisfies extension", 3);
            Manifest manifest = ExtensionUtil.getManifest(file);
            for (Extension extension : Extension.getAvailable(manifest)) {
                if (!extension.isCompatibleWith(this.requiredExtension)) continue;
                this.setLibraryProperty(file);
                return;
            }
            String message = "File " + file + " skipped as it does not satisfy extension";
            this.getProject().log(message, 3);
            throw new BuildException(message);
        }
        this.getProject().log("Setting property to " + file + " without verifying library satisfies extension", 3);
        this.setLibraryProperty(file);
    }

    private void setLibraryProperty(File file) {
        this.getProject().setNewProperty(this.propertyName, file.getAbsolutePath());
    }

    private void validate() throws BuildException {
        if (null == this.propertyName) {
            throw new BuildException("Property attribute must be specified.");
        }
        if (null == this.requiredExtension) {
            throw new BuildException("Extension element must be specified.");
        }
    }
}

