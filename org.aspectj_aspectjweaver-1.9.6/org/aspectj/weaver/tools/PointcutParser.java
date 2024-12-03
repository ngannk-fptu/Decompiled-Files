/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.aspectj.bridge.IMessageHandler;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.bridge.SourceLocation;
import org.aspectj.weaver.BindingScope;
import org.aspectj.weaver.IHasPosition;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.IntMap;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.WeakClassLoaderReference;
import org.aspectj.weaver.World;
import org.aspectj.weaver.internal.tools.PointcutExpressionImpl;
import org.aspectj.weaver.internal.tools.TypePatternMatcherImpl;
import org.aspectj.weaver.patterns.AndPointcut;
import org.aspectj.weaver.patterns.CflowPointcut;
import org.aspectj.weaver.patterns.FormalBinding;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.KindedPointcut;
import org.aspectj.weaver.patterns.NotPointcut;
import org.aspectj.weaver.patterns.OrPointcut;
import org.aspectj.weaver.patterns.ParserException;
import org.aspectj.weaver.patterns.PatternParser;
import org.aspectj.weaver.patterns.Pointcut;
import org.aspectj.weaver.patterns.SimpleScope;
import org.aspectj.weaver.patterns.ThisOrTargetAnnotationPointcut;
import org.aspectj.weaver.patterns.ThisOrTargetPointcut;
import org.aspectj.weaver.patterns.TypePattern;
import org.aspectj.weaver.reflect.PointcutParameterImpl;
import org.aspectj.weaver.reflect.ReflectionWorld;
import org.aspectj.weaver.tools.PointcutDesignatorHandler;
import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParameter;
import org.aspectj.weaver.tools.PointcutPrimitive;
import org.aspectj.weaver.tools.TypePatternMatcher;
import org.aspectj.weaver.tools.UnsupportedPointcutPrimitiveException;

public class PointcutParser {
    private ReflectionWorld world;
    private WeakClassLoaderReference classLoaderReference;
    private final Set<PointcutPrimitive> supportedPrimitives;
    private final Set<PointcutDesignatorHandler> pointcutDesignators = new HashSet<PointcutDesignatorHandler>();

    public static Set<PointcutPrimitive> getAllSupportedPointcutPrimitives() {
        HashSet<PointcutPrimitive> primitives = new HashSet<PointcutPrimitive>();
        primitives.add(PointcutPrimitive.ADVICE_EXECUTION);
        primitives.add(PointcutPrimitive.ARGS);
        primitives.add(PointcutPrimitive.CALL);
        primitives.add(PointcutPrimitive.EXECUTION);
        primitives.add(PointcutPrimitive.GET);
        primitives.add(PointcutPrimitive.HANDLER);
        primitives.add(PointcutPrimitive.INITIALIZATION);
        primitives.add(PointcutPrimitive.PRE_INITIALIZATION);
        primitives.add(PointcutPrimitive.SET);
        primitives.add(PointcutPrimitive.STATIC_INITIALIZATION);
        primitives.add(PointcutPrimitive.TARGET);
        primitives.add(PointcutPrimitive.THIS);
        primitives.add(PointcutPrimitive.WITHIN);
        primitives.add(PointcutPrimitive.WITHIN_CODE);
        primitives.add(PointcutPrimitive.AT_ANNOTATION);
        primitives.add(PointcutPrimitive.AT_THIS);
        primitives.add(PointcutPrimitive.AT_TARGET);
        primitives.add(PointcutPrimitive.AT_ARGS);
        primitives.add(PointcutPrimitive.AT_WITHIN);
        primitives.add(PointcutPrimitive.AT_WITHINCODE);
        primitives.add(PointcutPrimitive.REFERENCE);
        return primitives;
    }

    public static PointcutParser getPointcutParserSupportingAllPrimitivesAndUsingContextClassloaderForResolution() {
        PointcutParser p = new PointcutParser();
        p.setClassLoader(Thread.currentThread().getContextClassLoader());
        return p;
    }

