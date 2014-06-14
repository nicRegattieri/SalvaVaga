package com.nicdev.salvavaga.app;

/**
 * Created by nicolaregattieri on 13/05/14.
 */
public class MyMarkerObj {
    private long id;
    private String title;
    private String snippet;
    private String position;

    public MyMarkerObj() {
    }


    public MyMarkerObj(String title, String snippet, String position) {
        this.title = title;
        this.snippet = snippet;
        this.position = position;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the snippet
     */
    public String getSnippet() {
        return snippet;
    }

    /**
     * @param snippet the snippet to set
     */
    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    /**
     * @return the position
     */
    public String getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(String position) {
        this.position = position;
    }


}