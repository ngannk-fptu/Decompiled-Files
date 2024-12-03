/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Properties;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.FileUtils;

public class BuildNumber
extends Task {
    private static final String DEFAULT_PROPERTY_NAME = "build.number";
    private static final String DEFAULT_FILENAME = "build.number";
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private File myFile;

    public void setFile(File file) {
        this.myFile = file;
    }

    @Override
    public void execute() throws BuildException {
        File savedFile = this.myFile;
        this.validate();
        Properties properties = this.loadProperties();
        int buildNumber = this.getBuildNumber(properties);
        properties.put("build.number", String.valueOf(buildNumber + 1));
        try (OutputStream output = Files.newOutputStream(this.myFile.toPath(), new OpenOption[0]);){
            properties.store(output, "Build Number for ANT. Do not edit!");
        }
        catch (IOException ioe) {
            throw new BuildException("Error while writing " + this.myFile, ioe);
        }
        finally {
            this.myFile = savedFile;
        }
        this.getProject().setNewProperty("build.number", String.valueOf(buildNumber));
    }

    private int getBuildNumber(Properties properties) throws BuildException {
        String buildNumber = properties.getProperty("build.number", "0").trim();
        try {
            return Integer.parseInt(buildNumber);
        }
        catch (NumberFormatException nfe) {
            throw new BuildException(this.myFile + " contains a non integer build number: " + buildNumber, nfe);
        }
    }

    private Properties loadProperties() throws BuildException {
        Properties properties;
        block8: {
            InputStream input = Files.newInputStream(this.myFile.toPath(), new OpenOption[0]);
            try {
                Properties properties2 = new Properties();
                properties2.load(input);
                properties = properties2;
                if (input == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (input != null) {
                        try {
                            input.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException ioe) {
                    throw new BuildException(ioe);
                }
            }
            input.close();
        }
        return properties;
    }

    private void validate() throws BuildException {
        if (null == this.myFile) {
            this.myFile = FILE_UTILS.resolveFile(this.getProject().getBaseDir(), "build.number");
        }
        if (!this.myFile.exists()) {
            try {
                FILE_UTILS.createNewFile(this.myFile);
            }
            catch (IOException ioe) {
                throw new BuildException(this.myFile + " doesn't exist and new file can't be created.", ioe);
            }
        }
        if (!this.myFile.canRead()) {
            throw new BuildException("Unable to read from " + this.myFile + ".");
        }
        if (!this.myFile.canWrite()) {
            throw new BuildException("Unable to write to " + this.myFile + ".");
        }
    }
}