    public static PointcutParser getPointcutParserSupportingSpecifiedPrimitivesAndUsingContextClassloaderForResolution(Set<PointcutPrimitive> supportedPointcutKinds) {
        PointcutParser p = new PointcutParser(supportedPointcutKinds);
        p.setClassLoader(Thread.currentThread().getContextClassLoader());
        return p;
    }

    public static PointcutParser getPointcutParserSupportingAllPrimitivesAndUsingSpecifiedClassloaderForResolution(ClassLoader classLoader) {
        PointcutParser p = new PointcutParser();
        p.setClassLoader(classLoader);
        return p;
    }

    public static PointcutParser getPointcutParserSupportingSpecifiedPrimitivesAndUsingSpecifiedClassLoaderForResolution(Set<PointcutPrimitive> supportedPointcutKinds, ClassLoader classLoader) {
        PointcutParser p = new PointcutParser(supportedPointcutKinds);
        p.setClassLoader(classLoader);
        return p;
    }

    protected PointcutParser() {
        this.supportedPrimitives = PointcutParser.getAllSupportedPointcutPrimitives();
        this.setClassLoader(PointcutParser.class.getClassLoader());
    }

    private PointcutParser(Set<PointcutPrimitive> supportedPointcutKinds) {
        this.supportedPrimitives = supportedPointcutKinds;
        for (PointcutPrimitive pointcutPrimitive : supportedPointcutKinds) {
            if (pointcutPrimitive != PointcutPrimitive.IF && pointcutPrimitive != PointcutPrimitive.CFLOW && pointcutPrimitive != PointcutPrimitive.CFLOW_BELOW) continue;
            throw new UnsupportedOperationException("Cannot handle if, cflow, and cflowbelow primitives");
        }
        this.setClassLoader(PointcutParser.class.getClassLoader());
    }

    protected void setWorld(ReflectionWorld aWorld) {
        this.world = aWorld;
    }

    protected void setClassLoader(ClassLoader aLoader) {
        this.classLoaderReference = new WeakClassLoaderReference(aLoader);
        this.world = ReflectionWorld.getReflectionWorldFor(this.classLoaderReference);
    }

    protected void setClassLoader(ClassLoader aLoader, boolean shareWorlds) {
        this.classLoaderReference = new WeakClassLoaderReference(aLoader);
        this.world = shareWorlds ? ReflectionWorld.getReflectionWorldFor(this.classLoaderReference) : new ReflectionWorld(this.classLoaderReference);
    }

    public void setLintProperties(String resourcePath) throws IOException {
        URL url = this.classLoaderReference.getClassLoader().getResource(resourcePath);
        InputStream is = url.openStream();
        Properties p = new Properties();
        p.load(is);
        this.setLintProperties(p);
    }

    public void setLintProperties(Properties properties) {
        this.getWorld().getLint().setFromProperties(properties);
    }

    public void registerPointcutDesignatorHandler(PointcutDesignatorHandler designatorHandler) {
        this.pointcutDesignators.add(designatorHandler);
        if (this.world != null) {
            this.world.registerPointcutHandler(designatorHandler);
        }
    }

    public PointcutParameter createPointcutParameter(String name, Class<?> type) {
        return new PointcutParameterImpl(name, type);
    }

    public PointcutExpression parsePointcutExpression(String expression) throws UnsupportedPointcutPrimitiveException, IllegalArgumentException {
        return this.parsePointcutExpression(expression, null, new PointcutParameter[0]);
    }

