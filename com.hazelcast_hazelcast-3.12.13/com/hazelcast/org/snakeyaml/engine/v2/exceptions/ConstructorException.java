/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.exceptions;

import com.hazelcast.org.snakeyaml.engine.v2.exceptions.Mark;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.MarkedYamlEngineException;
import java.util.Optional;

public class ConstructorException
extends MarkedYamlEngineException {
    public ConstructorException(String context, Optional<Mark> contextMark, String problem, Optional<Mark> problemMark, Throwable cause) {
        super(context, contextMark, problem, problemMark, cause);
    }

    public ConstructorException(String context, Optional<Mark> contextMark, String problem, Optional<Mark> problemMark) {
        this(context, contextMark, problem, problemMark, null);
    }
}

