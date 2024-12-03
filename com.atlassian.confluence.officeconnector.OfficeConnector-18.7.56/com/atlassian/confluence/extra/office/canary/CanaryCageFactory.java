/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Maybe
 *  javax.annotation.Nonnull
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.office.canary;

import com.atlassian.confluence.extra.office.canary.CanaryCage;
import com.atlassian.confluence.extra.office.canary.CanaryEnvironment;
import com.atlassian.fugue.Maybe;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import javax.annotation.Nonnull;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CanaryCageFactory {
    private static final Logger log = LoggerFactory.getLogger(CanaryCageFactory.class);

    @Nonnull
    public static CanaryCage newCanaryCage(Path privateWorkingDirectory, String canaryJarResourceLocation, String mainClassFullname) throws Exception {
        log.info("Initialising cage for {}", (Object)canaryJarResourceLocation);
        CanaryEnvironment environment = CanaryEnvironment.currentEnv();
        Maybe<Path> javaExePath = environment.findJavaExePath();
        if (javaExePath.isEmpty()) {
            throw new UnsupportedOperationException("Failed to determine java executable path. Canary has nothing to perch on.");
        }
        Path canaryJarFile = CanaryCageFactory.extractCanaryJarToFile(privateWorkingDirectory, canaryJarResourceLocation);
        CanaryCage canaryCage = new CanaryCage(environment, canaryJarFile, mainClassFullname);
        log.info("Successfully initialised canary cage for {}", (Object)canaryJarResourceLocation);
        return canaryCage;
    }

    private static Path extractCanaryJarToFile(Path privateWorkingDirectory, String canaryJarResourceLocation) throws IOException {
        ClassLoader classLoader = CanaryCage.class.getClassLoader();
        InputStream canaryJarInputStream = classLoader.getResourceAsStream(canaryJarResourceLocation);
        if (canaryJarInputStream == null) {
            throw new IllegalStateException("Could not locate canary jar resource in classpath at " + canaryJarResourceLocation);
        }
        Path canaryJarFile = Files.createDirectories(privateWorkingDirectory, new FileAttribute[0]).resolve(canaryJarResourceLocation);
        log.info("Extracting canary JAR from from classpath at {} to disk file at {}", (Object)canaryJarResourceLocation, (Object)canaryJarFile.toAbsolutePath());
        try {
            Path path;
            block10: {
                OutputStream outputStream = Files.newOutputStream(canaryJarFile, new OpenOption[0]);
                try {
                    IOUtils.copy((InputStream)canaryJarInputStream, (OutputStream)outputStream);
                    path = canaryJarFile;
                    if (outputStream == null) break block10;
                }
                catch (Throwable throwable) {
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                outputStream.close();
            }
            return path;
        }
        finally {
            canaryJarInputStream.close();
        }
    }
}

