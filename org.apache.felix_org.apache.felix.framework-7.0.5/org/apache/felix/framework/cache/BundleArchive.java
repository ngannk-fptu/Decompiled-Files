/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.cache;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.felix.framework.Logger;
import org.apache.felix.framework.cache.BundleArchiveRevision;
import org.apache.felix.framework.cache.BundleCache;
import org.apache.felix.framework.cache.ConnectRevision;
import org.apache.felix.framework.cache.DirectoryRevision;
import org.apache.felix.framework.cache.JarRevision;
import org.apache.felix.framework.util.WeakZipFileFactory;
import org.osgi.framework.connect.ConnectModule;
import org.osgi.framework.connect.ModuleConnector;

public class BundleArchive {
    public static final transient String FILE_PROTOCOL = "file:";
    public static final transient String REFERENCE_PROTOCOL = "reference:";
    public static final transient String INPUTSTREAM_PROTOCOL = "inputstream:";
    private static final transient String BUNDLE_INFO_FILE = "bundle.info";
    private static final transient String REVISION_LOCATION_FILE = "revision.location";
    private static final transient String REVISION_DIRECTORY = "version";
    private static final transient String DATA_DIRECTORY = "data";
    private final Logger m_logger;
    private final Map m_configMap;
    private final WeakZipFileFactory m_zipFactory;
    private final File m_archiveRootDir;
    private long m_id = -1L;
    private String m_originalLocation = null;
    private int m_persistentState = -1;
    private int m_startLevel = -1;
    private long m_lastModified = -1L;
    private long m_refreshCount = -1L;
    private final ModuleConnector m_connector;
    private final SortedMap<Long, BundleArchiveRevision> m_revisions = new TreeMap<Long, BundleArchiveRevision>();

    public BundleArchive(Logger logger, Map configMap, WeakZipFileFactory zipFactory, ModuleConnector connectFactory, File archiveRootDir, long id, int startLevel, String location, InputStream is) throws Exception {
        this.m_logger = logger;
        this.m_configMap = configMap;
        this.m_zipFactory = zipFactory;
        this.m_archiveRootDir = archiveRootDir;
        this.m_id = id;
        if (this.m_id <= 0L) {
            throw new IllegalArgumentException("Bundle ID cannot be less than or equal to zero.");
        }
        this.m_originalLocation = location;
        this.m_persistentState = 2;
        this.m_startLevel = startLevel;
        this.m_lastModified = System.currentTimeMillis();
        this.m_refreshCount = 0L;
        this.m_connector = connectFactory;
        this.initialize();
        this.reviseInternal(false, new Long(0L), this.m_originalLocation, is);
    }

    public BundleArchive(Logger logger, Map configMap, WeakZipFileFactory zipFactory, ModuleConnector connectFactory, File archiveRootDir) throws Exception {
        File[] children;
        this.m_logger = logger;
        this.m_configMap = configMap;
        this.m_zipFactory = zipFactory;
        this.m_archiveRootDir = archiveRootDir;
        this.readBundleInfo();
        for (File child : children = this.m_archiveRootDir.listFiles()) {
            int idx;
            if (!child.getName().startsWith(REVISION_DIRECTORY) || !BundleCache.getSecureAction().isFileDirectory(child) || (idx = child.getName().lastIndexOf(46)) <= 0) continue;
            Long revNum = Long.decode(child.getName().substring(idx + 1));
            this.m_revisions.put(revNum, null);
        }
        if (this.m_revisions.isEmpty()) {
            throw new Exception("No valid revisions in bundle archive directory: " + archiveRootDir);
        }
        Long currentRevNum = this.m_revisions.lastKey();
        this.m_revisions.remove(currentRevNum);
        String location = this.getRevisionLocation(currentRevNum);
        this.m_connector = connectFactory;
        this.reviseInternal(true, currentRevNum, location, null);
    }

    public synchronized long getId() throws Exception {
        return this.m_id;
    }

    public synchronized String getLocation() throws Exception {
        return this.m_originalLocation;
    }

    public synchronized int getPersistentState() throws Exception {
        return this.m_persistentState;
    }

