package com.pradeephr.readow.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "channel", strict = false)
public class RssChannel
{

//    @Element
//    private String title;

    @ElementList(inline = true)
    public List<RssItem> item;

}
