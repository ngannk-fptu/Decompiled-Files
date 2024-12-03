/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.descriptor.web;

import java.util.ArrayList;
import org.apache.tomcat.util.digester.CallParamRule;

final class CallParamMultiRule
extends CallParamRule {
    CallParamMultiRule(int paramIndex) {
        super(paramIndex);
    }

    @Override
    public void end(String namespace, String name) {
        if (this.bodyTextStack != null && !this.bodyTextStack.empty()) {
            Object[] parameters = (Object[])this.digester.peekParams();
            ArrayList<String> params = (ArrayList<String>)parameters[this.paramIndex];
            if (params == null) {
                parameters[this.paramIndex] = params = new ArrayList<String>();
            }
            params.add((String)this.bodyTextStack.pop());
        }
    }
}

