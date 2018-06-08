package com;

import java.util.*;

public class Poliz {
    public class PolizException extends Exception{
        public PolizException(String message) {
            super(message);
        }
    }

    ArrayList<Token> result = new ArrayList<>();
    LinkedList<Token> tokens;
    Map<String, Object> variableTable = new HashMap<>();
    Map<String, String> typesTable = new HashMap<>();
    Stack<Token> stack = new Stack<>();

    Poliz(LinkedList<Token> tokens){
        this.tokens = tokens;
        toPoliz();
    }

    public void toPoliz() {
//        Stack<Token> stack = new Stack<>();
        Token upperInStack;
        Stack<Integer> YPL =new Stack<>();
        Stack<Integer> BP = new Stack<>();
        Stack<String> currentOperator = new Stack<>();

        while (!tokens.isEmpty()) {
            Token token = tokens.poll();
            String type = token.getTokenType();

            switch (type) {
                //Если следующий токен - операнд, то он сразу добавляется в ПОЛИЗ
                case "VAR":
                    if(!tokens.isEmpty() && (tokens.get(0).getTokenType().equals("OP_ASSIGN") ||
                            tokens.get(0).getTokenType().equals("ADD") || tokens.get(0).getTokenType().equals("REMOVE"))){
                        while(!stack.isEmpty())
                            result.add(stack.pop());
                    }
                    result.add(token);
                    break;
                case "DIGIT":
                case "TRUE":
                case "FALSE":
                    result.add(token);
                    break;
                case "OP":
                case "GET":
                    //Вытолкнуть из стека все операции с более высоким приоритетом
                    while (!stack.isEmpty() && ((upperInStack = stack.peek()).getTokenType().equals("OP") || upperInStack.getTokenType().equals("OP2"))
                            && upperInStack.getPriority() >= token.getPriority()) {
                        result.add(stack.pop());
                    }
                    stack.push(token);
                    break;
                case "OP_ASSIGN":
                case "ADD":
                case "REMOVE":
                case "CONTAINS":
                    stack.push(token);
                    break;
                case "LIST":
                case "SET":
                    while(!stack.isEmpty())
                        result.add(stack.pop());
                    stack.push(token);
                    break;
                case "BR1":
                    stack.push(token);
                    break;
                case "BR2":
                    //Выталкивать из стека все операции пока не встретится открывающаяся скобка
                    while (!stack.peek().getValue().equals("(")) {
                        result.add(stack.pop());
                    }
                    stack.pop();
                    break;
                case "WHILE":
                    //добавляем метку
                    while(!stack.isEmpty())
                        result.add(stack.pop());
                    BP.add(result.size());
                    currentOperator.add("WHILE");
                    break;
                case "OP_COMPARE":
                    while(stack.peek().getPriority() >= token.getPriority())
                        result.add(stack.pop());
                    stack.push(token);
                    break;
                case "BR3":
                    if (!currentOperator.peek().equals("ELSE")) {
                        YPL.add(result.size());
                        result.add(new Token("УПЛ", "", -1));
                    }
                    break;
                case "BR4":
                    while(!stack.isEmpty())
                        result.add(stack.pop());
                    if (!currentOperator.peek().equals("ELSE")) {
                        if (!currentOperator.peek().equals("IF")) result.add(new Token("БП", Integer.toString(BP.pop()), -1));
                        result.get(YPL.pop()).setValue(Integer.toString(result.size()));
                    }
                    currentOperator.pop();
                    break;
                case "IF":
                    while(!stack.isEmpty())
                        result.add(stack.pop());
                    currentOperator.add("IF");
                    break;
                case "ELSE":
                    currentOperator.add("ELSE");
                    break;
            }
        }
        while (!stack.isEmpty()) {
            result.add(stack.pop());
        }
    }

