/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.exceptions;

import com.hazelcast.org.snakeyaml.engine.v2.exceptions.Mark;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.MarkedYamlEngineException;
import java.util.Optional;

public class ParserException
extends MarkedYamlEngineException {
    public ParserException(String context, Optional<Mark> contextMark, String problem, Optional<Mark> problemMark) {
        super(context, contextMark, problem, problemMark, null);
    }

    public ParserException(String problem, Optional<Mark> problemMark) {
        super(null, Optional.empty(), problem, problemMark, null);
    }
}

