/**
 * alert-common
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.alert.common.descriptor.config.ui;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.alert.common.descriptor.config.field.ConfigField;
import com.synopsys.integration.util.Stringable;

public abstract class UIConfig extends Stringable {
    private final String label;
    private final String description;
    private final String urlName;
    private final String componentNamespace;
    private List<ConfigField> configFields = List.of();

    protected UIConfig(String label, String description, String urlName, String componentNamespace) {
        this.label = label;
        this.description = description;
        this.urlName = urlName;
        this.componentNamespace = componentNamespace;
    }

    public UIConfig(String label, String description, String urlName) {
        this(label, description, urlName, "");
    }

    @PostConstruct
    public void setConfigFields() {
        configFields = createFields();
    }

    protected abstract List<ConfigField> createFields();

    /**
     * This should be used when validating the fields.
     */
    public List<ConfigField> getFields() {
        return configFields;
    }

    /**
     * This list of ConfigFields is used to define what is shown in the UI. This does not copy the validation functions.
     */
    public List<ConfigField> getMetadataFields() {
        return configFields.stream().map(SerializationUtils::clone).collect(Collectors.toList());
    }

    public final boolean hasFields() {
        return null != configFields && !configFields.isEmpty();
    }

    public List<ConfigField> createTestFields() {
        return List.of();
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public String getUrlName() {
        return urlName;
    }

    public boolean autoGenerateUI() {
        return StringUtils.isBlank(getComponentNamespace());
    }

    public String getComponentNamespace() {
        return componentNamespace;
    }
}
