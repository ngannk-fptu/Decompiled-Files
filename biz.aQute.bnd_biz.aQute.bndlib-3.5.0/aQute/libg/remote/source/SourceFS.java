/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.remote.source;

import aQute.lib.collections.MultiMap;
import aQute.lib.io.IO;
import aQute.libg.cryptography.SHA1;
import aQute.libg.remote.Delta;
import aQute.libg.remote.Sink;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SourceFS {
    static Pattern WINDOWS_PREFIX = Pattern.compile("(\\p{Alpha}):\\\\(.*)");
    static Pattern WINDOWS_FILE_P = Pattern.compile("(?:\\p{Alpha}:|\\\\)(\\\\[\\p{Alnum}-_+.~@$%&=]+)*");
    static Pattern UNIX_FILE_P = Pattern.compile("(/[\\p{Alnum}-_+.~@$%&=]+)+");
    static Pattern LOCAL_P = File.separatorChar == '\\' ? WINDOWS_FILE_P : UNIX_FILE_P;
    private MultiMap<String, File> shas = new MultiMap();
    private final Map<File, FileDescription> files = new HashMap<File, FileDescription>();
    private final boolean pathConversion;
    private final String cwd;
    private final char separatorChar;
    private Sink sink;
    private String areaId;

    SourceFS(char separatorChar, File cwd, Sink sink, String areaId) {
        this.separatorChar = separatorChar;
        this.cwd = cwd.getAbsolutePath();
        this.pathConversion = File.separatorChar != separatorChar;
        this.sink = sink;
        this.areaId = areaId;
    }

    public String transform(String s) throws Exception {
        Matcher m = LOCAL_P.matcher(s);
        StringBuffer sb = new StringBuffer();
        boolean found = false;
        while (m.find()) {
            FileDescription fd = this.toRemote(m.group(0));
            fd.touched = true;
            m.appendReplacement(sb, fd.path);
            found = true;
        }
        if (!found) {
            return s;
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private FileDescription toRemote(String localPath) throws Exception {
        File f = new File(localPath);
        return this.toRemote(f);
    }

    private FileDescription toRemote(File f) throws NoSuchAlgorithmException, Exception {
        FileDescription fd = this.files.get(f);
        if (fd != null) {
            return fd;
        }
        String remotePath = this.toRemotePath(f);
        fd = new FileDescription();
        fd.file = f;
        fd.path = remotePath;
        fd.modified = f.lastModified();
        if (f.isFile()) {
            fd.sha = this.updateSha(null, f);
            fd.touched = true;
        } else if (f.isDirectory()) {
            fd.dir = true;
            for (File sub : f.listFiles()) {
                this.toRemote(sub);
            }
        }
        this.files.put(f, fd);
        return fd;
    }

    private String toRemotePath(File f) {
        String remotePath;
        String abs = f.getAbsolutePath();
        if (abs.startsWith(this.cwd)) {
            remotePath = abs.substring(this.cwd.length());
            while (remotePath.startsWith(File.separator)) {
                remotePath = remotePath.substring(1);
            }
        } else {
            Matcher m;
            remotePath = File.separatorChar == '\\' ? (abs.startsWith("\\\\") ? "_ABS\\REMOTE" + abs.substring(1) : ((m = WINDOWS_PREFIX.matcher(abs)).matches() ? "_ABS\\" + m.group(1) + "\\" + m.group(2) : "_ABS\\" + abs)) : "_ABS" + abs;
        }
        if (this.pathConversion) {
            remotePath = remotePath.replace(File.separatorChar, this.separatorChar);
        }
        return remotePath;
    }

    public void sync() throws Exception {
        Delta delta;
        HashSet<FileDescription> toBeDeleted = new HashSet<FileDescription>();
        ArrayList<Delta> deltas = new ArrayList<Delta>();
        for (FileDescription fd : new HashSet<FileDescription>(this.files.values())) {
            if (!fd.transform) continue;
            delta = new Delta();
            delta.path = fd.path;
            delta.content = this.transform(IO.collect(fd.file));
            deltas.add(delta);
        }
        for (FileDescription fd : this.files.values()) {
            String updateSha;
            if (fd.file.isDirectory() || fd.modified == fd.file.lastModified() && !fd.touched) continue;
            fd.touched = false;
            delta = new Delta();
            delta.path = fd.path;
            fd.modified = fd.file.lastModified();
            fd.touched = true;
            if (!fd.file.isFile()) {
                delta.delete = true;
                toBeDeleted.add(fd);
                deltas.add(delta);
                continue;
            }
            if (fd.transform) continue;
            delta.sha = fd.sha = (updateSha = this.updateSha(fd.sha, fd.file));
            deltas.add(delta);
        }
        this.files.values().removeAll(toBeDeleted);
        this.sync(deltas);
    }

    protected void sync(List<Delta> deltas) throws Exception {
        this.sink.sync(this.areaId, deltas);
    }

    public String updateSha(String oldSha, File file) throws NoSuchAlgorithmException, Exception {
        if (oldSha != null) {
            this.shas.remove(oldSha);
        }
        if (file != null && file.isFile()) {
            String sha = SHA1.digest(file).asHex();
            this.shas.add(sha, file);
            return sha;
        }
        return null;
    }

    public byte[] getData(String sha) throws Exception {
        List files = (List)this.shas.get(sha);
        if (files == null) {
            return null;
        }
        for (File f : files) {
            if (!f.isFile()) continue;
            assert (sha.equals(SHA1.digest(f).asHex()));
            return IO.read(f);
        }
        return null;
    }

    public void markTransform(File f) throws Exception {
        FileDescription fd = this.toRemote(f);
        fd.transform = true;
    }

    public String add(File file) throws Exception {
        return this.toRemote((File)file).path;
    }

    static class FileDescription {
        File file;
        String path;
        String sha;
        long modified;
        boolean touched;
        public boolean transform;
        public boolean dir;

        FileDescription() {
        }
    }
}

