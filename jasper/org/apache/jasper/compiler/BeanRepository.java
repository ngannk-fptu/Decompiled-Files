/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jasper.compiler;

import java.util.HashMap;
import org.apache.jasper.JasperException;
import org.apache.jasper.compiler.ErrorDispatcher;
import org.apache.jasper.compiler.Node;

public class BeanRepository {
    private final HashMap<String, String> beanTypes;
    private final ClassLoader loader;
    private final ErrorDispatcher errDispatcher;

    public BeanRepository(ClassLoader loader, ErrorDispatcher err) {
        this.loader = loader;
        this.errDispatcher = err;
        this.beanTypes = new HashMap();
    }

    public void addBean(Node.UseBean n, String s, String type, String scope) throws JasperException {
        if (!(scope == null || scope.equals("page") || scope.equals("request") || scope.equals("session") || scope.equals("application"))) {
            this.errDispatcher.jspError((Node)n, "jsp.error.usebean.badScope", new String[0]);
        }
        this.beanTypes.put(s, type);
    }

    public Class<?> getBeanType(String bean) throws JasperException {
        Class<?> clazz = null;
        try {
            clazz = this.loader.loadClass(this.beanTypes.get(bean));
        }
        catch (ClassNotFoundException ex) {
            throw new JasperException(ex);
        }
        return clazz;
    }

    public boolean checkVariable(String bean) {
        return this.beanTypes.containsKey(bean);
    }
}

