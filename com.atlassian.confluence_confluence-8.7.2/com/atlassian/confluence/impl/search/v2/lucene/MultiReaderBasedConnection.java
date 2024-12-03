/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.internal.search.v2.lucene.DefaultConfiguration
 *  com.atlassian.confluence.internal.search.v2.lucene.ILuceneConnection
 *  com.atlassian.confluence.internal.search.v2.lucene.ILuceneConnection$Configuration
 *  com.atlassian.confluence.internal.search.v2.lucene.ILuceneConnection$ReaderAction
 *  com.atlassian.confluence.internal.search.v2.lucene.ILuceneConnection$SearcherAction
 *  com.atlassian.confluence.internal.search.v2.lucene.ILuceneConnection$SearcherWithTokenAction
 *  com.atlassian.confluence.internal.search.v2.lucene.LuceneException
 *  com.atlassian.confluence.internal.search.v2.lucene.SearchTokenExpiredException
 *  com.google.common.base.Preconditions
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.collect.ImmutableList
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.index.MultiReader
 *  org.apache.lucene.search.IndexSearcher
 */
package com.atlassian.confluence.impl.search.v2.lucene;

import com.atlassian.confluence.internal.search.v2.lucene.DefaultConfiguration;
import com.atlassian.confluence.internal.search.v2.lucene.ILuceneConnection;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneException;
import com.atlassian.confluence.internal.search.v2.lucene.MultiConnection;
import com.atlassian.confluence.internal.search.v2.lucene.SearchTokenExpiredException;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.search.IndexSearcher;

