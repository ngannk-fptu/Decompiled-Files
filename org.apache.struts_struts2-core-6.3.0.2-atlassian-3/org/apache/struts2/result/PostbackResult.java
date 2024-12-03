/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.struts2.result;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.result.StrutsResultSupport;

public class PostbackResult
extends StrutsResultSupport {
    private static final long serialVersionUID = -2283504349296877429L;
    private String actionName;
    private String namespace;
    private String method;
    private boolean prependServletContext = true;
    private boolean cache = true;
    protected ActionMapper actionMapper;

    @Override
    protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
        ActionContext ctx = invocation.getInvocationContext();
        HttpServletRequest request = ctx.getServletRequest();
        HttpServletResponse response = ctx.getServletResponse();
        if (!this.cache) {
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0L);
        }
        response.setContentType("text/html");
        PrintWriter pw = new PrintWriter((OutputStream)response.getOutputStream());
        pw.write("<!DOCTYPE html><html><body><form action=\"" + finalLocation + "\" method=\"POST\">");
        this.writeFormElements(request, pw);
        this.writePrologueScript(pw);
        pw.write("</html>");
        pw.flush();
    }

    @Override
    public void execute(ActionInvocation invocation) throws Exception {
        if (invocation == null) {
            throw new IllegalArgumentException("Invocation cannot be null!");
        }
        String postbackUri = this.makePostbackUri(invocation);
        this.setLocation(postbackUri);
        super.execute(invocation);
    }

    protected boolean isElementIncluded(String name, String[] values) {
        return !name.startsWith("action:");
    }

    protected String makePostbackUri(ActionInvocation invocation) {
        String postbackUri;
        ActionContext ctx = invocation.getInvocationContext();
        HttpServletRequest request = ctx.getServletRequest();
        if (this.actionName != null) {
            this.actionName = this.conditionalParse(this.actionName, invocation);
            this.parseLocation = false;
            this.namespace = this.namespace == null ? invocation.getProxy().getNamespace() : this.conditionalParse(this.namespace, invocation);
            this.method = this.method == null ? "" : this.conditionalParse(this.method, invocation);
            postbackUri = request.getContextPath() + this.actionMapper.getUriFromActionMapping(new ActionMapping(this.actionName, this.namespace, this.method, null));
        } else {
            String location = this.getLocation();
            if (!location.matches("^([a-zA-Z]+:)?//.*") && this.prependServletContext && request.getContextPath() != null && request.getContextPath().length() > 0) {
                location = request.getContextPath() + location;
            }
            postbackUri = location;
        }
        return postbackUri;
    }

    @Inject
    public final void setActionMapper(ActionMapper mapper) {
        this.actionMapper = mapper;
    }

    public final void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public final void setCache(boolean cache) {
        this.cache = cache;
    }

    public final void setMethod(String method) {
        this.method = method;
    }

    public final void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public final void setPrependServletContext(boolean prependServletContext) {
        this.prependServletContext = prependServletContext;
    }

    protected void writeFormElement(PrintWriter pw, String name, String[] values) throws UnsupportedEncodingException {
        for (String value : values) {
            String encName = URLEncoder.encode(name, "UTF-8");
            String encValue = URLEncoder.encode(value, "UTF-8");
            pw.write("<input type=\"hidden\" name=\"" + encName + "\" value=\"" + encValue + "\"/>");
        }
    }

    private void writeFormElements(HttpServletRequest request, PrintWriter pw) throws UnsupportedEncodingException {
        Map params = request.getParameterMap();
        for (Map.Entry entry : params.entrySet()) {
            String[] values;
            String name = (String)entry.getKey();
            if (!this.isElementIncluded(name, values = (String[])entry.getValue())) continue;
            this.writeFormElement(pw, name, values);
        }
    }

    protected void writePrologueScript(PrintWriter pw) {
        pw.write("<script>");
        pw.write("setTimeout(function(){document.forms[0].submit();},0);");
        pw.write("</script>");
    }
}

