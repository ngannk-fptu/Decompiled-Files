/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.search;

import com.atlassian.user.Entity;
import java.text.Collator;
import java.util.Comparator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class EntityNameAlphaComparator
implements Comparator<Entity> {
    private final Collator collator;

    public EntityNameAlphaComparator(Collator collator) {
        this.collator = collator;
    }

    public EntityNameAlphaComparator() {
        this.collator = Collator.getInstance();
    }

    @Override
    public int compare(Entity o1, Entity o2) {
        return this.collator.compare(o1.getName(), o2.getName());
    }
}

