package com.pradeephr.readow.model;

public class ReadLaterModel {

    private int dataId;
    private String ArticleTitle;
    private String ArticleLink;
    private String ArticlePubDate;

    public ReadLaterModel(int dataId, String articleTitle, String articleLink, String articlePubDate) {
        this.dataId = dataId;
        ArticleTitle = articleTitle;
        ArticleLink = articleLink;
        ArticlePubDate = articlePubDate;
    }

    public ReadLaterModel() {
    }

    public int getDataId() {
        return dataId;
    }

    public void setDataId(int dataId) {
        this.dataId = dataId;
    }

    public String getArticleTitle() {
        return ArticleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        ArticleTitle = articleTitle;
    }

    public String getArticleLink() {
        return ArticleLink;
    }

    public void setArticleLink(String articleLink) {
        ArticleLink = articleLink;
    }

    public String getArticlePubDate() {
        return ArticlePubDate;
    }

    public void setArticlePubDate(String articlePubDate) {
        ArticlePubDate = articlePubDate;
    }
}
