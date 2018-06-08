package com;

import java.util.LinkedList;

public class Main {

    public static void main(String[] args) throws Poliz.PolizException{
        String s = "set b b add 3 b add 3 b remove 3";
	    Lexer l = new Lexer();
        LinkedList<Token> tokens = l.start(s);
        for(Token t : tokens){
            t.print();
        }

        Parser parser = new Parser();
        LinkedList<Token> tokens2 = new LinkedList<>();
        tokens2.addAll(tokens);
        parser.parse(tokens2);
        Poliz poliz = new Poliz(tokens);
        for(Token t: poliz.result){
            System.out.print(t.getValue() + " ");
        }
        poliz.calculate();
        poliz.showVariables();
    }
}
