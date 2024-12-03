/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.jsp.JspException
 */
package org.springframework.web.servlet.tags.form;

import java.nio.charset.UnsupportedCharsetException;
import java.util.Map;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import org.springframework.core.Conventions;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.RequestDataValueProcessor;
import org.springframework.web.servlet.tags.form.AbstractFormTag;
import org.springframework.web.servlet.tags.form.AbstractHtmlElementTag;
import org.springframework.web.servlet.tags.form.TagWriter;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.UriUtils;

public class FormTag
extends AbstractHtmlElementTag {
    private static final String DEFAULT_METHOD = "post";
    public static final String DEFAULT_COMMAND_NAME = "command";
    private static final String MODEL_ATTRIBUTE = "modelAttribute";
    public static final String MODEL_ATTRIBUTE_VARIABLE_NAME = Conventions.getQualifiedAttributeName(AbstractFormTag.class, "modelAttribute");
    private static final String DEFAULT_METHOD_PARAM = "_method";
    private static final String FORM_TAG = "form";
    private static final String INPUT_TAG = "input";
    private static final String ACTION_ATTRIBUTE = "action";
    private static final String METHOD_ATTRIBUTE = "method";
    private static final String TARGET_ATTRIBUTE = "target";
    private static final String ENCTYPE_ATTRIBUTE = "enctype";
    private static final String ACCEPT_CHARSET_ATTRIBUTE = "accept-charset";
    private static final String ONSUBMIT_ATTRIBUTE = "onsubmit";
    private static final String ONRESET_ATTRIBUTE = "onreset";
    private static final String AUTOCOMPLETE_ATTRIBUTE = "autocomplete";
    private static final String NAME_ATTRIBUTE = "name";
    private static final String VALUE_ATTRIBUTE = "value";
    private static final String TYPE_ATTRIBUTE = "type";
    @Nullable
    private TagWriter tagWriter;
    private String modelAttribute = "command";
    @Nullable
    private String name;
    @Nullable
    private String action;
    @Nullable
    private String servletRelativeAction;
    private String method = "post";
    @Nullable
    private String target;
    @Nullable
    private String enctype;
    @Nullable
    private String acceptCharset;
    @Nullable
    private String onsubmit;
    @Nullable
    private String onreset;
    @Nullable
    private String autocomplete;
    private String methodParam = "_method";
    @Nullable
    private String previousNestedPath;

    public void setModelAttribute(String modelAttribute) {
        this.modelAttribute = modelAttribute;
    }

    protected String getModelAttribute() {
        return this.modelAttribute;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    @Nullable
    protected String getName() throws JspException {
        return this.name;
    }

    public void setAction(@Nullable String action) {
        this.action = action != null ? action : "";
    }

    @Nullable
    protected String getAction() {
        return this.action;
    }

    public void setServletRelativeAction(@Nullable String servletRelativeAction) {
        this.servletRelativeAction = servletRelativeAction;
    }

    @Nullable
    protected String getServletRelativeAction() {
        return this.servletRelativeAction;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    protected String getMethod() {
        return this.method;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Nullable
    public String getTarget() {
        return this.target;
    }

    public void setEnctype(String enctype) {
        this.enctype = enctype;
    }

    @Nullable
    protected String getEnctype() {
        return this.enctype;
    }

    public void setAcceptCharset(String acceptCharset) {
        this.acceptCharset = acceptCharset;
    }

    @Nullable
    protected String getAcceptCharset() {
        return this.acceptCharset;
    }

    public void setOnsubmit(String onsubmit) {
        this.onsubmit = onsubmit;
    }

    @Nullable
    protected String getOnsubmit() {
        return this.onsubmit;
    }

    public void setOnreset(String onreset) {
        this.onreset = onreset;
    }

    @Nullable
    protected String getOnreset() {
        return this.onreset;
    }

    public void setAutocomplete(String autocomplete) {
        this.autocomplete = autocomplete;
    }

    @Nullable
    protected String getAutocomplete() {
        return this.autocomplete;
    }

    public void setMethodParam(String methodParam) {
        this.methodParam = methodParam;
    }

    protected String getMethodParam() {
        return this.methodParam;
    }

    protected boolean isMethodBrowserSupported(String method) {
        return "get".equalsIgnoreCase(method) || DEFAULT_METHOD.equalsIgnoreCase(method);
    }

    @Override
    protected int writeTagContent(TagWriter tagWriter) throws JspException {
        this.tagWriter = tagWriter;
        tagWriter.startTag(FORM_TAG);
        this.writeDefaultAttributes(tagWriter);
        tagWriter.writeAttribute(ACTION_ATTRIBUTE, this.resolveAction());
        this.writeOptionalAttribute(tagWriter, METHOD_ATTRIBUTE, this.getHttpMethod());
        this.writeOptionalAttribute(tagWriter, TARGET_ATTRIBUTE, this.getTarget());
        this.writeOptionalAttribute(tagWriter, ENCTYPE_ATTRIBUTE, this.getEnctype());
        this.writeOptionalAttribute(tagWriter, ACCEPT_CHARSET_ATTRIBUTE, this.getAcceptCharset());
        this.writeOptionalAttribute(tagWriter, ONSUBMIT_ATTRIBUTE, this.getOnsubmit());
        this.writeOptionalAttribute(tagWriter, ONRESET_ATTRIBUTE, this.getOnreset());
        this.writeOptionalAttribute(tagWriter, AUTOCOMPLETE_ATTRIBUTE, this.getAutocomplete());
        tagWriter.forceBlock();
        if (!this.isMethodBrowserSupported(this.getMethod())) {
            this.assertHttpMethod(this.getMethod());
            String inputName = this.getMethodParam();
            String inputType = "hidden";
            tagWriter.startTag(INPUT_TAG);
            this.writeOptionalAttribute(tagWriter, TYPE_ATTRIBUTE, inputType);
            this.writeOptionalAttribute(tagWriter, NAME_ATTRIBUTE, inputName);
            this.writeOptionalAttribute(tagWriter, VALUE_ATTRIBUTE, this.processFieldValue(inputName, this.getMethod(), inputType));
            tagWriter.endTag();
        }
        String modelAttribute = this.resolveModelAttribute();
        this.pageContext.setAttribute(MODEL_ATTRIBUTE_VARIABLE_NAME, (Object)modelAttribute, 2);
        this.previousNestedPath = (String)this.pageContext.getAttribute("nestedPath", 2);
        this.pageContext.setAttribute("nestedPath", (Object)(modelAttribute + "."), 2);
        return 1;
    }

    private String getHttpMethod() {
        return this.isMethodBrowserSupported(this.getMethod()) ? this.getMethod() : DEFAULT_METHOD;
    }

    private void assertHttpMethod(String method) {
        for (HttpMethod httpMethod : HttpMethod.values()) {
            if (!httpMethod.name().equalsIgnoreCase(method)) continue;
            return;
        }
        throw new IllegalArgumentException("Invalid HTTP method: " + method);
    }

    @Override
    protected String autogenerateId() throws JspException {
        return this.resolveModelAttribute();
    }

    protected String resolveModelAttribute() throws JspException {
        Object resolvedModelAttribute = this.evaluate(MODEL_ATTRIBUTE, this.getModelAttribute());
        if (resolvedModelAttribute == null) {
            throw new IllegalArgumentException("modelAttribute must not be null");
        }
        return (String)resolvedModelAttribute;
    }

    protected String resolveAction() throws JspException {
        String action = this.getAction();
        String servletRelativeAction = this.getServletRelativeAction();
        if (StringUtils.hasText(action)) {
            action = this.getDisplayString(this.evaluate(ACTION_ATTRIBUTE, action));
            return this.processAction(action);
        }
        if (StringUtils.hasText(servletRelativeAction)) {
            String pathToServlet = this.getRequestContext().getPathToServlet();
            if (servletRelativeAction.startsWith("/") && !servletRelativeAction.startsWith(this.getRequestContext().getContextPath())) {
                servletRelativeAction = pathToServlet + servletRelativeAction;
            }
            servletRelativeAction = this.getDisplayString(this.evaluate(ACTION_ATTRIBUTE, servletRelativeAction));
            return this.processAction(servletRelativeAction);
        }
        String requestUri = this.getRequestContext().getRequestUri();
        String encoding = this.pageContext.getResponse().getCharacterEncoding();
        try {
            requestUri = UriUtils.encodePath(requestUri, encoding);
        }
        catch (UnsupportedCharsetException unsupportedCharsetException) {
            // empty catch block
        }
        ServletResponse response = this.pageContext.getResponse();
        if (response instanceof HttpServletResponse) {
            requestUri = ((HttpServletResponse)response).encodeURL(requestUri);
            String queryString = this.getRequestContext().getQueryString();
            if (StringUtils.hasText(queryString)) {
                requestUri = requestUri + "?" + HtmlUtils.htmlEscape(queryString);
            }
        }
        if (StringUtils.hasText(requestUri)) {
            return this.processAction(requestUri);
        }
        throw new IllegalArgumentException("Attribute 'action' is required. Attempted to resolve against current request URI but request URI was null.");
    }

    private String processAction(String action) {
        RequestDataValueProcessor processor = this.getRequestContext().getRequestDataValueProcessor();
        ServletRequest request = this.pageContext.getRequest();
        if (processor != null && request instanceof HttpServletRequest) {
            action = processor.processAction((HttpServletRequest)request, action, this.getHttpMethod());
        }
        return action;
    }

    public int doEndTag() throws JspException {
        RequestDataValueProcessor processor = this.getRequestContext().getRequestDataValueProcessor();
        ServletRequest request = this.pageContext.getRequest();
        if (processor != null && request instanceof HttpServletRequest) {
            this.writeHiddenFields(processor.getExtraHiddenFields((HttpServletRequest)request));
        }
        Assert.state(this.tagWriter != null, "No TagWriter set");
        this.tagWriter.endTag();
        return 6;
    }

    private void writeHiddenFields(@Nullable Map<String, String> hiddenFields) throws JspException {
        if (!CollectionUtils.isEmpty(hiddenFields)) {
            Assert.state(this.tagWriter != null, "No TagWriter set");
            this.tagWriter.appendValue("<div>\n");
            for (Map.Entry<String, String> entry : hiddenFields.entrySet()) {
                this.tagWriter.appendValue("<input type=\"hidden\" ");
                this.tagWriter.appendValue("name=\"" + entry.getKey() + "\" value=\"" + entry.getValue() + "\" ");
                this.tagWriter.appendValue("/>\n");
            }
            this.tagWriter.appendValue("</div>");
        }
    }

    @Override
    public void doFinally() {
        super.doFinally();
        this.pageContext.removeAttribute(MODEL_ATTRIBUTE_VARIABLE_NAME, 2);
        if (this.previousNestedPath != null) {
            this.pageContext.setAttribute("nestedPath", (Object)this.previousNestedPath, 2);
        } else {
            this.pageContext.removeAttribute("nestedPath", 2);
        }
        this.tagWriter = null;
        this.previousNestedPath = null;
    }

    @Override
    protected String resolveCssClass() throws JspException {
        return ObjectUtils.getDisplayString(this.evaluate("cssClass", this.getCssClass()));
    }

    @Override
    public void setPath(String path) {
        throw new UnsupportedOperationException("The 'path' attribute is not supported for forms");
    }

    @Override
    public void setCssErrorClass(String cssErrorClass) {
        throw new UnsupportedOperationException("The 'cssErrorClass' attribute is not supported for forms");
    }
}

