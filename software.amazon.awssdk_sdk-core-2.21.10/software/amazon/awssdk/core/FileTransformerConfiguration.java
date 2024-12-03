/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.Validate
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.core;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
public final class FileTransformerConfiguration
implements ToCopyableBuilder<Builder, FileTransformerConfiguration> {
    private final FileWriteOption fileWriteOption;
    private final FailureBehavior failureBehavior;
    private final ExecutorService executorService;

    private FileTransformerConfiguration(DefaultBuilder builder) {
        this.fileWriteOption = (FileWriteOption)((Object)Validate.paramNotNull((Object)((Object)builder.fileWriteOption), (String)"fileWriteOption"));
        this.failureBehavior = (FailureBehavior)((Object)Validate.paramNotNull((Object)((Object)builder.failureBehavior), (String)"failureBehavior"));
        this.executorService = builder.executorService;
    }

    public FileWriteOption fileWriteOption() {
        return this.fileWriteOption;
    }

    public FailureBehavior failureBehavior() {
        return this.failureBehavior;
    }

    public Optional<ExecutorService> executorService() {
        return Optional.ofNullable(this.executorService);
    }

    public static Builder builder() {
        return new DefaultBuilder();
    }

    public static FileTransformerConfiguration defaultCreateNew() {
        return (FileTransformerConfiguration)FileTransformerConfiguration.builder().fileWriteOption(FileWriteOption.CREATE_NEW).failureBehavior(FailureBehavior.DELETE).build();
    }

    public static FileTransformerConfiguration defaultCreateOrReplaceExisting() {
        return (FileTransformerConfiguration)FileTransformerConfiguration.builder().fileWriteOption(FileWriteOption.CREATE_OR_REPLACE_EXISTING).failureBehavior(FailureBehavior.LEAVE).build();
    }

    public static FileTransformerConfiguration defaultCreateOrAppend() {
        return (FileTransformerConfiguration)FileTransformerConfiguration.builder().fileWriteOption(FileWriteOption.CREATE_OR_APPEND_TO_EXISTING).failureBehavior(FailureBehavior.LEAVE).build();
    }

    public Builder toBuilder() {
        return new DefaultBuilder(this);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FileTransformerConfiguration that = (FileTransformerConfiguration)o;
        if (this.fileWriteOption != that.fileWriteOption) {
            return false;
        }
        if (this.failureBehavior != that.failureBehavior) {
            return false;
        }
        return Objects.equals(this.executorService, that.executorService);
    }

    public int hashCode() {
        int result = this.fileWriteOption != null ? this.fileWriteOption.hashCode() : 0;
        result = 31 * result + (this.failureBehavior != null ? this.failureBehavior.hashCode() : 0);
        result = 31 * result + (this.executorService != null ? this.executorService.hashCode() : 0);
        return result;
    }

    private static final class DefaultBuilder
    implements Builder {
        private FileWriteOption fileWriteOption;
        private FailureBehavior failureBehavior;
        private ExecutorService executorService;

        private DefaultBuilder() {
        }

        private DefaultBuilder(FileTransformerConfiguration fileTransformerConfiguration) {
            this.fileWriteOption = fileTransformerConfiguration.fileWriteOption;
            this.failureBehavior = fileTransformerConfiguration.failureBehavior;
            this.executorService = fileTransformerConfiguration.executorService;
        }

        @Override
        public Builder fileWriteOption(FileWriteOption fileWriteOption) {
            this.fileWriteOption = fileWriteOption;
            return this;
        }

        @Override
        public Builder failureBehavior(FailureBehavior failureBehavior) {
            this.failureBehavior = failureBehavior;
            return this;
        }

        @Override
        public Builder executorService(ExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        public FileTransformerConfiguration build() {
            return new FileTransformerConfiguration(this);
        }
    }

    public static interface Builder
    extends CopyableBuilder<Builder, FileTransformerConfiguration> {
        public Builder fileWriteOption(FileWriteOption var1);

        public Builder failureBehavior(FailureBehavior var1);

        public Builder executorService(ExecutorService var1);
    }

    public static enum FailureBehavior {
        DELETE,
        LEAVE;

    }

    public static enum FileWriteOption {
        CREATE_NEW,
        CREATE_OR_REPLACE_EXISTING,
        CREATE_OR_APPEND_TO_EXISTING;

    }
}

