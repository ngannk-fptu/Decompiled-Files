/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpSession
 *  org.apache.struts.tiles.AttributeDefinition
 *  org.apache.struts.tiles.ComponentContext
 *  org.apache.struts.tiles.ComponentDefinition
 *  org.apache.struts.tiles.Controller
 *  org.apache.struts.tiles.DefinitionAttribute
 *  org.apache.struts.tiles.DefinitionNameAttribute
 *  org.apache.struts.tiles.DefinitionsFactoryException
 *  org.apache.struts.tiles.DirectStringAttribute
 *  org.apache.struts.tiles.TilesUtil
 *  org.apache.velocity.context.Context
 */
package org.apache.velocity.tools.struts;

import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts.tiles.AttributeDefinition;
import org.apache.struts.tiles.ComponentContext;
import org.apache.struts.tiles.ComponentDefinition;
import org.apache.struts.tiles.Controller;
import org.apache.struts.tiles.DefinitionAttribute;
import org.apache.struts.tiles.DefinitionNameAttribute;
import org.apache.struts.tiles.DefinitionsFactoryException;
import org.apache.struts.tiles.DirectStringAttribute;
import org.apache.struts.tiles.TilesUtil;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.ValidScope;
import org.apache.velocity.tools.view.ImportSupport;
import org.apache.velocity.tools.view.ViewContext;

