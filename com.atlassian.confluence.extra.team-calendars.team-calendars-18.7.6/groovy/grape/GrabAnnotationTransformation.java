/*
 * Decompiled with CFR 0.152.
 */
package groovy.grape;

import groovy.grape.Grape;
import groovy.lang.Grab;
import groovy.lang.GrabConfig;
import groovy.lang.GrabExclude;
import groovy.lang.GrabResolver;
import groovy.lang.Grapes;
import groovy.transform.CompilationUnitAware;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ImportNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.io.StringReaderSource;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.tools.GrapeUtil;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.ASTTransformationVisitor;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase=CompilePhase.CONVERSION)
public class GrabAnnotationTransformation
extends ClassCodeVisitorSupport
implements ASTTransformation,
CompilationUnitAware {
    private static final String GRAB_CLASS_NAME = Grab.class.getName();
    private static final String GRAB_DOT_NAME = GRAB_CLASS_NAME.substring(GRAB_CLASS_NAME.lastIndexOf("."));
    private static final String GRAB_SHORT_NAME = GRAB_DOT_NAME.substring(1);
    private static final String GRABEXCLUDE_CLASS_NAME = GrabExclude.class.getName();
    private static final String GRABEXCLUDE_DOT_NAME = GrabAnnotationTransformation.dotName(GRABEXCLUDE_CLASS_NAME);
    private static final String GRABEXCLUDE_SHORT_NAME = GrabAnnotationTransformation.shortName(GRABEXCLUDE_DOT_NAME);
    private static final String GRABCONFIG_CLASS_NAME = GrabConfig.class.getName();
    private static final String GRABCONFIG_DOT_NAME = GrabAnnotationTransformation.dotName(GRABCONFIG_CLASS_NAME);
    private static final String GRABCONFIG_SHORT_NAME = GrabAnnotationTransformation.shortName(GRABCONFIG_DOT_NAME);
    private static final String GRAPES_CLASS_NAME = Grapes.class.getName();
    private static final String GRAPES_DOT_NAME = GrabAnnotationTransformation.dotName(GRAPES_CLASS_NAME);
    private static final String GRAPES_SHORT_NAME = GrabAnnotationTransformation.shortName(GRAPES_DOT_NAME);
    private static final String GRABRESOLVER_CLASS_NAME = GrabResolver.class.getName();
    private static final String GRABRESOLVER_DOT_NAME = GrabAnnotationTransformation.dotName(GRABRESOLVER_CLASS_NAME);
    private static final String GRABRESOLVER_SHORT_NAME = GrabAnnotationTransformation.shortName(GRABRESOLVER_DOT_NAME);
    private static final ClassNode THREAD_CLASSNODE = ClassHelper.make(Thread.class);
    private static final ClassNode SYSTEM_CLASSNODE = ClassHelper.make(System.class);
    private static final List<String> GRABEXCLUDE_REQUIRED = Arrays.asList("group", "module");
    private static final List<String> GRABRESOLVER_REQUIRED = Arrays.asList("name", "root");
    private static final List<String> GRAB_REQUIRED = Arrays.asList("group", "module", "version");
    private static final List<String> GRAB_OPTIONAL = Arrays.asList("classifier", "transitive", "conf", "ext", "type", "changing", "force", "initClass");
    private static final List<String> GRAB_BOOLEAN = Arrays.asList("transitive", "changing", "force", "initClass");
    private static final Collection<String> GRAB_ALL = DefaultGroovyMethods.plus(GRAB_REQUIRED, GRAB_OPTIONAL);
    private static final Pattern IVY_PATTERN = Pattern.compile("([a-zA-Z0-9-/._+=]+)#([a-zA-Z0-9-/._+=]+)(;([a-zA-Z0-9-/.\\(\\)\\[\\]\\{\\}_+=,:@][a-zA-Z0-9-/.\\(\\)\\]\\{\\}_+=,:@]*))?(\\[([a-zA-Z0-9-/._+=,]*)\\])?");
    private static final Pattern ATTRIBUTES_PATTERN = Pattern.compile("(.*;|^)([a-zA-Z0-9]+)=([a-zA-Z0-9.*\\[\\]\\-\\(\\),]*)$");
    private static final String AUTO_DOWNLOAD_SETTING = "autoDownload";
    private static final String DISABLE_CHECKSUMS_SETTING = "disableChecksums";
    private static final String SYSTEM_PROPERTIES_SETTING = "systemProperties";
    boolean allowShortGrab;
    Set<String> grabAliases;
    List<AnnotationNode> grabAnnotations;
    boolean allowShortGrabExcludes;
    Set<String> grabExcludeAliases;
    List<AnnotationNode> grabExcludeAnnotations;
    boolean allowShortGrabConfig;
    Set<String> grabConfigAliases;
    List<AnnotationNode> grabConfigAnnotations;
    boolean allowShortGrapes;
    Set<String> grapesAliases;
    List<AnnotationNode> grapesAnnotations;
    boolean allowShortGrabResolver;
    Set<String> grabResolverAliases;
    List<AnnotationNode> grabResolverAnnotations;
    CompilationUnit compilationUnit;
    SourceUnit sourceUnit;
    ClassLoader loader;
    boolean initContextClassLoader;
    Boolean autoDownload;
    Boolean disableChecksums;
    Map<String, String> systemProperties;

    private static String dotName(String className) {
        return className.substring(className.lastIndexOf("."));
    }

    private static String shortName(String className) {
        return className.substring(1);
    }

    @Override
    public SourceUnit getSourceUnit() {
        return this.sourceUnit;
    }

    @Override
    public void setCompilationUnit(CompilationUnit compilationUnit) {
        this.compilationUnit = compilationUnit;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        this.sourceUnit = source;
        this.loader = null;
        this.initContextClassLoader = false;
        ModuleNode mn = (ModuleNode)nodes[0];
        this.allowShortGrab = true;
        this.allowShortGrabExcludes = true;
        this.allowShortGrabConfig = true;
        this.allowShortGrapes = true;
        this.allowShortGrabResolver = true;
        this.grabAliases = new HashSet<String>();
        this.grabExcludeAliases = new HashSet<String>();
        this.grabConfigAliases = new HashSet<String>();
        this.grapesAliases = new HashSet<String>();
        this.grabResolverAliases = new HashSet<String>();
        for (ImportNode im : mn.getImports()) {
            String alias = im.getAlias();
            String className = im.getClassName();
            if (className.endsWith(GRAB_DOT_NAME) && (alias == null || alias.length() == 0) || GRAB_CLASS_NAME.equals(alias)) {
                this.allowShortGrab = false;
            } else if (GRAB_CLASS_NAME.equals(className)) {
                this.grabAliases.add(im.getAlias());
            }
            if (className.endsWith(GRAPES_DOT_NAME) && (alias == null || alias.length() == 0) || GRAPES_CLASS_NAME.equals(alias)) {
                this.allowShortGrapes = false;
            } else if (GRAPES_CLASS_NAME.equals(className)) {
                this.grapesAliases.add(im.getAlias());
            }
            if (className.endsWith(GRABRESOLVER_DOT_NAME) && (alias == null || alias.length() == 0) || GRABRESOLVER_CLASS_NAME.equals(alias)) {
                this.allowShortGrabResolver = false;
                continue;
            }
            if (!GRABRESOLVER_CLASS_NAME.equals(className)) continue;
            this.grabResolverAliases.add(im.getAlias());
        }
        ArrayList grabMaps = new ArrayList();
        ArrayList<Map<String, Object>> grabMapsInit = new ArrayList<Map<String, Object>>();
        ArrayList<Map<String, Object>> grabExcludeMaps = new ArrayList<Map<String, Object>>();
        for (ClassNode classNode : this.sourceUnit.getAST().getClasses()) {
            String mval;
            this.grabAnnotations = new ArrayList<AnnotationNode>();
            this.grabExcludeAnnotations = new ArrayList<AnnotationNode>();
            this.grabConfigAnnotations = new ArrayList<AnnotationNode>();
            this.grapesAnnotations = new ArrayList<AnnotationNode>();
            this.grabResolverAnnotations = new ArrayList<AnnotationNode>();
            this.visitClass(classNode);
            ClassNode grapeClassNode = ClassHelper.make(Grape.class);
            ArrayList<Statement> grabResolverInitializers = new ArrayList<Statement>();
            if (!this.grapesAnnotations.isEmpty()) {
                for (AnnotationNode node : this.grapesAnnotations) {
                    Expression init = node.getMember("initClass");
                    Expression value = node.getMember("value");
                    if (value instanceof ListExpression) {
                        for (Object object : ((ListExpression)value).getExpressions()) {
                            if (!(object instanceof ConstantExpression)) continue;
                            this.extractGrab(init, (ConstantExpression)object);
                        }
                        continue;
                    }
                    if (!(value instanceof ConstantExpression)) continue;
                    this.extractGrab(init, (ConstantExpression)value);
                }
            }
            if (!this.grabResolverAnnotations.isEmpty()) {
                block8: for (AnnotationNode node : this.grabResolverAnnotations) {
                    HashMap<String, Object> grabResolverMap = new HashMap<String, Object>();
                    String sval = AbstractASTTransformation.getMemberStringValue(node, "value");
                    if (sval != null && sval.length() > 0) {
                        for (String string : GRABRESOLVER_REQUIRED) {
                            mval = AbstractASTTransformation.getMemberStringValue(node, string);
                            if (mval != null && mval.isEmpty()) {
                                mval = null;
                            }
                            if (mval == null) continue;
                            this.addError("The attribute \"" + string + "\" conflicts with attribute 'value' in @" + node.getClassNode().getNameWithoutPackage() + " annotations", node);
                            continue block8;
                        }
                        grabResolverMap.put("name", sval);
                        grabResolverMap.put("root", sval);
                    } else {
                        for (String string : GRABRESOLVER_REQUIRED) {
                            mval = AbstractASTTransformation.getMemberStringValue(node, string);
                            Expression member2 = node.getMember(string);
                            if (member2 == null || mval != null && mval.isEmpty()) {
                                this.addError("The missing attribute \"" + string + "\" is required in @" + node.getClassNode().getNameWithoutPackage() + " annotations", node);
                                continue block8;
                            }
                            if (mval == null) {
                                this.addError("Attribute \"" + string + "\" has value " + member2.getText() + " but should be an inline constant String in @" + node.getClassNode().getNameWithoutPackage() + " annotations", node);
                                continue block8;
                            }
                            grabResolverMap.put(string, mval);
                        }
                    }
                    String root = (String)grabResolverMap.get("root");
                    if (root != null && !root.contains(":")) {
                        void var16_23;
                        Object var16_21 = null;
                        if (!(this.getSourceUnit().getSource() instanceof StringReaderSource)) {
                            URI uRI = this.getSourceUnit().getSource().getURI();
                        }
                        if (var16_23 == null) {
                            URI uRI = new File(".").toURI();
                        }
                        try {
                            void var16_25;
                            URI rootURI = var16_25.resolve(new URI(root));
                            grabResolverMap.put("root", rootURI.toString());
                        }
                        catch (URISyntaxException rootURI) {
                            // empty catch block
                        }
                    }
                    Grape.addResolver(grabResolverMap);
                    GrabAnnotationTransformation.addGrabResolverAsStaticInitIfNeeded(grapeClassNode, node, grabResolverInitializers, grabResolverMap);
                }
            }
            if (!this.grabConfigAnnotations.isEmpty()) {
                for (AnnotationNode node : this.grabConfigAnnotations) {
                    this.checkForClassLoader(node);
                    this.checkForInitContextClassLoader(node);
                    this.checkForAutoDownload(node);
                    this.checkForSystemProperties(node);
                    this.checkForDisableChecksums(node);
                }
                this.addInitContextClassLoaderIfNeeded(classNode);
            }
            if (!this.grabExcludeAnnotations.isEmpty()) {
                block12: for (AnnotationNode node : this.grabExcludeAnnotations) {
                    HashMap<String, Object> grabExcludeMap = new HashMap<String, Object>();
                    GrabAnnotationTransformation.checkForConvenienceForm(node, true);
                    for (String s : GRABEXCLUDE_REQUIRED) {
                        Expression expression = node.getMember(s);
                        if (expression == null) {
                            this.addError("The missing attribute \"" + s + "\" is required in @" + node.getClassNode().getNameWithoutPackage() + " annotations", node);
                            continue block12;
                        }
                        if (expression != null && !(expression instanceof ConstantExpression)) {
                            this.addError("Attribute \"" + s + "\" has value " + expression.getText() + " but should be an inline constant in @" + node.getClassNode().getNameWithoutPackage() + " annotations", node);
                            continue block12;
                        }
                        grabExcludeMap.put(s, ((ConstantExpression)expression).getValue());
                    }
                    grabExcludeMaps.add(grabExcludeMap);
                }
            }
            if (!this.grabAnnotations.isEmpty()) {
                block14: for (AnnotationNode node : this.grabAnnotations) {
                    HashMap<String, Object> grabMap = new HashMap<String, Object>();
                    GrabAnnotationTransformation.checkForConvenienceForm(node, false);
                    for (String s : GRAB_ALL) {
                        void var16_29;
                        Expression expression = node.getMember(s);
                        mval = AbstractASTTransformation.getMemberStringValue(node, s);
                        if (mval != null && mval.isEmpty()) {
                            Object var16_28 = null;
                        }
                        if (var16_29 == null && !GRAB_OPTIONAL.contains(s)) {
                            this.addError("The missing attribute \"" + s + "\" is required in @" + node.getClassNode().getNameWithoutPackage() + " annotations", node);
                            continue block14;
                        }
                        if (var16_29 != null && !(var16_29 instanceof ConstantExpression)) {
                            this.addError("Attribute \"" + s + "\" has value " + var16_29.getText() + " but should be an inline constant in @" + node.getClassNode().getNameWithoutPackage() + " annotations", node);
                            continue block14;
                        }
                        if (node.getMember(s) == null) continue;
                        grabMap.put(s, ((ConstantExpression)var16_29).getValue());
                    }
                    grabMaps.add(grabMap);
                    if (node.getMember("initClass") != null && node.getMember("initClass") != ConstantExpression.TRUE) continue;
                    grabMapsInit.add(grabMap);
                }
                this.callGrabAsStaticInitIfNeeded(classNode, grapeClassNode, grabMapsInit, grabExcludeMaps);
            }
            if (grabResolverInitializers.isEmpty()) continue;
            classNode.addStaticInitializerStatements(grabResolverInitializers, true);
        }
        if (!grabMaps.isEmpty()) {
            HashMap<String, Object> basicArgs = new HashMap<String, Object>();
            basicArgs.put("classLoader", this.loader != null ? this.loader : this.sourceUnit.getClassLoader());
            if (!grabExcludeMaps.isEmpty()) {
                basicArgs.put("excludes", grabExcludeMaps);
            }
            if (this.autoDownload != null) {
                basicArgs.put(AUTO_DOWNLOAD_SETTING, this.autoDownload);
            }
            if (this.disableChecksums != null) {
                basicArgs.put(DISABLE_CHECKSUMS_SETTING, this.disableChecksums);
            }
            if (this.systemProperties != null) {
                basicArgs.put(SYSTEM_PROPERTIES_SETTING, this.systemProperties);
            }
            try {
                Grape.grab(basicArgs, grabMaps.toArray(new Map[grabMaps.size()]));
                if (this.compilationUnit != null) {
                    ASTTransformationVisitor.addGlobalTransformsAfterGrab(this.compilationUnit.getASTTransformationsContext());
                }
            }
            catch (RuntimeException re) {
                source.addException(re);
            }
        }
    }

    private void callGrabAsStaticInitIfNeeded(ClassNode classNode, ClassNode grapeClassNode, List<Map<String, Object>> grabMapsInit, List<Map<String, Object>> grabExcludeMaps) {
        ArrayList<Statement> grabInitializers = new ArrayList<Statement>();
        MapExpression basicArgs = new MapExpression();
        if (this.autoDownload != null) {
            basicArgs.addMapEntryExpression(GeneralUtils.constX(AUTO_DOWNLOAD_SETTING), GeneralUtils.constX(this.autoDownload));
        }
        if (this.disableChecksums != null) {
            basicArgs.addMapEntryExpression(GeneralUtils.constX(DISABLE_CHECKSUMS_SETTING), GeneralUtils.constX(this.disableChecksums));
        }
        if (this.systemProperties != null && !this.systemProperties.isEmpty()) {
            BlockStatement block = new BlockStatement();
            for (Map.Entry entry : this.systemProperties.entrySet()) {
                block.addStatement(GeneralUtils.stmt(GeneralUtils.callX(SYSTEM_CLASSNODE, "setProperty", (Expression)GeneralUtils.args(GeneralUtils.constX(entry.getKey()), GeneralUtils.constX(entry.getValue())))));
            }
            StaticMethodCallExpression enabled = GeneralUtils.callX(SYSTEM_CLASSNODE, "getProperty", (Expression)GeneralUtils.args(GeneralUtils.constX("groovy.grape.enable"), GeneralUtils.constX("true")));
            grabInitializers.add(GeneralUtils.ifS((Expression)GeneralUtils.eqX(enabled, GeneralUtils.constX("true")), block));
        }
        if (!grabExcludeMaps.isEmpty()) {
            ListExpression list = new ListExpression();
            for (Map map : grabExcludeMaps) {
                Set entries = map.entrySet();
                MapExpression inner = new MapExpression();
                for (Map.Entry entry : entries) {
                    inner.addMapEntryExpression(GeneralUtils.constX(entry.getKey()), GeneralUtils.constX(entry.getValue()));
                }
                list.addExpression(inner);
            }
            basicArgs.addMapEntryExpression(GeneralUtils.constX("excludes"), list);
        }
        ArrayList<Expression> argList = new ArrayList<Expression>();
        argList.add(basicArgs);
        if (grabMapsInit.isEmpty()) {
            return;
        }
        for (Map map : grabMapsInit) {
            MapExpression dependencyArg = new MapExpression();
            for (String s : GRAB_REQUIRED) {
                dependencyArg.addMapEntryExpression(GeneralUtils.constX(s), GeneralUtils.constX(map.get(s)));
            }
            for (String s : GRAB_OPTIONAL) {
                if (!map.containsKey(s)) continue;
                dependencyArg.addMapEntryExpression(GeneralUtils.constX(s), GeneralUtils.constX(map.get(s)));
            }
            argList.add(dependencyArg);
        }
        grabInitializers.add(GeneralUtils.stmt(GeneralUtils.callX(grapeClassNode, "grab", (Expression)GeneralUtils.args(argList))));
        classNode.addStaticInitializerStatements(grabInitializers, true);
    }

    private static void addGrabResolverAsStaticInitIfNeeded(ClassNode grapeClassNode, AnnotationNode node, List<Statement> grabResolverInitializers, Map<String, Object> grabResolverMap) {
        if (node.getMember("initClass") == null || node.getMember("initClass") == ConstantExpression.TRUE) {
            MapExpression resolverArgs = new MapExpression();
            for (Map.Entry<String, Object> next : grabResolverMap.entrySet()) {
                resolverArgs.addMapEntryExpression(GeneralUtils.constX(next.getKey()), GeneralUtils.constX(next.getValue()));
            }
            grabResolverInitializers.add(GeneralUtils.stmt(GeneralUtils.callX(grapeClassNode, "addResolver", (Expression)GeneralUtils.args(resolverArgs))));
        }
    }

    private void addInitContextClassLoaderIfNeeded(ClassNode classNode) {
        if (this.initContextClassLoader) {
            Statement initStatement = GeneralUtils.stmt(GeneralUtils.callX((Expression)GeneralUtils.callX(THREAD_CLASSNODE, "currentThread"), "setContextClassLoader", (Expression)GeneralUtils.callX(GeneralUtils.callThisX("getClass"), "getClassLoader")));
            classNode.addObjectInitializerStatements(initStatement);
        }
    }

    private void checkForClassLoader(AnnotationNode node) {
        Expression val = node.getMember("systemClassLoader");
        if (val == null || !(val instanceof ConstantExpression)) {
            return;
        }
        Object systemClassLoaderObject = ((ConstantExpression)val).getValue();
        if (!(systemClassLoaderObject instanceof Boolean)) {
            return;
        }
        Boolean systemClassLoader = (Boolean)systemClassLoaderObject;
        if (systemClassLoader.booleanValue()) {
            this.loader = ClassLoader.getSystemClassLoader();
        }
    }

    private void checkForInitContextClassLoader(AnnotationNode node) {
        Expression val = node.getMember("initContextClassLoader");
        if (val == null || !(val instanceof ConstantExpression)) {
            return;
        }
        Object initContextClassLoaderObject = ((ConstantExpression)val).getValue();
        if (!(initContextClassLoaderObject instanceof Boolean)) {
            return;
        }
        this.initContextClassLoader = (Boolean)initContextClassLoaderObject;
    }

    private void checkForAutoDownload(AnnotationNode node) {
        Expression val = node.getMember(AUTO_DOWNLOAD_SETTING);
        if (val == null || !(val instanceof ConstantExpression)) {
            return;
        }
        Object autoDownloadValue = ((ConstantExpression)val).getValue();
        if (!(autoDownloadValue instanceof Boolean)) {
            return;
        }
        this.autoDownload = (Boolean)autoDownloadValue;
    }

    private void checkForDisableChecksums(AnnotationNode node) {
        Expression val = node.getMember(DISABLE_CHECKSUMS_SETTING);
        if (val == null || !(val instanceof ConstantExpression)) {
            return;
        }
        Object disableChecksumsValue = ((ConstantExpression)val).getValue();
        if (!(disableChecksumsValue instanceof Boolean)) {
            return;
        }
        this.disableChecksums = (Boolean)disableChecksumsValue;
    }

    private void checkForSystemProperties(AnnotationNode node) {
        this.systemProperties = new HashMap<String, String>();
        List<String> nameValueList = AbstractASTTransformation.getMemberList(node, SYSTEM_PROPERTIES_SETTING);
        if (nameValueList != null) {
            for (String nameValue : nameValueList) {
                int equalsDelim = nameValue.indexOf(61);
                if (equalsDelim == -1) continue;
                this.systemProperties.put(nameValue.substring(0, equalsDelim), nameValue.substring(equalsDelim + 1));
            }
        }
    }

    private static void checkForConvenienceForm(AnnotationNode node, boolean exclude) {
        Object value;
        Expression val = node.getMember("value");
        if (val == null || !(val instanceof ConstantExpression)) {
            return;
        }
        Object allParts = ((ConstantExpression)val).getValue();
        if (!(allParts instanceof String)) {
            return;
        }
        String allstr = (String)allParts;
        boolean done = false;
        while (!done) {
            Matcher attrs = ATTRIBUTES_PATTERN.matcher(allstr);
            if (attrs.find()) {
                String attrName = attrs.group(2);
                String attrValue = attrs.group(3);
                if (attrName == null || attrValue == null) continue;
                boolean isBool = GRAB_BOOLEAN.contains(attrName);
                value = GeneralUtils.constX(isBool ? Boolean.valueOf(attrValue) : attrValue);
                ((ASTNode)value).setSourcePosition(node);
                node.addMember(attrName, (Expression)value);
                int lastSemi = allstr.lastIndexOf(59);
                if (lastSemi == -1) {
                    allstr = "";
                    break;
                }
                allstr = allstr.substring(0, lastSemi);
                continue;
            }
            done = true;
        }
        if (allstr.contains("#")) {
            Matcher m = IVY_PATTERN.matcher(allstr);
            if (!m.find()) {
                return;
            }
            if (m.group(1) == null || m.group(2) == null) {
                return;
            }
            node.addMember("module", GeneralUtils.constX(m.group(2)));
            node.addMember("group", GeneralUtils.constX(m.group(1)));
            if (m.group(6) != null) {
                node.addMember("conf", GeneralUtils.constX(m.group(6)));
            }
            if (m.group(4) != null) {
                node.addMember("version", GeneralUtils.constX(m.group(4)));
            } else if (!exclude && node.getMember("version") == null) {
                node.addMember("version", GeneralUtils.constX("*"));
            }
            node.getMembers().remove("value");
        } else if (allstr.contains(":")) {
            Map<String, Object> parts = GrapeUtil.getIvyParts(allstr);
            for (Map.Entry<String, Object> entry : parts.entrySet()) {
                String key = entry.getKey();
                value = entry.getValue().toString();
                if (key.equals("version") && ((String)value).equals("*") && exclude) continue;
                node.addMember(key, GeneralUtils.constX(value));
            }
            node.getMembers().remove("value");
        }
    }

    private void extractGrab(Expression init, ConstantExpression ce) {
        if (ce.getValue() instanceof AnnotationNode) {
            String name;
            AnnotationNode annotation = (AnnotationNode)ce.getValue();
            if (init != null && annotation.getMember("initClass") != null) {
                annotation.setMember("initClass", init);
            }
            if (GRAB_CLASS_NAME.equals(name = annotation.getClassNode().getName()) || this.allowShortGrab && GRAB_SHORT_NAME.equals(name) || this.grabAliases.contains(name)) {
                this.grabAnnotations.add(annotation);
            }
            if (GRABEXCLUDE_CLASS_NAME.equals(name) || this.allowShortGrabExcludes && GRABEXCLUDE_SHORT_NAME.equals(name) || this.grabExcludeAliases.contains(name)) {
                this.grabExcludeAnnotations.add(annotation);
            }
            if (GRABCONFIG_CLASS_NAME.equals(name) || this.allowShortGrabConfig && GRABCONFIG_SHORT_NAME.equals(name) || this.grabConfigAliases.contains(name)) {
                this.grabConfigAnnotations.add(annotation);
            }
            if (GRABRESOLVER_CLASS_NAME.equals(name) || this.allowShortGrabResolver && GRABRESOLVER_SHORT_NAME.equals(name) || this.grabResolverAliases.contains(name)) {
                this.grabResolverAnnotations.add(annotation);
            }
        }
    }

    @Override
    public void visitAnnotations(AnnotatedNode node) {
        super.visitAnnotations(node);
        for (AnnotationNode an : node.getAnnotations()) {
            String name = an.getClassNode().getName();
            if (GRAB_CLASS_NAME.equals(name) || this.allowShortGrab && GRAB_SHORT_NAME.equals(name) || this.grabAliases.contains(name)) {
                this.grabAnnotations.add(an);
            }
            if (GRABEXCLUDE_CLASS_NAME.equals(name) || this.allowShortGrabExcludes && GRABEXCLUDE_SHORT_NAME.equals(name) || this.grabExcludeAliases.contains(name)) {
                this.grabExcludeAnnotations.add(an);
            }
            if (GRABCONFIG_CLASS_NAME.equals(name) || this.allowShortGrabConfig && GRABCONFIG_SHORT_NAME.equals(name) || this.grabConfigAliases.contains(name)) {
                this.grabConfigAnnotations.add(an);
            }
            if (GRAPES_CLASS_NAME.equals(name) || this.allowShortGrapes && GRAPES_SHORT_NAME.equals(name) || this.grapesAliases.contains(name)) {
                this.grapesAnnotations.add(an);
            }
            if (!GRABRESOLVER_CLASS_NAME.equals(name) && (!this.allowShortGrabResolver || !GRABRESOLVER_SHORT_NAME.equals(name)) && !this.grabResolverAliases.contains(name)) continue;
            this.grabResolverAnnotations.add(an);
        }
    }
}

