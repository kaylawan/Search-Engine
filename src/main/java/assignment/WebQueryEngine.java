package assignment;
import java.util.*;

/**
 * A query engine which holds an underlying web index and can answer textual queries with a
 * collection of relevant pages.
 */
public class WebQueryEngine {
    private WebIndex index;
    public WebQueryEngine(WebIndex index){
        this.index = index;
    }
    /**
     * Returns a WebQueryEngine that uses the given Index to construct answers to queries.
     *
     * @param index The WebIndex this WebQueryEngine should use.
     * @return A WebQueryEngine ready to be queried.
     */
    public static WebQueryEngine fromIndex(WebIndex index) {
        return new WebQueryEngine(index);
    }
    /**
     * Returns a Collection of URLs (as Strings) of web pages satisfying the query expression.
     *
     * @param query A query expression.
     * @return A collection of web pages satisfying the query.
     */
    //Parses and tokenizes the input query and returns an associated set of web pages.
    public Set<Page> query(String query) {
        List<String> myQuery = parse(query);
        //empty query
        if(myQuery.size() == 0){
            return new HashSet<>();
        }
        //single word query
        else if(myQuery.size() == 1){
            return index.search(myQuery.get(0));
        }
        //contains a tokenized query that must be parsed
        return evaluateAfterFix(afterFix(myQuery));
    }

    //Tokenize query. Splits the input query into operators, operands, and parentheses
    public List<String> parse(String query) {
        //TODO
        List<String> allTokens = new LinkedList<>();
        StringBuilder token = new StringBuilder();
        for(int i = 0; i < query.length(); i++) {
            switch (query.charAt(i)) {
                //Parentheses Token (Open)
                case '(':
                    if(token.length() > 0) {
                        allTokens.add(token.toString());
                        token = new StringBuilder();
                    }
                    allTokens.add("(");
                    break;
                //Parentheses Token (Close)
                case ')':
                    if(token.length() > 0) {
                        allTokens.add(token.toString());
                        token = new StringBuilder();
                    }
                    allTokens.add(")");
                    break;
                //Operand Token (Phrase)
                case '\"':
                    if(token.length() > 0) {
                        allTokens.add(token.toString());
                        token = new StringBuilder();
                    }
                    i++;
                    token.append('\"');
                    while(i < query.length() && query.charAt(i) != '\"'){
                        token.append(Character.toLowerCase(query.charAt(i)));
                        i++;
                    }
                    token.append('\"');
                    allTokens.add(token.toString());
                    token = new StringBuilder();
                    break;
                //Operator Token (and)
                case '&':
                    if(token.length() > 0) {
                        allTokens.add(token.toString());
                        token = new StringBuilder();
                    }
                    allTokens.add("&");
                    break;
                //Operator Token (or)
                case '|':
                    if(token.length() > 0) {
                        allTokens.add(token.toString());
                        token = new StringBuilder();
                    }
                    allTokens.add("|");
                    break;
                //Operand Token (not)
                case '!':
                    if(token.length() == 0 && (i+1) < query.length() && Character.isLetterOrDigit(query.charAt(i + 1)))
                    {
                        token.append('!');
                    }
                    break;
                //Operand Token (word)
                default:
                    if(Character.isLetterOrDigit(query.charAt(i))) {
                        token.append(Character.toLowerCase(query.charAt(i)));
                    } else {
                        if(token.length() > 0) {
                            allTokens.add(token.toString());
                            token = new StringBuilder();
                        }
                    }
                    break;
            }
        }
        int length = token.length();
        if(length> 0) {
            allTokens.add(token.toString());
        }
        return allTokens;
    }

    private boolean precedence(String op, String sub) {
        boolean and = sub.equals("&");
        boolean or = sub.equals("|");
        boolean opand = op.equals("&");
        boolean opor = op.equals("|");
        return (and||or) && !(opand && opor);
    }

