/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.functions.Function2
 *  kotlin.jvm.functions.Function3
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.sequences.Sequence
 *  kotlin.sequences.SequencesKt
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.service.model;

import com.addonengine.addons.analytics.service.model.LazyFetching;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000B\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0019\n\u0002\u0010\u000b\n\u0002\b\u0010\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 =*\b\b\u0000\u0010\u0001*\u00020\u0002*\b\b\u0001\u0010\u0003*\u00020\u00022\u00020\u0002:\u0001=B\u00ae\u0001\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012>\u0010\u0007\u001a:\u0012\u0015\u0012\u0013\u0018\u00018\u0001\u00a2\u0006\f\b\t\u0012\b\b\n\u0012\u0004\b\b(\u000b\u0012\u0013\u0012\u00110\u0005\u00a2\u0006\f\b\t\u0012\b\b\n\u0012\u0004\b\b(\f\u0012\n\u0012\b\u0012\u0004\u0012\u00028\u00000\r0\b\u0012\b\u0010\u000e\u001a\u0004\u0018\u00018\u0001\u0012M\u0010\u000f\u001aI\u0012\u0015\u0012\u0013\u0018\u00018\u0001\u00a2\u0006\f\b\t\u0012\b\b\n\u0012\u0004\b\b(\u0011\u0012\u0013\u0012\u00110\u0005\u00a2\u0006\f\b\t\u0012\b\b\n\u0012\u0004\b\b(\u0012\u0012\u0013\u0012\u00118\u0000\u00a2\u0006\f\b\t\u0012\b\b\n\u0012\u0004\b\b(\u0013\u0012\u0004\u0012\u00028\u00010\u0010\u00a2\u0006\u0002\u0010\u0014J\f\u0010:\u001a\b\u0012\u0004\u0012\u00028\u00000;J\r\u0010<\u001a\u0004\u0018\u00018\u0000\u00a2\u0006\u0002\u0010!R \u0010\u0015\u001a\b\u0012\u0004\u0012\u00028\u00000\rX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u0017\"\u0004\b\u0018\u0010\u0019R\u001a\u0010\u001a\u001a\u00020\u0005X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u001c\"\u0004\b\u001d\u0010\u001eR\u001e\u0010\u001f\u001a\u0004\u0018\u00018\u0001X\u0086\u000e\u00a2\u0006\u0010\n\u0002\u0010$\u001a\u0004\b \u0010!\"\u0004\b\"\u0010#RI\u0010\u0007\u001a:\u0012\u0015\u0012\u0013\u0018\u00018\u0001\u00a2\u0006\f\b\t\u0012\b\b\n\u0012\u0004\b\b(\u000b\u0012\u0013\u0012\u00110\u0005\u00a2\u0006\f\b\t\u0012\b\b\n\u0012\u0004\b\b(\f\u0012\n\u0012\b\u0012\u0004\u0012\u00028\u00000\r0\b\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010&R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b'\u0010\u001cR\u0015\u0010\u000e\u001a\u0004\u0018\u00018\u0001\u00a2\u0006\n\n\u0002\u0010$\u001a\u0004\b(\u0010!R\u001a\u0010)\u001a\u00020*X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b+\u0010,\"\u0004\b-\u0010.R\u001a\u0010/\u001a\u00020\u0005X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b0\u0010\u001c\"\u0004\b1\u0010\u001eRX\u0010\u000f\u001aI\u0012\u0015\u0012\u0013\u0018\u00018\u0001\u00a2\u0006\f\b\t\u0012\b\b\n\u0012\u0004\b\b(\u0011\u0012\u0013\u0012\u00110\u0005\u00a2\u0006\f\b\t\u0012\b\b\n\u0012\u0004\b\b(\u0012\u0012\u0013\u0012\u00118\u0000\u00a2\u0006\f\b\t\u0012\b\b\n\u0012\u0004\b\b(\u0013\u0012\u0004\u0012\u00028\u00010\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b2\u00103R\u001e\u0010\u0012\u001a\u0004\u0018\u00010\u0005X\u0086\u000e\u00a2\u0006\u0010\n\u0002\u00108\u001a\u0004\b4\u00105\"\u0004\b6\u00107R\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b9\u0010\u001c\u00a8\u0006>"}, d2={"Lcom/addonengine/addons/analytics/service/model/LazyFetching;", "T", "", "OFFSET", "firstBatchSize", "", "subsequentBatchSize", "delegate", "Lkotlin/Function2;", "Lkotlin/ParameterName;", "name", "offset", "limit", "", "firstOffset", "nextOffset", "Lkotlin/Function3;", "previousOffset", "previousBatchSize", "lastElement", "(IILkotlin/jvm/functions/Function2;Ljava/lang/Object;Lkotlin/jvm/functions/Function3;)V", "currentBatch", "getCurrentBatch", "()Ljava/util/List;", "setCurrentBatch", "(Ljava/util/List;)V", "currentBatchSize", "getCurrentBatchSize", "()I", "setCurrentBatchSize", "(I)V", "currentOffset", "getCurrentOffset", "()Ljava/lang/Object;", "setCurrentOffset", "(Ljava/lang/Object;)V", "Ljava/lang/Object;", "getDelegate", "()Lkotlin/jvm/functions/Function2;", "getFirstBatchSize", "getFirstOffset", "firstRequestWasMade", "", "getFirstRequestWasMade", "()Z", "setFirstRequestWasMade", "(Z)V", "index", "getIndex", "setIndex", "getNextOffset", "()Lkotlin/jvm/functions/Function3;", "getPreviousBatchSize", "()Ljava/lang/Integer;", "setPreviousBatchSize", "(Ljava/lang/Integer;)V", "Ljava/lang/Integer;", "getSubsequentBatchSize", "asSequence", "Lkotlin/sequences/Sequence;", "next", "Companion", "analytics"})
public final class LazyFetching<T, OFFSET> {
    @NotNull
    public static final Companion Companion = new Companion(null);
    private final int firstBatchSize;
    private final int subsequentBatchSize;
    @NotNull
    private final Function2<OFFSET, Integer, List<T>> delegate;
    @Nullable
    private final OFFSET firstOffset;
    @NotNull
    private final Function3<OFFSET, Integer, T, OFFSET> nextOffset;
    private boolean firstRequestWasMade;
    @Nullable
    private OFFSET currentOffset;
    private int currentBatchSize;
    @Nullable
    private Integer previousBatchSize;
    private int index;
    @NotNull
    private List<? extends T> currentBatch;

    public LazyFetching(int firstBatchSize, int subsequentBatchSize, @NotNull Function2<? super OFFSET, ? super Integer, ? extends List<? extends T>> delegate, @Nullable OFFSET firstOffset, @NotNull Function3<? super OFFSET, ? super Integer, ? super T, ? extends OFFSET> nextOffset) {
        Intrinsics.checkNotNullParameter(delegate, (String)"delegate");
        Intrinsics.checkNotNullParameter(nextOffset, (String)"nextOffset");
        this.firstBatchSize = firstBatchSize;
        this.subsequentBatchSize = subsequentBatchSize;
        this.delegate = delegate;
        this.firstOffset = firstOffset;
        this.nextOffset = nextOffset;
        this.currentOffset = this.firstOffset;
        this.currentBatchSize = this.firstBatchSize;
        this.currentBatch = CollectionsKt.emptyList();
    }

    public final int getFirstBatchSize() {
        return this.firstBatchSize;
    }

    public final int getSubsequentBatchSize() {
        return this.subsequentBatchSize;
    }

    @NotNull
    public final Function2<OFFSET, Integer, List<T>> getDelegate() {
        return this.delegate;
    }

    @Nullable
    public final OFFSET getFirstOffset() {
        return this.firstOffset;
    }

    @NotNull
    public final Function3<OFFSET, Integer, T, OFFSET> getNextOffset() {
        return this.nextOffset;
    }

    public final boolean getFirstRequestWasMade() {
        return this.firstRequestWasMade;
    }

    public final void setFirstRequestWasMade(boolean bl) {
        this.firstRequestWasMade = bl;
    }

    @Nullable
    public final OFFSET getCurrentOffset() {
        return this.currentOffset;
    }

    public final void setCurrentOffset(@Nullable OFFSET OFFSET) {
        this.currentOffset = OFFSET;
    }

    public final int getCurrentBatchSize() {
        return this.currentBatchSize;
    }

    public final void setCurrentBatchSize(int n) {
        this.currentBatchSize = n;
    }

    @Nullable
    public final Integer getPreviousBatchSize() {
        return this.previousBatchSize;
    }

    public final void setPreviousBatchSize(@Nullable Integer n) {
        this.previousBatchSize = n;
    }

    public final int getIndex() {
        return this.index;
    }

    public final void setIndex(int n) {
        this.index = n;
    }

    @NotNull
    public final List<T> getCurrentBatch() {
        return this.currentBatch;
    }

    public final void setCurrentBatch(@NotNull List<? extends T> list) {
        Intrinsics.checkNotNullParameter(list, (String)"<set-?>");
        this.currentBatch = list;
    }

    @NotNull
    public final Sequence<T> asSequence() {
        return SequencesKt.generateSequence((Function0)new Function0<T>(this){
            final /* synthetic */ LazyFetching<T, OFFSET> this$0;
            {
                this.this$0 = $receiver;
                super(0);
            }

            @Nullable
            public final T invoke() {
                return this.this$0.next();
            }
        });
    }

    @Nullable
    public final T next() {
        if (this.index >= this.currentBatch.size()) {
            if (this.firstRequestWasMade) {
                Integer n = this.previousBatchSize;
                Intrinsics.checkNotNull((Object)n);
                this.currentOffset = this.nextOffset.invoke(this.currentOffset, (Object)n, CollectionsKt.last(this.currentBatch));
            }
            this.currentBatch = (List)this.delegate.invoke(this.currentOffset, (Object)this.currentBatchSize);
            this.previousBatchSize = this.currentBatchSize;
            this.currentBatchSize = this.subsequentBatchSize;
            this.firstRequestWasMade = true;
            this.index = 0;
            if (this.currentBatch.isEmpty()) {
                return null;
            }
        }
        int n = this.index;
        this.index = n + 1;
        return this.currentBatch.get(n);
    }

    @Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002Jj\u0010\u0003\u001a\u000e\u0012\u0004\u0012\u0002H\u0005\u0012\u0004\u0012\u00020\u00060\u0004\"\b\b\u0002\u0010\u0005*\u00020\u00012\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\u00062<\u0010\t\u001a8\u0012\u0013\u0012\u00110\u0006\u00a2\u0006\f\b\u000b\u0012\b\b\f\u0012\u0004\b\b(\r\u0012\u0013\u0012\u00110\u0006\u00a2\u0006\f\b\u000b\u0012\b\b\f\u0012\u0004\b\b(\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00050\u000f0\nJ\u008a\u0001\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u0002H\u0005\u0012\u0004\u0012\u0002H\u00110\u0004\"\b\b\u0002\u0010\u0005*\u00020\u0001\"\b\b\u0003\u0010\u0011*\u00020\u00012\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\u00062>\u0010\t\u001a:\u0012\u0015\u0012\u0013\u0018\u0001H\u0011\u00a2\u0006\f\b\u000b\u0012\b\b\f\u0012\u0004\b\b(\r\u0012\u0013\u0012\u00110\u0006\u00a2\u0006\f\b\u000b\u0012\b\b\f\u0012\u0004\b\b(\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00050\u000f0\n2\u0012\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u0002H\u0005\u0012\u0004\u0012\u0002H\u00110\u0013\u00a8\u0006\u0014"}, d2={"Lcom/addonengine/addons/analytics/service/model/LazyFetching$Companion;", "", "()V", "numerical", "Lcom/addonengine/addons/analytics/service/model/LazyFetching;", "T", "", "firstBatchSize", "subsequentBatchSize", "delegate", "Lkotlin/Function2;", "Lkotlin/ParameterName;", "name", "offset", "limit", "", "seek", "OFFSET", "nextPageToken", "Lkotlin/Function1;", "analytics"})
    public static final class Companion {
        private Companion() {
        }

        @NotNull
        public final <T> LazyFetching<T, Integer> numerical(int firstBatchSize, int subsequentBatchSize, @NotNull Function2<? super Integer, ? super Integer, ? extends List<? extends T>> delegate) {
            Intrinsics.checkNotNullParameter(delegate, (String)"delegate");
            return new LazyFetching(firstBatchSize, subsequentBatchSize, (Function2)new Function2<Integer, Integer, List<? extends T>>(delegate){
                final /* synthetic */ Function2<Integer, Integer, List<T>> $delegate;
                {
                    this.$delegate = $delegate;
                    super(2);
                }

                @NotNull
                public final List<T> invoke(@Nullable Integer a, int b) {
                    Integer n = a;
                    return (List)this.$delegate.invoke((Object)(n != null ? n : 0), (Object)b);
                }
            }, 0, numerical.2.INSTANCE);
        }

        @NotNull
        public final <T, OFFSET> LazyFetching<T, OFFSET> seek(int firstBatchSize, int subsequentBatchSize, @NotNull Function2<? super OFFSET, ? super Integer, ? extends List<? extends T>> delegate, @NotNull Function1<? super T, ? extends OFFSET> nextPageToken) {
            Intrinsics.checkNotNullParameter(delegate, (String)"delegate");
            Intrinsics.checkNotNullParameter(nextPageToken, (String)"nextPageToken");
            return new LazyFetching(firstBatchSize, subsequentBatchSize, delegate, null, (Function3)new Function3<OFFSET, Integer, T, OFFSET>(nextPageToken){
                final /* synthetic */ Function1<T, OFFSET> $nextPageToken;
                {
                    this.$nextPageToken = $nextPageToken;
                    super(3);
                }

                @NotNull
                public final OFFSET invoke(@Nullable OFFSET prevOffset, int prevBatchSize, @NotNull T lastElement) {
                    Intrinsics.checkNotNullParameter(lastElement, (String)"lastElement");
                    return (OFFSET)this.$nextPageToken.invoke(lastElement);
                }
            });
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

