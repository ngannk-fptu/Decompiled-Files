/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Throwables
 *  com.google.common.io.CharStreams
 *  javax.activation.DataSource
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.google.common.base.Throwables;
import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.activation.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputStreamSerializer {
    private static final Logger log = LoggerFactory.getLogger(InputStreamSerializer.class);
    private final List<Supplier<InputStream>> inputStreamFactories = new LinkedList<Supplier<InputStream>>();
    private final Consumer<Exception> exceptionHandler;
    private static final Consumer<Exception> RETHROW_UNCHECKED = exception -> {
        throw Throwables.propagate((Throwable)exception);
    };
    private static final Consumer<Exception> TO_LOG = exception -> log.error(exception.getMessage(), (Throwable)exception);
    private static final Consumer<Exception> RETHROW_UNCHECKED_IN_DEV_MODE_OTHERWISE_LOG = exception -> {
        if (ConfluenceSystemProperties.isDevMode()) {
            RETHROW_UNCHECKED.accept((Exception)exception);
        } else {
            TO_LOG.accept((Exception)exception);
        }
    };

    private InputStreamSerializer(Consumer<Exception> exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public static InputStreamSerializer lenient() {
        return new InputStreamSerializer(TO_LOG);
    }

    public static InputStreamSerializer eager() {
        return new InputStreamSerializer(RETHROW_UNCHECKED);
    }

    public static InputStreamSerializer eagerInDevMode() {
        return new InputStreamSerializer(RETHROW_UNCHECKED_IN_DEV_MODE_OTHERWISE_LOG);
    }

    public InputStreamSerializer add(Callable<InputStream> ... inputStreamFactories) {
        return this.addAll(Arrays.stream(inputStreamFactories).map(this::supplierFor));
    }

    public InputStreamSerializer addAll(Iterable<Callable<InputStream>> inputStreamFactories) {
        return this.addAll(StreamSupport.stream(inputStreamFactories.spliterator(), false).map(this::supplierFor));
    }

    private InputStreamSerializer addAll(Stream<Supplier<InputStream>> inputStreamFactories) {
        this.inputStreamFactories.addAll(inputStreamFactories.collect(Collectors.toList()));
        return this;
    }

    private Supplier<InputStream> supplierFor(Callable<InputStream> inputStreamFactory) {
        return () -> {
            try {
                return (InputStream)inputStreamFactory.call();
            }
            catch (Exception ex) {
                this.exceptionHandler.accept(ex);
                return null;
            }
        };
    }

    public InputStreamSerializer addDataSource(DataSource ... dataSources) {
        return this.addAllDataSources(Arrays.asList(dataSources));
    }

    public InputStreamSerializer addAllDataSources(Iterable<DataSource> dataSources) {
        Stream<Supplier<InputStream>> inputSuppliers = StreamSupport.stream(dataSources.spliterator(), false).map(InputStreamSerializer.dataSourceToInputSupplier(this.exceptionHandler));
        return this.addAll(inputSuppliers);
    }

    static Function<DataSource, Supplier<InputStream>> dataSourceToInputSupplier(Consumer<Exception> exceptionHandler) {
        return dataSource -> () -> {
            try {
                return dataSource.getInputStream();
            }
            catch (IOException ex) {
                exceptionHandler.accept(ex);
                return null;
            }
        };
    }

    public String toString() {
        return this.readInputsToString();
    }

    public String readInputsToString() {
        return this.inputStreamFactories.stream().filter(supplier -> supplier != null).map(is -> {
            String string;
            block8: {
                InputStream stream = (InputStream)is.get();
                try {
                    string = CharStreams.toString((Readable)new InputStreamReader(stream, Charset.defaultCharset()));
                    if (stream == null) break block8;
                }
                catch (Throwable throwable) {
                    try {
                        if (stream != null) {
                            try {
                                stream.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    catch (IOException exception) {
                        this.exceptionHandler.accept(exception);
                        return "";
                    }
                }
                stream.close();
            }
            return string;
        }).collect(Collectors.joining(""));
    }
}

