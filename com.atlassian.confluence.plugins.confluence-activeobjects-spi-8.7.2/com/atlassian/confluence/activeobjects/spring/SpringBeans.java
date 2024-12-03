/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.confluence.activeobjects.spring;

import com.atlassian.confluence.activeobjects.spring.ExportBeans;
import com.atlassian.confluence.activeobjects.spring.ImportBeans;
import com.atlassian.confluence.activeobjects.spring.PluginBeans;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value={ImportBeans.class, PluginBeans.class, ExportBeans.class})
public class SpringBeans {
}

