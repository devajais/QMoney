
package com.crio.warmup.stock.optional;

import com.crio.warmup.stock.PortfolioManagerApplication;
import com.crio.warmup.stock.dto.PortfolioTrade;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class ModuleTwoTest {

  @Test
  void readStockFromJson() throws Exception {
    //given
    String filename = "assessments/trades.json";
    List<String> expected = Arrays.asList(new String[]{"MSFT", "CSCO", "CTS"});

    //when
    List<PortfolioTrade> trades = PortfolioManagerApplication
        .readTradesFromJson(filename);
    List<String> actual = trades.stream().map(PortfolioTrade::getSymbol).collect(Collectors.toList());

    //then
    Assertions.assertEquals(expected, actual);
  }

  @Test
  void prepareUrl() throws Exception {
    //given
    PortfolioTrade trade = new PortfolioTrade();
    trade.setPurchaseDate(LocalDate.parse("2010-01-01"));
    trade.setSymbol("AAPL");
    String token = "132aab6c109da7a9045edaa14d7022e6933e412f";
    //when
    String tiingoUrl = PortfolioManagerApplication
            .prepareUrl(trade, LocalDate.parse("2010-01-10"), token);

    //then
    String uri = "https://api.tiingo.com/tiingo/daily/AAPL/prices?startDate=2010-01-01&endDate=2010-01-10&token=132aab6c109da7a9045edaa14d7022e6933e412f";

    Assertions.assertEquals(tiingoUrl, uri);
  }


}
