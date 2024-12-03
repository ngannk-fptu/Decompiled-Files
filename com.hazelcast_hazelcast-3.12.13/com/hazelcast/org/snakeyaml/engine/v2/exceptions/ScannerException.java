/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.exceptions;

import com.hazelcast.org.snakeyaml.engine.v2.exceptions.Mark;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.MarkedYamlEngineException;
import java.util.Optional;

public class ScannerException
extends MarkedYamlEngineException {
    public ScannerException(String context, Optional<Mark> contextMark, String problem, Optional<Mark> problemMark) {
        super(context, contextMark, problem, problemMark, null);
    }

    public ScannerException(String problem, Optional<Mark> problemMark) {
        super(null, Optional.empty(), problem, problemMark, null);
    }
}

