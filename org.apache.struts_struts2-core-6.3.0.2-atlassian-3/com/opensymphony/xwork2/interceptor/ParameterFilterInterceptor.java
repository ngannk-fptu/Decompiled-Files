/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.TextParseUtil;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.HttpParameters;

@Deprecated
public class ParameterFilterInterceptor
extends AbstractInterceptor {
    private static final Logger LOG = LogManager.getLogger(ParameterFilterInterceptor.class);
    private Collection<String> allowed;
    private Collection<String> blocked;
    private Map<String, Boolean> includesExcludesMap;
    private boolean defaultBlock = false;

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        HttpParameters parameters = invocation.getInvocationContext().getParameters();
        Map<String, Boolean> includesExcludesMap = this.getIncludesExcludesMap();
        for (String param : parameters.keySet()) {
            boolean currentAllowed = !this.isDefaultBlock();
            for (Map.Entry<String, Boolean> entry : includesExcludesMap.entrySet()) {
                String currRule = entry.getKey();
                if (!param.startsWith(currRule) || param.length() != currRule.length() && !this.isPropertySeparator(param.charAt(currRule.length()))) continue;
                currentAllowed = entry.getValue();
            }
            if (currentAllowed) continue;
            LOG.debug("Removing param: {}", (Object)param);
            parameters = parameters.remove(param);
        }
        invocation.getInvocationContext().withParameters(parameters);
        return invocation.invoke();
    }

    private boolean isPropertySeparator(char c) {
        return c == '.' || c == '(' || c == '[';
    }

    private Map<String, Boolean> getIncludesExcludesMap() {
        if (this.includesExcludesMap == null) {
            this.includesExcludesMap = new TreeMap<String, Boolean>();
            if (this.getAllowedCollection() != null) {
                for (String e : this.getAllowedCollection()) {
                    this.includesExcludesMap.put(e, Boolean.TRUE);
                }
            }
            if (this.getBlockedCollection() != null) {
                for (String b : this.getBlockedCollection()) {
                    this.includesExcludesMap.put(b, Boolean.FALSE);
                }
            }
        }
        return this.includesExcludesMap;
    }

    public boolean isDefaultBlock() {
        return this.defaultBlock;
    }

    public void setDefaultBlock(boolean defaultExclude) {
        this.defaultBlock = defaultExclude;
    }

    public Collection<String> getBlockedCollection() {
        return this.blocked;
    }

    public void setBlockedCollection(Collection<String> blocked) {
        this.blocked = blocked;
    }

    public void setBlocked(String blocked) {
        this.setBlockedCollection(this.asCollection(blocked));
    }

    public Collection<String> getAllowedCollection() {
        return this.allowed;
    }

    public void setAllowedCollection(Collection<String> allowed) {
        this.allowed = allowed;
    }

    public void setAllowed(String allowed) {
        this.setAllowedCollection(this.asCollection(allowed));
    }

    private Collection<String> asCollection(String commaDelim) {
        if (StringUtils.isBlank((CharSequence)commaDelim)) {
            return null;
        }
        return TextParseUtil.commaDelimitedStringToSet(commaDelim);
    }
}

