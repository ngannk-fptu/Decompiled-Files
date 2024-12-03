/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.ProjectHelperRepository;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.URLProvider;
import org.apache.tools.ant.types.resources.URLResource;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.util.FileUtils;

public class ImportTask
extends Task {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private String file;
    private boolean optional;
    private String targetPrefix = "USE_PROJECT_NAME_AS_TARGET_PREFIX";
    private String prefixSeparator = ".";
    private final Union resources = new Union();

    public ImportTask() {
        this.resources.setCache(true);
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public void setAs(String prefix) {
        this.targetPrefix = prefix;
    }

    public void setPrefixSeparator(String s) {
        this.prefixSeparator = s;
    }

    public void add(ResourceCollection r) {
        this.resources.add(r);
    }

    @Override
    public void execute() {
        if (this.file == null && this.resources.isEmpty()) {
            throw new BuildException("import requires file attribute or at least one nested resource");
        }
        if (this.getOwningTarget() == null || !this.getOwningTarget().getName().isEmpty()) {
            throw new BuildException("import only allowed as a top-level task");
        }
        ProjectHelper helper = (ProjectHelper)this.getProject().getReference("ant.projectHelper");
        if (helper == null) {
            throw new BuildException("import requires support in ProjectHelper");
        }
        if (helper.getImportStack().isEmpty()) {
            throw new BuildException("import requires support in ProjectHelper");
        }
        if (this.getLocation() == null || this.getLocation().getFileName() == null) {
            throw new BuildException("Unable to get location of import task");
        }
        Union resourcesToImport = new Union(this.getProject(), this.resources);
        Resource fromFileAttribute = this.getFileAttributeResource();
        if (fromFileAttribute != null) {
            this.resources.add(fromFileAttribute);
        }
        for (Resource r : resourcesToImport) {
            this.importResource(helper, r);
        }
    }

    private void importResource(ProjectHelper helper, Resource importedResource) {
        this.getProject().log("Importing file " + importedResource + " from " + this.getLocation().getFileName(), 3);
        if (!importedResource.isExists()) {
            String message = "Cannot find " + importedResource + " imported from " + this.getLocation().getFileName();
            if (this.optional) {
                this.getProject().log(message, 3);
                return;
            }
            throw new BuildException(message);
        }
        if (!this.isInIncludeMode() && this.hasAlreadyBeenImported(importedResource, helper.getImportStack())) {
            this.getProject().log("Skipped already imported file:\n   " + importedResource + "\n", 3);
            return;
        }
        String oldPrefix = ProjectHelper.getCurrentTargetPrefix();
        boolean oldIncludeMode = ProjectHelper.isInIncludeMode();
        String oldSep = ProjectHelper.getCurrentPrefixSeparator();
        try {
            String prefix = this.isInIncludeMode() && oldPrefix != null && this.targetPrefix != null ? oldPrefix + oldSep + this.targetPrefix : (this.isInIncludeMode() ? this.targetPrefix : ("USE_PROJECT_NAME_AS_TARGET_PREFIX".equals(this.targetPrefix) ? oldPrefix : this.targetPrefix));
            ImportTask.setProjectHelperProps(prefix, this.prefixSeparator, this.isInIncludeMode());
            ProjectHelper subHelper = ProjectHelperRepository.getInstance().getProjectHelperForBuildFile(importedResource);
            subHelper.getImportStack().addAll(helper.getImportStack());
            subHelper.getExtensionStack().addAll(helper.getExtensionStack());
            this.getProject().addReference("ant.projectHelper", subHelper);
            subHelper.parse(this.getProject(), importedResource);
            this.getProject().addReference("ant.projectHelper", helper);
            helper.getImportStack().clear();
            helper.getImportStack().addAll(subHelper.getImportStack());
            helper.getExtensionStack().clear();
            helper.getExtensionStack().addAll(subHelper.getExtensionStack());
        }
        catch (BuildException ex) {
            throw ProjectHelper.addLocationToBuildException(ex, this.getLocation());
        }
        finally {
            ImportTask.setProjectHelperProps(oldPrefix, oldSep, oldIncludeMode);
        }
    }

    private Resource getFileAttributeResource() {
        if (this.file != null) {
            if (this.isExistingAbsoluteFile(this.file)) {
                return new FileResource(FILE_UTILS.normalize(this.file));
            }
            File buildFile = new File(this.getLocation().getFileName()).getAbsoluteFile();
            if (buildFile.exists()) {
                File buildFileParent = new File(buildFile.getParent());
                File importedFile = FILE_UTILS.resolveFile(buildFileParent, this.file);
                return new FileResource(importedFile);
            }
            try {
                URL buildFileURL = new URL(this.getLocation().getFileName());
                URL importedFile = new URL(buildFileURL, this.file);
                return new URLResource(importedFile);
            }
            catch (MalformedURLException ex) {
                this.log(ex.toString(), 3);
                throw new BuildException("failed to resolve %s relative to %s", this.file, this.getLocation().getFileName());
            }
        }
        return null;
    }

    private boolean isExistingAbsoluteFile(String name) {
        File f = new File(name);
        return f.isAbsolute() && f.exists();
    }

    private boolean hasAlreadyBeenImported(Resource importedResource, Vector<Object> importStack) {
        File importedFile = importedResource.asOptional(FileProvider.class).map(FileProvider::getFile).orElse(null);
        URL importedURL = importedResource.asOptional(URLProvider.class).map(URLProvider::getURL).orElse(null);
        return importStack.stream().anyMatch(o -> this.isOneOf(o, importedResource, importedFile, importedURL));
    }

    private boolean isOneOf(Object o, Resource importedResource, File importedFile, URL importedURL) {
        if (o.equals(importedResource) || o.equals(importedFile) || o.equals(importedURL)) {
            return true;
        }
        if (o instanceof Resource) {
            FileProvider fp;
            if (importedFile != null && (fp = ((Resource)o).as(FileProvider.class)) != null && fp.getFile().equals(importedFile)) {
                return true;
            }
            if (importedURL != null) {
                URLProvider up = ((Resource)o).as(URLProvider.class);
                return up != null && up.getURL().equals(importedURL);
            }
        }
        return false;
    }

    protected final boolean isInIncludeMode() {
        return "include".equals(this.getTaskType());
    }

    private static void setProjectHelperProps(String prefix, String prefixSep, boolean inIncludeMode) {
        ProjectHelper.setCurrentTargetPrefix(prefix);
        ProjectHelper.setCurrentPrefixSeparator(prefixSep);
        ProjectHelper.setInIncludeMode(inIncludeMode);
    }
}

