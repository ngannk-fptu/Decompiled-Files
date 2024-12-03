/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  freemarker.template.TemplateModelException
 *  freemarker.template.TransformControl
 */
package org.apache.struts2.views.freemarker.tags;

import freemarker.template.TemplateModelException;
import freemarker.template.TransformControl;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import org.apache.struts2.components.Component;

public class CallbackWriter
extends Writer
implements TransformControl {
    private Component bean;
    private Writer writer;
    private StringWriter body;
    private boolean afterBody = false;

    public CallbackWriter(Component bean, Writer writer) {
        this.bean = bean;
        this.writer = writer;
        if (bean.usesBody()) {
            this.body = new StringWriter();
        }
    }

    @Override
    public void close() throws IOException {
        if (this.bean.usesBody()) {
            this.body.close();
        }
    }

    @Override
    public void flush() throws IOException {
        this.writer.flush();
        if (this.bean.usesBody()) {
            this.body.flush();
        }
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        if (this.bean.usesBody() && !this.afterBody) {
            this.body.write(cbuf, off, len);
        } else {
            this.writer.write(cbuf, off, len);
        }
    }

    public int onStart() throws TemplateModelException, IOException {
        boolean result = this.bean.start(this);
        if (result) {
            return 1;
        }
        return 0;
    }

    public int afterBody() throws TemplateModelException, IOException {
        this.afterBody = true;
        boolean result = this.bean.end(this, this.bean.usesBody() ? this.body.toString() : "");
        if (result) {
            return 0;
        }
        return 1;
    }

    public void onError(Throwable throwable) throws Throwable {
        throw throwable;
    }

    public Component getBean() {
        return this.bean;
    }
}

