/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ballerinalang.stdlib.time.util;

import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BLangVMErrors;
import org.ballerinalang.bre.bvm.BLangVMStructs;
import org.ballerinalang.connector.api.BLangConnectorSPIUtil;
import org.ballerinalang.model.types.BTypes;
import org.ballerinalang.model.values.BError;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.util.codegen.PackageInfo;
import org.ballerinalang.util.codegen.StructureTypeInfo;
import org.ballerinalang.util.exceptions.BallerinaException;

import java.time.ZoneId;
import java.time.zone.ZoneRulesException;
import java.util.Date;
import java.util.TimeZone;

import static org.ballerinalang.util.BLangConstants.ORG_NAME_SEPARATOR;

/**
 * A util class for the time package's native implementation.
 *
 * @since 0.95.4
 */
public class TimeUtils {
    private static final String TIME_PACKAGE_PATH = "ballerina" + ORG_NAME_SEPARATOR + "time";
    private static final String STRUCT_TYPE_TIME = "Time";
    private static final String STRUCT_TYPE_TIMEZONE = "TimeZone";

    private static final String TIME_ERROR_RECORD = "TimeError";
    private static final String TIME_ERROR_CODE = "{ballerina/time}TimeError";

    public static BMap<String, BValue> createTimeZone(StructureTypeInfo timezoneStructInfo, String zoneIdValue) {
        ZoneId zoneId = getTimeZone(zoneIdValue);
        //Get offset in seconds
        TimeZone tz = TimeZone.getTimeZone(zoneId);
        int offsetInMills = tz.getOffset(new Date().getTime());
        int offset = offsetInMills / 1000;
        return BLangVMStructs.createBStruct(timezoneStructInfo, zoneIdValue, offset);

    }

    public static ZoneId getTimeZone(String zoneIdValue) {
        try {
            return ZoneId.of(zoneIdValue);
        } catch (ZoneRulesException e) {
            throw new BallerinaException("invalid timezone id: " + zoneIdValue);
        }
    }

    public static BMap<String, BValue> createTimeStruct(StructureTypeInfo timezoneStructInfo,
                                                        StructureTypeInfo timeStructInfo, long millis,
                                                        String zoneIdName) {
        BMap<String, BValue> timezone = createTimeZone(timezoneStructInfo, zoneIdName);
        return BLangVMStructs.createBStruct(timeStructInfo, millis, timezone);
    }

    public static StructureTypeInfo getTimeZoneStructInfo(Context context) {
        PackageInfo timePackageInfo = context.getProgramFile().getPackageInfo(TIME_PACKAGE_PATH);
        if (timePackageInfo == null) {
            return null;
        }
        return timePackageInfo.getStructInfo(STRUCT_TYPE_TIMEZONE);
    }

    public static StructureTypeInfo getTimeStructInfo(Context context) {
        PackageInfo timePackageInfo = context.getProgramFile().getPackageInfo(TIME_PACKAGE_PATH);
        if (timePackageInfo == null) {
            return null;
        }
        return timePackageInfo.getStructInfo(STRUCT_TYPE_TIME);
    }

    public static BError getTimeError(Context context, String message) {
        BMap<String, BValue> sqlClientErrorDetailRecord = BLangConnectorSPIUtil
                .createBStruct(context, TIME_PACKAGE_PATH, TIME_ERROR_RECORD, message);
        return BLangVMErrors.createError(context, true, BTypes.typeError, TIME_ERROR_CODE, sqlClientErrorDetailRecord);
    }
}
