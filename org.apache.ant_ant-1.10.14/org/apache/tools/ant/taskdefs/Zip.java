/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import java.util.stream.Stream;
import java.util.zip.CRC32;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.ArchiveFileSet;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.ResourceFactory;
import org.apache.tools.ant.types.ZipFileSet;
import org.apache.tools.ant.types.ZipScanner;
import org.apache.tools.ant.types.resources.ArchiveResource;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.types.resources.ZipResource;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;
import org.apache.tools.ant.util.DateUtils;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.GlobPatternMapper;
import org.apache.tools.ant.util.IdentityMapper;
import org.apache.tools.ant.util.MergingMapper;
import org.apache.tools.ant.util.ResourceUtils;
import org.apache.tools.zip.Zip64Mode;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipExtraField;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

public class Zip
extends MatchingTask {
    private static final int BUFFER_SIZE = 8192;
    private static final int ZIP_FILE_TIMESTAMP_GRANULARITY = 2000;
    private static final int ROUNDUP_MILLIS = 1999;
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private static final long EMPTY_CRC = new CRC32().getValue();
    private static final ResourceSelector MISSING_SELECTOR = target -> !target.isExists();
    private static final ResourceUtils.ResourceSelectorProvider MISSING_DIR_PROVIDER = sr -> MISSING_SELECTOR;
    protected File zipFile;
    private ZipScanner zs;
    private File baseDir;
    protected Hashtable<String, String> entries = new Hashtable();
    private final List<FileSet> groupfilesets = new Vector<FileSet>();
    private final List<ZipFileSet> filesetsFromGroupfilesets = new Vector<ZipFileSet>();
    protected String duplicate = "add";
    private boolean doCompress = true;
    private boolean doUpdate = false;
    private boolean savedDoUpdate = false;
    private boolean doFilesonly = false;
    protected String archiveType = "zip";
    protected String emptyBehavior = "skip";
    private final List<ResourceCollection> resources = new Vector<ResourceCollection>();
    protected Hashtable<String, String> addedDirs = new Hashtable();
    private final List<String> addedFiles = new Vector<String>();
    private String fixedModTime = null;
    private long modTimeMillis = 0L;
    protected boolean doubleFilePass = false;
    protected boolean skipWriting = false;
    private boolean updatedFile = false;
    private boolean addingNewFiles = false;
    private String encoding;
    private boolean keepCompression = false;
    private boolean roundUp = true;
    private String comment = "";
    private int level = -1;
    private boolean preserve0Permissions = false;
    private boolean useLanguageEncodingFlag = true;
    private UnicodeExtraField createUnicodeExtraFields = UnicodeExtraField.NEVER;
    private boolean fallBackToUTF8 = false;
    private Zip64ModeAttribute zip64Mode = Zip64ModeAttribute.AS_NEEDED;
    private static final ThreadLocal<Boolean> HAVE_NON_FILE_SET_RESOURCES_TO_ADD = ThreadLocal.withInitial(() -> Boolean.FALSE);
    private static final ThreadLocal<ZipExtraField[]> CURRENT_ZIP_EXTRA = new ThreadLocal();

    protected final boolean isFirstPass() {
        return !this.doubleFilePass || this.skipWriting;
    }

    @Deprecated
    public void setZipfile(File zipFile) {
        this.setDestFile(zipFile);
    }

    @Deprecated
    public void setFile(File file) {
        this.setDestFile(file);
    }

    public void setDestFile(File destFile) {
        this.zipFile = destFile;
    }

    public File getDestFile() {
        return this.zipFile;
    }

    public void setBasedir(File baseDir) {
        this.baseDir = baseDir;
    }

    public void setCompress(boolean c) {
        this.doCompress = c;
    }

    public boolean isCompress() {
        return this.doCompress;
    }

    public void setFilesonly(boolean f) {
        this.doFilesonly = f;
    }

    public void setUpdate(boolean c) {
        this.doUpdate = c;
        this.savedDoUpdate = c;
    }

    public boolean isInUpdateMode() {
        return this.doUpdate;
    }

    public void addFileset(FileSet set) {
        this.add(set);
    }

    public void addZipfileset(ZipFileSet set) {
        this.add(set);
    }

    public void add(ResourceCollection a) {
        this.resources.add(a);
    }

    public void addZipGroupFileset(FileSet set) {
        this.groupfilesets.add(set);
    }

    public void setDuplicate(Duplicate df) {
        this.duplicate = df.getValue();
    }

    public void setWhenempty(WhenEmpty we) {
        this.emptyBehavior = we.getValue();
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setKeepCompression(boolean keep) {
        this.keepCompression = keep;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return this.comment;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return this.level;
    }

    public void setRoundUp(boolean r) {
        this.roundUp = r;
    }

    public void setPreserve0Permissions(boolean b) {
        this.preserve0Permissions = b;
    }

    public boolean getPreserve0Permissions() {
        return this.preserve0Permissions;
    }

    public void setUseLanguageEncodingFlag(boolean b) {
        this.useLanguageEncodingFlag = b;
    }

    public boolean getUseLanguageEnodingFlag() {
        return this.useLanguageEncodingFlag;
    }

    public void setCreateUnicodeExtraFields(UnicodeExtraField b) {
        this.createUnicodeExtraFields = b;
    }

    public UnicodeExtraField getCreateUnicodeExtraFields() {
        return this.createUnicodeExtraFields;
    }

    public void setFallBackToUTF8(boolean b) {
        this.fallBackToUTF8 = b;
    }

    public boolean getFallBackToUTF8() {
        return this.fallBackToUTF8;
    }

    public void setZip64Mode(Zip64ModeAttribute b) {
        this.zip64Mode = b;
    }

    public Zip64ModeAttribute getZip64Mode() {
        return this.zip64Mode;
    }

    public void setModificationtime(String time) {
        this.fixedModTime = time;
    }

    public String getModificationtime() {
        return this.fixedModTime;
    }

    @Override
    public void execute() throws BuildException {
        if (this.doubleFilePass) {
            this.skipWriting = true;
            this.executeMain();
            this.skipWriting = false;
        }
        this.executeMain();
    }

    protected boolean hasUpdatedFile() {
        return this.updatedFile;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void executeMain() throws BuildException {
        this.checkAttributesAndElements();
        File renamedFile = null;
        this.addingNewFiles = true;
        this.processDoUpdate();
        this.processGroupFilesets();
        ArrayList<ResourceCollection> vfss = new ArrayList<ResourceCollection>();
        if (this.baseDir != null) {
            FileSet fs = (FileSet)this.getImplicitFileSet().clone();
            fs.setDir(this.baseDir);
            vfss.add(fs);
        }
        vfss.addAll(this.resources);
        ResourceCollection[] fss = vfss.toArray(new ResourceCollection[0]);
        boolean success = false;
        try {
            String action;
            ArchiveState state = this.getResourcesToAdd(fss, this.zipFile, false);
            if (!state.isOutOfDate()) {
                return;
            }
            File parent = this.zipFile.getParentFile();
            if (!(parent == null || parent.isDirectory() || parent.mkdirs() || parent.isDirectory())) {
                throw new BuildException("Failed to create missing parent directory for %s", this.zipFile);
            }
            this.updatedFile = true;
            if (!this.zipFile.exists() && state.isWithoutAnyResources()) {
                this.createEmptyZip(this.zipFile);
                return;
            }
            Resource[][] addThem = state.getResourcesToAdd();
            if (this.doUpdate) {
                renamedFile = this.renameFile();
            }
            String string = action = this.doUpdate ? "Updating " : "Building ";
            if (!this.skipWriting) {
                this.log(action + this.archiveType + ": " + this.zipFile.getAbsolutePath());
            }
            ZipOutputStream zOut = null;
            try {
                if (!this.skipWriting) {
                    zOut = new ZipOutputStream(this.zipFile);
                    zOut.setEncoding(this.encoding);
                    zOut.setUseLanguageEncodingFlag(this.useLanguageEncodingFlag);
                    zOut.setCreateUnicodeExtraFields(this.createUnicodeExtraFields.getPolicy());
                    zOut.setFallbackToUTF8(this.fallBackToUTF8);
                    zOut.setMethod(this.doCompress ? 8 : 0);
                    zOut.setLevel(this.level);
                    zOut.setUseZip64(this.zip64Mode.getMode());
                }
                this.initZipOutputStream(zOut);
                for (int i = 0; i < fss.length; ++i) {
                    if (addThem[i].length == 0) continue;
                    this.addResources(fss[i], addThem[i], zOut);
                }
                if (this.doUpdate) {
                    this.addingNewFiles = false;
                    ZipFileSet oldFiles = new ZipFileSet();
                    oldFiles.setProject(this.getProject());
                    oldFiles.setSrc(renamedFile);
                    oldFiles.setDefaultexcludes(false);
                    for (String addedFile : this.addedFiles) {
                        oldFiles.createExclude().setName(addedFile);
                    }
                    DirectoryScanner ds = oldFiles.getDirectoryScanner(this.getProject());
                    ((ZipScanner)ds).setEncoding(this.encoding);
                    Stream<String> includedResourceNames = Stream.of(ds.getIncludedFiles());
                    if (!this.doFilesonly) {
                        includedResourceNames = Stream.concat(includedResourceNames, Stream.of(ds.getIncludedDirectories()));
                    }
                    Resource[] r = (Resource[])includedResourceNames.map(ds::getResource).toArray(Resource[]::new);
                    this.addResources(oldFiles, r, zOut);
                }
                if (zOut != null) {
                    zOut.setComment(this.comment);
                }
                this.finalizeZipOutputStream(zOut);
                if (this.doUpdate && !renamedFile.delete()) {
                    this.log("Warning: unable to delete temporary file " + renamedFile.getName(), 1);
                }
                success = true;
            }
            catch (Throwable throwable) {
                this.closeZout(zOut, success);
                throw throwable;
            }
            this.closeZout(zOut, success);
        }
        catch (IOException ioe) {
            String msg = "Problem creating " + this.archiveType + ": " + ioe.getMessage();
            if (!(this.doUpdate && renamedFile == null || this.zipFile.delete())) {
                msg = msg + " (and the archive is probably corrupt but I could not delete it)";
            }
            if (this.doUpdate && renamedFile != null) {
                try {
                    FILE_UTILS.rename(renamedFile, this.zipFile);
                }
                catch (IOException e) {
                    msg = msg + " (and I couldn't rename the temporary file " + renamedFile.getName() + " back)";
                }
            }
            throw new BuildException(msg, ioe, this.getLocation());
        }
        finally {
            this.cleanUp();
        }
    }

    private File renameFile() {
        File renamedFile = FILE_UTILS.createTempFile(this.getProject(), "zip", ".tmp", this.zipFile.getParentFile(), true, false);
        try {
            FILE_UTILS.rename(this.zipFile, renamedFile);
        }
        catch (IOException | SecurityException e) {
            throw new BuildException("Unable to rename old file (%s) to temporary file", this.zipFile.getAbsolutePath());
        }
        return renamedFile;
    }

    private void closeZout(ZipOutputStream zOut, boolean success) throws IOException {
        block3: {
            if (zOut == null) {
                return;
            }
            try {
                zOut.close();
            }
            catch (IOException ex) {
                if (!success) break block3;
                throw ex;
            }
        }
    }

    private void checkAttributesAndElements() {
        if (this.baseDir == null && this.resources.isEmpty() && this.groupfilesets.isEmpty() && "zip".equals(this.archiveType)) {
            throw new BuildException("basedir attribute must be set, or at least one resource collection must be given!");
        }
        if (this.zipFile == null) {
            throw new BuildException("You must specify the %s file to create!", this.archiveType);
        }
        if (this.fixedModTime != null) {
            try {
                this.modTimeMillis = DateUtils.parseLenientDateTime(this.fixedModTime).getTime();
            }
            catch (ParseException pe) {
                throw new BuildException("Failed to parse date string %s.", this.fixedModTime);
            }
            if (this.roundUp) {
                this.modTimeMillis += 1999L;
            }
        }
        if (this.zipFile.exists() && !this.zipFile.isFile()) {
            throw new BuildException("%s is not a file.", this.zipFile);
        }
        if (this.zipFile.exists() && !this.zipFile.canWrite()) {
            throw new BuildException("%s is read-only.", this.zipFile);
        }
    }

    private void processDoUpdate() {
        if (this.doUpdate && !this.zipFile.exists()) {
            this.doUpdate = false;
            this.logWhenWriting("ignoring update attribute as " + this.archiveType + " doesn't exist.", 4);
        }
    }

    private void processGroupFilesets() {
        for (FileSet fs : this.groupfilesets) {
            this.logWhenWriting("Processing groupfileset ", 3);
            DirectoryScanner scanner = fs.getDirectoryScanner(this.getProject());
            File basedir = scanner.getBasedir();
            for (String file : scanner.getIncludedFiles()) {
                this.logWhenWriting("Adding file " + file + " to fileset", 3);
                ZipFileSet zf = new ZipFileSet();
                zf.setProject(this.getProject());
                zf.setSrc(new File(basedir, file));
                this.add(zf);
                this.filesetsFromGroupfilesets.add(zf);
            }
        }
    }

    protected final boolean isAddingNewFiles() {
        return this.addingNewFiles;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final void addResources(FileSet fileset, Resource[] resources, ZipOutputStream zOut) throws IOException {
        String prefix = "";
        String fullpath = "";
        int dirMode = 16877;
        int fileMode = 33188;
        ArchiveFileSet zfs = null;
        if (fileset instanceof ArchiveFileSet) {
            zfs = (ArchiveFileSet)fileset;
            prefix = zfs.getPrefix(this.getProject());
            fullpath = zfs.getFullpath(this.getProject());
            dirMode = zfs.getDirMode(this.getProject());
            fileMode = zfs.getFileMode(this.getProject());
        }
        if (!prefix.isEmpty() && !fullpath.isEmpty()) {
            throw new BuildException("Both prefix and fullpath attributes must not be set on the same fileset.");
        }
        if (resources.length != 1 && !fullpath.isEmpty()) {
            throw new BuildException("fullpath attribute may only be specified for filesets that specify a single file.");
        }
        if (!prefix.isEmpty()) {
            if (!prefix.endsWith("/") && !prefix.endsWith("\\")) {
                prefix = prefix + "/";
            }
            this.addParentDirs(null, prefix, zOut, "", dirMode);
        }
        try (ZipFile zf = null;){
            boolean dealingWithFiles = false;
            File base = null;
            if (zfs == null || zfs.getSrc(this.getProject()) == null) {
                dealingWithFiles = true;
                base = fileset.getDir(this.getProject());
            } else if (zfs instanceof ZipFileSet) {
                zf = new ZipFile(zfs.getSrc(this.getProject()), this.encoding);
            }
            for (Resource resource : resources) {
                String name = fullpath.isEmpty() ? resource.getName() : fullpath;
                if ((name = name.replace(File.separatorChar, '/')).isEmpty()) continue;
                if (resource.isDirectory()) {
                    if (this.doFilesonly) continue;
                    int thisDirMode = zfs != null && zfs.hasDirModeBeenSet() ? dirMode : this.getUnixMode(resource, zf, dirMode);
                    this.addDirectoryResource(resource, name, prefix, base, zOut, dirMode, thisDirMode);
                    continue;
                }
                this.addParentDirs(base, name, zOut, prefix, dirMode);
                if (dealingWithFiles) {
                    File f = FILE_UTILS.resolveFile(base, resource.getName());
                    this.zipFile(f, zOut, prefix + name, fileMode);
                    continue;
                }
                int thisFileMode = zfs != null && zfs.hasFileModeBeenSet() ? fileMode : this.getUnixMode(resource, zf, fileMode);
                this.addResource(resource, name, prefix, zOut, thisFileMode, zf, zfs == null ? null : zfs.getSrc(this.getProject()));
            }
        }
    }

    private void addDirectoryResource(Resource r, String name, String prefix, File base, ZipOutputStream zOut, int defaultDirMode, int thisDirMode) throws IOException {
        int nextToLastSlash;
        if (!name.endsWith("/")) {
            name = name + "/";
        }
        if ((nextToLastSlash = name.lastIndexOf(47, name.length() - 2)) != -1) {
            this.addParentDirs(base, name.substring(0, nextToLastSlash + 1), zOut, prefix, defaultDirMode);
        }
        this.zipDir(r, zOut, prefix + name, thisDirMode, r instanceof ZipResource ? ((ZipResource)r).getExtraFields() : null);
    }

    private int getUnixMode(Resource r, ZipFile zf, int defaultMode) {
        int unixMode = defaultMode;
        if (zf != null) {
            ZipEntry ze = zf.getEntry(r.getName());
            unixMode = ze.getUnixMode();
            if (!(unixMode != 0 && unixMode != 16384 || this.preserve0Permissions)) {
                unixMode = defaultMode;
            }
        } else if (r instanceof ArchiveResource) {
            unixMode = ((ArchiveResource)r).getMode();
        }
        return unixMode;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addResource(Resource r, String name, String prefix, ZipOutputStream zOut, int mode, ZipFile zf, File fromArchive) throws IOException {
        if (zf != null) {
            ZipEntry ze = zf.getEntry(r.getName());
            if (ze != null) {
                boolean oldCompress = this.doCompress;
                if (this.keepCompression) {
                    this.doCompress = ze.getMethod() == 8;
                }
                try (BufferedInputStream is = new BufferedInputStream(zf.getInputStream(ze));){
                    this.zipFile(is, zOut, prefix + name, ze.getTime(), fromArchive, mode, ze.getExtraFields(true));
                }
                finally {
                    this.doCompress = oldCompress;
                }
            }
        } else {
            try (BufferedInputStream is = new BufferedInputStream(r.getInputStream());){
                this.zipFile(is, zOut, prefix + name, r.getLastModified(), fromArchive, mode, r instanceof ZipResource ? ((ZipResource)r).getExtraFields() : null);
            }
        }
    }

    protected final void addResources(ResourceCollection rc, Resource[] resources, ZipOutputStream zOut) throws IOException {
        if (rc instanceof FileSet) {
            this.addResources((FileSet)rc, resources, zOut);
            return;
        }
        for (Resource resource : resources) {
            String name = resource.getName();
            if (name == null || (name = name.replace(File.separatorChar, '/')).isEmpty() || resource.isDirectory() && this.doFilesonly) continue;
            File base = null;
            FileProvider fp = resource.as(FileProvider.class);
            if (fp != null) {
                base = ResourceUtils.asFileResource(fp).getBaseDir();
            }
            if (resource.isDirectory()) {
                this.addDirectoryResource(resource, name, "", base, zOut, 16877, 16877);
                continue;
            }
            this.addParentDirs(base, name, zOut, "", 16877);
            if (fp != null) {
                File f = fp.getFile();
                this.zipFile(f, zOut, name, 33188);
                continue;
            }
            this.addResource(resource, name, "", zOut, 33188, null, null);
        }
    }

    protected void initZipOutputStream(ZipOutputStream zOut) throws IOException, BuildException {
    }

    protected void finalizeZipOutputStream(ZipOutputStream zOut) throws IOException, BuildException {
    }

    protected boolean createEmptyZip(File zipFile) throws BuildException {
        if (!this.skipWriting) {
            this.log("Note: creating empty " + this.archiveType + " archive " + zipFile, 2);
        }
        try (OutputStream os = Files.newOutputStream(zipFile.toPath(), new OpenOption[0]);){
            byte[] empty = new byte[22];
            empty[0] = 80;
            empty[1] = 75;
            empty[2] = 5;
            empty[3] = 6;
            os.write(empty);
        }
        catch (IOException ioe) {
            throw new BuildException("Could not create empty ZIP archive (" + ioe.getMessage() + ")", ioe, this.getLocation());
        }
        return true;
    }

    private synchronized ZipScanner getZipScanner() {
        if (this.zs == null) {
            this.zs = new ZipScanner();
            this.zs.setEncoding(this.encoding);
            this.zs.setSrc(this.zipFile);
        }
        return this.zs;
    }

    protected ArchiveState getResourcesToAdd(ResourceCollection[] rcs, File zipFile, boolean needsUpdate) throws BuildException {
        ArrayList<FileSet> filesets = new ArrayList<FileSet>();
        ArrayList<ResourceCollection> rest = new ArrayList<ResourceCollection>();
        for (ResourceCollection rc : rcs) {
            if (rc instanceof FileSet) {
                filesets.add((FileSet)rc);
                continue;
            }
            rest.add(rc);
        }
        ResourceCollection[] rc = rest.toArray(new ResourceCollection[0]);
        ArchiveState as = this.getNonFileSetResourcesToAdd(rc, zipFile, needsUpdate);
        FileSet[] fs = filesets.toArray(new FileSet[0]);
        ArchiveState as2 = this.getResourcesToAdd(fs, zipFile, as.isOutOfDate());
        if (!as.isOutOfDate() && as2.isOutOfDate()) {
            as = this.getNonFileSetResourcesToAdd(rc, zipFile, true);
        }
        Resource[][] toAdd = new Resource[rcs.length][];
        int fsIndex = 0;
        int restIndex = 0;
        for (int i = 0; i < rcs.length; ++i) {
            toAdd[i] = rcs[i] instanceof FileSet ? as2.getResourcesToAdd()[fsIndex++] : as.getResourcesToAdd()[restIndex++];
        }
        return new ArchiveState(as2.isOutOfDate(), toAdd);
    }

    protected ArchiveState getResourcesToAdd(FileSet[] filesets, File zipFile, boolean needsUpdate) throws BuildException {
        int i;
        Resource[][] initialResources = this.grabResources(filesets);
        if (Zip.isEmpty(initialResources)) {
            if (Boolean.FALSE.equals(HAVE_NON_FILE_SET_RESOURCES_TO_ADD.get())) {
                if (needsUpdate && this.doUpdate) {
                    return new ArchiveState(true, initialResources);
                }
                if ("skip".equals(this.emptyBehavior)) {
                    if (this.doUpdate) {
                        this.logWhenWriting(this.archiveType + " archive " + zipFile + " not updated because no new files were included.", 3);
                    } else {
                        this.logWhenWriting("Warning: skipping " + this.archiveType + " archive " + zipFile + " because no files were included.", 1);
                    }
                } else {
                    if ("fail".equals(this.emptyBehavior)) {
                        throw new BuildException("Cannot create " + this.archiveType + " archive " + zipFile + ": no files were included.", this.getLocation());
                    }
                    if (!zipFile.exists()) {
                        needsUpdate = true;
                    }
                }
            }
            return new ArchiveState(needsUpdate, initialResources);
        }
        if (!zipFile.exists()) {
            return new ArchiveState(true, initialResources);
        }
        if (needsUpdate && !this.doUpdate) {
            return new ArchiveState(true, initialResources);
        }
        Resource[][] newerResources = new Resource[filesets.length][];
        for (i = 0; i < filesets.length; ++i) {
            if (this.fileset instanceof ZipFileSet && ((ZipFileSet)this.fileset).getSrc(this.getProject()) != null) continue;
            File base = filesets[i].getDir(this.getProject());
            for (int j = 0; j < initialResources[i].length; ++j) {
                File resourceAsFile = FILE_UTILS.resolveFile(base, initialResources[i][j].getName());
                if (!resourceAsFile.equals(zipFile)) continue;
                throw new BuildException("A zip file cannot include itself", this.getLocation());
            }
        }
        for (i = 0; i < filesets.length; ++i) {
            if (initialResources[i].length == 0) {
                newerResources[i] = new Resource[0];
                continue;
            }
            FileNameMapper myMapper = new IdentityMapper();
            if (filesets[i] instanceof ZipFileSet) {
                ZipFileSet zfs = (ZipFileSet)filesets[i];
                if (zfs.getFullpath(this.getProject()) != null && !zfs.getFullpath(this.getProject()).isEmpty()) {
                    MergingMapper fm = new MergingMapper();
                    fm.setTo(zfs.getFullpath(this.getProject()));
                    myMapper = fm;
                } else if (zfs.getPrefix(this.getProject()) != null && !zfs.getPrefix(this.getProject()).isEmpty()) {
                    GlobPatternMapper gm = new GlobPatternMapper();
                    gm.setFrom("*");
                    String prefix = zfs.getPrefix(this.getProject());
                    if (!prefix.endsWith("/") && !prefix.endsWith("\\")) {
                        prefix = prefix + "/";
                    }
                    gm.setTo(prefix + "*");
                    myMapper = gm;
                }
            }
            newerResources[i] = this.selectOutOfDateResources(initialResources[i], myMapper);
            boolean bl = needsUpdate = needsUpdate || newerResources[i].length > 0;
            if (needsUpdate && !this.doUpdate) break;
        }
        if (needsUpdate && !this.doUpdate) {
            return new ArchiveState(true, initialResources);
        }
        return new ArchiveState(needsUpdate, newerResources);
    }

    protected ArchiveState getNonFileSetResourcesToAdd(ResourceCollection[] rcs, File zipFile, boolean needsUpdate) throws BuildException {
        Resource[][] initialResources = this.grabNonFileSetResources(rcs);
        boolean empty = Zip.isEmpty(initialResources);
        HAVE_NON_FILE_SET_RESOURCES_TO_ADD.set(!empty);
        if (empty) {
            return new ArchiveState(needsUpdate, initialResources);
        }
        if (!zipFile.exists()) {
            return new ArchiveState(true, initialResources);
        }
        if (needsUpdate && !this.doUpdate) {
            return new ArchiveState(true, initialResources);
        }
        Resource[][] newerResources = new Resource[rcs.length][];
        for (int i = 0; i < rcs.length; ++i) {
            if (initialResources[i].length == 0) {
                newerResources[i] = new Resource[0];
                continue;
            }
            for (int j = 0; j < initialResources[i].length; ++j) {
                FileProvider fp = initialResources[i][j].as(FileProvider.class);
                if (fp == null || !zipFile.equals(fp.getFile())) continue;
                throw new BuildException("A zip file cannot include itself", this.getLocation());
            }
            newerResources[i] = this.selectOutOfDateResources(initialResources[i], new IdentityMapper());
            boolean bl = needsUpdate = needsUpdate || newerResources[i].length > 0;
            if (needsUpdate && !this.doUpdate) break;
        }
        if (needsUpdate && !this.doUpdate) {
            return new ArchiveState(true, initialResources);
        }
        return new ArchiveState(needsUpdate, newerResources);
    }

    private Resource[] selectOutOfDateResources(Resource[] initial, FileNameMapper mapper) {
        Resource[] rs = this.selectFileResources(initial);
        Resource[] result = ResourceUtils.selectOutOfDateSources((ProjectComponent)this, rs, mapper, (ResourceFactory)this.getZipScanner(), 2000L);
        if (!this.doFilesonly) {
            Union u = new Union();
            u.addAll(Arrays.asList(this.selectDirectoryResources(initial)));
            ResourceCollection rc = ResourceUtils.selectSources(this, u, mapper, this.getZipScanner(), MISSING_DIR_PROVIDER);
            if (!rc.isEmpty()) {
                ArrayList<Resource> newer = new ArrayList<Resource>();
                newer.addAll(Arrays.asList(((Union)rc).listResources()));
                newer.addAll(Arrays.asList(result));
                result = newer.toArray(result);
            }
        }
        return result;
    }

    protected Resource[][] grabResources(FileSet[] filesets) {
        Resource[][] result = new Resource[filesets.length][];
        for (int i = 0; i < filesets.length; ++i) {
            DirectoryScanner rs;
            boolean skipEmptyNames = true;
            if (filesets[i] instanceof ZipFileSet) {
                ZipFileSet zfs = (ZipFileSet)filesets[i];
                boolean bl = skipEmptyNames = zfs.getPrefix(this.getProject()).isEmpty() && zfs.getFullpath(this.getProject()).isEmpty();
            }
            if ((rs = filesets[i].getDirectoryScanner(this.getProject())) instanceof ZipScanner) {
                ((ZipScanner)rs).setEncoding(this.encoding);
            }
            Vector<Resource> resources = new Vector<Resource>();
            if (!this.doFilesonly) {
                for (String d : rs.getIncludedDirectories()) {
                    if (d.isEmpty() && skipEmptyNames) continue;
                    resources.add(rs.getResource(d));
                }
            }
            for (String f : rs.getIncludedFiles()) {
                if (f.isEmpty() && skipEmptyNames) continue;
                resources.add(rs.getResource(f));
            }
            result[i] = resources.toArray(new Resource[0]);
        }
        return result;
    }

    protected Resource[][] grabNonFileSetResources(ResourceCollection[] rcs) {
        Resource[][] result = new Resource[rcs.length][];
        for (int i = 0; i < rcs.length; ++i) {
            ArrayList<Resource> dirs = new ArrayList<Resource>();
            ArrayList<Resource> files = new ArrayList<Resource>();
            for (Resource r : rcs[i]) {
                if (r.isDirectory()) {
                    dirs.add(r);
                    continue;
                }
                if (!r.isExists()) continue;
                files.add(r);
            }
            dirs.sort(Comparator.comparing(Resource::getName));
            ArrayList<Resource> rs = new ArrayList<Resource>(dirs);
            rs.addAll(files);
            result[i] = rs.toArray(new Resource[0]);
        }
        return result;
    }

    protected void zipDir(File dir, ZipOutputStream zOut, String vPath, int mode) throws IOException {
        this.zipDir(dir, zOut, vPath, mode, null);
    }

    protected void zipDir(File dir, ZipOutputStream zOut, String vPath, int mode, ZipExtraField[] extra) throws IOException {
        this.zipDir(dir == null ? null : new FileResource(dir), zOut, vPath, mode, extra);
    }

    protected void zipDir(Resource dir, ZipOutputStream zOut, String vPath, int mode, ZipExtraField[] extra) throws IOException {
        if (this.doFilesonly) {
            this.logWhenWriting("skipping directory " + vPath + " for file-only archive", 3);
            return;
        }
        if (this.addedDirs.get(vPath) != null) {
            return;
        }
        this.logWhenWriting("adding directory " + vPath, 3);
        this.addedDirs.put(vPath, vPath);
        if (!this.skipWriting) {
            int millisToAdd;
            ZipEntry ze = new ZipEntry(vPath);
            int n = millisToAdd = this.roundUp ? 1999 : 0;
            if (this.fixedModTime != null) {
                ze.setTime(this.modTimeMillis);
            } else if (dir != null && dir.isExists()) {
                ze.setTime(dir.getLastModified() + (long)millisToAdd);
            } else {
                ze.setTime(System.currentTimeMillis() + (long)millisToAdd);
            }
            ze.setSize(0L);
            ze.setMethod(0);
            ze.setCrc(EMPTY_CRC);
            ze.setUnixMode(mode);
            if (extra != null) {
                ze.setExtraFields(extra);
            }
            zOut.putNextEntry(ze);
        }
    }

    protected final ZipExtraField[] getCurrentExtraFields() {
        return CURRENT_ZIP_EXTRA.get();
    }

    protected final void setCurrentExtraFields(ZipExtraField[] extra) {
        CURRENT_ZIP_EXTRA.set(extra);
    }

    protected void zipFile(InputStream in, ZipOutputStream zOut, String vPath, long lastModified, File fromArchive, int mode) throws IOException {
        if (this.entries.containsKey(vPath)) {
            if ("preserve".equals(this.duplicate)) {
                this.logWhenWriting(vPath + " already added, skipping", 2);
                return;
            }
            if ("fail".equals(this.duplicate)) {
                throw new BuildException("Duplicate file %s was found and the duplicate attribute is 'fail'.", vPath);
            }
            this.logWhenWriting("duplicate file " + vPath + " found, adding.", 3);
        } else {
            this.logWhenWriting("adding entry " + vPath, 3);
        }
        this.entries.put(vPath, vPath);
        if (!this.skipWriting) {
            InputStream markableInputStream;
            ZipEntry ze = new ZipEntry(vPath);
            ze.setTime(this.fixedModTime != null ? this.modTimeMillis : lastModified);
            ze.setMethod(this.doCompress ? 8 : 0);
            InputStream inputStream = markableInputStream = in.markSupported() ? in : new BufferedInputStream(in);
            if (!zOut.isSeekable() && !this.doCompress) {
                long size = 0L;
                CRC32 cal = new CRC32();
                markableInputStream.mark(Integer.MAX_VALUE);
                byte[] buffer = new byte[8192];
                int count = 0;
                do {
                    size += (long)count;
                    cal.update(buffer, 0, count);
                } while ((count = markableInputStream.read(buffer, 0, buffer.length)) != -1);
                markableInputStream.reset();
                ze.setSize(size);
                ze.setCrc(cal.getValue());
            }
            ze.setUnixMode(mode);
            ZipExtraField[] extra = this.getCurrentExtraFields();
            if (extra != null) {
                ze.setExtraFields(extra);
            }
            zOut.putNextEntry(ze);
            byte[] buffer = new byte[8192];
            int count = 0;
            do {
                if (count == 0) continue;
                zOut.write(buffer, 0, count);
            } while ((count = markableInputStream.read(buffer, 0, buffer.length)) != -1);
        }
        this.addedFiles.add(vPath);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final void zipFile(InputStream in, ZipOutputStream zOut, String vPath, long lastModified, File fromArchive, int mode, ZipExtraField[] extra) throws IOException {
        try {
            this.setCurrentExtraFields(extra);
            this.zipFile(in, zOut, vPath, lastModified, fromArchive, mode);
        }
        finally {
            this.setCurrentExtraFields(null);
        }
    }

    protected void zipFile(File file, ZipOutputStream zOut, String vPath, int mode) throws IOException {
        if (file.equals(this.zipFile)) {
            throw new BuildException("A zip file cannot include itself", this.getLocation());
        }
        try (BufferedInputStream bIn = new BufferedInputStream(Files.newInputStream(file.toPath(), new OpenOption[0]));){
            this.zipFile(bIn, zOut, vPath, file.lastModified() + (long)(this.roundUp ? 1999 : 0), null, mode);
        }
    }

    protected final void addParentDirs(File baseDir, String entry, ZipOutputStream zOut, String prefix, int dirMode) throws IOException {
        if (!this.doFilesonly) {
            String dir;
            Stack<String> directories = new Stack<String>();
            int slashPos = entry.length();
            while ((slashPos = entry.lastIndexOf(47, slashPos - 1)) != -1) {
                dir = entry.substring(0, slashPos + 1);
                if (this.addedDirs.get(prefix + dir) != null) break;
                directories.push(dir);
            }
            while (!directories.isEmpty()) {
                dir = (String)directories.pop();
                File f = baseDir != null ? new File(baseDir, dir) : new File(dir);
                this.zipDir(f, zOut, prefix + dir, dirMode);
            }
        }
    }

    protected void cleanUp() {
        this.addedDirs.clear();
        this.addedFiles.clear();
        this.entries.clear();
        this.addingNewFiles = false;
        this.doUpdate = this.savedDoUpdate;
        this.resources.removeAll(this.filesetsFromGroupfilesets);
        this.filesetsFromGroupfilesets.clear();
        HAVE_NON_FILE_SET_RESOURCES_TO_ADD.set(Boolean.FALSE);
    }

    public void reset() {
        this.resources.clear();
        this.zipFile = null;
        this.baseDir = null;
        this.groupfilesets.clear();
        this.duplicate = "add";
        this.archiveType = "zip";
        this.doCompress = true;
        this.emptyBehavior = "skip";
        this.doUpdate = false;
        this.doFilesonly = false;
        this.encoding = null;
    }

    protected static final boolean isEmpty(Resource[][] r) {
        for (Resource[] element : r) {
            if (element.length <= 0) continue;
            return false;
        }
        return true;
    }

    protected Resource[] selectFileResources(Resource[] orig) {
        return this.selectResources(orig, r -> {
            if (!r.isDirectory()) {
                return true;
            }
            if (this.doFilesonly) {
                this.logWhenWriting("Ignoring directory " + r.getName() + " as only files will be added.", 3);
            }
            return false;
        });
    }

    protected Resource[] selectDirectoryResources(Resource[] orig) {
        return this.selectResources(orig, Resource::isDirectory);
    }

    protected Resource[] selectResources(Resource[] orig, ResourceSelector selector) {
        if (orig.length == 0) {
            return orig;
        }
        Resource[] result = (Resource[])Stream.of(orig).filter(selector::isSelected).toArray(Resource[]::new);
        return result.length == orig.length ? orig : result;
    }

    protected void logWhenWriting(String msg, int level) {
        if (!this.skipWriting) {
            this.log(msg, level);
        }
    }

    public static final class UnicodeExtraField
    extends EnumeratedAttribute {
        private static final Map<String, ZipOutputStream.UnicodeExtraFieldPolicy> POLICIES = new HashMap<String, ZipOutputStream.UnicodeExtraFieldPolicy>();
        private static final String NEVER_KEY = "never";
        private static final String ALWAYS_KEY = "always";
        private static final String N_E_KEY = "not-encodeable";
        public static final UnicodeExtraField NEVER;

        @Override
        public String[] getValues() {
            return new String[]{NEVER_KEY, ALWAYS_KEY, N_E_KEY};
        }

        private UnicodeExtraField(String name) {
            this.setValue(name);
        }

        public UnicodeExtraField() {
        }

        public ZipOutputStream.UnicodeExtraFieldPolicy getPolicy() {
            return POLICIES.get(this.getValue());
        }

        static {
            POLICIES.put(NEVER_KEY, ZipOutputStream.UnicodeExtraFieldPolicy.NEVER);
            POLICIES.put(ALWAYS_KEY, ZipOutputStream.UnicodeExtraFieldPolicy.ALWAYS);
            POLICIES.put(N_E_KEY, ZipOutputStream.UnicodeExtraFieldPolicy.NOT_ENCODEABLE);
            NEVER = new UnicodeExtraField(NEVER_KEY);
        }
    }

    public static final class Zip64ModeAttribute
    extends EnumeratedAttribute {
        private static final Map<String, Zip64Mode> MODES = new HashMap<String, Zip64Mode>();
        private static final String NEVER_KEY = "never";
        private static final String ALWAYS_KEY = "always";
        private static final String A_N_KEY = "as-needed";
        public static final Zip64ModeAttribute NEVER;
        public static final Zip64ModeAttribute AS_NEEDED;

        @Override
        public String[] getValues() {
            return new String[]{NEVER_KEY, ALWAYS_KEY, A_N_KEY};
        }

        private Zip64ModeAttribute(String name) {
            this.setValue(name);
        }

        public Zip64ModeAttribute() {
        }

        public Zip64Mode getMode() {
            return MODES.get(this.getValue());
        }

        static {
            MODES.put(NEVER_KEY, Zip64Mode.Never);
            MODES.put(ALWAYS_KEY, Zip64Mode.Always);
            MODES.put(A_N_KEY, Zip64Mode.AsNeeded);
            NEVER = new Zip64ModeAttribute(NEVER_KEY);
            AS_NEEDED = new Zip64ModeAttribute(A_N_KEY);
        }
    }

    public static class Duplicate
    extends EnumeratedAttribute {
        @Override
        public String[] getValues() {
            return new String[]{"add", "preserve", "fail"};
        }
    }

    public static class WhenEmpty
    extends EnumeratedAttribute {
        @Override
        public String[] getValues() {
            return new String[]{"fail", "skip", "create"};
        }
    }

    public static class ArchiveState {
        private final boolean outOfDate;
        private final Resource[][] resourcesToAdd;

        ArchiveState(boolean state, Resource[][] r) {
            this.outOfDate = state;
            this.resourcesToAdd = r;
        }

        public boolean isOutOfDate() {
            return this.outOfDate;
        }

        public Resource[][] getResourcesToAdd() {
            return this.resourcesToAdd;
        }

        public boolean isWithoutAnyResources() {
            if (this.resourcesToAdd == null) {
                return true;
            }
            for (Resource[] element : this.resourcesToAdd) {
                if (element == null || element.length <= 0) continue;
                return false;
            }
            return true;
        }
    }
}

