/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.statistics;

import java.util.List;
import org.jfree.data.category.CategoryDataset;

public interface MultiValueCategoryDataset
extends CategoryDataset {
    public List getValues(int var1, int var2);

    public List getValues(Comparable var1, Comparable var2);
}

