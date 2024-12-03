/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.impl;

import com.atlassian.confluence.util.i18n.Message;
import com.atlassian.confluence.util.zip.FileUnzipper;
import com.atlassian.confluence.util.zip.Unzipper;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

public class UnexpectedImportZipFileContents
extends Throwable {
    private final String missingFile;
    private final File exportZip;
    private final Unzipper unzipper;

    public UnexpectedImportZipFileContents(String name, File exportZip) {
        this.missingFile = name;
        this.exportZip = exportZip;
        this.unzipper = null;
    }

    public UnexpectedImportZipFileContents(String name, Unzipper unzipper) {
        this.missingFile = name;
        this.unzipper = unzipper;
        this.exportZip = null;
    }

    @Override
    public String getMessage() {
        Object entrySpecificMessage;
        try {
            ZipEntry[] zipEntries;
            ZipEntry[] zipEntryArray = zipEntries = this.unzipper != null ? this.unzipper.entries() : new FileUnzipper(this.exportZip, null).entries();
            entrySpecificMessage = zipEntries.length > 0 ? "It contained: " + UnexpectedImportZipFileContents.entriesToFileNames(zipEntries) : "It did not contain any files, or was not a valid zip file.";
        }
        catch (IOException e) {
            entrySpecificMessage = "Error determining contents of zip file:" + e.getMessage();
        }
        return "The zip file did not contain an entry '" + this.missingFile + "'. " + (String)entrySpecificMessage;
    }

    public Message getI18nMessage() {
        try {
            ZipEntry[] zipEntries;
            ZipEntry[] zipEntryArray = zipEntries = this.unzipper != null ? this.unzipper.entries() : new FileUnzipper(this.exportZip, null).entries();
            if (zipEntries.length > 0) {
                String fileNamesArg = UnexpectedImportZipFileContents.entriesToFileNames(zipEntries);
                return Message.getInstance("error.restore.zip.invalid.missing.files", this.missingFile, fileNamesArg);
            }
            return Message.getInstance("error.restore.zip.invalid.no.files", this.missingFile);
        }
        catch (IOException e) {
            return Message.getInstance("error.restore.zip.invalid.error", this.missingFile, e.getMessage());
        }
    }

    private static String entriesToFileNames(ZipEntry[] zipEntries) {
        return Arrays.stream(zipEntries).map(ZipEntry::getName).collect(Collectors.joining(", "));
    }
}

