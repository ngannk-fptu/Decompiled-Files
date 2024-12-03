/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.ordering;

import org.apache.jackrabbit.webdav.ordering.OrderingConstants;
import org.apache.jackrabbit.webdav.property.HrefProperty;

public class OrderingType
extends HrefProperty
implements OrderingConstants {
    public OrderingType() {
        this((String)null);
    }

    public OrderingType(String href) {
        super(ORDERING_TYPE, href != null ? href : "DAV:unordered", true);
    }
}

