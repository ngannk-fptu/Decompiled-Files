/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.base.Splitter
 *  com.google.common.base.Strings
 *  com.google.common.collect.Lists
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.edgeindex.rest;

import com.atlassian.confluence.plugins.edgeindex.EdgeQueries;
import com.atlassian.confluence.plugins.edgeindex.EdgeQueryParameter;
import com.atlassian.confluence.plugins.edgeindex.EdgeTypeRepository;
import com.atlassian.confluence.plugins.edgeindex.ScoreConfig;
import com.atlassian.confluence.plugins.edgeindex.model.EdgeTargetInfo;
import com.atlassian.confluence.plugins.edgeindex.model.EdgeType;
import com.atlassian.confluence.plugins.edgeindex.rest.StreamItemFactory;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;

@Path(value="/stream")
public class StreamResource {
    private static final int DEFAULT_ITEMS_PER_PAGE = 10;
    private static final long DEFAULT_POPULAR_STREAM_DAYS = 7L;
    private final TransactionTemplate transactionTemplate;
    private final EdgeQueries edgeQueries;
    private final EdgeTypeRepository edgeTypeRepository;
    private final StreamItemFactory streamItemFactory;

    public StreamResource(TransactionTemplate transactionTemplate, EdgeQueries edgeQueries, EdgeTypeRepository edgeTypeRepository, StreamItemFactory streamItemFactory) {
        this.transactionTemplate = transactionTemplate;
        this.edgeQueries = edgeQueries;
        this.edgeTypeRepository = edgeTypeRepository;
        this.streamItemFactory = streamItemFactory;
    }

    @GET
    @Path(value="/content")
    @Consumes(value={"application/json", "application/x-www-form-urlencoded"})
    @Produces(value={"application/json"})
    @AnonymousAllowed
    public Response getMostPopular(@QueryParam(value="days") Long days, @QueryParam(value="pageSize") Integer pageSizeQueryParam, @QueryParam(value="excerpt") @DefaultValue(value="false") boolean excerpt, @QueryParam(value="thumbnails") @DefaultValue(value="false") boolean thumbnails, @QueryParam(value="nextPageOffset") String nextPageOffset, @QueryParam(value="debug") @DefaultValue(value="false") boolean debug, @QueryParam(value="followeeEdge") Float followeeEdge, @QueryParam(value="commentCreateEdge") Float commentCreateEdge, @QueryParam(value="likeCreateEdge") Float likeCreateEdge, @QueryParam(value="timeDecayBase") Float timeDecayBase, @QueryParam(value="urlStrategy") @DefaultValue(value="desktop") String urlStrategy, @QueryParam(value="edgeTypes") String edgeTypes) {
        ScoreConfig scoreConfig = new ScoreConfig();
        if (followeeEdge != null) {
            scoreConfig.setFolloweeEdge(followeeEdge.floatValue());
        }
        if (commentCreateEdge != null) {
            scoreConfig.setScore((EdgeType)this.edgeTypeRepository.getEdgeIndexTypeByKey("comment.create").get(), commentCreateEdge.floatValue());
        }
        if (likeCreateEdge != null) {
            scoreConfig.setScore((EdgeType)this.edgeTypeRepository.getEdgeIndexTypeByKey("like.create").get(), likeCreateEdge.floatValue());
        }
        if (timeDecayBase != null) {
            scoreConfig.setTimeDecayBase(timeDecayBase.floatValue());
        }
        int pageSize = pageSizeQueryParam == null ? 10 : pageSizeQueryParam;
        int offset = 0;
        if (StringUtils.isNotBlank((CharSequence)nextPageOffset)) {
            try {
                offset = Integer.parseInt(nextPageOffset);
            }
            catch (NumberFormatException e) {
                return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)("Invalid nextPageOffset: " + nextPageOffset)).build();
            }
        }
        EdgeQueryParameter queryParameter = EdgeQueryParameter.builder().since(days == null ? 7L : days, TimeUnit.DAYS).withEdgeTypes(!Strings.isNullOrEmpty((String)edgeTypes) ? Lists.newArrayList((Iterable)Splitter.on((char)',').split((CharSequence)edgeTypes)) : null).withScoreConfig(scoreConfig).withMaxEdgeInfo(offset + pageSize + 1).withAcceptFilter(targetInfo -> !"mobile".equalsIgnoreCase(urlStrategy) || !"attachment".equalsIgnoreCase(targetInfo.getTargetType())).build();
        List<EdgeTargetInfo> edgeTargetInfoList = this.edgeQueries.getMostPopular(queryParameter);
        HashMap<String, Object> result = new HashMap<String, Object>();
        if (edgeTargetInfoList.size() == offset + pageSize + 1) {
            result.put("nextPageOffset", offset + pageSize);
        }
        List<EdgeTargetInfo> edgeTargetInfoSubList = edgeTargetInfoList.subList(offset, Math.min(offset + pageSize, edgeTargetInfoList.size()));
        List streamItems = (List)this.transactionTemplate.execute(() -> this.streamItemFactory.fromEdgeTargetInfos(edgeTargetInfoSubList, urlStrategy, debug));
        result.put("streamItems", streamItems);
        return Response.ok(result).build();
    }
}

