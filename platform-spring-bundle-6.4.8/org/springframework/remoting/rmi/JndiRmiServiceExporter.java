/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.remoting.rmi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Properties;
import javax.naming.NamingException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jndi.JndiTemplate;
import org.springframework.lang.Nullable;
import org.springframework.remoting.rmi.RmiBasedExporter;
import org.springframework.util.ReflectionUtils;

@Deprecated
public class JndiRmiServiceExporter
extends RmiBasedExporter
implements InitializingBean,
DisposableBean {
    @Nullable
    private static Method exportObject;
    @Nullable
    private static Method unexportObject;
    private JndiTemplate jndiTemplate = new JndiTemplate();
    private String jndiName;
    private Remote exportedObject;

    public void setJndiTemplate(JndiTemplate jndiTemplate) {
        this.jndiTemplate = jndiTemplate != null ? jndiTemplate : new JndiTemplate();
    }

    public void setJndiEnvironment(Properties jndiEnvironment) {
        this.jndiTemplate = new JndiTemplate(jndiEnvironment);
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }

    @Override
    public void afterPropertiesSet() throws NamingException, RemoteException {
        this.prepare();
    }

    public void prepare() throws NamingException, RemoteException {
        if (this.jndiName == null) {
            throw new IllegalArgumentException("Property 'jndiName' is required");
        }
        this.exportedObject = this.getObjectToExport();
        this.invokePortableRemoteObject(exportObject);
        this.rebind();
    }

    public void rebind() throws NamingException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Binding RMI service to JNDI location [" + this.jndiName + "]"));
        }
        this.jndiTemplate.rebind(this.jndiName, this.exportedObject);
    }

    @Override
    public void destroy() throws NamingException, RemoteException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Unbinding RMI service from JNDI location [" + this.jndiName + "]"));
        }
        this.jndiTemplate.unbind(this.jndiName);
        this.invokePortableRemoteObject(unexportObject);
    }

    private void invokePortableRemoteObject(@Nullable Method method) throws RemoteException {
        if (method != null) {
            try {
                method.invoke(null, this.exportedObject);
            }
            catch (InvocationTargetException ex) {
                Throwable targetEx = ex.getTargetException();
                if (targetEx instanceof RemoteException) {
                    throw (RemoteException)targetEx;
                }
                ReflectionUtils.rethrowRuntimeException(targetEx);
            }
            catch (Throwable ex) {
                throw new IllegalStateException("PortableRemoteObject invocation failed", ex);
            }
        }
    }

    static {
        try {
            Class<?> portableRemoteObject = JndiRmiServiceExporter.class.getClassLoader().loadClass("javax.rmi.PortableRemoteObject");
            exportObject = portableRemoteObject.getMethod("exportObject", Remote.class);
            unexportObject = portableRemoteObject.getMethod("unexportObject", Remote.class);
        }
        catch (Throwable ex) {
            exportObject = null;
            unexportObject = null;
        }
    }
}

