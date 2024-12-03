/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.reflection;

import groovy.lang.GroovyRuntimeException;
import groovy.lang.MetaMethod;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.reflection.CachedClass;

public abstract class GeneratedMetaMethod
extends MetaMethod {
    private final String name;
    private final CachedClass declaringClass;
    private final Class returnType;

    public GeneratedMetaMethod(String name, CachedClass declaringClass, Class returnType, Class[] parameters) {
        this.name = name;
        this.declaringClass = declaringClass;
        this.returnType = returnType;
        this.nativeParamTypes = parameters;
    }

    @Override
    public int getModifiers() {
        return 1;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Class getReturnType() {
        return this.returnType;
    }

    @Override
    public CachedClass getDeclaringClass() {
        return this.declaringClass;
    }

    public static class DgmMethodRecord
    implements Serializable {
        private static final long serialVersionUID = -5639988016452884450L;
        public String className;
        public String methodName;
        public Class returnType;
        public Class[] parameters;
        private static final Class[] PRIMITIVE_CLASSES = new Class[]{Boolean.TYPE, Character.TYPE, Byte.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Double.TYPE, Float.TYPE, Void.TYPE, boolean[].class, char[].class, byte[].class, short[].class, int[].class, long[].class, double[].class, float[].class, Object[].class, String[].class, Class[].class, Byte[].class, CharSequence[].class};

        public static void saveDgmInfo(List<DgmMethodRecord> records, String file) throws IOException {
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
            LinkedHashMap<String, Integer> classes = new LinkedHashMap<String, Integer>();
            int nextClassId = 0;
            for (Class primitive : PRIMITIVE_CLASSES) {
                classes.put(primitive.getName(), nextClassId++);
            }
            for (DgmMethodRecord dgmMethodRecord : records) {
                String name = dgmMethodRecord.returnType.getName();
                Integer id = (Integer)classes.get(name);
                if (id == null) {
                    id = nextClassId++;
                    classes.put(name, id);
                }
                for (int i = 0; i < dgmMethodRecord.parameters.length; ++i) {
                    name = dgmMethodRecord.parameters[i].getName();
                    id = (Integer)classes.get(name);
                    if (id != null) continue;
                    id = nextClassId++;
                    classes.put(name, id);
                }
            }
            for (Map.Entry entry : classes.entrySet()) {
                out.writeUTF((String)entry.getKey());
                out.writeInt((Integer)entry.getValue());
            }
            out.writeUTF("");
            out.writeInt(records.size());
            for (DgmMethodRecord dgmMethodRecord : records) {
                out.writeUTF(dgmMethodRecord.className);
                out.writeUTF(dgmMethodRecord.methodName);
                out.writeInt((Integer)classes.get(dgmMethodRecord.returnType.getName()));
                out.writeInt(dgmMethodRecord.parameters.length);
                for (int i = 0; i < dgmMethodRecord.parameters.length; ++i) {
                    Integer key = (Integer)classes.get(dgmMethodRecord.parameters[i].getName());
                    out.writeInt(key);
                }
            }
            out.close();
        }

        public static List<DgmMethodRecord> loadDgmInfo() throws IOException {
            String name;
            ClassLoader loader = DgmMethodRecord.class.getClassLoader();
            DataInputStream in = new DataInputStream(new BufferedInputStream(loader.getResourceAsStream("META-INF/dgminfo")));
            HashMap classes = new HashMap();
            for (int i = 0; i < PRIMITIVE_CLASSES.length; ++i) {
                classes.put(i, PRIMITIVE_CLASSES[i]);
            }
            int skip = 0;
            while ((name = in.readUTF()).length() != 0) {
                int key = in.readInt();
                if (skip++ < PRIMITIVE_CLASSES.length) continue;
                Class<?> cls = null;
                try {
                    cls = loader.loadClass(name);
                }
                catch (ClassNotFoundException e) {
                    continue;
                }
                classes.put(key, cls);
            }
            int size = in.readInt();
            ArrayList<DgmMethodRecord> res = new ArrayList<DgmMethodRecord>(size);
            for (int i = 0; i != size; ++i) {
                boolean skipRecord = false;
                DgmMethodRecord record = new DgmMethodRecord();
                record.className = in.readUTF();
                record.methodName = in.readUTF();
                record.returnType = (Class)classes.get(in.readInt());
                if (record.returnType == null) {
                    skipRecord = true;
                }
                int psize = in.readInt();
                record.parameters = new Class[psize];
                for (int j = 0; j < record.parameters.length; ++j) {
                    record.parameters[j] = (Class)classes.get(in.readInt());
                    if (record.parameters[j] != null) continue;
                    skipRecord = true;
                }
                if (skipRecord) continue;
                res.add(record);
            }
            in.close();
            return res;
        }
    }

    public static class Proxy
    extends GeneratedMetaMethod {
        private volatile MetaMethod proxy;
        private final String className;

        public Proxy(String className, String name, CachedClass declaringClass, Class returnType, Class[] parameters) {
            super(name, declaringClass, returnType, parameters);
            this.className = className;
        }

        @Override
        public boolean isValidMethod(Class[] arguments) {
            return this.proxy().isValidMethod(arguments);
        }

        @Override
        public Object doMethodInvoke(Object object, Object[] argumentArray) {
            return this.proxy().doMethodInvoke(object, argumentArray);
        }

        @Override
        public Object invoke(Object object, Object[] arguments) {
            return this.proxy().invoke(object, arguments);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public final MetaMethod proxy() {
            if (this.proxy == null) {
                Proxy proxy = this;
                synchronized (proxy) {
                    if (this.proxy == null) {
                        this.createProxy();
                    }
                }
            }
            return this.proxy;
        }

        private void createProxy() {
            try {
                Class<?> aClass = this.getClass().getClassLoader().loadClass(this.className.replace('/', '.'));
                Constructor<?> constructor = aClass.getConstructor(String.class, CachedClass.class, Class.class, Class[].class);
                this.proxy = (MetaMethod)constructor.newInstance(this.getName(), this.getDeclaringClass(), this.getReturnType(), this.getNativeParameterTypes());
            }
            catch (Throwable t) {
                t.printStackTrace();
                throw new GroovyRuntimeException("Failed to create DGM method proxy : " + t, t);
            }
        }
    }
}

