/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.FrameworkUtil
 *  org.osgi.framework.ServiceReference
 */
package aQute.bnd.testing;

import aQute.bnd.make.component.ComponentAnnotationReader;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Clazz;
import aQute.bnd.osgi.URLResource;
import aQute.lib.collections.MultiMap;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public class DSTestWiring {
    static Pattern REFERENCE = Pattern.compile("([^/]+)/([^/]+)(?:/([^/]+))?");
    BundleContext context = null;
    final MultiMap<Class<?>, Component<?>> map;
    final Set<Component<?>> components;
    final List<Component<?>> ordered;

    public DSTestWiring() {
        Bundle b = FrameworkUtil.getBundle(DSTestWiring.class);
        if (b != null) {
            this.context = b.getBundleContext();
        }
        this.map = new MultiMap();
        this.components = new HashSet();
        this.ordered = new ArrayList();
    }

    public void setContext(BundleContext context) {
        this.context = context;
    }

    public void wire() throws Exception {
        for (Component<?> c : this.components) {
            c.wire(this.ordered);
        }
    }

    public <T> Component<T> add(Class<T> type) throws Exception {
        Component c = new Component();
        c.type = type;
        c.index(type);
        this.components.add(c);
        return c;
    }

    public Component<?> add(String cname) throws ClassNotFoundException, Exception {
        try {
            return this.add((Object)this.getClass().getClassLoader().loadClass(cname));
        }
        catch (ClassNotFoundException cnfe) {
            if (this.context != null) {
                for (Bundle b : this.context.getBundles()) {
                    try {
                        Class c = b.loadClass(cname);
                        return this.add((Object)c);
                    }
                    catch (ClassNotFoundException e) {
                    }
                }
            }
            throw cnfe;
        }
    }

    public <T> Component<T> add(T instance) throws Exception {
        return this.add((T)instance.getClass()).instance((Class<?>)instance);
    }

    public <T> T get(Class<T> c) {
        List components = (List)this.map.get(c);
        if (components == null || components.size() == 0) {
            return null;
        }
        return c.cast(((Component)components.get((int)0)).instance);
    }

    public class Component<T> {
        Class<T> type;
        T instance;
        Map<String, Object> properties = new HashMap<String, Object>();
        boolean wiring;
        Method activate;
        Method deactivate;
        List<Reference> references = new ArrayList<Reference>();

        T wire(List<Component<?>> ordered) throws Exception {
            URL url;
            if (this.instance == null) {
                this.instance = this.type.getConstructor(new Class[0]).newInstance(new Object[0]);
            }
            if (ordered.contains(this)) {
                return this.instance;
            }
            if (this.wiring) {
                throw new RuntimeException("Cycle " + this.type);
            }
            this.wiring = true;
            ClassLoader loader = this.type.getClassLoader();
            if (loader != null && (url = loader.getResource(this.type.getName().replace('.', '/') + ".class")) != null) {
                this.doReferences(url);
                block0: for (Reference ref : this.references) {
                    Method m = ref.set;
                    Class<?> requested = m.getParameterTypes()[0];
                    List refComp = (List)DSTestWiring.this.map.get(requested);
                    if (refComp == null || refComp.isEmpty()) {
                        if (ref.optional) continue;
                        if (DSTestWiring.this.context != null) {
                            ServiceReference[] refs = DSTestWiring.this.context.getServiceReferences(requested.getName(), ref.target);
                            for (int i = 1; i < 30 && refs == null; ++i) {
                                Thread.sleep(100 * i + 1);
                                refs = DSTestWiring.this.context.getServiceReferences(requested.getName(), ref.target);
                            }
                            if (refs != null && refs.length > 0) {
                                for (ServiceReference r : refs) {
                                    Object o = DSTestWiring.this.context.getService(r);
                                    m.setAccessible(true);
                                    m.invoke(this.instance, o);
                                    if (!ref.multiple) continue block0;
                                }
                                continue;
                            }
                        }
                        throw new IllegalStateException(this.type + " requires at least one component for " + ref.name + " of type " + requested);
                    }
                    for (Component c : refComp) {
                        m.setAccessible(true);
                        m.invoke(this.instance, c.wire(ordered));
                        if (ref.multiple) continue;
                        continue block0;
                    }
                }
                if (this.activate != null) {
                    this.activate.setAccessible(true);
                    Class<?>[] types = this.activate.getParameterTypes();
                    Object[] parameters = new Object[types.length];
                    for (int i = 0; i < types.length; ++i) {
                        if (Map.class.isAssignableFrom(types[i])) {
                            parameters[i] = this.properties;
                            continue;
                        }
                        if (DSTestWiring.this.map.containsKey(types[i])) {
                            parameters[i] = ((Component)((List)DSTestWiring.this.map.get(types[i])).get((int)0)).instance;
                            continue;
                        }
                        throw new IllegalArgumentException("Not a pojo " + this.activate.getDeclaringClass() + ", requires " + types[i]);
                    }
                    this.activate.invoke(this.instance, parameters);
                }
            }
            ordered.add(this);
            return this.instance;
        }

        private void doReferences(URL url) throws Exception {
            try (Analyzer a = new Analyzer();
                 URLResource resource = new URLResource(url);){
                Clazz clazz = new Clazz(a, "", resource);
                Map<String, String> d = ComponentAnnotationReader.getDefinition(clazz);
                for (String key : d.keySet()) {
                    if ("activate:".equals(key)) {
                        this.activate = this.findMethod(d.get(key));
                        continue;
                    }
                    if ("deactivate:".equals(key)) {
                        this.deactivate = this.findMethod(d.get(key));
                        continue;
                    }
                    Matcher matcher = REFERENCE.matcher(key);
                    if (!matcher.matches()) continue;
                    Reference r = new Reference();
                    r.name = matcher.group(1);
                    r.set = this.findMethod(matcher.group(2));
                    r.unset = this.findMethod(matcher.group(3));
                    String type = d.get(key);
                    if (type.endsWith("*")) {
                        r.multiple = true;
                        r.optional = true;
                        r.dynamic = true;
                    } else if (type.endsWith("?")) {
                        r.multiple = false;
                        r.optional = true;
                        r.dynamic = true;
                    } else if (type.endsWith("+")) {
                        r.multiple = true;
                        r.optional = false;
                        r.dynamic = true;
                    } else {
                        r.multiple = false;
                        r.optional = false;
                        r.dynamic = false;
                    }
                    this.references.add(r);
                }
            }
        }

        private Method findMethod(String group) {
            for (Method m : this.type.getDeclaredMethods()) {
                if (!m.getName().equals(group)) continue;
                return m;
            }
            return null;
        }

        public Component<T> $(String key, Object value) {
            this.properties.put(key, value);
            return this;
        }

        public Component<T> instance(T x) {
            this.instance = x;
            return this;
        }

        void index(Class<?> c) {
            while (c != null && c != Object.class) {
                DSTestWiring.this.map.add(c, this);
                for (Class<?> interf : c.getInterfaces()) {
                    this.index(interf);
                }
                c = c.getSuperclass();
            }
        }

        public String toString() {
            return "Component [" + (this.type != null ? "type=" + this.type + ", " : "") + (this.activate != null ? "activate=" + this.activate + ", " : "") + (this.deactivate != null ? "deactivate=" + this.deactivate + ", " : "") + (this.references != null ? "references=" + this.references : "") + "]";
        }
    }

    public static class Reference {
        String name;
        Method set;
        Method unset;
        boolean multiple;
        boolean optional;
        boolean dynamic;
        String target;

        public String toString() {
            return "Reference [" + (this.name != null ? "name=" + this.name + ", " : "") + "multiple=" + this.multiple + ", " + (this.target != null ? "target=" + this.target : "") + "]";
        }
    }
}