    public PointcutExpression parsePointcutExpression(String expression, Class<?> inScope, PointcutParameter[] formalParameters) throws UnsupportedPointcutPrimitiveException, IllegalArgumentException {
        PointcutExpressionImpl pcExpr = null;
        try {
            Pointcut pc = this.resolvePointcutExpression(expression, inScope, formalParameters);
            pc = this.concretizePointcutExpression(pc, inScope, formalParameters);
            this.validateAgainstSupportedPrimitives(pc, expression);
            pcExpr = new PointcutExpressionImpl(pc, expression, formalParameters, this.getWorld());
        }
        catch (ParserException pEx) {
            throw new IllegalArgumentException(this.buildUserMessageFromParserException(expression, pEx));
        }
        catch (ReflectionWorld.ReflectionWorldException rwEx) {
            throw new IllegalArgumentException(rwEx.getMessage());
        }
        return pcExpr;
    }

    protected Pointcut resolvePointcutExpression(String expression, Class<?> inScope, PointcutParameter[] formalParameters) {
        try {
            PatternParser parser = new PatternParser(expression);
            parser.setPointcutDesignatorHandlers(this.pointcutDesignators, this.world);
            Pointcut pc = parser.parsePointcut();
            this.validateAgainstSupportedPrimitives(pc, expression);
            IScope resolutionScope = this.buildResolutionScope(inScope == null ? Object.class : inScope, formalParameters);
            pc = pc.resolve(resolutionScope);
            return pc;
        }
        catch (ParserException pEx) {
            throw new IllegalArgumentException(this.buildUserMessageFromParserException(expression, pEx));
        }
    }

    protected Pointcut concretizePointcutExpression(Pointcut pc, Class<?> inScope, PointcutParameter[] formalParameters) {
        ResolvedType declaringTypeForResolution = null;
        declaringTypeForResolution = inScope != null ? this.getWorld().resolve(inScope.getName()) : ResolvedType.OBJECT.resolve(this.getWorld());
        IntMap arity = new IntMap(formalParameters.length);
        for (int i = 0; i < formalParameters.length; ++i) {
            arity.put(i, i);
        }
        return pc.concretize(declaringTypeForResolution, declaringTypeForResolution, arity);
    }

    public TypePatternMatcher parseTypePattern(String typePattern) throws IllegalArgumentException {
        try {
            TypePattern tp = new PatternParser(typePattern).parseTypePattern();
            tp.resolve(this.world);
            return new TypePatternMatcherImpl(tp, this.world);
        }
        catch (ParserException pEx) {
            throw new IllegalArgumentException(this.buildUserMessageFromParserException(typePattern, pEx));
        }
        catch (ReflectionWorld.ReflectionWorldException rwEx) {
            throw new IllegalArgumentException(rwEx.getMessage());
        }
    }

    private World getWorld() {
        return this.world;
    }

    Set<PointcutPrimitive> getSupportedPrimitives() {
        return this.supportedPrimitives;
    }

    IMessageHandler setCustomMessageHandler(IMessageHandler aHandler) {
        IMessageHandler current = this.getWorld().getMessageHandler();
        this.getWorld().setMessageHandler(aHandler);
        return current;
    }

    private IScope buildResolutionScope(Class<?> inScope, PointcutParameter[] formalParameters) {
        if (formalParameters == null) {
            formalParameters = new PointcutParameter[]{};
        }
        FormalBinding[] formalBindings = new FormalBinding[formalParameters.length];
        for (int i = 0; i < formalBindings.length; ++i) {
            formalBindings[i] = new FormalBinding(this.toUnresolvedType(formalParameters[i].getType()), formalParameters[i].getName(), i);
        }
        if (inScope == null) {
            return new SimpleScope(this.getWorld(), formalBindings);
        }
        ResolvedType inType = this.getWorld().resolve(inScope.getName());
        ISourceContext sourceContext = new ISourceContext(){

            @Override
            public ISourceLocation makeSourceLocation(IHasPosition position) {
                return new SourceLocation(new File(""), 0);
            }

            @Override
            public ISourceLocation makeSourceLocation(int line, int offset) {
                return new SourceLocation(new File(""), line);
            }

            @Override
            public int getOffset() {
                return 0;
            }

            @Override
            public void tidy() {
            }
        };
        return new BindingScope(inType, sourceContext, formalBindings);
    }

