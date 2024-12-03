/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.codegen;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.codegen.AttributeNamesConstants;
import org.eclipse.jdt.internal.compiler.codegen.CachedIndexEntry;
import org.eclipse.jdt.internal.compiler.codegen.CharArrayCache;
import org.eclipse.jdt.internal.compiler.codegen.DoubleCache;
import org.eclipse.jdt.internal.compiler.codegen.FloatCache;
import org.eclipse.jdt.internal.compiler.codegen.IntegerCache;
import org.eclipse.jdt.internal.compiler.codegen.LongCache;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
import org.eclipse.jdt.internal.compiler.util.HashtableOfInteger;
import org.eclipse.jdt.internal.compiler.util.HashtableOfObject;
import org.eclipse.jdt.internal.compiler.util.Util;

public class ConstantPool
implements ClassFileConstants,
TypeIds {
    public static final int DOUBLE_INITIAL_SIZE = 5;
    public static final int FLOAT_INITIAL_SIZE = 3;
    public static final int INT_INITIAL_SIZE = 248;
    public static final int LONG_INITIAL_SIZE = 5;
    public static final int UTF8_INITIAL_SIZE = 778;
    public static final int STRING_INITIAL_SIZE = 761;
    public static final int METHODS_AND_FIELDS_INITIAL_SIZE = 450;
    public static final int CLASS_INITIAL_SIZE = 86;
    public static final int NAMEANDTYPE_INITIAL_SIZE = 272;
    public static final int CONSTANTPOOL_INITIAL_SIZE = 2000;
    public static final int CONSTANTPOOL_GROW_SIZE = 6000;
    public static final int DYNAMIC_INITIAL_SIZE = 10;
    protected DoubleCache doubleCache;
    protected FloatCache floatCache;
    protected IntegerCache intCache;
    protected LongCache longCache;
    public CharArrayCache UTF8Cache = new CharArrayCache(778);
    protected CharArrayCache stringCache = new CharArrayCache(761);
    protected HashtableOfObject methodsAndFieldsCache = new HashtableOfObject(450);
    protected CharArrayCache classCache = new CharArrayCache(86);
    protected CharArrayCache moduleCache = new CharArrayCache(5);
    protected CharArrayCache packageCache = new CharArrayCache(5);
    protected HashtableOfObject nameAndTypeCacheForFieldsAndMethods = new HashtableOfObject(272);
    protected HashtableOfInteger dynamicCache = new HashtableOfInteger(10);
    public byte[] poolContent;
    public int currentIndex = 1;
    public int currentOffset;
    public int[] offsets = new int[5];
    public ClassFile classFile;
    public static final char[] Append = "append".toCharArray();
    public static final char[] ARRAY_NEWINSTANCE_NAME = "newInstance".toCharArray();
    public static final char[] ARRAY_NEWINSTANCE_SIGNATURE = "(Ljava/lang/Class;[I)Ljava/lang/Object;".toCharArray();
    public static final char[] ArrayCopy = "arraycopy".toCharArray();
    public static final char[] ArrayCopySignature = "(Ljava/lang/Object;ILjava/lang/Object;II)V".toCharArray();
    public static final char[] ArrayJavaLangClassConstantPoolName = "[Ljava/lang/Class;".toCharArray();
    public static final char[] ArrayJavaLangObjectConstantPoolName = "[Ljava/lang/Object;".toCharArray();
    public static final char[] booleanBooleanSignature = "(Z)Ljava/lang/Boolean;".toCharArray();
    public static final char[] BooleanConstrSignature = "(Z)V".toCharArray();
    public static final char[] BOOLEANVALUE_BOOLEAN_METHOD_NAME = "booleanValue".toCharArray();
    public static final char[] BOOLEANVALUE_BOOLEAN_METHOD_SIGNATURE = "()Z".toCharArray();
    public static final char[] byteByteSignature = "(B)Ljava/lang/Byte;".toCharArray();
    public static final char[] ByteConstrSignature = "(B)V".toCharArray();
    public static final char[] BYTEVALUE_BYTE_METHOD_NAME = "byteValue".toCharArray();
    public static final char[] BYTEVALUE_BYTE_METHOD_SIGNATURE = "()B".toCharArray();
    public static final char[] charCharacterSignature = "(C)Ljava/lang/Character;".toCharArray();
    public static final char[] CharConstrSignature = "(C)V".toCharArray();
    public static final char[] CHARVALUE_CHARACTER_METHOD_NAME = "charValue".toCharArray();
    public static final char[] CHARVALUE_CHARACTER_METHOD_SIGNATURE = "()C".toCharArray();
    public static final char[] Clinit = "<clinit>".toCharArray();
    public static final char[] DefaultConstructorSignature = "()V".toCharArray();
    public static final char[] ClinitSignature = DefaultConstructorSignature;
    public static final char[] Close = "close".toCharArray();
    public static final char[] CloseSignature = "()V".toCharArray();
    public static final char[] DesiredAssertionStatus = "desiredAssertionStatus".toCharArray();
    public static final char[] DesiredAssertionStatusSignature = "()Z".toCharArray();
    public static final char[] DoubleConstrSignature = "(D)V".toCharArray();
    public static final char[] doubleDoubleSignature = "(D)Ljava/lang/Double;".toCharArray();
    public static final char[] DOUBLEVALUE_DOUBLE_METHOD_NAME = "doubleValue".toCharArray();
    public static final char[] DOUBLEVALUE_DOUBLE_METHOD_SIGNATURE = "()D".toCharArray();
    public static final char[] EnumName = "$enum$name".toCharArray();
    public static final char[] EnumOrdinal = "$enum$ordinal".toCharArray();
    public static final char[] Exit = "exit".toCharArray();
    public static final char[] ExitIntSignature = "(I)V".toCharArray();
    public static final char[] FloatConstrSignature = "(F)V".toCharArray();
    public static final char[] floatFloatSignature = "(F)Ljava/lang/Float;".toCharArray();
    public static final char[] FLOATVALUE_FLOAT_METHOD_NAME = "floatValue".toCharArray();
    public static final char[] FLOATVALUE_FLOAT_METHOD_SIGNATURE = "()F".toCharArray();
    public static final char[] ForName = "forName".toCharArray();
    public static final char[] ForNameSignature = "(Ljava/lang/String;)Ljava/lang/Class;".toCharArray();
    public static final char[] GET_BOOLEAN_METHOD_NAME = "getBoolean".toCharArray();
    public static final char[] GET_BOOLEAN_METHOD_SIGNATURE = "(Ljava/lang/Object;)Z".toCharArray();
    public static final char[] GET_BYTE_METHOD_NAME = "getByte".toCharArray();
    public static final char[] GET_BYTE_METHOD_SIGNATURE = "(Ljava/lang/Object;)B".toCharArray();
    public static final char[] GET_CHAR_METHOD_NAME = "getChar".toCharArray();
    public static final char[] GET_CHAR_METHOD_SIGNATURE = "(Ljava/lang/Object;)C".toCharArray();
    public static final char[] GET_DOUBLE_METHOD_NAME = "getDouble".toCharArray();
    public static final char[] GET_DOUBLE_METHOD_SIGNATURE = "(Ljava/lang/Object;)D".toCharArray();
    public static final char[] GET_FLOAT_METHOD_NAME = "getFloat".toCharArray();
    public static final char[] GET_FLOAT_METHOD_SIGNATURE = "(Ljava/lang/Object;)F".toCharArray();
    public static final char[] GET_INT_METHOD_NAME = "getInt".toCharArray();
    public static final char[] GET_INT_METHOD_SIGNATURE = "(Ljava/lang/Object;)I".toCharArray();
    public static final char[] GET_LONG_METHOD_NAME = "getLong".toCharArray();
    public static final char[] GET_LONG_METHOD_SIGNATURE = "(Ljava/lang/Object;)J".toCharArray();
    public static final char[] GET_OBJECT_METHOD_NAME = "get".toCharArray();
    public static final char[] GET_OBJECT_METHOD_SIGNATURE = "(Ljava/lang/Object;)Ljava/lang/Object;".toCharArray();
    public static final char[] GET_SHORT_METHOD_NAME = "getShort".toCharArray();
    public static final char[] GET_SHORT_METHOD_SIGNATURE = "(Ljava/lang/Object;)S".toCharArray();
    public static final char[] GetClass = "getClass".toCharArray();
    public static final char[] GetClassSignature = "()Ljava/lang/Class;".toCharArray();
    public static final char[] GetComponentType = "getComponentType".toCharArray();
    public static final char[] GetComponentTypeSignature = GetClassSignature;
    public static final char[] GetConstructor = "getConstructor".toCharArray();
    public static final char[] GetConstructorSignature = "([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;".toCharArray();
    public static final char[] GETDECLAREDCONSTRUCTOR_NAME = "getDeclaredConstructor".toCharArray();
    public static final char[] GETDECLAREDCONSTRUCTOR_SIGNATURE = "([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;".toCharArray();
    public static final char[] GETDECLAREDFIELD_NAME = "getDeclaredField".toCharArray();
    public static final char[] GETDECLAREDFIELD_SIGNATURE = "(Ljava/lang/String;)Ljava/lang/reflect/Field;".toCharArray();
    public static final char[] GETDECLAREDMETHOD_NAME = "getDeclaredMethod".toCharArray();
    public static final char[] GETDECLAREDMETHOD_SIGNATURE = "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;".toCharArray();
    public static final char[] GetMessage = "getMessage".toCharArray();
    public static final char[] GetMessageSignature = "()Ljava/lang/String;".toCharArray();
    public static final char[] HasNext = "hasNext".toCharArray();
    public static final char[] HasNextSignature = "()Z".toCharArray();
    public static final char[] Init = "<init>".toCharArray();
    public static final char[] IntConstrSignature = "(I)V".toCharArray();
    public static final char[] ITERATOR_NAME = "iterator".toCharArray();
    public static final char[] ITERATOR_SIGNATURE = "()Ljava/util/Iterator;".toCharArray();
    public static final char[] Intern = "intern".toCharArray();
    public static final char[] InternSignature = GetMessageSignature;
    public static final char[] IntIntegerSignature = "(I)Ljava/lang/Integer;".toCharArray();
    public static final char[] INTVALUE_INTEGER_METHOD_NAME = "intValue".toCharArray();
    public static final char[] INTVALUE_INTEGER_METHOD_SIGNATURE = "()I".toCharArray();
    public static final char[] INVOKE_METHOD_METHOD_NAME = "invoke".toCharArray();
    public static final char[] INVOKE_METHOD_METHOD_SIGNATURE = "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;".toCharArray();
    public static final char[][] JAVA_LANG_REFLECT_ACCESSIBLEOBJECT = new char[][]{TypeConstants.JAVA, TypeConstants.LANG, TypeConstants.REFLECT, "AccessibleObject".toCharArray()};
    public static final char[][] JAVA_LANG_REFLECT_ARRAY = new char[][]{TypeConstants.JAVA, TypeConstants.LANG, TypeConstants.REFLECT, "Array".toCharArray()};
    public static final char[] IllegalArgumentExceptionConstructorSignature = "(Ljava/lang/String;)V".toCharArray();
    public static final char[] JavaIoPrintStreamSignature = "Ljava/io/PrintStream;".toCharArray();
    public static final char[] JavaLangAssertionErrorConstantPoolName = "java/lang/AssertionError".toCharArray();
    public static final char[] JavaLangBooleanConstantPoolName = "java/lang/Boolean".toCharArray();
    public static final char[] JavaLangByteConstantPoolName = "java/lang/Byte".toCharArray();
    public static final char[] JavaLangCharacterConstantPoolName = "java/lang/Character".toCharArray();
    public static final char[] JavaLangClassConstantPoolName = "java/lang/Class".toCharArray();
    public static final char[] JavaLangClassNotFoundExceptionConstantPoolName = "java/lang/ClassNotFoundException".toCharArray();
    public static final char[] JavaLangClassSignature = "Ljava/lang/Class;".toCharArray();
    public static final char[] JavaLangDoubleConstantPoolName = "java/lang/Double".toCharArray();
    public static final char[] JavaLangEnumConstantPoolName = "java/lang/Enum".toCharArray();
    public static final char[] JavaLangErrorConstantPoolName = "java/lang/Error".toCharArray();
    public static final char[] JavaLangIncompatibleClassChangeErrorConstantPoolName = "java/lang/IncompatibleClassChangeError".toCharArray();
    public static final char[] JavaLangExceptionConstantPoolName = "java/lang/Exception".toCharArray();
    public static final char[] JavaLangFloatConstantPoolName = "java/lang/Float".toCharArray();
    public static final char[] JavaLangIntegerConstantPoolName = "java/lang/Integer".toCharArray();
    public static final char[] JavaLangLongConstantPoolName = "java/lang/Long".toCharArray();
    public static final char[] JavaLangNoClassDefFoundErrorConstantPoolName = "java/lang/NoClassDefFoundError".toCharArray();
    public static final char[] JavaLangNoSuchFieldErrorConstantPoolName = "java/lang/NoSuchFieldError".toCharArray();
    public static final char[] JavaLangObjectConstantPoolName = "java/lang/Object".toCharArray();
    public static final char[] JAVALANGREFLECTACCESSIBLEOBJECT_CONSTANTPOOLNAME = "java/lang/reflect/AccessibleObject".toCharArray();
    public static final char[] JAVALANGREFLECTARRAY_CONSTANTPOOLNAME = "java/lang/reflect/Array".toCharArray();
    public static final char[] JavaLangReflectConstructorConstantPoolName = "java/lang/reflect/Constructor".toCharArray();
    public static final char[] JavaLangReflectConstructorNewInstanceSignature = "([Ljava/lang/Object;)Ljava/lang/Object;".toCharArray();
    public static final char[] JAVALANGREFLECTFIELD_CONSTANTPOOLNAME = "java/lang/reflect/Field".toCharArray();
    public static final char[] JAVALANGREFLECTMETHOD_CONSTANTPOOLNAME = "java/lang/reflect/Method".toCharArray();
    public static final char[] JavaLangShortConstantPoolName = "java/lang/Short".toCharArray();
    public static final char[] JavaLangStringBufferConstantPoolName = "java/lang/StringBuffer".toCharArray();
    public static final char[] JavaLangStringBuilderConstantPoolName = "java/lang/StringBuilder".toCharArray();
    public static final char[] JavaLangStringConstantPoolName = "java/lang/String".toCharArray();
    public static final char[] JavaLangStringSignature = "Ljava/lang/String;".toCharArray();
    public static final char[] JavaLangObjectSignature = "Ljava/lang/Object;".toCharArray();
    public static final char[] JavaLangSystemConstantPoolName = "java/lang/System".toCharArray();
    public static final char[] JavaLangThrowableConstantPoolName = "java/lang/Throwable".toCharArray();
    public static final char[] JavaLangIllegalArgumentExceptionConstantPoolName = "java/lang/IllegalArgumentException".toCharArray();
    public static final char[] JavaLangVoidConstantPoolName = "java/lang/Void".toCharArray();
    public static final char[] JavaUtilIteratorConstantPoolName = "java/util/Iterator".toCharArray();
    public static final char[] LongConstrSignature = "(J)V".toCharArray();
    public static final char[] longLongSignature = "(J)Ljava/lang/Long;".toCharArray();
    public static final char[] LONGVALUE_LONG_METHOD_NAME = "longValue".toCharArray();
    public static final char[] LONGVALUE_LONG_METHOD_SIGNATURE = "()J".toCharArray();
    public static final char[] Name = "name".toCharArray();
    public static final char[] NewInstance = "newInstance".toCharArray();
    public static final char[] NewInstanceSignature = "(Ljava/lang/Class;[I)Ljava/lang/Object;".toCharArray();
    public static final char[] Next = "next".toCharArray();
    public static final char[] NextSignature = "()Ljava/lang/Object;".toCharArray();
    public static final char[] ObjectConstrSignature = "(Ljava/lang/Object;)V".toCharArray();
    public static final char[] ObjectSignature = "Ljava/lang/Object;".toCharArray();
    public static final char[] Ordinal = "ordinal".toCharArray();
    public static final char[] OrdinalSignature = "()I".toCharArray();
    public static final char[] Out = "out".toCharArray();
    public static final char[] SET_BOOLEAN_METHOD_NAME = "setBoolean".toCharArray();
    public static final char[] SET_BOOLEAN_METHOD_SIGNATURE = "(Ljava/lang/Object;Z)V".toCharArray();
    public static final char[] SET_BYTE_METHOD_NAME = "setByte".toCharArray();
    public static final char[] SET_BYTE_METHOD_SIGNATURE = "(Ljava/lang/Object;B)V".toCharArray();
    public static final char[] SET_CHAR_METHOD_NAME = "setChar".toCharArray();
    public static final char[] SET_CHAR_METHOD_SIGNATURE = "(Ljava/lang/Object;C)V".toCharArray();
    public static final char[] SET_DOUBLE_METHOD_NAME = "setDouble".toCharArray();
    public static final char[] SET_DOUBLE_METHOD_SIGNATURE = "(Ljava/lang/Object;D)V".toCharArray();
    public static final char[] SET_FLOAT_METHOD_NAME = "setFloat".toCharArray();
    public static final char[] SET_FLOAT_METHOD_SIGNATURE = "(Ljava/lang/Object;F)V".toCharArray();
    public static final char[] SET_INT_METHOD_NAME = "setInt".toCharArray();
    public static final char[] SET_INT_METHOD_SIGNATURE = "(Ljava/lang/Object;I)V".toCharArray();
    public static final char[] SET_LONG_METHOD_NAME = "setLong".toCharArray();
    public static final char[] SET_LONG_METHOD_SIGNATURE = "(Ljava/lang/Object;J)V".toCharArray();
    public static final char[] SET_OBJECT_METHOD_NAME = "set".toCharArray();
    public static final char[] SET_OBJECT_METHOD_SIGNATURE = "(Ljava/lang/Object;Ljava/lang/Object;)V".toCharArray();
    public static final char[] SET_SHORT_METHOD_NAME = "setShort".toCharArray();
    public static final char[] SET_SHORT_METHOD_SIGNATURE = "(Ljava/lang/Object;S)V".toCharArray();
    public static final char[] SETACCESSIBLE_NAME = "setAccessible".toCharArray();
    public static final char[] SETACCESSIBLE_SIGNATURE = "(Z)V".toCharArray();
    public static final char[] ShortConstrSignature = "(S)V".toCharArray();
    public static final char[] shortShortSignature = "(S)Ljava/lang/Short;".toCharArray();
    public static final char[] SHORTVALUE_SHORT_METHOD_NAME = "shortValue".toCharArray();
    public static final char[] SHORTVALUE_SHORT_METHOD_SIGNATURE = "()S".toCharArray();
    public static final char[] StringBufferAppendBooleanSignature = "(Z)Ljava/lang/StringBuffer;".toCharArray();
    public static final char[] StringBufferAppendCharSignature = "(C)Ljava/lang/StringBuffer;".toCharArray();
    public static final char[] StringBufferAppendDoubleSignature = "(D)Ljava/lang/StringBuffer;".toCharArray();
    public static final char[] StringBufferAppendFloatSignature = "(F)Ljava/lang/StringBuffer;".toCharArray();
    public static final char[] StringBufferAppendIntSignature = "(I)Ljava/lang/StringBuffer;".toCharArray();
    public static final char[] StringBufferAppendLongSignature = "(J)Ljava/lang/StringBuffer;".toCharArray();
    public static final char[] StringBufferAppendObjectSignature = "(Ljava/lang/Object;)Ljava/lang/StringBuffer;".toCharArray();
    public static final char[] StringBufferAppendStringSignature = "(Ljava/lang/String;)Ljava/lang/StringBuffer;".toCharArray();
    public static final char[] StringBuilderAppendBooleanSignature = "(Z)Ljava/lang/StringBuilder;".toCharArray();
    public static final char[] StringBuilderAppendCharSignature = "(C)Ljava/lang/StringBuilder;".toCharArray();
    public static final char[] StringBuilderAppendDoubleSignature = "(D)Ljava/lang/StringBuilder;".toCharArray();
    public static final char[] StringBuilderAppendFloatSignature = "(F)Ljava/lang/StringBuilder;".toCharArray();
    public static final char[] StringBuilderAppendIntSignature = "(I)Ljava/lang/StringBuilder;".toCharArray();
    public static final char[] StringBuilderAppendLongSignature = "(J)Ljava/lang/StringBuilder;".toCharArray();
    public static final char[] StringBuilderAppendObjectSignature = "(Ljava/lang/Object;)Ljava/lang/StringBuilder;".toCharArray();
    public static final char[] StringBuilderAppendStringSignature = "(Ljava/lang/String;)Ljava/lang/StringBuilder;".toCharArray();
    public static final char[] StringConstructorSignature = "(Ljava/lang/String;)V".toCharArray();
    public static final char[] This = "this".toCharArray();
    public static final char[] ToString = "toString".toCharArray();
    public static final char[] ToStringSignature = GetMessageSignature;
    public static final char[] TYPE = "TYPE".toCharArray();
    public static final char[] ValueOf = "valueOf".toCharArray();
    public static final char[] ValueOfBooleanSignature = "(Z)Ljava/lang/String;".toCharArray();
    public static final char[] ValueOfCharSignature = "(C)Ljava/lang/String;".toCharArray();
    public static final char[] ValueOfDoubleSignature = "(D)Ljava/lang/String;".toCharArray();
    public static final char[] ValueOfFloatSignature = "(F)Ljava/lang/String;".toCharArray();
    public static final char[] ValueOfIntSignature = "(I)Ljava/lang/String;".toCharArray();
    public static final char[] ValueOfLongSignature = "(J)Ljava/lang/String;".toCharArray();
    public static final char[] ValueOfObjectSignature = "(Ljava/lang/Object;)Ljava/lang/String;".toCharArray();
    public static final char[] ValueOfStringClassSignature = "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;".toCharArray();
    public static final char[] JAVA_LANG_ANNOTATION_DOCUMENTED = "Ljava/lang/annotation/Documented;".toCharArray();
    public static final char[] JAVA_LANG_ANNOTATION_ELEMENTTYPE = "Ljava/lang/annotation/ElementType;".toCharArray();
    public static final char[] JAVA_LANG_ANNOTATION_RETENTION = "Ljava/lang/annotation/Retention;".toCharArray();
    public static final char[] JAVA_LANG_ANNOTATION_RETENTIONPOLICY = "Ljava/lang/annotation/RetentionPolicy;".toCharArray();
    public static final char[] JAVA_LANG_ANNOTATION_TARGET = "Ljava/lang/annotation/Target;".toCharArray();
    public static final char[] JAVA_LANG_DEPRECATED = "Ljava/lang/Deprecated;".toCharArray();
    public static final char[] JAVA_LANG_ANNOTATION_INHERITED = "Ljava/lang/annotation/Inherited;".toCharArray();
    public static final char[] JAVA_LANG_SAFEVARARGS = "Ljava/lang/SafeVarargs;".toCharArray();
    public static final char[] JAVA_LANG_INVOKE_METHODHANDLE_POLYMORPHICSIGNATURE = "Ljava/lang/invoke/MethodHandle$PolymorphicSignature;".toCharArray();
    public static final char[] METAFACTORY = "metafactory".toCharArray();
    public static final char[] JAVA_LANG_INVOKE_LAMBDAMETAFACTORY_METAFACTORY_SIGNATURE = "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;".toCharArray();
    public static final char[] ALTMETAFACTORY = "altMetafactory".toCharArray();
    public static final char[] JAVA_LANG_INVOKE_LAMBDAMETAFACTORY_ALTMETAFACTORY_SIGNATURE = "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;".toCharArray();
    public static final char[] JavaLangInvokeSerializedLambda = "Ljava/lang/invoke/SerializedLambda;".toCharArray();
    public static final char[] JavaLangInvokeSerializedLambdaConstantPoolName = "java/lang/invoke/SerializedLambda".toCharArray();
    public static final char[] GetImplMethodName = "getImplMethodName".toCharArray();
    public static final char[] GetImplMethodNameSignature = "()Ljava/lang/String;".toCharArray();
    public static final char[] GetImplMethodKind = "getImplMethodKind".toCharArray();
    public static final char[] GetImplMethodKindSignature = "()I".toCharArray();
    public static final char[] GetFunctionalInterfaceClass = "getFunctionalInterfaceClass".toCharArray();
    public static final char[] GetFunctionalInterfaceClassSignature = "()Ljava/lang/String;".toCharArray();
    public static final char[] GetFunctionalInterfaceMethodName = "getFunctionalInterfaceMethodName".toCharArray();
    public static final char[] GetFunctionalInterfaceMethodNameSignature = "()Ljava/lang/String;".toCharArray();
    public static final char[] GetFunctionalInterfaceMethodSignature = "getFunctionalInterfaceMethodSignature".toCharArray();
    public static final char[] GetFunctionalInterfaceMethodSignatureSignature = "()Ljava/lang/String;".toCharArray();
    public static final char[] GetImplClass = "getImplClass".toCharArray();
    public static final char[] GetImplClassSignature = "()Ljava/lang/String;".toCharArray();
    public static final char[] GetImplMethodSignature = "getImplMethodSignature".toCharArray();
    public static final char[] GetImplMethodSignatureSignature = "()Ljava/lang/String;".toCharArray();
    public static final char[] GetCapturedArg = "getCapturedArg".toCharArray();
    public static final char[] GetCapturedArgSignature = "(I)Ljava/lang/Object;".toCharArray();
    public static final char[] JAVA_LANG_ANNOTATION_REPEATABLE = "Ljava/lang/annotation/Repeatable;".toCharArray();
    public static final char[] HashCode = "hashCode".toCharArray();
    public static final char[] HashCodeSignature = "()I".toCharArray();
    public static final char[] Equals = "equals".toCharArray();
    public static final char[] EqualsSignature = "(Ljava/lang/Object;)Z".toCharArray();
    public static final char[] AddSuppressed = "addSuppressed".toCharArray();
    public static final char[] AddSuppressedSignature = "(Ljava/lang/Throwable;)V".toCharArray();
    public static final char[] Clone = "clone".toCharArray();
    public static final char[] CloneSignature = "()Ljava/lang/Object;".toCharArray();
    public static final char[] BOOTSTRAP = "bootstrap".toCharArray();
    public static final char[] JAVA_LANG_RUNTIME_OBJECTMETHOD_BOOTSTRAP_SIGNATURE = "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/TypeDescriptor;Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/invoke/MethodHandle;)Ljava/lang/Object;".toCharArray();
    public static final char[] JDK_INTERNAL_PREVIEW_FEATURE = "Ljdk/internal/PreviewFeature;".toCharArray();

    public ConstantPool(ClassFile classFile) {
        this.initialize(classFile);
    }

    public void initialize(ClassFile givenClassFile) {
        this.poolContent = givenClassFile.header;
        this.currentOffset = givenClassFile.headerOffset;
        this.currentIndex = 1;
        this.classFile = givenClassFile;
    }

    public byte[] dumpBytes() {
        this.poolContent = new byte[this.currentOffset];
        System.arraycopy(this.poolContent, 0, this.poolContent, 0, this.currentOffset);
        return this.poolContent;
    }

    public int literalIndex(byte[] utf8encoding, char[] stringCharArray) {
        int index = this.UTF8Cache.putIfAbsent(stringCharArray, this.currentIndex);
        if (index < 0) {
            if ((index = -index) > 65535) {
                this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
            }
            ++this.currentIndex;
            int length = this.offsets.length;
            if (length <= index) {
                this.offsets = new int[index * 2];
                System.arraycopy(this.offsets, 0, this.offsets, 0, length);
            }
            this.offsets[index] = this.currentOffset;
            this.writeU1(1);
            int utf8encodingLength = utf8encoding.length;
            if (this.currentOffset + 2 + utf8encodingLength >= this.poolContent.length) {
                this.resizePoolContents(2 + utf8encodingLength);
            }
            this.poolContent[this.currentOffset++] = (byte)(utf8encodingLength >> 8);
            this.poolContent[this.currentOffset++] = (byte)utf8encodingLength;
            System.arraycopy(utf8encoding, 0, this.poolContent, this.currentOffset, utf8encodingLength);
            this.currentOffset += utf8encodingLength;
        }
        return index;
    }

    public int literalIndex(TypeBinding binding) {
        TypeBinding typeBinding = binding.leafComponentType();
        if ((typeBinding.tagBits & 0x800L) != 0L) {
            Util.recordNestedType(this.classFile, typeBinding);
        }
        return this.literalIndex(binding.signature());
    }

    public int literalIndex(char[] utf8Constant) {
        int index = this.UTF8Cache.putIfAbsent(utf8Constant, this.currentIndex);
        if (index < 0) {
            int length;
            if ((index = -index) > 65535) {
                this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
            }
            if ((length = this.offsets.length) <= index) {
                this.offsets = new int[index * 2];
                System.arraycopy(this.offsets, 0, this.offsets, 0, length);
            }
            this.offsets[index] = this.currentOffset;
            this.writeU1(1);
            int savedCurrentOffset = this.currentOffset;
            if (this.currentOffset + 2 >= this.poolContent.length) {
                this.resizePoolContents(2);
            }
            this.currentOffset += 2;
            length = 0;
            int i = 0;
            while (i < utf8Constant.length) {
                char current = utf8Constant[i];
                if (current >= '\u0001' && current <= '\u007f') {
                    this.writeU1(current);
                    ++length;
                } else if (current > '\u07ff') {
                    length += 3;
                    this.writeU1(0xE0 | current >> 12 & 0xF);
                    this.writeU1(0x80 | current >> 6 & 0x3F);
                    this.writeU1(0x80 | current & 0x3F);
                } else {
                    length += 2;
                    this.writeU1(0xC0 | current >> 6 & 0x1F);
                    this.writeU1(0x80 | current & 0x3F);
                }
                ++i;
            }
            if (length >= 65535) {
                this.currentOffset = savedCurrentOffset - 1;
                this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceForConstant(this.classFile.referenceBinding.scope.referenceType());
            }
            if (index > 65535) {
                this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
            }
            ++this.currentIndex;
            this.poolContent[savedCurrentOffset] = (byte)(length >> 8);
            this.poolContent[savedCurrentOffset + 1] = (byte)length;
        }
        return index;
    }

    public int literalIndex(char[] stringCharArray, byte[] utf8encoding) {
        int index = this.stringCache.putIfAbsent(stringCharArray, this.currentIndex);
        if (index < 0) {
            int length;
            ++this.currentIndex;
            if ((index = -index) > 65535) {
                this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
            }
            if ((length = this.offsets.length) <= index) {
                this.offsets = new int[index * 2];
                System.arraycopy(this.offsets, 0, this.offsets, 0, length);
            }
            this.offsets[index] = this.currentOffset;
            this.writeU1(8);
            int stringIndexOffset = this.currentOffset;
            if (this.currentOffset + 2 >= this.poolContent.length) {
                this.resizePoolContents(2);
            }
            this.currentOffset += 2;
            int stringIndex = this.literalIndex(utf8encoding, stringCharArray);
            this.poolContent[stringIndexOffset++] = (byte)(stringIndex >> 8);
            this.poolContent[stringIndexOffset] = (byte)stringIndex;
        }
        return index;
    }

    public int literalIndex(double key) {
        int index;
        if (this.doubleCache == null) {
            this.doubleCache = new DoubleCache(5);
        }
        if ((index = this.doubleCache.putIfAbsent(key, this.currentIndex)) < 0) {
            if ((index = -index) > 65535) {
                this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
            }
            this.currentIndex += 2;
            int length = this.offsets.length;
            if (length <= index) {
                this.offsets = new int[index * 2];
                System.arraycopy(this.offsets, 0, this.offsets, 0, length);
            }
            this.offsets[index] = this.currentOffset;
            this.writeU1(6);
            long temp = Double.doubleToLongBits(key);
            length = this.poolContent.length;
            if (this.currentOffset + 8 >= length) {
                this.resizePoolContents(8);
            }
            this.poolContent[this.currentOffset++] = (byte)(temp >>> 56);
            this.poolContent[this.currentOffset++] = (byte)(temp >>> 48);
            this.poolContent[this.currentOffset++] = (byte)(temp >>> 40);
            this.poolContent[this.currentOffset++] = (byte)(temp >>> 32);
            this.poolContent[this.currentOffset++] = (byte)(temp >>> 24);
            this.poolContent[this.currentOffset++] = (byte)(temp >>> 16);
            this.poolContent[this.currentOffset++] = (byte)(temp >>> 8);
            this.poolContent[this.currentOffset++] = (byte)temp;
        }
        return index;
    }

    public int literalIndex(float key) {
        int index;
        if (this.floatCache == null) {
            this.floatCache = new FloatCache(3);
        }
        if ((index = this.floatCache.putIfAbsent(key, this.currentIndex)) < 0) {
            if ((index = -index) > 65535) {
                this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
            }
            ++this.currentIndex;
            int length = this.offsets.length;
            if (length <= index) {
                this.offsets = new int[index * 2];
                System.arraycopy(this.offsets, 0, this.offsets, 0, length);
            }
            this.offsets[index] = this.currentOffset;
            this.writeU1(4);
            int temp = Float.floatToIntBits(key);
            if (this.currentOffset + 4 >= this.poolContent.length) {
                this.resizePoolContents(4);
            }
            this.poolContent[this.currentOffset++] = (byte)(temp >>> 24);
            this.poolContent[this.currentOffset++] = (byte)(temp >>> 16);
            this.poolContent[this.currentOffset++] = (byte)(temp >>> 8);
            this.poolContent[this.currentOffset++] = (byte)temp;
        }
        return index;
    }

    public int literalIndex(int key) {
        int index;
        if (this.intCache == null) {
            this.intCache = new IntegerCache(248);
        }
        if ((index = this.intCache.putIfAbsent(key, this.currentIndex)) < 0) {
            int length;
            ++this.currentIndex;
            if ((index = -index) > 65535) {
                this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
            }
            if ((length = this.offsets.length) <= index) {
                this.offsets = new int[index * 2];
                System.arraycopy(this.offsets, 0, this.offsets, 0, length);
            }
            this.offsets[index] = this.currentOffset;
            this.writeU1(3);
            if (this.currentOffset + 4 >= this.poolContent.length) {
                this.resizePoolContents(4);
            }
            this.poolContent[this.currentOffset++] = (byte)(key >>> 24);
            this.poolContent[this.currentOffset++] = (byte)(key >>> 16);
            this.poolContent[this.currentOffset++] = (byte)(key >>> 8);
            this.poolContent[this.currentOffset++] = (byte)key;
        }
        return index;
    }

    public int literalIndex(long key) {
        int index;
        if (this.longCache == null) {
            this.longCache = new LongCache(5);
        }
        if ((index = this.longCache.putIfAbsent(key, this.currentIndex)) < 0) {
            if ((index = -index) > 65535) {
                this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
            }
            this.currentIndex += 2;
            int length = this.offsets.length;
            if (length <= index) {
                this.offsets = new int[index * 2];
                System.arraycopy(this.offsets, 0, this.offsets, 0, length);
            }
            this.offsets[index] = this.currentOffset;
            this.writeU1(5);
            if (this.currentOffset + 8 >= this.poolContent.length) {
                this.resizePoolContents(8);
            }
            this.poolContent[this.currentOffset++] = (byte)(key >>> 56);
            this.poolContent[this.currentOffset++] = (byte)(key >>> 48);
            this.poolContent[this.currentOffset++] = (byte)(key >>> 40);
            this.poolContent[this.currentOffset++] = (byte)(key >>> 32);
            this.poolContent[this.currentOffset++] = (byte)(key >>> 24);
            this.poolContent[this.currentOffset++] = (byte)(key >>> 16);
            this.poolContent[this.currentOffset++] = (byte)(key >>> 8);
            this.poolContent[this.currentOffset++] = (byte)key;
        }
        return index;
    }

    public int literalIndex(String stringConstant) {
        char[] stringCharArray = stringConstant.toCharArray();
        int index = this.stringCache.putIfAbsent(stringCharArray, this.currentIndex);
        if (index < 0) {
            int length;
            ++this.currentIndex;
            if ((index = -index) > 65535) {
                this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
            }
            if ((length = this.offsets.length) <= index) {
                this.offsets = new int[index * 2];
                System.arraycopy(this.offsets, 0, this.offsets, 0, length);
            }
            this.offsets[index] = this.currentOffset;
            this.writeU1(8);
            int stringIndexOffset = this.currentOffset;
            if (this.currentOffset + 2 >= this.poolContent.length) {
                this.resizePoolContents(2);
            }
            this.currentOffset += 2;
            int stringIndex = this.literalIndex(stringCharArray);
            this.poolContent[stringIndexOffset++] = (byte)(stringIndex >> 8);
            this.poolContent[stringIndexOffset] = (byte)stringIndex;
        }
        return index;
    }

    public int literalIndexForModule(char[] moduleName) {
        int index = this.moduleCache.putIfAbsent(moduleName, this.currentIndex);
        if (index < 0) {
            int length;
            ++this.currentIndex;
            if ((index = -index) > 65535) {
                this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
            }
            if ((length = this.offsets.length) <= index) {
                this.offsets = new int[index * 2];
                System.arraycopy(this.offsets, 0, this.offsets, 0, length);
            }
            this.offsets[index] = this.currentOffset;
            this.writeU1(19);
            int stringIndexOffset = this.currentOffset;
            if (this.currentOffset + 2 >= this.poolContent.length) {
                this.resizePoolContents(2);
            }
            this.currentOffset += 2;
            int stringIndex = this.literalIndex(moduleName);
            this.poolContent[stringIndexOffset++] = (byte)(stringIndex >> 8);
            this.poolContent[stringIndexOffset] = (byte)stringIndex;
        }
        return index;
    }

    public int literalIndexForPackage(char[] packageName) {
        int index = this.packageCache.putIfAbsent(packageName, this.currentIndex);
        if (index < 0) {
            int length;
            ++this.currentIndex;
            if ((index = -index) > 65535) {
                this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
            }
            if ((length = this.offsets.length) <= index) {
                this.offsets = new int[index * 2];
                System.arraycopy(this.offsets, 0, this.offsets, 0, length);
            }
            this.offsets[index] = this.currentOffset;
            this.writeU1(20);
            int stringIndexOffset = this.currentOffset;
            if (this.currentOffset + 2 >= this.poolContent.length) {
                this.resizePoolContents(2);
            }
            this.currentOffset += 2;
            int stringIndex = this.literalIndex(packageName);
            this.poolContent[stringIndexOffset++] = (byte)(stringIndex >> 8);
            this.poolContent[stringIndexOffset] = (byte)stringIndex;
        }
        return index;
    }

    public int literalIndexForType(char[] constantPoolName) {
        int index = this.classCache.putIfAbsent(constantPoolName, this.currentIndex);
        if (index < 0) {
            int length;
            ++this.currentIndex;
            if ((index = -index) > 65535) {
                this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
            }
            if ((length = this.offsets.length) <= index) {
                this.offsets = new int[index * 2];
                System.arraycopy(this.offsets, 0, this.offsets, 0, length);
            }
            this.offsets[index] = this.currentOffset;
            this.writeU1(7);
            int nameIndexOffset = this.currentOffset;
            if (this.currentOffset + 2 >= this.poolContent.length) {
                this.resizePoolContents(2);
            }
            this.currentOffset += 2;
            int nameIndex = this.literalIndex(constantPoolName);
            this.poolContent[nameIndexOffset++] = (byte)(nameIndex >> 8);
            this.poolContent[nameIndexOffset] = (byte)nameIndex;
        }
        return index;
    }

    public int literalIndexForType(TypeBinding binding) {
        TypeBinding typeBinding = binding.leafComponentType();
        if ((typeBinding.tagBits & 0x800L) != 0L) {
            Util.recordNestedType(this.classFile, typeBinding);
        }
        return this.literalIndexForType(binding.constantPoolName());
    }

    public int literalIndexForMethod(char[] declaringClass, char[] selector, char[] signature, boolean isInterface) {
        int index = this.putInCacheIfAbsent(declaringClass, selector, signature, this.currentIndex);
        if (index < 0) {
            int length;
            ++this.currentIndex;
            if ((index = -index) > 65535) {
                this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
            }
            if ((length = this.offsets.length) <= index) {
                this.offsets = new int[index * 2];
                System.arraycopy(this.offsets, 0, this.offsets, 0, length);
            }
            this.offsets[index] = this.currentOffset;
            this.writeU1(isInterface ? 11 : 10);
            int classIndexOffset = this.currentOffset;
            if (this.currentOffset + 4 >= this.poolContent.length) {
                this.resizePoolContents(4);
            }
            this.currentOffset += 4;
            int classIndex = this.literalIndexForType(declaringClass);
            int nameAndTypeIndex = this.literalIndexForNameAndType(selector, signature);
            this.poolContent[classIndexOffset++] = (byte)(classIndex >> 8);
            this.poolContent[classIndexOffset++] = (byte)classIndex;
            this.poolContent[classIndexOffset++] = (byte)(nameAndTypeIndex >> 8);
            this.poolContent[classIndexOffset] = (byte)nameAndTypeIndex;
        }
        return index;
    }

    public int literalIndexForMethod(TypeBinding declaringClass, char[] selector, char[] signature, boolean isInterface) {
        if ((declaringClass.tagBits & 0x800L) != 0L) {
            Util.recordNestedType(this.classFile, declaringClass);
        }
        return this.literalIndexForMethod(declaringClass.constantPoolName(), selector, signature, isInterface);
    }

    public int literalIndexForNameAndType(char[] name, char[] signature) {
        int index = this.putInNameAndTypeCacheIfAbsent(name, signature, this.currentIndex);
        if (index < 0) {
            int length;
            ++this.currentIndex;
            if ((index = -index) > 65535) {
                this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
            }
            if ((length = this.offsets.length) <= index) {
                this.offsets = new int[index * 2];
                System.arraycopy(this.offsets, 0, this.offsets, 0, length);
            }
            this.offsets[index] = this.currentOffset;
            this.writeU1(12);
            int nameIndexOffset = this.currentOffset;
            if (this.currentOffset + 4 >= this.poolContent.length) {
                this.resizePoolContents(4);
            }
            this.currentOffset += 4;
            int nameIndex = this.literalIndex(name);
            int typeIndex = this.literalIndex(signature);
            this.poolContent[nameIndexOffset++] = (byte)(nameIndex >> 8);
            this.poolContent[nameIndexOffset++] = (byte)nameIndex;
            this.poolContent[nameIndexOffset++] = (byte)(typeIndex >> 8);
            this.poolContent[nameIndexOffset] = (byte)typeIndex;
        }
        return index;
    }

    public int literalIndexForMethodHandle(MethodBinding binding) {
        boolean isInterface = binding.declaringClass.isInterface();
        int referenceKind = isInterface ? (binding.isStatic() ? 6 : (binding.isPrivate() ? 7 : 9)) : (binding.isConstructor() ? 8 : (binding.isStatic() ? 6 : (binding.isPrivate() ? 7 : 5)));
        return this.literalIndexForMethodHandle(referenceKind, binding.declaringClass, binding.selector, binding.signature(), isInterface);
    }

    public int literalIndexForMethodHandle(int referenceKind, TypeBinding declaringClass, char[] selector, char[] signature, boolean isInterface) {
        int index;
        int indexForMethod = this.literalIndexForMethod(declaringClass, selector, signature, isInterface);
        int length = this.offsets.length;
        if (length <= (index = this.currentIndex++)) {
            this.offsets = new int[index * 2];
            System.arraycopy(this.offsets, 0, this.offsets, 0, length);
        }
        this.offsets[index] = this.currentOffset;
        this.writeU1(15);
        this.writeU1(referenceKind);
        this.writeU2(indexForMethod);
        return index;
    }

    public int literalIndexForMethodHandleFieldRef(int referenceKind, char[] declaringClass, char[] name, char[] signature) {
        int index;
        assert (referenceKind == 1);
        int indexForField = this.literalIndexForField(declaringClass, name, signature);
        int length = this.offsets.length;
        if (length <= (index = this.currentIndex++)) {
            this.offsets = new int[index * 2];
            System.arraycopy(this.offsets, 0, this.offsets, 0, length);
        }
        this.offsets[index] = this.currentOffset;
        this.writeU1(15);
        this.writeU1(referenceKind);
        this.writeU2(indexForField);
        return index;
    }

    public int literalIndexForMethodType(char[] descriptor) {
        int index;
        int signatureIndex = this.literalIndex(descriptor);
        int length = this.offsets.length;
        if (length <= (index = this.currentIndex++)) {
            this.offsets = new int[index * 2];
            System.arraycopy(this.offsets, 0, this.offsets, 0, length);
        }
        this.offsets[index] = this.currentOffset;
        this.writeU1(16);
        this.writeU2(signatureIndex);
        return index;
    }

    private int literalIndexForInvokeAndConstantDynamic(int bootStrapIndex, char[] selector, char[] descriptor, int tag) {
        int index = this.putInDynamicCacheIfAbsent(bootStrapIndex, selector, descriptor, this.currentIndex);
        if (index < 0) {
            int length;
            ++this.currentIndex;
            if ((index = -index) > 65535) {
                this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
            }
            if ((length = this.offsets.length) <= index) {
                this.offsets = new int[index * 2];
                System.arraycopy(this.offsets, 0, this.offsets, 0, length);
            }
            this.offsets[index] = this.currentOffset;
            this.writeU1(tag);
            int classIndexOffset = this.currentOffset;
            if (this.currentOffset + 4 >= this.poolContent.length) {
                this.resizePoolContents(4);
            }
            this.currentOffset += 4;
            int nameAndTypeIndex = this.literalIndexForNameAndType(selector, descriptor);
            this.poolContent[classIndexOffset++] = (byte)(bootStrapIndex >> 8);
            this.poolContent[classIndexOffset++] = (byte)bootStrapIndex;
            this.poolContent[classIndexOffset++] = (byte)(nameAndTypeIndex >> 8);
            this.poolContent[classIndexOffset] = (byte)nameAndTypeIndex;
        }
        return index;
    }

    public int literalIndexForDynamic(int bootStrapIndex, char[] selector, char[] descriptor) {
        return this.literalIndexForInvokeAndConstantDynamic(bootStrapIndex, selector, descriptor, 17);
    }

    public int literalIndexForInvokeDynamic(int bootStrapIndex, char[] selector, char[] descriptor) {
        return this.literalIndexForInvokeAndConstantDynamic(bootStrapIndex, selector, descriptor, 18);
    }

    public int literalIndexForField(char[] declaringClass, char[] name, char[] signature) {
        int index = this.putInCacheIfAbsent(declaringClass, name, signature, this.currentIndex);
        if (index < 0) {
            int length;
            ++this.currentIndex;
            if ((index = -index) > 65535) {
                this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
            }
            if ((length = this.offsets.length) <= index) {
                this.offsets = new int[index * 2];
                System.arraycopy(this.offsets, 0, this.offsets, 0, length);
            }
            this.offsets[index] = this.currentOffset;
            this.writeU1(9);
            int classIndexOffset = this.currentOffset;
            if (this.currentOffset + 4 >= this.poolContent.length) {
                this.resizePoolContents(4);
            }
            this.currentOffset += 4;
            int classIndex = this.literalIndexForType(declaringClass);
            int nameAndTypeIndex = this.literalIndexForNameAndType(name, signature);
            this.poolContent[classIndexOffset++] = (byte)(classIndex >> 8);
            this.poolContent[classIndexOffset++] = (byte)classIndex;
            this.poolContent[classIndexOffset++] = (byte)(nameAndTypeIndex >> 8);
            this.poolContent[classIndexOffset] = (byte)nameAndTypeIndex;
        }
        return index;
    }

    public int literalIndexForLdc(char[] stringCharArray) {
        int savedCurrentIndex = this.currentIndex;
        int savedCurrentOffset = this.currentOffset;
        int index = this.stringCache.putIfAbsent(stringCharArray, this.currentIndex);
        if (index < 0) {
            if ((index = -index) > 65535) {
                this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
            }
            ++this.currentIndex;
            int length = this.offsets.length;
            if (length <= index) {
                this.offsets = new int[index * 2];
                System.arraycopy(this.offsets, 0, this.offsets, 0, length);
            }
            this.offsets[index] = this.currentOffset;
            this.writeU1(8);
            int stringIndexOffset = this.currentOffset;
            if (this.currentOffset + 2 >= this.poolContent.length) {
                this.resizePoolContents(2);
            }
            this.currentOffset += 2;
            int stringIndex = this.UTF8Cache.putIfAbsent(stringCharArray, this.currentIndex);
            if (stringIndex < 0) {
                if ((stringIndex = -stringIndex) > 65535) {
                    this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
                }
                ++this.currentIndex;
                length = this.offsets.length;
                if (length <= stringIndex) {
                    this.offsets = new int[stringIndex * 2];
                    System.arraycopy(this.offsets, 0, this.offsets, 0, length);
                }
                this.offsets[stringIndex] = this.currentOffset;
                this.writeU1(1);
                int lengthOffset = this.currentOffset;
                if (this.currentOffset + 2 >= this.poolContent.length) {
                    this.resizePoolContents(2);
                }
                this.currentOffset += 2;
                length = 0;
                int i = 0;
                while (i < stringCharArray.length) {
                    char current = stringCharArray[i];
                    if (current >= '\u0001' && current <= '\u007f') {
                        ++length;
                        if (this.currentOffset + 1 >= this.poolContent.length) {
                            this.resizePoolContents(1);
                        }
                        this.poolContent[this.currentOffset++] = (byte)current;
                    } else if (current > '\u07ff') {
                        length += 3;
                        if (this.currentOffset + 3 >= this.poolContent.length) {
                            this.resizePoolContents(3);
                        }
                        this.poolContent[this.currentOffset++] = (byte)(0xE0 | current >> 12 & 0xF);
                        this.poolContent[this.currentOffset++] = (byte)(0x80 | current >> 6 & 0x3F);
                        this.poolContent[this.currentOffset++] = (byte)(0x80 | current & 0x3F);
                    } else {
                        if (this.currentOffset + 2 >= this.poolContent.length) {
                            this.resizePoolContents(2);
                        }
                        length += 2;
                        this.poolContent[this.currentOffset++] = (byte)(0xC0 | current >> 6 & 0x1F);
                        this.poolContent[this.currentOffset++] = (byte)(0x80 | current & 0x3F);
                    }
                    ++i;
                }
                if (length >= 65535) {
                    this.currentOffset = savedCurrentOffset;
                    this.currentIndex = savedCurrentIndex;
                    this.stringCache.remove(stringCharArray);
                    this.UTF8Cache.remove(stringCharArray);
                    return 0;
                }
                this.poolContent[lengthOffset++] = (byte)(length >> 8);
                this.poolContent[lengthOffset] = (byte)length;
            }
            this.poolContent[stringIndexOffset++] = (byte)(stringIndex >> 8);
            this.poolContent[stringIndexOffset] = (byte)stringIndex;
        }
        return index;
    }

    private int putInNameAndTypeCacheIfAbsent(char[] key1, char[] key2, int value) {
        int index;
        Object key1Value = this.nameAndTypeCacheForFieldsAndMethods.get(key1);
        if (key1Value == null) {
            CachedIndexEntry cachedIndexEntry = new CachedIndexEntry(key2, value);
            index = -value;
            this.nameAndTypeCacheForFieldsAndMethods.put(key1, cachedIndexEntry);
        } else if (key1Value instanceof CachedIndexEntry) {
            CachedIndexEntry entry = (CachedIndexEntry)key1Value;
            if (CharOperation.equals(key2, entry.signature)) {
                index = entry.index;
            } else {
                CharArrayCache charArrayCache = new CharArrayCache();
                charArrayCache.putIfAbsent(entry.signature, entry.index);
                index = charArrayCache.putIfAbsent(key2, value);
                this.nameAndTypeCacheForFieldsAndMethods.put(key1, charArrayCache);
            }
        } else {
            CharArrayCache charArrayCache = (CharArrayCache)key1Value;
            index = charArrayCache.putIfAbsent(key2, value);
        }
        return index;
    }

    private int putInDynamicCacheIfAbsent(int bootstrapIndex, char[] selector, char[] descriptor, int value) {
        int index;
        HashtableOfObject key1Value = (HashtableOfObject)this.dynamicCache.get(bootstrapIndex);
        if (key1Value == null) {
            key1Value = new HashtableOfObject();
            this.dynamicCache.put(bootstrapIndex, key1Value);
            CachedIndexEntry cachedIndexEntry = new CachedIndexEntry(descriptor, value);
            index = -value;
            key1Value.put(selector, cachedIndexEntry);
        } else {
            Object key2Value = key1Value.get(selector);
            if (key2Value == null) {
                CachedIndexEntry cachedIndexEntry = new CachedIndexEntry(descriptor, value);
                index = -value;
                key1Value.put(selector, cachedIndexEntry);
            } else if (key2Value instanceof CachedIndexEntry) {
                CachedIndexEntry entry = (CachedIndexEntry)key2Value;
                if (CharOperation.equals(descriptor, entry.signature)) {
                    index = entry.index;
                } else {
                    CharArrayCache charArrayCache = new CharArrayCache();
                    charArrayCache.putIfAbsent(entry.signature, entry.index);
                    index = charArrayCache.putIfAbsent(descriptor, value);
                    key1Value.put(selector, charArrayCache);
                }
            } else {
                CharArrayCache charArrayCache = (CharArrayCache)key2Value;
                index = charArrayCache.putIfAbsent(descriptor, value);
            }
        }
        return index;
    }

    private int putInCacheIfAbsent(char[] key1, char[] key2, char[] key3, int value) {
        int index;
        HashtableOfObject key1Value = (HashtableOfObject)this.methodsAndFieldsCache.get(key1);
        if (key1Value == null) {
            key1Value = new HashtableOfObject();
            this.methodsAndFieldsCache.put(key1, key1Value);
            CachedIndexEntry cachedIndexEntry = new CachedIndexEntry(key3, value);
            index = -value;
            key1Value.put(key2, cachedIndexEntry);
        } else {
            Object key2Value = key1Value.get(key2);
            if (key2Value == null) {
                CachedIndexEntry cachedIndexEntry = new CachedIndexEntry(key3, value);
                index = -value;
                key1Value.put(key2, cachedIndexEntry);
            } else if (key2Value instanceof CachedIndexEntry) {
                CachedIndexEntry entry = (CachedIndexEntry)key2Value;
                if (CharOperation.equals(key3, entry.signature)) {
                    index = entry.index;
                } else {
                    CharArrayCache charArrayCache = new CharArrayCache();
                    charArrayCache.putIfAbsent(entry.signature, entry.index);
                    index = charArrayCache.putIfAbsent(key3, value);
                    key1Value.put(key2, charArrayCache);
                }
            } else {
                CharArrayCache charArrayCache = (CharArrayCache)key2Value;
                index = charArrayCache.putIfAbsent(key3, value);
            }
        }
        return index;
    }

    public void resetForClinit(int constantPoolIndex, int constantPoolOffset) {
        this.currentIndex = constantPoolIndex;
        this.currentOffset = constantPoolOffset;
        if (this.UTF8Cache.get(AttributeNamesConstants.CodeName) >= constantPoolIndex) {
            this.UTF8Cache.remove(AttributeNamesConstants.CodeName);
        }
        if (this.UTF8Cache.get(ClinitSignature) >= constantPoolIndex) {
            this.UTF8Cache.remove(ClinitSignature);
        }
        if (this.UTF8Cache.get(Clinit) >= constantPoolIndex) {
            this.UTF8Cache.remove(Clinit);
        }
    }

    private final void resizePoolContents(int minimalSize) {
        int length = this.poolContent.length;
        int toAdd = length;
        if (toAdd < minimalSize) {
            toAdd = minimalSize;
        }
        this.poolContent = new byte[length + toAdd];
        System.arraycopy(this.poolContent, 0, this.poolContent, 0, length);
    }

    protected final void writeU1(int value) {
        if (this.currentOffset + 1 >= this.poolContent.length) {
            this.resizePoolContents(1);
        }
        this.poolContent[this.currentOffset++] = (byte)value;
    }

    protected final void writeU2(int value) {
        if (this.currentOffset + 2 >= this.poolContent.length) {
            this.resizePoolContents(2);
        }
        this.poolContent[this.currentOffset++] = (byte)(value >>> 8);
        this.poolContent[this.currentOffset++] = (byte)value;
    }

    public void reset() {
        if (this.doubleCache != null) {
            this.doubleCache.clear();
        }
        if (this.floatCache != null) {
            this.floatCache.clear();
        }
        if (this.intCache != null) {
            this.intCache.clear();
        }
        if (this.longCache != null) {
            this.longCache.clear();
        }
        this.UTF8Cache.clear();
        this.stringCache.clear();
        this.methodsAndFieldsCache.clear();
        this.classCache.clear();
        this.packageCache.clear();
        this.moduleCache.clear();
        this.nameAndTypeCacheForFieldsAndMethods.clear();
        this.dynamicCache.clear();
        this.currentIndex = 1;
        this.currentOffset = 0;
    }
}

