/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.exceptions;

import com.hazelcast.org.snakeyaml.engine.v2.exceptions.ConstructorException;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.Mark;
import java.util.Optional;

public class DuplicateKeyException
extends ConstructorException {
    public DuplicateKeyException(Optional<Mark> contextMark, Object key, Optional<Mark> problemMark) {
        super("while constructing a mapping", contextMark, "found duplicate key " + key.toString(), problemMark);
    }
}

