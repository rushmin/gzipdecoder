package org.wso2;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {

        try {

            List<Byte> zippedBytes;

            zippedBytes = readWirelogContent();


            //zippedBytes = readWiresharkContent();

            byte[] zippedBytesArray = new byte[zippedBytes.size()];

            for (int i = 0; i < zippedBytes.size(); i++) {
                System.out.println(i);
                zippedBytesArray[i] = zippedBytes.get(i);
            }

            GzipCompressorInputStream in = new GzipCompressorInputStream(new ByteArrayInputStream(zippedBytesArray));

            ByteArrayOutputStream unzippedContent = new ByteArrayOutputStream();
            IOUtils.copy(in, unzippedContent);

            System.out.println(unzippedContent.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static List<Byte> readWirelogContent() throws Exception {

        InputStream content = Main.class.getResourceAsStream("/wirelog-content.txt");
        List<String> lines = IOUtils.readLines(content, "UTF-8");
        String log = lines.get(0);


        String blockPattern = "\\[(0x|\\\\).+?\\]";
        Pattern pattern = Pattern.compile(blockPattern);
        Matcher matcher = pattern.matcher(log);

        Map<Integer, Integer> blocks = new HashMap<Integer, Integer>();
        while(matcher.find()) {
            blocks.put(matcher.start(), matcher.end());
        }

        List<Byte> zippedBytes = new ArrayList<Byte>();

        int i = 0;
        while (i < log.length()) {

            Integer lastIndexOfBlock = blocks.get(i);

            Byte zippedByte = null;
            if (lastIndexOfBlock != null) {
                String blockContent = log.substring(i, lastIndexOfBlock);
                int blockLength = blockContent.length();

                String blockBody = blockContent.substring(1, blockLength - 1);

                if (blockBody.toLowerCase().startsWith("0x")) {
                    zippedByte = (byte) (Integer.parseInt(blockBody.substring(2), 16));
                } else if (blockBody.equals("\\r")) {
                    zippedByte = 13;
                }

                zippedBytes.add(zippedByte);
                i = i + blockLength;

            } else {
                zippedBytes.add((byte) log.charAt(i));
                i++;
            }
        }

        return zippedBytes;
    }

    private static List<Byte> readWiresharkContent() throws Exception {

        InputStream content = Main.class.getResourceAsStream("/wireshark-content.txt");
        List<String> lines = IOUtils.readLines(content, "UTF-8");
        String log = lines.get(0);

        List<Byte> zippedBytes = new ArrayList<Byte>();

        String[] segments = log.split(":");

        for(String segment : segments){
            zippedBytes.add((byte) Integer.parseInt(segment, 16));

        }

        return zippedBytes;
    }

}
