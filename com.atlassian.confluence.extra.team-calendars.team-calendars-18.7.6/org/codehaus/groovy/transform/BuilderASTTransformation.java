/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import groovy.lang.GroovyClassLoader;
import groovy.transform.CompilationUnitAware;
import groovy.transform.Undefined;
import groovy.transform.builder.Builder;
import groovy.transform.builder.DefaultStrategy;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.CanonicalASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase=CompilePhase.SEMANTIC_ANALYSIS)
public class BuilderASTTransformation
extends AbstractASTTransformation
implements CompilationUnitAware {
    private static final Class MY_CLASS = Builder.class;
    private static final ClassNode MY_TYPE = ClassHelper.make(MY_CLASS);
    public static final String MY_TYPE_NAME = "@" + MY_TYPE.getNameWithoutPackage();
    public static final ClassNode[] NO_EXCEPTIONS = ClassNode.EMPTY_ARRAY;
    public static final Parameter[] NO_PARAMS = Parameter.EMPTY_ARRAY;
    private CompilationUnit compilationUnit;

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        this.init(nodes, source);
        AnnotatedNode parent = (AnnotatedNode)nodes[1];
        AnnotationNode anno = (AnnotationNode)nodes[0];
        if (!MY_TYPE.equals(anno.getClassNode())) {
            return;
        }
        if (parent instanceof ClassNode || parent instanceof MethodNode) {
            if (parent instanceof ClassNode && !this.checkNotInterface((ClassNode)parent, MY_TYPE_NAME)) {
                return;
            }
            if (parent instanceof MethodNode && !this.checkStatic((MethodNode)parent, MY_TYPE_NAME)) {
                return;
            }
            GroovyClassLoader classLoader = this.compilationUnit != null ? this.compilationUnit.getTransformLoader() : source.getClassLoader();
            BuilderStrategy strategy = this.createBuilderStrategy(anno, classLoader);
            if (strategy == null) {
                return;
            }
            strategy.build(this, parent, anno);
        }
    }

    private boolean checkStatic(MethodNode mNode, String annotationName) {
        if (!(mNode.isStatic() || mNode.isStaticConstructor() || mNode instanceof ConstructorNode)) {
            this.addError("Error processing method '" + mNode.getName() + "'. " + annotationName + " not allowed for instance methods.", mNode);
            return false;
        }
        return true;
    }

    private BuilderStrategy createBuilderStrategy(AnnotationNode anno, GroovyClassLoader loader) {
        ClassNode strategyClass = this.getMemberClassValue(anno, "builderStrategy", ClassHelper.make(DefaultStrategy.class));
        if (strategyClass == null) {
            this.addError("Couldn't determine builderStrategy class", anno);
            return null;
        }
        String className = strategyClass.getName();
        try {
            Object instance = loader.loadClass(className).newInstance();
            if (instance == null) {
                this.addError("Can't load builderStrategy '" + className + "'", anno);
                return null;
            }
            if (!BuilderStrategy.class.isAssignableFrom(instance.getClass())) {
                this.addError("The builderStrategy class '" + strategyClass.getName() + "' on " + MY_TYPE_NAME + " is not a builderStrategy", anno);
                return null;
            }
            return (BuilderStrategy)instance;
        }
        catch (Exception e) {
            this.addError("Can't load builderStrategy '" + className + "' " + e, anno);
            return null;
        }
    }

    @Override
    public void setCompilationUnit(CompilationUnit unit) {
        this.compilationUnit = unit;
    }

    public static abstract class AbstractBuilderStrategy
    implements BuilderStrategy {
        protected static List<PropertyInfo> getPropertyInfoFromClassNode(ClassNode cNode, List<String> includes, List<String> excludes) {
            ArrayList<PropertyInfo> props = new ArrayList<PropertyInfo>();
            for (FieldNode fNode : GeneralUtils.getInstancePropertyFields(cNode)) {
                if (AbstractASTTransformation.shouldSkip(fNode.getName(), excludes, includes)) continue;
                props.add(new PropertyInfo(fNode.getName(), fNode.getType()));
            }
            return props;
        }

        protected String getSetterName(String prefix, String fieldName) {
            return prefix.isEmpty() ? fieldName : prefix + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        }

        protected boolean unsupportedAttribute(BuilderASTTransformation transform, AnnotationNode anno, String memberName) {
            return this.unsupportedAttribute(transform, anno, memberName, "");
        }

        protected boolean unsupportedAttribute(BuilderASTTransformation transform, AnnotationNode anno, String memberName, String extraMessage) {
            Object memberValue = transform.getMemberValue(anno, memberName);
            if (memberValue != null && memberValue instanceof String && Undefined.isUndefined((String)memberValue)) {
                return false;
            }
            if (memberValue == null && (memberValue = transform.getMemberClassValue(anno, memberName)) != null && Undefined.isUndefined((ClassNode)memberValue)) {
                memberValue = null;
            }
            if (memberValue != null) {
                String message = extraMessage.length() == 0 ? "" : " " + extraMessage;
                transform.addError("Error during " + MY_TYPE_NAME + " processing: Annotation attribute '" + memberName + "' not supported by " + this.getClass().getName() + message, anno);
                return true;
            }
            return false;
        }

        protected void checkKnownProperty(BuilderASTTransformation transform, AnnotationNode anno, String name, List<PropertyInfo> properties) {
            for (PropertyInfo prop : properties) {
                if (!name.equals(prop.getName())) continue;
                return;
            }
            transform.addError("Error during " + MY_TYPE_NAME + " processing: tried to include unknown property '" + name + "'", anno);
        }

        protected void checkKnownField(BuilderASTTransformation transform, AnnotationNode anno, String name, List<FieldNode> fields) {
            for (FieldNode field : fields) {
                if (!name.equals(field.getName())) continue;
                return;
            }
            transform.addError("Error during " + MY_TYPE_NAME + " processing: tried to include unknown property '" + name + "'", anno);
        }

        protected boolean getIncludeExclude(BuilderASTTransformation transform, AnnotationNode anno, ClassNode cNode, List<String> excludes, List<String> includes) {
            List<String> directIncludes;
            List<String> directExcludes = BuilderASTTransformation.getMemberList(anno, "excludes");
            if (directExcludes != null) {
                excludes.addAll(directExcludes);
            }
            if ((directIncludes = BuilderASTTransformation.getMemberList(anno, "includes")) != null) {
                includes.addAll(directIncludes);
            }
            if (includes.isEmpty() && excludes.isEmpty() && transform.hasAnnotation(cNode, CanonicalASTTransformation.MY_TYPE)) {
                AnnotationNode canonical = cNode.getAnnotations(CanonicalASTTransformation.MY_TYPE).get(0);
                if (excludes.isEmpty()) {
                    List<String> canonicalExcludes = BuilderASTTransformation.getMemberList(canonical, "excludes");
                    if (canonicalExcludes != null) {
                        excludes.addAll(canonicalExcludes);
                    }
                }
                if (includes.isEmpty()) {
                    List<String> canonicalIncludes = BuilderASTTransformation.getMemberList(canonical, "includes");
                    if (canonicalIncludes != null) {
                        includes.addAll(canonicalIncludes);
                    }
                }
            }
            return transform.checkIncludeExclude(anno, excludes, includes, MY_TYPE_NAME);
        }

        protected static class PropertyInfo {
            private String name;
            private ClassNode type;

            public PropertyInfo(String name, ClassNode type) {
                this.name = name;
                this.type = type;
            }

            public String getName() {
                return this.name;
            }

            public ClassNode getType() {
                return this.type;
            }

            public void setName(String name) {
                this.name = name;
            }

            public void setType(ClassNode type) {
                this.type = type;
            }
        }
    }

    public static interface BuilderStrategy {
        public void build(BuilderASTTransformation var1, AnnotatedNode var2, AnnotationNode var3);
    }
}

