package app;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AnalysisMain {

    //pdf文件们所在目录
    private static String basePath = "src/main/resources/PDF/";
    //解析完成后，解析结果存放目录
    private static String baseAnalysisResultPath = "src/main/resources/result/";
    //pdf文件内容切割，从……
    private static String subStrFrom1 = "内容及提供的资料";
    //pdf文件内容切割，从……
    private static String subStrFrom2 = "接待对象";
    //pdf文件内容切割，到……
    private static String subStrTo1 = "一、";
    //pdf文件内容切割，到……
    private static String subStrTo2 = "一、";
    //stock 长度
    private static int stockLen = 6;

    public static void main(String[] args) throws Exception {
        List<String> allFilenameStocks = getAllFilenameStocks(basePath);
        ArrayList<File> files = new ArrayList<File>();
        //加载所有file
        listAllFiles(basePath, files);
        for (String stock : allFilenameStocks) {
            StringBuilder sb = new StringBuilder();
            //同一个stock的pdf结果写入同一个txt结果中
            for (File file : files) {
                if (file.getName().startsWith(stock)) {
                    String subStringByFile = subStringByFile(file);
                    sb.append(subStringByFile);
                }
            }
            String result2Write = sb.toString();
            writeResultToFile(baseAnalysisResultPath + stock + ".txt", result2Write);
        }
    }

    /**
     * 读取pdf中文字信息(全部)
     *
     * @param inputFile
     */
    private static String READPDF(File inputFile) {
        //创建文档对象
        PDDocument doc = null;
        String content = "";
        try {
            //加载一个pdf对象
            doc = PDDocument.load(inputFile);
            //获取一个PDFTextStripper文本剥离对象
            PDFTextStripper textStripper = new PDFTextStripper();
            content = textStripper.getText(doc);
            doc.close();
            return content;
        } catch (Exception e) {
            System.out.println("" + inputFile.getPath() + "读取失败");
            return null;
        }
    }

    /**
     * 列出文件夹中所有文件名
     */
    private static void listAllFilename(String path, List<String> filenames) {
        File file = new File(path);
        File[] tempList = file.listFiles();
        if (tempList == null) {
            return;
        }
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                filenames.add(tempList[i].getName());
            }
            if (tempList[i].isDirectory()) {
                listAllFilename(tempList[i].getAbsolutePath(), filenames);
            }
        }
    }

    private static List<String> getAllFilenameStocks(String path) {
        ArrayList<String> filenames = new ArrayList<String>();
        listAllFilename(path, filenames);
        ArrayList<String> result = new ArrayList<String>();
        if (filenames.size() != 0) {
            for (String filename : filenames) {
                String stock = filename.substring(0, stockLen);
                if (!result.contains(stock)) {
                    result.add(stock);
                }
            }
        }
        return result;
    }

    /**
     * 列出文件夹中所有文件
     */
    private static void listAllFiles(String path, List<File> files) {
        File file = new File(path);
        File[] tempList = file.listFiles();
        if (tempList == null) {
            return;
        }
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                files.add(tempList[i]);
            }
            if (tempList[i].isDirectory()) {
                listAllFiles(tempList[i].getAbsolutePath(), files);
            }
        }
    }

    /**
     * files subString
     */
    private static String subStringByFiles(List<File> files) {
        StringBuilder sb = new StringBuilder();
        for (File file : files) {
            sb.append(subStringByFile(file));
        }
        return sb.toString();
    }

    private static String subStringByFile(File file) {
        String pdfContent = READPDF(file);
        if (pdfContent == null) {
            System.out.println("文件：" + file.getName() + "无法读取内容");
            return "";
        }
        int from = pdfContent.indexOf(subStrFrom1);
        if (from == -1) {
            from = pdfContent.indexOf(subStrFrom2);
        }
        if (from == -1) {
            System.out.println("文件：" + file.getName() + "无法找到内容切割起止位置");
            return "";
        }
        String fromStr = pdfContent.substring(from);
        int to = fromStr.indexOf(subStrTo1);
        if (to == -1) {
            to = fromStr.indexOf(subStrTo2);
        }
        if (to == -1) {
            System.out.println("文件：" + file.getName() + "无法找到内容切割终止位置，直接窃取到文章末尾，请手动处理");
            return fromStr;
        } else {
            return fromStr.substring(0, to);
        }
    }

    private static List<File> listCompanyByEqualStock(List<File> files, String stock) {
        ArrayList<File> result = new ArrayList<File>();
        for (File file : files) {
            String fileName = file.getName();
            if (fileName.startsWith(stock)) {
                result.add(file);
            }
        }
        return result;
    }

    private static void writeResultToFile(String path, String data) {
        try {
            File file = new File(path);
//            if (!file.exists()) {
//
//            }
            FileUtils.writeStringToFile(file, data);
        } catch (IOException e) {
            System.out.println("文件：" + path + "写入失败");
        }
    }
}