    //Shunting-yard algorithm
    public List<String> afterFix(List<String> allTokens) {
        //TODO
        List<String> results = new LinkedList<>();
        Deque<String> stack = new LinkedList<>();
        //tackles implicit and operations (words separated by spaces)
        boolean recentOperand = false;
        for(String token: allTokens) {
            switch (token) {
                case "&", "|" -> {
                    recentOperand = false;
                    while (!stack.isEmpty() && precedence(token, stack.peek())) {
                        results.add(stack.pop());
                    }
                    stack.push(token);
                }
                case "(" -> {
                    //Add the implicit &
                    if (recentOperand) {
                        recentOperand = false;
                        while (!stack.isEmpty() && precedence("&", stack.peek())) {
                            results.add(stack.pop());
                        }
                        stack.push("&");
                    }
                    stack.push(token);
                }
                case ")" -> {
                    recentOperand = true;
                    while (true) {
                        assert stack.peek() != null;
                        if (!!stack.peek().equals("(")) break;
                        results.add(stack.pop());
                    }
                    stack.pop();
                }
                //default case adds token
                default -> {
                    if (recentOperand) {
                        while (!stack.isEmpty() && precedence("&", stack.peek())) {
                            results.add(stack.pop());
                        }
                        stack.push("&");
                    }
                    recentOperand = true;
                    results.add(token);
                }
            }
        }
        while(!stack.isEmpty()){
            results.add(stack.pop());
        }
        return results;
    }
    //Evaluate afterFix tokens after Shunting-yard
    private Set<Page> evaluateAfterFix(List<String> allTokens) {
        Stack<String> operandStack = new Stack<>();
        Set<Page> results = null;

        for (String element : allTokens) {
            if ((element.equals("&")) && (results == null)) {
                results = and(operandStack.pop(), operandStack.pop());
            }
            else if ((element.equals("&")) && (results != null)) {
                andPlus(operandStack.pop(), results);
            }
            else if((element.equals("|")) && (results == null)) {
                results = or(operandStack.pop(), operandStack.pop());
            }
            else if((element.equals("|")) && (results!=null)){
                orPlus(operandStack.pop(), results);
            }
            else operandStack.push(element);
        }
        if(results == null){
            return new HashSet<>();
        }
        return results;
    }

    private Set<Page> and(String lhs, String rhs) {
        //TODO
        char leftChar = lhs.charAt(0);
        char rightChar = rhs.charAt(0);

        if(leftChar == '!') {
            Set<Page> results = index.search(rhs);
            results.removeAll(index.search(lhs.substring(1)));
            return results;
        }
        else if(leftChar != '\"') {
            Set<Page> results = index.searchWord(lhs);
            if(rhs.charAt(0) != '\"') {
                results.retainAll(index.searchWord(rhs));
            }
            else {
                results = index.searchPhraseAdd(rhs, results);
            }
            return results;
        }
        if(rightChar=='!') {
            Set<Page> results = index.search(lhs);
            results.removeAll(index.search(rhs.substring(1)));
            System.out.println("here");
            return results;
        }
        else if(rightChar!= '\"') {
            Set<Page> results = index.searchWord(rhs);
            if(lhs.charAt(0) != '\"') {
                results.retainAll(index.searchWord(lhs));
            }
            else {
                results = index.searchPhraseAdd(lhs, results);
            }
            return results;
        }
        else {
            Set<Page> results = index.search(lhs);
            results = index.searchPhraseAdd(rhs, results);
            return results;
        }
    }
    //alternate and function
    private void andPlus(String token, Set<Page> pageSet) {
        //TODO
        if(token.charAt(0) == '!') {
            pageSet.removeAll(index.searchWord(token.substring(1)));
        }
        else if (token.charAt(0) == '"') {
            index.searchPhraseAdd(token, pageSet);
        }
        else {
            pageSet.retainAll(index.searchWord(token));
        }
    }
    private Set<Page> or(String lhs, String rhs) {
        char leftChar = lhs.charAt(0);
        char rightChar = rhs.charAt(0);
        if(leftChar == '!' && rightChar == '!') {
            Set<Page> results = index.search(lhs.substring(1));
            results.retainAll(index.search(rhs.substring(1)));
            return index.inverse(results);
        }
        if(leftChar == '!') {
            Set<Page> results = index.search(lhs.substring(1));
            results.removeAll(index.search(rhs));
            return index.inverse(results);
        }
        else if(leftChar != '\"') {
            Set<Page> results = index.searchWord(lhs);
            if(rhs.charAt(0) != '\"') {
                results.addAll(index.searchWord(rhs));
            }
            else {
                results.addAll(index.searchPhraseRemove(rhs, results));
            }
            return results;
        }
        if(rightChar == '!') {
            Set<Page> results = index.search(rhs.substring(1));
            results.removeAll(index.search(lhs));
            return index.inverse(results);
        }
        else if(rightChar != '\"') {
            Set<Page> results = index.searchWord(rhs);
            if(lhs.charAt(0) != '\"') {
                results.addAll(index.searchWord(lhs));
            } else {
                results.addAll(index.searchPhraseRemove(lhs, results));
            }
            return results;
        }
        else{
            Set<Page> results = index.search(lhs);
            results.addAll(index.searchPhraseRemove(lhs, results));
            return results;
        }
    }
    //alternate or function
    private void orPlus(String token, Set<Page> pageSet) {
        //TODO
        if (token.charAt(0) == '"') {
            pageSet.addAll(index.searchPhraseRemove(token, pageSet));
        }
        else {
            pageSet.addAll(index.searchWord(token));
        }
    }
}