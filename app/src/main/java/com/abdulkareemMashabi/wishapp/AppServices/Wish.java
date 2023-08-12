package com.abdulkareemMashabi.wishapp.AppServices;

public class Wish implements Comparable<Wish> {
    private String wish;
    private String date;

    private String documentId;

    public Wish(String wish, String date, String documentId) {
        this.wish = wish;
        this.date = date;
        this.documentId= documentId;
    }

    public void setWish(String wish) {
        this.wish = wish;
    }

    public String getWish() {
        return wish;
    }

    public String getDate() {
        return date;
    }

    // compare based on date
    @Override
    public int compareTo(Wish wish) {
        return -1 * getDate().compareTo(wish.getDate());
    }

    public String getDocumentId() {
        return documentId;
    }
}
