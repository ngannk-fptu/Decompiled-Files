/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.beanutils.ConvertUtils
 *  org.apache.commons.beanutils.MethodUtils
 */
package org.apache.commons.digester;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CallMethodRule
extends Rule {
    protected String bodyText = null;
    protected int targetOffset = 0;
    protected String methodName = null;
    protected int paramCount = 0;
    protected Class<?>[] paramTypes = null;
    private String[] paramClassNames = null;
    protected boolean useExactMatch = false;

    @Deprecated
    public CallMethodRule(Digester digester, String methodName, int paramCount) {
        this(methodName, paramCount);
    }

    @Deprecated
    public CallMethodRule(Digester digester, String methodName, int paramCount, String[] paramTypes) {
        this(methodName, paramCount, paramTypes);
    }

    @Deprecated
    public CallMethodRule(Digester digester, String methodName, int paramCount, Class<?>[] paramTypes) {
        this(methodName, paramCount, paramTypes);
    }

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
            for (int i = 0; i < this.paramTypes.length; ++i) {
                this.paramTypes[i] = String.class;
            }
        }
    }

    public CallMethodRule(String methodName) {
        this(0, methodName, 0, (Class[])null);
    }

    public CallMethodRule(int targetOffset, String methodName) {
        this(targetOffset, methodName, 0, (Class[])null);
    }

    public CallMethodRule(String methodName, int paramCount, String[] paramTypes) {
        this(0, methodName, paramCount, paramTypes);
    }

    public CallMethodRule(int targetOffset, String methodName, int paramCount, String[] paramTypes) {
        this.targetOffset = targetOffset;
        this.methodName = methodName;
        this.paramCount = paramCount;
        if (paramTypes == null) {
            this.paramTypes = new Class[paramCount];
            for (int i = 0; i < this.paramTypes.length; ++i) {
                this.paramTypes[i] = String.class;
            }
        } else {
            this.paramClassNames = new String[paramTypes.length];
            for (int i = 0; i < this.paramClassNames.length; ++i) {
                this.paramClassNames[i] = paramTypes[i];
            }
        }
    }

    public CallMethodRule(String methodName, int paramCount, Class<?>[] paramTypes) {
        this(0, methodName, paramCount, paramTypes);
    }

    public CallMethodRule(int targetOffset, String methodName, int paramCount, Class<?>[] paramTypes) {
        this.targetOffset = targetOffset;
        this.methodName = methodName;
        this.paramCount = paramCount;
        if (paramTypes == null) {
            this.paramTypes = new Class[paramCount];
            for (int i = 0; i < this.paramTypes.length; ++i) {
                this.paramTypes[i] = String.class;
            }
        } else {
            this.paramTypes = new Class[paramTypes.length];
            for (int i = 0; i < this.paramTypes.length; ++i) {
                this.paramTypes[i] = paramTypes[i];
            }
        }
    }

    public boolean getUseExactMatch() {
        return this.useExactMatch;
    }

    public void setUseExactMatch(boolean useExactMatch) {
        this.useExactMatch = useExactMatch;
    }

    @Override
    public void setDigester(Digester digester) {
        super.setDigester(digester);
        if (this.paramClassNames != null) {
            this.paramTypes = new Class[this.paramClassNames.length];
            for (int i = 0; i < this.paramClassNames.length; ++i) {
                try {
                    this.paramTypes[i] = digester.getClassLoader().loadClass(this.paramClassNames[i]);
                    continue;
                }
                catch (ClassNotFoundException e) {
                    digester.getLogger().error((Object)("(CallMethodRule) Cannot load class " + this.paramClassNames[i]), (Throwable)e);
                    this.paramTypes[i] = null;
                }
            }
        }
    }

    @Override
    public void begin(Attributes attributes) throws Exception {
        if (this.paramCount > 0) {
            Object[] parameters = new Object[this.paramCount];
            for (int i = 0; i < parameters.length; ++i) {
                parameters[i] = null;
            }
            this.digester.pushParams(parameters);
        }
    }

    @Override
    public void body(String bodyText) throws Exception {
        if (this.paramCount == 0) {
            this.bodyText = bodyText.trim();
        }
    }

    @Override
    public void end() throws Exception {
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
        } else if (this.paramTypes != null && this.paramTypes.length != 0) {
            if (this.bodyText == null) {
                return;
            }
            parameters = new Object[]{this.bodyText};
            if (this.paramTypes.length == 0) {
                this.paramTypes = new Class[1];
                this.paramTypes[0] = String.class;
            }
        }
        Object[] paramValues = new Object[this.paramTypes.length];
        for (int i = 0; i < this.paramTypes.length; ++i) {
            paramValues[i] = parameters[i] == null || parameters[i] instanceof String && !String.class.isAssignableFrom(this.paramTypes[i]) ? ConvertUtils.convert((String)((String)parameters[i]), this.paramTypes[i]) : parameters[i];
        }
        Object target = this.targetOffset >= 0 ? this.digester.peek(this.targetOffset) : this.digester.peek(this.digester.getCount() + this.targetOffset);
        if (target == null) {
            StringBuffer sb = new StringBuffer();
            sb.append("[CallMethodRule]{");
            sb.append(this.digester.match);
            sb.append("} Call target is null (");
            sb.append("targetOffset=");
            sb.append(this.targetOffset);
            sb.append(",stackdepth=");
            sb.append(this.digester.getCount());
            sb.append(")");
            throw new SAXException(sb.toString());
        }
        if (this.digester.log.isDebugEnabled()) {
            StringBuffer sb = new StringBuffer("[CallMethodRule]{");
            sb.append(this.digester.match);
            sb.append("} Call ");
            sb.append(target.getClass().getName());
            sb.append(".");
            sb.append(this.methodName);
            sb.append("(");
            for (int i = 0; i < paramValues.length; ++i) {
                if (i > 0) {
                    sb.append(",");
                }
                if (paramValues[i] == null) {
                    sb.append("null");
                } else {
                    sb.append(paramValues[i].toString());
                }
                sb.append("/");
                if (this.paramTypes[i] == null) {
                    sb.append("null");
                    continue;
                }
                sb.append(this.paramTypes[i].getName());
            }
            sb.append(")");
            this.digester.log.debug((Object)sb.toString());
        }
        Object result = null;
        result = this.useExactMatch ? MethodUtils.invokeExactMethod((Object)target, (String)this.methodName, (Object[])paramValues, (Class[])this.paramTypes) : MethodUtils.invokeMethod((Object)target, (String)this.methodName, (Object[])paramValues, (Class[])this.paramTypes);
        this.processMethodCallResult(result);
    }

    @Override
    public void finish() throws Exception {
        this.bodyText = null;
    }

    protected void processMethodCallResult(Object result) {
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("CallMethodRule[");
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
        sb.append("}");
        sb.append("]");
        return sb.toString();
    }
}

