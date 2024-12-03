/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.codegen.bean;

import com.mchange.v1.lang.ClassUtils;
import com.mchange.v2.codegen.CodegenUtils;
import com.mchange.v2.codegen.IndentedWriter;
import com.mchange.v2.codegen.bean.BeangenUtils;
import com.mchange.v2.codegen.bean.ClassInfo;
import com.mchange.v2.codegen.bean.GeneratorExtension;
import com.mchange.v2.codegen.bean.Property;
import com.mchange.v2.codegen.bean.PropertyBeanGenerator;
import com.mchange.v2.codegen.bean.SerializableExtension;
import com.mchange.v2.codegen.bean.SimpleClassInfo;
import com.mchange.v2.codegen.bean.SimpleProperty;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SimplePropertyBeanGenerator
implements PropertyBeanGenerator {
    private static final MLogger logger = MLog.getLogger(SimplePropertyBeanGenerator.class);
    private boolean inner = false;
    private int java_version = 140;
    private boolean force_unmodifiable = false;
    private String generatorName = this.getClass().getName();
    protected ClassInfo info;
    protected Property[] props;
    protected IndentedWriter iw;
    protected Set generalImports;
    protected Set specificImports;
    protected Set interfaceNames;
    protected Class superclassType;
    protected List interfaceTypes;
    protected Class[] propertyTypes;
    protected List generatorExtensions = new ArrayList();

    public synchronized void setInner(boolean bl) {
        this.inner = bl;
    }

    public synchronized boolean isInner() {
        return this.inner;
    }

    public synchronized void setJavaVersion(int n) {
        this.java_version = n;
    }

    public synchronized int getJavaVersion() {
        return this.java_version;
    }

    public synchronized void setGeneratorName(String string) {
        this.generatorName = string;
    }

    public synchronized String getGeneratorName() {
        return this.generatorName;
    }

    public synchronized void setForceUnmodifiable(boolean bl) {
        this.force_unmodifiable = bl;
    }

    public synchronized boolean isForceUnmodifiable() {
        return this.force_unmodifiable;
    }

    public synchronized void addExtension(GeneratorExtension generatorExtension) {
        this.generatorExtensions.add(generatorExtension);
    }

    public synchronized void removeExtension(GeneratorExtension generatorExtension) {
        this.generatorExtensions.remove(generatorExtension);
    }

    @Override
    public synchronized void generate(ClassInfo classInfo, Property[] propertyArray, Writer writer) throws IOException {
        this.info = classInfo;
        this.props = propertyArray;
        Arrays.sort(propertyArray, BeangenUtils.PROPERTY_COMPARATOR);
        this.iw = writer instanceof IndentedWriter ? (IndentedWriter)writer : new IndentedWriter(writer);
        this.generalImports = new TreeSet();
        if (classInfo.getGeneralImports() != null) {
            this.generalImports.addAll(Arrays.asList(classInfo.getGeneralImports()));
        }
        this.specificImports = new TreeSet();
        if (classInfo.getSpecificImports() != null) {
            this.specificImports.addAll(Arrays.asList(classInfo.getSpecificImports()));
        }
        this.interfaceNames = new TreeSet();
        if (classInfo.getInterfaceNames() != null) {
            this.interfaceNames.addAll(Arrays.asList(classInfo.getInterfaceNames()));
        }
        this.addInternalImports();
        this.addInternalInterfaces();
        this.resolveTypes();
        if (!this.inner) {
            this.writeHeader();
            this.iw.println();
        }
        this.generateClassJavaDocComment();
        this.writeClassDeclaration();
        this.iw.println('{');
        this.iw.upIndent();
        this.writeCoreBody();
        this.iw.downIndent();
        this.iw.println('}');
    }

    protected void resolveTypes() {
        String[] stringArray = this.generalImports.toArray(new String[this.generalImports.size()]);
        String[] stringArray2 = this.specificImports.toArray(new String[this.specificImports.size()]);
        if (this.info.getSuperclassName() != null) {
            try {
                this.superclassType = ClassUtils.forName(this.info.getSuperclassName(), stringArray, stringArray2);
            }
            catch (Exception exception) {
                if (logger.isLoggable(MLevel.WARNING)) {
                    logger.warning(this.getClass().getName() + " could not resolve superclass '" + this.info.getSuperclassName() + "'.");
                }
                this.superclassType = null;
            }
        }
        this.interfaceTypes = new ArrayList(this.interfaceNames.size());
        for (String string : this.interfaceNames) {
            try {
                this.interfaceTypes.add(ClassUtils.forName(string, stringArray, stringArray2));
            }
            catch (Exception exception) {
                if (logger.isLoggable(MLevel.WARNING)) {
                    logger.warning(this.getClass().getName() + " could not resolve interface '" + string + "'.");
                }
                this.interfaceTypes.add(null);
            }
        }
        this.propertyTypes = new Class[this.props.length];
        int n = this.props.length;
        for (int i = 0; i < n; ++i) {
            String string = this.props[i].getSimpleTypeName();
            try {
                this.propertyTypes[i] = ClassUtils.forName(string, stringArray, stringArray2);
                continue;
            }
            catch (Exception exception) {
                if (logger.isLoggable(MLevel.WARNING)) {
                    logger.log(MLevel.WARNING, this.getClass().getName() + " could not resolve property type '" + string + "'.", exception);
                }
                this.propertyTypes[i] = null;
            }
        }
    }

    protected void addInternalImports() {
        if (this.boundProperties()) {
            this.specificImports.add("java.beans.PropertyChangeEvent");
            this.specificImports.add("java.beans.PropertyChangeSupport");
            this.specificImports.add("java.beans.PropertyChangeListener");
        }
        if (this.constrainedProperties()) {
            this.specificImports.add("java.beans.PropertyChangeEvent");
            this.specificImports.add("java.beans.PropertyVetoException");
            this.specificImports.add("java.beans.VetoableChangeSupport");
            this.specificImports.add("java.beans.VetoableChangeListener");
        }
        for (GeneratorExtension generatorExtension : this.generatorExtensions) {
            this.specificImports.addAll(generatorExtension.extraSpecificImports());
            this.generalImports.addAll(generatorExtension.extraGeneralImports());
        }
    }

    protected void addInternalInterfaces() {
        for (GeneratorExtension generatorExtension : this.generatorExtensions) {
            this.interfaceNames.addAll(generatorExtension.extraInterfaceNames());
        }
    }

    protected void writeCoreBody() throws IOException {
        this.writeJavaBeansChangeSupport();
        this.writePropertyVariables();
        this.writeOtherVariables();
        this.iw.println();
        this.writeGetterSetterPairs();
        if (this.boundProperties()) {
            this.iw.println();
            this.writeBoundPropertyEventSourceMethods();
        }
        if (this.constrainedProperties()) {
            this.iw.println();
            this.writeConstrainedPropertyEventSourceMethods();
        }
        this.writeInternalUtilityFunctions();
        this.writeOtherFunctions();
        this.writeOtherClasses();
        String[] stringArray = this.interfaceNames.toArray(new String[this.interfaceNames.size()]);
        String[] stringArray2 = this.generalImports.toArray(new String[this.generalImports.size()]);
        String[] stringArray3 = this.specificImports.toArray(new String[this.specificImports.size()]);
        SimpleClassInfo simpleClassInfo = new SimpleClassInfo(this.info.getPackageName(), this.info.getModifiers(), this.info.getClassName(), this.info.getSuperclassName(), stringArray, stringArray2, stringArray3);
        for (GeneratorExtension generatorExtension : this.generatorExtensions) {
            this.iw.println();
            generatorExtension.generate(simpleClassInfo, this.superclassType, this.props, this.propertyTypes, this.iw);
        }
    }

    protected void writeInternalUtilityFunctions() throws IOException {
        this.iw.println("private boolean eqOrBothNull( Object a, Object b )");
        this.iw.println("{");
        this.iw.upIndent();
        this.iw.println("return");
        this.iw.upIndent();
        this.iw.println("a == b ||");
        this.iw.println("(a != null && a.equals(b));");
        this.iw.downIndent();
        this.iw.downIndent();
        this.iw.println("}");
    }

    protected void writeConstrainedPropertyEventSourceMethods() throws IOException {
        this.iw.println("public void addVetoableChangeListener( VetoableChangeListener vcl )");
        this.iw.println("{ vcs.addVetoableChangeListener( vcl ); }");
        this.iw.println();
        this.iw.println("public void removeVetoableChangeListener( VetoableChangeListener vcl )");
        this.iw.println("{ vcs.removeVetoableChangeListener( vcl ); }");
        this.iw.println();
        if (this.java_version >= 140) {
            this.iw.println("public VetoableChangeListener[] getVetoableChangeListeners()");
            this.iw.println("{ return vcs.getVetoableChangeListeners(); }");
        }
    }

    protected void writeBoundPropertyEventSourceMethods() throws IOException {
        this.iw.println("public void addPropertyChangeListener( PropertyChangeListener pcl )");
        this.iw.println("{ pcs.addPropertyChangeListener( pcl ); }");
        this.iw.println();
        this.iw.println("public void addPropertyChangeListener( String propName, PropertyChangeListener pcl )");
        this.iw.println("{ pcs.addPropertyChangeListener( propName, pcl ); }");
        this.iw.println();
        this.iw.println("public void removePropertyChangeListener( PropertyChangeListener pcl )");
        this.iw.println("{ pcs.removePropertyChangeListener( pcl ); }");
        this.iw.println();
        this.iw.println("public void removePropertyChangeListener( String propName, PropertyChangeListener pcl )");
        this.iw.println("{ pcs.removePropertyChangeListener( propName, pcl ); }");
        this.iw.println();
        if (this.java_version >= 140) {
            this.iw.println("public PropertyChangeListener[] getPropertyChangeListeners()");
            this.iw.println("{ return pcs.getPropertyChangeListeners(); }");
        }
    }

    protected void writeJavaBeansChangeSupport() throws IOException {
        if (this.boundProperties()) {
            this.iw.println("protected PropertyChangeSupport pcs = new PropertyChangeSupport( this );");
            this.iw.println();
            this.iw.println("protected PropertyChangeSupport getPropertyChangeSupport()");
            this.iw.println("{ return pcs; }");
        }
        if (this.constrainedProperties()) {
            this.iw.println("protected VetoableChangeSupport vcs = new VetoableChangeSupport( this );");
            this.iw.println();
            this.iw.println("protected VetoableChangeSupport getVetoableChangeSupport()");
            this.iw.println("{ return vcs; }");
        }
    }

    protected void writeOtherVariables() throws IOException {
    }

    protected void writeOtherFunctions() throws IOException {
    }

    protected void writeOtherClasses() throws IOException {
    }

    protected void writePropertyVariables() throws IOException {
        int n = this.props.length;
        for (int i = 0; i < n; ++i) {
            this.writePropertyVariable(this.props[i]);
        }
    }

    protected void writePropertyVariable(Property property) throws IOException {
        BeangenUtils.writePropertyVariable(property, this.iw);
    }

    protected void writePropertyMembers() throws IOException {
        throw new InternalError("writePropertyMembers() deprecated and removed. please us writePropertyVariables().");
    }

    protected void writePropertyMember(Property property) throws IOException {
        throw new InternalError("writePropertyMember() deprecated and removed. please us writePropertyVariable().");
    }

    protected void writeGetterSetterPairs() throws IOException {
        int n = this.props.length;
        for (int i = 0; i < n; ++i) {
            this.writeGetterSetterPair(this.props[i], this.propertyTypes[i]);
            if (i == n - 1) continue;
            this.iw.println();
        }
    }

    protected void writeGetterSetterPair(Property property, Class clazz) throws IOException {
        this.writePropertyGetter(property, clazz);
        if (!property.isReadOnly() && !this.force_unmodifiable) {
            this.iw.println();
            this.writePropertySetter(property, clazz);
        }
    }

    protected void writePropertyGetter(Property property, Class clazz) throws IOException {
        BeangenUtils.writePropertyGetter(property, this.getGetterDefensiveCopyExpression(property, clazz), this.iw);
    }

    protected void writePropertySetter(Property property, Class clazz) throws IOException {
        BeangenUtils.writePropertySetter(property, this.getSetterDefensiveCopyExpression(property, clazz), this.iw);
    }

    protected String getGetterDefensiveCopyExpression(Property property, Class clazz) {
        return property.getDefensiveCopyExpression();
    }

    protected String getSetterDefensiveCopyExpression(Property property, Class clazz) {
        return property.getDefensiveCopyExpression();
    }

    protected String getConstructorDefensiveCopyExpression(Property property, Class clazz) {
        return property.getDefensiveCopyExpression();
    }

    protected void writeHeader() throws IOException {
        this.writeBannerComments();
        this.iw.println();
        this.iw.println("package " + this.info.getPackageName() + ';');
        this.iw.println();
        this.writeImports();
    }

    protected void writeBannerComments() throws IOException {
        this.iw.println("/*");
        this.iw.println(" * This class autogenerated by " + this.generatorName + '.');
        this.iw.println(" * " + new Date());
        this.iw.println(" * DO NOT HAND EDIT!");
        this.iw.println(" */");
    }

    protected void generateClassJavaDocComment() throws IOException {
        this.iw.println("/**");
        this.iw.println(" * This class was generated by " + this.generatorName + ".");
        this.iw.println(" */");
    }

    protected void writeImports() throws IOException {
        Iterator iterator = this.generalImports.iterator();
        while (iterator.hasNext()) {
            this.iw.println("import " + iterator.next() + ".*;");
        }
        iterator = this.specificImports.iterator();
        while (iterator.hasNext()) {
            this.iw.println("import " + iterator.next() + ";");
        }
    }

    protected void writeClassDeclaration() throws IOException {
        this.iw.print(CodegenUtils.getModifierString(this.info.getModifiers()) + " class " + this.info.getClassName());
        String string = this.info.getSuperclassName();
        if (string != null) {
            this.iw.print(" extends " + string);
        }
        if (this.interfaceNames.size() > 0) {
            this.iw.print(" implements ");
            boolean bl = true;
            Iterator iterator = this.interfaceNames.iterator();
            while (iterator.hasNext()) {
                if (bl) {
                    bl = false;
                } else {
                    this.iw.print(", ");
                }
                this.iw.print((String)iterator.next());
            }
        }
        this.iw.println();
    }

    boolean boundProperties() {
        return BeangenUtils.hasBoundProperties(this.props);
    }

    boolean constrainedProperties() {
        return BeangenUtils.hasConstrainedProperties(this.props);
    }

    public static void main(String[] stringArray) {
        try {
            SimpleClassInfo simpleClassInfo = new SimpleClassInfo("test", 1, stringArray[0], null, null, new String[]{"java.awt"}, null);
            Property[] propertyArray = new Property[]{new SimpleProperty("number", "int", null, "7", false, true, false), new SimpleProperty("fpNumber", "float", null, null, true, true, false), new SimpleProperty("location", "Point", "new Point( location.x, location.y )", "new Point( 0, 0 )", false, true, true)};
            FileWriter fileWriter = new FileWriter(stringArray[0] + ".java");
            SimplePropertyBeanGenerator simplePropertyBeanGenerator = new SimplePropertyBeanGenerator();
            simplePropertyBeanGenerator.addExtension(new SerializableExtension());
            simplePropertyBeanGenerator.generate(simpleClassInfo, propertyArray, fileWriter);
            fileWriter.flush();
            fileWriter.close();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}

