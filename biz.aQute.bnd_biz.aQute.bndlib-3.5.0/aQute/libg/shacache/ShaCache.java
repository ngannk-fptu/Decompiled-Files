/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.shacache;

import aQute.lib.io.IO;
import aQute.libg.cryptography.SHA1;
import aQute.libg.shacache.ShaSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

public class ShaCache {
    static Pattern SHA_P = Pattern.compile("[A-F0-9]{40,40}", 2);
    private final File root;

    public ShaCache(File root) {
        this.root = root;
        try {
            IO.mkdirs(this.root);
        }
        catch (IOException e) {
            throw new IllegalArgumentException("Cannot create shacache root directory " + root, e);
        }
    }

    public InputStream getStream(String sha, ShaSource ... sources) throws Exception {
        if (!SHA_P.matcher(sha).matches()) {
            throw new IllegalArgumentException("Not a SHA");
        }
        File f = new File(this.root, sha);
        if (!f.isFile()) {
            for (ShaSource s : sources) {
                try {
                    InputStream in = s.get(sha);
                    if (in == null) continue;
                    if (s.isFast()) {
                        return in;
                    }
                    File tmp = IO.createTempFile(this.root, sha.toLowerCase(), ".shacache");
                    IO.copy(in, tmp);
                    String digest = SHA1.digest(tmp).asHex();
                    if (!digest.equalsIgnoreCase(sha)) continue;
                    IO.rename(tmp, f);
                    break;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (!f.isFile()) {
            return null;
        }
        return IO.stream(f);
    }

    public File getFile(String sha, ShaSource ... sources) throws Exception {
        if (!SHA_P.matcher(sha).matches()) {
            throw new IllegalArgumentException("Not a SHA");
        }
        File f = new File(this.root, sha);
        if (f.isFile()) {
            return f;
        }
        for (ShaSource s : sources) {
            try {
                InputStream in = s.get(sha);
                if (in == null) continue;
                File tmp = IO.createTempFile(this.root, sha.toLowerCase(), ".shacache");
                IO.copy(in, tmp);
                String digest = SHA1.digest(tmp).asHex();
                if (!digest.equalsIgnoreCase(sha)) continue;
                IO.rename(tmp, f);
                break;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!f.isFile()) {
            return null;
        }
        return f;
    }

    public void purge() throws Exception {
        IO.deleteWithException(this.root);
        IO.mkdirs(this.root);
    }

    public File getRoot() {
        return this.root;
    }
}

