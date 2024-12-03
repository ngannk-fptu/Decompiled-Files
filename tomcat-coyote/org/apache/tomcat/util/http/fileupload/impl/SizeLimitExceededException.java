/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.http.fileupload.impl;

import org.apache.tomcat.util.http.fileupload.impl.SizeException;

public class SizeLimitExceededException
extends SizeException {
    private static final long serialVersionUID = -2474893167098052828L;

    public SizeLimitExceededException(String message, long actual, long permitted) {
        super(message, actual, permitted);
    }
}

