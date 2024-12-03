/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.dispatcher;

import com.opensymphony.xwork2.ActionContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.HostConfig;
import org.apache.struts2.dispatcher.StaticContentLoader;

public class InitOperations {
    public Dispatcher initDispatcher(HostConfig filterConfig) {
        Dispatcher dispatcher = this.createDispatcher(filterConfig);
        dispatcher.init();
        return dispatcher;
    }

    public StaticContentLoader initStaticContentLoader(HostConfig filterConfig, Dispatcher dispatcher) {
        StaticContentLoader loader = dispatcher.getStaticContentLoader();
        loader.setHostConfig(filterConfig);
        return loader;
    }

    public Dispatcher findDispatcherOnThread() {
        Dispatcher dispatcher = Dispatcher.getInstance();
        if (dispatcher == null) {
            throw new IllegalStateException("Must have the StrutsPrepareFilter execute before this one");
        }
        return dispatcher;
    }

    protected Dispatcher createDispatcher(HostConfig filterConfig) {
        HashMap<String, String> params = new HashMap<String, String>();
        Iterator<String> parameterNames = filterConfig.getInitParameterNames();
        while (parameterNames.hasNext()) {
            String name = parameterNames.next();
            String value = filterConfig.getInitParameter(name);
            params.put(name, value);
        }
        return new Dispatcher(filterConfig.getServletContext(), params);
    }

    public void cleanup() {
        ActionContext.clear();
    }

    public List<Pattern> buildExcludedPatternsList(Dispatcher dispatcher) {
        String excludePatterns = dispatcher.getContainer().getInstance(String.class, "struts.action.excludePattern");
        String separator = dispatcher.getContainer().getInstance(String.class, "struts.action.excludePattern.separator");
        if (separator == null) {
            separator = ",";
        }
        return this.buildExcludedPatternsList(excludePatterns, separator);
    }

    private List<Pattern> buildExcludedPatternsList(String patterns, String separator) {
        if (null != patterns && patterns.trim().length() != 0) {
            String[] tokens;
            ArrayList<Pattern> list = new ArrayList<Pattern>();
            for (String token : tokens = patterns.split(separator)) {
                list.add(Pattern.compile(token.trim()));
            }
            return Collections.unmodifiableList(list);
        }
        return null;
    }
}

