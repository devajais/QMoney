
package com.crio.warmup.stock;


import com.crio.warmup.stock.dto.*;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;


public class PortfolioManagerApplication {

   public static final String token = "132aab6c109da7a9045edaa14d7022e6933e412f";
  private static RestTemplate restTemplate = new RestTemplate() ;
  // TODO: CRIO_TASK_MODULE_REST_API
  //  Find out the closing price of each stock on the end_date and return the list
  //  of all symbols in ascending order by its close value on end date.

  // Note:
  // 1. You may have to register on Tiingo to get the api_token.
  // 2. Look at args parameter and the module instructions carefully.
  // 2. You can copy relevant code from #mainReadFile to parse the Json.
  // 3. Use RestTemplate#getForObject in order to call the API,
  //    and deserialize the results in List<Candle>

  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {

    List<PortfolioTrade> trades = readTradesFromJson(args[0]);

        LocalDate endDate = LocalDate.parse(args[1]);
        
        String token = "132aab6c109da7a9045edaa14d7022e6933e412f";

        List<TotalReturnsDto> pair = new ArrayList<>();
        List<String> symbols = new ArrayList<>();

        for(PortfolioTrade trade: trades){
           String url =  prepareUrl(trade ,endDate, token);            
           TiingoCandle[] candle =  restTemplate.getForObject(url, TiingoCandle[].class);                  
           if(candle != null)
              pair.add(new TotalReturnsDto(trade.getSymbol(), candle[candle.length-1].getClose()));
        }

        //n
        Collections.sort(pair, closingComparator);
    
        for(TotalReturnsDto trd : pair){
           symbols.add(trd.getSymbol());
        }
     return symbols;
  }

  public static final Comparator<TotalReturnsDto> closingComparator = new Comparator<TotalReturnsDto>() {

     @Override
     public int compare(TotalReturnsDto t1, TotalReturnsDto t2) {
        return (int) (t1.getClosingPrice().compareTo(t2.getClosingPrice()));
     }
     
  };

  public static List<String> mainReadFile(String[] args) throws StreamReadException, DatabindException, IOException, URISyntaxException {
    List<String>  symbols  = new ArrayList<>();                                                                      
       List<PortfolioTrade> trades = readTradesFromJson(args[0]);
        for(PortfolioTrade trade: trades){          
         symbols.add(trade.getSymbol());
        }
    return symbols;    
 }

  // public static List<TotalReturnsDto> mainReadQuotesHelper(String args[],List<PortfolioTrade> trades) throws IOException, URISyntaxException{
  //   RestTemplate restTemplate = new RestTemplate();
  //   List<TotalReturnsDto> tests = new ArrayList<>();
  //   for(PortfolioTrade t : trades){
  //     String uri = "https://api.tiingo.com/tiingo/daily/"+t.getSymbol()+
  //     "prices?startDate="+t.getPurchaseDate().toString()+"&endDate="+args[1]+
  //     "&token=7c3f69956d729d588f563b78600db6c62f998561";
  //     TiingoCandle[] results = restTemplate.getForObject(uri, TiingoCandle[].class);
  //     if(results != null){
  //       tests.add(new TotalReturnsDto(t.getSymbol(), results[results.length-1].getClose()));
  //     }
  //   }
  //   return tests;
  // }

