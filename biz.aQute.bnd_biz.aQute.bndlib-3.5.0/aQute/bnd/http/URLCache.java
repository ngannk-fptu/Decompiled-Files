/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package aQute.bnd.http;

import aQute.lib.exceptions.Exceptions;
import aQute.lib.io.IO;
import aQute.lib.json.JSONCodec;
import aQute.libg.cryptography.SHA1;
import aQute.libg.cryptography.SHA256;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class URLCache {
    private static final Logger logger = LoggerFactory.getLogger(URLCache.class);
    private static final JSONCodec codec = new JSONCodec();
    private final File root;
    private ConcurrentMap<File, Info> infos = new ConcurrentHashMap<File, Info>();

    public URLCache(File root) {
        this.root = new File(root, "shas");
        try {
            IO.mkdirs(this.root);
        }
        catch (IOException e) {
            throw Exceptions.duck(e);
        }
    }

    public Info get(URI uri) throws Exception {
        return this.get(null, uri);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Info get(File file, URI uri) throws Exception {
        URLCache uRLCache = this;
        synchronized (uRLCache) {
            Info info;
            if (file == null) {
                file = this.getCacheFileFor(uri);
            }
            if ((info = (Info)this.infos.get(file)) == null) {
                info = new Info(file, uri);
                this.infos.put(file, info);
            }
            if (!info.lock.tryLock(5L, TimeUnit.MINUTES)) {
                logger.debug("Could not locked url cache {} - {}", (Object)uri, (Object)info);
            }
            return info;
        }
    }

    public static String toName(URI uri) throws Exception {
        return SHA1.digest(uri.toASCIIString().getBytes(StandardCharsets.UTF_8)).asHex();
    }

    public static void update(File file, String tag) {
        throw new UnsupportedOperationException();
    }

    public File getCacheFileFor(URI url) throws Exception {
        return new File(this.root, URLCache.toName(url) + ".content");
    }

    public class Info
    implements Closeable {
        File file;
        File jsonFile;
        InfoDTO dto;
        URI url;
        ReentrantLock lock = new ReentrantLock();

        @Deprecated
        public Info(URI url) throws Exception {
            this(uRLCache.getCacheFileFor(url), url);
        }

        public Info(File content, URI url) throws Exception {
            this.file = content;
            this.url = url;
            this.jsonFile = new File(content.getParentFile(), content.getName() + ".json");
            if (this.jsonFile.isFile()) {
                try {
                    this.dto = codec.dec().from(this.jsonFile).get(InfoDTO.class);
                }
                catch (Exception e) {
                    this.dto = new InfoDTO();
                    logger.error("URLCache Failed to load data for {} from {}", (Object)content, (Object)this.jsonFile);
                }
            } else {
                this.dto = new InfoDTO();
            }
            this.dto.uri = url;
        }

        @Override
        public void close() throws IOException {
            logger.debug("Unlocking url cache {}", (Object)this.url);
            this.lock.unlock();
        }

        public void update(InputStream inputStream, String etag, long modified) throws Exception {
            IO.mkdirs(this.file.getParentFile());
            IO.copy(inputStream, this.file);
            if (modified > 0L) {
                this.file.setLastModified(modified);
            }
            this.update(etag);
        }

        public void update(String etag) throws Exception {
            this.dto.sha_1 = SHA1.digest(this.file).asHex();
            this.dto.sha_256 = SHA256.digest(this.file).asHex();
            this.dto.etag = etag;
            this.dto.modified = this.file.lastModified();
            codec.enc().to(this.jsonFile).put(this.dto);
        }

        public boolean isPresent() {
            boolean f = this.file.isFile();
            boolean j = this.jsonFile.isFile();
            return f && j;
        }

        public void delete() {
            IO.delete(this.file);
            IO.delete(this.jsonFile);
        }

        public String getETag() {
            return this.dto.etag;
        }

        public long getModified() {
            return this.dto.modified;
        }

        public String toString() {
            return "Info [file=" + this.file.getName() + ", url=" + this.url + "]";
        }
    }

    public static class InfoDTO {
        public String etag;
        public String sha_1;
        public long modified;
        public URI uri;
        public String sha_256;
    }
}

