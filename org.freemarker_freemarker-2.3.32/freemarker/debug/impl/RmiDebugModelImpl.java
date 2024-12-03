/*
 * Decompiled with CFR 0.152.
 */
package freemarker.debug.impl;

import freemarker.debug.DebugModel;
import freemarker.debug.impl.RmiDebuggedEnvironmentImpl;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.TemplateTransformModel;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;

class RmiDebugModelImpl
extends UnicastRemoteObject
implements DebugModel {
    private static final long serialVersionUID = 1L;
    private final TemplateModel model;
    private final int type;

    RmiDebugModelImpl(TemplateModel model, int extraTypes) throws RemoteException {
        this.model = model;
        this.type = RmiDebugModelImpl.calculateType(model) + extraTypes;
    }

    private static DebugModel getDebugModel(TemplateModel tm) throws RemoteException {
        return (DebugModel)RmiDebuggedEnvironmentImpl.getCachedWrapperFor(tm);
    }

    @Override
    public String getAsString() throws TemplateModelException {
        return ((TemplateScalarModel)this.model).getAsString();
    }

    @Override
    public Number getAsNumber() throws TemplateModelException {
        return ((TemplateNumberModel)this.model).getAsNumber();
    }

    @Override
    public Date getAsDate() throws TemplateModelException {
        return ((TemplateDateModel)this.model).getAsDate();
    }

    @Override
    public int getDateType() {
        return ((TemplateDateModel)this.model).getDateType();
    }

    @Override
    public boolean getAsBoolean() throws TemplateModelException {
        return ((TemplateBooleanModel)this.model).getAsBoolean();
    }

    @Override
    public int size() throws TemplateModelException {
        if (this.model instanceof TemplateSequenceModel) {
            return ((TemplateSequenceModel)this.model).size();
        }
        return ((TemplateHashModelEx)this.model).size();
    }

    @Override
    public DebugModel get(int index) throws TemplateModelException, RemoteException {
        return RmiDebugModelImpl.getDebugModel(((TemplateSequenceModel)this.model).get(index));
    }

    @Override
    public DebugModel[] get(int fromIndex, int toIndex) throws TemplateModelException, RemoteException {
        DebugModel[] dm = new DebugModel[toIndex - fromIndex];
        TemplateSequenceModel s = (TemplateSequenceModel)this.model;
        for (int i = fromIndex; i < toIndex; ++i) {
            dm[i - fromIndex] = RmiDebugModelImpl.getDebugModel(s.get(i));
        }
        return dm;
    }

    @Override
    public DebugModel[] getCollection() throws TemplateModelException, RemoteException {
        ArrayList<DebugModel> list = new ArrayList<DebugModel>();
        TemplateModelIterator i = ((TemplateCollectionModel)this.model).iterator();
        while (i.hasNext()) {
            list.add(RmiDebugModelImpl.getDebugModel(i.next()));
        }
        return list.toArray(new DebugModel[list.size()]);
    }

    @Override
    public DebugModel get(String key) throws TemplateModelException, RemoteException {
        return RmiDebugModelImpl.getDebugModel(((TemplateHashModel)this.model).get(key));
    }

    @Override
    public DebugModel[] get(String[] keys) throws TemplateModelException, RemoteException {
        DebugModel[] dm = new DebugModel[keys.length];
        TemplateHashModel h = (TemplateHashModel)this.model;
        for (int i = 0; i < keys.length; ++i) {
            dm[i] = RmiDebugModelImpl.getDebugModel(h.get(keys[i]));
        }
        return dm;
    }

    @Override
    public String[] keys() throws TemplateModelException {
        TemplateHashModelEx h = (TemplateHashModelEx)this.model;
        ArrayList<String> list = new ArrayList<String>();
        TemplateModelIterator i = h.keys().iterator();
        while (i.hasNext()) {
            list.add(((TemplateScalarModel)i.next()).getAsString());
        }
        return list.toArray(new String[list.size()]);
    }

    @Override
    public int getModelTypes() {
        return this.type;
    }

    private static int calculateType(TemplateModel model) {
        int type = 0;
        if (model instanceof TemplateScalarModel) {
            ++type;
        }
        if (model instanceof TemplateNumberModel) {
            type += 2;
        }
        if (model instanceof TemplateDateModel) {
            type += 4;
        }
        if (model instanceof TemplateBooleanModel) {
            type += 8;
        }
        if (model instanceof TemplateSequenceModel) {
            type += 16;
        }
        if (model instanceof TemplateCollectionModel) {
            type += 32;
        }
        if (model instanceof TemplateHashModelEx) {
            type += 128;
        } else if (model instanceof TemplateHashModel) {
            type += 64;
        }
        if (model instanceof TemplateMethodModelEx) {
            type += 512;
        } else if (model instanceof TemplateMethodModel) {
            type += 256;
        }
        if (model instanceof TemplateTransformModel) {
            type += 1024;
        }
        return type;
    }
}

