/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.FileInfo
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 *  com.atlassian.dc.filestore.api.FileStore$Path
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  org.apache.commons.io.FilenameUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore;

import com.atlassian.confluence.api.model.backuprestore.FileInfo;
import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.dc.filestore.api.FileStore;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.CallSite;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.zip.ZipFile;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackupRestoreFilesystemManager {
    private static final Logger log = LoggerFactory.getLogger(BackupRestoreFilesystemManager.class);
    public static final String RESTORE = "restore";
    public static final String SITE = "site";
    public static final String SPACE = "space";
    private static final String FILE_NAME_SEPARATOR = "-";
    private static final String SPACES = "spaces";
    private static final String XML_EXTENSION = "xml";
    private static final String ZIP_EXTENSION = "zip";
    static final int MAX_SPACE_KEY_LENGTH = 10;
    static final int MAX_PREFIX_LENGTH = 100;
    static final String TIMESTAMP_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    static final String SPACE_BACKUP_FILENAME_PREFIX = System.getProperty("confluence.backup.space-backup-filename-prefix", "Confluence-space-export");
    static final String SITE_BACKUP_FILENAME_PREFIX = System.getProperty("confluence.backup.site-backup-filename-prefix", "Confluence-site-export");
    static final String ILLEGAL_CHARACTERS = "/?<>\\:*|";
    static final String ENDS_IN_ZIP = "^.*[zZ][iI][pP]$";
    private final FilesystemPath confluenceHome;
    private final FilesystemPath localHome;

    public BackupRestoreFilesystemManager(FilesystemPath confluenceHome, FilesystemPath localHome) {
        this.confluenceHome = confluenceHome;
        this.localHome = localHome;
    }

    private String getCreationTime(File file) throws IllegalStateException {
        try {
            FileTime creationFileTime = (FileTime)Files.getAttribute(file.getAbsoluteFile().toPath(), "creationTime", new LinkOption[0]);
            return creationFileTime.toInstant().toString();
        }
        catch (IOException e) {
            throw new IllegalStateException("Cannot access file attributes for file: " + file.getName(), e);
        }
    }

    public List<FileInfo> getFiles(JobScope jobScope) {
        ArrayList<FileInfo> fileInfoList = new ArrayList<FileInfo>();
        ArrayList<JobScope> scopes = new ArrayList<JobScope>();
        if (jobScope == null) {
            scopes.add(JobScope.SPACE);
            scopes.add(JobScope.SITE);
        } else {
            scopes.add(jobScope);
        }
        for (JobScope scope : scopes) {
            File backupsPath = this.confluenceHome.path(new String[]{RESTORE, scope.toString().toLowerCase()}).asJavaFile();
            File[] backupFiles = backupsPath.listFiles();
            if (backupFiles == null) continue;
            for (File file : backupFiles) {
                if (file.isDirectory() || !file.getName().endsWith(".zip")) continue;
                fileInfoList.add(new FileInfo(file.getName(), this.getCreationTime(file), file.length(), scope));
            }
        }
        return fileInfoList;
    }

    public FilesystemPath writeFileLocally(JobScope jobScope, String fileName, Supplier<InputStream> inputStreamSupplier) throws IOException {
        String fileNameToWrite = this.getFileNameToWrite(jobScope, fileName);
        FilesystemPath fileNamePath = this.getRestoreWorkingDirPath(jobScope).path(new String[]{fileNameToWrite});
        fileNamePath.fileWriter().write(inputStreamSupplier.get());
        log.debug("Wrote the uploaded file to {}", (Object)fileNamePath);
        return fileNamePath;
    }

    String getFileNameToWrite(JobScope jobScope, String fileName) {
        String fileNameWithoutExtension = FilenameUtils.getBaseName((String)fileName);
        String fileNameExtension = FilenameUtils.getExtension((String)fileName);
        return this.getFileNameToWrite(jobScope, fileNameWithoutExtension, fileNameExtension);
    }

    private String getFileNameToWrite(JobScope jobScope, String fileName, String extension) {
        boolean fileAlreadyPresent;
        AtomicReference<CallSite> newFileName = new AtomicReference<CallSite>((CallSite)((Object)(fileName + "." + extension)));
        int suffixCounter = 1;
        do {
            if (!(fileAlreadyPresent = this.isFileAlreadyPresent(jobScope, (String)((Object)newFileName.get())))) continue;
            newFileName.set((CallSite)((Object)(fileName + FILE_NAME_SEPARATOR + suffixCounter++ + "." + extension)));
        } while (fileAlreadyPresent);
        return (String)((Object)newFileName.get());
    }

    private boolean isFileAlreadyPresent(JobScope jobScope, String newFileName) {
        return this.getFiles(jobScope).stream().anyMatch(x -> x.getName().equalsIgnoreCase(newFileName));
    }

    public String getRestoreWorkingDir(JobScope jobScope) {
        return this.getRestoreWorkingDirPath(jobScope).toString();
    }

    private FilesystemPath getRestoreWorkingDirPath(JobScope jobScope) {
        if (JobScope.SITE == jobScope) {
            return this.confluenceHome.path(new String[]{RESTORE}).path(new String[]{SITE});
        }
        if (JobScope.SPACE == jobScope) {
            return this.confluenceHome.path(new String[]{RESTORE}).path(new String[]{SPACE});
        }
        throw new IllegalStateException(String.format("Unsupported job scope %s", jobScope));
    }

    public boolean isValidFilename(String filename) {
        if (StringUtils.isEmpty((CharSequence)filename)) {
            return false;
        }
        return filename.length() <= 255 && !StringUtils.containsAny((CharSequence)filename, (CharSequence)ILLEGAL_CHARACTERS) && filename.matches(ENDS_IN_ZIP);
    }

    public void validateZipFile(File file) {
        try {
            ZipFile ignored = new ZipFile(file);
            ignored.close();
        }
        catch (IOException e) {
            try {
                Files.delete(file.toPath());
            }
            catch (IOException ex) {
                log.error("Cannot delete the invalid zip file that was uploaded {}. Remember to clean it up manually.", (Object)file.getPath(), (Object)ex);
            }
            throw new IllegalArgumentException("Not a valid ZIP file");
        }
    }

    public File getFile(String fileName, JobScope jobScope) throws FileNotFoundException {
        File file = this.getRestoreWorkingDirPath(jobScope).path(new String[]{fileName}).asJavaFile();
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + file.getName());
        }
        return file;
    }

    public Long getFileSize(String fileName, JobScope jobScope) {
        try {
            return this.getFile(fileName, jobScope).length();
        }
        catch (FileNotFoundException e) {
            return null;
        }
    }

    public String moveExistingLocalFileToRestoreDir(File fileToMove, JobScope jobScope) throws BackupRestoreException {
        String fileNameToWrite = this.getFileNameToWrite(jobScope, fileToMove.getName());
        FilesystemPath destinationFilePath = this.getRestoreWorkingDirPath(jobScope).path(new String[]{fileNameToWrite});
        try {
            this.localHome.path(new String[]{fileToMove.getPath()}).moveFile((FileStore.Path)destinationFilePath);
            return fileNameToWrite;
        }
        catch (IOException e) {
            throw new BackupRestoreException(String.format("Moving the backup zip to the restore directory failed, however the backup was successful. The complete backup zip is located on the server at %s", fileToMove.getAbsolutePath()), e);
        }
    }

    public void deleteZipFile(String filePath, JobScope jobScope) throws IOException {
        FilesystemPath fileToDelete = this.getRestoreWorkingDirPath(jobScope).path(new String[]{filePath});
        try {
            fileToDelete.deleteFile();
        }
        catch (FileNotFoundException | NoSuchFileException ex) {
            log.trace("File {} was not found. Assume it is already deleted.", (Object)fileToDelete, (Object)ex);
        }
    }

    public String generateSpaceBackupFileName(String prefix, Set<String> spaceKeys, Supplier<LocalDateTime> localDateTimeSupplier) {
        StringBuilder fileNameBuilder = new StringBuilder();
        if (!StringUtils.isBlank((CharSequence)prefix)) {
            fileNameBuilder.append(StringUtils.truncate((String)prefix, (int)100));
            fileNameBuilder.append(FILE_NAME_SEPARATOR);
        }
        fileNameBuilder.append(BackupRestoreFilesystemManager.getBackedUpSpaceInfo(spaceKeys));
        fileNameBuilder.append(FILE_NAME_SEPARATOR);
        fileNameBuilder.append(localDateTimeSupplier.get().format(DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT)));
        return this.getFileNameToWrite(JobScope.SPACE, fileNameBuilder.toString(), "xml.zip");
    }

    public String generateSiteBackupFileName(String prefix, Supplier<LocalDateTime> localDateTimeSupplier) {
        StringBuilder fileNameBuilder = new StringBuilder();
        if (!StringUtils.isBlank((CharSequence)prefix)) {
            fileNameBuilder.append(StringUtils.truncate((String)prefix, (int)100));
            fileNameBuilder.append(FILE_NAME_SEPARATOR);
        }
        fileNameBuilder.append(localDateTimeSupplier.get().format(DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT)));
        return this.getFileNameToWrite(JobScope.SITE, fileNameBuilder.toString(), "xml.zip");
    }

    private static String getBackedUpSpaceInfo(Set<String> spaceKeys) {
        if (spaceKeys.size() == 1 && spaceKeys.iterator().next().length() <= 10) {
            return spaceKeys.iterator().next();
        }
        return spaceKeys.size() + "-spaces";
    }
}

