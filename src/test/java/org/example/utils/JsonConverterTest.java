package org.example.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.model.Currency;
import org.example.model.ExchangeRate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.contentOf;

class JsonConverterTest {


    private  String json;
    private ExchangeRate exchangeRate;


    @BeforeEach
    public void beforeEach(){
        json = "{\"id\":0,\"baseCurrency\":{\"id\":0,\"code\":\"USD\",\"fullName\":\"Dollar\",\"sign\":\"$\"},\"targetCurrency\":{\"id\":1,\"code\":\"RUB\",\"fullName\":\"Rubble\",\"sign\":\"P\"},\"rate\":95.0}";
        Currency currency1= new Currency(0, "USD", "Dollar","$");
        Currency currency2= new Currency(1, "RUB", "Rubble","P");

        exchangeRate = new ExchangeRate(0, currency1,currency2, 95.0);
    }

    @Test
    void convertObjectToJson() throws JsonProcessingException {
       String actual = JsonConverter.convertObjectToJson(exchangeRate);
        actual= actual.replaceAll("\\r\\n", "")
                .replace(" ", "");  // remove line separators and spaces
       assertThat(actual).isEqualTo(json);

    }

    @Test
    void convertJsonToObject() throws JsonProcessingException {

//        System.out.println(json);
        ExchangeRate exchangeRate = JsonConverter.convertJsonToObject(json, ExchangeRate.class);

        assertThat(JsonConverter.convertJsonToObject(json, ExchangeRate.class))
                .matches(r -> r.getId() == exchangeRate.getId())
                .matches(r -> r.getRate() == exchangeRate.getRate())
                
                .matches(r -> r.getBaseCurrency().getId() == exchangeRate.getBaseCurrency().getId())
                .matches(r -> r.getBaseCurrency().getSign() .equals(exchangeRate.getBaseCurrency().getSign()))
                .matches(r -> r.getBaseCurrency().getCode() .equals(exchangeRate.getBaseCurrency().getCode()))
                .matches(r -> r.getBaseCurrency().getFullName() .equals(exchangeRate.getBaseCurrency().getFullName()))

                .matches(r -> r.getTargetCurrency().getId() == exchangeRate.getTargetCurrency().getId())
                .matches(r -> r.getTargetCurrency().getSign() .equals(exchangeRate.getTargetCurrency().getSign()))
                .matches(r -> r.getTargetCurrency().getCode() .equals(exchangeRate.getTargetCurrency().getCode()))
                .matches(r -> r.getTargetCurrency().getFullName() .equals(exchangeRate.getTargetCurrency().getFullName()));


    }
}