/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi;

import aQute.bnd.osgi.EmbeddedResource;
import aQute.bnd.osgi.FileResource;
import aQute.bnd.osgi.Instruction;
import aQute.bnd.osgi.JarResource;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.Resource;
import aQute.bnd.osgi.Verifier;
import aQute.bnd.osgi.ZipResource;
import aQute.bnd.version.Version;
import aQute.lib.base64.Base64;
import aQute.lib.io.IO;
import aQute.lib.io.NonClosingInputStream;
import aQute.lib.zip.ZipUtil;
import aQute.service.reporter.Reporter;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Jar
implements Closeable {
    static final int BUFFER_SIZE = 65536;
    static final String DEFAULT_MANIFEST_NAME = "META-INF/MANIFEST.MF";
    private static final Pattern DEFAULT_DO_NOT_COPY = Pattern.compile("CVS|\\.svn|\\.git|\\.DS_Store");
    public static final Object[] EMPTY_ARRAY = new Jar[0];
    final TreeMap<String, Resource> resources = new TreeMap();
    final TreeMap<String, Map<String, Resource>> directories = new TreeMap();
    Manifest manifest;
    boolean manifestFirst;
    String manifestName = "META-INF/MANIFEST.MF";
    String name;
    File source;
    ZipFile zipFile;
    long lastModified;
    String lastModifiedReason;
    Reporter reporter;
    boolean doNotTouchManifest;
    boolean nomanifest;
    Compression compression = Compression.DEFLATE;
    boolean closed;
    String[] algorithms;
    private static final byte[] EOL = new byte[]{13, 10};
    private static final byte[] SEPARATOR = new byte[]{58, 32};
    static final Pattern BSN = Pattern.compile("\\s*([-\\w\\d\\._]+)\\s*;?.*");
    static Pattern SIGNER_FILES_P = Pattern.compile("(.+\\.(SF|DSA|RSA))|(.*/SIG-.*)", 2);

    public Jar(String name) {
        this.name = name;
    }

    public Jar(String name, File dirOrFile, Pattern doNotCopy) throws ZipException, IOException {
        this(name);
        this.source = dirOrFile;
        if (dirOrFile.isDirectory()) {
            this.buildFromDirectory(dirOrFile.toPath().toAbsolutePath(), doNotCopy);
        } else if (dirOrFile.isFile()) {
            this.buildFromZip(dirOrFile);
        } else {
            throw new IllegalArgumentException("A Jar can only accept a file or directory that exists: " + dirOrFile);
        }
    }

    public Jar(String name, InputStream in, long lastModified) throws IOException {
        this(name);
        this.buildFromInputStream(in, lastModified);
    }

    public static Jar fromResource(String name, Resource resource) throws Exception {
        if (resource instanceof JarResource) {
            return ((JarResource)resource).getJar();
        }
        if (resource instanceof FileResource) {
            return new Jar(name, ((FileResource)resource).getFile());
        }
        return new Jar(name).buildFromResource(resource);
    }

    public Jar(String name, String path) throws IOException {
        this(name, new File(path));
    }

    public Jar(File f) throws IOException {
        this(Jar.getName(f), f, null);
    }

    private static String getName(File f) {
        String name = (f = f.getAbsoluteFile()).getName();
        if (name.equals("bin") || name.equals("src")) {
            return f.getParentFile().getName();
        }
        if (name.endsWith(".jar")) {
            name = name.substring(0, name.length() - 4);
        }
        return name;
    }

    public Jar(String string, InputStream resourceAsStream) throws IOException {
        this(string, resourceAsStream, 0L);
    }

    public Jar(String string, File file) throws ZipException, IOException {
        this(string, file, DEFAULT_DO_NOT_COPY);
    }

    private Jar buildFromDirectory(final Path baseDir, final Pattern doNotCopy) throws IOException {
        Files.walkFileTree(baseDir, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(){

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                String name;
                if (doNotCopy != null && doNotCopy.matcher(name = dir.getFileName().toString()).matches()) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                Jar.this.updateModified(attrs.lastModifiedTime().toMillis(), "Dir change " + dir);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String relativePath = baseDir.relativize(file).toString();
                if (File.separatorChar != '/') {
                    relativePath = relativePath.replace(File.separatorChar, '/');
                }
                Jar.this.putResource(relativePath, new FileResource(file, attrs), true);
                return FileVisitResult.CONTINUE;
            }
        });
        return this;
    }

    private Jar buildFromZip(File file) throws IOException {
        try {
            this.zipFile = new ZipFile(file);
            Enumeration<? extends ZipEntry> e = this.zipFile.entries();
            while (e.hasMoreElements()) {
                ZipEntry entry = e.nextElement();
                if (entry.isDirectory()) continue;
                this.putResource(entry.getName(), new ZipResource(this.zipFile, entry), true);
            }
            return this;
        }
        catch (ZipException e) {
            ZipException ze = new ZipException("The JAR/ZIP file (" + file.getAbsolutePath() + ") seems corrupted, error: " + e.getMessage());
            ze.initCause(e);
            throw ze;
        }
        catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Problem opening JAR: " + file.getAbsolutePath());
        }
    }

    private Jar buildFromResource(Resource resource) throws Exception {
        return this.buildFromInputStream(resource.openInputStream(), resource.lastModified());
    }

    private Jar buildFromInputStream(InputStream in, long lastModified) throws IOException {
        try (ZipInputStream jin = new ZipInputStream(in);){
            ZipEntry entry;
            NonClosingInputStream noclose = new NonClosingInputStream(jin);
            while ((entry = jin.getNextEntry()) != null) {
                if (entry.isDirectory()) continue;
                long size = entry.getSize();
                byte[] data = size == -1L ? IO.read(noclose) : IO.copy((InputStream)noclose, new byte[(int)size]);
                this.putResource(entry.getName(), new EmbeddedResource(data, lastModified), true);
            }
        }
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "Jar:" + this.name;
    }

    public boolean putResource(String path, Resource resource) {
        this.check();
        return this.putResource(path, resource, true);
    }

    public boolean putResource(String path, Resource resource, boolean overwrite) {
        boolean duplicate;
        String dir;
        Map<String, Resource> s;
        this.check();
        this.updateModified(resource.lastModified(), path);
        while (path.startsWith("/")) {
            path = path.substring(1);
        }
        if (path.equals(this.manifestName)) {
            this.manifest = null;
            if (this.resources.isEmpty()) {
                this.manifestFirst = true;
            }
        }
        if ((s = this.directories.get(dir = this.getDirectory(path))) == null) {
            String dd;
            s = new TreeMap<String, Resource>();
            this.directories.put(dir, s);
            int n = dir.lastIndexOf(47);
            while (n > 0 && !this.directories.containsKey(dd = dir.substring(0, n))) {
                this.directories.put(dd, null);
                n = dd.lastIndexOf(47);
            }
        }
        if (!(duplicate = s.containsKey(path)) || overwrite) {
            this.resources.put(path, resource);
            s.put(path, resource);
        }
        return duplicate;
    }

    public Resource getResource(String path) {
        this.check();
        if (this.resources == null) {
            return null;
        }
        return this.resources.get(path);
    }

    private String getDirectory(String path) {
        this.check();
        int n = path.lastIndexOf(47);
        if (n < 0) {
            return "";
        }
        return path.substring(0, n);
    }

    public Map<String, Map<String, Resource>> getDirectories() {
        this.check();
        return this.directories;
    }

    public Map<String, Resource> getResources() {
        this.check();
        return this.resources;
    }

    public boolean addDirectory(Map<String, Resource> directory, boolean overwrite) {
        this.check();
        boolean duplicates = false;
        if (directory == null) {
            return false;
        }
        for (Map.Entry<String, Resource> entry : directory.entrySet()) {
            String key = entry.getKey();
            duplicates |= this.putResource(key, entry.getValue(), overwrite);
        }
        return duplicates;
    }

    public Manifest getManifest() throws Exception {
        Resource manifestResource;
        this.check();
        if (this.manifest == null && (manifestResource = this.getResource(this.manifestName)) != null) {
            try (InputStream in = manifestResource.openInputStream();){
                this.manifest = new Manifest(in);
            }
        }
        return this.manifest;
    }

    public boolean exists(String path) {
        this.check();
        return this.resources.containsKey(path);
    }

    public void setManifest(Manifest manifest) {
        this.check();
        this.manifestFirst = true;
        this.manifest = manifest;
    }

    public void setManifest(File file) throws IOException {
        this.check();
        try (InputStream fin = IO.stream(file);){
            Manifest m = new Manifest(fin);
            this.setManifest(m);
        }
    }

    public void setManifestName(String manifestName) {
        this.check();
        if (manifestName == null || manifestName.length() == 0) {
            throw new IllegalArgumentException("Manifest name cannot be null or empty!");
        }
        this.manifestName = manifestName;
    }

    public void write(File file) throws Exception {
        this.check();
        try (OutputStream out = IO.outputStream(file);){
            this.write(out);
        }
        catch (Exception t) {
            IO.delete(file);
            throw t;
        }
        file.setLastModified(this.lastModified);
    }

    public void write(String file) throws Exception {
        this.check();
        this.write(new File(file));
    }

    public void write(OutputStream out) throws Exception {
        this.check();
        if (!this.doNotTouchManifest && !this.nomanifest && this.algorithms != null) {
            this.doChecksums(out);
            return;
        }
        ZipOutputStream jout = this.nomanifest || this.doNotTouchManifest ? new ZipOutputStream(out) : new JarOutputStream(out);
        switch (this.compression) {
            case STORE: {
                jout.setMethod(8);
                break;
            }
        }
        HashSet<String> done = new HashSet<String>();
        HashSet<String> directories = new HashSet<String>();
        if (this.doNotTouchManifest) {
            Resource r = this.getResource(this.manifestName);
            if (r != null) {
                this.writeResource(jout, directories, this.manifestName, r);
                done.add(this.manifestName);
            }
        } else {
            this.doManifest(done, jout);
        }
        for (Map.Entry<String, Resource> entry : this.getResources().entrySet()) {
            if (done.contains(entry.getKey())) continue;
            this.writeResource(jout, directories, entry.getKey(), entry.getValue());
        }
        jout.finish();
    }

    public void writeFolder(File dir) throws Exception {
        IO.mkdirs(dir);
        if (!dir.exists()) {
            throw new IllegalArgumentException("The directory " + dir + " to write the JAR " + this + " could not be created");
        }
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("The directory " + dir + " to write the JAR " + this + " to is not a directory");
        }
        this.check();
        HashSet<String> done = new HashSet<String>();
        HashSet directories = new HashSet();
        if (this.doNotTouchManifest) {
            Resource r = this.getResource(this.manifestName);
            if (r != null) {
                this.copyResource(dir, this.manifestName, r);
                done.add(this.manifestName);
            }
        } else {
            File file = IO.getFile(dir, this.manifestName);
            IO.mkdirs(file.getParentFile());
            try (OutputStream fout = IO.outputStream(file);){
                this.writeManifest(fout);
                done.add(this.manifestName);
            }
        }
        for (Map.Entry<String, Resource> entry : this.getResources().entrySet()) {
            String path = entry.getKey();
            if (done.contains(path)) continue;
            Resource resource = entry.getValue();
            this.copyResource(dir, path, resource);
        }
    }

    private void copyResource(File dir, String path, Resource resource) throws IOException, Exception {
        File to = IO.getFile(dir, path);
        IO.mkdirs(to.getParentFile());
        IO.copy(resource.openInputStream(), to);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doChecksums(OutputStream out) throws IOException, Exception {
        String[] algs = this.algorithms;
        this.algorithms = null;
        try {
            File f = File.createTempFile(this.padString(this.getName(), 3, '_'), ".jar");
            this.write(f);
            try (Jar tmp = new Jar(f);){
                tmp.calcChecksums(this.algorithms);
                tmp.write(out);
            }
            finally {
                IO.delete(f);
            }
        }
        finally {
            this.algorithms = algs;
        }
    }

    private String padString(String s, int length, char pad) {
        if (s == null) {
            s = "";
        }
        if (s.length() >= length) {
            return s;
        }
        char[] cs = new char[length];
        Arrays.fill(cs, pad);
        char[] orig = s.toCharArray();
        System.arraycopy(orig, 0, cs, 0, orig.length);
        return new String(cs);
    }

    private void doManifest(Set<String> done, ZipOutputStream jout) throws Exception {
        this.check();
        if (this.nomanifest) {
            return;
        }
        JarEntry ze = new JarEntry(this.manifestName);
        ZipUtil.setModifiedTime(ze, this.lastModified);
        jout.putNextEntry(ze);
        this.writeManifest(jout);
        jout.closeEntry();
        done.add(ze.getName());
    }

    public void writeManifest(OutputStream out) throws Exception {
        this.check();
        this.stripSignatures();
        Jar.writeManifest(this.getManifest(), out);
    }

    public static void writeManifest(Manifest manifest, OutputStream out) throws IOException {
        if (manifest == null) {
            return;
        }
        manifest = Jar.clean(manifest);
        Jar.outputManifest(manifest, out);
    }

    public static void outputManifest(Manifest manifest, OutputStream out) throws IOException {
        Jar.writeEntry(out, "Manifest-Version", "1.0");
        Jar.attributes(manifest.getMainAttributes(), out);
        TreeSet<String> keys = new TreeSet<String>();
        for (String o : manifest.getEntries().keySet()) {
            keys.add(o.toString());
        }
        for (String key : keys) {
            out.write(EOL);
            Jar.writeEntry(out, "Name", key);
            Jar.attributes(manifest.getAttributes(key), out);
        }
        out.flush();
    }

    private static void writeEntry(OutputStream out, String name, String value) throws IOException {
        int width = Jar.write(out, 0, name);
        width = Jar.write(out, width, SEPARATOR);
        Jar.write(out, width, value);
        out.write(EOL);
    }

    private static int write(OutputStream out, int width, String s) throws IOException {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        return Jar.write(out, width, bytes);
    }

    private static int write(OutputStream out, int width, byte[] bytes) throws IOException {
        int w = width;
        for (int i = 0; i < bytes.length; ++i) {
            if (w >= 72 - EOL.length) {
                out.write(EOL);
                out.write(32);
                w = 1;
            }
            out.write(bytes[i]);
            ++w;
        }
        return w;
    }

    private static void attributes(Attributes value, OutputStream out) throws IOException {
        TreeMap<String, String> map = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        for (Map.Entry<Object, Object> entry : value.entrySet()) {
            map.put(entry.getKey().toString(), entry.getValue().toString());
        }
        map.remove("Manifest-Version");
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            Jar.writeEntry(out, (String)entry.getKey(), (String)entry.getValue());
        }
    }

    private static Manifest clean(Manifest org) {
        Manifest result = new Manifest();
        for (Map.Entry<Object, Object> entry : org.getMainAttributes().entrySet()) {
            String nice = Jar.clean((String)entry.getValue());
            result.getMainAttributes().put(entry.getKey(), nice);
        }
        for (String name : org.getEntries().keySet()) {
            Attributes attrs = result.getAttributes(name);
            if (attrs == null) {
                attrs = new Attributes();
                result.getEntries().put(name, attrs);
            }
            for (Map.Entry<Object, Object> entry : org.getAttributes(name).entrySet()) {
                String nice = Jar.clean((String)entry.getValue());
                attrs.put(entry.getKey(), nice);
            }
        }
        return result;
    }

    private static String clean(String s) {
        StringBuilder sb = new StringBuilder(s);
        boolean changed = false;
        boolean replacedPrev = false;
        block3: for (int i = 0; i < sb.length(); ++i) {
            char c = s.charAt(i);
            switch (c) {
                case '\u0000': 
                case '\n': 
                case '\r': {
                    changed = true;
                    if (!replacedPrev) {
                        sb.replace(i, i + 1, " ");
                        replacedPrev = true;
                        continue block3;
                    }
                    sb.delete(i, i + 1);
                    continue block3;
                }
                default: {
                    replacedPrev = false;
                }
            }
        }
        if (changed) {
            return sb.toString();
        }
        return s;
    }

    private void writeResource(ZipOutputStream jout, Set<String> directories, String path, Resource resource) throws Exception {
        if (resource == null) {
            return;
        }
        try {
            this.createDirectories(directories, jout, path);
            if (path.endsWith("<<EMPTY>>")) {
                return;
            }
            ZipEntry ze = new ZipEntry(path);
            ze.setMethod(8);
            long lastModified = resource.lastModified();
            if (lastModified == 0L) {
                lastModified = System.currentTimeMillis();
            }
            ZipUtil.setModifiedTime(ze, lastModified);
            if (resource.getExtra() != null) {
                ze.setExtra(resource.getExtra().getBytes(StandardCharsets.UTF_8));
            }
            jout.putNextEntry(ze);
            resource.write(jout);
            jout.closeEntry();
        }
        catch (Exception e) {
            throw new Exception("Problem writing resource " + path, e);
        }
    }

    void createDirectories(Set<String> directories, ZipOutputStream zip, String name) throws IOException {
        int index = name.lastIndexOf(47);
        if (index > 0) {
            String path = name.substring(0, index);
            if (directories.contains(path)) {
                return;
            }
            this.createDirectories(directories, zip, path);
            ZipEntry ze = new ZipEntry(path + '/');
            zip.putNextEntry(ze);
            zip.closeEntry();
            directories.add(path);
        }
    }

    public String getName() {
        return this.name;
    }

    public boolean addAll(Jar sub, Instruction filter) {
        return this.addAll(sub, filter, "");
    }

    public boolean addAll(Jar sub, Instruction filter, String destination) {
        this.check();
        boolean dupl = false;
        for (String name : sub.getResources().keySet()) {
            if (this.manifestName.equals(name) || filter != null && filter.matches(name) == filter.isNegated()) continue;
            dupl |= this.putResource(Processor.appendPath(destination, name), sub.getResource(name), true);
        }
        return dupl;
    }

    @Override
    public void close() {
        this.closed = true;
        IO.close(this.zipFile);
        for (Resource r : this.resources.values()) {
            IO.close(r);
        }
        this.resources.clear();
        this.directories.clear();
        this.manifest = null;
        this.source = null;
    }

    public long lastModified() {
        return this.lastModified;
    }

    public void updateModified(long time, String reason) {
        if (time > this.lastModified) {
            this.lastModified = time;
            this.lastModifiedReason = reason;
        }
    }

    public void setReporter(Reporter reporter) {
        this.reporter = reporter;
    }

    public boolean hasDirectory(String path) {
        this.check();
        return this.directories.get(path) != null;
    }

    public List<String> getPackages() {
        this.check();
        ArrayList<String> list = new ArrayList<String>(this.directories.size());
        for (Map.Entry<String, Map<String, Resource>> i : this.directories.entrySet()) {
            if (i.getValue() == null) continue;
            String path = i.getKey();
            String pack = path.replace('/', '.');
            list.add(pack);
        }
        return list;
    }

    public File getSource() {
        this.check();
        return this.source;
    }

    public boolean addAll(Jar src) {
        this.check();
        return this.addAll(src, null);
    }

    public boolean rename(String oldPath, String newPath) {
        this.check();
        Resource resource = this.remove(oldPath);
        if (resource == null) {
            return false;
        }
        return this.putResource(newPath, resource);
    }

    public Resource remove(String path) {
        this.check();
        Resource resource = this.resources.remove(path);
        String dir = this.getDirectory(path);
        Map<String, Resource> mdir = this.directories.get(dir);
        mdir.remove(path);
        return resource;
    }

    public void setDoNotTouchManifest() {
        this.doNotTouchManifest = true;
    }

    public void calcChecksums(String[] algorithms) throws Exception {
        Manifest m;
        this.check();
        if (algorithms == null) {
            algorithms = new String[]{"SHA", "MD5"};
        }
        if ((m = this.getManifest()) == null) {
            m = new Manifest();
            this.setManifest(m);
        }
        MessageDigest[] digests = new MessageDigest[algorithms.length];
        int n = 0;
        for (String algorithm : algorithms) {
            digests[n++] = MessageDigest.getInstance(algorithm);
        }
        byte[] buffer = new byte[65536];
        for (Map.Entry<String, Resource> entry : this.resources.entrySet()) {
            if (entry.getKey().equals(this.manifestName)) continue;
            Resource r = entry.getValue();
            Attributes attributes = m.getAttributes(entry.getKey());
            if (attributes == null) {
                attributes = new Attributes();
                this.getManifest().getEntries().put(entry.getKey(), attributes);
            }
            try (InputStream in = r.openInputStream();){
                for (MessageDigest d : digests) {
                    d.reset();
                }
                int size = in.read(buffer);
                while (size > 0) {
                    for (MessageDigest d : digests) {
                        d.update(buffer, 0, size);
                    }
                    size = in.read(buffer);
                }
            }
            for (MessageDigest d : digests) {
                attributes.putValue(d.getAlgorithm() + "-Digest", Base64.encodeBase64(d.digest()));
            }
        }
    }

    public String getBsn() throws Exception {
        this.check();
        Manifest m = this.getManifest();
        if (m == null) {
            return null;
        }
        String s = m.getMainAttributes().getValue("Bundle-SymbolicName");
        if (s == null) {
            return null;
        }
        Matcher matcher = BSN.matcher(s);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }

    public String getVersion() throws Exception {
        this.check();
        Manifest m = this.getManifest();
        if (m == null) {
            return null;
        }
        String s = m.getMainAttributes().getValue("Bundle-Version");
        if (s == null) {
            return null;
        }
        return s.trim();
    }

    public void expand(File dir) throws Exception {
        this.check();
        dir = dir.getAbsoluteFile();
        IO.mkdirs(dir);
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("Not a dir: " + dir.getAbsolutePath());
        }
        for (Map.Entry<String, Resource> entry : this.getResources().entrySet()) {
            File f = IO.getFile(dir, entry.getKey());
            File fp = f.getParentFile();
            IO.mkdirs(fp);
            IO.copy(entry.getValue().openInputStream(), f);
        }
    }

    public void ensureManifest() throws Exception {
        if (this.getManifest() != null) {
            return;
        }
        this.manifest = new Manifest();
    }

    public boolean isManifestFirst() {
        return this.manifestFirst;
    }

    public void copy(Jar srce, String path, boolean overwrite) {
        this.check();
        this.addDirectory(srce.getDirectories().get(path), overwrite);
    }

    public void setCompression(Compression compression) {
        this.compression = compression;
    }

    public Compression hasCompression() {
        return this.compression;
    }

    void check() {
        if (this.closed) {
            throw new RuntimeException("Already closed " + this.name);
        }
    }

    public URI getDataURI(String path, String mime, int max) throws Exception {
        Resource r = this.getResource(path);
        if (r.size() >= (long)max || r.size() <= 0L) {
            return null;
        }
        byte[] data = new byte[(int)r.size()];
        try (DataInputStream din = new DataInputStream(r.openInputStream());){
            din.readFully(data);
            String encoded = Base64.encodeBase64(data);
            URI uRI = new URI("data:" + mime + ";base64," + encoded);
            return uRI;
        }
    }

    public void setDigestAlgorithms(String[] algorithms) {
        this.algorithms = algorithms;
    }

    public byte[] getTimelessDigest() throws Exception {
        this.check();
        MessageDigest md = MessageDigest.getInstance("SHA1");
        DigestOutputStream dout = new DigestOutputStream(IO.nullStream, md);
        Manifest m = this.getManifest();
        if (m != null) {
            Manifest m2 = new Manifest(m);
            Attributes main = m2.getMainAttributes();
            String lastmodified = (String)main.remove(new Attributes.Name("Bnd-LastModified"));
            String version = main.getValue(new Attributes.Name("Bundle-Version"));
            if (version != null && Verifier.isVersion(version)) {
                Version v = new Version(version);
                main.putValue("Bundle-Version", v.getWithoutQualifier().toString());
            }
            Jar.writeManifest(m2, dout);
            for (Map.Entry<String, Resource> entry : this.getResources().entrySet()) {
                String path = entry.getKey();
                if (path.equals(this.manifestName)) continue;
                Resource resource = entry.getValue();
                ((OutputStream)dout).write(path.getBytes(StandardCharsets.UTF_8));
                resource.write(dout);
            }
        }
        return md.digest();
    }

    public void stripSignatures() {
        Map<String, Resource> map = this.getDirectories().get("META-INF");
        if (map != null) {
            for (String file : new HashSet<String>(map.keySet())) {
                if (!SIGNER_FILES_P.matcher(file).matches()) continue;
                this.remove(file);
            }
        }
    }

    public void removePrefix(String prefixLow) {
        String prefixHigh = prefixLow + "\uffff";
        this.resources.navigableKeySet().subSet(prefixLow, prefixHigh).clear();
        if (prefixLow.endsWith("/")) {
            prefixLow = prefixLow.substring(0, prefixLow.length() - 1);
        }
        this.directories.navigableKeySet().subSet(prefixLow, prefixHigh).clear();
    }

    public static enum Compression {
        DEFLATE,
        STORE;

    }
}

