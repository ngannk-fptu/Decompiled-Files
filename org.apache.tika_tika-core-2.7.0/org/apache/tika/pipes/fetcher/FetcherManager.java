/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.pipes.fetcher;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.tika.config.ConfigBase;
import org.apache.tika.exception.TikaConfigException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.pipes.fetcher.Fetcher;

public class FetcherManager
extends ConfigBase {
    private final Map<String, Fetcher> fetcherMap = new ConcurrentHashMap<String, Fetcher>();

    public static FetcherManager load(Path p) throws IOException, TikaConfigException {
        try (InputStream is = Files.newInputStream(p, new OpenOption[0]);){
            FetcherManager fetcherManager = FetcherManager.buildComposite("fetchers", FetcherManager.class, "fetcher", Fetcher.class, is);
            return fetcherManager;
        }
    }

    public FetcherManager(List<Fetcher> fetchers) throws TikaConfigException {
        for (Fetcher fetcher : fetchers) {
            String name = fetcher.getName();
            if (name == null || name.trim().length() == 0) {
                throw new TikaConfigException("fetcher name must not be blank");
            }
            if (this.fetcherMap.containsKey(fetcher.getName())) {
                throw new TikaConfigException("Multiple fetchers cannot support the same prefix: " + fetcher.getName());
            }
            this.fetcherMap.put(fetcher.getName(), fetcher);
        }
    }

    public Fetcher getFetcher(String fetcherName) throws IOException, TikaException {
        Fetcher fetcher = this.fetcherMap.get(fetcherName);
        if (fetcher == null) {
            throw new IllegalArgumentException("Can't find fetcher for fetcherName: " + fetcherName + ". I've loaded: " + this.fetcherMap.keySet());
        }
        return fetcher;
    }

    public Set<String> getSupported() {
        return this.fetcherMap.keySet();
    }

    public Fetcher getFetcher() {
        if (this.fetcherMap.size() == 0) {
            throw new IllegalArgumentException("fetchers size must == 1 for the no arg call");
        }
        if (this.fetcherMap.size() > 1) {
            throw new IllegalArgumentException("need to specify 'fetcherName' if > 1 fetchers are available");
        }
        Iterator<Fetcher> iterator = this.fetcherMap.values().iterator();
        if (iterator.hasNext()) {
            Fetcher fetcher = iterator.next();
            return fetcher;
        }
        throw new IllegalArgumentException("fetchers size must == 0");
    }
}

