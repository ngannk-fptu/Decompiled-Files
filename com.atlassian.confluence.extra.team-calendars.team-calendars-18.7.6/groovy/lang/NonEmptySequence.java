/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.Sequence;
import java.util.List;

public class NonEmptySequence
extends Sequence {
    public NonEmptySequence() {
        super((Class)null);
    }

    public NonEmptySequence(Class type) {
        super(type);
    }

    public NonEmptySequence(Class type, List content) {
        super(type, content);
    }

    @Override
    public int minimumSize() {
        return 1;
    }
}

