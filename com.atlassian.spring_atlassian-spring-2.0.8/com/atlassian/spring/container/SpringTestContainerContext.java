/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeansException
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.support.FileSystemXmlApplicationContext
 */
package com.atlassian.spring.container;

import com.atlassian.spring.container.SpringContainerContext;
import java.io.IOException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class SpringTestContainerContext
extends SpringContainerContext {
    public static final String[] DEFAULT_CONFIG_FILES = new String[0];
    public String[] userConfigFiles;

    public SpringTestContainerContext() throws BeansException, IOException {
        this.configure(this.getDefaultConfigFiles());
    }

    protected String[] getDefaultConfigFiles() {
        return DEFAULT_CONFIG_FILES;
    }

    public SpringTestContainerContext(ApplicationContext context) {
        this.setApplicationContext(context);
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return super.getApplicationContext();
    }

    @Override
    public void refresh() {
        try {
            if (this.userConfigFiles != null) {
                this.configure(this.userConfigFiles);
            } else {
                this.configure(this.getDefaultConfigFiles());
            }
            this.contextReloaded();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void configure(String[] files) throws BeansException, IOException {
        this.userConfigFiles = files;
        this.setApplicationContext((ApplicationContext)new FileSystemXmlApplicationContext(files));
    }
}

