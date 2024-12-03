/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.util.Collection;
import javax.media.jai.CollectionOp;
import javax.media.jai.PropertyChangeEventJAI;

public class CollectionChangeEvent
extends PropertyChangeEventJAI {
    public CollectionChangeEvent(CollectionOp source, Collection oldValue, Collection newValue) {
        super(source, "Collection", oldValue, newValue);
    }
}

