/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.inject.Container
 *  com.opensymphony.xwork2.util.ValueStack
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.struts2.components.Component
 *  org.apache.struts2.util.ValueStackProvider
 *  org.apache.velocity.context.AbstractContext
 *  org.apache.velocity.context.Context
 *  org.apache.velocity.context.InternalContextAdapter
 *  org.apache.velocity.context.InternalWrapperContext
 *  org.apache.velocity.exception.MethodInvocationException
 *  org.apache.velocity.exception.ParseErrorException
 *  org.apache.velocity.exception.ResourceNotFoundException
 *  org.apache.velocity.runtime.directive.Directive
 *  org.apache.velocity.runtime.parser.node.ASTReference
 *  org.apache.velocity.runtime.parser.node.ASTStringLiteral
 *  org.apache.velocity.runtime.parser.node.Node
 *  org.apache.velocity.runtime.parser.node.SimpleNode
 */
package org.apache.struts2.views.velocity.components;

import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.ValueStack;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.components.Component;
import org.apache.struts2.util.ValueStackProvider;
import org.apache.velocity.context.AbstractContext;
import org.apache.velocity.context.Context;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.context.InternalWrapperContext;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.ASTReference;
import org.apache.velocity.runtime.parser.node.ASTStringLiteral;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;

public abstract class AbstractDirective
extends Directive {
    private static final Logger LOG = LogManager.getLogger(AbstractDirective.class);

    public String getName() {
        return "s" + this.getBeanName();
    }

    public abstract String getBeanName();

    public int getType() {
        return 2;
    }

    protected abstract Component getBean(ValueStack var1, HttpServletRequest var2, HttpServletResponse var3);

    public boolean render(InternalContextAdapter ctx, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        ValueStack stack = this.extractValueStack((Context)ctx);
        if (stack == null) {
            stack = (ValueStack)ctx.get("stack");
        }
        HttpServletRequest req = (HttpServletRequest)stack.getContext().get("com.opensymphony.xwork2.dispatcher.HttpServletRequest");
        HttpServletResponse res = (HttpServletResponse)stack.getContext().get("com.opensymphony.xwork2.dispatcher.HttpServletResponse");
        Component bean = this.getBean(stack, req, res);
        Container container = stack.getActionContext().getContainer();
        container.inject((Object)bean);
        Map params = this.createPropertyMap(ctx, node);
        bean.copyParams(params);
        bean.start(writer);
        if (this.getType() == 1) {
            Node body = node.jjtGetChild(node.jjtGetNumChildren() - 1);
            body.render(ctx, writer);
        }
        bean.end(writer, "");
        return true;
    }

    private ValueStack extractValueStack(Context context) {
        do {
            if (!(context instanceof ValueStackProvider)) continue;
            return ((ValueStackProvider)context).getValueStack();
        } while ((context = this.extractContext(context)) != null);
        return null;
    }

    private Context extractContext(Context context) {
        if (context instanceof InternalWrapperContext) {
            return ((InternalWrapperContext)context).getInternalUserContext();
        }
        if (context instanceof AbstractContext) {
            return ((AbstractContext)context).getChainedContext();
        }
        return null;
    }

    protected Map createPropertyMap(InternalContextAdapter contextAdapter, Node node) throws ParseErrorException, MethodInvocationException {
        Map propertyMap;
        int children = node.jjtGetNumChildren();
        if (this.getType() == 1) {
            --children;
        }
        Node firstChild = null;
        Object firstValue = null;
        if (children == 1 && null != (firstChild = node.jjtGetChild(0)) && null != (firstValue = firstChild.value(contextAdapter)) && firstValue instanceof Map) {
            propertyMap = (Map)firstValue;
        } else {
            propertyMap = new HashMap();
            int length = children;
            for (int index = 0; index < length; ++index) {
                this.putProperty(propertyMap, contextAdapter, node.jjtGetChild(index));
            }
        }
        return propertyMap;
    }

    protected void putProperty(Map propertyMap, InternalContextAdapter contextAdapter, Node node) throws ParseErrorException, MethodInvocationException {
        if (this.putPropertyWithType(propertyMap, contextAdapter, node)) {
            return;
        }
        LOG.debug("Property value type preservation failed, falling back to default string resolution behaviour.");
        String param = node.value(contextAdapter).toString();
        int idx = param.indexOf(61);
        if (idx == -1) {
            throw new ParseErrorException("#" + this.getName() + " arguments must include an assignment operator!  For example #tag( Component \"template=mytemplate\" ).  #tag( TextField \"mytemplate\" ) is illegal!");
        }
        String property = param.substring(0, idx);
        String value = param.substring(idx + 1);
        propertyMap.put(property, value);
    }

    private boolean putPropertyWithType(Map propertyMap, InternalContextAdapter contextAdapter, Node node) {
        String param = node.value(contextAdapter).toString();
        int idx = param.indexOf(61);
        if (idx == -1 || !(node instanceof ASTStringLiteral)) {
            return false;
        }
        try {
            String property = param.substring(0, idx);
            SimpleNode nodeTree = (SimpleNode)this.reflectField(node, "nodeTree");
            if (nodeTree != null && nodeTree.jjtGetNumChildren() == 3 && nodeTree.jjtGetChild(1) instanceof ASTReference && StringUtils.isBlank((CharSequence)nodeTree.jjtGetChild(2).literal())) {
                ASTReference ref = (ASTReference)nodeTree.jjtGetChild(1);
                Object resolvedVar = ref.value(contextAdapter);
                if (this.reflectField(ref, "nullString").equals(resolvedVar)) {
                    resolvedVar = null;
                }
                String firstChild = nodeTree.jjtGetChild(0).literal();
                char lastChar = firstChild.charAt(firstChild.length() - 1);
                char secondLastChar = firstChild.charAt(firstChild.length() - 2);
                if (lastChar == '=') {
                    propertyMap.put(property, resolvedVar);
                    return true;
                }
                if (secondLastChar == '=' && lastChar == '!') {
                    resolvedVar = Boolean.FALSE.equals(resolvedVar);
                    propertyMap.put(property, resolvedVar);
                    return true;
                }
                LOG.debug("Tag attribute type unable to be preserved due to unsupported operand and/or string manipulation : {}", (Object)param);
            } else if (nodeTree == null && ("'false'".equalsIgnoreCase(param.substring(idx + 1)) || "false".equalsIgnoreCase(param.substring(idx + 1)))) {
                propertyMap.put(property, false);
                return true;
            }
        }
        catch (ClassCastException | IllegalAccessException | NoSuchFieldException e) {
            LOG.debug(MessageFormat.format("Exception preserving tag attribute type : {0}", param), (Throwable)e);
        }
        return false;
    }

    private <T> T reflectField(Object instance, String fieldName) throws NoSuchFieldException, IllegalAccessException, ClassCastException {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T)field.get(instance);
    }
}

