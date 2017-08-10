import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Comparator {
    private static int BUFFER_SIZE = 10485760;   // 10 MB

    public static HashMap<File, File> compareContent(HashMap<String, File> source, HashMap<String, File> destination) throws Exception {
        HashMap<File, File> notEqual = new HashMap<>();
        Set<String> keys = source.keySet();
        double interval = keys.size() > 100 ? keys.size() / 100 : 1;
        Iterator<String> it = keys.iterator();
        for (int i = 0; it.hasNext(); ) {
            for (int j = 0; j < interval && it.hasNext(); j++, i++) {
                String key = it.next();
                File f2 = destination.get(key);
                if (f2 != null) {
                    File f1 = source.get(key);
                    if (!areFilesEqual(f1, f2))
                        notEqual.put(f1, f2);
                }
            }
            double percentage = i * 100 / keys.size();
            System.out.println("Compared " + percentage + "%.");
        }
        return notEqual;
    }

    public static boolean areFilesEqual(File f1, File f2) throws Exception {
        long f1Length = f1.length();
        if (f1Length != f2.length() || f1.lastModified() != f2.lastModified())
            return false;

        FileInputStream fis1 = new FileInputStream(f1.getAbsolutePath());
        FileInputStream fis2 = new FileInputStream(f2.getAbsolutePath());

        byte data1[] = new byte[BUFFER_SIZE];
        byte data2[] = new byte[BUFFER_SIZE];
        boolean equal = true;
        boolean readError = false;

        for (long i = 0; i < f1Length && equal; i += BUFFER_SIZE) {
            int read1 = fis1.read(data1);
            int read2 = fis2.read(data2);

            if (read1 != read2) {
                readError = true;
                break;
            }

            if (!Arrays.equals(data1, data2))
                equal = false;
        }

        fis1.close();
        fis2.close();

        if (readError)
            throw new Exception("An error occurred reading the data from the files.");

        return equal;
    }
}