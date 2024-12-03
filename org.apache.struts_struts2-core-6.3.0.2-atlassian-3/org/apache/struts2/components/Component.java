/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.BooleanUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.reflect.MethodUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.security.NotExcludedAcceptedPatternsChecker;
import com.opensymphony.xwork2.util.ValueStack;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsException;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.util.ComponentUtils;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.views.util.UrlHelper;

public class Component {
    private static final Logger LOG = LogManager.getLogger(Component.class);
    public static final String COMPONENT_STACK = "__component_stack";
    protected static ConcurrentMap<Class<?>, Collection<String>> standardAttributesMap = new ConcurrentHashMap();
    protected boolean devMode = false;
    protected boolean escapeHtmlBody = false;
    protected ValueStack stack;
    protected Map<String, Object> parameters;
    protected ActionMapper actionMapper;
    protected boolean throwExceptionOnELFailure;
    protected boolean performClearTagStateForTagPoolingServers = false;
    private UrlHelper urlHelper;
    private NotExcludedAcceptedPatternsChecker notExcludedAcceptedPatterns;

    public Component(ValueStack stack) {
        this.stack = stack;
        this.parameters = new LinkedHashMap<String, Object>();
        this.getComponentStack().push(this);
    }

    private String getComponentName() {
        Class<?> c = this.getClass();
        String name = c.getName();
        int dot = name.lastIndexOf(46);
        return name.substring(dot + 1).toLowerCase();
    }

    @Inject(value="struts.devMode", required=false)
    public void setDevMode(String devMode) {
        this.devMode = BooleanUtils.toBoolean((String)devMode);
    }

    @Inject
    public void setActionMapper(ActionMapper mapper) {
        this.actionMapper = mapper;
    }

    @Inject(value="struts.el.throwExceptionOnFailure")
    public void setThrowExceptionsOnELFailure(String throwException) {
        this.throwExceptionOnELFailure = BooleanUtils.toBoolean((String)throwException);
    }

    @Inject(value="struts.ui.escapeHtmlBody", required=false)
    public void setEscapeHtmlBody(String escapeHtmlBody) {
        this.escapeHtmlBody = BooleanUtils.toBoolean((String)escapeHtmlBody);
    }

    @Inject
    public void setUrlHelper(UrlHelper urlHelper) {
        this.urlHelper = urlHelper;
    }

    @Inject
    public void setNotExcludedAcceptedPatterns(NotExcludedAcceptedPatternsChecker notExcludedAcceptedPatterns) {
        this.notExcludedAcceptedPatterns = notExcludedAcceptedPatterns;
    }

    public ValueStack getStack() {
        return this.stack;
    }

    public Stack<Component> getComponentStack() {
        Stack componentStack = (Stack)this.stack.getContext().get(COMPONENT_STACK);
        if (componentStack == null) {
            componentStack = new Stack();
            this.stack.getContext().put(COMPONENT_STACK, componentStack);
        }
        return componentStack;
    }

    public boolean start(Writer writer) {
        return true;
    }

    public boolean end(Writer writer, String body) {
        return this.end(writer, body, true);
    }

    protected boolean end(Writer writer, String body, boolean popComponentStack) {
        assert (body != null);
        try {
            writer.write(body);
        }
        catch (IOException e) {
            throw new StrutsException("IOError while writing the body: " + e.getMessage(), e);
        }
        if (popComponentStack) {
            this.popComponentStack();
        }
        return false;
    }

    protected void popComponentStack() {
        this.getComponentStack().pop();
    }

    protected Component findAncestor(Class<?> clazz) {
        Stack<Component> componentStack = this.getComponentStack();
        int currPosition = componentStack.search(this);
        if (currPosition >= 0) {
            int start;
            for (int i = start = componentStack.size() - currPosition - 1; i >= 0; --i) {
                Component component = (Component)componentStack.get(i);
                if (!clazz.isAssignableFrom(component.getClass()) || component == this) continue;
                return component;
            }
        }
        return null;
    }

    protected String findString(String expr) {
        return (String)this.findValue(expr, String.class);
    }

    protected String findString(String expr, String field, String errorMsg) {
        if (expr == null) {
            throw this.fieldError(field, errorMsg, null);
        }
        return this.findString(expr);
    }

    protected StrutsException fieldError(String field, String errorMsg, Exception e) {
        String msg = "tag '" + this.getComponentName() + "', field '" + field + (this.parameters != null && this.parameters.containsKey("name") ? "', name '" + this.parameters.get("name") : "") + "': " + errorMsg;
        throw new StrutsException(msg, e);
    }

    protected Object findValue(String expression) {
        if (expression == null) {
            return null;
        }
        expression = this.stripExpression(expression);
        return this.getStack().findValue(expression, this.throwExceptionOnELFailure);
    }

