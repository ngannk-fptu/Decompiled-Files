/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.management;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import net.sf.ehcache.util.MergedEnumeration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceClassLoader
extends ClassLoader {
    private static final String PRIVATE_CLASS_SUFFIX = ".class_terracotta";
    private static final int BUFFER_SIZE = 1024;
    private static final Logger LOG = LoggerFactory.getLogger(ResourceClassLoader.class);
    private final String prefix;
    private final String implementationVersion;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ResourceClassLoader(String prefix, ClassLoader parent) {
        super(parent);
        this.prefix = prefix;
        String temporaryImplementationVersion = null;
        InputStream in = null;
        try {
            URL manifestResource = this.getParent().getResource(prefix + "/META-INF/MANIFEST.MF");
            in = manifestResource.openStream();
            Manifest man = new Manifest(in);
            Attributes attributes = man.getMainAttributes();
            temporaryImplementationVersion = attributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
        }
        catch (Exception e) {
            LOG.debug("Could not read the Manifest", (Throwable)e);
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (Exception exception) {}
        }
        this.implementationVersion = temporaryImplementationVersion;
    }

    @Override
    public synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (name.startsWith("java.")) {
            return this.getParent().loadClass(name);
        }
        Class<?> c = this.findLoadedClass(name);
        if (c == null) {
            try {
                c = this.findClass(name);
            }
            catch (ClassNotFoundException e) {
                c = super.loadClass(name, resolve);
            }
        }
        if (resolve) {
            this.resolveClass(c);
        }
        return c;
    }

    @Override
    public URL getResource(String name) {
        URL url = this.findResource(name);
        if (url == null) {
            return super.getResource(name);
        }
        return url;
    }

    @Override
    protected URL findResource(String name) {
        String resource = this.replaceWithPrivateSuffix(name);
        return this.getParent().getResource(this.prefix + "/" + resource);
    }

    @Override
    public Enumeration<URL> getResources(String resourceName) throws IOException {
        if (resourceName.startsWith("META-INF/services/")) {
            return this.findResources(resourceName);
        }
        Enumeration[] tmp = new Enumeration[]{this.findResources(resourceName), this.getParent().getResources(resourceName)};
        return new MergedEnumeration<URL>(tmp[0], tmp[1]);
    }

    @Override
    protected Enumeration<URL> findResources(String name) throws IOException {
        String resource = this.replaceWithPrivateSuffix(name);
        Enumeration<URL> resources = this.getParent().getResources(this.prefix + "/" + resource);
        ArrayList<URL> urls = new ArrayList<URL>();
        while (resources.hasMoreElements()) {
            URL nextElement = resources.nextElement();
            URL elementToAdd = nextElement.toExternalForm().startsWith("vfs") ? this.translateFromVFSToPhysicalURL(nextElement) : nextElement;
            urls.add(elementToAdd);
        }
        return Collections.enumeration(urls);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        String classRealName = this.prefix + "/" + className.replace('.', '/') + PRIVATE_CLASS_SUFFIX;
        URL classResource = this.getParent().getResource(classRealName);
        if (classResource != null) {
            String pkgname;
            int index = className.lastIndexOf(46);
            if (index != -1 && this.getPackage(pkgname = className.substring(0, index)) == null) {
                this.definePackage(pkgname, null, null, null, null, this.implementationVersion, null, null);
            }
            InputStream in = null;
            try {
                Class<?> defineClass;
                byte[] array = new byte[1024];
                in = classResource.openStream();
                ByteArrayOutputStream out = new ByteArrayOutputStream(array.length);
                int length = in.read(array);
                while (length > 0) {
                    out.write(array, 0, length);
                    length = in.read(array);
                }
                Class<?> clazz = defineClass = this.defineClass(className, out.toByteArray(), 0, out.size());
                return clazz;
            }
            catch (IOException e) {
                LOG.warn("Impossible to open " + classRealName + " for loading", (Throwable)e);
            }
            finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                }
                catch (Exception exception) {}
            }
        }
        throw new ClassNotFoundException(className);
    }

    private URL translateFromVFSToPhysicalURL(URL vfsUrl) throws IOException {
        URL physicalUrl = null;
        URLConnection vfsURLConnection = vfsUrl.openConnection();
        Object vfsVirtualFile = vfsURLConnection.getContent();
        try {
            Class<?> vfsUtilsClass = Class.forName("org.jboss.vfs.VFSUtils");
            Class<?> virtualFileClass = Class.forName("org.jboss.vfs.VirtualFile");
            Method getPathName = virtualFileClass.getDeclaredMethod("getPathName", new Class[0]);
            Method getPhysicalURL = vfsUtilsClass.getDeclaredMethod("getPhysicalURL", virtualFileClass);
            Method recursiveCopy = vfsUtilsClass.getDeclaredMethod("recursiveCopy", virtualFileClass, File.class);
            String pathName = (String)getPathName.invoke(vfsVirtualFile, (Object[])null);
            physicalUrl = (URL)getPhysicalURL.invoke(null, vfsVirtualFile);
            File physicalURLAsFile = new File(physicalUrl.getFile());
            if (physicalURLAsFile.isDirectory() && physicalURLAsFile.list().length == 0) {
                this.unpackVfsResourceToPhysicalURLLocation(physicalUrl, vfsVirtualFile, recursiveCopy);
            }
        }
        catch (ClassNotFoundException e) {
            physicalUrl = vfsUrl;
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
        return physicalUrl;
    }

    private void unpackVfsResourceToPhysicalURLLocation(URL physicalUrl, Object vfsVirtualFile, Method recursiveCopy) throws IllegalAccessException, InvocationTargetException {
        String physicalPath = physicalUrl.getFile() + "/../";
        recursiveCopy.invoke(null, vfsVirtualFile, new File(physicalPath));
    }

    private String replaceWithPrivateSuffix(String name) {
        return name.endsWith(".class") ? name.substring(0, name.lastIndexOf(".class")) + PRIVATE_CLASS_SUFFIX : name;
    }
}

