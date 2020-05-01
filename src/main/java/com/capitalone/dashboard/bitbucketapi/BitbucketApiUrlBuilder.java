package com.capitalone.dashboard.bitbucketapi;

import com.capitalone.dashboard.collector.GitSettings;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Build Bitbucket API URLs based on this specification :
 * https://docs.atlassian.com/bitbucket-server/rest/5.13.1/bitbucket-rest.html#idm45902674676512
 */
@Component
public class BitbucketApiUrlBuilder {

    private final GitSettings settings;

    @Autowired
    public BitbucketApiUrlBuilder(GitSettings settings) {
        this.settings = settings;
    }

    private static final Log LOG = LogFactory.getLog(BitbucketApiUrlBuilder.class);

    private static final String FORWARD_SLASH = "/";

    /**
     * This method parses Bitbucket URL and creates URI compliant with Bitbucket API
     *
     * @param rawUrl <p>Examples:
     *               <p>ssh://git@company.com/project/repository.git *
     *               https://username@company.com/scm/project/repository.git *
     *               ssh://git@company.com/~username/repository.git *
     *               https://username@company.com/scm/~username/repository.git
     * @return URI Bitbucket API URI
     */
    public URI buildReposApiUrl(String rawUrl) throws URISyntaxException {
        LOG.debug("bitbucket url :" + rawUrl);
        URIBuilder builder = new URIBuilder("");
        String bitbucketproduct = settings.getProduct() != null ? settings.getProduct() : StringUtils.EMPTY;
        BitbucketUrlParser.BitbucketUrlParts urlParts = BitbucketUrlParser.parseBitbucketUrl(rawUrl);
        String apiPath = settings.getApi() != null ? settings.getApi() : StringUtils.EMPTY;
        if (apiPath.endsWith(FORWARD_SLASH)) {
            apiPath = settings.getApi().substring(0, settings.getApi().length() - 1);
        }
        builder.setScheme(urlParts.getScheme()).setHost(urlParts.getHost());
        if (urlParts.getPort() != -1) {
            builder.setPort(urlParts.getPort());
        }
        if(bitbucketproduct.equalsIgnoreCase("cloud")) {
            builder.setPath(
                    apiPath + FORWARD_SLASH + urlParts.getProjectKey() + FORWARD_SLASH + urlParts.getRepoKey());
        }else {
            builder.setPath(
            		apiPath + "/projects/" + urlParts.getProjectKey() + "/repos/" + urlParts.getRepoKey());
        }
        return builder.build();
    }

    public URI buildPullRequestApiUrl(String rawUrl) throws URISyntaxException {
        URI uri = buildReposApiUrl(rawUrl);
        URIBuilder builder = new URIBuilder(uri);
        String bitbucketproduct = settings.getProduct() != null ? settings.getProduct() : StringUtils.EMPTY;
        if(bitbucketproduct.equalsIgnoreCase("cloud")) {
            builder.setPath(builder.getPath() + "/pullrequests");
        }else {
            builder.setPath(builder.getPath() + "/pull-requests");
        }
        return builder.build();
    }

    public URI buildPullRequestActivitiesApiUrl(String rawUrl, String pullRequestId)
            throws URISyntaxException {
        URI uri = buildReposApiUrl(rawUrl);
        URIBuilder builder = new URIBuilder(uri);
        String bitbucketproduct = settings.getProduct() != null ? settings.getProduct() : StringUtils.EMPTY;
        if(bitbucketproduct.equalsIgnoreCase("cloud")) {
            builder.setPath(builder.getPath() + "/pullrequests/" + pullRequestId + "/activity");
        }else {
            builder.setPath(builder.getPath() + "/pull-requests/" + pullRequestId + "/activities");
        }
        return builder.build();
    }

    public URI buildPullRequestCommitsApiUrl(String rawUrl, String pullRequestId)
            throws URISyntaxException {
        URI uri = buildReposApiUrl(rawUrl);
        URIBuilder builder = new URIBuilder(uri);
        String bitbucketproduct = settings.getProduct() != null ? settings.getProduct() : StringUtils.EMPTY;
        //Only configured for Bitbucket Cloud - May change for Bitbucket Server
        if(bitbucketproduct.equalsIgnoreCase("cloud")) {
            builder.setPath(builder.getPath() + "/pullrequests/" + pullRequestId + "/commits");
        }
        return builder.build();
    }

    public URI buildPullRequestCommentsApiUrl(String rawUrl, String pullRequestId)
            throws URISyntaxException {
        URI uri = buildReposApiUrl(rawUrl);
        URIBuilder builder = new URIBuilder(uri);
        String bitbucketproduct = settings.getProduct() != null ? settings.getProduct() : StringUtils.EMPTY;
        //Only configured for Bitbucket Cloud - May change for Bitbucket Server
        if(bitbucketproduct.equalsIgnoreCase("cloud")) {
            builder.setPath(builder.getPath() + "/pullrequests/" + pullRequestId + "/comments");
        }
        return builder.build();
    }
}

/*
 * SPDX-Copyright: Copyright (c) Capital One Services, LLC
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2019 Capital One Services, LLC
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
