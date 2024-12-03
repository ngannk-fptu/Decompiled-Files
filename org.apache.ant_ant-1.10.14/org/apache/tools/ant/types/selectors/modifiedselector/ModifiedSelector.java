/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors.modifiedselector;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.IntrospectionHelper;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;
import org.apache.tools.ant.types.selectors.BaseExtendSelector;
import org.apache.tools.ant.types.selectors.modifiedselector.Algorithm;
import org.apache.tools.ant.types.selectors.modifiedselector.Cache;
import org.apache.tools.ant.types.selectors.modifiedselector.ChecksumAlgorithm;
import org.apache.tools.ant.types.selectors.modifiedselector.DigestAlgorithm;
import org.apache.tools.ant.types.selectors.modifiedselector.EqualComparator;
import org.apache.tools.ant.types.selectors.modifiedselector.HashvalueAlgorithm;
import org.apache.tools.ant.types.selectors.modifiedselector.LastModifiedAlgorithm;
import org.apache.tools.ant.types.selectors.modifiedselector.PropertiesfileCache;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.ResourceUtils;

public class ModifiedSelector
extends BaseExtendSelector
implements BuildListener,
ResourceSelector {
    private static final String CACHE_PREFIX = "cache.";
    private static final String ALGORITHM_PREFIX = "algorithm.";
    private static final String COMPARATOR_PREFIX = "comparator.";
    private CacheName cacheName = null;
    private String cacheClass;
    private AlgorithmName algoName = null;
    private String algorithmClass;
    private ComparatorName compName = null;
    private String comparatorClass;
    private boolean update = true;
    private boolean selectDirectories = true;
    private boolean selectResourcesWithoutInputStream = true;
    private boolean delayUpdate = true;
    private Comparator<? super String> comparator = null;
    private Algorithm algorithm = null;
    private Cache cache = null;
    private int modified = 0;
    private boolean isConfigured = false;
    private List<Parameter> configParameter = Collections.synchronizedList(new ArrayList());
    private List<Parameter> specialParameter = Collections.synchronizedList(new ArrayList());
    private ClassLoader myClassLoader = null;
    private Path classpath = null;

    @Override
    public void verifySettings() {
        this.configure();
        if (this.cache == null) {
            this.setError("Cache must be set.");
        } else if (this.algorithm == null) {
            this.setError("Algorithm must be set.");
        } else if (!this.cache.isValid()) {
            this.setError("Cache must be proper configured.");
        } else if (!this.algorithm.isValid()) {
            this.setError("Algorithm must be proper configured.");
        }
    }

    public void configure() {
        File cachefile;
        if (this.isConfigured) {
            return;
        }
        this.isConfigured = true;
        Project p = this.getProject();
        String filename = "cache.properties";
        if (p != null) {
            cachefile = new File(p.getBaseDir(), filename);
            this.getProject().addBuildListener(this);
        } else {
            cachefile = new File(filename);
            this.setDelayUpdate(false);
        }
        PropertiesfileCache defaultCache = new PropertiesfileCache(cachefile);
        DigestAlgorithm defaultAlgorithm = new DigestAlgorithm();
        EqualComparator defaultComparator = new EqualComparator();
        for (Parameter parameter : this.configParameter) {
            if (parameter.getName().indexOf(46) > 0) {
                this.specialParameter.add(parameter);
                continue;
            }
            this.useParameter(parameter);
        }
        this.configParameter.clear();
        if (this.algoName != null) {
            if ("hashvalue".equals(this.algoName.getValue())) {
                this.algorithm = new HashvalueAlgorithm();
            } else if ("digest".equals(this.algoName.getValue())) {
                this.algorithm = new DigestAlgorithm();
            } else if ("checksum".equals(this.algoName.getValue())) {
                this.algorithm = new ChecksumAlgorithm();
            } else if ("lastmodified".equals(this.algoName.getValue())) {
                this.algorithm = new LastModifiedAlgorithm();
            }
        } else {
            this.algorithm = this.algorithmClass != null ? this.loadClass(this.algorithmClass, "is not an Algorithm.", Algorithm.class) : defaultAlgorithm;
        }
        if (this.cacheName != null) {
            if ("propertyfile".equals(this.cacheName.getValue())) {
                this.cache = new PropertiesfileCache();
            }
        } else {
            this.cache = this.cacheClass != null ? this.loadClass(this.cacheClass, "is not a Cache.", Cache.class) : defaultCache;
        }
        if (this.compName != null) {
            if ("equal".equals(this.compName.getValue())) {
                this.comparator = new EqualComparator();
            } else if ("rule".equals(this.compName.getValue())) {
                throw new BuildException("RuleBasedCollator not yet supported.");
            }
        } else {
            Comparator localComparator;
            this.comparator = this.comparatorClass != null ? (localComparator = this.loadClass(this.comparatorClass, "is not a Comparator.", Comparator.class)) : defaultComparator;
        }
        this.specialParameter.forEach(this::useParameter);
        this.specialParameter.clear();
    }

    protected <T> T loadClass(String classname, String msg, Class<? extends T> type) {
        try {
            ClassLoader cl = this.getClassLoader();
            Class<?> clazz = cl != null ? cl.loadClass(classname) : Class.forName(classname);
            Object rv = clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            if (!type.isInstance(rv)) {
                throw new BuildException("Specified class (%s) %s", classname, msg);
            }
            return (T)rv;
        }
        catch (ClassNotFoundException e) {
            throw new BuildException("Specified class (%s) not found.", classname);
        }
        catch (Exception e) {
            throw new BuildException(e);
        }
    }

    @Override
    public boolean isSelected(Resource resource) {
        if (resource.isFilesystemOnly()) {
            FileResource fileResource = (FileResource)resource;
            File file = fileResource.getFile();
            String filename = fileResource.getName();
            File basedir = fileResource.getBaseDir();
            return this.isSelected(basedir, filename, file);
        }
        try {
            FileUtils fu = FileUtils.getFileUtils();
            File tmpFile = fu.createTempFile(this.getProject(), "modified-", ".tmp", null, true, false);
            FileResource tmpResource = new FileResource(tmpFile);
            ResourceUtils.copyResource(resource, tmpResource);
            boolean isSelected = this.isSelected(tmpFile.getParentFile(), tmpFile.getName(), resource.toLongString());
            tmpFile.delete();
            return isSelected;
        }
        catch (UnsupportedOperationException uoe) {
            this.log("The resource '" + resource.getName() + "' does not provide an InputStream, so it is not checked. According to 'selres' attribute value it is " + (this.selectResourcesWithoutInputStream ? "" : " not") + "selected.", 2);
            return this.selectResourcesWithoutInputStream;
        }
        catch (Exception e) {
            throw new BuildException(e);
        }
    }

    @Override
    public boolean isSelected(File basedir, String filename, File file) {
        return this.isSelected(basedir, filename, file.getAbsolutePath());
    }

    private boolean isSelected(File basedir, String filename, String cacheKey) {
        String newValue;
        boolean rv;
        this.validate();
        File f = new File(basedir, filename);
        if (f.isDirectory()) {
            return this.selectDirectories;
        }
        String cachedValue = String.valueOf(this.cache.get(f.getAbsolutePath()));
        boolean bl = rv = this.comparator.compare(cachedValue, newValue = this.algorithm.getValue(f)) != 0;
        if (this.update && rv) {
            this.cache.put(f.getAbsolutePath(), newValue);
            this.setModified(this.getModified() + 1);
            if (!this.getDelayUpdate()) {
                this.saveCache();
            }
        }
        return rv;
    }

    protected void saveCache() {
        if (this.getModified() > 0) {
            this.cache.save();
            this.setModified(0);
        }
    }

    public void setAlgorithmClass(String classname) {
        this.algorithmClass = classname;
    }

    public void setComparatorClass(String classname) {
        this.comparatorClass = classname;
    }

    public void setCacheClass(String classname) {
        this.cacheClass = classname;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public void setSeldirs(boolean seldirs) {
        this.selectDirectories = seldirs;
    }

    public void setSelres(boolean newValue) {
        this.selectResourcesWithoutInputStream = newValue;
    }

    public int getModified() {
        return this.modified;
    }

    public void setModified(int modified) {
        this.modified = modified;
    }

    public boolean getDelayUpdate() {
        return this.delayUpdate;
    }

    public void setDelayUpdate(boolean delayUpdate) {
        this.delayUpdate = delayUpdate;
    }

    public void addClasspath(Path path) {
        if (this.classpath != null) {
            throw new BuildException("<classpath> can be set only once.");
        }
        this.classpath = path;
    }

    public ClassLoader getClassLoader() {
        if (this.myClassLoader == null) {
            this.myClassLoader = this.classpath == null ? this.getClass().getClassLoader() : this.getProject().createClassLoader(this.classpath);
        }
        return this.myClassLoader;
    }

    public void setClassLoader(ClassLoader loader) {
        this.myClassLoader = loader;
    }

    public void addParam(String key, Object value) {
        Parameter par = new Parameter();
        par.setName(key);
        par.setValue(String.valueOf(value));
        this.configParameter.add(par);
    }

    public void addParam(Parameter parameter) {
        this.configParameter.add(parameter);
    }

    @Override
    public void setParameters(Parameter ... parameters) {
        if (parameters != null) {
            Collections.addAll(this.configParameter, parameters);
        }
    }

    public void useParameter(Parameter parameter) {
        String key = parameter.getName();
        String value = parameter.getValue();
        if ("cache".equals(key)) {
            CacheName cn = new CacheName();
            cn.setValue(value);
            this.setCache(cn);
        } else if ("algorithm".equals(key)) {
            AlgorithmName an = new AlgorithmName();
            an.setValue(value);
            this.setAlgorithm(an);
        } else if ("comparator".equals(key)) {
            ComparatorName cn = new ComparatorName();
            cn.setValue(value);
            this.setComparator(cn);
        } else if ("update".equals(key)) {
            this.setUpdate("true".equalsIgnoreCase(value));
        } else if ("delayupdate".equals(key)) {
            this.setDelayUpdate("true".equalsIgnoreCase(value));
        } else if ("seldirs".equals(key)) {
            this.setSeldirs("true".equalsIgnoreCase(value));
        } else if (key.startsWith(CACHE_PREFIX)) {
            String name = key.substring(CACHE_PREFIX.length());
            this.tryToSetAParameter(this.cache, name, value);
        } else if (key.startsWith(ALGORITHM_PREFIX)) {
            String name = key.substring(ALGORITHM_PREFIX.length());
            this.tryToSetAParameter(this.algorithm, name, value);
        } else if (key.startsWith(COMPARATOR_PREFIX)) {
            String name = key.substring(COMPARATOR_PREFIX.length());
            this.tryToSetAParameter(this.comparator, name, value);
        } else {
            this.setError("Invalid parameter " + key);
        }
    }

    protected void tryToSetAParameter(Object obj, String name, String value) {
        Project prj = this.getProject() != null ? this.getProject() : new Project();
        IntrospectionHelper iHelper = IntrospectionHelper.getHelper(prj, obj.getClass());
        try {
            iHelper.setAttribute(prj, obj, name, value);
        }
        catch (BuildException buildException) {
            // empty catch block
        }
    }

    @Override
    public String toString() {
        return String.format("{modifiedselector update=%s seldirs=%s cache=%s algorithm=%s comparator=%s}", this.update, this.selectDirectories, this.cache, this.algorithm, this.comparator);
    }

    @Override
    public void buildFinished(BuildEvent event) {
        if (this.getDelayUpdate()) {
            this.saveCache();
        }
    }

    @Override
    public void targetFinished(BuildEvent event) {
        if (this.getDelayUpdate()) {
            this.saveCache();
        }
    }

    @Override
    public void taskFinished(BuildEvent event) {
        if (this.getDelayUpdate()) {
            this.saveCache();
        }
    }

    @Override
    public void buildStarted(BuildEvent event) {
    }

    @Override
    public void targetStarted(BuildEvent event) {
    }

    @Override
    public void taskStarted(BuildEvent event) {
    }

    @Override
    public void messageLogged(BuildEvent event) {
    }

    public Cache getCache() {
        return this.cache;
    }

    public void setCache(CacheName name) {
        this.cacheName = name;
    }

    public Algorithm getAlgorithm() {
        return this.algorithm;
    }

    public void setAlgorithm(AlgorithmName name) {
        this.algoName = name;
    }

    public Comparator<? super String> getComparator() {
        return this.comparator;
    }

    public void setComparator(ComparatorName name) {
        this.compName = name;
    }

    public static class CacheName
    extends EnumeratedAttribute {
        @Override
        public String[] getValues() {
            return new String[]{"propertyfile"};
        }
    }

    public static class AlgorithmName
    extends EnumeratedAttribute {
        @Override
        public String[] getValues() {
            return new String[]{"hashvalue", "digest", "checksum", "lastmodified"};
        }
    }

    public static class ComparatorName
    extends EnumeratedAttribute {
        @Override
        public String[] getValues() {
            return new String[]{"equal", "rule"};
        }
    }
}

