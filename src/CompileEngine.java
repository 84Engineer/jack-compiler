import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.io.IOException;

public class CompileEngine {

    private final String filePath;
    private JackTokenizer jackTokenizer;
    private Document dom;

    public CompileEngine(JackTokenizer jackTokenizer, String filePath) {
        this.jackTokenizer = jackTokenizer;
        String[] parts = filePath.split("\\.");
        this.filePath = parts[0] + ".compiled.xml";
    }

    public void compile() {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.newDocument();

            Element root = dom.createElement("class");
            dom.appendChild(root);

            jackTokenizer.advance();
            compileClass(root);

            try {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                tr.setOutputProperty(OutputKeys.METHOD, "html");
                tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                tr.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                // send DOM to file
                tr.transform(new DOMSource(dom),
                        new StreamResult(new FileOutputStream(filePath)));

            } catch (TransformerException | IOException te) {
                System.out.println(te.getMessage());
            }
        } catch (ParserConfigurationException pce) {
            System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
        }

    }

    private void proceedNextTokens(Element parent) {
//        if (jackTokenizer.hasMoreTokens()) {
//            jackTokenizer.advance();
            switch (jackTokenizer.tokenType()) {
                case KEYWORD:
                    switch (jackTokenizer.stringVal()) {
                        case "class":
                            compileClass(parent);
                            break;
                        case "method":
                        case "function":
                        case "constructor":
                            compileSubroutine(parent);
                            break;
                        case "field":
                        case "static":
                            compileClassVarDec(parent);
                            break;
                        case "var":
                            compileVarDec(parent);
                            break;
                        case "do":
//                            compileDo(parent);
                            break;
                        case "let":
//                            compileLet(parent);
                            break;
                        case "while":
//                            compileWhile(parent);
                            break;
                        case "return":
//                            compileReturn(parent);
                            break;
                        case "if":
//                            compileIf(parent);
                            break;

                    }
                    break;
                case SYMBOL:
                    break;
                case IDENTIFIER:
                    break;
                case INT_CONST:
                    break;
                case STRING_CONST:
                    break;
            }

//            if (jackTokenizer.hasMoreTokens()) {
//                jackTokenizer.advance();
//            }

//        }
    }


    private void compileClass(Element clazz) {
        eat(clazz, JackTokenizer.TokenType.KEYWORD, "class");
        eat(clazz, JackTokenizer.TokenType.IDENTIFIER, null);
        eat(clazz, JackTokenizer.TokenType.SYMBOL, "{");

        while (jackTokenizer.getCurrentLength() < jackTokenizer.getLength() - 1) {
            proceedNextTokens(clazz);
        }

        eat(clazz, JackTokenizer.TokenType.SYMBOL, "}");
    }

    private Element compileClassVarDec(Element parent) {
        Element parentTag = eat(parent, "classVarDec");
        eat(parentTag, JackTokenizer.TokenType.KEYWORD, null);
        try {
            eat(parentTag, JackTokenizer.TokenType.KEYWORD, null);
        } catch (Exception e) {
            // If not keyword, then identifier
            eat(parentTag, JackTokenizer.TokenType.IDENTIFIER, null);
        }
//        eat(parentTag, JackTokenizer.TokenType.KEYWORD, null);
        eat(parentTag, JackTokenizer.TokenType.IDENTIFIER, null);
        eat(parentTag, JackTokenizer.TokenType.SYMBOL, ";");
        parent.appendChild(parentTag);
        return parentTag;
    }

    private void compileSubroutine(Element parent) {
        Element declarTag = eat(parent, "subroutineDec");
        eat(declarTag, JackTokenizer.TokenType.KEYWORD, null);
        try {
            eat(declarTag, JackTokenizer.TokenType.KEYWORD, null);
        } catch (Exception e) {
            // If not keyword, then identifier
            eat(declarTag, JackTokenizer.TokenType.IDENTIFIER, null);
        }
        eat(declarTag, JackTokenizer.TokenType.IDENTIFIER, null);
        eat(declarTag, JackTokenizer.TokenType.SYMBOL, "(");

        compileParams(declarTag);

        eat(declarTag, JackTokenizer.TokenType.SYMBOL, ")");

        Element bodyTag = eat(declarTag, "subroutineBody");

        eat(bodyTag, JackTokenizer.TokenType.SYMBOL, "{");

        // TODO all the staff

        eat(bodyTag, JackTokenizer.TokenType.SYMBOL, "}");

    }

    private void compileParams(Element parent) {
        Element parentTag = eat(parent, "parameterList");
        boolean comma = false;
        while (!jackTokenizer.stringVal().equals(")")) {
            if (comma) {
                eat(parentTag, JackTokenizer.TokenType.SYMBOL, ",");
            }
            try {
                eat(parentTag, JackTokenizer.TokenType.KEYWORD, null);
            } catch (Exception e) {
                // If not keyword, then identifier
                eat(parentTag, JackTokenizer.TokenType.IDENTIFIER, null);
            }
            eat(parentTag, JackTokenizer.TokenType.IDENTIFIER, null);
            comma = true;
        }
    }



    private void compileIf() {

    }

    private void compileReturn() {
    }

    private void compileWhile() {

    }

    private void compileLet() {
    }

    private void compileDo() {

    }

    private void compileVarDec(Element parent) {
        Element varDec = eat(parent, "varDec");
        eat(varDec, JackTokenizer.TokenType.KEYWORD, "var");
        try {
            eat(varDec, JackTokenizer.TokenType.KEYWORD, null);
        } catch (Exception e) {
            // If not keyword, then identifier
            eat(varDec, JackTokenizer.TokenType.IDENTIFIER, null);
        }
        eat(varDec, JackTokenizer.TokenType.SYMBOL, ";");
    }



    private Element eat(Element parent, JackTokenizer.TokenType tokenType, String expected) {
//        if (jackTokenizer.hasMoreTokens()) {
//            jackTokenizer.advance();

        Element result;
            if (jackTokenizer.tokenType() == tokenType && (expected == null || expected.equals(jackTokenizer.stringVal()))) {
                result = writeToXml(parent, jackTokenizer.stringVal(), tokenType);
            } else {
                throw new IllegalArgumentException("Unexpected value: " + jackTokenizer.stringVal() + " of type: " + tokenType);
            }
//        }
        if (jackTokenizer.hasMoreTokens()) {
            jackTokenizer.advance();
        }
        return result;

//        throw new IllegalStateException("No more elements");
    }

    private Element eat(Element parent, String tag) {
        Element element = dom.createElement(tag);
        parent.appendChild(element);
        return element;
    }

    private Element writeToXml(Element parent, String value, JackTokenizer.TokenType type) {
        Element element = dom.createElement(type.toString().toLowerCase());
        element.setTextContent(value);
        parent.appendChild(element);
        return element;
    }


}
