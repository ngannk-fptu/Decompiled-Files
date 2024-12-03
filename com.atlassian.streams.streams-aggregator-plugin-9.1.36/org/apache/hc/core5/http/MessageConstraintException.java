/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http;

import java.io.IOException;
import org.apache.hc.core5.http.HttpException;

public class MessageConstraintException
extends IOException {
    private static final long serialVersionUID = 6077207720446368695L;

    public MessageConstraintException(String message) {
        super(HttpException.clean(message));
    }
}

