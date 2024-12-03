/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.inject.Initializable;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.validator.ShortCircuitableValidator;
import com.opensymphony.xwork2.validator.Validator;
import com.opensymphony.xwork2.validator.ValidatorConfig;
import com.opensymphony.xwork2.validator.ValidatorFactory;
import com.opensymphony.xwork2.validator.ValidatorFileParser;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsException;

public class DefaultValidatorFactory
implements ValidatorFactory,
Initializable {
    protected Map<String, String> validators = new HashMap<String, String>();
    private static Logger LOG = LogManager.getLogger(DefaultValidatorFactory.class);
    protected ObjectFactory objectFactory;
    protected ValidatorFileParser validatorFileParser;

    @Inject
    public DefaultValidatorFactory(@Inject ObjectFactory objectFactory, @Inject ValidatorFileParser parser) {
        this.objectFactory = objectFactory;
        this.validatorFileParser = parser;
    }

    @Override
    public void init() {
        this.parseValidators();
    }

    @Override
    public Validator getValidator(ValidatorConfig cfg) {
        Validator validator;
        String className = this.lookupRegisteredValidatorType(cfg.getType());
        try {
            validator = this.objectFactory.buildValidator(className, cfg.getParams(), ActionContext.getContext().getContextMap());
        }
        catch (Exception e) {
            String msg = "There was a problem creating a Validator of type " + className + " : caused by " + e.getMessage();
            throw new StrutsException(msg, e, cfg);
        }
        validator.setMessageKey(cfg.getMessageKey());
        validator.setDefaultMessage(cfg.getDefaultMessage());
        validator.setMessageParameters(cfg.getMessageParams());
        if (validator instanceof ShortCircuitableValidator) {
            ((ShortCircuitableValidator)((Object)validator)).setShortCircuit(cfg.isShortCircuit());
        }
        return validator;
    }

    @Override
    public void registerValidator(String name, String className) {
        LOG.debug("Registering validator of class {} with name {}", (Object)className, (Object)name);
        this.validators.put(name, className);
    }

    @Override
    public String lookupRegisteredValidatorType(String name) {
        String className = this.validators.get(name);
        if (className == null) {
            throw new IllegalArgumentException("There is no validator class mapped to the name " + name);
        }
        return className;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void parseValidators() {
        LOG.debug("Loading validator definitions.");
        ArrayList<File> files = new ArrayList<File>();
        try {
            Iterator<URL> urls = ClassLoaderUtil.getResources("", DefaultValidatorFactory.class, false);
            while (urls.hasNext()) {
                URL u = urls.next();
                try {
                    URI uri = new URI(u.toExternalForm().replaceAll(" ", "%20"));
                    if (uri.isOpaque() || !"file".equalsIgnoreCase(uri.getScheme())) continue;
                    File f = new File(uri);
                    FilenameFilter filter = new FilenameFilter(){

                        @Override
                        public boolean accept(File file, String fileName) {
                            return fileName.contains("-validators.xml");
                        }
                    };
                    if (f.isDirectory()) {
                        try {
                            File[] ff = f.listFiles(filter);
                            if (ff == null || ff.length <= 0) continue;
                            files.addAll(Arrays.asList(ff));
                        }
                        catch (SecurityException se) {
                            LOG.error("Security Exception while accessing directory '{}'", (Object)f, (Object)se);
                        }
                        continue;
                    }
                    ZipInputStream zipInputStream = null;
                    try {
                        InputStream inputStream = u.openStream();
                        Throwable throwable = null;
                        try {
                            zipInputStream = inputStream instanceof ZipInputStream ? (ZipInputStream)inputStream : new ZipInputStream(inputStream);
                            ZipEntry zipEntry = zipInputStream.getNextEntry();
                            while (zipEntry != null) {
                                if (zipEntry.getName().endsWith("-validators.xml")) {
                                    LOG.trace("Adding validator {}", (Object)zipEntry.getName());
                                    files.add(new File(zipEntry.getName()));
                                }
                                zipEntry = zipInputStream.getNextEntry();
                            }
                        }
                        catch (Throwable throwable2) {
                            throwable = throwable2;
                            throw throwable2;
                        }
                        finally {
                            if (inputStream == null) continue;
                            if (throwable != null) {
                                try {
                                    inputStream.close();
                                }
                                catch (Throwable throwable3) {
                                    throwable.addSuppressed(throwable3);
                                }
                                continue;
                            }
                            inputStream.close();
                        }
                    }
                    finally {
                        if (zipInputStream == null) continue;
                        zipInputStream.close();
                    }
                }
                catch (Exception ex) {
                    LOG.error("Unable to load {}", (Object)u, (Object)ex);
                }
            }
        }
        catch (IOException e) {
            throw new ConfigurationException("Unable to parse validators", e);
        }
        String resourceName = "com/opensymphony/xwork2/validator/validators/default.xml";
        this.retrieveValidatorConfiguration(resourceName);
        resourceName = "validators.xml";
        this.retrieveValidatorConfiguration(resourceName);
        for (File file : files) {
            this.retrieveValidatorConfiguration(file.getName());
        }
    }

    private void retrieveValidatorConfiguration(String resourceName) {
        InputStream is = ClassLoaderUtil.getResourceAsStream(resourceName, DefaultValidatorFactory.class);
        if (is != null) {
            this.validatorFileParser.parseValidatorDefinitions(this.validators, is, resourceName);
        }
    }
}

