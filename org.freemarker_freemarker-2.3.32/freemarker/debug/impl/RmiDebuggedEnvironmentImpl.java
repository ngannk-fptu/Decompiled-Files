/*
 * Decompiled with CFR 0.152.
 */
package freemarker.debug.impl;

import freemarker.cache.CacheStorage;
import freemarker.cache.SoftCacheStorage;
import freemarker.core.Configurable;
import freemarker.core.Environment;
import freemarker.debug.DebuggedEnvironment;
import freemarker.debug.impl.RmiDebugModelImpl;
import freemarker.template.Configuration;
import freemarker.template.SimpleCollection;
import freemarker.template.SimpleScalar;
import freemarker.template.Template;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.UndeclaredThrowableException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

class RmiDebuggedEnvironmentImpl
extends RmiDebugModelImpl
implements DebuggedEnvironment {
    private static final long serialVersionUID = 1L;
    private static final CacheStorage storage = new SoftCacheStorage(new IdentityHashMap());
    private static final Object idLock = new Object();
    private static long nextId = 1L;
    private static Set remotes = new HashSet();
    private boolean stopped = false;
    private final long id;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private RmiDebuggedEnvironmentImpl(Environment env) throws RemoteException {
        super(new DebugEnvironmentModel(env), 2048);
        Object object = idLock;
        synchronized (object) {
            this.id = nextId++;
        }
    }

    static synchronized Object getCachedWrapperFor(Object key) throws RemoteException {
        Object value = storage.get(key);
        if (value == null) {
            if (key instanceof TemplateModel) {
                int extraTypes = key instanceof DebugConfigurationModel ? 8192 : (key instanceof DebugTemplateModel ? 4096 : 0);
                value = new RmiDebugModelImpl((TemplateModel)key, extraTypes);
            } else if (key instanceof Environment) {
                value = new RmiDebuggedEnvironmentImpl((Environment)key);
            } else if (key instanceof Template) {
                value = new DebugTemplateModel((Template)key);
            } else if (key instanceof Configuration) {
                value = new DebugConfigurationModel((Configuration)key);
            }
        }
        if (value != null) {
            storage.put(key, value);
        }
        if (value instanceof Remote) {
            remotes.add(value);
        }
        return value;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void resume() {
        RmiDebuggedEnvironmentImpl rmiDebuggedEnvironmentImpl = this;
        synchronized (rmiDebuggedEnvironmentImpl) {
            this.notify();
        }
    }

    @Override
    public void stop() {
        this.stopped = true;
        this.resume();
    }

    @Override
    public long getId() {
        return this.id;
    }

    boolean isStopped() {
        return this.stopped;
    }

    public static void cleanup() {
        for (Object remoteObject : remotes) {
            try {
                UnicastRemoteObject.unexportObject((Remote)remoteObject, true);
            }
            catch (Exception exception) {}
        }
    }

    private static class DebugEnvironmentModel
    extends DebugConfigurableModel {
        private static final List KEYS = DebugEnvironmentModel.composeList(DebugConfigurableModel.KEYS, Arrays.asList("currentNamespace", "dataModel", "globalNamespace", "knownVariables", "mainNamespace", "template"));
        private TemplateModel knownVariables = new DebugMapModel(){

            @Override
            Collection keySet() {
                try {
                    return ((Environment)DebugEnvironmentModel.this.configurable).getKnownVariableNames();
                }
                catch (TemplateModelException e) {
                    throw new UndeclaredThrowableException(e);
                }
            }

            @Override
            public TemplateModel get(String key) throws TemplateModelException {
                return ((Environment)DebugEnvironmentModel.this.configurable).getVariable(key);
            }
        };

        DebugEnvironmentModel(Environment env) {
            super(env);
        }

        @Override
        Collection keySet() {
            return KEYS;
        }

        @Override
        public TemplateModel get(String key) throws TemplateModelException {
            if ("currentNamespace".equals(key)) {
                return ((Environment)this.configurable).getCurrentNamespace();
            }
            if ("dataModel".equals(key)) {
                return ((Environment)this.configurable).getDataModel();
            }
            if ("globalNamespace".equals(key)) {
                return ((Environment)this.configurable).getGlobalNamespace();
            }
            if ("knownVariables".equals(key)) {
                return this.knownVariables;
            }
            if ("mainNamespace".equals(key)) {
                return ((Environment)this.configurable).getMainNamespace();
            }
            if ("template".equals(key)) {
                try {
                    return (TemplateModel)RmiDebuggedEnvironmentImpl.getCachedWrapperFor(((Environment)this.configurable).getTemplate());
                }
                catch (RemoteException e) {
                    throw new TemplateModelException(e);
                }
            }
            return super.get(key);
        }
    }

    private static class DebugTemplateModel
    extends DebugConfigurableModel {
        private static final List KEYS = DebugTemplateModel.composeList(DebugConfigurableModel.KEYS, Arrays.asList("configuration", "name"));
        private final SimpleScalar name;

        DebugTemplateModel(Template template) {
            super(template);
            this.name = new SimpleScalar(template.getName());
        }

        @Override
        Collection keySet() {
            return KEYS;
        }

        @Override
        public TemplateModel get(String key) throws TemplateModelException {
            if ("configuration".equals(key)) {
                try {
                    return (TemplateModel)RmiDebuggedEnvironmentImpl.getCachedWrapperFor(((Template)this.configurable).getConfiguration());
                }
                catch (RemoteException e) {
                    throw new TemplateModelException(e);
                }
            }
            if ("name".equals(key)) {
                return this.name;
            }
            return super.get(key);
        }
    }

    private static class DebugConfigurationModel
    extends DebugConfigurableModel {
        private static final List KEYS = DebugConfigurationModel.composeList(DebugConfigurableModel.KEYS, Collections.singleton("sharedVariables"));
        private TemplateModel sharedVariables = new DebugMapModel(){

            @Override
            Collection keySet() {
                return ((Configuration)DebugConfigurationModel.this.configurable).getSharedVariableNames();
            }

            @Override
            public TemplateModel get(String key) {
                return ((Configuration)DebugConfigurationModel.this.configurable).getSharedVariable(key);
            }
        };

        DebugConfigurationModel(Configuration config) {
            super(config);
        }

        @Override
        Collection keySet() {
            return KEYS;
        }

        @Override
        public TemplateModel get(String key) throws TemplateModelException {
            if ("sharedVariables".equals(key)) {
                return this.sharedVariables;
            }
            return super.get(key);
        }
    }

    private static class DebugConfigurableModel
    extends DebugMapModel {
        static final List KEYS = Arrays.asList("arithmetic_engine", "boolean_format", "classic_compatible", "locale", "number_format", "object_wrapper", "template_exception_handler");
        final Configurable configurable;

        DebugConfigurableModel(Configurable configurable) {
            this.configurable = configurable;
        }

        @Override
        Collection keySet() {
            return KEYS;
        }

        @Override
        public TemplateModel get(String key) throws TemplateModelException {
            String s = this.configurable.getSetting(key);
            return s == null ? null : new SimpleScalar(s);
        }
    }

    private static abstract class DebugMapModel
    implements TemplateHashModelEx {
        private DebugMapModel() {
        }

        @Override
        public int size() {
            return this.keySet().size();
        }

        @Override
        public TemplateCollectionModel keys() {
            return new SimpleCollection(this.keySet());
        }

        @Override
        public TemplateCollectionModel values() throws TemplateModelException {
            Collection keys = this.keySet();
            ArrayList<TemplateModel> list = new ArrayList<TemplateModel>(keys.size());
            Iterator it = keys.iterator();
            while (it.hasNext()) {
                list.add(this.get((String)it.next()));
            }
            return new SimpleCollection(list);
        }

        @Override
        public boolean isEmpty() {
            return this.size() == 0;
        }

        abstract Collection keySet();

        static List composeList(Collection c1, Collection c2) {
            ArrayList list = new ArrayList(c1);
            list.addAll(c2);
            Collections.sort(list);
            return list;
        }
    }
}

