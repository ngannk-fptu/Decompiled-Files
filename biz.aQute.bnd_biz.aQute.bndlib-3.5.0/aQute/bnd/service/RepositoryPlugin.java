/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.service;

import aQute.bnd.osgi.Processor;
import aQute.bnd.version.Version;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

public interface RepositoryPlugin {
    public static final PutOptions DEFAULTOPTIONS = new PutOptions();

    public PutResult put(InputStream var1, PutOptions var2) throws Exception;

    public File get(String var1, Version var2, Map<String, String> var3, DownloadListener ... var4) throws Exception;

    public boolean canWrite();

    public List<String> list(String var1) throws Exception;

    public SortedSet<Version> versions(String var1) throws Exception;

    public String getName();

    public String getLocation();

    public static interface DownloadListener {
        public void success(File var1) throws Exception;

        public void failure(File var1, String var2) throws Exception;

        public boolean progress(File var1, int var2) throws Exception;
    }

    public static class PutResult {
        public URI artifact = null;
        public byte[] digest = null;
        public boolean alreadyReleased;
    }

    public static class PutOptions {
        public static final String BUNDLE = "application/vnd.osgi.bundle";
        public static final String LIB = "application/vnd.aQute.lib";
        public byte[] digest = null;
        public String type;
        public String bsn = null;
        public Version version = null;
        public Processor context;
    }
}

