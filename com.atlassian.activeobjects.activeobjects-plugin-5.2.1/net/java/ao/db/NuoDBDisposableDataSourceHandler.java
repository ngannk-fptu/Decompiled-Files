/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.java.ao.db;

import com.google.common.collect.Maps;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.sql.DataSource;
import net.java.ao.Disposable;
import net.java.ao.DisposableDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NuoDBDisposableDataSourceHandler {
    private static final ClassLoader CLASS_LOADER = NuoDBDisposableDataSourceHandler.class.getClassLoader();

    public static DisposableDataSource newInstance(DataSource dataSource) {
        return NuoDBDisposableDataSourceHandler.newInstance(dataSource, null);
    }

    public static DisposableDataSource newInstance(DataSource dataSource, Disposable disposable) {
        return (DisposableDataSource)NuoDBDisposableDataSourceHandler.newProxy(new Class[]{DisposableDataSource.class}, new DelegatingDisposableDataSourceHandler(dataSource, disposable));
    }

    private static Object newProxy(Class<?>[] interfaces, InvocationHandler invocationHandler) {
        Object proxy = Proxy.newProxyInstance(CLASS_LOADER, interfaces, invocationHandler);
        return proxy;
    }

    static class DelegatingInvocationHandler<T>
    implements InvocationHandler {
        protected final Logger logger = LoggerFactory.getLogger(this.getClass());
        protected final T target;

        protected DelegatingInvocationHandler(T target) {
            this.target = Objects.requireNonNull(target, "target can't be null");
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return this.delegate(method, args);
        }

        protected Object delegate(Method method, Object[] args) throws Throwable {
            Method m = this.target.getClass().getMethod(method.getName(), method.getParameterTypes());
            m.setAccessible(true);
            try {
                return m.invoke(this.target, args);
            }
            catch (IllegalAccessException exception) {
                throw new RuntimeException(exception);
            }
            catch (IllegalArgumentException exception) {
                throw new RuntimeException(exception);
            }
            catch (InvocationTargetException exception) {
                throw exception.getCause();
            }
        }
    }

    static class DelegatingResultSetHandler
    extends DelegatingInvocationHandler<ResultSet> {
        private static final String GET_STATEMENT = "getStatement";
        private static final String GET_OBJECT = "getObject";
        private ResultSetMetaData metaData;
        private Statement statement;
        private Map<String, Integer> columns;

        protected DelegatingResultSetHandler(Statement statement, ResultSet resultSet) throws SQLException {
            super(resultSet);
            this.statement = statement;
            this.metaData = ((ResultSet)this.target).getMetaData();
            int count = this.metaData.getColumnCount();
            HashMap columns = Maps.newHashMap();
            for (int index = 0; index < count; ++index) {
                int column = index + 1;
                columns.put(this.metaData.getColumnName(column), column);
            }
            this.columns = columns;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object result;
            String name = method.getName();
            if (name.equals(GET_STATEMENT)) {
                result = this.statement;
            } else if (name.equals(GET_OBJECT)) {
                Integer column = null;
                column = method.getParameterTypes()[0].equals(String.class) ? this.columns.get(args[0]) : (Integer)args[0];
                result = column != null && this.metaData.getColumnType(column) == -5 ? Long.valueOf(((ResultSet)this.target).getLong(column)) : super.invoke(proxy, method, args);
            } else {
                result = super.invoke(proxy, method, args);
            }
            return result;
        }
    }

    static class DelegatingStatementHandler
    extends DelegatingInvocationHandler<Statement> {
        private static final String GET_CONNECTION = "getConnection";
        private static final String GET_RESULT_SET = "getResultSet";
        private static final String EXECUTE_QUERY = "executeQuery";
        private Connection connection;

        public DelegatingStatementHandler(Connection connection, Statement statement) {
            super(statement);
            this.connection = connection;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName();
            Object result = name.equals(GET_CONNECTION) ? this.connection : (name.equals(EXECUTE_QUERY) || name.equals(GET_RESULT_SET) ? NuoDBDisposableDataSourceHandler.newProxy(new Class[]{ResultSet.class}, new DelegatingResultSetHandler((Statement)proxy, (ResultSet)super.invoke(proxy, method, args))) : super.invoke(proxy, method, args));
            return result;
        }
    }

    static class DelegatingConnectionHandler
    extends DelegatingInvocationHandler<Connection> {
        public static final String CREATE_STATEMENT = "createStatement";
        public static final String PREPARE_STATEMENT = "prepareStatement";
        public static final String PREPARE_CALL = "prepareCall";

        public DelegatingConnectionHandler(Connection connection) {
            super(connection);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName();
            Class<?>[] parameterTypes = method.getParameterTypes();
            Class<Statement> statement = null;
            Integer resultSetTypeArgIndex = null;
            if (name.equals(CREATE_STATEMENT)) {
                statement = Statement.class;
                if (parameterTypes.length >= 2) {
                    resultSetTypeArgIndex = 0;
                }
            } else if (name.equals(PREPARE_STATEMENT)) {
                statement = PreparedStatement.class;
                if (parameterTypes.length >= 3) {
                    resultSetTypeArgIndex = 1;
                }
            } else if (name.equals(PREPARE_CALL)) {
                statement = CallableStatement.class;
                if (parameterTypes.length >= 3) {
                    resultSetTypeArgIndex = 1;
                }
            }
            if (resultSetTypeArgIndex != null && (Integer)args[resultSetTypeArgIndex] != 1003) {
                args[resultSetTypeArgIndex.intValue()] = 1003;
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("Result set type changed to forward only");
                }
            }
            Object result = super.invoke(proxy, method, args);
            return statement != null ? NuoDBDisposableDataSourceHandler.newProxy(new Class[]{statement}, new DelegatingStatementHandler((Connection)proxy, (Statement)result)) : result;
        }
    }

    static class DelegatingDisposableDataSourceHandler
    extends DelegatingInvocationHandler<DataSource> {
        private static final String GET_CONNECTION = "getConnection";
        private static final String DISPOSE = "dispose";
        private Disposable disposable;

        public DelegatingDisposableDataSourceHandler(DataSource dataSource, Disposable disposable) {
            super(dataSource);
            this.disposable = disposable;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName();
            Class<?>[] parameterTypes = method.getParameterTypes();
            Object result = null;
            if (name.equals(DISPOSE) && parameterTypes.length == 0 && this.disposable != null) {
                this.disposable.dispose();
            } else if (name.equals(GET_CONNECTION)) {
                Connection connection = (Connection)this.delegate(method, args);
                result = NuoDBDisposableDataSourceHandler.newProxy(new Class[]{Connection.class}, new DelegatingConnectionHandler(connection));
            } else {
                result = this.delegate(method, args);
            }
            return result;
        }
    }
}

