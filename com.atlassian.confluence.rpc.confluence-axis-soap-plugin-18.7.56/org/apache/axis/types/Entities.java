/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.types;

import java.util.StringTokenizer;
import org.apache.axis.types.Entity;
import org.apache.axis.types.NCName;

public class Entities
extends NCName {
    private Entity[] entities;

    public Entities() {
    }

    public Entities(String stValue) throws IllegalArgumentException {
        StringTokenizer tokenizer = new StringTokenizer(stValue);
        int count = tokenizer.countTokens();
        this.entities = new Entity[count];
        for (int i = 0; i < count; ++i) {
            this.entities[i] = new Entity(tokenizer.nextToken());
        }
    }
}

