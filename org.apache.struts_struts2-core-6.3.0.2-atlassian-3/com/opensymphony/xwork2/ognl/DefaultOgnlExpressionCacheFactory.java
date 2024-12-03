/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.EnumUtils
 */
package com.opensymphony.xwork2.ognl;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.DefaultOgnlCacheFactory;
import com.opensymphony.xwork2.ognl.ExpressionCacheFactory;
import com.opensymphony.xwork2.ognl.OgnlCacheFactory;
import org.apache.commons.lang3.EnumUtils;

public class DefaultOgnlExpressionCacheFactory<Key, Value>
extends DefaultOgnlCacheFactory<Key, Value>
implements ExpressionCacheFactory<Key, Value> {
    @Deprecated
    public DefaultOgnlExpressionCacheFactory() {
    }

    @Inject
    public DefaultOgnlExpressionCacheFactory(@Inject(value="struts.ognl.expressionCacheMaxSize") String cacheMaxSize, @Inject(value="struts.ognl.expressionCacheType") String defaultCacheType) {
        super(Integer.parseInt(cacheMaxSize), (OgnlCacheFactory.CacheType)EnumUtils.getEnumIgnoreCase(OgnlCacheFactory.CacheType.class, (String)defaultCacheType));
    }
}