    protected String stripExpression(String expression) {
        return ComponentUtils.stripExpression(expression);
    }

    protected String completeExpression(String expr) {
        return expr;
    }

    protected Object findValue(String expr, String field, String errorMsg) {
        if (expr == null) {
            throw this.fieldError(field, errorMsg, null);
        }
        Object value = null;
        Exception problem = null;
        try {
            value = this.findValue(expr);
        }
        catch (Exception e) {
            problem = e;
        }
        if (value == null) {
            throw this.fieldError(field, errorMsg, problem);
        }
        return value;
    }

    protected Object findValue(String expression, Class<?> toType) {
        String strippedExpression = this.stripExpression(expression);
        return this.getStack().findValue(strippedExpression, toType, this.throwExceptionOnELFailure);
    }

    protected String determineActionURL(String action, String namespace, String method, HttpServletRequest req, HttpServletResponse res, Map<String, Object> parameters, String scheme, boolean includeContext, boolean encodeResult, boolean forceAddSchemeHostAndPort, boolean escapeAmp) {
        String finalAction = this.findString(action);
        String finalMethod = method != null ? this.findString(method) : null;
        String finalNamespace = this.determineNamespace(namespace, this.getStack(), req);
        ActionMapping mapping = new ActionMapping(finalAction, finalNamespace, finalMethod, parameters);
        String uri = this.actionMapper.getUriFromActionMapping(mapping);
        return this.urlHelper.buildUrl(uri, req, res, parameters, scheme, includeContext, encodeResult, forceAddSchemeHostAndPort, escapeAmp);
    }

    protected String determineNamespace(String namespace, ValueStack stack, HttpServletRequest req) {
        String result = namespace == null ? this.getNamespace(stack) : this.findString(namespace);
        if (result == null) {
            result = "";
        }
        return result;
    }

    protected String getNamespace(ValueStack stack) {
        ActionContext context = ActionContext.of(stack.getContext());
        ActionInvocation invocation = context.getActionInvocation();
        return invocation.getProxy().getNamespace();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void copyParams(Map<String, Object> params) {
        this.stack.push(this.parameters);
        this.stack.push(this);
        try {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String key = entry.getKey();
                if (key.indexOf(45) >= 0) {
                    this.parameters.put(key, entry.getValue());
                    continue;
                }
                this.stack.setValue(key, entry.getValue());
            }
        }
        finally {
            this.stack.pop();
            this.stack.pop();
        }
    }

    /*
     * Exception decompiling
     */
    protected String toString(Throwable t) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    public Map<String, Object> getParameters() {
        return this.parameters;
    }

    public void addAllParameters(Map<String, Object> params) {
        this.parameters.putAll(params);
    }

    public void addParameter(String key, Object value) {
        if (key != null) {
            Map<String, Object> params = this.getParameters();
            if (value == null) {
                params.remove(key);
            } else {
                params.put(key, value);
            }
        }
    }

    public boolean usesBody() {
        return false;
    }

    public boolean escapeHtmlBody() {
        return this.escapeHtmlBody;
    }

    public boolean isValidTagAttribute(String attrName) {
        return this.getStandardAttributes().contains(attrName);
    }

    protected Collection<String> getStandardAttributes() {
        Class<?> clz = this.getClass();
        HashSet<String> standardAttributes = (HashSet<String>)standardAttributesMap.get(clz);
        if (standardAttributes == null) {
            List methods = MethodUtils.getMethodsListWithAnnotation(clz, StrutsTagAttribute.class, (boolean)true, (boolean)true);
            standardAttributes = new HashSet<String>(methods.size());
            for (Method m : methods) {
                standardAttributes.add(StringUtils.uncapitalize((String)m.getName().substring(3)));
            }
            standardAttributesMap.putIfAbsent(clz, standardAttributes);
        }
        return standardAttributes;
    }

    @StrutsTagAttribute(description="Whether to clear all tag state during doEndTag() processing (if applicable)", type="Boolean", defaultValue="false")
    public void setPerformClearTagStateForTagPoolingServers(boolean performClearTagStateForTagPoolingServers) {
        this.performClearTagStateForTagPoolingServers = performClearTagStateForTagPoolingServers;
    }

    public boolean getPerformClearTagStateForTagPoolingServers() {
        return this.performClearTagStateForTagPoolingServers;
    }

    protected boolean isAcceptableExpression(String expression) {
        NotExcludedAcceptedPatternsChecker.IsAllowed isAllowed = this.notExcludedAcceptedPatterns.isAllowed(expression);
        if (isAllowed.isAllowed()) {
            return true;
        }
        LOG.warn("Expression [{}] isn't allowed by pattern [{}]! See Accepted / Excluded patterns at\nhttps://struts.apache.org/security/", (Object)expression, (Object)isAllowed.getAllowedPattern());
        return false;
    }
}