@DefaultKey(value="tiles")
@ValidScope(value={"request"})
public class TilesTool
extends ImportSupport {
    static final String PAGE_SCOPE = "page";
    static final String REQUEST_SCOPE = "request";
    static final String SESSION_SCOPE = "session";
    static final String APPLICATION_SCOPE = "application";
    protected Context velocityContext;
    protected Stack contextStack;
    protected boolean catchExceptions = true;

    @Deprecated
    public void init(Object obj) {
        if (obj instanceof ViewContext) {
            ViewContext ctx = (ViewContext)obj;
            this.setVelocityContext(ctx.getVelocityContext());
            this.setRequest(ctx.getRequest());
            this.setResponse(ctx.getResponse());
            this.setServletContext(ctx.getServletContext());
            this.setLog(ctx.getVelocityEngine().getLog());
        }
    }

    public void setVelocityContext(Context context) {
        if (context == null) {
            throw new NullPointerException("velocity context should not be null");
        }
        this.velocityContext = context;
    }

    public void setCatchExceptions(boolean catchExceptions) {
        this.catchExceptions = catchExceptions;
    }

    public String get(Object obj) throws Exception {
        try {
            Object value = this.getCurrentContext().getAttribute(obj.toString());
            if (value != null) {
                return this.processObjectValue(value);
            }
            return this.processAsDefinitionOrURL(obj.toString());
        }
        catch (Exception e) {
            this.LOG.error((Object)("TilesTool : Exeption while rendering Tile " + obj), (Throwable)e);
            if (!this.catchExceptions) {
                throw e;
            }
            return null;
        }
    }

    public Object getAttribute(String name) {
        Object value = this.getCurrentContext().getAttribute(name);
        if (value == null) {
            this.LOG.warn((Object)("TilesTool : Tile attribute '" + name + "' wasn't found in context."));
        }
        return value;
    }

    public void importAttribute(String name) {
        this.importAttribute(name, PAGE_SCOPE);
    }

    public void importAttribute(String name, String scope) {
        Object value = this.getCurrentContext().getAttribute(name);
        if (value == null) {
            this.LOG.warn((Object)("TilesTool : Tile attribute '" + name + "' wasn't found in context."));
        }
        if (scope.equals(PAGE_SCOPE)) {
            this.velocityContext.put(name, value);
        } else if (scope.equals(REQUEST_SCOPE)) {
            this.request.setAttribute(name, value);
        } else if (scope.equals(SESSION_SCOPE)) {
            this.request.getSession().setAttribute(name, value);
        } else if (scope.equals(APPLICATION_SCOPE)) {
            this.application.setAttribute(name, value);
        }
    }

    public void importAttributes() {
        this.importAttributes(PAGE_SCOPE);
    }

    public void importAttributes(String scope) {
        block5: {
            Iterator names;
            ComponentContext context;
            block7: {
                block6: {
                    block4: {
                        context = this.getCurrentContext();
                        names = context.getAttributeNames();
                        if (!scope.equals(PAGE_SCOPE)) break block4;
                        while (names.hasNext()) {
                            String name = (String)names.next();
                            this.velocityContext.put(name, context.getAttribute(name));
                        }
                        break block5;
                    }
                    if (!scope.equals(REQUEST_SCOPE)) break block6;
                    while (names.hasNext()) {
                        String name = (String)names.next();
                        this.request.setAttribute(name, context.getAttribute(name));
                    }
                    break block5;
                }
                if (!scope.equals(SESSION_SCOPE)) break block7;
                HttpSession session = this.request.getSession();
                while (names.hasNext()) {
                    String name = (String)names.next();
                    session.setAttribute(name, context.getAttribute(name));
                }
                break block5;
            }
            if (!scope.equals(APPLICATION_SCOPE)) break block5;
            while (names.hasNext()) {
                String name = (String)names.next();
                this.application.setAttribute(name, context.getAttribute(name));
            }
        }
    }

    protected String processObjectValue(Object value) throws Exception {
        if (value instanceof AttributeDefinition) {
            return this.processTypedAttribute((AttributeDefinition)value);
        }
        if (value instanceof ComponentDefinition) {
            return this.processDefinition((ComponentDefinition)value);
        }
        return this.processAsDefinitionOrURL(value.toString());
    }

    protected String processTypedAttribute(AttributeDefinition value) throws Exception {
        if (value instanceof DirectStringAttribute) {
            return (String)value.getValue();
        }
        if (value instanceof DefinitionAttribute) {
            return this.processDefinition((ComponentDefinition)value.getValue());
        }
        if (value instanceof DefinitionNameAttribute) {
            return this.processAsDefinitionOrURL((String)value.getValue());
        }
        return this.doInsert((String)value.getValue(), null, null);
    }

    protected String processAsDefinitionOrURL(String name) throws Exception {
        try {
            ComponentDefinition definition = TilesUtil.getDefinition((String)name, (ServletRequest)this.request, (ServletContext)this.application);
            if (definition != null) {
                return this.processDefinition(definition);
            }
        }
        catch (DefinitionsFactoryException definitionsFactoryException) {
            // empty catch block
        }
        return this.processUrl(name);
    }

    protected String processDefinition(ComponentDefinition definition) throws Exception {
        Controller controller = null;
        try {
            controller = definition.getOrCreateController();
            String role = definition.getRole();
            String page = definition.getTemplate();
            return this.doInsert(definition.getAttributes(), page, role, controller);
        }
        catch (InstantiationException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    protected String processUrl(String url) throws Exception {
        return this.doInsert(url, null, null);
    }

    protected String doInsert(String page, String role, Controller controller) throws Exception {
        if (role != null && !this.request.isUserInRole(role)) {
            return null;
        }
        ComponentContext subCompContext = new ComponentContext();
        return this.doInsert(subCompContext, page, role, controller);
    }

    protected String doInsert(Map attributes, String page, String role, Controller controller) throws Exception {
        if (role != null && !this.request.isUserInRole(role)) {
            return null;
        }
        ComponentContext subCompContext = new ComponentContext(attributes);
        return this.doInsert(subCompContext, page, role, controller);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected String doInsert(ComponentContext subCompContext, String page, String role, Controller controller) throws Exception {
        this.pushTilesContext();
        try {
            ComponentContext.setContext((ComponentContext)subCompContext, (ServletRequest)this.request);
            if (controller != null) {
                controller.execute(subCompContext, this.request, this.response, this.application);
            }
            String string = this.acquireString(page);
            return string;
        }
        finally {
            this.popTilesContext();
        }
    }

    protected ComponentContext getCurrentContext() {
        return ComponentContext.getContext((ServletRequest)this.request);
    }

    protected void pushTilesContext() {
        if (this.contextStack == null) {
            this.contextStack = new Stack();
        }
        this.contextStack.push(this.getCurrentContext());
    }

    protected void popTilesContext() {
        ComponentContext context = (ComponentContext)this.contextStack.pop();
        ComponentContext.setContext((ComponentContext)context, (ServletRequest)this.request);
    }
}

