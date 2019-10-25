/**
 * alert-common
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.alert.common.persistence.model;

import java.util.Map;
import java.util.Objects;

import com.synopsys.integration.alert.common.rest.model.AlertSerializableModel;

public class UserRoleModel extends AlertSerializableModel {
    private final String name;
    private final Boolean custom;
    private final PermissionMatrixModel permissions;

    public UserRoleModel(String name, Boolean custom, PermissionMatrixModel permissions) {
        this.name = name;
        this.custom = custom;
        this.permissions = permissions;
    }

    public static final UserRoleModel of(String name) {
        Objects.requireNonNull(name);
        return new UserRoleModel(name, false, new PermissionMatrixModel(Map.of()));
    }

    public static final UserRoleModel of(String name, Boolean custom) {
        Objects.requireNonNull(name);
        return new UserRoleModel(name, custom, new PermissionMatrixModel(Map.of()));
    }

    public static final UserRoleModel of(String name, PermissionMatrixModel permissions) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(permissions);
        return new UserRoleModel(name, false, permissions);
    }

    public String getName() {
        return name;
    }

    public boolean isCustom() {
        return custom;
    }

    public PermissionMatrixModel getPermissions() {
        return permissions;
    }

}
