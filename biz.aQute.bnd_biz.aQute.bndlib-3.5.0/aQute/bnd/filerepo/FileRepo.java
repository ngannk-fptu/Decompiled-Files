/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.filerepo;

import aQute.bnd.version.Version;
import aQute.bnd.version.VersionRange;
import aQute.lib.io.IO;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Deprecated
public class FileRepo {
    File root;
    Pattern REPO_FILE = Pattern.compile("([-a-zA-z0-9_\\.]+)-([0-9\\.]+)\\.(jar|lib)");

    public FileRepo(File root) {
        this.root = root;
    }

    public File[] get(String bsn, final VersionRange versionRange) throws Exception {
        File f = new File(this.root, bsn);
        if (!f.isDirectory()) {
            return null;
        }
        return f.listFiles(new FilenameFilter(){

            @Override
            public boolean accept(File dir, String name) {
                Matcher m = FileRepo.this.REPO_FILE.matcher(name);
                if (!m.matches()) {
                    return false;
                }
                if (versionRange == null) {
                    return true;
                }
                Version v = new Version(m.group(2));
                return versionRange.includes(v);
            }
        });
    }

    public List<String> list(String regex) throws Exception {
        if (regex == null) {
            regex = ".*";
        }
        final Pattern pattern = Pattern.compile(regex);
        String[] list = this.root.list(new FilenameFilter(){

            @Override
            public boolean accept(File dir, String name) {
                Matcher matcher = pattern.matcher(name);
                return matcher.matches();
            }
        });
        return Arrays.asList(list);
    }

    public List<Version> versions(String bsn) throws Exception {
        File dir = new File(this.root, bsn);
        final ArrayList<Version> versions = new ArrayList<Version>();
        dir.list(new FilenameFilter(){

            @Override
            public boolean accept(File dir, String name) {
                Matcher m = FileRepo.this.REPO_FILE.matcher(name);
                if (m.matches()) {
                    versions.add(new Version(m.group(2)));
                    return true;
                }
                return false;
            }
        });
        return versions;
    }

    public File get(String bsn, VersionRange range, int strategy) throws Exception {
        File[] files = this.get(bsn, range);
        if (files == null || files.length == 0) {
            return null;
        }
        if (files.length == 1) {
            return files[0];
        }
        if (strategy < 0) {
            return files[0];
        }
        return files[files.length - 1];
    }

    public File put(String bsn, Version version) throws IOException {
        File dir = new File(this.root, bsn);
        IO.mkdirs(dir);
        File file = new File(dir, bsn + "-" + version.getMajor() + "." + version.getMinor() + "." + version.getMicro() + ".jar");
        return file;
    }
}

