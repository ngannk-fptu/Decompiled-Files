/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 */
package org.apache.http.impl.client.cache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.cache.InputLimit;
import org.apache.http.client.cache.Resource;
import org.apache.http.client.cache.ResourceFactory;
import org.apache.http.impl.client.cache.HeapResource;
import org.apache.http.impl.client.cache.IOUtils;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public class HeapResourceFactory
implements ResourceFactory {
    @Override
    public Resource generate(String requestId, InputStream inStream, InputLimit limit) throws IOException {
        int l;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buf = new byte[2048];
        long total = 0L;
        while ((l = inStream.read(buf)) != -1) {
            outStream.write(buf, 0, l);
            if (limit == null || (total += (long)l) <= limit.getValue()) continue;
            limit.reached();
            break;
        }
        return this.createResource(outStream.toByteArray());
    }

    @Override
    public Resource copy(String requestId, Resource resource) throws IOException {
        byte[] body;
        if (resource instanceof HeapResource) {
            body = ((HeapResource)resource).getByteArray();
        } else {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            IOUtils.copyAndClose(resource.getInputStream(), outStream);
            body = outStream.toByteArray();
        }
        return this.createResource(body);
    }

    Resource createResource(byte[] buf) {
        return new HeapResource(buf);
    }
}

