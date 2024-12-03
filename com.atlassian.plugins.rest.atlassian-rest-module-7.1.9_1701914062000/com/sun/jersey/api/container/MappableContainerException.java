/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.container;

import com.sun.jersey.api.container.ContainerException;

public class MappableContainerException
extends ContainerException {
    public MappableContainerException(Throwable cause) {
        super(MappableContainerException.strip(cause));
    }

    private static Throwable strip(Throwable cause) {
        if (cause instanceof MappableContainerException) {
            MappableContainerException mce;
            while ((cause = (mce = (MappableContainerException)cause).getCause()) instanceof MappableContainerException) {
            }
        }
        return cause;
    }
}

