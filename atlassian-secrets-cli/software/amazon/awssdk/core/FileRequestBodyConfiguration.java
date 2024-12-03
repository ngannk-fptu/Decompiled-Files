/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core;

import java.nio.file.Path;
import java.util.Objects;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
public final class FileRequestBodyConfiguration
implements ToCopyableBuilder<Builder, FileRequestBodyConfiguration> {
    private final Integer chunkSizeInBytes;
    private final Long position;
    private final Long numBytesToRead;
    private final Path path;

    private FileRequestBodyConfiguration(DefaultBuilder builder) {
        this.path = Validate.notNull(builder.path, "path", new Object[0]);
        this.chunkSizeInBytes = Validate.isPositiveOrNull(builder.chunkSizeInBytes, "chunkSizeInBytes");
        this.position = Validate.isNotNegativeOrNull(builder.position, "position");
        this.numBytesToRead = Validate.isNotNegativeOrNull(builder.numBytesToRead, "numBytesToRead");
    }

    public static Builder builder() {
        return new DefaultBuilder();
    }

    public Integer chunkSizeInBytes() {
        return this.chunkSizeInBytes;
    }

    public Long position() {
        return this.position;
    }

    public Long numBytesToRead() {
        return this.numBytesToRead;
    }

    public Path path() {
        return this.path;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FileRequestBodyConfiguration that = (FileRequestBodyConfiguration)o;
        if (!Objects.equals(this.chunkSizeInBytes, that.chunkSizeInBytes)) {
            return false;
        }
        if (!Objects.equals(this.position, that.position)) {
            return false;
        }
        if (!Objects.equals(this.numBytesToRead, that.numBytesToRead)) {
            return false;
        }
        return Objects.equals(this.path, that.path);
    }

    public int hashCode() {
        int result = this.chunkSizeInBytes != null ? this.chunkSizeInBytes.hashCode() : 0;
        result = 31 * result + (this.position != null ? this.position.hashCode() : 0);
        result = 31 * result + (this.numBytesToRead != null ? this.numBytesToRead.hashCode() : 0);
        result = 31 * result + (this.path != null ? this.path.hashCode() : 0);
        return result;
    }

    @Override
    public Builder toBuilder() {
        return new DefaultBuilder(this);
    }

    private static final class DefaultBuilder
    implements Builder {
        private Long position;
        private Path path;
        private Integer chunkSizeInBytes;
        private Long numBytesToRead;

        private DefaultBuilder(FileRequestBodyConfiguration configuration) {
            this.position = configuration.position;
            this.path = configuration.path;
            this.chunkSizeInBytes = configuration.chunkSizeInBytes;
            this.numBytesToRead = configuration.numBytesToRead;
        }

        private DefaultBuilder() {
        }

        @Override
        public Builder path(Path path) {
            this.path = path;
            return this;
        }

        @Override
        public Builder chunkSizeInBytes(Integer chunkSizeInBytes) {
            this.chunkSizeInBytes = chunkSizeInBytes;
            return this;
        }

        @Override
        public Builder position(Long position) {
            this.position = position;
            return this;
        }

        @Override
        public Builder numBytesToRead(Long numBytesToRead) {
            this.numBytesToRead = numBytesToRead;
            return this;
        }

        @Override
        public FileRequestBodyConfiguration build() {
            return new FileRequestBodyConfiguration(this);
        }
    }

    public static interface Builder
    extends CopyableBuilder<Builder, FileRequestBodyConfiguration> {
        public Builder path(Path var1);

        public Builder chunkSizeInBytes(Integer var1);

        public Builder position(Long var1);

        public Builder numBytesToRead(Long var1);
    }
}

