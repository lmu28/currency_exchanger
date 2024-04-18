package org.example.utils;

import java.util.Arrays;
import java.util.List;

public class UrlSeparator {

    static List<String> separateUrl(String url){
        return Arrays.asList(url.split("/"));
    }


}
