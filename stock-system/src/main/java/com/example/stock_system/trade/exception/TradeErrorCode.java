package com.example.stock_system.trade.exception;

import lombok.Getter;

@Getter
public enum TradeErrorCode {
    TRADE_NOT_FOUND(404, "AG401", "주문이 존재하지 않습니다."),
    ;

    private final int status;
    private final String divisionCode;
    private final String message;

    TradeErrorCode(int status, String divisionCode, String message) {
        this.status = status;
        this.divisionCode = divisionCode;
        this.message = message;
    }
}
