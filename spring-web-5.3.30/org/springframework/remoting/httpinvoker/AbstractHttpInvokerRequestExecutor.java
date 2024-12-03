/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.BeanClassLoaderAware
 *  org.springframework.lang.Nullable
 *  org.springframework.remoting.rmi.CodebaseAwareObjectInputStream
 *  org.springframework.remoting.support.RemoteInvocation
 *  org.springframework.remoting.support.RemoteInvocationResult
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.remoting.httpinvoker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.lang.Nullable;
import org.springframework.remoting.httpinvoker.HttpInvokerClientConfiguration;
import org.springframework.remoting.httpinvoker.HttpInvokerRequestExecutor;
import org.springframework.remoting.rmi.CodebaseAwareObjectInputStream;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationResult;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

@Deprecated
public abstract class AbstractHttpInvokerRequestExecutor
implements HttpInvokerRequestExecutor,
BeanClassLoaderAware {
    public static final String CONTENT_TYPE_SERIALIZED_OBJECT = "application/x-java-serialized-object";
    private static final int SERIALIZED_INVOCATION_BYTE_ARRAY_INITIAL_SIZE = 1024;
    protected static final String HTTP_METHOD_POST = "POST";
    protected static final String HTTP_HEADER_ACCEPT_LANGUAGE = "Accept-Language";
    protected static final String HTTP_HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    protected static final String HTTP_HEADER_CONTENT_ENCODING = "Content-Encoding";
    protected static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
    protected static final String HTTP_HEADER_CONTENT_LENGTH = "Content-Length";
    protected static final String ENCODING_GZIP = "gzip";
    protected final Log logger = LogFactory.getLog(this.getClass());
    private String contentType = "application/x-java-serialized-object";
    private boolean acceptGzipEncoding = true;
    @Nullable
    private ClassLoader beanClassLoader;

    public void setContentType(String contentType) {
        Assert.notNull((Object)contentType, (String)"'contentType' must not be null");
        this.contentType = contentType;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setAcceptGzipEncoding(boolean acceptGzipEncoding) {
        this.acceptGzipEncoding = acceptGzipEncoding;
    }

    public boolean isAcceptGzipEncoding() {
        return this.acceptGzipEncoding;
    }

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Nullable
    protected ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }

    @Override
    public final RemoteInvocationResult executeRequest(HttpInvokerClientConfiguration config, RemoteInvocation invocation) throws Exception {
        ByteArrayOutputStream baos = this.getByteArrayOutputStream(invocation);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Sending HTTP invoker request for service at [" + config.getServiceUrl() + "], with size " + baos.size()));
        }
        return this.doExecuteRequest(config, baos);
    }

    protected ByteArrayOutputStream getByteArrayOutputStream(RemoteInvocation invocation) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        this.writeRemoteInvocation(invocation, baos);
        return baos;
    }

    protected void writeRemoteInvocation(RemoteInvocation invocation, OutputStream os) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(this.decorateOutputStream(os));){
            this.doWriteRemoteInvocation(invocation, oos);
        }
    }

    protected OutputStream decorateOutputStream(OutputStream os) throws IOException {
        return os;
    }

    protected void doWriteRemoteInvocation(RemoteInvocation invocation, ObjectOutputStream oos) throws IOException {
        oos.writeObject(invocation);
    }

    protected abstract RemoteInvocationResult doExecuteRequest(HttpInvokerClientConfiguration var1, ByteArrayOutputStream var2) throws Exception;

    protected RemoteInvocationResult readRemoteInvocationResult(InputStream is, @Nullable String codebaseUrl) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = this.createObjectInputStream(this.decorateInputStream(is), codebaseUrl);){
            RemoteInvocationResult remoteInvocationResult = this.doReadRemoteInvocationResult(ois);
            return remoteInvocationResult;
        }
    }

    protected InputStream decorateInputStream(InputStream is) throws IOException {
        return is;
    }

    protected ObjectInputStream createObjectInputStream(InputStream is, @Nullable String codebaseUrl) throws IOException {
        return new CodebaseAwareObjectInputStream(is, this.getBeanClassLoader(), codebaseUrl);
    }

    protected RemoteInvocationResult doReadRemoteInvocationResult(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        Object obj = ois.readObject();
        if (!(obj instanceof RemoteInvocationResult)) {
            throw new RemoteException("Deserialized object needs to be assignable to type [" + RemoteInvocationResult.class.getName() + "]: " + ClassUtils.getDescriptiveType((Object)obj));
        }
        return (RemoteInvocationResult)obj;
    }
}

