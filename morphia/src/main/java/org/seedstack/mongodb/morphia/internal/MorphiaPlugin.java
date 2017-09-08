/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.mongodb.morphia.internal;

import com.google.common.collect.Lists;
import io.nuun.kernel.api.plugin.InitState;
import io.nuun.kernel.api.plugin.context.InitContext;
import io.nuun.kernel.api.plugin.request.ClasspathScanRequest;
import org.mongodb.morphia.Morphia;
import org.seedstack.mongodb.morphia.MorphiaDatastore;
import org.seedstack.seed.Application;
import org.seedstack.seed.core.SeedRuntime;
import org.seedstack.seed.core.internal.AbstractSeedPlugin;
import org.seedstack.seed.core.internal.validation.ValidationPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ValidatorFactory;
import java.util.Collection;
import java.util.HashSet;

/**
 * This plugin manages the MongoDb Morphia object/document mapping library.
 */
public class MorphiaPlugin extends AbstractSeedPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(MorphiaPlugin.class);
    private final Collection<MorphiaDatastore> morphiaDatastores = new HashSet<>();
    private final Morphia morphia = new Morphia();
    private ValidatorFactory validatorFactory;

    @Override
    public String name() {
        return "morphia";
    }

    @Override
    public Collection<Class<?>> dependencies() {
        return Lists.newArrayList(ValidationPlugin.class);
    }

    @Override
    public Collection<ClasspathScanRequest> classpathScanRequests() {
        return classpathScanRequestBuilder()
                .specification(MorphiaSpecifications.PERSISTED_CLASSES)
                .build();
    }

    @Override
    protected void setup(SeedRuntime seedRuntime) {
        validatorFactory = seedRuntime.getValidatorFactory();
    }

    @Override
    public InitState initialize(InitContext initContext) {
        Application application = getApplication();

        new InternalValidationExtension(validatorFactory, morphia);
        LOGGER.debug("Validation is enabled on Morphia entities");

        Collection<Class<?>> morphiaScannedClasses = initContext.scannedTypesBySpecification().get(MorphiaSpecifications.PERSISTED_CLASSES);
        if (morphiaScannedClasses != null && !morphiaScannedClasses.isEmpty()) {
            morphia.map(new HashSet<>(morphiaScannedClasses));
            for (Class<?> morphiaClass : morphiaScannedClasses) {
                MorphiaDatastore morphiaDatastore = MorphiaUtils.createDatastoreAnnotation(application, morphiaClass);
                if (!morphiaDatastores.contains(morphiaDatastore)) {
                    morphiaDatastores.add(morphiaDatastore);
                }
            }
        }

        return InitState.INITIALIZED;
    }

    @Override
    public Object nativeUnitModule() {
        return new MorphiaModule(morphiaDatastores, morphia);
    }
}