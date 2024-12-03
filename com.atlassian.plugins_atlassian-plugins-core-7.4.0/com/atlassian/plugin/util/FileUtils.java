/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.util;

import com.atlassian.plugin.util.zip.UrlUnzipper;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {
    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    public static void conditionallyExtractZipFile(URL zipUrl, File destDir) {
        try {
            UrlUnzipper unzipper = new UrlUnzipper(zipUrl, destDir);
            unzipper.conditionalUnzip();
        }
        catch (IOException e) {
            log.error("Found {}, but failed to read file", (Object)zipUrl, (Object)e);
        }
    }
}

