/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.GadgetSpecUriNotAllowedException
 */
package com.atlassian.gadgets.publisher.internal;

import com.atlassian.gadgets.GadgetSpecUriNotAllowedException;

public class PublishedGadgetSpecNotFoundException
extends GadgetSpecUriNotAllowedException {
    public PublishedGadgetSpecNotFoundException(Throwable e) {
        super(e);
    }

    public PublishedGadgetSpecNotFoundException(String message) {
        super(message);
    }
}

