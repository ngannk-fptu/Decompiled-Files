/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  com.sun.xml.bind.util.Which
 *  javax.xml.ws.RespectBinding
 *  javax.xml.ws.RespectBindingFeature
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.WebServiceFeature
 *  javax.xml.ws.soap.Addressing
 *  javax.xml.ws.soap.AddressingFeature
 *  javax.xml.ws.soap.MTOM
 *  javax.xml.ws.soap.MTOMFeature
 *  javax.xml.ws.spi.WebServiceFeatureAnnotation
 */
package com.sun.xml.ws.binding;

import com.oracle.webservices.api.EnvelopeStyleFeature;
import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.bind.util.Which;
import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.api.FeatureConstructor;
import com.sun.xml.ws.api.FeatureListValidator;
import com.sun.xml.ws.api.FeatureListValidatorAnnotation;
import com.sun.xml.ws.api.ImpliesWebServiceFeature;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.WSFeatureList;
import com.sun.xml.ws.api.model.wsdl.WSDLFeaturedObject;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.model.RuntimeModelerException;
import com.sun.xml.ws.resources.ModelerMessages;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.RespectBinding;
import javax.xml.ws.RespectBindingFeature;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.Addressing;
import javax.xml.ws.soap.AddressingFeature;
import javax.xml.ws.soap.MTOM;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;

