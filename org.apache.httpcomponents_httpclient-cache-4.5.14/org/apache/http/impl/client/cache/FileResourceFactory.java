/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 */
package org.apache.http.impl.client.cache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.cache.InputLimit;
import org.apache.http.client.cache.Resource;
import org.apache.http.client.cache.ResourceFactory;
import org.apache.http.impl.client.cache.BasicIdGenerator;
import org.apache.http.impl.client.cache.FileResource;
import org.apache.http.impl.client.cache.IOUtils;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public class FileResourceFactory
implements ResourceFactory {
    private final File cacheDir;
    private final BasicIdGenerator idgen;

    public FileResourceFactory(File cacheDir) {
        this.cacheDir = cacheDir;
        this.idgen = new BasicIdGenerator();
    }

    private File generateUniqueCacheFile(String requestId) {
        StringBuilder buffer = new StringBuilder();
        this.idgen.generate(buffer);
        buffer.append('.');
        int len = Math.min(requestId.length(), 100);
        for (int i = 0; i < len; ++i) {
            char ch = requestId.charAt(i);
            if (Character.isLetterOrDigit(ch) || ch == '.') {
                buffer.append(ch);
                continue;
            }
            buffer.append('-');
        }
        return new File(this.cacheDir, buffer.toString());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Resource generate(String requestId, InputStream inStream, InputLimit limit) throws IOException {
        File file = this.generateUniqueCacheFile(requestId);
        FileOutputStream outStream = new FileOutputStream(file);
        try {
            int l;
            byte[] buf = new byte[2048];
            long total = 0L;
            while ((l = inStream.read(buf)) != -1) {
                outStream.write(buf, 0, l);
                if (limit == null || (total += (long)l) <= limit.getValue()) continue;
                limit.reached();
                break;
            }
        }
        finally {
            outStream.close();
        }
        return new FileResource(file);
    }

    @Override
    public Resource copy(String requestId, Resource resource) throws IOException {
        File file = this.generateUniqueCacheFile(requestId);
        if (resource instanceof FileResource) {
            File src = ((FileResource)resource).getFile();
            IOUtils.copyFile(src, file);
        } else {
            FileOutputStream out = new FileOutputStream(file);
            IOUtils.copyAndClose(resource.getInputStream(), out);
        }
        return new FileResource(file);
    }
}

