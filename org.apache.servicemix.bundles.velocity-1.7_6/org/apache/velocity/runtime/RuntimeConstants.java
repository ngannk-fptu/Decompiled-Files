/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime;

public interface RuntimeConstants {
    public static final String RUNTIME_LOG = "runtime.log";
    public static final String RUNTIME_LOG_LOGSYSTEM = "runtime.log.logsystem";
    public static final String RUNTIME_LOG_LOGSYSTEM_CLASS = "runtime.log.logsystem.class";
    public static final String RUNTIME_REFERENCES_STRICT = "runtime.references.strict";
    public static final String RUNTIME_REFERENCES_STRICT_ESCAPE = "runtime.references.strict.escape";
    public static final String RUNTIME_LOG_ERROR_STACKTRACE = "runtime.log.error.stacktrace";
    public static final String RUNTIME_LOG_WARN_STACKTRACE = "runtime.log.warn.stacktrace";
    public static final String RUNTIME_LOG_INFO_STACKTRACE = "runtime.log.info.stacktrace";
    public static final String RUNTIME_LOG_REFERENCE_LOG_INVALID = "runtime.log.invalid.references";
    public static final String TRACE_PREFIX = " [trace] ";
    public static final String DEBUG_PREFIX = " [debug] ";
    public static final String INFO_PREFIX = "  [info] ";
    public static final String WARN_PREFIX = "  [warn] ";
    public static final String ERROR_PREFIX = " [error] ";
    public static final String UNKNOWN_PREFIX = " [unknown] ";
    public static final String COUNTER_NAME = "directive.foreach.counter.name";
    public static final String HAS_NEXT_NAME = "directive.foreach.iterator.name";
    public static final String COUNTER_INITIAL_VALUE = "directive.foreach.counter.initial.value";
    public static final String MAX_NUMBER_LOOPS = "directive.foreach.maxloops";
    public static final String SKIP_INVALID_ITERATOR = "directive.foreach.skip.invalid";
    public static final String SET_NULL_ALLOWED = "directive.set.null.allowed";
    public static final String DIRECTIVE_IF_TOSTRING_NULLCHECK = "directive.if.tostring.nullcheck";
    public static final String ERRORMSG_START = "directive.include.output.errormsg.start";
    public static final String ERRORMSG_END = "directive.include.output.errormsg.end";
    public static final String PARSE_DIRECTIVE_MAXDEPTH = "directive.parse.max.depth";
    public static final String DEFINE_DIRECTIVE_MAXDEPTH = "directive.define.max.depth";
    public static final String EVALUATE_CONTEXT_CLASS = "directive.evaluate.context.class";
    public static final String PROVIDE_SCOPE_CONTROL = "provide.scope.control";
    public static final String RESOURCE_MANAGER_CLASS = "resource.manager.class";
    public static final String RESOURCE_MANAGER_CACHE_CLASS = "resource.manager.cache.class";
    public static final String RESOURCE_MANAGER_DEFAULTCACHE_SIZE = "resource.manager.defaultcache.size";
    public static final String RESOURCE_MANAGER_LOGWHENFOUND = "resource.manager.logwhenfound";
    public static final String RESOURCE_LOADER = "resource.loader";
    public static final String FILE_RESOURCE_LOADER_PATH = "file.resource.loader.path";
    public static final String FILE_RESOURCE_LOADER_CACHE = "file.resource.loader.cache";
    public static final String EVENTHANDLER_REFERENCEINSERTION = "eventhandler.referenceinsertion.class";
    public static final String EVENTHANDLER_NULLSET = "eventhandler.nullset.class";
    public static final String EVENTHANDLER_METHODEXCEPTION = "eventhandler.methodexception.class";
    public static final String EVENTHANDLER_INCLUDE = "eventhandler.include.class";
    public static final String EVENTHANDLER_INVALIDREFERENCES = "eventhandler.invalidreferences.class";
    public static final String VM_LIBRARY = "velocimacro.library";
    public static final String VM_LIBRARY_DEFAULT = "VM_global_library.vm";
    public static final String VM_LIBRARY_AUTORELOAD = "velocimacro.library.autoreload";
    public static final String VM_PERM_ALLOW_INLINE = "velocimacro.permissions.allow.inline";
    public static final String VM_PERM_ALLOW_INLINE_REPLACE_GLOBAL = "velocimacro.permissions.allow.inline.to.replace.global";
    public static final String VM_PERM_INLINE_LOCAL = "velocimacro.permissions.allow.inline.local.scope";
    public static final String VM_MESSAGES_ON = "velocimacro.messages.on";
    public static final String VM_CONTEXT_LOCALSCOPE = "velocimacro.context.localscope";
    public static final String VM_ARGUMENTS_STRICT = "velocimacro.arguments.strict";
    public static final String VM_MAX_DEPTH = "velocimacro.max.depth";
    public static final String VM_BODY_REFERENCE = "velocimacro.body.reference";
    public static final String INTERPOLATE_STRINGLITERALS = "runtime.interpolate.string.literals";
    public static final String INPUT_ENCODING = "input.encoding";
    public static final String OUTPUT_ENCODING = "output.encoding";
    public static final String ENCODING_DEFAULT = "ISO-8859-1";
    public static final String UBERSPECT_CLASSNAME = "runtime.introspector.uberspect";
    public static final String INTROSPECTOR_RESTRICT_PACKAGES = "introspector.restrict.packages";
    public static final String INTROSPECTOR_RESTRICT_CLASSES = "introspector.restrict.classes";
    public static final String STRICT_MATH = "runtime.strict.math";
    public static final String PARSER_POOL_CLASS = "parser.pool.class";
    public static final String PARSER_POOL_SIZE = "parser.pool.size";
    public static final String DEFAULT_RUNTIME_PROPERTIES = "org/apache/velocity/runtime/defaults/velocity.properties";
    public static final String DEFAULT_RUNTIME_DIRECTIVES = "org/apache/velocity/runtime/defaults/directive.properties";
    public static final int NUMBER_OF_PARSERS = 20;
}

