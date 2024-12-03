/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.util.sandbox;

import com.atlassian.confluence.impl.util.sandbox.SandboxMessage;
import com.atlassian.confluence.impl.util.sandbox.SandboxMessageType;
import com.atlassian.confluence.impl.util.sandbox.SandboxServerContext;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;

class SandboxServerClassLoader
extends ClassLoader {
    private final SandboxServerContext context;
    private final String uniqueDirectoryName;

    static <I> Function<I, ClassLoader> factory(SandboxServerContext context) {
        return key -> new SandboxServerClassLoader(context, key.toString());
    }

    SandboxServerClassLoader(SandboxServerContext context, String uniqueDirectoryName) {
        this.context = context;
        this.uniqueDirectoryName = uniqueDirectoryName;
    }

    @Override
    protected synchronized URL findResource(String name) {
        try {
            this.context.sendMessage(new SandboxMessage(SandboxMessageType.FIND_RESOURCE_REQUEST, name));
            SandboxMessage message = this.context.receiveMessage();
            byte[] bytes = (byte[])message.getPayload();
            if (bytes.length == 0) {
                return null;
            }
            Path path = Paths.get(this.uniqueDirectoryName, name);
            if (path == null) {
                return null;
            }
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent, new FileAttribute[0]);
            }
            Files.write(path, bytes, new OpenOption[0]);
            this.context.log(Level.FINE, "Write to " + path.toString());
            return path.toUri().toURL();
        }
        catch (IOException e) {
            return null;
        }
    }

    @Override
    protected synchronized Enumeration<URL> findResources(String name) throws IOException {
        this.context.sendMessage(new SandboxMessage(SandboxMessageType.FIND_RESOURCES_REQUEST, name));
        SandboxMessage message = this.context.receiveMessage();
        List data = (List)message.getPayload();
        ArrayList<URL> resources = new ArrayList<URL>();
        for (int i = 0; i < data.size(); ++i) {
            Path path = Paths.get(this.uniqueDirectoryName, String.valueOf(i), name);
            if (path == null) continue;
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent, new FileAttribute[0]);
            }
            Files.write(path, (byte[])data.get(i), new OpenOption[0]);
            this.context.log(Level.FINE, "Write to " + path.toString());
            resources.add(path.toUri().toURL());
        }
        return Collections.enumeration(resources);
    }

    @Override
    protected synchronized Class<?> findClass(String name) throws ClassNotFoundException {
        this.context.log(Level.FINE, "requesting class " + name);
        this.context.sendMessage(new SandboxMessage(SandboxMessageType.FIND_CLASS_REQUEST, name));
        SandboxMessage message = this.context.receiveMessage();
        byte[] bytes = (byte[])message.getPayload();
        if (bytes.length == 0) {
            throw new ClassNotFoundException("Unable to load class " + name);
        }
        this.context.log(Level.FINE, "got class " + name);
        this.initializePackage(name);
        return this.defineClass(name, bytes, 0, bytes.length);
    }

    private void initializePackage(String className) {
        String pkgname;
        Package pkg;
        int i = className.lastIndexOf(46);
        if (i != -1 && (pkg = this.getPackage(pkgname = className.substring(0, i))) == null) {
            this.definePackage(pkgname, null, null, null, null, null, null, null);
        }
    }
}

