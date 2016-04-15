/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.mongodb.morphia.internal;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.name.Names;
import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.seedstack.mongodb.morphia.MorphiaDatastore;
import org.seedstack.seed.Application;

import javax.inject.Inject;

/**
 * @author redouane.loulou@ext.mpsa.com
 */
class DatastoreProvider implements Provider<Datastore> {
    private final MorphiaDatastore morphiaDatastore;
    private final Morphia morphia;
    @Inject
    private Injector injector;
    @Inject
    private Application application;

    DatastoreProvider(MorphiaDatastore morphiaDatastore, Morphia morphia) {
        super();
        this.morphiaDatastore = morphiaDatastore;
        this.morphia = morphia;
    }

    @Override
    public Datastore get() {
        String resolvedDbName = MorphiaUtils.resolveDatabaseAlias(
                MorphiaUtils.getMongoClientConfiguration(application.getConfiguration(), morphiaDatastore.clientName()),
                morphiaDatastore.dbName()
        );
        Datastore datastore = morphia.createDatastore(
                injector.getInstance(Key.get(MongoClient.class, Names.named(morphiaDatastore.clientName()))),
                resolvedDbName
        );
        datastore.ensureIndexes(true);
        datastore.ensureCaps();
        return datastore;
    }
}
