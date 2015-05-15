import java.io.*;

public class ByteFrequencyInSectors {

    public static final int SECTOR_SIZE = 512;
    public static final int CLUSTER_SIZE = 4096;
    public static final int BLOCK_SIZE = SECTOR_SIZE;
    public static final int NUMBER_OF_BLOCKS = 20;

    public static final String fileNameCSV_table_Frequency_Of_Bytes_In_Each_Position = "TableFrequencyOfBytesInEachPosition.csv";
    public static final String fileNameCSV_table_Frequency_Of_Bytes_In_Each_Position_sorted = "TableFrequencyOfBytesInEachPosition_sorted_by_string.csv";
    public static final String fileNameCSV_table_Frequency_Of_Bytes_In_Each_Position_sorted_by_line = "TableFrequencyOfBytesInEachPosition_sorted_by_line.csv";

    public static String convertByteArrayToHexString( byte[] ByteArray ) {
        StringBuilder HexString = new StringBuilder(ByteArray.length * 2);
        for ( byte each_byte : ByteArray )
            HexString.append(String.format("%02x", each_byte & 0xFF));
        return HexString.toString();
    }

    public static int[][] sortTableFrequency (int[][] table_Frequency) {
        int[][] table_Frequency_Byte_Sorted = new int[NUMBER_OF_BLOCKS * BLOCK_SIZE][256];  // таблица с байтами в порядке из частоты встречаемости каждого байта в каждой позиции в порядке убывания
        for (int ordinal_number_of_byte = 0; ordinal_number_of_byte < NUMBER_OF_BLOCKS * BLOCK_SIZE; ordinal_number_of_byte++) {
            for (int byte_value = 0; byte_value < 256; byte_value++) {
                table_Frequency_Byte_Sorted[ordinal_number_of_byte][byte_value]=byte_value;
            }
        }

        for (int ordinal_number_of_byte = 0; ordinal_number_of_byte < NUMBER_OF_BLOCKS * BLOCK_SIZE; ordinal_number_of_byte++) {
            for (int i = 0; i < 256 - 1; i++) {
                for (int j = 0; j < 256 - i - 1; j++) {
                    if (table_Frequency[ordinal_number_of_byte][j] < table_Frequency[ordinal_number_of_byte][j+1]) {
                        int tmp = table_Frequency[ordinal_number_of_byte][j+1];
                        table_Frequency[ordinal_number_of_byte][j+1] = table_Frequency[ordinal_number_of_byte][j];
                        table_Frequency[ordinal_number_of_byte][j] = tmp;

                        tmp = table_Frequency_Byte_Sorted[ordinal_number_of_byte][j+1];
                        table_Frequency_Byte_Sorted[ordinal_number_of_byte][j+1] = table_Frequency_Byte_Sorted[ordinal_number_of_byte][j];
                        table_Frequency_Byte_Sorted[ordinal_number_of_byte][j] = tmp;
                    }
                }
            }
        }

        return table_Frequency_Byte_Sorted;
    }

    public static int[] sortTableFrequencyByLine(int[][] table_Frequency) {
        int[] tmp_mas = new int[256];
        int[] order = new int[NUMBER_OF_BLOCKS * BLOCK_SIZE];
        for (int i = 0; i < 256; i++) {
            order[i] = i;
        }
        int tmp;
        for (int i = 0; i < 256 - 1; i++) {
            for (int j = 0; j < 256 - i - 1; j++) {
                if (table_Frequency[j][0] < table_Frequency[j + 1][0]) {
                    /*System.arraycopy(table_Frequency[j + 1], 0, tmp_mas, 0, 256);
                    System.arraycopy(table_Frequency[j], 0, table_Frequency[j + 1], 0, 256);
                    System.arraycopy(tmp_mas, 0, table_Frequency[j], 0, 256);*/

                    tmp = table_Frequency[j + 1][0];
                    table_Frequency[j + 1][0] = table_Frequency[j][0];
                    table_Frequency[j][0] = tmp;

                    tmp = order[j + 1];
                    order[j+1] = order[j];
                    order[j] = tmp;
                }
            }
        }

        return order;
    }


