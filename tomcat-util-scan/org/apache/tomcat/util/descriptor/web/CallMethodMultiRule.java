/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.IntrospectionUtils
 */
package org.apache.tomcat.util.descriptor.web;

import java.util.ArrayList;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.digester.CallMethodRule;
import org.xml.sax.SAXException;

final class CallMethodMultiRule
extends CallMethodRule {
    final int multiParamIndex;

    CallMethodMultiRule(String methodName, int paramCount, int multiParamIndex) {
        super(methodName, paramCount);
        this.multiParamIndex = multiParamIndex;
    }

    @Override
    public void end(String namespace, String name) throws Exception {
        Object[] parameters = null;
        if (this.paramCount > 0) {
            parameters = (Object[])this.digester.popParams();
        } else {
            parameters = new Object[]{};
            super.end(namespace, name);
        }
        ArrayList multiParams = (ArrayList)parameters[this.multiParamIndex];
        Object[] paramValues = new Object[this.paramTypes.length];
        for (int i = 0; i < this.paramTypes.length; ++i) {
            if (i == this.multiParamIndex) continue;
            paramValues[i] = parameters[i] == null || parameters[i] instanceof String && !String.class.isAssignableFrom(this.paramTypes[i]) ? IntrospectionUtils.convert((String)((String)parameters[i]), (Class)this.paramTypes[i]) : parameters[i];
        }
        Object target = this.targetOffset >= 0 ? this.digester.peek(this.targetOffset) : this.digester.peek(this.digester.getCount() + this.targetOffset);
        if (target == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("[CallMethodRule]{");
            sb.append("");
            sb.append("} Call target is null (");
            sb.append("targetOffset=");
            sb.append(this.targetOffset);
            sb.append(",stackdepth=");
            sb.append(this.digester.getCount());
            sb.append(')');
            throw new SAXException(sb.toString());
        }
        if (multiParams == null) {
            paramValues[this.multiParamIndex] = null;
            IntrospectionUtils.callMethodN((Object)target, (String)this.methodName, (Object[])paramValues, (Class[])this.paramTypes);
            return;
        }
        for (Object param : multiParams) {
            paramValues[this.multiParamIndex] = param == null || param instanceof String && !String.class.isAssignableFrom(this.paramTypes[this.multiParamIndex]) ? IntrospectionUtils.convert((String)((String)param), (Class)this.paramTypes[this.multiParamIndex]) : param;
            IntrospectionUtils.callMethodN((Object)target, (String)this.methodName, (Object[])paramValues, (Class[])this.paramTypes);
            StringBuilder code = this.digester.getGeneratedCode();
            if (code == null) continue;
            code.append(this.digester.toVariableName(target)).append('.').append(this.methodName);
            code.append('(');
            for (int i = 0; i < paramValues.length; ++i) {
                if (i > 0) {
                    code.append(", ");
                }
                if (paramValues[i] instanceof String) {
                    code.append("\"").append(paramValues[i].toString()).append("\"");
                    continue;
                }
                code.append(this.digester.toVariableName(paramValues[i]));
            }
            code.append(");");
            code.append(System.lineSeparator());
        }
    }
}

