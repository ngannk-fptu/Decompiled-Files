/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.pipes.emitter;

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
import org.apache.tika.pipes.emitter.Emitter;

public class EmitterManager
extends ConfigBase {
    private final Map<String, Emitter> emitterMap = new ConcurrentHashMap<String, Emitter>();

    public static EmitterManager load(Path tikaConfigPath) throws IOException, TikaConfigException {
        try (InputStream is = Files.newInputStream(tikaConfigPath, new OpenOption[0]);){
            EmitterManager emitterManager = EmitterManager.buildComposite("emitters", EmitterManager.class, "emitter", Emitter.class, is);
            return emitterManager;
        }
    }

    private EmitterManager() {
    }

    public EmitterManager(List<Emitter> emitters) {
        for (Emitter emitter : emitters) {
            if (this.emitterMap.containsKey(emitter.getName())) {
                throw new IllegalArgumentException("Multiple emitters cannot support the same name: " + emitter.getName());
            }
            this.emitterMap.put(emitter.getName(), emitter);
        }
    }

    public Set<String> getSupported() {
        return this.emitterMap.keySet();
    }

    public Emitter getEmitter(String emitterName) {
        Emitter emitter = this.emitterMap.get(emitterName);
        if (emitter == null) {
            throw new IllegalArgumentException("Can't find emitter for prefix: " + emitterName);
        }
        return emitter;
    }

    public Emitter getEmitter() {
        if (this.emitterMap.size() == 0) {
            throw new IllegalArgumentException("emitters size must == 1 for the no arg call");
        }
        if (this.emitterMap.size() > 1) {
            throw new IllegalArgumentException("need to specify 'emitterName' if > 1 emitters are available");
        }
        Iterator<Emitter> iterator = this.emitterMap.values().iterator();
        if (iterator.hasNext()) {
            Emitter emitter = iterator.next();
            return emitter;
        }
        throw new IllegalArgumentException("emitters size must == 0");
    }
}

