/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.descriptor.web;

import java.util.List;
import org.apache.tomcat.util.descriptor.web.InjectionTarget;

public interface Injectable {
    public String getName();

    public void addInjectionTarget(String var1, String var2);

    public List<InjectionTarget> getInjectionTargets();
}

