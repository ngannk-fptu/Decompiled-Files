/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.ws;

import java.util.concurrent.Future;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Response;

public interface Dispatch<T>
extends BindingProvider {
    public T invoke(T var1);

    public Response<T> invokeAsync(T var1);

    public Future<?> invokeAsync(T var1, AsyncHandler<T> var2);

    public void invokeOneWay(T var1);
}