    private UnresolvedType toUnresolvedType(Class<?> clazz) {
        if (clazz.isArray()) {
            return UnresolvedType.forSignature(clazz.getName().replace('.', '/'));
        }
        return UnresolvedType.forName(clazz.getName());
    }

    private void validateAgainstSupportedPrimitives(Pointcut pc, String expression) {
        switch (pc.getPointcutKind()) {
            case 5: {
                this.validateAgainstSupportedPrimitives(((AndPointcut)pc).getLeft(), expression);
                this.validateAgainstSupportedPrimitives(((AndPointcut)pc).getRight(), expression);
                break;
            }
            case 4: {
                if (this.supportedPrimitives.contains(PointcutPrimitive.ARGS)) break;
                throw new UnsupportedPointcutPrimitiveException(expression, PointcutPrimitive.ARGS);
            }
            case 10: {
                CflowPointcut cfp = (CflowPointcut)pc;
                if (cfp.isCflowBelow()) {
                    throw new UnsupportedPointcutPrimitiveException(expression, PointcutPrimitive.CFLOW_BELOW);
                }
                throw new UnsupportedPointcutPrimitiveException(expression, PointcutPrimitive.CFLOW);
            }
            case 13: {
                if (this.supportedPrimitives.contains(PointcutPrimitive.HANDLER)) break;
                throw new UnsupportedPointcutPrimitiveException(expression, PointcutPrimitive.HANDLER);
            }
            case 9: 
            case 14: 
            case 15: {
                throw new UnsupportedPointcutPrimitiveException(expression, PointcutPrimitive.IF);
            }
            case 1: {
                this.validateKindedPointcut((KindedPointcut)pc, expression);
                break;
            }
            case 7: {
                this.validateAgainstSupportedPrimitives(((NotPointcut)pc).getNegatedPointcut(), expression);
                break;
            }
            case 6: {
                this.validateAgainstSupportedPrimitives(((OrPointcut)pc).getLeft(), expression);
                this.validateAgainstSupportedPrimitives(((OrPointcut)pc).getRight(), expression);
                break;
            }
            case 3: {
                boolean isThis = ((ThisOrTargetPointcut)pc).isThis();
                if (isThis && !this.supportedPrimitives.contains(PointcutPrimitive.THIS)) {
                    throw new UnsupportedPointcutPrimitiveException(expression, PointcutPrimitive.THIS);
                }
                if (this.supportedPrimitives.contains(PointcutPrimitive.TARGET)) break;
                throw new UnsupportedPointcutPrimitiveException(expression, PointcutPrimitive.TARGET);
            }
            case 2: {
                if (this.supportedPrimitives.contains(PointcutPrimitive.WITHIN)) break;
                throw new UnsupportedPointcutPrimitiveException(expression, PointcutPrimitive.WITHIN);
            }
            case 12: {
                if (this.supportedPrimitives.contains(PointcutPrimitive.WITHIN_CODE)) break;
                throw new UnsupportedPointcutPrimitiveException(expression, PointcutPrimitive.WITHIN_CODE);
            }
            case 19: {
                boolean isThis = ((ThisOrTargetAnnotationPointcut)pc).isThis();
                if (isThis && !this.supportedPrimitives.contains(PointcutPrimitive.AT_THIS)) {
                    throw new UnsupportedPointcutPrimitiveException(expression, PointcutPrimitive.AT_THIS);
                }
                if (this.supportedPrimitives.contains(PointcutPrimitive.AT_TARGET)) break;
                throw new UnsupportedPointcutPrimitiveException(expression, PointcutPrimitive.AT_TARGET);
            }
            case 21: {
                if (this.supportedPrimitives.contains(PointcutPrimitive.AT_ARGS)) break;
                throw new UnsupportedPointcutPrimitiveException(expression, PointcutPrimitive.AT_ARGS);
            }
            case 16: {
                if (this.supportedPrimitives.contains(PointcutPrimitive.AT_ANNOTATION)) break;
                throw new UnsupportedPointcutPrimitiveException(expression, PointcutPrimitive.AT_ANNOTATION);
            }
            case 17: {
                if (this.supportedPrimitives.contains(PointcutPrimitive.AT_WITHIN)) break;
                throw new UnsupportedPointcutPrimitiveException(expression, PointcutPrimitive.AT_WITHIN);
            }
            case 18: {
                if (this.supportedPrimitives.contains(PointcutPrimitive.AT_WITHINCODE)) break;
                throw new UnsupportedPointcutPrimitiveException(expression, PointcutPrimitive.AT_WITHINCODE);
            }
            case 8: {
                if (this.supportedPrimitives.contains(PointcutPrimitive.REFERENCE)) break;
                throw new UnsupportedPointcutPrimitiveException(expression, PointcutPrimitive.REFERENCE);
            }
            case 22: {
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown pointcut kind: " + pc.getPointcutKind());
            }
        }
    }

