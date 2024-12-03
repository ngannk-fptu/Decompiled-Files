/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.digester.ObjectCreateRule
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.startup;

import java.util.HashMap;
import java.util.Set;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.digester.ObjectCreateRule;
import org.apache.tomcat.util.res.StringManager;
import org.xml.sax.Attributes;

public class ListenerCreateRule
extends ObjectCreateRule {
    private static final Log log = LogFactory.getLog(ListenerCreateRule.class);
    protected static final StringManager sm = StringManager.getManager(ListenerCreateRule.class);

    public ListenerCreateRule(String className, String attributeName) {
        super(className, attributeName);
    }

    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        if ("true".equals(attributes.getValue("optional"))) {
            try {
                super.begin(namespace, name, attributes);
            }
            catch (Exception e) {
                String className = this.getRealClassName(attributes);
                if (log.isDebugEnabled()) {
                    log.info((Object)sm.getString("listener.createFailed", new Object[]{className}), (Throwable)e);
                } else {
                    log.info((Object)sm.getString("listener.createFailed", new Object[]{className}));
                }
                OptionalListener instance = new OptionalListener(className);
                this.digester.push((Object)instance);
                StringBuilder code = this.digester.getGeneratedCode();
                if (code != null) {
                    code.append(OptionalListener.class.getName().replace('$', '.')).append(' ');
                    code.append(this.digester.toVariableName((Object)instance)).append(" = new ");
                    code.append(OptionalListener.class.getName().replace('$', '.')).append("(\"").append(className).append("\");");
                    code.append(System.lineSeparator());
                }
            }
        } else {
            super.begin(namespace, name, attributes);
        }
    }

    public static class OptionalListener
    implements LifecycleListener {
        protected final String className;
        protected final HashMap<String, String> properties = new HashMap();

        public OptionalListener(String className) {
            this.className = className;
        }

        public String getClassName() {
            return this.className;
        }

        @Override
        public void lifecycleEvent(LifecycleEvent event) {
        }

        public Set<String> getProperties() {
            return this.properties.keySet();
        }

        public Object getProperty(String name) {
            return this.properties.get(name);
        }

        public boolean setProperty(String name, String value) {
            this.properties.put(name, value);
            return true;
        }
    }
}

