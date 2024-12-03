/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.confluence.api.model.content.Container
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.Label
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.content.SpaceType
 *  com.atlassian.confluence.api.service.content.ContentLabelService
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.api.service.content.SpaceService
 *  com.atlassian.confluence.content.render.xhtml.model.inline.Emoticon
 *  com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor
 *  com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor$Propagation
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.feature.RequiresDarkFeature
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.google.common.collect.Iterators
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  org.apache.commons.lang3.RandomStringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.masterdetail.rest;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.confluence.api.model.content.Container;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.Label;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.content.SpaceType;
import com.atlassian.confluence.api.service.content.ContentLabelService;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.api.service.content.SpaceService;
import com.atlassian.confluence.content.render.xhtml.model.inline.Emoticon;
import com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.feature.RequiresDarkFeature;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.collect.Iterators;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.StringJoiner;
import java.util.stream.IntStream;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AnonymousAllowed
@Path(value="detailssummarytest")
@RequiresDarkFeature(value={"masterdetail.test"})
public class BulkTestDataResource {
    private final Logger logger = LoggerFactory.getLogger(BulkTestDataResource.class);
    private final ContentService contentService;
    private final SpaceService spaceService;
    private final ContentLabelService contentLabelService;
    private final TransactionalHostContextAccessor transactionalHostContextAccessor;
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private static final Random rnd = new Random();

    public BulkTestDataResource(@ComponentImport ContentService contentService, @ComponentImport SpaceService spaceService, @ComponentImport ContentLabelService contentLabelService, @ComponentImport TransactionalHostContextAccessor transactionalHostContextAccessor) {
        this.contentService = contentService;
        this.spaceService = spaceService;
        this.contentLabelService = contentLabelService;
        this.transactionalHostContextAccessor = transactionalHostContextAccessor;
    }

    @GET
    @Path(value="create")
    @Produces(value={"application/json"})
    public String easyCreateTestData() {
        this.createTestData(RandomStringUtils.randomAlphabetic((int)8), false, 20, 100, 3, null, false);
        return "Done";
    }

    @POST
    @Path(value="create")
    @XsrfProtectionExcluded
    @Produces(value={"application/json"})
    public void createTestData(@QueryParam(value="keyIndex") String keyIndex, @QueryParam(value="blog") @DefaultValue(value="false") boolean blog, @QueryParam(value="pagesPerLabel") @DefaultValue(value="500") int pagesPerLabel, @QueryParam(value="totalPages") @DefaultValue(value="3000") int totalPages, @QueryParam(value="propertyMacrosPerPage") @DefaultValue(value="1") int propertyMacrosPerPage, @QueryParam(value="reverseSort") @DefaultValue(value="null") Boolean reverseSort, @QueryParam(value="verticalHeadings") @DefaultValue(value="false") boolean verticalHeadings) {
        ContentType contentType = blog ? ContentType.BLOG_POST : ContentType.PAGE;
        Space space = Space.builder().key("PPR" + keyIndex).type(SpaceType.GLOBAL).name("Page Property Report " + keyIndex).description(ContentRepresentation.PLAIN, "Page Property Report").build();
        this.transactionalHostContextAccessor.doInTransaction(TransactionalHostContextAccessor.Propagation.REQUIRES_NEW, () -> this.spaceService.create(space, false));
        StringJoiner labelQuery = new StringJoiner(",");
        for (int i = 0; i < totalPages; i += pagesPerLabel) {
            this.logger.info("Number of page created {}", (Object)i);
            this.createPages(keyIndex, pagesPerLabel, propertyMacrosPerPage, verticalHeadings, contentType, space, i);
            labelQuery.add("label" + i);
            int index = i;
            this.transactionalHostContextAccessor.doInTransaction(TransactionalHostContextAccessor.Propagation.REQUIRES_NEW, () -> this.contentService.create(Content.builder((ContentType)contentType).title(String.format("Page Property Report %s-%s", keyIndex, index)).body(String.format("<p><ac:structured-macro ac:name=\"detailssummary\"><ac:parameter ac:name=\"pageSize\">500</ac:parameter>%s<ac:parameter ac:name=\"cql\">label in (%s); and space = currentSpace()</ac:parameter></ac:structured-macro></p>", reverseSort != null ? String.format("<ac:parameter ac:name=\"sortBy\">property one</ac:parameter><ac:parameter ac:name=\"reverseSort\">%s</ac:parameter>", reverseSort) : "", labelQuery), ContentRepresentation.STORAGE).space(space).container((Container)space).build()));
        }
    }

