/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.auxiliary;

import java.lang.reflect.Type;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodAccessorFactory;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.auxiliary.AuxiliaryType;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.CompoundList;
import net.bytebuddy.utility.RandomString;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum PrivilegedMemberLookupAction implements AuxiliaryType
{
    FOR_PUBLIC_METHOD("getMethod", "name", String.class, "parameters", Class[].class),
    FOR_DECLARED_METHOD("getDeclaredMethod", "name", String.class, "parameters", Class[].class),
    FOR_PUBLIC_CONSTRUCTOR("getConstructor", "parameters", Class[].class),
    FOR_DECLARED_CONSTRUCTOR("getDeclaredConstructor", "parameters", Class[].class);

    private static final String TYPE_FIELD = "type";
    private static final MethodDescription.InDefinedShape DEFAULT_CONSTRUCTOR;
    private final MethodDescription.InDefinedShape methodDescription;
    private final Map<String, Class<?>> fields;

    private PrivilegedMemberLookupAction(String name, String field, Class<?> type) {
        try {
            this.methodDescription = new MethodDescription.ForLoadedMethod(Class.class.getMethod(name, type));
        }
        catch (NoSuchMethodException exception) {
            throw new IllegalStateException("Could not locate method: " + name, exception);
        }
        this.fields = Collections.singletonMap(field, type);
    }

    private PrivilegedMemberLookupAction(String name, String firstField, Class<?> firstType, String secondField, Class<?> secondType) {
        try {
            this.methodDescription = new MethodDescription.ForLoadedMethod(Class.class.getMethod(name, firstType, secondType));
        }
        catch (NoSuchMethodException exception) {
            throw new IllegalStateException("Could not locate method: " + name, exception);
        }
        this.fields = new LinkedHashMap();
        this.fields.put(firstField, firstType);
        this.fields.put(secondField, secondType);
    }

    public static AuxiliaryType of(MethodDescription methodDescription) {
        if (methodDescription.isConstructor()) {
            return methodDescription.isPublic() ? FOR_PUBLIC_CONSTRUCTOR : FOR_DECLARED_CONSTRUCTOR;
        }
        if (methodDescription.isMethod()) {
            return methodDescription.isPublic() ? FOR_PUBLIC_METHOD : FOR_DECLARED_METHOD;
        }
        throw new IllegalStateException("Cannot load constant for type initializer: " + methodDescription);
    }

    @Override
    public String getSuffix() {
        return RandomString.hashOf(this.name().hashCode());
    }

    @Override
    public DynamicType make(String auxiliaryTypeName, ClassFileVersion classFileVersion, MethodAccessorFactory methodAccessorFactory) {
        Implementation.Composable constructor = MethodCall.invoke(DEFAULT_CONSTRUCTOR).andThen(FieldAccessor.ofField(TYPE_FIELD).setsArgumentAt(0));
        int index = 1;
        for (String field : this.fields.keySet()) {
            constructor = constructor.andThen(FieldAccessor.ofField(field).setsArgumentAt(index++));
        }
        DynamicType.Builder.FieldDefinition.Optional.Valuable builder = new ByteBuddy(classFileVersion).with(TypeValidation.DISABLED).subclass(PrivilegedExceptionAction.class, (ConstructorStrategy)ConstructorStrategy.Default.NO_CONSTRUCTORS).name(auxiliaryTypeName).modifiers(DEFAULT_TYPE_MODIFIER).defineConstructor(Visibility.PUBLIC).withParameters(CompoundList.of(Class.class, new ArrayList(this.fields.values()))).intercept(constructor).method(ElementMatchers.named("run")).intercept(MethodCall.invoke(this.methodDescription).onField(TYPE_FIELD).withField(this.fields.keySet().toArray(new String[0]))).defineField(TYPE_FIELD, (Type)((Object)Class.class), Visibility.PRIVATE);
        for (Map.Entry<String, Class<?>> entry : this.fields.entrySet()) {
            builder = builder.defineField(entry.getKey(), (Type)entry.getValue(), Visibility.PRIVATE);
        }
        return builder.make();
    }

    static {
        DEFAULT_CONSTRUCTOR = (MethodDescription.InDefinedShape)((MethodList)TypeDescription.ForLoadedType.of(Object.class).getDeclaredMethods().filter(ElementMatchers.isConstructor())).getOnly();
    }
}

