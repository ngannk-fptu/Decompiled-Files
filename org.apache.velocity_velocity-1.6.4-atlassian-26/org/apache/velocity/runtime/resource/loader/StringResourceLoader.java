/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.ExtendedProperties
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.velocity.runtime.resource.loader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResource;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.apache.velocity.runtime.resource.util.StringResourceRepositoryImpl;
import org.apache.velocity.util.ClassUtils;

public class StringResourceLoader
extends ResourceLoader {
    public static final String REPOSITORY_STATIC = "repository.static";
    public static final boolean REPOSITORY_STATIC_DEFAULT = true;
    public static final String REPOSITORY_CLASS = "repository.class";
    public static final String REPOSITORY_CLASS_DEFAULT = StringResourceRepositoryImpl.class.getName();
    public static final String REPOSITORY_NAME = "repository.name";
    public static final String REPOSITORY_NAME_DEFAULT = StringResourceRepository.class.getName();
    public static final String REPOSITORY_ENCODING = "repository.encoding";
    public static final String REPOSITORY_ENCODING_DEFAULT = "UTF-8";
    protected static final Map STATIC_REPOSITORIES = Collections.synchronizedMap(new HashMap());
    protected StringResourceRepository repository;

    public static StringResourceRepository getRepository() {
        return StringResourceLoader.getRepository(REPOSITORY_NAME_DEFAULT);
    }

    public static StringResourceRepository getRepository(String name) {
        return (StringResourceRepository)STATIC_REPOSITORIES.get(name);
    }

    public static void setRepository(String name, StringResourceRepository repo) {
        STATIC_REPOSITORIES.put(name, repo);
    }

    public static StringResourceRepository removeRepository(String name) {
        return (StringResourceRepository)STATIC_REPOSITORIES.remove(name);
    }

    public static void clearRepositories() {
        STATIC_REPOSITORIES.clear();
    }

    @Override
    public void init(ExtendedProperties configuration) {
        this.log.trace("StringResourceLoader : initialization starting.");
        String repoClass = configuration.getString(REPOSITORY_CLASS, REPOSITORY_CLASS_DEFAULT);
        String repoName = configuration.getString(REPOSITORY_NAME, REPOSITORY_NAME_DEFAULT);
        boolean isStatic = configuration.getBoolean(REPOSITORY_STATIC, true);
        String encoding = configuration.getString(REPOSITORY_ENCODING);
        if (isStatic) {
            this.repository = StringResourceLoader.getRepository(repoName);
            if (this.repository != null && this.log.isDebugEnabled()) {
                this.log.debug("Loaded repository '" + repoName + "' from static repo store");
            }
        } else {
            this.repository = (StringResourceRepository)this.rsvc.getApplicationAttribute(repoName);
            if (this.repository != null && this.log.isDebugEnabled()) {
                this.log.debug("Loaded repository '" + repoName + "' from application attributes");
            }
        }
        if (this.repository == null) {
            this.repository = this.createRepository(repoClass, encoding);
            if (isStatic) {
                StringResourceLoader.setRepository(repoName, this.repository);
            } else {
                this.rsvc.setApplicationAttribute(repoName, this.repository);
            }
        } else {
            if (!this.repository.getClass().getName().equals(repoClass)) {
                this.log.debug("Cannot change class of string repository '" + repoName + "' from " + this.repository.getClass().getName() + " to " + repoClass + ". The change will be ignored.");
            }
            if (encoding != null && !this.repository.getEncoding().equals(encoding)) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Changing the default encoding of string repository '" + repoName + "' from " + this.repository.getEncoding() + " to " + encoding);
                }
                this.repository.setEncoding(encoding);
            }
        }
        this.log.trace("StringResourceLoader : initialization complete.");
    }

    public StringResourceRepository createRepository(String className, String encoding) {
        StringResourceRepository repo;
        if (this.log.isDebugEnabled()) {
            this.log.debug("Creating string repository using class " + className + "...");
        }
        try {
            repo = (StringResourceRepository)ClassUtils.getNewInstance(className);
        }
        catch (ClassNotFoundException cnfe) {
            throw new VelocityException("Could not find '" + className + "'", cnfe);
        }
        catch (IllegalAccessException iae) {
            throw new VelocityException("Could not access '" + className + "'", iae);
        }
        catch (InstantiationException ie) {
            throw new VelocityException("Could not instantiate '" + className + "'", ie);
        }
        if (encoding != null) {
            repo.setEncoding(encoding);
        } else {
            repo.setEncoding(REPOSITORY_ENCODING_DEFAULT);
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug("Default repository encoding is " + repo.getEncoding());
        }
        return repo;
    }

    @Override
    public boolean resourceExists(String name) {
        if (name == null) {
            return false;
        }
        return this.repository.getStringResource(name) != null;
    }

    @Override
    public InputStream getResourceStream(String name) throws ResourceNotFoundException {
        if (StringUtils.isEmpty((CharSequence)name)) {
            throw new ResourceNotFoundException("No template name provided");
        }
        StringResource resource = this.repository.getStringResource(name);
        if (resource == null) {
            throw new ResourceNotFoundException("Could not locate resource '" + name + "'");
        }
        byte[] byteArray = null;
        try {
            byteArray = resource.getBody().getBytes(resource.getEncoding());
            return new ByteArrayInputStream(byteArray);
        }
        catch (UnsupportedEncodingException ue) {
            throw new VelocityException("Could not convert String using encoding " + resource.getEncoding(), ue);
        }
    }

    @Override
    public boolean isSourceModified(Resource resource) {
        StringResource original = null;
        boolean result = true;
        original = this.repository.getStringResource(resource.getName());
        if (original != null) {
            result = original.getLastModified() != resource.getLastModified();
        }
        return result;
    }

    @Override
    public long getLastModified(Resource resource) {
        StringResource original = null;
        original = this.repository.getStringResource(resource.getName());
        return original != null ? original.getLastModified() : 0L;
    }
}

