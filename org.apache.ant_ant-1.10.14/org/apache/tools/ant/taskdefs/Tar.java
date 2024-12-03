/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import java.util.zip.GZIPOutputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.ArchiveFileSet;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.ArchiveResource;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.TarResource;
import org.apache.tools.ant.types.selectors.SelectorUtils;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.MergingMapper;
import org.apache.tools.ant.util.ResourceUtils;
import org.apache.tools.ant.util.SourceFileScanner;
import org.apache.tools.bzip2.CBZip2OutputStream;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarOutputStream;

public class Tar
extends MatchingTask {
    private static final int BUFFER_SIZE = 8192;
    @Deprecated
    public static final String WARN = "warn";
    @Deprecated
    public static final String FAIL = "fail";
    @Deprecated
    public static final String TRUNCATE = "truncate";
    @Deprecated
    public static final String GNU = "gnu";
    @Deprecated
    public static final String OMIT = "omit";
    File tarFile;
    File baseDir;
    private TarLongFileMode longFileMode = new TarLongFileMode();
    Vector<TarFileSet> filesets = new Vector();
    private final List<ResourceCollection> resourceCollections = new Vector<ResourceCollection>();
    private boolean longWarningGiven = false;
    private TarCompressionMethod compression = new TarCompressionMethod();
    private String encoding;

    public TarFileSet createTarFileSet() {
        TarFileSet fs = new TarFileSet();
        fs.setProject(this.getProject());
        this.filesets.addElement(fs);
        return fs;
    }

    public void add(ResourceCollection res) {
        this.resourceCollections.add(res);
    }

    @Deprecated
    public void setTarfile(File tarFile) {
        this.tarFile = tarFile;
    }

    public void setDestFile(File destFile) {
        this.tarFile = destFile;
    }

    public void setBasedir(File baseDir) {
        this.baseDir = baseDir;
    }

    @Deprecated
    public void setLongfile(String mode) {
        this.log("DEPRECATED - The setLongfile(String) method has been deprecated. Use setLongfile(Tar.TarLongFileMode) instead.");
        this.longFileMode = new TarLongFileMode();
        this.longFileMode.setValue(mode);
    }

    public void setLongfile(TarLongFileMode mode) {
        this.longFileMode = mode;
    }

    public void setCompression(TarCompressionMethod mode) {
        this.compression = mode;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void execute() throws BuildException {
        if (this.tarFile == null) {
            throw new BuildException("tarfile attribute must be set!", this.getLocation());
        }
        if (this.tarFile.exists() && this.tarFile.isDirectory()) {
            throw new BuildException("tarfile is a directory!", this.getLocation());
        }
        if (this.tarFile.exists() && !this.tarFile.canWrite()) {
            throw new BuildException("Can not write to the specified tarfile!", this.getLocation());
        }
        Vector<TarFileSet> savedFileSets = new Vector<TarFileSet>(this.filesets);
        try {
            if (this.baseDir != null) {
                if (!this.baseDir.exists()) {
                    throw new BuildException("basedir does not exist!", this.getLocation());
                }
                TarFileSet mainFileSet = new TarFileSet(this.fileset);
                mainFileSet.setDir(this.baseDir);
                this.filesets.addElement(mainFileSet);
            }
            if (this.filesets.isEmpty() && this.resourceCollections.isEmpty()) {
                throw new BuildException("You must supply either a basedir attribute or some nested resource collections.", this.getLocation());
            }
            boolean upToDate = true;
            for (TarFileSet tfs : this.filesets) {
                upToDate &= this.check(tfs);
            }
            for (ResourceCollection rcol : this.resourceCollections) {
                upToDate &= this.check(rcol);
            }
            if (upToDate) {
                this.log("Nothing to do: " + this.tarFile.getAbsolutePath() + " is up to date.", 2);
                return;
            }
            File parent = this.tarFile.getParentFile();
            if (!(parent == null || parent.isDirectory() || parent.mkdirs() || parent.isDirectory())) {
                throw new BuildException("Failed to create missing parent directory for %s", this.tarFile);
            }
            this.log("Building tar: " + this.tarFile.getAbsolutePath(), 2);
            try (TarOutputStream tOut = new TarOutputStream(this.compression.compress(new BufferedOutputStream(Files.newOutputStream(this.tarFile.toPath(), new OpenOption[0]))), this.encoding);){
                tOut.setDebug(true);
                if (this.longFileMode.isTruncateMode()) {
                    tOut.setLongFileMode(1);
                } else if (this.longFileMode.isFailMode() || this.longFileMode.isOmitMode()) {
                    tOut.setLongFileMode(0);
                } else if (this.longFileMode.isPosixMode()) {
                    tOut.setLongFileMode(3);
                } else {
                    tOut.setLongFileMode(2);
                }
                this.longWarningGiven = false;
                for (TarFileSet tfs : this.filesets) {
                    this.tar(tfs, tOut);
                }
                for (ResourceCollection rcol : this.resourceCollections) {
                    this.tar(rcol, tOut);
                }
            }
            catch (IOException ioe) {
                String msg = "Problem creating TAR: " + ioe.getMessage();
                throw new BuildException(msg, ioe, this.getLocation());
            }
        }
        finally {
            this.filesets = savedFileSets;
        }
    }

    protected void tarFile(File file, TarOutputStream tOut, String vPath, TarFileSet tarFileSet) throws IOException {
        if (file.equals(this.tarFile)) {
            return;
        }
        this.tarResource(new FileResource(file), tOut, vPath, tarFileSet);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void tarResource(Resource r, TarOutputStream tOut, String vPath, TarFileSet tarFileSet) throws IOException {
        if (!r.isExists()) {
            return;
        }
        boolean preserveLeadingSlashes = false;
        if (tarFileSet != null) {
            String fullpath = tarFileSet.getFullpath(this.getProject());
            if (fullpath.isEmpty()) {
                if (vPath.isEmpty()) {
                    return;
                }
                vPath = Tar.getCanonicalPrefix(tarFileSet, this.getProject()) + vPath;
            } else {
                vPath = fullpath;
            }
            preserveLeadingSlashes = tarFileSet.getPreserveLeadingSlashes();
            if (vPath.startsWith("/") && !preserveLeadingSlashes) {
                int l = vPath.length();
                if (l <= 1) {
                    return;
                }
                vPath = vPath.substring(1, l);
            }
        }
        if (r.isDirectory() && !vPath.endsWith("/")) {
            vPath = vPath + "/";
        }
        if (vPath.length() >= 100) {
            if (this.longFileMode.isOmitMode()) {
                this.log("Omitting: " + vPath, 2);
                return;
            }
            if (this.longFileMode.isWarnMode()) {
                this.log("Entry: " + vPath + " longer than " + 100 + " characters.", 1);
                if (!this.longWarningGiven) {
                    this.log("Resulting tar file can only be processed successfully by GNU compatible tar commands", 1);
                    this.longWarningGiven = true;
                }
            } else if (this.longFileMode.isFailMode()) {
                throw new BuildException("Entry: " + vPath + " longer than " + 100 + "characters.", this.getLocation());
            }
        }
        TarEntry te = new TarEntry(vPath, preserveLeadingSlashes);
        te.setModTime(r.getLastModified());
        if (r instanceof ArchiveResource) {
            ArchiveResource ar = (ArchiveResource)r;
            te.setMode(ar.getMode());
            if (r instanceof TarResource) {
                TarResource tr = (TarResource)r;
                te.setUserName(tr.getUserName());
                te.setUserId(tr.getLongUid());
                te.setGroupName(tr.getGroup());
                te.setGroupId(tr.getLongGid());
                String linkName = tr.getLinkName();
                byte linkFlag = tr.getLinkFlag();
                if (linkFlag == 49 && linkName != null && linkName.length() > 0 && !linkName.startsWith("/")) {
                    linkName = Tar.getCanonicalPrefix(tarFileSet, this.getProject()) + linkName;
                }
                te.setLinkName(linkName);
                te.setLinkFlag(linkFlag);
            }
        }
        if (!r.isDirectory()) {
            if ((long)r.size() > 0x1FFFFFFFFL) {
                throw new BuildException("Resource: " + r + " larger than " + 0x1FFFFFFFFL + " bytes.");
            }
            te.setSize(r.getSize());
            if (tarFileSet != null && tarFileSet.hasFileModeBeenSet()) {
                te.setMode(tarFileSet.getMode());
            }
        } else if (tarFileSet != null && tarFileSet.hasDirModeBeenSet()) {
            te.setMode(tarFileSet.getDirMode(this.getProject()));
        }
        if (tarFileSet != null) {
            if (tarFileSet.hasUserNameBeenSet()) {
                te.setUserName(tarFileSet.getUserName());
            }
            if (tarFileSet.hasGroupBeenSet()) {
                te.setGroupName(tarFileSet.getGroup());
            }
            if (tarFileSet.hasUserIdBeenSet()) {
                te.setUserId(tarFileSet.getUid());
            }
            if (tarFileSet.hasGroupIdBeenSet()) {
                te.setGroupId(tarFileSet.getGid());
            }
        }
        InputStream in = null;
        try {
            tOut.putNextEntry(te);
            if (!r.isDirectory()) {
                in = r.getInputStream();
                byte[] buffer = new byte[8192];
                int count = 0;
                do {
                    tOut.write(buffer, 0, count);
                } while ((count = in.read(buffer, 0, buffer.length)) != -1);
            }
            tOut.closeEntry();
        }
        catch (Throwable throwable) {
            FileUtils.close(in);
            throw throwable;
        }
        FileUtils.close(in);
    }

    @Deprecated
    protected boolean archiveIsUpToDate(String[] files) {
        return this.archiveIsUpToDate(files, this.baseDir);
    }

    protected boolean archiveIsUpToDate(String[] files, File dir) {
        SourceFileScanner sfs = new SourceFileScanner(this);
        MergingMapper mm = new MergingMapper();
        mm.setTo(this.tarFile.getAbsolutePath());
        return sfs.restrict(files, dir, null, mm).length == 0;
    }

    protected boolean archiveIsUpToDate(Resource r) {
        return SelectorUtils.isOutOfDate((Resource)new FileResource(this.tarFile), r, FileUtils.getFileUtils().getFileTimestampGranularity());
    }

    protected boolean supportsNonFileResources() {
        return this.getClass().equals(Tar.class);
    }

    protected boolean check(ResourceCollection rc) {
        boolean upToDate = true;
        if (Tar.isFileFileSet(rc)) {
            FileSet fs = (FileSet)rc;
            upToDate = this.check(fs.getDir(this.getProject()), Tar.getFileNames(fs));
        } else {
            if (!rc.isFilesystemOnly() && !this.supportsNonFileResources()) {
                throw new BuildException("only filesystem resources are supported");
            }
            if (rc.isFilesystemOnly()) {
                HashSet<File> basedirs = new HashSet<File>();
                HashMap<File, List> basedirToFilesMap = new HashMap<File, List>();
                for (Resource res : rc) {
                    FileResource r = ResourceUtils.asFileResource(res.as(FileProvider.class));
                    File base = r.getBaseDir();
                    if (base == null) {
                        base = Copy.NULL_FILE_PLACEHOLDER;
                    }
                    basedirs.add(base);
                    List files = basedirToFilesMap.computeIfAbsent(base, k -> new Vector());
                    if (base == Copy.NULL_FILE_PLACEHOLDER) {
                        files.add(r.getFile().getAbsolutePath());
                        continue;
                    }
                    files.add(r.getName());
                }
                for (File base : basedirs) {
                    File tmpBase = base == Copy.NULL_FILE_PLACEHOLDER ? null : base;
                    List files = (List)basedirToFilesMap.get(base);
                    upToDate &= this.check(tmpBase, files);
                }
            } else {
                for (Resource r : rc) {
                    upToDate = this.archiveIsUpToDate(r);
                }
            }
        }
        return upToDate;
    }

    protected boolean check(File basedir, String[] files) {
        boolean upToDate = this.archiveIsUpToDate(files, basedir);
        for (String file : files) {
            if (!this.tarFile.equals(new File(basedir, file))) continue;
            throw new BuildException("A tar file cannot include itself", this.getLocation());
        }
        return upToDate;
    }

    protected boolean check(File basedir, Collection<String> files) {
        return this.check(basedir, files.toArray(new String[0]));
    }

    protected void tar(ResourceCollection rc, TarOutputStream tOut) throws IOException {
        ArchiveFileSet afs = null;
        if (rc instanceof ArchiveFileSet) {
            afs = (ArchiveFileSet)rc;
        }
        if (afs != null && afs.size() > 1 && !afs.getFullpath(this.getProject()).isEmpty()) {
            throw new BuildException("fullpath attribute may only be specified for filesets that specify a single file.");
        }
        TarFileSet tfs = this.asTarFileSet(afs);
        if (Tar.isFileFileSet(rc)) {
            FileSet fs = (FileSet)rc;
            for (String file : Tar.getFileNames(fs)) {
                File f = new File(fs.getDir(this.getProject()), file);
                String name = file.replace(File.separatorChar, '/');
                this.tarFile(f, tOut, name, tfs);
            }
        } else if (rc.isFilesystemOnly()) {
            for (Resource r : rc) {
                File f = r.as(FileProvider.class).getFile();
                this.tarFile(f, tOut, f.getName(), tfs);
            }
        } else {
            for (Resource r : rc) {
                this.tarResource(r, tOut, r.getName(), tfs);
            }
        }
    }

    protected static boolean isFileFileSet(ResourceCollection rc) {
        return rc instanceof FileSet && rc.isFilesystemOnly();
    }

    protected static String[] getFileNames(FileSet fs) {
        DirectoryScanner ds = fs.getDirectoryScanner(fs.getProject());
        String[] directories = ds.getIncludedDirectories();
        String[] filesPerSe = ds.getIncludedFiles();
        String[] files = new String[directories.length + filesPerSe.length];
        System.arraycopy(directories, 0, files, 0, directories.length);
        System.arraycopy(filesPerSe, 0, files, directories.length, filesPerSe.length);
        return files;
    }

    protected TarFileSet asTarFileSet(ArchiveFileSet archiveFileSet) {
        TarFileSet tfs;
        if (archiveFileSet instanceof TarFileSet) {
            tfs = (TarFileSet)archiveFileSet;
        } else {
            tfs = new TarFileSet();
            tfs.setProject(this.getProject());
            if (archiveFileSet != null) {
                tfs.setPrefix(archiveFileSet.getPrefix(this.getProject()));
                tfs.setFullpath(archiveFileSet.getFullpath(this.getProject()));
                if (archiveFileSet.hasFileModeBeenSet()) {
                    tfs.integerSetFileMode(archiveFileSet.getFileMode(this.getProject()));
                }
                if (archiveFileSet.hasDirModeBeenSet()) {
                    tfs.integerSetDirMode(archiveFileSet.getDirMode(this.getProject()));
                }
                if (archiveFileSet instanceof org.apache.tools.ant.types.TarFileSet) {
                    org.apache.tools.ant.types.TarFileSet t = (org.apache.tools.ant.types.TarFileSet)archiveFileSet;
                    if (t.hasUserNameBeenSet()) {
                        tfs.setUserName(t.getUserName());
                    }
                    if (t.hasGroupBeenSet()) {
                        tfs.setGroup(t.getGroup());
                    }
                    if (t.hasUserIdBeenSet()) {
                        tfs.setUid(t.getUid());
                    }
                    if (t.hasGroupIdBeenSet()) {
                        tfs.setGid(t.getGid());
                    }
                }
            }
        }
        return tfs;
    }

    private static String getCanonicalPrefix(TarFileSet tarFileSet, Project project) {
        String prefix = tarFileSet.getPrefix(project);
        if (prefix.isEmpty() || prefix.endsWith("/")) {
            return prefix;
        }
        prefix = prefix + "/";
        return prefix;
    }

    public static class TarLongFileMode
    extends EnumeratedAttribute {
        public static final String WARN = "warn";
        public static final String FAIL = "fail";
        public static final String TRUNCATE = "truncate";
        public static final String GNU = "gnu";
        public static final String POSIX = "posix";
        public static final String OMIT = "omit";
        private static final String[] VALID_MODES = new String[]{"warn", "fail", "truncate", "gnu", "posix", "omit"};

        public TarLongFileMode() {
            this.setValue("warn");
        }

        @Override
        public String[] getValues() {
            return VALID_MODES;
        }

        public boolean isTruncateMode() {
            return "truncate".equalsIgnoreCase(this.getValue());
        }

        public boolean isWarnMode() {
            return "warn".equalsIgnoreCase(this.getValue());
        }

        public boolean isGnuMode() {
            return "gnu".equalsIgnoreCase(this.getValue());
        }

        public boolean isFailMode() {
            return "fail".equalsIgnoreCase(this.getValue());
        }

        public boolean isOmitMode() {
            return "omit".equalsIgnoreCase(this.getValue());
        }

        public boolean isPosixMode() {
            return POSIX.equalsIgnoreCase(this.getValue());
        }
    }

    public static final class TarCompressionMethod
    extends EnumeratedAttribute {
        private static final String NONE = "none";
        private static final String GZIP = "gzip";
        private static final String BZIP2 = "bzip2";
        private static final String XZ = "xz";

        public TarCompressionMethod() {
            this.setValue(NONE);
        }

        @Override
        public String[] getValues() {
            return new String[]{NONE, GZIP, BZIP2, XZ};
        }

        private OutputStream compress(OutputStream ostream) throws IOException {
            String v = this.getValue();
            if (GZIP.equals(v)) {
                return new GZIPOutputStream(ostream);
            }
            if (XZ.equals(v)) {
                return TarCompressionMethod.newXZOutputStream(ostream);
            }
            if (BZIP2.equals(v)) {
                ostream.write(66);
                ostream.write(90);
                return new CBZip2OutputStream(ostream);
            }
            return ostream;
        }

        private static OutputStream newXZOutputStream(OutputStream ostream) throws BuildException {
            try {
                Class<?> fClazz = Class.forName("org.tukaani.xz.FilterOptions");
                Class<?> oClazz = Class.forName("org.tukaani.xz.LZMA2Options");
                Class<OutputStream> sClazz = Class.forName("org.tukaani.xz.XZOutputStream").asSubclass(OutputStream.class);
                Constructor<OutputStream> c = sClazz.getConstructor(OutputStream.class, fClazz);
                return c.newInstance(ostream, oClazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]));
            }
            catch (ClassNotFoundException ex) {
                throw new BuildException("xz compression requires the XZ for Java library", ex);
            }
            catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException ex) {
                throw new BuildException("failed to create XZOutputStream", ex);
            }
        }
    }

    public static class TarFileSet
    extends org.apache.tools.ant.types.TarFileSet {
        private String[] files = null;
        private boolean preserveLeadingSlashes = false;

        public TarFileSet(FileSet fileset) {
            super(fileset);
        }

        public TarFileSet() {
        }

        public String[] getFiles(Project p) {
            if (this.files == null) {
                this.files = Tar.getFileNames(this);
            }
            return this.files;
        }

        public void setMode(String octalString) {
            this.setFileMode(octalString);
        }

        public int getMode() {
            return this.getFileMode(this.getProject());
        }

        public void setPreserveLeadingSlashes(boolean b) {
            this.preserveLeadingSlashes = b;
        }

        public boolean getPreserveLeadingSlashes() {
            return this.preserveLeadingSlashes;
        }
    }
}

