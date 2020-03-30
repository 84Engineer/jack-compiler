import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class JackTokenizer {

    private Document source;
    private NodeList tokens;
    private int currentToken;
    private int length;

    public enum TokenType {

        KEYWORD("keyword"), SYMBOL("symbol"), IDENTIFIER("identifier"),
        INTEGERCONSTANT("integerConstant"), STRINGCONSTANT("stringConstant");

        private String tag;

        private TokenType(String tag) {
            this.tag = tag;
        }

        public String getTag() {
            return tag;
        }
    }

    public JackTokenizer(Document source) {
        this.source = source;
        this.tokens = source.getChildNodes().item(0).getChildNodes();
        this.currentToken = -1;
        this.length = tokens.getLength();
    }

    public boolean hasMoreTokens() {
        return currentToken + 1 < length;
    }

    public int getLength() {
        return length;
    }

    public int getCurrentLength() {
        return currentToken;
    }

    public void advance() {
        currentToken++;
    }

    public TokenType tokenType() {
        return TokenType.valueOf(tokens.item(currentToken).getNodeName().toUpperCase());
    }

    public TokenType prevTokenType() {
        return TokenType.valueOf(tokens.item(currentToken - 1).getNodeName().toUpperCase());
    }

    public String keyWord() {
        return getValue();
    }

    public char symbol() {
        return getValue().toCharArray()[0];
    }

    public String identifier() {
        return getValue();
    }

    public int intVal() {
        return Integer.parseInt(getValue());
    }

    public String stringVal() {
        return getValue();
    }

    private String getValue() {
        return tokens.item(currentToken).getTextContent();
    }

    public String getPrevValue() {
        return tokens.item(currentToken - 1).getTextContent();
    }

}
