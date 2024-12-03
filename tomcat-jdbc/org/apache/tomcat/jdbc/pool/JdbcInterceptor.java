/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jdbc.pool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.apache.tomcat.jdbc.pool.PooledConnection;

public abstract class JdbcInterceptor
implements InvocationHandler {
    public static final String CLOSE_VAL = "close";
    public static final String TOSTRING_VAL = "toString";
    public static final String ISCLOSED_VAL = "isClosed";
    public static final String GETCONNECTION_VAL = "getConnection";
    public static final String UNWRAP_VAL = "unwrap";
    public static final String ISWRAPPERFOR_VAL = "isWrapperFor";
    public static final String ISVALID_VAL = "isValid";
    public static final String EQUALS_VAL = "equals";
    public static final String HASHCODE_VAL = "hashCode";
    protected Map<String, PoolProperties.InterceptorProperty> properties = null;
    private volatile JdbcInterceptor next = null;
    private boolean useEquals = true;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (this.getNext() != null) {
            return this.getNext().invoke(proxy, method, args);
        }
        throw new NullPointerException();
    }

    public JdbcInterceptor getNext() {
        return this.next;
    }

    public void setNext(JdbcInterceptor next) {
        this.next = next;
    }

    public boolean compare(String name1, String name2) {
        if (this.isUseEquals()) {
            return name1.equals(name2);
        }
        return name1 == name2;
    }

    public boolean compare(String methodName, Method method) {
        return this.compare(methodName, method.getName());
    }

    public abstract void reset(ConnectionPool var1, PooledConnection var2);

    public void disconnected(ConnectionPool parent, PooledConnection con, boolean finalizing) {
    }

    public Map<String, PoolProperties.InterceptorProperty> getProperties() {
        return this.properties;
    }

    public void setProperties(Map<String, PoolProperties.InterceptorProperty> properties) {
        this.properties = properties;
        String useEquals = "useEquals";
        PoolProperties.InterceptorProperty p = properties.get("useEquals");
        if (p != null) {
            this.setUseEquals(Boolean.parseBoolean(p.getValue()));
        }
    }

    public boolean isUseEquals() {
        return this.useEquals;
    }

    public void setUseEquals(boolean useEquals) {
        this.useEquals = useEquals;
    }

    public void poolClosed(ConnectionPool pool) {
    }

    public void poolStarted(ConnectionPool pool) {
    }
}

