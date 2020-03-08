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
        this.filePath = filePath;
    }

    public void compile() {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.newDocument();

            compileClass();

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

            } catch (TransformerException | IOException te) {
                System.out.println(te.getMessage());
            }
        } catch (ParserConfigurationException pce) {
            System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
        }

    }


    private void compileClass() {

        if (jackTokenizer.hasMoreTokens()) {
            jackTokenizer.advance();

            eat("class", );


        }
    }

    private void eat(String expected, JackTokenizer.TokenType tokenType) {
        if (jackTokenizer.tokenType() == tokenType && jackTokenizer.stringVal().equals(expected)) {
            writeToXml();
            jackTokenizer.advance();
        } else {
            throw new IllegalArgumentException("Unexpected value: "jackTokenizer.stringVal() + " expected: " + expected);
        }
    }

    private void writeToXml() {
        Element element = dom.createElement(jackTokenizer.tokenType().toString().toLowerCase());
        switch (jackTokenizer.tokenType()) {
            case KEYWORD:
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

    }


}
