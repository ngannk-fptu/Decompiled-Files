/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.wadl.config;

import com.sun.jersey.api.wadl.config.WadlGeneratorDescription;
import com.sun.jersey.server.wadl.WadlGenerator;
import com.sun.jersey.server.wadl.generators.WadlGeneratorJAXBGrammarGenerator;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

class WadlGeneratorLoader {
    private static final Logger LOGGER = Logger.getLogger(WadlGeneratorLoader.class.getName());

    WadlGeneratorLoader() {
    }

    static WadlGenerator loadWadlGenerators(List<WadlGenerator> wadlGenerators) throws Exception {
        WadlGenerator wadlGenerator = new WadlGeneratorJAXBGrammarGenerator();
        if (wadlGenerators != null && !wadlGenerators.isEmpty()) {
            for (WadlGenerator generator : wadlGenerators) {
                generator.setWadlGeneratorDelegate(wadlGenerator);
                wadlGenerator = generator;
            }
        }
        wadlGenerator.init();
        return wadlGenerator;
    }

    static WadlGenerator loadWadlGeneratorDescriptions(WadlGeneratorDescription ... wadlGeneratorDescriptions) throws Exception {
        List<WadlGeneratorDescription> list = wadlGeneratorDescriptions != null ? Arrays.asList(wadlGeneratorDescriptions) : null;
        return WadlGeneratorLoader.loadWadlGeneratorDescriptions(list);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static WadlGenerator loadWadlGeneratorDescriptions(List<WadlGeneratorDescription> wadlGeneratorDescriptions) throws Exception {
        WadlGenerator wadlGenerator = new WadlGeneratorJAXBGrammarGenerator();
        CallbackList callbacks = new CallbackList();
        try {
            if (wadlGeneratorDescriptions != null && !wadlGeneratorDescriptions.isEmpty()) {
                for (WadlGeneratorDescription wadlGeneratorDescription : wadlGeneratorDescriptions) {
                    WadlGeneratorControl control = WadlGeneratorLoader.loadWadlGenerator(wadlGeneratorDescription, wadlGenerator);
                    wadlGenerator = control.wadlGenerator;
                    callbacks.add(control.callback);
                }
            }
            wadlGenerator.init();
        }
        finally {
            callbacks.callback();
        }
        return wadlGenerator;
    }

    private static WadlGeneratorControl loadWadlGenerator(WadlGeneratorDescription wadlGeneratorDescription, WadlGenerator wadlGeneratorDelegate) throws Exception {
        LOGGER.info("Loading wadlGenerator " + wadlGeneratorDescription.getGeneratorClass().getName());
        WadlGenerator generator = wadlGeneratorDescription.getGeneratorClass().newInstance();
        generator.setWadlGeneratorDelegate(wadlGeneratorDelegate);
        CallbackList callbacks = null;
        if (wadlGeneratorDescription.getProperties() != null && !wadlGeneratorDescription.getProperties().isEmpty()) {
            callbacks = new CallbackList();
            for (Map.Entry<Object, Object> entry : wadlGeneratorDescription.getProperties().entrySet()) {
                Callback callback = WadlGeneratorLoader.setProperty(generator, entry.getKey().toString(), entry.getValue());
                callbacks.add(callback);
            }
        }
        return new WadlGeneratorControl(generator, callbacks);
    }

    private static Callback setProperty(Object generator, String propertyName, Object propertyValue) throws Exception {
        Callback result = null;
        String methodName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        Method setterMethod = WadlGeneratorLoader.getMethodByName(methodName, generator.getClass());
        if (setterMethod.getParameterTypes().length != 1) {
            throw new RuntimeException("Method " + methodName + " is not a setter, it does not expect exactly one parameter, but " + setterMethod.getParameterTypes().length);
        }
        Class<?> paramClazz = setterMethod.getParameterTypes()[0];
        if (paramClazz.isAssignableFrom(propertyValue.getClass())) {
            setterMethod.invoke(generator, propertyValue);
        } else if (File.class.equals(paramClazz) && propertyValue instanceof String) {
            LOGGER.warning("Configuring the " + setterMethod.getDeclaringClass().getSimpleName() + " with the file based property " + propertyName + " is deprecated and will be removed in future versions of jersey! You should use the InputStream based property instead.");
            String filename = propertyValue.toString();
            if (filename.startsWith("classpath:")) {
                String strippedFilename = filename.substring("classpath:".length());
                URL resource = generator.getClass().getResource(strippedFilename);
                if (resource == null) {
                    throw new RuntimeException("The file '" + strippedFilename + "' does not exist in the classpath. It's loaded by the generator class, so if you use a relative filename it's relative to the generator class, otherwise you might want to load it via an absolute classpath reference like classpath:/somefile.xml");
                }
                File file = new File(resource.toURI());
                setterMethod.invoke(generator, file);
            } else {
                setterMethod.invoke(generator, new File(filename));
            }
        } else if (InputStream.class.equals(paramClazz) && propertyValue instanceof String) {
            InputStream is;
            final String resource = propertyValue.toString();
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            if (loader == null) {
                loader = WadlGeneratorLoader.class.getClassLoader();
            }
            if ((is = loader.getResourceAsStream(resource)) == null) {
                String message = "The resource '" + resource + "' does not exist.";
                throw new RuntimeException(message);
            }
            result = new Callback(){

                @Override
                public void callback() {
                    try {
                        is.close();
                    }
                    catch (IOException e) {
                        LOGGER.log(Level.WARNING, "Could not close InputStream from resource " + resource, e);
                    }
                }
            };
            try {
                setterMethod.invoke(generator, is);
            }
            catch (Exception e) {
                is.close();
                throw e;
            }
        } else {
            Constructor<?> paramTypeConstructor = paramClazz.getConstructor(propertyValue.getClass());
            if (paramTypeConstructor != null) {
                Object typedPropertyValue = paramTypeConstructor.newInstance(propertyValue);
                setterMethod.invoke(generator, typedPropertyValue);
            } else {
                throw new RuntimeException("The property '" + propertyName + "' could not be set because the expected parameter is neither of type " + propertyValue.getClass() + " nor of any type that provides a constructor expecting a " + propertyValue.getClass() + ". The expected parameter is of type " + paramClazz.getName());
            }
        }
        return result;
    }

    private static Method getMethodByName(String methodName, Class<?> clazz) {
        for (Method method : clazz.getMethods()) {
            if (!method.getName().equals(methodName)) continue;
            return method;
        }
        throw new RuntimeException("Method '" + methodName + "' not found for class " + clazz.getName());
    }

    private static class CallbackList
    extends ArrayList<Callback>
    implements Callback {
        private static final long serialVersionUID = 1L;

        private CallbackList() {
        }

        @Override
        public void callback() {
            for (Callback callback : this) {
                callback.callback();
            }
        }

        @Override
        public boolean add(Callback e) {
            return e != null ? super.add(e) : false;
        }
    }

    private static interface Callback {
        public void callback();
    }

    private static class WadlGeneratorControl {
        WadlGenerator wadlGenerator;
        Callback callback;

        public WadlGeneratorControl(WadlGenerator wadlGenerator, Callback callback) {
            this.wadlGenerator = wadlGenerator;
            this.callback = callback;
        }
    }
}

