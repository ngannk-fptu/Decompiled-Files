/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.ext.beans.BeansWrapperConfiguration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Version;
import freemarker.template._TemplateAPI;
import freemarker.template._VersionInts;

public abstract class DefaultObjectWrapperConfiguration
extends BeansWrapperConfiguration {
    private boolean useAdaptersForContainers;
    private boolean forceLegacyNonListCollections;
    private boolean iterableSupport;
    private boolean domNodeSupport;
    private boolean jythonSupport;

    protected DefaultObjectWrapperConfiguration(Version incompatibleImprovements) {
        super(DefaultObjectWrapper.normalizeIncompatibleImprovementsVersion(incompatibleImprovements), true);
        _TemplateAPI.checkCurrentVersionNotRecycled(incompatibleImprovements, "freemarker.configuration", "DefaultObjectWrapper");
        this.useAdaptersForContainers = this.getIncompatibleImprovements().intValue() >= _VersionInts.V_2_3_22;
        this.forceLegacyNonListCollections = true;
        this.domNodeSupport = true;
        this.jythonSupport = true;
    }

    public boolean getUseAdaptersForContainers() {
        return this.useAdaptersForContainers;
    }

    public void setUseAdaptersForContainers(boolean useAdaptersForContainers) {
        this.useAdaptersForContainers = useAdaptersForContainers;
    }

    public boolean getForceLegacyNonListCollections() {
        return this.forceLegacyNonListCollections;
    }

    public void setForceLegacyNonListCollections(boolean legacyNonListCollectionWrapping) {
        this.forceLegacyNonListCollections = legacyNonListCollectionWrapping;
    }

    public boolean getDOMNodeSupport() {
        return this.domNodeSupport;
    }

    public void setDOMNodeSupport(boolean domNodeSupport) {
        this.domNodeSupport = domNodeSupport;
    }

    public boolean getJythonSupport() {
        return this.jythonSupport;
    }

    public void setJythonSupport(boolean jythonSupport) {
        this.jythonSupport = jythonSupport;
    }

    public boolean getIterableSupport() {
        return this.iterableSupport;
    }

    public void setIterableSupport(boolean iterableSupport) {
        this.iterableSupport = iterableSupport;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        int prime = 31;
        result = result * 31 + (this.useAdaptersForContainers ? 1231 : 1237);
        result = result * 31 + (this.forceLegacyNonListCollections ? 1231 : 1237);
        result = result * 31 + (this.iterableSupport ? 1231 : 1237);
        result = result * 31 + (this.domNodeSupport ? 1231 : 1237);
        result = result * 31 + (this.jythonSupport ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object that) {
        if (!super.equals(that)) {
            return false;
        }
        DefaultObjectWrapperConfiguration thatDowCfg = (DefaultObjectWrapperConfiguration)that;
        return this.useAdaptersForContainers == thatDowCfg.getUseAdaptersForContainers() && this.forceLegacyNonListCollections == thatDowCfg.forceLegacyNonListCollections && this.iterableSupport == thatDowCfg.iterableSupport && this.domNodeSupport == thatDowCfg.domNodeSupport && this.jythonSupport == thatDowCfg.jythonSupport;
    }
}

