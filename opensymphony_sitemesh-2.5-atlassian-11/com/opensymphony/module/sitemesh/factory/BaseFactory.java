/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.factory;

import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.DecoratorMapper;
import com.opensymphony.module.sitemesh.Factory;
import com.opensymphony.module.sitemesh.PageParser;
import com.opensymphony.module.sitemesh.factory.FactoryException;
import com.opensymphony.module.sitemesh.mapper.PathMapper;
import com.opensymphony.module.sitemesh.util.ClassLoaderUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class BaseFactory
extends Factory {
    protected Config config = null;
    protected DecoratorMapper decoratorMapper = null;
    protected Map pageParsers = null;
    protected PathMapper excludeUrls = null;

    protected BaseFactory(Config config) {
        this.config = config;
        this.clearDecoratorMappers();
        this.clearParserMappings();
        this.clearExcludeUrls();
    }

    public DecoratorMapper getDecoratorMapper() {
        return this.decoratorMapper;
    }

    public PageParser getPageParser(String contentType) {
        return (PageParser)this.pageParsers.get(contentType);
    }

    public boolean shouldParsePage(String contentType) {
        return this.pageParsers.containsKey(contentType);
    }

    public boolean isPathExcluded(String path) {
        return this.excludeUrls.get(path) != null;
    }

    protected void clearDecoratorMappers() {
        this.decoratorMapper = null;
    }

    protected void pushDecoratorMapper(String className, Properties properties) {
        try {
            Class decoratorMapperClass = ClassLoaderUtil.loadClass(className, this.getClass());
            DecoratorMapper newMapper = this.getDecoratorMapper(decoratorMapperClass);
            newMapper.init(this.config, properties, this.decoratorMapper);
            this.decoratorMapper = newMapper;
        }
        catch (ClassNotFoundException e) {
            throw new FactoryException("Could not load DecoratorMapper class : " + className, e);
        }
        catch (Exception e) {
            throw new FactoryException("Could not initialize DecoratorMapper : " + className, e);
        }
    }

    protected DecoratorMapper getDecoratorMapper(Class decoratorMapperClass) throws InstantiationException, IllegalAccessException {
        return (DecoratorMapper)decoratorMapperClass.newInstance();
    }

    protected void clearParserMappings() {
        this.pageParsers = new HashMap();
    }

    protected void mapParser(String contentType, String className) {
        if (className.endsWith(".DefaultPageParser")) {
            return;
        }
        try {
            PageParser pp = (PageParser)ClassLoaderUtil.loadClass(className, this.getClass()).newInstance();
            this.pageParsers.put(contentType, pp);
        }
        catch (ClassNotFoundException e) {
            throw new FactoryException("Could not load PageParser class : " + className, e);
        }
        catch (Exception e) {
            throw new FactoryException("Could not instantiate PageParser : " + className, e);
        }
    }

    protected void addExcludeUrl(String path) {
        this.excludeUrls.put("", path);
    }

    protected void clearExcludeUrls() {
        this.excludeUrls = new PathMapper();
    }
}

