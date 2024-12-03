/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 *  javax.servlet.jsp.JspWriter
 *  javax.servlet.jsp.PageContext
 *  javax.servlet.jsp.tagext.BodyContent
 *  javax.servlet.jsp.tagext.BodyTag
 *  javax.servlet.jsp.tagext.IterationTag
 *  javax.servlet.jsp.tagext.Tag
 *  javax.servlet.jsp.tagext.TryCatchFinally
 */
package freemarker.ext.jsp;

import freemarker.ext.jsp.FreeMarkerPageContext;
import freemarker.ext.jsp.JspTagModelBase;
import freemarker.ext.jsp.JspWriterAdapter;
import freemarker.ext.jsp.PageContextFactory;
import freemarker.log.Logger;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateTransformModel;
import freemarker.template.TransformControl;
import java.beans.IntrospectionException;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.IterationTag;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TryCatchFinally;

class TagTransformModel
extends JspTagModelBase
implements TemplateTransformModel {
    private static final Logger LOG = Logger.getLogger("freemarker.jsp");
    private final boolean isBodyTag;
    private final boolean isIterationTag;
    private final boolean isTryCatchFinally;

    public TagTransformModel(String tagName, Class tagClass) throws IntrospectionException {
        super(tagName, tagClass);
        this.isIterationTag = IterationTag.class.isAssignableFrom(tagClass);
        this.isBodyTag = this.isIterationTag && BodyTag.class.isAssignableFrom(tagClass);
        this.isTryCatchFinally = TryCatchFinally.class.isAssignableFrom(tagClass);
    }

    @Override
    public Writer getWriter(Writer out, Map args) throws TemplateModelException {
        try {
            boolean usesAdapter;
            Tag tag = (Tag)this.getTagInstance();
            FreeMarkerPageContext pageContext = PageContextFactory.getCurrentPageContext();
            Tag parentTag = (Tag)pageContext.peekTopTag(Tag.class);
            tag.setParent(parentTag);
            tag.setPageContext((PageContext)pageContext);
            this.setupTag(tag, args, pageContext.getObjectWrapper());
            if (out instanceof JspWriter) {
                if (out != pageContext.getOut()) {
                    throw new TemplateModelException("out != pageContext.getOut(). Out is " + out + " pageContext.getOut() is " + pageContext.getOut());
                }
                usesAdapter = false;
            } else {
                out = new JspWriterAdapter((Writer)out);
                pageContext.pushWriter((JspWriter)out);
                usesAdapter = true;
            }
            TagWriter w = new TagWriter((Writer)out, tag, pageContext, usesAdapter);
            pageContext.pushTopTag(tag);
            pageContext.pushWriter((JspWriter)w);
            return w;
        }
        catch (Exception e) {
            throw this.toTemplateModelExceptionOrRethrow(e);
        }
    }

    class TagWriter
    extends BodyContentImpl
    implements TransformControl {
        private final Tag tag;
        private final FreeMarkerPageContext pageContext;
        private boolean needPop;
        private final boolean needDoublePop;
        private boolean closed;

        TagWriter(Writer out, Tag tag, FreeMarkerPageContext pageContext, boolean needDoublePop) {
            super((JspWriter)out, false);
            this.needPop = true;
            this.closed = false;
            this.needDoublePop = needDoublePop;
            this.tag = tag;
            this.pageContext = pageContext;
        }

        public String toString() {
            return "TagWriter for " + this.tag.getClass().getName() + " wrapping a " + this.getEnclosingWriter().toString();
        }

        Tag getTag() {
            return this.tag;
        }

        FreeMarkerPageContext getPageContext() {
            return this.pageContext;
        }

        @Override
        public int onStart() throws TemplateModelException {
            try {
                int dst = this.tag.doStartTag();
                switch (dst) {
                    case 0: 
                    case 6: {
                        this.endEvaluation();
                        return 0;
                    }
                    case 2: {
                        if (TagTransformModel.this.isBodyTag) {
                            this.initBuffer();
                            BodyTag btag = (BodyTag)this.tag;
                            btag.setBodyContent((BodyContent)this);
                            btag.doInitBody();
                        } else {
                            throw new TemplateModelException("Can't buffer body since " + this.tag.getClass().getName() + " does not implement BodyTag.");
                        }
                    }
                    case 1: {
                        return 1;
                    }
                }
                throw new RuntimeException("Illegal return value " + dst + " from " + this.tag.getClass().getName() + ".doStartTag()");
            }
            catch (Exception e) {
                throw TagTransformModel.this.toTemplateModelExceptionOrRethrow(e);
            }
        }

        @Override
        public int afterBody() throws TemplateModelException {
            try {
                if (TagTransformModel.this.isIterationTag) {
                    int dab = ((IterationTag)this.tag).doAfterBody();
                    switch (dab) {
                        case 0: {
                            this.endEvaluation();
                            return 1;
                        }
                        case 2: {
                            return 0;
                        }
                    }
                    throw new TemplateModelException("Unexpected return value " + dab + "from " + this.tag.getClass().getName() + ".doAfterBody()");
                }
                this.endEvaluation();
                return 1;
            }
            catch (Exception e) {
                throw TagTransformModel.this.toTemplateModelExceptionOrRethrow(e);
            }
        }

        private void endEvaluation() throws JspException {
            if (this.needPop) {
                this.pageContext.popWriter();
                this.needPop = false;
            }
            if (this.tag.doEndTag() == 5) {
                LOG.warn("Tag.SKIP_PAGE was ignored from a " + this.tag.getClass().getName() + " tag.");
            }
        }

        @Override
        public void onError(Throwable t) throws Throwable {
            if (!TagTransformModel.this.isTryCatchFinally) {
                throw t;
            }
            ((TryCatchFinally)this.tag).doCatch(t);
        }

        @Override
        public void close() {
            if (this.closed) {
                return;
            }
            this.closed = true;
            if (this.needPop) {
                this.pageContext.popWriter();
            }
            this.pageContext.popTopTag();
            try {
                if (TagTransformModel.this.isTryCatchFinally) {
                    ((TryCatchFinally)this.tag).doFinally();
                }
                this.tag.release();
            }
            finally {
                if (this.needDoublePop) {
                    this.pageContext.popWriter();
                }
            }
        }
    }

    static class BodyContentImpl
    extends BodyContent {
        private CharArrayWriter buf;

        BodyContentImpl(JspWriter out, boolean buffer) {
            super(out);
            if (buffer) {
                this.initBuffer();
            }
        }

        void initBuffer() {
            this.buf = new CharArrayWriter();
        }

        public void flush() throws IOException {
            if (this.buf == null) {
                this.getEnclosingWriter().flush();
            }
        }

        public void clear() throws IOException {
            if (this.buf == null) {
                throw new IOException("Can't clear");
            }
            this.buf = new CharArrayWriter();
        }

        public void clearBuffer() throws IOException {
            if (this.buf == null) {
                throw new IOException("Can't clear");
            }
            this.buf = new CharArrayWriter();
        }

        public int getRemaining() {
            return Integer.MAX_VALUE;
        }

        public void newLine() throws IOException {
            this.write(JspWriterAdapter.NEWLINE);
        }

        public void close() throws IOException {
        }

        public void print(boolean arg0) throws IOException {
            this.write(arg0 ? Boolean.TRUE.toString() : Boolean.FALSE.toString());
        }

        public void print(char arg0) throws IOException {
            this.write(arg0);
        }

        public void print(char[] arg0) throws IOException {
            this.write(arg0);
        }

        public void print(double arg0) throws IOException {
            this.write(Double.toString(arg0));
        }

        public void print(float arg0) throws IOException {
            this.write(Float.toString(arg0));
        }

        public void print(int arg0) throws IOException {
            this.write(Integer.toString(arg0));
        }

        public void print(long arg0) throws IOException {
            this.write(Long.toString(arg0));
        }

        public void print(Object arg0) throws IOException {
            this.write(arg0 == null ? "null" : arg0.toString());
        }

        public void print(String arg0) throws IOException {
            this.write(arg0);
        }

        public void println() throws IOException {
            this.newLine();
        }

        public void println(boolean arg0) throws IOException {
            this.print(arg0);
            this.newLine();
        }

        public void println(char arg0) throws IOException {
            this.print(arg0);
            this.newLine();
        }

        public void println(char[] arg0) throws IOException {
            this.print(arg0);
            this.newLine();
        }

        public void println(double arg0) throws IOException {
            this.print(arg0);
            this.newLine();
        }

        public void println(float arg0) throws IOException {
            this.print(arg0);
            this.newLine();
        }

        public void println(int arg0) throws IOException {
            this.print(arg0);
            this.newLine();
        }

        public void println(long arg0) throws IOException {
            this.print(arg0);
            this.newLine();
        }

        public void println(Object arg0) throws IOException {
            this.print(arg0);
            this.newLine();
        }

        public void println(String arg0) throws IOException {
            this.print(arg0);
            this.newLine();
        }

        public void write(int c) throws IOException {
            if (this.buf != null) {
                this.buf.write(c);
            } else {
                this.getEnclosingWriter().write(c);
            }
        }

        public void write(char[] cbuf, int off, int len) throws IOException {
            if (this.buf != null) {
                this.buf.write(cbuf, off, len);
            } else {
                this.getEnclosingWriter().write(cbuf, off, len);
            }
        }

        public String getString() {
            return this.buf.toString();
        }

        public Reader getReader() {
            return new CharArrayReader(this.buf.toCharArray());
        }

        public void writeOut(Writer out) throws IOException {
            this.buf.writeTo(out);
        }
    }
}