    public void calculate() throws PolizException{
        try {
            int i = 0;
            Token currentToken;
         //   Stack<Token> stack = new Stack<>();

            while (i < result.size()) {
                currentToken = result.get(i++);
                switch (currentToken.getTokenType()) {
                    case "VAR":
                    case "DIGIT":
                    case "TRUE":
                    case "FALSE":
                        stack.add(currentToken);
                        break;
                    case "OP":
                    case "OP_COMPARE":
                    case "GET":
                    case "ADD":
                    case "REMOVE":
                    case "CONTAINS":
                    case "OP_ASSIGN":
                        implementOperator(currentToken, stack.pop(), stack.pop());
                        break;
                    case "LIST":
                    case "SET":
                        implementOperator(currentToken, stack.pop());
                        break;
                    case "УПЛ":
                        Token p;
                        if (!stack.isEmpty()) {
                            p = stack.pop();
                            if (p.getTokenType().equals("TRUE"))
                                break;
                            else
                                if (p.getTokenType().equals("FALSE")) {
                                i = Integer.parseInt(currentToken.getValue());
                                break;
                                }
                                else
                                    if (p.getTokenType().equals("VAR")) {
                                        if (typesTable.get(p.getValue()).equals("TRUE")) break;
                                        else
                                            if (typesTable.get(p.getValue()).equals("FALSE")) {
                                                i = Integer.parseInt(currentToken.getValue());
                                                break;
                                            }
                                    }
                                    else throw new PolizException("Unacceptable type " + typesTable.get(p.getValue()));
                        }
                        break;
                    case "БП":
                        i = Integer.parseInt(currentToken.getValue());
                        break;
                }
            }
        }
        catch (PolizException e){
            System.out.println(e.getMessage());
        }
    }

