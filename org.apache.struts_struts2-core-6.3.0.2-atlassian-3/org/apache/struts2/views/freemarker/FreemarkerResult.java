/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  freemarker.template.Configuration
 *  freemarker.template.ObjectWrapper
 *  freemarker.template.Template
 *  freemarker.template.TemplateException
 *  freemarker.template.TemplateExceptionHandler
 *  freemarker.template.TemplateModel
 *  freemarker.template.TemplateModelException
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.views.freemarker;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.result.StrutsResultSupport;
import org.apache.struts2.views.freemarker.FreemarkerManager;

public class FreemarkerResult
extends StrutsResultSupport {
    private static final long serialVersionUID = -3778230771704661631L;
    private static final Logger LOG = LogManager.getLogger(FreemarkerResult.class);
    protected ActionInvocation invocation;
    protected Configuration configuration;
    protected ObjectWrapper wrapper;
    protected FreemarkerManager freemarkerManager;
    private Writer writer;
    private Boolean writeIfCompleted = null;
    protected String location;
    private String pContentType = "text/html";
    private static final String PARENT_TEMPLATE_WRITER = FreemarkerResult.class.getName() + ".parentWriter";

    public FreemarkerResult() {
    }

    public FreemarkerResult(String location) {
        super(location);
    }

    @Inject
    public void setFreemarkerManager(FreemarkerManager mgr) {
        this.freemarkerManager = mgr;
    }

    public void setContentType(String aContentType) {
        this.pContentType = aContentType;
    }

    public String getContentType() {
        return this.pContentType;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void doExecute(String locationArg, ActionInvocation invocation) throws IOException, TemplateException {
        block14: {
            TemplateModel model;
            String namespace;
            this.location = locationArg;
            this.invocation = invocation;
            this.configuration = this.getConfiguration();
            this.wrapper = this.getObjectWrapper();
            ActionContext ctx = invocation.getInvocationContext();
            HttpServletRequest req = ctx.getServletRequest();
            String absoluteLocation = this.location.startsWith("/") ? this.location : ((namespace = invocation.getProxy().getNamespace()) == null || namespace.length() == 0 || namespace.equals("/") ? "/" + this.location : (namespace.startsWith("/") ? namespace + "/" + this.location : "/" + namespace + "/" + this.location));
            Template template = this.configuration.getTemplate(absoluteLocation, this.deduceLocale());
            if (this.preTemplateProcess(template, model = this.createModel())) {
                try {
                    boolean willWriteIfCompleted = this.writeIfCompleted != null ? this.isWriteIfCompleted() : template.getTemplateExceptionHandler() == TemplateExceptionHandler.RETHROW_HANDLER;
                    Writer writer = this.getWriter();
                    if (willWriteIfCompleted) {
                        CharArrayWriter parentCharArrayWriter = (CharArrayWriter)req.getAttribute(PARENT_TEMPLATE_WRITER);
                        boolean isTopTemplate = parentCharArrayWriter == null;
                        if (isTopTemplate) {
                            parentCharArrayWriter = new CharArrayWriter();
                            req.setAttribute(PARENT_TEMPLATE_WRITER, (Object)parentCharArrayWriter);
                        }
                        try {
                            template.process((Object)model, (Writer)parentCharArrayWriter);
                            if (isTopTemplate) {
                                parentCharArrayWriter.flush();
                                parentCharArrayWriter.writeTo(writer);
                            }
                            break block14;
                        }
                        catch (TemplateException | IOException e) {
                            if (LOG.isErrorEnabled()) {
                                LOG.error("Error processing Freemarker result!", e);
                            }
                            throw e;
                        }
                        finally {
                            if (isTopTemplate) {
                                req.removeAttribute(PARENT_TEMPLATE_WRITER);
                                parentCharArrayWriter.close();
                            }
                        }
                    }
                    template.process((Object)model, writer);
                }
                finally {
                    this.postTemplateProcess(template, model);
                }
            }
        }
    }

    protected Configuration getConfiguration() throws TemplateException {
        return this.freemarkerManager.getConfiguration(ActionContext.getContext().getServletContext());
    }

    protected ObjectWrapper getObjectWrapper() {
        return this.configuration.getObjectWrapper();
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    protected Writer getWriter() throws IOException {
        if (this.writer != null) {
            return this.writer;
        }
        return ActionContext.getContext().getServletResponse().getWriter();
    }

    protected TemplateModel createModel() throws TemplateModelException {
        ServletContext servletContext = ActionContext.getContext().getServletContext();
        HttpServletRequest request = ActionContext.getContext().getServletRequest();
        HttpServletResponse response = ActionContext.getContext().getServletResponse();
        ValueStack stack = ActionContext.getContext().getValueStack();
        Object action = null;
        if (this.invocation != null) {
            action = this.invocation.getAction();
        }
        return this.freemarkerManager.buildTemplateModel(stack, action, servletContext, request, response, this.wrapper);
    }

    protected Locale deduceLocale() {
        if (this.invocation.getAction() instanceof LocaleProvider) {
            return ((LocaleProvider)this.invocation.getAction()).getLocale();
        }
        return this.configuration.getLocale();
    }

    protected void postTemplateProcess(Template template, TemplateModel model) throws IOException {
    }

    protected boolean preTemplateProcess(Template template, TemplateModel model) throws IOException {
        Object attrContentType = template.getCustomAttribute("content_type");
        HttpServletResponse response = ActionContext.getContext().getServletResponse();
        if (response.getContentType() == null) {
            if (attrContentType != null) {
                response.setContentType(attrContentType.toString());
            } else {
                String encoding;
                String contentType = this.getContentType();
                if (contentType == null) {
                    contentType = "text/html";
                }
                if ((encoding = template.getEncoding()) != null) {
                    contentType = contentType + "; charset=" + encoding;
                }
                response.setContentType(contentType);
            }
        } else if (this.isInsideActionTag()) {
            response.setContentType(response.getContentType());
        }
        return true;
    }

    private boolean isInsideActionTag() {
        Object attribute = ActionContext.getContext().getServletRequest().getAttribute("struts.actiontag.invocation");
        return (Boolean)ObjectUtils.defaultIfNull((Object)attribute, (Object)Boolean.FALSE);
    }

    public boolean isWriteIfCompleted() {
        return this.writeIfCompleted != null && this.writeIfCompleted != false;
    }

    public void setWriteIfCompleted(Boolean writeIfCompleted) {
        this.writeIfCompleted = writeIfCompleted;
    }
}

