/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.opensymphony.module.sitemesh.mapper;

import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.DecoratorMapper;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.mapper.AbstractDecoratorMapper;
import com.opensymphony.module.sitemesh.mapper.DefaultDecorator;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;

public class LanguageDecoratorMapper
extends AbstractDecoratorMapper {
    private Map map = null;

    public void init(Config config, Properties properties, DecoratorMapper parent) throws InstantiationException {
        super.init(config, properties, parent);
        this.map = new HashMap();
        this.initMap(properties);
    }

    public Decorator getDecorator(HttpServletRequest request, Page page) {
        try {
            Decorator result = null;
            final Decorator d = super.getDecorator(request, page);
            String path = LanguageDecoratorMapper.modifyPath(d.getPage(), this.getExt(request.getHeader("Accept-Language")));
            File decFile = new File(this.config.getServletContext().getRealPath(path));
            if (decFile.isFile()) {
                result = new DefaultDecorator(d.getName(), path, null){

                    public String getInitParameter(String paramName) {
                        return d.getInitParameter(paramName);
                    }
                };
            }
            return result == null ? super.getDecorator(request, page) : result;
        }
        catch (NullPointerException e) {
            return super.getDecorator(request, page);
        }
    }

    private String getExt(String acceptLanguage) {
        for (Map.Entry entry : this.map.entrySet()) {
            if (!acceptLanguage.substring(0, 2).equals(entry.getKey())) continue;
            return (String)entry.getValue();
        }
        return null;
    }

    private static String modifyPath(String path, String ext) {
        int dot = path.indexOf(46);
        if (dot > -1) {
            return path.substring(0, dot) + '-' + ext + path.substring(dot);
        }
        return path + '-' + ext;
    }

    private void initMap(Properties props) {
        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            String key = (String)entry.getKey();
            if (!key.startsWith("match.")) continue;
            String match = key.substring(6);
            this.map.put(match, entry.getValue());
        }
    }
}

