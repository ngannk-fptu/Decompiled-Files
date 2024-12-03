/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  freemarker.core.CollectionAndSequence
 *  freemarker.ext.beans.BeansWrapper
 *  freemarker.ext.beans.MapModel
 *  freemarker.ext.util.ModelFactory
 *  freemarker.template.ObjectWrapper
 *  freemarker.template.SimpleSequence
 *  freemarker.template.TemplateCollectionModel
 *  freemarker.template.TemplateHashModelEx
 *  freemarker.template.TemplateModel
 *  freemarker.template.TemplateSequenceModel
 *  freemarker.template.Version
 */
package org.apache.struts2.views.freemarker;

import freemarker.core.CollectionAndSequence;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.MapModel;
import freemarker.ext.util.ModelFactory;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.Version;
import java.util.Map;
import java.util.Set;

public class StrutsBeanWrapper
extends BeansWrapper {
    private boolean altMapWrapper;

    public StrutsBeanWrapper(boolean altMapWrapper, Version incompatibleImprovements) {
        super(incompatibleImprovements);
        this.altMapWrapper = altMapWrapper;
    }

    protected ModelFactory getModelFactory(Class clazz) {
        if (this.altMapWrapper && Map.class.isAssignableFrom(clazz)) {
            return FriendlyMapModel.FACTORY;
        }
        return super.getModelFactory(clazz);
    }

    private static final class FriendlyMapModel
    extends MapModel
    implements TemplateHashModelEx {
        static final ModelFactory FACTORY = new ModelFactory(){

            public TemplateModel create(Object object, ObjectWrapper wrapper) {
                return new FriendlyMapModel((Map)object, (BeansWrapper)wrapper);
            }
        };

        public FriendlyMapModel(Map map, BeansWrapper wrapper) {
            super(map, wrapper);
        }

        public boolean isEmpty() {
            return ((Map)this.object).isEmpty();
        }

        protected Set keySet() {
            return ((Map)this.object).keySet();
        }

        public TemplateCollectionModel values() {
            return new CollectionAndSequence((TemplateSequenceModel)new SimpleSequence(((Map)this.object).values(), (ObjectWrapper)this.wrapper));
        }
    }
}

