/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.vault.repository.query;

import java.util.function.Predicate;
import org.springframework.util.Assert;
import org.springframework.vault.repository.convert.SecretDocument;

public class VaultQuery {
    private final Predicate<String> predicate;

    public VaultQuery() {
        this(s -> true);
    }

    public VaultQuery(Predicate<String> predicate) {
        Assert.notNull(predicate, (String)"Predicate must not be null");
        this.predicate = predicate;
    }

    public boolean test(SecretDocument document) {
        Assert.notNull(this.predicate, (String)"Predicate must not be null");
        return this.predicate.test(document.getId());
    }

    public boolean test(String id) {
        Assert.notNull((Object)id, (String)"Id to test must not be null");
        return this.predicate.test(id);
    }

    public VaultQuery and(VaultQuery other) {
        return new VaultQuery(this.predicate.and(other.predicate));
    }

    public VaultQuery and(Predicate<String> predicate) {
        return new VaultQuery(this.predicate.and(predicate));
    }

    public VaultQuery or(VaultQuery other) {
        return new VaultQuery(this.predicate.or(other.predicate));
    }

    public Predicate<String> getPredicate() {
        return this.predicate;
    }
}

