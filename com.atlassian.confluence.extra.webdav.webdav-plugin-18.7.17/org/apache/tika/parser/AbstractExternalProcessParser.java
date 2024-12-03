/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.parser;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.tika.parser.AbstractParser;

public abstract class AbstractExternalProcessParser
extends AbstractParser {
    private static final long serialVersionUID = 7186985395903074255L;
    private static final ConcurrentHashMap<String, Process> PROCESS_MAP = new ConcurrentHashMap();

    protected String register(Process p) {
        String id = UUID.randomUUID().toString();
        PROCESS_MAP.put(id, p);
        return id;
    }

    protected Process release(String id) {
        return PROCESS_MAP.remove(id);
    }

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> PROCESS_MAP.forEachValue(1L, Process::destroyForcibly)));
    }
}

