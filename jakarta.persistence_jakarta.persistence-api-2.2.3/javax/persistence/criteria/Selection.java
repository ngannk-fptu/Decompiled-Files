/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.criteria;

import java.util.List;
import javax.persistence.TupleElement;

public interface Selection<X>
extends TupleElement<X> {
    public Selection<X> alias(String var1);

    public boolean isCompoundSelection();

    public List<Selection<?>> getCompoundSelectionItems();
}

