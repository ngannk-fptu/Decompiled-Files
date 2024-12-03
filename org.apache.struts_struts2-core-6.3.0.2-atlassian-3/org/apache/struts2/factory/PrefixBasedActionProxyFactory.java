/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.factory;

import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Initializable;
import com.opensymphony.xwork2.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.factory.StrutsActionProxyFactory;

public class PrefixBasedActionProxyFactory
extends StrutsActionProxyFactory
implements Initializable {
    private static final Logger LOG = LogManager.getLogger(PrefixBasedActionProxyFactory.class);
    private Map<String, ActionProxyFactory> actionProxyFactories = new HashMap<String, ActionProxyFactory>();
    private Set<String> prefixes = new HashSet<String>();

    @Override
    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    @Inject(value="struts.mapper.prefixMapping")
    public void setPrefixBasedActionProxyFactories(String list) {
        if (list != null) {
            this.prefixes = new HashSet<String>(Arrays.asList(list.split(",")));
        }
    }

    @Override
    public void init() {
        for (String factory : this.prefixes) {
            String[] thisFactory = factory.split(":");
            if (thisFactory.length != 2) continue;
            String factoryPrefix = thisFactory[0].trim();
            String factoryName = thisFactory[1].trim();
            ActionProxyFactory obj = this.container.getInstance(ActionProxyFactory.class, factoryName);
            if (obj != null) {
                this.actionProxyFactories.put(factoryPrefix, obj);
                continue;
            }
            LOG.warn("Invalid PrefixBasedActionProxyFactory config entry: [{}]", (Object)factory);
        }
    }

    @Override
    public ActionProxy createActionProxy(String namespace, String actionName, String methodName, Map<String, Object> extraContext, boolean executeResult, boolean cleanupContext) {
        String uri = namespace + (namespace.endsWith("/") ? actionName : "/" + actionName);
        int lastIndex = uri.lastIndexOf(47);
        while (lastIndex > -1) {
            String key = uri.substring(0, lastIndex);
            ActionProxyFactory actionProxyFactory = this.actionProxyFactories.get(key);
            if (actionProxyFactory != null) {
                LOG.debug("Using ActionProxyFactory [{}] for prefix [{}]", (Object)actionProxyFactory, (Object)key);
                return actionProxyFactory.createActionProxy(namespace, actionName, methodName, extraContext, executeResult, cleanupContext);
            }
            LOG.debug("No ActionProxyFactory defined for [{}]", (Object)key);
            lastIndex = uri.lastIndexOf(47, lastIndex - 1);
        }
        LOG.debug("Cannot find any matching ActionProxyFactory, falling back to [{}]", (Object)super.getClass().getName());
        return super.createActionProxy(namespace, actionName, methodName, extraContext, executeResult, cleanupContext);
    }
}

