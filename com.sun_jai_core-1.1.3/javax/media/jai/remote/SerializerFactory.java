/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.remote;

import com.sun.media.jai.rmi.InterfaceState;
import com.sun.media.jai.rmi.SerializerImpl;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;
import javax.media.jai.remote.JaiI18N;
import javax.media.jai.remote.SerSerializer;
import javax.media.jai.remote.SerializableState;
import javax.media.jai.remote.Serializer;

public final class SerializerFactory {
    private static Hashtable repository = new Hashtable();
    private static Serializer serializableSerializer = new SerSerializer();
    static final SerializableState NULL_STATE = new SerializableState(){

        public Class getObjectClass() {
            return class$java$lang$Object == null ? (class$java$lang$Object = SerializerFactory.class$("java.lang.Object")) : class$java$lang$Object;
        }

        public Object getObject() {
            return null;
        }
    };
    static /* synthetic */ Class class$java$lang$Object;
    static /* synthetic */ Class class$java$io$Serializable;

    protected SerializerFactory() {
    }

    public static synchronized void registerSerializer(Serializer s) {
        if (s == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        Class c = s.getSupportedClass();
        if (repository.containsKey(c)) {
            Object value = repository.get(c);
            if (value instanceof Vector) {
                ((Vector)value).add(0, s);
            } else {
                Vector<Serializer> v = new Vector<Serializer>(2);
                v.add(0, s);
                v.add(1, (Serializer)value);
                repository.put(c, v);
            }
        } else {
            repository.put(c, s);
        }
    }

    public static synchronized void unregisterSerializer(Serializer s) {
        if (s == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        Class c = s.getSupportedClass();
        Object value = repository.get(c);
        if (value != null) {
            if (value instanceof Vector) {
                Vector v = (Vector)value;
                v.remove(s);
                if (v.size() == 1) {
                    repository.put(c, v.get(0));
                }
            } else {
                repository.remove(c);
            }
        }
    }

    public static synchronized Serializer[] getSerializers(Class c) {
        if (c == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        Object value = repository.get(c);
        Serializer[] result = null;
        if (value == null && (class$java$io$Serializable == null ? (class$java$io$Serializable = SerializerFactory.class$("java.io.Serializable")) : class$java$io$Serializable).isAssignableFrom(c)) {
            result = new Serializer[]{serializableSerializer};
        } else if (value instanceof Vector) {
            result = ((Vector)value).toArray(new Serializer[0]);
        } else if (value != null) {
            result = new Serializer[]{(Serializer)value};
        }
        return result;
    }

    public static synchronized Serializer getSerializer(Class c) {
        if (c == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        Object value = repository.get(c);
        if (value == null) {
            Class theClass = c;
            while (theClass != (class$java$lang$Object == null ? SerializerFactory.class$("java.lang.Object") : class$java$lang$Object)) {
                Serializer s;
                Class theSuperclass = theClass.getSuperclass();
                if (SerializerFactory.isSupportedClass(theSuperclass) && (s = SerializerFactory.getSerializer(theSuperclass)).permitsSubclasses()) {
                    value = s;
                    break;
                }
                theClass = theSuperclass;
            }
        }
        if (value == null && (class$java$io$Serializable == null ? (class$java$io$Serializable = SerializerFactory.class$("java.io.Serializable")) : class$java$io$Serializable).isAssignableFrom(c)) {
            value = serializableSerializer;
        }
        return value instanceof Vector ? (Serializer)((Vector)value).get(0) : (Serializer)value;
    }

    public static boolean isSupportedClass(Class c) {
        if (c == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if ((class$java$io$Serializable == null ? (class$java$io$Serializable = SerializerFactory.class$("java.io.Serializable")) : class$java$io$Serializable).isAssignableFrom(c)) {
            return true;
        }
        return repository.containsKey(c);
    }

    public static Class[] getSupportedClasses() {
        Class[] classes = new Class[repository.size() + 1];
        repository.keySet().toArray(classes);
        classes[classes.length - 1] = class$java$io$Serializable == null ? (class$java$io$Serializable = SerializerFactory.class$("java.io.Serializable")) : class$java$io$Serializable;
        return classes;
    }

    public static Class getDeserializedClass(Class c) {
        if (c == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        Class deserializedClass = null;
        if (SerializerFactory.isSupportedClass(c)) {
            deserializedClass = c;
        } else {
            Class theClass = c;
            while (theClass != (class$java$lang$Object == null ? SerializerFactory.class$("java.lang.Object") : class$java$lang$Object)) {
                Serializer s;
                Class theSuperclass = theClass.getSuperclass();
                if (SerializerFactory.isSupportedClass(theSuperclass) && (s = SerializerFactory.getSerializer(theSuperclass)).permitsSubclasses()) {
                    deserializedClass = theSuperclass;
                    break;
                }
                theClass = theSuperclass;
            }
        }
        return deserializedClass;
    }

    public static SerializableState getState(Object o, RenderingHints h) {
        if (o == null) {
            return NULL_STATE;
        }
        Class<?> c = o.getClass();
        SerializableState state = null;
        if (SerializerFactory.isSupportedClass(c)) {
            Serializer s = SerializerFactory.getSerializer(c);
            state = s.getState(o, h);
        } else {
            Class<?> theClass = c;
            while (theClass != (class$java$lang$Object == null ? SerializerFactory.class$("java.lang.Object") : class$java$lang$Object)) {
                Serializer s;
                Class<?> theSuperclass = theClass.getSuperclass();
                if (SerializerFactory.isSupportedClass(theSuperclass) && (s = SerializerFactory.getSerializer(theSuperclass)).permitsSubclasses()) {
                    state = s.getState(o, h);
                    break;
                }
                theClass = theSuperclass;
            }
            if (state == null) {
                int numSupportedInterfaces;
                Class[] interfaces = SerializerFactory.getInterfaces(c);
                Vector<Serializer> serializers = null;
                int numInterfaces = interfaces == null ? 0 : interfaces.length;
                for (int i = 0; i < numInterfaces; ++i) {
                    Class iface = interfaces[i];
                    if (!SerializerFactory.isSupportedClass(iface)) continue;
                    if (serializers == null) {
                        serializers = new Vector<Serializer>();
                    }
                    serializers.add(SerializerFactory.getSerializer(iface));
                }
                int n = numSupportedInterfaces = serializers == null ? 0 : serializers.size();
                if (numSupportedInterfaces == 0) {
                    throw new IllegalArgumentException(JaiI18N.getString("SerializerFactory1"));
                }
                if (numSupportedInterfaces == 1) {
                    state = ((Serializer)serializers.get(0)).getState(o, h);
                } else {
                    Serializer[] sArray = serializers.toArray(new Serializer[0]);
                    state = new InterfaceState(o, sArray, h);
                }
            }
        }
        return state;
    }

    private static Class[] getInterfaces(Class c) {
        if (c == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        ArrayList interfaces = new ArrayList();
        for (Class laClasse = c; laClasse != (class$java$lang$Object == null ? SerializerFactory.class$("java.lang.Object") : class$java$lang$Object); laClasse = laClasse.getSuperclass()) {
            Class<?>[] iFaces = laClasse.getInterfaces();
            if (iFaces == null) continue;
            for (int i = 0; i < iFaces.length; ++i) {
                interfaces.add(iFaces[i]);
            }
        }
        return interfaces.size() == 0 ? null : interfaces.toArray(new Class[interfaces.size()]);
    }

    public static final SerializableState getState(Object o) {
        return SerializerFactory.getState(o, null);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        SerializerImpl.registerSerializers();
    }
}

