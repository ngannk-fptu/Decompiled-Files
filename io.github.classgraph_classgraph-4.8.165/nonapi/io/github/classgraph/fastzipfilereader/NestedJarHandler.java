/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.fastzipfilereader;

import io.github.classgraph.ModuleReaderProxy;
import io.github.classgraph.ModuleRef;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.ZipException;
import nonapi.io.github.classgraph.concurrency.InterruptionChecker;
import nonapi.io.github.classgraph.concurrency.SingletonMap;
import nonapi.io.github.classgraph.fastzipfilereader.FastZipEntry;
import nonapi.io.github.classgraph.fastzipfilereader.LogicalZipFile;
import nonapi.io.github.classgraph.fastzipfilereader.PhysicalZipFile;
import nonapi.io.github.classgraph.fastzipfilereader.ZipFileSlice;
import nonapi.io.github.classgraph.fileslice.ArraySlice;
import nonapi.io.github.classgraph.fileslice.FileSlice;
import nonapi.io.github.classgraph.fileslice.Slice;
import nonapi.io.github.classgraph.recycler.Recycler;
import nonapi.io.github.classgraph.recycler.Resettable;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.FastPathResolver;
import nonapi.io.github.classgraph.utils.FileUtils;
import nonapi.io.github.classgraph.utils.JarUtils;
import nonapi.io.github.classgraph.utils.LogNode;

