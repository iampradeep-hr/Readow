package com.pradeephr.readow.model;

import androidx.annotation.NonNull;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "rss", strict = false)
public class RssFeed
{
    @Element
    public RssChannel channel;

    @NonNull
    @Override
    public String toString() {
        return "RssFeed [channel=" + channel + "]";
    }
}
