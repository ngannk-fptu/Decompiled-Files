/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import java.util.Properties;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.CompositeMapper;
import org.apache.tools.ant.util.ContainerMapper;
import org.apache.tools.ant.util.FileNameMapper;

public class Mapper
extends DataType {
    protected MapperType type = null;
    protected String classname = null;
    protected Path classpath = null;
    protected String from = null;
    protected String to = null;
    private ContainerMapper container = null;

    public Mapper(Project p) {
        this.setProject(p);
    }

    public void setType(MapperType type) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.type = type;
    }

    public void addConfigured(FileNameMapper fileNameMapper) {
        this.add(fileNameMapper);
    }

    public void add(FileNameMapper fileNameMapper) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        if (this.container == null) {
            if (this.type == null && this.classname == null) {
                this.container = new CompositeMapper();
            } else {
                FileNameMapper m = this.getImplementation();
                if (m instanceof ContainerMapper) {
                    this.container = (ContainerMapper)m;
                } else {
                    throw new BuildException(m + " mapper implementation does not support nested mappers!");
                }
            }
        }
        this.container.add(fileNameMapper);
        this.setChecked(false);
    }

    public void addConfiguredMapper(Mapper mapper) {
        this.add(mapper.getImplementation());
    }

    public void setClassname(String classname) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.classname = classname;
    }

    public void setClasspath(Path classpath) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        if (this.classpath == null) {
            this.classpath = classpath;
        } else {
            this.classpath.append(classpath);
        }
    }

    public Path createClasspath() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        if (this.classpath == null) {
            this.classpath = new Path(this.getProject());
        }
        this.setChecked(false);
        return this.classpath.createPath();
    }

    public void setClasspathRef(Reference ref) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.createClasspath().setRefid(ref);
    }

    public void setFrom(String from) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.from = from;
    }

    public void setTo(String to) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.to = to;
    }

    @Override
    public void setRefid(Reference r) throws BuildException {
        if (this.type != null || this.from != null || this.to != null) {
            throw this.tooManyAttributes();
        }
        super.setRefid(r);
    }

    public FileNameMapper getImplementation() throws BuildException {
        if (this.isReference()) {
            this.dieOnCircularReference();
            Reference r = this.getRefid();
            Object o = r.getReferencedObject(this.getProject());
            if (o instanceof FileNameMapper) {
                return (FileNameMapper)o;
            }
            if (o instanceof Mapper) {
                return ((Mapper)o).getImplementation();
            }
            String od = o == null ? "null" : o.getClass().getName();
            throw new BuildException(od + " at reference '" + r.getRefId() + "' is not a valid mapper reference.");
        }
        if (this.type == null && this.classname == null && this.container == null) {
            throw new BuildException("nested mapper or one of the attributes type or classname is required");
        }
        if (this.container != null) {
            return this.container;
        }
        if (this.type != null && this.classname != null) {
            throw new BuildException("must not specify both type and classname attribute");
        }
        try {
            FileNameMapper m = this.getImplementationClass().getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            Project p = this.getProject();
            if (p != null) {
                p.setProjectReference(m);
            }
            m.setFrom(this.from);
            m.setTo(this.to);
            return m;
        }
        catch (BuildException be) {
            throw be;
        }
        catch (Throwable t) {
            throw new BuildException(t);
        }
    }

    protected Class<? extends FileNameMapper> getImplementationClass() throws ClassNotFoundException {
        String cName = this.classname;
        if (this.type != null) {
            cName = this.type.getImplementation();
        }
        ClassLoader loader = this.classpath == null ? this.getClass().getClassLoader() : this.getProject().createClassLoader(this.classpath);
        return Class.forName(cName, true, loader).asSubclass(FileNameMapper.class);
    }

    @Deprecated
    protected Mapper getRef() {
        return this.getCheckedRef(Mapper.class);
    }

    public static class MapperType
    extends EnumeratedAttribute {
        private Properties implementations = new Properties();

        public MapperType() {
            this.implementations.put("identity", "org.apache.tools.ant.util.IdentityMapper");
            this.implementations.put("flatten", "org.apache.tools.ant.util.FlatFileNameMapper");
            this.implementations.put("glob", "org.apache.tools.ant.util.GlobPatternMapper");
            this.implementations.put("merge", "org.apache.tools.ant.util.MergingMapper");
            this.implementations.put("regexp", "org.apache.tools.ant.util.RegexpPatternMapper");
            this.implementations.put("package", "org.apache.tools.ant.util.PackageNameMapper");
            this.implementations.put("unpackage", "org.apache.tools.ant.util.UnPackageNameMapper");
        }

        @Override
        public String[] getValues() {
            return new String[]{"identity", "flatten", "glob", "merge", "regexp", "package", "unpackage"};
        }

        public String getImplementation() {
            return this.implementations.getProperty(this.getValue());
        }
    }
}

