/**
 * blackduck-alert
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.alert.provider.blackduck;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.alert.common.descriptor.ProviderDescriptor;
import com.blackducksoftware.integration.alert.database.entity.DatabaseEntity;
import com.blackducksoftware.integration.alert.database.entity.EntityPropertyMapper;
import com.blackducksoftware.integration.alert.database.provider.blackduck.GlobalBlackDuckConfigEntity;
import com.blackducksoftware.integration.alert.web.model.Config;
import com.blackducksoftware.integration.alert.web.provider.blackduck.GlobalBlackDuckConfig;
import com.blackducksoftware.integration.alert.workflow.startup.AlertStartupProperty;

@Component
public class BlackDuckDescriptor extends ProviderDescriptor {
    public static final String PROVIDER_NAME = "provider_blackduck";
    private final EntityPropertyMapper entityPropertyMapper;

    @Autowired
    public BlackDuckDescriptor(final BlackDuckContentConverter blackDuckContentConverter, final BlackDuckRepositoryAccessor blackDuckRepositoryAccessor,
            final EntityPropertyMapper entityPropertyMapper, final BlackDuckProvider provider) {
        super(PROVIDER_NAME, blackDuckContentConverter, blackDuckRepositoryAccessor, provider);
        this.entityPropertyMapper = entityPropertyMapper;
    }

    @Override
    public void validateGlobalConfig(final Config restModel, final Map<String, String> fieldErrors) {
    }

    @Override
    public void testGlobalConfig(final DatabaseEntity entity) {
    }

    @Override
    public Set<AlertStartupProperty> getGlobalEntityPropertyMapping() {
        return entityPropertyMapper.mapEntityToProperties(getName(), GlobalBlackDuckConfigEntity.class);
    }

    @Override
    public Config getGlobalRestModelObject() {
        return new GlobalBlackDuckConfig();
    }

}
