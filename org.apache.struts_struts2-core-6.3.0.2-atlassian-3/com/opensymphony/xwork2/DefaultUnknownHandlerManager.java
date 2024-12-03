/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.UnknownHandler;
import com.opensymphony.xwork2.UnknownHandlerManager;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.UnknownHandlerConfig;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class DefaultUnknownHandlerManager
implements UnknownHandlerManager {
    private Container container;
    protected ArrayList<UnknownHandler> unknownHandlers;

    @Inject
    public void setContainer(Container container) {
        this.container = container;
        try {
            this.build();
        }
        catch (Exception e) {
            throw new ConfigurationException(e);
        }
    }

    protected void build() throws Exception {
        block4: {
            Configuration configuration = this.container.getInstance(Configuration.class);
            ObjectFactory factory = this.container.getInstance(ObjectFactory.class);
            if (configuration == null || this.container == null) break block4;
            List<UnknownHandlerConfig> unkownHandlerStack = configuration.getUnknownHandlerStack();
            this.unknownHandlers = new ArrayList();
            if (unkownHandlerStack != null && !unkownHandlerStack.isEmpty()) {
                for (UnknownHandlerConfig unknownHandlerConfig : unkownHandlerStack) {
                    UnknownHandler uh = factory.buildUnknownHandler(unknownHandlerConfig.getName(), new HashMap<String, Object>());
                    this.unknownHandlers.add(uh);
                }
            } else {
                Set<String> unknownHandlerNames = this.container.getInstanceNames(UnknownHandler.class);
                for (String unknownHandlerName : unknownHandlerNames) {
                    UnknownHandler uh = this.container.getInstance(UnknownHandler.class, unknownHandlerName);
                    this.unknownHandlers.add(uh);
                }
            }
        }
    }

    @Override
    public Result handleUnknownResult(ActionContext actionContext, String actionName, ActionConfig actionConfig, String resultCode) {
        for (UnknownHandler unknownHandler : this.unknownHandlers) {
            Result result = unknownHandler.handleUnknownResult(actionContext, actionName, actionConfig, resultCode);
            if (result == null) continue;
            return result;
        }
        return null;
    }

    @Override
    public Object handleUnknownMethod(Object action, String methodName) throws NoSuchMethodException {
        for (UnknownHandler unknownHandler : this.unknownHandlers) {
            Object result = unknownHandler.handleUnknownActionMethod(action, methodName);
            if (result == null) continue;
            return result;
        }
        if (this.unknownHandlers.isEmpty()) {
            throw new NoSuchMethodException(String.format("No UnknownHandlers defined to handle method [%s]", methodName));
        }
        throw new NoSuchMethodException(String.format("None of defined UnknownHandlers can handle method [%s]", methodName));
    }

    @Override
    public ActionConfig handleUnknownAction(String namespace, String actionName) {
        for (UnknownHandler unknownHandler : this.unknownHandlers) {
            ActionConfig result = unknownHandler.handleUnknownAction(namespace, actionName);
            if (result == null) continue;
            return result;
        }
        return null;
    }

    @Override
    public boolean hasUnknownHandlers() {
        return this.unknownHandlers != null && !this.unknownHandlers.isEmpty();
    }

    @Override
    public List<UnknownHandler> getUnknownHandlers() {
        return this.unknownHandlers;
    }
}

