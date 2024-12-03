/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperConfiguration;
import freemarker.ext.beans.DefaultMemberAccessPolicy;
import freemarker.ext.beans.LegacyDefaultMemberAccessPolicy;
import freemarker.ext.beans.MemberAccessPolicy;
import freemarker.ext.dom.NodeModel;
import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template.DefaultArrayAdapter;
import freemarker.template.DefaultEnumerationAdapter;
import freemarker.template.DefaultIterableAdapter;
import freemarker.template.DefaultIteratorAdapter;
import freemarker.template.DefaultListAdapter;
import freemarker.template.DefaultMapAdapter;
import freemarker.template.DefaultNonListCollectionAdapter;
import freemarker.template.DefaultObjectWrapperConfiguration;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleCollection;
import freemarker.template.SimpleDate;
import freemarker.template.SimpleHash;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.Version;
import freemarker.template._TemplateAPI;
import freemarker.template._VersionInts;
import java.lang.reflect.Array;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Node;

public class DefaultObjectWrapper
extends BeansWrapper {
    @Deprecated
    static final DefaultObjectWrapper instance;
    private static final Class<?> JYTHON_OBJ_CLASS;
    private static final ObjectWrapper JYTHON_WRAPPER;
    private boolean useAdaptersForContainers;
    private boolean forceLegacyNonListCollections;
    private boolean iterableSupport;
    private boolean domNodeSupport;
    private boolean jythonSupport;
    private final boolean useAdapterForEnumerations;

    @Deprecated
    public DefaultObjectWrapper() {
        this(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
    }

    public DefaultObjectWrapper(Version incompatibleImprovements) {
        this(new DefaultObjectWrapperConfiguration(incompatibleImprovements){}, false);
    }

    protected DefaultObjectWrapper(BeansWrapperConfiguration bwCfg, boolean writeProtected) {
        super(bwCfg, writeProtected, false);
        DefaultObjectWrapperConfiguration dowDowCfg = bwCfg instanceof DefaultObjectWrapperConfiguration ? (DefaultObjectWrapperConfiguration)bwCfg : new DefaultObjectWrapperConfiguration(bwCfg.getIncompatibleImprovements()){};
        this.useAdaptersForContainers = dowDowCfg.getUseAdaptersForContainers();
        this.useAdapterForEnumerations = this.useAdaptersForContainers && this.getIncompatibleImprovements().intValue() >= _VersionInts.V_2_3_26;
        this.forceLegacyNonListCollections = dowDowCfg.getForceLegacyNonListCollections();
        this.iterableSupport = dowDowCfg.getIterableSupport();
        this.domNodeSupport = dowDowCfg.getDOMNodeSupport();
        this.jythonSupport = dowDowCfg.getJythonSupport();
        this.finalizeConstruction(writeProtected);
    }

    protected DefaultObjectWrapper(DefaultObjectWrapperConfiguration dowCfg, boolean writeProtected) {
        this((BeansWrapperConfiguration)dowCfg, writeProtected);
    }

    @Override
    public TemplateModel wrap(Object obj) throws TemplateModelException {
        if (obj == null) {
            return super.wrap(null);
        }
        if (obj instanceof TemplateModel) {
            return (TemplateModel)obj;
        }
        if (obj instanceof String) {
            return new SimpleScalar((String)obj);
        }
        if (obj instanceof Number) {
            return new SimpleNumber((Number)obj);
        }
        if (obj instanceof java.util.Date) {
            if (obj instanceof Date) {
                return new SimpleDate((Date)obj);
            }
            if (obj instanceof Time) {
                return new SimpleDate((Time)obj);
            }
            if (obj instanceof Timestamp) {
                return new SimpleDate((Timestamp)obj);
            }
            return new SimpleDate((java.util.Date)obj, this.getDefaultDateType());
        }
        Class<?> objClass = obj.getClass();
        if (objClass.isArray()) {
            if (this.useAdaptersForContainers) {
                return DefaultArrayAdapter.adapt(obj, this);
            }
            obj = this.convertArray(obj);
        }
        if (obj instanceof Collection) {
            if (this.useAdaptersForContainers) {
                if (obj instanceof List) {
                    return DefaultListAdapter.adapt((List)obj, this);
                }
                return (TemplateModel)((Object)(this.forceLegacyNonListCollections ? new SimpleSequence((Collection)obj, (ObjectWrapper)this) : DefaultNonListCollectionAdapter.adapt((Collection)obj, this)));
            }
            return new SimpleSequence((Collection)obj, (ObjectWrapper)this);
        }
        if (obj instanceof Map) {
            return (TemplateModel)((Object)(this.useAdaptersForContainers ? DefaultMapAdapter.adapt((Map)obj, this) : new SimpleHash((Map)obj, this)));
        }
        if (obj instanceof Boolean) {
            return obj.equals(Boolean.TRUE) ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
        if (obj instanceof Iterator) {
            return (TemplateModel)((Object)(this.useAdaptersForContainers ? DefaultIteratorAdapter.adapt((Iterator)obj, this) : new SimpleCollection((Iterator)obj, (ObjectWrapper)this)));
        }
        if (this.useAdapterForEnumerations && obj instanceof Enumeration) {
            return DefaultEnumerationAdapter.adapt((Enumeration)obj, this);
        }
        if (this.iterableSupport && obj instanceof Iterable) {
            return DefaultIterableAdapter.adapt((Iterable)obj, this);
        }
        return this.handleUnknownType(obj);
    }

    protected TemplateModel handleUnknownType(Object obj) throws TemplateModelException {
        MemberAccessPolicy memberAccessPolicy;
        if (this.domNodeSupport && obj instanceof Node) {
            return this.wrapDomNode(obj);
        }
        if (this.jythonSupport && ((memberAccessPolicy = this.getMemberAccessPolicy()) instanceof DefaultMemberAccessPolicy || memberAccessPolicy instanceof LegacyDefaultMemberAccessPolicy) && JYTHON_WRAPPER != null && JYTHON_OBJ_CLASS.isInstance(obj)) {
            return JYTHON_WRAPPER.wrap(obj);
        }
        return super.wrap(obj);
    }

    public TemplateModel wrapDomNode(Object obj) {
        return NodeModel.wrap((Node)obj);
    }

    protected Object convertArray(Object arr) {
        int size = Array.getLength(arr);
        ArrayList<Object> list = new ArrayList<Object>(size);
        for (int i = 0; i < size; ++i) {
            list.add(Array.get(arr, i));
        }
        return list;
    }

    public boolean getUseAdaptersForContainers() {
        return this.useAdaptersForContainers;
    }

    public void setUseAdaptersForContainers(boolean useAdaptersForContainers) {
        this.checkModifiable();
        this.useAdaptersForContainers = useAdaptersForContainers;
    }

    public boolean getForceLegacyNonListCollections() {
        return this.forceLegacyNonListCollections;
    }

    public void setForceLegacyNonListCollections(boolean forceLegacyNonListCollections) {
        this.checkModifiable();
        this.forceLegacyNonListCollections = forceLegacyNonListCollections;
    }

    public boolean getIterableSupport() {
        return this.iterableSupport;
    }

    public void setIterableSupport(boolean iterableSupport) {
        this.checkModifiable();
        this.iterableSupport = iterableSupport;
    }

    public final boolean getDOMNodeSupport() {
        return this.domNodeSupport;
    }

    public void setDOMNodeSupport(boolean domNodeSupport) {
        this.checkModifiable();
        this.domNodeSupport = domNodeSupport;
    }

    public final boolean getJythonSupport() {
        return this.jythonSupport;
    }

    public void setJythonSupport(boolean jythonSupport) {
        this.checkModifiable();
        this.jythonSupport = jythonSupport;
    }

    protected static Version normalizeIncompatibleImprovementsVersion(Version incompatibleImprovements) {
        _TemplateAPI.checkVersionNotNullAndSupported(incompatibleImprovements);
        Version bwIcI = BeansWrapper.normalizeIncompatibleImprovementsVersion(incompatibleImprovements);
        return incompatibleImprovements.intValue() < _VersionInts.V_2_3_22 || bwIcI.intValue() >= _VersionInts.V_2_3_22 ? bwIcI : Configuration.VERSION_2_3_22;
    }

    @Override
    protected String toPropertiesString() {
        int smwEnd;
        String bwProps = super.toPropertiesString();
        if (bwProps.startsWith("simpleMapWrapper") && (smwEnd = bwProps.indexOf(44)) != -1) {
            bwProps = bwProps.substring(smwEnd + 1).trim();
        }
        return "useAdaptersForContainers=" + this.useAdaptersForContainers + ", forceLegacyNonListCollections=" + this.forceLegacyNonListCollections + ", iterableSupport=" + this.iterableSupport + ", domNodeSupport=" + this.domNodeSupport + ", jythonSupport=" + this.jythonSupport + bwProps;
    }

    static {
        ObjectWrapper ow;
        Class<?> cl;
        block4: {
            instance = new DefaultObjectWrapper();
            try {
                cl = Class.forName("org.python.core.PyObject");
                ow = (ObjectWrapper)Class.forName("freemarker.ext.jython.JythonWrapper").getField("INSTANCE").get(null);
            }
            catch (Throwable e) {
                cl = null;
                ow = null;
                if (e instanceof ClassNotFoundException) break block4;
                try {
                    Logger.getLogger("freemarker.template.DefaultObjectWrapper").error("Failed to init Jython support, so it was disabled.", e);
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
        }
        JYTHON_OBJ_CLASS = cl;
        JYTHON_WRAPPER = ow;
    }
}