    private void createPages(String keyIndex, int pagesPerLabel, int propertyMacrosPerPage, boolean verticalHeadings, ContentType contentType, Space space, int i) {
        int index = i;
        Iterators.partition((Iterator)IntStream.range(1, pagesPerLabel).iterator(), (int)100).forEachRemaining(commitBatch -> {
            this.logger.info("Processing batch {}", commitBatch);
            this.transactionalHostContextAccessor.doInTransaction(TransactionalHostContextAccessor.Propagation.REQUIRES_NEW, () -> {
                this.logger.info("Start new transaction from {}", commitBatch.get(0));
                commitBatch.forEach(id -> {
                    Content newContent = this.contentService.create(Content.builder((ContentType)contentType).title(String.format("Page Property %s %s-%s", keyIndex, index, id)).body(this.generatePagePropertyBody(propertyMacrosPerPage, verticalHeadings), ContentRepresentation.STORAGE).space(space).container((Container)space).build());
                    this.contentLabelService.addLabels(newContent.getId(), Collections.singletonList(Label.builder((String)("label" + index)).build()));
                });
                this.logger.info("Commit new transaction: {}", commitBatch.get(commitBatch.size() - 1));
                return null;
            });
        });
    }

    private String generatePagePropertyBody(int propertyMacrosPerPage, boolean verticalHeadings) {
        Emoticon[] emoticons = Emoticon.values();
        StringJoiner bodyStringJoiner = new StringJoiner("\n");
        for (int i = 1; i <= propertyMacrosPerPage; ++i) {
            String[] propertyValues = new String[]{String.format("%s <ac:structured-macro ac:name=\"status\" ac:schema-version=\"1\"><ac:parameter ac:name=\"colour\">Blue</ac:parameter><ac:parameter ac:name=\"title\">%s</ac:parameter> </ac:structured-macro><time datetime=\"%s\" />", RandomStringUtils.random((int)16, (boolean)true, (boolean)true), RandomStringUtils.random((int)8, (boolean)true, (boolean)true), format.format(new Date(-946771200000L + Math.abs(rnd.nextLong()) % 2522880000000L))), String.format("%s <ac:emoticon ac:name=\"%s\" />", RandomStringUtils.random((int)32, (boolean)true, (boolean)true), emoticons[Math.abs(rnd.nextInt() % emoticons.length)].getType()), String.format("%s <ac:structured-macro ac:name=\"note\" ac:schema-version=\"1\" ac:macro-id=\"4d37e61b-e40f-4e1f-a382-0525c1be58b3\"><ac:parameter ac:name=\"title\">Testing</ac:parameter><ac:rich-text-body><p>testing</p></ac:rich-text-body></ac:structured-macro>", RandomStringUtils.random((int)24, (boolean)true, (boolean)true))};
            String propertyTable = verticalHeadings ? String.format("<tr><th>property one</th><th>property two</th></tr><tr><td>%s</td><td>%s</td></tr>", propertyValues[0], propertyValues[1]) : String.format("<tr><td>property one</td><td>%s</td></tr><tr><td>property two</td><td>%s</td></tr><tr><td>property three</td><td>%s</td></tr>", propertyValues);
            bodyStringJoiner.add(String.format("<p><ac:structured-macro ac:name=\"details\"><ac:parameter ac:name=\"id\">%s</ac:parameter><ac:rich-text-body><table><colgroup><col/><col/></colgroup><tbody>%s</tbody></table></ac:rich-text-body></ac:structured-macro></p>", i, propertyTable));
        }
        return bodyStringJoiner.toString();
    }
}

