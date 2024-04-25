package org.example.utils;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHandler {


    public static Map<String,String> getParams(HttpServletRequest req, List<String> params){
        Map<String,String> paramsMap = new HashMap<>();
        for (String p: params){
            paramsMap.put(p, req.getParameter(p));
        }
        return paramsMap;
    }
}
