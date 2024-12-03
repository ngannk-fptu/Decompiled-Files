/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.util.promise;

import java.util.Collection;
import java.util.Collections;
import org.osgi.util.promise.Promise;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FailedPromisesException
extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final Collection<Promise<?>> failed;

    public FailedPromisesException(Collection<Promise<?>> failed, Throwable cause) {
        super(cause);
        this.failed = Collections.unmodifiableCollection(failed);
    }

    public Collection<Promise<?>> getFailedPromises() {
        return this.failed;
    }
}

