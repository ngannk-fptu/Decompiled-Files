/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Manifest;
import org.apache.tools.ant.taskdefs.ManifestException;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.util.StreamUtils;

public class ManifestTask
extends Task {
    public static final String VALID_ATTRIBUTE_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_";
    private Manifest nestedManifest = new Manifest();
    private File manifestFile;
    private Mode mode = new Mode();
    private String encoding;
    private boolean mergeClassPaths = false;
    private boolean flattenClassPaths = false;

    public ManifestTask() {
        this.mode.setValue("replace");
    }

    public void addConfiguredSection(Manifest.Section section) throws ManifestException {
        StreamUtils.enumerationAsStream(section.getAttributeKeys()).map(section::getAttribute).forEach(this::checkAttribute);
        this.nestedManifest.addConfiguredSection(section);
    }

    public void addConfiguredAttribute(Manifest.Attribute attribute) throws ManifestException {
        this.checkAttribute(attribute);
        this.nestedManifest.addConfiguredAttribute(attribute);
    }

    private void checkAttribute(Manifest.Attribute attribute) throws BuildException {
        String name = attribute.getName();
        char ch = name.charAt(0);
        if (ch == '-' || ch == '_') {
            throw new BuildException("Manifest attribute names must not start with '%c'.", Character.valueOf(ch));
        }
        for (int i = 0; i < name.length(); ++i) {
            ch = name.charAt(i);
            if (VALID_ATTRIBUTE_CHARS.indexOf(ch) >= 0) continue;
            throw new BuildException("Manifest attribute names must not contain '%c'", Character.valueOf(ch));
        }
    }

    public void setFile(File f) {
        this.manifestFile = f;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setMode(Mode m) {
        this.mode = m;
    }

    public void setMergeClassPathAttributes(boolean b) {
        this.mergeClassPaths = b;
    }

    public void setFlattenAttributes(boolean b) {
        this.flattenClassPaths = b;
    }

    @Override
    public void execute() throws BuildException {
        if (this.manifestFile == null) {
            throw new BuildException("the file attribute is required");
        }
        Manifest toWrite = Manifest.getDefaultManifest();
        Manifest current = null;
        BuildException error = null;
        if (this.manifestFile.exists()) {
            Charset charset = Charset.forName(this.encoding == null ? "UTF-8" : this.encoding);
            try (InputStreamReader isr = new InputStreamReader(Files.newInputStream(this.manifestFile.toPath(), new OpenOption[0]), charset);){
                current = new Manifest(isr);
            }
            catch (ManifestException m) {
                error = new BuildException("Existing manifest " + this.manifestFile + " is invalid", m, this.getLocation());
            }
            catch (IOException e2) {
                error = new BuildException("Failed to read " + this.manifestFile, e2, this.getLocation());
            }
        } else {
            File parent = this.manifestFile.getParentFile();
            if (!(parent == null || parent.isDirectory() || parent.mkdirs() || parent.isDirectory())) {
                throw new BuildException("Failed to create missing parent directory for %s", this.manifestFile);
            }
        }
        StreamUtils.enumerationAsStream(this.nestedManifest.getWarnings()).forEach(e -> this.log("Manifest warning: " + e, 1));
        try {
            if ("update".equals(this.mode.getValue()) && this.manifestFile.exists()) {
                if (current != null) {
                    toWrite.merge(current, false, this.mergeClassPaths);
                } else if (error != null) {
                    throw error;
                }
            }
            toWrite.merge(this.nestedManifest, false, this.mergeClassPaths);
        }
        catch (ManifestException m) {
            throw new BuildException("Manifest is invalid", m, this.getLocation());
        }
        if (toWrite.equals(current)) {
            this.log("Manifest has not changed, do not recreate", 3);
            return;
        }
        try (PrintWriter w = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(this.manifestFile.toPath(), new OpenOption[0]), Manifest.JAR_CHARSET));){
            toWrite.write(w, this.flattenClassPaths);
            if (w.checkError()) {
                throw new IOException("Encountered an error writing manifest");
            }
        }
        catch (IOException e3) {
            throw new BuildException("Failed to write " + this.manifestFile, e3, this.getLocation());
        }
    }

    public static class Mode
    extends EnumeratedAttribute {
        @Override
        public String[] getValues() {
            return new String[]{"update", "replace"};
        }
    }
}

