/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.ws;

import java.util.Map;
import java.util.concurrent.Future;

public interface Response<T>
extends Future<T> {
    public Map<String, Object> getContext();
}

