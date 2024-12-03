/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  com.google.common.base.Charsets
 *  com.google.common.io.Files
 *  org.apache.commons.io.FileUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.springframework.dao.DataAccessException
 *  org.springframework.dao.DataAccessResourceFailureException
 */
package com.atlassian.confluence.impl.journal;

import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.impl.journal.JournalStateStore;
import com.atlassian.confluence.setup.BootstrapManager;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.io.FileUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;

public class FilesystemJournalStateStore
implements JournalStateStore {
    private final File journalDir;

    public FilesystemJournalStateStore(BootstrapManager bootstrapManager) {
        this.journalDir = new File(bootstrapManager.getLocalHome(), "journal");
        this.journalDir.mkdir();
        if (!this.journalDir.isDirectory()) {
            throw new IllegalStateException("Failed to create directory '" + this.journalDir.getPath() + "'");
        }
    }

    @Override
    public long getMostRecentId(@NonNull JournalIdentifier journalId) throws DataAccessException {
        File journalFile = this.journalFile(journalId);
        if (!journalFile.exists()) {
            return 0L;
        }
        try {
            String firstLine = Files.readFirstLine((File)journalFile, (Charset)Charsets.UTF_8);
            return Long.parseLong(firstLine);
        }
        catch (Exception e) {
            throw new DataAccessResourceFailureException("Failed to read id for journal '" + journalId.getJournalName() + "': " + e.getMessage(), (Throwable)e);
        }
    }

    @Override
    public void setMostRecentId(@NonNull JournalIdentifier journalId, long id) throws DataAccessException {
        try {
            File tmpFile = File.createTempFile(journalId.getJournalName() + "-", ".tmp", this.journalDir);
            Files.write((CharSequence)Long.toString(id), (File)tmpFile, (Charset)Charsets.UTF_8);
            File journalFile = this.journalFile(journalId);
            Files.move((File)tmpFile, (File)journalFile);
        }
        catch (IOException e) {
            throw new DataAccessResourceFailureException("Failed to write id " + id + " for journal '" + journalId.getJournalName() + "': " + e.getMessage(), (Throwable)e);
        }
    }

    @Override
    public void resetAllJournalStates() throws DataAccessException {
        try {
            FileUtils.cleanDirectory((File)this.journalDir);
        }
        catch (IOException e) {
            throw new DataAccessResourceFailureException("Failed to reset all journal states: " + e.getMessage(), (Throwable)e);
        }
    }

    private File journalFile(@NonNull JournalIdentifier journalId) {
        return new File(this.journalDir, journalId.getJournalName());
    }
}

