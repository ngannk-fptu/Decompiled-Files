/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.caucho.hessian.io.AbstractHessianInput
 *  com.caucho.hessian.io.AbstractHessianOutput
 *  com.caucho.hessian.io.Hessian2Input
 *  com.caucho.hessian.io.Hessian2Output
 *  com.caucho.hessian.io.HessianDebugInputStream
 *  com.caucho.hessian.io.HessianDebugOutputStream
 *  com.caucho.hessian.io.HessianInput
 *  com.caucho.hessian.io.HessianOutput
 *  com.caucho.hessian.io.HessianRemoteResolver
 *  com.caucho.hessian.io.SerializerFactory
 *  com.caucho.hessian.server.HessianSkeleton
 *  org.apache.commons.logging.Log
 */
package org.springframework.remoting.caucho;

import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.HessianDebugInputStream;
import com.caucho.hessian.io.HessianDebugOutputStream;
import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.caucho.hessian.io.HessianRemoteResolver;
import com.caucho.hessian.io.SerializerFactory;
import com.caucho.hessian.server.HessianSkeleton;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.remoting.support.RemoteExporter;
import org.springframework.util.Assert;
import org.springframework.util.CommonsLogWriter;

@Deprecated
public class HessianExporter
extends RemoteExporter
implements InitializingBean {
    public static final String CONTENT_TYPE_HESSIAN = "application/x-hessian";
    private SerializerFactory serializerFactory = new SerializerFactory();
    @Nullable
    private HessianRemoteResolver remoteResolver;
    @Nullable
    private Log debugLogger;
    @Nullable
    private HessianSkeleton skeleton;

    public void setSerializerFactory(@Nullable SerializerFactory serializerFactory) {
        this.serializerFactory = serializerFactory != null ? serializerFactory : new SerializerFactory();
    }

    public void setSendCollectionType(boolean sendCollectionType) {
        this.serializerFactory.setSendCollectionType(sendCollectionType);
    }

    public void setAllowNonSerializable(boolean allowNonSerializable) {
        this.serializerFactory.setAllowNonSerializable(allowNonSerializable);
    }

    public void setRemoteResolver(HessianRemoteResolver remoteResolver) {
        this.remoteResolver = remoteResolver;
    }

    public void setDebug(boolean debug) {
        this.debugLogger = debug ? this.logger : null;
    }

    @Override
    public void afterPropertiesSet() {
        this.prepare();
    }

    public void prepare() {
        this.checkService();
        this.checkServiceInterface();
        this.skeleton = new HessianSkeleton(this.getProxyForService(), this.getServiceInterface());
    }

    public void invoke(InputStream inputStream, OutputStream outputStream) throws Throwable {
        Assert.notNull((Object)this.skeleton, "Hessian exporter has not been initialized");
        this.doInvoke(this.skeleton, inputStream, outputStream);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void doInvoke(HessianSkeleton skeleton, InputStream inputStream, OutputStream outputStream) throws Throwable {
        ClassLoader originalClassLoader = this.overrideThreadContextClassLoader();
        try {
            Object out;
            Hessian2Input in;
            int code;
            InputStream isToUse = inputStream;
            OutputStream osToUse = outputStream;
            if (this.debugLogger != null && this.debugLogger.isDebugEnabled()) {
                try (PrintWriter debugWriter = new PrintWriter(new CommonsLogWriter(this.debugLogger));){
                    HessianDebugInputStream dis = new HessianDebugInputStream(inputStream, debugWriter);
                    HessianDebugOutputStream dos = new HessianDebugOutputStream(outputStream, debugWriter);
                    dis.startTop2();
                    dos.startTop2();
                    isToUse = dis;
                    osToUse = dos;
                }
            }
            if (!isToUse.markSupported()) {
                isToUse = new BufferedInputStream(isToUse);
                isToUse.mark(1);
            }
            if ((code = isToUse.read()) == 72) {
                int major = isToUse.read();
                int minor = isToUse.read();
                if (major != 2) {
                    throw new IOException("Version " + major + '.' + minor + " is not understood");
                }
                in = new Hessian2Input(isToUse);
                out = new Hessian2Output(osToUse);
                in.readCall();
            } else if (code == 67) {
                isToUse.reset();
                in = new Hessian2Input(isToUse);
                out = new Hessian2Output(osToUse);
                in.readCall();
            } else if (code == 99) {
                int major = isToUse.read();
                int minor = isToUse.read();
                in = new HessianInput(isToUse);
                out = major >= 2 ? new Hessian2Output(osToUse) : new HessianOutput(osToUse);
            } else {
                throw new IOException("Expected 'H'/'C' (Hessian 2.0) or 'c' (Hessian 1.0) in hessian input at " + code);
            }
            in.setSerializerFactory(this.serializerFactory);
            out.setSerializerFactory(this.serializerFactory);
            if (this.remoteResolver != null) {
                in.setRemoteResolver(this.remoteResolver);
            }
            try {
                skeleton.invoke((AbstractHessianInput)in, (AbstractHessianOutput)out);
            }
            finally {
                try {
                    in.close();
                    isToUse.close();
                }
                catch (IOException iOException) {}
                try {
                    out.close();
                    osToUse.close();
                }
                catch (IOException iOException) {}
            }
        }
        finally {
            this.resetThreadContextClassLoader(originalClassLoader);
        }
    }
}

