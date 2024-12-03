/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nonnull.ReturnValuesAreNonnullByDefault
 *  io.atlassian.util.concurrent.LazyReference
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.plugin.parsers;

import com.atlassian.annotations.nonnull.ReturnValuesAreNonnullByDefault;
import io.atlassian.util.concurrent.LazyReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public class SafeModeCommandLineArguments {
    public static final String PLUGIN_LIST_SEPARATOR = ":";
    private static final String DISABLE_NON_SYSTEM_LINUX = "--disable-all-addons";
    private static final String DISABLE_NON_SYSTEM_WINDOWS = "/disablealladdons";
    private static final String DISABLE_LAST_ENABLED_LINUX = "--disable-last-enabled";
    private static final String DISABLE_LAST_ENABLED_WINDOWS = "/disablelastenabled";
    private static final Pattern DISABLE_ADDONS_ARGUMENT_PATTERN = Pattern.compile(".*disable-?addons=([^\\s]+).*");
    private static final Pattern QUOTED_DISABLE_ADDONS_ARGUMENT_PATTERN = Pattern.compile(".*disable-?addons=\"([^\"].+)\".*$");
    private static final Pattern DISABLE_ADDONS_PRESENT_PATTERN = Pattern.compile(".*disable-?addons.*");
    private final String commandLineArguments;
    private final LazyReference<Boolean> safeMode = new LazyReference<Boolean>(){

        protected Boolean create() {
            return SafeModeCommandLineArguments.this.commandLineArguments.contains(SafeModeCommandLineArguments.getDisableNonSystemWindows()) || SafeModeCommandLineArguments.this.commandLineArguments.contains(SafeModeCommandLineArguments.getDisableNonSystemLinux());
        }
    };
    private final LazyReference<Boolean> lastEnabledDisabled = new LazyReference<Boolean>(){

        protected Boolean create() {
            return SafeModeCommandLineArguments.this.commandLineArguments.contains(SafeModeCommandLineArguments.getDisableLastEnabledLinux()) || SafeModeCommandLineArguments.this.commandLineArguments.contains(SafeModeCommandLineArguments.getDisableLastEnabledWindows());
        }
    };
    private final LazyReference<Optional<List<String>>> disabledPlugins = new LazyReference<Optional<List<String>>>(){

        protected Optional<List<String>> create() {
            Matcher disableAddonsArgumentMatcher = DISABLE_ADDONS_ARGUMENT_PATTERN.matcher(SafeModeCommandLineArguments.this.commandLineArguments);
            Matcher quotedDisableAddonsArgumentMatcher = QUOTED_DISABLE_ADDONS_ARGUMENT_PATTERN.matcher(SafeModeCommandLineArguments.this.commandLineArguments);
            Matcher disableAddonsPresentMatcher = DISABLE_ADDONS_PRESENT_PATTERN.matcher(SafeModeCommandLineArguments.this.commandLineArguments);
            if (quotedDisableAddonsArgumentMatcher.matches()) {
                return Optional.of(Collections.unmodifiableList(Arrays.asList(quotedDisableAddonsArgumentMatcher.group(1).split(SafeModeCommandLineArguments.getPluginListSeparator()))));
            }
            if (disableAddonsArgumentMatcher.matches()) {
                return Optional.of(Collections.unmodifiableList(Arrays.asList(disableAddonsArgumentMatcher.group(1).split(SafeModeCommandLineArguments.getPluginListSeparator()))));
            }
            if (disableAddonsPresentMatcher.matches()) {
                return Optional.of(Collections.emptyList());
            }
            return Optional.empty();
        }
    };

    public SafeModeCommandLineArguments(String commandLineArguments) {
        this.commandLineArguments = commandLineArguments;
    }

    public static String getDisableLastEnabledLinux() {
        return DISABLE_LAST_ENABLED_LINUX;
    }

    public static String getDisableLastEnabledWindows() {
        return DISABLE_LAST_ENABLED_WINDOWS;
    }

    static String getPluginListSeparator() {
        return PLUGIN_LIST_SEPARATOR;
    }

    static String getDisableNonSystemLinux() {
        return DISABLE_NON_SYSTEM_LINUX;
    }

    static String getDisableNonSystemWindows() {
        return DISABLE_NON_SYSTEM_WINDOWS;
    }

    public String getSafeModeArguments() {
        return this.commandLineArguments;
    }

    public Optional<List<String>> getDisabledPlugins() {
        return (Optional)this.disabledPlugins.get();
    }

    public boolean isSafeMode() {
        return (Boolean)this.safeMode.get();
    }

    public boolean shouldLastEnabledBeDisabled() {
        return (Boolean)this.lastEnabledDisabled.get();
    }

    public boolean isDisabledByParam(String pluginKey) {
        return this.getDisabledPlugins().map(plugins -> plugins.contains(pluginKey)).orElse(false);
    }
}

