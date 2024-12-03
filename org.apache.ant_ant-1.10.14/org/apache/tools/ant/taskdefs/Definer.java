/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import org.apache.tools.ant.AntTypeDefinition;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.taskdefs.Antlib;
import org.apache.tools.ant.taskdefs.DefBase;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.util.FileUtils;

public abstract class Definer
extends DefBase {
    private static final String ANTLIB_XML = "/antlib.xml";
    private static final ThreadLocal<Map<URL, Location>> RESOURCE_STACK = ThreadLocal.withInitial(HashMap::new);
    private String name;
    private String classname;
    private File file;
    private String resource;
    private boolean restrict = false;
    private int format = 0;
    private boolean definerSet = false;
    private int onError = 0;
    private String adapter;
    private String adaptTo;
    private Class<?> adapterClass;
    private Class<?> adaptToClass;

    protected void setRestrict(boolean restrict) {
        this.restrict = restrict;
    }

    public void setOnError(OnError onError) {
        this.onError = onError.getIndex();
    }

    public void setFormat(Format format) {
        this.format = format.getIndex();
    }

    public String getName() {
        return this.name;
    }

    public File getFile() {
        return this.file;
    }

    public String getResource() {
        return this.resource;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void execute() throws BuildException {
        ClassLoader al = this.createLoader();
        if (!this.definerSet) {
            if (this.getURI() == null) {
                throw new BuildException("name, file or resource attribute of " + this.getTaskName() + " is undefined", this.getLocation());
            }
            if (this.getURI().startsWith("antlib:")) {
                String uri1 = this.getURI();
                this.setResource(Definer.makeResourceFromURI(uri1));
            } else {
                throw new BuildException("Only antlib URIs can be located from the URI alone, not the URI '" + this.getURI() + "'");
            }
        }
        if (this.name != null) {
            if (this.classname == null) {
                throw new BuildException("classname attribute of " + this.getTaskName() + " element is undefined", this.getLocation());
            }
            this.addDefinition(al, this.name, this.classname);
        } else {
            URL url;
            Enumeration<URL> urls;
            if (this.classname != null) {
                throw new BuildException("You must not specify classname together with file or resource.", this.getLocation());
            }
            if (this.file == null) {
                urls = this.resourceToURLs(al);
            } else {
                url = this.fileToURL();
                if (url == null) {
                    return;
                }
                urls = Collections.enumeration(Collections.singleton(url));
            }
            while (urls.hasMoreElements()) {
                url = urls.nextElement();
                int fmt = this.format;
                if (url.getPath().toLowerCase(Locale.ENGLISH).endsWith(".xml")) {
                    fmt = 1;
                }
                if (fmt == 0) {
                    this.loadProperties(al, url);
                    break;
                }
                if (RESOURCE_STACK.get().get(url) != null) {
                    this.log("Warning: Recursive loading of " + url + " ignored at " + this.getLocation() + " originally loaded at " + RESOURCE_STACK.get().get(url), 1);
                    continue;
                }
                try {
                    RESOURCE_STACK.get().put(url, this.getLocation());
                    this.loadAntlib(al, url);
                }
                finally {
                    RESOURCE_STACK.get().remove(url);
                }
            }
        }
    }

    public static String makeResourceFromURI(String uri) {
        String resource;
        String path = uri.substring("antlib:".length());
        if (path.startsWith("//")) {
            resource = path.substring("//".length());
            if (!resource.endsWith(".xml")) {
                resource = resource + ANTLIB_XML;
            }
        } else {
            resource = path.replace('.', '/') + ANTLIB_XML;
        }
        return resource;
    }

    private URL fileToURL() {
        String message = null;
        if (!this.file.exists()) {
            message = "File " + this.file + " does not exist";
        }
        if (message == null && !this.file.isFile()) {
            message = "File " + this.file + " is not a file";
        }
        if (message == null) {
            try {
                return FileUtils.getFileUtils().getFileURL(this.file);
            }
            catch (Exception ex) {
                message = "File " + this.file + " cannot use as URL: " + ex.toString();
            }
        }
        switch (this.onError) {
            case 3: {
                throw new BuildException(message);
            }
            case 0: 
            case 1: {
                this.log(message, 1);
                break;
            }
            case 2: {
                this.log(message, 3);
                break;
            }
        }
        return null;
    }

    private Enumeration<URL> resourceToURLs(ClassLoader classLoader) {
        Enumeration<URL> ret;
        try {
            ret = classLoader.getResources(this.resource);
        }
        catch (IOException e) {
            throw new BuildException("Could not fetch resources named " + this.resource, e, this.getLocation());
        }
        if (!ret.hasMoreElements()) {
            String message = "Could not load definitions from resource " + this.resource + ". It could not be found.";
            switch (this.onError) {
                case 3: {
                    throw new BuildException(message);
                }
                case 0: 
                case 1: {
                    this.log(message, 1);
                    break;
                }
                case 2: {
                    this.log(message, 3);
                    break;
                }
            }
        }
        return ret;
    }

    protected void loadProperties(ClassLoader al, URL url) {
        try (InputStream is = url.openStream();){
            if (is == null) {
                this.log("Could not load definitions from " + url, 1);
                return;
            }
            Properties props = new Properties();
            props.load(is);
            Iterator<String> iterator = props.stringPropertyNames().iterator();
            while (iterator.hasNext()) {
                String key;
                this.name = key = iterator.next();
                this.classname = props.getProperty(this.name);
                this.addDefinition(al, this.name, this.classname);
            }
        }
        catch (IOException ex) {
            throw new BuildException(ex, this.getLocation());
        }
    }

    private void loadAntlib(ClassLoader classLoader, URL url) {
        try {
            Antlib antlib = Antlib.createAntlib(this.getProject(), url, this.getURI());
            antlib.setClassLoader(classLoader);
            antlib.setURI(this.getURI());
            antlib.execute();
        }
        catch (BuildException ex) {
            throw ProjectHelper.addLocationToBuildException(ex, this.getLocation());
        }
    }

    public void setFile(File file) {
        if (this.definerSet) {
            this.tooManyDefinitions();
        }
        this.definerSet = true;
        this.file = file;
    }

    public void setResource(String res) {
        if (this.definerSet) {
            this.tooManyDefinitions();
        }
        this.definerSet = true;
        this.resource = res;
    }

    public void setAntlib(String antlib) {
        if (this.definerSet) {
            this.tooManyDefinitions();
        }
        if (!antlib.startsWith("antlib:")) {
            throw new BuildException("Invalid antlib attribute - it must start with antlib:");
        }
        this.setURI(antlib);
        this.resource = antlib.substring("antlib:".length()).replace('.', '/') + ANTLIB_XML;
        this.definerSet = true;
    }

    public void setName(String name) {
        if (this.definerSet) {
            this.tooManyDefinitions();
        }
        this.definerSet = true;
        this.name = name;
    }

    public String getClassname() {
        return this.classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public void setAdapter(String adapter) {
        this.adapter = adapter;
    }

    protected void setAdapterClass(Class<?> adapterClass) {
        this.adapterClass = adapterClass;
    }

    public void setAdaptTo(String adaptTo) {
        this.adaptTo = adaptTo;
    }

    protected void setAdaptToClass(Class<?> adaptToClass) {
        this.adaptToClass = adaptToClass;
    }

    protected void addDefinition(ClassLoader al, String name, String classname) throws BuildException {
        Class<?> cl = null;
        try {
            try {
                name = ProjectHelper.genComponentName(this.getURI(), name);
                if (this.onError != 2) {
                    cl = Class.forName(classname, true, al);
                }
                if (this.adapter != null) {
                    this.adapterClass = Class.forName(this.adapter, true, al);
                }
                if (this.adaptTo != null) {
                    this.adaptToClass = Class.forName(this.adaptTo, true, al);
                }
                AntTypeDefinition def = new AntTypeDefinition();
                def.setName(name);
                def.setClassName(classname);
                def.setClass(cl);
                def.setAdapterClass(this.adapterClass);
                def.setAdaptToClass(this.adaptToClass);
                def.setRestrict(this.restrict);
                def.setClassLoader(al);
                if (cl != null) {
                    def.checkClass(this.getProject());
                }
                ComponentHelper.getComponentHelper(this.getProject()).addDataTypeDefinition(def);
            }
            catch (ClassNotFoundException cnfe) {
                throw new BuildException(this.getTaskName() + " class " + classname + " cannot be found\n using the classloader " + al, cnfe, this.getLocation());
            }
            catch (NoClassDefFoundError ncdfe) {
                throw new BuildException(this.getTaskName() + " A class needed by class " + classname + " cannot be found: " + ncdfe.getMessage() + "\n using the classloader " + al, ncdfe, this.getLocation());
            }
        }
        catch (BuildException ex) {
            switch (this.onError) {
                case 0: 
                case 3: {
                    throw ex;
                }
                case 1: {
                    this.log(ex.getLocation() + "Warning: " + ex.getMessage(), 1);
                    break;
                }
                default: {
                    this.log(ex.getLocation() + ex.getMessage(), 4);
                }
            }
        }
    }

    private void tooManyDefinitions() {
        throw new BuildException("Only one of the attributes name, file and resource can be set", this.getLocation());
    }

    public static class Format
    extends EnumeratedAttribute {
        public static final int PROPERTIES = 0;
        public static final int XML = 1;

        @Override
        public String[] getValues() {
            return new String[]{"properties", "xml"};
        }
    }

    public static class OnError
    extends EnumeratedAttribute {
        public static final int FAIL = 0;
        public static final int REPORT = 1;
        public static final int IGNORE = 2;
        public static final int FAIL_ALL = 3;
        public static final String POLICY_FAIL = "fail";
        public static final String POLICY_REPORT = "report";
        public static final String POLICY_IGNORE = "ignore";
        public static final String POLICY_FAILALL = "failall";

        public OnError() {
        }

        public OnError(String value) {
            this.setValue(value);
        }

        @Override
        public String[] getValues() {
            return new String[]{POLICY_FAIL, POLICY_REPORT, POLICY_IGNORE, POLICY_FAILALL};
        }
    }
}