public class NestedJarHandler {
    public final ScanSpec scanSpec;
    public ReflectionUtils reflectionUtils;
    private SingletonMap<File, PhysicalZipFile, IOException> canonicalFileToPhysicalZipFileMap = new SingletonMap<File, PhysicalZipFile, IOException>(){

        @Override
        public PhysicalZipFile newInstance(File canonicalFile, LogNode log) throws IOException {
            return new PhysicalZipFile(canonicalFile, NestedJarHandler.this, log);
        }
    };
    private SingletonMap<FastZipEntry, ZipFileSlice, IOException> fastZipEntryToZipFileSliceMap = new SingletonMap<FastZipEntry, ZipFileSlice, IOException>(){

        @Override
        public ZipFileSlice newInstance(FastZipEntry childZipEntry, LogNode log) throws IOException, InterruptedException {
            ZipFileSlice childZipEntrySlice;
            if (!childZipEntry.isDeflated) {
                childZipEntrySlice = new ZipFileSlice(childZipEntry);
            } else {
                if (log != null) {
                    log.log("Inflating nested zip entry: " + childZipEntry + " ; uncompressed size: " + childZipEntry.uncompressedSize);
                }
                PhysicalZipFile physicalZipFile = new PhysicalZipFile(childZipEntry.getSlice().open(), childZipEntry.uncompressedSize >= 0L && childZipEntry.uncompressedSize <= 0x7FFFFFF7L ? (long)((int)childZipEntry.uncompressedSize) : -1L, childZipEntry.entryName, NestedJarHandler.this, log);
                childZipEntrySlice = new ZipFileSlice(physicalZipFile, childZipEntry);
            }
            return childZipEntrySlice;
        }
    };
    private SingletonMap<ZipFileSlice, LogicalZipFile, IOException> zipFileSliceToLogicalZipFileMap = new SingletonMap<ZipFileSlice, LogicalZipFile, IOException>(){

        @Override
        public LogicalZipFile newInstance(ZipFileSlice zipFileSlice, LogNode log) throws IOException, InterruptedException {
            return new LogicalZipFile(zipFileSlice, NestedJarHandler.this, log, NestedJarHandler.this.scanSpec.enableMultiReleaseVersions);
        }
    };
    public SingletonMap<String, Map.Entry<LogicalZipFile, String>, IOException> nestedPathToLogicalZipFileAndPackageRootMap = new SingletonMap<String, Map.Entry<LogicalZipFile, String>, IOException>(){

        @Override
        public Map.Entry<LogicalZipFile, String> newInstance(String nestedJarPathRaw, LogNode log) throws IOException, InterruptedException {
            LogicalZipFile childLogicalZipFile;
            ZipFileSlice childZipEntrySlice;
            Map.Entry<LogicalZipFile, String> parentLogicalZipFileAndPackageRoot;
            String nestedJarPath = FastPathResolver.resolve(nestedJarPathRaw);
            int lastPlingIdx = nestedJarPath.lastIndexOf(33);
            if (lastPlingIdx < 0) {
                LogicalZipFile logicalZipFile;
                PhysicalZipFile physicalZipFile;
                boolean isURL = JarUtils.URL_SCHEME_PATTERN.matcher(nestedJarPath).matches();
                if (isURL) {
                    String scheme = nestedJarPath.substring(0, nestedJarPath.indexOf(58));
                    if (NestedJarHandler.this.scanSpec.allowedURLSchemes == null || !NestedJarHandler.this.scanSpec.allowedURLSchemes.contains(scheme)) {
                        throw new IOException("Scanning of URL scheme \"" + scheme + "\" has not been enabled -- cannot scan classpath element: " + nestedJarPath);
                    }
                    physicalZipFile = NestedJarHandler.this.downloadJarFromURL(nestedJarPath, log);
                } else {
                    try {
                        File canonicalFile = new File(nestedJarPath).getCanonicalFile();
                        physicalZipFile = (PhysicalZipFile)NestedJarHandler.this.canonicalFileToPhysicalZipFileMap.get(canonicalFile, log);
                    }
                    catch (SingletonMap.NewInstanceException | SingletonMap.NullSingletonException e) {
                        throw new IOException("Could not get PhysicalZipFile for path " + nestedJarPath + " : " + (e.getCause() == null ? e : e.getCause()));
                    }
                    catch (SecurityException e) {
                        throw new IOException("Path component " + nestedJarPath + " could not be canonicalized: " + e);
                    }
                }
                ZipFileSlice topLevelSlice = new ZipFileSlice(physicalZipFile);
                try {
                    logicalZipFile = (LogicalZipFile)NestedJarHandler.this.zipFileSliceToLogicalZipFileMap.get(topLevelSlice, log);
                }
                catch (SingletonMap.NullSingletonException e) {
                    throw new IOException("Could not get toplevel slice " + topLevelSlice + " : " + e);
                }
                catch (SingletonMap.NewInstanceException e) {
                    throw new IOException("Could not get toplevel slice " + topLevelSlice, e);
                }
                return new AbstractMap.SimpleEntry<LogicalZipFile, String>(logicalZipFile, "");
            }
            String parentPath = nestedJarPath.substring(0, lastPlingIdx);
            String childPath = nestedJarPath.substring(lastPlingIdx + 1);
            childPath = FileUtils.sanitizeEntryPath(childPath, true, true);
            try {
                parentLogicalZipFileAndPackageRoot = NestedJarHandler.this.nestedPathToLogicalZipFileAndPackageRootMap.get(parentPath, log);
            }
            catch (SingletonMap.NullSingletonException e) {
                throw new IOException("Could not get parent logical zipfile " + parentPath + " : " + e);
            }
            catch (SingletonMap.NewInstanceException e) {
                throw new IOException("Could not get parent logical zipfile " + parentPath, e);
            }
            LogicalZipFile parentLogicalZipFile = parentLogicalZipFileAndPackageRoot.getKey();
            boolean isDirectory = false;
            while (childPath.endsWith("/")) {
                isDirectory = true;
                childPath = childPath.substring(0, childPath.length() - 1);
            }
            FastZipEntry childZipEntry = null;
            if (!isDirectory) {
                for (FastZipEntry fastZipEntry : parentLogicalZipFile.entries) {
                    if (!fastZipEntry.entryName.equals(childPath)) continue;
                    childZipEntry = fastZipEntry;
                    break;
                }
            }
            if (childZipEntry == null) {
                String childPathPrefix = childPath + "/";
                for (FastZipEntry entry : parentLogicalZipFile.entries) {
                    if (!entry.entryName.startsWith(childPathPrefix)) continue;
                    isDirectory = true;
                    break;
                }
            }
            if (isDirectory) {
                if (!childPath.isEmpty()) {
                    if (log != null) {
                        log.log("Path " + childPath + " in jarfile " + parentLogicalZipFile + " is a directory, not a file -- using as package root");
                    }
                    parentLogicalZipFile.classpathRoots.add(childPath);
                }
                return new AbstractMap.SimpleEntry<LogicalZipFile, String>(parentLogicalZipFile, childPath);
            }
            if (childZipEntry == null) {
                throw new IOException("Path " + childPath + " does not exist in jarfile " + parentLogicalZipFile);
            }
            if (!NestedJarHandler.this.scanSpec.scanNestedJars) {
                throw new IOException("Nested jar scanning is disabled -- skipping nested jar " + nestedJarPath);
            }
            try {
                childZipEntrySlice = (ZipFileSlice)NestedJarHandler.this.fastZipEntryToZipFileSliceMap.get(childZipEntry, log);
            }
            catch (SingletonMap.NullSingletonException nullSingletonException) {
                throw new IOException("Could not get child zip entry slice " + childZipEntry + " : " + nullSingletonException);
            }
            catch (SingletonMap.NewInstanceException newInstanceException) {
                throw new IOException("Could not get child zip entry slice " + childZipEntry, newInstanceException);
            }
            LogNode logNode = log == null ? null : log.log("Getting zipfile slice " + childZipEntrySlice + " for nested jar " + childZipEntry.entryName);
            try {
                childLogicalZipFile = (LogicalZipFile)NestedJarHandler.this.zipFileSliceToLogicalZipFileMap.get(childZipEntrySlice, logNode);
            }
            catch (SingletonMap.NullSingletonException e) {
                throw new IOException("Could not get child logical zipfile " + childZipEntrySlice + " : " + e);
            }
            catch (SingletonMap.NewInstanceException e) {
                throw new IOException("Could not get child logical zipfile " + childZipEntrySlice, e);
            }
            return new AbstractMap.SimpleEntry<LogicalZipFile, String>(childLogicalZipFile, "");
        }
    };
    public SingletonMap<ModuleRef, Recycler<ModuleReaderProxy, IOException>, IOException> moduleRefToModuleReaderProxyRecyclerMap = new SingletonMap<ModuleRef, Recycler<ModuleReaderProxy, IOException>, IOException>(){

        @Override
        public Recycler<ModuleReaderProxy, IOException> newInstance(final ModuleRef moduleRef, LogNode ignored) {
            return new Recycler<ModuleReaderProxy, IOException>(){

                @Override
                public ModuleReaderProxy newInstance() throws IOException {
                    return moduleRef.open();
                }
            };
        }
    };
    private Recycler<RecyclableInflater, RuntimeException> inflaterRecycler = new Recycler<RecyclableInflater, RuntimeException>(){

        @Override
        public RecyclableInflater newInstance() throws RuntimeException {
            return new RecyclableInflater();
        }
    };
    private Set<Slice> openSlices = Collections.newSetFromMap(new ConcurrentHashMap());
    private Set<File> tempFiles = Collections.newSetFromMap(new ConcurrentHashMap());
    public static final String TEMP_FILENAME_LEAF_SEPARATOR = "---";
    private final AtomicBoolean closed = new AtomicBoolean(false);
    public InterruptionChecker interruptionChecker;
    private static final int DEFAULT_BUFFER_SIZE = 16384;
    private static final int MAX_INITIAL_BUFFER_SIZE = 0x1000000;
    private static final int HTTP_TIMEOUT = 5000;
    private static Method runFinalizationMethod;

