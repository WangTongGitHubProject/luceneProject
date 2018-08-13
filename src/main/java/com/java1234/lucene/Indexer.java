package com.java1234.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import sun.applet.Main;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;


/**
 * Created by wangtong on 2018/8/13.
 */
public class Indexer {

    private IndexWriter indexWriter;//写索引实例

    /**
     * 构造方法 实例化IndexWriter
     *
     * @param indexDir
     * @throws Exception
     */
    public Indexer(String indexDir) throws Exception {
        Directory directory = FSDirectory.open(Paths.get(indexDir));
        Analyzer analyzer = new StandardAnalyzer(); //标准分词器
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        indexWriter = new IndexWriter(directory, indexWriterConfig);
    }

    /**
     * 关闭写索引
     *
     * @throws Exception
     */
    public void close() throws Exception {
        indexWriter.close();
    }

    /**
     * 索引指定文件
     * @param dataDir
     * @return
     * @throws Exception
     */
    public int index(String dataDir) throws Exception {
        File[] files = new File(dataDir).listFiles();
        for (File file : files){
            indexFile(file);
        }
        return indexWriter.numDocs();
    }

    /**
     * 索引指定文件
     * @param file
     */
    private void indexFile(File file) throws Exception{
        System.out.println("索引文件： " + file.getCanonicalPath());
        Document document = getDocument(file);
        indexWriter.addDocument(document);
    }

    /**
     * 获取文档，文档里面在设置每个字段
     * @param file
     * @return
     * @throws Exception
     */
    private Document getDocument(File file)throws Exception{
        Document document = new Document();
        document.add(new TextField("contents",new FileReader(file)));
        document.add(new TextField("fileName",file.getName(),Field.Store.YES));
        document.add(new TextField("fullPath",file.getCanonicalPath(), Field.Store.YES));
        return document;
    }

    public static void main(String[] args){
        String indexDir = "D:\\lucene";
        String dataDir = "D:\\lucene\\data";
        Indexer indexer = null;
        int numIndexed = 0;
        long start=System.currentTimeMillis();
        try {
            indexer = new Indexer(indexDir);
            numIndexed=indexer.index(dataDir);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                indexer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long end=System.currentTimeMillis();
        System.out.println("索引："+numIndexed+" 个文件 花费了"+(end-start)+" 毫秒");
    }


}
