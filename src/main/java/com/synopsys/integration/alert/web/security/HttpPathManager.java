/**
 * blackduck-alert
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
package com.synopsys.integration.alert.web.security;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.synopsys.integration.alert.web.controller.BaseController;

@Component
public class HttpPathManager {
    public static final String PATH_ROOT_HASHTAG = "/#";
    public static final String PATH_ROOT = "/";

    public static final String PATH_CSS_STYLE_CSS = "/css/style.css";
    public static final String PATH_FAVICON_ICO = "/favicon.ico";
    public static final String PATH_FONTS = "/fonts/**";
    public static final String PATH_H2_CONSOLE = "/h2/**";
    public static final String PATH_INDEX_HTML = "index.html";
    public static final String PATH_JS_BUNDLE_JS = "/js/bundle.js";
    public static final String PATH_JS_BUNDLE_JS_MAP = "/js/bundle.js.map";
    public static final String PATH_SAML_ROOT = "/saml/**";
    public static final String PATH_ABOUT = BaseController.BASE_PATH + "/about";
    public static final String PATH_LOGIN = BaseController.BASE_PATH + "/login";
    public static final String PATH_LOGOUT = BaseController.BASE_PATH + "/logout";
    public static final String PATH_PASSWORD_RESET = BaseController.BASE_PATH + "/resetPassword";
    public static final String PATH_PASSWORD_RESET_ROOT = BaseController.BASE_PATH + "/resetPassword/**";
    public static final String PATH_SYSTEM_MESSAGES_LATEST = BaseController.BASE_PATH + "/system/messages/latest";
    public static final String PATH_VERIFY = BaseController.BASE_PATH + "/verify";
    public static final String PATH_VERIFY_SAML = PATH_VERIFY + "/saml";

    private static final String[] DEFAULT_PATHS = {
        PATH_ROOT,
        PATH_ROOT_HASHTAG,
        PATH_FAVICON_ICO,
        PATH_FONTS,
        PATH_JS_BUNDLE_JS,
        PATH_JS_BUNDLE_JS_MAP,
        PATH_CSS_STYLE_CSS,
        PATH_INDEX_HTML,
        PATH_SAML_ROOT,
        PATH_ABOUT,
        PATH_LOGIN,
        PATH_LOGOUT,
        PATH_PASSWORD_RESET,
        PATH_PASSWORD_RESET_ROOT,
        PATH_SYSTEM_MESSAGES_LATEST,
        PATH_VERIFY,
        PATH_VERIFY_SAML
    };

    private static final String[] DEFAULT_SAML_PATHS = {
        PATH_ROOT_HASHTAG,
        PATH_FAVICON_ICO,
        PATH_FONTS,
        PATH_JS_BUNDLE_JS,
        PATH_JS_BUNDLE_JS_MAP,
        PATH_CSS_STYLE_CSS,
        PATH_SAML_ROOT,
        PATH_ABOUT,
        PATH_SYSTEM_MESSAGES_LATEST,
        PATH_VERIFY,
        PATH_VERIFY_SAML,
        PATH_LOGOUT
    };

    private final Collection<String> allowedPaths;
    private final Collection<String> samlAllowedPaths;

    @Autowired
    public HttpPathManager() {
        allowedPaths = createDefaultAllowedPaths();
        samlAllowedPaths = createSamlDefaultAllowedPaths();
    }

    private List<String> createDefaultPaths(String[] paths) {
        List<String> list = new LinkedList<>();
        Collections.addAll(list, paths);
        return list;
    }

    private List<String> createDefaultAllowedPaths() {
        return createDefaultPaths(DEFAULT_PATHS);
    }

    private List<String> createSamlDefaultAllowedPaths() { return createDefaultPaths(DEFAULT_SAML_PATHS);}

    public void addAllowedPath(String path) {
        allowedPaths.add(path);
    }

    public void addSamlAllowedPath(String path) {
        samlAllowedPaths.add(path);
    }

    public String[] getAllowedPaths() {
        String[] allowedPathArray = new String[allowedPaths.size()];
        return allowedPaths.toArray(allowedPathArray);
    }

    public String[] getSamlAllowedPaths() {
        String[] allowedPathArray = new String[samlAllowedPaths.size()];
        return samlAllowedPaths.toArray(allowedPathArray);
    }
}
