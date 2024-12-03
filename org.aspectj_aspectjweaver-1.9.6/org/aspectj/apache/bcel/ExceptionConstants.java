/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel;

public interface ExceptionConstants {
    public static final Class<Throwable> THROWABLE = Throwable.class;
    public static final Class<RuntimeException> RUNTIME_EXCEPTION = RuntimeException.class;
    public static final Class<LinkageError> LINKING_EXCEPTION = LinkageError.class;
    public static final Class<ClassCircularityError> CLASS_CIRCULARITY_ERROR = ClassCircularityError.class;
    public static final Class<ClassFormatError> CLASS_FORMAT_ERROR = ClassFormatError.class;
    public static final Class<ExceptionInInitializerError> EXCEPTION_IN_INITIALIZER_ERROR = ExceptionInInitializerError.class;
    public static final Class<IncompatibleClassChangeError> INCOMPATIBLE_CLASS_CHANGE_ERROR = IncompatibleClassChangeError.class;
    public static final Class<AbstractMethodError> ABSTRACT_METHOD_ERROR = AbstractMethodError.class;
    public static final Class<IllegalAccessError> ILLEGAL_ACCESS_ERROR = IllegalAccessError.class;
    public static final Class<InstantiationError> INSTANTIATION_ERROR = InstantiationError.class;
    public static final Class<NoSuchFieldError> NO_SUCH_FIELD_ERROR = NoSuchFieldError.class;
    public static final Class<NoSuchMethodError> NO_SUCH_METHOD_ERROR = NoSuchMethodError.class;
    public static final Class<NoClassDefFoundError> NO_CLASS_DEF_FOUND_ERROR = NoClassDefFoundError.class;
    public static final Class<UnsatisfiedLinkError> UNSATISFIED_LINK_ERROR = UnsatisfiedLinkError.class;
    public static final Class<VerifyError> VERIFY_ERROR = VerifyError.class;
    public static final Class<NullPointerException> NULL_POINTER_EXCEPTION = NullPointerException.class;
    public static final Class<ArrayIndexOutOfBoundsException> ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION = ArrayIndexOutOfBoundsException.class;
    public static final Class<ArithmeticException> ARITHMETIC_EXCEPTION = ArithmeticException.class;
    public static final Class<NegativeArraySizeException> NEGATIVE_ARRAY_SIZE_EXCEPTION = NegativeArraySizeException.class;
    public static final Class<ClassCastException> CLASS_CAST_EXCEPTION = ClassCastException.class;
    public static final Class<IllegalMonitorStateException> ILLEGAL_MONITOR_STATE = IllegalMonitorStateException.class;
    public static final Class[] EXCS_CLASS_AND_INTERFACE_RESOLUTION = new Class[]{NO_CLASS_DEF_FOUND_ERROR, CLASS_FORMAT_ERROR, VERIFY_ERROR, ABSTRACT_METHOD_ERROR, EXCEPTION_IN_INITIALIZER_ERROR, ILLEGAL_ACCESS_ERROR};
    public static final Class[] EXCS_CLASS_AND_INTERFACE_RESOLUTION_MULTIANEWARRAY = new Class[]{NO_CLASS_DEF_FOUND_ERROR, CLASS_FORMAT_ERROR, VERIFY_ERROR, ABSTRACT_METHOD_ERROR, EXCEPTION_IN_INITIALIZER_ERROR, ILLEGAL_ACCESS_ERROR, NEGATIVE_ARRAY_SIZE_EXCEPTION, ILLEGAL_ACCESS_ERROR};
    public static final Class[] EXCS_CLASS_AND_INTERFACE_RESOLUTION_ANEWARRAY = new Class[]{NO_CLASS_DEF_FOUND_ERROR, CLASS_FORMAT_ERROR, VERIFY_ERROR, ABSTRACT_METHOD_ERROR, EXCEPTION_IN_INITIALIZER_ERROR, ILLEGAL_ACCESS_ERROR, NEGATIVE_ARRAY_SIZE_EXCEPTION};
    public static final Class[] EXCS_CLASS_AND_INTERFACE_RESOLUTION_CHECKCAST = new Class[]{NO_CLASS_DEF_FOUND_ERROR, CLASS_FORMAT_ERROR, VERIFY_ERROR, ABSTRACT_METHOD_ERROR, EXCEPTION_IN_INITIALIZER_ERROR, ILLEGAL_ACCESS_ERROR, CLASS_CAST_EXCEPTION};
    public static final Class[] EXCS_CLASS_AND_INTERFACE_RESOLUTION_FOR_ALLOCATIONS = new Class[]{NO_CLASS_DEF_FOUND_ERROR, CLASS_FORMAT_ERROR, VERIFY_ERROR, ABSTRACT_METHOD_ERROR, EXCEPTION_IN_INITIALIZER_ERROR, ILLEGAL_ACCESS_ERROR, INSTANTIATION_ERROR, ILLEGAL_ACCESS_ERROR};
    public static final Class[] EXCS_FIELD_AND_METHOD_RESOLUTION = new Class[]{NO_SUCH_FIELD_ERROR, ILLEGAL_ACCESS_ERROR, NO_SUCH_METHOD_ERROR};
    public static final Class[] EXCS_FIELD_AND_METHOD_RESOLUTION_GETFIELD_PUTFIELD = new Class[]{NO_SUCH_FIELD_ERROR, ILLEGAL_ACCESS_ERROR, NO_SUCH_METHOD_ERROR, INCOMPATIBLE_CLASS_CHANGE_ERROR, NULL_POINTER_EXCEPTION};
    public static final Class[] EXCS_FIELD_AND_METHOD_RESOLUTION_GETSTATIC_PUTSTATIC = new Class[]{NO_SUCH_FIELD_ERROR, ILLEGAL_ACCESS_ERROR, NO_SUCH_METHOD_ERROR, INCOMPATIBLE_CLASS_CHANGE_ERROR};
    public static final Class[] EXCS_INTERFACE_METHOD_RESOLUTION_INVOKEINTERFACE = new Class[]{INCOMPATIBLE_CLASS_CHANGE_ERROR, ILLEGAL_ACCESS_ERROR, ABSTRACT_METHOD_ERROR, UNSATISFIED_LINK_ERROR};
    public static final Class[] EXCS_INTERFACE_METHOD_RESOLUTION_INVOKESPECIAL_INVOKEVIRTUAL = new Class[]{INCOMPATIBLE_CLASS_CHANGE_ERROR, NULL_POINTER_EXCEPTION, ABSTRACT_METHOD_ERROR, UNSATISFIED_LINK_ERROR};
    public static final Class[] EXCS_INTERFACE_METHOD_RESOLUTION_INVOKESTATIC = new Class[]{INCOMPATIBLE_CLASS_CHANGE_ERROR, UNSATISFIED_LINK_ERROR};
    public static final Class[] EXCS_INTERFACE_METHOD_RESOLUTION = new Class[0];
    public static final Class[] EXCS_STRING_RESOLUTION = new Class[0];
    public static final Class[] EXCS_ARRAY_EXCEPTION = new Class[]{NULL_POINTER_EXCEPTION, ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION};
}

