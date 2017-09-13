/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.mongodb.morphia.internal.specification;

import org.mongodb.morphia.query.CriteriaContainer;
import org.seedstack.business.specification.PropertySpecification;
import org.seedstack.business.spi.specification.SpecificationConverter;
import org.seedstack.business.spi.specification.SpecificationTranslator;

public class MorphiaPropertyConverter implements SpecificationConverter<PropertySpecification<?, ?>, MorphiaTranslationContext<?>, CriteriaContainer> {
    @Override
    public CriteriaContainer convert(PropertySpecification<?, ?> specification, MorphiaTranslationContext<?> context, SpecificationTranslator<MorphiaTranslationContext<?>, CriteriaContainer> translator) {
        context.setFieldEnd(specification.getPath());
        return translator.translate(specification.getValueSpecification(), context);
    }
}
