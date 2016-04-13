/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.mongodb.morphia.internal;

import org.apache.commons.configuration.Configuration;
import org.seedstack.mongodb.morphia.MorphiaDatastore;
import org.seedstack.seed.Application;
import org.seedstack.seed.SeedException;

public final class MorphiaUtils {
    private MorphiaUtils() {

    }

    /**
     * Returns an instance of the annotation MorphiaDatastore if the morphia configuration is ok.
     *
     * @param application  Application
     * @param morphiaClass persistent morphia object
     * @return MorphiaDatastore
     */
    public static MorphiaDatastore getMongoDatastore(Application application, Class<?> morphiaClass) {
        Configuration morphiaEntityConfiguration = application.getConfiguration(morphiaClass).subset("morphia");
        if (morphiaEntityConfiguration.isEmpty()) {
            throw SeedException.createNew(MorphiaErrorCodes.UNKNOW_DATASTORE_CONFIGURATION)
                    .put("aggregate", morphiaClass.getName());
        }

        String clientName = morphiaEntityConfiguration.getString("clientName");
        if (clientName == null) {
            throw SeedException.createNew(MorphiaErrorCodes.UNKNOW_DATASTORE_CLIENT)
                    .put("aggregate", morphiaClass.getName());
        }

        String dbName = morphiaEntityConfiguration.getString("dbName");
        if (dbName == null) {
            throw SeedException.createNew(MorphiaErrorCodes.UNKNOW_DATASTORE_DATABASE)
                    .put("aggregate", morphiaClass.getName())
                    .put("clientName", clientName);
        }

        checkMongoClient(application.getConfiguration(), morphiaClass, clientName, dbName);

        return new MorphiaDatastoreImpl(clientName, dbName);
    }

    /**
     * Resolve the real database name given an alias.
     *
     * @param clientConfiguration the configuration of the client.
     * @param dbName              the name of the alias or the database.
     * @return the resolved database name (may be the provided database name if no alias is defined).
     */
    public static String resolveDatabaseAlias(Configuration clientConfiguration, String dbName) {
        String[] databases = clientConfiguration.getStringArray("databases");
        if (databases != null) {
            for (String database : databases) {
                if (dbName.equals(clientConfiguration.getString(String.format("alias.%s", database), dbName))) {
                    return database;
                }
            }
        }
        return dbName;
    }

    /**
     * Return the configuration of a MongoDb client.
     *
     * @param configuration the global application configuration.
     * @param clientName    the client name.
     * @return the configuration of the specified MongoDb client.
     */
    public static Configuration getMongoClientConfiguration(Configuration configuration, String clientName) {
        return configuration.subset(MongoDbPlugin.CONFIGURATION_PREFIX + ".client." + clientName);
    }

    private static void checkMongoClient(Configuration configuration, Class<?> mappedClass, String clientName, String dbName) {
        Configuration mongodbClientConfiguration = getMongoClientConfiguration(configuration, clientName);

        if (mongodbClientConfiguration.isEmpty()) {
            throw SeedException.createNew(MongoDbErrorCodes.UNKNOWN_CLIENT_SPECIFIED)
                    .put("aggregate", mappedClass.getName())
                    .put("clientName", clientName)
                    .put("dbName", dbName);
        }

        boolean async = mongodbClientConfiguration.getBoolean("async", false);
        if (async) {
            throw SeedException.createNew(MorphiaErrorCodes.ERROR_ASYNC_CLIENT)
                    .put("aggregate", mappedClass.getName())
                    .put("clientName", clientName)
                    .put("dbName", dbName);
        }

        String[] dbNames = mongodbClientConfiguration.getStringArray("databases");
        boolean found = false;
        for (String nameToCheck : dbNames) {
            if (nameToCheck.equals(resolveDatabaseAlias(mongodbClientConfiguration, dbName))) {
                found = true;
                break;
            }
        }
        if (!found) {
            throw SeedException.createNew(MorphiaErrorCodes.UNKNOW_DATABASE_NAME)
                    .put("aggregate", mappedClass.getName())
                    .put("clientName", clientName)
                    .put("dbName", dbName);
        }
    }
}
