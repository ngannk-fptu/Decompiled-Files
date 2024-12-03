/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.StrutsException;
import org.apache.struts2.components.UIBean;
import org.apache.struts2.dispatcher.PrepareOperations;
import org.apache.struts2.views.annotations.StrutsTag;

@StrutsTag(name="debug", tldTagClass="org.apache.struts2.views.jsp.ui.DebugTag", description="Prints debugging information (Only if 'struts.devMode' is enabled)")
public class Debug
extends UIBean {
    public static final String TEMPLATE = "debug";
    protected ReflectionProvider reflectionProvider;

    public Debug(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Inject
    public void setReflectionProvider(ReflectionProvider prov) {
        this.reflectionProvider = prov;
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    public boolean start(Writer writer) {
        boolean result = super.start(writer);
        if (this.showDebug()) {
            ValueStack stack = this.getStack();
            Iterator iter = stack.getRoot().iterator();
            ArrayList<DebugMapEntry> stackValues = new ArrayList<DebugMapEntry>(stack.getRoot().size());
            while (iter.hasNext()) {
                Map<String, Object> values;
                Object o = iter.next();
                try {
                    values = this.reflectionProvider.getBeanMap(o);
                }
                catch (Exception e) {
                    throw new StrutsException("Caught an exception while getting the property values of " + o, e);
                }
                stackValues.add(new DebugMapEntry(o.getClass().getName(), values));
            }
            this.addParameter("stackValues", stackValues);
        }
        return result;
    }

    @Override
    public boolean end(Writer writer, String body) {
        if (this.showDebug()) {
            return super.end(writer, body);
        }
        this.popComponentStack();
        return false;
    }

    protected boolean showDebug() {
        return this.devMode || Boolean.TRUE == PrepareOperations.getDevModeOverride();
    }

    private static class DebugMapEntry
    implements Map.Entry {
        private Object key;
        private Object value;

        DebugMapEntry(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        public Object getKey() {
            return this.key;
        }

        public Object getValue() {
            return this.value;
        }

        public Object setValue(Object newVal) {
            Object oldVal = this.value;
            this.value = newVal;
            return oldVal;
        }
    }
}

