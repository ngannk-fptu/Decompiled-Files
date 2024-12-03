/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 *  javax.servlet.jsp.JspTagException
 *  org.springframework.context.MessageSource
 *  org.springframework.context.MessageSourceResolvable
 *  org.springframework.context.NoSuchMessageException
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 *  org.springframework.web.util.JavaScriptUtils
 *  org.springframework.web.util.TagUtils
 */
package org.springframework.web.servlet.tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.tags.ArgumentAware;
import org.springframework.web.servlet.tags.HtmlEscapingAwareTag;
import org.springframework.web.util.JavaScriptUtils;
import org.springframework.web.util.TagUtils;

public class MessageTag
extends HtmlEscapingAwareTag
implements ArgumentAware {
    public static final String DEFAULT_ARGUMENT_SEPARATOR = ",";
    @Nullable
    private MessageSourceResolvable message;
    @Nullable
    private String code;
    @Nullable
    private Object arguments;
    private String argumentSeparator = ",";
    private List<Object> nestedArguments = Collections.emptyList();
    @Nullable
    private String text;
    @Nullable
    private String var;
    private String scope = "page";
    private boolean javaScriptEscape = false;

    public void setMessage(MessageSourceResolvable message) {
        this.message = message;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setArguments(Object arguments) {
        this.arguments = arguments;
    }

    public void setArgumentSeparator(String argumentSeparator) {
        this.argumentSeparator = argumentSeparator;
    }

    @Override
    public void addArgument(@Nullable Object argument) throws JspTagException {
        this.nestedArguments.add(argument);
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setJavaScriptEscape(boolean javaScriptEscape) throws JspException {
        this.javaScriptEscape = javaScriptEscape;
    }

    @Override
    protected final int doStartTagInternal() throws JspException, IOException {
        this.nestedArguments = new ArrayList<Object>();
        return 1;
    }

    public int doEndTag() throws JspException {
        try {
            String msg = this.resolveMessage();
            msg = this.htmlEscape(msg);
            String string = msg = this.javaScriptEscape ? JavaScriptUtils.javaScriptEscape((String)msg) : msg;
            if (this.var != null) {
                this.pageContext.setAttribute(this.var, (Object)msg, TagUtils.getScope((String)this.scope));
            } else {
                this.writeMessage(msg);
            }
            return 6;
        }
        catch (IOException ex) {
            throw new JspTagException(ex.getMessage(), (Throwable)ex);
        }
        catch (NoSuchMessageException ex) {
            throw new JspTagException(this.getNoSuchMessageExceptionDescription(ex));
        }
    }

    public void release() {
        super.release();
        this.arguments = null;
    }

    protected String resolveMessage() throws JspException, NoSuchMessageException {
        MessageSource messageSource = this.getMessageSource();
        if (this.message != null) {
            return messageSource.getMessage(this.message, this.getRequestContext().getLocale());
        }
        if (this.code != null || this.text != null) {
            Object[] argumentsArray = this.resolveArguments(this.arguments);
            if (!this.nestedArguments.isEmpty()) {
                argumentsArray = this.appendArguments(argumentsArray, this.nestedArguments.toArray());
            }
            if (this.text != null) {
                String msg = messageSource.getMessage(this.code, argumentsArray, this.text, this.getRequestContext().getLocale());
                return msg != null ? msg : "";
            }
            return messageSource.getMessage(this.code, argumentsArray, this.getRequestContext().getLocale());
        }
        throw new JspTagException("No resolvable message");
    }

    private Object[] appendArguments(@Nullable Object[] sourceArguments, Object[] additionalArguments) {
        if (ObjectUtils.isEmpty((Object[])sourceArguments)) {
            return additionalArguments;
        }
        Object[] arguments = new Object[sourceArguments.length + additionalArguments.length];
        System.arraycopy(sourceArguments, 0, arguments, 0, sourceArguments.length);
        System.arraycopy(additionalArguments, 0, arguments, sourceArguments.length, additionalArguments.length);
        return arguments;
    }

    @Nullable
    protected Object[] resolveArguments(@Nullable Object arguments) throws JspException {
        if (arguments instanceof String) {
            return StringUtils.delimitedListToStringArray((String)((String)arguments), (String)this.argumentSeparator);
        }
        if (arguments instanceof Object[]) {
            return (Object[])arguments;
        }
        if (arguments instanceof Collection) {
            return ((Collection)arguments).toArray();
        }
        if (arguments != null) {
            return new Object[]{arguments};
        }
        return null;
    }

    protected void writeMessage(String msg) throws IOException {
        this.pageContext.getOut().write(msg);
    }

    protected MessageSource getMessageSource() {
        return this.getRequestContext().getMessageSource();
    }

    protected String getNoSuchMessageExceptionDescription(NoSuchMessageException ex) {
        return ex.getMessage();
    }
}

