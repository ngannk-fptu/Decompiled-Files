/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import java.io.Writer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.components.ContextBean;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

@StrutsTag(name="bean", tldTagClass="org.apache.struts2.views.jsp.BeanTag", description="Instantiate a JavaBean and place it in the context")
public class Bean
extends ContextBean {
    protected static final Logger LOG = LogManager.getLogger(Bean.class);
    protected Object bean;
    protected String name;
    protected ObjectFactory objectFactory;
    protected ReflectionProvider reflectionProvider;

    public Bean(ValueStack stack) {
        super(stack);
    }

    @Inject
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    @Inject
    public void setReflectionProvider(ReflectionProvider prov) {
        this.reflectionProvider = prov;
    }

    @Override
    public boolean start(Writer writer) {
        boolean result = super.start(writer);
        ValueStack stack = this.getStack();
        try {
            String beanName = this.findString(this.name, "name", "Bean name is required. Example: com.acme.FooBean or proper Spring bean ID");
            this.bean = this.objectFactory.buildBean(beanName, stack.getContext(), false);
        }
        catch (Exception e) {
            LOG.error("Could not instantiate bean", (Throwable)e);
            return false;
        }
        stack.push(this.bean);
        this.putInContext(this.bean);
        return result;
    }

    @Override
    public boolean end(Writer writer, String body) {
        ValueStack stack = this.getStack();
        stack.pop();
        return super.end(writer, body);
    }

    @Override
    public void addParameter(String key, Object value) {
        this.reflectionProvider.setProperty(key, value, this.bean, this.getStack().getContext());
    }

    @StrutsTagAttribute(description="The class name of the bean to be instantiated (must respect JavaBean specification)", required=true)
    public void setName(String name) {
        this.name = name;
    }
}

