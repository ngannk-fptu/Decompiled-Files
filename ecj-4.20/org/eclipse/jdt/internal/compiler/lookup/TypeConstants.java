/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.core.compiler.CharOperation;

public interface TypeConstants {
    public static final char[] JAVA = "java".toCharArray();
    public static final char[] JAVAX = "javax".toCharArray();
    public static final char[] LANG = "lang".toCharArray();
    public static final char[] IO = "io".toCharArray();
    public static final char[] NIO = "nio".toCharArray();
    public static final char[] UTIL = "util".toCharArray();
    public static final char[] ZIP = "zip".toCharArray();
    public static final char[] JDK = "jdk".toCharArray();
    public static final char[] ANNOTATION = "annotation".toCharArray();
    public static final char[] REFLECT = "reflect".toCharArray();
    public static final char[] LENGTH = "length".toCharArray();
    public static final char[] CLONE = "clone".toCharArray();
    public static final char[] EQUALS = "equals".toCharArray();
    public static final char[] GETCLASS = "getClass".toCharArray();
    public static final char[] HASHCODE = "hashCode".toCharArray();
    public static final char[] TOSTRING = "toString".toCharArray();
    public static final char[] OBJECT = "Object".toCharArray();
    public static final char[] MAIN = "main".toCharArray();
    public static final char[] SERIALVERSIONUID = "serialVersionUID".toCharArray();
    public static final char[] SERIALPERSISTENTFIELDS = "serialPersistentFields".toCharArray();
    public static final char[] READRESOLVE = "readResolve".toCharArray();
    public static final char[] WRITEREPLACE = "writeReplace".toCharArray();
    public static final char[] READOBJECT = "readObject".toCharArray();
    public static final char[] WRITEOBJECT = "writeObject".toCharArray();
    public static final char[] CharArray_JAVA_LANG_OBJECT = "java.lang.Object".toCharArray();
    public static final char[] CharArray_JAVA_LANG_ENUM = "java.lang.Enum".toCharArray();
    public static final char[] CharArray_JAVA_LANG_RECORD = "java.lang.Record".toCharArray();
    public static final char[] CharArray_JAVA_LANG_RECORD_SLASH = "java/lang/Record".toCharArray();
    public static final char[] CharArray_JAVA_LANG_ANNOTATION_ANNOTATION = "java.lang.annotation.Annotation".toCharArray();
    public static final char[] CharArray_JAVA_IO_OBJECTINPUTSTREAM = "java.io.ObjectInputStream".toCharArray();
    public static final char[] CharArray_JAVA_IO_OBJECTOUTPUTSTREAM = "java.io.ObjectOutputStream".toCharArray();
    public static final char[] CharArray_JAVA_IO_OBJECTSTREAMFIELD = "java.io.ObjectStreamField".toCharArray();
    public static final char[] ANONYM_PREFIX = "new ".toCharArray();
    public static final char[] ANONYM_SUFFIX = "(){}".toCharArray();
    public static final char[] WILDCARD_NAME = new char[]{'?'};
    public static final char[] WILDCARD_SUPER = " super ".toCharArray();
    public static final char[] WILDCARD_EXTENDS = " extends ".toCharArray();
    public static final char[] WILDCARD_MINUS = new char[]{'-'};
    public static final char[] WILDCARD_STAR = new char[]{'*'};
    public static final char[] WILDCARD_PLUS = new char[]{'+'};
    public static final char[] WILDCARD_CAPTURE_NAME_PREFIX = "capture#".toCharArray();
    public static final char[] WILDCARD_CAPTURE_NAME_SUFFIX = "-of ".toCharArray();
    public static final char[] WILDCARD_CAPTURE_SIGNABLE_NAME_SUFFIX = "capture-of ".toCharArray();
    public static final char[] WILDCARD_CAPTURE = new char[]{'!'};
    public static final char[] CAPTURE18 = new char[]{'^'};
    public static final char[] BYTE = "byte".toCharArray();
    public static final char[] SHORT = "short".toCharArray();
    public static final char[] INT = "int".toCharArray();
    public static final char[] LONG = "long".toCharArray();
    public static final char[] FLOAT = "float".toCharArray();
    public static final char[] DOUBLE = "double".toCharArray();
    public static final char[] CHAR = "char".toCharArray();
    public static final char[] BOOLEAN = "boolean".toCharArray();
    public static final char[] NULL = "null".toCharArray();
    public static final char[] VOID = "void".toCharArray();
    public static final char[] VALUE = "value".toCharArray();
    public static final char[] VALUES = "values".toCharArray();
    public static final char[] VALUEOF = "valueOf".toCharArray();
    public static final char[] UPPER_SOURCE = "SOURCE".toCharArray();
    public static final char[] UPPER_CLASS = "CLASS".toCharArray();
    public static final char[] UPPER_RUNTIME = "RUNTIME".toCharArray();
    public static final char[] ANNOTATION_PREFIX = "@".toCharArray();
    public static final char[] ANNOTATION_SUFFIX = "()".toCharArray();
    public static final char[] TYPE = "TYPE".toCharArray();
    public static final char[] UPPER_FIELD = "FIELD".toCharArray();
    public static final char[] UPPER_METHOD = "METHOD".toCharArray();
    public static final char[] UPPER_PARAMETER = "PARAMETER".toCharArray();
    public static final char[] UPPER_CONSTRUCTOR = "CONSTRUCTOR".toCharArray();
    public static final char[] UPPER_LOCAL_VARIABLE = "LOCAL_VARIABLE".toCharArray();
    public static final char[] UPPER_ANNOTATION_TYPE = "ANNOTATION_TYPE".toCharArray();
    public static final char[] UPPER_PACKAGE = "PACKAGE".toCharArray();
    public static final char[] ANONYMOUS_METHOD = "lambda$".toCharArray();
    public static final char[] DESERIALIZE_LAMBDA = "$deserializeLambda$".toCharArray();
    public static final char[] LAMBDA_TYPE = "<lambda>".toCharArray();
    public static final char[] UPPER_MODULE = "MODULE".toCharArray();
    public static final char[] UPPER_RECORD_COMPONENT = "RECORD_COMPONENT".toCharArray();
    public static final char[] VAR = "var".toCharArray();
    public static final char[] RECORD_RESTRICTED_IDENTIFIER = "record".toCharArray();
    public static final char[] RECORD_CLASS = "Record".toCharArray();
    public static final char[] PERMITS = "permits".toCharArray();
    public static final char[] SEALED = "sealed".toCharArray();
    public static final String KEYWORD_EXTENDS = "extends";
    public static final String IMPLEMENTS = "implements";
    public static final char[] TYPE_USE_TARGET = "TYPE_USE".toCharArray();
    public static final char[] TYPE_PARAMETER_TARGET = "TYPE_PARAMETER".toCharArray();
    public static final char[] ORG = "org".toCharArray();
    public static final char[] ECLIPSE = "eclipse".toCharArray();
    public static final char[] CORE = "core".toCharArray();
    public static final char[] RUNTIME = "runtime".toCharArray();
    public static final char[] APACHE = "apache".toCharArray();
    public static final char[] COMMONS = "commons".toCharArray();
    public static final char[] LANG3 = "lang3".toCharArray();
    public static final char[] COM = "com".toCharArray();
    public static final char[] GOOGLE = "google".toCharArray();
    public static final char[] JDT = "jdt".toCharArray();
    public static final char[] INTERNAL = "internal".toCharArray();
    public static final char[] COMPILER = "compiler".toCharArray();
    public static final char[] LOOKUP = "lookup".toCharArray();
    public static final char[] TYPEBINDING = "TypeBinding".toCharArray();
    public static final char[] DOM = "dom".toCharArray();
    public static final char[] ITYPEBINDING = "ITypeBinding".toCharArray();
    public static final char[] SPRING = "springframework".toCharArray();
    public static final char[][] JAVA_LANG = new char[][]{JAVA, LANG};
    public static final char[][] JAVA_IO = new char[][]{JAVA, IO};
    public static final char[][] JAVA_LANG_ANNOTATION = new char[][]{JAVA, LANG, ANNOTATION};
    public static final char[][] JAVA_LANG_ANNOTATION_ANNOTATION = new char[][]{JAVA, LANG, ANNOTATION, "Annotation".toCharArray()};
    public static final char[][] JAVA_LANG_ASSERTIONERROR = new char[][]{JAVA, LANG, "AssertionError".toCharArray()};
    public static final char[][] JAVA_LANG_CLASS = new char[][]{JAVA, LANG, "Class".toCharArray()};
    public static final char[][] JAVA_LANG_CLASSNOTFOUNDEXCEPTION = new char[][]{JAVA, LANG, "ClassNotFoundException".toCharArray()};
    public static final char[][] JAVA_LANG_NOSUCHFIELDERROR = new char[][]{JAVA, LANG, "NoSuchFieldError".toCharArray()};
    public static final char[][] JAVA_LANG_CLONEABLE = new char[][]{JAVA, LANG, "Cloneable".toCharArray()};
    public static final char[][] JAVA_LANG_ENUM = new char[][]{JAVA, LANG, "Enum".toCharArray()};
    public static final char[][] JAVA_LANG_EXCEPTION = new char[][]{JAVA, LANG, "Exception".toCharArray()};
    public static final char[][] JAVA_LANG_ERROR = new char[][]{JAVA, LANG, "Error".toCharArray()};
    public static final char[][] JAVA_LANG_ILLEGALARGUMENTEXCEPTION = new char[][]{JAVA, LANG, "IllegalArgumentException".toCharArray()};
    public static final char[][] JAVA_LANG_INCOMPATIBLECLASSCHANGEERROR = new char[][]{JAVA, LANG, "IncompatibleClassChangeError".toCharArray()};
    public static final char[][] JAVA_LANG_ITERABLE = new char[][]{JAVA, LANG, "Iterable".toCharArray()};
    public static final char[][] JAVA_LANG_NOCLASSDEFERROR = new char[][]{JAVA, LANG, "NoClassDefError".toCharArray()};
    public static final char[][] JAVA_LANG_OBJECT = new char[][]{JAVA, LANG, OBJECT};
    public static final char[][] JAVA_LANG_RECORD = new char[][]{JAVA, LANG, RECORD_CLASS};
    public static final char[][] JAVA_LANG_STRING = new char[][]{JAVA, LANG, "String".toCharArray()};
    public static final char[][] JAVA_LANG_STRINGBUFFER = new char[][]{JAVA, LANG, "StringBuffer".toCharArray()};
    public static final char[][] JAVA_LANG_STRINGBUILDER = new char[][]{JAVA, LANG, "StringBuilder".toCharArray()};
    public static final char[][] JAVA_LANG_SYSTEM = new char[][]{JAVA, LANG, "System".toCharArray()};
    public static final char[][] JAVA_LANG_RUNTIMEEXCEPTION = new char[][]{JAVA, LANG, "RuntimeException".toCharArray()};
    public static final char[][] JAVA_LANG_THROWABLE = new char[][]{JAVA, LANG, "Throwable".toCharArray()};
    public static final char[][] JAVA_LANG_REFLECT_CONSTRUCTOR = new char[][]{JAVA, LANG, REFLECT, "Constructor".toCharArray()};
    public static final char[][] JAVA_IO_PRINTSTREAM = new char[][]{JAVA, IO, "PrintStream".toCharArray()};
    public static final char[][] JAVA_IO_SERIALIZABLE = new char[][]{JAVA, IO, "Serializable".toCharArray()};
    public static final char[][] JAVA_LANG_BYTE = new char[][]{JAVA, LANG, "Byte".toCharArray()};
    public static final char[][] JAVA_LANG_SHORT = new char[][]{JAVA, LANG, "Short".toCharArray()};
    public static final char[][] JAVA_LANG_CHARACTER = new char[][]{JAVA, LANG, "Character".toCharArray()};
    public static final char[][] JAVA_LANG_INTEGER = new char[][]{JAVA, LANG, "Integer".toCharArray()};
    public static final char[][] JAVA_LANG_LONG = new char[][]{JAVA, LANG, "Long".toCharArray()};
    public static final char[][] JAVA_LANG_FLOAT = new char[][]{JAVA, LANG, "Float".toCharArray()};
    public static final char[][] JAVA_LANG_DOUBLE = new char[][]{JAVA, LANG, "Double".toCharArray()};
    public static final char[][] JAVA_LANG_BOOLEAN = new char[][]{JAVA, LANG, "Boolean".toCharArray()};
    public static final char[][] JAVA_LANG_VOID = new char[][]{JAVA, LANG, "Void".toCharArray()};
    public static final char[][] JAVA_UTIL_COLLECTION = new char[][]{JAVA, UTIL, "Collection".toCharArray()};
    public static final char[][] JAVA_UTIL_ITERATOR = new char[][]{JAVA, UTIL, "Iterator".toCharArray()};
    public static final char[][] JAVA_UTIL_OBJECTS = new char[][]{JAVA, UTIL, "Objects".toCharArray()};
    public static final char[][] JAVA_UTIL_LIST = new char[][]{JAVA, UTIL, "List".toCharArray()};
    public static final char[][] JAVA_UTIL_ARRAYS = new char[][]{JAVA, UTIL, "Arrays".toCharArray()};
    public static final char[][] JAVA_LANG_DEPRECATED = new char[][]{JAVA, LANG, "Deprecated".toCharArray()};
    public static final char[] FOR_REMOVAL = "forRemoval".toCharArray();
    public static final char[] SINCE = "since".toCharArray();
    public static final char[] ESSENTIAL_API = "essentialAPI".toCharArray();
    public static final char[][] JAVA_LANG_ANNOTATION_DOCUMENTED = new char[][]{JAVA, LANG, ANNOTATION, "Documented".toCharArray()};
    public static final char[][] JAVA_LANG_ANNOTATION_INHERITED = new char[][]{JAVA, LANG, ANNOTATION, "Inherited".toCharArray()};
    public static final char[][] JAVA_LANG_ANNOTATION_REPEATABLE = new char[][]{JAVA, LANG, ANNOTATION, "Repeatable".toCharArray()};
    public static final char[][] JAVA_LANG_OVERRIDE = new char[][]{JAVA, LANG, "Override".toCharArray()};
    public static final char[][] JAVA_LANG_FUNCTIONAL_INTERFACE = new char[][]{JAVA, LANG, "FunctionalInterface".toCharArray()};
    public static final char[][] JAVA_LANG_ANNOTATION_RETENTION = new char[][]{JAVA, LANG, ANNOTATION, "Retention".toCharArray()};
    public static final char[][] JAVA_LANG_SUPPRESSWARNINGS = new char[][]{JAVA, LANG, "SuppressWarnings".toCharArray()};
    public static final char[][] JAVA_LANG_ANNOTATION_TARGET = new char[][]{JAVA, LANG, ANNOTATION, "Target".toCharArray()};
    public static final char[][] JAVA_LANG_ANNOTATION_RETENTIONPOLICY = new char[][]{JAVA, LANG, ANNOTATION, "RetentionPolicy".toCharArray()};
    public static final char[][] JAVA_LANG_ANNOTATION_ELEMENTTYPE = new char[][]{JAVA, LANG, ANNOTATION, "ElementType".toCharArray()};
    public static final char[][] JDK_INTERNAL_PREVIEW_FEATURE = new char[][]{JDK, INTERNAL, "PreviewFeature".toCharArray()};
    public static final char[][] JAVA_LANG_REFLECT_FIELD = new char[][]{JAVA, LANG, REFLECT, "Field".toCharArray()};
    public static final char[][] JAVA_LANG_REFLECT_METHOD = new char[][]{JAVA, LANG, REFLECT, "Method".toCharArray()};
    public static final char[][] JAVA_IO_CLOSEABLE = new char[][]{JAVA, IO, "Closeable".toCharArray()};
    public static final char[][] JAVA_IO_OBJECTSTREAMEXCEPTION = new char[][]{JAVA, IO, "ObjectStreamException".toCharArray()};
    public static final char[][] JAVA_IO_EXTERNALIZABLE = new char[][]{JAVA, IO, "Externalizable".toCharArray()};
    public static final char[][] JAVA_IO_IOEXCEPTION = new char[][]{JAVA, IO, "IOException".toCharArray()};
    public static final char[][] JAVA_IO_OBJECTOUTPUTSTREAM = new char[][]{JAVA, IO, "ObjectOutputStream".toCharArray()};
    public static final char[][] JAVA_IO_OBJECTINPUTSTREAM = new char[][]{JAVA, IO, "ObjectInputStream".toCharArray()};
    public static final char[][] JAVA_NIO_FILE_FILES = new char[][]{JAVA, "nio".toCharArray(), "file".toCharArray(), "Files".toCharArray()};
    public static final char[][] JAVAX_RMI_CORBA_STUB = new char[][]{JAVAX, "rmi".toCharArray(), "CORBA".toCharArray(), "Stub".toCharArray()};
    public static final char[][] JAVA_LANG_SAFEVARARGS = new char[][]{JAVA, LANG, "SafeVarargs".toCharArray()};
    public static final char[] INVOKE = "invoke".toCharArray();
    public static final char[][] JAVA_LANG_INVOKE_METHODHANDLE_POLYMORPHICSIGNATURE = new char[][]{JAVA, LANG, INVOKE, "MethodHandle".toCharArray(), "PolymorphicSignature".toCharArray()};
    public static final char[][] JAVA_LANG_INVOKE_METHODHANDLE_$_POLYMORPHICSIGNATURE = new char[][]{JAVA, LANG, INVOKE, "MethodHandle$PolymorphicSignature".toCharArray()};
    public static final char[][] JAVA_LANG_INVOKE_LAMBDAMETAFACTORY = new char[][]{JAVA, LANG, INVOKE, "LambdaMetafactory".toCharArray()};
    public static final char[][] JAVA_LANG_INVOKE_SERIALIZEDLAMBDA = new char[][]{JAVA, LANG, INVOKE, "SerializedLambda".toCharArray()};
    public static final char[][] JAVA_LANG_INVOKE_METHODHANDLES = new char[][]{JAVA, LANG, INVOKE, "MethodHandles".toCharArray()};
    public static final char[][] JAVA_LANG_AUTOCLOSEABLE = new char[][]{JAVA, LANG, "AutoCloseable".toCharArray()};
    public static final char[] CLOSE = "close".toCharArray();
    public static final char[][] JAVA_LANG_RUNTIME_OBJECTMETHODS = new char[][]{JAVA, LANG, RUNTIME, "ObjectMethods".toCharArray()};
    public static final char[][] GUAVA_CLOSEABLES = new char[][]{COM, GOOGLE, "common".toCharArray(), IO, "Closeables".toCharArray()};
    public static final char[][] APACHE_IOUTILS = new char[][]{ORG, APACHE, COMMONS, IO, "IOUtils".toCharArray()};
    public static final char[][] APACHE_DBUTILS = new char[][]{ORG, APACHE, COMMONS, "dbutils".toCharArray(), "DbUtils".toCharArray()};
    public static final char[] CLOSE_QUIETLY = "closeQuietly".toCharArray();
    public static final CloseMethodRecord[] closeMethods = new CloseMethodRecord[]{new CloseMethodRecord(GUAVA_CLOSEABLES, CLOSE_QUIETLY, 1), new CloseMethodRecord(GUAVA_CLOSEABLES, CLOSE, 1), new CloseMethodRecord(APACHE_IOUTILS, CLOSE_QUIETLY, 1), new CloseMethodRecord(APACHE_DBUTILS, CLOSE, 1), new CloseMethodRecord(APACHE_DBUTILS, CLOSE_QUIETLY, 3), new CloseMethodRecord(APACHE_DBUTILS, "commitAndClose".toCharArray(), 1), new CloseMethodRecord(APACHE_DBUTILS, "commitAndCloseQuietly".toCharArray(), 1), new CloseMethodRecord(APACHE_DBUTILS, "rollbackAndClose".toCharArray(), 1), new CloseMethodRecord(APACHE_DBUTILS, "rollbackAndCloseQuietly".toCharArray(), 1)};
    public static final char[][] JAVA_IO_WRAPPER_CLOSEABLES = new char[][]{"BufferedInputStream".toCharArray(), "BufferedOutputStream".toCharArray(), "BufferedReader".toCharArray(), "BufferedWriter".toCharArray(), "InputStreamReader".toCharArray(), "PrintWriter".toCharArray(), "LineNumberReader".toCharArray(), "DataInputStream".toCharArray(), "DataOutputStream".toCharArray(), "ObjectInputStream".toCharArray(), "ObjectOutputStream".toCharArray(), "FilterInputStream".toCharArray(), "FilterOutputStream".toCharArray(), "DataInputStream".toCharArray(), "DataOutputStream".toCharArray(), "PushbackInputStream".toCharArray(), "SequenceInputStream".toCharArray(), "PrintStream".toCharArray(), "PushbackReader".toCharArray(), "OutputStreamWriter".toCharArray()};
    public static final char[][] JAVA_UTIL_ZIP_WRAPPER_CLOSEABLES = new char[][]{"GZIPInputStream".toCharArray(), "InflaterInputStream".toCharArray(), "DeflaterInputStream".toCharArray(), "CheckedInputStream".toCharArray(), "ZipInputStream".toCharArray(), "JarInputStream".toCharArray(), "GZIPOutputStream".toCharArray(), "InflaterOutputStream".toCharArray(), "DeflaterOutputStream".toCharArray(), "CheckedOutputStream".toCharArray(), "ZipOutputStream".toCharArray(), "JarOutputStream".toCharArray()};
    public static final char[][][] OTHER_WRAPPER_CLOSEABLES = new char[][][]{new char[][]{JAVA, "security".toCharArray(), "DigestInputStream".toCharArray()}, new char[][]{JAVA, "security".toCharArray(), "DigestOutputStream".toCharArray()}, new char[][]{JAVA, "beans".toCharArray(), "XMLEncoder".toCharArray()}, new char[][]{JAVA, "beans".toCharArray(), "XMLDecoder".toCharArray()}, new char[][]{JAVAX, "sound".toCharArray(), "sampled".toCharArray(), "AudioInputStream".toCharArray()}};
    public static final char[][] JAVA_IO_RESOURCE_FREE_CLOSEABLES = new char[][]{"StringReader".toCharArray(), "StringWriter".toCharArray(), "ByteArrayInputStream".toCharArray(), "ByteArrayOutputStream".toCharArray(), "CharArrayReader".toCharArray(), "CharArrayWriter".toCharArray(), "StringBufferInputStream".toCharArray()};
    public static final char[][] JAVA_UTIL_STREAM = new char[][]{JAVA, UTIL, "stream".toCharArray()};
    public static final char[][] RESOURCE_FREE_CLOSEABLE_J_U_STREAMS = new char[][]{"Stream".toCharArray(), "DoubleStream".toCharArray(), "LongStream".toCharArray(), "IntStream".toCharArray()};
    public static final char[][] ONE_UTIL_STREAMEX = new char[][]{"one".toCharArray(), UTIL, "streamex".toCharArray()};
    public static final char[][] RESOURCE_FREE_CLOSEABLE_STREAMEX = new char[][]{"StreamEx".toCharArray(), "IntStreamEx".toCharArray(), "DoubleStreamEx".toCharArray(), "LongStreamEx".toCharArray(), "EntryStream".toCharArray()};
    public static final char[] CHANNELS = "channels".toCharArray();
    public static final char[][][] FLUENT_RESOURCE_CLASSES = new char[][][]{new char[][]{JAVA, IO, "CharArrayWriter".toCharArray()}, new char[][]{JAVA, IO, "Console".toCharArray()}, new char[][]{JAVA, IO, "PrintStream".toCharArray()}, new char[][]{JAVA, IO, "PrintWriter".toCharArray()}, new char[][]{JAVA, IO, "StringWriter".toCharArray()}, new char[][]{JAVA, IO, "Writer".toCharArray()}, new char[][]{JAVA, NIO, CHANNELS, "AsynchronousFileChannel".toCharArray()}, new char[][]{JAVA, NIO, CHANNELS, "AsynchronousServerSocketChannel".toCharArray()}, new char[][]{JAVA, NIO, CHANNELS, "FileChannel".toCharArray()}, new char[][]{JAVA, NIO, CHANNELS, "NetworkChannel".toCharArray()}, new char[][]{JAVA, NIO, CHANNELS, "SeekableByteChannel".toCharArray()}, new char[][]{JAVA, NIO, CHANNELS, "SelectableChannel".toCharArray()}, new char[][]{JAVA, NIO, CHANNELS, "Selector".toCharArray()}, new char[][]{JAVA, NIO, CHANNELS, "ServerSocketChannel".toCharArray()}, new char[][]{JAVA, UTIL, "Formatter".toCharArray()}, new char[][]{JAVA, UTIL, "Scanner".toCharArray()}};
    public static final char[] ASSERT_CLASS = "Assert".toCharArray();
    public static final char[] ASSERTIONS_CLASS = "Assertions".toCharArray();
    public static final char[][] ORG_ECLIPSE_CORE_RUNTIME_ASSERT = new char[][]{ORG, ECLIPSE, CORE, RUNTIME, ASSERT_CLASS};
    public static final char[] IS_NOTNULL = "isNotNull".toCharArray();
    public static final char[] JUNIT = "junit".toCharArray();
    public static final char[] FRAMEWORK = "framework".toCharArray();
    public static final char[] JUPITER = "jupiter".toCharArray();
    public static final char[] PARAMS = "params".toCharArray();
    public static final char[] PROVIDER = "provider".toCharArray();
    public static final char[] API = "api".toCharArray();
    public static final char[][] JUNIT_FRAMEWORK_ASSERT = new char[][]{JUNIT, FRAMEWORK, ASSERT_CLASS};
    public static final char[][] ORG_JUNIT_ASSERT = new char[][]{ORG, JUNIT, ASSERT_CLASS};
    public static final char[][] ORG_JUNIT_JUPITER_API_ASSERTIONS = new char[][]{ORG, JUNIT, JUPITER, API, ASSERTIONS_CLASS};
    public static final char[] ASSERT_NULL = "assertNull".toCharArray();
    public static final char[] ASSERT_NOTNULL = "assertNotNull".toCharArray();
    public static final char[] ASSERT_TRUE = "assertTrue".toCharArray();
    public static final char[] ASSERT_FALSE = "assertFalse".toCharArray();
    public static final char[] METHOD_SOURCE = "MethodSource".toCharArray();
    public static final char[][] ORG_JUNIT_METHOD_SOURCE = new char[][]{ORG, JUNIT, JUPITER, PARAMS, PROVIDER, METHOD_SOURCE};
    public static final char[] VALIDATE_CLASS = "Validate".toCharArray();
    public static final char[][] ORG_APACHE_COMMONS_LANG_VALIDATE = new char[][]{ORG, APACHE, COMMONS, LANG, VALIDATE_CLASS};
    public static final char[][] ORG_APACHE_COMMONS_LANG3_VALIDATE = new char[][]{ORG, APACHE, COMMONS, LANG3, VALIDATE_CLASS};
    public static final char[][] ORG_ECLIPSE_JDT_INTERNAL_COMPILER_LOOKUP_TYPEBINDING = new char[][]{ORG, ECLIPSE, JDT, INTERNAL, COMPILER, LOOKUP, TYPEBINDING};
    public static final char[][] ORG_ECLIPSE_JDT_CORE_DOM_ITYPEBINDING = new char[][]{ORG, ECLIPSE, JDT, CORE, DOM, ITYPEBINDING};
    public static final char[] IS_TRUE = "isTrue".toCharArray();
    public static final char[] NOT_NULL = "notNull".toCharArray();
    public static final char[][] COM_GOOGLE_COMMON_BASE_PRECONDITIONS = new char[][]{COM, GOOGLE, "common".toCharArray(), "base".toCharArray(), "Preconditions".toCharArray()};
    public static final char[] CHECK_NOT_NULL = "checkNotNull".toCharArray();
    public static final char[] CHECK_ARGUMENT = "checkArgument".toCharArray();
    public static final char[] CHECK_STATE = "checkState".toCharArray();
    public static final char[] REQUIRE_NON_NULL = "requireNonNull".toCharArray();
    public static final char[] INJECT_PACKAGE = "inject".toCharArray();
    public static final char[] INJECT_TYPE = "Inject".toCharArray();
    public static final char[][] JAVAX_ANNOTATION_INJECT_INJECT = new char[][]{JAVAX, INJECT_PACKAGE, INJECT_TYPE};
    public static final char[][] COM_GOOGLE_INJECT_INJECT = new char[][]{COM, GOOGLE, INJECT_PACKAGE, INJECT_TYPE};
    public static final char[] OPTIONAL = "optional".toCharArray();
    public static final char[] IS_INSTANCE = "isInstance".toCharArray();
    public static final char[] NON_NULL = "nonNull".toCharArray();
    public static final char[] IS_NULL = "isNull".toCharArray();
    public static final char[][] JAVA_UTIL_MAP = new char[][]{JAVA, UTIL, "Map".toCharArray()};
    public static final char[] GET = "get".toCharArray();
    public static final char[] REMOVE = "remove".toCharArray();
    public static final char[] REMOVE_ALL = "removeAll".toCharArray();
    public static final char[] CONTAINS_ALL = "containsAll".toCharArray();
    public static final char[] RETAIN_ALL = "retainAll".toCharArray();
    public static final char[] CONTAINS_KEY = "containsKey".toCharArray();
    public static final char[] CONTAINS_VALUE = "containsValue".toCharArray();
    public static final char[] CONTAINS = "contains".toCharArray();
    public static final char[] INDEX_OF = "indexOf".toCharArray();
    public static final char[] LAST_INDEX_OF = "lastIndexOf".toCharArray();
    public static final char[] AUTOWIRED = "Autowired".toCharArray();
    public static final char[] BEANS = "beans".toCharArray();
    public static final char[] FACTORY = "factory".toCharArray();
    public static final char[][] ORG_SPRING_AUTOWIRED = new char[][]{ORG, SPRING, BEANS, FACTORY, ANNOTATION, AUTOWIRED};
    public static final char[] REQUIRED = "required".toCharArray();
    public static final int CONSTRAINT_EQUAL = 0;
    public static final int CONSTRAINT_EXTENDS = 1;
    public static final int CONSTRAINT_SUPER = 2;
    public static final char[] INIT = "<init>".toCharArray();
    public static final char[] CLINIT = "<clinit>".toCharArray();
    public static final char[] SYNTHETIC_SWITCH_ENUM_TABLE = "$SWITCH_TABLE$".toCharArray();
    public static final char[] SYNTHETIC_ENUM_VALUES = "ENUM$VALUES".toCharArray();
    public static final char[] SYNTHETIC_ASSERT_DISABLED = "$assertionsDisabled".toCharArray();
    public static final char[] SYNTHETIC_CLASS = "class$".toCharArray();
    public static final char[] SYNTHETIC_OUTER_LOCAL_PREFIX = "val$".toCharArray();
    public static final char[] SYNTHETIC_ENCLOSING_INSTANCE_PREFIX = "this$".toCharArray();
    public static final char[] SYNTHETIC_ACCESS_METHOD_PREFIX = "access$".toCharArray();
    public static final char[] SYNTHETIC_ENUM_CONSTANT_INITIALIZATION_METHOD_PREFIX = " enum constant initialization$".toCharArray();
    public static final char[] SYNTHETIC_STATIC_FACTORY = "<factory>".toCharArray();
    public static final char[] DEFAULT_LOCATION__PARAMETER = "PARAMETER".toCharArray();
    public static final char[] DEFAULT_LOCATION__RETURN_TYPE = "RETURN_TYPE".toCharArray();
    public static final char[] DEFAULT_LOCATION__FIELD = "FIELD".toCharArray();
    public static final char[] DEFAULT_LOCATION__TYPE_ARGUMENT = "TYPE_ARGUMENT".toCharArray();
    public static final char[] DEFAULT_LOCATION__TYPE_PARAMETER = "TYPE_PARAMETER".toCharArray();
    public static final char[] DEFAULT_LOCATION__TYPE_BOUND = "TYPE_BOUND".toCharArray();
    public static final char[] DEFAULT_LOCATION__ARRAY_CONTENTS = "ARRAY_CONTENTS".toCharArray();
    public static final char[] PACKAGE_INFO_NAME = "package-info".toCharArray();
    public static final char[] MODULE_INFO_NAME = "module-info".toCharArray();
    public static final String MODULE_INFO_NAME_STRING = "module-info";
    public static final char[] MODULE_INFO_FILE_NAME = "module-info.java".toCharArray();
    public static final char[] MODULE_INFO_CLASS_NAME = "module-info.class".toCharArray();
    public static final String MODULE_INFO_FILE_NAME_STRING = "module-info.java";
    public static final String MODULE_INFO_CLASS_NAME_STRING = "module-info.class";
    public static final char[] JAVA_BASE = "java.base".toCharArray();
    public static final String META_INF_MANIFEST_MF = "META-INF/MANIFEST.MF";
    public static final String AUTOMATIC_MODULE_NAME = "Automatic-Module-Name";
    public static final char[][] JDK_INTERNAL_VALUEBASED = new char[][]{"jdk".toCharArray(), "internal".toCharArray(), "ValueBased".toCharArray()};

