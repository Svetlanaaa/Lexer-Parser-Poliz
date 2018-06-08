package com;

import java.util.LinkedList;

public class Parser {
    private class ParserException extends Exception{
        public ParserException(String message) {
            super(message);
        }
    }

    private LinkedList<Token> tokens;
    private Token currentToken;

    private Token getNextToken(){
        tokens.pop();
        if (tokens.isEmpty()){
            return new Token("END", "", -1);
        }
        else{
            return tokens.getFirst();
        }
    }

    public void parse(LinkedList<Token> token) {
        try {
            tokens = token;
            currentToken = this.tokens.getFirst();
                while (tokens.size() != 0) {
                    lang();
                    if (!tokens.isEmpty()) currentToken = getNextToken();
                }
            if (!currentToken.getTokenType().equals("END"))
                throw new ParserException("Unexpected symbol " + currentToken.getValue());
        }
        catch (ParserException e){
            System.out.println(e.getMessage());
        }
    }

    public void lang()throws ParserException{
        //lang -> (expr_while |expr_assign | expr_if | expr_list | add_remove_list)*
        String type = currentToken.getTokenType();
        switch (type){
            case "WHILE":
                expr_while();
                break;
            case "IF":
                expr_if();
                break;
            case "LIST":
                expr_list();
                break;
            case "SET":
                expr_set();
                break;
            default:
                if(tokens.size() > 1 &&
                        (tokens.get(1).getTokenType().equals("ADD") || tokens.get(1).getTokenType().equals("REMOVE"))) {
                    add_remove();
                }
                else{
                    expr_assign();
                }
        }
    }

    private void expr_set() throws ParserException{
        //expr_set -> SET VAR
        currentToken = getNextToken();
        value();
    }
    private void expr_list() throws ParserException{
        //expr_list -> LIST VAR
        currentToken = getNextToken();
        value();
    }
    private void add_remove()throws ParserException{
        //add_remove -> VAR ADD|REMOVE value
        if(!currentToken.getTokenType().equals("VAR"))
            throw new ParserException("Expected variable, but found "+ currentToken.getValue());
        currentToken = getNextToken();
        if(!(currentToken.getTokenType().equals("ADD") ||currentToken.getTokenType().equals("REMOVE")))
            throw new ParserException("Expected ADD or REMOVE, but found "+ currentToken.getValue());
        currentToken = getNextToken();
        value();
    }

    private void expr_if() throws  ParserException{
        //expr_if -> IF condition body ELSE body
        currentToken = getNextToken();
        condition();
        body();
        if(tokens.size() > 1 && tokens.get(1).getTokenType().equals("ELSE")) {
            getNextToken();
            body();
        }
    }

    private void expr_while()throws ParserException{
        //expr_while -> WHILE condition body_while
        currentToken = getNextToken();
        condition();
        body();
    }

    private void contains_list_set()throws ParserException{
        //contains_list_set -> VAR CONTAINS value
        currentToken = getNextToken();
        if(!currentToken.getTokenType().equals("VAR"))
            throw new ParserException("Expected variable, but found "+ currentToken.getValue());
        currentToken = getNextToken();
        if(!currentToken.getTokenType().equals("CONTAINS"))
            throw new ParserException("Expected CONTAINS, but found "+ currentToken.getValue());
        currentToken = getNextToken();
        value();
    }

    private void condition()throws ParserException{
        //condition -> BR1 comp|compare_contains  BR2
        if (currentToken.getTokenType().equals("BR1")) {
            if(tokens.size() > 2 && tokens.get(2).getTokenType().equals("CONTAINS"))
                compare_contains();
            else comp();
            currentToken = getNextToken();
            if (!currentToken.getTokenType().equals("BR2")) {
                throw new ParserException("Expected ), but found "+ currentToken.getValue());
            }
        }
        else throw new ParserException("Expected (, but found "+ currentToken.getValue());
    }

    private void compare_contains() throws ParserException{
        contains_list_set();
        currentToken = getNextToken();
        if (!currentToken.getTokenType().equals("OP_COMPARE"))
            throw new ParserException("Expected operator of comparison, but found "+ currentToken.getValue());
        currentToken = getNextToken();
        if (!(currentToken.getTokenType().equals("TRUE") || currentToken.getTokenType().equals("FALSE")))
            throw new ParserException("Expected TRUE or FALSE, but found "+ currentToken.getValue());
    }

