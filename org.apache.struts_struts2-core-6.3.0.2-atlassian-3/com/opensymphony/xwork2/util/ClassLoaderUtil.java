/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Set;

public class ClassLoaderUtil {
    public static Iterator<URL> getResources(String resourceName, Class callingClass, boolean aggregate) throws IOException {
        ClassLoader cl;
        AggregateIterator<URL> iterator = new AggregateIterator<URL>();
        iterator.addEnumeration(Thread.currentThread().getContextClassLoader().getResources(resourceName));
        if (!iterator.hasNext() || aggregate) {
            iterator.addEnumeration(ClassLoaderUtil.class.getClassLoader().getResources(resourceName));
        }
        if ((!iterator.hasNext() || aggregate) && (cl = callingClass.getClassLoader()) != null) {
            iterator.addEnumeration(cl.getResources(resourceName));
        }
        if (!(iterator.hasNext() || resourceName == null || resourceName.length() != 0 && resourceName.charAt(0) == '/')) {
            return ClassLoaderUtil.getResources('/' + resourceName, callingClass, aggregate);
        }
        return iterator;
    }

    public static URL getResource(String resourceName, Class callingClass) {
        ClassLoader cl;
        URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        if (url == null) {
            url = ClassLoaderUtil.class.getClassLoader().getResource(resourceName);
        }
        if (url == null && (cl = callingClass.getClassLoader()) != null) {
            url = cl.getResource(resourceName);
        }
        if (url == null && resourceName != null && (resourceName.length() == 0 || resourceName.charAt(0) != '/')) {
            return ClassLoaderUtil.getResource('/' + resourceName, callingClass);
        }
        return url;
    }

    public static InputStream getResourceAsStream(String resourceName, Class callingClass) {
        URL url = ClassLoaderUtil.getResource(resourceName, callingClass);
        try {
            return url != null ? url.openStream() : null;
        }
        catch (IOException e) {
            return null;
        }
    }

    public static Class loadClass(String className, Class callingClass) throws ClassNotFoundException {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        }
        catch (ClassNotFoundException e) {
            try {
                return Class.forName(className);
            }
            catch (ClassNotFoundException ex) {
                try {
                    return ClassLoaderUtil.class.getClassLoader().loadClass(className);
                }
                catch (ClassNotFoundException exc) {
                    return callingClass.getClassLoader().loadClass(className);
                }
            }
        }
    }

    public static void printClassLoader() {
        System.out.println("ClassLoaderUtils.printClassLoader");
        ClassLoaderUtil.printClassLoader(Thread.currentThread().getContextClassLoader());
    }

    public static void printClassLoader(ClassLoader cl) {
        System.out.println("ClassLoaderUtils.printClassLoader(cl = " + cl + ")");
        if (cl != null) {
            ClassLoaderUtil.printClassLoader(cl.getParent());
        }
    }

    static class AggregateIterator<E>
    implements Iterator<E> {
        LinkedList<Enumeration<E>> enums = new LinkedList();
        Enumeration<E> cur = null;
        E next = null;
        Set<E> loaded = new HashSet();

        AggregateIterator() {
        }

        public AggregateIterator<E> addEnumeration(Enumeration<E> e) {
            if (e.hasMoreElements()) {
                if (this.cur == null) {
                    this.cur = e;
                    this.next = e.nextElement();
                    this.loaded.add(this.next);
                } else {
                    this.enums.add(e);
                }
            }
            return this;
        }

        @Override
        public boolean hasNext() {
            return this.next != null;
        }

        @Override
        public E next() {
            if (this.next != null) {
                E prev = this.next;
                this.next = this.loadNext();
                return prev;
            }
            throw new NoSuchElementException();
        }

        private Enumeration<E> determineCurrentEnumeration() {
            if (this.cur != null && !this.cur.hasMoreElements()) {
                this.cur = this.enums.size() > 0 ? this.enums.removeLast() : null;
            }
            return this.cur;
        }

        private E loadNext() {
            if (this.determineCurrentEnumeration() != null) {
                E tmp = this.cur.nextElement();
                int loadedSize = this.loaded.size();
                while (this.loaded.contains(tmp) && (tmp = this.loadNext()) != null && this.loaded.size() <= loadedSize) {
                }
                if (tmp != null) {
                    this.loaded.add(tmp);
                }
                return tmp;
            }
            return null;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

