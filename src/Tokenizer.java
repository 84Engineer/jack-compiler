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
import java.util.Arrays;
import java.util.List;


public class Tokenizer {

    public static String COMMENT = "//";
    public static String COMMENT_MULTILINE_START = "/**";
    public static String COMMENT_MULTILINE_MID = "*";
    public static String COMMENT_MULTILINE_END = "*/";

    private List<String> input;
    private String filePath;
    private Document dom;
    private Element rootEle;

    public Tokenizer(List<String> input, String filePath) throws IOException {
        this.input = input;
        String[] parts = filePath.split("\\.");
        this.filePath = parts[0] + "T.xml";
    }

    public Document process() {

        String source = compileString();

        System.out.println(source);

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.newDocument();

            rootEle = dom.createElement("tokens");

            String construct = "";
            boolean inString = false;

            char[] chars = source.toCharArray();

            for (char aChar : chars) {

                if (aChar == '"') {
                    if (!inString) {
                        inString = true;
                        construct += aChar;
                        continue;
                    } else {
                        inString = false;
                        construct += aChar;
                    }
                }
                if (aChar == ' ') {
                    if (inString) {
                        construct += aChar;
                        continue;
                    } else if (!construct.isEmpty()) {
                        processLine(construct);
                        construct = "";
                        continue;
                    } else {
                        continue;
                    }
                }
                if (checkSymbol(aChar)) {
                    if (!construct.isEmpty()) {
                        processLine(construct);
                        construct = "";
                    }
                    processSymbol(aChar);
                    continue;
                }
                construct += aChar;

            }

            dom.appendChild(rootEle);

            try {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                tr.setOutputProperty(OutputKeys.METHOD, "xml");
                tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                tr.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                // send DOM to file
                tr.transform(new DOMSource(dom),
                        new StreamResult(new FileOutputStream(filePath)));

                return dom;

            } catch (TransformerException te) {
                System.out.println(te.getMessage());
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        } catch (ParserConfigurationException pce) {
            System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
        }
        return null;
    }

    private boolean checkSymbol(char c) {
        return Constants.SYMBOLS.contains(c);
    }

    private void processSymbol(char c) {
        createElement(dom, "symbol", "" + c, rootEle);
    }

//    private boolean processKeyword(String word) {
//        if (Constants.KEYWORDS.contains(word)) {
//            createElement(dom, "keyword", word, rootEle);
//            return true;
//        }
//        return false;
//    }

    private boolean processLine(String line) {
        if (Constants.KEYWORDS.contains(line)) {
            createElement(dom, "keyword", line, rootEle);
            return true;
        } else if (line.matches("[0-9]*")) {
            createElement(dom, "integerConstant", line, rootEle);
            return true;
        } else if (line.matches("[A-Za-z0-9_]*")) {
            createElement(dom, "identifier", line, rootEle);
            return true;
        } else if (line.matches("\".*\"")) {
            createElement(dom, "stringConstant", line.substring(1, line.length() - 2), rootEle);
            return true;
        } else {
            throw new IllegalArgumentException(line);
        }
    }


    private void createElement(Document dom, String tagName, String value, Element rootEle) {
        Element element = dom.createElement(tagName);
        element.setTextContent(value);
        rootEle.appendChild(element);
    }

    private String compileString() {
        StringBuilder sb = new StringBuilder();
        for (String line : input) {
            if (isEmptyLine(line)) {
                continue;
            }
            line = cleanLine(line);
            sb.append(line);
        }
        return sb.toString();
    }


    private boolean isEmptyLine(String codeLine) {
        codeLine = codeLine.trim();
        if (codeLine.equals("")) {
            return true;
        }
        for (String content : Arrays.asList(COMMENT, COMMENT_MULTILINE_START,
                COMMENT_MULTILINE_MID, COMMENT_MULTILINE_END)) {
            if (codeLine.startsWith(content)) {
                return true;
            }
        }
        return false;
    }

    private String cleanLine(String codeLine) {
        if (codeLine.contains(COMMENT)) {
            codeLine = codeLine.substring(0, codeLine.indexOf(COMMENT));
        } else if (codeLine.contains(COMMENT_MULTILINE_START)) {
            codeLine = codeLine.substring(0, codeLine.indexOf(COMMENT_MULTILINE_START));
        }
        return codeLine.trim();
    }


}
