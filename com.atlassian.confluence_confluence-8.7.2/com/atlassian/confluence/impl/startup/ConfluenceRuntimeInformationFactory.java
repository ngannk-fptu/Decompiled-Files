/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.jdk.utilities.runtimeinformation.MemoryInformation
 *  com.atlassian.jdk.utilities.runtimeinformation.RuntimeInformation
 *  com.atlassian.jdk.utilities.runtimeinformation.RuntimeInformationBean
 *  com.atlassian.jdk.utilities.runtimeinformation.RuntimeInformationFactory
 *  com.google.common.annotations.VisibleForTesting
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.impl.startup;

import com.atlassian.jdk.utilities.runtimeinformation.MemoryInformation;
import com.atlassian.jdk.utilities.runtimeinformation.RuntimeInformation;
import com.atlassian.jdk.utilities.runtimeinformation.RuntimeInformationBean;
import com.atlassian.jdk.utilities.runtimeinformation.RuntimeInformationFactory;
import com.google.common.annotations.VisibleForTesting;
import java.lang.management.ManagementFactory;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class ConfluenceRuntimeInformationFactory
extends RuntimeInformationFactory {
    public static RuntimeInformation getFilteredRuntimeInformation() {
        return new FilteredRuntimeInformation(ConfluenceRuntimeInformationFactory.getRuntimeInformation());
    }

    @VisibleForTesting
    static class FilteredRuntimeInformation
    implements RuntimeInformation {
        private static final Pattern SYSPROP_PATTERN = Pattern.compile("-D\\S*=.*");
        private static final Pattern XX_BOOLEAN_PATTERN = Pattern.compile("-XX:[+-].*");
        private static final Pattern HEAP_PATTERN = Pattern.compile("-X[sm][nsx][0-9]+[kKmMgG]?");
        private static final Pattern XX_QUANTITATIVE_PATTERN = Pattern.compile("-XX:\\S*=[0-9]+[kKmMgG]?");
        private final RuntimeInformation information;
        private final Supplier<List<String>> arguments;

        private FilteredRuntimeInformation(RuntimeInformation information) {
            this(information, () -> information instanceof RuntimeInformationBean ? ManagementFactory.getRuntimeMXBean().getInputArguments() : Collections.emptyList());
        }

        @VisibleForTesting
        FilteredRuntimeInformation(RuntimeInformation information, Supplier<List<String>> arguments) {
            this.information = information;
            this.arguments = arguments;
        }

        public long getTotalHeapMemory() {
            return this.information.getTotalHeapMemory();
        }

        public long getTotalHeapMemoryUsed() {
            return this.information.getTotalHeapMemoryUsed();
        }

        private static String filterArgument(String argument) {
            if (SYSPROP_PATTERN.matcher(argument).matches()) {
                return "";
            }
            if (XX_BOOLEAN_PATTERN.matcher(argument).matches() || HEAP_PATTERN.matcher(argument).matches() || XX_QUANTITATIVE_PATTERN.matcher(argument).matches()) {
                return argument;
            }
            return "";
        }

        public String getJvmInputArguments() {
            return this.arguments.get().stream().map(FilteredRuntimeInformation::filterArgument).filter(arg -> !StringUtils.isBlank((CharSequence)arg)).collect(Collectors.joining(" "));
        }

        public List<MemoryInformation> getMemoryPoolInformation() {
            return this.information.getMemoryPoolInformation();
        }

        public long getTotalPermGenMemory() {
            return this.information.getTotalPermGenMemory();
        }

        public long getTotalPermGenMemoryUsed() {
            return this.information.getTotalPermGenMemoryUsed();
        }

        public long getTotalNonHeapMemory() {
            return this.information.getTotalNonHeapMemory();
        }

        public long getTotalNonHeapMemoryUsed() {
            return this.information.getTotalNonHeapMemoryUsed();
        }

        public long getXmx() {
            return this.information.getXmx();
        }

        public long getXms() {
            return this.information.getXms();
        }
    }
}

