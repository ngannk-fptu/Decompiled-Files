/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.ext.beans.ClassIntrospector;
import freemarker.ext.beans.DefaultMemberAccessPolicy;
import freemarker.ext.beans.MemberAccessPolicy;
import freemarker.ext.beans.MethodAppearanceFineTuner;
import freemarker.ext.beans.MethodSorter;
import freemarker.ext.beans.SingletonCustomizer;
import freemarker.template.Configuration;
import freemarker.template.Version;
import freemarker.template._TemplateAPI;
import freemarker.template._VersionInts;
import freemarker.template.utility.NullArgumentException;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

final class ClassIntrospectorBuilder
implements Cloneable {
    private static final Map<ClassIntrospectorBuilder, Reference<ClassIntrospector>> INSTANCE_CACHE = new HashMap<ClassIntrospectorBuilder, Reference<ClassIntrospector>>();
    private static final ReferenceQueue<ClassIntrospector> INSTANCE_CACHE_REF_QUEUE = new ReferenceQueue();
    private final Version incompatibleImprovements;
    private int exposureLevel = 1;
    private boolean exposeFields;
    private MemberAccessPolicy memberAccessPolicy;
    private boolean treatDefaultMethodsAsBeanMembers;
    private MethodAppearanceFineTuner methodAppearanceFineTuner;
    private MethodSorter methodSorter;

    ClassIntrospectorBuilder(ClassIntrospector ci) {
        this.incompatibleImprovements = ci.incompatibleImprovements;
        this.exposureLevel = ci.exposureLevel;
        this.exposeFields = ci.exposeFields;
        this.memberAccessPolicy = ci.memberAccessPolicy;
        this.treatDefaultMethodsAsBeanMembers = ci.treatDefaultMethodsAsBeanMembers;
        this.methodAppearanceFineTuner = ci.methodAppearanceFineTuner;
        this.methodSorter = ci.methodSorter;
    }

    ClassIntrospectorBuilder(Version incompatibleImprovements) {
        this.incompatibleImprovements = ClassIntrospectorBuilder.normalizeIncompatibleImprovementsVersion(incompatibleImprovements);
        this.treatDefaultMethodsAsBeanMembers = incompatibleImprovements.intValue() >= _VersionInts.V_2_3_26;
        this.memberAccessPolicy = DefaultMemberAccessPolicy.getInstance(this.incompatibleImprovements);
    }

    private static Version normalizeIncompatibleImprovementsVersion(Version incompatibleImprovements) {
        _TemplateAPI.checkVersionNotNullAndSupported(incompatibleImprovements);
        return incompatibleImprovements.intValue() >= _VersionInts.V_2_3_30 ? Configuration.VERSION_2_3_30 : (incompatibleImprovements.intValue() >= _VersionInts.V_2_3_21 ? Configuration.VERSION_2_3_21 : Configuration.VERSION_2_3_0);
    }

    protected Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException("Failed to clone ClassIntrospectorBuilder", e);
        }
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + this.incompatibleImprovements.hashCode();
        result = 31 * result + (this.exposeFields ? 1231 : 1237);
        result = 31 * result + (this.treatDefaultMethodsAsBeanMembers ? 1231 : 1237);
        result = 31 * result + this.exposureLevel;
        result = 31 * result + this.memberAccessPolicy.hashCode();
        result = 31 * result + System.identityHashCode(this.methodAppearanceFineTuner);
        result = 31 * result + System.identityHashCode(this.methodSorter);
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ClassIntrospectorBuilder other = (ClassIntrospectorBuilder)obj;
        if (!this.incompatibleImprovements.equals(other.incompatibleImprovements)) {
            return false;
        }
        if (this.exposeFields != other.exposeFields) {
            return false;
        }
        if (this.treatDefaultMethodsAsBeanMembers != other.treatDefaultMethodsAsBeanMembers) {
            return false;
        }
        if (this.exposureLevel != other.exposureLevel) {
            return false;
        }
        if (!this.memberAccessPolicy.equals(other.memberAccessPolicy)) {
            return false;
        }
        if (this.methodAppearanceFineTuner != other.methodAppearanceFineTuner) {
            return false;
        }
        return this.methodSorter == other.methodSorter;
    }

    public int getExposureLevel() {
        return this.exposureLevel;
    }

    public void setExposureLevel(int exposureLevel) {
        if (exposureLevel < 0 || exposureLevel > 3) {
            throw new IllegalArgumentException("Illegal exposure level: " + exposureLevel);
        }
        this.exposureLevel = exposureLevel;
    }

    public boolean getExposeFields() {
        return this.exposeFields;
    }

    public void setExposeFields(boolean exposeFields) {
        this.exposeFields = exposeFields;
    }

    public boolean getTreatDefaultMethodsAsBeanMembers() {
        return this.treatDefaultMethodsAsBeanMembers;
    }

    public void setTreatDefaultMethodsAsBeanMembers(boolean treatDefaultMethodsAsBeanMembers) {
        this.treatDefaultMethodsAsBeanMembers = treatDefaultMethodsAsBeanMembers;
    }

    public MemberAccessPolicy getMemberAccessPolicy() {
        return this.memberAccessPolicy;
    }

    public void setMemberAccessPolicy(MemberAccessPolicy memberAccessPolicy) {
        NullArgumentException.check(memberAccessPolicy);
        this.memberAccessPolicy = memberAccessPolicy;
    }

    public MethodAppearanceFineTuner getMethodAppearanceFineTuner() {
        return this.methodAppearanceFineTuner;
    }

    public void setMethodAppearanceFineTuner(MethodAppearanceFineTuner methodAppearanceFineTuner) {
        this.methodAppearanceFineTuner = methodAppearanceFineTuner;
    }

    public MethodSorter getMethodSorter() {
        return this.methodSorter;
    }

    public void setMethodSorter(MethodSorter methodSorter) {
        this.methodSorter = methodSorter;
    }

    public Version getIncompatibleImprovements() {
        return this.incompatibleImprovements;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void removeClearedReferencesFromInstanceCache() {
        Reference<ClassIntrospector> clearedRef;
        while ((clearedRef = INSTANCE_CACHE_REF_QUEUE.poll()) != null) {
            Map<ClassIntrospectorBuilder, Reference<ClassIntrospector>> map = INSTANCE_CACHE;
            synchronized (map) {
                Iterator<Reference<ClassIntrospector>> it = INSTANCE_CACHE.values().iterator();
                while (it.hasNext()) {
                    if (it.next() != clearedRef) continue;
                    it.remove();
                    break;
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void clearInstanceCache() {
        Map<ClassIntrospectorBuilder, Reference<ClassIntrospector>> map = INSTANCE_CACHE;
        synchronized (map) {
            INSTANCE_CACHE.clear();
        }
    }

    static Map<ClassIntrospectorBuilder, Reference<ClassIntrospector>> getInstanceCache() {
        return INSTANCE_CACHE;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    ClassIntrospector build() {
        if ((this.methodAppearanceFineTuner == null || this.methodAppearanceFineTuner instanceof SingletonCustomizer) && (this.methodSorter == null || this.methodSorter instanceof SingletonCustomizer)) {
            ClassIntrospector instance;
            Map<ClassIntrospectorBuilder, Reference<ClassIntrospector>> map = INSTANCE_CACHE;
            synchronized (map) {
                Reference<ClassIntrospector> instanceRef = INSTANCE_CACHE.get(this);
                ClassIntrospector classIntrospector = instance = instanceRef != null ? instanceRef.get() : null;
                if (instance == null) {
                    ClassIntrospectorBuilder thisClone = (ClassIntrospectorBuilder)this.clone();
                    instance = new ClassIntrospector(thisClone, new Object(), true, true);
                    INSTANCE_CACHE.put(thisClone, new WeakReference<ClassIntrospector>(instance, INSTANCE_CACHE_REF_QUEUE));
                }
            }
            ClassIntrospectorBuilder.removeClearedReferencesFromInstanceCache();
            return instance;
        }
        return new ClassIntrospector(this, new Object(), true, false);
    }
}

