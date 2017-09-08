package org.seedstack.mongodb.morphia.internal;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.seedstack.mongodb.morphia.MorphiaDatastore;
import org.seedstack.seed.Application;

import javax.inject.Inject;

import static org.seedstack.mongodb.morphia.internal.MorphiaUtils.createDatastoreAnnotation;
import static org.seedstack.mongodb.morphia.internal.MorphiaUtils.getMongoClientConfig;

public class DatastoreFactory {
    private final Application application;
    private final Injector injector;
    private final Morphia morphia;

    @Inject
    DatastoreFactory(Application application, Injector injector, Morphia morphia) {
        this.application = application;
        this.injector = injector;
        this.morphia = morphia;
    }

    public Datastore createDatastore(Class<?> morphiaClass) {
        MorphiaDatastore datastoreAnnotation = createDatastoreAnnotation(application, morphiaClass);
        return createDatastore(datastoreAnnotation.clientName(), datastoreAnnotation.dbName());
    }

    public Datastore createDatastore(String clientName, String dbName) {
        Datastore datastore = morphia.createDatastore(
                injector.getInstance(Key.get(MongoClient.class, Names.named(clientName))),
                MorphiaUtils.resolveDatabaseAlias(
                        getMongoClientConfig(application, clientName),
                        dbName
                )
        );
        datastore.ensureIndexes(true);
        datastore.ensureCaps();
        return datastore;
    }
}
