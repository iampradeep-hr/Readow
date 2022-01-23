package com.pradeephr.readow.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "item", strict = false)
public class RssItem
{
    @Element
    public String title;

    @Element
    public String link;

    @Element
    public String pubDate;

    @Element(required = false)
    public String description;


}