    public synchronized void setPersistentState(int state) throws Exception {
        if (this.m_persistentState != state) {
            this.m_persistentState = state;
            this.writeBundleInfo();
        }
    }

    public synchronized int getStartLevel() throws Exception {
        return this.m_startLevel;
    }

    public synchronized void setStartLevel(int level) throws Exception {
        if (this.m_startLevel != level) {
            this.m_startLevel = level;
            this.writeBundleInfo();
        }
    }

    public synchronized long getLastModified() throws Exception {
        return this.m_lastModified;
    }

    public synchronized void setLastModified(long lastModified) throws Exception {
        if (this.m_lastModified != lastModified) {
            this.m_lastModified = lastModified;
            this.writeBundleInfo();
        }
    }

    private long getRefreshCount() throws Exception {
        return this.m_refreshCount;
    }

    private void setRefreshCount(long count) throws Exception {
        if (this.m_refreshCount != count) {
            this.m_refreshCount = count;
            this.writeBundleInfo();
        }
    }

    public File getDataFile(String fileName) throws Exception {
        String dataDirPath;
        File dataDir = new File(this.m_archiveRootDir, DATA_DIRECTORY);
        if (!(BundleCache.getSecureAction().fileExists(dataDir) || BundleCache.getSecureAction().mkdirs(dataDir) || BundleCache.getSecureAction().fileExists(dataDir))) {
            throw new IOException("Unable to create bundle data directory.");
        }
        File dataFile = new File(dataDir, fileName);
        String dataFilePath = BundleCache.getSecureAction().getCanonicalPath(dataFile);
        if (!dataFilePath.equals(dataDirPath = BundleCache.getSecureAction().getCanonicalPath(dataDir)) && !dataFilePath.startsWith(dataDirPath + File.separatorChar)) {
            throw new IllegalArgumentException("The data file must be inside the data dir.");
        }
        return dataFile;
    }

    public synchronized Long getCurrentRevisionNumber() {
        return this.m_revisions.isEmpty() ? null : this.m_revisions.lastKey();
    }

    public synchronized BundleArchiveRevision getCurrentRevision() {
        return this.m_revisions.isEmpty() ? null : (BundleArchiveRevision)this.m_revisions.get(this.m_revisions.lastKey());
    }

    public synchronized boolean isRemovalPending() {
        return this.m_revisions.size() > 1;
    }

    public synchronized void revise(String location, InputStream is) throws Exception {
        Long revNum = this.m_revisions.isEmpty() ? new Long(0L) : new Long(this.m_revisions.lastKey() + 1L);
        this.reviseInternal(false, revNum, location, is);
    }

    private void reviseInternal(boolean isReload, Long revNum, String location, InputStream is) throws Exception {
        BundleArchiveRevision revision;
        if (is != null) {
            location = INPUTSTREAM_PROTOCOL;
        }
        if ((revision = this.createRevisionFromLocation(location, is, revNum)) == null) {
            throw new Exception("Unable to revise archive.");
        }
        if (!isReload) {
            this.setRevisionLocation(location, revNum);
        }
        this.m_revisions.put(revNum, revision);
    }