    private void validateKindedPointcut(KindedPointcut pc, String expression) {
        Shadow.Kind kind = pc.getKind();
        if (kind == Shadow.MethodCall || kind == Shadow.ConstructorCall) {
            if (!this.supportedPrimitives.contains(PointcutPrimitive.CALL)) {
                throw new UnsupportedPointcutPrimitiveException(expression, PointcutPrimitive.CALL);
            }
        } else if (kind == Shadow.MethodExecution || kind == Shadow.ConstructorExecution) {
            if (!this.supportedPrimitives.contains(PointcutPrimitive.EXECUTION)) {
                throw new UnsupportedPointcutPrimitiveException(expression, PointcutPrimitive.EXECUTION);
            }
        } else if (kind == Shadow.AdviceExecution) {
            if (!this.supportedPrimitives.contains(PointcutPrimitive.ADVICE_EXECUTION)) {
                throw new UnsupportedPointcutPrimitiveException(expression, PointcutPrimitive.ADVICE_EXECUTION);
            }
        } else if (kind == Shadow.FieldGet) {
            if (!this.supportedPrimitives.contains(PointcutPrimitive.GET)) {
                throw new UnsupportedPointcutPrimitiveException(expression, PointcutPrimitive.GET);
            }
        } else if (kind == Shadow.FieldSet) {
            if (!this.supportedPrimitives.contains(PointcutPrimitive.SET)) {
                throw new UnsupportedPointcutPrimitiveException(expression, PointcutPrimitive.SET);
            }
        } else if (kind == Shadow.Initialization) {
            if (!this.supportedPrimitives.contains(PointcutPrimitive.INITIALIZATION)) {
                throw new UnsupportedPointcutPrimitiveException(expression, PointcutPrimitive.INITIALIZATION);
            }
        } else if (kind == Shadow.PreInitialization) {
            if (!this.supportedPrimitives.contains(PointcutPrimitive.PRE_INITIALIZATION)) {
                throw new UnsupportedPointcutPrimitiveException(expression, PointcutPrimitive.PRE_INITIALIZATION);
            }
        } else if (kind == Shadow.StaticInitialization && !this.supportedPrimitives.contains(PointcutPrimitive.STATIC_INITIALIZATION)) {
            throw new UnsupportedPointcutPrimitiveException(expression, PointcutPrimitive.STATIC_INITIALIZATION);
        }
    }

    private String buildUserMessageFromParserException(String pc, ParserException ex) {
        StringBuffer msg = new StringBuffer();
        msg.append("Pointcut is not well-formed: expecting '");
        msg.append(ex.getMessage());
        msg.append("'");
        IHasPosition location = ex.getLocation();
        msg.append(" at character position ");
        msg.append(location.getStart());
        msg.append("\n");
        msg.append(pc);
        msg.append("\n");
        for (int i = 0; i < location.getStart(); ++i) {
            msg.append(" ");
        }
        for (int j = location.getStart(); j <= location.getEnd(); ++j) {
            msg.append("^");
        }
        msg.append("\n");
        return msg.toString();
    }
}

