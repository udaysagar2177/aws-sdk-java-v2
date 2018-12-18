/*
 * Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.awssdk.services.s3.utils;

import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Optional;
import org.reactivestreams.Publisher;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.SdkHttpResponse;

public final class InterceptorTestUtils {

    private InterceptorTestUtils() {
    }

    public static Context.ModifyHttpRequest modifyHttpRequestContext(SdkRequest request) {
        return new Context.ModifyHttpRequest() {
            @Override
            public SdkHttpRequest httpRequest() {
                return sdkHttpFullRequest();
            }

            @Override
            public Optional<RequestBody> requestBody() {
                return null;
            }

            @Override
            public Optional<AsyncRequestBody> asyncRequestBody() {
                return null;
            }

            @Override
            public SdkRequest request() {
                return request;
            }
        };
    }

    public static SdkHttpFullRequest sdkHttpFullRequest() {
        return SdkHttpFullRequest.builder()
                                 .uri(URI.create("http://localhost:8080"))
                                 .method(SdkHttpMethod.GET)
                                 .build();
    }

    public static Context.ModifyResponse modifyResponseContext(SdkResponse response, SdkHttpResponse sdkHttpResponse) {
        return new Context.ModifyResponse() {
            @Override
            public SdkResponse response() {
                return response;
            }

            @Override
            public SdkHttpResponse httpResponse() {
                return sdkHttpResponse;
            }

            @Override
            public Optional<Publisher<ByteBuffer>> responsePublisher() {
                return Optional.empty();
            }

            @Override
            public Optional<InputStream> responseBody() {
                return Optional.empty();
            }

            @Override
            public SdkHttpRequest httpRequest() {
                return null;
            }

            @Override
            public Optional<RequestBody> requestBody() {
                return Optional.empty();
            }

            @Override
            public Optional<AsyncRequestBody> asyncRequestBody() {
                return Optional.empty();
            }

            @Override
            public SdkRequest request() {
                return null;
            }
        };
    }
}
