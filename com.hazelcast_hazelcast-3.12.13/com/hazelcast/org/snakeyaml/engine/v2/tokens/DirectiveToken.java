/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.tokens;

import com.hazelcast.org.snakeyaml.engine.v2.exceptions.Mark;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.Token;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class DirectiveToken<T>
extends Token {
    private final String name;
    private final Optional<List<T>> value;
    public static final String YAML_DIRECTIVE = "YAML";
    public static final String TAG_DIRECTIVE = "TAG";

    public DirectiveToken(String name, Optional<List<T>> value, Optional<Mark> startMark, Optional<Mark> endMark) {
        super(startMark, endMark);
        Objects.requireNonNull(name);
        this.name = name;
        Objects.requireNonNull(value);
        if (value.isPresent() && value.get().size() != 2) {
            throw new YamlEngineException("Two strings/integers must be provided instead of " + value.get().size());
        }
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public Optional<List<T>> getValue() {
        return this.value;
    }

    @Override
    public Token.ID getTokenId() {
        return Token.ID.Directive;
    }
}

