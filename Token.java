package com;

public class Token {
    private String tokenType;
    private String value;
    private int priority;

    public int getPriority(){return priority;};

    Token(String _tokenType, String _value, int _priority){
        tokenType = _tokenType;
        value = _value;
        priority = _priority;
    }

    Token(String _tokenType, String _value){
        tokenType = _tokenType;
        value = _value;
    }
    String getTokenType(){return tokenType;};

    public String getValue() {
        return value;
    }

    public void setValue(String i){
        value = i;
    }

    void print(){
        System.out.println(tokenType + "  " + value);
    }
}
