/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ConsumerType
 */
package org.osgi.util.promise;

import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.util.promise.Promise;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@ConsumerType
public interface Failure {
    public void fail(Promise<?> var1) throws Exception;
}