    public NestedJarHandler(ScanSpec scanSpec, InterruptionChecker interruptionChecker, ReflectionUtils reflectionUtils) {
        this.scanSpec = scanSpec;
        this.interruptionChecker = interruptionChecker;
        this.reflectionUtils = reflectionUtils;
    }

    private static String leafname(String path) {
        return path.substring(path.lastIndexOf(47) + 1);
    }

    private String sanitizeFilename(String filename) {
        return filename.replace('/', '_').replace('\\', '_').replace(':', '_').replace('?', '_').replace('&', '_').replace('=', '_').replace(' ', '_');
    }

    public File makeTempFile(String filePathBase, boolean onlyUseLeafname) throws IOException {
        File tempFile = File.createTempFile("ClassGraph--", TEMP_FILENAME_LEAF_SEPARATOR + this.sanitizeFilename(onlyUseLeafname ? NestedJarHandler.leafname(filePathBase) : filePathBase));
        tempFile.deleteOnExit();
        this.tempFiles.add(tempFile);
        return tempFile;
    }

    void removeTempFile(File tempFile) throws IOException, SecurityException {
        if (!this.tempFiles.remove(tempFile)) {
            throw new IOException("Not a temp file: " + tempFile);
        }
        Files.delete(tempFile.toPath());
    }

