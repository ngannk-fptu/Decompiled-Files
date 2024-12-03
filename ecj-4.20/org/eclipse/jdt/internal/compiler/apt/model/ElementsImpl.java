/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.apt.model;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import org.eclipse.jdt.internal.compiler.apt.model.AnnotationMirrorImpl;
import org.eclipse.jdt.internal.compiler.apt.model.ElementImpl;
import org.eclipse.jdt.internal.compiler.apt.model.ElementsImpl9;
import org.eclipse.jdt.internal.compiler.apt.model.ExecutableElementImpl;
import org.eclipse.jdt.internal.compiler.apt.model.Factory;
import org.eclipse.jdt.internal.compiler.apt.model.NameImpl;
import org.eclipse.jdt.internal.compiler.apt.model.PackageElementImpl;
import org.eclipse.jdt.internal.compiler.apt.model.TypeElementImpl;
import org.eclipse.jdt.internal.compiler.apt.model.VariableElementImpl;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Javadoc;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodVerifier;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;

public class ElementsImpl
implements Elements {
    private static final Pattern INITIAL_DELIMITER = Pattern.compile("^\\s*/\\*+");
    protected final BaseProcessingEnvImpl _env;

    protected ElementsImpl(BaseProcessingEnvImpl env) {
        this._env = env;
    }

    public static ElementsImpl create(BaseProcessingEnvImpl env) {
        return SourceVersion.latest().compareTo(SourceVersion.RELEASE_8) <= 0 ? new ElementsImpl(env) : new ElementsImpl9(env);
    }

    @Override
    public List<? extends AnnotationMirror> getAllAnnotationMirrors(Element e) {
        if (e.getKind() == ElementKind.CLASS && e instanceof TypeElementImpl) {
            ArrayList<AnnotationBinding> annotations = new ArrayList<AnnotationBinding>();
            HashSet<ReferenceBinding> annotationTypes = new HashSet<ReferenceBinding>();
            ReferenceBinding binding = (ReferenceBinding)((TypeElementImpl)e)._binding;
            boolean checkIfInherited = false;
            while (binding != null) {
                if (binding instanceof ParameterizedTypeBinding) {
                    binding = ((ParameterizedTypeBinding)binding).genericType();
                }
                AnnotationBinding[] annotationBindingArray = Factory.getPackedAnnotationBindings(binding.getAnnotations());
                int n = annotationBindingArray.length;
                int n2 = 0;
                while (n2 < n) {
                    AnnotationBinding annotation = annotationBindingArray[n2];
                    if (annotation != null) {
                        ReferenceBinding annotationType = annotation.getAnnotationType();
                        if (!(checkIfInherited && (annotationType.getAnnotationTagBits() & 0x1000000000000L) == 0L || annotationTypes.contains(annotationType))) {
                            annotationTypes.add(annotationType);
                            annotations.add(annotation);
                        }
                    }
                    ++n2;
                }
                binding = binding.superclass();
                checkIfInherited = true;
            }
            ArrayList<AnnotationMirror> list = new ArrayList<AnnotationMirror>(annotations.size());
            for (AnnotationBinding annotation : annotations) {
                list.add(this._env.getFactory().newAnnotationMirror(annotation));
            }
            return Collections.unmodifiableList(list);
        }
        return e.getAnnotationMirrors();
    }

    @Override
    public List<? extends Element> getAllMembers(TypeElement type) {
        if (type == null || !(type instanceof TypeElementImpl)) {
            return Collections.emptyList();
        }
        ReferenceBinding binding = (ReferenceBinding)((TypeElementImpl)type)._binding;
        HashMap<String, ReferenceBinding> types = new HashMap<String, ReferenceBinding>();
        ArrayList<FieldBinding> fields = new ArrayList<FieldBinding>();
        HashMap<String, Set<MethodBinding>> methods = new HashMap<String, Set<MethodBinding>>();
        LinkedHashSet<ReferenceBinding> superinterfaces = new LinkedHashSet<ReferenceBinding>();
        boolean ignoreVisibility = true;
        while (binding != null) {
            this.addMembers(binding, ignoreVisibility, types, fields, methods);
            LinkedHashSet<ReferenceBinding> newfound = new LinkedHashSet<ReferenceBinding>();
            this.collectSuperInterfaces(binding, superinterfaces, newfound);
            for (ReferenceBinding superinterface : newfound) {
                this.addMembers(superinterface, false, types, fields, methods);
            }
            superinterfaces.addAll(newfound);
            binding = binding.superclass();
            ignoreVisibility = false;
        }
        ArrayList<Element> allMembers = new ArrayList<Element>();
        for (ReferenceBinding nestedType : types.values()) {
            allMembers.add(this._env.getFactory().newElement(nestedType));
        }
        for (FieldBinding field : fields) {
            allMembers.add(this._env.getFactory().newElement(field));
        }
        for (Set sameNamedMethods : methods.values()) {
            for (MethodBinding method : sameNamedMethods) {
                allMembers.add(this._env.getFactory().newElement(method));
            }
        }
        return allMembers;
    }

    private void collectSuperInterfaces(ReferenceBinding type, Set<ReferenceBinding> existing, Set<ReferenceBinding> newfound) {
        ReferenceBinding[] referenceBindingArray = type.superInterfaces();
        int n = referenceBindingArray.length;
        int n2 = 0;
        while (n2 < n) {
            ReferenceBinding superinterface = referenceBindingArray[n2];
            if (!existing.contains(superinterface) && !newfound.contains(superinterface)) {
                newfound.add(superinterface);
                this.collectSuperInterfaces(superinterface, existing, newfound);
            }
            ++n2;
        }
    }

    private void addMembers(ReferenceBinding binding, boolean ignoreVisibility, Map<String, ReferenceBinding> types, List<FieldBinding> fields, Map<String, Set<MethodBinding>> methods) {
        Binding[] bindingArray = binding.memberTypes();
        int n = bindingArray.length;
        int n2 = 0;
        while (n2 < n) {
            String name;
            ReferenceBinding subtype = bindingArray[n2];
            if ((ignoreVisibility || !subtype.isPrivate()) && types.get(name = new String(subtype.sourceName())) == null) {
                types.put(name, subtype);
            }
            ++n2;
        }
        bindingArray = binding.fields();
        n = bindingArray.length;
        n2 = 0;
        while (n2 < n) {
            Binding field = bindingArray[n2];
            if (ignoreVisibility || !((FieldBinding)field).isPrivate()) {
                fields.add((FieldBinding)field);
            }
            ++n2;
        }
        bindingArray = binding.methods();
        n = bindingArray.length;
        n2 = 0;
        while (n2 < n) {
            Binding method = bindingArray[n2];
            if (!((MethodBinding)method).isSynthetic() && (ignoreVisibility || !((MethodBinding)method).isPrivate() && !((MethodBinding)method).isConstructor())) {
                String methodName = new String(((MethodBinding)method).selector);
                Set<MethodBinding> sameNamedMethods = methods.get(methodName);
                if (sameNamedMethods == null) {
                    sameNamedMethods = new HashSet<MethodBinding>(4);
                    methods.put(methodName, sameNamedMethods);
                    sameNamedMethods.add((MethodBinding)method);
                } else {
                    boolean unique = true;
                    if (!ignoreVisibility) {
                        for (MethodBinding existing : sameNamedMethods) {
                            MethodVerifier verifier = this._env.getLookupEnvironment().methodVerifier();
                            if (!verifier.doesMethodOverride(existing, (MethodBinding)method)) continue;
                            unique = false;
                            break;
                        }
                    }
                    if (unique) {
                        sameNamedMethods.add((MethodBinding)method);
                    }
                }
            }
            ++n2;
        }
    }

    @Override
    public Name getBinaryName(TypeElement type) {
        TypeElementImpl typeElementImpl = (TypeElementImpl)type;
        ReferenceBinding referenceBinding = (ReferenceBinding)typeElementImpl._binding;
        return new NameImpl(CharOperation.replaceOnCopy(referenceBinding.constantPoolName(), '/', '.'));
    }

    @Override
    public String getConstantExpression(Object value) {
        if (!(value instanceof Integer || value instanceof Byte || value instanceof Float || value instanceof Double || value instanceof Long || value instanceof Short || value instanceof Character || value instanceof String || value instanceof Boolean)) {
            throw new IllegalArgumentException("Not a valid wrapper type : " + value.getClass());
        }
        if (value instanceof Character) {
            StringBuilder builder = new StringBuilder();
            builder.append('\'').append(value).append('\'');
            return String.valueOf(builder);
        }
        if (value instanceof String) {
            StringBuilder builder = new StringBuilder();
            builder.append('\"').append(value).append('\"');
            return String.valueOf(builder);
        }
        if (value instanceof Float) {
            StringBuilder builder = new StringBuilder();
            builder.append(value).append('f');
            return String.valueOf(builder);
        }
        if (value instanceof Long) {
            StringBuilder builder = new StringBuilder();
            builder.append(value).append('L');
            return String.valueOf(builder);
        }
        if (value instanceof Short) {
            StringBuilder builder = new StringBuilder();
            builder.append("(short)").append(value);
            return String.valueOf(builder);
        }
        if (value instanceof Byte) {
            StringBuilder builder = new StringBuilder();
            builder.append("(byte)0x");
            byte intValue = (Byte)value;
            String hexString = Integer.toHexString(intValue & 0xFF);
            if (hexString.length() < 2) {
                builder.append('0');
            }
            builder.append(hexString);
            return String.valueOf(builder);
        }
        return String.valueOf(value);
    }

    @Override
    public String getDocComment(Element e) {
        char[] unparsed = this.getUnparsedDocComment(e);
        return ElementsImpl.formatJavadoc(unparsed);
    }

    private char[] getUnparsedDocComment(Element e) {
        char[] contents;
        Javadoc javadoc = null;
        ReferenceContext referenceContext = null;
        switch (e.getKind()) {
            case ENUM: 
            case CLASS: 
            case ANNOTATION_TYPE: 
            case INTERFACE: 
            case RECORD: {
                TypeElementImpl typeElementImpl = (TypeElementImpl)e;
                ReferenceBinding referenceBinding = (ReferenceBinding)typeElementImpl._binding;
                if (!(referenceBinding instanceof SourceTypeBinding)) break;
                SourceTypeBinding sourceTypeBinding = (SourceTypeBinding)referenceBinding;
                referenceContext = sourceTypeBinding.scope.referenceContext;
                javadoc = ((TypeDeclaration)referenceContext).javadoc;
                break;
            }
            case PACKAGE: {
                PackageElementImpl packageElementImpl = (PackageElementImpl)e;
                PackageBinding packageBinding = (PackageBinding)packageElementImpl._binding;
                char[][] compoundName = CharOperation.arrayConcat(packageBinding.compoundName, TypeConstants.PACKAGE_INFO_NAME);
                ReferenceBinding type = this._env.getLookupEnvironment().getType(compoundName);
                if (type == null || !type.isValidBinding() || !(type instanceof SourceTypeBinding)) break;
                SourceTypeBinding sourceTypeBinding = (SourceTypeBinding)type;
                referenceContext = sourceTypeBinding.scope.referenceContext;
                javadoc = ((TypeDeclaration)referenceContext).javadoc;
                break;
            }
            case METHOD: 
            case CONSTRUCTOR: {
                ExecutableElementImpl executableElementImpl = (ExecutableElementImpl)e;
                MethodBinding methodBinding = (MethodBinding)executableElementImpl._binding;
                AbstractMethodDeclaration sourceMethod = methodBinding.sourceMethod();
                if (sourceMethod == null) break;
                javadoc = sourceMethod.javadoc;
                referenceContext = sourceMethod;
                break;
            }
            case ENUM_CONSTANT: 
            case FIELD: 
            case RECORD_COMPONENT: {
                VariableElementImpl variableElementImpl = (VariableElementImpl)e;
                FieldBinding fieldBinding = (FieldBinding)variableElementImpl._binding;
                FieldDeclaration sourceField = fieldBinding.sourceField();
                if (sourceField == null) break;
                javadoc = sourceField.javadoc;
                if (!(fieldBinding.declaringClass instanceof SourceTypeBinding)) break;
                SourceTypeBinding sourceTypeBinding = (SourceTypeBinding)fieldBinding.declaringClass;
                referenceContext = sourceTypeBinding.scope.referenceContext;
                break;
            }
            default: {
                return null;
            }
        }
        if (javadoc != null && referenceContext != null && (contents = referenceContext.compilationResult().getCompilationUnit().getContents()) != null) {
            return CharOperation.subarray(contents, javadoc.sourceStart, javadoc.sourceEnd - 1);
        }
        return null;
    }

    private static String formatJavadoc(char[] unparsed) {
        if (unparsed == null || unparsed.length < 5) {
            return null;
        }
        String[] lines = new String(unparsed).split("\n");
        Matcher delimiterMatcher = INITIAL_DELIMITER.matcher(lines[0]);
        if (!delimiterMatcher.find()) {
            return null;
        }
        int iOpener = delimiterMatcher.end();
        lines[0] = lines[0].substring(iOpener);
        if (lines.length == 1) {
            StringBuilder sb = new StringBuilder();
            char[] chars = lines[0].toCharArray();
            boolean startingWhitespaces = true;
            char[] cArray = chars;
            int n = chars.length;
            int n2 = 0;
            while (n2 < n) {
                char c = cArray[n2];
                if (Character.isWhitespace(c)) {
                    if (!startingWhitespaces) {
                        sb.append(c);
                    }
                } else {
                    startingWhitespaces = false;
                    sb.append(c);
                }
                ++n2;
            }
            return sb.toString();
        }
        int firstLine = lines[0].trim().length() > 0 ? 0 : 1;
        int lastLine = lines[lines.length - 1].trim().length() > 0 ? lines.length - 1 : lines.length - 2;
        StringBuilder sb = new StringBuilder();
        if (lines[0].length() != 0 && firstLine == 1) {
            sb.append('\n');
        }
        boolean preserveLineSeparator = lines[0].length() == 0;
        int line = firstLine;
        while (line <= lastLine) {
            char[] chars = lines[line].toCharArray();
            int starsIndex = ElementsImpl.getStars(chars);
            int leadingWhitespaces = 0;
            boolean recordLeadingWhitespaces = true;
            int i = 0;
            int max = chars.length;
            while (i < max) {
                char c = chars[i];
                switch (c) {
                    case ' ': {
                        if (starsIndex == -1) {
                            if (recordLeadingWhitespaces) {
                                ++leadingWhitespaces;
                                break;
                            }
                            sb.append(c);
                            break;
                        }
                        if (i < starsIndex) break;
                        sb.append(c);
                        break;
                    }
                    default: {
                        recordLeadingWhitespaces = false;
                        if (leadingWhitespaces != 0) {
                            int max2;
                            int j;
                            int numberOfTabs = leadingWhitespaces / 8;
                            if (numberOfTabs != 0) {
                                j = 0;
                                max2 = numberOfTabs;
                                while (j < max2) {
                                    sb.append("        ");
                                    ++j;
                                }
                                if (leadingWhitespaces % 8 >= 1) {
                                    sb.append(' ');
                                }
                            } else if (line != 0) {
                                j = 0;
                                max2 = leadingWhitespaces;
                                while (j < max2) {
                                    sb.append(' ');
                                    ++j;
                                }
                            }
                            leadingWhitespaces = 0;
                            sb.append(c);
                            break;
                        }
                        if (c == '\t') {
                            if (i < starsIndex) break;
                            sb.append(c);
                            break;
                        }
                        if (c == '*' && i <= starsIndex) break;
                        sb.append(c);
                    }
                }
                ++i;
            }
            int end = lines.length - 1;
            if (line < end) {
                sb.append('\n');
            } else if (preserveLineSeparator && line == end) {
                sb.append('\n');
            }
            ++line;
        }
        return sb.toString();
    }

    private static int getStars(char[] line) {
        int i = 0;
        int max = line.length;
        while (i < max) {
            char c = line[i];
            if (!Character.isWhitespace(c)) {
                if (c != '*') break;
                int j = i + 1;
                while (j < max) {
                    if (line[j] != '*') {
                        return j;
                    }
                    ++j;
                }
                return max - 1;
            }
            ++i;
        }
        return -1;
    }

    @Override
    public Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValuesWithDefaults(AnnotationMirror a) {
        return ((AnnotationMirrorImpl)a).getElementValuesWithDefaults();
    }

    @Override
    public Name getName(CharSequence cs) {
        return new NameImpl(cs);
    }

    @Override
    public PackageElement getPackageElement(CharSequence name) {
        LookupEnvironment le = this._env.getLookupEnvironment();
        if (name.length() == 0) {
            return (PackageElement)this._env.getFactory().newElement(le.defaultPackage);
        }
        char[] packageName = name.toString().toCharArray();
        PackageBinding packageBinding = le.createPackage(CharOperation.splitOn('.', packageName));
        if (packageBinding == null) {
            return null;
        }
        return (PackageElement)this._env.getFactory().newElement(packageBinding);
    }

    @Override
    public PackageElement getPackageOf(Element type) {
        switch (type.getKind()) {
            case ENUM: 
            case CLASS: 
            case ANNOTATION_TYPE: 
            case INTERFACE: 
            case RECORD: {
                TypeElementImpl typeElementImpl = (TypeElementImpl)type;
                ReferenceBinding referenceBinding = (ReferenceBinding)typeElementImpl._binding;
                return (PackageElement)this._env.getFactory().newElement(referenceBinding.fPackage);
            }
            case PACKAGE: {
                return (PackageElement)type;
            }
            case METHOD: 
            case CONSTRUCTOR: {
                ExecutableElementImpl executableElementImpl = (ExecutableElementImpl)type;
                MethodBinding methodBinding = (MethodBinding)executableElementImpl._binding;
                return (PackageElement)this._env.getFactory().newElement(methodBinding.declaringClass.fPackage);
            }
            case ENUM_CONSTANT: 
            case FIELD: 
            case RECORD_COMPONENT: {
                VariableElementImpl variableElementImpl = (VariableElementImpl)type;
                FieldBinding fieldBinding = (FieldBinding)variableElementImpl._binding;
                return (PackageElement)this._env.getFactory().newElement(fieldBinding.declaringClass.fPackage);
            }
            case PARAMETER: {
                VariableElementImpl variableElementImpl = (VariableElementImpl)type;
                LocalVariableBinding localVariableBinding = (LocalVariableBinding)variableElementImpl._binding;
                return (PackageElement)this._env.getFactory().newElement(localVariableBinding.declaringScope.classScope().referenceContext.binding.fPackage);
            }
            case LOCAL_VARIABLE: 
            case EXCEPTION_PARAMETER: 
            case STATIC_INIT: 
            case INSTANCE_INIT: 
            case TYPE_PARAMETER: 
            case OTHER: {
                return null;
            }
        }
        return null;
    }

    @Override
    public TypeElement getTypeElement(CharSequence name) {
        char[][] compoundName;
        LookupEnvironment le = this._env.getLookupEnvironment();
        ReferenceBinding binding = le.getType(compoundName = CharOperation.splitOn('.', name.toString().toCharArray()));
        if (binding == null) {
            ReferenceBinding topLevelBinding = null;
            int topLevelSegments = compoundName.length;
            while (--topLevelSegments > 0) {
                char[][] topLevelName = new char[topLevelSegments][];
                int i = 0;
                while (i < topLevelSegments) {
                    topLevelName[i] = compoundName[i];
                    ++i;
                }
                topLevelBinding = le.getType(topLevelName);
                if (topLevelBinding != null) break;
            }
            if (topLevelBinding == null) {
                return null;
            }
            binding = topLevelBinding;
            int i = topLevelSegments;
            while (binding != null && i < compoundName.length) {
                binding = binding.getMemberType(compoundName[i]);
                ++i;
            }
        }
        if (binding == null) {
            return null;
        }
        if ((binding.tagBits & 0x80L) != 0L) {
            return null;
        }
        return new TypeElementImpl(this._env, binding, null);
    }

    @Override
    public boolean hides(Element hider, Element hidden) {
        if (hidden == null) {
            throw new NullPointerException();
        }
        return ((ElementImpl)hider).hides(hidden);
    }

    @Override
    public boolean isDeprecated(Element e) {
        if (!(e instanceof ElementImpl)) {
            return false;
        }
        return (((ElementImpl)e)._binding.getAnnotationTagBits() & 0x400000000000L) != 0L;
    }

    @Override
    public boolean overrides(ExecutableElement overrider, ExecutableElement overridden, TypeElement type) {
        if (overridden == null || type == null) {
            throw new NullPointerException();
        }
        return ((ExecutableElementImpl)overrider).overrides(overridden, type);
    }

    @Override
    public void printElements(Writer w, Element ... elements) {
        String lineSeparator = System.getProperty("line.separator");
        Element[] elementArray = elements;
        int n = elements.length;
        int n2 = 0;
        while (n2 < n) {
            Element element = elementArray[n2];
            try {
                w.write(element.toString());
                w.write(lineSeparator);
            }
            catch (IOException iOException) {}
            ++n2;
        }
        try {
            w.flush();
        }
        catch (IOException iOException) {}
    }

    @Override
    public boolean isFunctionalInterface(TypeElement type) {
        ReferenceBinding binding;
        if (type != null && type.getKind() == ElementKind.INTERFACE && (binding = (ReferenceBinding)((TypeElementImpl)type)._binding) instanceof SourceTypeBinding) {
            return binding.isFunctionalInterface(((SourceTypeBinding)binding).scope);
        }
        return false;
    }
}

