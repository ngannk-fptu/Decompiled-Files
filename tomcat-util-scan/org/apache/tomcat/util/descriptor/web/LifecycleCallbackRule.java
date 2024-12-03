/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.descriptor.web.WebRuleSet;
import org.apache.tomcat.util.descriptor.web.WebXml;
import org.apache.tomcat.util.digester.CallMethodRule;

final class LifecycleCallbackRule
extends CallMethodRule {
    private final boolean postConstruct;

    LifecycleCallbackRule(String methodName, int paramCount, boolean postConstruct) {
        super(methodName, paramCount);
        this.postConstruct = postConstruct;
    }

    @Override
    public void end(String namespace, String name) throws Exception {
        Object[] params = (Object[])this.digester.peekParams();
        if (params != null && params.length == 2) {
            WebXml webXml = (WebXml)this.digester.peek();
            if (this.postConstruct) {
                if (webXml.getPostConstructMethods().containsKey(params[0])) {
                    throw new IllegalArgumentException(WebRuleSet.sm.getString("webRuleSet.postconstruct.duplicate", new Object[]{params[0]}));
                }
            } else if (webXml.getPreDestroyMethods().containsKey(params[0])) {
                throw new IllegalArgumentException(WebRuleSet.sm.getString("webRuleSet.predestroy.duplicate", new Object[]{params[0]}));
            }
        }
        super.end(namespace, name);
    }
}