    public void markSliceAsOpen(Slice slice) throws IOException {
        this.openSlices.add(slice);
    }

    public void markSliceAsClosed(Slice slice) {
        this.openSlices.remove(slice);
    }

    /*
     * Exception decompiling
     */
    private PhysicalZipFile downloadJarFromURL(String jarURL, LogNode log) throws IOException, InterruptedException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [16[CATCHBLOCK]], but top level block is 6[TRYBLOCK]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    public InputStream openInflaterInputStream(final InputStream rawInputStream) throws IOException {
        return new InputStream(){
            private final RecyclableInflater recyclableInflater;
            private final Inflater inflater;
            private final AtomicBoolean closed;
            private final byte[] buf;
            private static final int INFLATE_BUF_SIZE = 8192;
            {
                this.recyclableInflater = (RecyclableInflater)NestedJarHandler.this.inflaterRecycler.acquire();
                this.inflater = this.recyclableInflater.getInflater();
                this.closed = new AtomicBoolean();
                this.buf = new byte[8192];
            }

            @Override
            public int read() throws IOException {
                if (this.closed.get()) {
                    throw new IOException("Already closed");
                }
                if (this.inflater.finished()) {
                    return -1;
                }
                int numDeflatedBytesRead = this.read(this.buf, 0, 1);
                if (numDeflatedBytesRead < 0) {
                    return -1;
                }
                return this.buf[0] & 0xFF;
            }

            @Override
            public int read(byte[] outBuf, int off, int len) throws IOException {
                if (this.closed.get()) {
                    throw new IOException("Already closed");
                }
                if (len < 0) {
                    throw new IllegalArgumentException("len cannot be negative");
                }
                if (len == 0) {
                    return 0;
                }
                try {
                    int totInflatedBytes = 0;
                    while (!this.inflater.finished() && totInflatedBytes < len) {
                        int numInflatedBytes = this.inflater.inflate(outBuf, off + totInflatedBytes, len - totInflatedBytes);
                        if (numInflatedBytes == 0) {
                            if (this.inflater.needsDictionary()) {
                                throw new IOException("Inflater needs preset dictionary");
                            }
                            if (!this.inflater.needsInput()) continue;
                            int numRawBytesRead = rawInputStream.read(this.buf, 0, this.buf.length);
                            if (numRawBytesRead == -1) {
                                this.buf[0] = 0;
                                this.inflater.setInput(this.buf, 0, 1);
                                continue;
                            }
                            this.inflater.setInput(this.buf, 0, numRawBytesRead);
                            continue;
                        }
                        totInflatedBytes += numInflatedBytes;
                    }
                    if (totInflatedBytes == 0) {
                        return -1;
                    }
                    return totInflatedBytes;
                }
                catch (DataFormatException e) {
                    throw new ZipException(e.getMessage() != null ? e.getMessage() : "Invalid deflated zip entry data");
                }
            }

            @Override
            public long skip(long numToSkip) throws IOException {
                int readLen;
                int numBytesRead;
                if (this.closed.get()) {
                    throw new IOException("Already closed");
                }
                if (numToSkip < 0L) {
                    throw new IllegalArgumentException("numToSkip cannot be negative");
                }
                if (numToSkip == 0L) {
                    return 0L;
                }
                if (this.inflater.finished()) {
                    return -1L;
                }
                long totBytesSkipped = 0L;
                while ((numBytesRead = this.read(this.buf, 0, readLen = (int)Math.min(numToSkip - totBytesSkipped, (long)this.buf.length))) > 0) {
                    totBytesSkipped -= (long)numBytesRead;
                }
                return totBytesSkipped;
            }

            @Override
            public int available() throws IOException {
                if (this.closed.get()) {
                    throw new IOException("Already closed");
                }
                return this.inflater.finished() ? 0 : 1;
            }

            @Override
            public synchronized void mark(int readlimit) {
                throw new IllegalArgumentException("Not supported");
            }

            @Override
            public synchronized void reset() throws IOException {
                throw new IllegalArgumentException("Not supported");
            }

            @Override
            public boolean markSupported() {
                return false;
            }

            @Override
            public void close() {
                if (!this.closed.getAndSet(true)) {
                    try {
                        rawInputStream.close();
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    NestedJarHandler.this.inflaterRecycler.recycle(this.recyclableInflater);
                }
            }
        };
    }

    public Slice readAllBytesWithSpilloverToDisk(InputStream inputStream, String tempFileBaseName, long inputStreamLengthHint, LogNode log) throws IOException {
        try (InputStream inptStream = inputStream;){
            if (inputStreamLengthHint <= (long)this.scanSpec.maxBufferedJarRAMSize) {
                byte[] overflowBuf;
                int overflowBufBytesUsed;
                int bufSize = inputStreamLengthHint == -1L ? this.scanSpec.maxBufferedJarRAMSize : (inputStreamLengthHint == 0L ? 16384 : Math.min((int)inputStreamLengthHint, this.scanSpec.maxBufferedJarRAMSize));
                byte[] buf = new byte[bufSize];
                int bufLength = buf.length;
                int bufBytesUsed = 0;
                int bytesRead = 0;
                while ((bytesRead = inptStream.read(buf, bufBytesUsed, bufLength - bufBytesUsed)) > 0) {
                    bufBytesUsed += bytesRead;
                }
                if (bytesRead == 0 && (overflowBufBytesUsed = inptStream.read(overflowBuf = new byte[1], 0, 1)) == 1) {
                    FileSlice fileSlice = this.spillToDisk(inptStream, tempFileBaseName, buf, overflowBuf, log);
                    return fileSlice;
                }
                if (bufBytesUsed < buf.length) {
                    buf = Arrays.copyOf(buf, bufBytesUsed);
                }
                ArraySlice arraySlice = new ArraySlice(buf, false, 0L, this);
                return arraySlice;
            }
            FileSlice fileSlice = this.spillToDisk(inptStream, tempFileBaseName, null, null, log);
            return fileSlice;
        }
    }

    private FileSlice spillToDisk(InputStream inputStream, String tempFileBaseName, byte[] buf, byte[] overflowBuf, LogNode log) throws IOException {
        File tempFile;
        try {
            tempFile = this.makeTempFile(tempFileBaseName, true);
        }
        catch (IOException e) {
            throw new IOException("Could not create temporary file: " + e.getMessage());
        }
        if (log != null) {
            log.log("Could not fit InputStream content into max RAM buffer size, saving to temporary file: " + tempFileBaseName + " -> " + tempFile);
        }
        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(tempFile));){
            int bytesRead;
            if (buf != null) {
                ((OutputStream)outputStream).write(buf);
                ((OutputStream)outputStream).write(overflowBuf);
            }
            byte[] copyBuf = new byte[8192];
            while ((bytesRead = inputStream.read(copyBuf, 0, copyBuf.length)) > 0) {
                ((OutputStream)outputStream).write(copyBuf, 0, bytesRead);
            }
        }
        return new FileSlice(tempFile, this, log);
    }

    public static byte[] readAllBytesAsArray(InputStream inputStream, long uncompressedLengthHint) throws IOException {
        if (uncompressedLengthHint > 0x7FFFFFF7L) {
            throw new IOException("InputStream is too large to read");
        }
        try (InputStream inptStream = inputStream;){
            int bufferSize = uncompressedLengthHint < 1L ? 16384 : Math.min((int)uncompressedLengthHint, 0x1000000);
            byte[] buf = new byte[bufferSize];
            int totBytesRead = 0;
            while (true) {
                int extraByte;
                int bytesRead;
                if ((bytesRead = inptStream.read(buf, totBytesRead, buf.length - totBytesRead)) > 0) {
                    totBytesRead += bytesRead;
                    continue;
                }
                if (bytesRead < 0 || (extraByte = inptStream.read()) == -1) break;
                if (buf.length == 0x7FFFFFF7) {
                    throw new IOException("InputStream too large to read into array");
                }
                buf = Arrays.copyOf(buf, (int)Math.min((long)buf.length * 2L, 0x7FFFFFF7L));
                buf[totBytesRead++] = (byte)extraByte;
            }
            byte[] byArray = totBytesRead == buf.length ? buf : Arrays.copyOf(buf, totBytesRead);
            return byArray;
        }
    }

    public void close(LogNode log) {
        if (!this.closed.getAndSet(true)) {
            boolean interrupted = false;
            if (this.moduleRefToModuleReaderProxyRecyclerMap != null) {
                boolean completedWithoutInterruption = false;
                while (!completedWithoutInterruption) {
                    try {
                        for (Recycler<ModuleReaderProxy, IOException> recycler : this.moduleRefToModuleReaderProxyRecyclerMap.values()) {
                            recycler.forceClose();
                        }
                        completedWithoutInterruption = true;
                    }
                    catch (InterruptedException e) {
                        interrupted = true;
                    }
                }
                this.moduleRefToModuleReaderProxyRecyclerMap.clear();
                this.moduleRefToModuleReaderProxyRecyclerMap = null;
            }
            if (this.zipFileSliceToLogicalZipFileMap != null) {
                this.zipFileSliceToLogicalZipFileMap.clear();
                this.zipFileSliceToLogicalZipFileMap = null;
            }
            if (this.nestedPathToLogicalZipFileAndPackageRootMap != null) {
                this.nestedPathToLogicalZipFileAndPackageRootMap.clear();
                this.nestedPathToLogicalZipFileAndPackageRootMap = null;
            }
            if (this.canonicalFileToPhysicalZipFileMap != null) {
                this.canonicalFileToPhysicalZipFileMap.clear();
                this.canonicalFileToPhysicalZipFileMap = null;
            }
            if (this.fastZipEntryToZipFileSliceMap != null) {
                this.fastZipEntryToZipFileSliceMap.clear();
                this.fastZipEntryToZipFileSliceMap = null;
            }
            if (this.openSlices != null) {
                while (!this.openSlices.isEmpty()) {
                    for (Slice slice : new ArrayList<Slice>(this.openSlices)) {
                        try {
                            slice.close();
                        }
                        catch (IOException recycler) {
                            // empty catch block
                        }
                        this.markSliceAsClosed(slice);
                    }
                }
                this.openSlices.clear();
                this.openSlices = null;
            }
            if (this.inflaterRecycler != null) {
                this.inflaterRecycler.forceClose();
                this.inflaterRecycler = null;
            }
            if (this.tempFiles != null) {
                LogNode rmLog;
                LogNode logNode = rmLog = this.tempFiles.isEmpty() || log == null ? null : log.log("Removing temporary files");
                while (!this.tempFiles.isEmpty()) {
                    for (File tempFile : new ArrayList<File>(this.tempFiles)) {
                        try {
                            this.removeTempFile(tempFile);
                        }
                        catch (IOException | SecurityException e) {
                            if (rmLog == null) continue;
                            rmLog.log("Removing temporary file failed: " + tempFile);
                        }
                    }
                }
                this.tempFiles = null;
            }
            if (interrupted) {
                this.interruptionChecker.interrupt();
            }
        }
    }

    public void runFinalizationMethod() {
        if (runFinalizationMethod == null) {
            runFinalizationMethod = this.reflectionUtils.staticMethodForNameOrNull("System", "runFinalization");
        }
        if (runFinalizationMethod != null) {
            try {
                runFinalizationMethod.invoke(null, new Object[0]);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    public void closeDirectByteBuffer(ByteBuffer backingByteBuffer) {
        FileUtils.closeDirectByteBuffer(backingByteBuffer, this.reflectionUtils, null);
    }

    private static class RecyclableInflater
    implements Resettable,
    AutoCloseable {
        private final Inflater inflater = new Inflater(true);

        private RecyclableInflater() {
        }

        public Inflater getInflater() {
            return this.inflater;
        }

        @Override
        public void reset() {
            this.inflater.reset();
        }

        @Override
        public void close() {
            this.inflater.end();
        }
    }

    private static class CloseableUrlConnection
    implements AutoCloseable {
        public final URLConnection conn;
        public final HttpURLConnection httpConn;

        public CloseableUrlConnection(URL url) throws IOException {
            this.conn = url.openConnection();
            this.httpConn = this.conn instanceof HttpURLConnection ? (HttpURLConnection)this.conn : null;
        }

        @Override
        public void close() {
            if (this.httpConn != null) {
                this.httpConn.disconnect();
            }
        }
    }
}

