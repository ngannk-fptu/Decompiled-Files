/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.util.Collection;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.ModuleInfo;

public interface IndexView {
    public Collection<ClassInfo> getKnownClasses();

    public ClassInfo getClassByName(DotName var1);

    public Collection<ClassInfo> getKnownDirectSubclasses(DotName var1);

    public Collection<ClassInfo> getAllKnownSubclasses(DotName var1);

    public Collection<ClassInfo> getKnownDirectImplementors(DotName var1);

    public Collection<ClassInfo> getAllKnownImplementors(DotName var1);

    public Collection<AnnotationInstance> getAnnotations(DotName var1);

    public Collection<AnnotationInstance> getAnnotationsWithRepeatable(DotName var1, IndexView var2);

    public Collection<ModuleInfo> getKnownModules();

    public ModuleInfo getModuleByName(DotName var1);

    public Collection<ClassInfo> getKnownUsers(DotName var1);
}

