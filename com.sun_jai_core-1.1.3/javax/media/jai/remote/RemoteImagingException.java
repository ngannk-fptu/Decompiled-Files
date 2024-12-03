/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.remote;

import javax.media.jai.util.ImagingException;

public class RemoteImagingException
extends ImagingException {
    public RemoteImagingException() {
    }

    public RemoteImagingException(String message) {
        super(message);
    }

    public RemoteImagingException(Throwable cause) {
        super(cause);
    }

    public RemoteImagingException(String message, Throwable cause) {
        super(message, cause);
    }
}