    public static void main(String [] args) {
        /* Поиск в папке всех файлов с заданным расширением */
        //File folder = new File("M:\\File examples\\JPEG");
        File folder = new File("M:\\File examples\\JPG");
        File[] listOfFiles = folder.listFiles(new FilenameFilter() {
            public boolean accept(File folder, String fileName) {
                return fileName.toLowerCase().endsWith(".jpg");
            }
        });
        /*for (File fileName : listOfFiles) {
            if (fileName.isFile()) {
                System.out.println(fileName.getName());
            }
        }*/

        int[][] table_Bytes_In_Each_Position = new int[NUMBER_OF_BLOCKS * BLOCK_SIZE][256];
        //double[][] table_Frequency = new double[NUMBER_OF_BLOCKS * BLOCK_SIZE][256];  // таблица с частотой встречаемости каждого байта в каждой позиции
        double[][] table_Frequency_Of_Bytes_In_Each_Position = new double[NUMBER_OF_BLOCKS * BLOCK_SIZE][256];  // таблица с частотой встречаемости каждого байта в каждой позиции
        int[][] table_Frequency_Byte_Sorted = new int[NUMBER_OF_BLOCKS * BLOCK_SIZE][256];  // таблица с байтами в порядке из частоты встречаемости каждого байта в каждой позиции в порядке убывания
        int numberOfFiles = 0;

        /* Чтение NUMBER_OF_BLOCKS * BLOCK_SIZE первых байт каждого файла и подсчёт количества байтов в каждой позиции */
        for (File fileName : listOfFiles) {
            if ( (fileName.isFile()) && (fileName.length() > 10240) ) { // Провервка на файл и его размер > 10240 байтов
                numberOfFiles ++;

                try {
                    byte[] mDataBuffer = new byte[NUMBER_OF_BLOCKS * BLOCK_SIZE];
                    FileInputStream inputStream = new FileInputStream(fileName);
                    inputStream.read(mDataBuffer);
                    System.out.print(fileName.getName() + " = " + inputStream.available() + " " + fileName.length());
                    System.out.format("\t%02x%02x%02x%02x\n", mDataBuffer[0] & 0xFF, mDataBuffer[1] & 0xFF, mDataBuffer[2] & 0xFF, mDataBuffer[3] & 0xFF);

                    //System.out.print(convertByteArrayToHexString(mDataBuffer));   // Вывод на экран HEX считанного файла
                    //System.out.println();

                    for (int ordinal_number_of_byte = 0; ordinal_number_of_byte < NUMBER_OF_BLOCKS * BLOCK_SIZE; ordinal_number_of_byte++) {
                        table_Bytes_In_Each_Position[ordinal_number_of_byte][(int) mDataBuffer[ordinal_number_of_byte] & 0xFF]++;
                    }

                    inputStream.close();
                } catch (FileNotFoundException ex) {
                    System.out.println("Unable to open file '" + fileName + "'");
                } catch (IOException ex) {
                    System.out.println("Error reading file '" + fileName + "'");
                }
            }
        }
        System.out.println(numberOfFiles);

        /* Подчёт частоты встречаемости каждого байта для каждой позиции */
        for (int ordinal_number_of_byte = 0; ordinal_number_of_byte < NUMBER_OF_BLOCKS * BLOCK_SIZE; ordinal_number_of_byte++) {
            for (int byte_value = 0; byte_value < 256; byte_value++) {
                table_Frequency_Of_Bytes_In_Each_Position[ordinal_number_of_byte][byte_value] = (double)table_Bytes_In_Each_Position[ordinal_number_of_byte][byte_value] / numberOfFiles;
            }
        }

        /* Вывод на экран частоты встречаемости байт в каждой позиции */
        /*for (int ordinal_number_of_byte = 0; ordinal_number_of_byte < NUMBER_OF_BLOCKS * BLOCK_SIZE; ordinal_number_of_byte++) {
            System.out.print(ordinal_number_of_byte + ":");
            for (int byte_value = 0; byte_value < 256; byte_value++) {
                System.out.print("\t" + String.format("%02x", byte_value & 0xFF).toUpperCase() + " (" + table_Frequency[ordinal_number_of_byte][byte_value] + ")");
                //System.out.print("\t" + byte_value + " (" + Table_Frequency[ordinal_number_of_byte][byte_value] + ")");
            }
            System.out.println();
        }*/

        /* Формирование CSV данных для записи в файл */
        StringBuilder CSVString_Bytes_In_Each_Position = new StringBuilder(1000000000);
        for (int byte_value = 0; byte_value < 256; byte_value++) {
            CSVString_Bytes_In_Each_Position.append(String.format(";%02x", byte_value & 0xFF).toUpperCase());
        }
        CSVString_Bytes_In_Each_Position.append("\n");
        for (int ordinal_number_of_byte = 0; ordinal_number_of_byte < NUMBER_OF_BLOCKS * BLOCK_SIZE; ordinal_number_of_byte++) {
            CSVString_Bytes_In_Each_Position.append(String.format("%d", ordinal_number_of_byte+1));
            for (int byte_value = 0; byte_value < 256; byte_value++) {
                //CSVString.append(String.format(";%d", table_Frequency[ordinal_number_of_byte][byte_value]));
                CSVString_Bytes_In_Each_Position.append(String.format(";%.3f", table_Frequency_Of_Bytes_In_Each_Position[ordinal_number_of_byte][byte_value]).replace(",","."));
            }
            CSVString_Bytes_In_Each_Position.append("\n");
        }
        /* Запись в файл частоты встречаемости байт в каждой позиции в формате CSV */
        try {
            File fileCSV = new File(fileNameCSV_table_Frequency_Of_Bytes_In_Each_Position);
            BufferedWriter outputStream = new BufferedWriter(new FileWriter(fileCSV));
            outputStream.write(CSVString_Bytes_In_Each_Position.toString());
            outputStream.close();
        }
        catch (IOException ex) {
            System.out.println("Error writing to file '" + fileNameCSV_table_Frequency_Of_Bytes_In_Each_Position + "'");
        }

        /* Сортировка частоты встречаемости байт */
        table_Frequency_Byte_Sorted = sortTableFrequency(table_Bytes_In_Each_Position);
        /*for (int ordinal_number_of_byte = 0; ordinal_number_of_byte < NUMBER_OF_BLOCKS * BLOCK_SIZE; ordinal_number_of_byte++) {
            for (int byte_value = 0; byte_value < 256; byte_value++) {
                System.out.print(table_Frequency_Byte_Sorted[ordinal_number_of_byte][byte_value] + " ");
            }
            System.out.println();
        }

        System.out.println("================================================");
        for (int ordinal_number_of_byte = 0; ordinal_number_of_byte < NUMBER_OF_BLOCKS * BLOCK_SIZE; ordinal_number_of_byte++) {
            for (int byte_value = 0; byte_value < 256; byte_value++) {
                System.out.print(table_Bytes_In_Each_Position[ordinal_number_of_byte][byte_value] + " ");
            }
            System.out.println();
        }*/

        /* Формирование CSV данных для записи в файл */
        StringBuilder CSVString_Frequency_Of_Bytes_In_Each_Position = new StringBuilder(1000000000);
        for (int ordinal_number_of_byte = 0; ordinal_number_of_byte < NUMBER_OF_BLOCKS * BLOCK_SIZE; ordinal_number_of_byte++) {
            CSVString_Frequency_Of_Bytes_In_Each_Position.append(String.format("%d", ordinal_number_of_byte+1));
            for (int byte_value = 0; byte_value < 256; byte_value++) {
                CSVString_Frequency_Of_Bytes_In_Each_Position.append(String.format(";%02x (%.3f)", table_Frequency_Byte_Sorted[ordinal_number_of_byte][byte_value] & 0xFF, table_Frequency_Of_Bytes_In_Each_Position[ordinal_number_of_byte][table_Frequency_Byte_Sorted[ordinal_number_of_byte][byte_value]]).replace(",","."));
            }
            CSVString_Frequency_Of_Bytes_In_Each_Position.append("\n");
        }
        /* Запись в файл частоты встречаемости байт в каждой позиции в формате CSV */
        try {
            File fileCSV = new File(fileNameCSV_table_Frequency_Of_Bytes_In_Each_Position_sorted);
            BufferedWriter outputStream = new BufferedWriter(new FileWriter(fileCSV));
            outputStream.write(CSVString_Frequency_Of_Bytes_In_Each_Position.toString());
            outputStream.close();
        }
        catch (IOException ex) {
            System.out.println("Error writing to file '" + fileNameCSV_table_Frequency_Of_Bytes_In_Each_Position_sorted + "'");
        }

        int[] order = new int[NUMBER_OF_BLOCKS * BLOCK_SIZE];
        order = sortTableFrequencyByLine(table_Bytes_In_Each_Position);
        /* Формирование CSV данных для записи в файл */
        StringBuilder CSVString_Frequency_Of_Bytes_In_Each_Position_Sorted_By_Line = new StringBuilder(1000000000);
        for (int ordinal_number_of_byte = 0; ordinal_number_of_byte < NUMBER_OF_BLOCKS * BLOCK_SIZE; ordinal_number_of_byte++) {
            CSVString_Frequency_Of_Bytes_In_Each_Position_Sorted_By_Line.append(String.format("%d", order[ordinal_number_of_byte]+1));
            for (int byte_value = 0; byte_value < 256; byte_value++) {
                CSVString_Frequency_Of_Bytes_In_Each_Position_Sorted_By_Line.append(String.format(";%02x (%.3f)", table_Frequency_Byte_Sorted[order[ordinal_number_of_byte]][byte_value] & 0xFF, table_Frequency_Of_Bytes_In_Each_Position[order[ordinal_number_of_byte]][table_Frequency_Byte_Sorted[order[ordinal_number_of_byte]][byte_value]]).replace(",","."));
            }
            CSVString_Frequency_Of_Bytes_In_Each_Position_Sorted_By_Line.append("\n");
        }
        /* Запись в файл частоты встречаемости байт в каждой позиции отсортированной по строкам в формате CSV */
        try {
            File fileCSV = new File(fileNameCSV_table_Frequency_Of_Bytes_In_Each_Position_sorted_by_line);
            BufferedWriter outputStream = new BufferedWriter(new FileWriter(fileCSV));
            outputStream.write(CSVString_Frequency_Of_Bytes_In_Each_Position_Sorted_By_Line.toString());
            outputStream.close();
        }
        catch (IOException ex) {
            System.out.println("Error writing to file '" + fileNameCSV_table_Frequency_Of_Bytes_In_Each_Position_sorted_by_line + "'");
        }

    }
}