    private void implementOperator(Token... tokens) throws PolizException {
        Token op = tokens[0];
        switch (op.getTokenType()) {
            case "OP_ASSIGN": {
                int a;
                if (tokens[1].getTokenType().equals("VAR")) {
                    a = (Integer) variableTable.get(tokens[1].getValue());
                } else {
                    a = Integer.parseInt(tokens[1].getValue());
                }
                variableTable.put(tokens[2].getValue(), a);
                if (!typesTable.containsKey(tokens[2].getValue())) typesTable.put(tokens[2].getValue(), "DIGITAL");
                break;
            }
            case "OP": {
                int a1, a2;
                if (tokens[1].getTokenType().equals("VAR")) {
                    a1 = (Integer) variableTable.get(tokens[1].getValue());
                } else {
                    a1 = Integer.parseInt(tokens[1].getValue());
                }
                if (tokens[2].getTokenType().equals("VAR")) {
                    a2 = (Integer) variableTable.get(tokens[2].getValue());
                } else {
                    a2 = Integer.parseInt(tokens[2].getValue());
                }
                switch (op.getValue()) {
                    case "\\+":
                        stack.add(new Token("DIGITAL", Integer.toString(a2 + a1)));
                        break;
                    case "-":
                        stack.add(new Token("DIGITAL", Integer.toString(a2 - a1)));
                        break;
                    case "*":
                        stack.add(new Token("DIGITAL", Integer.toString(a2 * a1)));
                        break;
                    case "/":
                        stack.add(new Token("DIGITAL", Integer.toString(a2 / a1)));
                        break;
                }
                break;
            }
            case "OP_COMPARE": {
                if (tokens[1].getValue().equals("TRUE") || tokens[1].getTokenType().equals("FALSE")) {
                    boolean a2 = Boolean.valueOf(tokens[1].getValue());
                    boolean a1 = Boolean.valueOf(tokens[2].getValue());
                    switch (op.getValue()) {
                        case "==":
                            if (a1 == a2) stack.add(new Token("TRUE", "true"));
                            else stack.add(new Token("FALSE", "false"));
                            break;
                        case "!=":
                            if (a1 != a2) stack.add(new Token("TRUE", "true"));
                            else stack.add(new Token("FALSE", "false"));
                            break;
                    }
                } else {
                    int a1, a2;
                    if (tokens[2].getTokenType().equals("VAR")) {
                        a1 = (Integer) variableTable.get(tokens[2].getValue());
                    } else {
                        a1 = Integer.parseInt(tokens[2].getValue());
                    }
                    if (tokens[1].getTokenType().equals("VAR")) {
                        a2 = (Integer) variableTable.get(tokens[1].getValue());
                    } else {
                        a2 = Integer.parseInt(tokens[1].getValue());
                    }
                    switch (op.getValue()) {
                        case ">":
                            if (a1 > a2) stack.add(new Token("TRUE", "true"));
                            else stack.add(new Token("FALSE", "false"));
                            break;
                        case "<":
                            if (a1 < a2) stack.add(new Token("TRUE", "true"));
                            else stack.add(new Token("FALSE", "false"));
                            break;
                        case "==":
                            if (a1 == a2) stack.add(new Token("TRUE", "true"));
                            else stack.add(new Token("FALSE", "false"));
                            break;
                        case "=!":
                            if (a1 != a2) stack.add(new Token("TRUE", "true"));
                            else stack.add(new Token("FALSE", "false"));
                            break;
                        case "<=":
                            if (a1 <= a2) stack.add(new Token("TRUE", "true"));
                            else stack.add(new Token("FALSE", "false"));
                            break;
                        case ">=":
                            if (a1 >= a2) stack.add(new Token("TRUE", "true"));
                            else stack.add(new Token("FALSE", "false"));
                            break;
                    }
                }
                break;
            }
            case "LIST": {
                Token var = tokens[1];
                variableTable.put(var.getValue(), new MyLinkedList());
                typesTable.put(var.getValue(), "LIST");
                break;
            }
            case "SET": {
                Token var = tokens[1];
                variableTable.put(var.getValue(), new MyHashSet());
                typesTable.put(var.getValue(), "SET");
                break;
            }
            case "GET": {
                int a;
                if (tokens[1].getTokenType().equals("VAR")) {
                    a = (Integer) variableTable.get(tokens[1].getValue());
                } else {
                    a = Integer.parseInt(tokens[1].getValue());
                }
                String struct = tokens[2].getValue();

                if (typesTable.get(struct).equals("LIST")) {
                    MyLinkedList list = (MyLinkedList) variableTable.get(struct);
                    stack.add(new Token("DIGIT", (String) list.get(a)));
                } else throw new PolizException("Cannot resolve GET");
                break;
            }
            case "ADD": {
                Object a;
                if (tokens[1].getTokenType().equals("VAR")) {
                    a = variableTable.get(tokens[1].getValue());
                } else {
                    a = tokens[1].getValue();
                }
                String struct = tokens[2].getValue();

                if (typesTable.get(struct).equals("LIST")) {
                    MyLinkedList list = (MyLinkedList) variableTable.get(struct);
                    list.add(a);
                } else if (typesTable.get(struct).equals("SET")) {
                    MyHashSet list = (MyHashSet) variableTable.get(struct);
                    list.add(a);
                } else throw new PolizException("Cannot resolve ADD");
                break;
            }
            case "REMOVE": {
                int a;
                if (tokens[1].getTokenType().equals("VAR")) {
                    a = (Integer) variableTable.get(tokens[1].getValue());
                } else {
                    a = Integer.parseInt(tokens[1].getValue());
                }
                String struct = tokens[2].getValue();

                if (typesTable.get(struct).equals("LIST")) {
                    MyLinkedList list = (MyLinkedList) variableTable.get(struct);
                    list.remove(a);
                } else if (typesTable.get(struct).equals("SET")) {
                    MyHashSet list = (MyHashSet) variableTable.get(struct);
                    list.remove(a);
                } else throw new PolizException("Cannot resolve REMOVE");
                break;
            }
            case "CONTAINS":{
                Object a;
                if (tokens[1].getTokenType().equals("VAR")) {
                    a = variableTable.get(tokens[1].getValue());
                } else {
                    a = tokens[1].getValue();
                }
                String struct = tokens[2].getValue();

                if (typesTable.get(struct).equals("LIST")) {
                    MyLinkedList list = (MyLinkedList) variableTable.get(struct);
                    if (list.contains(a)) stack.add(new Token("TRUE", "true"));
                    else  stack.add(new Token("FALSE", "false"));
                } else if (typesTable.get(struct).equals("SET")) {
                    MyHashSet list = (MyHashSet) variableTable.get(struct);
                    if (list.contains(a)) stack.add(new Token("TRUE", "true"));
                    else  stack.add(new Token("FALSE", "false"));
                } else throw new PolizException("Cannot resolve ADD");
                break;
            }
        }
    }

    public void showVariables(){
        System.out.println("\n\n");
        for(Map.Entry<String, Object> pair : variableTable.entrySet())
        {
            String value = pair.getKey()+" ";
            if (typesTable.get(pair.getKey()).equals("LIST")) {
                MyLinkedList list = (MyLinkedList) pair.getValue();
                for (int i =0; i< list.size(); i++){
                    Integer o =(Integer)list.get(i);
                    value += Integer.toString(o) + ",";
                }
            }
            else if  (!typesTable.get(pair.getKey()).equals("SET")) {
                Integer o =(Integer) pair.getValue();
                value += Integer.toString(o) ;
            }
            System.out.println(value);
        }
    }
}
