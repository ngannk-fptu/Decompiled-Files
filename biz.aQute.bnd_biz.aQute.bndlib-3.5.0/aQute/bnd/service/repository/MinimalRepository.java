/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.service.repository;

import aQute.bnd.version.Version;
import aQute.service.reporter.Report;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public interface MinimalRepository {
    public Report add(File var1) throws Exception;

    public Iterable<String> list(String var1);

    public List<Version> versions(String var1);

    public Future<File> get(String var1, Version var2, Map<String, String> var3);

    public boolean is(Gestalt var1);

    public static enum Gestalt {
        ADD,
        REMOTE;

    }
}

