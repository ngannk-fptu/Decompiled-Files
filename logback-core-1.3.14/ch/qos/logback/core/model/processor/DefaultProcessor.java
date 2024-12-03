/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.model.processor;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.util.beans.BeanDescriptionCache;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.ModelHandlerFactoryMethod;
import ch.qos.logback.core.model.NamedComponentModel;
import ch.qos.logback.core.model.processor.ChainedModelFilter;
import ch.qos.logback.core.model.processor.ModelFilter;
import ch.qos.logback.core.model.processor.ModelHandlerBase;
import ch.qos.logback.core.model.processor.ModelHandlerException;
import ch.qos.logback.core.model.processor.ModelInterpretationContext;
import ch.qos.logback.core.model.processor.PhaseIndicator;
import ch.qos.logback.core.model.processor.ProcessingPhase;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.FilterReply;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class DefaultProcessor
extends ContextAwareBase {
    protected final ModelInterpretationContext mic;
    final HashMap<Class<? extends Model>, ModelHandlerFactoryMethod> modelClassToHandlerMap = new HashMap();
    final HashMap<Class<? extends Model>, Supplier<ModelHandlerBase>> modelClassToDependencyAnalyserMap = new HashMap();
    ChainedModelFilter phaseOneFilter = new ChainedModelFilter();
    ChainedModelFilter phaseTwoFilter = new ChainedModelFilter();
    static final int DENIED = -1;

    public DefaultProcessor(Context context, ModelInterpretationContext mic) {
        this.setContext(context);
        this.mic = mic;
    }

    public void addHandler(Class<? extends Model> modelClass, ModelHandlerFactoryMethod modelFactoryMethod) {
        this.modelClassToHandlerMap.put(modelClass, modelFactoryMethod);
        ProcessingPhase phase = this.determineProcessingPhase(modelClass);
        switch (phase) {
            case FIRST: {
                this.getPhaseOneFilter().allow(modelClass);
                break;
            }
            case SECOND: {
                this.getPhaseTwoFilter().allow(modelClass);
                break;
            }
            default: {
                throw new IllegalArgumentException("unexpected value " + (Object)((Object)phase) + " for model class " + modelClass.getName());
            }
        }
    }

    private ProcessingPhase determineProcessingPhase(Class<? extends Model> modelClass) {
        PhaseIndicator phaseIndicator = modelClass.getAnnotation(PhaseIndicator.class);
        if (phaseIndicator == null) {
            return ProcessingPhase.FIRST;
        }
        ProcessingPhase phase = phaseIndicator.phase();
        return phase;
    }

    public void addAnalyser(Class<? extends Model> modelClass, Supplier<ModelHandlerBase> analyserSupplier) {
        this.modelClassToDependencyAnalyserMap.put(modelClass, analyserSupplier);
    }

    private void traversalLoop(TraverseMethod traverseMethod, Model model, ModelFilter modelfFilter, String phaseName) {
        int handledModelCount;
        int LIMIT = 3;
        for (int i = 0; i < LIMIT && (handledModelCount = traverseMethod.traverse(model, modelfFilter)) != 0; ++i) {
        }
    }

    public void process(Model model) {
        if (model == null) {
            this.addError("Expecting non null model to process");
            return;
        }
        this.initialObjectPush();
        this.mainTraverse(model, this.getPhaseOneFilter());
        this.analyseDependencies(model);
        this.traversalLoop(this::secondPhaseTraverse, model, this.getPhaseTwoFilter(), "phase 2");
        this.addInfo("End of configuration.");
        this.finalObjectPop();
    }

    private void finalObjectPop() {
        this.mic.popObject();
    }

    private void initialObjectPush() {
        this.mic.pushObject(this.context);
    }

    public ChainedModelFilter getPhaseOneFilter() {
        return this.phaseOneFilter;
    }

    public ChainedModelFilter getPhaseTwoFilter() {
        return this.phaseTwoFilter;
    }

    protected void analyseDependencies(Model model) {
        Supplier<ModelHandlerBase> analyserSupplier = this.modelClassToDependencyAnalyserMap.get(model.getClass());
        ModelHandlerBase analyser = null;
        if (analyserSupplier != null) {
            analyser = analyserSupplier.get();
        }
        if (analyser != null && !model.isSkipped()) {
            this.callAnalyserHandleOnModel(model, analyser);
        }
        for (Model m : model.getSubModels()) {
            this.analyseDependencies(m);
        }
        if (analyser != null && !model.isSkipped()) {
            this.callAnalyserPostHandleOnModel(model, analyser);
        }
    }

    private void callAnalyserPostHandleOnModel(Model model, ModelHandlerBase analyser) {
        try {
            analyser.postHandle(this.mic, model);
        }
        catch (ModelHandlerException e) {
            this.addError("Failed to invoke postHandle on model " + model.getTag(), e);
        }
    }

    private void callAnalyserHandleOnModel(Model model, ModelHandlerBase analyser) {
        try {
            analyser.handle(this.mic, model);
        }
        catch (ModelHandlerException e) {
            this.addError("Failed to traverse model " + model.getTag(), e);
        }
    }

    private ModelHandlerBase createHandler(Model model) {
        ModelHandlerFactoryMethod modelFactoryMethod = this.modelClassToHandlerMap.get(model.getClass());
        if (modelFactoryMethod == null) {
            this.addError("Can't handle model of type " + model.getClass() + "  with tag: " + model.getTag() + " at line " + model.getLineNumber());
            return null;
        }
        ModelHandlerBase handler = modelFactoryMethod.make(this.context, this.mic);
        if (handler == null) {
            return null;
        }
        if (!handler.isSupportedModelType(model)) {
            this.addWarn("Handler [" + handler.getClass() + "] does not support " + model.idString());
            return null;
        }
        return handler;
    }

    protected int mainTraverse(Model model, ModelFilter modelFiler) {
        FilterReply filterReply = modelFiler.decide(model);
        if (filterReply == FilterReply.DENY) {
            return -1;
        }
        int count = 0;
        try {
            ModelHandlerBase handler = null;
            boolean unhandled = model.isUnhandled();
            if (unhandled && (handler = this.createHandler(model)) != null) {
                handler.handle(this.mic, model);
                model.markAsHandled();
                ++count;
            }
            if (!model.isSkipped()) {
                for (Model m : model.getSubModels()) {
                    count += this.mainTraverse(m, modelFiler);
                }
            }
            if (unhandled && handler != null) {
                handler.postHandle(this.mic, model);
            }
        }
        catch (ModelHandlerException e) {
            this.addError("Failed to traverse model " + model.getTag(), e);
        }
        return count;
    }

    protected int secondPhaseTraverse(Model model, ModelFilter modelFilter) {
        FilterReply filterReply = modelFilter.decide(model);
        if (filterReply == FilterReply.DENY) {
            return 0;
        }
        int count = 0;
        try {
            boolean allDependenciesStarted = this.allDependenciesStarted(model);
            ModelHandlerBase handler = null;
            if (model.isUnhandled() && allDependenciesStarted && (handler = this.createHandler(model)) != null) {
                handler.handle(this.mic, model);
                model.markAsHandled();
                ++count;
            }
            if (!allDependenciesStarted && !this.dependencyIsADirectSubmodel(model)) {
                return count;
            }
            if (!model.isSkipped()) {
                for (Model m : model.getSubModels()) {
                    count += this.secondPhaseTraverse(m, modelFilter);
                }
            }
            if (handler != null) {
                handler.postHandle(this.mic, model);
            }
        }
        catch (ModelHandlerException e) {
            this.addError("Failed to traverse model " + model.getTag(), e);
        }
        return count;
    }

    private boolean dependencyIsADirectSubmodel(Model model) {
        List<String> dependecyNames = this.mic.getDependeeNamesForModel(model);
        if (dependecyNames == null || dependecyNames.isEmpty()) {
            return false;
        }
        for (Model submodel : model.getSubModels()) {
            NamedComponentModel namedComponentModel;
            String subModelName;
            if (!(submodel instanceof NamedComponentModel) || !dependecyNames.contains(subModelName = (namedComponentModel = (NamedComponentModel)submodel).getName())) continue;
            return true;
        }
        return false;
    }

    private boolean allDependenciesStarted(Model model) {
        List<String> dependencyNames = this.mic.getDependeeNamesForModel(model);
        if (dependencyNames == null || dependencyNames.isEmpty()) {
            return true;
        }
        for (String name : dependencyNames) {
            boolean isStarted = this.mic.isNamedDependeeStarted(name);
            if (isStarted) continue;
            return false;
        }
        return true;
    }

    ModelHandlerBase instantiateHandler(Class<? extends ModelHandlerBase> handlerClass) {
        try {
            Constructor<? extends ModelHandlerBase> commonConstructor = this.getWithContextConstructor(handlerClass);
            if (commonConstructor != null) {
                return commonConstructor.newInstance(this.context);
            }
            Constructor<? extends ModelHandlerBase> constructorWithBDC = this.getWithContextAndBDCConstructor(handlerClass);
            if (constructorWithBDC != null) {
                return constructorWithBDC.newInstance(this.context, this.mic.getBeanDescriptionCache());
            }
            this.addError("Failed to find suitable constructor for class [" + handlerClass + "]");
            return null;
        }
        catch (IllegalAccessException | IllegalArgumentException | InstantiationException | SecurityException | InvocationTargetException e1) {
            this.addError("Failed to instantiate " + handlerClass);
            return null;
        }
    }

    private Constructor<? extends ModelHandlerBase> getWithContextConstructor(Class<? extends ModelHandlerBase> handlerClass) {
        try {
            Constructor<? extends ModelHandlerBase> constructor = handlerClass.getConstructor(Context.class);
            return constructor;
        }
        catch (NoSuchMethodException e) {
            return null;
        }
    }

    private Constructor<? extends ModelHandlerBase> getWithContextAndBDCConstructor(Class<? extends ModelHandlerBase> handlerClass) {
        try {
            Constructor<? extends ModelHandlerBase> constructor = handlerClass.getConstructor(Context.class, BeanDescriptionCache.class);
            return constructor;
        }
        catch (NoSuchMethodException e) {
            return null;
        }
    }

    static interface TraverseMethod {
        public int traverse(Model var1, ModelFilter var2);
    }
}

