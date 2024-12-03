/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.tomcat.util.modeler.Registry
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.manager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import javax.management.Attribute;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.OperationsException;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.mbeans.MBeanDumper;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;

public class JMXProxyServlet
extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String[] NO_PARAMETERS = new String[0];
    private static final StringManager sm = StringManager.getManager(JMXProxyServlet.class);
    protected transient MBeanServer mBeanServer = null;
    protected transient Registry registry;

    public void init() throws ServletException {
        this.registry = Registry.getRegistry(null, null);
        this.mBeanServer = Registry.getRegistry(null, null).getMBeanServer();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/plain;charset=utf-8");
        response.setHeader("X-Content-Type-Options", "nosniff");
        PrintWriter writer = response.getWriter();
        if (this.mBeanServer == null) {
            writer.println("Error - No mbean server");
            return;
        }
        String qry = request.getParameter("set");
        if (qry != null) {
            String name = request.getParameter("att");
            String val = request.getParameter("val");
            this.setAttribute(writer, qry, name, val);
            return;
        }
        qry = request.getParameter("get");
        if (qry != null) {
            String name = request.getParameter("att");
            this.getAttribute(writer, qry, name, request.getParameter("key"));
            return;
        }
        qry = request.getParameter("invoke");
        if (qry != null) {
            String opName = request.getParameter("op");
            String[] params = this.getInvokeParameters(request.getParameter("ps"));
            this.invokeOperation(writer, qry, opName, params);
            return;
        }
        qry = request.getParameter("qry");
        if (qry == null) {
            qry = "*:*";
        }
        this.listBeans(writer, qry);
    }

    public void getAttribute(PrintWriter writer, String onameStr, String att, String key) {
        try {
            ObjectName oname = new ObjectName(onameStr);
            Object value = this.mBeanServer.getAttribute(oname, att);
            if (null != key && value instanceof CompositeData) {
                value = ((CompositeData)value).get(key);
            }
            String valueStr = value != null ? value.toString() : "<null>";
            writer.print("OK - Attribute get '");
            writer.print(onameStr);
            writer.print("' - ");
            writer.print(att);
            if (null != key) {
                writer.print(" - key '");
                writer.print(key);
                writer.print("'");
            }
            writer.print(" = ");
            writer.println(MBeanDumper.escape(valueStr));
        }
        catch (Exception ex) {
            writer.println("Error - " + ex.toString());
            ex.printStackTrace(writer);
        }
    }

    public void setAttribute(PrintWriter writer, String onameStr, String att, String val) {
        try {
            this.setAttributeInternal(onameStr, att, val);
            writer.println("OK - Attribute set");
        }
        catch (Exception ex) {
            writer.println("Error - " + ex.toString());
            ex.printStackTrace(writer);
        }
    }

    public void listBeans(PrintWriter writer, String qry) {
        Set<ObjectName> names = null;
        try {
            names = this.mBeanServer.queryNames(new ObjectName(qry), null);
            writer.println("OK - Number of results: " + names.size());
            writer.println();
        }
        catch (Exception ex) {
            writer.println("Error - " + ex.toString());
            ex.printStackTrace(writer);
            return;
        }
        String dump = MBeanDumper.dumpBeans(this.mBeanServer, names);
        writer.print(dump);
    }

    public boolean isSupported(String type) {
        return true;
    }

    private void invokeOperation(PrintWriter writer, String onameStr, String op, String[] valuesStr) {
        try {
            Object retVal = this.invokeOperationInternal(onameStr, op, valuesStr);
            if (retVal != null) {
                writer.println("OK - Operation " + op + " returned:");
                this.output("", writer, retVal);
            } else {
                writer.println("OK - Operation " + op + " without return value");
            }
        }
        catch (Exception ex) {
            writer.println("Error - " + ex.toString());
            ex.printStackTrace(writer);
        }
    }

    private String[] getInvokeParameters(String paramString) {
        if (paramString == null) {
            return NO_PARAMETERS;
        }
        return paramString.split(",");
    }

    private void setAttributeInternal(String onameStr, String attributeName, String value) throws OperationsException, MBeanException, ReflectionException {
        ObjectName oname = new ObjectName(onameStr);
        String type = this.registry.getType(oname, attributeName);
        Object valueObj = this.registry.convertValue(type, value);
        this.mBeanServer.setAttribute(oname, new Attribute(attributeName, valueObj));
    }

    private Object invokeOperationInternal(String onameStr, String operation, String[] parameters) throws OperationsException, MBeanException, ReflectionException {
        ObjectName oname = new ObjectName(onameStr);
        int paramCount = null == parameters ? 0 : parameters.length;
        MBeanOperationInfo methodInfo = this.registry.getMethodInfo(oname, operation, paramCount);
        if (null == methodInfo) {
            MBeanInfo info = null;
            try {
                info = this.registry.getMBeanServer().getMBeanInfo(oname);
            }
            catch (InstanceNotFoundException infe) {
                throw infe;
            }
            catch (Exception e) {
                throw new IllegalArgumentException(sm.getString("jmxProxyServlet.noBeanFound", new Object[]{onameStr}), e);
            }
            throw new IllegalArgumentException(sm.getString("jmxProxyServlet.noOperationOnBean", new Object[]{operation, paramCount, onameStr, info.getClassName()}));
        }
        MBeanParameterInfo[] signature = methodInfo.getSignature();
        String[] signatureTypes = new String[signature.length];
        Object[] values = new Object[signature.length];
        for (int i = 0; i < signature.length; ++i) {
            MBeanParameterInfo pi = signature[i];
            signatureTypes[i] = pi.getType();
            values[i] = this.registry.convertValue(pi.getType(), parameters[i]);
        }
        return this.mBeanServer.invoke(oname, operation, values, signatureTypes);
    }

    private void output(String indent, PrintWriter writer, Object result) {
        if (result instanceof Object[]) {
            for (Object obj : (Object[])result) {
                this.output("  " + indent, writer, obj);
            }
        } else {
            String strValue = result != null ? result.toString() : "<null>";
            writer.println(indent + strValue);
        }
    }
}

