/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.builder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;
import javax.sql.DataSource;
import net.java.ao.Disposable;
import net.java.ao.DisposableDataSource;

public final class DelegatingDisposableDataSourceHandler
implements InvocationHandler {
    private final DataSource dataSource;
    private final Disposable disposable;

    public DelegatingDisposableDataSourceHandler(DataSource dataSource, Disposable disposable) {
        this.dataSource = Objects.requireNonNull(dataSource, "dataSource can't be null");
        this.disposable = Objects.requireNonNull(disposable, "disposable can't be null");
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (DelegatingDisposableDataSourceHandler.isDisposeMethod(method)) {
            this.disposable.dispose();
            return null;
        }
        return this.delegate(method, args);
    }

    private Object delegate(Method method, Object[] args) throws Throwable {
        Method m = this.dataSource.getClass().getMethod(method.getName(), method.getParameterTypes());
        m.setAccessible(true);
        try {
            return m.invoke((Object)this.dataSource, args);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    public static DisposableDataSource newInstance(DataSource ds, Disposable disposable) {
        return (DisposableDataSource)Proxy.newProxyInstance(DelegatingDisposableDataSourceHandler.class.getClassLoader(), new Class[]{DisposableDataSource.class}, (InvocationHandler)new DelegatingDisposableDataSourceHandler(ds, disposable));
    }

    private static boolean isDisposeMethod(Method method) {
        return method.getName().equals("dispose") && method.getParameterTypes().length == 0;
    }
}

