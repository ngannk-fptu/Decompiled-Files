/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.extension;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.optional.extension.Extension;
import org.apache.tools.ant.taskdefs.optional.extension.ExtensionAdapter;
import org.apache.tools.ant.taskdefs.optional.extension.ExtensionSet;
import org.apache.tools.ant.taskdefs.optional.extension.ExtraAttribute;

public final class JarLibManifestTask
extends Task {
    private static final String MANIFEST_VERSION = "1.0";
    private static final String CREATED_BY = "Created-By";
    private File destFile;
    private Extension extension;
    private final List<ExtensionSet> dependencies = new ArrayList<ExtensionSet>();
    private final List<ExtensionSet> optionals = new ArrayList<ExtensionSet>();
    private final List<ExtraAttribute> extraAttributes = new ArrayList<ExtraAttribute>();

    public void setDestfile(File destFile) {
        this.destFile = destFile;
    }

    public void addConfiguredExtension(ExtensionAdapter extensionAdapter) throws BuildException {
        if (null != this.extension) {
            throw new BuildException("Can not have multiple extensions defined in one library.");
        }
        this.extension = extensionAdapter.toExtension();
    }

    public void addConfiguredDepends(ExtensionSet extensionSet) {
        this.dependencies.add(extensionSet);
    }

    public void addConfiguredOptions(ExtensionSet extensionSet) {
        this.optionals.add(extensionSet);
    }

    public void addConfiguredAttribute(ExtraAttribute attribute) {
        this.extraAttributes.add(attribute);
    }

    @Override
    public void execute() throws BuildException {
        this.validate();
        Manifest manifest = new Manifest();
        Attributes attributes = manifest.getMainAttributes();
        attributes.put(Attributes.Name.MANIFEST_VERSION, MANIFEST_VERSION);
        attributes.putValue(CREATED_BY, "Apache Ant " + this.getProject().getProperty("ant.version"));
        this.appendExtraAttributes(attributes);
        if (null != this.extension) {
            Extension.addExtension(this.extension, attributes);
        }
        List<Extension> depends = this.toExtensions(this.dependencies);
        this.appendExtensionList(attributes, Extension.EXTENSION_LIST, "lib", depends.size());
        this.appendLibraryList(attributes, "lib", depends);
        List<Extension> option = this.toExtensions(this.optionals);
        this.appendExtensionList(attributes, Extension.OPTIONAL_EXTENSION_LIST, "opt", option.size());
        this.appendLibraryList(attributes, "opt", option);
        try {
            this.log("Generating manifest " + this.destFile.getAbsoluteFile(), 2);
            this.writeManifest(manifest);
        }
        catch (IOException ioe) {
            throw new BuildException(ioe.getMessage(), ioe);
        }
    }

    private void validate() throws BuildException {
        if (null == this.destFile) {
            throw new BuildException("Destfile attribute not specified.");
        }
        if (this.destFile.exists() && !this.destFile.isFile()) {
            throw new BuildException("%s is not a file.", this.destFile);
        }
    }

    private void appendExtraAttributes(Attributes attributes) {
        for (ExtraAttribute attribute : this.extraAttributes) {
            attributes.putValue(attribute.getName(), attribute.getValue());
        }
    }

    private void writeManifest(Manifest manifest) throws IOException {
        try (OutputStream output = Files.newOutputStream(this.destFile.toPath(), new OpenOption[0]);){
            manifest.write(output);
            output.flush();
        }
    }

    private void appendLibraryList(Attributes attributes, String listPrefix, List<Extension> extensions) throws BuildException {
        int size = extensions.size();
        for (int i = 0; i < size; ++i) {
            Extension.addExtension(extensions.get(i), listPrefix + i + "-", attributes);
        }
    }

    private void appendExtensionList(Attributes attributes, Attributes.Name extensionKey, String listPrefix, int size) {
        attributes.put(extensionKey, IntStream.range(0, size).mapToObj(i -> listPrefix + i).collect(Collectors.joining(" ")));
    }

    private List<Extension> toExtensions(List<ExtensionSet> extensionSets) throws BuildException {
        Project prj = this.getProject();
        return extensionSets.stream().map(xset -> xset.toExtensions(prj)).flatMap(Stream::of).collect(Collectors.toList());
    }
}

