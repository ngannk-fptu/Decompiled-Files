/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.metamodel;

import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.IdentifiableType;

public interface EntityType<X>
extends IdentifiableType<X>,
Bindable<X> {
    public String getName();
}

