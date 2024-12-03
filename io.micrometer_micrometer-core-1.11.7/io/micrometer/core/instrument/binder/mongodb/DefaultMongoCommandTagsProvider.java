/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mongodb.event.CommandEvent
 *  com.mongodb.event.CommandStartedEvent
 *  com.mongodb.event.CommandSucceededEvent
 *  io.micrometer.common.util.StringUtils
 *  io.micrometer.common.util.internal.logging.WarnThenDebugLogger
 *  org.bson.BsonDocument
 *  org.bson.BsonString
 *  org.bson.BsonValue
 */
package io.micrometer.core.instrument.binder.mongodb;

import com.mongodb.event.CommandEvent;
import com.mongodb.event.CommandStartedEvent;
import com.mongodb.event.CommandSucceededEvent;
import io.micrometer.common.util.StringUtils;
import io.micrometer.common.util.internal.logging.WarnThenDebugLogger;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.mongodb.MongoCommandTagsProvider;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.BsonValue;

public class DefaultMongoCommandTagsProvider
implements MongoCommandTagsProvider {
    private static final Set<String> COMMANDS_WITH_COLLECTION_NAME = new HashSet<String>(Arrays.asList("aggregate", "count", "distinct", "mapReduce", "geoSearch", "delete", "find", "findAndModify", "insert", "update", "collMod", "compact", "convertToCapped", "create", "createIndexes", "drop", "dropIndexes", "killCursors", "listIndexes", "reIndex"));
    private static final WarnThenDebugLogger WARN_THEN_DEBUG_LOGGER = new WarnThenDebugLogger(DefaultMongoCommandTagsProvider.class);
    private final ConcurrentMap<Integer, String> inFlightCommandCollectionNames = new ConcurrentHashMap<Integer, String>();

    @Override
    public Iterable<Tag> commandTags(CommandEvent event) {
        return Tags.of(Tag.of("command", event.getCommandName()), Tag.of("collection", this.getAndRemoveCollectionNameForCommand(event)), Tag.of("cluster.id", event.getConnectionDescription().getConnectionId().getServerId().getClusterId().getValue()), Tag.of("server.address", event.getConnectionDescription().getServerAddress().toString()), Tag.of("status", event instanceof CommandSucceededEvent ? "SUCCESS" : "FAILED"));
    }

    @Override
    public void commandStarted(CommandStartedEvent event) {
        this.determineCollectionName(event.getCommandName(), event.getCommand()).ifPresent(collectionName -> this.addCollectionNameForCommand((CommandEvent)event, (String)collectionName));
    }

    private void addCollectionNameForCommand(CommandEvent event, String collectionName) {
        if (this.inFlightCommandCollectionNames.size() < 1000) {
            this.inFlightCommandCollectionNames.put(event.getRequestId(), collectionName);
            return;
        }
        WARN_THEN_DEBUG_LOGGER.log("Collection names cache is full - Mongo is not calling listeners properly");
    }

    private String getAndRemoveCollectionNameForCommand(CommandEvent event) {
        String collectionName = (String)this.inFlightCommandCollectionNames.remove(event.getRequestId());
        return collectionName != null ? collectionName : "unknown";
    }

    protected Optional<String> determineCollectionName(String commandName, BsonDocument command) {
        Optional<String> collectionName;
        if (COMMANDS_WITH_COLLECTION_NAME.contains(commandName) && (collectionName = this.getNonEmptyBsonString(command.get((Object)commandName))).isPresent()) {
            return collectionName;
        }
        return this.getNonEmptyBsonString(command.get((Object)"collection"));
    }

    private Optional<String> getNonEmptyBsonString(BsonValue bsonValue) {
        return Optional.ofNullable(bsonValue).filter(BsonValue::isString).map(BsonValue::asString).map(BsonString::getValue).map(String::trim).filter(StringUtils::isNotEmpty);
    }
}

