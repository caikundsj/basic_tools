package com.tasks.example.streaming;

import cn.comm.utils.ConfigUtils;
import cn.train.base.IBaseRun;
import cn.train.base.env.BaseStreamApp;
import org.apache.flink.streaming.api.datastream.DataStreamSource;

public class LearnStream_TF extends BaseStreamApp implements IBaseRun {
    /*
    数据流转换
    map:    DataStream → DataStream 获取一个元素并生成一个元素。  ex:dataStream.map(new MapFunction<Integer, Integer>() {...}
    flatMap:    DataStream → DataStream 获取一个元素并生成零个、一个或多个元素。 ex:dataStream.flatMap(new FlatMapFunction<String, String>() {...}
    filter:    DataStream → DataStream 计算每个元素的布尔函数并保留函数返回 true 的那些。 ex:dataStream.filter(new FilterFunction<Integer>() {...}
    keyBy:    DataStream → KeyedStream 在逻辑上将流划分为不相交的分区。具有相同键的所有记录都分配给相同的分区。在内部，keyBy()是通过散列分区实现的。 ex:dataStream.keyBy(value -> value.getSomeKey());
     ∟>   Reduce:    KeyedStream → DataStream 键数据流上的“滚动”减少。将当前元素与最后减少的值组合并发出新值。 ex:keyedStream.reduce(new ReduceFunction<Integer>() {...}
     ∟>   Window:    KeyedStream → WindowedStream 可以在已经分区的 KeyedStreams 上定义 Windows。Windows 根据某些特征（例如，最近 5 秒内到达的数据）对每个键中的数据进行分组。 ex:dataStream.keyBy(value -> value.f0).window(TumblingEventTimeWindows.of(Time.seconds(5)));
    WindowAll:  DataStream → AllWindowedStream 可以在常规 DataStreams 上定义窗口。Windows 根据某些特征（例如，最近 5 秒内到达的数据）对所有流事件进行分组。 ex:dataStream.windowAll(TumblingEventTimeWindows.of(Time.seconds(5)));
     ∟>   ∟> Window Apply: WindowedStream → DataStream | AllWindowedStream → DataStream 将通用功能应用于整个窗口。 ex:windowedStream.apply(new WindowFunction<Tuple2<String,Integer>, Integer, Tuple, Window>() {...} | ex:allWindowedStream.apply (new AllWindowFunction<Tuple2<String,Integer>, Integer, Window>() {...}
    WindowReduce: WindowedStream → DataStream 对窗口应用 reduce function 并返回 reduce 后的值。 ex:windowedStream.reduce (new ReduceFunction<Tuple2<String,Integer>>() {...}

    Union: DataStream* → DataStream 将两个或多个数据流联合来创建一个包含所有流中数据的新流。注意：如果一个数据流和自身进行联合，这个流中的每个数据将在合并后的流中出现两次。 ex:dataStream.union(otherStream1, otherStream2, ...);
    Window Join: DataStream,DataStream → DataStream 根据指定的 key 和窗口 join 两个数据流。 ex:dataStream.join(otherStream).where(<key selector>).equalTo(<key selector>).window(TumblingEventTimeWindows.of(Time.seconds(3))).apply (new JoinFunction () {...});
    Interval Join:  KeyedStream,KeyedStream → DataStream 根据 key 相等并且满足指定的时间范围内（e1.timestamp + lowerBound <= e2.timestamp <= e1.timestamp + upperBound）的条件将分别属于两个 keyed stream 的元素 e1 和 e2 Join 在一起。 ex:keyedStream.intervalJoin(otherKeyedStream).between(Time.milliseconds(-2), Time.milliseconds(2)).upperBoundExclusive(true).lowerBoundExclusive(true).process(new IntervalJoinFunction() {...});
    Window CoGroup: DataStream,DataStream → DataStream 根据指定的 key 和窗口将两个数据流组合在一起。 ex:dataStream.coGroup(otherStream).where(0).equalTo(1).window(TumblingEventTimeWindows.of(Time.seconds(3))).apply (new CoGroupFunction () {...});
    Connect: DataStream,DataStream → ConnectedStream “连接” 两个数据流并保留各自的类型。connect 允许在两个流的处理逻辑之间共享状态。 ex:
            ∟> DataStream<Integer> someStream = //...
            ∟> DataStream<String> otherStream = //...
            ∟> ConnectedStreams<Integer, String> connectedStreams = someStream.connect(otherStream);
    CoMap, CoFlatMap: ConnectedStream → DataStream 类似于在连接的数据流上进行 map 和 flatMap。
    Iterate: DataStream → IterativeStream → ConnectedStream 通过将一个算子的输出重定向到某个之前的算子来在流中创建“反馈”循环。这对于定义持续更新模型的算法特别有用。下面的代码从一个流开始，并不断地应用迭代自身。大于 0 的元素被发送回反馈通道，其余元素被转发到下游。
    Cache: DataStream → CachedDataStream 把算子的结果缓存起来。目前只支持批执行模式下运行的作业。算子的结果在算子第一次执行的时候会被缓存起来，之后的 作业中会复用该算子缓存的结果。如果算子的结果丢失了，它会被原来的算子重新计算并缓存。

    物理分区：
        ∟> 自定义分区： DataStream → DataStream  使用用户定义的 Partitioner 为每个元素选择目标任务。 ex: dataStream.partitionCustom(partitioner, "someKey");
        ∟> 随机分区: DataStream → DataStream 将元素随机地均匀划分到分区。 ex: dataStream.shuffle();
        ∟> Rescaling: DataStream → DataStream 将元素以 Round-robin 轮询的方式分发到下游算子。如果你想实现数据管道，这将很有用，例如，想将数据源多个并发实例的数据分发到多个下游 map 来实现负载分配，但又不想像 rebalance() 那样引起完全重新平衡。该算子将只会到本地数据传输而不是网络数据传输，这取决于其它配置值，例如 TaskManager 的 slot 数量。 ex: dataStream.rescale();
        ∟> 广播: DataStream → DataStream 将元素广播到每个分区 。 ex:dataStream.broadcast();
    算子链和资源组:
        ∟>

     */
    @Override
    public void doMain() throws Exception {
        DataStreamSource<String> source = env.readTextFile("tmp.csv");

        env.execute();
    }

    public static void main(String[] args) throws Exception {
        try {
            /*
                加载配置文件中的配置项
             */
            ConfigUtils.initLoadConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new LearnStream_TF().doMain();
    }
}