    public static enum BoundCheckStatus {
        OK,
        NULL_PROBLEM,
        UNCHECKED,
        MISMATCH;


        boolean isOKbyJLS() {
            switch (this) {
                case OK: 
                case NULL_PROBLEM: {
                    return true;
                }
            }
            return false;
        }

        public BoundCheckStatus betterOf(BoundCheckStatus other) {
            if (this.ordinal() < other.ordinal()) {
                return this;
            }
            return other;
        }
    }

    public static class CloseMethodRecord {
        public char[][] typeName;
        public char[] selector;
        public int numCloseableArgs;

        public CloseMethodRecord(char[][] typeName, char[] selector, int num) {
            this.typeName = typeName;
            this.selector = selector;
            this.numCloseableArgs = num;
        }
    }

    public static enum DangerousMethod {
        Contains,
        Remove,
        RemoveAll,
        ContainsAll,
        RetainAll,
        Get,
        ContainsKey,
        ContainsValue,
        IndexOf,
        LastIndexOf,
        Equals;


        public static DangerousMethod detectSelector(char[] selector) {
            switch (selector[0]) {
                case 'r': {
                    if (CharOperation.prefixEquals(REMOVE, selector)) {
                        if (CharOperation.equals(selector, REMOVE)) {
                            return Remove;
                        }
                        if (!CharOperation.equals(selector, REMOVE_ALL)) break;
                        return RemoveAll;
                    }
                    if (!CharOperation.equals(selector, RETAIN_ALL)) break;
                    return RetainAll;
                }
                case 'c': {
                    if (!CharOperation.prefixEquals(CONTAINS, selector)) break;
                    if (CharOperation.equals(selector, CONTAINS)) {
                        return Contains;
                    }
                    if (CharOperation.equals(selector, CONTAINS_ALL)) {
                        return ContainsAll;
                    }
                    if (CharOperation.equals(selector, CONTAINS_KEY)) {
                        return ContainsKey;
                    }
                    if (!CharOperation.equals(selector, CONTAINS_VALUE)) break;
                    return ContainsValue;
                }
                case 'g': {
                    if (!CharOperation.equals(selector, GET)) break;
                    return Get;
                }
                case 'i': {
                    if (!CharOperation.equals(selector, INDEX_OF)) break;
                    return IndexOf;
                }
                case 'l': {
                    if (!CharOperation.equals(selector, LAST_INDEX_OF)) break;
                    return LastIndexOf;
                }
                case 'e': {
                    if (!CharOperation.equals(selector, EQUALS)) break;
                    return Equals;
                }
            }
            return null;
        }
    }
}

