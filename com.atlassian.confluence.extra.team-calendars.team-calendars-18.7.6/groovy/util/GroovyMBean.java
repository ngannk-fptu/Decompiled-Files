/*
 * Decompiled with CFR 0.152.
 */
package groovy.util;

import groovy.lang.GroovyObjectSupport;
import groovy.lang.GroovyRuntimeException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.management.Attribute;
import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

public class GroovyMBean
extends GroovyObjectSupport {
    private final MBeanServerConnection server;
    private final ObjectName name;
    private MBeanInfo beanInfo;
    private final boolean ignoreErrors;
    private final Map<String, String[]> operations = new HashMap<String, String[]>();

    public GroovyMBean(MBeanServerConnection server, String objectName) throws JMException, IOException {
        this(server, objectName, false);
    }

    public GroovyMBean(MBeanServerConnection server, String objectName, boolean ignoreErrors) throws JMException, IOException {
        this(server, new ObjectName(objectName), ignoreErrors);
    }

    public GroovyMBean(MBeanServerConnection server, ObjectName name) throws JMException, IOException {
        this(server, name, false);
    }

    public GroovyMBean(MBeanServerConnection server, ObjectName name, boolean ignoreErrors) throws JMException, IOException {
        MBeanOperationInfo[] operationInfos;
        this.server = server;
        this.name = name;
        this.ignoreErrors = ignoreErrors;
        this.beanInfo = server.getMBeanInfo(name);
        for (MBeanOperationInfo info : operationInfos = this.beanInfo.getOperations()) {
            String[] signature = this.createSignature(info);
            String operationKey = this.createOperationKey(info.getName(), signature.length);
            this.operations.put(operationKey, signature);
        }
    }

    public MBeanServerConnection server() {
        return this.server;
    }

    public ObjectName name() {
        return this.name;
    }

    public MBeanInfo info() {
        return this.beanInfo;
    }

    @Override
    public Object getProperty(String property) {
        block3: {
            try {
                return this.server.getAttribute(this.name, property);
            }
            catch (MBeanException e) {
                this.throwExceptionWithTarget("Could not access property: " + property + ". Reason: ", e);
            }
            catch (Exception e) {
                if (this.ignoreErrors) break block3;
                this.throwException("Could not access property: " + property + ". Reason: ", e);
            }
        }
        return null;
    }

    @Override
    public void setProperty(String property, Object value) {
        try {
            this.server.setAttribute(this.name, new Attribute(property, value));
        }
        catch (MBeanException e) {
            this.throwExceptionWithTarget("Could not set property: " + property + ". Reason: ", e);
        }
        catch (Exception e) {
            this.throwException("Could not set property: " + property + ". Reason: ", e);
        }
    }

    @Override
    public Object invokeMethod(String method, Object arguments) {
        Object[] argArray = arguments instanceof Object[] ? (Object[])arguments : new Object[]{arguments};
        String operationKey = this.createOperationKey(method, argArray.length);
        String[] signature = this.operations.get(operationKey);
        if (signature != null) {
            try {
                return this.server.invoke(this.name, method, argArray, signature);
            }
            catch (MBeanException e) {
                this.throwExceptionWithTarget("Could not invoke method: " + method + ". Reason: ", e);
            }
            catch (Exception e) {
                this.throwException("Could not invoke method: " + method + ". Reason: ", e);
            }
            return null;
        }
        return super.invokeMethod(method, arguments);
    }

    protected String[] createSignature(MBeanOperationInfo info) {
        MBeanParameterInfo[] params = info.getSignature();
        String[] answer = new String[params.length];
        for (int i = 0; i < params.length; ++i) {
            answer[i] = params[i].getType();
        }
        return answer;
    }

    protected String createOperationKey(String operation, int params) {
        return operation + "_" + params;
    }

    public Collection<String> listAttributeNames() {
        ArrayList<String> list = new ArrayList<String>();
        try {
            MBeanAttributeInfo[] attrs;
            for (MBeanAttributeInfo attr : attrs = this.beanInfo.getAttributes()) {
                list.add(attr.getName());
            }
        }
        catch (Exception e) {
            this.throwException("Could not list attribute names. Reason: ", e);
        }
        return list;
    }

    public List<String> listAttributeValues() {
        ArrayList<String> list = new ArrayList<String>();
        Collection<String> names = this.listAttributeNames();
        for (String name : names) {
            try {
                Object val = this.getProperty(name);
                if (val == null) continue;
                list.add(name + " : " + val.toString());
            }
            catch (Exception e) {
                this.throwException("Could not list attribute values. Reason: ", e);
            }
        }
        return list;
    }

    public Collection<String> listAttributeDescriptions() {
        ArrayList<String> list = new ArrayList<String>();
        try {
            MBeanAttributeInfo[] attrs;
            for (MBeanAttributeInfo attr : attrs = this.beanInfo.getAttributes()) {
                list.add(this.describeAttribute(attr));
            }
        }
        catch (Exception e) {
            this.throwException("Could not list attribute descriptions. Reason: ", e);
        }
        return list;
    }

    protected String describeAttribute(MBeanAttributeInfo attr) {
        StringBuilder buf = new StringBuilder();
        buf.append("(");
        if (attr.isReadable()) {
            buf.append("r");
        }
        if (attr.isWritable()) {
            buf.append("w");
        }
        buf.append(") ").append(attr.getType()).append(" ").append(attr.getName());
        return buf.toString();
    }

    public String describeAttribute(String attributeName) {
        String ret = "Attribute not found";
        try {
            MBeanAttributeInfo[] attributes;
            for (MBeanAttributeInfo attribute : attributes = this.beanInfo.getAttributes()) {
                if (!attribute.getName().equals(attributeName)) continue;
                return this.describeAttribute(attribute);
            }
        }
        catch (Exception e) {
            this.throwException("Could not describe attribute '" + attributeName + "'. Reason: ", e);
        }
        return ret;
    }

    public Collection<String> listOperationNames() {
        ArrayList<String> list = new ArrayList<String>();
        try {
            MBeanOperationInfo[] operations;
            for (MBeanOperationInfo operation : operations = this.beanInfo.getOperations()) {
                list.add(operation.getName());
            }
        }
        catch (Exception e) {
            this.throwException("Could not list operation names. Reason: ", e);
        }
        return list;
    }

    public Collection<String> listOperationDescriptions() {
        ArrayList<String> list = new ArrayList<String>();
        try {
            MBeanOperationInfo[] operations;
            for (MBeanOperationInfo operation : operations = this.beanInfo.getOperations()) {
                list.add(this.describeOperation(operation));
            }
        }
        catch (Exception e) {
            this.throwException("Could not list operation descriptions. Reason: ", e);
        }
        return list;
    }

    public List<String> describeOperation(String operationName) {
        ArrayList<String> list = new ArrayList<String>();
        try {
            MBeanOperationInfo[] operations;
            for (MBeanOperationInfo operation : operations = this.beanInfo.getOperations()) {
                if (!operation.getName().equals(operationName)) continue;
                list.add(this.describeOperation(operation));
            }
        }
        catch (Exception e) {
            this.throwException("Could not describe operations matching name '" + operationName + "'. Reason: ", e);
        }
        return list;
    }

    protected String describeOperation(MBeanOperationInfo operation) {
        StringBuilder buf = new StringBuilder();
        buf.append(operation.getReturnType()).append(" ").append(operation.getName()).append("(");
        MBeanParameterInfo[] params = operation.getSignature();
        for (int j = 0; j < params.length; ++j) {
            MBeanParameterInfo param = params[j];
            if (j != 0) {
                buf.append(", ");
            }
            buf.append(param.getType()).append(" ").append(param.getName());
        }
        buf.append(")");
        return buf.toString();
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("MBean Name:").append("\n  ").append(this.name.getCanonicalName()).append("\n  ");
        if (!this.listAttributeDescriptions().isEmpty()) {
            buf.append("\nAttributes:");
            for (String attrDesc : this.listAttributeDescriptions()) {
                buf.append("\n  ").append(attrDesc);
            }
        }
        if (!this.listOperationDescriptions().isEmpty()) {
            buf.append("\nOperations:");
            for (String attrDesc : this.listOperationDescriptions()) {
                buf.append("\n  ").append(attrDesc);
            }
        }
        return buf.toString();
    }

    private void throwException(String m, Exception e) {
        if (!this.ignoreErrors) {
            throw new GroovyRuntimeException(m + e, e);
        }
    }

    private void throwExceptionWithTarget(String m, MBeanException e) {
        if (!this.ignoreErrors) {
            throw new GroovyRuntimeException(m + e, e.getTargetException());
        }
    }
}

