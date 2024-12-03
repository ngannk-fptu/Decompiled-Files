/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.data.history;

import java.util.Arrays;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;

public class RevisionSort
extends Sort {
    private static final long serialVersionUID = 618238321589063537L;
    private static final String PROPERTY = "__revisionNumber__";
    private static final RevisionSort ASC = new RevisionSort(Sort.Direction.ASC);
    private static final RevisionSort DESC = new RevisionSort(Sort.Direction.DESC);

    private RevisionSort(Sort.Direction direction) {
        super(Arrays.asList(new Sort.Order(direction, PROPERTY)));
    }

    public static RevisionSort asc() {
        return ASC;
    }

    public static RevisionSort desc() {
        return DESC;
    }

    public static Sort.Direction getRevisionDirection(Sort sort) {
        Assert.notNull((Object)sort, (String)"Sort must not be null!");
        Sort.Order order = sort.getOrderFor(PROPERTY);
        return order == null ? Sort.Direction.ASC : order.getDirection();
    }
}

