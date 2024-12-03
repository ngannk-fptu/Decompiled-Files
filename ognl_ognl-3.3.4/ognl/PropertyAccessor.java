/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.util.Map;
import ognl.OgnlContext;
import ognl.OgnlException;

public interface PropertyAccessor {
    public Object getProperty(Map var1, Object var2, Object var3) throws OgnlException;

    public void setProperty(Map var1, Object var2, Object var3, Object var4) throws OgnlException;

    public String getSourceAccessor(OgnlContext var1, Object var2, Object var3);

    public String getSourceSetter(OgnlContext var1, Object var2, Object var3);
}

