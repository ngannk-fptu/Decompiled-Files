/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.hpack;

import org.apache.hc.core5.http2.hpack.HPackException;

public class HeaderListConstraintException
extends HPackException {
    private static final long serialVersionUID = 9130981983188889920L;

    public HeaderListConstraintException(String message) {
        super(message);
    }
}

