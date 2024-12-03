/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.extension;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.extension.Extension;
import org.apache.tools.ant.taskdefs.optional.extension.ExtensionAdapter;
import org.apache.tools.ant.taskdefs.optional.extension.LibFileSet;
import org.apache.tools.ant.types.FileSet;

public final class ExtensionUtil {
    private ExtensionUtil() {
    }

    static ArrayList<Extension> toExtensions(List<? extends ExtensionAdapter> adapters) throws BuildException {
        return adapters.stream().map(ExtensionAdapter::toExtension).collect(Collectors.toCollection(ArrayList::new));
    }

    static void extractExtensions(Project project, List<Extension> libraries, List<FileSet> fileset) throws BuildException {
        if (!fileset.isEmpty()) {
            Collections.addAll(libraries, ExtensionUtil.getExtensions(project, fileset));
        }
    }

    private static Extension[] getExtensions(Project project, List<FileSet> libraries) throws BuildException {
        ArrayList<Extension> extensions = new ArrayList<Extension>();
        for (FileSet fileSet : libraries) {
            boolean includeImpl = true;
            boolean includeURL = true;
            if (fileSet instanceof LibFileSet) {
                LibFileSet libFileSet = (LibFileSet)fileSet;
                includeImpl = libFileSet.isIncludeImpl();
                includeURL = libFileSet.isIncludeURL();
            }
            DirectoryScanner scanner = fileSet.getDirectoryScanner(project);
            File basedir = scanner.getBasedir();
            for (String fileName : scanner.getIncludedFiles()) {
                File file = new File(basedir, fileName);
                ExtensionUtil.loadExtensions(file, extensions, includeImpl, includeURL);
            }
        }
        return extensions.toArray(new Extension[0]);
    }

    private static void loadExtensions(File file, List<Extension> extensionList, boolean includeImpl, boolean includeURL) throws BuildException {
        try (JarFile jarFile = new JarFile(file);){
            for (Extension extension : Extension.getAvailable(jarFile.getManifest())) {
                ExtensionUtil.addExtension(extensionList, extension, includeImpl, includeURL);
            }
        }
        catch (Exception e) {
            throw new BuildException(e.getMessage(), e);
        }
    }

    private static void addExtension(List<Extension> extensionList, Extension originalExtension, boolean includeImpl, boolean includeURL) {
        boolean hasImplAttributes;
        Extension extension = originalExtension;
        if (!includeURL && null != extension.getImplementationURL()) {
            extension = new Extension(extension.getExtensionName(), extension.getSpecificationVersion().toString(), extension.getSpecificationVendor(), extension.getImplementationVersion().toString(), extension.getImplementationVendor(), extension.getImplementationVendorID(), null);
        }
        boolean bl = hasImplAttributes = null != extension.getImplementationURL() || null != extension.getImplementationVersion() || null != extension.getImplementationVendorID() || null != extension.getImplementationVendor();
        if (!includeImpl && hasImplAttributes) {
            extension = new Extension(extension.getExtensionName(), extension.getSpecificationVersion().toString(), extension.getSpecificationVendor(), null, null, null, extension.getImplementationURL());
        }
        extensionList.add(extension);
    }

    static Manifest getManifest(File file) throws BuildException {
        Manifest manifest;
        JarFile jarFile = new JarFile(file);
        try {
            Manifest m = jarFile.getManifest();
            if (m == null) {
                throw new BuildException("%s doesn't have a MANIFEST", file);
            }
            manifest = m;
        }
        catch (Throwable throwable) {
            try {
                try {
                    jarFile.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException ioe) {
                throw new BuildException(ioe.getMessage(), ioe);
            }
        }
        jarFile.close();
        return manifest;
    }
}

