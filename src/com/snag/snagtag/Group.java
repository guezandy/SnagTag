package com.snag.snagtag;

import java.util.ArrayList;
import java.util.List;

public class Group {

  public String string;
  public String url;
  public final List<String> children = new ArrayList<String>();

  public Group(String string, String url) {
    this.string = string;
    this.url = url;
  }

} 
