/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  freemarker.cache.URLTemplateLoader
 */
package org.apache.struts2.views.freemarker;

import com.opensymphony.xwork2.util.ClassLoaderUtil;
import freemarker.cache.URLTemplateLoader;
import java.net.URL;

public class StrutsClassTemplateLoader
extends URLTemplateLoader {
    protected URL getURL(String name) {
        return ClassLoaderUtil.getResource(name, ((Object)((Object)this)).getClass());
    }
}

