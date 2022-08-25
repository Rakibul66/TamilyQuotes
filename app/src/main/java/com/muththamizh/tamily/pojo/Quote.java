package com.muththamizh.tamily.pojo;


public class Quote {

    private String quoteId;
    private String quote;

    public Quote(String quoteId, String quote) {
        this.quoteId = quoteId;
        this.quote = quote;
    }


    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

}
