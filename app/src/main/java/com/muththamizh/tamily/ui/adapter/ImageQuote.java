package com.muththamizh.tamily.ui.adapter;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class ImageQuote {

    private String quoteId, quote;
    private @ServerTimestamp
    Date timestamp;

    public ImageQuote(String quoteId, String quote) {
        this.quoteId = quoteId;
        this.quote = quote;
    }

    public ImageQuote() {
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
