/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Ordering
 *  javax.annotation.Nullable
 *  org.dom4j.Element
 *  org.osgi.framework.Version
 */
package com.atlassian.confluence.notifications.impl;

import com.atlassian.confluence.notifications.impl.VersionedResourceCompilation;
import com.atlassian.confluence.notifications.impl.VersionedResourceContext;
import com.atlassian.confluence.notifications.impl.VersionedResourceNode;
import com.atlassian.plugin.ModuleCompleteKey;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import java.util.LinkedHashMap;
import javax.annotation.Nullable;
import org.dom4j.Element;
import org.osgi.framework.Version;

public class VersionedResourceRoot
extends VersionedResourceNode {
    private static final Ordering EXTENDS_VERSION_ORDERING = new Ordering<Element>(){

        public int compare(@Nullable Element leftCompilationDescriptor, @Nullable Element rightCompilationDescriptor) {
            Version leftExtendsVersion = VersionedResourceRoot.parseExtendsVersion(leftCompilationDescriptor);
            Version rightExtendsVersion = VersionedResourceRoot.parseExtendsVersion(leftCompilationDescriptor);
            if (leftExtendsVersion == null && rightExtendsVersion == null) {
                return 0;
            }
            if (leftExtendsVersion == null) {
                return -1;
            }
            if (rightExtendsVersion == null) {
                return 1;
            }
            return leftExtendsVersion.compareTo(rightExtendsVersion);
        }
    };
    protected final String key;
    protected final Iterable<VersionedResourceCompilation> compilations;

    protected VersionedResourceRoot(Element rootDescriptor, VersionedResourceContext context) {
        super(rootDescriptor, context);
        this.key = rootDescriptor.attributeValue("key");
        Preconditions.checkNotNull((Object)this.key, (String)"Descriptor [%s] is missing a key attribute.", (Object)rootDescriptor);
        LinkedHashMap<Version, VersionedResourceCompilation> compilationsMap = new LinkedHashMap<Version, VersionedResourceCompilation>();
        ImmutableList compilationDescriptors = EXTENDS_VERSION_ORDERING.immutableSortedCopy(VersionedResourceRoot.children(rootDescriptor, "compilation"));
        for (Element compilationDescriptor : compilationDescriptors) {
            VersionedResourceCompilation compilation;
            Version extendsVersion = VersionedResourceRoot.parseExtendsVersion(compilationDescriptor);
            if (extendsVersion == null) {
                compilation = new VersionedResourceCompilation(compilationDescriptor, context, this);
            } else {
                VersionedResourceCompilation extendsCompilation = (VersionedResourceCompilation)compilationsMap.get(extendsVersion);
                Preconditions.checkNotNull((Object)extendsCompilation, (String)"Could not find a compilation with version [%s] to extend compilation [%s] under descriptor [%s].", (Object)extendsVersion, (Object)compilationDescriptor, (Object)rootDescriptor);
                compilation = new VersionedResourceCompilation(compilationDescriptor, context, this, extendsCompilation);
            }
            Preconditions.checkArgument((compilationsMap.get(compilation.version) == null ? 1 : 0) != 0, (String)"Found redundant compilation [%s] with version [%s] under descriptor [%s].", (Object)compilationDescriptor, (Object)compilation.version, (Object)rootDescriptor);
            compilationsMap.put(compilation.version, compilation);
        }
        if (compilationsMap.size() == 0) {
            VersionedResourceCompilation rootCompilation = new VersionedResourceCompilation(rootDescriptor, context, this);
            compilationsMap.put(rootCompilation.version, rootCompilation);
        }
        this.compilations = ImmutableList.copyOf(compilationsMap.values());
    }

    private static Version parseExtendsVersion(Element element) {
        String extendsVersionString = element.attributeValue("extends-version");
        if (extendsVersionString == null) {
            return null;
        }
        return Version.parseVersion((String)extendsVersionString);
    }

    @Override
    public ModuleCompleteKey key() {
        return new ModuleCompleteKey(this.context.getPlugin().getKey(), this.key);
    }

    @Override
    public String name() {
        return this.key().getModuleKey();
    }

    public Iterable<VersionedResourceCompilation> compilations() {
        return this.compilations;
    }
}

