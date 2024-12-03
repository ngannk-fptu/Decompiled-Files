/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.extensions;

import java.lang.reflect.Constructor;
import javax.xml.transform.TransformerException;
import org.apache.xalan.extensions.ExtensionHandler;

public class ExtensionNamespaceSupport {
    String m_namespace = null;
    String m_handlerClass = null;
    Class[] m_sig = null;
    Object[] m_args = null;

    public ExtensionNamespaceSupport(String namespace, String handlerClass, Object[] constructorArgs) {
        this.m_namespace = namespace;
        this.m_handlerClass = handlerClass;
        this.m_args = constructorArgs;
        this.m_sig = new Class[this.m_args.length];
        for (int i = 0; i < this.m_args.length; ++i) {
            if (this.m_args[i] == null) {
                this.m_sig = null;
                break;
            }
            this.m_sig[i] = this.m_args[i].getClass();
        }
    }

    public String getNamespace() {
        return this.m_namespace;
    }

    public ExtensionHandler launch() throws TransformerException {
        ExtensionHandler handler = null;
        try {
            Class cl = ExtensionHandler.getClassForName(this.m_handlerClass);
            Constructor<Object> con = null;
            if (this.m_sig != null) {
                con = cl.getConstructor(this.m_sig);
            } else {
                Constructor<?>[] cons = cl.getConstructors();
                for (int i = 0; i < cons.length; ++i) {
                    if (cons[i].getParameterTypes().length != this.m_args.length) continue;
                    con = cons[i];
                    break;
                }
            }
            if (con == null) {
                throw new TransformerException("ExtensionHandler constructor not found");
            }
            handler = (ExtensionHandler)con.newInstance(this.m_args);
        }
        catch (Exception e) {
            throw new TransformerException(e);
        }
        return handler;
    }
}

