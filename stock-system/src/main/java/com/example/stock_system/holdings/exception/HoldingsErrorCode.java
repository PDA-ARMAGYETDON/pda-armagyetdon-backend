package com.example.stock_system.holdings.exception;

import lombok.Getter;

@Getter
public enum HoldingsErrorCode {
    HOLDINGS_NOT_FOUND(404, "AG401", "보유 종목이 존재하지 않습니다."),
    NOT_ENOUGH_HOLDINGS(400, "AG001", "보유 수량이 부족합니다."),
    ;

    private final int status;
    private final String divisionCode;
    private final String message;

    HoldingsErrorCode(int status, String divisionCode, String message) {
        this.status = status;
        this.divisionCode = divisionCode;
        this.message = message;
    }
}
