/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.remote.sink;

import aQute.lib.io.IO;
import aQute.libg.cryptography.SHA1;
import aQute.libg.remote.Delta;
import aQute.libg.remote.Source;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SinkFS {
    final Map<File, String> shas = new ConcurrentHashMap<File, String>();
    final Map<String, File> files = new ConcurrentHashMap<String, File>();
    private Source[] sources;
    private File shacache;

    public SinkFS(Source[] sources, File shacache) {
        this.shacache = shacache;
        this.setSources(sources);
    }

    public void setSources(Source[] sources) {
        this.sources = sources;
    }

    public boolean delta(File cwd, Collection<Delta> deltas) {
        for (Delta delta : deltas) {
            try {
                this.delta(cwd, delta);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private void delta(File cwd, Delta delta) throws Exception {
        File file = new File(delta.path);
        if (file.isAbsolute()) {
            throw new IllegalArgumentException("Absolute paths are not allowed " + delta.path);
        }
        file = new File(cwd, delta.path);
        if (delta.delete) {
            IO.delete(file);
        } else if (delta.sha != null) {
            String existing = this.shas.get(file);
            if (existing == null || !delta.sha.equals(existing)) {
                byte[] data = this.getData(delta.sha);
                if (data != null) {
                    this.copy(data, file, delta.sha);
                } else {
                    this.shas.remove(file);
                }
            }
        } else if (delta.content != null) {
            byte[] bytes = delta.content.getBytes(StandardCharsets.UTF_8);
            String sha = SHA1.digest(bytes).asHex();
            this.copy(bytes, file, sha);
        }
    }

    private void copy(byte[] data, File file, String sha) throws Exception {
        IO.mkdirs(file.getParentFile());
        IO.copy(data, file);
        this.shas.put(file, sha);
    }

    private byte[] getData(String sha) throws Exception {
        File shaf = new File(this.shacache, sha);
        if (shaf.isFile()) {
            return IO.read(shaf);
        }
        for (Source source : this.sources) {
            byte[] data = source.getData(sha);
            if (data == null) continue;
            File tmp = IO.createTempFile(this.shacache, "shacache", null);
            IO.copy(data, tmp);
            IO.rename(tmp, shaf);
            return data;
        }
        return null;
    }
}

