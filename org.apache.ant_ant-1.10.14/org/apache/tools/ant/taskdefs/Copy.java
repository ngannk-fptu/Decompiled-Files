/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.FilterChain;
import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.FlatFileNameMapper;
import org.apache.tools.ant.util.IdentityMapper;
import org.apache.tools.ant.util.LinkedHashtable;
import org.apache.tools.ant.util.ResourceUtils;
import org.apache.tools.ant.util.SourceFileScanner;
import org.apache.tools.ant.util.StringUtils;

public class Copy
extends Task {
    private static final String MSG_WHEN_COPYING_EMPTY_RC_TO_FILE = "Cannot perform operation from directory to file.";
    @Deprecated
    static final String LINE_SEPARATOR = StringUtils.LINE_SEP;
    static final File NULL_FILE_PLACEHOLDER = new File("/NULL_FILE");
    protected File file = null;
    protected File destFile = null;
    protected File destDir = null;
    protected Vector<ResourceCollection> rcs = new Vector();
    protected Vector<ResourceCollection> filesets = this.rcs;
    private boolean enableMultipleMappings = false;
    protected boolean filtering = false;
    protected boolean preserveLastModified = false;
    protected boolean forceOverwrite = false;
    protected boolean flatten = false;
    protected int verbosity = 3;
    protected boolean includeEmpty = true;
    protected boolean failonerror = true;
    protected Hashtable<String, String[]> fileCopyMap = new LinkedHashtable<String, String[]>();
    protected Hashtable<String, String[]> dirCopyMap = new LinkedHashtable<String, String[]>();
    protected Hashtable<File, File> completeDirMap = new LinkedHashtable<File, File>();
    protected Mapper mapperElement = null;
    protected FileUtils fileUtils;
    private final Vector<FilterChain> filterChains = new Vector();
    private final Vector<FilterSet> filterSets = new Vector();
    private String inputEncoding = null;
    private String outputEncoding = null;
    private long granularity = 0L;
    private boolean force = false;
    private boolean quiet = false;
    private Resource singleResource = null;

    public Copy() {
        this.fileUtils = FileUtils.getFileUtils();
        this.granularity = this.fileUtils.getFileTimestampGranularity();
    }

    protected FileUtils getFileUtils() {
        return this.fileUtils;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setTofile(File destFile) {
        this.destFile = destFile;
    }

    public void setTodir(File destDir) {
        this.destDir = destDir;
    }

    public FilterChain createFilterChain() {
        FilterChain filterChain = new FilterChain();
        this.filterChains.addElement(filterChain);
        return filterChain;
    }

    public FilterSet createFilterSet() {
        FilterSet filterSet = new FilterSet();
        this.filterSets.addElement(filterSet);
        return filterSet;
    }

    @Deprecated
    public void setPreserveLastModified(String preserve) {
        this.setPreserveLastModified(Project.toBoolean(preserve));
    }

    public void setPreserveLastModified(boolean preserve) {
        this.preserveLastModified = preserve;
    }

    public boolean getPreserveLastModified() {
        return this.preserveLastModified;
    }

    protected Vector<FilterSet> getFilterSets() {
        return this.filterSets;
    }

    protected Vector<FilterChain> getFilterChains() {
        return this.filterChains;
    }

    public void setFiltering(boolean filtering) {
        this.filtering = filtering;
    }

    public void setOverwrite(boolean overwrite) {
        this.forceOverwrite = overwrite;
    }

    public void setForce(boolean f) {
        this.force = f;
    }

    public boolean getForce() {
        return this.force;
    }

    public void setFlatten(boolean flatten) {
        this.flatten = flatten;
    }

    public void setVerbose(boolean verbose) {
        this.verbosity = verbose ? 2 : 3;
    }

    public void setIncludeEmptyDirs(boolean includeEmpty) {
        this.includeEmpty = includeEmpty;
    }

    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    public void setEnableMultipleMappings(boolean enableMultipleMappings) {
        this.enableMultipleMappings = enableMultipleMappings;
    }

    public boolean isEnableMultipleMapping() {
        return this.enableMultipleMappings;
    }

    public void setFailOnError(boolean failonerror) {
        this.failonerror = failonerror;
    }

    public void addFileset(FileSet set) {
        this.add(set);
    }

    public void add(ResourceCollection res) {
        this.rcs.add(res);
    }

    public Mapper createMapper() throws BuildException {
        if (this.mapperElement != null) {
            throw new BuildException("Cannot define more than one mapper", this.getLocation());
        }
        this.mapperElement = new Mapper(this.getProject());
        return this.mapperElement;
    }

    public void add(FileNameMapper fileNameMapper) {
        this.createMapper().add(fileNameMapper);
    }

    public void setEncoding(String encoding) {
        this.inputEncoding = encoding;
        if (this.outputEncoding == null) {
            this.outputEncoding = encoding;
        }
    }

    public String getEncoding() {
        return this.inputEncoding;
    }

    public void setOutputEncoding(String encoding) {
        this.outputEncoding = encoding;
    }

    public String getOutputEncoding() {
        return this.outputEncoding;
    }

    public void setGranularity(long granularity) {
        this.granularity = granularity;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void execute() throws BuildException {
        block32: {
            File savedFile = this.file;
            File savedDestFile = this.destFile;
            File savedDestDir = this.destDir;
            ResourceCollection savedRc = null;
            if (this.file == null && this.destFile != null && this.rcs.size() == 1) {
                savedRc = this.rcs.elementAt(0);
            }
            try {
                try {
                    this.validateAttributes();
                }
                catch (BuildException e) {
                    if (this.failonerror || !this.getMessage(e).equals(MSG_WHEN_COPYING_EMPTY_RC_TO_FILE)) {
                        throw e;
                    }
                    this.log("Warning: " + this.getMessage(e), 0);
                    this.singleResource = null;
                    this.file = savedFile;
                    this.destFile = savedDestFile;
                    this.destDir = savedDestDir;
                    if (savedRc != null) {
                        this.rcs.insertElementAt(savedRc, 0);
                    }
                    this.fileCopyMap.clear();
                    this.dirCopyMap.clear();
                    this.completeDirMap.clear();
                    return;
                }
                this.copySingleFile();
                HashMap<File, List<String>> filesByBasedir = new HashMap<File, List<String>>();
                HashMap<File, List<String>> dirsByBasedir = new HashMap<File, List<String>>();
                HashSet<File> baseDirs = new HashSet<File>();
                ArrayList<Resource> nonFileResources = new ArrayList<Resource>();
                for (ResourceCollection rc : this.rcs) {
                    if (rc instanceof FileSet && rc.isFilesystemOnly()) {
                        DirectoryScanner ds;
                        FileSet fs = (FileSet)rc;
                        try {
                            ds = fs.getDirectoryScanner(this.getProject());
                        }
                        catch (BuildException e) {
                            if (this.failonerror || !this.getMessage(e).endsWith(" does not exist.")) {
                                throw e;
                            }
                            if (this.quiet) continue;
                            this.log("Warning: " + this.getMessage(e), 0);
                            continue;
                        }
                        File fromDir = fs.getDir(this.getProject());
                        if (!this.flatten && this.mapperElement == null && ds.isEverythingIncluded() && !fs.hasPatterns()) {
                            this.completeDirMap.put(fromDir, this.destDir);
                        }
                        Copy.add(fromDir, ds.getIncludedFiles(), filesByBasedir);
                        Copy.add(fromDir, ds.getIncludedDirectories(), dirsByBasedir);
                        baseDirs.add(fromDir);
                        continue;
                    }
                    if (!rc.isFilesystemOnly() && !this.supportsNonFileResources()) {
                        throw new BuildException("Only FileSystem resources are supported.");
                    }
                    for (Resource r : rc) {
                        if (!r.isExists()) {
                            String message = "Warning: Could not find resource " + r.toLongString() + " to copy.";
                            if (!this.failonerror) {
                                if (this.quiet) continue;
                                this.log(message, 0);
                                continue;
                            }
                            throw new BuildException(message);
                        }
                        File baseDir = NULL_FILE_PLACEHOLDER;
                        String name = r.getName();
                        FileProvider fp = r.as(FileProvider.class);
                        if (fp != null) {
                            FileResource fr = ResourceUtils.asFileResource(fp);
                            baseDir = Copy.getKeyFile(fr.getBaseDir());
                            if (fr.getBaseDir() == null) {
                                name = fr.getFile().getAbsolutePath();
                            }
                        }
                        if (r.isDirectory() || fp != null) {
                            Copy.add(baseDir, name, r.isDirectory() ? dirsByBasedir : filesByBasedir);
                            baseDirs.add(baseDir);
                            continue;
                        }
                        nonFileResources.add(r);
                    }
                }
                this.iterateOverBaseDirs(baseDirs, dirsByBasedir, filesByBasedir);
                try {
                    this.doFileOperations();
                }
                catch (BuildException e) {
                    if (!this.failonerror) {
                        if (!this.quiet) {
                            this.log("Warning: " + this.getMessage(e), 0);
                        }
                    }
                    throw e;
                }
                if (nonFileResources.isEmpty() && this.singleResource == null) break block32;
                Resource[] nonFiles = nonFileResources.toArray(new Resource[0]);
                Map<Resource, String[]> map = this.scan(nonFiles, this.destDir);
                if (this.singleResource != null) {
                    map.put(this.singleResource, new String[]{this.destFile.getAbsolutePath()});
                }
                try {
                    this.doResourceOperations(map);
                }
                catch (BuildException e) {
                    if (!this.failonerror) {
                        if (!this.quiet) {
                            this.log("Warning: " + this.getMessage(e), 0);
                        }
                        break block32;
                    }
                    throw e;
                }
            }
            finally {
                this.singleResource = null;
                this.file = savedFile;
                this.destFile = savedDestFile;
                this.destDir = savedDestDir;
                if (savedRc != null) {
                    this.rcs.insertElementAt(savedRc, 0);
                }
                this.fileCopyMap.clear();
                this.dirCopyMap.clear();
                this.completeDirMap.clear();
            }
        }
    }

    private void copySingleFile() {
        if (this.file != null) {
            if (this.file.exists()) {
                if (this.destFile == null) {
                    this.destFile = new File(this.destDir, this.file.getName());
                }
                if (this.forceOverwrite || !this.destFile.exists() || this.file.lastModified() - this.granularity > this.destFile.lastModified()) {
                    this.fileCopyMap.put(this.file.getAbsolutePath(), new String[]{this.destFile.getAbsolutePath()});
                } else {
                    this.log(this.file + " omitted as " + this.destFile + " is up to date.", 3);
                }
            } else {
                String message = "Warning: Could not find file " + this.file.getAbsolutePath() + " to copy.";
                if (!this.failonerror) {
                    if (!this.quiet) {
                        this.log(message, 0);
                    }
                } else {
                    throw new BuildException(message);
                }
            }
        }
    }

    private void iterateOverBaseDirs(Set<File> baseDirs, Map<File, List<String>> dirsByBasedir, Map<File, List<String>> filesByBasedir) {
        for (File f : baseDirs) {
            List<String> files = filesByBasedir.get(f);
            List<String> dirs = dirsByBasedir.get(f);
            String[] srcFiles = new String[]{};
            if (files != null) {
                srcFiles = files.toArray(srcFiles);
            }
            String[] srcDirs = new String[]{};
            if (dirs != null) {
                srcDirs = dirs.toArray(srcDirs);
            }
            this.scan(f == NULL_FILE_PLACEHOLDER ? null : f, this.destDir, srcFiles, srcDirs);
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected void validateAttributes() throws BuildException {
        if (this.file == null && this.rcs.isEmpty()) {
            throw new BuildException("Specify at least one source--a file or a resource collection.");
        }
        if (this.destFile != null && this.destDir != null) {
            throw new BuildException("Only one of tofile and todir may be set.");
        }
        if (this.destFile == null && this.destDir == null) {
            throw new BuildException("One of tofile or todir must be set.");
        }
        if (this.file != null && this.file.isDirectory()) {
            throw new BuildException("Use a resource collection to copy directories.");
        }
        if (this.destFile != null && !this.rcs.isEmpty()) {
            if (this.rcs.size() > 1) {
                throw new BuildException("Cannot concatenate multiple files into a single file.");
            }
            ResourceCollection rc = this.rcs.elementAt(0);
            if (!rc.isFilesystemOnly() && !this.supportsNonFileResources()) {
                throw new BuildException("Only FileSystem resources are supported.");
            }
            if (rc.isEmpty()) {
                throw new BuildException(MSG_WHEN_COPYING_EMPTY_RC_TO_FILE);
            }
            if (rc.size() != 1) throw new BuildException("Cannot concatenate multiple files into a single file.");
            Resource res = (Resource)rc.iterator().next();
            FileProvider r = res.as(FileProvider.class);
            if (this.file != null) throw new BuildException("Cannot concatenate multiple files into a single file.");
            if (r != null) {
                this.file = r.getFile();
            } else {
                this.singleResource = res;
            }
            this.rcs.removeElementAt(0);
        }
        if (this.destFile == null) return;
        this.destDir = this.destFile.getParentFile();
    }

    protected void scan(File fromDir, File toDir, String[] files, String[] dirs) {
        FileNameMapper mapper = this.getMapper();
        this.buildMap(fromDir, toDir, files, mapper, this.fileCopyMap);
        if (this.includeEmpty) {
            this.buildMap(fromDir, toDir, dirs, mapper, this.dirCopyMap);
        }
    }

    protected Map<Resource, String[]> scan(Resource[] fromResources, File toDir) {
        return this.buildMap(fromResources, toDir, this.getMapper());
    }

    protected void buildMap(File fromDir, File toDir, String[] names, FileNameMapper mapper, Hashtable<String, String[]> map) {
        String[] toCopy = null;
        if (this.forceOverwrite) {
            ArrayList<String> v = new ArrayList<String>();
            String[] stringArray = names;
            int n = stringArray.length;
            for (int i = 0; i < n; ++i) {
                String name = stringArray[i];
                if (mapper.mapFileName(name) == null) continue;
                v.add(name);
            }
            toCopy = v.toArray(new String[0]);
        } else {
            SourceFileScanner ds = new SourceFileScanner(this);
            toCopy = ds.restrict(names, fromDir, toDir, mapper, this.granularity);
        }
        for (String name : toCopy) {
            File src = new File(fromDir, name);
            String[] mappedFiles = mapper.mapFileName(name);
            if (mappedFiles == null || mappedFiles.length == 0) continue;
            if (!this.enableMultipleMappings) {
                map.put(src.getAbsolutePath(), new String[]{new File(toDir, mappedFiles[0]).getAbsolutePath()});
                continue;
            }
            for (int k = 0; k < mappedFiles.length; ++k) {
                mappedFiles[k] = new File(toDir, mappedFiles[k]).getAbsolutePath();
            }
            map.put(src.getAbsolutePath(), mappedFiles);
        }
    }

    protected Map<Resource, String[]> buildMap(Resource[] fromResources, File toDir, FileNameMapper mapper) {
        Resource[] toCopy;
        HashMap<Resource, String[]> map = new HashMap<Resource, String[]>();
        if (this.forceOverwrite) {
            ArrayList<Resource> v = new ArrayList<Resource>();
            Resource[] resourceArray = fromResources;
            int n = resourceArray.length;
            for (int i = 0; i < n; ++i) {
                Resource rc = resourceArray[i];
                if (mapper.mapFileName(rc.getName()) == null) continue;
                v.add(rc);
            }
            toCopy = v.toArray(new Resource[0]);
        } else {
            toCopy = ResourceUtils.selectOutOfDateSources((ProjectComponent)this, fromResources, mapper, name -> new FileResource(toDir, name), this.granularity);
        }
        for (Resource rc : toCopy) {
            String[] mappedFiles = mapper.mapFileName(rc.getName());
            if (mappedFiles == null || mappedFiles.length == 0) {
                throw new BuildException("Can't copy a resource without a name if the mapper doesn't provide one.");
            }
            if (!this.enableMultipleMappings) {
                map.put(rc, new String[]{new File(toDir, mappedFiles[0]).getAbsolutePath()});
                continue;
            }
            for (int k = 0; k < mappedFiles.length; ++k) {
                mappedFiles[k] = new File(toDir, mappedFiles[k]).getAbsolutePath();
            }
            map.put(rc, mappedFiles);
        }
        return map;
    }

    protected void doFileOperations() {
        if (!this.fileCopyMap.isEmpty()) {
            this.log("Copying " + this.fileCopyMap.size() + " file" + (this.fileCopyMap.size() == 1 ? "" : "s") + " to " + this.destDir.getAbsolutePath());
            for (Map.Entry<String, String[]> e : this.fileCopyMap.entrySet()) {
                String fromFile = e.getKey();
                for (String toFile : e.getValue()) {
                    if (fromFile.equals(toFile)) {
                        this.log("Skipping self-copy of " + fromFile, this.verbosity);
                        continue;
                    }
                    try {
                        this.log("Copying " + fromFile + " to " + toFile, this.verbosity);
                        FilterSetCollection executionFilters = new FilterSetCollection();
                        if (this.filtering) {
                            executionFilters.addFilterSet(this.getProject().getGlobalFilterSet());
                        }
                        for (FilterSet filterSet : this.filterSets) {
                            executionFilters.addFilterSet(filterSet);
                        }
                        this.fileUtils.copyFile(new File(fromFile), new File(toFile), executionFilters, this.filterChains, this.forceOverwrite, this.preserveLastModified, false, this.inputEncoding, this.outputEncoding, this.getProject(), this.getForce());
                    }
                    catch (IOException ioe) {
                        String msg = "Failed to copy " + fromFile + " to " + toFile + " due to " + this.getDueTo(ioe);
                        File targetFile = new File(toFile);
                        if (!(ioe instanceof ResourceUtils.ReadOnlyTargetFileException) && targetFile.exists() && !targetFile.delete()) {
                            msg = msg + " and I couldn't delete the corrupt " + toFile;
                        }
                        if (this.failonerror) {
                            throw new BuildException(msg, ioe, this.getLocation());
                        }
                        this.log(msg, 0);
                    }
                }
            }
        }
        if (this.includeEmpty) {
            int createCount = 0;
            for (String[] dirs : this.dirCopyMap.values()) {
                for (String dir : dirs) {
                    File d = new File(dir);
                    if (d.exists()) continue;
                    if (!d.mkdirs() && !d.isDirectory()) {
                        this.log("Unable to create directory " + d.getAbsolutePath(), 0);
                        continue;
                    }
                    ++createCount;
                }
            }
            if (createCount > 0) {
                this.log("Copied " + this.dirCopyMap.size() + " empty director" + (this.dirCopyMap.size() == 1 ? "y" : "ies") + " to " + createCount + " empty director" + (createCount == 1 ? "y" : "ies") + " under " + this.destDir.getAbsolutePath());
            }
        }
    }

    protected void doResourceOperations(Map<Resource, String[]> map) {
        if (!map.isEmpty()) {
            this.log("Copying " + map.size() + " resource" + (map.size() == 1 ? "" : "s") + " to " + this.destDir.getAbsolutePath());
            for (Map.Entry<Resource, String[]> e : map.entrySet()) {
                Resource fromResource = e.getKey();
                for (String toFile : e.getValue()) {
                    try {
                        this.log("Copying " + fromResource + " to " + toFile, this.verbosity);
                        FilterSetCollection executionFilters = new FilterSetCollection();
                        if (this.filtering) {
                            executionFilters.addFilterSet(this.getProject().getGlobalFilterSet());
                        }
                        for (FilterSet filterSet : this.filterSets) {
                            executionFilters.addFilterSet(filterSet);
                        }
                        ResourceUtils.copyResource(fromResource, new FileResource(this.destDir, toFile), executionFilters, this.filterChains, this.forceOverwrite, this.preserveLastModified, false, this.inputEncoding, this.outputEncoding, this.getProject(), this.getForce());
                    }
                    catch (IOException ioe) {
                        String msg = "Failed to copy " + fromResource + " to " + toFile + " due to " + this.getDueTo(ioe);
                        File targetFile = new File(toFile);
                        if (!(ioe instanceof ResourceUtils.ReadOnlyTargetFileException) && targetFile.exists() && !targetFile.delete()) {
                            msg = msg + " and I couldn't delete the corrupt " + toFile;
                        }
                        if (this.failonerror) {
                            throw new BuildException(msg, ioe, this.getLocation());
                        }
                        this.log(msg, 0);
                    }
                }
            }
        }
    }

    protected boolean supportsNonFileResources() {
        return this.getClass().equals(Copy.class);
    }

    private static void add(File baseDir, String[] names, Map<File, List<String>> m) {
        if (names != null) {
            baseDir = Copy.getKeyFile(baseDir);
            List l = m.computeIfAbsent(baseDir, k -> new ArrayList(names.length));
            l.addAll(Arrays.asList(names));
        }
    }

    private static void add(File baseDir, String name, Map<File, List<String>> m) {
        if (name != null) {
            Copy.add(baseDir, new String[]{name}, m);
        }
    }

    private static File getKeyFile(File f) {
        return f == null ? NULL_FILE_PLACEHOLDER : f;
    }

    private FileNameMapper getMapper() {
        FileNameMapper mapper = null;
        mapper = this.mapperElement != null ? this.mapperElement.getImplementation() : (this.flatten ? new FlatFileNameMapper() : new IdentityMapper());
        return mapper;
    }

    private String getMessage(Exception ex) {
        return ex.getMessage() == null ? ex.toString() : ex.getMessage();
    }

    private String getDueTo(Exception ex) {
        boolean baseIOException = ex.getClass() == IOException.class;
        StringBuilder message = new StringBuilder();
        if (!baseIOException || ex.getMessage() == null) {
            message.append(ex.getClass().getName());
        }
        if (ex.getMessage() != null) {
            if (!baseIOException) {
                message.append(" ");
            }
            message.append(ex.getMessage());
        }
        if (ex.getClass().getName().contains("MalformedInput")) {
            message.append(String.format("%nThis is normally due to the input file containing invalid%nbytes for the character encoding used : %s%n", this.inputEncoding == null ? this.fileUtils.getDefaultEncoding() : this.inputEncoding));
        }
        return message.toString();
    }
}