  // TODO:
  //  After refactor, make sure that the tests pass by using these two commands
  //  ./gradlew test --tests PortfolioManagerApplicationTest.readTradesFromJson
  //  ./gradlew test --tests PortfolioManagerApplicationTest.mainReadFile
  public static List<PortfolioTrade> readTradesFromJson(String filename) throws IOException, URISyntaxException {
    
    File assFile =  resolveFileFromResources(filename);
    List<PortfolioTrade> tradeReturn  = new ArrayList<>();
  
  ObjectMapper mapper = getObjectMapper();
  mapper.registerModule(new JavaTimeModule());
  mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  
  PortfolioTrade[] trades = mapper.readValue(assFile,PortfolioTrade[].class);         
  
  for(PortfolioTrade trade: trades)
         tradeReturn.add(trade);
    return tradeReturn;     
}

// public static File fileResolver(String string) throws StreamReadException, DatabindException, IOException{
//   File file = new File("src/main/resources/"+string);
//   return file;
// }

private static ObjectMapper getObjectMapper() {
  ObjectMapper objectMapper = new ObjectMapper();
  objectMapper.registerModule(new JavaTimeModule());
  return objectMapper;
}
private static File resolveFileFromResources(String filename) throws URISyntaxException {
  return Paths.get(
      Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();
}

public static List<String> debugOutputs() {

  String valueOfArgument0 = "trades.json";
  String resultOfResolveFilePathArgs0 = "trades.json";
  String toStringOfObjectMapper = "ObjectMapper";
  String functionNameFromTestFileInStackTrace = "mainReadFile";
  String lineNumberFromTestFileInStackTrace = "";


 return Arrays.asList(new String[]{valueOfArgument0, resultOfResolveFilePathArgs0,
     toStringOfObjectMapper, functionNameFromTestFileInStackTrace,
     lineNumberFromTestFileInStackTrace});
}

private static void printJsonObject(Object object) throws IOException {
  Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
  ObjectMapper mapper = new ObjectMapper();
  logger.info(mapper.writeValueAsString(object));
}
  // TODO:
  //  Build the Url using given parameters and use this function in your code to cann the API.
  public static String prepareUrl(PortfolioTrade trade, LocalDate endDate, String token) {    
    String url = "https://api.tiingo.com/tiingo/daily/"+ trade.getSymbol() +"/prices?startDate="+trade.getPurchaseDate()+"&endDate="+endDate+"&token="+token; 
    return url;
  }




  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Now that you have the list of PortfolioTrade and their data, calculate annualized returns
  //  for the stocks provided in the Json.
  //  Use the function you just wrote #calculateAnnualizedReturns.
  //  Return the list of AnnualizedReturns sorted by annualizedReturns in descending order.

  // Note:
  // 1. You may need to copy relevant code from #mainReadQuotes to parse the Json.
  // 2. Remember to get the latest quotes from Tiingo API.








  // TODO:
  //  Ensure all tests are passing using below command
  //  ./gradlew test --tests ModuleThreeRefactorTest
  static Double getOpeningPriceOnStartDate(List<Candle> candles) {
   Candle first = candles.get(0);
   return first.getOpen();
  }


  public static Double getClosingPriceOnEndDate(List<Candle> candles) {
   Candle last = candles.get(candles.size()-1);
   return last.getClose();
  }


  public static List<Candle> fetchCandles(PortfolioTrade trade, LocalDate endDate, String token) {
     
   String symbol = trade.getSymbol();
   LocalDate startDate = trade.getPurchaseDate();
   String url = "https://api.tiingo.com/tiingo/daily/"+symbol+"/prices?startDate="+startDate.toString()+"&endDate="+endDate.toString()+"&token="+token;

   RestTemplate restTemplate = new RestTemplate();
   TiingoCandle[] stockStartToEndDate = restTemplate.getForObject(url, TiingoCandle[].class);
   List<Candle> candleList = Arrays.asList(stockStartToEndDate);
   return candleList;

  }

  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)
      throws IOException, URISyntaxException {

      List<AnnualizedReturn> annualizedReturns = new ArrayList<>();
      LocalDate endDate = LocalDate.parse(args[1]);
      File trades = resolveFileFromResources(args[0]);
      ObjectMapper objectMapper = getObjectMapper();
      PortfolioTrade[] tradesObj = objectMapper.readValue(trades, PortfolioTrade[].class);
      for(PortfolioTrade trade : tradesObj){
         annualizedReturns.add(getAnnualizedReturn(trade,endDate));
      }
      Comparator<AnnualizedReturn> sortAnnRet = Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
      Collections.sort(annualizedReturns, sortAnnRet);
      return annualizedReturns;
  }



  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Return the populated list of AnnualizedReturn for all stocks.
  //  Annualized returns should be calculated in two steps:
  //   1. Calculate totalReturn = (sell_value - buy_value) / buy_value.
  //      1.1 Store the same as totalReturns
  //   2. Calculate extrapolated annualized returns by scaling the same in years span.
  //      The formula is:
  //      annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  //      2.1 Store the same as annualized_returns
  //  Test the same using below specified command. The build should be successful.
  //     ./gradlew test --tests PortfolioManagerApplicationTest.testCalculateAnnualizedReturn

  public static AnnualizedReturn getAnnualizedReturn(PortfolioTrade trade, LocalDate endDate) {
   String symbol = trade.getSymbol();
   LocalDate startDate = trade.getPurchaseDate();
   String url = "https://api.tiingo.com/tiingo/daily/"+symbol+"/prices?startDate="+startDate.toString()+"&endDate="+endDate.toString()+"&token="+token;

   RestTemplate restTemplate = new RestTemplate();
   TiingoCandle[] stockStartToEndDate = restTemplate.getForObject(url, TiingoCandle[].class);
   
   if(stockStartToEndDate!=null){
      TiingoCandle stockStartDate = stockStartToEndDate[0];
      TiingoCandle stockLatest = stockStartToEndDate[stockStartToEndDate.length - 1];

      Double buyPrice = stockStartDate.getOpen();
      Double sellPrice = stockLatest.getClose();
      AnnualizedReturn annualizedReturn = calculateAnnualizedReturns(endDate, trade, buyPrice, sellPrice);
      return annualizedReturn;
   }
   else{
      return new AnnualizedReturn(symbol, Double.NaN, Double.NaN);
   }
}

public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) {
      Double totalReturn = (sellPrice-buyPrice)/buyPrice;
      String Symbol = trade.getSymbol();
      LocalDate purchaseDate = trade.getPurchaseDate();
      Double numOfYears = (double) ChronoUnit.DAYS.between(purchaseDate, endDate)/365.24;

      double annualizedReturn =  Math.pow((1+totalReturn),(1/numOfYears))-1;
      return new AnnualizedReturn(Symbol, annualizedReturn, totalReturn);
  }







  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Once you are done with the implementation inside PortfolioManagerImpl and
  //  PortfolioManagerFactory, create PortfolioManager using PortfolioManagerFactory.
  //  Refer to the code from previous modules to get the List<PortfolioTrades> and endDate, and
  //  call the newly implemented method in PortfolioManager to calculate the annualized returns.

  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.

  public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args)
      throws Exception {
       String file = args[0];
       LocalDate endDate = LocalDate.parse(args[1]);
       File contents = resolveFileFromResources(file);
       ObjectMapper objectMapper = getObjectMapper();
       PortfolioTrade[] trades = objectMapper.readValue(contents, PortfolioTrade[].class);
       PortfolioManager portfolioManager = PortfolioManagerFactory.getPortfolioManager(new RestTemplate());
       return portfolioManager.calculateAnnualizedReturn(Arrays.asList(trades), endDate);
  }


  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());




    printJsonObject(mainCalculateReturnsAfterRefactor(args));
  }
  public static String getToken() {
    return token;
}
}

