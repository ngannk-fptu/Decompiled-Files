/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package aQute.bnd.resource.repository;

import aQute.bnd.resource.repository.ResourceDescriptorImpl;
import aQute.bnd.service.RepositoryPlugin;
import aQute.bnd.service.repository.ResourceRepository;
import aQute.bnd.service.repository.SearchableRepository;
import aQute.bnd.service.url.URLConnectionHandler;
import aQute.bnd.url.DefaultURLConnectionHandler;
import aQute.bnd.version.VersionRange;
import aQute.lib.collections.MultiMap;
import aQute.lib.exceptions.Exceptions;
import aQute.lib.hex.Hex;
import aQute.lib.io.IO;
import aQute.lib.json.JSONCodec;
import aQute.libg.cryptography.SHA1;
import aQute.libg.reporter.ReporterAdapter;
import aQute.service.reporter.Reporter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import java.util.zip.InflaterInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceRepositoryImpl
implements ResourceRepository {
    private static final Logger logger = LoggerFactory.getLogger(ResourceRepositoryImpl.class);
    private static Comparator<SearchableRepository.ResourceDescriptor> RESOURCE_DESCRIPTOR_COMPARATOR = new Comparator<SearchableRepository.ResourceDescriptor>(){

        @Override
        public int compare(SearchableRepository.ResourceDescriptor o1, SearchableRepository.ResourceDescriptor o2) {
            if (o1 == o2) {
                return 0;
            }
            int r = o1.bsn.compareTo(o2.bsn);
            if (r > 0) {
                return 1;
            }
            if (r < 0) {
                return -1;
            }
            return o1.version.compareTo(o2.version);
        }
    };
    private static final long THRESHOLD = 14400000L;
    protected static final RepositoryPlugin.DownloadListener[] EMPTY_LISTENER = new RepositoryPlugin.DownloadListener[0];
    static JSONCodec codec = new JSONCodec();
    private final List<ResourceRepository.Listener> listeners = new CopyOnWriteArrayList<ResourceRepository.Listener>();
    private boolean dirty;
    private FileLayout index;
    private Map<URI, Long> failures = new HashMap<URI, Long>();
    private File cache;
    private File hosting;
    private Reporter reporter = new ReporterAdapter(System.out);
    private Executor executor;
    private File indexFile;
    private URLConnectionHandler connector = new DefaultURLConnectionHandler();
    final MultiMap<File, RepositoryPlugin.DownloadListener> queues = new MultiMap();
    final Semaphore limitDownloads = new Semaphore(5);

    public ResourceRepositoryImpl() {
        ((ReporterAdapter)this.reporter).setTrace(true);
    }

    public List<ResourceDescriptorImpl> filter(String repoId, String filter) throws Exception {
        ArrayList<ResourceDescriptorImpl> result = new ArrayList<ResourceDescriptorImpl>();
        for (ResourceDescriptorImpl rdi : this.getIndex().descriptors) {
            if (repoId != null && !rdi.repositories.contains(repoId)) continue;
            result.add(rdi);
        }
        return result;
    }

    void delete(byte[] id) throws Exception {
        Iterator<ResourceDescriptorImpl> i = this.getIndex().descriptors.iterator();
        while (i.hasNext()) {
            ResourceDescriptorImpl d = i.next();
            if (!Arrays.equals(id, d.id)) continue;
            i.remove();
            logger.debug("removing resource {} from index", (Object)d);
            this.event(ResourceRepository.TYPE.REMOVE, d, null);
            this.setDirty();
        }
        this.save();
    }

    @Override
    public boolean delete(String repoId, byte[] id) throws Exception {
        ResourceDescriptorImpl rd = this.getResourceDescriptor(id);
        if (rd == null) {
            return false;
        }
        if (repoId == null) {
            this.delete(id);
            return true;
        }
        boolean remove = rd.repositories.remove(repoId);
        if (rd.repositories.isEmpty()) {
            this.delete(rd.id);
        } else {
            this.save();
        }
        return remove;
    }

    @Override
    public boolean deleteCache(byte[] id) throws Exception {
        File dir = IO.getFile(this.cache, Hex.toHexString(id));
        if (dir.isDirectory()) {
            IO.delete(dir);
            return true;
        }
        return false;
    }

    @Override
    public boolean add(String repoId, SearchableRepository.ResourceDescriptor rd) throws Exception {
        ResourceDescriptorImpl rdi = this.getResourceDescriptor(rd.id);
        boolean add = false;
        if (rdi != null) {
            add = true;
            logger.debug("adding repo {} to resource {} to index", (Object)repoId, (Object)rdi);
        } else {
            rdi = new ResourceDescriptorImpl(rd);
            this.getIndex().descriptors.add(rdi);
            logger.debug("adding resource {} to index", (Object)rdi);
        }
        rdi.repositories.add(repoId);
        this.event(ResourceRepository.TYPE.ADD, rdi, null);
        this.setDirty();
        this.save();
        return add;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public File getResource(byte[] rd, RepositoryPlugin.DownloadListener ... blockers) throws Exception {
        final ResourceDescriptorImpl rds = this.getResourceDescriptor(rd);
        if (rds == null) {
            logger.debug("no such descriptor {}", (Object)Hex.toHexString(rd));
            return null;
        }
        final File path = IO.getFile(this.cache, Hex.toHexString(rds.id) + "/" + rds.bsn + "-" + rds.version + ".jar");
        if (path.isFile()) {
            this.ok(blockers, path);
            return path;
        }
        Map<URI, Long> map = this.failures;
        synchronized (map) {
            Long l = this.failures.get(rds.url);
            if (l != null && System.currentTimeMillis() - l < 14400000L) {
                logger.debug("descriptor {}, had earlier failure not retrying", (Object)Hex.toHexString(rd));
                return null;
            }
        }
        if (blockers == null || blockers.length == 0) {
            logger.debug("descriptor {}, not found, immediate download", (Object)Hex.toHexString(rd));
            this.download(rds, path);
            return path;
        }
        logger.debug("descriptor {}, not found, background download", (Object)Hex.toHexString(rd));
        map = this.queues;
        synchronized (map) {
            List list = (List)this.queues.get(path);
            boolean first = list == null || list.isEmpty();
            for (RepositoryPlugin.DownloadListener b : blockers) {
                this.queues.add(path, b);
            }
            if (!first) {
                logger.debug("someone else is downloading our file {}", this.queues.get(path));
                return path;
            }
        }
        this.limitDownloads.acquire();
        this.executor.execute(new Runnable(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                try {
                    ResourceRepositoryImpl.this.download(rds, path);
                    MultiMap<File, RepositoryPlugin.DownloadListener> multiMap = ResourceRepositoryImpl.this.queues;
                    synchronized (multiMap) {
                        ResourceRepositoryImpl.this.ok(((List)ResourceRepositoryImpl.this.queues.get(path)).toArray(EMPTY_LISTENER), path);
                    }
                }
                catch (Exception e) {
                    MultiMap<File, RepositoryPlugin.DownloadListener> multiMap = ResourceRepositoryImpl.this.queues;
                    synchronized (multiMap) {
                        ResourceRepositoryImpl.this.fail(e, ((List)ResourceRepositoryImpl.this.queues.get(path)).toArray(EMPTY_LISTENER), path);
                    }
                }
                finally {
                    MultiMap<File, RepositoryPlugin.DownloadListener> multiMap = ResourceRepositoryImpl.this.queues;
                    synchronized (multiMap) {
                        ResourceRepositoryImpl.this.queues.remove(path);
                    }
                    ResourceRepositoryImpl.this.limitDownloads.release();
                }
            }
        });
        return path;
    }

    @Override
    public void addListener(ResourceRepository.Listener rrl) {
        this.listeners.add(rrl);
    }

    public void removeListener(ResourceRepository.Listener rrl) {
        this.listeners.remove(rrl);
    }

    private void setDirty() {
        this.dirty = true;
    }

    @Override
    public ResourceDescriptorImpl getResourceDescriptor(byte[] rd) throws Exception {
        for (ResourceDescriptorImpl d : this.getIndex().descriptors) {
            if (!Arrays.equals(d.id, rd)) continue;
            return d;
        }
        return null;
    }

    void ok(RepositoryPlugin.DownloadListener[] blockers, File file) {
        for (RepositoryPlugin.DownloadListener dl : blockers) {
            try {
                dl.success(file);
            }
            catch (Exception e) {
                // empty catch block
            }
        }
    }

    void fail(Exception e, RepositoryPlugin.DownloadListener[] blockers, File file) {
        String reason = Exceptions.toString(e);
        for (RepositoryPlugin.DownloadListener dl : blockers) {
            try {
                dl.failure(file, reason);
            }
            catch (Exception ee) {
                // empty catch block
            }
        }
    }

    void download(SearchableRepository.ResourceDescriptor rds, File path) throws Exception {
        logger.debug("starting download {}", (Object)path);
        Exception exception = new Exception();
        this.event(ResourceRepository.TYPE.START_DOWNLOAD, rds, null);
        for (int i = 0; i < 3; ++i) {
            try {
                this.download0(rds.url, path, rds.id);
                this.event(ResourceRepository.TYPE.END_DOWNLOAD, rds, null);
                logger.debug("succesful download {}", (Object)path);
                this.failures.remove(rds.url);
                return;
            }
            catch (FileNotFoundException e) {
                logger.debug("no such file download {}", (Object)path);
                exception = e;
                break;
            }
            catch (Exception e) {
                logger.debug("exception download {}", (Object)path);
                exception = e;
                this.sleep(3000);
                continue;
            }
        }
        this.failures.put(rds.url, System.currentTimeMillis());
        logger.debug("failed download {}", (Object)path, (Object)exception);
        this.event(ResourceRepository.TYPE.ERROR, rds, exception);
        this.event(ResourceRepository.TYPE.END_DOWNLOAD, rds, exception);
        throw exception;
    }

    void download0(URI url, File path, byte[] sha) throws Exception {
        InputStream in;
        IO.mkdirs(path.getParentFile());
        File tmp = IO.createTempFile(path.getParentFile(), "tmp", ".jar");
        URL u = url.toURL();
        URLConnection conn = u.openConnection();
        if (conn instanceof HttpURLConnection) {
            HttpURLConnection http = (HttpURLConnection)conn;
            http.setRequestProperty("Accept-Encoding", "deflate");
            http.setInstanceFollowRedirects(true);
            this.connector.handle(conn);
            int result = http.getResponseCode();
            if (result / 100 != 2) {
                String s = "";
                InputStream err = http.getErrorStream();
                Throwable throwable = null;
                try {
                    try {
                        if (err != null) {
                            s = IO.collect(err);
                        }
                        if (result == 404) {
                            logger.debug("not found ");
                            throw new FileNotFoundException("Cannot find " + url + " : " + s);
                        }
                        throw new IOException("Failed request " + result + ":" + http.getResponseMessage() + " " + s);
                    }
                    catch (Throwable throwable2) {
                        throwable = throwable2;
                        throw throwable2;
                    }
                }
                catch (Throwable throwable3) {
                    if (err != null) {
                        if (throwable != null) {
                            try {
                                err.close();
                            }
                            catch (Throwable x2) {
                                throwable.addSuppressed(x2);
                            }
                        } else {
                            err.close();
                        }
                    }
                    throw throwable3;
                }
            }
            String deflate = http.getHeaderField("Content-Encoding");
            in = http.getInputStream();
            if (deflate != null && deflate.toLowerCase().contains("deflate")) {
                in = new InflaterInputStream(in);
                logger.debug("inflate");
            }
        } else {
            this.connector.handle(conn);
            in = conn.getInputStream();
        }
        IO.copy(in, tmp);
        byte[] digest = SHA1.digest(tmp).digest();
        if (!Arrays.equals(digest, sha)) {
            logger.debug("sha's did not match {}, expected {}, got {}", new Object[]{tmp, Hex.toHexString(sha), digest});
            throw new IllegalArgumentException("Invalid sha downloaded");
        }
        IO.rename(tmp, path);
    }

    private void event(ResourceRepository.TYPE type, SearchableRepository.ResourceDescriptor rds, Exception exception) {
        for (ResourceRepository.Listener l : this.listeners) {
            try {
                l.events(new ResourceRepository.ResourceRepositoryEvent(type, rds, exception));
            }
            catch (Exception e) {
                logger.debug("listener {} throws exception", (Object)l, (Object)e);
            }
        }
    }

    private boolean sleep(int i) {
        try {
            Thread.sleep(i);
            return true;
        }
        catch (InterruptedException e) {
            return false;
        }
    }

    private void save() throws Exception {
        if (!this.dirty) {
            return;
        }
        File tmp = new File(this.indexFile.getAbsolutePath() + ".tmp");
        IO.mkdirs(tmp.getParentFile());
        try (PrintWriter ps = IO.writer(tmp, StandardCharsets.UTF_8);){
            Formatter frm = new Formatter(ps);
            this.getIndex().write(frm);
            frm.close();
        }
        IO.rename(tmp, this.indexFile);
    }

    private FileLayout getIndex() throws Exception {
        if (this.index != null) {
            return this.index;
        }
        if (!this.indexFile.isFile()) {
            this.index = new FileLayout();
            return this.index;
        }
        this.index = codec.dec().from(this.indexFile).get(FileLayout.class);
        return this.index;
    }

    public void setReporter(Reporter processor) {
        this.reporter = processor;
    }

    public void setIndexFile(File file) {
        this.indexFile = file;
    }

    public void setCache(File cache) {
        this.cache = cache;
        this.hosting = new File(cache, "hosting");
    }

    public void setExecutor(Executor executor) throws Exception {
        this.executor = executor;
    }

    public void setURLConnector(URLConnectionHandler connector) throws Exception {
        this.connector = connector;
    }

    @Override
    public SortedSet<SearchableRepository.ResourceDescriptor> find(String repoId, String bsn, VersionRange range) throws Exception {
        TreeSet<SearchableRepository.ResourceDescriptor> result = new TreeSet<SearchableRepository.ResourceDescriptor>(RESOURCE_DESCRIPTOR_COMPARATOR);
        for (ResourceDescriptorImpl r : this.filter(repoId, null)) {
            if (!bsn.equals(r.bsn) || range != null && !range.includes(r.version)) continue;
            result.add(r);
        }
        return result;
    }

    @Override
    public File getCacheDir(String name) {
        File dir = new File(this.hosting, name);
        try {
            IO.mkdirs(dir);
        }
        catch (IOException e) {
            throw Exceptions.duck(e);
        }
        return dir;
    }

    public String toString() {
        return "ResourceRepositoryImpl [" + (this.cache != null ? "cache=" + this.cache + ", " : "") + (this.indexFile != null ? "indexFile=" + this.indexFile + ", " : "") + "]";
    }

    public static class FileLayout {
        public int version;
        public List<ResourceDescriptorImpl> descriptors = new ArrayList<ResourceDescriptorImpl>();
        public int increment;
        public long date;

        void write(Formatter format) throws Exception {
            Collections.sort(this.descriptors);
            this.date = System.currentTimeMillis();
            format.format("{\n\"version\"      :%s,\n\"descriptors\"   : [\n", this.version);
            String del = "";
            for (ResourceDescriptorImpl rd : this.descriptors) {
                format.format(del, new Object[0]);
                format.flush();
                codec.enc().to(format.out()).keepOpen().put(rd);
                del = ",\n";
            }
            format.format("\n]}\n", new Object[0]);
            format.flush();
        }
    }
}

