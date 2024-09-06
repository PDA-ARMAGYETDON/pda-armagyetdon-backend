package com.example.stock_system.stocks;

import com.example.common.dto.ApiResponse;
import com.example.stock_system.stocks.dto.GetStockPricesResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/stocks")
public class StocksController {
    private final StocksService stocksService;

    @GetMapping("/prices")
    public ApiResponse<GetStockPricesResponse> getStockPrices(@RequestParam String code) {
        return new ApiResponse<>(200, true, "종목 가격을 조회했습니다.", stocksService.getStockPrices(code));
    }
}
