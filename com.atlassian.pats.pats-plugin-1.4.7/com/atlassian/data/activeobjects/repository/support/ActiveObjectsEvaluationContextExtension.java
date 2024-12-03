/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.data.activeobjects.repository.support;

import com.atlassian.data.activeobjects.repository.query.EscapeCharacter;
import org.springframework.data.spel.spi.EvaluationContextExtension;

public class ActiveObjectsEvaluationContextExtension
implements EvaluationContextExtension {
    private final AoRootObject root;

    public ActiveObjectsEvaluationContextExtension(char escapeCharacter) {
        this.root = AoRootObject.of(EscapeCharacter.of(escapeCharacter));
    }

    @Override
    public String getExtensionId() {
        return "ao";
    }

    @Override
    public Object getRootObject() {
        return this.root;
    }

    public static class AoRootObject {
        private final EscapeCharacter character;

        public String escape(String source) {
            return this.character.escape(source);
        }

        public String escapeCharacter() {
            return String.valueOf(this.character.getEscapeCharacter());
        }

        private AoRootObject(EscapeCharacter character) {
            this.character = character;
        }

        public static AoRootObject of(EscapeCharacter character) {
            return new AoRootObject(character);
        }
    }
}

