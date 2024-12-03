/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.service.repository;

import aQute.bnd.service.RepositoryPlugin;
import aQute.bnd.service.repository.SearchableRepository;
import aQute.bnd.version.VersionRange;
import java.io.File;
import java.util.List;
import java.util.SortedSet;

public interface ResourceRepository {
    public static final String FILENAME = "repo.json";

    public List<? extends SearchableRepository.ResourceDescriptor> filter(String var1, String var2) throws Exception;

    public File getResource(byte[] var1, RepositoryPlugin.DownloadListener ... var2) throws Exception;

    public SearchableRepository.ResourceDescriptor getResourceDescriptor(byte[] var1) throws Exception;

    public boolean delete(String var1, byte[] var2) throws Exception;

    public boolean add(String var1, SearchableRepository.ResourceDescriptor var2) throws Exception;

    public void addListener(Listener var1);

    public boolean deleteCache(byte[] var1) throws Exception;

    public SortedSet<SearchableRepository.ResourceDescriptor> find(String var1, String var2, VersionRange var3) throws Exception;

    public File getCacheDir(String var1);

    public static interface Listener {
        public void events(ResourceRepositoryEvent ... var1) throws Exception;
    }

    public static class ResourceRepositoryEvent {
        public TYPE type;
        public SearchableRepository.ResourceDescriptor descriptor;
        public Exception exception;

        public ResourceRepositoryEvent(TYPE type, SearchableRepository.ResourceDescriptor rds, Exception exception) {
            this.type = type;
            this.descriptor = rds;
            this.exception = exception;
        }
    }

    public static enum TYPE {
        ADD,
        REMOVE,
        START_DOWNLOAD,
        END_DOWNLOAD,
        ERROR;

    }
}

