/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  freemarker.ext.beans.BeanModel
 *  freemarker.ext.beans.BeansWrapper
 *  freemarker.template.SimpleNumber
 *  freemarker.template.SimpleSequence
 *  freemarker.template.TemplateModel
 *  freemarker.template.TemplateModelException
 *  freemarker.template.TemplateTransformModel
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.views.freemarker.tags;

import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.ValueStack;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateTransformModel;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.freemarker.tags.CallbackWriter;

public abstract class TagModel
implements TemplateTransformModel {
    private static final Logger LOG = LogManager.getLogger(TagModel.class);
    protected ValueStack stack;
    protected HttpServletRequest req;
    protected HttpServletResponse res;

    public TagModel(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        this.stack = stack;
        this.req = req;
        this.res = res;
    }

    public Writer getWriter(Writer writer, Map params) throws TemplateModelException, IOException {
        Component bean = this.getBean();
        Container container = this.stack.getActionContext().getContainer();
        container.inject(bean);
        Map unwrappedParameters = this.unwrapParameters(params);
        bean.copyParams(unwrappedParameters);
        return new CallbackWriter(bean, writer);
    }

    protected abstract Component getBean();

    protected Map unwrapParameters(Map params) {
        HashMap map = new HashMap(params.size());
        BeansWrapper objectWrapper = BeansWrapper.getDefaultInstance();
        for (Map.Entry entry : params.entrySet()) {
            Object value = entry.getValue();
            if (value == null) continue;
            if (value instanceof TemplateModel) {
                try {
                    map.put(entry.getKey(), objectWrapper.unwrap((TemplateModel)value));
                }
                catch (TemplateModelException e) {
                    LOG.error("failed to unwrap [{}] it will be ignored", (Object)value.toString(), (Object)e);
                }
                continue;
            }
            map.put(entry.getKey(), value.toString());
        }
        return map;
    }

    protected Map convertParams(Map params) {
        HashMap map = new HashMap(params.size());
        for (Map.Entry entry : params.entrySet()) {
            Object value = entry.getValue();
            if (value == null || this.complexType(value)) continue;
            map.put(entry.getKey(), value.toString());
        }
        return map;
    }

    protected Map getComplexParams(Map params) {
        HashMap map = new HashMap(params.size());
        for (Map.Entry entry : params.entrySet()) {
            Object value = entry.getValue();
            if (value == null || !this.complexType(value)) continue;
            if (value instanceof BeanModel) {
                map.put(entry.getKey(), ((BeanModel)value).getWrappedObject());
                continue;
            }
            if (value instanceof SimpleNumber) {
                map.put(entry.getKey(), ((SimpleNumber)value).getAsNumber());
                continue;
            }
            if (!(value instanceof SimpleSequence)) continue;
            try {
                map.put(entry.getKey(), ((SimpleSequence)value).toList());
            }
            catch (TemplateModelException e) {
                if (!LOG.isErrorEnabled()) continue;
                LOG.error("There was a problem converting a SimpleSequence to a list", (Throwable)e);
            }
        }
        return map;
    }

    protected boolean complexType(Object value) {
        return value instanceof BeanModel || value instanceof SimpleNumber || value instanceof SimpleSequence;
    }
}

