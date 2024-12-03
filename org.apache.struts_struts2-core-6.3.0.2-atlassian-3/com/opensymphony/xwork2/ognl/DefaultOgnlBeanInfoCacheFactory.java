/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.EnumUtils
 */
package com.opensymphony.xwork2.ognl;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.BeanInfoCacheFactory;
import com.opensymphony.xwork2.ognl.DefaultOgnlCacheFactory;
import com.opensymphony.xwork2.ognl.OgnlCacheFactory;
import org.apache.commons.lang3.EnumUtils;

public class DefaultOgnlBeanInfoCacheFactory<Key, Value>
extends DefaultOgnlCacheFactory<Key, Value>
implements BeanInfoCacheFactory<Key, Value> {
    @Deprecated
    public DefaultOgnlBeanInfoCacheFactory() {
    }

    @Inject
    public DefaultOgnlBeanInfoCacheFactory(@Inject(value="struts.ognl.beanInfoCacheMaxSize") String cacheMaxSize, @Inject(value="struts.ognl.beanInfoCacheType") String defaultCacheType) {
        super(Integer.parseInt(cacheMaxSize), (OgnlCacheFactory.CacheType)EnumUtils.getEnumIgnoreCase(OgnlCacheFactory.CacheType.class, (String)defaultCacheType));
    }
}

