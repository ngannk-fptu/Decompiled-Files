/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.types.selectors.SelectorUtils;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.IdentityMapper;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

public class Expand
extends Task {
    public static final String NATIVE_ENCODING = "native-encoding";
    public static final String ERROR_MULTIPLE_MAPPERS = "Cannot define more than one mapper";
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private static final int BUFFER_SIZE = 1024;
    private File dest;
    private File source;
    private boolean overwrite = true;
    private Mapper mapperElement = null;
    private List<PatternSet> patternsets = new Vector<PatternSet>();
    private Union resources = new Union();
    private boolean resourcesSpecified = false;
    private boolean failOnEmptyArchive = false;
    private boolean stripAbsolutePathSpec = true;
    private boolean scanForUnicodeExtraFields = true;
    private Boolean allowFilesToEscapeDest = null;
    private String encoding;

    public Expand() {
        this("UTF8");
    }

    protected Expand(String encoding) {
        this.encoding = encoding;
    }

    public void setFailOnEmptyArchive(boolean b) {
        this.failOnEmptyArchive = b;
    }

    public boolean getFailOnEmptyArchive() {
        return this.failOnEmptyArchive;
    }

    @Override
    public void execute() throws BuildException {
        if ("expand".equals(this.getTaskType())) {
            this.log("!! expand is deprecated. Use unzip instead. !!");
        }
        if (this.source == null && !this.resourcesSpecified) {
            throw new BuildException("src attribute and/or resources must be specified");
        }
        if (this.dest == null) {
            throw new BuildException("Dest attribute must be specified");
        }
        if (this.dest.exists() && !this.dest.isDirectory()) {
            throw new BuildException("Dest must be a directory.", this.getLocation());
        }
        if (this.source != null) {
            if (this.source.isDirectory()) {
                throw new BuildException("Src must not be a directory. Use nested filesets instead.", this.getLocation());
            }
            if (!this.source.exists()) {
                throw new BuildException("src '" + this.source + "' doesn't exist.");
            }
            if (!this.source.canRead()) {
                throw new BuildException("src '" + this.source + "' cannot be read.");
            }
            this.expandFile(FILE_UTILS, this.source, this.dest);
        }
        for (Resource r : this.resources) {
            if (!r.isExists()) {
                this.log("Skipping '" + r.getName() + "' because it doesn't exist.");
                continue;
            }
            FileProvider fp = r.as(FileProvider.class);
            if (fp != null) {
                this.expandFile(FILE_UTILS, fp.getFile(), this.dest);
                continue;
            }
            this.expandResource(r, this.dest);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void expandFile(FileUtils fileUtils, File srcF, File dir) {
        this.log("Expanding: " + srcF + " into " + dir, 2);
        FileNameMapper mapper = this.getMapper();
        if (!srcF.exists()) {
            throw new BuildException("Unable to expand " + srcF + " as the file does not exist", this.getLocation());
        }
        try (ZipFile zf = new ZipFile(srcF, this.encoding, this.scanForUnicodeExtraFields);){
            boolean empty = true;
            Enumeration<ZipEntry> entries = zf.getEntries();
            while (entries.hasMoreElements()) {
                ZipEntry ze = entries.nextElement();
                empty = false;
                InputStream is = null;
                this.log("extracting " + ze.getName(), 4);
                try {
                    is = zf.getInputStream(ze);
                    this.extractFile(fileUtils, srcF, dir, is, ze.getName(), new Date(ze.getTime()), ze.isDirectory(), mapper);
                }
                catch (Throwable throwable) {
                    FileUtils.close(is);
                    throw throwable;
                }
                FileUtils.close(is);
            }
            if (empty && this.getFailOnEmptyArchive()) {
                throw new BuildException("archive '%s' is empty", srcF);
            }
            this.log("expand complete", 3);
        }
        catch (IOException ioe) {
            throw new BuildException("Error while expanding " + srcF.getPath() + "\n" + ioe.toString(), ioe);
        }
    }

    protected void expandResource(Resource srcR, File dir) {
        throw new BuildException("only filesystem based resources are supported by this task.");
    }

    protected FileNameMapper getMapper() {
        if (this.mapperElement != null) {
            return this.mapperElement.getImplementation();
        }
        return new IdentityMapper();
    }

    protected void extractFile(FileUtils fileUtils, File srcF, File dir, InputStream compressedInputStream, String entryName, Date entryDate, boolean isDirectory, FileNameMapper mapper) throws IOException {
        String[] mappedNames;
        boolean allowedOutsideOfDest;
        boolean entryNameStartsWithPathSpec;
        boolean bl = entryNameStartsWithPathSpec = !entryName.isEmpty() && (entryName.charAt(0) == File.separatorChar || entryName.charAt(0) == '/' || entryName.charAt(0) == '\\');
        if (this.stripAbsolutePathSpec && entryNameStartsWithPathSpec) {
            this.log("stripped absolute path spec from " + entryName, 3);
            entryName = entryName.substring(1);
        }
        boolean bl2 = allowedOutsideOfDest = Boolean.TRUE == this.getAllowFilesToEscapeDest() || null == this.getAllowFilesToEscapeDest() && !this.stripAbsolutePathSpec && entryNameStartsWithPathSpec;
        if (this.patternsets != null && !this.patternsets.isEmpty()) {
            String name = entryName.replace('/', File.separatorChar).replace('\\', File.separatorChar);
            HashSet<String> includePatterns = new HashSet<String>();
            HashSet<String> excludePatterns = new HashSet<String>();
            for (PatternSet patternSet : this.patternsets) {
                String[] incls = patternSet.getIncludePatterns(this.getProject());
                if (incls == null || incls.length == 0) {
                    incls = new String[]{"**"};
                }
                for (String incl : incls) {
                    String pattern = incl.replace('/', File.separatorChar).replace('\\', File.separatorChar);
                    if (pattern.endsWith(File.separator)) {
                        pattern = pattern + "**";
                    }
                    includePatterns.add(pattern);
                }
                String[] excls = patternSet.getExcludePatterns(this.getProject());
                if (excls == null) continue;
                for (String excl : excls) {
                    String pattern = excl.replace('/', File.separatorChar).replace('\\', File.separatorChar);
                    if (pattern.endsWith(File.separator)) {
                        pattern = pattern + "**";
                    }
                    excludePatterns.add(pattern);
                }
            }
            boolean included = false;
            for (String pattern : includePatterns) {
                if (!SelectorUtils.matchPath(pattern, name)) continue;
                included = true;
                break;
            }
            for (String pattern : excludePatterns) {
                if (!SelectorUtils.matchPath(pattern, name)) continue;
                included = false;
                break;
            }
            if (!included) {
                this.log("skipping " + entryName + " as it is excluded or not included.", 3);
                return;
            }
        }
        if ((mappedNames = mapper.mapFileName(entryName)) == null || mappedNames.length == 0) {
            mappedNames = new String[]{entryName};
        }
        File f = fileUtils.resolveFile(dir, mappedNames[0]);
        if (!allowedOutsideOfDest && !fileUtils.isLeadingPath(dir, f, true)) {
            this.log("skipping " + entryName + " as its target " + f.getCanonicalPath() + " is outside of " + dir.getCanonicalPath() + ".", 3);
            return;
        }
        try {
            if (!this.overwrite && f.exists() && f.lastModified() >= entryDate.getTime()) {
                this.log("Skipping " + f + " as it is up-to-date", 4);
                return;
            }
            this.log("expanding " + entryName + " to " + f, 3);
            File dirF = f.getParentFile();
            if (dirF != null) {
                dirF.mkdirs();
            }
            if (isDirectory) {
                f.mkdirs();
            } else {
                byte[] buffer = new byte[1024];
                try (OutputStream outputStream = Files.newOutputStream(f.toPath(), new OpenOption[0]);){
                    int length;
                    while ((length = compressedInputStream.read(buffer)) >= 0) {
                        outputStream.write(buffer, 0, length);
                    }
                }
            }
            fileUtils.setFileLastModified(f, entryDate.getTime());
        }
        catch (FileNotFoundException ex) {
            this.log("Unable to expand to file " + f.getPath(), ex, 1);
        }
    }

    public void setDest(File d) {
        this.dest = d;
    }

    public void setSrc(File s) {
        this.source = s;
    }

    public void setOverwrite(boolean b) {
        this.overwrite = b;
    }

    public void addPatternset(PatternSet set) {
        this.patternsets.add(set);
    }

    public void addFileset(FileSet set) {
        this.add(set);
    }

    public void add(ResourceCollection rc) {
        this.resourcesSpecified = true;
        this.resources.add(rc);
    }

    public Mapper createMapper() throws BuildException {
        if (this.mapperElement != null) {
            throw new BuildException(ERROR_MULTIPLE_MAPPERS, this.getLocation());
        }
        this.mapperElement = new Mapper(this.getProject());
        return this.mapperElement;
    }

    public void add(FileNameMapper fileNameMapper) {
        this.createMapper().add(fileNameMapper);
    }

    public void setEncoding(String encoding) {
        this.internalSetEncoding(encoding);
    }

    protected void internalSetEncoding(String encoding) {
        if (NATIVE_ENCODING.equals(encoding)) {
            encoding = null;
        }
        this.encoding = encoding;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setStripAbsolutePathSpec(boolean b) {
        this.stripAbsolutePathSpec = b;
    }

    public void setScanForUnicodeExtraFields(boolean b) {
        this.internalSetScanForUnicodeExtraFields(b);
    }

    protected void internalSetScanForUnicodeExtraFields(boolean b) {
        this.scanForUnicodeExtraFields = b;
    }

    public boolean getScanForUnicodeExtraFields() {
        return this.scanForUnicodeExtraFields;
    }

    public void setAllowFilesToEscapeDest(boolean b) {
        this.allowFilesToEscapeDest = b;
    }

    public Boolean getAllowFilesToEscapeDest() {
        return this.allowFilesToEscapeDest;
    }
}

