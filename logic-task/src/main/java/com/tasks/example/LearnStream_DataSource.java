package com.tasks.example;

import cn.train.base.IBaseRun;
import cn.train.base.env.BaseTableApp;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;

import org.apache.flink.api.java.io.TextInputFormat;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.functions.source.FileProcessingMode;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.util.SplittableIterator;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;


public class LearnStream_DataSource<T> extends BaseTableApp<T> implements IBaseRun {
    /*
    基于文件：
            1.readTextFile(path) - 读取文本文件，例如遵守 TextInputFormat 规范的文件，逐行读取并将它们作为字符串返回。
            2.readFile(fileInputFormat, path) - 按照指定的文件输入格式读取（一次）文件。
            3.readFile(fileInputFormat, path, watchType, interval, pathFilter, typeInfo) - 这是前两个方法内部调用的方法。
            它基于给定的 fileInputFormat 读取路径 path 上的文件。根据提供的 watchType 的不同，source 可能定期（每 interval 毫秒）
            监控路径上的新数据（watchType 为 FileProcessingMode.PROCESS_CONTINUOUSLY），或者处理一次当前路径中的数据然后
            退出（watchType 为 FileProcessingMode.PROCESS_ONCE)。使用 pathFilter，用户可以进一步排除正在处理的文件。
        实现：
            在底层，Flink 将文件读取过程拆分为两个子任务，即 目录监控 和 数据读取。每个子任务都由一个单独的实体实现。
        监控由单个非并行（并行度 = 1）任务实现，而读取由多个并行运行的任务执行。后者的并行度和作业的并行度相等。
        单个监控任务的作用是扫描目录（定期或仅扫描一次，取决于 watchType），找到要处理的文件，将它们划分为 分片，
        并将这些分片分配给下游 reader。Reader 是将实际获取数据的角色。每个分片只能被一个 reader 读取，而一个 reader 可以一个一个地读取多个分片。
        重要提示：
            1.如果 watchType 设置为 FileProcessingMode.PROCESS_CONTINUOUSLY，当一个文件被修改时，它的内容会被完全重新处理。这可能会
            打破 “精确一次” 的语义，因为在文件末尾追加数据将导致重新处理文件的所有内容。
            2.如果 watchType 设置为 FileProcessingMode.PROCESS_ONCE，source 扫描一次路径然后退出，无需等待 reader 读完文件内容。
            当然，reader 会继续读取数据，直到所有文件内容都读完。关闭 source 会导致在那之后不再有检查点。
            这可能会导致节点故障后恢复速度变慢，因为作业将从最后一个检查点恢复读取。
    基于套接字：
               socketTextStream - 从套接字读取。元素可以由分隔符分隔。
    基于集合：
            1.fromCollection(Collection) - 从 Java Java.util.Collection 创建数据流。集合中的所有元素必须属于同一类型。
            2.fromCollection(Iterator, Class) - 从迭代器创建数据流。class 参数指定迭代器返回元素的数据类型。
            3.fromElements(T ...) - 从给定的对象序列中创建数据流。所有的对象必须属于同一类型。
            4.fromParallelCollection(SplittableIterator, Class) - 从迭代器并行创建数据流。class 参数指定迭代器返回元素的数据类型。
            5.generateSequence(from, to) - 基于给定间隔内的数字序列并行生成数据流。
    自定义：
                addSource - 关联一个新的 source function。例如，你可以使用 addSource(new FlinkKafkaConsumer<>(...)) 来从 Apache Kafka 获取数据。
     */
    @Override
    public void doMain() throws Exception {
        //基于文件
        DataStreamSource<String> readTextFile = env.readTextFile("/tmp.csv");
        env.readFile(new TextInputFormat(new Path("/tmp.csv")),
                "D:/tmp.csv",
                FileProcessingMode.PROCESS_ONCE,
                1,
                BasicTypeInfo.STRING_TYPE_INFO).print();
        //基于套接字
        env.socketTextStream("10.10.35.43", 9099).print();
        //基于集合
        env.fromCollection(Arrays.asList(1, 2, 3, 4, 5)).print();

        class CustomIterator implements Iterator<Integer>, Serializable { //自定义迭代器
            private Integer i = 0;

            @Override
            public boolean hasNext() {
                return i < 100;
            }

            @Override
            public Integer next() {
                i++;
                return i;
            }
        }
        env.fromCollection(new CustomIterator(), BasicTypeInfo.INT_TYPE_INFO).print();
        env.fromElements(1, 2, 3, 4, 5).print();
        class CustomSplittableIterator extends SplittableIterator implements Serializable{

            @Override
            public Iterator[] split(int i) {
                return new Iterator[0];
            }

            @Override
            public int getMaximumNumberOfSplits() {
                return 0;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public Object next() {
                return null;
            }
        }
        env.fromParallelCollection(new CustomSplittableIterator(),BasicTypeInfo.INT_TYPE_INFO).print();
        //自定义
        env.fromSequence(0, 100);
        env.addSource(new SourceFunction<Long>() {

            private long count = 0L;
            private volatile boolean isRunning = true;

            public void run(SourceContext<Long> ctx) {
                while (isRunning && count < 1000) {
                    // 通过collect将输入发送出去
                    ctx.collect(count);
                    count++;
                }
            }

            public void cancel() {
                isRunning = false;
            }

        }).print();
        //抽取 source/sink 工具 --> common 模块
    }
}