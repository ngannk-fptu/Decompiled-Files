/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

import com.atlassian.lucene36.analysis.tokenattributes.CharTermAttributeImpl;
import com.atlassian.lucene36.analysis.tokenattributes.TermAttribute;
import com.atlassian.lucene36.util.Attribute;
import com.atlassian.lucene36.util.AttributeImpl;
import com.atlassian.lucene36.util.AttributeReflector;
import com.atlassian.lucene36.util.WeakIdentityMap;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class AttributeSource {
    private final Map<Class<? extends Attribute>, AttributeImpl> attributes;
    private final Map<Class<? extends AttributeImpl>, AttributeImpl> attributeImpls;
    private final State[] currentState;
    private final AttributeFactory factory;
    private static final WeakIdentityMap<Class<? extends AttributeImpl>, LinkedList<WeakReference<Class<? extends Attribute>>>> knownImplClasses = WeakIdentityMap.newConcurrentHashMap();

    public AttributeSource() {
        this(AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY);
    }

    public AttributeSource(AttributeSource input) {
        if (input == null) {
            throw new IllegalArgumentException("input AttributeSource must not be null");
        }
        this.attributes = input.attributes;
        this.attributeImpls = input.attributeImpls;
        this.currentState = input.currentState;
        this.factory = input.factory;
    }

    public AttributeSource(AttributeFactory factory) {
        this.attributes = new LinkedHashMap<Class<? extends Attribute>, AttributeImpl>();
        this.attributeImpls = new LinkedHashMap<Class<? extends AttributeImpl>, AttributeImpl>();
        this.currentState = new State[1];
        this.factory = factory;
    }

    public AttributeFactory getAttributeFactory() {
        return this.factory;
    }

    public Iterator<Class<? extends Attribute>> getAttributeClassesIterator() {
        return Collections.unmodifiableSet(this.attributes.keySet()).iterator();
    }

    public Iterator<AttributeImpl> getAttributeImplsIterator() {
        final State initState = this.getCurrentState();
        if (initState != null) {
            return new Iterator<AttributeImpl>(){
                private State state;
                {
                    this.state = initState;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public AttributeImpl next() {
                    if (this.state == null) {
                        throw new NoSuchElementException();
                    }
                    AttributeImpl att = this.state.attribute;
                    this.state = this.state.next;
                    return att;
                }

                @Override
                public boolean hasNext() {
                    return this.state != null;
                }
            };
        }
        return Collections.emptySet().iterator();
    }

    static LinkedList<WeakReference<Class<? extends Attribute>>> getAttributeInterfaces(Class<? extends AttributeImpl> clazz) {
        LinkedList<WeakReference<Class<Attribute>>> foundInterfaces = knownImplClasses.get(clazz);
        if (foundInterfaces == null) {
            foundInterfaces = new LinkedList();
            Class<? extends AttributeImpl> actClazz = clazz;
            do {
                for (Class<?> curInterface : actClazz.getInterfaces()) {
                    if (curInterface == Attribute.class || !Attribute.class.isAssignableFrom(curInterface)) continue;
                    foundInterfaces.add(new WeakReference<Class<Attribute>>(curInterface.asSubclass(Attribute.class)));
                }
            } while ((actClazz = actClazz.getSuperclass()) != null);
            knownImplClasses.put(clazz, foundInterfaces);
        }
        return foundInterfaces;
    }

    public void addAttributeImpl(AttributeImpl att) {
        Class<?> clazz = att.getClass();
        if (this.attributeImpls.containsKey(clazz)) {
            return;
        }
        LinkedList<WeakReference<Class<? extends Attribute>>> foundInterfaces = AttributeSource.getAttributeInterfaces(clazz);
        for (WeakReference weakReference : foundInterfaces) {
            Class curInterface = (Class)weakReference.get();
            assert (curInterface != null) : "We have a strong reference on the class holding the interfaces, so they should never get evicted";
            if (this.attributes.containsKey(curInterface)) continue;
            this.currentState[0] = null;
            this.attributes.put(curInterface, att);
            this.attributeImpls.put(clazz, att);
        }
    }

    public <A extends Attribute> A addAttribute(Class<A> attClass) {
        AttributeImpl attImpl = this.attributes.get(attClass);
        if (attImpl == null) {
            if (!attClass.isInterface() || !Attribute.class.isAssignableFrom(attClass)) {
                throw new IllegalArgumentException("addAttribute() only accepts an interface that extends Attribute, but " + attClass.getName() + " does not fulfil this contract.");
            }
            attImpl = this.factory.createAttributeInstance(attClass);
            this.addAttributeImpl(attImpl);
        }
        return (A)((Attribute)attClass.cast(attImpl));
    }

    public boolean hasAttributes() {
        return !this.attributes.isEmpty();
    }

    public boolean hasAttribute(Class<? extends Attribute> attClass) {
        return this.attributes.containsKey(attClass);
    }

    public <A extends Attribute> A getAttribute(Class<A> attClass) {
        AttributeImpl attImpl = this.attributes.get(attClass);
        if (attImpl == null) {
            throw new IllegalArgumentException("This AttributeSource does not have the attribute '" + attClass.getName() + "'.");
        }
        return (A)((Attribute)attClass.cast(attImpl));
    }

    private State getCurrentState() {
        State s = this.currentState[0];
        if (s != null || !this.hasAttributes()) {
            return s;
        }
        s = this.currentState[0] = new State();
        State c = this.currentState[0];
        Iterator<AttributeImpl> it = this.attributeImpls.values().iterator();
        c.attribute = it.next();
        while (it.hasNext()) {
            c = c.next = new State();
            c.attribute = it.next();
        }
        return s;
    }

    public void clearAttributes() {
        State state = this.getCurrentState();
        while (state != null) {
            state.attribute.clear();
            state = state.next;
        }
    }

    public State captureState() {
        State state = this.getCurrentState();
        return state == null ? null : (State)state.clone();
    }

    public void restoreState(State state) {
        if (state == null) {
            return;
        }
        do {
            AttributeImpl targetImpl;
            if ((targetImpl = this.attributeImpls.get(state.attribute.getClass())) == null) {
                throw new IllegalArgumentException("State contains AttributeImpl of type " + state.attribute.getClass().getName() + " that is not in in this AttributeSource");
            }
            state.attribute.copyTo(targetImpl);
        } while ((state = state.next) != null);
    }

    public int hashCode() {
        int code = 0;
        State state = this.getCurrentState();
        while (state != null) {
            code = code * 31 + state.attribute.hashCode();
            state = state.next;
        }
        return code;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof AttributeSource) {
            AttributeSource other = (AttributeSource)obj;
            if (this.hasAttributes()) {
                if (!other.hasAttributes()) {
                    return false;
                }
                if (this.attributeImpls.size() != other.attributeImpls.size()) {
                    return false;
                }
                State thisState = this.getCurrentState();
                State otherState = other.getCurrentState();
                while (thisState != null && otherState != null) {
                    if (otherState.attribute.getClass() != thisState.attribute.getClass() || !otherState.attribute.equals(thisState.attribute)) {
                        return false;
                    }
                    thisState = thisState.next;
                    otherState = otherState.next;
                }
                return true;
            }
            return !other.hasAttributes();
        }
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder().append('(');
        if (this.hasAttributes()) {
            State state = this.getCurrentState();
            while (state != null) {
                if (sb.length() > 1) {
                    sb.append(',');
                }
                sb.append(state.attribute.toString());
                state = state.next;
            }
        }
        return sb.append(')').toString();
    }

    public final String reflectAsString(final boolean prependAttClass) {
        final StringBuilder buffer = new StringBuilder();
        this.reflectWith(new AttributeReflector(){

            @Override
            public void reflect(Class<? extends Attribute> attClass, String key, Object value) {
                if (buffer.length() > 0) {
                    buffer.append(',');
                }
                if (prependAttClass) {
                    buffer.append(attClass.getName()).append('#');
                }
                buffer.append(key).append('=').append(value == null ? "null" : value);
            }
        });
        return buffer.toString();
    }

    public final void reflectWith(AttributeReflector reflector) {
        State state = this.getCurrentState();
        while (state != null) {
            state.attribute.reflectWith(reflector);
            state = state.next;
        }
    }

    public AttributeSource cloneAttributes() {
        AttributeSource clone = new AttributeSource(this.factory);
        if (this.hasAttributes()) {
            State state = this.getCurrentState();
            while (state != null) {
                clone.attributeImpls.put(state.attribute.getClass(), (AttributeImpl)state.attribute.clone());
                state = state.next;
            }
            for (Map.Entry<Class<? extends Attribute>, AttributeImpl> entry : this.attributes.entrySet()) {
                clone.attributes.put(entry.getKey(), clone.attributeImpls.get(entry.getValue().getClass()));
            }
        }
        return clone;
    }

    public final void copyTo(AttributeSource target) {
        State state = this.getCurrentState();
        while (state != null) {
            AttributeImpl targetImpl = target.attributeImpls.get(state.attribute.getClass());
            if (targetImpl == null) {
                throw new IllegalArgumentException("This AttributeSource contains AttributeImpl of type " + state.attribute.getClass().getName() + " that is not in the target");
            }
            state.attribute.copyTo(targetImpl);
            state = state.next;
        }
    }

    public static final class State
    implements Cloneable {
        AttributeImpl attribute;
        State next;

        public Object clone() {
            State clone = new State();
            clone.attribute = (AttributeImpl)this.attribute.clone();
            if (this.next != null) {
                clone.next = (State)this.next.clone();
            }
            return clone;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class AttributeFactory {
        public static final AttributeFactory DEFAULT_ATTRIBUTE_FACTORY = new DefaultAttributeFactory();

        public abstract AttributeImpl createAttributeInstance(Class<? extends Attribute> var1);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        private static final class DefaultAttributeFactory
        extends AttributeFactory {
            private static final WeakIdentityMap<Class<? extends Attribute>, WeakReference<Class<? extends AttributeImpl>>> attClassImplMap = WeakIdentityMap.newConcurrentHashMap();

            private DefaultAttributeFactory() {
            }

            @Override
            public AttributeImpl createAttributeInstance(Class<? extends Attribute> attClass) {
                try {
                    return DefaultAttributeFactory.getClassForInterface(attClass).newInstance();
                }
                catch (InstantiationException e) {
                    throw new IllegalArgumentException("Could not instantiate implementing class for " + attClass.getName());
                }
                catch (IllegalAccessException e) {
                    throw new IllegalArgumentException("Could not instantiate implementing class for " + attClass.getName());
                }
            }

            private static Class<? extends AttributeImpl> getClassForInterface(Class<? extends Attribute> attClass) {
                Class clazz;
                WeakReference<Class<? extends AttributeImpl>> ref = attClassImplMap.get(attClass);
                Class<CharTermAttributeImpl> clazz2 = clazz = ref == null ? null : (Class<CharTermAttributeImpl>)ref.get();
                if (clazz == null) {
                    try {
                        clazz = TermAttribute.class.equals(attClass) ? CharTermAttributeImpl.class : Class.forName(attClass.getName() + "Impl", true, attClass.getClassLoader()).asSubclass(AttributeImpl.class);
                        attClassImplMap.put(attClass, new WeakReference<Class<CharTermAttributeImpl>>(clazz));
                    }
                    catch (ClassNotFoundException e) {
                        throw new IllegalArgumentException("Could not find implementing class for " + attClass.getName());
                    }
                }
                return clazz;
            }
        }
    }
}