    private void comp()throws ParserException{
        //comp -> VAR OP_COMPARE (value | true_false)
        currentToken = getNextToken();
        if (currentToken.getTokenType().equals("VAR")) {
            currentToken = getNextToken();
            if (!currentToken.getTokenType().equals("OP_COMPARE"))
                throw new ParserException("Expected operator of comparison, but found "+ currentToken.getValue());
            currentToken = getNextToken();
            if (currentToken.getTokenType().equals("TRUE") || currentToken.getTokenType().equals("FALSE")) {
                true_false();
            }
            else {
                value();
            }
        }
        else throw new ParserException("Expected variable, but found "+ currentToken.getValue());
    }

    private void true_false()throws ParserException{
        //true_false -> TRUE | FALSE
        if (!(currentToken.getTokenType().equals("TRUE") || currentToken.getTokenType().equals("FALSE")))
            throw new ParserException("Expected TRUE or FALSE, but found "+ currentToken.getValue());
    }

    private void get_list_set() throws ParserException{
        //get_list_set -> VAR GET value
        getNextToken();
        currentToken = getNextToken();
        value();
    }

    private void value()throws ParserException{
        // value -> VAR | DIGITAL | get_list_set
        if (!(currentToken.getTokenType().equals("VAR") || currentToken.getTokenType().equals("DIGIT")))
            throw new ParserException("Expected variable or digit, but found "+ currentToken.getValue());
        if (tokens.size() > 1 && tokens.get(1).getTokenType().equals("GET"))
            get_list_set();
    }

    private void body()throws ParserException{
        // body -> BR3 in_body BR4
        currentToken = getNextToken();
            if (currentToken.getTokenType().equals("BR3")) {
                in_body();
                currentToken = getNextToken();
                if (!currentToken.getTokenType().equals("BR4")) throw new ParserException("Expected }, but found "+ currentToken.getValue());
            } else throw new ParserException("Expected {, but found "+ currentToken.getValue());
    }

    private void in_body()throws ParserException{
        // in_body -> (expr_while | expr_assign | expr_if)*
        while(tokens.size() > 1 && !tokens.get(1).getTokenType().equals("BR4")) {
            currentToken = getNextToken();
            lang();
        }
    }

    private void expr_assign()throws ParserException{
        //expr_assign -> VAR OP_ASSIGN assign_value
        if (!currentToken.getTokenType().equals("VAR")) throw new ParserException("Expected variable, but found "+ currentToken.getValue());
        currentToken = getNextToken();
        if (!currentToken.getTokenType().equals("OP_ASSIGN"))
            throw new ParserException("Expected operator of assignation, but found "+ currentToken.getValue());
        assign_value();
    }

    private void assign_value()throws ParserException{
        //assign_value -> expr_op | value
        currentToken = getNextToken();
        if(currentToken.getTokenType().equals("BR1") || (tokens.size()>1 && tokens.get(1).getTokenType().equals("OP"))){
            expr_op();
        }
        else{
            value();
        }
    }

    private void expr_op()throws ParserException{
        // expr_op -> (value|brs) ( OP (value|brs))+
        if (currentToken.getTokenType().equals("BR1")) {
            brs();
        }
        else {
            value();
        }
        currentToken = getNextToken();
        if (!currentToken.getTokenType().equals("OP")) throw new ParserException("Expected operator + or -, but found "+ currentToken.getValue());
        while(currentToken.getTokenType().equals("OP")) {
            currentToken = getNextToken();
            if (currentToken.getTokenType().equals("BR1")) {
                brs();
            }
            else {
                value();
            }
            if (tokens.size() > 1) {
                if (tokens.get(1).getTokenType().equals("OP")) currentToken = getNextToken();
            }
            else throw new ParserException("Unexpected completion");
        }
    }

    private void brs()throws ParserException{
        // brs -> BR1 expr_op+ BR2
        if (!currentToken.getTokenType().equals("BR1")) throw new ParserException("Expected (, but found "+ currentToken.getValue());
        currentToken = getNextToken();
        expr_op();
        currentToken = getNextToken();
        if (!currentToken.getTokenType().equals("BR2")) throw new ParserException("Expected ), but found "+ currentToken.getValue());
    }

}
