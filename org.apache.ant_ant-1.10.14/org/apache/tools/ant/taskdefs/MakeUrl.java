/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;

public class MakeUrl
extends Task {
    public static final String ERROR_MISSING_FILE = "A source file is missing: ";
    public static final String ERROR_NO_PROPERTY = "No property defined";
    public static final String ERROR_NO_FILES = "No files defined";
    private String property;
    private File file;
    private String separator = " ";
    private List<FileSet> filesets = new LinkedList<FileSet>();
    private List<Path> paths = new LinkedList<Path>();
    private boolean validate = true;

    public void setProperty(String property) {
        this.property = property;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void addFileSet(FileSet fileset) {
        this.filesets.add(fileset);
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }

    public void addPath(Path path) {
        this.paths.add(path);
    }

    private String filesetsToURL() {
        if (this.filesets.isEmpty()) {
            return "";
        }
        int count = 0;
        StringBuilder urls = new StringBuilder();
        for (FileSet fs : this.filesets) {
            DirectoryScanner scanner = fs.getDirectoryScanner(this.getProject());
            for (String file : scanner.getIncludedFiles()) {
                File f = new File(scanner.getBasedir(), file);
                this.validateFile(f);
                String asUrl = this.toURL(f);
                urls.append(asUrl);
                this.log(asUrl, 4);
                urls.append(this.separator);
                ++count;
            }
        }
        return this.stripTrailingSeparator(urls, count);
    }

    private String stripTrailingSeparator(StringBuilder urls, int count) {
        if (count > 0) {
            urls.delete(urls.length() - this.separator.length(), urls.length());
            return new String(urls);
        }
        return "";
    }

    private String pathsToURL() {
        if (this.paths.isEmpty()) {
            return "";
        }
        int count = 0;
        StringBuilder urls = new StringBuilder();
        for (Path path : this.paths) {
            for (String element : path.list()) {
                File f = new File(element);
                this.validateFile(f);
                String asUrl = this.toURL(f);
                urls.append(asUrl);
                this.log(asUrl, 4);
                urls.append(this.separator);
                ++count;
            }
        }
        return this.stripTrailingSeparator(urls, count);
    }

    private void validateFile(File fileToCheck) {
        if (this.validate && !fileToCheck.exists()) {
            throw new BuildException(ERROR_MISSING_FILE + fileToCheck);
        }
    }

    @Override
    public void execute() throws BuildException {
        String url;
        this.validate();
        if (this.getProject().getProperty(this.property) != null) {
            return;
        }
        String filesetURL = this.filesetsToURL();
        if (this.file == null) {
            url = filesetURL;
        } else {
            this.validateFile(this.file);
            url = this.toURL(this.file);
            if (!filesetURL.isEmpty()) {
                url = url + this.separator + filesetURL;
            }
        }
        String pathURL = this.pathsToURL();
        if (!pathURL.isEmpty()) {
            url = url.isEmpty() ? pathURL : url + this.separator + pathURL;
        }
        this.log("Setting " + this.property + " to URL " + url, 3);
        this.getProject().setNewProperty(this.property, url);
    }

    private void validate() {
        if (this.property == null) {
            throw new BuildException(ERROR_NO_PROPERTY);
        }
        if (this.file == null && this.filesets.isEmpty() && this.paths.isEmpty()) {
            throw new BuildException(ERROR_NO_FILES);
        }
    }

    private String toURL(File fileToConvert) {
        return FileUtils.getFileUtils().toURI(fileToConvert.getAbsolutePath());
    }
}

