/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 */
package com.google.inject.internal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.ConfigurationException;
import com.google.inject.CreationException;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.google.inject.Scope;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.Exceptions;
import com.google.inject.internal.util.Classes;
import com.google.inject.internal.util.SourceProvider;
import com.google.inject.internal.util.StackTraceElements;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.ElementSource;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.Message;
import com.google.inject.spi.ScopeBinding;
import com.google.inject.spi.TypeConverterBinding;
import com.google.inject.spi.TypeListenerBinding;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.List;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Errors
implements Serializable {
    private final Errors root;
    private final Errors parent;
    private final Object source;
    private List<Message> errors;
    private static final String CONSTRUCTOR_RULES = "Classes must have either one (and only one) constructor annotated with @Inject or a zero-argument constructor that is not private.";
    private static final Collection<Converter<?>> converters = ImmutableList.of((Object)new Converter<Class>(Class.class){

        @Override
        public String toString(Class c) {
            return c.getName();
        }
    }, (Object)new Converter<Member>(Member.class){

        @Override
        public String toString(Member member) {
            return Classes.toString(member);
        }
    }, (Object)new Converter<Key>(Key.class){

        @Override
        public String toString(Key key) {
            if (key.getAnnotationType() != null) {
                return key.getTypeLiteral() + " annotated with " + (key.getAnnotation() != null ? key.getAnnotation() : key.getAnnotationType());
            }
            return key.getTypeLiteral().toString();
        }
    });

    public Errors() {
        this.root = this;
        this.parent = null;
        this.source = SourceProvider.UNKNOWN_SOURCE;
    }

    public Errors(Object source) {
        this.root = this;
        this.parent = null;
        this.source = source;
    }

    private Errors(Errors parent, Object source) {
        this.root = parent.root;
        this.parent = parent;
        this.source = source;
    }

    public Errors withSource(Object source) {
        return source == this.source || source == SourceProvider.UNKNOWN_SOURCE ? this : new Errors(this, source);
    }

    public Errors missingImplementation(Key key) {
        return this.addMessage("No implementation for %s was bound.", key);
    }

    public Errors jitDisabled(Key key) {
        return this.addMessage("Explicit bindings are required and %s is not explicitly bound.", key);
    }

    public Errors atInjectRequired(Class clazz) {
        return this.addMessage("Explicit @Inject annotations are required on constructors, but %s has no constructors annotated with @Inject.", clazz);
    }

    public Errors converterReturnedNull(String stringValue, Object source, TypeLiteral<?> type, TypeConverterBinding typeConverterBinding) {
        return this.addMessage("Received null converting '%s' (bound at %s) to %s%n using %s.", stringValue, Errors.convert(source), type, typeConverterBinding);
    }

    public Errors conversionTypeError(String stringValue, Object source, TypeLiteral<?> type, TypeConverterBinding typeConverterBinding, Object converted) {
        return this.addMessage("Type mismatch converting '%s' (bound at %s) to %s%n using %s.%n Converter returned %s.", stringValue, Errors.convert(source), type, typeConverterBinding, converted);
    }

    public Errors conversionError(String stringValue, Object source, TypeLiteral<?> type, TypeConverterBinding typeConverterBinding, RuntimeException cause) {
        return this.errorInUserCode(cause, "Error converting '%s' (bound at %s) to %s%n using %s.%n Reason: %s", stringValue, Errors.convert(source), type, typeConverterBinding, cause);
    }

    public Errors ambiguousTypeConversion(String stringValue, Object source, TypeLiteral<?> type, TypeConverterBinding a, TypeConverterBinding b) {
        return this.addMessage("Multiple converters can convert '%s' (bound at %s) to %s:%n %s and%n %s.%n Please adjust your type converter configuration to avoid overlapping matches.", stringValue, Errors.convert(source), type, a, b);
    }

    public Errors bindingToProvider() {
        return this.addMessage("Binding to Provider is not allowed.", new Object[0]);
    }

    public Errors subtypeNotProvided(Class<? extends Provider<?>> providerType, Class<?> type) {
        return this.addMessage("%s doesn't provide instances of %s.", providerType, type);
    }

    public Errors notASubtype(Class<?> implementationType, Class<?> type) {
        return this.addMessage("%s doesn't extend %s.", implementationType, type);
    }

    public Errors recursiveImplementationType() {
        return this.addMessage("@ImplementedBy points to the same class it annotates.", new Object[0]);
    }

    public Errors recursiveProviderType() {
        return this.addMessage("@ProvidedBy points to the same class it annotates.", new Object[0]);
    }

    public Errors missingRuntimeRetention(Class<? extends Annotation> annotation) {
        return this.addMessage(Errors.format("Please annotate %s with @Retention(RUNTIME).", annotation), new Object[0]);
    }

    public Errors missingScopeAnnotation(Class<? extends Annotation> annotation) {
        return this.addMessage(Errors.format("Please annotate %s with @ScopeAnnotation.", annotation), new Object[0]);
    }

    public Errors optionalConstructor(Constructor constructor) {
        return this.addMessage("%s is annotated @Inject(optional=true), but constructors cannot be optional.", constructor);
    }

    public Errors cannotBindToGuiceType(String simpleName) {
        return this.addMessage("Binding to core guice framework type is not allowed: %s.", simpleName);
    }

    public Errors scopeNotFound(Class<? extends Annotation> scopeAnnotation) {
        return this.addMessage("No scope is bound to %s.", scopeAnnotation);
    }

    public Errors scopeAnnotationOnAbstractType(Class<? extends Annotation> scopeAnnotation, Class<?> type, Object source) {
        return this.addMessage("%s is annotated with %s, but scope annotations are not supported for abstract types.%n Bound at %s.", type, scopeAnnotation, Errors.convert(source));
    }

    public Errors misplacedBindingAnnotation(Member member, Annotation bindingAnnotation) {
        return this.addMessage("%s is annotated with %s, but binding annotations should be applied to its parameters instead.", member, bindingAnnotation);
    }

    public Errors missingConstructor(Class<?> implementation) {
        return this.addMessage("Could not find a suitable constructor in %s. Classes must have either one (and only one) constructor annotated with @Inject or a zero-argument constructor that is not private.", implementation);
    }

    public Errors tooManyConstructors(Class<?> implementation) {
        return this.addMessage("%s has more than one constructor annotated with @Inject. Classes must have either one (and only one) constructor annotated with @Inject or a zero-argument constructor that is not private.", implementation);
    }

    public Errors constructorNotDefinedByType(Constructor<?> constructor, TypeLiteral<?> type) {
        return this.addMessage("%s does not define %s", type, constructor);
    }

    public Errors duplicateScopes(ScopeBinding existing, Class<? extends Annotation> annotationType, Scope scope) {
        return this.addMessage("Scope %s is already bound to %s at %s.%n Cannot bind %s.", existing.getScope(), annotationType, existing.getSource(), scope);
    }

    public Errors voidProviderMethod() {
        return this.addMessage("Provider methods must return a value. Do not return void.", new Object[0]);
    }

    public Errors missingConstantValues() {
        return this.addMessage("Missing constant value. Please call to(...).", new Object[0]);
    }

    public Errors cannotInjectInnerClass(Class<?> type) {
        return this.addMessage("Injecting into inner classes is not supported.  Please use a 'static' class (top-level or nested) instead of %s.", type);
    }

    public Errors duplicateBindingAnnotations(Member member, Class<? extends Annotation> a, Class<? extends Annotation> b) {
        return this.addMessage("%s has more than one annotation annotated with @BindingAnnotation: %s and %s", member, a, b);
    }

    public Errors staticInjectionOnInterface(Class<?> clazz) {
        return this.addMessage("%s is an interface, but interfaces have no static injection points.", clazz);
    }

    public Errors cannotInjectFinalField(Field field) {
        return this.addMessage("Injected field %s cannot be final.", field);
    }

    public Errors cannotInjectAbstractMethod(Method method) {
        return this.addMessage("Injected method %s cannot be abstract.", method);
    }

    public Errors cannotInjectNonVoidMethod(Method method) {
        return this.addMessage("Injected method %s must return void.", method);
    }

    public Errors cannotInjectMethodWithTypeParameters(Method method) {
        return this.addMessage("Injected method %s cannot declare type parameters of its own.", method);
    }

    public Errors duplicateScopeAnnotations(Class<? extends Annotation> a, Class<? extends Annotation> b) {
        return this.addMessage("More than one scope annotation was found: %s and %s.", a, b);
    }

    public Errors recursiveBinding() {
        return this.addMessage("Binding points to itself.", new Object[0]);
    }

    public Errors bindingAlreadySet(Key<?> key, Object source) {
        return this.addMessage("A binding to %s was already configured at %s.", key, Errors.convert(source));
    }

    public Errors jitBindingAlreadySet(Key<?> key) {
        return this.addMessage("A just-in-time binding to %s was already configured on a parent injector.", key);
    }

    public Errors childBindingAlreadySet(Key<?> key, Set<Object> sources) {
        Formatter allSources = new Formatter();
        for (Object source : sources) {
            if (source == null) {
                allSources.format("%n    (bound by a just-in-time binding)", new Object[0]);
                continue;
            }
            allSources.format("%n    bound at %s", source);
        }
        Errors errors = this.addMessage("Unable to create binding for %s. It was already configured on one or more child injectors or private modules%s%n  If it was in a PrivateModule, did you forget to expose the binding?", key, allSources.out());
        return errors;
    }

    public Errors errorCheckingDuplicateBinding(Key<?> key, Object source, Throwable t) {
        return this.addMessage("A binding to %s was already configured at %s and an error was thrown while checking duplicate bindings.  Error: %s", key, Errors.convert(source), t);
    }

    public Errors errorInjectingMethod(Throwable cause) {
        return this.errorInUserCode(cause, "Error injecting method, %s", cause);
    }

    public Errors errorNotifyingTypeListener(TypeListenerBinding listener, TypeLiteral<?> type, Throwable cause) {
        return this.errorInUserCode(cause, "Error notifying TypeListener %s (bound at %s) of %s.%n Reason: %s", listener.getListener(), Errors.convert(listener.getSource()), type, cause);
    }

    public Errors errorInjectingConstructor(Throwable cause) {
        return this.errorInUserCode(cause, "Error injecting constructor, %s", cause);
    }

    public Errors errorInProvider(RuntimeException runtimeException) {
        Throwable unwrapped = this.unwrap(runtimeException);
        return this.errorInUserCode(unwrapped, "Error in custom provider, %s", unwrapped);
    }

    public Errors errorInUserInjector(MembersInjector<?> listener, TypeLiteral<?> type, RuntimeException cause) {
        return this.errorInUserCode(cause, "Error injecting %s using %s.%n Reason: %s", type, listener, cause);
    }

    public Errors errorNotifyingInjectionListener(InjectionListener<?> listener, TypeLiteral<?> type, RuntimeException cause) {
        return this.errorInUserCode(cause, "Error notifying InjectionListener %s of %s.%n Reason: %s", listener, type, cause);
    }

    public Errors exposedButNotBound(Key<?> key) {
        return this.addMessage("Could not expose() %s, it must be explicitly bound.", key);
    }

    public Errors keyNotFullySpecified(TypeLiteral<?> typeLiteral) {
        return this.addMessage("%s cannot be used as a key; It is not fully specified.", typeLiteral);
    }

    public Errors errorEnhancingClass(Class<?> clazz, Throwable cause) {
        return this.errorInUserCode(cause, "Unable to method intercept: %s", clazz);
    }

    public static Collection<Message> getMessagesFromThrowable(Throwable throwable) {
        if (throwable instanceof ProvisionException) {
            return ((ProvisionException)throwable).getErrorMessages();
        }
        if (throwable instanceof ConfigurationException) {
            return ((ConfigurationException)throwable).getErrorMessages();
        }
        if (throwable instanceof CreationException) {
            return ((CreationException)throwable).getErrorMessages();
        }
        return ImmutableSet.of();
    }

    public Errors errorInUserCode(Throwable cause, String messageFormat, Object ... arguments) {
        Collection<Message> messages = Errors.getMessagesFromThrowable(cause);
        if (!messages.isEmpty()) {
            return this.merge(messages);
        }
        return this.addMessage(cause, messageFormat, arguments);
    }

    private Throwable unwrap(RuntimeException runtimeException) {
        if (runtimeException instanceof Exceptions.UnhandledCheckedUserException) {
            return runtimeException.getCause();
        }
        return runtimeException;
    }

    public Errors cannotInjectRawProvider() {
        return this.addMessage("Cannot inject a Provider that has no type parameter", new Object[0]);
    }

    public Errors cannotInjectRawMembersInjector() {
        return this.addMessage("Cannot inject a MembersInjector that has no type parameter", new Object[0]);
    }

    public Errors cannotInjectTypeLiteralOf(Type unsupportedType) {
        return this.addMessage("Cannot inject a TypeLiteral of %s", unsupportedType);
    }

    public Errors cannotInjectRawTypeLiteral() {
        return this.addMessage("Cannot inject a TypeLiteral that has no type parameter", new Object[0]);
    }

    public Errors cannotSatisfyCircularDependency(Class<?> expectedType) {
        return this.addMessage("Tried proxying %s to support a circular dependency, but it is not an interface.", expectedType);
    }

    public Errors circularProxiesDisabled(Class<?> expectedType) {
        return this.addMessage("Tried proxying %s to support a circular dependency, but circular proxies are disabled.", expectedType);
    }

    public void throwCreationExceptionIfErrorsExist() {
        if (!this.hasErrors()) {
            return;
        }
        throw new CreationException(this.getMessages());
    }

    public void throwConfigurationExceptionIfErrorsExist() {
        if (!this.hasErrors()) {
            return;
        }
        throw new ConfigurationException(this.getMessages());
    }

    public void throwProvisionExceptionIfErrorsExist() {
        if (!this.hasErrors()) {
            return;
        }
        throw new ProvisionException(this.getMessages());
    }

    private Message merge(Message message) {
        ArrayList sources = Lists.newArrayList();
        sources.addAll(this.getSources());
        sources.addAll(message.getSources());
        return new Message(sources, message.getMessage(), message.getCause());
    }

    public Errors merge(Collection<Message> messages) {
        for (Message message : messages) {
            this.addMessage(this.merge(message));
        }
        return this;
    }

    public Errors merge(Errors moreErrors) {
        if (moreErrors.root == this.root || moreErrors.root.errors == null) {
            return this;
        }
        this.merge(moreErrors.root.errors);
        return this;
    }

    public List<Object> getSources() {
        ArrayList sources = Lists.newArrayList();
        Errors e = this;
        while (e != null) {
            if (e.source != SourceProvider.UNKNOWN_SOURCE) {
                sources.add(0, e.source);
            }
            e = e.parent;
        }
        return sources;
    }

    public void throwIfNewErrors(int expectedSize) throws ErrorsException {
        if (this.size() == expectedSize) {
            return;
        }
        throw this.toException();
    }

    public ErrorsException toException() {
        return new ErrorsException(this);
    }

    public boolean hasErrors() {
        return this.root.errors != null;
    }

    public Errors addMessage(String messageFormat, Object ... arguments) {
        return this.addMessage(null, messageFormat, arguments);
    }

    private Errors addMessage(Throwable cause, String messageFormat, Object ... arguments) {
        String message = Errors.format(messageFormat, arguments);
        this.addMessage(new Message(this.getSources(), message, cause));
        return this;
    }

    public Errors addMessage(Message message) {
        if (this.root.errors == null) {
            this.root.errors = Lists.newArrayList();
        }
        this.root.errors.add(message);
        return this;
    }

    public static String format(String messageFormat, Object ... arguments) {
        for (int i = 0; i < arguments.length; ++i) {
            arguments[i] = Errors.convert(arguments[i]);
        }
        return String.format(messageFormat, arguments);
    }

    public List<Message> getMessages() {
        if (this.root.errors == null) {
            return ImmutableList.of();
        }
        ArrayList result = Lists.newArrayList(this.root.errors);
        Collections.sort(result, new Comparator<Message>(){

            @Override
            public int compare(Message a, Message b) {
                return a.getSource().compareTo(b.getSource());
            }
        });
        return result;
    }

    public static String format(String heading, Collection<Message> errorMessages) {
        Formatter fmt = new Formatter().format(heading, new Object[0]).format(":%n%n", new Object[0]);
        int index = 1;
        boolean displayCauses = Errors.getOnlyCause(errorMessages) == null;
        for (Message errorMessage : errorMessages) {
            fmt.format("%s) %s%n", index++, errorMessage.getMessage());
            List<Object> dependencies = errorMessage.getSources();
            for (int i = dependencies.size() - 1; i >= 0; --i) {
                Object source = dependencies.get(i);
                Errors.formatSource(fmt, source);
            }
            Throwable cause = errorMessage.getCause();
            if (displayCauses && cause != null) {
                StringWriter writer = new StringWriter();
                cause.printStackTrace(new PrintWriter(writer));
                fmt.format("Caused by: %s", writer.getBuffer());
            }
            fmt.format("%n", new Object[0]);
        }
        if (errorMessages.size() == 1) {
            fmt.format("1 error", new Object[0]);
        } else {
            fmt.format("%s errors", errorMessages.size());
        }
        return fmt.toString();
    }

    public <T> T checkForNull(T value, Object source, Dependency<?> dependency) throws ErrorsException {
        if (value != null || dependency.isNullable()) {
            return value;
        }
        int parameterIndex = dependency.getParameterIndex();
        String parameterName = parameterIndex != -1 ? "parameter " + parameterIndex + " of " : "";
        this.addMessage("null returned by binding at %s%n but %s%s is not @Nullable", source, parameterName, dependency.getInjectionPoint().getMember());
        throw this.toException();
    }

    public static Throwable getOnlyCause(Collection<Message> messages) {
        Throwable onlyCause = null;
        for (Message message : messages) {
            Throwable messageCause = message.getCause();
            if (messageCause == null) continue;
            if (onlyCause != null) {
                return null;
            }
            onlyCause = messageCause;
        }
        return onlyCause;
    }

    public int size() {
        return this.root.errors == null ? 0 : this.root.errors.size();
    }

    public static Object convert(Object o) {
        ElementSource source = null;
        if (o instanceof ElementSource) {
            source = (ElementSource)o;
            o = source.getDeclaringSource();
        }
        return Errors.convert(o, source);
    }

    public static Object convert(Object o, ElementSource source) {
        for (Converter<?> converter : converters) {
            if (!converter.appliesTo(o)) continue;
            return Errors.appendModules(converter.convert(o), source);
        }
        return Errors.appendModules(o, source);
    }

    private static Object appendModules(Object source, ElementSource elementSource) {
        String modules = Errors.moduleSourceString(elementSource);
        if (modules.length() == 0) {
            return source;
        }
        return source + modules;
    }

    private static String moduleSourceString(ElementSource elementSource) {
        if (elementSource == null) {
            return "";
        }
        ArrayList modules = Lists.newArrayList(elementSource.getModuleClassNames());
        while (elementSource.getOriginalElementSource() != null) {
            elementSource = elementSource.getOriginalElementSource();
            modules.addAll(0, elementSource.getModuleClassNames());
        }
        if (modules.size() <= 1) {
            return "";
        }
        StringBuilder builder = new StringBuilder(" (via modules: ");
        for (int i = modules.size() - 1; i >= 0; --i) {
            builder.append((String)modules.get(i));
            if (i == 0) continue;
            builder.append(" -> ");
        }
        builder.append(")");
        return builder.toString();
    }

    public static void formatSource(Formatter formatter, Object source) {
        ElementSource elementSource = null;
        if (source instanceof ElementSource) {
            elementSource = (ElementSource)source;
            source = elementSource.getDeclaringSource();
        }
        Errors.formatSource(formatter, source, elementSource);
    }

    public static void formatSource(Formatter formatter, Object source, ElementSource elementSource) {
        String modules = Errors.moduleSourceString(elementSource);
        if (source instanceof Dependency) {
            Dependency dependency = (Dependency)source;
            InjectionPoint injectionPoint = dependency.getInjectionPoint();
            if (injectionPoint != null) {
                Errors.formatInjectionPoint(formatter, dependency, injectionPoint, elementSource);
            } else {
                Errors.formatSource(formatter, dependency.getKey(), elementSource);
            }
        } else if (source instanceof InjectionPoint) {
            Errors.formatInjectionPoint(formatter, null, (InjectionPoint)source, elementSource);
        } else if (source instanceof Class) {
            formatter.format("  at %s%s%n", StackTraceElements.forType((Class)source), modules);
        } else if (source instanceof Member) {
            formatter.format("  at %s%s%n", StackTraceElements.forMember((Member)source), modules);
        } else if (source instanceof TypeLiteral) {
            formatter.format("  while locating %s%s%n", source, modules);
        } else if (source instanceof Key) {
            Key key = (Key)source;
            formatter.format("  while locating %s%n", Errors.convert(key, elementSource));
        } else {
            formatter.format("  at %s%s%n", source, modules);
        }
    }

    public static void formatInjectionPoint(Formatter formatter, Dependency<?> dependency, InjectionPoint injectionPoint, ElementSource elementSource) {
        Member member = injectionPoint.getMember();
        Class<? extends Member> memberType = Classes.memberType(member);
        if (memberType == Field.class) {
            dependency = injectionPoint.getDependencies().get(0);
            formatter.format("  while locating %s%n", Errors.convert(dependency.getKey(), elementSource));
            formatter.format("    for field at %s%n", StackTraceElements.forMember(member));
        } else if (dependency != null) {
            formatter.format("  while locating %s%n", Errors.convert(dependency.getKey(), elementSource));
            formatter.format("    for parameter %s at %s%n", dependency.getParameterIndex(), StackTraceElements.forMember(member));
        } else {
            Errors.formatSource(formatter, injectionPoint.getMember());
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static abstract class Converter<T> {
        final Class<T> type;

        Converter(Class<T> type) {
            this.type = type;
        }

        boolean appliesTo(Object o) {
            return o != null && this.type.isAssignableFrom(o.getClass());
        }

        String convert(Object o) {
            return this.toString(this.type.cast(o));
        }

        abstract String toString(T var1);
    }
}

