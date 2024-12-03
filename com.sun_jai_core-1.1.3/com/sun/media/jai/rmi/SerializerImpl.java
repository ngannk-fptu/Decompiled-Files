/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.rmi;

import com.sun.media.jai.rmi.JaiI18N;
import com.sun.media.jai.util.ImageUtil;
import java.awt.RenderingHints;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.media.jai.remote.RemoteImagingException;
import javax.media.jai.remote.SerializableState;
import javax.media.jai.remote.Serializer;
import javax.media.jai.remote.SerializerFactory;
import javax.media.jai.util.ImagingException;
import javax.media.jai.util.ImagingListener;

public final class SerializerImpl
implements Serializer {
    private Class theClass;
    private boolean areSubclassesPermitted;
    private Constructor ctor;
    static /* synthetic */ Class class$com$sun$media$jai$rmi$ColorModelState;
    static /* synthetic */ Class class$com$sun$media$jai$rmi$DataBufferState;
    static /* synthetic */ Class class$com$sun$media$jai$rmi$HashSetState;
    static /* synthetic */ Class class$com$sun$media$jai$rmi$HashtableState;
    static /* synthetic */ Class class$com$sun$media$jai$rmi$RasterState;
    static /* synthetic */ Class class$com$sun$media$jai$rmi$RenderedImageState;
    static /* synthetic */ Class class$com$sun$media$jai$rmi$RenderContextState;
    static /* synthetic */ Class class$com$sun$media$jai$rmi$RenderingHintsState;
    static /* synthetic */ Class class$com$sun$media$jai$rmi$RenderingKeyState;
    static /* synthetic */ Class class$com$sun$media$jai$rmi$SampleModelState;
    static /* synthetic */ Class class$com$sun$media$jai$rmi$VectorState;
    static /* synthetic */ Class class$com$sun$media$jai$rmi$ShapeState;
    static /* synthetic */ Class class$com$sun$media$jai$rmi$SerializableStateImpl;
    static /* synthetic */ Class class$com$sun$media$jai$rmi$SerializerImpl;
    static /* synthetic */ Class class$java$lang$Class;
    static /* synthetic */ Class class$java$lang$Object;
    static /* synthetic */ Class class$java$awt$RenderingHints;

    public static final void registerSerializers() {
        SerializerImpl.registerSerializers(class$com$sun$media$jai$rmi$ColorModelState == null ? (class$com$sun$media$jai$rmi$ColorModelState = SerializerImpl.class$("com.sun.media.jai.rmi.ColorModelState")) : class$com$sun$media$jai$rmi$ColorModelState);
        SerializerImpl.registerSerializers(class$com$sun$media$jai$rmi$DataBufferState == null ? (class$com$sun$media$jai$rmi$DataBufferState = SerializerImpl.class$("com.sun.media.jai.rmi.DataBufferState")) : class$com$sun$media$jai$rmi$DataBufferState);
        SerializerImpl.registerSerializers(class$com$sun$media$jai$rmi$HashSetState == null ? (class$com$sun$media$jai$rmi$HashSetState = SerializerImpl.class$("com.sun.media.jai.rmi.HashSetState")) : class$com$sun$media$jai$rmi$HashSetState);
        SerializerImpl.registerSerializers(class$com$sun$media$jai$rmi$HashtableState == null ? (class$com$sun$media$jai$rmi$HashtableState = SerializerImpl.class$("com.sun.media.jai.rmi.HashtableState")) : class$com$sun$media$jai$rmi$HashtableState);
        SerializerImpl.registerSerializers(class$com$sun$media$jai$rmi$RasterState == null ? (class$com$sun$media$jai$rmi$RasterState = SerializerImpl.class$("com.sun.media.jai.rmi.RasterState")) : class$com$sun$media$jai$rmi$RasterState);
        SerializerImpl.registerSerializers(class$com$sun$media$jai$rmi$RenderedImageState == null ? (class$com$sun$media$jai$rmi$RenderedImageState = SerializerImpl.class$("com.sun.media.jai.rmi.RenderedImageState")) : class$com$sun$media$jai$rmi$RenderedImageState);
        SerializerImpl.registerSerializers(class$com$sun$media$jai$rmi$RenderContextState == null ? (class$com$sun$media$jai$rmi$RenderContextState = SerializerImpl.class$("com.sun.media.jai.rmi.RenderContextState")) : class$com$sun$media$jai$rmi$RenderContextState);
        SerializerImpl.registerSerializers(class$com$sun$media$jai$rmi$RenderingHintsState == null ? (class$com$sun$media$jai$rmi$RenderingHintsState = SerializerImpl.class$("com.sun.media.jai.rmi.RenderingHintsState")) : class$com$sun$media$jai$rmi$RenderingHintsState);
        SerializerImpl.registerSerializers(class$com$sun$media$jai$rmi$RenderingKeyState == null ? (class$com$sun$media$jai$rmi$RenderingKeyState = SerializerImpl.class$("com.sun.media.jai.rmi.RenderingKeyState")) : class$com$sun$media$jai$rmi$RenderingKeyState);
        SerializerImpl.registerSerializers(class$com$sun$media$jai$rmi$SampleModelState == null ? (class$com$sun$media$jai$rmi$SampleModelState = SerializerImpl.class$("com.sun.media.jai.rmi.SampleModelState")) : class$com$sun$media$jai$rmi$SampleModelState);
        SerializerImpl.registerSerializers(class$com$sun$media$jai$rmi$VectorState == null ? (class$com$sun$media$jai$rmi$VectorState = SerializerImpl.class$("com.sun.media.jai.rmi.VectorState")) : class$com$sun$media$jai$rmi$VectorState);
        SerializerImpl.registerSerializers(class$com$sun$media$jai$rmi$ShapeState == null ? (class$com$sun$media$jai$rmi$ShapeState = SerializerImpl.class$("com.sun.media.jai.rmi.ShapeState")) : class$com$sun$media$jai$rmi$ShapeState);
    }

    private static void registerSerializers(Class ssi) {
        String message;
        String message2;
        if (ssi == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (!(class$com$sun$media$jai$rmi$SerializableStateImpl == null ? (class$com$sun$media$jai$rmi$SerializableStateImpl = SerializerImpl.class$("com.sun.media.jai.rmi.SerializableStateImpl")) : class$com$sun$media$jai$rmi$SerializableStateImpl).isAssignableFrom(ssi)) {
            throw new IllegalArgumentException(JaiI18N.getString("SerializerImpl0"));
        }
        ImagingListener listener = ImageUtil.getImagingListener((RenderingHints)null);
        Class[] classes = null;
        try {
            Method m1 = ssi.getMethod("getSupportedClasses", null);
            classes = (Class[])m1.invoke(null, null);
        }
        catch (NoSuchMethodException e) {
            message2 = JaiI18N.getString("SerializerImpl1");
            listener.errorOccurred(message2, new RemoteImagingException(message2, e), class$com$sun$media$jai$rmi$SerializerImpl == null ? (class$com$sun$media$jai$rmi$SerializerImpl = SerializerImpl.class$("com.sun.media.jai.rmi.SerializerImpl")) : class$com$sun$media$jai$rmi$SerializerImpl, false);
        }
        catch (IllegalAccessException e) {
            message2 = JaiI18N.getString("SerializerImpl1");
            listener.errorOccurred(message2, new RemoteImagingException(message2, e), class$com$sun$media$jai$rmi$SerializerImpl == null ? (class$com$sun$media$jai$rmi$SerializerImpl = SerializerImpl.class$("com.sun.media.jai.rmi.SerializerImpl")) : class$com$sun$media$jai$rmi$SerializerImpl, false);
        }
        catch (InvocationTargetException e) {
            message2 = JaiI18N.getString("SerializerImpl1");
            listener.errorOccurred(message2, new RemoteImagingException(message2, e), class$com$sun$media$jai$rmi$SerializerImpl == null ? (class$com$sun$media$jai$rmi$SerializerImpl = SerializerImpl.class$("com.sun.media.jai.rmi.SerializerImpl")) : class$com$sun$media$jai$rmi$SerializerImpl, false);
        }
        boolean supportsSubclasses = false;
        try {
            Method m2 = ssi.getMethod("permitsSubclasses", null);
            Boolean b = (Boolean)m2.invoke(null, null);
            supportsSubclasses = b;
        }
        catch (NoSuchMethodException e) {
            message = JaiI18N.getString("SerializerImpl4");
            listener.errorOccurred(message, new RemoteImagingException(message, e), class$com$sun$media$jai$rmi$SerializerImpl == null ? (class$com$sun$media$jai$rmi$SerializerImpl = SerializerImpl.class$("com.sun.media.jai.rmi.SerializerImpl")) : class$com$sun$media$jai$rmi$SerializerImpl, false);
        }
        catch (IllegalAccessException e) {
            message = JaiI18N.getString("SerializerImpl4");
            listener.errorOccurred(message, new RemoteImagingException(message, e), class$com$sun$media$jai$rmi$SerializerImpl == null ? (class$com$sun$media$jai$rmi$SerializerImpl = SerializerImpl.class$("com.sun.media.jai.rmi.SerializerImpl")) : class$com$sun$media$jai$rmi$SerializerImpl, false);
        }
        catch (InvocationTargetException e) {
            message = JaiI18N.getString("SerializerImpl4");
            listener.errorOccurred(message, new RemoteImagingException(message, e), class$com$sun$media$jai$rmi$SerializerImpl == null ? (class$com$sun$media$jai$rmi$SerializerImpl = SerializerImpl.class$("com.sun.media.jai.rmi.SerializerImpl")) : class$com$sun$media$jai$rmi$SerializerImpl, false);
        }
        int numClasses = classes.length;
        for (int i = 0; i < numClasses; ++i) {
            SerializerImpl s = new SerializerImpl(ssi, classes[i], supportsSubclasses);
            SerializerFactory.registerSerializer(s);
        }
    }

    protected SerializerImpl(Class ssi, Class c, boolean areSubclassesPermitted) {
        this.theClass = c;
        this.areSubclassesPermitted = areSubclassesPermitted;
        try {
            Class[] paramTypes = new Class[]{class$java$lang$Class == null ? (class$java$lang$Class = SerializerImpl.class$("java.lang.Class")) : class$java$lang$Class, class$java$lang$Object == null ? (class$java$lang$Object = SerializerImpl.class$("java.lang.Object")) : class$java$lang$Object, class$java$awt$RenderingHints == null ? (class$java$awt$RenderingHints = SerializerImpl.class$("java.awt.RenderingHints")) : class$java$awt$RenderingHints};
            this.ctor = ssi.getConstructor(paramTypes);
        }
        catch (NoSuchMethodException e) {
            String message = this.theClass.getName() + ": " + JaiI18N.getString("SerializerImpl2");
            this.sendExceptionToListener(message, new RemoteImagingException(message, e));
        }
    }

    public SerializableState getState(Object o, RenderingHints h) {
        Object state = null;
        try {
            state = this.ctor.newInstance(this.theClass, o, h);
        }
        catch (InstantiationException e) {
            String message = this.theClass.getName() + ": " + JaiI18N.getString("SerializerImpl3");
            this.sendExceptionToListener(message, new RemoteImagingException(message, e));
        }
        catch (IllegalAccessException e) {
            String message = this.theClass.getName() + ": " + JaiI18N.getString("SerializerImpl3");
            this.sendExceptionToListener(message, new RemoteImagingException(message, e));
        }
        catch (InvocationTargetException e) {
            String message = this.theClass.getName() + ": " + JaiI18N.getString("SerializerImpl3");
            this.sendExceptionToListener(message, new RemoteImagingException(message, e));
        }
        return state;
    }

    public Class getSupportedClass() {
        return this.theClass;
    }

    public boolean permitsSubclasses() {
        return this.areSubclassesPermitted;
    }

    private void sendExceptionToListener(String message, Exception e) {
        ImagingListener listener = ImageUtil.getImagingListener((RenderingHints)null);
        listener.errorOccurred(message, new ImagingException(message, e), this, false);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