public final class WebServiceFeatureList
extends AbstractMap<Class<? extends WebServiceFeature>, WebServiceFeature>
implements WSFeatureList {
    private Map<Class<? extends WebServiceFeature>, WebServiceFeature> wsfeatures = new HashMap<Class<? extends WebServiceFeature>, WebServiceFeature>();
    private boolean isValidating = false;
    @Nullable
    private WSDLFeaturedObject parent;
    private static final Logger LOGGER = Logger.getLogger(WebServiceFeatureList.class.getName());

    public static WebServiceFeatureList toList(Iterable<WebServiceFeature> features) {
        if (features instanceof WebServiceFeatureList) {
            return (WebServiceFeatureList)features;
        }
        WebServiceFeatureList w = new WebServiceFeatureList();
        if (features != null) {
            w.addAll(features);
        }
        return w;
    }

    public WebServiceFeatureList() {
    }

    public WebServiceFeatureList(WebServiceFeature ... features) {
        if (features != null) {
            for (WebServiceFeature f : features) {
                this.addNoValidate(f);
            }
        }
    }

    public void validate() {
        if (!this.isValidating) {
            this.isValidating = true;
            for (WebServiceFeature ff : this) {
                this.validate(ff);
            }
        }
    }

    private void validate(WebServiceFeature feature) {
        FeatureListValidatorAnnotation fva = feature.getClass().getAnnotation(FeatureListValidatorAnnotation.class);
        if (fva != null) {
            Class<? extends FeatureListValidator> beanClass = fva.bean();
            try {
                FeatureListValidator validator = beanClass.newInstance();
                validator.validate(this);
            }
            catch (InstantiationException e) {
                throw new WebServiceException((Throwable)e);
            }
            catch (IllegalAccessException e) {
                throw new WebServiceException((Throwable)e);
            }
        }
    }

    public WebServiceFeatureList(WebServiceFeatureList features) {
        if (features != null) {
            this.wsfeatures.putAll(features.wsfeatures);
            this.parent = features.parent;
            this.isValidating = features.isValidating;
        }
    }

    public WebServiceFeatureList(@NotNull Class<?> endpointClass) {
        this.parseAnnotations(endpointClass);
    }

    public void parseAnnotations(Iterable<Annotation> annIt) {
        for (Annotation ann : annIt) {
            WebServiceFeature feature = WebServiceFeatureList.getFeature(ann);
            if (feature == null) continue;
            this.add(feature);
        }
    }

    public static WebServiceFeature getFeature(Annotation a) {
        WebServiceFeature ftr = null;
        if (!a.annotationType().isAnnotationPresent(WebServiceFeatureAnnotation.class)) {
            ftr = null;
        } else if (a instanceof Addressing) {
            Addressing addAnn = (Addressing)a;
            try {
                ftr = new AddressingFeature(addAnn.enabled(), addAnn.required(), addAnn.responses());
            }
            catch (NoSuchMethodError e) {
                throw new RuntimeModelerException(ModelerMessages.RUNTIME_MODELER_ADDRESSING_RESPONSES_NOSUCHMETHOD(WebServiceFeatureList.toJar(Which.which(Addressing.class))), new Object[0]);
            }
        } else if (a instanceof MTOM) {
            MTOM mtomAnn = (MTOM)a;
            ftr = new MTOMFeature(mtomAnn.enabled(), mtomAnn.threshold());
        } else if (a instanceof RespectBinding) {
            RespectBinding rbAnn = (RespectBinding)a;
            ftr = new RespectBindingFeature(rbAnn.enabled());
        } else {
            ftr = WebServiceFeatureList.getWebServiceFeatureBean(a);
        }
        return ftr;
    }

    public void parseAnnotations(Class<?> endpointClass) {
        for (Annotation a : endpointClass.getAnnotations()) {
            BindingID bindingID;
            MTOMFeature bindingMtomSetting;
            WebServiceFeature ftr = WebServiceFeatureList.getFeature(a);
            if (ftr == null) continue;
            if (ftr instanceof MTOMFeature && (bindingMtomSetting = (bindingID = BindingID.parse(endpointClass)).createBuiltinFeatureList().get(MTOMFeature.class)) != null && bindingMtomSetting.isEnabled() ^ ftr.isEnabled()) {
                throw new RuntimeModelerException(ModelerMessages.RUNTIME_MODELER_MTOM_CONFLICT(bindingID, ftr.isEnabled()), new Object[0]);
            }
            this.add(ftr);
        }
    }

    private static String toJar(String url) {
        if (!url.startsWith("jar:")) {
            return url;
        }
        url = url.substring(4);
        return url.substring(0, url.lastIndexOf(33));
    }

    private static WebServiceFeature getWebServiceFeatureBean(Annotation a) {
        WebServiceFeature bean;
        WebServiceFeatureAnnotation wsfa = a.annotationType().getAnnotation(WebServiceFeatureAnnotation.class);
        Class beanClass = wsfa.bean();
        Constructor<?> ftrCtr = null;
        String[] paramNames = null;
        for (Constructor<?> con : beanClass.getConstructors()) {
            FeatureConstructor ftrCtrAnn = con.getAnnotation(FeatureConstructor.class);
            if (ftrCtrAnn == null) continue;
            if (ftrCtr == null) {
                ftrCtr = con;
                paramNames = ftrCtrAnn.value();
                continue;
            }
            throw new WebServiceException(ModelerMessages.RUNTIME_MODELER_WSFEATURE_MORETHANONE_FTRCONSTRUCTOR(a, beanClass));
        }
        if (ftrCtr == null) {
            WebServiceFeature bean2 = WebServiceFeatureList.getWebServiceFeatureBeanViaBuilder(a, beanClass);
            if (bean2 != null) {
                return bean2;
            }
            throw new WebServiceException(ModelerMessages.RUNTIME_MODELER_WSFEATURE_NO_FTRCONSTRUCTOR(a, beanClass));
        }
        if (ftrCtr.getParameterTypes().length != paramNames.length) {
            throw new WebServiceException(ModelerMessages.RUNTIME_MODELER_WSFEATURE_ILLEGAL_FTRCONSTRUCTOR(a, beanClass));
        }
        try {
            Object[] params = new Object[paramNames.length];
            for (int i = 0; i < paramNames.length; ++i) {
                Method m = a.annotationType().getDeclaredMethod(paramNames[i], new Class[0]);
                params[i] = m.invoke((Object)a, new Object[0]);
            }
            bean = (WebServiceFeature)ftrCtr.newInstance(params);
        }
        catch (Exception e) {
            throw new WebServiceException((Throwable)e);
        }
        return bean;
    }

    private static WebServiceFeature getWebServiceFeatureBeanViaBuilder(Annotation annotation, Class<? extends WebServiceFeature> beanClass) {
        try {
            Method featureBuilderMethod = beanClass.getDeclaredMethod("builder", new Class[0]);
            Object builder = featureBuilderMethod.invoke(beanClass, new Object[0]);
            Method buildMethod = builder.getClass().getDeclaredMethod("build", new Class[0]);
            for (Method builderMethod : builder.getClass().getDeclaredMethods()) {
                if (builderMethod.equals(buildMethod) || builderMethod.isSynthetic()) continue;
                String methodName = builderMethod.getName();
                Method annotationMethod = annotation.annotationType().getDeclaredMethod(methodName, new Class[0]);
                Object annotationFieldValue = annotationMethod.invoke((Object)annotation, new Object[0]);
                Object[] arg = new Object[]{annotationFieldValue};
                if (WebServiceFeatureList.skipDuringOrgJvnetWsToComOracleWebservicesPackageMove(builderMethod, annotationFieldValue)) continue;
                builderMethod.invoke(builder, arg);
            }
            Object result = buildMethod.invoke(builder, new Object[0]);
            if (result instanceof WebServiceFeature) {
                return (WebServiceFeature)result;
            }
            throw new WebServiceException("Not a WebServiceFeature: " + result);
        }
        catch (NoSuchMethodException e) {
            LOGGER.log(Level.INFO, "Unable to find builder method on webservice feature: " + beanClass.getName(), e);
            return null;
        }
        catch (IllegalAccessException e) {
            throw new WebServiceException((Throwable)e);
        }
        catch (InvocationTargetException e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    private static boolean skipDuringOrgJvnetWsToComOracleWebservicesPackageMove(Method builderMethod, Object annotationFieldValue) {
        Class<?> annotationFieldValueClass = annotationFieldValue.getClass();
        if (!annotationFieldValueClass.isEnum()) {
            return false;
        }
        Class<?>[] builderMethodParameterTypes = builderMethod.getParameterTypes();
        if (builderMethodParameterTypes.length != 1) {
            throw new WebServiceException("expected only 1 parameter");
        }
        String builderParameterTypeName = builderMethodParameterTypes[0].getName();
        if (!builderParameterTypeName.startsWith("com.oracle.webservices.test.features_annotations_enums.apinew") && !builderParameterTypeName.startsWith("com.oracle.webservices.api")) {
            return false;
        }
        return false;
    }

    @Override
    public Iterator<WebServiceFeature> iterator() {
        if (this.parent != null) {
            return new MergedFeatures(this.parent.getFeatures());
        }
        return this.wsfeatures.values().iterator();
    }

    @Override
    @NotNull
    public WebServiceFeature[] toArray() {
        if (this.parent != null) {
            return new MergedFeatures(this.parent.getFeatures()).toArray();
        }
        return this.wsfeatures.values().toArray(new WebServiceFeature[0]);
    }

    @Override
    public boolean isEnabled(@NotNull Class<? extends WebServiceFeature> feature) {
        WebServiceFeature ftr = this.get(feature);
        return ftr != null && ftr.isEnabled();
    }

    public boolean contains(@NotNull Class<? extends WebServiceFeature> feature) {
        WebServiceFeature ftr = this.get(feature);
        return ftr != null;
    }

    @Override
    @Nullable
    public <F extends WebServiceFeature> F get(@NotNull Class<F> featureType) {
        WebServiceFeature f = (WebServiceFeature)featureType.cast(this.wsfeatures.get(featureType));
        if (f == null && this.parent != null) {
            return this.parent.getFeatures().get(featureType);
        }
        return (F)f;
    }

    public void add(@NotNull WebServiceFeature f) {
        if (this.addNoValidate(f) && this.isValidating) {
            this.validate(f);
        }
    }

    private boolean addNoValidate(@NotNull WebServiceFeature f) {
        if (!this.wsfeatures.containsKey(f.getClass())) {
            this.wsfeatures.put(f.getClass(), f);
            if (f instanceof ImpliesWebServiceFeature) {
                ((ImpliesWebServiceFeature)f).implyFeatures(this);
            }
            return true;
        }
        return false;
    }

    public void addAll(@NotNull Iterable<WebServiceFeature> list) {
        for (WebServiceFeature f : list) {
            this.add(f);
        }
    }

    void setMTOMEnabled(boolean b) {
        this.wsfeatures.put(MTOMFeature.class, (WebServiceFeature)new MTOMFeature(b));
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof WebServiceFeatureList)) {
            return false;
        }
        WebServiceFeatureList w = (WebServiceFeatureList)other;
        return this.wsfeatures.equals(w.wsfeatures) && this.parent == w.parent;
    }

    @Override
    public String toString() {
        return this.wsfeatures.toString();
    }

    @Override
    public void mergeFeatures(@NotNull Iterable<WebServiceFeature> features, boolean reportConflicts) {
        for (WebServiceFeature wsdlFtr : features) {
            if (this.get(wsdlFtr.getClass()) == null) {
                this.add(wsdlFtr);
                continue;
            }
            if (!reportConflicts || this.isEnabled(wsdlFtr.getClass()) == wsdlFtr.isEnabled()) continue;
            LOGGER.warning(ModelerMessages.RUNTIME_MODELER_FEATURE_CONFLICT(this.get(wsdlFtr.getClass()), wsdlFtr));
        }
    }

    @Override
    public void mergeFeatures(WebServiceFeature[] features, boolean reportConflicts) {
        for (WebServiceFeature wsdlFtr : features) {
            if (this.get(wsdlFtr.getClass()) == null) {
                this.add(wsdlFtr);
                continue;
            }
            if (!reportConflicts || this.isEnabled(wsdlFtr.getClass()) == wsdlFtr.isEnabled()) continue;
            LOGGER.warning(ModelerMessages.RUNTIME_MODELER_FEATURE_CONFLICT(this.get(wsdlFtr.getClass()), wsdlFtr));
        }
    }

    public void mergeFeatures(@NotNull WSDLPort wsdlPort, boolean honorWsdlRequired, boolean reportConflicts) {
        if (honorWsdlRequired && !this.isEnabled(RespectBindingFeature.class)) {
            return;
        }
        if (!honorWsdlRequired) {
            this.addAll(wsdlPort.getFeatures());
            return;
        }
        for (WebServiceFeature wsdlFtr : wsdlPort.getFeatures()) {
            if (this.get(wsdlFtr.getClass()) == null) {
                try {
                    Method m = wsdlFtr.getClass().getMethod("isRequired", new Class[0]);
                    try {
                        boolean required = (Boolean)m.invoke((Object)wsdlFtr, new Object[0]);
                        if (!required) continue;
                        this.add(wsdlFtr);
                        continue;
                    }
                    catch (IllegalAccessException e) {
                        throw new WebServiceException((Throwable)e);
                    }
                    catch (InvocationTargetException e) {
                        throw new WebServiceException((Throwable)e);
                    }
                }
                catch (NoSuchMethodException e) {
                    this.add(wsdlFtr);
                    continue;
                }
            }
            if (!reportConflicts || this.isEnabled(wsdlFtr.getClass()) == wsdlFtr.isEnabled()) continue;
            LOGGER.warning(ModelerMessages.RUNTIME_MODELER_FEATURE_CONFLICT(this.get(wsdlFtr.getClass()), wsdlFtr));
        }
    }

    public void setParentFeaturedObject(@NotNull WSDLFeaturedObject parent) {
        this.parent = parent;
    }

    @Nullable
    public static <F extends WebServiceFeature> F getFeature(@NotNull WebServiceFeature[] features, @NotNull Class<F> featureType) {
        for (WebServiceFeature f : features) {
            if (f.getClass() != featureType) continue;
            return (F)f;
        }
        return null;
    }

    @Override
    public Set<Map.Entry<Class<? extends WebServiceFeature>, WebServiceFeature>> entrySet() {
        return this.wsfeatures.entrySet();
    }

    @Override
    public WebServiceFeature put(Class<? extends WebServiceFeature> key, WebServiceFeature value) {
        return this.wsfeatures.put(key, value);
    }

    public static SOAPVersion getSoapVersion(WSFeatureList features) {
        EnvelopeStyleFeature env = features.get(EnvelopeStyleFeature.class);
        if (env != null) {
            return SOAPVersion.from(env);
        }
        env = features.get(EnvelopeStyleFeature.class);
        return env != null ? SOAPVersion.from(env) : null;
    }

    public static boolean isFeatureEnabled(Class<? extends WebServiceFeature> type, WebServiceFeature[] features) {
        WebServiceFeature ftr = WebServiceFeatureList.getFeature(features, type);
        return ftr != null && ftr.isEnabled();
    }

    public static WebServiceFeature[] toFeatureArray(WSBinding binding) {
        if (!binding.isFeatureEnabled(EnvelopeStyleFeature.class)) {
            WebServiceFeature[] f = new WebServiceFeature[]{binding.getSOAPVersion().toFeature()};
            binding.getFeatures().mergeFeatures(f, false);
        }
        return binding.getFeatures().toArray();
    }

    private final class MergedFeatures
    implements Iterator<WebServiceFeature> {
        private final Stack<WebServiceFeature> features = new Stack();

        public MergedFeatures(WSFeatureList parent) {
            for (WebServiceFeature f : WebServiceFeatureList.this.wsfeatures.values()) {
                this.features.push(f);
            }
            for (WebServiceFeature f : parent) {
                if (WebServiceFeatureList.this.wsfeatures.containsKey(f.getClass())) continue;
                this.features.push(f);
            }
        }

        @Override
        public boolean hasNext() {
            return !this.features.empty();
        }

        @Override
        public WebServiceFeature next() {
            if (!this.features.empty()) {
                return this.features.pop();
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            if (!this.features.empty()) {
                this.features.pop();
            }
        }

        public WebServiceFeature[] toArray() {
            return this.features.toArray(new WebServiceFeature[0]);
        }
    }
}

