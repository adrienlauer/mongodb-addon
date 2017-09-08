/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.mongodb.morphia.internal;

import com.google.inject.Provider;
import org.mongodb.morphia.Datastore;
import org.seedstack.mongodb.morphia.MorphiaDatastore;

import javax.inject.Inject;

class DatastoreProvider implements Provider<Datastore> {
    private final MorphiaDatastore morphiaDatastore;
    @Inject
    private DatastoreFactory datastoreFactory;

    DatastoreProvider(MorphiaDatastore morphiaDatastore) {
        this.morphiaDatastore = morphiaDatastore;
    }

    @Override
    public Datastore get() {
        return datastoreFactory.createDatastore(morphiaDatastore.clientName(), morphiaDatastore.dbName());
    }
}
