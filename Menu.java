import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Menu {
    private static int EXIT = 0;

    public void start() {
        int option;
        StringBuilder sb = new StringBuilder();
        sb.append("\nOptions:\n")
                .append("1. Compare the content of all corresponding files in two directories with the same structure.\n")
                .append("2. Compare the content of two files.\n")
                .append("3. Compare all corresponding files in two directories with the same structure by size and modification date.\n")
                .append("4. Compare two files by size and modification date.\n")
                .append("5. List files that are not in the source directory, but are in the destination directory and have a equal files placed on other location in the source. (Time consuming operation)\n")
                .append(EXIT).append(". Exit.\n");

        do {
            display(sb.toString());
            option = selectOption(EXIT, 5);

            try {
                switch (option) {
                    case 1:
                        compareDirectoriesByContent();
                        break;
                    case 2:
                        compareFilesByContent();
                        break;
                    case 3:
                        compareDirectoriesBySizeAndDate();
                        break;
                    case 4:
                        compareFilesBySizeAndDate();
                        break;
                    case 5:
                        listFilesThatAreNotInSourceButHaveCopiesThere();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (option != EXIT);
    }

    private void compareDirectoriesByContent() throws Exception {
        compareDirectories(true);
    }

    private void compareFilesByContent() throws Exception {
        compareFiles(true);
    }

    private void compareDirectoriesBySizeAndDate() throws Exception {
        compareDirectories(false);
    }

    private void compareFilesBySizeAndDate() throws Exception {
        compareFiles(false);
    }

    private void compareDirectories(boolean compareContent) throws Exception {
        HashMap<String, File> source = new HashMap<>();
        HashMap<String, File> destination = new HashMap<>();
        getDirectoriesFiles(source, destination);

        ArrayList<File> notFoundOnDestination = Checker.findNonExistingOnDestination(source, destination);
        ArrayList<File> notFoundOnSource = Checker.findNonExistingOnDestination(destination, source);
        System.out.println("Not found on source: " + notFoundOnSource.size());
        System.out.println("Not found on destiny: " + notFoundOnDestination.size());

        Comparator comparator = new Comparator(compareContent);
        HashMap<File, File> notEqual = comparator.compareDirectories(source, destination);
        System.out.println("Not equal content: " + notEqual.size());
        list(notEqual);
    }

    private void compareFiles(boolean compareContent) throws Exception {
        String path1 = getString("File 1: ");
        String path2 = getString("File 2: ");
        File f1 = getFile(path1);
        File f2 = getFile(path2);

        Comparator comparator = new Comparator(compareContent);
        if (comparator.areFilesEqual(f1, f2))
            display("The files are equal.\n");
        else
            display("The files are not equal.\n");
    }

    private void listFilesThatAreNotInSourceButHaveCopiesThere() throws Exception {
        HashMap<String, File> source = new HashMap<>();
        HashMap<String, File> destination = new HashMap<>();
        getDirectoriesFiles(source, destination);

        ArrayList<File> notFound = Checker.findNonExistingOnDestination(destination, source);

        Finder finder = new Finder();
        ArrayList<ArrayList<File>> repeatedFiles = finder.findRepeatedFiles(source, notFound);

        if (repeatedFiles.isEmpty())
            display("No repeated files have been found.\n");
        else {
            display("Repeated files:\n");
            list(repeatedFiles);
        }
    }

    private int selectOption(int begin, int end) {
        int option;
        boolean invalid = true;
        Scanner scanner = new Scanner(System.in);
        do {
            display("Select an option (example: " + begin + "): ");
            option = scanner.nextInt();
            if (option >= begin && option <= end)
                invalid = false;
            else
                display("Invalid option.\n");
        } while (invalid);
        return option;
    }

    private void getDirectoriesFiles(HashMap<String, File> source, HashMap<String, File> destination) throws Exception {
        String dir1 = getString("Source directory: ");
        String dir2 = getString("Destination directory: ");
        dir1 = adjustDirectory(dir1);
        dir2 = adjustDirectory(dir2);

        File directory1 = getDirectory(dir1);
        File directory2 = getDirectory(dir2);
        Loader.getFiles(dir1, directory1, source);
        Loader.getFiles(dir2, directory2, destination);
    }

    private String getString(String message) {
        display(message);
        Scanner scanner = new Scanner(System.in);
        String dir = scanner.nextLine();
        return dir;
    }

    private void list(HashMap<File, File> map) {
        StringBuilder sb = new StringBuilder();
        for (File f1 : map.keySet()) {
            File f2 = map.get(f1);
            sb.append(f1.getAbsolutePath()).append("\n")
                    .append(f2.getAbsolutePath()).append("\n\n");
        }
        display(sb.toString());
    }

    private void list(ArrayList<ArrayList<File>> list) {
        StringBuilder sb = new StringBuilder();
        for (ArrayList<File> l : list) {
            for (File f : l) {
                sb.append(f.getAbsolutePath()).append("\n");
            }
            sb.append("\n");
        }
        display(sb.toString());
    }

    private void display(String str) {
        System.out.print(str);
        System.out.flush();
    }

    private String adjustDirectory(String dir) {
        String newDir = dir;
        if (newDir.endsWith("/") || newDir.endsWith("\\"))
            newDir = newDir.substring(0, newDir.length() - 1);
        return newDir;
    }

    private File getDirectory(String dir) throws Exception {
        File f = new File(dir);
        if (!f.isDirectory())
            throw new Exception("'" + dir + "' is not a directory.");
        return f;
    }

    private File getFile(String path) throws Exception {
        File f = new File(path);
        if (!f.isFile())
            throw new Exception("'" + path + "' is not a file. File extension missing?");
        return f;
    }
}
