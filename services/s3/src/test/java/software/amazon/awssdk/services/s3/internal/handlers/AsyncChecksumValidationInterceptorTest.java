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

package software.amazon.awssdk.services.s3.internal.handlers;


import org.junit.Test;
import software.amazon.awssdk.services.s3.utils.InterceptorTestUtils;

public class AsyncChecksumValidationInterceptorTest {

    private AsyncChecksumValidationInterceptor interceptor = new AsyncChecksumValidationInterceptor();

    @Test
    public void modifyAsyncHttpContent_putObjectRequestChecksumEnabled_shouldWrapChecksumRequestBody() {
        InterceptorTestUtils.modifyHttpRequestContext()

        //interceptor.modifyAsyncHttpContent()
    }

    @Test
    public void modifyAsyncHttpContent_nonPutObjectRequest_shouldNotModify() {

    }

    @Test
    public void modifyAsyncHttpContent_putObjectRequest_checksumDisabled_shouldNotModify() {

    }

    @Test
    public void modifyAsyncHttpResponseContent_getObjectRequest_checksumEnabled_shouldWrapChecksumValidatingPublisher() {

    }

    @Test
    public void modifyAsyncHttpResponseContent_getObjectRequest_checksumDisabled_shouldNotModify() {

    }


    @Test
    public void modifyAsyncHttpResponseContent_nonGetObjectRequest_shouldNotModify() {

    }


}
