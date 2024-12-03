/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.BuildException
 *  org.apache.tools.ant.Task
 */
package org.hibernate.tool.enhance;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.hibernate.bytecode.enhance.spi.DefaultEnhancementContext;
import org.hibernate.bytecode.enhance.spi.Enhancer;
import org.hibernate.bytecode.enhance.spi.UnloadedClass;
import org.hibernate.bytecode.enhance.spi.UnloadedField;
import org.hibernate.cfg.Environment;

public class EnhancementTask
extends Task {
    private String base;
    private String dir;
    private boolean failOnError = true;
    private boolean enableLazyInitialization = false;
    private boolean enableDirtyTracking = false;
    private boolean enableAssociationManagement = false;
    private boolean enableExtendedEnhancement = false;
    private List<File> sourceSet = new ArrayList<File>();

    public void setBase(String base) {
        this.base = base;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    public void setEnableLazyInitialization(boolean enableLazyInitialization) {
        this.enableLazyInitialization = enableLazyInitialization;
    }

    public void setEnableDirtyTracking(boolean enableDirtyTracking) {
        this.enableDirtyTracking = enableDirtyTracking;
    }

    public void setEnableAssociationManagement(boolean enableAssociationManagement) {
        this.enableAssociationManagement = enableAssociationManagement;
    }

    public void setEnableExtendedEnhancement(boolean enableExtendedEnhancement) {
        this.enableExtendedEnhancement = enableExtendedEnhancement;
    }

    private boolean shouldApply() {
        return this.enableLazyInitialization || this.enableDirtyTracking || this.enableAssociationManagement || this.enableExtendedEnhancement;
    }

    public void execute() throws BuildException {
        if (!this.shouldApply()) {
            this.log("Skipping Hibernate bytecode enhancement task execution since no feature is enabled", 1);
            return;
        }
        if (!this.dir.startsWith(this.base)) {
            throw new BuildException("The enhancement directory 'dir' (" + this.dir + ") is no subdirectory of 'base' (" + this.base + ")");
        }
        File root = new File(this.dir);
        if (!root.exists()) {
            this.log("Skipping Hibernate enhancement task execution since there is no classes dir " + this.dir, 2);
            return;
        }
        this.walkDir(root);
        if (this.sourceSet.isEmpty()) {
            this.log("Skipping Hibernate enhancement task execution since there are no classes to enhance on " + this.dir, 2);
            return;
        }
        this.log("Starting Hibernate enhancement task for classes on " + this.dir, 2);
        final ClassLoader classLoader = this.toClassLoader(Collections.singletonList(new File(this.base)));
        DefaultEnhancementContext enhancementContext = new DefaultEnhancementContext(){

            @Override
            public ClassLoader getLoadingClassLoader() {
                return classLoader;
            }

            @Override
            public boolean doBiDirectionalAssociationManagement(UnloadedField field) {
                return EnhancementTask.this.enableAssociationManagement;
            }

            @Override
            public boolean doDirtyCheckingInline(UnloadedClass classDescriptor) {
                return EnhancementTask.this.enableDirtyTracking;
            }

            @Override
            public boolean hasLazyLoadableAttributes(UnloadedClass classDescriptor) {
                return EnhancementTask.this.enableLazyInitialization;
            }

            @Override
            public boolean isLazyLoadable(UnloadedField field) {
                return EnhancementTask.this.enableLazyInitialization;
            }

            @Override
            public boolean doExtendedEnhancement(UnloadedClass classDescriptor) {
                return EnhancementTask.this.enableExtendedEnhancement;
            }
        };
        if (this.enableExtendedEnhancement) {
            this.log("Extended enhancement is enabled. Classes other than entities may be modified. You should consider access the entities using getter/setter methods and disable this property. Use at your own risk.", 1);
        }
        Enhancer enhancer = Environment.getBytecodeProvider().getEnhancer(enhancementContext);
        for (File file : this.sourceSet) {
            byte[] enhancedBytecode = this.doEnhancement(file, enhancer);
            if (enhancedBytecode == null) continue;
            this.writeOutEnhancedClass(enhancedBytecode, file);
            this.log("Successfully enhanced class [" + file + "]", 2);
        }
    }

    private ClassLoader toClassLoader(List<File> runtimeClasspath) throws BuildException {
        ArrayList<URL> urls = new ArrayList<URL>();
        for (File file : runtimeClasspath) {
            try {
                urls.add(file.toURI().toURL());
                this.log("Adding classpath entry for classes root " + file.getAbsolutePath(), 4);
            }
            catch (MalformedURLException e) {
                String msg = "Unable to resolve classpath entry to URL: " + file.getAbsolutePath();
                if (this.failOnError) {
                    throw new BuildException(msg, (Throwable)e);
                }
                this.log(msg, 1);
            }
        }
        return new URLClassLoader(urls.toArray(new URL[urls.size()]), Enhancer.class.getClassLoader());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private byte[] doEnhancement(File javaClassFile, Enhancer enhancer) throws BuildException {
        try {
            String className = javaClassFile.getAbsolutePath().substring(this.base.length() + 1, javaClassFile.getAbsolutePath().length() - ".class".length()).replace(File.separatorChar, '.');
            ByteArrayOutputStream originalBytes = new ByteArrayOutputStream();
            try (FileInputStream fileInputStream = new FileInputStream(javaClassFile);){
                int length;
                byte[] buffer = new byte[1024];
                while ((length = fileInputStream.read(buffer)) != -1) {
                    originalBytes.write(buffer, 0, length);
                }
            }
            return enhancer.enhance(className, originalBytes.toByteArray());
        }
        catch (Exception e) {
            String msg = "Unable to enhance class: " + javaClassFile.getName();
            if (this.failOnError) {
                throw new BuildException(msg, (Throwable)e);
            }
            this.log(msg, e, 1);
            return null;
        }
    }

    private void walkDir(File dir) {
        this.walkDir(dir, new FileFilter(){

            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().endsWith(".class");
            }
        }, new FileFilter(){

            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
    }

    private void walkDir(File dir, FileFilter classesFilter, FileFilter dirFilter) {
        File[] files;
        File[] dirs = dir.listFiles(dirFilter);
        if (dirs != null) {
            for (File dir1 : dirs) {
                this.walkDir(dir1, classesFilter, dirFilter);
            }
        }
        if ((files = dir.listFiles(classesFilter)) != null) {
            Collections.addAll(this.sourceSet, files);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeOutEnhancedClass(byte[] enhancedBytecode, File file) throws BuildException {
        try {
            if (file.delete()) {
                if (!file.createNewFile()) {
                    this.log("Unable to recreate class file", 0);
                }
            } else {
                this.log("Unable to delete class file", 0);
            }
        }
        catch (IOException e) {
            this.log("Problem preparing class file for writing out enhancements", e, 1);
        }
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file, false);
            ((OutputStream)outputStream).write(enhancedBytecode);
            outputStream.flush();
        }
        catch (IOException e) {
            String msg = String.format("Error writing to enhanced class [%s] to file [%s]", file.getName(), file.getAbsolutePath());
            if (this.failOnError) {
                throw new BuildException(msg, (Throwable)e);
            }
            this.log(msg, e, 1);
        }
        finally {
            try {
                if (outputStream != null) {
                    ((OutputStream)outputStream).close();
                }
            }
            catch (IOException iOException) {}
        }
    }
}

