/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.ExtendedProperties
 *  org.apache.commons.lang.StringUtils
 */
package org.apache.velocity.runtime.resource.loader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.io.UnicodeInputStream;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

public class FileResourceLoader
extends ResourceLoader {
    private List paths = new ArrayList();
    private Map templatePaths = Collections.synchronizedMap(new HashMap());
    private boolean unicode = false;

    public void init(ExtendedProperties configuration) {
        if (this.log.isTraceEnabled()) {
            this.log.trace("FileResourceLoader : initialization starting.");
        }
        this.paths.addAll(configuration.getVector("path"));
        this.unicode = configuration.getBoolean("unicode", false);
        if (this.log.isDebugEnabled()) {
            this.log.debug("Do unicode file recognition:  " + this.unicode);
        }
        if (this.log.isDebugEnabled()) {
            org.apache.velocity.util.StringUtils.trimStrings(this.paths);
            int sz = this.paths.size();
            for (int i = 0; i < sz; ++i) {
                this.log.debug("FileResourceLoader : adding path '" + (String)this.paths.get(i) + "'");
            }
            this.log.trace("FileResourceLoader : initialization complete.");
        }
    }

    public InputStream getResourceStream(String templateName) throws ResourceNotFoundException {
        if (StringUtils.isEmpty((String)templateName)) {
            throw new ResourceNotFoundException("Need to specify a file name or file path!");
        }
        String template = org.apache.velocity.util.StringUtils.normalizePath(templateName);
        if (template == null || template.length() == 0) {
            String msg = "File resource error : argument " + template + " contains .. and may be trying to access " + "content outside of template root.  Rejected.";
            this.log.error("FileResourceLoader : " + msg);
            throw new ResourceNotFoundException(msg);
        }
        int size = this.paths.size();
        for (int i = 0; i < size; ++i) {
            String path = (String)this.paths.get(i);
            InputStream inputStream = null;
            try {
                inputStream = this.findTemplate(path, template);
            }
            catch (IOException ioe) {
                String msg = "Exception while loading Template " + template;
                this.log.error(msg, ioe);
                throw new VelocityException(msg, ioe);
            }
            if (inputStream == null) continue;
            this.templatePaths.put(templateName, path);
            return inputStream;
        }
        throw new ResourceNotFoundException("FileResourceLoader : cannot find " + template);
    }

    public boolean resourceExists(String name) {
        if (name == null) {
            return false;
        }
        if ((name = org.apache.velocity.util.StringUtils.normalizePath(name)) == null || name.length() == 0) {
            return false;
        }
        int size = this.paths.size();
        for (int i = 0; i < size; ++i) {
            String path = (String)this.paths.get(i);
            try {
                File file = this.getFile(path, name);
                if (!file.canRead()) continue;
                return true;
            }
            catch (Exception ioe) {
                String msg = "Exception while checking for template " + name;
                this.log.debug(msg, ioe);
            }
        }
        return false;
    }

    private InputStream findTemplate(String path, String template) throws IOException {
        try {
            File file = this.getFile(path, template);
            if (file.canRead()) {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(file.getAbsolutePath());
                    if (this.unicode) {
                        UnicodeInputStream uis = null;
                        try {
                            uis = new UnicodeInputStream(fis, true);
                            if (this.log.isDebugEnabled()) {
                                this.log.debug("File Encoding for " + file + " is: " + uis.getEncodingFromStream());
                            }
                            return new BufferedInputStream(uis);
                        }
                        catch (IOException e) {
                            this.closeQuiet(uis);
                            throw e;
                        }
                    }
                    return new BufferedInputStream(fis);
                }
                catch (IOException e) {
                    this.closeQuiet(fis);
                    throw e;
                }
            }
            return null;
        }
        catch (FileNotFoundException fnfe) {
            return null;
        }
    }

    private void closeQuiet(InputStream is) {
        if (is != null) {
            try {
                is.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    public boolean isSourceModified(Resource resource) {
        boolean modified = true;
        String fileName = resource.getName();
        String path = (String)this.templatePaths.get(fileName);
        File currentFile = null;
        for (int i = 0; currentFile == null && i < this.paths.size(); ++i) {
            String testPath = (String)this.paths.get(i);
            File testFile = this.getFile(testPath, fileName);
            if (!testFile.canRead()) continue;
            currentFile = testFile;
        }
        File file = this.getFile(path, fileName);
        if (currentFile != null && file.exists() && currentFile.equals(file) && file.canRead()) {
            modified = file.lastModified() != resource.getLastModified();
        }
        return modified;
    }

    public long getLastModified(Resource resource) {
        String path = (String)this.templatePaths.get(resource.getName());
        File file = this.getFile(path, resource.getName());
        if (file.canRead()) {
            return file.lastModified();
        }
        return 0L;
    }

    private File getFile(String path, String template) {
        File file = null;
        if ("".equals(path)) {
            file = new File(template);
        } else {
            if (template.startsWith("/")) {
                template = template.substring(1);
            }
            file = new File(path, template);
        }
        return file;
    }
}

