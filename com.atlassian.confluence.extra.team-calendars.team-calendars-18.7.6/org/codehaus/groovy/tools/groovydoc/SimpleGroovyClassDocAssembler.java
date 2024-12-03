/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.groovydoc;

import groovyjarjarantlr.collections.AST;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.codehaus.groovy.antlr.GroovySourceAST;
import org.codehaus.groovy.antlr.LineColumn;
import org.codehaus.groovy.antlr.SourceBuffer;
import org.codehaus.groovy.antlr.parser.GroovyTokenTypes;
import org.codehaus.groovy.antlr.treewalker.VisitorAdapter;
import org.codehaus.groovy.control.ResolveVisitor;
import org.codehaus.groovy.groovydoc.GroovyAnnotationRef;
import org.codehaus.groovy.groovydoc.GroovyClassDoc;
import org.codehaus.groovy.groovydoc.GroovyConstructorDoc;
import org.codehaus.groovy.groovydoc.GroovyFieldDoc;
import org.codehaus.groovy.groovydoc.GroovyMethodDoc;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.tools.groovydoc.LinkArgument;
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyAbstractableElementDoc;
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyAnnotationRef;
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyClassDoc;
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyConstructorDoc;
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyExecutableMemberDoc;
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyFieldDoc;
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyMethodDoc;
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyParameter;
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyProgramElementDoc;
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyType;

