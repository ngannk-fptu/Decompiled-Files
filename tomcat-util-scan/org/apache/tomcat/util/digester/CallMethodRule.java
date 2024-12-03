/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.IntrospectionUtils
 */
package org.apache.tomcat.util.digester;

import java.util.Arrays;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class CallMethodRule
extends Rule {
    protected String bodyText = null;
    protected final int targetOffset;
    protected final String methodName;
    protected final int paramCount;
    protected Class<?>[] paramTypes = null;
    protected boolean useExactMatch = false;

    public CallMethodRule(String methodName, int paramCount) {
        this(0, methodName, paramCount);
    }

    public CallMethodRule(int targetOffset, String methodName, int paramCount) {
        this.targetOffset = targetOffset;
        this.methodName = methodName;
        this.paramCount = paramCount;
        if (paramCount == 0) {
            this.paramTypes = new Class[]{String.class};
        } else {
            this.paramTypes = new Class[paramCount];
            Arrays.fill(this.paramTypes, String.class);
        }
    }

    public CallMethodRule(String methodName) {
        this(0, methodName, 0, null);
    }

    public CallMethodRule(int targetOffset, String methodName, int paramCount, Class<?>[] paramTypes) {
        this.targetOffset = targetOffset;
        this.methodName = methodName;
        this.paramCount = paramCount;
        if (paramTypes == null) {
            this.paramTypes = new Class[paramCount];
            Arrays.fill(this.paramTypes, String.class);
        } else {
            this.paramTypes = new Class[paramTypes.length];
            System.arraycopy(paramTypes, 0, this.paramTypes, 0, this.paramTypes.length);
        }
    }

    public boolean getUseExactMatch() {
        return this.useExactMatch;
    }

    public void setUseExactMatch(boolean useExactMatch) {
        this.useExactMatch = useExactMatch;
    }

    @Override
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        if (this.paramCount > 0) {
            Object[] parameters = new Object[this.paramCount];
            this.digester.pushParams(parameters);
        }
    }

    @Override
    public void body(String namespace, String name, String bodyText) throws Exception {
        if (this.paramCount == 0) {
            this.bodyText = bodyText.trim().intern();
        }
    }

    @Override
    public void end(String namespace, String name) throws Exception {
        StringBuilder sb;
        Object[] parameters = null;
        if (this.paramCount > 0) {
            parameters = (Object[])this.digester.popParams();
            if (this.digester.log.isTraceEnabled()) {
                int size = parameters.length;
                for (int i = 0; i < size; ++i) {
                    this.digester.log.trace((Object)("[CallMethodRule](" + i + ")" + parameters[i]));
                }
            }
            if (this.paramCount == 1 && parameters[0] == null) {
                return;
            }
        } else if (this.paramTypes.length != 0) {
            if (this.bodyText == null) {
                return;
            }
            parameters = new Object[]{this.bodyText};
        }
        Object[] paramValues = new Object[this.paramTypes.length];
        for (int i = 0; i < this.paramTypes.length; ++i) {
            Object param = parameters[i];
            paramValues[i] = null == param && !this.paramTypes[i].isPrimitive() ? null : (param instanceof String && !String.class.isAssignableFrom(this.paramTypes[i]) ? IntrospectionUtils.convert((String)((String)parameters[i]), this.paramTypes[i]) : parameters[i]);
        }
        Object target = this.targetOffset >= 0 ? this.digester.peek(this.targetOffset) : this.digester.peek(this.digester.getCount() + this.targetOffset);
        if (target == null) {
            sb = new StringBuilder();
            sb.append("[CallMethodRule]{");
            sb.append(this.digester.match);
            sb.append("} Call target is null (");
            sb.append("targetOffset=");
            sb.append(this.targetOffset);
            sb.append(",stackdepth=");
            sb.append(this.digester.getCount());
            sb.append(')');
            throw new SAXException(sb.toString());
        }
        if (this.digester.log.isDebugEnabled()) {
            sb = new StringBuilder("[CallMethodRule]{");
            sb.append(this.digester.match);
            sb.append("} Call ");
            sb.append(target.getClass().getName());
            sb.append('.');
            sb.append(this.methodName);
            sb.append('(');
            for (int i = 0; i < paramValues.length; ++i) {
                if (i > 0) {
                    sb.append(',');
                }
                if (paramValues[i] == null) {
                    sb.append("null");
                } else {
                    sb.append(paramValues[i].toString());
                }
                sb.append('/');
                if (this.paramTypes[i] == null) {
                    sb.append("null");
                    continue;
                }
                sb.append(this.paramTypes[i].getName());
            }
            sb.append(')');
            this.digester.log.debug((Object)sb.toString());
        }
        Object result = IntrospectionUtils.callMethodN((Object)target, (String)this.methodName, (Object[])paramValues, (Class[])this.paramTypes);
        this.processMethodCallResult(result);
        StringBuilder code = this.digester.getGeneratedCode();
        if (code != null) {
            code.append(this.digester.toVariableName(target)).append('.').append(this.methodName);
            code.append('(');
            for (int i = 0; i < paramValues.length; ++i) {
                if (i > 0) {
                    code.append(", ");
                }
                if (this.bodyText != null) {
                    code.append("\"").append(IntrospectionUtils.escape((String)this.bodyText)).append("\"");
                    continue;
                }
                if (paramValues[i] instanceof String) {
                    code.append("\"").append(IntrospectionUtils.escape((String)paramValues[i].toString())).append("\"");
                    continue;
                }
                code.append(this.digester.toVariableName(paramValues[i]));
            }
            code.append(");");
            code.append(System.lineSeparator());
        }
    }

    @Override
    public void finish() throws Exception {
        this.bodyText = null;
    }

    protected void processMethodCallResult(Object result) {
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("CallMethodRule[");
        sb.append("methodName=");
        sb.append(this.methodName);
        sb.append(", paramCount=");
        sb.append(this.paramCount);
        sb.append(", paramTypes={");
        if (this.paramTypes != null) {
            for (int i = 0; i < this.paramTypes.length; ++i) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(this.paramTypes[i].getName());
            }
        }
        sb.append('}');
        sb.append(']');
        return sb.toString();
    }
}

