/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.engine.jdbc.ColumnNameCache;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.service.ServiceRegistry;

@Deprecated
public class ResultSetWrapperProxy
implements InvocationHandler {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(ResultSetWrapperProxy.class);
    private static final SqlExceptionHelper SQL_EXCEPTION_HELPER = new SqlExceptionHelper(false);
    private static final Map<ResultSetMethodKey, Method> NAME_TO_INDEX_METHOD_MAPPING;
    private final ResultSet rs;
    private final ColumnNameCache columnNameCache;

    private ResultSetWrapperProxy(ResultSet rs, ColumnNameCache columnNameCache) {
        this.rs = rs;
        this.columnNameCache = columnNameCache;
    }

    public static ResultSet generateProxy(ResultSet resultSet, ColumnNameCache columnNameCache, ServiceRegistry serviceRegistry) {
        return (ResultSet)serviceRegistry.getService(ClassLoaderService.class).generateProxy(new ResultSetWrapperProxy(resultSet, columnNameCache), ResultSet.class);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Method columnIndexMethod;
        if ("findColumn".equals(method.getName())) {
            return this.findColumn((String)args[0]);
        }
        if (ResultSetWrapperProxy.isFirstArgColumnLabel(method) && (columnIndexMethod = NAME_TO_INDEX_METHOD_MAPPING.get(new ResultSetMethodKey(method.getName(), method.getParameterTypes()))) != null) {
            try {
                Integer columnIndex = this.findColumn((String)args[0]);
                return this.invokeMethod(columnIndexMethod, this.buildColumnIndexMethodArgs(args, columnIndex));
            }
            catch (SQLException ex) {
                String msg = "Exception getting column index for column: [" + args[0] + "].\nReverting to using: [" + args[0] + "] as first argument for method: [" + method + "]";
                SQL_EXCEPTION_HELPER.logExceptions(ex, msg);
            }
        }
        return this.invokeMethod(method, args);
    }

    private Integer findColumn(String columnName) throws SQLException {
        return this.columnNameCache.getIndexForColumnName(columnName, this.rs);
    }

    private static boolean isFirstArgColumnLabel(Method method) {
        if (!method.getName().startsWith("get") && !method.getName().startsWith("update")) {
            return false;
        }
        if (method.getParameterCount() <= 0) {
            return false;
        }
        return method.getParameterTypes()[0].equals(String.class);
    }

    private static Method locateCorrespondingColumnIndexMethod(Method columnNameMethod) throws NoSuchMethodException {
        Class[] actualParameterTypes = new Class[columnNameMethod.getParameterCount()];
        actualParameterTypes[0] = Integer.TYPE;
        System.arraycopy(columnNameMethod.getParameterTypes(), 1, actualParameterTypes, 1, columnNameMethod.getParameterCount() - 1);
        return columnNameMethod.getDeclaringClass().getMethod(columnNameMethod.getName(), actualParameterTypes);
    }

    private Object[] buildColumnIndexMethodArgs(Object[] incomingArgs, Integer columnIndex) {
        Object[] actualArgs = new Object[incomingArgs.length];
        actualArgs[0] = columnIndex;
        System.arraycopy(incomingArgs, 1, actualArgs, 1, incomingArgs.length - 1);
        return actualArgs;
    }

    private Object invokeMethod(Method method, Object[] args) throws Throwable {
        try {
            return method.invoke((Object)this.rs, args);
        }
        catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    static {
        HashMap<ResultSetMethodKey, Method> nameToIndexMethodMapping = new HashMap<ResultSetMethodKey, Method>();
        for (Method method : ResultSet.class.getDeclaredMethods()) {
            if (!ResultSetWrapperProxy.isFirstArgColumnLabel(method)) continue;
            try {
                nameToIndexMethodMapping.put(new ResultSetMethodKey(method.getName(), method.getParameterTypes()), ResultSetWrapperProxy.locateCorrespondingColumnIndexMethod(method));
            }
            catch (NoSuchMethodException e) {
                LOG.unableToSwitchToMethodUsingColumnIndex(method);
            }
        }
        NAME_TO_INDEX_METHOD_MAPPING = Collections.unmodifiableMap(nameToIndexMethodMapping);
    }

    private static class ResultSetMethodKey {
        private String methodName;
        private Class<?>[] parameterTypes;

        public ResultSetMethodKey(String methodName, Class<?>[] parameterTypes) {
            this.methodName = methodName;
            this.parameterTypes = parameterTypes;
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + this.methodName.hashCode();
            result = 31 * result + Arrays.hashCode(this.parameterTypes);
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            ResultSetMethodKey other = (ResultSetMethodKey)obj;
            if (!this.methodName.equals(other.methodName)) {
                return false;
            }
            return Arrays.equals(this.parameterTypes, other.parameterTypes);
        }
    }
}

