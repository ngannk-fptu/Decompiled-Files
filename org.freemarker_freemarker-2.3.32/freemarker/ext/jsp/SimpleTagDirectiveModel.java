/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspContext
 *  javax.servlet.jsp.JspException
 *  javax.servlet.jsp.tagext.JspFragment
 *  javax.servlet.jsp.tagext.JspTag
 *  javax.servlet.jsp.tagext.SimpleTag
 *  javax.servlet.jsp.tagext.Tag
 */
package freemarker.ext.jsp;

import freemarker.core.Environment;
import freemarker.ext.jsp.FreeMarkerPageContext;
import freemarker.ext.jsp.JspTagModelBase;
import freemarker.ext.jsp.JspWriterAdapter;
import freemarker.ext.jsp.PageContextFactory;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.jsp.tagext.SimpleTag;
import javax.servlet.jsp.tagext.Tag;

class SimpleTagDirectiveModel
extends JspTagModelBase
implements TemplateDirectiveModel {
    protected SimpleTagDirectiveModel(String tagName, Class tagClass) throws IntrospectionException {
        super(tagName, tagClass);
        if (!SimpleTag.class.isAssignableFrom(tagClass)) {
            throw new IllegalArgumentException(tagClass.getName() + " does not implement either the " + Tag.class.getName() + " interface or the " + SimpleTag.class.getName() + " interface.");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void execute(Environment env, Map args, TemplateModel[] outArgs, final TemplateDirectiveBody body) throws TemplateException, IOException {
        block11: {
            try {
                SimpleTag tag = (SimpleTag)this.getTagInstance();
                final FreeMarkerPageContext pageContext = PageContextFactory.getCurrentPageContext();
                pageContext.pushWriter(new JspWriterAdapter(env.getOut()));
                try {
                    tag.setJspContext((JspContext)pageContext);
                    JspTag parentTag = (JspTag)pageContext.peekTopTag(JspTag.class);
                    if (parentTag != null) {
                        tag.setParent(parentTag);
                    }
                    this.setupTag(tag, args, pageContext.getObjectWrapper());
                    if (body != null) {
                        tag.setJspBody(new JspFragment(){

                            public JspContext getJspContext() {
                                return pageContext;
                            }

                            public void invoke(Writer out) throws JspException, IOException {
                                try {
                                    body.render((Writer)(out == null ? pageContext.getOut() : out));
                                }
                                catch (TemplateException e) {
                                    throw new TemplateExceptionWrapperJspException(e);
                                }
                            }
                        });
                        pageContext.pushTopTag(tag);
                        try {
                            tag.doTag();
                            break block11;
                        }
                        finally {
                            pageContext.popTopTag();
                        }
                    }
                    tag.doTag();
                }
                finally {
                    pageContext.popWriter();
                }
            }
            catch (TemplateException e) {
                throw e;
            }
            catch (Exception e) {
                throw this.toTemplateModelExceptionOrRethrow(e);
            }
        }
    }

    static final class TemplateExceptionWrapperJspException
    extends JspException {
        public TemplateExceptionWrapperJspException(TemplateException cause) {
            super("Nested content has thrown template exception", (Throwable)cause);
        }

        public TemplateException getCause() {
            return (TemplateException)super.getCause();
        }
    }
}

