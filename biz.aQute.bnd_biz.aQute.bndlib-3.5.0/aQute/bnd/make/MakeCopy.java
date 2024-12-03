/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.make;

import aQute.bnd.osgi.Builder;
import aQute.bnd.osgi.EmbeddedResource;
import aQute.bnd.osgi.FileResource;
import aQute.bnd.osgi.Resource;
import aQute.bnd.osgi.URLResource;
import aQute.bnd.service.MakePlugin;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class MakeCopy
implements MakePlugin {
    @Override
    public Resource make(Builder builder, String destination, Map<String, String> argumentsOnMake) throws Exception {
        String type = argumentsOnMake.get("type");
        if (!type.equals("copy")) {
            return null;
        }
        String from = argumentsOnMake.get("from");
        if (from == null) {
            String content = argumentsOnMake.get("content");
            if (content == null) {
                throw new IllegalArgumentException("No 'from' or 'content' field in copy " + argumentsOnMake);
            }
            return new EmbeddedResource(content.getBytes(StandardCharsets.UTF_8), 0L);
        }
        File f = builder.getFile(from);
        if (f.isFile()) {
            return new FileResource(f);
        }
        try {
            URL url = new URL(from);
            return new URLResource(url);
        }
        catch (MalformedURLException mfue) {
            throw new IllegalArgumentException("Copy source does not exist " + from + " for destination " + destination);
        }
    }
}