public class SimpleGroovyClassDocAssembler
extends VisitorAdapter
implements GroovyTokenTypes {
    private static final String FS = "/";
    private static final Pattern PREV_JAVADOC_COMMENT_PATTERN = Pattern.compile("(?s)/\\*\\*(.*?)\\*/");
    private final Stack<GroovySourceAST> stack;
    private Map<String, GroovyClassDoc> classDocs;
    private List<String> importedClassesAndPackages;
    private Map<String, String> aliases;
    private List<LinkArgument> links;
    private Properties properties;
    private SimpleGroovyFieldDoc currentFieldDoc;
    private SourceBuffer sourceBuffer;
    private String packagePath;
    private LineColumn lastLineCol;
    private boolean insideEnum;
    private Map<String, SimpleGroovyClassDoc> foundClasses;
    private boolean isGroovy;
    private boolean deferSetup;
    private String className;

    public SimpleGroovyClassDocAssembler(String packagePath, String file, SourceBuffer sourceBuffer, List<LinkArgument> links, Properties properties, boolean isGroovy) {
        this.sourceBuffer = sourceBuffer;
        this.packagePath = packagePath;
        this.links = links;
        this.properties = properties;
        this.isGroovy = isGroovy;
        this.stack = new Stack();
        this.className = file;
        this.classDocs = new LinkedHashMap<String, GroovyClassDoc>();
        if (file != null) {
            int idx = file.lastIndexOf(".");
            this.className = file.substring(0, idx);
        }
        this.deferSetup = packagePath.equals("DefaultPackage");
        this.importedClassesAndPackages = new ArrayList<String>();
        this.aliases = new LinkedHashMap<String, String>();
        if (!this.deferSetup) {
            this.setUpImports(packagePath, links, isGroovy, this.className);
        }
        this.lastLineCol = new LineColumn(1, 1);
    }

    private void setUpImports(String packagePath, List<LinkArgument> links, boolean isGroovy, String className) {
        this.importedClassesAndPackages.add(packagePath + "/*");
        if (isGroovy) {
            for (String pkg : ResolveVisitor.DEFAULT_IMPORTS) {
                this.importedClassesAndPackages.add(pkg.replace('.', '/') + "*");
            }
        } else {
            this.importedClassesAndPackages.add("java/lang/*");
        }
        SimpleGroovyClassDoc currentClassDoc = new SimpleGroovyClassDoc(this.importedClassesAndPackages, this.aliases, className, links);
        currentClassDoc.setFullPathName(packagePath + FS + className);
        currentClassDoc.setGroovy(isGroovy);
        this.classDocs.put(currentClassDoc.getFullPathName(), currentClassDoc);
    }

    public Map<String, GroovyClassDoc> getGroovyClassDocs() {
        this.postProcessClassDocs();
        return this.classDocs;
    }

    @Override
    public void visitInterfaceDef(GroovySourceAST t, int visit) {
        this.visitClassDef(t, visit);
    }

    @Override
    public void visitTraitDef(GroovySourceAST t, int visit) {
        this.visitClassDef(t, visit);
    }

    @Override
    public void visitEnumDef(GroovySourceAST t, int visit) {
        this.visitClassDef(t, visit);
        SimpleGroovyClassDoc currentClassDoc = this.getCurrentOrTopLevelClassDoc(t);
        if (visit == 4 && currentClassDoc != null) {
            SimpleGroovyClassDocAssembler.adjustForAutomaticEnumMethods(currentClassDoc);
        }
    }

    @Override
    public void visitAnnotationDef(GroovySourceAST t, int visit) {
        this.visitClassDef(t, visit);
    }

    @Override
    public void visitClassDef(GroovySourceAST t, int visit) {
        if (visit == 1) {
            String shortName;
            SimpleGroovyClassDoc parent = this.getCurrentClassDoc();
            String className = shortName = SimpleGroovyClassDocAssembler.getIdentFor(t);
            if (parent != null && this.isNested() && !this.insideAnonymousInnerClass()) {
                className = parent.name() + "." + className;
            } else {
                this.foundClasses = new LinkedHashMap<String, SimpleGroovyClassDoc>();
            }
            SimpleGroovyClassDoc current = (SimpleGroovyClassDoc)this.classDocs.get(this.packagePath + FS + className);
            if (current == null) {
                current = new SimpleGroovyClassDoc(this.importedClassesAndPackages, this.aliases, className, this.links);
                current.setGroovy(this.isGroovy);
            }
            current.setRawCommentText(this.getJavaDocCommentsBeforeNode(t));
            current.setFullPathName(this.packagePath + FS + current.name());
            current.setTokenType(t.getType());
            current.setNameWithTypeArgs(this.getIdentPlusTypeArgsFor(t));
            this.processAnnotations(t, current);
            this.processModifiers(t, current);
            this.classDocs.put(current.getFullPathName(), current);
            this.foundClasses.put(shortName, current);
            if (parent != null) {
                parent.addNested(current);
                current.setOuter(parent);
            }
        }
    }

    @Override
    public void visitPackageDef(GroovySourceAST t, int visit) {
        if (visit == 1 && this.deferSetup) {
            String packageWithSlashes = this.extractImportPath(t);
            this.setUpImports(packageWithSlashes, this.links, this.isGroovy, this.className);
        }
    }

    @Override
    public void visitImport(GroovySourceAST t, int visit) {
        if (visit == 1) {
            String importTextWithSlashesInsteadOfDots = this.extractImportPath(t);
            GroovySourceAST child = t.childOfType(114);
            if (child != null) {
                String alias = child.childOfType(90).getNextSibling().getText();
                child = child.childOfType(90);
                importTextWithSlashesInsteadOfDots = this.recurseDownImportBranch(child);
                this.aliases.put(alias, importTextWithSlashesInsteadOfDots);
            }
            this.importedClassesAndPackages.add(importTextWithSlashesInsteadOfDots);
        }
    }

    @Override
    public void visitExtendsClause(GroovySourceAST t, int visit) {
        SimpleGroovyClassDoc currentClassDoc = this.getCurrentClassDoc();
        if (visit == 1) {
            for (GroovySourceAST superClassNode : SimpleGroovyClassDocAssembler.findTypeNames(t)) {
                String superClassName = this.extractName(superClassNode);
                if (currentClassDoc.isInterface()) {
                    currentClassDoc.addInterfaceName(superClassName);
                    continue;
                }
                currentClassDoc.setSuperClassName(superClassName);
            }
        }
    }

    @Override
    public void visitImplementsClause(GroovySourceAST t, int visit) {
        if (visit == 1) {
            for (GroovySourceAST classNode : SimpleGroovyClassDocAssembler.findTypeNames(t)) {
                this.getCurrentClassDoc().addInterfaceName(this.extractName(classNode));
            }
        }
    }

    private static List<GroovySourceAST> findTypeNames(GroovySourceAST t) {
        ArrayList<GroovySourceAST> types = new ArrayList<GroovySourceAST>();
        for (AST child = t.getFirstChild(); child != null; child = child.getNextSibling()) {
            GroovySourceAST groovySourceAST = (GroovySourceAST)child;
            if (groovySourceAST.getType() == 12) {
                types.add((GroovySourceAST)groovySourceAST.getFirstChild());
                continue;
            }
            types.add(groovySourceAST);
        }
        return types;
    }

    @Override
    public void visitCtorIdent(GroovySourceAST t, int visit) {
        if (visit == 1 && !this.insideEnum && !this.insideAnonymousInnerClass()) {
            SimpleGroovyClassDoc currentClassDoc = this.getCurrentClassDoc();
            SimpleGroovyConstructorDoc currentConstructorDoc = new SimpleGroovyConstructorDoc(currentClassDoc.name(), currentClassDoc);
            currentConstructorDoc.setRawCommentText(this.getJavaDocCommentsBeforeNode(t));
            this.processModifiers(t, currentConstructorDoc);
            this.addParametersTo(t, currentConstructorDoc);
            this.processAnnotations(t, currentConstructorDoc);
            currentClassDoc.add(currentConstructorDoc);
        }
    }

    @Override
    public void visitMethodDef(GroovySourceAST t, int visit) {
        if (visit == 1 && !this.insideEnum && !this.insideAnonymousInnerClass()) {
            SimpleGroovyClassDoc currentClassDoc = this.getCurrentClassDoc();
            if (currentClassDoc == null) {
                if ("true".equals(this.properties.getProperty("processScripts", "true"))) {
                    currentClassDoc = this.getOrMakeScriptClassDoc();
                } else {
                    return;
                }
            }
            SimpleGroovyMethodDoc currentMethodDoc = this.createMethod(t, currentClassDoc);
            StringBuilder params = new StringBuilder();
            this.getTypeParameters(t.childOfType(72), params, "def");
            currentMethodDoc.setTypeParameters(params.toString());
            currentClassDoc.add(currentMethodDoc);
        }
    }

    private SimpleGroovyClassDoc getOrMakeScriptClassDoc() {
        SimpleGroovyClassDoc currentClassDoc;
        if (this.foundClasses != null && this.foundClasses.containsKey(this.className)) {
            currentClassDoc = this.foundClasses.get(this.className);
        } else {
            currentClassDoc = new SimpleGroovyClassDoc(this.importedClassesAndPackages, this.aliases, this.className, this.links);
            currentClassDoc.setFullPathName(this.packagePath + FS + this.className);
            currentClassDoc.setPublic(true);
            currentClassDoc.setScript(true);
            currentClassDoc.setGroovy(this.isGroovy);
            currentClassDoc.setSuperClassName("groovy/lang/Script");
            if ("true".equals(this.properties.getProperty("includeMainForScripts", "true"))) {
                currentClassDoc.add(SimpleGroovyClassDocAssembler.createMainMethod(currentClassDoc));
            }
            this.classDocs.put(currentClassDoc.getFullPathName(), currentClassDoc);
            if (this.foundClasses == null) {
                this.foundClasses = new LinkedHashMap<String, SimpleGroovyClassDoc>();
            }
            this.foundClasses.put(this.className, currentClassDoc);
        }
        return currentClassDoc;
    }

    private SimpleGroovyMethodDoc createMethod(GroovySourceAST t, SimpleGroovyClassDoc currentClassDoc) {
        String methodName = SimpleGroovyClassDocAssembler.getIdentFor(t);
        SimpleGroovyMethodDoc currentMethodDoc = new SimpleGroovyMethodDoc(methodName, currentClassDoc);
        currentMethodDoc.setRawCommentText(this.getJavaDocCommentsBeforeNode(t));
        this.processModifiers(t, currentMethodDoc);
        currentMethodDoc.setReturnType(new SimpleGroovyType(this.getTypeOrDefault(t)));
        this.addParametersTo(t, currentMethodDoc);
        this.processAnnotations(t, currentMethodDoc);
        return currentMethodDoc;
    }

    private static GroovyMethodDoc createMainMethod(SimpleGroovyClassDoc currentClassDoc) {
        SimpleGroovyMethodDoc mainMethod = new SimpleGroovyMethodDoc("main", currentClassDoc);
        mainMethod.setPublic(true);
        mainMethod.setStatic(true);
        mainMethod.setCommentText("Implicit main method for Groovy Scripts");
        mainMethod.setFirstSentenceCommentText(mainMethod.commentText());
        SimpleGroovyParameter args = new SimpleGroovyParameter("args");
        SimpleGroovyType argsType = new SimpleGroovyType("java.lang.String[]");
        args.setType(argsType);
        mainMethod.add(args);
        SimpleGroovyType returnType = new SimpleGroovyType("void");
        mainMethod.setReturnType(returnType);
        return mainMethod;
    }

    @Override
    public void visitAnnotationFieldDef(GroovySourceAST t, int visit) {
        if (this.isGroovy && visit == 1) {
            SimpleGroovyClassDoc currentClassDoc = this.getCurrentClassDoc();
            SimpleGroovyMethodDoc currentMethodDoc = this.createMethod(t, currentClassDoc);
            String defaultText = this.getDefaultValue(t);
            if (defaultText != null) {
                String orig = currentMethodDoc.getRawCommentText();
                currentMethodDoc.setRawCommentText(orig + "\n* @default " + defaultText);
            }
            currentClassDoc.add(currentMethodDoc);
        } else if (visit == 1) {
            this.visitVariableDef(t, visit);
            String defaultText = this.getDefaultValue(t);
            if (this.isGroovy) {
                this.currentFieldDoc.setPublic(true);
            }
            if (defaultText != null) {
                this.currentFieldDoc.setConstantValueExpression(defaultText);
                String orig = this.currentFieldDoc.getRawCommentText();
                this.currentFieldDoc.setRawCommentText(orig + "\n* @default " + defaultText);
            }
        }
    }

    @Override
    public void visitEnumConstantDef(GroovySourceAST t, int visit) {
        if (visit == 1) {
            SimpleGroovyClassDoc currentClassDoc = this.getCurrentClassDoc();
            this.insideEnum = true;
            String enumConstantName = SimpleGroovyClassDocAssembler.getIdentFor(t);
            SimpleGroovyFieldDoc currentEnumConstantDoc = new SimpleGroovyFieldDoc(enumConstantName, currentClassDoc);
            currentEnumConstantDoc.setRawCommentText(this.getJavaDocCommentsBeforeNode(t));
            this.processModifiers(t, currentEnumConstantDoc);
            String typeName = this.getTypeNodeAsText(t.childOfType(12), currentClassDoc.getTypeDescription());
            currentEnumConstantDoc.setType(new SimpleGroovyType(typeName));
            currentClassDoc.addEnumConstant(currentEnumConstantDoc);
        } else if (visit == 4) {
            this.insideEnum = false;
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void visitVariableDef(GroovySourceAST t, int visit) {
        if (visit != 1 || this.insideAnonymousInnerClass()) return;
        boolean validField = true;
        SimpleGroovyClassDoc currentClassDoc = this.getCurrentClassDoc();
        if (currentClassDoc == null) {
            if (!"true".equals(this.properties.getProperty("processScripts", "true"))) return;
            currentClassDoc = this.getOrMakeScriptClassDoc();
            validField = false;
        } else if (!this.isFieldDefinition()) {
            return;
        }
        String fieldName = SimpleGroovyClassDocAssembler.getIdentFor(t);
        if (fieldName.isEmpty()) {
            return;
        }
        this.currentFieldDoc = new SimpleGroovyFieldDoc(fieldName, currentClassDoc);
        this.currentFieldDoc.setRawCommentText(this.getJavaDocCommentsBeforeNode(t));
        boolean isProp = this.processModifiers(t, this.currentFieldDoc);
        this.currentFieldDoc.setType(new SimpleGroovyType(this.getTypeOrDefault(t)));
        this.processAnnotations(t, this.currentFieldDoc);
        if (!validField) {
            for (GroovyAnnotationRef ref : this.currentFieldDoc.annotations()) {
                if (!"Field".equals(ref.name()) && !"groovy/transform/Field".equals(ref.name())) continue;
                validField = true;
                break;
            }
        }
        if (!validField) {
            return;
        }
        if (isProp) {
            currentClassDoc.addProperty(this.currentFieldDoc);
            return;
        } else {
            currentClassDoc.add(this.currentFieldDoc);
        }
    }

    @Override
    public void visitAssign(GroovySourceAST t, int visit) {
        this.gobbleComments(t, visit);
    }

    @Override
    public void visitMethodCall(GroovySourceAST t, int visit) {
        this.gobbleComments(t, visit);
    }

    private void gobbleComments(GroovySourceAST t, int visit) {
        SimpleGroovyClassDoc currentClassDoc;
        if (visit == 1 && ((currentClassDoc = this.getCurrentClassDoc()) == null || currentClassDoc.isScript()) && (t.getLine() > this.lastLineCol.getLine() || t.getLine() == this.lastLineCol.getLine() && t.getColumn() > this.lastLineCol.getColumn())) {
            this.getJavaDocCommentsBeforeNode(t);
            this.lastLineCol = new LineColumn(t.getLine(), t.getColumn());
        }
    }

    private void postProcessClassDocs() {
        for (GroovyClassDoc groovyClassDoc : this.classDocs.values()) {
            GroovyConstructorDoc[] constructors;
            SimpleGroovyClassDoc classDoc = (SimpleGroovyClassDoc)groovyClassDoc;
            if (!classDoc.isClass() || (constructors = classDoc.constructors()) == null || constructors.length != 0) continue;
            SimpleGroovyConstructorDoc constructorDoc = new SimpleGroovyConstructorDoc(classDoc.name(), classDoc);
            classDoc.add(constructorDoc);
        }
    }

    private boolean isNested() {
        return this.getCurrentClassDoc() != null;
    }

    private static boolean isTopLevelConstruct(GroovySourceAST node) {
        if (node == null) {
            return false;
        }
        int type = node.getType();
        return type == 13 || type == 14 || type == 15 || type == 64 || type == 61;
    }

    private static void adjustForAutomaticEnumMethods(SimpleGroovyClassDoc currentClassDoc) {
        SimpleGroovyMethodDoc valueOf = new SimpleGroovyMethodDoc("valueOf", currentClassDoc);
        valueOf.setRawCommentText("Returns the enum constant of this type with the specified name.");
        SimpleGroovyParameter parameter = new SimpleGroovyParameter("name");
        parameter.setTypeName("String");
        valueOf.add(parameter);
        valueOf.setReturnType(new SimpleGroovyType(currentClassDoc.name()));
        currentClassDoc.add(valueOf);
        SimpleGroovyMethodDoc values = new SimpleGroovyMethodDoc("values", currentClassDoc);
        values.setRawCommentText("Returns an array containing the constants of this enum type, in the order they are declared.");
        values.setReturnType(new SimpleGroovyType(currentClassDoc.name() + "[]"));
        currentClassDoc.add(values);
    }

    private String extractImportPath(GroovySourceAST t) {
        return this.recurseDownImportBranch(SimpleGroovyClassDocAssembler.getPackageDotType(t));
    }

    private static GroovySourceAST getPackageDotType(GroovySourceAST t) {
        GroovySourceAST child = t.childOfType(90);
        if (child == null) {
            child = t.childOfType(87);
        }
        return child;
    }

    private String recurseDownImportBranch(GroovySourceAST t) {
        if (t != null) {
            if (t.getType() == 90) {
                GroovySourceAST firstChild = (GroovySourceAST)t.getFirstChild();
                GroovySourceAST secondChild = (GroovySourceAST)firstChild.getNextSibling();
                return this.recurseDownImportBranch(firstChild) + FS + this.recurseDownImportBranch(secondChild);
            }
            if (t.getType() == 87) {
                return t.getText();
            }
            if (t.getType() == 113) {
                return t.getText();
            }
        }
        return "";
    }

    private void addAnnotationRef(SimpleGroovyProgramElementDoc node, GroovySourceAST t) {
        GroovySourceAST classNode = SimpleGroovyClassDocAssembler.getPackageDotType(t);
        if (classNode != null) {
            node.addAnnotationRef(new SimpleGroovyAnnotationRef(this.extractName(classNode), this.getChildTextFromSource(t).trim()));
        }
    }

    private void addAnnotationRef(SimpleGroovyParameter node, GroovySourceAST t) {
        GroovySourceAST classNode = SimpleGroovyClassDocAssembler.getPackageDotType(t);
        if (classNode != null) {
            node.addAnnotationRef(new SimpleGroovyAnnotationRef(this.extractName(classNode), this.getChildTextFromSource(t).trim()));
        }
    }

    private void addAnnotationRefs(SimpleGroovyProgramElementDoc node, List<GroovySourceAST> nodes) {
        for (GroovySourceAST t : nodes) {
            this.addAnnotationRef(node, t);
        }
    }

    private void processAnnotations(GroovySourceAST t, SimpleGroovyProgramElementDoc node) {
        GroovySourceAST modifiers = t.childOfType(5);
        if (modifiers != null) {
            this.addAnnotationRefs(node, modifiers.childrenOfType(66));
        }
    }

    private String getDefaultValue(GroovySourceAST t) {
        GroovySourceAST child = (GroovySourceAST)t.getFirstChild();
        if (t.getNumberOfChildren() != 4) {
            return null;
        }
        for (int i = 1; i < t.getNumberOfChildren(); ++i) {
            child = (GroovySourceAST)child.getNextSibling();
        }
        GroovySourceAST nodeToProcess = child;
        if (child.getType() != 69 && child.getNumberOfChildren() > 0) {
            nodeToProcess = (GroovySourceAST)child.getFirstChild();
        }
        return this.getChildTextFromSource(nodeToProcess, ";");
    }

    private String getChildTextFromSource(GroovySourceAST child) {
        return this.sourceBuffer.getSnippet(new LineColumn(child.getLine(), child.getColumn()), new LineColumn(child.getLineLast(), child.getColumnLast()));
    }

    private String getChildTextFromSource(GroovySourceAST child, String tokens) {
        String text = this.sourceBuffer.getSnippet(new LineColumn(child.getLine(), child.getColumn()), new LineColumn(child.getLine() + 1, 0));
        StringTokenizer st = new StringTokenizer(text, tokens);
        return st.nextToken();
    }

    private boolean isFieldDefinition() {
        GroovySourceAST parentNode = this.getParentNode();
        return parentNode != null && parentNode.getType() == 6;
    }

    private boolean insideAnonymousInnerClass() {
        GroovySourceAST grandParentNode = this.getGrandParentNode();
        return grandParentNode != null && grandParentNode.getType() == 159;
    }

    private boolean processModifiers(GroovySourceAST t, SimpleGroovyAbstractableElementDoc memberOrClass) {
        GroovySourceAST modifiers = t.childOfType(5);
        boolean hasNonPublicVisibility = false;
        boolean hasPublicVisibility = false;
        if (modifiers != null) {
            block8: for (AST currentModifier = modifiers.getFirstChild(); currentModifier != null; currentModifier = currentModifier.getNextSibling()) {
                int type = currentModifier.getType();
                switch (type) {
                    case 116: {
                        memberOrClass.setPublic(true);
                        hasPublicVisibility = true;
                        continue block8;
                    }
                    case 117: {
                        memberOrClass.setProtected(true);
                        hasNonPublicVisibility = true;
                        continue block8;
                    }
                    case 115: {
                        memberOrClass.setPrivate(true);
                        hasNonPublicVisibility = true;
                        continue block8;
                    }
                    case 83: {
                        memberOrClass.setStatic(true);
                        continue block8;
                    }
                    case 38: {
                        memberOrClass.setFinal(true);
                        continue block8;
                    }
                    case 39: {
                        memberOrClass.setAbstract(true);
                    }
                }
            }
            if (!hasNonPublicVisibility && this.isGroovy && !(memberOrClass instanceof GroovyFieldDoc)) {
                if (this.isPackageScope(modifiers)) {
                    memberOrClass.setPackagePrivate(true);
                    hasNonPublicVisibility = true;
                } else {
                    memberOrClass.setPublic(true);
                }
            } else if (!(hasNonPublicVisibility || hasPublicVisibility || this.isGroovy)) {
                if (this.insideInterface(memberOrClass) || this.insideAnnotationDef(memberOrClass)) {
                    memberOrClass.setPublic(true);
                } else {
                    memberOrClass.setPackagePrivate(true);
                }
            }
            if (memberOrClass instanceof GroovyFieldDoc && this.isGroovy && !hasNonPublicVisibility & !hasPublicVisibility && this.isPackageScope(modifiers)) {
                memberOrClass.setPackagePrivate(true);
                hasNonPublicVisibility = true;
            }
            if (memberOrClass instanceof GroovyFieldDoc && !hasNonPublicVisibility && !hasPublicVisibility && this.isGroovy) {
                return true;
            }
        } else if (this.isGroovy && !(memberOrClass instanceof GroovyFieldDoc)) {
            memberOrClass.setPublic(true);
        } else if (!this.isGroovy) {
            if (this.insideInterface(memberOrClass) || this.insideAnnotationDef(memberOrClass)) {
                memberOrClass.setPublic(true);
            } else {
                memberOrClass.setPackagePrivate(true);
            }
        }
        return memberOrClass instanceof GroovyFieldDoc && this.isGroovy && !hasNonPublicVisibility & !hasPublicVisibility;
    }

    private boolean isPackageScope(GroovySourceAST modifiers) {
        List<String> names = this.getAnnotationNames(modifiers);
        return names.contains("groovy/transform/PackageScope") || names.contains("PackageScope");
    }

    private List<String> getAnnotationNames(GroovySourceAST modifiers) {
        ArrayList<String> annotationNames = new ArrayList<String>();
        List<GroovySourceAST> annotations = modifiers.childrenOfType(66);
        for (GroovySourceAST annotation : annotations) {
            annotationNames.add(this.buildName((GroovySourceAST)annotation.getFirstChild()));
        }
        return annotationNames;
    }

    private boolean insideInterface(SimpleGroovyAbstractableElementDoc memberOrClass) {
        SimpleGroovyClassDoc current = this.getCurrentClassDoc();
        if (current == null || current == memberOrClass) {
            return false;
        }
        return current.isInterface();
    }

    private boolean insideAnnotationDef(SimpleGroovyAbstractableElementDoc memberOrClass) {
        SimpleGroovyClassDoc current = this.getCurrentClassDoc();
        if (current == null || current == memberOrClass) {
            return false;
        }
        return current.isAnnotationType();
    }

    private String getJavaDocCommentsBeforeNode(GroovySourceAST t) {
        Matcher m;
        String result = "";
        LineColumn thisLineCol = new LineColumn(t.getLine(), t.getColumn());
        String text = this.sourceBuffer.getSnippet(this.lastLineCol, thisLineCol);
        if (text != null && (m = PREV_JAVADOC_COMMENT_PATTERN.matcher(text)).find()) {
            result = m.group(1);
        }
        if (SimpleGroovyClassDocAssembler.isMajorType(t)) {
            this.lastLineCol = thisLineCol;
        }
        return result;
    }

    private static boolean isMajorType(GroovySourceAST t) {
        if (t == null) {
            return false;
        }
        int tt = t.getType();
        return tt == 13 || tt == 15 || tt == 14 || tt == 8 || tt == 64 || tt == 61 || tt == 9 || tt == 68 || tt == 62 || tt == 46;
    }

    private static String getText(GroovySourceAST node) {
        String returnValue = null;
        if (node != null) {
            returnValue = node.getText();
        }
        return returnValue;
    }

    private String extractName(GroovySourceAST typeNode) {
        String typeName = this.buildName(typeNode);
        if (!typeName.contains(FS)) {
            String slashName = FS + typeName;
            for (int i = this.importedClassesAndPackages.size() - 1; i >= 0; --i) {
                String name = this.importedClassesAndPackages.get(i);
                if (this.aliases.containsValue(name) || !name.endsWith(slashName)) continue;
                typeName = name;
                break;
            }
        }
        return typeName;
    }

    private String buildName(GroovySourceAST t) {
        if (t != null) {
            if (t.getType() == 90) {
                GroovySourceAST firstChild = (GroovySourceAST)t.getFirstChild();
                GroovySourceAST secondChild = (GroovySourceAST)firstChild.getNextSibling();
                return this.buildName(firstChild) + FS + this.buildName(secondChild);
            }
            if (t.getType() == 87) {
                return t.getText();
            }
        }
        return "";
    }

    private String getTypeOrDefault(GroovySourceAST t) {
        GroovySourceAST typeNode = t.childOfType(12);
        return this.getTypeNodeAsText(typeNode, "def");
    }

    private String getTypeNodeAsText(GroovySourceAST typeNode, String defaultText) {
        if (typeNode == null) {
            return defaultText;
        }
        if (typeNode.getType() == 12) {
            return this.getAsText(typeNode, defaultText);
        }
        if (typeNode.getType() == 71) {
            return this.getTypeNodeAsText((GroovySourceAST)typeNode.getFirstChild(), defaultText);
        }
        if (typeNode.getType() == 74) {
            AST next = typeNode.getNextSibling();
            if (next == null && typeNode.getFirstChild() != null) {
                next = typeNode.getFirstChild();
            }
            if (next == null) {
                return "?";
            }
            String boundType = this.getTypeNodeAsText((GroovySourceAST)next.getFirstChild(), defaultText);
            if (next.getType() == 75) {
                return "? extends " + boundType;
            }
            if (next.getType() == 76) {
                return "? super " + boundType;
            }
        } else if (typeNode.getType() == 87) {
            String ident = this.getAsTextCurrent(typeNode, defaultText);
            AST next = typeNode.getNextSibling();
            if (next == null && typeNode.getFirstChild() != null) {
                next = typeNode.getFirstChild();
            }
            if (next == null) {
                return ident;
            }
            String boundType = this.getTypeNodeAsText((GroovySourceAST)next.getFirstChild(), defaultText);
            if (next.getType() == 75) {
                return ident + " extends " + boundType;
            }
            if (next.getType() == 76) {
                return ident + " super " + boundType;
            }
        }
        return defaultText;
    }

    private String getAsText(GroovySourceAST typeNode, String defaultText) {
        GroovySourceAST child = (GroovySourceAST)typeNode.getFirstChild();
        return this.getAsTextCurrent(child, defaultText);
    }

    private String getAsTextCurrent(GroovySourceAST node, String defaultText) {
        if (node == null) {
            return defaultText;
        }
        switch (node.getType()) {
            case 105: {
                return "boolean";
            }
            case 106: {
                return "byte";
            }
            case 107: {
                return "char";
            }
            case 112: {
                return "double";
            }
            case 110: {
                return "float";
            }
            case 109: {
                return "int";
            }
            case 111: {
                return "long";
            }
            case 108: {
                return "short";
            }
            case 104: {
                return "void";
            }
            case 17: {
                String componentType = this.getAsText(node, defaultText);
                if (!componentType.equals("def")) {
                    return componentType + "[]";
                }
                return "java/lang/Object[]";
            }
            case 87: {
                StringBuilder ident = new StringBuilder();
                ident.append(node.getText());
                GroovySourceAST identChild = (GroovySourceAST)node.getFirstChild();
                this.getTypeArguments(identChild, ident, defaultText);
                return ident.toString();
            }
            case 90: {
                StringBuilder dot = new StringBuilder();
                for (GroovySourceAST dotChild = (GroovySourceAST)node.getFirstChild(); dotChild != null; dotChild = (GroovySourceAST)dotChild.getNextSibling()) {
                    if (dotChild.getType() == 87 || dotChild.getType() == 90) {
                        if (dot.length() > 0) {
                            dot.append(FS);
                        }
                        dot.append(this.getAsTextCurrent(dotChild, defaultText));
                        continue;
                    }
                    if (dotChild.getType() != 70) continue;
                    this.getTypeArguments(dotChild, dot, defaultText);
                }
                return dot.toString();
            }
        }
        return defaultText;
    }

    private void getTypeArguments(GroovySourceAST child, StringBuilder result, String defaultText) {
        if (child != null && child.getType() == 70 && child.getNumberOfChildren() > 0) {
            result.append("<");
            ArrayList<String> typeArgumentParts = new ArrayList<String>();
            for (GroovySourceAST typeArgumentsNext = (GroovySourceAST)child.getFirstChild(); typeArgumentsNext != null; typeArgumentsNext = (GroovySourceAST)typeArgumentsNext.getNextSibling()) {
                if (typeArgumentsNext.getType() != 71 || typeArgumentsNext.getNumberOfChildren() <= 0) continue;
                typeArgumentParts.add(this.getTypeNodeAsText((GroovySourceAST)typeArgumentsNext.getFirstChild(), defaultText));
            }
            result.append(DefaultGroovyMethods.join(typeArgumentParts, ", "));
            result.append(">");
        }
    }

    private void getTypeParameters(GroovySourceAST child, StringBuilder result, String defaultText) {
        if (child != null && child.getType() == 72 && child.getNumberOfChildren() > 0) {
            result.append("<");
            ArrayList<String> typeParameterParts = new ArrayList<String>();
            for (GroovySourceAST typeParametersNext = (GroovySourceAST)child.getFirstChild(); typeParametersNext != null; typeParametersNext = (GroovySourceAST)typeParametersNext.getNextSibling()) {
                if (typeParametersNext.getType() != 73 || typeParametersNext.getNumberOfChildren() <= 0) continue;
                typeParameterParts.add(this.getTypeNodeAsText((GroovySourceAST)typeParametersNext.getFirstChild(), defaultText));
            }
            result.append(DefaultGroovyMethods.join(typeParameterParts, ", "));
            result.append(">");
        }
    }

    private void addParametersTo(GroovySourceAST t, SimpleGroovyExecutableMemberDoc executableMemberDoc) {
        GroovySourceAST parametersNode = t.childOfType(20);
        if (parametersNode != null && parametersNode.getNumberOfChildren() > 0) {
            for (GroovySourceAST currentNode = (GroovySourceAST)parametersNode.getFirstChild(); currentNode != null; currentNode = (GroovySourceAST)currentNode.getNextSibling()) {
                String parameterTypeName = this.getTypeOrDefault(currentNode);
                String parameterName = SimpleGroovyClassDocAssembler.getText(currentNode.childOfType(87));
                SimpleGroovyParameter parameter = new SimpleGroovyParameter(parameterName);
                parameter.setVararg(currentNode.getType() == 47);
                parameter.setTypeName(parameterTypeName);
                GroovySourceAST modifiers = currentNode.childOfType(5);
                if (modifiers != null) {
                    List<GroovySourceAST> annotations = modifiers.childrenOfType(66);
                    for (GroovySourceAST a : annotations) {
                        this.addAnnotationRef(parameter, a);
                    }
                }
                executableMemberDoc.add(parameter);
                if (currentNode.getNumberOfChildren() != 4) continue;
                this.handleDefaultValue(currentNode, parameter);
            }
        }
    }

    private void handleDefaultValue(GroovySourceAST currentNode, SimpleGroovyParameter parameter) {
        GroovySourceAST paramPart = (GroovySourceAST)currentNode.getFirstChild();
        for (int i = 1; i < currentNode.getNumberOfChildren(); ++i) {
            paramPart = (GroovySourceAST)paramPart.getNextSibling();
        }
        GroovySourceAST nodeToProcess = paramPart;
        if (paramPart.getNumberOfChildren() > 0) {
            nodeToProcess = (GroovySourceAST)paramPart.getFirstChild();
        }
        parameter.setDefaultValue(this.getChildTextFromSource(nodeToProcess, ",)"));
    }

    @Override
    public void push(GroovySourceAST t) {
        this.stack.push(t);
    }

    @Override
    public GroovySourceAST pop() {
        if (!this.stack.empty()) {
            return this.stack.pop();
        }
        return null;
    }

    private GroovySourceAST getParentNode() {
        GroovySourceAST parentNode = null;
        GroovySourceAST currentNode = this.stack.pop();
        if (!this.stack.empty()) {
            parentNode = this.stack.peek();
        }
        this.stack.push(currentNode);
        return parentNode;
    }

    private GroovySourceAST getGrandParentNode() {
        GroovySourceAST grandParentNode = null;
        GroovySourceAST currentNode = this.stack.pop();
        if (!this.stack.empty()) {
            GroovySourceAST parentNode = this.stack.pop();
            if (!this.stack.empty()) {
                grandParentNode = this.stack.peek();
            }
            this.stack.push(parentNode);
        }
        this.stack.push(currentNode);
        return grandParentNode;
    }

    private SimpleGroovyClassDoc getCurrentOrTopLevelClassDoc(GroovySourceAST node) {
        SimpleGroovyClassDoc current = this.getCurrentClassDoc();
        if (current != null) {
            return current;
        }
        return this.foundClasses.get(SimpleGroovyClassDocAssembler.getIdentFor(node));
    }

    private SimpleGroovyClassDoc getCurrentClassDoc() {
        if (this.stack.isEmpty()) {
            return null;
        }
        GroovySourceAST node = this.getParentNode();
        if (SimpleGroovyClassDocAssembler.isTopLevelConstruct(node) && this.foundClasses != null) {
            return this.foundClasses.get(SimpleGroovyClassDocAssembler.getIdentFor(node));
        }
        GroovySourceAST saved = this.stack.pop();
        SimpleGroovyClassDoc result = this.getCurrentClassDoc();
        this.stack.push(saved);
        return result;
    }

    private static String getIdentFor(GroovySourceAST gpn) {
        GroovySourceAST ident = gpn.childOfType(87);
        return ident == null ? "" : ident.getText();
    }

    private String getIdentPlusTypeArgsFor(GroovySourceAST gpn) {
        GroovySourceAST groovySourceAST = gpn.childOfType(87);
        StringBuilder ident = new StringBuilder();
        ident.append(groovySourceAST.getText());
        GroovySourceAST typeParams = (GroovySourceAST)groovySourceAST.getNextSibling();
        this.getTypeParameters(typeParams, ident, "def");
        return ident.toString();
    }
}

