import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtil {

    private String filePath;

    public FileUtil(String filePath) {
        this.filePath = filePath;
    }

    public List<String> readAllLines(String path) throws IOException {
        return Files.readAllLines(Paths.get(path));
    }

    public void saveAllLines(List<String> lines) throws IOException {
        Files.write(getOutputFile(), lines);
    }

    public String getFileName(String filePath) {
        String separator = File.separator.equals("\\") ? "\\\\" : File.separator;
        String[] fileParts = filePath.split(separator);
        return fileParts[fileParts.length - 1].split("\\.")[0];
    }

    public List<String> getAllFiles() {
        Path path = Paths.get(filePath);
        if (Files.isDirectory(path)) {
            return Stream.of(Objects.requireNonNull(path.toFile().listFiles()))
                    .map(File::getAbsolutePath)
                    .filter(f -> f.endsWith(".jack"))
                    .collect(Collectors.toList());

        }
        List<String> file = new ArrayList<>();
        file.add(new File(filePath).getAbsolutePath());
        return file;
    }

    private Path getOutputFile() {
        Path sourcePath = Paths.get(filePath);

        Path parent;
        String outputFileName;

        if (sourcePath.toFile().isDirectory()) {
            parent = sourcePath;
            outputFileName = sourcePath.toFile().getName() + ".asm";
        } else {
            parent = sourcePath.getParent();
            outputFileName = getFileName(sourcePath.getFileName().toString()) + ".asm";
        }

        return parent != null ? parent.resolve(outputFileName) : sourcePath.resolveSibling(outputFileName);
    }


}
