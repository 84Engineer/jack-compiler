import org.w3c.dom.Document;

import java.io.IOException;
import java.util.List;

public class JackAnalyzer {

    public static void main(String[] args) throws IOException {

        if (args.length == 0) {
            throw new IllegalArgumentException("Source code file not passed.");
        }

        FileUtil fileUtil = new FileUtil(args[0]);

        for (String filePath : fileUtil.getAllFiles()) {
            List<String> sourceCode = fileUtil.readAllLines(filePath);

            Tokenizer tokenizer = new Tokenizer(sourceCode, filePath);
            Document tokens = tokenizer.process();

            JackTokenizer jackTokenizer = new JackTokenizer(tokens);

        }

    }


}
