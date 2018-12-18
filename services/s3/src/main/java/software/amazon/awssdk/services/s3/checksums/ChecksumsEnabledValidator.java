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

package software.amazon.awssdk.services.s3.checksums;

import static software.amazon.awssdk.services.s3.checksums.ChecksumConstant.CHECKSUM_ENABLED_RESPONSE_HEADER;
import static software.amazon.awssdk.services.s3.checksums.ChecksumConstant.ENABLE_MD5_CHECKSUM_HEADER_VALUE;
import static software.amazon.awssdk.services.s3.checksums.ChecksumConstant.SERVER_SIDE_CUSTOMER_ENCRYPTION_HEADER;
import static software.amazon.awssdk.services.s3.checksums.ChecksumConstant.SERVER_SIDE_ENCRYPTION_HEADER;
import static software.amazon.awssdk.services.s3.model.ServerSideEncryption.AWS_KMS;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.signer.AwsSignerExecutionAttribute;
import software.amazon.awssdk.core.ClientType;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.core.checksums.SdkChecksum;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.interceptor.ExecutionAttribute;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.http.SdkHttpHeaders;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.internal.handlers.AsyncChecksumValidationInterceptor;
import software.amazon.awssdk.services.s3.internal.handlers.SyncChecksumValidationInterceptor;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.utils.BinaryUtils;
import software.amazon.awssdk.utils.internal.Base16Lower;

/**
 * Class used by {@link SyncChecksumValidationInterceptor} and
 * {@link AsyncChecksumValidationInterceptor} to determine if trailing checksums
 * should be enabled for a given request.
 */
@SdkInternalApi
public final class ChecksumsEnabledValidator {

    public static final ExecutionAttribute<SdkChecksum> CHECKSUM = new ExecutionAttribute<>("checksum");

    private ChecksumsEnabledValidator() {
    }

    /**
     * Validates that trailing checksums should be enabled based on {@link ClientType} and the presence
     * or S3 specific headers.
     *
     * @param expectedClientType - The expected client type for enabling checksums
     * @param executionAttributes - {@link ExecutionAttributes} to determine the actual client type
     * @param headers A map of headers for a given request
     * @return If trailing checksums should be enabled for this request.
     */
    public static boolean putObjectTrailingChecksumsEnabled(ClientType expectedClientType,
                                                            ExecutionAttributes executionAttributes,
                                                            Map<String, List<String>> headers) {

        ClientType actualClientType = executionAttributes.getAttribute(SdkExecutionAttribute.CLIENT_TYPE);

        if (!expectedClientType.equals(actualClientType)) {
            return false;
        }

        return trailingChecksumsEnabled(executionAttributes, headers);
    }

    /**
     * Validate if the trailing checksums is present in the header
     *
     * @param headers the SdkHttpHeaders
     * @return true if the trailing checksum is present in the header, false otherwise.
     */
    public static boolean getObjectTrailingChecksumsEnabled(SdkHttpHeaders headers) {
        return headers.firstMatchingHeader(CHECKSUM_ENABLED_RESPONSE_HEADER).filter(b -> b.equals(ENABLE_MD5_CHECKSUM_HEADER_VALUE)).isPresent();
    }

    public static boolean trailingChecksumsEnabledFromConfig(ExecutionAttributes executionAttributes) {
        S3Configuration serviceConfiguration =
            (S3Configuration) executionAttributes.getAttribute(AwsSignerExecutionAttribute.SERVICE_CONFIG);

        return serviceConfiguration == null || serviceConfiguration.checksumValidationEnabled();
    }

    public static boolean trailingChecksumsEnabled(ExecutionAttributes executionAttributes,
                                                   Map<String, List<String>> headers) {

        // S3 doesn't support trailing checksums for customer encryption
        if (headers.containsKey(SERVER_SIDE_CUSTOMER_ENCRYPTION_HEADER)) {
            return false;
        }

        // S3 doesn't support trailing checksums for KMS encrypted objects
        if (headers.getOrDefault(SERVER_SIDE_ENCRYPTION_HEADER, Collections.emptyList()).contains(AWS_KMS.toString())) {
            return false;
        }

        return trailingChecksumsEnabledFromConfig(executionAttributes);
    }


    public static void validatePutObjectChecksum(SdkResponse sdkResponse, ExecutionAttributes executionAttributes) {
        SdkChecksum checksum = executionAttributes.getAttribute(CHECKSUM);
        PutObjectResponse response = (PutObjectResponse) sdkResponse;

        if (response.eTag() != null) {
            String contentMd5 = BinaryUtils.toBase64(checksum.getChecksumBytes());
            byte[] digest = BinaryUtils.fromBase64(contentMd5);
            byte[] ssHash = Base16Lower.decode(response.eTag().replace("\"", ""));

            if (!Arrays.equals(digest, ssHash)) {
                throw SdkClientException.create("Data read has a different checksum than expected.");
            }
        }
    }

}
