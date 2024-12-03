/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.xml.BeanDefinitionParser
 *  org.springframework.beans.factory.xml.NamespaceHandlerSupport
 */
package org.springframework.scripting.config;

import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.scripting.config.ScriptBeanDefinitionParser;
import org.springframework.scripting.config.ScriptingDefaultsParser;

public class LangNamespaceHandler
extends NamespaceHandlerSupport {
    public void init() {
        this.registerScriptBeanDefinitionParser("groovy", "org.springframework.scripting.groovy.GroovyScriptFactory");
        this.registerScriptBeanDefinitionParser("bsh", "org.springframework.scripting.bsh.BshScriptFactory");
        this.registerScriptBeanDefinitionParser("std", "org.springframework.scripting.support.StandardScriptFactory");
        this.registerBeanDefinitionParser("defaults", new ScriptingDefaultsParser());
    }

    private void registerScriptBeanDefinitionParser(String key, String scriptFactoryClassName) {
        this.registerBeanDefinitionParser(key, (BeanDefinitionParser)new ScriptBeanDefinitionParser(scriptFactoryClassName));
    }
}

