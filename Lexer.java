package com;

import javafx.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private ArrayList<Pair<String, Pattern>> tokenTypes = new ArrayList<Pair<String, Pattern>>();
    private Map<String, Integer> priority = new HashMap<String, Integer>();
    Lexer(){
        tokenTypes.add(new Pair<String, Pattern>("TRUE", Pattern.compile("^true$")));
        tokenTypes.add(new Pair<String, Pattern>("FALSE", Pattern.compile("^false$")));
        tokenTypes.add(new Pair<String, Pattern>("WHILE", Pattern.compile("^while$")));
        tokenTypes.add(new Pair<String, Pattern>("IF", Pattern.compile("^if$")));
        tokenTypes.add(new Pair<String, Pattern>("ELSE", Pattern.compile("^else$")));
        tokenTypes.add(new Pair<String, Pattern>("LIST", Pattern.compile("^list$")));
        tokenTypes.add(new Pair<String, Pattern>("SET", Pattern.compile("^set$")));
        tokenTypes.add(new Pair<String, Pattern>("ADD", Pattern.compile("^add$")));
        tokenTypes.add(new Pair<String, Pattern>("REMOVE", Pattern.compile("^remove$")));
        tokenTypes.add(new Pair<String, Pattern>("GET", Pattern.compile("^get$")));
        tokenTypes.add(new Pair<String, Pattern>("CONTAINS", Pattern.compile("^contains$")));
        tokenTypes.add(new Pair<String, Pattern>("VAR", Pattern.compile("^[a-z][0-9a-z]*$")));
        tokenTypes.add(new Pair<String, Pattern>("OP", Pattern.compile("^\\+$|^-$|^\\*$|^/$")));
        tokenTypes.add(new Pair<String, Pattern>("DIGIT", Pattern.compile("^0$|^[1-9][0-9]*$")));
        tokenTypes.add(new Pair<String, Pattern>("OP_ASSIGN", Pattern.compile("^=$")));
        tokenTypes.add(new Pair<String, Pattern>("OP_COMPARE", Pattern.compile("^==$|^<=$|^>=$|^=!$|^<$|^>$")));
        tokenTypes.add(new Pair<String, Pattern>("BR1", Pattern.compile("^\\($")));
        tokenTypes.add(new Pair<String, Pattern>("BR2", Pattern.compile("^\\)$")));
        tokenTypes.add(new Pair<String, Pattern>("BR3", Pattern.compile("^\\{$")));
        tokenTypes.add(new Pair<String, Pattern>("BR4", Pattern.compile("^\\}$")));

        priority.put("*", 3);
        priority.put("/", 3);
        priority.put("-", 2);
        priority.put("+", 2);
        priority.put("get", 4);
        priority.put("contains", 4);

    }

    LinkedList<Token> start(String inputS){
        LinkedList<Token> tokens = new LinkedList<>();
        int first = 0;
        int last = first;
        String lastS = null;
        String typeLastS = null;

        do {
            boolean f = false;
            String newToken = inputS.substring(first, last+1);
            for(Pair<String,Pattern> type : tokenTypes){
                Matcher m = type.getValue().matcher(newToken);
                if(m.find()){
                    lastS = new String(newToken);
                    typeLastS = new String(type.getKey());
                    last++;
                    f = true;
                    break;
                }
            }

            if (!f || last == inputS.length()) {
                if(typeLastS.equals("OP") || typeLastS.equals("GET") || typeLastS.equals("CONTAINS"))
                tokens.add(new Token(typeLastS, lastS, priority.get(lastS)));
                else
                    if(typeLastS.equals("OP_COMPARE"))
                    tokens.add(new Token(typeLastS, lastS, 1));
                    else  tokens.add(new Token(typeLastS, lastS, 0));
                first = last;
                if (last != inputS.length() && inputS.toCharArray()[last] == ' ' ) { first++; last++; }
            }
        }
        while (last < inputS.length());

        return tokens;
    }
}