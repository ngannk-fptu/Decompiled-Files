/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.abdera.Abdera;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.ElementWrapper;
import org.apache.abdera.parser.stax.FOMAttribute;
import org.apache.abdera.parser.stax.util.ResolveFunction;
import org.apache.abdera.util.AbstractXPath;
import org.apache.abdera.xpath.XPathException;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.xpath.DocumentNavigator;
import org.jaxen.BaseXPath;
import org.jaxen.Function;
import org.jaxen.FunctionContext;
import org.jaxen.JaxenException;
import org.jaxen.SimpleFunctionContext;
import org.jaxen.SimpleVariableContext;
import org.jaxen.VariableContext;
import org.jaxen.XPath;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FOMXPath
extends AbstractXPath {
    private final Map<QName, Function> functions;
    private final Map<QName, Object> variables;

    public FOMXPath(Abdera abdera) {
        this(null, null, null);
    }

    protected FOMXPath(Map<String, String> defaultNamespaces) {
        this(defaultNamespaces, null, null);
    }

    protected FOMXPath(Map<String, String> defaultNamespaces, Map<QName, Function> defaultFunctions, Map<QName, Object> defaultVariables) {
        super(defaultNamespaces);
        this.functions = defaultFunctions != null ? defaultFunctions : this.initDefaultFunctions();
        this.variables = defaultVariables != null ? defaultVariables : this.initDefaultVariables();
    }

    @Override
    protected Map<String, String> initDefaultNamespaces() {
        Map<String, String> namespaces = super.initDefaultNamespaces();
        namespaces.put("abdera", "http://abdera.apache.org");
        return namespaces;
    }

    private Map<QName, Function> initDefaultFunctions() {
        HashMap<QName, Function> functions = new HashMap<QName, Function>();
        functions.put(ResolveFunction.QNAME, new ResolveFunction());
        return functions;
    }

    private Map<QName, Object> initDefaultVariables() {
        HashMap<QName, Object> variables = new HashMap<QName, Object>();
        return variables;
    }

    public static XPath getXPath(String path) throws JaxenException {
        return FOMXPath.getXPath(path, null);
    }

    private static FunctionContext getFunctionContext(Map<QName, Function> functions, SimpleFunctionContext context) {
        if (context == null) {
            context = new SimpleFunctionContext();
        }
        for (QName qname : functions.keySet()) {
            Function function = functions.get(qname);
            context.registerFunction(qname.getNamespaceURI(), qname.getLocalPart(), function);
        }
        return context;
    }

    private static VariableContext getVariableContext(Map<QName, Object> variables, SimpleVariableContext context) {
        if (context == null) {
            context = new SimpleVariableContext();
        }
        for (QName qname : variables.keySet()) {
            Object value = variables.get(qname);
            context.setVariableValue(qname.getNamespaceURI(), qname.getLocalPart(), value);
        }
        return context;
    }

    public static XPath getXPath(String path, Map<String, String> namespaces, Map<QName, Function> functions, Map<QName, Object> variables) throws JaxenException {
        DocumentNavigator nav = new DocumentNavigator();
        BaseXPath contextpath = new BaseXPath(path, nav);
        if (namespaces != null) {
            for (Map.Entry<String, String> entry : namespaces.entrySet()) {
                contextpath.addNamespace(entry.getKey(), entry.getValue());
            }
        }
        if (functions != null) {
            contextpath.setFunctionContext(FOMXPath.getFunctionContext(functions, (SimpleFunctionContext)contextpath.getFunctionContext()));
        }
        if (variables != null) {
            contextpath.setVariableContext(FOMXPath.getVariableContext(variables, (SimpleVariableContext)contextpath.getVariableContext()));
        }
        return contextpath;
    }

    public static XPath getXPath(String path, Map<String, String> namespaces) throws JaxenException {
        return FOMXPath.getXPath(path, namespaces, null, null);
    }

    public List selectNodes(String path, Base base, Map<String, String> namespaces, Map<QName, Function> functions, Map<QName, Object> variables) throws XPathException {
        try {
            base = this.getElementWrapped(base);
            ArrayList<FOMAttribute> nodes = new ArrayList<FOMAttribute>();
            XPath xpath = FOMXPath.getXPath(path, namespaces, functions, variables);
            List results = xpath.selectNodes(base);
            for (Object obj : results) {
                if (obj instanceof OMAttribute) {
                    nodes.add(new FOMAttribute((OMAttribute)obj));
                    continue;
                }
                nodes.add((FOMAttribute)obj);
            }
            return nodes;
        }
        catch (JaxenException e) {
            throw new XPathException(e);
        }
    }

    @Override
    public List selectNodes(String path, Base base, Map<String, String> namespaces) throws XPathException {
        return this.selectNodes(path, base, namespaces, this.functions, this.variables);
    }

    public Object selectSingleNode(String path, Base base, Map<String, String> namespaces, Map<QName, Function> functions, Map<QName, Object> variables) throws XPathException {
        try {
            base = this.getElementWrapped(base);
            XPath xpath = FOMXPath.getXPath(path, namespaces, functions, variables);
            Object obj = xpath.selectSingleNode(base);
            if (obj instanceof OMAttribute) {
                obj = new FOMAttribute((OMAttribute)obj);
            }
            return obj;
        }
        catch (JaxenException e) {
            throw new XPathException(e);
        }
    }

    @Override
    public Object selectSingleNode(String path, Base base, Map<String, String> namespaces) throws XPathException {
        return this.selectSingleNode(path, base, namespaces, this.functions, this.variables);
    }

    public Object evaluate(String path, Base base, Map<String, String> namespaces, Map<QName, Function> functions, Map<QName, Object> variables) throws XPathException {
        try {
            base = this.getElementWrapped(base);
            XPath xpath = FOMXPath.getXPath(path, namespaces, functions, variables);
            return xpath.evaluate(base);
        }
        catch (JaxenException e) {
            throw new XPathException(e);
        }
    }

    @Override
    public Object evaluate(String path, Base base, Map<String, String> namespaces) throws XPathException {
        return this.evaluate(path, base, namespaces, this.functions, this.variables);
    }

    public String valueOf(String path, Base base, Map<String, String> namespaces, Map<QName, Function> functions, Map<QName, Object> variables) throws XPathException {
        try {
            base = this.getElementWrapped(base);
            XPath xpath = FOMXPath.getXPath(path, namespaces, functions, variables);
            return xpath.stringValueOf(base);
        }
        catch (JaxenException e) {
            throw new XPathException(e);
        }
    }

    @Override
    public String valueOf(String path, Base base, Map<String, String> namespaces) throws XPathException {
        return this.valueOf(path, base, namespaces, this.functions, this.variables);
    }

    public boolean booleanValueOf(String path, Base base, Map<String, String> namespaces, Map<QName, Function> functions, Map<QName, Object> variables) throws XPathException {
        try {
            base = this.getElementWrapped(base);
            XPath xpath = FOMXPath.getXPath(path, namespaces, functions, variables);
            return xpath.booleanValueOf(base);
        }
        catch (JaxenException e) {
            throw new XPathException(e);
        }
    }

    @Override
    public boolean booleanValueOf(String path, Base base, Map<String, String> namespaces) throws XPathException {
        return this.booleanValueOf(path, base, namespaces, this.functions, this.variables);
    }

    public Number numericValueOf(String path, Base base, Map<String, String> namespaces, Map<QName, Function> functions, Map<QName, Object> variables) throws XPathException {
        try {
            base = this.getElementWrapped(base);
            XPath xpath = FOMXPath.getXPath(path, namespaces, functions, variables);
            return xpath.numberValueOf(base);
        }
        catch (JaxenException e) {
            throw new XPathException(e);
        }
    }

    @Override
    public Number numericValueOf(String path, Base base, Map<String, String> namespaces) throws XPathException {
        return this.numericValueOf(path, base, namespaces, this.functions, this.variables);
    }

    public Map<QName, Function> getDefaultFunctions() {
        return new HashMap<QName, Function>(this.functions);
    }

    public synchronized void setDefaultFunctions(Map<QName, Function> functions) {
        this.functions.clear();
        this.functions.putAll(functions);
    }

    public Map<QName, Object> getDefaultVariables() {
        return new HashMap<QName, Object>(this.variables);
    }

    public synchronized void setDefaultVariables(Map<QName, Object> variables) {
        this.variables.clear();
        this.variables.putAll(variables);
    }

    private Base getElementWrapped(Base base) {
        if (base instanceof ElementWrapper) {
            base = ((ElementWrapper)base).getInternal();
        }
        return base;
    }
}

