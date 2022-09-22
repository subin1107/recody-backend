package com.recody.recodybackend.record.exceptions;

import com.recody.recodybackend.common.exceptions.ErrorType;

public enum RecordErrorType implements ErrorType {
    
    NoRecordFound("NoRecordFound")
    ;
    
    private final String errorCode;
    
    RecordErrorType(String errorCode) {
        this.errorCode = errorCode;
    }
    
    @Override
    public String getErrorCode() {
        return errorCode;
    }
}