public class MultiReaderBasedConnection
implements MultiConnection {
    private final AtomicLong currentSearchToken = new AtomicLong();
    private final Cache<Long, TokenDetails> issuedTokens;
    private final Map<SearchIndex, ILuceneConnection> connectionByIndex;

    public MultiReaderBasedConnection(SearchIndex index, ILuceneConnection connection) {
        this(Collections.singletonMap(index, connection));
    }

    public MultiReaderBasedConnection(Map<SearchIndex, ILuceneConnection> connectionByIndex) {
        this((ILuceneConnection.Configuration)new DefaultConfiguration(), connectionByIndex);
    }

    public MultiReaderBasedConnection(ILuceneConnection.Configuration connectionConfig, Map<SearchIndex, ILuceneConnection> connectionByIndex) {
        this.connectionByIndex = connectionByIndex;
        this.issuedTokens = CacheBuilder.newBuilder().expireAfterWrite(connectionConfig.getIndexSearcherMaxAge(), TimeUnit.SECONDS).maximumSize(100000L).build();
    }

    @Override
    public Object withReader(EnumSet<SearchIndex> indexes, ILuceneConnection.ReaderAction action) throws LuceneException {
        return this.applyToReaders(indexes, readers -> {
            Object object;
            block8: {
                MultiReader multiReader = this.multiReader((List<IndexReader>)readers);
                try {
                    object = action.perform((IndexReader)multiReader);
                    if (multiReader == null) break block8;
                }
                catch (Throwable throwable) {
                    try {
                        if (multiReader != null) {
                            try {
                                multiReader.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    catch (IOException e) {
                        throw new LuceneException((Throwable)e);
                    }
                }
                multiReader.close();
            }
            return object;
        });
    }

    @Override
    public void withSearch(EnumSet<SearchIndex> indexes, ILuceneConnection.SearcherAction action) throws LuceneException {
        this.applyToReaders(indexes, readers -> {
            Object var5_7;
            block8: {
                MultiReader multiReader = this.multiReader((List<IndexReader>)readers);
                try {
                    IndexSearcher multiSearcher = new IndexSearcher((IndexReader)multiReader);
                    action.perform(multiSearcher);
                    var5_7 = null;
                    if (multiReader == null) break block8;
                }
                catch (Throwable throwable) {
                    try {
                        if (multiReader != null) {
                            try {
                                multiReader.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    catch (IOException e) {
                        throw new LuceneException((Throwable)e);
                    }
                }
                multiReader.close();
            }
            return var5_7;
        });
    }

    @Override
    public <T> T withSearcher(EnumSet<SearchIndex> indexes, ILuceneConnection.SearcherWithTokenAction<T> action) {
        return (T)this.applyToReadersWithNewToken(indexes, (tokens, readers) -> {
            Object object;
            block8: {
                long newToken = this.generateToken();
                this.issuedTokens.put((Object)newToken, (Object)new TokenDetails(indexes, (List<Long>)tokens));
                MultiReader multiReader = this.multiReader((List<IndexReader>)readers);
                try {
                    object = action.perform(new IndexSearcher((IndexReader)multiReader), newToken);
                    if (multiReader == null) break block8;
                }
                catch (Throwable throwable) {
                    try {
                        if (multiReader != null) {
                            try {
                                multiReader.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    catch (IOException e) {
                        throw new LuceneException((Throwable)e);
                    }
                }
                multiReader.close();
            }
            return object;
        });
    }

    @Override
    public <T> T withSearcher(EnumSet<SearchIndex> indexes, long searchToken, ILuceneConnection.SearcherWithTokenAction<T> action) throws SearchTokenExpiredException {
        TokenDetails tokenDetails = (TokenDetails)this.issuedTokens.getIfPresent((Object)searchToken);
        if (tokenDetails == null) {
            throw new SearchTokenExpiredException(searchToken);
        }
        Preconditions.checkArgument((boolean)indexes.equals(tokenDetails.targetedIndexes), (Object)("Targeted indexes " + indexes + " are not equal to the targeted indexes of the provided token " + tokenDetails.targetedIndexes));
        return (T)this.applyToReadersWithExistingToken(indexes, tokenDetails.childTokens, (tokens, readers) -> {
            Object object;
            block8: {
                MultiReader multiReader = this.multiReader((List<IndexReader>)readers);
                try {
                    object = action.perform(new IndexSearcher((IndexReader)multiReader), searchToken);
                    if (multiReader == null) break block8;
                }
                catch (Throwable throwable) {
                    try {
                        if (multiReader != null) {
                            try {
                                multiReader.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    catch (IOException e) {
                        throw new LuceneException((Throwable)e);
                    }
                }
                multiReader.close();
            }
            return object;
        });
    }

    private long generateToken() {
        return this.currentSearchToken.incrementAndGet();
    }

    private MultiReader multiReader(List<IndexReader> readers) {
        return new MultiReader(readers.toArray(new IndexReader[0]), false);
    }

    private <T> T applyToReadersWithExistingToken(EnumSet<SearchIndex> indexes, List<Long> tokens, BiFunction<List<Long>, List<IndexReader>, T> searchAction) throws SearchTokenExpiredException {
        Preconditions.checkArgument((indexes.size() == tokens.size() ? 1 : 0) != 0, (Object)"The number of tokens should correspond to the number of connections");
        List<ILuceneConnection> childConnections = this.connectionByIndex.entrySet().stream().filter(e -> indexes.contains(e.getKey())).map(Map.Entry::getValue).collect(Collectors.toList());
        return this.applyToReadersWithExistingToken(childConnections, tokens, Collections.emptyList(), Collections.emptyList(), searchAction);
    }

    private <T> T applyToReadersWithExistingToken(List<ILuceneConnection> childConnections, List<Long> requestedTokens, List<Long> childTokens, List<IndexReader> readers, BiFunction<List<Long>, List<IndexReader>, T> searchAction) throws SearchTokenExpiredException {
        if (childConnections.isEmpty()) {
            return searchAction.apply(childTokens, readers);
        }
        ILuceneConnection firstConnection = childConnections.get(0);
        Long firstRequestedToken = requestedTokens.get(0);
        try {
            return (T)firstConnection.withSearcher(firstRequestedToken.longValue(), (searcher, token) -> {
                List<ILuceneConnection> connectionsTail = childConnections.subList(1, childConnections.size());
                List<Long> tokensTail = requestedTokens.subList(1, requestedTokens.size());
                ImmutableList withNewToken = ImmutableList.builder().add((Object)token).addAll((Iterable)childTokens).build();
                ImmutableList withNewReader = ImmutableList.builder().add((Object)searcher.getIndexReader()).addAll((Iterable)readers).build();
                try {
                    return this.applyToReadersWithExistingToken(connectionsTail, tokensTail, (List<Long>)withNewToken, (List<IndexReader>)withNewReader, searchAction);
                }
                catch (SearchTokenExpiredException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        catch (RuntimeException e) {
            if (e.getCause() instanceof SearchTokenExpiredException) {
                throw (SearchTokenExpiredException)e.getCause();
            }
            throw e;
        }
    }

    private <T> T applyToReadersWithNewToken(EnumSet<SearchIndex> indexes, BiFunction<List<Long>, List<IndexReader>, T> searchAction) {
        List<ILuceneConnection> childConnections = this.connectionByIndex.entrySet().stream().filter(e -> indexes.contains(e.getKey())).map(Map.Entry::getValue).collect(Collectors.toList());
        return this.applyToReadersWithNewToken(childConnections, Collections.emptyList(), Collections.emptyList(), searchAction);
    }

    private <T> T applyToReadersWithNewToken(List<ILuceneConnection> childConnections, List<Long> tokens, List<IndexReader> readers, BiFunction<List<Long>, List<IndexReader>, T> searchAction) {
        if (childConnections.isEmpty()) {
            return searchAction.apply(tokens, readers);
        }
        ILuceneConnection firstConnection = childConnections.get(0);
        return (T)firstConnection.withSearcher((searcher, token) -> {
            List<ILuceneConnection> connectionsTail = childConnections.subList(1, childConnections.size());
            ImmutableList withNewToken = ImmutableList.builder().add((Object)token).addAll((Iterable)tokens).build();
            ImmutableList withNewReader = ImmutableList.builder().add((Object)searcher.getIndexReader()).addAll((Iterable)readers).build();
            return this.applyToReadersWithNewToken(connectionsTail, (List<Long>)withNewToken, (List<IndexReader>)withNewReader, searchAction);
        });
    }

    private Object applyToReaders(EnumSet<SearchIndex> indexes, Function<List<IndexReader>, Object> readersConsumer) {
        List<ILuceneConnection> childConnections = this.connectionByIndex.entrySet().stream().filter(e -> indexes.contains(e.getKey())).map(Map.Entry::getValue).collect(Collectors.toList());
        return this.applyToReaders(childConnections, Collections.emptyList(), readersConsumer);
    }

    private Object applyToReaders(List<ILuceneConnection> childConnections, List<IndexReader> readers, Function<List<IndexReader>, Object> readersConsumer) {
        if (childConnections.isEmpty()) {
            return readersConsumer.apply(readers);
        }
        ILuceneConnection firstConnection = childConnections.get(0);
        return firstConnection.withReader(firstReader -> {
            List<ILuceneConnection> connectionsTail = childConnections.subList(1, childConnections.size());
            ImmutableList withNewReader = ImmutableList.builder().add((Object)firstReader).addAll((Iterable)readers).build();
            return this.applyToReaders(connectionsTail, (List<IndexReader>)withNewReader, readersConsumer);
        });
    }

    private static class TokenDetails {
        private final EnumSet<SearchIndex> targetedIndexes;
        private final List<Long> childTokens;

        public TokenDetails(EnumSet<SearchIndex> targetedIndexes, List<Long> childTokens) {
            this.targetedIndexes = targetedIndexes;
            this.childTokens = childTokens;
        }
    }
}

