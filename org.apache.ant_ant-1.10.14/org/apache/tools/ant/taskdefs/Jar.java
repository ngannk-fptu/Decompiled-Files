/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;
import java.util.zip.ZipEntry;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Manifest;
import org.apache.tools.ant.taskdefs.ManifestException;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.ArchiveFileSet;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.ZipFileSet;
import org.apache.tools.ant.types.spi.Service;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.StreamUtils;
import org.apache.tools.zip.JarMarker;
import org.apache.tools.zip.ZipExtraField;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

public class Jar
extends Zip {
    private static final String INDEX_NAME = "META-INF/INDEX.LIST";
    private static final String MANIFEST_NAME = "META-INF/MANIFEST.MF";
    private List<Service> serviceList = new ArrayList<Service>();
    private Manifest configuredManifest;
    private Manifest savedConfiguredManifest;
    private Manifest filesetManifest;
    private Manifest originalManifest;
    private FilesetManifestConfig filesetManifestConfig;
    private boolean mergeManifestsMain = true;
    private Manifest manifest;
    private String manifestEncoding;
    private File manifestFile;
    private boolean index = false;
    private boolean indexMetaInf = false;
    private boolean createEmpty = false;
    private List<String> rootEntries;
    private Path indexJars;
    private FileNameMapper indexJarsMapper = null;
    private StrictMode strict = new StrictMode("ignore");
    private boolean mergeClassPaths = false;
    private boolean flattenClassPaths = false;
    private static final ZipExtraField[] JAR_MARKER = new ZipExtraField[]{JarMarker.getInstance()};

    public Jar() {
        this.archiveType = "jar";
        this.emptyBehavior = "create";
        this.setEncoding("UTF8");
        this.setZip64Mode(Zip.Zip64ModeAttribute.NEVER);
        this.rootEntries = new Vector<String>();
    }

    @Override
    public void setWhenempty(Zip.WhenEmpty we) {
        this.log("JARs are never empty, they contain at least a manifest file", 1);
    }

    public void setWhenmanifestonly(Zip.WhenEmpty we) {
        this.emptyBehavior = we.getValue();
    }

    public void setStrict(StrictMode strict) {
        this.strict = strict;
    }

    @Deprecated
    public void setJarfile(File jarFile) {
        this.setDestFile(jarFile);
    }

    public void setIndex(boolean flag) {
        this.index = flag;
    }

    public void setIndexMetaInf(boolean flag) {
        this.indexMetaInf = flag;
    }

    public void setManifestEncoding(String manifestEncoding) {
        this.manifestEncoding = manifestEncoding;
    }

    public void addConfiguredManifest(Manifest newManifest) throws ManifestException {
        if (this.configuredManifest == null) {
            this.configuredManifest = newManifest;
        } else {
            this.configuredManifest.merge(newManifest, false, this.mergeClassPaths);
        }
        this.savedConfiguredManifest = this.configuredManifest;
    }

    public void setManifest(File manifestFile) {
        if (!manifestFile.exists()) {
            throw new BuildException("Manifest file: " + manifestFile + " does not exist.", this.getLocation());
        }
        this.manifestFile = manifestFile;
    }

    private Manifest getManifest(File manifestFile) {
        Manifest manifest;
        InputStreamReader isr = new InputStreamReader(Files.newInputStream(manifestFile.toPath(), new OpenOption[0]), this.getManifestCharset());
        try {
            manifest = this.getManifest(isr);
        }
        catch (Throwable throwable) {
            try {
                try {
                    isr.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException e) {
                throw new BuildException("Unable to read manifest file: " + manifestFile + " (" + e.getMessage() + ")", e);
            }
        }
        isr.close();
        return manifest;
    }

    private Manifest getManifestFromJar(File jarFile) throws IOException {
        try (java.util.zip.ZipFile zf = new java.util.zip.ZipFile(jarFile);){
            Manifest manifest;
            ZipEntry ze = StreamUtils.enumerationAsStream(zf.entries()).filter(entry -> MANIFEST_NAME.equalsIgnoreCase(entry.getName())).findFirst().orElse(null);
            if (ze == null) {
                Manifest manifest2 = null;
                return manifest2;
            }
            try (InputStreamReader isr = new InputStreamReader(zf.getInputStream(ze), StandardCharsets.UTF_8);){
                manifest = this.getManifest(isr);
            }
            return manifest;
        }
    }

    private Manifest getManifest(Reader r) {
        try {
            return new Manifest(r);
        }
        catch (ManifestException e) {
            this.log("Manifest is invalid: " + e.getMessage(), 0);
            throw new BuildException("Invalid Manifest: " + this.manifestFile, e, this.getLocation());
        }
        catch (IOException e) {
            throw new BuildException("Unable to read manifest file (" + e.getMessage() + ")", e);
        }
    }

    private boolean jarHasIndex(File jarFile) throws IOException {
        try (java.util.zip.ZipFile zf = new java.util.zip.ZipFile(jarFile);){
            boolean bl = StreamUtils.enumerationAsStream(zf.entries()).anyMatch(ze -> INDEX_NAME.equalsIgnoreCase(ze.getName()));
            return bl;
        }
    }

    public void setFilesetmanifest(FilesetManifestConfig config) {
        this.filesetManifestConfig = config;
        boolean bl = this.mergeManifestsMain = config != null && "merge".equals(config.getValue());
        if (this.filesetManifestConfig != null && !"skip".equals(this.filesetManifestConfig.getValue())) {
            this.doubleFilePass = true;
        }
    }

    public void addMetainf(ZipFileSet fs) {
        fs.setPrefix("META-INF/");
        super.addFileset(fs);
    }

    public void addConfiguredIndexJars(Path p) {
        if (this.indexJars == null) {
            this.indexJars = new Path(this.getProject());
        }
        this.indexJars.append(p);
    }

    public void addConfiguredIndexJarsMapper(Mapper mapper) {
        if (this.indexJarsMapper != null) {
            throw new BuildException("Cannot define more than one indexjar-mapper", this.getLocation());
        }
        this.indexJarsMapper = mapper.getImplementation();
    }

    public FileNameMapper getIndexJarsMapper() {
        return this.indexJarsMapper;
    }

    public void addConfiguredService(Service service) {
        service.check();
        this.serviceList.add(service);
    }

    private void writeServices(ZipOutputStream zOut) throws IOException {
        for (Service service : this.serviceList) {
            InputStream is = service.getAsStream();
            try {
                super.zipFile(is, zOut, "META-INF/services/" + service.getType(), System.currentTimeMillis(), null, 33188);
            }
            finally {
                if (is == null) continue;
                is.close();
            }
        }
    }

    public void setMergeClassPathAttributes(boolean b) {
        this.mergeClassPaths = b;
    }

    public void setFlattenAttributes(boolean b) {
        this.flattenClassPaths = b;
    }

    @Override
    protected void initZipOutputStream(ZipOutputStream zOut) throws IOException, BuildException {
        if (!this.skipWriting) {
            Manifest jarManifest = this.createManifest();
            this.writeManifest(zOut, jarManifest);
            this.writeServices(zOut);
        }
    }

    private Manifest createManifest() throws BuildException {
        try {
            Manifest finalManifest;
            boolean mergeFileSetFirst;
            if (this.manifest == null && this.manifestFile != null) {
                this.manifest = this.getManifest(this.manifestFile);
            }
            boolean bl = mergeFileSetFirst = !this.mergeManifestsMain && this.filesetManifest != null && this.configuredManifest == null && this.manifest == null;
            if (mergeFileSetFirst) {
                finalManifest = new Manifest();
                finalManifest.merge(this.filesetManifest, false, this.mergeClassPaths);
                finalManifest.merge(Manifest.getDefaultManifest(), true, this.mergeClassPaths);
            } else {
                finalManifest = Manifest.getDefaultManifest();
            }
            if (this.isInUpdateMode()) {
                finalManifest.merge(this.originalManifest, false, this.mergeClassPaths);
            }
            if (!mergeFileSetFirst) {
                finalManifest.merge(this.filesetManifest, false, this.mergeClassPaths);
            }
            finalManifest.merge(this.configuredManifest, !this.mergeManifestsMain, this.mergeClassPaths);
            finalManifest.merge(this.manifest, !this.mergeManifestsMain, this.mergeClassPaths);
            return finalManifest;
        }
        catch (ManifestException e) {
            this.log("Manifest is invalid: " + e.getMessage(), 0);
            throw new BuildException("Invalid Manifest", e, this.getLocation());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeManifest(ZipOutputStream zOut, Manifest manifest) throws IOException {
        StreamUtils.enumerationAsStream(manifest.getWarnings()).forEach(warning -> this.log("Manifest warning: " + warning, 1));
        this.zipDir((Resource)null, zOut, "META-INF/", 16877, JAR_MARKER);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter((OutputStream)baos, Manifest.JAR_CHARSET);
        PrintWriter writer = new PrintWriter(osw);
        manifest.write(writer, this.flattenClassPaths);
        if (writer.checkError()) {
            throw new IOException("Encountered an error writing the manifest");
        }
        writer.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        try {
            super.zipFile(bais, zOut, MANIFEST_NAME, System.currentTimeMillis(), null, 33188);
        }
        finally {
            FileUtils.close(bais);
        }
        super.initZipOutputStream(zOut);
    }

    @Override
    protected void finalizeZipOutputStream(ZipOutputStream zOut) throws IOException, BuildException {
        if (this.index) {
            this.createIndexList(zOut);
        }
    }

    private void createIndexList(ZipOutputStream zOut) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter((OutputStream)baos, StandardCharsets.UTF_8));
        writer.println("JarIndex-Version: 1.0");
        writer.println();
        writer.println(this.zipFile.getName());
        this.writeIndexLikeList(new ArrayList<String>(this.addedDirs.keySet()), this.rootEntries, writer);
        writer.println();
        if (this.indexJars != null) {
            FileNameMapper mapper = this.indexJarsMapper;
            if (mapper == null) {
                mapper = this.createDefaultIndexJarsMapper();
            }
            for (String indexJarEntry : this.indexJars.list()) {
                String[] names = mapper.mapFileName(indexJarEntry);
                if (names == null || names.length <= 0) continue;
                ArrayList<String> dirs = new ArrayList<String>();
                ArrayList<String> files = new ArrayList<String>();
                Jar.grabFilesAndDirs(indexJarEntry, dirs, files);
                if (dirs.size() + files.size() <= 0) continue;
                writer.println(names[0]);
                this.writeIndexLikeList(dirs, files, writer);
                writer.println();
            }
        }
        if (writer.checkError()) {
            throw new IOException("Encountered an error writing jar index");
        }
        writer.close();
        try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());){
            super.zipFile(bais, zOut, INDEX_NAME, System.currentTimeMillis(), null, 33188);
        }
    }

    private FileNameMapper createDefaultIndexJarsMapper() {
        Manifest mf = this.createManifest();
        Manifest.Attribute classpath = mf.getMainSection().getAttribute("Class-Path");
        String[] cpEntries = null;
        if (classpath != null && classpath.getValue() != null) {
            StringTokenizer tok = new StringTokenizer(classpath.getValue(), " ");
            cpEntries = new String[tok.countTokens()];
            int c = 0;
            while (tok.hasMoreTokens()) {
                cpEntries[c++] = tok.nextToken();
            }
        }
        return new IndexJarsFilenameMapper(cpEntries);
    }

    @Override
    protected void zipFile(InputStream is, ZipOutputStream zOut, String vPath, long lastModified, File fromArchive, int mode) throws IOException {
        if (MANIFEST_NAME.equalsIgnoreCase(vPath)) {
            if (this.isFirstPass()) {
                this.filesetManifest(fromArchive, is);
            }
        } else if (INDEX_NAME.equalsIgnoreCase(vPath) && this.index) {
            this.logWhenWriting("Warning: selected " + this.archiveType + " files include a " + INDEX_NAME + " which will be replaced by a newly generated one.", 1);
        } else {
            if (this.index && !vPath.contains("/")) {
                this.rootEntries.add(vPath);
            }
            super.zipFile(is, zOut, vPath, lastModified, fromArchive, mode);
        }
    }

    private void filesetManifest(File file, InputStream is) throws IOException {
        if (this.manifestFile != null && this.manifestFile.equals(file)) {
            this.log("Found manifest " + file, 3);
            if (is == null) {
                this.manifest = this.getManifest(file);
            } else {
                try (InputStreamReader isr = new InputStreamReader(is, this.getManifestCharset());){
                    this.manifest = this.getManifest(isr);
                }
            }
        } else if (this.filesetManifestConfig != null && !"skip".equals(this.filesetManifestConfig.getValue())) {
            this.logWhenWriting("Found manifest to merge in file " + file, 3);
            try {
                Manifest newManifest;
                if (is == null) {
                    newManifest = this.getManifest(file);
                } else {
                    try (InputStreamReader isr = new InputStreamReader(is, this.getManifestCharset());){
                        newManifest = this.getManifest(isr);
                    }
                }
                if (this.filesetManifest == null) {
                    this.filesetManifest = newManifest;
                } else {
                    this.filesetManifest.merge(newManifest, false, this.mergeClassPaths);
                }
            }
            catch (UnsupportedEncodingException e) {
                throw new BuildException("Unsupported encoding while reading manifest: " + e.getMessage(), e);
            }
            catch (ManifestException e) {
                this.log("Manifest in file " + file + " is invalid: " + e.getMessage(), 0);
                throw new BuildException("Invalid Manifest", e, this.getLocation());
            }
        }
    }

    @Override
    protected Zip.ArchiveState getResourcesToAdd(ResourceCollection[] rcs, File zipFile, boolean needsUpdate) throws BuildException {
        if (this.skipWriting) {
            Resource[][] manifests = this.grabManifests(rcs);
            int count = 0;
            for (Resource[] mf : manifests) {
                count += mf.length;
            }
            this.log("found a total of " + count + " manifests in " + manifests.length + " resource collections", 3);
            return new Zip.ArchiveState(true, manifests);
        }
        if (zipFile.exists()) {
            try {
                this.originalManifest = this.getManifestFromJar(zipFile);
                if (this.originalManifest == null) {
                    this.log("Updating jar since the current jar has no manifest", 3);
                    needsUpdate = true;
                } else {
                    Manifest mf = this.createManifest();
                    if (!mf.equals(this.originalManifest)) {
                        this.log("Updating jar since jar manifest has changed", 3);
                        needsUpdate = true;
                    }
                }
            }
            catch (Throwable t) {
                this.log("error while reading original manifest in file: " + zipFile.toString() + " due to " + t.getMessage(), 1);
                needsUpdate = true;
            }
        } else {
            needsUpdate = true;
        }
        this.createEmpty = needsUpdate;
        if (!needsUpdate && this.index) {
            try {
                needsUpdate = !this.jarHasIndex(zipFile);
            }
            catch (IOException e) {
                needsUpdate = true;
            }
        }
        return super.getResourcesToAdd(rcs, zipFile, needsUpdate);
    }

    @Override
    protected boolean createEmptyZip(File zipFile) throws BuildException {
        if (!this.createEmpty) {
            return true;
        }
        if ("skip".equals(this.emptyBehavior)) {
            if (!this.skipWriting) {
                this.log("Warning: skipping " + this.archiveType + " archive " + zipFile + " because no files were included.", 1);
            }
            return true;
        }
        if ("fail".equals(this.emptyBehavior)) {
            throw new BuildException("Cannot create " + this.archiveType + " archive " + zipFile + ": no files were included.", this.getLocation());
        }
        if (!this.skipWriting) {
            this.log("Building MANIFEST-only jar: " + this.getDestFile().getAbsolutePath());
            try (ZipOutputStream zOut = new ZipOutputStream(this.getDestFile());){
                zOut.setEncoding(this.getEncoding());
                zOut.setUseZip64(this.getZip64Mode().getMode());
                if (this.isCompress()) {
                    zOut.setMethod(8);
                } else {
                    zOut.setMethod(0);
                }
                this.initZipOutputStream(zOut);
                this.finalizeZipOutputStream(zOut);
            }
            catch (IOException ioe) {
                throw new BuildException("Could not create almost empty JAR archive (" + ioe.getMessage() + ")", ioe, this.getLocation());
            }
            finally {
                this.createEmpty = false;
            }
        }
        return true;
    }

    @Override
    protected void cleanUp() {
        super.cleanUp();
        this.checkJarSpec();
        if (!this.doubleFilePass || !this.skipWriting) {
            this.manifest = null;
            this.configuredManifest = this.savedConfiguredManifest;
            this.filesetManifest = null;
            this.originalManifest = null;
        }
        this.rootEntries.clear();
    }

    private void checkJarSpec() {
        Manifest.Section mainSection;
        StringBuilder message = new StringBuilder();
        Manifest.Section section = mainSection = this.configuredManifest == null ? null : this.configuredManifest.getMainSection();
        if (mainSection == null) {
            message.append("No Implementation-Title set.");
            message.append("No Implementation-Version set.");
            message.append("No Implementation-Vendor set.");
        } else {
            if (mainSection.getAttribute("Implementation-Title") == null) {
                message.append("No Implementation-Title set.");
            }
            if (mainSection.getAttribute("Implementation-Version") == null) {
                message.append("No Implementation-Version set.");
            }
            if (mainSection.getAttribute("Implementation-Vendor") == null) {
                message.append("No Implementation-Vendor set.");
            }
        }
        if (message.length() > 0) {
            message.append(String.format("%nLocation: %s%n", this.getLocation()));
            if ("fail".equalsIgnoreCase(this.strict.getValue())) {
                throw new BuildException(message.toString(), this.getLocation());
            }
            this.logWhenWriting(message.toString(), this.strict.getLogLevel());
        }
    }

    @Override
    public void reset() {
        super.reset();
        this.emptyBehavior = "create";
        this.configuredManifest = null;
        this.filesetManifestConfig = null;
        this.mergeManifestsMain = false;
        this.manifestFile = null;
        this.index = false;
    }

    protected final void writeIndexLikeList(List<String> dirs, List<String> files, PrintWriter writer) {
        Collections.sort(dirs);
        Collections.sort(files);
        for (String dir : dirs) {
            if ((dir = dir.replace('\\', '/')).startsWith("./")) {
                dir = dir.substring(2);
            }
            while (dir.startsWith("/")) {
                dir = dir.substring(1);
            }
            int pos = dir.lastIndexOf(47);
            if (pos != -1) {
                dir = dir.substring(0, pos);
            }
            if (!this.indexMetaInf && dir.startsWith("META-INF")) continue;
            writer.println(dir);
        }
        files.forEach(writer::println);
    }

    protected static String findJarName(String fileName, String[] classpath) {
        if (classpath == null) {
            return new File(fileName).getName();
        }
        fileName = fileName.replace(File.separatorChar, '/');
        TreeMap<String, String> matches = new TreeMap<String, String>(Comparator.comparingInt(s -> s == null ? 0 : s.length()).reversed());
        String[] stringArray = classpath;
        int n = stringArray.length;
        block0: for (int i = 0; i < n; ++i) {
            String element;
            String candidate = element = stringArray[i];
            while (true) {
                if (fileName.endsWith(candidate)) {
                    matches.put(candidate, element);
                    continue block0;
                }
                int slash = candidate.indexOf(47);
                if (slash < 0) continue block0;
                candidate = candidate.substring(slash + 1);
            }
        }
        return matches.isEmpty() ? null : (String)matches.get(matches.firstKey());
    }

    protected static void grabFilesAndDirs(String file, List<String> dirs, List<String> files) throws IOException {
        try (ZipFile zf = new ZipFile(file, "utf-8");){
            HashSet dirSet = new HashSet();
            StreamUtils.enumerationAsStream(zf.getEntries()).forEach(ze -> {
                String name = ze.getName();
                if (ze.isDirectory()) {
                    dirSet.add(name);
                } else if (!name.contains("/")) {
                    files.add(name);
                } else {
                    dirSet.add(name.substring(0, name.lastIndexOf(47) + 1));
                }
            });
            dirs.addAll(dirSet);
        }
    }

    private Resource[][] grabManifests(ResourceCollection[] rcs) {
        Resource[][] manifests = new Resource[rcs.length][];
        for (int i = 0; i < rcs.length; ++i) {
            Resource[][] resources = rcs[i] instanceof FileSet ? this.grabResources(new FileSet[]{(FileSet)rcs[i]}) : this.grabNonFileSetResources(new ResourceCollection[]{rcs[i]});
            for (int j = 0; j < resources[0].length; ++j) {
                String name = resources[0][j].getName().replace('\\', '/');
                if (rcs[i] instanceof ArchiveFileSet) {
                    ArchiveFileSet afs = (ArchiveFileSet)rcs[i];
                    if (!afs.getFullpath(this.getProject()).isEmpty()) {
                        name = afs.getFullpath(this.getProject());
                    } else if (!afs.getPrefix(this.getProject()).isEmpty()) {
                        String prefix = afs.getPrefix(this.getProject());
                        if (!prefix.endsWith("/") && !prefix.endsWith("\\")) {
                            prefix = prefix + "/";
                        }
                        name = prefix + name;
                    }
                }
                if (!MANIFEST_NAME.equalsIgnoreCase(name)) continue;
                manifests[i] = new Resource[]{resources[0][j]};
                break;
            }
            if (manifests[i] != null) continue;
            manifests[i] = new Resource[0];
        }
        return manifests;
    }

    private Charset getManifestCharset() {
        if (this.manifestEncoding == null) {
            return Charset.defaultCharset();
        }
        try {
            return Charset.forName(this.manifestEncoding);
        }
        catch (IllegalArgumentException e) {
            throw new BuildException("Unsupported encoding while reading manifest: " + e.getMessage(), e);
        }
    }

    public static class StrictMode
    extends EnumeratedAttribute {
        public StrictMode() {
        }

        public StrictMode(String value) {
            this.setValue(value);
        }

        @Override
        public String[] getValues() {
            return new String[]{"fail", "warn", "ignore"};
        }

        public int getLogLevel() {
            return "ignore".equals(this.getValue()) ? 3 : 1;
        }
    }

    public static class FilesetManifestConfig
    extends EnumeratedAttribute {
        @Override
        public String[] getValues() {
            return new String[]{"skip", "merge", "mergewithoutmain"};
        }
    }

    private static class IndexJarsFilenameMapper
    implements FileNameMapper {
        private String[] classpath;

        IndexJarsFilenameMapper(String[] classpath) {
            this.classpath = classpath;
        }

        @Override
        public void setFrom(String from) {
        }

        @Override
        public void setTo(String to) {
        }

        @Override
        public String[] mapFileName(String sourceFileName) {
            String[] stringArray;
            String result = Jar.findJarName(sourceFileName, this.classpath);
            if (result == null) {
                stringArray = null;
            } else {
                String[] stringArray2 = new String[1];
                stringArray = stringArray2;
                stringArray2[0] = result;
            }
            return stringArray;
        }
    }
}