    public synchronized boolean rollbackRevise() throws Exception {
        if (this.m_revisions.size() <= 1) {
            return false;
        }
        Long revNum = this.m_revisions.lastKey();
        BundleArchiveRevision revision = (BundleArchiveRevision)this.m_revisions.remove(revNum);
        try {
            revision.close();
        }
        catch (Exception ex) {
            this.m_logger.log(1, this.getClass().getName() + ": Unable to dispose latest revision", ex);
        }
        File revisionDir = new File(this.m_archiveRootDir, REVISION_DIRECTORY + this.getRefreshCount() + "." + revNum.toString());
        if (BundleCache.getSecureAction().fileExists(revisionDir)) {
            BundleCache.deleteDirectoryTree(revisionDir);
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private synchronized String getRevisionLocation(Long revNum) throws Exception {
        InputStream is = null;
        BufferedReader br = null;
        try {
            is = BundleCache.getSecureAction().getInputStream(new File(new File(this.m_archiveRootDir, REVISION_DIRECTORY + this.getRefreshCount() + "." + revNum.toString()), REVISION_LOCATION_FILE));
            br = new BufferedReader(new InputStreamReader(is));
            String string = br.readLine();
            return string;
        }
        finally {
            if (br != null) {
                br.close();
            }
            if (is != null) {
                is.close();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private synchronized void setRevisionLocation(String location, Long revNum) throws Exception {
        OutputStream os = null;
        BufferedWriter bw = null;
        try {
            os = BundleCache.getSecureAction().getOutputStream(new File(new File(this.m_archiveRootDir, REVISION_DIRECTORY + this.getRefreshCount() + "." + revNum.toString()), REVISION_LOCATION_FILE));
            bw = new BufferedWriter(new OutputStreamWriter(os));
            bw.write(location, 0, location.length());
        }
        finally {
            if (bw != null) {
                bw.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }

    public synchronized void close() {
        for (BundleArchiveRevision revision : this.m_revisions.values()) {
            if (revision == null) continue;
            try {
                revision.close();
            }
            catch (Exception ex) {
                this.m_logger.log(1, "Unable to close revision - " + revision.getRevisionRootDir(), ex);
            }
        }
    }

    public synchronized void closeAndDelete() {
        this.close();
        if (!BundleCache.deleteDirectoryTree(this.m_archiveRootDir)) {
            this.m_logger.log(1, "Unable to delete archive directory - " + this.m_archiveRootDir);
        }
    }

    public synchronized void purge() throws Exception {
        Long currentRevNum = this.getCurrentRevisionNumber();
        Map<String, Object> headers = this.getCurrentRevision().getManifestHeader();
        boolean hasNativeLibs = headers != null && this.getCurrentRevision().getManifestHeader().containsKey("Bundle-NativeCode");
        this.close();
        long refreshCount = this.getRefreshCount();
        for (Long revNum : this.m_revisions.keySet()) {
            if (revNum.equals(currentRevNum)) continue;
            File revisionDir = new File(this.m_archiveRootDir, REVISION_DIRECTORY + refreshCount + "." + revNum.toString());
            if (!BundleCache.getSecureAction().fileExists(revisionDir)) continue;
            BundleCache.deleteDirectoryTree(revisionDir);
        }
        if (hasNativeLibs) {
            this.setRefreshCount(refreshCount + 1L);
            File currentDir = new File(this.m_archiveRootDir, REVISION_DIRECTORY + (refreshCount + 1L) + "." + currentRevNum.toString());
            File revisionDir = new File(this.m_archiveRootDir, REVISION_DIRECTORY + refreshCount + "." + currentRevNum.toString());
            BundleCache.getSecureAction().renameFile(revisionDir, currentDir);
        }
        this.m_revisions.clear();
        BundleArchiveRevision revision = this.createRevisionFromLocation(this.getRevisionLocation(currentRevNum), null, currentRevNum);
        this.m_revisions.put(currentRevNum, revision);
    }

    private void initialize() throws Exception {
        OutputStream os = null;
        BufferedWriter bw = null;
        try {
            if (BundleCache.getSecureAction().fileExists(this.m_archiveRootDir)) {
                return;
            }
            if (!BundleCache.getSecureAction().mkdir(this.m_archiveRootDir)) {
                this.m_logger.log(1, this.getClass().getName() + ": Unable to create archive directory.");
                throw new IOException("Unable to create archive directory.");
            }
            this.writeBundleInfo();
        }
        finally {
            if (bw != null) {
                bw.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }

    private BundleArchiveRevision createRevisionFromLocation(String location, InputStream is, Long revNum) throws Exception {
        File revisionRootDir = new File(this.m_archiveRootDir, REVISION_DIRECTORY + this.getRefreshCount() + "." + revNum.toString());
        BundleArchiveRevision result = null;
        try {
            if (location != null && location.startsWith(REFERENCE_PROTOCOL)) {
                if (!(location = location.substring(REFERENCE_PROTOCOL.length())).startsWith(FILE_PROTOCOL)) {
                    throw new IOException("Reference URLs can only be files: " + location);
                }
                location = BundleArchive.decode(location);
                File file = new File(location.substring(FILE_PROTOCOL.length()));
                if (!BundleCache.getSecureAction().fileExists(file)) {
                    throw new IOException("Referenced file does not exist: " + file);
                }
                result = BundleCache.getSecureAction().isFileDirectory(file) ? new DirectoryRevision(this.m_logger, this.m_configMap, this.m_zipFactory, revisionRootDir, location) : new JarRevision(this.m_logger, this.m_configMap, this.m_zipFactory, revisionRootDir, location, true, null);
            } else if (location.startsWith(INPUTSTREAM_PROTOCOL)) {
                result = new JarRevision(this.m_logger, this.m_configMap, this.m_zipFactory, revisionRootDir, location, false, is);
            } else {
                ConnectModule module;
                ConnectModule connectModule = module = this.m_connector != null ? (ConnectModule)this.m_connector.connect(location).orElse(null) : null;
                result = module != null ? new ConnectRevision(this.m_logger, this.m_configMap, this.m_zipFactory, revisionRootDir, location, module) : new JarRevision(this.m_logger, this.m_configMap, this.m_zipFactory, revisionRootDir, location, false, null);
            }
        }
        catch (Exception ex) {
            if (BundleCache.getSecureAction().fileExists(revisionRootDir) && !BundleCache.deleteDirectoryTree(revisionRootDir)) {
                this.m_logger.log(1, this.getClass().getName() + ": Unable to delete revision directory - " + revisionRootDir);
            }
            throw ex;
        }
        return result;
    }

    private static String decode(String s) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c == '%') {
                out.reset();
                do {
                    if (i + 2 >= s.length()) {
                        throw new IllegalArgumentException("Incomplete % sequence at: " + i);
                    }
                    int d1 = Character.digit(s.charAt(i + 1), 16);
                    int d2 = Character.digit(s.charAt(i + 2), 16);
                    if (d1 == -1 || d2 == -1) {
                        throw new IllegalArgumentException("Invalid % sequence (" + s.substring(i, i + 3) + ") at: " + String.valueOf(i));
                    }
                    out.write((byte)((d1 << 4) + d2));
                } while ((i += 3) < s.length() && s.charAt(i) == '%');
                result.append(out.toString("UTF-8"));
                continue;
            }
            result.append(c);
            ++i;
        }
        return result.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void readBundleInfo() throws Exception {
        File infoFile = new File(this.m_archiveRootDir, BUNDLE_INFO_FILE);
        InputStream is = null;
        BufferedReader br = null;
        try {
            is = BundleCache.getSecureAction().getInputStream(infoFile);
            br = new BufferedReader(new InputStreamReader(is));
            this.m_id = Long.parseLong(br.readLine());
            this.m_originalLocation = br.readLine();
            this.m_persistentState = Integer.parseInt(br.readLine());
            this.m_startLevel = Integer.parseInt(br.readLine());
            this.m_lastModified = Long.parseLong(br.readLine());
            this.m_refreshCount = Long.parseLong(br.readLine());
        }
        finally {
            if (br != null) {
                br.close();
            }
            if (is != null) {
                is.close();
            }
        }
    }

    private void writeBundleInfo() throws Exception {
        OutputStream os = null;
        BufferedWriter bw = null;
        try {
            os = BundleCache.getSecureAction().getOutputStream(new File(this.m_archiveRootDir, BUNDLE_INFO_FILE));
            bw = new BufferedWriter(new OutputStreamWriter(os));
            String s = Long.toString(this.m_id);
            bw.write(s, 0, s.length());
            bw.newLine();
            s = this.m_originalLocation == null ? "" : this.m_originalLocation;
            bw.write(s, 0, s.length());
            bw.newLine();
            s = Integer.toString(this.m_persistentState);
            bw.write(s, 0, s.length());
            bw.newLine();
            s = Integer.toString(this.m_startLevel);
            bw.write(s, 0, s.length());
            bw.newLine();
            s = Long.toString(this.m_lastModified);
            bw.write(s, 0, s.length());
            bw.newLine();
            s = Long.toString(this.m_refreshCount);
            bw.write(s, 0, s.length());
            bw.newLine();
        }
        catch (IOException ex) {
            this.m_logger.log(1, this.getClass().getName() + ": Unable to cache bundle info - " + ex);
            throw ex;
        }
        finally {
            if (bw != null) {
                bw.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }
}

