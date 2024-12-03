/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.ClassIntrospectorBuilder;
import freemarker.ext.beans.MemberAccessPolicy;
import freemarker.ext.beans.MethodAppearanceFineTuner;
import freemarker.ext.beans.MethodSorter;
import freemarker.template.ObjectWrapper;
import freemarker.template.Version;
import freemarker.template._TemplateAPI;
import freemarker.template._VersionInts;

public abstract class BeansWrapperConfiguration
implements Cloneable {
    private final Version incompatibleImprovements;
    private ClassIntrospectorBuilder classIntrospectorBuilder;
    private boolean simpleMapWrapper = false;
    private boolean preferIndexedReadMethod;
    private int defaultDateType = 0;
    private ObjectWrapper outerIdentity = null;
    private boolean strict = false;
    private boolean useModelCache = false;

    protected BeansWrapperConfiguration(Version incompatibleImprovements, boolean isIncompImprsAlreadyNormalized) {
        _TemplateAPI.checkVersionNotNullAndSupported(incompatibleImprovements);
        if (!isIncompImprsAlreadyNormalized) {
            _TemplateAPI.checkCurrentVersionNotRecycled(incompatibleImprovements, "freemarker.beans", "BeansWrapper");
        }
        this.incompatibleImprovements = incompatibleImprovements = isIncompImprsAlreadyNormalized ? incompatibleImprovements : BeansWrapper.normalizeIncompatibleImprovementsVersion(incompatibleImprovements);
        this.preferIndexedReadMethod = incompatibleImprovements.intValue() < _VersionInts.V_2_3_27;
        this.classIntrospectorBuilder = new ClassIntrospectorBuilder(incompatibleImprovements);
    }

    protected BeansWrapperConfiguration(Version incompatibleImprovements) {
        this(incompatibleImprovements, false);
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + this.incompatibleImprovements.hashCode();
        result = 31 * result + (this.simpleMapWrapper ? 1231 : 1237);
        result = 31 * result + (this.preferIndexedReadMethod ? 1231 : 1237);
        result = 31 * result + this.defaultDateType;
        result = 31 * result + (this.outerIdentity != null ? this.outerIdentity.hashCode() : 0);
        result = 31 * result + (this.strict ? 1231 : 1237);
        result = 31 * result + (this.useModelCache ? 1231 : 1237);
        result = 31 * result + this.classIntrospectorBuilder.hashCode();
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
        BeansWrapperConfiguration other = (BeansWrapperConfiguration)obj;
        if (!this.incompatibleImprovements.equals(other.incompatibleImprovements)) {
            return false;
        }
        if (this.simpleMapWrapper != other.simpleMapWrapper) {
            return false;
        }
        if (this.preferIndexedReadMethod != other.preferIndexedReadMethod) {
            return false;
        }
        if (this.defaultDateType != other.defaultDateType) {
            return false;
        }
        if (this.outerIdentity != other.outerIdentity) {
            return false;
        }
        if (this.strict != other.strict) {
            return false;
        }
        if (this.useModelCache != other.useModelCache) {
            return false;
        }
        return this.classIntrospectorBuilder.equals(other.classIntrospectorBuilder);
    }

    protected Object clone(boolean deepCloneKey) {
        try {
            BeansWrapperConfiguration clone = (BeansWrapperConfiguration)super.clone();
            if (deepCloneKey) {
                clone.classIntrospectorBuilder = (ClassIntrospectorBuilder)this.classIntrospectorBuilder.clone();
            }
            return clone;
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException("Failed to clone BeansWrapperConfiguration", e);
        }
    }

    public boolean isSimpleMapWrapper() {
        return this.simpleMapWrapper;
    }

    public void setSimpleMapWrapper(boolean simpleMapWrapper) {
        this.simpleMapWrapper = simpleMapWrapper;
    }

    public boolean getPreferIndexedReadMethod() {
        return this.preferIndexedReadMethod;
    }

    public void setPreferIndexedReadMethod(boolean preferIndexedReadMethod) {
        this.preferIndexedReadMethod = preferIndexedReadMethod;
    }

    public int getDefaultDateType() {
        return this.defaultDateType;
    }

    public void setDefaultDateType(int defaultDateType) {
        this.defaultDateType = defaultDateType;
    }

    public ObjectWrapper getOuterIdentity() {
        return this.outerIdentity;
    }

    public void setOuterIdentity(ObjectWrapper outerIdentity) {
        this.outerIdentity = outerIdentity;
    }

    public boolean isStrict() {
        return this.strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public boolean getUseModelCache() {
        return this.useModelCache;
    }

    public void setUseModelCache(boolean useModelCache) {
        this.useModelCache = useModelCache;
    }

    public Version getIncompatibleImprovements() {
        return this.incompatibleImprovements;
    }

    public int getExposureLevel() {
        return this.classIntrospectorBuilder.getExposureLevel();
    }

    public void setExposureLevel(int exposureLevel) {
        this.classIntrospectorBuilder.setExposureLevel(exposureLevel);
    }

    public boolean getExposeFields() {
        return this.classIntrospectorBuilder.getExposeFields();
    }

    public void setExposeFields(boolean exposeFields) {
        this.classIntrospectorBuilder.setExposeFields(exposeFields);
    }

    public MemberAccessPolicy getMemberAccessPolicy() {
        return this.classIntrospectorBuilder.getMemberAccessPolicy();
    }

    public void setMemberAccessPolicy(MemberAccessPolicy memberAccessPolicy) {
        this.classIntrospectorBuilder.setMemberAccessPolicy(memberAccessPolicy);
    }

    public boolean getTreatDefaultMethodsAsBeanMembers() {
        return this.classIntrospectorBuilder.getTreatDefaultMethodsAsBeanMembers();
    }

    public void setTreatDefaultMethodsAsBeanMembers(boolean treatDefaultMethodsAsBeanMembers) {
        this.classIntrospectorBuilder.setTreatDefaultMethodsAsBeanMembers(treatDefaultMethodsAsBeanMembers);
    }

    public MethodAppearanceFineTuner getMethodAppearanceFineTuner() {
        return this.classIntrospectorBuilder.getMethodAppearanceFineTuner();
    }

    public void setMethodAppearanceFineTuner(MethodAppearanceFineTuner methodAppearanceFineTuner) {
        this.classIntrospectorBuilder.setMethodAppearanceFineTuner(methodAppearanceFineTuner);
    }

    MethodSorter getMethodSorter() {
        return this.classIntrospectorBuilder.getMethodSorter();
    }

    void setMethodSorter(MethodSorter methodSorter) {
        this.classIntrospectorBuilder.setMethodSorter(methodSorter);
    }

    ClassIntrospectorBuilder getClassIntrospectorBuilder() {
        return this.classIntrospectorBuilder;
    }
}

