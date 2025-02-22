/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.exceptions;

import com.hazelcast.org.snakeyaml.engine.v2.exceptions.Mark;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.MarkedYamlEngineException;
import java.util.Objects;
import java.util.Optional;

public class ComposerException
extends MarkedYamlEngineException {
    public ComposerException(String context, Optional<Mark> contextMark, String problem, Optional<Mark> problemMark) {
        super(context, contextMark, problem, problemMark);
        Objects.requireNonNull(context);
    }

    public ComposerException(String problem, Optional<Mark> problemMark) {
        super("", Optional.empty(), problem, problemMark);
    }
}

